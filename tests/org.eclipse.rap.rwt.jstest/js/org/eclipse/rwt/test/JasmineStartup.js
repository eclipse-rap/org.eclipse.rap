/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
namespace( "org.eclipse.rwt.test" );

org.eclipse.rwt.test.JasmineStartup = {

  run : function() {

    ////////
    // Setup

    var jasmineEnv = jasmine.getEnv();

    if( window.location.href.indexOf( "fast=true" ) !== -1 ) {
      console.info( "Setting up Jasmine..." );
      jasmineEnv.updateInterval = 3000;
      jasmineEnv.addReporter( {
        reportRunnerStarting : function() {
          console.info( "Start Tests..." );
        },
        reportRunnerResults : function( runner ) {
          var msg = "Tests Finished!";
          console.info( msg );
          window.alert( msg );
        },
        reportSuiteResults : function( suite ) {
          if( suite.parentSuite === null ) {
            console.info( "DONE: " + suite.description );
          }
        },
        reportSpecResults : function( spec ) {
          if( !spec.results().passed() ) {
            var description = [ spec.description ];
            var suite = spec.suite;
            while( suite ) {
              description.push( suite.description );
              suite = suite.parentSuite;
            }
            console.warn( "FAILED: " + description.reverse().join( " " ) );
            window.alert( "Tests Failed!" );
            abort();
          }
        }
      } );
    } else {
      var trashId = 0; //counting up the DOM elements that were not cleaned up
      var allowedCreations = {
        "rwt.widgets.base.ClientDocumentBlocker" : true,
        "rwt.widgets.base.WidgetToolTip" : true,
        "rwt.widgets.base.Image" : true // very likely a cursor created by DragAndDropHandler.js
      };
      var htmlReporter = new jasmine.HtmlReporter();
      jasmineEnv.addReporter( htmlReporter );
      jasmineEnv.specFilter = function( spec ) {
        return htmlReporter.specFilter( spec );
      };
      // Some minor enhancements to HtmlReporter:
      var currentChildren;
      jasmineEnv.addReporter( {
        reportRunnerStarting : function() {
          currentChildren = document.body.childNodes.length;
        },
        reportSpecResults : function( spec ) {
          if( !spec.results().passed() ) {
            // abort to allow inspection, following tests would not have a "blank slate" anyway
            abort();
          }
          updateScrollPositions();
          checkForDirtyDOM();
        }
      } );
    }

    ////////
    // Start

    rwt.runtime.System.getInstance().addEventListener( "uiready", function() {
      org.eclipse.rwt.test.fixture.Fixture.setup();
      org.eclipse.rwt.test.LegacyAsserts.createShortcuts();
      convertOldTests( findOldTests() );
      window.removeEventListener( "error", window.loaderrorHandler, false );
      // Avoid running tests if not there were errors during loading (possibly tests are missing)
      if( window.loaderrors.length > 0 ) {
        window.alert( window.loaderrors.length + " Error(s) during test loading" );
      } else {
        window.setTimeout( function() {
          jasmineEnv.execute();
        }, 0 );
      }
    } );

    /////////////////
    // Global Helpers

    window.any = jasmine.any;

    window.mock = function( constr, name ) {
      if( constr.$$initializer ) {
        rwt.qx.Class.__initializeClass( constr );
      }
      var result = createObjectWithPrototype( constr.prototype );
      result.jasmineToString = function() {
        return name ? "mock of " + name : "mock";
      };
      for( var key in constr.prototype ) {
        if( constr.prototype[ key ] instanceof Function ) {
          result[ key ] = jasmine.createSpy( ( name || "mock" ) + '.' + key );
        }
      }
      return result;
   };

    window.same = function( expected ) {
      return {
        jasmineMatches : function( actual ) {
          return expected === actual;
        }
      };
    };

    /////////
    // Helper

    function createObjectWithPrototype( proto ) {
      var HelperConstr = new Function();
      HelperConstr.prototype = proto;
      return new HelperConstr();
    }

    function updateScrollPositions() {
      // Some tests focus something off-screen, causing scrolling away form the stats
      document.getElementById( "HTMLReporter" ).scrollIntoView();
      if( document.getElementsByClassName ) {
        // This lets the summary work as a progress indicator even when formatted to into one line
        var summary = document.getElementsByClassName( "symbolSummary" )[ 0 ];
        var passed = summary.getElementsByClassName( "passed" );
        if( passed.length > 1 ) {
          var lastPassed = passed[ passed.length - 1 ];
          summary.scrollLeft = lastPassed.offsetLeft - Math.round( summary.clientWidth / 2 );
        }
      }
    }

    /*global console:false */
    function checkForDirtyDOM() {
      if( currentChildren < document.body.childNodes.length ) {
        var element = document.body.lastChild;
        var className = element.rwtWidget ? element.rwtWidget.classname : "";
        if( !allowedCreations[ className ] ) {
          var spec = jasmine.getEnv().currentSpec;
          var currentTest = spec.suite.description + " " + spec.description;
          var id = "trash_" + trashId;
          if( window.console ) {
            console.warn(   currentTest
                          + " did not clean up the DOM properly - "
                          + id
                          + " "
                          + className );
          }
          document.body.lastChild.setAttribute( "id", id );
          trashId++;
          currentChildren = document.body.childNodes.length;
        }
      }
    }

    function findOldTests() {
      var classes = rwt.qx.Class.__registry;
      var filter = createTestClassFilter();
      var shortName;
      var testClasses = [];
      for( var clazz in classes ) {
        if( clazz.substr( clazz.length - 4 ) == "Test" ) {
          rwt.qx.Class.__initializeClass( classes[ clazz ] );
          if( filter( clazz ) ) {
            testClasses.push( classes[ clazz ] );
          }
        }
      }
      return testClasses;
    }

    function convertOldTests( oldTests ) {
      for( var i = 0; i < oldTests.length; i++ ) {
        convertOldTestClass( oldTests[ i ] );
      }
    }

    function convertOldTestClass( TestClass ) {
      var testInstance = new TestClass();
      var testNames = getTestFunctions( testInstance );
      describe( testInstance.classname, function() {
        beforeEach( function() {
          if( testInstance.setUp ) {
            testInstance.setUp();
          }
        } );
        afterEach( function() {
          if( testInstance.tearDown ) {
            testInstance.tearDown();
          }
          org.eclipse.rwt.test.fixture.Fixture.reset();
        } );
        for( var i = 0; i < testNames.length; i++ ) {
          if( testInstance[ testNames[ i ] ] instanceof Function ) {
            convertTestFunction( testInstance, testNames[ i ] );
          } else if( testInstance[ testNames[ i ] ] instanceof Array ) {
            convertMultiPartTest( testInstance, testNames[ i ] );
          }
        }
      } );
    }

    function convertTestFunction( testInstance, testName ) {
      it( testName, function() {
        testInstance[ testName ]();
      } );
    }

    function convertMultiPartTest( testInstance, testName ) {
      var parts = testInstance[ testName ];
      it( testName, function() {
        for( var i = 0; i < parts.length; i++ ) {
          runs( wrapTestPart( testInstance, parts[ i ] ) );
          if( i < parts.length - 1 ) {
            waitsFor( org.eclipse.rwt.test.fixture.TestUtil.shouldContinueTest,
                      "does not support to wait that long",
                      10000000 );
          }
        }
      } );
    }

    function wrapTestPart( instance, f ) {
      return function() {
        var args = org.eclipse.rwt.test.fixture.TestUtil.getStored();
        org.eclipse.rwt.test.fixture.TestUtil.store( null );
        f.apply( instance, args ? args : [] );
        org.eclipse.rwt.test.fixture.TestUtil.flush();
      };
    }

    function abort() {
      freezeQooxdoo();
      window.setTimeout( function() {
        if( document.getElementsByClassName ) {
          var menuItem = document.getElementsByClassName( "detailsMenuItem" )[ 0 ];
          if( menuItem ) {
            menuItem.click();
          }
        }
      }, 0 );
      throw new Error( "Abort Jasmine Runner" ); // Is there a better method to stop jasmine?
    }

    function freezeQooxdoo() {
      rwt.widgets.base.Widget.__allowFlushs = false;
      rwt.event.EventHandler.detachEvents();
      rwt.qx.Target.prototype.dispatchEvent = function(){};
      rwt.animation.Animation._stopLoop();
      if( rwt.client.Client.supportsTouch() ) {
        rwt.runtime.MobileWebkitSupport._removeListeners();
      }
    }

    function getTestFunctions( obj ){
      var testFunctions = [];
      for( var key in obj ) {
        if( key.substr( 0, 4 ) === "test" ) {
          testFunctions.push( key );
        }
      }
      return testFunctions;
    }

    function getShortClassName( src ) {
      var result = src.replace( /^.*?(\w+)\.js.*$/, "$1" );
      result = result.split( "." ).pop();
      return result;
    }

    function createTestClassFilter() {
      var classes = rwt.qx.Class.__registry;
      var platform = rwt.client.Client.getPlatform();
      return function( clazz ) {
        var result = true;
        if( classes[ clazz ].prototype.TARGETPLATFORM instanceof Array ) {
          var targetPlatform = classes[ clazz ].prototype.TARGETPLATFORM;
          result = targetPlatform.indexOf( platform ) != -1;
        }
        return result;
      };
    }

  }

};
