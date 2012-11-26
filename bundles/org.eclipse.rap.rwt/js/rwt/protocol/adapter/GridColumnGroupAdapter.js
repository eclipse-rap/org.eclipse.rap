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

rwt.protocol.AdapterRegistry.add( "rwt.widgets.GridColumnGroup", {

  factory : function( properties ) {
    var result;
    rwt.protocol.AdapterUtil.callWithTarget( properties.parent, function( parent ) {
      result = new rwt.widgets.GridColumn( parent, true );
      rwt.protocol.AdapterUtil.addDestroyableChild( parent, result );
    } );
    return result;
  },

  destructor : function( column ) {
    rwt.protocol.AdapterUtil.removeDestroyableChild( column._grid, column );
    column.dispose();
  },

  properties : [
    "left",
    "width",
    "height",
    "text",
    "image",
    "font",
    "expanded",
    "visibility",
    "customVariant"
  ],

  listeners : [
    "Expand",
    "Collapse"
  ]

} );
