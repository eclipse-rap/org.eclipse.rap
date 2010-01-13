/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
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
    },

    execute : function( script ) {
      var result = true;
      try {
        if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
          this.getContentWindow().execScript( script , "JScript" );
        } else {
          this.getContentWindow().eval( script );
        }
      } catch( e ) {
        result = false;
      }
      var req = org.eclipse.swt.Request.getInstance();
      var id = org.eclipse.swt.WidgetManager.getInstance().findIdByWidget( this );
      req.addParameter( id + ".executeResult", result );
      req.send();
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
