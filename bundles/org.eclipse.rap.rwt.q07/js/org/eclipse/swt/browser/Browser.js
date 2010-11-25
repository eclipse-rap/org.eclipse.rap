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

    executedFunctionName : {
      check : "String",
      nullable : true,
      init : null
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
      }
      var req = org.eclipse.swt.Request.getInstance();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( this );
      req.addParameter( id + ".executeResult", success );
      req.addParameter( id + ".evaluateResult", result );
      req.send();
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
      var window = this.getContentWindow();
      if( window == null || !this.isLoaded() ) {
        qx.client.Timer.once( function() {
          this.createFunction( name );
        }, this, 100 );
      } else {
        var req = org.eclipse.swt.Request.getInstance();
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var that = this;
        try {
          window[ name ] = function() {
            var args = that.objectToString( arguments );
            req.addParameter( id + ".executeFunction", name );
            req.addParameter( id + ".executeArguments", args );
            that.setExecutedFunctionName( name );
            that.setExecutedFunctionResult( null );
            that.setExecutedFunctionError( null );
            req.sendSyncronous();            
            var error = that.getExecutedFunctionError();
            if( error != null ) {
              throw new Error( error );
            }
            return that.getExecutedFunctionResult();
          }
        } catch( e ) {
          this.warn( "Unable to create function: " + name + " error: " + e );
        }
      }
    },

    destroyFunction : function( name ) {
      var window = this.getContentWindow();
      if( window != null ) {
        try {
          if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
            var script = "window." + name + " = undefined";
            window.execScript( script , "JScript" );
          } else {
            var script = "delete window." +  name;
            window.eval( script );
          }
        } catch( e ) {
          this.warn( "Unable to destroy function: " + name + " error: " + e );
        }
      }
    },

    setFunctionResult : function( result, error ) {      
      this.setExecutedFunctionResult( result );
      this.setExecutedFunctionError( error );
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
