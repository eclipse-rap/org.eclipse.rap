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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.GC", {

  factory : function( properties ) {
    var parent = org.eclipse.rwt.protocol.ObjectManager.getObject( properties.parent );
    return new org.eclipse.swt.graphics.GC( parent );
  },
  
  destructor : function( gc ) {
    gc.dispose();
  },

  properties : [],

  propertyHandler : {},

  listeners : [],

  listenerHandler : {},

  methods : []


} );