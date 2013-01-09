/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.rwt.test.TestRunner", {
  extend : rwt.qx.Target,
  type : "singleton",

  construct : function() {
    this.base( arguments );
    this._FREEZEONFAIL = true;
    this._NOTRYCATCH = this._getURLParam( "notry" ) !== null;
    this._FULLSCREEN = true;
    this._FILTERCHAR = "$";
    this._presenter = org.eclipse.rwt.test.Presenter.getInstance();
    this._presenter.setFullScreen( this._FULLSCREEN );
    this._testClasses = [];
    this._testFunctions = [];
    this._currentClass = 0;
    this._currentInstance = null;
    this._currentFunction = 0;
    this._args = [];
    this._pause = null;
    this._failed = false;
    this._log = null;
    this._asserts = 0;
    this._loopWrapper = null;
    var testScripts = this._getTestScripts();
    var classes = rwt.qx.Class.__registry;
    var filter = this._createTestClassFilter();
    var shortName;
    for( var clazz in classes ) {
      if( clazz.substr( clazz.length - 4 ) == "Test" ) {
        rwt.qx.Class.__initializeClass( classes[ clazz ] );
        shortName = this._getShortClassName( clazz );
        if( testScripts[ shortName ] ) {
          delete testScripts[ shortName ];
        } else {
          var msg = "TestClass " + clazz + " does not match filename.";
          this._criticalFail( msg );
        }
        if( filter( clazz ) ) {
          this._testClasses.push( classes[ clazz ] );
        }
      }
    }
    for( var script in testScripts ) {
      this._criticalFail( "File " + script + ".js could not be parsed. " +
                          "Probably the file contains corrupted JavaScript." );
    }
    // helper for debugging
    getLog = function() {
      return org.eclipse.rwt.test.TestRunner.getInstance().getLog();
    };
  },

  members : {

    run : function() {
      this._prepare();
      this._initTest();
      this._loopWrapper();
    },

    pause : function( value ) {
      this._pause = value;
    },

    setArguments : function( args ) {
      this._args = args;
    },

    ////////////
    // Internals

    _loop : function() {
      this._executeTestFunction();
      if( this._iterate() ) {
        var time = 5;
        if( this._pause !== null ) {
          time = this._pause;
          this._pause = null;
        }
        window.setTimeout( this._loopWrapper, time );
      }
    },

    _iterate : function() {
      var result = true;
      if( this._failed ) {
        result = false;
      } else {
        this._currentFunction++;
        if( this._currentFunction == this._testFunctions.length ) {
          this._currentClass++;
          this._testFinished();
          if( this._currentClass == this._testClasses.length ) {
            this._allFinished();
            result = false;
          } else {
            this._initTest();
          }
        }
      }
      return result;
    },

    _prepare : function() {
      // prevent flush by timer
      this._disableAutoFlush();
      org.eclipse.rwt.test.fixture.TestUtil.initRequestLog();
      org.eclipse.rwt.test.fixture.TestUtil.prepareTimerUse();
      // prevent actual dom-events
      rwt.event.EventHandler.detachEvents();
      org.eclipse.rwt.test.fixture.TestUtil.initErrorPageLog();
      var that = this;
      this._loopWrapper = function(){ that._loop(); };
      this.info( "Found " + this._testClasses.length + " Tests.", false );
      this.info( "Starting tests...", false );
    },

    _initTest : function() {
      this._presenter.setNumberTestsFinished( this._currentClass + 0.5 , this._testClasses.length );
      var className = this._testClasses[ this._currentClass ].classname;
      this._presenter.log( '', false );
      this.info( "+ " + className, false, "filter=" + className.split( "." ).pop() );
      this._currentInstance = null;
      this._createTestInstance();
      this._testFunctions = this._getTestFunctions( this._currentInstance );
      this._currentFunction = 0;
    },

    _testFinished : function() {
      this._args = [];
      this._currentInstance.dispose();
      this._presenter.setNumberTestsFinished( this._currentClass, this._testClasses.length );
    },

    _allFinished : function() {
      this.info( '', false );
      this.info( "Tests done.", false );
      rwt.runtime.MobileWebkitSupport._removeListeners();
    },

    _executeTestFunction : function() {
      this._asserts = 0;
      var test = this._testFunctions[ this._currentFunction ];
      var fun;
      var setup = true;
      var teardown = true;
      if( test instanceof Array ) {
        var testName = test[ 0 ];
        var testPart = test[ 1 ];
        if( testPart !== 0 ) {
          setup = false;
        }
        if( testPart !== this._currentInstance[ testName ].length - 1 ) {
          teardown = false;
        }
        fun = this._currentInstance[ test[ 0 ] ][ test[ 1 ] ];
      }  else {
        fun = this._currentInstance[ test ];
      }
      if( this._NOTRYCATCH ) {
        this._executeTest( fun, setup, teardown );
        if( !this._failed ) {
          this._cleanUp();
          this.info( test + " - OK ", true, this._getCurrentTestLink() );
        }
      } else {
        try {
          this._executeTest( fun, setup, teardown );
          if( !this._failed ) {
            this._cleanUp();
            this.info( test + " - OK ", true, this._getCurrentTestLink() );
          }
        } catch( e ) {
          this._handleException( e );
        }
      }
    },

    _executeTest : function( fun, setup, teardown ) {
      if( setup && this._currentInstance.setUp instanceof Function ) {
        // TODO [tb] : execute setUp/tearDown not between multipart tests
        this._currentInstance.setUp();
      }
      fun.apply( this._currentInstance, this._args );
      if( teardown && this._currentInstance.tearDown instanceof Function ) {
        this._currentInstance.tearDown();
      }
    },

    _createTestInstance : function() {
      if( this._NOTRYCATCH ) {
        this._currentInstance = new this._testClasses[ this._currentClass ]();
      } else {
        try {
          this._currentInstance = new this._testClasses[ this._currentClass ]();
        } catch( e ) {
          this._handleException( e );
        }
      }
    },

    _handleException : function( e ) {
      if( this._FREEZEONFAIL ) {
        this._freezeQooxdoo();
      }
      this._presenter.setFailed( true );
      this._failed = true;
      var classname = this._testFunctions[ this._currentFunction ];
      this.info( classname + " failed:", true, this._getCurrentTestLink() );
      try{
        if( e.msg ) {
          this.info( e.msg, false );
        } else if( e.message ) {
          this.info( e.message, false );
        } else {
          this.info( e, false );
        }
        if( e.stack ) {
          this.info( e.stack, false );
        }
      } catch( ex ) {
        this.info( e, false );
      }
      this.info( this._asserts + " asserts succeeded.", false );
      this._createFailLog( e, this._currentInstance );
      this._checkFlushState();
      this.info( "Tests aborted!", false );
    },

    _cleanUp : function() {
      if( this._pause === null ) {
        org.eclipse.rwt.test.fixture.TestUtil.initRequestLog();
        org.eclipse.rwt.test.fixture.TestUtil.clearTimerOnceLog();
        org.eclipse.rwt.test.fixture.TestUtil.restoreAppearance();
        org.eclipse.rwt.test.fixture.TestUtil.emptyDragCache();
        org.eclipse.rwt.test.fixture.TestUtil.resetEventHandler();
        org.eclipse.rwt.test.fixture.TestUtil.cleanUpKeyUtil();
        org.eclipse.rwt.test.fixture.TestUtil.clearErrorPage();
        org.eclipse.rwt.test.fixture.TestUtil.resetObjectManager();
        org.eclipse.rwt.test.fixture.TestUtil.resetWindowManager();
        org.eclipse.rwt.test.fixture.TestUtil.clearXMLHttpRequests();
        rwt.event.EventHandler.setFocusRoot(
          rwt.widgets.base.ClientDocument.getInstance()
        );
      }
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    // called by Asserts.js
    processAssert : function( assertType, expected, value, isFailed, message ) {
      if( !this._failed ) {
        if( isFailed ) {
          var expectedString;
          var valueString;
          if( assertType === "assertEquals" ) {
            var expectedString = this._getObjectSummary( expected );
            var valueString = this._getObjectSummary( value );
          } else {
            var expectedString = expected + "";
            var valueString = value + "";
          }
          var errorMessage =   'Assert "'
                             + ( message ? message : this._asserts + 1 )
                             + '", type "'
                             + assertType
                             + '" failed : Expected "'
                             + expectedString
                             + '" but found "'
                             + valueString
                             + '"';
          var error = {
            "assert" : true,
            "testClass" : this._testClasses[ this._currentClass ].classname,
            "testFunction" : this._currentFunction,
            "expected" : expected,
            "actual" : value,
            "msg" : errorMessage,
            toString : function() {
              return this.msg;
            }
          };
          throw( error );
        } else {
          this._asserts++;
        }
      }
    },

    _getObjectSummary : function( value ) {
      var result = value;
      try {
        if( value instanceof Array ) {
          result = value.join();
        } else if(    value instanceof Object
                   && !( value instanceof rwt.qx.Object ) )
        {
          var arr = [];
          for( var key in value ) {
            arr.push( " " + key + " : " + value[ key ] );
          }
          result = "{" + arr.join() + " }";
        }
      } catch( ex ) {
        // iterating over Objects might fail, keep result as is
      }
      return result;
    },

    _criticalFail : function( msg ) {
      this._presenter.log( "Critical error: " + msg, false );
      this._presenter.log( "Testrunner aborted." , false );
      this._presenter.setFailed( true );
      this._presenter.setNumberTestsFinished( 0, 0 );
      throw msg;
    },

    _createFailLog : function( e, testInstance ) {
      this._log = {};
      this._log.error = e;
      this._log.asserts = this._asserts;
      this._log.obj = testInstance;
      this._log.currentFunction = this._currentFunction;
    },

    _checkFlushState : function() {
      if( rwt.widgets.base.Widget._inFlushGlobalQueues ) {
        this.info( "Error occurred during Flush!");
        if( rwt.widgets.base.Widget._globalWidgetQueue.length > 0 ) {
          this.info( "There are widgets in _globalWidgetQueue!" );
        }
        if( rwt.widgets.base.Widget._globalElementQueue.length > 0 ) {
          this.info( "There are widgets in _globalElementQueue!" );
        }
        if( rwt.widgets.base.Widget._globalStateQueue.length > 0 ) {
          this.info( "There are widgets in _globalStateQueue!" );
        }
        if( rwt.widgets.base.Widget._globalJobQueue.length > 0 ) {
          this.info( "There are widgets in _globalJobQueue!" );
        }
        if( rwt.widgets.base.Widget._globalLayoutQueue.length > 0 ) {
          this.info( "There are widgets in _globalLayoutQueue!" );
        }
        if( rwt.widgets.base.Widget._fastGlobalDisplayQueue.length > 0 ) {
          this.info( "There are widgets in _fastGlobalDisplayQueue!" );
        }
        if( rwt.widgets.base.Widget._lazyGlobalDisplayQueues.length > 0 ) {
          this.info( "There are widgets in _lazyGlobalDisplayQueues!" );
        }
        if( rwt.widgets.base.Widget._globalDisposeQueue.length > 0 ) {
          this.info( "There are widgets in _globalDisposeQueue!" );
        }
      }
    },

    _freezeQooxdoo : function() {
      rwt.widgets.base.Widget.__allowFlushs = false;
      rwt.event.EventHandler.detachEvents();
      rwt.qx.Target.prototype.dispatchEvent = function(){};
      rwt.animation.Animation._stopLoop();
      rwt.runtime.MobileWebkitSupport._removeListeners();
    },

    _disableAutoFlush : function() {
      rwt.widgets.base.Widget._removeAutoFlush();
      rwt.widgets.base.Widget._initAutoFlush = function(){};
      rwt.widgets.base.Widget.__orgFlushGlobalQueues = rwt.widgets.base.Widget.flushGlobalQueues;
      rwt.widgets.base.Widget.flushGlobalQueues = function() {
        if( this.__allowFlushs ) {
          rwt.widgets.base.Widget.__orgFlushGlobalQueues();
        }
      };
      rwt.widgets.base.Widget.__allowFlushs = true;
    },

    getLog : function(){
      return this._log;
    },

    _getTestFunctions : function( obj ){
      var testFunctions = [];
      var filterStr = this._getTestInstanceFilterStr( obj );
      for ( var key in obj ) {
        if( key.substr( 0, 4 ) === "test" ) {
          if( filterStr === null || key.indexOf( filterStr ) !== -1 ) {
            if( typeof obj[ key ] === "function" ) {
              testFunctions.push( key );
            } else if( obj[ key ] instanceof Array ) {
              var arr = obj[ key ];
              for( var i = 0; i < arr.length; i++ ) {
                if( typeof arr[ i ] === "function" ) {
                  testFunctions.push( [ key, i ] );
                }
              }
            }
          }
        }
      }
      return testFunctions;
    },

    _getTestScripts : function() {
      var result = {};
      var head = document.documentElement.firstChild;
      if( head.tagName.toLowerCase() != "head" ) {
        throw "could not find <head>";
      }
      for( var i = 0; i < head.childNodes.length; i++ ) {
        var node = head.childNodes[ i ];
        if( node.nodeName.toLowerCase() == "script" ) {
          var src = node.getAttribute( "src" );
          if( src && src.indexOf( "Test.js" ) != -1 ) {
            result[ this._getShortClassName( src ) ] = true;
          }
        }
      }
      return result;
    },

    _getShortClassName : function( src ) {
      var result = src.replace( /^.*?(\w+)\.js.*$/, "$1" );
      result = result.split( "." ).pop();
      return result;
    },

    _createTestClassFilter : function() {
      var classes = rwt.qx.Class.__registry;
      var engine = rwt.client.Client.getEngine();
      var platform = rwt.client.Client.getPlatform();
      var param = this._getFilterParam();
      var filterchar = this._FILTERCHAR;
      var filter = function( clazz ) {
        var result = true;
        if( classes[ clazz ].prototype.TARGETENGINE instanceof Array ) {
          var targetEngine = classes[ clazz ].prototype.TARGETENGINE;
          result = targetEngine.indexOf( engine ) != -1;
        }
        if( classes[ clazz ].prototype.TARGETPLATFORM instanceof Array ) {
          var targetPlatform = classes[ clazz ].prototype.TARGETPLATFORM;
          result = result && targetPlatform.indexOf( platform ) != -1;
        }
        if( result && param != null ) {
          var found = false;
          for( var i = 0; i < param.length; i++ ) {
            var classStr = param[ i ].split( filterchar )[ 0 ];
            if( clazz.indexOf( classStr ) != -1 ) {
              found = true;
            }
          }
          result = found;
        }
        return result;
      };
      return filter;
    },

    _getURLParam : function( name ) {
      var result = null;
      var href = window.location.href;
      var hashes = href.slice( href.indexOf( "?" ) + 1).split( "&" );
      for( var i = 0; i < hashes.length; i++ ) {
        var hash = hashes[ i ].split( "=" );
        if( hash[ 0 ] === name ) {
          result = hash[ 1 ];
        }
      }
      return result;
    },

    _getTestInstanceFilterStr : function( testInstance ) {
      var testClassName = testInstance.classname.split( "." ).pop();
      var param = this._getFilterParam();
      param = param ? param : [];
      var filterStr = null;
      for( var i = 0; i < param.length && filterStr === null; i++ ) {
        if( param[ i ].indexOf( testClassName + this._FILTERCHAR ) !== -1 ) {
          filterStr = param[ i ].split( this._FILTERCHAR )[ 1 ];
        }
      }
      return filterStr ? filterStr : null;
    },

    _getFilterParam : function() {
      var result = null;
      var param = this._getURLParam( "filter" );
      if( param != null ) {
        result = param.split( "," );
      }
      return result;
    },

    _getCurrentTestLink : function() {
      var testClassName = this._currentInstance.classname.split( "." ).pop();
      var testFunctionName = this._testFunctions[ this._currentFunction ];
      return "filter=" + testClassName + this._FILTERCHAR + testFunctionName;
    },

    info : function( text, indent, link ) {
      this._presenter.log( text, indent, link );
    }

  }
} );
