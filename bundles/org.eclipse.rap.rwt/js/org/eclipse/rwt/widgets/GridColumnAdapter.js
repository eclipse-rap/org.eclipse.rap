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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.GridColumn", {

  factory : function( properties ) {
    var result;
    org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( properties.parent, function( parent ) {
      result = new org.eclipse.rwt.widgets.GridColumn( parent );
    } );
    return result;
  },

  destructor : function( column ) {
    column.dispose();
  },

  properties : [
    // Always set column index first
    "index",
    "left",
    "width",
    "text",
    "image",
    "font",
    "footerText",
    "footerImage",
    "footerFont",
    "toolTip",
    "resizable",
    "moveable",
    "alignment",
    "fixed",
    "customVariant"
  ],

  propertyHandler : {
    "toolTip" : org.eclipse.rwt.protocol.AdapterUtil.getControlPropertyHandler( "toolTip" )
  },

  listeners : [
    "selection"
  ],

  listenerHandler : {},

  methods : []

} );