/*******************************************************************************
 * Copyright (c) 2007, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

rwt.qx.Class.define( "rwt.widgets.ExternalBrowser", {

  extend : rwt.qx.Object,

  statics : {

    getInstance : function() {
      return rwt.runtime.Singletons.get( rwt.widgets.ExternalBrowser );
    }

  },

  members : {
    // maps id's (aka window names) to window instances
    // key = id, value = window object
    _map : {},

    open : function( id, url, features ) {
      var escapedId = this._escapeId( id );
      var win = window.open( url, escapedId, features, true );
      if( win != null ) {
        win.focus();
        this._map[ escapedId ] = win;
      }
    },

    close : function( id ) {
      var escapedId = this._escapeId( id );
      var win = this._map[ escapedId ];
      if( win != null ) {
        win.close();
      }
      delete this._map[ escapedId ];
    },

    _escapeId : function( id ) {
      var result = id;
      result = result.replace( /_/g, "_0" );
      // IE does not accept '-' in popup-window names
      result = result.replace( /-/g, "_1" );
      result = result.replace( /\./g, "_" );
      // IE does not accept blanks in popup-window names
      result = result.replace( / /g, "__" );
      return result;
    }
  }
} );

