/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Theme.define( "org.eclipse.swt.theme.BordersBase",
{
  title : "Borders Base Theme",

  borders : {

    "black" : {
      width : 1,
      color : "black"
    },

    "shadow" : {
      width : 1,
      color : "widget.shadow"
    },

    "lightShadow" : {
      width : 1,
      color : "widget.lightshadow"
    },

    // border for tooltips
    "info" : {
      width : 1,
      color : "widget.info.foreground"
    },

    "inset" : {
      width : 2,
      color : [ "widget.shadow", "widget.highlight", "widget.highlight", "widget.shadow" ],
      innerColor : [ "widget.darkshadow", "widget.lightshadow", "widget.lightshadow", "widget.darkshadow" ]
    },

    "outset" : {
      width : 2,
      color : [ "widget.lightshadow", "widget.darkshadow", "widget.darkshadow", "widget.lightshadow" ],
      innerColor : [ "widget.highlight", "widget.shadow", "widget.shadow", "widget.highlight" ]
    },

    "groove" : {
      width : 2,
      color : [ "widget.shadow", "widget.highlight", "widget.highlight", "widget.shadow" ],
      innerColor : [ "widget.highlight", "widget.shadow", "widget.shadow", "widget.highlight" ]
    },

    "ridge" : {
      width : 2,
      color : [ "widget.highlight", "widget.shadow", "widget.shadow", "widget.highlight" ],
      innerColor : [ "widget.shadow", "widget.highlight", "widget.highlight", "widget.shadow" ]
    },

    "thinInset" : {
      width : 1,
      color : [ "widget.shadow", "widget.highlight", "widget.highlight", "widget.shadow" ]
    },

    "thinOutset" : {
      width : 1,
      color : [ "widget.highlight", "widget.shadow", "widget.shadow", "widget.highlight" ]
    },

    "verticalDivider" : {
      widthTop : 1,
      widthBottom : 1,
      colorTop: "widget.shadow",
      colorBottom : "widget.highlight"
    },

    "horizontalDivider" : {
      widthLeft : 1,
      widthRight : 1,
      colorLeft : "widget.highlight",
      colorRight : "widget.shadow"
    },

    // -- toolbar --

    "toolbar.BORDER.border" : {
      width : 1,
      color : [ "widget.highlight", "widget.shadow", "widget.shadow", "widget.highlight" ]
    },

    "toolbar.border" : {
      width : 0
    },

    // -- table --

    "table.column.border" : {
      width : [ 0, 1, 2, 0 ],
      color : [ "widget.thinborder", "widget.thinborder" ]
    },

    "table.column.hover.border" : {
      width : [ 0, 1, 2, 0 ],
      color : [ "widget.selection-marker", "widget.thinborder" ]
    },

//    "table.row.horizontalLine" : {
//      widthTop : 1,
//      widthBottom : 1,
//      colorTop : "table.row.line",
//      colorBottom : "table.row.line"
//    },

    // -- tree --
    "tree.column.border" : {
      width : [ 0, 1, 2, 0 ],
      color : [ "widget.thinborder", "widget.thinborder" ]
    },

    "tree.column.hover.border" : {
      width : [ 0, 1, 2, 0 ],
      color : [ "widget.selection-marker", "widget.thinborder" ]
    },

    // -- separator --

    "separator.shadowin.horizontal.border" : {
      widthTop : 1,
      widthBottom : 1,
      colorTop : "widget.lightshadow",
      colorBottom : "widget.highlight"
    },

    "separator.shadowin.vertical.border" : {
      widthLeft : 1,
      widthRight : 1,
      colorLeft : "widget.lightshadow",
      colorRight : "widget.highlight"
    },

    "separator.shadowout.horizontal.border" : {
      widthTop : 1,
      widthBottom : 1,
      colorTop : "widget.highlight",
      colorBottom : "widget.shadow"
    },

    "separator.shadowout.vertical.border" : {
      widthLeft : 1,
      widthRight : 1,
      colorLeft : "widget.highlight",
      colorRight : "widget.shadow"
    },

    // -- ctabfolder --

    "ctabfolder.border" : {
      width : 1,
      color : "#c0c0c0"
    },

    "ctabfolder.button.border" : {
      width : 1,
      color : "#c0c0c0"
    }
  }
} );
