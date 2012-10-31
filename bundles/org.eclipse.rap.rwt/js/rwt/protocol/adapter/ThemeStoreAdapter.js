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

rwt.protocol.AdapterRegistry.add( "rwt.theme.ThemeStore", {

  factory : function( properties ) {
    return rwt.theme.ThemeStore.getInstance();
  },

  service : true,

  properties : [],

  propertyHandler : {},

  methods : [
    "loadActiveTheme"
  ],

  methodHandler : {
    "loadActiveTheme" : function( object, params ) {
      var request = new rwt.remote.Request( params.url, "GET", "application/json" );
      request.setAsynchronous( false );
      request.send();
    }
  }

} );
