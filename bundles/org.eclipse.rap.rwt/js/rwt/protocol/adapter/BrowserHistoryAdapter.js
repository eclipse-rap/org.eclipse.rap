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

rwt.protocol.AdapterRegistry.add( "rwt.client.BrowserHistory", {

  factory : function( properties ) {
    return rwt.client.History.getInstance();
  },

  service : true,

  properties : [],

  propertyHandler : {},

  listeners : [
    "navigation"
  ],

  listenerHandler : {},

  methods : [
    "add"
  ],

  methodHandler : {
    "add" : function( object, value ) {
      var entries = value.entries;
      for( var i = 0; i < entries.length; i++) {
        object.addToHistory( entries[ i ][ 0 ], entries[ i ][ 1 ] );
      }
    }
  }

} );
