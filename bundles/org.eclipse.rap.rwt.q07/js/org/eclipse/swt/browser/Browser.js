/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.browser.Browser", {
  extend : qx.ui.embed.Iframe,

  construct : function() {
    this.base( arguments );
    this._hasProgressListener = false;
    // TODO [rh] preliminary workaround to make Browser accessible by tab
    this.setTabIndex( 1 );
    this.setAppearance( "browser" );
    this.addEventListener( "load", this._onLoad, this );
  },

  destruct : function() {
    this.removeEventListener( "load", this._onLoad, this );
  },
  
  properties : {
  
    asynchronousResult : {
      check : "Boolean",
      init : false
    },

    executedFunctionPending : {
      check : "Boolean",
      init : false
    },
    
    executedFunctionResult : {
      nullable : true,
      init : null
    },
    
    executedFunctionError : {
      check : "String",
      nullable : true,
      init : null
    }
    
  },

  members : {

    _onLoad : function( evt ) {
      this.release();
      this._sendProgressEvent();
    },
    
    _sendProgressEvent : function() {
      if( this._hasProgressListener ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = widgetManager.findIdByWidget( this );
        req.addEvent( "org.eclipse.swt.events.progressCompleted", id );
        req.send();
      }
    },
    
    setHasProgressListener : function( value ) {
      this._hasProgressListener = value;
    },

    execute : function( script ) {
      var success = true;
      var result = null;
      try {
        result = this._parseEvalResult( this._eval( script ) );
      } catch( e ) {
        success = false;
        this.warn( "Browser execute failed: " + e );
      }
      var req = org.eclipse.swt.Request.getInstance();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( this );
      req.addParameter( id + ".executeResult", success );
      req.addParameter( id + ".evaluateResult", result );
      if( this.getExecutedFunctionPending() ) {
        req.sendSyncronous();
      } else {
        req.send();
      }
    },
    
    _eval : function( script ) {
      var win = this.getContentWindow();
      if( !win.eval && win.execScript ) {
        // Workaround for IE bug, see: http://www.thismuchiknow.co.uk/?p=25
        win.execScript( "null;", "JScript" );
      }
      return win.eval( script );
    },
    
    _parseEvalResult : function( value ) {
      var result = null;
      var win; 
      if( qx.core.Variant.isSet( "qx.client", "gecko" ) ) {
        // in gecko the prototypes from the parent-frame are used
        win = window;
      } else {
        win = this.getContentWindow();
      }
      // NOTE: This mimics the behavior of the evaluate method in SWT:
      if( value instanceof win.Function ) {
        result = this.objectToString( [ [] ] );
      } else if( value instanceof win.Array ) {
        result = this.objectToString( [ value ] );
      } else if( typeof value !== "object" && typeof value !== "function" ) {
        // above: some browser say regular expressions of the type "function"
        result = this.objectToString( [ value ] );
      }
      return result;
    },

    createFunction : function( name ) {
      var win = this.getContentWindow();
      if( win == null || !this.isLoaded() ) {
        qx.client.Timer.once( function() {
          this.createFunction( name );
        }, this, 100 );
      } else {
        try {
          this._createFunctionImpl( name );
          this._createFunctionWrapper( name );
        } catch( e ) {
          this.warn( "Unable to create function: " + name + " error: " + e );
        }
      }
    },

    _createFunctionImpl : function( name ) {
      var win = this.getContentWindow();
      var req = org.eclipse.swt.Request.getInstance();
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var that = this;
      win[ name + "_impl" ] = function() {
        var result = {};
        if( that.getExecutedFunctionPending() ) {
          result.error = "Unable to execute browser function \""
                       + name
                       + "\". Another browser function is still pending.";
        } else {
          var args = that.objectToString( arguments );
          req.addParameter( id + ".executeFunction", name );
          req.addParameter( id + ".executeArguments", args );
          that.setExecutedFunctionResult( null );
          that.setExecutedFunctionError( null );
          that.setExecutedFunctionPending( true );
          that.setAsynchronousResult( false );
          req.sendSyncronous();
          if( that.getExecutedFunctionPending() ) {
            that.setAsynchronousResult( true );
          } else {
            var error = that.getExecutedFunctionError();
            if( error != null ) {
              result.error = error;
            } else {
              result.result = that.getExecutedFunctionResult();
            }
          }
        }
        return result;
      }
    },

    // [if] This wrapper function is a workaround for bug 332313
    _createFunctionWrapper : function( name ) {
      var script = [];
      script.push( "function " + name + "(){" );
      script.push( "  var result = " + name + "_impl.apply( window, arguments );" );
      script.push( "  if( result.error ) {" );
      script.push( "    throw new Error( result.error );" );
      script.push( "  }" );
      script.push( "  return result.result;" );
      script.push( "}");
      this._eval( script.join( "" ) );
    },

    destroyFunction : function( name ) {
      var win = this.getContentWindow();
      if( win != null ) {
	      try {
	        var script = [];
	        if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
	          script.push( "window." + name + " = undefined;" );
	          script.push( "window." + name + "_impl = undefined;" );
	        } else {
	          script.push( "delete window." +  name + ";" );
	          script.push( "delete window." +  name + "_impl;" );
	        }
	        this._eval( script.join( "" ) );
	      } catch( e ) {
	        this.warn( "Unable to destroy function: " + name + " error: " + e );
	      }
	    }
    },

    setFunctionResult : function( name, result, error ) {
      this.setExecutedFunctionResult( result );
      this.setExecutedFunctionError( error );
      this.setExecutedFunctionPending( false );
    },

    objectToString : function( object ) {
      var result;
      var type = typeof( object );
      if( object === null ) {
        result = String( object );
      } else if( type == "object" ) {
        result = [];
        for( var i = 0; i < object.length; i++ ) {
          var value = object[ i ];
          type = typeof( value );
          if( type == "string" ) {
            value = '"' + value.replace( "\"", "\\\"" ) + '"';
          } else if( type == "object" && value !== null ) {
            value = this.objectToString( value );
          }
          result.push( String( value ) );
        }
        result = "[" + String( result ) + "]";
      } else if( type == "string" ) {
        result = '"' + object.replace( "\"", "\\\"" ) + '"';
      } else {
        result = String( object );
      }
      return result;
    }
  }
});
