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
    style : function( states ) {
      return {
        source : "widget/cursors/move.gif"
      };
    }
  },

  "cursor-dnd-copy" : {
    style : function( states ) {
      return {
        source : "widget/cursors/copy.gif"
      };
    }
  },

  "cursor-dnd-alias" : {
    style : function( states ) {
      return {
        source : "widget/cursors/alias.gif"
      };
    }
  },

  "cursor-dnd-nodrop" : {
    style : function( states ) {
      return {
        source : "widget/cursors/nodrop.gif"
      };
    }
  },

  "client-document" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        font : tv.getCssFont( "*", "font" ),
        textColor : "black",
        backgroundColor : "white",
        // TODO [rst] Eliminate absolute references
        backgroundImage : "./resource/widget/rap/display/bg.gif"
      };
    }
  },

  "client-document-blocker" : {
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

  "atom" : {
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
  "label" : {
  },

  // Appearance used for qooxdoo "labelObjects" which are part of Atoms etc.
  "label-graytext" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        textColor : states.disabled
                    ? tv.getCssColor( "*", "color" )
                    : "undefined"
      };
    }
  },

  // this applies to a qooxdoo qx.ui.basic.Atom that represents an RWT Label
  "label-wrapper" : {
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.font = tv.getCssFont( "Label", "font" );
      var decoration = tv.getCssIdentifier( "Label", "text-decoration" );
      if( decoration != null && decoration != "none" ) {
        var decoratedFont = new qx.ui.core.Font();
        decoratedFont.setSize( result.font.getSize() );
        decoratedFont.setFamily( result.font.getFamily() );
        decoratedFont.setBold( result.font.getBold() );
        decoratedFont.setItalic( result.font.getItalic() );
        decoratedFont.setDecoration( decoration );
        result.font = decoratedFont;
      }
      result.textColor = tv.getCssColor( "Label", "color" );
      result.backgroundColor = tv.getCssColor( "Label", "background-color" );
      result.border = tv.getCssBorder( "Label", "border" );
      result.cursor = "default";
      return result;
    }
  },

  "clabel" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.textColor = states.disabled
                         ? "widget.graytext"
                         : tv.getCssColor( "*", "color" );
      result.backgroundColor = tv.getCssColor( "*", "background-color" );
      result.font = tv.getCssFont( "*", "font" );
      if( states.rwt_SHADOW_IN ) {
        result.border = "thinInset";
      } else if( states.rwt_SHADOW_OUT ) {
        result.border = "thinOutset";
      } else {
        result.border = tv.getCssBorder( "Label", "border" );
      }
      result.cursor = "default";
      return result;
    }
  },

  "htmlcontainer" : {
    include : "label"
  },

  "popup" : {
  },

  "tool-tip" : {
    include : "popup",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = "info";
      result.padding = [ 1, 3, 2, 3 ];
      result.backgroundColor = tv.getCssColor( "ToolTip", "background-color" );
      result.textColor = tv.getCssColor( "ToolTip", "color" );
      return result;
    }
  },

  "iframe" : {
    style : function( states ) {
      return {
        border : "inset"
      };
    }
  },

  /*
  ---------------------------------------------------------------------------
    BUTTON
  ---------------------------------------------------------------------------
  */

  "button" : {
    include : "atom",

    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );

      result.font = tv.getCssFont( "Button", "font" );
      result.textColor = tv.getCssColor( "Button", "color" );
      result.backgroundColor = tv.getCssColor( "Button", "background-color" );
      result.backgroundImage = tv.getCssImage( "Button", "background-image" );
      result.border = tv.getCssBorder( "Button", "border" );
      result.spacing = tv.getCssDimension( "Button", "spacing" );
      result.padding = tv.getCssBoxDimensions( "Button", "padding" );
      return result;
    }
  },

  // ------------------------------------------------------------------------
  // CheckBox

  "check-box" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : tv.getCssBorder( "Button", "border" ),
        font : tv.getCssFont( "Button", "font" ),
        textColor : tv.getCssColor( "Button", "color" ),
        backgroundColor : tv.getCssColor( "Button", "background-color" ),
        backgroundImage : tv.getCssImage( "Button", "background-image" ),
        padding : tv.getCssBoxDimensions( "Button", "padding" )
      }
    }
  },

  "check-box-icon" : {
    include: "image",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.width = 13;
      result.height = 13;
      result.clipWidth = 13;
      result.clipHeight = 13;
      result.source = tv.getCssImage( "Button-CheckIcon", "background-image" );
      return result;
    }
  },

  // ------------------------------------------------------------------------
  // RadioButton

  "radio-button" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : tv.getCssBorder( "Button", "border" ),
        font : tv.getCssFont( "Button", "font" ),
        textColor : tv.getCssColor( "Button", "color" ),
        backgroundColor : tv.getCssColor( "Button", "background-color" ),
        padding : tv.getCssBoxDimensions( "Button", "padding" )
      }
    }
  },

  "radio-button-icon" : {
    include: "image",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.width = 13;
      result.height = 13;
      result.clipWidth = 13;
      result.clipHeight = 13;
      result.source = tv.getCssImage( "Button-RadioIcon", "background-image" );
      return result;
    }
  },

  /*
  ---------------------------------------------------------------------------
    TOOLBAR
  ---------------------------------------------------------------------------
  */

  "toolbar" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        font : tv.getCssFont( "*", "font" ),
        overflow : "hidden",
        border : states.rwt_BORDER ? "toolbar.BORDER.border" : "toolbar.border",
        textColor : tv.getCssColor( "ToolBar", "color" ),
        backgroundColor : tv.getCssColor( "ToolBar", "background-color" )
      };
    }
  },

  "toolbar-separator" : {
    style : function( states ) {
      return {
        width : 8
      };
    }
  },

  "toolbar-separator-line" : {
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

  "toolbar-button" : {
    style : function( states ) {
      if( states.pressed || states.checked ) {
        states.selected = true;
      } else if( states.selected ) {
        delete states.selected;
      }
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        cursor : "default",
        overflow : "hidden",
        spacing : 4,
        width : "auto",
        verticalChildrenAlign : "middle"
      };
      result.textColor = states.disabled
                         ? "widget.graytext"
                         : tv.getCssColor( "ToolItem", "color" );
      result.backgroundColor = tv.getCssColor( "ToolItem", "background-color" );
      result.backgroundImage = states.checked && !states.over
                               ? "static/image/dotted_white.gif"
                               : null;
      result.border = tv.getCssBorder( "ToolItem", "border" );
      result.padding = tv.getCssBoxDimensions( "ToolItem", "padding" );
      return result;
    }
  },

  /*
  ---------------------------------------------------------------------------
    WINDOW (SHELL)
  ---------------------------------------------------------------------------
  */

  "window" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      // padding is only applied on the server, since client area content is
      // positioned absolutely
      result.backgroundColor = tv.getCssColor( "Shell", "background-color" );
      result.border = tv.getCssBorder( "Shell", "border" );
      result.minWidth = states.rwt_TITLE ? 80 : 5;
      result.minHeight = states.rwt_TITLE ? 25 : 5;
      return result;
    }
  },

  "window-captionbar" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        verticalChildrenAlign : "middle"
      };
      result.margin = tv.getCssBoxDimensions( "Shell-Titlebar", "margin" );
      result.padding = tv.getCssBoxDimensions( "Shell-Titlebar", "padding" );
      result.textColor = tv.getCssColor( "Shell-Titlebar", "color" );
      result.backgroundColor
        = tv.getCssColor( "Shell-Titlebar", "background-color" );
      result.backgroundImage
        = tv.getCssImage( "Shell-Titlebar", "background-image" );
      if( states.rwt_TITLE ) {
        result.minHeight = tv.getCssDimension( "Shell-Titlebar", "height" );
      } else {
        result.minHeight = 0;
      }
      result.maxHeight = result.minHeight;
      return result;
    }
  },

  "window-resize-frame" : {
    style : function( states ) {
      return {
        border : "shadow"
      };
    }
  },

  "window-captionbar-icon" : {
    style : function( states ) {
      return {
        marginRight : 2
      };
    }
  },

  "window-captionbar-title" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        cursor : "default",
        font : tv.getCssFont( "Shell-Titlebar", "font" ),
        marginRight : 2
      };
    }
  },

  "window-captionbar-minimize-button" : {
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.icon = tv.getCssImage( "Shell-MinButton", "background-image" );
      result.margin = tv.getCssBoxDimensions( "Shell-MinButton", "margin" );
      return result;
    }
  },

  "window-captionbar-maximize-button" : {
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.icon = tv.getCssImage( "Shell-MaxButton", "background-image" );
      result.margin = tv.getCssBoxDimensions( "Shell-MaxButton", "margin" );
      return result;
    }
  },

  "window-captionbar-restore-button" : {
    include : "window-captionbar-maximize-button"
  },

  "window-captionbar-close-button" : {
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.icon = tv.getCssImage( "Shell-CloseButton", "background-image" );
      result.margin = tv.getCssBoxDimensions( "Shell-CloseButton", "margin" );
      return result;
    }
  },

  "window-statusbar" : {
    style : function( states ) {
      return {
        border : "thinInset",
        height : "auto"
      };
    }
  },

  "window-statusbar-text" : {
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

  "resizer" : {
    style : function( states ) {
      return {
        border : "outset"
      };
    }
  },

  "resizer-frame" : {
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

  "menu" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        width : "auto",
        height : "auto",
        textColor : tv.getCssColor( "Menu", "color" ),
        backgroundColor : tv.getCssColor( "Menu", "background-color" ),
        font : tv.getCssFont( "Menu", "font" ),
        overflow : "hidden",
        border : tv.getCssBorder( "Menu", "border" ),
        padding : tv.getCssBoxDimensions( "Menu", "padding" )
      };
    }
  },

  "menu-layout" : {
    style : function( states ) {
      return {
        top    : 0,
        right  : 0,
        bottom : 0,
        left   : 0
      };
    }
  },

  "menu-button" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        minWidth : "auto",
        height : "auto",
        spacing : 2,
        padding : [ 2, 4 ],
        cursor : "default",
        verticalChildrenAlign : "middle",
        backgroundColor : tv.getCssColor( "MenuItem", "background-color" )
      };
      result.textColor = states.disabled
                         ? "widget.graytext"
                         : tv.getCssColor( "MenuItem", "color" );
      return result;
    }
  },

  "menu-button-arrow" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        source : tv.getCssImage( "MenuItem-CascadeIcon", "background-image" )
      };
    }
  },

  "menu-check-box" : {
    include : "menu-button",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        icon : states.checked
               ? tv.getCssImage( "MenuItem-CheckIcon", "background-image" )
               : "static/image/blank.gif"
      };
    }
  },

  "menu-radio-button" : {
    include : "menu-button",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        icon : states.checked
               ? tv.getCssImage( "MenuItem-RadioIcon", "background-image" )
               : "static/image/blank.gif"
      };
    }
  },

  "menu-separator" : {
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

  "menu-separator-line" : {
    style : function( states ) {
      return {
        right  : 0,
        left   : 0,
        height : 0,
        border : "verticalDivider"
      };
    }
  },

  "menubar-button" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        cursor : "default",
        overflow : "hidden",
        spacing : 4,
        width : "auto",
        padding : [ 3, 4 ],
        verticalChildrenAlign : "middle",
        backgroundImage : states.checked && !states.over
                          ? "static/image/dotted_white.gif"
                          : null
      };
      if( states.disabled ) {
        result.backgroundColor = tv.getCssColor( "ToolBar", "background-color" );
        result.textColor = "widget.graytext";
      } else {
        result.backgroundColor = tv.getCssColor( "MenuItem", "background-color" );
        result.textColor = tv.getCssColor( "MenuItem", "color" );
      }
      return result;
    }
  },

  /*
  ---------------------------------------------------------------------------
    LIST
  ---------------------------------------------------------------------------
  */

  "list" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.cursor = "default";
      result.overflow = "hidden";
      result.font = tv.getCssFont( "List", "font" );
      result.textColor = tv.getCssColor( "List", "color" );
      result.backgroundColor = tv.getCssColor( "List", "background-color" );
      result.border = tv.getCssBorder( "List", "border" );
      return result;
    }
  },

  "list-item" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        height                  : "auto",
        horizontalChildrenAlign : "left",
        verticalChildrenAlign   : "middle",
        spacing                 : 4,
        padding                 : [ 3, 5 ],
        minWidth                : "auto"
      };
      result.textColor = states.disabled ? "widget.graytext" : "undefined";
      if( states.selected ) {
        result.textColor = tv.getCssColor( "List-Item", "color" );
      }
      result.backgroundColor = tv.getCssColor( "List-Item", "background-color" );
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
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.font = tv.getCssFont( "Text", "font" );
      result.textColor = tv.getCssColor( "Text", "color" );
      result.backgroundColor = tv.getCssColor( "Text", "background-color" );
      result.border = tv.getCssBorder( "Text", "border" );
      // [if] Do not apply top/bottom paddings on the client
      var cssPadding = tv.getCssBoxDimensions( "Text", "padding" );
      result.paddingRight = cssPadding[ 1 ];
      result.paddingLeft = cssPadding[ 3 ];
      return result;
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

  "combo" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "Combo", "border" );
      result.backgroundColor = tv.getCssColor( "Combo",
                                               "background-color" );
      result.textColor = tv.getCssColor( "Combo", "color" );
      result.font = tv.getCssFont( "Combo", "font" );
      return result;
    }
  },

  "combo-list" : {
    include : "list",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
   	  var result = {};
      result.border = tv.getCssBorder( "Combo-List", "border" );
      result.height = "auto";
      result.overflow = "scrollY";
      result.textColor = tv.getCssColor( "Combo", "color" );
      result.font = tv.getCssFont( "*", "font" );
      result.backgroundColor = tv.getCssColor( "Combo", 
                                               "background-color" );
      return result;
    }
  },

  "combo-field" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.font = tv.getCssFont( "*", "font" );
      // [if] Do not apply top/bottom paddings on the client
      var cssPadding = tv.getCssBoxDimensions( "Text", "padding" );
      result.paddingRight = cssPadding[ 1 ];
      result.paddingLeft = cssPadding[ 3 ];
      result.width = null;
      result.height = null;
      result.left = 0;
      result.right = tv.getCssDimension( "Combo-Button", "width" );
      result.top = 0;
      result.bottom = 0;
      result.textColor = states.disabled
                         ? "widget.graytext"
                         : tv.getCssColor( "Combo", "color" );
      result.backgroundColor = tv.getCssColor( "Combo", 
                                               "background-color" );
      return result;
    }
  },

  "combo-button" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "Combo-Button", "border" );
      result.width = tv.getCssDimension( "Combo-Button", "width" );
      result.height = null;
      result.top = 0;
      result.bottom = 0;
      result.right = 0;
      result.icon = tv.getCssImage( "Combo-Button", "background-image" );
        // TODO [rst] rather use button.bgcolor?
      result.backgroundColor = tv.getCssColor( "Combo-Button", 
                                               "background-color" );
      return result;
    }
  },
  
  /*
  ---------------------------------------------------------------------------
    CCOMBO
  ---------------------------------------------------------------------------
  */

  "ccombo" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "CCombo", "border" );
      result.backgroundColor = tv.getCssColor( "CCombo",
                                               "background-color" );
      result.textColor = tv.getCssColor( "CCombo", "color" );
      result.font = tv.getCssFont( "CCombo", "font" );
      return result;
    }
  },

  "ccombo-list" : {
    include : "list",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "CCombo-List", "border" );
      result.height = "auto";
      result.overflow = "scrollY";
      result.textColor = tv.getCssColor( "CCombo", "color" );
      result.font = tv.getCssFont( "*", "font" );
      result.backgroundColor = tv.getCssColor( "CCombo", 
                                               "background-color" );
      return result;
    }
  },

  "ccombo-field" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.font = tv.getCssFont( "*", "font" );
      // [if] Do not apply top/bottom paddings on the client
      var cssPadding = tv.getCssBoxDimensions( "Text", "padding" );
      result.paddingRight = cssPadding[ 1 ];
      result.paddingLeft = cssPadding[ 3 ];
      result.width = null;
      result.height = null;
      result.left = 0;
      result.right = tv.getCssDimension( "CCombo-Button", "width" );
      result.top = 0;
      result.bottom = 0;
      result.textColor = states.disabled
                         ? "widget.graytext"
                         : tv.getCssColor( "CCombo", "color" );
      result.backgroundColor = tv.getCssColor( "CCombo", 
                                               "background-color" );
      return result;
    }
  },

  "ccombo-button" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "CCombo-Button", "border" );
      result.width = tv.getCssDimension( "CCombo-Button", "width" );
      result.height = null;
      result.top = 0;
      result.bottom = 0;
      result.right = 0;
      result.icon = tv.getCssImage( "CCombo-Button", "background-image" );
        // TODO [rst] rather use button.bgcolor?
      result.backgroundColor = tv.getCssColor( "CCombo-Button", 
                                               "background-color" );
      return result;
    }
  },

  /*
  ---------------------------------------------------------------------------
    TREE
  ---------------------------------------------------------------------------
  */

  "tree-element" : {
    style : function( states ) {
      return {
        height                : 16,
        verticalChildrenAlign : "middle"
      };
    }
  },

  "tree-element-icon" : {
    style : function( states ) {
      return {
        width  : 16,
        height : 16,
        marginRight : 2
      };
    }
  },

  "tree-element-label" : {
    include : "label",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.cursor = "default";
      result.height = 16;
      result.padding = 2;
      if( states.selected ) {
        result.textColor = tv.getCssColor( "TreeItem", "color" );
        result.backgroundColor = tv.getCssColor( "TreeItem", "background-color" );
      } else if( states.disabled ) {
        result.textColor = "widget.graytext";
        result.backgroundColor = "undefined";
      } else {
        result.textColor = "undefined";
        result.backgroundColor = "undefined";
      }
      return result;
    }
  },

  "tree-folder" : {
    include : "tree-element"
  },

  "tree-folder-icon" : {
    include : "tree-element-icon"
  },

  "tree-folder-label" : {
    include : "tree-element-label"
  },

  "tree-container" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.font = tv.getCssFont( "*", "font" );
      result.border = tv.getCssBorder( "*", "border" );
      return result;
    }
  },

  "tree" : {
    include : "tree-folder",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.verticalChildrenAlign = "top";
      result.backgroundColor = tv.getCssColor( "Tree", "background-color" );
      result.textColor = states.disabled ? "widget.graytext" : tv.getCssColor( "Tree", "color" );
      return result;
    }
  },

  "tree-icon" : {
    include : "tree-folder-icon"
  },

  "tree-label" : {
    include : "tree-folder-label"
  },

  "tree-check-box" : {
    include : "image",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.width = 13;
      result.height = 13;
      result.clipWidth = 13;
      result.clipHeight = 13;
      result.source = tv.getCssImage( "Table-Checkbox", "background-image" );
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
      result.textColor = states.disabled ? "widget.graytext" : "undefined";
      result.backgroundColor = tv.getCssColor( "TreeColumn", "background-color" );
      if( states.mouseover && !states.disabled ) {
        result.border = "tree.column.hover.border";
      } else {
        result.border = "tree.column.border";
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
    style : function( states ) {
      return {
        // TODO [rh] use same bg-color as splitpane-spltter (see there)
        backgroundColor : "#d6d5d9",
        width : 3
      }
    }
  },

  "tree-column-sort-indicator" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.source = tv.getCssImage( "TreeColumn-SortIndicator",
                                      "background-image" );
      return result;
    }
  },

  /*
  ---------------------------------------------------------------------------
    TAB FOLDER
  ---------------------------------------------------------------------------
  */

  "tab-view" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.textColor = tv.getCssColor( "*", "color" );
      result.font = tv.getCssFont( "*", "font" );
      result.spacing = -1;
      result.border = tv.getCssBorder( "*", "border" );
      return result;
    }
  },

  "tab-view-bar" : {
    style : function( states ) {
      return {
        height : "auto"
      };
    }
  },

  "tab-view-pane" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
