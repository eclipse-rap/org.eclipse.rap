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

rwt.protocol.AdapterRegistry.add( "rwt.widgets.GridColumn", {

  factory : function( properties ) {
    var result;
    rwt.protocol.AdapterUtil.callWithTarget( properties.parent, function( parent ) {
      result = new rwt.widgets.GridColumn( parent );
      rwt.protocol.AdapterUtil.addDestroyableChild( parent, result );
    } );
    return result;
  },

  destructor : function( column ) {
    rwt.protocol.AdapterUtil.removeDestroyableChild( column._grid, column );
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
    "group",
    "customVariant",
    "visibility",
    "check"
  ],

  propertyHandler : {
    "toolTip" : rwt.protocol.AdapterUtil.getControlPropertyHandler( "toolTip" ),
    "group" : function( widget, value ) {
      rwt.protocol.AdapterUtil.callWithTarget( value, function( group ) {
        widget.setGroup( group );
      } );
    }
  },

  listeners : [
    "Selection"
  ]

} );
