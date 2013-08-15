/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var ObjectRegistry = rwt.remote.ObjectRegistry;

rwt.qx.Class.createNamespace( "rwt.scripting", {} );

rwt.scripting.WidgetProxyFactory = {

  _GC_KEY : "rwt.scripting.WidgetProxyFactory.GC",
  _wrapperMap : {},

  getWidgetProxy : function( obj ) {
    var result = null;
    if( obj instanceof Object ) {
      var hash = rwt.qx.Object.toHashCode( obj );
      if( this._wrapperMap[ hash ] == null ) {
        if( obj instanceof rwt.widgets.Composite ) {
          result = new CompositeWrapper( obj );
        } else {
          result = {};
        }
        this._wrapperMap[ hash ] = result;
        this._initWrapper( obj, result );
      }
      result = this._wrapperMap[ hash ];
    }
    return result;
  },

  _initWrapper : function( originalWidget, wrapper ) {
    this._attachSetter( wrapper, originalWidget );
    this._attachGetter( wrapper, originalWidget );
    if( rwt.remote.WidgetManager.getInstance().isControl( originalWidget ) ) {
      this._attachControlMethods( wrapper, originalWidget );
    }
    originalWidget.addEventListener( "destroy", function() {
      rwt.scripting.WidgetProxyFactory._disposeWidgetProxy( originalWidget );
    } );
  },

  _disposeWidgetProxy : function( widget ) {
    var hash = rwt.qx.Object.toHashCode( widget );
    var proxy = this._wrapperMap[ hash ];
    if( proxy ) {
      var userData = widget.getUserData( rwt.remote.HandlerUtil.SERVER_DATA );
      rwt.scripting.WidgetProxyFactory._disposeObject( proxy );
      rwt.scripting.WidgetProxyFactory._disposeObject( userData );
      delete this._wrapperMap[ hash ];
    }
  },

  _disposeObject : function( object ) {
    for( var key in object ) {
      if( object.hasOwnProperty( key ) ) {
        object[ key ] = null;
      }
    }
  },

  ////////////////////////
  // setter/getter support

  _attachSetter : function( proxy, source ) {
    var id = ObjectRegistry.getId( source );
    var handler = id ? ObjectRegistry.getEntry( id ).handler : null;
    if( handler ) {
      var properties = handler.properties;
      for( var i = 0; i < properties.length; i++ ) {
        var property = properties[ i ];
        proxy[ "set" + rwt.util.Strings.toFirstUp( property ) ] =
          this._createSetter( id, property );
      }
    }
  },

  _attachGetter : function( proxy, source ) {
    var id = ObjectRegistry.getId( source );
    var handler = id ? ObjectRegistry.getEntry( id ).handler : null;
    if( handler ) {
      var methods = handler.scriptingMethods || {};
      for( var name in methods ) {
        proxy[ name ] = rwt.util.Functions.bind( methods[ name ], source );
      }
    }
  },

  _createSetter : function( id, property ) {
    var setProperty = this._setProperty;
    var result = function( value ) {
      setProperty( id, property, value );
    };
    return result;
  },

  _setProperty : function( id, property, value ) {
    var props = {};
    props[ property ] = value;
    rwt.remote.MessageProcessor.processOperation( {
      "target" : id,
      "action" : "set",
      "properties" : props
    } );
  },


  ///////////////////////
  // misc methods support

  _attachControlMethods : function( proxy, source ) {
    var id = ObjectRegistry.getId( source );
    var that = this;
    proxy.redraw = function() {
      that._initGC( source );
    };
  },

  _initGC : function( widget ) {
    var gc = this._getGCFor( widget );
    var width = widget.getInnerWidth();
    var height = widget.getInnerHeight();
    var fillStyle = widget.getBackgroundColor();
    var strokeStyle = widget.getTextColor();
    var font = [[]];
    if( widget.getFont() ) {
      font[ 0 ] = widget.getFont().getFamily();
      font[ 1 ] = widget.getFont().getSize();
      font[ 2 ] = widget.getFont().getBold();
      font[ 3 ] = widget.getFont().getItalic();
    }
    gc.init(
      width,
      height,
      font,
      rwt.util.Colors.stringToRgb( fillStyle ? fillStyle : "#000000" ),
      rwt.util.Colors.stringToRgb( strokeStyle ? strokeStyle : "#000000" )
    );

  },

  _getGCFor : function( widget ) {
    var gc = widget.getUserData( rwt.scripting.WidgetProxyFactory._GC_KEY );
    if( gc == null ) {
      gc = this._findExistingGC( widget );
      if( gc == null ) {
        gc = new rwt.widgets.GC( widget );
      }
      widget.setUserData( rwt.scripting.WidgetProxyFactory._GC_KEY, gc );
    }
    return gc;
  },

  _findExistingGC : function( widget ) {
    var children = widget._getTargetNode().childNodes;
    var result = null;
    for( var i = 0; i < children.length && result == null; i++ ) {
      if( children[ i ].rwtObject instanceof rwt.widgets.GC ) {
        result = children[ i ].rwtObject;
      }
    }
    return result;
  }

};

/**
 * @private
 * @class RWT Scripting analoge to org.eclipse.swt.widgets.Composite
 * @description This constructor is not available in the global namespace. Instances can only
 * be obtained from {@link rap.getObject}.
 * @name Composite
 * @since 2.0
 */
 // TODO [tb] : where to put this? rap.CompositeWrapper? rwt.widget.Composite? in CompositeHandler?
function CompositeWrapper( widget ) {
  var children = null;
  if( !widget.isCreated() ) {
    children = [];
    widget.addEventListener( "create", function() {
      for( var i = 0; i < children.length; i++ ) {
        widget._getTargetNode().appendChild( children[ i ] );
      }
      widget.removeEventListener( "create", arguments.callee );
      children = null;
    } );
  }
  /**
   * @name append
   * @function
   * @memberOf Composite#
   * @description Adds a given HTMLElement to the Composite.
   * @param {HTMLElement} childElement The element to append.
   */
  this.append = function( childElement ) {
    if( children ) {
      children.push( childElement );
    } else {
      widget._getTargetNode().appendChild( childElement );
    }
  };
  /**
   * @name getClientArea
   * @function
   * @memberOf Composite#
   * @description Returns the client Area of the Composite
   * @returns {int[]} the client area as array [ x, y, width, height ]
   */
  this.getClientArea = function() {
    return widget.getClientArea();
  };

  /**
   * @name addListener
   * @function
   * @memberOf Composite#
   * @description Register the function as a listener of the given type
   * @param {string} type The type of the event (e.g. "Resize").
   * @param {Function} listener The callback function. It is executed in global context.
   */
  this.addListener = function( type, listener ) {
    widget.addEventListener( convertEventType( type ), listener, window );
  };

  /**
   * @name removeListener
   * @function
   * @memberOf Composite#
   * @description De-register the function as a listener of the given type
   * @param {string} type The type of the event (e.g. "Resize").
   * @param {Function} listener The callback function
   */
  this.removeListener = function( type, listener ) {
    widget.removeEventListener( convertEventType( type ), listener, window );
  };

}

//TODO [tb] : propper class/namespace for this event? (Control? SWT? RWT? rap? rwt.widget?)
/**
 * @event
 * @description Sent when widget changes size.
 * @name Composite#Resize
 */
function convertEventType( type ) {
  var result;
  if( type === "Resize" ) {
    result = "clientAreaChanged"; // works only for Composite
  } else {
    throw new Error( "Unkown event type " + type );
  }
  return result;
}

}());
