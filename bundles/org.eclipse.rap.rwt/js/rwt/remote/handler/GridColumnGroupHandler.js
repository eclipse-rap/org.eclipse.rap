/*******************************************************************************
 * Copyright (c) 2011, 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.remote.HandlerRegistry.add( "rwt.widgets.GridColumnGroup", {

  factory : function( properties ) {
    var result;
    var styleMap = rwt.remote.HandlerUtil.createStyleMap( properties.style );
    rwt.remote.HandlerUtil.callWithTarget( properties.parent, function( parent ) {
      result = new rwt.widgets.GridColumn( parent, true );
      result.setShowChevron( styleMap.TOGGLE === true );
      rwt.remote.HandlerUtil.addDestroyableChild( parent, result );
    } );
    return result;
  },

  destructor : function( column ) {
    rwt.remote.HandlerUtil.removeDestroyableChild( column._grid, column );
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
    "customVariant",
    "headerWordWrap",
    "fixed"
  ],

  events : [ "Expand", "Collapse" ]

} );
