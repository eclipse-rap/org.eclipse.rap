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

var ObjectManager = rwt.protocol.ObjectRegistry;

rwt.protocol.ServerObjectFactory = { // TODO [tb] : merge with Server.js or ServerObject.js?

  _db : {},

  getServerObject : function( target ) {
    var id = ObjectManager.getId( target );
    if( id === null ){
      throw new Error( "Invalid target for ServerObject, or target not in ObjectManager" );
    }
    return this._getServerObject( id );
  },

  remove : function( id ) {
    delete this._db[ id ];
  },

  _getServerObject : function( id ) {
    if( this._db[ id ] == null ) {
      this._db[ id ] = new rwt.protocol.ServerObject( id );
    }
    return this._db[ id ];
  }



};


}());
