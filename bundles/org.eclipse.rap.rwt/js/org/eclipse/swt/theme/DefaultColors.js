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

qx.Theme.define( "org.eclipse.swt.theme.DefaultColors",
{
  title : "RAP default colors",

  colors : {
        
    // -- widget --
    
    // default text color
    "widget.foreground" : [ 0, 0, 0 ],
    
    // default background color for widgets
    "widget.background" : [ 248, 248, 255 ], // 235, 233, 237 // == shell.background, control.background ?
    
    "widget.info.foreground" : [ 0, 0, 0 ],
    
    "widget.info.background" : [ 255, 255, 225 ],
    
    // used to draw outer shadow for borders
    "widget.shadow"      : [ 167, 166, 170 ],
    
    // used to draw outer highlight for borders
    "widget.highlight"   : [ 255, 255, 255 ],
    
    // used to draw inner shadow for borders
    "widget.darkshadow"  : [ 133, 135, 140 ],
    
    // used to draw inner highlight for borders
    "widget.lightshadow" : [ 220, 223, 228 ],
    
    // used for draw thin lines of groups, tabfolder headers, table headers, etc.
    "widget.thinborder" : [ 214, 210, 194 ],
    
    // 
    "graytext" : [ 167, 166, 170 ],
    
    // 
    "selection-indicator" : [ 254, 200, 60 ],
    
    // -- control --
    
    // -- button --
    
    "button.foreground" : [ 0, 0, 0 ],
    "button.background" : [ 247, 247, 251 ],
    "button.hover.background" : [ 251, 251, 254 ],
    "button.FLAT.pressed.background" : [ 225, 225, 230 ],
    
    // -- toolbar --
    
    "toolbar.foreground" : [ 0, 0, 0 ],
//    "toolbar.hover.foreground" : [ 255, 255, 255 ],
    "toolbar.hover.foreground" : [ 0, 0, 0 ],
    "toolbar.background" : [ 248, 248, 255 ], // == control.background
//    "toolbar.hover.background" : [ 49, 106, 197 ], // == list.focused.background
    "toolbar.hover.background" : [ 255, 255, 255 ],
    
    // -- menu --
    
    "menu.foreground" : [ 0, 0, 0 ],
    "menu.hover.foreground" : [ 255, 255, 255 ],
    "menu.background" : [ 248, 248, 255 ], // == control.background
    "menu.hover.background" : [ 49, 106, 197 ], // == list.focused.background
    
    // -- shell --
    
    "shell.foreground" : [ 0, 0, 0 ],
    "shell.background" : [ 248, 248, 255 ],
    "shell.title.foreground" : [ 255, 255, 255 ],
    "shell.title.background" : [ 0, 128, 192 ],
    "shell.title.inactive.foreground" : [ 221, 221, 221 ],
    "shell.title.inactive.background" : [ 121, 150, 165 ],
    
    // minimize button
    "shell.minbutton.background" : [ 59, 169, 41 ],
    "shell.minbutton.over.background" : [ 117, 208, 102 ],
    "shell.minbutton.inactive.background" : [ 135, 184, 127 ],
    
    // maximize / restore button
    "shell.maxbutton.background" : [ 59, 169, 41 ],
    "shell.maxbutton.over.background" : [ 117, 208, 102 ],
    "shell.maxbutton.inactive.background" : [ 135, 184, 127 ],
    
    // close button
    "shell.closebutton.background" : [ 239, 29, 47 ],
    "shell.closebutton.over.background" : [ 255, 120, 132 ],
    "shell.closebutton.inactive.background" : [ 198, 127, 133 ],
    
    // -- table --
    
    "table.column.background" : [ 232, 232, 215 ],
    "table.column.mouseover.background" : [ 248, 485, 255 ],
    
    // -- link --
    
    "link.foreground" : [ 0, 0, 127 ],
    
    // -- list -- (should also apply to tree, table, etc.)
    
    "list.foreground" : [ 0, 0, 0 ],
    "list.background" : [ 255, 255, 255 ],
    "list.selection.foreground" : [ 255, 255, 255 ],
    "list.selection.background" : [ 49, 106, 197 ],
    "list.selection.unfocused.foreground" : [ 255, 255, 255 ],
    "list.selection.unfocused.background" : [ 192, 192, 192 ],
    
    // -- tab folder --
    
    "tabfolder.background" : [ 248, 248, 255 ],
    "tabfolder.checked.background" : [ 248, 248, 255 ],
    "tabfolder.hover.background" : [ 248, 248, 255 ]
    
  }
});
