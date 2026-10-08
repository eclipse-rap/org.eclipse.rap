/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var ObjectRegistry = rwt.remote.ObjectRegistry;

rwt.remote.RemoteObjectFactory = { // TODO [tb] : merge with Connection.js? (not a factory)

  _db : {},

  getRemoteObject : function( target ) {
    var id = ObjectRegistry.getId( target );
    if( id == null ){
      throw new Error( "Invalid target for ServerObject, or target not in ObjectManager" );
    }
    return this._getRemoteObject( id );
  },

  remove : function( id ) {
    delete this._db[ id ];
  },

  _getRemoteObject : function( id ) {
    if( this._db[ id ] == null ) {
      this._db[ id ] = new rwt.remote.RemoteObject( id );
      this._initRemoteObject( id );
    }
    return this._db[ id ];
  },

  _initRemoteObject : function( id ) {
    var remoteObject = this._db[ id ];
    var handler = ObjectRegistry.getEntry( id ).handler;
    if( handler && handler.listeners ) {
      for( var i = 0; i < handler.listeners.length; i++ ) {
        var type = handler.listeners[ i ];
        remoteObject._.listen[ type ] = true;
      }
    }
  }

};


}());
