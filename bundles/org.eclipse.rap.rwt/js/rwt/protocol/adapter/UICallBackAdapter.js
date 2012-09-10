/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.protocol.AdapterRegistry.add( "rwt.UICallBack", {

  factory : function( properties ) {
    return rwt.client.UICallBack.getInstance();
  },

  destructor : rwt.util.Function.returnTrue,

  properties : [
    "active"
  ],

  propertyHandler : {},

  listeners : [],

  listenerHandler : {},

  methods : [
    "sendUIRequest"
  ]

} );
