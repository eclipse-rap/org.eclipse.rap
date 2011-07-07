/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.WidgetRenderAdapter", {

  extend : qx.core.Target,

  construct : function( widget ) {
    // Widget is responsible for the dispose:
    this._autoDispose = false;
    this.base( arguments );
    this._widget = widget;
    var key = this.classname;
    if( widget._adapters[ key ] != null ) {
      throw new Error( "Never create WidgetRenderAdapter directly!" );
    }
    widget._adapters[ key ] = this;
  },

  destruct : function() {
    // NOTE: disposing the adapter before the widget is not used or tested.
    this._widget = null;
  },

  events: {
    "visibility" : "qx.event.type.DataEvent"
  },

  members : {
    
    addRenderListener : function( type, listener, context ) {
      var rendererName = this._renderFunctionNames[ type ];
      if( !this.hasEventListeners( type ) ) {
        var that = this;
        this._widget[ rendererName ] = function( value ) {
          // NOTE : Could support multiple parameters using arguments object
          var event = new qx.event.type.DataEvent( type, value );
          var render = that.dispatchEvent( event, true );
          if( render ) {
            this.constructor.prototype[ rendererName ].call( this, value );
          }
        }
      }
      this.addEventListener( type, listener, context );
    },
    
    removeRenderListener : function( type, listener, context ) {
      this.removeEventListener( type, listener, context );
      if( !this.hasEventListeners( type ) ) {      
        var rendererName = this._renderFunctionNames[ type ];
        delete this._widget[ rendererName ];
      }
    },
    
    forceRender : function( type, value ) {
      var rendererName = this._renderFunctionNames[ type ];
      var proto = this._widget.constructor.prototype;
      proto[ rendererName ].call( this._widget, value );
    },

    // TODO [tb]: AnimationRenderer#getValueFromWidget would also fit here 
    
    _renderFunctionNames :  {
      "visibility" : "_applyVisibility"
    }

  }

} );
