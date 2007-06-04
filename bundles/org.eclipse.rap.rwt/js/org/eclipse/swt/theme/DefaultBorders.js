/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

qx.Theme.define( "org.eclipse.swt.theme.DefaultBorders",
{
  title : "RAP default borders",

  // -- qooxdoo defaults --
  
  borders :
  {
  
    "black" :
    {
      width : 1,
      color : "black"
    },

    "white" :
    {
      width : 1,
      color : "white"
    },

    "shadow" :
    {
      width : 1,
      color : "widget.shadow"
    },

    "lightShadow" :
    {
      width : 1,
      color : "widget.lightshadow"
    },

    // border for tooltips
    "info" :
    {
      width : 1,
      color : "widget.info.foreground"
    },

    "inset" :
    {
      width : 2,
      color : [ "widget.shadow", "widget.highlight", "widget.highlight", "widget.shadow" ],
      innerColor : [ "widget.darkshadow", "widget.lightshadow", "widget.lightshadow", "widget.darkshadow" ]
    },

    "outset" :
    {
      width : 2,
      color : [ "widget.lightshadow", "widget.darkshadow", "widget.darkshadow", "widget.lightshadow" ],
      innerColor : [ "widget.highlight", "widget.shadow", "widget.shadow", "widget.highlight" ]
    },

    "groove" :
    {
      width : 2,
      color : [ "widget.shadow", "widget.highlight", "widget.highlight", "widget.shadow" ],
      innerColor : [ "widget.highlight", "widget.shadow", "widget.shadow", "widget.highlight" ]
    },

    "ridge" :
    {
      width : 2,
      color : [ "widget.highlight", "widget.shadow", "widget.shadow", "widget.highlight" ],
      innerColor : [ "widget.shadow", "widget.highlight", "widget.highlight", "widget.shadow" ]
    },

    "thinInset" :
    {
      width : 1,
      color : [ "widget.shadow", "widget.highlight", "widget.highlight", "widget.shadow" ]
    },

    "thinOutset" :
    {
      width : 1,
      color : [ "widget.highlight", "widget.shadow", "widget.shadow", "widget.highlight" ]
    },

    "verticalDivider" :
    {
      widthTop : 1,
      widthBottom : 1,
      colorTop: "widget.shadow",
      colorBottom : "widget.highlight"
    },

    "horizontalDivider" :
    {
      widthLeft : 1,
      widthRight : 1,
      colorLeft : "widget.highlight",
      colorRight : "widget.shadow"
    },

    // -- defaults --
    
    "control.border" : {
      width : 0
    },
    
    "control.BORDER.border" : {
      width : 2,
      color : [ "widget.shadow", "widget.highlight", "widget.highlight", "widget.shadow" ],
      innerColor : [ "widget.darkshadow", "widget.lightshadow", "widget.lightshadow", "widget.darkshadow" ]
    },
    
    // -- label --
    
    "label.border" : {
    },
    
    "label.BORDER.border" : {
      width : 1,
      style : "inset"
    },
    
    // -- button --
    
    "button.border" : {
      width : 1,
      style : "solid",
      color : [ "white", "gray", "gray", "white" ]
    },
    "button.pressed.border" : {
      width : 1,
      style : "solid",
      color : [ "gray", "white", "white", "gray" ]
    },
    "border.FLAT.border" : {
      width : 1,
      style : "solid",
      color : "black"
    },
    "button.FLAT.hover.border" : {
      width : 1,
      style : "solid",
      color : "black"
    },
    "button.FLAT.pressed.border" : {
      width : 1,
      style : "solid",
      color : "black"
    },
    "border.BORDER.border" : {
// TODO [rst] Find out why this doesn't work:
//      width : 2,
//      style : "outset"
      width : 2,
      color : [ "widget.lightshadow", "widget.darkshadow", "widget.darkshadow", "widget.lightshadow" ],
      innerColor : [ "widget.highlight", "widget.shadow", "widget.shadow", "widget.highlight" ]
    },
    "button.BORDER.pressed.border" : {
// TODO [rst] Find out why this doesn't work:
//      width : 2,
//      style : "inset"
      width : 2,
      color : [ "widget.shadow", "widget.highlight", "widget.highlight", "widget.shadow" ],
      innerColor : [ "widget.darkshadow", "widget.lightshadow", "widget.lightshadow", "widget.darkshadow" ]
    },
    
    // -- toolbar --
    
    "toolbar.BORDER.border" : {
      width : 1,
      color : [ "widget.highlight", "widget.shadow", "widget.shadow", "widget.highlight" ]
    },
    
    "toolbar.border" : {
      width : 0
    },
    
    // -- menu --
    
    "menu.border" : { // == outset
      width : 2,
      color : [ "widget.lightshadow", "widget.darkshadow", "widget.darkshadow", "widget.lightshadow" ],
      innerColor : [ "widget.highlight", "widget.shadow", "widget.shadow", "widget.highlight" ]
    },
    
    // -- text --
    
    "text.border" : {
      width : 0
    },
    
    "text.BORDER.border" : {
      width : 2,
      color : [ "widget.shadow", "widget.highlight", "widget.highlight", "widget.shadow" ],
      innerColor : [ "widget.darkshadow", "widget.lightshadow", "widget.lightshadow", "widget.darkshadow" ]
    },
    
    // -- group --
    
    "group.border" : {
      width : 1,
      color : "#aca899"
    },
    
    // -- table --
    
    "table.column.border" : {
      style : "solid",
      width : [ 0, 1, 2, 0 ],
      color : [ "widget.thinborder", "widget.thinborder" ]
    },
    
    "table.column.hover.border" : {
      style : "solid",
      width : [ 0, 1, 2, 0 ],
      color : [ "widget.selection-marker", "widget.thinborder" ]
    },
    
    // -- shell --
    
    "shell.border" : {
      width : 2,
      color : [ "widget.lightshadow", "widget.darkshadow", "widget.darkshadow", "widget.lightshadow" ],
      innerColor : [ "widget.highlight", "widget.shadow", "widget.shadow", "widget.highlight" ]
    }
    
  }
});
