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
        backgroundColor : "white"
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
      result.backgroundImage = tv.getCssImage( "Label", "background-image" );
      result.backgroundGradient = tv.getCssGradient( "Label", "background-image" );
      result.border = tv.getCssBorder( "Label", "border" );
      result.cursor = tv.getCssCursor( "Label", "cursor" );
      return result;
    }
  },

  "clabel" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.textColor = tv.getCssColor( "CLabel", "color" );
      result.backgroundColor = tv.getCssColor( "CLabel", "background-color" );
      result.font = tv.getCssFont( "CLabel", "font" );
      if( states.rwt_SHADOW_IN ) {
        result.border = "thinInset";
      } else if( states.rwt_SHADOW_OUT ) {
        result.border = "thinOutset";
      } else {
        result.border = tv.getCssBorder( "CLabel", "border" );
      }
      result.backgroundImage = tv.getCssImage( "CLabel", "background-image" );
      result.backgroundGradient = tv.getCssGradient( "CLabel", 
                                                     "background-image" );
      result.cursor = tv.getCssCursor( "CLabel", "cursor" );
      result.padding = tv.getCssBoxDimensions( "CLabel", "padding" );
      result.spacing = tv.getCssDimension( "CLabel", "spacing" );
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
      result.border = tv.getCssBorder( "ToolTip", "border" );
      result.padding = tv.getCssBoxDimensions( "ToolTip", "padding" );
      result.textColor = tv.getCssColor( "ToolTip", "color" );
      result.font = tv.getCssFont( "ToolTip", "font" );
      result.backgroundColor = tv.getCssColor( "ToolTip", "background-color" );
      result.backgroundImage = tv.getCssImage( "ToolTip", "background-image" );
      result.backgroundGradient = tv.getCssGradient( "ToolTip", "background-image" );
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
      result.backgroundGradient = tv.getCssGradient( "Button", "background-image" );
      result.border = tv.getCssBorder( "Button", "border" );
      result.spacing = tv.getCssDimension( "Button", "spacing" );
      result.padding = tv.getCssBoxDimensions( "Button", "padding" );
      result.cursor = tv.getCssCursor( "Button", "cursor" );
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
        backgroundGradient : tv.getCssGradient( "Button", "background-image" ),
        spacing : tv.getCssDimension( "Button", "spacing" ),
        padding : tv.getCssBoxDimensions( "Button", "padding" ),
        selectionIndicator : tv.getCssSizedImage( "Button-CheckIcon", 
                                                  "background-image" ),
        cursor : tv.getCssCursor( "Button", "cursor" )
      }
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
        backgroundImage : tv.getCssImage( "Button", "background-image" ),
        backgroundGradient : tv.getCssGradient( "Button", "background-image" ),
        spacing : tv.getCssDimension( "Button", "spacing" ),
        padding : tv.getCssBoxDimensions( "Button", "padding" ),
        selectionIndicator : tv.getCssSizedImage( "Button-RadioIcon", 
                                                  "background-image" ),
        cursor : tv.getCssCursor( "Button", "cursor" )
      }
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
        border : tv.getCssBorder( "ToolBar", "border" ),
        textColor : tv.getCssColor( "ToolBar", "color" ),
        backgroundColor : tv.getCssColor( "ToolBar", "background-color" ),
        backgroundGradient : tv.getCssGradient( "ToolBar", "background-image" ),
        backgroundImage : tv.getCssImage( "ToolBar", "background-image" )
      };
    }
  },

  "toolbar-separator" : {
    style : function( states ) {
      return {};
    }
  },

  "toolbar-separator-line" : {
    style : function( states ) {
      var result = null;
      if( states.vertical ) {
        result = {
          left   : 2,
          height : 2,
          right  : 2,
          border : "verticalDivider"
        };        
      } else {
        result = {
          top    : 2,
          width  : 2,
          bottom : 2,
          border : "horizontalDivider"
        };                
      }
      return result;
    }
  },

  "toolbar-button" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        cursor : "default",
        overflow : "hidden",
        width : "auto",
        verticalChildrenAlign : "middle"
      };
      result.spacing = tv.getCssDimension( "ToolItem", "spacing" );
      result.textColor = states.disabled
                         ? tv.getCssColor( "*", "color" )
                         : tv.getCssColor( "ToolItem", "color" );
      result.backgroundColor = tv.getCssColor( "ToolItem", "background-color" );
      result.backgroundImage = states.selected && !states.over  // TODO [tb] : no longer needed?
                               ? "static/image/dotted_white.gif"
                               : tv.getCssImage( "ToolItem", "background-image" );
      result.backgroundGradient = tv.getCssGradient( "ToolItem", "background-image" );
      result.border = tv.getCssBorder( "ToolItem", "border" );
      result.padding = tv.getCssBoxDimensions( "ToolItem", "padding" );
      if( states.dropDown ) {
        result.dropDownArrow = tv.getCssSizedImage( "ToolItem-DropDownIcon",
                                                    "background-image" );
        result.separatorBorder = tv.getCssBorder( "ToolItem-DropDownIcon", 
                                                  "border" );
      } else {
        result.dropDownArrow = null;
        result.separatorBorder = null; 
      }
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
      result.backgroundImage
        = tv.getCssImage( "Shell", "background-image" );
      result.backgroundGradient
        = tv.getCssGradient( "Shell", "background-image" );
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
      result.backgroundGradient
        = tv.getCssGradient( "Shell-Titlebar", "background-image" );
      result.border = tv.getCssBorder( "Shell-Titlebar", "border" );
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

  "menu-item" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        spacing : 2,
        padding : [ 2, 4 ],        
        backgroundColor : tv.getCssColor( "MenuItem", "background-color" ),
        height : states.bar ? "100%" : "auto"         
      };
      if( states.disabled ) {
        result.textColor = tv.getCssColor( "*", "color" );
      } else {
        result.textColor = tv.getCssColor( "MenuItem", "color" );
      }
      if( states.cascade ) {                
        result.arrow
          = tv.getCssSizedImage( "MenuItem-CascadeIcon", "background-image" );
      } else {
        result.arrow = null;        
      }
      if( states.selected ) {
        if( states.check ) {
           result.selectionIndicator
             = tv.getCssSizedImage( "MenuItem-CheckIcon", "background-image" );
        } else if( states.radio ) {
           result.selectionIndicator
             = tv.getCssSizedImage( "MenuItem-RadioIcon", "background-image" );
        }
      } else {
        if( states.radio ) {
          var radioWidth
            = tv.getCssSizedImage( "MenuItem-RadioIcon", "background-image" )[ 1 ];
          result.selectionIndicator = [ null, radioWidth, 0 ];
        } else if( states.check ) {
          var checkWidth 
            = tv.getCssSizedImage( "MenuItem-CheckIcon", "background-image" )[ 1 ];
          result.selectionIndicator = [ null, checkWidth, 0 ];          
        } else {
          result.selectionIndicator = null;
        }
      }
      return result;
    }
  },

  "menu-separator" : {
    style : function( states ) {
      return {
        height : "auto",
        marginTop : 3,
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
        height : "auto",
        horizontalChildrenAlign : "left",
        verticalChildrenAlign : "middle",
        spacing : 4,
        padding : [ 3, 5 ],
        minWidth : "auto"
      };
      result.textColor = states.disabled ? tv.getCssColor( "*", "color" ) : "undefined";
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
      result.backgroundColor
        = tv.getCssColor( "Combo", "background-color" );
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
      result.backgroundColor = tv.getCssColor( "Combo", "background-color" );
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
      result.textColor = tv.getCssColor( "Combo", "color" );
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
      result.icon = tv.getCssImage( "Combo-Button-Icon", "background-image" );
      if( result.icon === org.eclipse.swt.theme.ThemeValues.NONE_IMAGE ) {
        result.icon = tv.getCssImage( "Combo-Button", "background-image" );
      } else {
        result.backgroundImage = tv.getCssImage( "Combo-Button", 
                                                 "background-image" );
      }      
      result.backgroundGradient = tv.getCssGradient( "Combo-Button", 
                                                     "background-image" );      
      // TODO [rst] rather use button.bgcolor?
      result.backgroundColor = tv.getCssColor( "Combo-Button",
                                               "background-color" );
      result.cursor = tv.getCssCursor( "Combo-Button", "cursor" );
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
      result.textColor = tv.getCssColor( "CCombo", "color" );
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
      result.icon = tv.getCssImage( "CCombo-Button-Icon", "background-image" );
      if( result.icon === org.eclipse.swt.theme.ThemeValues.NONE_IMAGE ) {
        result.icon = tv.getCssImage( "CCombo-Button", "background-image" );
      } else {
        result.backgroundImage = tv.getCssImage( "CCombo-Button", 
                                                 "background-image" );
      }      
      result.backgroundGradient = tv.getCssGradient( "CCombo-Button", 
                                                     "background-image" );
      // TODO [rst] rather use button.bgcolor?
      result.backgroundColor = tv.getCssColor( "CCombo-Button",
                                               "background-color" );
      result.cursor = tv.getCssCursor( "CCombo-Button", "cursor" );
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
        height : 16,
        verticalChildrenAlign : "middle"
      };
    }
  },

  "tree-element-icon" : {
    style : function( states ) {
      return {
        width : 16,
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
        result.textColor = tv.getCssColor( "*", "color" );
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
      result.textColor = states.disabled ? tv.getCssColor( "*", "color" ) : tv.getCssColor( "Tree", "color" );
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
      result.source = tv.getCssImage( "Tree-Checkbox", "background-image" );
      result.marginRight = 3;
      return result;
    }
  },

  "tree-column" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.cursor = "default";
      result.spacing = 2;
      result.textColor = tv.getCssColor( "*", "color" );
      result.backgroundColor = tv.getCssColor( "TreeColumn", "background-color" );
      result.backgroundImage = tv.getCssImage( "TreeColumn",
                                               "background-image" );
      result.backgroundGradient = tv.getCssGradient( "TreeColumn",
                                                     "background-image" );
      result.opacity = states.moving ? 0.6 : 1.0;
      result.padding = tv.getCssBoxDimensions( "TreeColumn", "padding" );
      var border = new qx.ui.core.Border( 0 );
      if( !states.dummy ) {
        border.setColorRight( tv.getCssColor( "Table-GridLine", "color" ) );
        border.setWidthRight( 1 );
      }
      var borderBottom = tv.getCssBorder( "TreeColumn", "border-bottom" );
      border.setWidthBottom( borderBottom.getWidthBottom() );
      border.setStyleBottom( borderBottom.getStyleBottom() );
      border.setColorBottom( borderBottom.getColorBottom() );
      result.border = border;
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
      result.backgroundImage = tv.getCssImage( "TabItem", "background-image" );
      result.backgroundGradient = tv.getCssGradient( "TabItem",
                                                     "background-image" );
      result.textColor = states.disabled
                         ? tv.getCssColor( "*", "color" )
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
        border : tv.getCssBorder( "Group-Label", "border" ),
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

      // [if] Do not apply top/bottom paddings on the client
      var cssPadding = tv.getCssBoxDimensions( "Spinner", "padding" );
      result.paddingRight = cssPadding[ 1 ];
      result.paddingLeft = cssPadding[ 3 ];
      result.textColor = states.disabled
                         ? tv.getCssColor( "*", "color" )
                         : "undefined";

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
      result.width = tv.getCssDimension( "Spinner-UpButton", "width" );
      result.icon = tv.getCssImage( "Spinner-UpButton-Icon", "background-image" );
      if( result.icon === org.eclipse.swt.theme.ThemeValues.NONE_IMAGE ) {
        result.icon = tv.getCssImage( "Spinner-UpButton", "background-image" );
      } else {
        result.backgroundImage = tv.getCssImage( "Spinner-UpButton", 
                                                 "background-image" );
      }      
      result.backgroundGradient = tv.getCssGradient( "Spinner-UpButton", 
                                                     "background-image" );
      result.border = tv.getCssBorder( "Spinner-UpButton", "border" );
      result.backgroundColor = tv.getCssColor( "Spinner-UpButton",
                                               "background-color" );
      result.cursor = tv.getCssCursor( "Spinner-UpButton", "cursor" );
      return result;
    }
  },

  "spinner-button-down" : {
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.width = tv.getCssDimension( "Spinner-DownButton", "width" );
      result.icon = tv.getCssImage( "Spinner-DownButton-Icon", "background-image" );
      if( result.icon === org.eclipse.swt.theme.ThemeValues.NONE_IMAGE ) {
        result.icon = tv.getCssImage( "Spinner-DownButton", "background-image" );
      } else {
        result.backgroundImage = tv.getCssImage( "Spinner-DownButton", 
                                                 "background-image" );
      }      
      result.backgroundGradient = tv.getCssGradient( "Spinner-DownButton", 
                                                     "background-image" );
      result.border = tv.getCssBorder( "Spinner-DownButton", "border" );
      result.backgroundColor = tv.getCssColor( "Spinner-DownButton",
                                               "background-color" );
      result.cursor = tv.getCssCursor( "Spinner-DownButton", "cursor" );
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
        border : tv.getCssBorder( "*", "border" ),
        checkWidth : tv.getCssDimension( "Table-Checkbox", "width" ),
        checkImageHeight : tv.getCssSizedImage( "Table-Checkbox", 
                                                "background-image" )[2]
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

  "table-column" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        cursor : "default",
        spacing : 2,
        opacity : states.moving ? 0.6 : 1.0
      };
      result.padding = tv.getCssBoxDimensions( "TableColumn", "padding" );
      result.textColor = tv.getCssColor( "TableColumn", "color" );
      result.font = tv.getCssFont( "TableColumn", "font" );
      result.backgroundColor = tv.getCssColor( "TableColumn", "background-color" );
      result.backgroundImage = tv.getCssImage( "TableColumn", "background-image" );
      result.backgroundGradient = tv.getCssGradient( "TableColumn",
                                                     "background-image" );
      var border = new qx.ui.core.Border( 0 );
      if( !states.dummy ) {
        border.setColorRight( tv.getCssColor( "Table-GridLine", "color" ) );
        border.setWidthRight( 1 );
      }
      var borderBottom = tv.getCssBorder( "TableColumn", "border-bottom" );
      border.setWidthBottom( borderBottom.getWidthBottom() );
      border.setStyleBottom( borderBottom.getStyleBottom() );
      border.setColorBottom( borderBottom.getColorBottom() );
      result.border = border;
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
                         ? tv.getCssColor( "*", "color" )
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
      var checkWidth = tv.getCssDimension( "Table-Checkbox", "width" );
      var checkImage = tv.getCssSizedImage( "Table-Checkbox", "background-image" );
      result.paddingLeft = Math.max( 0, ( checkWidth - checkImage[ 1 ] ) / 2 );
      result.source = checkImage[ 0 ];
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
      result.backgroundGradient = tv.getCssGradient( "Sash-Handle", "background-image" );
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
      result.font = tv.getCssFont( "CTabItem", "font" );
      result.textColor = tv.getCssColor( "CTabItem", "color" );
      return result;
    }
  },

  "ctabfolder-body" : {
    style: function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.backgroundColor = tv.getCssColor( "CTabItem", "background-color" );
      var color = tv.getCssColor( "CTabFolder", "border-color" );
      var radii = tv.getCssBoxDimensions( "CTabFolder", "border-radius" );
      if( radii[ 0 ] > 0 || radii[ 1 ] > 0 || radii[ 2 ] > 0 || radii[ 3 ] > 0 )
      {
        result.border = new org.eclipse.rwt.RoundedBorder( 0, color, 0 );
        if( states.barTop ) {
          result.border.setRadii( [ radii[ 0 ], radii[ 1 ], 0, 0 ] );
        } else {
          result.border.setRadii( [ 0, 0, radii[ 2 ], radii[ 3 ] ] );
        }
      } else {
        result.border = new qx.ui.core.Border( 0, "solid", color );
      }
      if( states.rwt_BORDER ) {
        result.border.setWidth( 1 );
      }
      return result;
    }
  },

  "ctabfolder-frame" : {
    style: function( states ) {
      var result = {};
      if( !states.rwt_FLAT ) {
        // get the background color for selected items
        var tv = new org.eclipse.swt.theme.ThemeValues( { "selected": true } );
        var color = tv.getCssColor( "CTabItem", "background-color" );
        result.border = new qx.ui.core.Border( 2, "solid", color );
      } else {
        result.border = "undefined";
      }
      result.backgroundColor = "undefined";
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
    style: function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.cursor = "default";
      var padding = tv.getCssBoxDimensions( "CTabItem", "padding" );
      result.paddingLeft = padding[ 3 ];
      result.paddingRight = padding[ 1 ];
      result.spacing = tv.getCssDimension( "CTabItem", "spacing" );
      result.textColor = tv.getCssColor( "CTabItem", "color" );
      var color = tv.getCssColor( "CTabFolder", "border-color" );
      // create a copy of the radii from theme
      var radii
         = tv.getCssBoxDimensions( "CTabFolder", "border-radius" ).slice( 0 );
      // cut off rounded corners at opposite side of tabs
      if( states.barTop ) {
        radii[ 2 ] = 0;
        radii[ 3 ] = 0;
      } else {
        radii[ 0 ] = 0;
        radii[ 1 ] = 0;
      }
      // cut off right rounded corners of unselected tabs
      if( !states.selected ) {
        radii[ 1 ] = 0;
        radii[ 2 ] = 0;
      }
      // cut off left rounded corners of unselected tabs except first
      if( !states.selected && !( states.firstItem && states.rwt_BORDER ) ) {
        radii[ 0 ] = 0;
        radii[ 3 ] = 0;
      }
      var rounded
         = radii[ 0 ] > 0 || radii[ 1 ] > 0 || radii[ 2 ] > 0 || radii[ 3 ] > 0;
      if( rounded ) {
        result.border = new org.eclipse.rwt.RoundedBorder( 0, color );
        result.border.setRadii( radii );
      } else {
        result.border = new qx.ui.core.Border( 0, "solid", color );
      }
      if( !states.nextSelected ) {
        result.border.setWidthRight( 1 );
      }
      if( states.selected ) {
        result.border.setWidthLeft( 1 );
        if( states.barTop ) {
          result.border.setWidthTop( 1 );
        } else {
          result.border.setWidthBottom( 1 );
        }
      }
      if( states.firstItem && states.rwt_BORDER && !rounded ) {
        result.border.setWidthLeft( 1 );
      }
      if( states.selected ) {
        result.backgroundColor = tv.getCssColor( "CTabItem", "background-color" );
        result.backgroundImage = tv.getCssImage( "CTabItem", "background-image" );
        result.backgroundGradient = tv.getCssGradient( "CTabItem",
                                                       "background-image" );
      } else {
        result.backgroundColor = "undefined";
        result.backgroundImage = null;
        result.backgroundGradient = null;
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
        var color = tv.getCssColor( "CTabFolder", "border-color" );
        result.border = new qx.ui.core.Border( 1, "solid", color );
      } else {
        result.backgroundColor = "undefined";
        result.border = "undefined";
      }
      return result;
    }
  },
  
  "ctabfolder-drop-down-button" : {
    include : "ctabfolder-button",
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );      
      result.icon = tv.getCssImage( "CTabFolder-DropDownButton-Icon",
                                    "background-image" );      
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
      result.backgroundColor = tv.getCssColor( "Composite", "background-color" );
      result.backgroundImage = tv.getCssImage( "Composite", "background-image" );
      result.backgroundGradient = tv.getCssGradient( "Composite",
                                                     "background-image" );
      result.border = tv.getCssBorder( "Composite", "border" );
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
      result.backgroundGradient = tv.getCssGradient( "CoolBar",
                                                     "background-image" );
      result.backgroundImage = tv.getCssImage( "CoolBar",
                                               "background-image" );
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
                   ? tv.getCssColor( "*", "color" )
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
      result.backgroundGradient = tv.getCssGradient( "ProgressBar",
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
      result.backgroundGradient = tv.getCssGradient( "ProgressBar-Indicator",
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
        padding   : [ 0, 3 ]
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
        cursor     : "default"
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
      result.width = tv.getCssDimension( "DateTime-UpButton", "width" );
      result.icon = tv.getCssImage( "DateTime-UpButton-Icon",
                                    "background-image" );
      if( result.icon === org.eclipse.swt.theme.ThemeValues.NONE_IMAGE ) {
        result.icon = tv.getCssImage( "DateTime-UpButton", "background-image" );
      } else {
        result.backgroundImage = tv.getCssImage( "DateTime-UpButton", 
                                                 "background-image" );
      }      
      result.backgroundGradient = tv.getCssGradient( "DateTime-UpButton", 
                                                     "background-image" );
      result.border = tv.getCssBorder( "DateTime-UpButton", "border" );
      result.backgroundColor = tv.getCssColor( "DateTime-UpButton",
                                               "background-color" );
      result.cursor = tv.getCssCursor( "DateTime-UpButton", "cursor" );
      return result;
    }
  },

  "datetime-button-down" : {
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.width = tv.getCssDimension( "DateTime-DownButton", "width" );
      result.icon = tv.getCssImage( "DateTime-DownButton-Icon",
                                    "background-image" );
      if( result.icon === org.eclipse.swt.theme.ThemeValues.NONE_IMAGE ) {
        result.icon = tv.getCssImage( "DateTime-DownButton", "background-image" );
      } else {
        result.backgroundImage = tv.getCssImage( "DateTime-DownButton", 
                                                 "background-image" );
      }      
      result.backgroundGradient = tv.getCssGradient( "DateTime-DownButton", 
                                                     "background-image" );
      result.border = tv.getCssBorder( "DateTime-DownButton", "border" );
      result.backgroundColor = tv.getCssColor( "DateTime-DownButton",
                                               "background-color" );
      result.cursor = tv.getCssCursor( "DateTime-DownButton", "cursor" );
      return result;
    }
  },
  
  "datetime-drop-down-button" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "DateTime-DropDownButton", "border" );      
      result.icon = tv.getCssImage( "DateTime-DropDownButton-Icon",
                                    "background-image" );
      if( result.icon === org.eclipse.swt.theme.ThemeValues.NONE_IMAGE ) {
        result.icon = tv.getCssImage( "DateTime-DropDownButton",
                                      "background-image" );
      } else {
        result.backgroundImage = tv.getCssImage( "DateTime-DropDownButton", 
                                                 "background-image" );
      }      
      result.backgroundGradient = tv.getCssGradient( "DateTime-DropDownButton", 
                                                     "background-image" );
      result.backgroundColor = tv.getCssColor( "DateTime-DropDownButton",
                                               "background-color" );
      result.cursor = tv.getCssCursor( "DateTime-DropDownButton", "cursor" );
      return result;
    }
  },
  
  "datetime-drop-down-calendar" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "DateTime-DropDownCalendar", "border" );
      result.backgroundColor = tv.getCssColor( "DateTime", "background-color" );
      return result;
    }
  },

  // ------------------------------------------------------------------------
  // Calendar

  "calendar-navBar" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : tv.getCssBorder( "DateTime-Calendar-Navbar", "border" ),
        backgroundColor : tv.getCssColor( "DateTime-Calendar-Navbar", 
                                          "background-color" ),
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
                               "background-image" ),
        cursor : tv.getCssCursor( "DateTime-Calendar-PreviousYearButton",
                                  "cursor" )
      };
    }
  },

  "calendar-toolbar-previous-month-button" : {
    include: "calendar-toolbar-button",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        icon : tv.getCssImage( "DateTime-Calendar-PreviousMonthButton",
                               "background-image" ),
        cursor : tv.getCssCursor( "DateTime-Calendar-PreviousMonthButton",
                                  "cursor" )
      };
    }
  },

  "calendar-toolbar-next-month-button" : {
    include: "calendar-toolbar-button",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        icon : tv.getCssImage( "DateTime-Calendar-NextMonthButton",
                               "background-image" ),
        cursor : tv.getCssCursor( "DateTime-Calendar-NextMonthButton",
                                  "cursor" )
      };
    }
  },

  "calendar-toolbar-next-year-button" : {
    include: "calendar-toolbar-button",

    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        icon : tv.getCssImage( "DateTime-Calendar-NextYearButton",
                               "background-image" ),
        cursor : tv.getCssCursor( "DateTime-Calendar-NextYearButton",
                                  "cursor" )
      };
    }
  },

  "calendar-monthyear" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        font          : tv.getCssFont( "DateTime-Calendar-Navbar", "font" ),
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
      // FIXME: [if] Bigger font size leads to text cutoff 
      var font = tv.getCssFont( "*", "font" );
      var smallFont = new qx.ui.core.Font();
      smallFont.setSize( 11 );
      smallFont.setFamily( font.getFamily() );
      smallFont.setBold( font.getBold() );
      smallFont.setItalic( font.getItalic() );
      return {
        font            : smallFont,
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
      if( states.disabled ) {
        result.textColor = tv.getCssColor( "DateTime", "color" );
        result.backgroundColor = tv.getCssColor( "DateTime", "background-color" );
      } else if( states.selected || states.otherMonth || states.over ) {
        result.textColor = tv.getCssColor( "DateTime-Calendar-Day", "color" );
        result.backgroundColor = tv.getCssColor( "DateTime-Calendar-Day",
                                                 "background-color" );
      } else {
        result.textColor = "undefined";
        result.backgroundColor = "undefined";
      }
      var borderColor = states.disabled ? tv.getCssColor( "*", "color" ) : "red";
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
      result.border = tv.getCssBorder( "ExpandBar", "border" );
      result.font = tv.getCssFont( "ExpandBar", "font" );
      result.textColor = tv.getCssColor( "ExpandBar", "color" );
      return result;
    }
  },

  "expand-item" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        overflow : "hidden",
        border : tv.getCssBorder( "ExpandItem", "border" )
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
      result.cursor = tv.getCssCursor( "ExpandItem-Header", "cursor" );
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
      result.border = tv.getCssBorder( "ExpandItem-Header", "border" );
      result.backgroundColor
        = tv.getCssColor( "ExpandItem-Header", "background-color" );
      result.textColor = states.disabled
                         ? tv.getCssColor( "*", "color" )
                         : "undefined";
      result.cursor = tv.getCssCursor( "ExpandItem-Header", "cursor" );
      result.backgroundImage = tv.getCssImage( "ExpandItem-Header",
                                               "background-image" );
      result.backgroundGradient = tv.getCssGradient( "ExpandItem-Header",
                                                     "background-image" );
      return result;
    }
  },

  // ------------------------------------------------------------------------
  // Slider

  "slider" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : tv.getCssBorder( "Slider", "border" ),
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
      result.backgroundColor = tv.getCssColor( "Slider-Thumb",
                                               "background-color" );
      result.border = tv.getCssBorder( "Slider-Thumb", "border" );
      result.backgroundImage = tv.getCssImage( "Slider-Thumb",
                                               "background-image" );
      result.backgroundGradient = tv.getCssGradient( "Slider-Thumb", 
                                                     "background-image" );
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
      result.icon = tv.getCssImage( "Slider-DownButton-Icon", "background-image" );
      if( result.icon === org.eclipse.swt.theme.ThemeValues.NONE_IMAGE ) {
        result.icon = tv.getCssImage( "Slider-DownButton", "background-image" );
      } else {
        result.backgroundImage = tv.getCssImage( "Slider-DownButton", 
                                                 "background-image" );
      }      
      result.backgroundGradient = tv.getCssGradient( "Slider-DownButton", 
                                                     "background-image" );
      result.border = tv.getCssBorder( "Slider-DownButton", "border" );
      if( states.horizontal ){
        result.width = org.eclipse.swt.widgets.Slider.BUTTON_WIDTH;
      } else {
        result.height = org.eclipse.swt.widgets.Slider.BUTTON_WIDTH;
      }
      result.cursor = tv.getCssCursor( "Slider-DownButton", "cursor" );
      return result;
    }
  },

  "slider-max-button" : {
    include : "button",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.backgroundColor = tv.getCssColor( "Slider-UpButton", "background-color" );
      result.icon = tv.getCssImage( "Slider-UpButton-Icon", "background-image" );
      if( result.icon === org.eclipse.swt.theme.ThemeValues.NONE_IMAGE ) {
        result.icon = tv.getCssImage( "Slider-UpButton", "background-image" );
      } else {
        result.backgroundImage = tv.getCssImage( "Slider-UpButton", 
                                                 "background-image" );
      }      
      result.backgroundGradient = tv.getCssGradient( "Slider-UpButton", 
                                                     "background-image" );
      result.border = tv.getCssBorder( "Slider-UpButton", "border" );
      if( states.horizontal ) {
        result.width = org.eclipse.swt.widgets.Slider.BUTTON_WIDTH;
      } else {
        result.height = org.eclipse.swt.widgets.Slider.BUTTON_WIDTH;
      }
      result.cursor = tv.getCssCursor( "Slider-UpButton", "cursor" );
      return result;
    }
  }
}
} );
