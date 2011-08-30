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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.Display", {

  factory : function( properties ) {
    return new org.eclipse.rwt.Display( properties.url, properties.rootId );
  },

  destructor : null, // destroy is currently not called for display
  
  properties : [],

  knownListeners : [],

  knownMethods : [
    "allowEvent",
    "cancelEvent"
  ]

} );