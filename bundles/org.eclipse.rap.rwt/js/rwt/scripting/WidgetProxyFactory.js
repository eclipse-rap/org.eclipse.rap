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

  _PROXY_KEY : "rwt.scripting.WidgetProxyFactory.PROXY",
  _GC_KEY : "rwt.scripting.WidgetProxyFactory.GC",

  getWidgetProxy : function( widget ) {
    // TODO [tb] : this delegates back to _initWrapper, see init.js. Should be simplified in RAP 2.2
    return rap._.getWrapperFor( widget );
  },

  _initWrapper : function( originalWidget, wrapper ) {
    this._attachSetter( wrapper, originalWidget );
    this._attachGetter( wrapper, originalWidget );
    this._attachUserData( wrapper, originalWidget );
    if( rwt.remote.WidgetManager.getInstance().isControl( originalWidget ) ) {
      this._attachControlMethods( wrapper, originalWidget );
    }
    this._addDisposeListener( originalWidget, function() {
      rwt.scripting.WidgetProxyFactory._disposeWidgetProxy( originalWidget );
    } );
  },

  _addDisposeListener : function( widget, listener ) {
    var orgDestroy = widget.destroy;
    widget.destroy = function() {
      listener( this );
      orgDestroy.call( widget );
    };
  },

  _disposeWidgetProxy : function( widget ) {
    var protoInstance = widget.getUserData( this._PROXY_KEY );
    var userData = widget.getUserData( rwt.remote.HandlerUtil.SERVER_DATA );
    rwt.scripting.WidgetProxyFactory._disposeObject( protoInstance );
    rwt.scripting.WidgetProxyFactory._disposeObject( userData );
  },

  _disposeObject : function( object ) {
    for( var key in object ) {
      if( object.hasOwnProperty( key ) ) {
        object[ key ] = null;
      }
    }
  },

  /////////////////
  // setter support

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

  /////////////////
  // Getter support

  _attachGetter : function( proxy, source ) {
    if( source.classname ) {
      var getterMap = this._getterMapping[ source.classname ];
      for( var key in getterMap ) {
        proxy[ key ] = getterMap[ key ]( source );
      }
    }
  },

  _getterMapping : {
    "rwt.widgets.Text" : {
      "getText" : function( widget ) { return function() { return widget.getValue(); }; },
      "getSelection" : function( widget ) { return function() { return widget.getSelection(); }; },
      "getEditable" : function( widget ) { return function() { return !widget.getReadOnly(); }; }
    },
    "rwt.widgets.List" : {
      "getSelection" : function( widget ) {
        return function() {
          var items = widget.getSelectedItems();
          var result = [];
          for( var i = 0; i < items.length; i++ ) {
            result[ i ] = rwt.util.Encoding.unescape( items[ i ].getLabel() );
          }
          return result;
        };
      }
    }
  },

  ///////////////////
  // widget data

  _attachUserData : function( proxy, source ) {
    var setter = this._setUserData;
    var getter = this._getUserData;
    proxy.setData = function( property, value ) {
      setter( source, arguments );
    };
    proxy.getData = function( property ) {
      return getter( source, arguments );
    };
  },

  _setUserData : function( source, args ) {
    if( args.length !== 2 ) {
      var msg =  "Wrong number of arguments in SetData: Expected 2, found " + args.length;
      throw new Error( msg );
    }
    var property = args[ 0 ];
    var value = args[ 1 ];
    var USERDATA_KEY = rwt.scripting.WidgetProxyFactory._USERDATA_KEY;
    var data = rwt.remote.HandlerUtil.getServerData( source );
    data[ property ] = value;
  },

  _getUserData : function( source, args ) {
    if( args.length !== 1 ) {
      var msg =  "Wrong number of arguments in SetData: Expected 1, found " + args.length;
      throw new Error( msg );
    }
    var property = args[ 0 ];
    var result = null;
    var data = rwt.remote.HandlerUtil.getServerData( source );
    if( typeof data[ property ] !== "undefined" ) {
      result = data[ property ];
    }
    return result;
  },

  ///////////////////////
  // misc methods support

  _attachControlMethods : function( proxy, source ) {
    var id = ObjectRegistry.getId( source );
    var that = this;
    proxy.redraw = function() {
      that._initGC( source );
    };
    proxy.forceFocus = function() {
      var result = false;
      if( source.getEnabled() && that._isVisible( source ) ) {
        rwt.widgets.Display.getCurrent().setFocusControl( id );
        result = true;
      }
      return result;
    };
  },

  _isVisible : function( widget ) {
    var result = true;
    var current = widget;
    while( current && result ) {
      result = current.getVisibility();
      current = current.getParent();
    }
    return result;
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

}());
