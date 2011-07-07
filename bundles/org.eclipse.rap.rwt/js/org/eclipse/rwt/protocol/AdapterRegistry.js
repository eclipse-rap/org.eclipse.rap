/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.protocol.AdapterRegistry", {

  statics : {
    
    _registry : {},
    
    add : function( key, adapter ) {
      this._registry[ key ] = adapter;
    },
    
    remove : function( key ) {
      delete this._registry[ key ];
    },
    
    getAdapter : function( key ) {
      return this._registry[ key ];
    }

  }

} );