//    result.height = "1*";
      result.overflow = "hidden";
      result.backgroundColor = tv.getCssColor( "*", "background-color" );
      result.border = new qx.ui.core.Border( 1, "solid", "widget.thinborder" );
      result.padding = 10;
      return result;
    }
  },

  "tab-view-page" : {
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

  "tab-view-button" : {
    include : "atom",

    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );

      var border_top_normal = new qx.ui.core.Border( 1, "solid", "widget.thinborder" );
      border_top_normal.setWidthBottom( 0 );

      var border_top_checked = new qx.ui.core.Border( 1, "solid", "widget.thinborder" );
      border_top_checked.setWidthBottom( 0 );
      var top_color = tv.getCssColor( "TabItem", "border-top-color" );
      border_top_checked.setTop( 3, "solid", top_color );

      var border_bottom_normal = new qx.ui.core.Border( 1, "solid", "widget.thinborder" );
      border_bottom_normal.setWidthTop( 0 );

      var border_bottom_checked = new qx.ui.core.Border( 1, "solid", "widget.thinborder" );
      border_bottom_checked.setWidthTop( 0 );
      var bottom_color = tv.getCssColor( "TabItem", "border-bottom-color" );
      border_bottom_checked.setBottom( 3, "solid", bottom_color );

      if( states.checked ) {
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
      result.backgroundColor = tv.getCssColor( "TabItem", "background-color" );
      result.textColor = states.disabled
                         ? "widget.graytext"
                         : "undefined";
      return result;
    }
  },

  /*
  ---------------------------------------------------------------------------
    GROUP BOX
  ---------------------------------------------------------------------------
  */

  "group-box" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        backgroundColor : tv.getCssColor( "Group", "background-color" ),
        border : tv.getCssBorder( "Group", "border" ),
        font : tv.getCssFont( "Group", "font"),
        textColor : tv.getCssColor( "Group", "color" )
      };
    }
  },

  "group-box-legend" : {
    include : "atom",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        top : 0,
        left : 0,
        padding : tv.getCssBoxDimensions( "Group-Label", "padding" ),
        margin : tv.getCssBoxDimensions( "Group-Label", "margin" ),
        backgroundColor : tv.getCssColor( "Group-Label", "background-color" ),
        font : tv.getCssFont( "Group", "font"),
        textColor : states.disabled
                    ? tv.getCssColor( "Group", "color" )
                    : tv.getCssColor( "Group-Label", "color" )
      };
    }
  },

  "group-box-frame" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var margin = tv.getCssBoxDimensions( "Group-Frame", "margin" );
      return {
        top     : margin[ 0 ],
        right   : margin[ 1 ],
        bottom  : margin[ 2 ],
        left    : margin[ 3 ],
        border  : tv.getCssBorder( "Group-Frame", "border" )
      };
    }
  },

  /*
  ---------------------------------------------------------------------------
    SPINNER
  ---------------------------------------------------------------------------
  */

  "spinner" : {
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );

      result.font = tv.getCssFont( "Spinner", "font" );
      result.textColor = tv.getCssColor( "Spinner", "color" );
      result.backgroundColor = tv.getCssColor( "Spinner", "background-color" );
      result.border = tv.getCssBorder( "Spinner", "border" );

      return result;
    }
  },

  "spinner-text-field" : {
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );

      result.padding = tv.getCssBoxDimensions( "Spinner", "padding" );
      result.textColor = states.disabled ? "widget.graytext" : "undefined";

      result.top = 0;
      result.left = 0;
      result.right = 0;
      result.bottom = 0;

      return result;
    }
  },

  "spinner-button-up" : {
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.width = tv.getCssDimension( "Spinner-Buttons", "width" );
      result.icon = tv.getCssImage( "Spinner-UpButton", "background-image" );
      result.border = tv.getCssBorder( "Spinner-Buttons", "border" );
      result.backgroundColor = tv.getCssColor( "Spinner-Buttons", 
                                               "background-color" );
      return result;
    }
  },

  "spinner-button-down" : {
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.width = tv.getCssDimension( "Spinner-Buttons", "width" );
      result.icon = tv.getCssImage( "Spinner-DownButton", "background-image" );
      result.border = tv.getCssBorder( "Spinner-Buttons", "border" );
      result.backgroundColor = tv.getCssColor( "Spinner-Buttons", 
                                               "background-color" );
      return result;
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
        textColor : tv.getCssColor( "Table", "color" ),
        font : tv.getCssFont( "*", "font" ),
        border : tv.getCssBorder( "*", "border" )
      };
    }
  },

  "table-client-area" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        backgroundColor : tv.getCssColor( "Table", "background-color" )
      };
      return result;
    }
  },

  "table-column-area" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        backgroundColor : tv.getCssColor( "TableColumn", "background-color" )
      };
      return result;
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
        textColor : states.disabled
                    ? "widget.graytext"
                    : tv.getCssColor( "*", "color" ),
        opacity : states.moving ? 0.6 : 1.0
      };
      result.backgroundColor = tv.getCssColor( "TableColumn", "background-color" );
      // TODO [rst] borders hard coded in BordersBase.js
      if( states.mouseover && !states.disabled ) {
        result.border = "table.column.hover.border";
      } else {
        result.border = "table.column.border";
      }
      return result;
    }
  },

  "table-column-resizer" : {
    style : function( states ) {
      return {
        width : 3,
        opacity : 0.3,
        backgroundColor : "black"
      }
    }
  },

  "table-column-sort-indicator" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.source = tv.getCssImage( "TableColumn-SortIndicator",
                                      "background-image" );
      return result;
    }
  },

  "table-row" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        cursor : "default"
      };
      if( states.lines ) {
        // TODO [rst] Optimize: this function might be called a few times,
        //            the border can be cached somewhere
        var border = new qx.ui.core.Border( 0 );
        border.setColor( tv.getCssColor( "Table-GridLine", "color" ) );
        border.setWidthBottom( 1 );
        result.border = border;
      } else {
        result.border = "undefined";
      }
      result.textColor = states.disabled
                         ? "widget.graytext"
                         : tv.getCssColor( "TableItem", "color" );
      if( result.textColor == "undefined" ) {
        result.textColor = "inherit";
      }
      result.backgroundColor = tv.getCssColor( "TableItem", "background-color" );
      return result;
    }
  },

  "table-check-box" : {
    include: "image",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.width = 13;
      result.height = 13;
      result.clipWidth = 13;
      result.clipHeight = 13;
      result.source = tv.getCssImage( "Table-Checkbox", "background-image" );
      return result;
    }
  },

  "table-gridline-vertical" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      var border = new qx.ui.core.Border( 0 );
      border.setColor( tv.getCssColor( "Table-GridLine", "color" ) );
      border.setWidthLeft( 1 );
      result.border = border;
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

  "sash-handle" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.backgroundImage = tv.getCssImage( "Sash-Handle", "background-image" );
      result.backgroundRepeat = "no-repeat";
      return result;
    }
  },

  // ------------------------------------------------------------------------
  // CTabFolder

  "ctabfolder" : {
    style: function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.font = tv.getCssFont( "*", "font" );
      result.textColor = tv.getCssColor( "CTabItem", "color" );
      result.backgroundColor = tv.getCssColor( "CTabItem", "background-color" );
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
        var color = tv.getCssColor( "CTabFolder", "border-color" );
        result.border = new qx.ui.core.Border( 1, "solid", color );
      } else {
        result.border = "undefined";
      }
      return result;
    }
  },

  "ctabfolder-frame" : {
    style: function( states ) {
      // TODO [if] This is quick fix to get the backgroud color of selected item
      states.selected = true;
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      if( !states.rwt_FLAT ) {
        var color = tv.getCssColor( "CTabItem", "background-color" );
        result.border = new qx.ui.core.Border( 2, "solid", color );
      } else {
        result.border = "undefined";
      }
      result.backgroundColor = tv.getCssColor( "*", "background-color" );
      return result;
    }
  },

  "ctabfolder-separator" : {
    style: function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var color = tv.getCssColor( "CTabFolder", "border-color" );
      var border = new qx.ui.core.Border();
      if( states.barTop ) {
        border.setBottom( 1, "solid", color );
      } else {
        border.setTop( 1, "solid", color );
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
      var color = tv.getCssColor( "CTabFolder", "border-color" );
      result.paddingLeft = 4;
      result.border = new qx.ui.core.Border();
      result.border.setRight( 1, "solid", color );
      if( states.selected ) {
        result.textColor = tv.getCssColor( "CTabItem", "color" );
        result.backgroundColor = tv.getCssColor( "CTabItem", "background-color" );
        if( states.barTop ) {
          result.border.setTop( 1, "solid", color );
        } else {
          result.border.setBottom( 1, "solid", color );
        }
      } else {
        result.textColor = "undefined";
        result.backgroundColor = "undefined";
      }
      if( states.firstItem && states.rwt_BORDER ) {
        result.border.setLeft( 1, "solid", color );
      }
      return result;
    }
  },

  "ctabfolder-button" : {
    include : "image",
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      if( states.over ) {
        result.backgroundColor = "white";
        result.border = "ctabfolder.button.border";
      } else {
        result.backgroundColor = "undefined";
        result.border = "undefined";
      }
      return result;
    }
  },

  "ctab-close-button" : {
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
      result.backgroundColor = tv.getCssColor( "*", "background-color" );
      result.border = tv.getCssBorder( "*", "border" );
      return result;
    }
  },

  // ------------------------------------------------------------------------
  // ScrolledComposite

  "scrolledcomposite" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : states.rwt_BORDER ? "shadow" : tv.getCssBorder( "*", "border" )
      }
    }
  },

  // ------------------------------------------------------------------------
  // CoolBar

  "coolbar" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "*", "border" );
      return result;
    }
  },

  "coolitem" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "*", "border" );
      return result;
    }
  },

  "coolitem-handle" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      if( states.vertical ) {
        result.height = tv.getCssDimension( "CoolItem-Handle", "width" );
      } else {
        result.width = tv.getCssDimension( "CoolItem-Handle", "width" );
      }
      result.border = tv.getCssBorder( "CoolItem-Handle", "border" );
      result.margin = [ 1, 2, 1, 0 ];
      result.cursor = "w-resize";
      return result;
    }
  },

  // ------------------------------------------------------------------------
  // Browser

  "browser" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : tv.getCssBorder( "*", "border" ),
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
        cursor: "default",
        padding : 2,
        font : tv.getCssFont( "Link", "font" ),
        border : tv.getCssBorder( "Link", "border" ),
        textColor : tv.getCssColor( "*", "color" )
      }
    }
  },

  "link-text" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        textColor: states.disabled
                   ? "widget.graytext"
                   : "inherit"
      }
    }
  },

  // ------------------------------------------------------------------------
  // Progress Bar

  "progressbar" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "ProgressBar", "border" );
      result.backgroundColor = tv.getCssColor( "ProgressBar",
                                               "background-color" );
      result.backgroundImage = tv.getCssImage( "ProgressBar",
                                               "background-image" );
      return result;
    }
  },

  "progressbar-indicator" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.backgroundColor = tv.getCssColor( "ProgressBar-Indicator",
                                               "background-color" );
      result.backgroundImage = tv.getCssImage( "ProgressBar-Indicator",
                                               "background-image" );
      return result;
    }
  },

  "scrollbar-blocker" : {
    style : function( states ) {
      return {
        backgroundColor : "black",
        opacity : 0.2
      };
    }
  },

  // ------------------------------------------------------------------------
  // Scale

  "scale" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : tv.getCssBorder( "Scale", "border" ),
        font : tv.getCssFont( "*", "font" ),
        textColor : tv.getCssColor( "*", "color" ),
        backgroundColor : tv.getCssColor( "Scale", "background-color" )
      }
    }
  },

  "scale-line" : {
    include : "image",

    style : function( states ) {
      var result = {};
      if( states.horizontal ) {
        result.left = org.eclipse.swt.widgets.Scale.PADDING;
        result.top = org.eclipse.swt.widgets.Scale.SCALE_LINE_OFFSET;
        result.source = "widget/scale/h_line.gif";
      } else {
        result.left = org.eclipse.swt.widgets.Scale.SCALE_LINE_OFFSET;
        result.top = org.eclipse.swt.widgets.Scale.PADDING;
        result.source = "widget/scale/v_line.gif";
      }
      return result;
    }
  },

  "scale-thumb" : {
    include : "image",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      if( states.horizontal ) {
        result.left = org.eclipse.swt.widgets.Scale.PADDING;
        result.top = org.eclipse.swt.widgets.Scale.THUMB_OFFSET;
        result.source = "widget/scale/h_thumb.gif";
      } else {
        result.left = org.eclipse.swt.widgets.Scale.THUMB_OFFSET;
        result.top = org.eclipse.swt.widgets.Scale.PADDING;
        result.source = "widget/scale/v_thumb.gif";
      }
      result.backgroundColor = tv.getCssColor( "Scale-Thumb", "background-color" );
      return result;
    }
  },

  "scale-min-marker" : {
    include : "image",

    style : function( states ) {
      var result = {};
      if( states.horizontal ) {
        result.left =   org.eclipse.swt.widgets.Scale.PADDING
                      + org.eclipse.swt.widgets.Scale.HALF_THUMB;
        result.source = "widget/scale/h_marker_big.gif";
      } else {
        result.top =   org.eclipse.swt.widgets.Scale.PADDING
                     + org.eclipse.swt.widgets.Scale.HALF_THUMB;
        result.source = "widget/scale/v_marker_big.gif";
      }
      return result;
    }
  },

  "scale-max-marker" : {
    include : "image",

    style : function( states ) {
      var result = {};
      if( states.horizontal ) {
        result.source = "widget/scale/h_marker_big.gif";
      } else {
        result.source = "widget/scale/v_marker_big.gif";
      }
      return result;
    }
  },

  "scale-middle-marker" : {
    include : "image",

    style : function( states ) {
      var result = {};
      if( states.horizontal ) {
        result.source = "widget/scale/h_marker_small.gif";
      } else {
        result.source = "widget/scale/v_marker_small.gif";
      }
      return result;
    }
  },

  // ------------------------------------------------------------------------
  // DateTime

  "datetime-date" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "DateTime", "border" );
      result.font = tv.getCssFont( "DateTime", "font" );
      result.textColor = tv.getCssColor( "DateTime", "color" );
      result.backgroundColor = tv.getCssColor( "DateTime", "background-color" );
      result.padding = 1;
      return result;
    }
  },

  "datetime-time" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "DateTime", "border" );
      result.font = tv.getCssFont( "DateTime", "font" );
      result.textColor = tv.getCssColor( "DateTime", "color" );
      result.backgroundColor = tv.getCssColor( "DateTime", "background-color" );
      result.padding = 1;
      return result;
    }
  },

  "datetime-calendar" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "DateTime", "border" );
      result.font = tv.getCssFont( "*", "font" );
      result.textColor = tv.getCssColor( "DateTime", "color" );
      result.backgroundColor = tv.getCssColor( "DateTime", "background-color" );
      return result;
    }
  },

  "datetime-field" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        cursor    : "default",
        textAlign : "center",
        padding   : [ 2, 3 ]
      };
      if( states.disabled ) {
        result.textColor = tv.getCssColor( "DateTime", "color" );
        result.backgroundColor = tv.getCssColor( "DateTime", "background-color" );
      } else if( states.selected ) {
        result.textColor = tv.getCssColor( "DateTime-Field", "color" );
        result.backgroundColor = tv.getCssColor( "DateTime-Field", "background-color" );
      } else {
        result.textColor = "undefined";
        result.backgroundColor = "undefined";
      }
      return result;
    }
  },

  "datetime-separator" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        cursor     : "default",
        paddingTop : 2
      };
      if( states.disabled ) {
        result.textColor = tv.getCssColor( "DateTime", "color" );
        result.backgroundColor = tv.getCssColor( "DateTime", "background-color" );
      } else {
        result.textColor = "undefined";
        result.backgroundColor = "undefined";
      }      
      return result;
    }
  },
  
  "datetime-button-up" : {
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.width = tv.getCssDimension( "DateTime-Buttons", "width" );
      result.icon = tv.getCssImage( "DateTime-UpButton", "background-image" );
      result.border = tv.getCssBorder( "DateTime-Buttons", "border" );
      result.backgroundColor = tv.getCssColor( "DateTime-Buttons", 
                                               "background-color" );
      return result;
    }
  },

  "datetime-button-down" : {
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.width = tv.getCssDimension( "DateTime-Buttons", "width" );
      result.icon = tv.getCssImage( "DateTime-DownButton", "background-image" );
      result.border = tv.getCssBorder( "DateTime-Buttons", "border" );
      result.backgroundColor = tv.getCssColor( "DateTime-Buttons", 
                                               "background-color" );
      return result;
    }
  },

  // ------------------------------------------------------------------------
  // Calendar

  "calendar-navBar" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        backgroundColor : tv.getCssColor( "DateTime-Calendar-Navbar", "background-color" ),
        padding : [ 4, 4, 4, 4 ]
      };
    }
  },

  "calendar-toolbar-button" : {
    style : function( states ) {
      var result = {
        spacing : 4,
        width : 16,
        height : 16,
        clipWidth : 16,
        clipHeight : 16,
        verticalChildrenAlign : "middle"
      };
      if (states.pressed || states.checked || states.abandoned) {
        result.padding = [ 2, 0, 0, 2 ];
      } else {
        result.padding = 2;
      }
      return result;
    }
  },

  "calendar-toolbar-previous-year-button" : {
    include: "calendar-toolbar-button",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        icon : tv.getCssImage( "DateTime-Calendar-PreviousYearButton",
                               "background-image" )
      };
    }
  },

  "calendar-toolbar-previous-month-button" : {
    include: "calendar-toolbar-button",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        icon : tv.getCssImage( "DateTime-Calendar-PreviousMonthButton",
                               "background-image" )
      };
    }
  },

  "calendar-toolbar-next-month-button" : {
    include: "calendar-toolbar-button",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        icon : tv.getCssImage( "DateTime-Calendar-NextMonthButton",
                               "background-image" )
      };
    }
  },

  "calendar-toolbar-next-year-button" : {
    include: "calendar-toolbar-button",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        icon : tv.getCssImage( "DateTime-Calendar-NextYearButton",
                               "background-image" )
      };
    }
  },

  "calendar-monthyear" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var boldFont
        = qx.ui.core.Font.fromString( "11 bold Tahoma, 'Lucida Sans Unicode', sans-serif" );
      return {
        font          : boldFont,
        textAlign     : "center",
        textColor     : states.disabled
                        ? tv.getCssColor( "DateTime", "color" )
                        : tv.getCssColor( "DateTime-Calendar-Navbar", "color" ),
        verticalAlign : "middle",
        cursor        : "default"
      };
    }
  },

  "calendar-datepane" : {
    style : function( states ) {
      return {
        backgroundColor : "undefined"
      };
    }
  },

  "calendar-week" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      if( states.header ) {
        var border = qx.ui.core.Border.fromConfig({
          right   : [ 1, "solid", "gray" ],
          bottom  : [ 1, "solid", "gray" ]
        });
      } else {
        var border = qx.ui.core.Border.fromConfig({
          right   : [ 1, "solid", "gray" ]
        });
      }
      return {
        textAlign       : "center",
        verticalAlign   : "middle",
        textColor       : states.disabled
                          ? tv.getCssColor( "DateTime", "color" )
                          : "undefined",
        border          : border
      };
    }
  },

  "calendar-weekday" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var border = qx.ui.core.Border.fromConfig({
        bottom : [ 1, "solid", "gray" ]
      });
      return {
        border          : border,
        textAlign       : "center",
        textColor       : states.disabled
                          ? tv.getCssColor( "DateTime", "color" )
                          : "undefined"
      };
    }
  },

  "calendar-day" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        textAlign       : "center",
        verticalAlign   : "middle"
      };
      if( states.selected || states.otherMonth ) {
        result.textColor = tv.getCssColor( "DateTime-Calendar-Day", "color" );
        result.backgroundColor = tv.getCssColor( "DateTime-Calendar-Day",
                                                 "background-color" );
      } else if( states.disabled ) {
        result.textColor = tv.getCssColor( "DateTime", "color" );
        result.backgroundColor = tv.getCssColor( "DateTime", "background-color" );
      } else {
        result.textColor = "undefined";
        result.backgroundColor = "undefined";
      }
      var borderColor = states.disabled ? "widget.graytext" : "red";
      var border = new qx.ui.core.Border( 1, "solid", borderColor );
      result.border = states.today ? border : "undefined";
      return result;
    }
  },

  // ------------------------------------------------------------------------
  // ExpandBar

  "expand-bar" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "*", "border" );
      result.font = tv.getCssFont( "ExpandBar", "font" );
      result.textColor = tv.getCssColor( "ExpandBar", "color" );
      return result;
    }
  },

  "expand-item" : {
    style : function( states ) {
      return {
        overflow : "hidden"
      }
    }
  },

  "expand-item-chevron-button" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.width = 16;
      result.height = 16;
      result.clipWidth = 16;
      result.clipHeight = 16;
      result.right = 4;
      result.source = tv.getCssImage( "ExpandItem-Button", "background-image" );
      result.cursor = states.disabled ? "default" : "pointer";
      return result;
    }
  },

  "expand-item-header" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.top = 0;
      result.left = 0;
      result.width = "100%";
      result.horizontalChildrenAlign =  "left";
      result.verticalChildrenAlign = "middle";
      result.paddingLeft = 4;
      result.paddingRight = 24;
      result.backgroundColor
        = tv.getCssColor( "ExpandItem-Header", "background-color" );
      result.textColor = states.disabled ? "widget.graytext" : "undefined";
      result.cursor = states.disabled ? "default" : "pointer";
      return result;
    }
  },

  // ------------------------------------------------------------------------
  // Slider

  "slider" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : tv.getCssBorder( "*", "border" ),
        font : tv.getCssFont( "*", "font" ),
        textColor : tv.getCssColor( "*", "color" ),
        backgroundColor : tv.getCssColor( "Slider", "background-color" )
      }
    }
  },

  "slider-line" : {
    include : "atom",
    style : function( states ) {
      var result = {};
      result.backgroundColor = "#eeeeee";
      result.opacity = 0;
      // Assigning icon for proper visualization in IE
      result.icon = "static/image/blank.gif";
      if( states.horizontal ){
        result.left = org.eclipse.swt.widgets.Slider.BUTTON_WIDTH;
      } else {
        result.top = org.eclipse.swt.widgets.Slider.BUTTON_WIDTH;
      }
      return result;
    }
  },

  "slider-thumb" : {
    include : "atom",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.backgroundColor = tv.getCssColor( "Slider-Thumb", "background-color" );
      result.border = tv.getCssBorder( "Slider-Thumb", "border" );
      // Assigning icon for proper visualization in IE
      result.icon = "static/image/blank.gif";
      return result;
    }
  },

  "slider-min-button" : {
    include : "button",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.backgroundColor = tv.getCssColor( "Slider-DownButton", "background-color" );
      result.icon = tv.getCssImage( "Slider-DownButton", "background-image" );
      result.border = tv.getCssBorder( "Slider-DownButton", "border" );
      if( states.horizontal ){
        result.width = org.eclipse.swt.widgets.Slider.BUTTON_WIDTH;
      } else {
        result.height = org.eclipse.swt.widgets.Slider.BUTTON_WIDTH;
      }
      return result;
    }
  },

  "slider-max-button" : {
    include : "button",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.backgroundColor = tv.getCssColor( "Slider-UpButton", "background-color" );
      result.icon = tv.getCssImage( "Slider-UpButton", "background-image" );
      result.border = tv.getCssBorder( "Slider-UpButton", "border" );
      if( states.horizontal ) {
        result.width = org.eclipse.swt.widgets.Slider.BUTTON_WIDTH;
      } else {
        result.height = org.eclipse.swt.widgets.Slider.BUTTON_WIDTH;
      }
      return result;
    }
  }
}
} );
