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

  _wrapperMap : {},

  getWidgetProxy : function( obj ) {
    var result = null;
    if( obj instanceof Object ) {
      var hash = rwt.qx.Object.toHashCode( obj );
      if( this._wrapperMap[ hash ] == null ) {
        if( obj instanceof rwt.widgets.Composite ) {
          result = new rwt.scripting.CompositeProxy( obj );
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
    this._attachMethods( wrapper, originalWidget );
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

  _attachMethods : function( proxy, source ) {
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
  }

};


}());
