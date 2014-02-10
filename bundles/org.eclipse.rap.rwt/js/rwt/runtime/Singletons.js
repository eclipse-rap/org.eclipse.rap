/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

/*jshint newcap: false */
namespace( "rwt.runtime" );

rwt.runtime.Singletons = {

  _holder : {},
  _sequence : 0,

  get : function( type ) {
    if( typeof type.__singleton === "undefined" ) {
      type.__singleton = "s" + this._sequence++;
    }
    var id = type.__singleton;
    if( !this._holder[ id ] ) {
      this._holder[ id ] = new type();
    }
    return this._holder[ id ];
  },

  clear : function( type ) {
    if( type ) {
      if( typeof type.__singleton !== "undefined" ) {
        delete this._holder[ type.__singleton ];
      }
    } else {
      this._holder = {};
    }
  }

};
