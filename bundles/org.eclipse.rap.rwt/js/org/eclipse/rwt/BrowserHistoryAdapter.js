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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.BrowserHistory", {

  factory : function( properties ) {
    return qx.client.History.getInstance();
  },

  destructor : qx.lang.Function.returnTrue,

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