/*******************************************************************************
 * Copyright (c) 2007-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/


qx.Theme.define( "org.eclipse.swt.theme.AppearancesBase",
{
  title : "Appearances Base Theme",

  appearances : {

  "empty" : {
  },

  "widget" : {
  },

  "image" : {
  },

  /*
  ---------------------------------------------------------------------------
    CORE
  ---------------------------------------------------------------------------
  */

  "cursor-dnd-move" : {
    style : function(states) {
      return {
        source : "widget/cursors/move.gif"
      };
    }
  },

  "cursor-dnd-copy" : {
    style : function(states) {
      return {
        source : "widget/cursors/copy.gif"
      };
    }
  },

  "cursor-dnd-alias" : {
    style : function(states) {
      return {
        source : "widget/cursors/alias.gif"
      };
    }
  },

  "cursor-dnd-nodrop" : {
    style : function(states) {
      return {
        source : "widget/cursors/nodrop.gif"
      };
    }
  },

  "client-document" :
  {
    style : function( states ) {
      return {
        font : "widget.font",
        textColor : "black",
        backgroundColor : "white",
        // TODO [rst] Eliminate absolute references
        backgroundImage : "./resource/widget/rap/display/bg.gif"
      };
    }
  },

  "client-document-blocker" :
  {
    style : function( states ) {
      // You could also use: "static/image/dotted_white.gif" for example as backgroundImage here
      // (Visible) background tiles could be dramatically slow down mshtml!
      // A background image or color is always needed for mshtml to block the events successfully.
      return {
        cursor : "default",
        backgroundImage : "static/image/blank.gif"
      };
    }
  },

  "atom" :
  {
    style : function( states ) {
      return {
        cursor                        : "default",
        spacing                       : 4,
        width                         : "auto",
        height                        : "auto",
        horizontalChildrenAlign       : "center",
        verticalChildrenAlign         : "middle"
      };
    }
  },

  // Note: This appearance applies to qooxdoo labels (as embedded in Atom,
  //       Button, etc.). For SWT Label, see apperance "label-wrapper".
  //       Any styles set for this appearance cannot be overridden by themeing
  //       of controls that include a label! This is because the "inheritance"
  //       feature does not overwrite theme property values from themes.
  "label" :
  {
  },

  // Appearance used for qooxdoo "labelObjects" which are part of Atoms etc.
  "label-graytext" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        textColor : states.disabled ? tv.getColor( "widget.graytext" ) : "undefined"
      };
    }
  },

  // this applies to a qooxdoo qx.ui.basic.Atom that represents an RWT Label
  "label-wrapper" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.font = tv.getFont( "widget.font" );
      if( states.rwt_BORDER ) {
        result.border = tv.getBorder( "label.BORDER.border" );
      } else {
        result.border = tv.getBorder( "label.border" );
      }
      if( states.over ) {
        result.backgroundColor = tv.getColor( "label.hover.background" );
        result.textColor = tv.getColor( "label.hover.foreground" );
      } else {
        result.backgroundColor = tv.getColor( "label.background" );
        result.textColor = tv.getColor( "label.foreground" );
      }
      return result;
    }
  },

  "clabel" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.textColor = states.disabled ? "widget.graytext" : "widget.foreground";
      result.backgroundColor = tv.getColor( "widget.background" );
      result.font = tv.getFont( "widget.font" );
      if( states.rwt_SHADOW_IN ) {
        result.border = "thinInset";
      } else if( states.rwt_SHADOW_OUT ) {
        result.border = "thinOutset";
      } else {
        result.border = tv.getBorder( states.rwt_BORDER
                                      ? "label.BORDER.border"
                                      : "label.border" );
      }
      return result;
    }
  },

  "htmlcontainer" :
  {
    include : "label"
  },

  "popup" :
  {
  },

  "tool-tip" :
  {
    include : "popup",

    style : function( states ) {
      return {
        backgroundColor : "widget.info.background",
        textColor       : "widget.info.foreground",
        border          : "info",
        padding         : [ 1, 3, 2, 3 ]
      };
    }
  },

  "iframe" :
  {
    style : function( states ) {
      return {
        border : "inset"
      };
    }
  },

  "check-box" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        font : tv.getFont( "widget.font" ),
        cursor : "default",
        width : "auto",
        height : "auto",
        horizontalChildrenAlign : "center",
        verticalChildrenAlign : "middle",
        spacing : 4,
        padding : [ 2, 3 ]
      };
      result.backgroundColor = tv.getColor( "button.CHECK.background" );
      if( states.disabled ) {
        result.textColor = tv.getColor( "widget.graytext" );
      } else {
        result.textColor = tv.getColor( "button.CHECK.foreground" );
      }
      if( states.rwt_BORDER ) {
        result.border = tv.getBorder( "control.BORDER.border" );
      } else {
        result.border = tv.getBorder( "control.border" );
      }
      return result;
    }
  },

  "radio-button" : {
    include : "check-box"
  },

  /*
  ---------------------------------------------------------------------------
    BUTTON
  ---------------------------------------------------------------------------
  */

  "button" :
  {
    include : "atom",

    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.font = tv.getFont( "button.font" );

      // foreground color
      if( states.disabled ) {
        result.textColor = tv.getColor( "widget.graytext" );
      } else if( states.rwt_FLAT && ( states.pressed || states.checked ) ) {
        result.textColor = tv.getColor( "button.FLAT.pressed.foreground" );
      } else if( states.over ) {
        result.textColor = tv.getColor( "button.hover.foreground" );
      } else {
        result.textColor = tv.getColor( "button.foreground" );
      }

      // background color
      if( states.rwt_FLAT && ( states.pressed || states.checked ) ) {
        result.backgroundColor = tv.getColor( "button.FLAT.pressed.background" );
      } else if( states.over ) {
        result.backgroundColor = tv.getColor( "button.hover.background" );
      } else {
        result.backgroundColor = tv.getColor( "button.background" );
      }

      // border
      if( states.rwt_FLAT ) {
        if( states.pressed || states.checked ) {
          result.border = tv.getBorder( "button.FLAT.pressed.border" );
        } else {
          result.border = tv.getBorder( "button.FLAT.border" );
        }
      } else if( states.rwt_BORDER ) {
        if( states.pressed || states.checked ) {
          result.border = tv.getBorder( "button.BORDER.pressed.border" );
        } else {
          result.border = tv.getBorder( "button.BORDER.border" );
        }
      } else {
        if( states.pressed || states.checked ) {
          result.border = tv.getBorder( "button.pressed.border" );
        } else {
          result.border = tv.getBorder( "button.border" );
        }
      }

      // padding
      if( !states.rwt_FLAT && ( states.pressed || states.checked ) ) {
        result.padding = [ 4, 3, 2, 5 ];
      } else {
        result.padding = [ 3, 4, 3, 4 ];
      }

      result.spacing = tv.getDimension( "button.spacing" );
      return result;
    }
  },

  /*
  ---------------------------------------------------------------------------
    TOOLBAR
  ---------------------------------------------------------------------------
  */

  "toolbar" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        font : tv.getFont( "widget.font" ),
        overflow : "hidden",
        border : tv.getBorder( states.rwt_BORDER ? "toolbar.BORDER.border" : "toolbar.border" ),
        textColor : tv.getColor( states.disabled ? "widget.graytext" : "widget.foreground" ),
        backgroundColor : tv.getColor( "toolbar.background" )
      };
    }
  },

  "toolbar-separator" :
  {
    style : function( states ) {
      return {
        width : 8
      };
    }
  },

  "toolbar-separator-line" :
  {
    style : function( states ) {
      return {
        top    : 2,
        left   : 3,
        width  : 2,
        bottom : 2,
        border : states.rwt_FLAT ? "horizontalDivider" : "undefined"
      };
    }
  },

  "toolbar-button" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        cursor : "default",
        overflow : "hidden",
        spacing : 4,
        width : "auto",
        verticalChildrenAlign : "middle"
      };
      result.backgroundImage = states.checked && !states.over
                               ? "static/image/dotted_white.gif"
                               : null;
      if( states.disabled ) {
        result.backgroundColor = tv.getColor( "toolbar.background" );
        result.textColor = "widget.graytext";
      } else if( states.over ) {
        result.backgroundColor = tv.getColor( "toolbar.hover.background" );
        result.textColor = tv.getColor( "toolbar.hover.foreground" );
      } else {
        result.backgroundColor = tv.getColor( "toolbar.background" );
        result.textColor = tv.getColor( "toolbar.foreground" );
      }
      if( states.pressed || states.checked || states.abandoned ) {
        result.border = "thinInset";
        result.padding = [ 3, 2, 1, 4 ];
      } else if( !states.rwt_FLAT || states.over ) {
        result.border = "thinOutset";
        result.padding = [ 2, 3 ];
      } else {
        result.border = "undefined";
        result.padding = [ 3, 4 ];
      }
      return result;
    }
  },

  /*
  ---------------------------------------------------------------------------
    WINDOW (SHELL)
  ---------------------------------------------------------------------------
  */

  "window" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        textColor : tv.getColor( "widget.foreground" ),
        backgroundColor : tv.getColor( "shell.background" ),
        border : tv.getBorder( ( states.rwt_TITLE || states.rwt_BORDER ) && !states.maximized
                               ? "shell.BORDER.border"
                               : "shell.border" ),
        minWidth  : states.rwt_TITLE ? 80 : 5,
        minHeight : states.rwt_TITLE ? 25 : 5
      };
    }
  },

  "window-captionbar" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        margin : tv.getBoxDimensions( "shell.title.margin" ),
        padding : tv.getBoxDimensions( "shell.title.padding" ),
        verticalChildrenAlign : "middle"
      };
      if( states.active ) {
        result.textColor = tv.getColor( "shell.title.foreground" );
        result.backgroundColor = tv.getColor( "shell.title.background" );
        result.backgroundImage = tv.getImage( "shell.title.active.bgimage" );
      } else {
        result.textColor = tv.getColor( "shell.title.inactive.foreground" );
        result.backgroundColor = tv.getColor( "shell.title.inactive.background" );
        result.backgroundImage = tv.getImage( "shell.title.inactive.bgimage" );
      }
      if( states.rwt_TITLE ) {
        result.minHeight = tv.getDimension( "shell.title.height" );
        result.maxHeight = tv.getDimension( "shell.title.height" );
      } else {
        result.minHeight = 0;
        result.maxHeight = 0;
      }
      return result;
    }
  },

  "window-resize-frame" :
  {
    style : function( states ) {
      return {
        border : "shadow"
      };
    }
  },

  "window-captionbar-icon" :
  {
    style : function( states ) {
      return {
        marginRight : 2
      };
    }
  },

  "window-captionbar-title" :
  {
    style : function( states ) {
      return {
        cursor : "default",
        font : "shell.title.font",
        marginRight : 2
      };
    }
  },

  "window-captionbar-button" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        margin : tv.getBoxDimensions( "shell.button.margin" )
      };
      return result;
    }
  },

  "window-captionbar-minimize-button" :
  {
    include : "window-captionbar-button",
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      if( states.active ) {
        if( states.over && !states.pressed ) {
          result.icon = tv.getImage( "shell.minbutton.over.image" );
        } else {
          result.icon = tv.getImage( "shell.minbutton.image" );
        }
      } else {
        if( states.over && !states.pressed ) {
          result.icon = tv.getImage( "shell.minbutton.inactive.over.image" );
        } else {
          result.icon = tv.getImage( "shell.minbutton.inactive.image" );
        }
      }
      return result;
    }
  },

  "window-captionbar-maximize-button" :
  {
    include : "window-captionbar-button",
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      if( states.active ) {
        if( states.over && !states.pressed ) {
          result.icon = tv.getImage( "shell.maxbutton.over.image" );
        } else {
          result.icon = tv.getImage( "shell.maxbutton.image" );
        }
      } else {
        if( states.over && !states.pressed ) {
          result.icon = tv.getImage( "shell.maxbutton.inactive.over.image" );
        } else {
          result.icon = tv.getImage( "shell.maxbutton.inactive.image" );
        }
      }
      return result;
    }
  },

  "window-captionbar-restore-button" :
  {
    include : "window-captionbar-button",
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      if( states.active ) {
        if( states.over && !states.pressed ) {
          result.icon = tv.getImage( "shell.restorebutton.over.image" );
        } else {
          result.icon = tv.getImage( "shell.restorebutton.image" );
        }
      } else {
        if( states.over && !states.pressed ) {
          result.icon = tv.getImage( "shell.restorebutton.inactive.over.image" );
        } else {
          result.icon = tv.getImage( "shell.restorebutton.inactive.image" );
        }
      }
      return result;
    }
  },

  "window-captionbar-close-button" :
  {
    include : "window-captionbar-button",
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      if( states.active ) {
        if( states.over && !states.pressed ) {
          result.icon = tv.getImage( "shell.closebutton.over.image" );
        } else {
          result.icon = tv.getImage( "shell.closebutton.image" );
        }
      } else {
        if( states.over && !states.pressed ) {
          result.icon = tv.getImage( "shell.closebutton.inactive.over.image" );
        } else {
          result.icon = tv.getImage( "shell.closebutton.inactive.image" );
        }
      }
      return result;
    }
  },

  "window-statusbar" :
  {
    style : function( states ) {
      return {
        border : "thinInset",
        height : "auto"
      };
    }
  },

  "window-statusbar-text" :
  {
    style : function( states ) {
      return {
        padding       : [ 1, 4 ],
        cursor        : "default"
      };
    }
  },

  /*
  ---------------------------------------------------------------------------
    RESIZER
  ---------------------------------------------------------------------------
  */

  // TODO [rst] necessary?

  "resizer" :
  {
    style : function( states ) {
      return {
        border : "outset"
      };
    }
  },

  "resizer-frame" :
  {
    style : function( states ) {
      return {
        border : "shadow"
      };
    }
  },

  /*
  ---------------------------------------------------------------------------
    MENU
  ---------------------------------------------------------------------------
  */

  "menu" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        width : "auto",
        height : "auto",
        textColor : tv.getColor( "menu.foreground" ),
        backgroundColor : tv.getColor( "menu.background" ),
        font : tv.getFont( "widget.font" ),
        overflow : "hidden",
        border : tv.getBorder( "menu.border" ),
        padding : tv.getBoxDimensions( "menu.padding" )
      };
    }
  },

  "menu-layout" :
  {
    style : function( states ) {
      return {
        top    : 0,
        right  : 0,
        bottom : 0,
        left   : 0
      };
    }
  },

  "menu-button" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        minWidth : "auto",
        height : "auto",
        spacing : 2,
        padding : [ 2, 4 ],
        cursor : "default",
        verticalChildrenAlign : "middle",
        backgroundColor : tv.getColor( states.over ? "menu.hover.background" : "menu.background" )
      };
      if( states.disabled ) {
        result.textColor = tv.getColor( "widget.graytext" );
      } else if( states.over ) {
        result.textColor = tv.getColor( "menu.hover.foreground" );
      } else {
        result.textColor = tv.getColor( "menu.foreground" );
      }
      return result;
    }
  },

  "menu-button-arrow" :
  {
    style : function( states ) {
      return {
        source : "widget/arrows/next.gif"
      };
    }
  },

  "menu-check-box" :
  {
    include : "menu-button",

    style : function(states)
    {
      return {
        icon : states.checked ? "widget/menu/checkbox.gif" : "static/image/blank.gif"
      };
    }
  },

  "menu-radio-button" :
  {
    include : "menu-button",

    style : function(states)
    {
      return {
        icon : states.checked ? "widget/menu/radiobutton.gif" : "static/image/blank.gif"
      };
    }
  },

  "menu-separator" :
  {
    style : function( states ) {
      return {
        height       : "auto",
        marginTop    : 3,
        marginBottom : 2,
        paddingLeft  : 3,
        paddingRight : 3
      };
    }
  },

  "menu-separator-line" :
  {
    style : function( states ) {
      return {
        right  : 0,
        left   : 0,
        height : 0,
        border : "verticalDivider"
      };
    }
  },

  "menubar-button" :
  {
    style : function( states ) {
      var result =
      {
        cursor : "default",
        overflow : "hidden",
        spacing : 4,
        width : "auto",
        padding : [ 3, 4 ],
        verticalChildrenAlign : "middle",
        backgroundImage : states.checked && !states.over ? "static/image/dotted_white.gif" : null
      };
      if( states.disabled ) {
        result.backgroundColor = "toolbar.background";
        result.textColor = "widget.graytext";
      } else if( states.over ) {
        result.backgroundColor = "menu.hover.background";
        result.textColor = "menu.hover.foreground";
      } else {
        result.backgroundColor = "menu.background";
        result.textColor = "menu.foreground";
      }
      return result;
    }
  },

  /*
  ---------------------------------------------------------------------------
    LIST
  ---------------------------------------------------------------------------
  */

  "list" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        overflow : "hidden",
        textColor : tv.getColor( "list.foreground" ),
        backgroundColor : tv.getColor( "list.background" ),
        border : states.rwt_BORDER ? "thinInset" : "undefined"
      };
    }
  },

  "list-item" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        cursor                  : "default",
        height                  : "auto",
        horizontalChildrenAlign : "left",
        verticalChildrenAlign   : "middle",
        spacing                 : 4,
        padding                 : [ 3, 5 ],
        minWidth                : "auto"
      };
      if( states.selected ) {
        if( states.parent_unfocused ) {
          result.textColor = tv.getColor( states.disabled ? "widget.graytext" : "list.selection.unfocused.foreground" );
          result.backgroundColor = tv.getColor( "list.selection.unfocused.background" );
        } else {
          result.textColor = tv.getColor( states.disabled ? "widget.graytext" : "list.selection.foreground" );
          result.backgroundColor = tv.getColor( "list.selection.background" );
        }
      } else {
        result.textColor = states.disabled ? tv.getColor( "widget.graytext" ) : "undefined";
        result.backgroundColor = null;
      }
      return result;
    }
  },

  /*
  ---------------------------------------------------------------------------
    TEXT
  ---------------------------------------------------------------------------
  */

  "text-field" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : tv.getBorder( states.rwt_BORDER ? "text.BORDER.border" : "text.border" ),
        font : tv.getFont( "widget.font" ),
        padding : tv.getBoxDimensions( "text.SINGLE.padding" ),
        textColor : tv.getColor( states.disabled ? "widget.graytext" : "list.foreground" ),
        backgroundColor : tv.getColor( "list.background" )
      };
    }
  },

  "text-area" : {
    include : "text-field",
    style : function( states ) {
      return {
        padding : states.rwt_BORDER ? [ 0, 0, 0, 4 ] : [ 0, 0, 0, 3 ]
      };
    }
  },

  /*
  ---------------------------------------------------------------------------
    COMBOBOX
  ---------------------------------------------------------------------------
  */

  "combo-box" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : "inset",
        backgroundColor : tv.getColor( "list.background" )
      };
    }
  },

  "combo-box-list" :
  {
    include : "list",

    style : function( states ) {
      return {
        border   : "undefined",
        overflow : "scrollY"
      };
    }
  },

  "combo-box-popup" :
  {
    include : "list",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        height    : "auto",
        border    : "shadow",
        textColor : tv.getColor( states.selected ? "list.selection.foreground" : "list.foreground" ),
        backgroundColor : tv.getColor( states.selected ? "list.selection.background" : "list.background" )
      };
    }
  },

  "combo-box-text-field" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        font : tv.getFont( "widget.font" ),
        padding : states.rwt_BORDER ? [ 1, 4 ] : [ 0, 3 ],
        textColor : tv.getColor( states.disabled ? "widget.graytext" : "list.foreground" ),
        backgroundColor : tv.getColor( "list.background" )
      };
    }
  },

  // Used both for ComboBox and ComboBoxEx
  "combo-box-button" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : "thinOutset",
        padding : [ 0, 3, 0, 2 ],
        icon : "widget/arrows/down.gif",
        // TODO [rst] rather use button.bgcolor?
        backgroundColor : tv.getColor( "widget.background" )
      };
    }
  },

  /*
  ---------------------------------------------------------------------------
    TREE
  ---------------------------------------------------------------------------
  */

  "tree-element" :
  {
    style : function( states ) {
      return {
        height                : 16,
        verticalChildrenAlign : "middle"
      };
    }
  },

  "tree-element-icon" :
  {
    style : function( states ) {
      return {
        width  : 16,
        height : 16
      };
    }
  },

  "tree-element-label" :
  {
    include : "label",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.cursor = "default";
      result.height = 16;
      result.padding = 2;
      if( states.disabled ) {
        result.textColor = tv.getColor( "widget.graytext" );
        result.backgroundColor = "undefined";
      } else if( states.selected ) {
        if( states.parent_unfocused ) {
          result.textColor = tv.getColor( "list.selection.unfocused.foreground" );
          result.backgroundColor = tv.getColor( "list.selection.unfocused.background" );
        } else {
          result.textColor = tv.getColor( "list.selection.foreground" );
          result.backgroundColor = tv.getColor( "list.selection.background" );
        }
      } else {
        result.textColor = tv.getColor( "list.foreground" );
        result.backgroundColor = "undefined";
      }      
      return result;
    }
  },

  "tree-folder" :
  {
    include : "tree-element"
  },

  "tree-folder-icon" :
  {
    include : "tree-element-icon"
  },

  "tree-folder-label" :
  {
    include : "tree-element-label"
  },

  "tree-container" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = states.rwt_BORDER
                      ? "control.BORDER.border"
                      : "control.border";
      return result;
    }
  },

  "tree" :
  {
    include : "tree-folder",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.verticalChildrenAlign = "top";
      result.backgroundColor = tv.getColor( "list.background" );
      result.border = tv.getBorder( states.rwt_BORDER
                                    ? "control.BORDER.border"
                                    : "control.border" );
      return result;
    }
  },

  "tree-icon" :
  {
    include : "tree-folder-icon"
  },

  "tree-label" :
  {
    include : "tree-folder-label"
  },

  "tree-check-box" : {
    include : "image",
    style : function( states ) {
      var result = {};
      if( states.grayed ) {
        if( states.checked ) {
          result.source = "widget/table/check_gray_on.gif";
        } else {
          result.source = "widget/table/check_gray_off.gif";
        }
      } else {
        if( states.checked ) {
          result.source = "widget/table/check_white_on.gif";
        } else {
          result.source = "widget/table/check_white_off.gif";
        }
      }
      result.marginRight = 3;
      return result;
    }
  },

  "tree-column" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.cursor = "default";
      result.paddingLeft = 2;
      result.paddingRight = 2;
      result.spacing = 2;
      result.textColor = states.disabled ? tv.getColor( "widget.graytext" ) : "undefined";
      if( states.mouseover && !states.disabled ) {
        result.backgroundColor = tv.getColor( "tree.column.hover.background" );
        result.border = tv.getBorder( "tree.column.hover.border" );
      } else {
        result.backgroundColor = tv.getColor( "tree.column.background" );
        result.border = tv.getBorder( "tree.column.border" );
      }
      if( states.moving ) {
        result.opacity = 0.6;
      } else {
        result.opacity = 1.0;
      }
      return result;
    }
  },

  "tree-column-resizer" : {
    style : function( sates ) {
      return {
        // TODO [rh] use same bg-color as splitpane-spltter (see there)
        backgroundColor : "#d6d5d9",
        width : 3
      }
    }
  },

  /*
  ---------------------------------------------------------------------------
    TAB FOLDER
  ---------------------------------------------------------------------------
  */

  "tab-view" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.textColor = tv.getColor( "widget.foreground" );
      result.font = tv.getFont( "widget.font" );
      result.spacing = -1;
      result.border = tv.getBorder( states.rwt_BORDER
                                    ? "control.BORDER.border"
                                    : "control.border" );
      return result;
    }
  },

  "tab-view-bar" :
  {
    style : function( states ) {
      return {
        height : "auto"
      };
    }
  },

  "tab-view-pane" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
