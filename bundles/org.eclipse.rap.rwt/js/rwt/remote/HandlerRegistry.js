/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
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

namespace( "rwt.remote" );

rwt.remote.HandlerRegistry = {

  _registry : {},

  add : function( key, handler ) {
    this._registry[ key ] = handler;
  },

  remove : function( key ) {
    delete this._registry[ key ];
  },

  getHandler : function( key ) {
    var result = this._registry[ key ];
    if( result === undefined ) {
      throw new Error( "No Handler for type " + key );
    }
    return result;
  },

  hasHandler : function( key ) {
    return this._registry[ key ] != null;
  }

};
