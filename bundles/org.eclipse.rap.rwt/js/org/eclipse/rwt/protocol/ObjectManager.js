/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
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

  add : function( id, object, type ) {
    this._map[ id ] = {
      "object" : object,
      "type" : type
    };
  },
  
  remove : function( id ) {
    delete this._map[ id ];
  },
  
  getObject : function( id ) {
    return this._map[ id ] ? this._map[ id ].object : undefined;
  },

  getType : function( id ) {
    return this._map[ id ] ? this._map[ id ].type : undefined;
  }

};