//    result.height = "1*";
      result.overflow = "hidden";
      result.backgroundColor = tv.getColor( "widget.background" );
      result.border = new qx.ui.core.Border( 1, "solid", "widget.thinborder" );
      result.padding = 10;
      return result;
    }
  },

  "tab-view-page" :
  {
//      style : function( states ) {
//        return {
// TODO [rst] disappeared in qx 0.7
//          top    : 0,
//          right  : 0,
//          bottom : 0,
//          left   : 0
//        };
//      }
  },

  "tab-view-button" :
  {
    include : "atom",

    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );

      var border_top_normal = new qx.ui.core.Border( 1, "solid", "widget.thinborder" );
      border_top_normal.setWidthBottom( 0 );

      var border_top_checked = new qx.ui.core.Border( 1, "solid", "widget.thinborder" );
      border_top_checked.setWidthBottom( 0 );
      border_top_checked.setTop( 3, "solid", "widget.selection-marker" );

      var border_bottom_normal = new qx.ui.core.Border( 1, "solid", "widget.thinborder" );
      border_bottom_normal.setWidthTop( 0 );

      var border_bottom_checked = new qx.ui.core.Border( 1, "solid", "widget.thinborder" );
      border_bottom_checked.setWidthTop( 0 );
      border_bottom_checked.setBottom( 3, "solid", "widget.selection-marker" );

      if( states.checked ) {
        result.backgroundColor = tv.getColor( "tabfolder.checked.background" );
        result.zIndex = 1; // TODO [rst] Doesn't this interfere with our z-order?
        result.padding = [ 2, 8, 4, 7 ];
        result.border = states.barTop ? border_top_checked : border_bottom_checked;
        result.margin = [ 0, -1, 0, -2 ];
        if( states.alignLeft ) {
          if( states.firstChild ) {
            result.paddingLeft = 6;
            result.paddingRight = 7;
            result.marginLeft = 0;
          }
        } else {
          if( states.lastChild ) {
            result.paddingLeft = 8;
            result.paddingRight = 5;
            result.marginRight = 0;
          }
        }
      } else {
        result.backgroundColor = tv.getColor( states.over ? "tabfolder.hover.background" : "tabfolder.background" ),
        result.zIndex = 0, // TODO [rst] Doesn't this interfere with our z-order?
        result.padding = [ 2, 6, 2, 5 ];
        result.marginRight = 1;
        result.marginLeft = 0;
        if( states.alignLeft ) {
          if( states.firstChild ) {
            result.paddingLeft = 6;
            result.paddingRight = 5;
          }
        } else {
          if( states.lastChild ) {
            result.paddingLeft = 6;
            result.paddingRight = 5;
            result.marginRight = 0;
          }
        }
        if( states.barTop ) {
          result.border = border_top_normal;
          result.marginTop = 3;
          result.marginBottom = 1;
        } else {
          result.border = border_bottom_normal;
          result.marginTop = 1;
          result.marginBottom = 3;
        }
      }
      result.textColor = states.disabled ? tv.getColor( "widget.graytext" ) : "undefined";
      return result;
    }
  },

  /*
  ---------------------------------------------------------------------------
    GROUP BOX
  ---------------------------------------------------------------------------
  */

  "group-box" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        backgroundColor : tv.getColor( "group.background" ),
        border : tv.getBorder( states.rwt_BORDER
                               ? "control.BORDER.border"
                               : "control.border" )
      };
    }
  },

  "group-box-legend" :
  {
    include : "atom",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        top : 0,
        left : 0,
        padding : tv.getBoxDimensions( "group.label.padding" ),
        margin : tv.getBoxDimensions( "group.label.margin" ),
        font : "group.label.font",
        backgroundColor : tv.getColor( "group.background" )
// TODO [rst] Group label is not grayed out in SWT - check other toolkits
//          textColor : states.disabled ? "widget.graytext" : "undefined"
      };
    }
  },

  "group-box-frame" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        top : 0,
        left : 0,
        right : 0,
        bottom : 0,
        margin : tv.getBoxDimensions( "group.margin" ),
        border : tv.getBorder( "group.frame.border" )
      };
    }
  },

  /*
  ---------------------------------------------------------------------------
    SPINNER
  ---------------------------------------------------------------------------
  */

  "spinner" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        backgroundColor : tv.getColor( "list.background" ),
        border : tv.getColor( states.rwt_BORDER ? "text.BORDER.border" : "text.border" )
      };
    }
  },

  "spinner-text-field" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        top : 0,
        left : 0,
        right : 0,
        bottom : 0,
        padding : tv.getBoxDimensions( "text.SINGLE.padding" ),
        textColor : states.disabled ? tv.getColor( "widget.graytext" ) : "undefined"
      };
    }
  },

  "spinner-button" :
  {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        width : 16,
        backgroundColor : tv.getColor( "widget.background" )
      };
      if( states.rwt_FLAT ) {
        result.border = "undefined";
      } else if( states.pressed || states.checked || states.abandoned ) {
        result.border = "inset";
      } else {
        result.border = "outset";
      }
      return result;
    }
  },

  "spinner-button-up" :
  {
    include : "spinner-button",
    style : function( states ) {
      return {
        source : "widget/arrows/up_small.gif",
        padding : [ 0, 3, 1 ]
      };
    }
  },

  "spinner-button-down" :
  {
    include : "spinner-button",
    style : function( states ) {
      return {
        source : "widget/arrows/down_small.gif",
        padding : [ 0, 3, 1 ]
      };
    }
  },

  /*
  ---------------------------------------------------------------------------
    TABLE
  ---------------------------------------------------------------------------
  */

  "table" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        backgroundColor : tv.getColor( "list.background" ),
        textColor : tv.getColor( "list.foreground" ),
        font : tv.getFont( "widget.font" ),
        border : tv.getBorder( states.rwt_BORDER ? "control.BORDER.border" : "control.border" )
      };
    }
  },

  "table-column" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        cursor : "default",
        paddingLeft : 2,
        paddingRight : 2,
        spacing : 2,
        textColor : tv.getColor( states.disabled ? "widget.graytext" : "widget.foreground" ),
        opacity : states.moving ? 0.6 : 1.0
      };
      if( states.mouseover && !states.disabled ) {
        result.backgroundColor = tv.getColor( "table.column.hover.background" );
        result.border = tv.getColor( "table.column.hover.border" );
      } else {
        result.backgroundColor = tv.getColor( "table.column.background" );
        result.border = tv.getColor( "table.column.border" );
      }
      return result;
    }
  },

  "table-column-resizer" : {
    style : function( sates ) {
      return {
        width : 3,
        opacity : 0.3,
        backgroundColor : "black"
      }
    }
  },

  "table-row" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        cursor : "default",
        border : states.lines ? "table.row.horizontalLine" : "undefined"
      };
      if( states.disabled ) {
        result.textColor = tv.getColor( "widget.graytext" );
        result.textColor = tv.getColor( "undiefined" );
      } else if( states.selected ) {
        result.textColor = tv.getColor( "list.selection.foreground" );
        result.backgroundColor = tv.getColor( "list.selection.background" );
      } else {
        result.textColor = "list.foreground";
        result.backgroundColor = "list.background";
      }
      return result;
    }
  },

  "table-check-box" : {
    include : "image",
    style : function( states ) {
      var result = {};
      if( states.grayed ) {
        if( states.checked ) {
          result.source = "widget/table/check_gray_on.gif";
        } else {
          result.source = "widget/table/check_gray_off.gif";
        }
      } else {
        if( states.checked ) {
          result.source = "widget/table/check_white_on.gif";
        } else {
          result.source = "widget/table/check_white_off.gif";
        }
      }
      return result;
    }
  },

  // ------------------------------------------------------------------------
  // Sash

  "sash" : {
    style : function( states ) {
      return {
        border : states.rwt_BORDER ? "inset" : "undefined",
        cursor : states.disabled
                 ? "undefined"
                 : states.horizontal
                   ? "row-resize"
                   : "col-resize"
      };
    }
  },

  "sash-slider" : {
    style : function( states ) {
      return {
        zIndex : 1e7,
        opacity : 0.3,
        backgroundColor : "black"
      };
    }
  },

  // ------------------------------------------------------------------------
  // CTabFolder

  "ctabfolder" : {
    style: function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.font = tv.getFont( "widget.font" );
      result.textColor = tv.getColor( "ctabfolder.foreground" );
      result.backgroundColor = tv.getColor( "ctabfolder.background" );
      return result;
    }
  },

  "ctabfolder-body" : {
    style: function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.backgroundColor = "undefined";
      result.textColor = "undefined";
      if( states.rwt_BORDER ) {
        result.border = tv.getBorder( "ctabfolder.border" );
      } else {
        result.border = "undefined";
      }
      return result;
    }
  },

  "ctabfolder-frame" : {
    style: function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      if( !states.rwt_FLAT ) {
        var color = tv.getColor( "ctabfolder.selection.background" );
        result.border = new qx.ui.core.Border( 2, "solid", color );
      } else {
        result.border = "undefined";
      }
      result.backgroundColor = tv.getColor( "widget.background" );
      return result;
    }
  },

  "ctabfolder-separator" : {
    style: function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var border = new qx.ui.core.Border();
      if( states.barTop ) {
        border.setBottom( 1, "solid", "#c0c0c0" );
      } else {
        border.setTop( 1, "solid", "#c0c0c0" );
      }
      result.border = border;
      return result;
    }
  },

  "ctab-item" : {
    include: "atom",

    style: function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.paddingLeft = 4;
      result.border = new qx.ui.core.Border();
      result.border.setRight( 1, "solid", "#c0c0c0" );
      if( states.selected ) {
        result.textColor = tv.getColor( "ctabfolder.selection.foreground" );
        result.backgroundColor = tv.getColor( "ctabfolder.selection.background" );
        if( states.barTop ) {
          result.border.setTop( 1, "solid", "#c0c0c0" );
        } else {
          result.border.setBottom( 1, "solid", "#c0c0c0" );
        }
      } else {
        result.textColor = "undefined";
        result.backgroundColor = "undefined";
      }
      if( states.firstItem && states.rwt_BORDER ) {
        result.border.setLeft( 1, "solid", "#c0c0c0" );
      }
      return result;
    }
  },

  "ctabfolder-button" :
  {
    include : "image",
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      if( states.over ) {
        result.backgroundColor = "white";
        result.border = tv.getBorder( "ctabfolder.button.border" );
      } else {
        result.backgroundColor = "undefined";
        result.border = "undefined";
      }
      return result;
    }
  },

  "ctab-close-button" :
  {
    include : "image",

    style : function( states ) {
      return {
        source : states.over
          ? "widget/ctabfolder/close_hover.gif"
          : "widget/ctabfolder/close.gif"
      }
    }
  },

  // ------------------------------------------------------------------------
  // Composite

  "composite" : {
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.backgroundColor = tv.getColor( "widget.background" );
      result.border = tv.getBorder( states.rwt_BORDER
                                    ? "control.BORDER.border"
                                    : "control.border" );
      return result;
    }
  },

  // ------------------------------------------------------------------------
  // ScrolledComposite

  "scrolledcomposite" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : states.rwt_BORDER ? "shadow" : tv.getBorder( "control.border" )
      }
    }
  },

  // ------------------------------------------------------------------------
  // CoolBar

  "coolbar" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : tv.getBorder( states.rwt_BORDER ? "control.BORDER.border" : "control.border" )
      }
    }
  },

  "coolitem-handle" : {
    style : function( states ) {
      return {
        width : "100%",
        border : "thinOutset",
        margin : [ 1, 2, 1, 0 ],
        cursor : "w-resize"
      }
    }
  },

  // ------------------------------------------------------------------------
  // Browser

  "browser" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : tv.getBorder( states.rwt_BORDER ? "control.BORDER.border" : "control.border" ),
        backgroundColor : "white"
      }
    }
  },

  // ------------------------------------------------------------------------
  // Label (style SWT.SEPARATOR)

  "separator" : {
    style : function( states ) {
      return {
        border : states.rwt_BORDER ? "thinInset" : "undefined"
      }
    }
  },

  "separator-line" : {
    style : function( states ) {
      var result = {};
      var orient = states.rwt_VERTICAL ? "vertical" : "horizontal";
      if( states.rwt_SHADOW_IN ) {
        result.border = "separator.shadowin." + orient + ".border";
      } else if( states.rwt_SHADOW_OUT ) {
        result.border = "separator.shadowout." + orient + ".border";
      } else {
        result.border = "undefined";
      }
      return result;
    }
  },

  // ------------------------------------------------------------------------
  // Link

  "link" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        font : tv.getFont( "widget.font" ),
        border : tv.getBorder( states.rwt_BORDER ? "control.BORDER.border" : "control.border" )
      }
    }
  },

  "link-text" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        textColor : states.disabled ? tv.getColor( "widget.graytext" ) : "undefined"
      }
    }
  },

  "link-href" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        cursor : "pointer",
        textColor : tv.getColor( states.disabled ? "widget.graytext" : "link.foreground" )
      }
    }
  },

  // ------------------------------------------------------------------------
  // Progress Bar

  "progressbar" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : "thinInset",
        backgroundImage : "widget/progressbar.bgimage",
        backgroundColor : tv.getColor( "progressbar.background" )
      }
    }
  },

  "progressbar-bar" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        backgroundImage : "widget/progressbar.fgimage",
        backgroundColor : tv.getColor( "progressbar.foreground" )
      }
    }
  },

  "scrollbar-blocker" : {
    style : function( states ) {
      return {
        backgroundColor : "black",
        opacity : 0.2
      };
    }
  }
}

} );
