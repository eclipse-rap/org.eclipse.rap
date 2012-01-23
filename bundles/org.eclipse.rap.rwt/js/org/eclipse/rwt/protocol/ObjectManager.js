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

namespace( "org.eclipse.rwt.protocol" );

org.eclipse.rwt.protocol.ObjectManager = {

  _map : {},
  _callbacks : {},

  add : function( id, object, adapter ) {
    this._map[ id ] = {
      "object" : object,
      "adapter" : adapter
    };
    object._rwtId = id;
    if( typeof object.applyObjectId === "function" ) {
      object.applyObjectId( id );
    }
    if( this._callbacks[ id ] ) {
      for( var i = 0; i < this._callbacks[ id ].length; i++ ) {
        this._callbacks[ id ][ i ]( object );
      }
      delete this._callbacks[ id ];
    }
  },

  remove : function( id ) {
    if( id != null ) {
      delete this._map[ id ];
    }
  },

  getId : function( object ) {
    var result = null;
    if( object != null && object._rwtId != null ) {
      result = object._rwtId;
    }
    return result;
  },

  getObject : function( id ) {
    return this._map[ id ] ? this._map[ id ].object : undefined;
  },

  getEntry : function( id ) {
    return this._map[ id ];
  },

  addRegistrationCallback : function( id, fun ) {
    if( !this._callbacks[ id ] ) {
      this._callbacks[ id ] = [];
    }
    this._callbacks[ id ].push( fun );
  }

};
