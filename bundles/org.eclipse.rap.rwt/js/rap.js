/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

/**
 * @namespace Holds all public API of the RAP WebClient.
 */
rap = {

  /**
   * @description Register a RAP protocol type-handler for a specific type of remote-objects.
   * @param {String} type
   * @param {TypeHandler} handler
   */
  registerTypeHandler : function( type, handler ) {
    handler.isPublic = true;
    rwt.protocol.AdapterRegistry.add( type, handler );
  },

  /**
   * @description Returns the actual client object associated with the given id.
   * If there is no object registered for the given id, null is returned.
   * For RAP internal objects (e.g. RWT widgets) a wrapper is returned instead of the real object.
   * @param {String} id
   * @returns {Object}
   */
  getObject : function( id ) {
    var entry = rwt.protocol.ObjectRegistry.getEntry( id );
    var result;
    if( entry && entry.adapter.isPublic ) {
      result = entry.object;
    } else {
      result = getWrapperFor( entry.object );
    }
    return result;
  },

  /**
   * @description Returns an instance of <code>RemoteObject</code> for the given client object.
   * The object has to be one created by an <code>TypeHandler</code> factory method. Multiple calls
   * for the same objects will return the same instance.
   * @param {Object} object
   * @returns {RemoteObject}
   */
  getRemoteObject : function( object ) {
    return rwt.remote.Server.getInstance().getServerObject( object );
  }

};

var wrapperMap = {};

function getWrapperFor( obj ) {
  var result = null;
  if( obj instanceof Object ) {
    var hash = qx.core.Object.toHashCode( obj );
    if( wrapperMap[ hash ] == null ) {
      if( obj instanceof rwt.widgets.Composite ) {
        wrapperMap[ hash ] = new ParentWrapper( obj );
      } else {
        wrapperMap[ hash ] = {};
      }
    }
    result = wrapperMap[ hash ];
  }
  return result;
}

function ParentWrapper( widget ) {
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
  this.append = function( childElement ) {
    if( children ) {
      children.push( childElement );
    } else {
      widget._getTargetNode().appendChild( childElement );
    }
  };
}

}());