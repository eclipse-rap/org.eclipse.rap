/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define("org.eclipse.rwt.test.TestRunner", {
  extend : qx.core.Target,
  type : "singleton",

  construct : function() {
    this.base( arguments );
    qx.log.Logger.ROOT_LOGGER.setMinLevel( qx.log.Logger.LEVEL_ERROR );
    this._FREEZEONFAIL = true; 
    this._NOTRYCATCH = false; 
    this._FULLSCREEN = true;
    this._presenter = org.eclipse.rwt.test.Presenter.getInstance();
    this._presenter.setFullScreen( this._FULLSCREEN );    
    this._testClasses = null;
    this._currentClass = null;
    this._currentFunction = null;
    this._log = null;
    this._testsTotal = 0;
    this._asserts = 0;
    var classes = qx.Class.__registry;
    this._testClasses = {};
    var testScripts = this._getTestScripts();
    var engine = qx.core.Client.getEngine();
    var skip;
    var shortName;
    for( var clazz in classes) {
      if( clazz.substr( clazz.length - 4 ) == "Test" ) {
        shortName = this._getShortClassName( clazz );
        if( testScripts[ shortName ] ) {
          delete testScripts[ shortName ];
        } else {
          var msg = "TestClass " + clazz + " does not match filename.";
          this._criticalFail( msg );
        }
        skip = false;       
        if( classes[ clazz ].prototype.TARGETENGINE instanceof Array ) {
          var targetEngine = classes[ clazz ].prototype.TARGETENGINE;
          skip = targetEngine.indexOf( engine ) == -1;
        }
        if( !skip ) {
          this._testClasses[clazz] = classes[clazz];
          this._testsTotal++;
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
    }
  },

  members : {
  	
  	run : function() {
  	  // prevent flush by timer
  	  this._disableAutoFlush();
  	  // prevent actual dom-events
  	  qx.event.handler.EventHandler.getInstance().detachEvents();
      this.info( "Starting tests...", false );
      if( this._NOTRYCATCH ) {
        this._run();
      } else {    
    	  try {
      	  this._run();
    	  } catch( e ) { 
    	    this.info( e );      
    	  }
      }
  	},

  	_run : function() {
  		var finished = 0;
      for( this._currentClass in this._testClasses ) {
        this._presenter.setNumberTestsFinished( finished + 0.5 , 
                                                this._testsTotal );
        this._presenter.log( '', false );
        this.info( "+ " + this._currentClass, false );      	
      	var obj = null;
      	this._currentFunction = "construct";
      	// also stop on "this.error":
      	qx.core.Object.prototype.error = function( msg, exc ) {
          this.getLogger().error(msg, this.toHashCode(), exc);
      	  throw msg; 
      	};
      	if( this._NOTRYCATCH ) {
          obj = new this._testClasses[ this._currentClass ]();
          var testFunctions = this._getTestFunctions( obj );      
          for ( this._currentFunction in testFunctions ){   
            this._asserts = 0;       	
            testFunctions[ this._currentFunction ].call( obj );
            this._cleanUp();
            this.info( this._currentFunction + " - OK ", true );
          }      	  
      	}  else {    	
          try {
            obj = new this._testClasses[ this._currentClass ]();
            var testFunctions = this._getTestFunctions( obj );      
            for ( this._currentFunction in testFunctions ){   
              this._asserts = 0;       	
              testFunctions[ this._currentFunction ].call( obj );
              this._cleanUp();
              this.info( this._currentFunction + " - OK ", true );
            }
          } catch( e ) {
            // a test failed:          
            if( this._FREEZEONFAIL ) this._freezeQooxdoo();
            this._presenter.setFailed( true );
            this.info( this._currentFunction + " failed:", true );
            this.info( e, false );          
            this.info( this._asserts + " asserts succeeded.", false );          
            this._createFailLog( e, obj );
            this._checkFlushState();
            throw( "Tests aborted!" );
          }
      	}          
        qx.ui.core.ClientDocument.getInstance().removeAll();
        obj.dispose();
        finished++;
        this._presenter.setNumberTestsFinished( finished, this._testsTotal );
      }
      this.info( '', false );
      this.info( "Tests done.", false );
  	},
  	
  	_cleanUp : function() {
  	  org.eclipse.rwt.test.fixture.TestUtil.clearRequestLog();
  	  org.eclipse.rwt.test.fixture.TestUtil.clearTimerOnceLog();
  	  org.eclipse.rwt.test.fixture.TestUtil.restoreAppearance();
  	  qx.ui.core.Widget.flushGlobalQueues();
  	},
  	
  	// called by Asserts.js
  	processAssert : function( assertType, expected, value, isFailed, message ) {  	  
      if( isFailed ) {
        var trace = qx.dev.StackTrace.getStackTrace();
        qx.lang.Array.removeAt( trace, 0 );
        var errorMessage =   'Assert "' 
                           + ( message ? message : this._asserts + 1 )
                           + '", type "' 
                           + assertType
                           + '" failed : Expected "'
                           + this._getObjectSummary( expected )
                           + '" but found "'
                           + this._getObjectSummary( value )
                           + '"';
        var error = {
          "assert" : true,
          "testClass" : this._currentClass, 
          "testFunction" : this._currentFunction, 
          "trace" : trace,
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
  	},
  	
  	_getObjectSummary : function( value ) {
  	  var result = value;
  	  if( value instanceof Array ) {
  	    result = value.join();
  	  } else if(    value instanceof Object 
  	             && !( value instanceof qx.core.Object ) )
  	  {
  	    var arr = [];
  	    for( var key in value ) {
  	      arr.push( " " + key + " : " + value[ key ] ); 
  	    }
  	    result = "{" + arr.join() + " }";
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
      try {
        var trace = this._getStackTrace( e );          
        this._log.trace = trace;
        this.getLogger().log(
          qx.log.Logger.LEVEL_INFO, 
          "Stack trace:", 
          "",
          null,
          trace
        );
      } catch( e ) {
        this._log.trace = e;
      }
  	},
  	
  	_checkFlushState : function() {
      if( qx.ui.core.Widget._inFlushGlobalQueues ) {
        this.info( "Error occurred during Flush!");
        if( qx.ui.core.Widget._globalWidgetQueue.length > 0 ) {
          this.info( "There are widgets in _globalWidgetQueue!" );
        }
        if( qx.ui.core.Widget._globalElementQueue.length > 0 ) {
          this.info( "There are widgets in _globalElementQueue!" );
        }
        if( qx.ui.core.Widget._globalStateQueue.length > 0 ) {
          this.info( "There are widgets in _globalStateQueue!" );
        }
        if( qx.ui.core.Widget._globalJobQueue.length > 0 ) {
          this.info( "There are widgets in _globalJobQueue!" );
        }
        if( qx.ui.core.Widget._globalLayoutQueue.length > 0 ) {
          this.info( "There are widgets in _globalLayoutQueue!" );
        }
        if( qx.ui.core.Widget._fastGlobalDisplayQueue.length > 0 ) {
          this.info( "There are widgets in _fastGlobalDisplayQueue!" );
        }
        if( qx.ui.core.Widget._lazyGlobalDisplayQueues.length > 0 ) {
          this.info( "There are widgets in _lazyGlobalDisplayQueues!" );
        }
        if( qx.ui.core.Widget._globalDisposeQueue.length > 0 ) {
          this.info( "There are widgets in _globalDisposeQueue!" );
        }
      }
  	},
  	
  	_freezeQooxdoo : function() {
      qx.ui.core.Widget.__allowFlushs = false;
      qx.event.handler.EventHandler.getInstance().detachEvents();
      qx.core.Target.prototype.dispatchEvent = function(){};
  	},
  	
    _disableAutoFlush : function() {
      qx.ui.core.Widget._removeAutoFlush();
      qx.ui.core.Widget._initAutoFlush = function(){};
      qx.ui.core.Widget.__orgFlushGlobalQueues 
       = qx.ui.core.Widget.flushGlobalQueues;
      qx.ui.core.Widget.flushGlobalQueues = function() {
        if( this.__allowFlushs ) {
          qx.ui.core.Widget.__orgFlushGlobalQueues();
        }
      }
      qx.ui.core.Widget.__allowFlushs = true;
    },
    
  	_getStackTrace : function( e ) {
  	  var trace = null;
      if( e.trace ) {
        trace = e.trace;
      } else {
        var fromErr = [];
        if( !qx.core.Variant.isSet( "qx.client", "webkit" ) ) {
          // this somehow crashes webkit          
          fromErr = qx.dev.StackTrace.getStackTraceFromError( e );
        }
        if( fromErr.length > 0 ) {
          trace = fromErr;
        } else {
          trace = qx.dev.StackTrace.getStackTrace();                
        }
      }
      return trace;
  	},
  	
    
    getLog : function(){
    	return this._log;
    },
    
    _getTestFunctions : function( obj ){
      var testFunctions = {};
      for ( var fun in obj ) {
        if(    fun.substr( 0, 4 ) == "test" 
            && typeof obj[ fun ] == "function") 
         {
          testFunctions[ fun ] = obj[ fun ];
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
          if( src && src.indexOf( "Test.js" ) == ( src.length - 7) ) {
            result[ this._getShortClassName( src ) ] = true;
          } 
        }
      }
      return result;
    },
    
    _getShortClassName : function( src ) {
      var result = src;
      var separator = ".";
      if( result.toLowerCase().slice( -3 ) == ".js" ) {
        result = result.substr( 0, result.length - 3 );
        separator = "/";
      }
      var splitted = result.split( separator );
      result = splitted.pop();
      return result;
   },
   
    info : function( text, indent ) {
      this.base( arguments, text );
      this._presenter.log( text, indent );
    }
    
  }
});