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
    this._testClasses = null;
    this._currentClass = null;
    this._currentFunction = null;
    this._log = null;
    this._presenter = null;
    this._testsTotal = 0;
    this._asserts = 0;
    var classes = qx.Class.__registry;
    this._testClasses = {};
    for( var clazz in classes) {
      if( clazz.substr( clazz.length - 4 ) == "Test" ) {
          this._testClasses[clazz] = classes[clazz];
          this._testsTotal++;
      }
    }    
    this._presenter = org.eclipse.rwt.test.Presenter.getInstance();    
    // helper for debugging
    getLog = function() {
      return org.eclipse.rwt.test.TestRunner.getInstance().getLog();
    }
    //temporarily setting this to true can help debugging in IE
    this._NOTRYCATCH = false; 
  },


  members : {
  	
  	run : function() {
  	  // prevent flush by timer
  	  this._disableAutoFlush();
  	  // prevent actual dom-events
  	  qx.event.handler.EventHandler.getInstance().detachEvents();
      this.info( "Starting tests..." );
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
        this.info( " -----===== " + this._currentClass + " =====-----");      	
      	var obj = null;
      	this._currentFunction = "construct";
      	if( this._NOTRYCATCH ) {
          obj = new this._testClasses[ this._currentClass ]();
          var testFunctions = this._getTestFunctions( obj );      
          for ( this._currentFunction in testFunctions ){   
            this._asserts = 0;       	
            testFunctions[ this._currentFunction ].call( obj );
            this.info( this._currentFunction + " - OK " );
          }      	  
      	}  else {    	
          try {
            obj = new this._testClasses[ this._currentClass ]();
            var testFunctions = this._getTestFunctions( obj );      
            for ( this._currentFunction in testFunctions ){   
              this._asserts = 0;       	
              testFunctions[ this._currentFunction ].call( obj );
              this.info( this._currentFunction + " - OK " );
            }
          } catch( e ) {
            // a test failed:          
            this._freezeQooxdoo();
            this._presenter.setFailed( true );
            this.info( this._currentFunction + " failed:" );
            this.info( e );          
            this.info( this._asserts + " asserts succeeded." );          
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
      this.info( " -----===== Tests done! =====-----");
      this.info( "ALL TESTS SUCCEEDED!" );
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
                           + expected
                           + '" but found "'
                           + value
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
    
    info : function( text ) {
      this.base( arguments, text );
      this._presenter.log( text );
    }
    
  }
});