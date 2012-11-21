/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

namespace( "rwt.protocol" );

rwt.protocol.MessageProcessor = {

  processMessage : function( messageObject ) {
    this.processHead( messageObject.head );
    var operations = messageObject.operations;
    for( var i = 0; i < operations.length; i++ ) {
      this.processOperationArray( operations[ i ] );
    }
  },

  processHead : function( head ) {
    var server = rwt.remote.Server.getInstance();
    if( head.url !== undefined ) {
      server.setUrl( head.url );
    }
    if( head.requestCounter !== undefined ) {
      server.setRequestCounter( head.requestCounter );
    }
    if( head.redirect !== undefined ) {
      document.location = head.redirect;
    }
  },

  processOperationArray : function( operation ) {
    var action = operation[ 0 ];
    try {
      switch( action ) {
        case "create":
          this._processCreate( operation[ 1 ], operation[ 2 ], operation[ 3 ] );
        break;
        case "set":
          this._processSet( operation[ 1 ], operation[ 2 ] );
        break;
        case "listen":
          this._processListen( operation[ 1 ], operation[ 2 ] );
        break;
        case "call":
          this._processCall( operation[ 1 ], operation[ 2 ], operation[ 3 ] );
        break;
        case "destroy":
          this._processDestroy( operation[ 1 ] );
        break;
      }
    } catch( ex ) {
      this._processError( ex, operation );
    }
  },

  processOperation : function( operation ) {
    switch( operation.action ) {
      case "create":
        this._processCreate( operation.target, operation.type, operation.properties );
      break;
      case "set":
        this._processSet( operation.target, operation.properties );
      break;
      case "destroy":
        this._processDestroy( operation.target );
      break;
      case "call":
        this._processCall( operation.target, operation.method, operation.properties );
      break;
      case "listen":
        this._processListen( operation.target, operation.properties );
      break;
    }
  },

  ////////////
  // Internals

  _processCreate : function( targetId, type, properties ) {
    var adapter = rwt.protocol.AdapterRegistry.getAdapter( type );
    if( adapter.service === true ) {
      throw new Error( "Objects of type " + type + " can not be created" );
    }
    var targetObject = adapter.factory( properties );
    this._addTarget( targetObject, targetId, adapter );
    this._processSetImpl( targetObject, adapter, properties );
  },

  _processDestroy : function( targetId ) {
    var objectEntry = rwt.protocol.ObjectRegistry.getEntry( targetId );
    var adapter = objectEntry.adapter;
    var targetObject = objectEntry.object;
    var children =   adapter.getDestroyableChildren
                   ? adapter.getDestroyableChildren( targetObject )
                   : null;
    if( adapter.destructor ) {
      adapter.destructor( targetObject );
    }
    rwt.protocol.ObjectRegistry.remove( targetId );
    rwt.protocol.ServerObjectFactory.remove( targetId );
    for( var i = 0; children != null && i < children.length; i++ ) {
      if( children[ i ] ) {
        this._processDestroy( rwt.protocol.ObjectRegistry.getId( children[ i ] ) );
      }
    }
  },

  _processSet : function( targetId, properties ) {
    var objectEntry = rwt.protocol.ObjectRegistry.getEntry( targetId );
    this._processSetImpl( objectEntry.object, objectEntry.adapter, properties );
  },

  _processSetImpl : function( targetObject, adapter, properties ) {
    if( properties && adapter.properties  instanceof Array ) {
      for( var i = 0; i < adapter.properties.length; i++ ) {
        var property = adapter.properties [ i ];
        var value = properties[ property ];
        if( value !== undefined ) {
          if( adapter.propertyHandler && adapter.propertyHandler[ property ] ) {
            adapter.propertyHandler[ property ].call( window, targetObject, value );
          } else {
            var setterName = this._getSetterName( property );
            targetObject[ setterName ]( value );
          }
        }
      }
    }
  },

  _processCall : function( targetId, method, properties ) {
    var objectEntry = rwt.protocol.ObjectRegistry.getEntry( targetId );
    var adapter = objectEntry.adapter;
    var targetObject = objectEntry.object;
    if( adapter.methods instanceof Array && adapter.methods.indexOf( method ) !== -1 ) {
      if( adapter.methodHandler && adapter.methodHandler[ method ] ) {
        adapter.methodHandler[ method ]( targetObject, properties );
      } else {
        targetObject[ method ]( properties );
      }
    }
  },

  _processListen : function( targetId, properties ) {
    var objectEntry = rwt.protocol.ObjectRegistry.getEntry( targetId );
    var adapter = objectEntry.adapter;
    var targetObject = objectEntry.object;
    if( adapter.listeners instanceof Array ) {
      for( var i = 0; i < adapter.listeners.length; i++ ) {
        var type = adapter.listeners[ i ];
        if( properties[ type ] === true ) {
          this._addListener( adapter, targetObject, type );
        } if( properties[ type ] === false ) {
          this._removeListener( adapter, targetObject, type );
        }
      }
    }
  },

  ////////////
  // Internals

  _processError : function( error, operation ) {
    var errorstr;
    if( error ) {
      errorstr = error.message ? error.message : error.toString();
    } else {
      errorstr = "No Error given!";
    }
    var msg = "Operation \"" + operation[ 0 ] + "\"";
    msg += " on target \"" +  operation[ 1 ] + "\"";
    var objectEntry = rwt.protocol.ObjectRegistry.getEntry( operation[ 1 ] );
    var target = objectEntry ? objectEntry.object : null;
    msg += " of type \"" +  ( target && target.classname ? target.classname : target ) + "\"";
    msg += " failed:";
    msg += "\n" + errorstr +"\n";
    msg += "Properties: \n" + this._getPropertiesString( operation );
    throw new Error( msg );
  },

  _getPropertiesString : function( operation ) {
    var result = "";
    var properties;
    switch( operation[ 0 ] ) {
      case "set":
      case "listen":
        properties = operation[ 2 ];
      break;
      case "create":
      case "call":
        properties = operation[ 3 ];
      break;
      default:
        properties = {};
      break;
    }
    for( var key in properties ) {
      result += key + " = " + properties[ key ] + "\n";
    }
    return result;
  },

  _addTarget : function( target, targetId, adapter ) {
    if( target instanceof rwt.widgets.base.Widget ) {
      // TODO [tb] : remove WidgetManager and then this if
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      widgetManager.add( target, targetId, false, adapter ); // uses ObjectManager internally
    } else {
      rwt.protocol.ObjectRegistry.add( targetId, target, adapter );
    }
  },

  _addListener : function( adapter, targetObject, eventType ) {
    if( adapter.listenerHandler &&  adapter.listenerHandler[ eventType ] ) {
      adapter.listenerHandler[ eventType ]( targetObject, true );
    } else {
      var setterName = this._getListenerSetterName( eventType );
      targetObject[ setterName ]( true );
    }
  },

  _removeListener : function( adapter, targetObject, eventType ) {
    if( adapter.listenerHandler &&  adapter.listenerHandler[ eventType ] ) {
      adapter.listenerHandler[ eventType ]( targetObject, false );
    } else {
      var setterName = this._getListenerSetterName( eventType );
      targetObject[ setterName ]( false );
    }
  },

  _getSetterName : function( property ) {
    return "set" + rwt.util.String.toFirstUp( property );
  },

  _getListenerSetterName : function( eventType ) {
    return "setHas" + rwt.util.String.toFirstUp( eventType ) + "Listener";
  }

};
