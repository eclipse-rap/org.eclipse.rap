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

qx.Theme.define( "org.eclipse.swt.theme.DefaultAppearances",
{
  title : "RAP default appearances",

  appearances :
  {
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

    "client-document" :
    {
      style : function( states ) {
        return {
          font : "default.font",
          textColor       : "black",
          backgroundColor : "white",
          // TODO [rst] Eliminate absolute references
          backgroundImage    : "./resource/widget/rap/display/bg.gif"
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
          cursor          : "default",
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
    // Button, etc.). For SWT Label, see apperance "label-wrapper".
    // Any styles set for this appearance cannot be overridden by themeing of
    // controls that include a label! This is because the "inheritance" feature
    // does not overwrite theme property values from themes.
    "label" :
    {
    },
    
    // this applies to a qooxdoo qx.ui.basic.Atom that represents an RWT Label
    "label-wrapper" :
    {
      style : function( states ) {
        return {
          textColor : states.disabled ? "widget.graytext" : "widget.foreground",
          backgroundColor : "widget.background",
          font : "default.font",
          border : states.rwt_BORDER ? "label.BORDER.border" : "label.border"
        };
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
        return {
          textColor : states.disabled ? "widget.graytext" : "widget.foreground",
          cursor : "default",
          width : "auto",
          height : "auto",
          horizontalChildrenAlign : "center",
          verticalChildrenAlign : "middle",
          spacing : 4,
          padding : [ 2, 3 ],
          border : states.rwt_BORDER ? "control.BORDER.border" : "control.border"
        };
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
        var result = { };
        
        result.font = "button.font";
        result.textColor = states.disabled ? "widget.graytext" : "button.foreground";
        
        // background color
        if( states.rwt_FLAT && ( states.pressed || states.checked ) ) {
          result.backgroundColor = "button.FLAT.pressed.background";
        } else if( states.over ) {
          result.backgroundColor = "button.hover.background";
        } else {
          result.backgroundColor = "button.background";
        }
        
        // border
        if( states.rwt_FLAT ) {
          if( states.over ) {
            result.border = "button.FLAT.hover.border";
          } else if( states.pressed || states.checked ) {
            result.border = "button.FLAT.pressed.border";
          } else {
            result.border = "border.FLAT.border";
          }
        } else if( states.rwt_BORDER ) {
          if( states.pressed || states.checked ) {
            result.border = "button.BORDER.pressed.border";
          } else {
            result.border = "border.BORDER.border";
          }
        } else {
          if( states.pressed || states.checked ) {
            result.border = "button.pressed.border";
          } else {
            result.border = "button.border";
          }
        }

        // padding
        if( !states.rwt_FLAT && ( states.pressed || states.checked ) ) {
          result.padding = [ 4, 3, 2, 5 ];
        } else {
          result.padding = [ 3, 4, 3, 4 ];
        }

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
        return {
          font            : "default.font",
          border          : states.rwt_BORDER ? "toolbar.BORDER.border" : "toolbar.border",
          textColor       : states.disabled ? "widget.graytext" : "widget.foreground",
          backgroundColor : "toolbar.background",
          height          : "auto"
        };
      }
    },
    
    /*
    
    // TODO [rst] Remove this as we don't use toolbar parts
    
    "toolbar-part" :
    {
      style : function( states ) {
        return {
          width : "auto"
        };
      }
    },
    
    "toolbar-part-handle" :
    {
      style : function( states ) {
        return {
          width : 10
        };
      }
    },

    "toolbar-part-handle-line" :
    {
      style : function( states ) {
        return {
          top    : 2,
          left   : 3,
          bottom : 2,
          width  : 4,
          border : "thinOutset"
        };
      }
    },
    
    */
    
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
        var result =
        {
          cursor : "default",
          spacing : 4,
          width : "auto",
          verticalChildrenAlign : "middle",
          // TODO [rst] find out what state "abandoned" means
          backgroundImage : states.checked && !states.over ? "static/image/dotted_white.gif" : null,
          backgroundColor : "toolbar.background",
          textColor : "toolbar.foreground"
        };
        if( states.disabled ) {
          result.textColor = "widget.graytext";
        } else if( states.over ) {
          result.backgroundColor = "toolbar.hover.background";
          result.textColor = "toolbar.hover.foreground";
        }
        if( states.pressed || states.checked || states.abandoned ) {
          result.border = "thinInset";
          result.padding = [3, 2, 1, 4 ];
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
        return {
          textColor       : "widget.foreground",
          backgroundColor : "shell.background",
//          padding         : 1,
          border          : ( states.rwt_TITLE || states.rwt_BORDER )
                              && !states.maximized
                                ? "shell.border"
                                : "black",
          minWidth  : states.rwt_TITLE ? 80 : 5,
          minHeight : states.rwt_TITLE ? 25 : 5
        };
      }
    },
    
    "window-captionbar" :
    {
      style : function( states ) {
        return {
          padding               : [ 1, 2 ],
          verticalChildrenAlign : "middle",
          height                : "auto",
          overflow              : "hidden",
          textColor             : states.active ? "shell.title.foreground" : "shell.title.inactive.foreground",
          backgroundColor       : states.active ? "shell.title.background" : "shell.title.inactive.background",
          backgroundImage       : states.active ? "widget/window/caption_active.gif" : "widget/window/caption_inactive.gif",
          minHeight             : states.rwt_TITLE ? 18 : 0,
          maxHeight             : states.rwt_TITLE ? 18 : 0
        };
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
          font : "bold.font",
          marginRight : 2
        };
      }
    },
    
    "window-captionbar-button" :
    {
      include : "button",

      style : function( states ) {
        var result = {
          border : "white",
          padding : 1,
          marginLeft : 1
        };
        return result;
      }
    },
    
    "window-captionbar-minimize-button" :
    {
      include : "window-captionbar-button",
      style : function( states ) {
        var result = {
          icon : "widget/window/minimize.gif"
        };
        if( states.over && !states.pressed ) {
          result.backgroundColor = "shell.minbutton.over.background";
        } else if( !states.active ) {
          result.backgroundColor = "shell.minbutton.inactive.background";
        } else {
          result.backgroundColor = "shell.minbutton.background";
        }
        return result;
      }
    },

    "window-captionbar-maximize-button" :
    {
      include : "window-captionbar-button",
      style : function( states ) {
        var result = {
          icon : "widget/window/maximize.gif"
        };
        if( states.over && !states.pressed ) {
          result.backgroundColor = "shell.maxbutton.over.background";
        } else if( !states.active ) {
          result.backgroundColor = "shell.maxbutton.inactive.background";
        } else {
          result.backgroundColor = "shell.maxbutton.background";
        }
        return result;
      }
    },

    "window-captionbar-restore-button" :
    {
      include : "window-captionbar-button",
      style : function( states ) {
        var result = {
          icon : "widget/window/restore.gif"
        };
        if( states.over && !states.pressed ) {
          result.backgroundColor = "shell.maxbutton.over.background";
        } else if( !states.active ) {
          result.backgroundColor = "shell.maxbutton.inactive.background";
        } else {
          result.backgroundColor = "shell.maxbutton.background";
        }
        return result;
      }
    },

    "window-captionbar-close-button" :
    {
      include : "window-captionbar-button",
      style : function( states ) {
        var result = {
          icon : "widget/window/close.gif"
        };
        if( states.over && !states.pressed ) {
          result.backgroundColor = "shell.closebutton.over.background";
        } else if( !states.active ) {
          result.backgroundColor = "shell.closebutton.inactive.background";
        } else {
          result.backgroundColor = "shell.closebutton.background";
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
        return {
          width           : "auto",
          height          : "auto",
          textColor       : "menu.foreground",
          backgroundColor : "menu.background",
          border          : "outset",
          padding         : 1
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
        var result = {
          minWidth              : "auto",
          height                : "auto",
          spacing               : 2,
          padding               : [ 2, 4 ],
          cursor                : "default",
          verticalChildrenAlign : "middle",
          backgroundColor       : states.over ? "#316ac5" : "undefined"
        };
        if( states.disabled ) {
          result.textColor = "widget.graytext";
        } else if( states.over ) {
          result.textColor = "list.selection.foreground";
        } else {
          result.textColor = "undefined";
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
      include : "menu-button"
    },
    
    "menu-radio-button" :
    {
      include : "menu-button"
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
    
    /*
    ---------------------------------------------------------------------------
      LIST
    ---------------------------------------------------------------------------
    */

    "list" :
    {
      style : function( states ) {
        return {
          textColor : states.disabled ? "widget.graytext" : "widget.foreground",
          overflow : "hidden",
          backgroundColor : "list.background",
          border : states.rwt_BORDER ? "thinInset" : "undefined"
        };
      }
    },

    "list-item" :
    {
      style : function( states ) {
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
            result.textColor = "list.selection.unfocused.foreground";
            result.backgroundColor = "list.selection.unfocused.background";
          } else {
            result.textColor = "list.selection.foreground";
            result.backgroundColor = "list.selection.background";
          }
        } else {
          result.textColor = "undefined";
          result.backgroundColor = null;
        }
        return result;
      }
    },

    /*
    ---------------------------------------------------------------------------
      FIELDS
    ---------------------------------------------------------------------------
    */

    "text-field" :
    {
      style : function( states ) {
        return {
          border : states.rwt_BORDER ? "text.BORDER.border" : "text.border",
          font : "default.font",
          padding : states.rwt_BORDER ? [ 1, 4 ] : [ 0, 3 ],
//          TODO [rst] Do we still need this? Seems to work without as well
//                     Do we ever create a widget without setting its size?
//          width           : "auto",
//          height          : "auto",
          textColor       : states.disabled ? "widget.graytext" : "undefined",
          backgroundColor : "list.background"
        };
      }
    },
    
    "text-area" : {
      include : "text-field"
    },
    
    /*
    ---------------------------------------------------------------------------
      COMBOBOX
    ---------------------------------------------------------------------------
    */
    
    "combo-box" :
    {
      style : function( states ) {
        return {
          minWidth        : 40, // TODO [rst] removed by qx
          width           : 120,
          height          : "auto", // TODO [rst] removed by qx
          border          : "inset",
          backgroundColor : "list.background"
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
        return {
          height    : "auto",
          maxHeight : 150,
          border    : "shadow",
          textColor : states.selected ? "list.selection.foreground" : "list.foreground",
          backgroundColor : states.selected ? "list.selection.background" : "list.background"
        };
      }
    },
    
    "combo-box-text-field" :
    {
      include : "text-field",

      style : function( states ) {
        return {
          border          : "undefined",
          backgroundColor : "transparent"
        };
      }
    },
    
    // Used both for ComboBox and ComboBoxEx
    "combo-box-button" :
    {
      include : "button",

      style : function( states ) {
        return {
          padding : [ 0, 3, 0, 2 ],
          icon : "widget/arrows/down.gif",
          // TODO [rst] rather use button.bgcolor?
          backgroundColor : "widget.background"
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
        var result = {
          cursor : "default",
          marginLeft : 3,
          height : 15,
          padding : 2
        };
        if( states.selected ) {
          result.textColor = "list.selection.foreground";
          result.backgroundColor = "list.selection.background";
          /*
          if( states.tree_focused ) {
            result.textColor = "list.selection.foreground";
            result.backgroundColor = "list.selection.background";
          } else {
            result.textColor = "list.selection.unfocused.foreground";
            result.backgroundColor = "list.selection.unfocused.background";
          }
          */
        } else {
          result.textColor = "list.foreground";
          result.backgroundColor = "list.background";
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

    "tree" :
    {
      include : "tree-folder",
      style : function( states ) {
        return {
          textColor : states.disabled ? "widget.graytext" : "widget.foreground",
          verticalChildrenAlign : "top",
          backgroundColor       : "list.background",
          border : states.rwt_BORDER
            ? "control.BORDER.border"
            : "control.border"
        };
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

    /*
    ---------------------------------------------------------------------------
      TABVIEW
    ---------------------------------------------------------------------------
    */

    "tab-view" :
    {
      style : function( states ) {
        return {
          font : "default.font",
          textColor : states.disabled ? "widget.graytext" : "widget.foreground",
          spacing : -1,
          border : states.rwt_BORDER 
            ? "control.BORDER.border" 
            : "control.border"
        };
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
        return {
//          height          : "1*",
          backgroundColor : "widget.background", // "#FAFBFE",
          border          : new qx.renderer.border.Border(1, "solid", "#aca899"),
          padding         : 10
        };
      }
    },
    
    "tab-view-page" :
    {
// TODO [rst] disappeared in qx 0.7
//      style : function( states ) {
//        return {
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
        var border_top_normal = new qx.renderer.border.Border(1, "solid", "#aca899");
        border_top_normal.setWidthBottom(0);

        var border_top_checked = new qx.renderer.border.Border(1, "solid", "#aca899");
        border_top_checked.setWidthBottom(0);
        border_top_checked.setTop(3, "solid", "widget.selection-marker");

        var border_bottom_normal = new qx.renderer.border.Border(1, "solid", "#aca899");
        border_bottom_normal.setWidthTop(0);

        var border_bottom_checked = new qx.renderer.border.Border(1, "solid", "#aca899");
        border_bottom_checked.setWidthTop(0);
        border_bottom_checked.setBottom(3, "solid", "widget.selection-marker");

        var result;

        if( states.checked ) {
          result = {
            backgroundColor : "tabfolder.checked.background",
            zIndex : 1, // TODO [rst] Doesn't this interfere with our z-order?
            padding : [ 2, 8, 4, 7 ],
            border : states.barTop ? border_top_checked : border_bottom_checked,
            margin : [ 0, -1, 0, -2 ]
          };
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
          result = {
            backgroundColor : states.over ? "tabfolder.hover.background" : "tabfolder.background",
            zIndex          : 0, // TODO [rst] Doesn't this interfere with our z-order?
            padding         : [ 2, 6, 2, 5 ],
            marginRight     : 1,
            marginLeft      : 0
          };
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
        return result;
      }
    },
    
    /*
    ---------------------------------------------------------------------------
      FIELDSET ( GROUP )
    ---------------------------------------------------------------------------
    */

    "group-box" :
    {
      style : function( states ) {
        return {
          backgroundColor : "widget.background"
        };
      }
    },

    "group-box-legend" :
    {
      include : "atom",

      style : function( states ) {
        return {
          top             : 1,
          left            : 10,
          backgroundColor : "widget.background",
          paddingRight    : 3,
          paddingLeft     : 4,
//          marginRight     : 10, TODO [rst] added by qx 0.7
          textColor       : states.disabled ? "widget.graytext" : "undefined"
        };
      }
    },

    "group-box-frame" :
    {
      style : function( states ) {
        return {
          top           : 8,
          left          : 2,
          right         : 2,
          bottom        : 2,
//          padding       : [ 12, 9 ], TODO [rst] why did we remove that?
          border        : "group.border"
        };
      }
    },

    "check-box-field-set-legend" :
    {
      include : "atom",

      style : function( states ) {
        return {
          top             : 1,
          left            : 10,
          backgroundColor : "#ece9d8",
          paddingRight    : 3
        };
      }
    },

    "radio-button-field-set-legend" :
    {
      include : "check-box-field-set-legend"
    },
    
    /*
    ---------------------------------------------------------------------------
      SPINNER
    ---------------------------------------------------------------------------
    */
    
    "spinner" :
    {
      style : function( states ) {
        return {
          width           : 60,
          height          : 22,
          border          : states.rwt_BORDER ? "inset" : "undefined",
          backgroundColor : "white"
        };
      }
    },

    "spinner-field" :
    {
      include : "text-field",

      style : function( states ) {
        return {
//          width  : "1*",
          border : "undefined"
        };
      }
    },

    "spinner-button-up" :
    {
      style : function( states ) {
        var result = {
//          height          : "1*",
          width : 16,
          source : "widget/arrows/up_small.gif",
          backgroundColor : "widget.background",
          padding : [ 0, 0, 0, 3 ]
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

    "spinner-button-down" :
    {
      style : function( states ) {
        var result = {
//          height          : "1*",
          width : 16,
          source : "widget/arrows/down_small.gif",
          backgroundColor : "widget.background",
          padding       : [ 1, 0, 0, 3 ]
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
    
    /*
    ---------------------------------------------------------------------------
      TABLE
    ---------------------------------------------------------------------------
    */
    
    // TODO [rst] Appearance table has gone in qx 0.7
    "table" : {
      style : function( states ) {
        return {
          backgroundColor : "white",
          font : "default.font",
          border : states.rwt_BORDER 
            ? "control.BORDER.border" 
            : "control.border"
        };
      }
    },
    
    // TODO [rst] Appearance table has gone in qx 0.7
    "table-column" : {
      style : function( states ) {
        var result = {
          cursor          : "default",
          paddingLeft     : 2,
          paddingRight    : 2,
          spacing         : 2
        };
        if( states.mouseover && !states.disabled ) {
          result.backgroundColor = "table.column.hover.background";
          result.border          = "table.column.hover.border";
        } else {
          result.backgroundColor = "table.column.background";
          result.border          = "table.column.border";
        }
        return result;
      }
    },
    
    "table-column-resizer" : {
      style : function( sates ) {
        return {
          // TODO [rh] use same bg-color as splitpane-spltter (see there)
          backgroundColor : "#d6d5d9",
          width : 3
        }
      }
    },
    
    "table-row" : {
      style : function( states ) {
        var result = {};
        if( states.disabled ) {
          if( states.selected ) {
            result.backgroundColor = "list.selection.unfocused.background";
            result.textColor = "list.selection.foreground";
          } else {
            result.textColor = "widget.graytext"
          }
        } else {
          if( states.selected ) {
            result.backgroundColor = "list.selection.background";
            result.textColor = "list.selection.foreground";
          } else {
            result.backgroundColor = null;
            result.textColor = null;
          }
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
    
    /*
    ---------------------------------------------------------------------------
      SPLITPANE
    ---------------------------------------------------------------------------
    */

    "splitpane" :
    {
      style : function( states ) {
        return {
//          overflow : "hidden",
          border : states.rwt_BORDER ? "inset" : "undefined"
        };
      }
    },

    "splitpane-glasspane" :
    {
      style : function( states ) {
        return {
//          zIndex          : 1e7,
          backgroundColor : "widget.shadow",
          opacity : states.visible ? 0.2 : 0
        };
      }
    },

    "splitpane-splitter" :
    {
      style : function( states ) {
        return {
//          backgroundColor : "widget.background", TODO [rst] why did we remove this?
          cursor : states.disabled 
            ? "undefined" : states.horizontal ? "col-resize" : "row-resize"
        };
      }
    },

    "splitpane-slider" :
    {
      style : function( states ) {
        return {
          opacity : 0.5,
//          zIndex  : 1e8,
          backgroundColor : states.dragging ? "widget.darkshadow" : "widget.background"
        };
      }
    },
    
    // Not used since 'knob' is never displayed but necessary to prevent error
    // when using SplitPane
    "splitpane-knob" :
    {
      style : function( states ) {
        var result = { opacity : states.dragging ? 0.5 : 1.0 };
        if( states.horizontal ) {
          result.top = "33%";
          result.left = null;
          result.marginLeft = -6;
          result.marginTop = 0;
          result.cursor = "col-resize";
        } else if( states.vertical ) {
          result.top = null;
          result.left = "33%";
          result.marginTop = -6;
          result.marginLeft = 0;
          result.cursor = "row-resize";
        }
        return result;
      }
    },
    
    /*
    ---------------------------------------------------------------------------
      RAP-SPECIFIC APPEARANCES
    ---------------------------------------------------------------------------
    */
    
    // ------------------------------------------------------------------------
    // CTabFolder

    "c-tab-item" :
    {
      include: "atom",
        
      style: function( states ) {
        var border_top = new qx.renderer.border.Border();
        border_top.setRight( 1, "solid", "#c0c0c0" );

        var border_top_checked = new qx.renderer.border.Border();
        border_top_checked.setLeft( 1, "outset", null );
        border_top_checked.setTop( 1, "outset", null );
        border_top_checked.setRight( 1, "solid", "#c0c0c0" );

        var border_bottom = new qx.renderer.border.Border();
        border_bottom.setLeft( 1, "solid", "#c0c0c0" );
        border_bottom.setRight( 1, "solid", "#c0c0c0" );

        var border_bottom_checked = new qx.renderer.border.Border();
        border_bottom_checked.setTop( 1, "solid", "#c0c0c0" );
        border_bottom_checked.setLeft( 1, "solid", "#c0c0c0" );
        border_bottom_checked.setRight( 1, "solid", "#c0c0c0" );

        var result = {
          border : border_top,
          paddingLeft : 4
        };
        if( states.checked ) {
          if( states.barTop ) {
            result.border = border_top_checked;
          } else {  // bar at bottom
            result.border = border_bottom_checked;
          }
        } else {
          if( states.barTop ) {
            result.border = border_top;
          } else {
            result.border = border_bottom;
          }
        }
        return result;
      }
    },
      
    "c-tab-close-button" :
    {
      include : "image",
      
      style : function( states ) {
        return {
          backgroundColor : states.over ? "#00008B" : "undefined"
        }
      }
    },
    
    // ------------------------------------------------------------------------
    // Composite
    
    "composite" : {
      include : "",
      
      style : function( states ) {
        return {
          border : states.rwt_BORDER ? "control.BORDER.border" : "control.border"
        }
      }
    },
      
    // ------------------------------------------------------------------------
    // CoolBar
    
    "coolbar" : {
      style : function( states ) {
        return {
          border : states.rwt_BORDER ? "control.BORDER.border" : "control.border"
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
        return {
          border : states.rwt_BORDER ? "control.BORDER.border" : "control.border",
          backgroundColor : "white"
        }
      }
    },

    // ------------------------------------------------------------------------
    // Label (style SWT.SEPARATOR)
    
    "separator" : {
      style : function( states ) {
        return {
          textColor : states.disabled ? "widget.graytext" : "undefined",
          border : states.rwt_BORDER ? "thinInset" : "undefined"
        }
      }
    },
    
    // ------------------------------------------------------------------------
    // Link
    
    "link" : {
      style : function( states ) {
        return {
          border : states.rwt_BORDER ? "control.BORDER.border" : "control.border"
        }
      }
    },
    
    "link-text" : {
      style : function( states ) {
        return {
          font : "default.font",
          textColor : states.disabled ? "widget.graytext" : "undefined"
        }
      }
    },
    
    "link-ref" : {
      style : function( states ) {
        return {
          cursor : "pointer",
          font : "link.ref.font",
          textColor : states.disabled ? "widget.graytext" : "link.foreground"
//          textColor : states.disabled ? "widget.graytext" : "#00007f"
        }
      }
    }
  }
});
