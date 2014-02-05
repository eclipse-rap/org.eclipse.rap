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
    var trashId = 0;//counting up the DOM elements that were not cleaned up
    var allowedCreations = {
      "rwt.widgets.base.ClientDocumentBlocker" : true,
      "rwt.widgets.base.WidgetToolTip" : true,
      "rwt.widgets.base.Image" : true // very likely a cursor created by DragAndDropHandler.js
    };
    var htmlReporter = new jasmine.HtmlReporter();
    var currentChildren;

    jasmineEnv.updateInterval = 200;
    jasmineEnv.addReporter( htmlReporter );
    jasmineEnv.specFilter = function( spec ) {
      return htmlReporter.specFilter( spec );
    };

    // Some minor enehancements to HtmlReporter:
    jasmineEnv.addReporter( {
      reportRunnerStarting : function() {
        currentChildren = document.body.childNodes.length;
      },
      reportSpecResults : function( spec ) {
        if( !spec.results().passed() ) {
          // abort to allow inspection
          abort();
        }
        updateScrollPositions();
        checkForDirtyDOM();
      }
    } );

    ////////
    // Start

    rwt.runtime.System.getInstance().addEventListener( "uiready", function() {
      org.eclipse.rwt.test.fixture.Fixture.setup();
      document.body.style.overflow = "scroll"; // qooxdoo disables scrolling, re-enable
      org.eclipse.rwt.test.LegacyAsserts.createShortcuts();
      if( window.location.search.match( /legacy=true/ ) ) {
        convertOldTests( findOldTests() );
      }
      window.setTimeout( function() {
        jasmineEnv.execute();
      }, 0 );
    } );

    /////////
    // Helper

    function updateScrollPositions() {
      // Some tests focus something off-screen, causing scrolling away form the stats
      document.getElementById( "HTMLReporter" ).scrollIntoView();
      // This lets the summary work as a progress indicator even when formatted to into one line
      var summary = document.getElementsByClassName( "symbolSummary" )[ 0 ];
      var passed = summary.getElementsByClassName( "passed" );
      if( passed.length > 1 ) {
        var lastPassed = passed[ passed.length - 1 ];
        summary.scrollLeft = lastPassed.offsetLeft - Math.round( summary.clientWidth / 2 );
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
                          + " did not clean up the DOM propperly - "
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
      rwt.qx.Class.__initializeClass( org.eclipse.rwt.test.TestRunner );
      var runnerProto = org.eclipse.rwt.test.TestRunner.prototype;
      var testScripts = runnerProto._getTestScripts();
      var classes = rwt.qx.Class.__registry;
      // old filter style still supported, jasmine filter is way too slow
      var filter = runnerProto._createTestClassFilter();
      var shortName;
      var testClasses = [];
      for( var clazz in classes ) {
        if( clazz.substr( clazz.length - 4 ) == "Test" ) {
          rwt.qx.Class.__initializeClass( classes[ clazz ] );
          shortName = runnerProto._getShortClassName( clazz );
          if( testScripts[ shortName ] ) {
            delete testScripts[ shortName ];
          } else {
            var msg = "TestClass " + clazz + " does not match filename.";
            throw new Error( msg );
          }
          if( filter( clazz ) ) {
            testClasses.push( classes[ clazz ] );
          }
        }
      }
      for( var script in testScripts ) {
        throw new Error( "File " + script + ".js could not be parsed. " +
                         "Probably the file contains corrupted JavaScript." );
      }
      return testClasses;
    }

    function convertOldTests( oldTests ) {
      for( var i = 0; i < oldTests.length; i++ ) {
        convertOldTestClass( oldTests[ i ] );
      }
    }

    function convertOldTestClass( TestClass ) {
      var runnerProto = org.eclipse.rwt.test.TestRunner.prototype;
      var testInstance = new TestClass();
      var testNames = runnerProto._getTestFunctions( testInstance );
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
          if( testNames[ i ] instanceof Array ) {
            // handle those
          } else {
            convertTestFunction( testInstance, testNames[ i ] );
          }
        }
      } );
    }

    function convertTestFunction( testInstance, testName ) {
      it( testName, function() {
        testInstance[ testName ]();
      } );
    }

    function abort() {
      var runnerProto = org.eclipse.rwt.test.TestRunner.prototype;
      runnerProto._freezeQooxdoo();
      throw new Error( "Abort Jasmine Runner" );
    }

  }

};
