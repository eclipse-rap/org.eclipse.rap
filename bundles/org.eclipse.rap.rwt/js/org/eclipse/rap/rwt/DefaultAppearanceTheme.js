/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

/* ************************************************************************

#module(ui_core)
#module(theme_appearance)
#optional(qx.renderer.color.Color)
#optional(qx.renderer.color.ColorObject)
#optional(qx.renderer.border.Border)
#optional(qx.renderer.border.BorderObject)
#optional(qx.renderer.font.Font)
#optional(qx.renderer.font.FontObject)

 ************************************************************************ */

qx.OO.defineClass(
  "org.eclipse.rap.rwt.DefaultAppearanceTheme", 
  qx.renderer.theme.AppearanceTheme,
  function(vTitle) {
    qx.renderer.theme.AppearanceTheme.call( this, 
                                            vTitle || "rap default appearance");
  }
);

org.eclipse.rap.rwt.DefaultAppearanceTheme.systemFontName
  = '"Segoe UI", Corbel, Calibri, Tahoma, "Lucida Sans Unicode", sans-serif';

qx.Class.colorGrayText = new qx.renderer.color.ColorObject( "graytext" );

qx.Proto._appearances = qx.lang.Object.carefullyMergeWith( {
  /*
  ---------------------------------------------------------------------------
    CORE
  ---------------------------------------------------------------------------
   */

  "image" : {
    initial : function(vTheme) {
      return {
        allowStretchX : false,
        allowStretchY : false
      }
    }
  },

  "client-document" : {
    setup : function() {
      this.bgcolor = new qx.renderer.color.ColorObject("#ffffff");
      this.color = new qx.renderer.color.ColorObject("windowtext");
    },

    initial : function(vTheme) {
      return {
        backgroundColor : this.bgcolor,
        backgroundImage : "./org/eclipse/rap/rwt/widgets/display/bg.gif",
        color : this.color,
        hideFocus : true,
        enableElementFocus : false
      }
    }
  },

  "blocker" : {
    initial : function(vTheme) {
      // You could also use: "static/image/dotted_white.gif" for example as backgroundImage here
      // (Visible) background tiles could be dramatically slow down mshtml!
      // A background image or color is always needed for mshtml to block the events successfully.
      return {
        cursor : qx.constant.Core.DEFAULT,
        backgroundImage : "static/image/blank.gif"
      }
    }
  },

  "atom" : {
    initial : function(vTheme) {
      return {
        cursor : qx.constant.Core.DEFAULT,
        spacing : 4,
        width : qx.constant.Core.AUTO,
        height : qx.constant.Core.AUTO,
        horizontalChildrenAlign : qx.constant.Layout.ALIGN_CENTER,
        verticalChildrenAlign : qx.constant.Layout.ALIGN_MIDDLE,
        stretchChildrenOrthogonalAxis : false,
        allowStretchY : false,
        allowStretchX : false
      }
    }
  },

  // this applies to qooxdoo labels (as embedded in Atom, Button, etc.)
  "label" : {
    setup : function() {
      this.color_disabled = org.eclipse.rap.rwt.DefaultAppearanceTheme.colorGrayText;
      this.font = new qx.renderer.font.Font( 11, org.eclipse.rap.rwt.DefaultAppearanceTheme.systemFontName );
      this.border_default = qx.renderer.border.BorderPresets.getInstance().none;
      this.border = qx.renderer.border.BorderPresets.getInstance().thinInset;
    },

    initial : function(vTheme) {
      return {
        font: this.font,
        wrap : false,
        border : this.border_default
      }
    },

    state : function(vTheme, vStates) {
      return {
        color : vStates.disabled ? this.color_disabled : null,
        border : vStates.rwt_BORDER ? this.border : this.border_default
      }
    }
  },
  
  // this applies to a qooxdoo qx.ui.basic.Atom that represents an RWT Label
  "label-wrapper" : {
    setup : function() {
      this.border = qx.renderer.border.BorderPresets.getInstance().thinInset;
      this.border_default = qx.renderer.border.BorderPresets.getInstance().none;
    },
    initial : function(vTheme) {
      return {
        hideFocus : true
      }
    },
    state : function(vTheme, vStates) {
      return {
        border : vStates.rwt_BORDER ? this.border : this.border_default
      }
    }
  },

  "htmlcontainer" : {
    initial : function(vTheme) {
      return vTheme.initialFrom("label");
    },

    state : function(vTheme, vStates) {
      return vTheme.stateFrom("label", vStates);
    }
  },

  "popup" : {
    initial : function(vTheme) {
      return {
        width : qx.constant.Core.AUTO,
        height : qx.constant.Core.AUTO
      }
    }
  },

  "tool-tip" : {
    setup : function() {
      this.bgcolor = new qx.renderer.color.ColorObject("InfoBackground");
      this.color = new qx.renderer.color.ColorObject("InfoText");
    },

    initial : function(vTheme) {
      return qx.lang.Object.mergeWith(vTheme.initialFrom("popup"), {
        backgroundColor : this.bgcolor,
        color : this.color,
        border : qx.renderer.border.BorderPresets.getInstance().info,
        paddingTop : 1,
        paddingRight : 3,
        paddingBottom : 2,
        paddingLeft : 3
      });
    }
  },

  "iframe" : {
    initial : function(vTheme) {
      return {
        border : qx.renderer.border.BorderPresets.getInstance().inset
      }
    }
  },


  /*
  ---------------------------------------------------------------------------
    BUTTON
  ---------------------------------------------------------------------------
   */

  "button" : {
    setup : function() {
      this.bgcolor_default = new qx.renderer.color.ColorObject("#f7f7fb");
      this.bgcolor_over = new qx.renderer.color.ColorObject("#fbfbfe");

      this.border = new qx.renderer.border.BorderObject();
      this.border.setTop( 1, "solid", "white" );
      this.border.setLeft( 1, "solid", "white" );
      this.border.setRight( 1, "solid", "gray" );
      this.border.setBottom( 1, "solid", "gray" );
      this.border_pressed = qx.renderer.border.BorderPresets.getInstance().thinInset;
      this.border_BORDER = qx.renderer.border.BorderPresets.getInstance().outset;
      this.border_BORDER_pressed = qx.renderer.border.BorderPresets.getInstance().inset;
      this.border_FLAT = new qx.renderer.border.BorderObject( 1, "solid", "black" );
    },

    initial : function( vTheme ) {
      return vTheme.initialFrom("atom");
    },

    state : function( vTheme, vStates ) {
      var vReturn = {};
      vReturn.backgroundColor = vStates.over ? this.bgcolor_over : this.bgcolor_default;
      
      if( vStates.rwt_FLAT ) {
        vReturn.border = this.border_FLAT;
      } else if (vStates.rwt_BORDER) {
        vReturn.border = vStates.pressed || vStates.checked ? this.border_BORDER_pressed : this.border_BORDER;
      } else {
        vReturn.border = vStates.pressed || vStates.checked ? this.border_pressed : this.border;
      }

      if( vStates.pressed ) {
        vReturn.paddingTop = 4;
        vReturn.paddingRight = 3;
        vReturn.paddingBottom = 2;
        vReturn.paddingLeft = 5;
      } else {
        vReturn.paddingTop = vReturn.paddingBottom = 3;
        vReturn.paddingRight = vReturn.paddingLeft = 4;
      }

      return vReturn;
    }
  },


  /*
  ---------------------------------------------------------------------------
    TOOLBAR
  ---------------------------------------------------------------------------
   */

  "toolbar" : {
    setup : function() {
      this.border_default = qx.renderer.border.BorderPresets.getInstance().none;
      this.border = qx.renderer.border.BorderPresets.getInstance().thinOutset;
    },

    initial : function(vTheme) {
      return {
        height : qx.constant.Core.AUTO
      }
    },
    
    state : function(vTheme, vStates) {
      return {
        border : vStates.rwt_BORDER ? this.border : this.border_default
      }
    }
  },

  "toolbar-part" : {
    initial : function(vTheme) {
      return {
        width : qx.constant.Core.AUTO
      }
    }
  },

  "toolbar-part-handle" : {
    initial : function(vTheme) {
      return {
        width : 10
      }
    }
  },

  "toolbar-part-handle-line" : {
    initial : function( vTheme ) {
      return {
        top : 2,
        left : 3,
        bottom : 2,
        width : 4,
        border : qx.renderer.border.BorderPresets.getInstance().thinOutset
      }
    }
  },

  "toolbar-separator" : {
    initial : function( vTheme ) {
      return {
        width : 8
      }
    }
  },

  "toolbar-separator-line" : {
    setup : function() {
      this.border_none = qx.renderer.border.BorderPresets.getInstance().none;
      var b = this.border = new qx.renderer.border.BorderObject;

      b.setLeftColor( "threedshadow" );
      b.setRightColor( "threedhighlight" );

      b.setLeftStyle( qx.constant.Style.BORDER_SOLID );
      b.setRightStyle( qx.constant.Style.BORDER_SOLID );

      b.setLeftWidth( 1 );
      b.setRightWidth( 1 );
      b.setTopWidth( 0 );
      b.setBottomWidth( 0 );
    },

    initial : function( vTheme ) {
      return {
        top : 2,
        left: 3,
        width : 2,
        bottom : 2
      }
    },
    
    state : function( vTheme, vStates ) {
      return {
        border : vStates.rwt_FLAT ? this.border : this.border_none
      }
    }
  },

  "toolbar-button" : {
    setup : function() {
      this.border_none = qx.renderer.border.BorderPresets.getInstance().none;
      this.border_raised = qx.renderer.border.BorderPresets.getInstance().thinOutset;
      this.border_pressed = qx.renderer.border.BorderPresets.getInstance().thinInset;

      this.checked_background = "static/image/dotted_white.gif";
    },

    initial : function(vTheme) {
      return {
        cursor : qx.constant.Core.DEFAULT,
        spacing : 4,
        width : qx.constant.Core.AUTO,
        verticalChildrenAlign : qx.constant.Layout.ALIGN_MIDDLE
      }
    },

    state : function(vTheme, vStates) {
      var vReturn = {
        backgroundImage : vStates.checked && !vStates.over ? this.checked_background : null
      }

      if (vStates.pressed || vStates.checked || vStates.abandoned) {
        vReturn.border = this.border_pressed;

        vReturn.paddingTop = 3;
        vReturn.paddingRight = 2;
        vReturn.paddingBottom = 1;
        vReturn.paddingLeft = 4;
      } else if ( !vStates.rwt_FLAT || vStates.over ) {
        vReturn.border = this.border_raised;
        vReturn.paddingTop = vReturn.paddingBottom = 2;
        vReturn.paddingLeft = vReturn.paddingRight = 3;
      } else {
        vReturn.border = this.border_none;
        vReturn.paddingTop = vReturn.paddingBottom = 3;
        vReturn.paddingLeft = vReturn.paddingRight = 4;
      }

      return vReturn;
    }
  },


  /*
  ---------------------------------------------------------------------------
    BAR VIEW
  ---------------------------------------------------------------------------
   */

  "bar-view" : {
    setup : function() {
      this.background = new qx.renderer.color.ColorObject("#FAFBFE");
    },

    initial : function(vTheme) {
      return {
        backgroundColor : this.background,
        border : qx.renderer.border.BorderPresets.getInstance().shadow
      }
    }
  },

  "bar-view-pane" : {
    state : function(vTheme, vStates) {
      if (vStates.barHorizontal) {
        return {
          width : null,
          height : qx.constant.Core.FLEX
        }
      }
      else {
        return {
          width : qx.constant.Core.FLEX,
          height : null
        }
      }
    }
  },

  "bar-view-page" : {
    initial : function(vTheme) {
      return {
        left : 10,
        right : 10,
        top : 10,
        bottom : 10
      }
    }
  },

  "bar-view-bar" : {
    setup : function() {
      this.background_color = new qx.renderer.color.ColorObject("#E1EEFF");

      this.border_color = new qx.renderer.color.ColorObject("threedshadow");

      this.border_top = new qx.renderer.border.BorderObject;
      this.border_top.setBottom(1, qx.constant.Style.BORDER_SOLID, this.border_color);

      this.border_bottom = new qx.renderer.border.BorderObject;
      this.border_bottom.setTop(1, qx.constant.Style.BORDER_SOLID, this.border_color);

      this.border_left = new qx.renderer.border.BorderObject;
      this.border_left.setRight(1, qx.constant.Style.BORDER_SOLID, this.border_color);

      this.border_right = new qx.renderer.border.BorderObject;
      this.border_right.setLeft(1, qx.constant.Style.BORDER_SOLID, this.border_color);
    },

    initial : function(vTheme) {
      return {
        backgroundColor : this.background_color
      }
    },

    state : function(vTheme, vStates) {
      if (vStates.barTop) {
        return {
          paddingTop : 1,
          paddingRight : 0,
          paddingBottom : 1,
          paddingLeft : 0,

          border : this.border_top,
          height : qx.constant.Core.AUTO,
          width : null,
          orientation : qx.constant.Layout.ORIENTATION_HORIZONTAL
        };
      }
      else if (vStates.barBottom) {
        return {
          paddingTop : 1,
          paddingRight : 0,
          paddingBottom : 1,
          paddingLeft : 0,

          border : this.border_bottom,
          height : qx.constant.Core.AUTO,
          width : null,
          orientation : qx.constant.Layout.ORIENTATION_HORIZONTAL
        };
      }
      else if (vStates.barLeft) {
        return {
          paddingTop : 0,
          paddingRight : 1,
          paddingBottom : 0,
          paddingLeft : 1,

          border : this.border_left,
          height : null,
          width : qx.constant.Core.AUTO,
          orientation : qx.constant.Layout.ORIENTATION_VERTICAL
        };
      }
      else if (vStates.barRight) {
        return {
          paddingTop : 0,
          paddingRight : 1,
          paddingBottom : 0,
          paddingLeft : 1,

          border : this.border_right,
          height : null,
          width : qx.constant.Core.AUTO,
          orientation : qx.constant.Layout.ORIENTATION_VERTICAL
        };
      }
    }
  },

  "bar-view-button" : {
    setup : function() {
      this.background_color_normal = null;
      this.background_color_checked = new qx.renderer.color.ColorObject("#FAFBFE");

      this.border_color = new qx.renderer.color.ColorObject("threedshadow");
      this.border_color_checked = new qx.renderer.color.ColorObject("#FEC83C");

      this.border_top_checked = new qx.renderer.border.Border(1, qx.constant.Style.BORDER_SOLID, this.border_color);
      this.border_top_checked.setBottom(3, qx.constant.Style.BORDER_SOLID, this.border_color_checked);

      this.border_bottom_checked = new qx.renderer.border.Border(1, qx.constant.Style.BORDER_SOLID, this.border_color);
      this.border_bottom_checked.setTop(3, qx.constant.Style.BORDER_SOLID, this.border_color_checked);

      this.border_left_checked = new qx.renderer.border.Border(1, qx.constant.Style.BORDER_SOLID, this.border_color);
      this.border_left_checked.setRight(3, qx.constant.Style.BORDER_SOLID, this.border_color_checked);

      this.border_right_checked = new qx.renderer.border.Border(1, qx.constant.Style.BORDER_SOLID, this.border_color);
      this.border_right_checked.setLeft(3, qx.constant.Style.BORDER_SOLID, this.border_color_checked);
    },

    initial : function(vTheme) {
      return qx.lang.Object.mergeWith(vTheme.initialFrom("atom"), {
        iconPosition : qx.constant.Layout.ALIGN_TOP
      });
    },

    state : function(vTheme, vStates) {
      var vReturn = {
        backgroundColor : vStates.checked ? this.background_color_checked : this.background_color_normal,
        allowStretchX : true,
        allowStretchY : true
      }

      if (vStates.checked || vStates.over) {
        if (vStates.barTop) {
          vReturn.border = this.border_top_checked;
          vReturn.paddingTop = 3;
          vReturn.paddingRight = 6;
          vReturn.paddingBottom = 1;
          vReturn.paddingLeft = 6;
        }
        else if (vStates.barBottom) {
          vReturn.border = this.border_bottom_checked;
          vReturn.paddingTop = 1;
          vReturn.paddingRight = 6;
          vReturn.paddingBottom = 3;
          vReturn.paddingLeft = 6;
        }
        else if (vStates.barLeft) {
          vReturn.border = this.border_left_checked;
          vReturn.paddingTop = 3;
          vReturn.paddingRight = 4;
          vReturn.paddingBottom = 3;
          vReturn.paddingLeft = 6;
        }
        else if (vStates.barRight) {
          vReturn.border = this.border_right_checked;
          vReturn.paddingTop = 3;
          vReturn.paddingRight = 6;
          vReturn.paddingBottom = 3;
          vReturn.paddingLeft = 4;
        }
      }
      else {
        vReturn.border = null;
        vReturn.paddingTop = vReturn.paddingBottom = 4;
        vReturn.paddingRight = vReturn.paddingLeft = 7;
      }

      if (vStates.barTop || vStates.barBottom) {
        vReturn.marginTop = vReturn.marginBottom = 0;
        vReturn.marginRight = vReturn.marginLeft = 1;
        vReturn.width = qx.constant.Core.AUTO;
        vReturn.height = null;
      }
      else if (vStates.barLeft || vStates.barRight) {
        vReturn.marginTop = vReturn.marginBottom = 1;
        vReturn.marginRight = vReturn.marginLeft = 0;
        vReturn.height = qx.constant.Core.AUTO;
        vReturn.width = null;
      }

      return vReturn;
    }
  },









  /*
  ---------------------------------------------------------------------------
    WINDOW
  ---------------------------------------------------------------------------
   */

  "window" : {
    setup : function() {
      this.bgcolor = new qx.renderer.color.ColorObject("#f8f8ff");
      this.color = new qx.renderer.color.ColorObject("windowtext");
    },

    initial : function(vTheme) {
      return {
        backgroundColor : this.bgcolor,
        color : this.color
      }
    },

    state : function(vTheme, vStates) {
      return {
        border : ( vStates.rwt_TITLE || vStates.rwt_BORDER )
                 && !vStates.maximized
          ? qx.renderer.border.BorderPresets.getInstance().outset
          : qx.renderer.border.BorderPresets.getInstance().black,
        minWidth : vStates.rwt_TITLE ? 80 : 5,
        minHeight : vStates.rwt_TITLE ? 25 : 5
      }
    }
  },

  "window-captionbar" : {
    setup : function() {
      this.bgimage_active = "./org/eclipse/rap/rwt/widgets/shell/caption_active.gif";
      this.bgimage_inactive = "./org/eclipse/rap/rwt/widgets/shell/caption_inactive.gif";
      this.color_active = new qx.renderer.color.ColorObject( "#ffffff" );
      this.color_inactive = new qx.renderer.color.ColorObject( "#dddddd" );
    },

    initial : function(vTheme) {
      return {
        paddingTop : 1,
        paddingRight : 2,
        paddingBottom : 1,
        paddingLeft : 2,
        marginBottom : 1,
        verticalChildrenAlign : qx.constant.Layout.ALIGN_MIDDLE,
        height : qx.constant.Core.AUTO,
        overflow : qx.constant.Style.OVERFLOW_HIDDEN
      }
    },

    state : function(vTheme, vStates) {
      return {
        minHeight : vStates.rwt_TITLE ? 18 : 0,
        maxHeight : vStates.rwt_TITLE ? 18 : 0,
        backgroundImage : vStates.active ? this.bgimage_active : this.bgimage_inactive,
        color : vStates.active ? this.color_active : this.color_inactive
      }
    }
  },

  "window-resize-frame" : {
    initial : function(vTheme) {
      return {
        border : qx.renderer.border.BorderPresets.getInstance().shadow
      }
    }
  },

  "window-captionbar-icon" : {
    initial : function(vTheme) {
      return {
        marginRight : 2
      }
    }
  },

  "window-captionbar-title" : {
    setup : function() {
      this.font = new qx.renderer.font.Font(11, org.eclipse.rap.rwt.DefaultAppearanceTheme.systemFontName );
      this.font.setBold(true);
    },
    initial : function(vTheme) {
      return {
        cursor : qx.constant.Core.DEFAULT,
        font : this.font,
        marginRight : 2,
        wrap : false
      }
    }
  },

  "window-captionbar-button" : {
    setup : function() {
      this.border = new qx.renderer.border.Border(1, "solid", "white");
    },
    initial : function(vTheme) {
      return qx.lang.Object.mergeWith(vTheme.initialFrom("button"), {
        border : this.border,
        paddingTop : 1,
        paddingBottom : 1,
        paddingRight : 1,
        paddingLeft : 1,
        marginLeft : 1
      });
    },

    state : function(vTheme, vStates) {
      return {};
      }
  },

  "window-captionbar-minimize-button" : {
    setup : function() {
      this.bgcolor = new qx.renderer.color.ColorObject( "#3ba929" );
      this.bgcolor_over = new qx.renderer.color.ColorObject( "#75d066" );
      this.bgcolor_inactive = new qx.renderer.color.ColorObject( "#87b87f" );
    },
    initial : function(vTheme) {
      return vTheme.initialFrom("window-captionbar-button");
    },
    state : function(vTheme, vStates) {
      var vReturn = vTheme.stateFrom("window-captionbar-button", vStates);
      vReturn.backgroundColor = vStates.active ?
                                  vStates.over && !vStates.pressed ?
                                    this.bgcolor_over : this.bgcolor :
                                  this.bgcolor_inactive;
      return vReturn;
    }
  },

  "window-captionbar-restore-button" : {
    setup : function() {
      this.bgcolor = new qx.renderer.color.ColorObject( "#3ba929" );
      this.bgcolor_over = new qx.renderer.color.ColorObject( "#75d066" );
      this.bgcolor_inactive = new qx.renderer.color.ColorObject( "#87b87f" );
      
    },
    initial : function(vTheme) {
      return vTheme.initialFrom("window-captionbar-button");
    },
    state : function(vTheme, vStates) {
      var vReturn = vTheme.stateFrom("window-captionbar-button", vStates);
      vReturn.backgroundColor = vStates.active ?
                                  vStates.over && !vStates.pressed ?
                                    this.bgcolor_over : this.bgcolor :
                                  this.bgcolor_inactive;
      return vReturn;
    }
  },

  "window-captionbar-maximize-button" : {
    setup : function() {
      this.bgcolor = new qx.renderer.color.ColorObject( "#3ba929" );
      this.bgcolor_over = new qx.renderer.color.ColorObject( "#75d066" );
      this.bgcolor_inactive = new qx.renderer.color.ColorObject( "#87b87f" );
    },
    initial : function(vTheme) {
      return vTheme.initialFrom("window-captionbar-button");
    },
    state : function(vTheme, vStates) {
      var vReturn = vTheme.stateFrom("window-captionbar-button", vStates);
      vReturn.backgroundColor = vStates.active ?
                                  vStates.over && !vStates.pressed ?
                                    this.bgcolor_over : this.bgcolor :
                                  this.bgcolor_inactive;
      return vReturn;
    }
  },

  "window-captionbar-close-button" : {
    setup: function() {
      this.bgcolor = new qx.renderer.color.ColorObject( "#ef1d2f" );
      this.bgcolor_over = new qx.renderer.color.ColorObject("#ff7884");
      this.bgcolor_inactive = new qx.renderer.color.ColorObject( "#c67f85" );
    },
    initial : function(vTheme) {
      return vTheme.initialFrom("window-captionbar-button");
    },
    state : function(vTheme, vStates) {
      var vReturn = vTheme.stateFrom("window-captionbar-button", vStates);
      vReturn.backgroundColor = vStates.active ?
                                  vStates.over && !vStates.pressed ?
                                    this.bgcolor_over : this.bgcolor :
                                  this.bgcolor_inactive;
      return vReturn;
    }
  },

  "window-statusbar" : {
    initial : function(vTheme) {
      return {
        border : qx.renderer.border.BorderPresets.getInstance().thinInset,
        height : qx.constant.Core.AUTO
      }
    }
  },

  "window-statusbar-text" : {
    initial : function(vTheme) {
      return {
        paddingTop : 1,
        paddingRight : 4,
        paddingBottom : 1,
        paddingLeft : 4,
        cursor : qx.constant.Core.DEFAULT
      }
    }
  },










  /*
  ---------------------------------------------------------------------------
    MENU
  ---------------------------------------------------------------------------
   */

  "menu" : {
    setup : function() {
      this.bgcolor = new qx.renderer.color.ColorObject("menu");
    },

    initial : function(vTheme) {
      return {
        width : qx.constant.Core.AUTO,
        height : qx.constant.Core.AUTO,
        backgroundColor : this.bgcolor,
        border : qx.renderer.border.BorderPresets.getInstance().outset,
        paddingTop : 1,
        paddingRight : 1,
        paddingBottom : 1,
        paddingLeft : 1
      }
    }
  },

  "menu-layout" : {
    initial : function(vTheme) {
      return {
        top : 0,
        right : 0,
        bottom : 0,
        left : 0
      }
    }
  },

  "menu-button" : {
    setup : function() {
      this.BGCOLOR_OVER = new qx.renderer.color.ColorObject("#316ac5");
      this.BGCOLOR_OUT = null;

      this.COLOR_OVER = new qx.renderer.color.ColorObject("highlighttext");
      this.COLOR_OUT = null;
    },

    initial : function(vTheme) {
      return {
        minWidth : qx.constant.Core.AUTO,
        height : qx.constant.Core.AUTO,
        spacing : 2,
        paddingTop : 2,
        paddingRight : 4,
        paddingBottom : 2,
        paddingLeft : 4,
        cursor : qx.constant.Core.DEFAULT,
        verticalChildrenAlign : qx.constant.Layout.ALIGN_MIDDLE,
        allowStretchX : true
      }
    },

    state : function(vTheme, vStates) {
      return {
        backgroundColor : vStates.over ? this.BGCOLOR_OVER : this.BGCOLOR_OUT,
        color : vStates.over ? this.COLOR_OVER : this.COLOR_OUT
      }
    }
  },

  "menu-check-box" : {
    initial : function(vTheme) {
      return vTheme.initialFrom("menu-button");
    },

    state : function(vTheme, vStates) {
      return vTheme.stateFrom("menu-button", vStates);
    }
  },

  "menu-radio-button" : {
    initial : function(vTheme) {
      return vTheme.initialFrom("menu-button");
    },

    state : function(vTheme, vStates) {
      return vTheme.stateFrom("menu-button", vStates);
    }
  },

  "menu-separator" : {
    initial : function(vTheme) {
      return {
        height : qx.constant.Core.AUTO,
        marginTop : 3,
        marginBottom : 2,
        paddingLeft : 3,
        paddingRight : 3
      }
    }
  },

  "menu-separator-line" : {
    initial : function(vTheme) {
      return {
        right : 0,
        left : 0,
        height : qx.constant.Core.AUTO,
        border : qx.renderer.border.BorderPresets.getInstance().verticalDivider
      }
    }
  },






  /*
  ---------------------------------------------------------------------------
    LIST
  ---------------------------------------------------------------------------
   */

  "list" : {
    setup : function() {
      this.bgcolor = new qx.renderer.color.Color("white");
      this.border_default = qx.renderer.border.BorderPresets.getInstance().none;
      this.border = qx.renderer.border.BorderPresets.getInstance().inset;
    },

    initial : function(vTheme) {
      return {
        overflow : qx.constant.Style.OVERFLOW_HIDDEN,
        backgroundColor : this.bgcolor
      }
    },
    
    state : function(vTheme, vStates) {
      return {
        border : vStates.rwt_BORDER ? this.border : this.border_default
      }
    }
  },

  "list-item" : {
    setup : function() {
      this.bgcolor_selected = new qx.renderer.color.ColorObject("#316ac5");
      this.bgcolor_selected_unfocused = new qx.renderer.color.ColorObject("#c0c0c0");
      this.color_selected = new qx.renderer.color.ColorObject("highlighttext");
    },

    initial : function(vTheme) {
      return {
        cursor : qx.constant.Core.DEFAULT,
        height : qx.constant.Core.AUTO,
        horizontalChildrenAlign : qx.constant.Layout.ALIGN_LEFT,
        verticalChildrenAlign : qx.constant.Layout.ALIGN_MIDDLE,
        spacing : 4,
        paddingTop : 3,
        paddingRight : 5,
        paddingBottom : 3,
        paddingLeft : 5,
        minWidth : qx.constant.Core.AUTO
      }
    },

    state : function(vTheme, vStates) {
      vResult = {};
      vResult.color = vStates.selected ? this.color_selected : null;
      if( vStates.selected ) {
        vResult.backgroundColor = vStates.focused
                                ? this.bgcolor_selected
                                : this.bgcolor_selected_unfocused;
      } else {
        vResult.backgroundColor = null;
      }
      return vResult;
    }
  },








  /*
  ---------------------------------------------------------------------------
    FIELDS
  ---------------------------------------------------------------------------
   */

  "text-field" : {
    setup : function() {
      this.font = new qx.renderer.font.Font(11, org.eclipse.rap.rwt.DefaultAppearanceTheme.systemFontName );
      this.border_default = qx.renderer.border.BorderPresets.getInstance().none;
      this.border = qx.renderer.border.BorderPresets.getInstance().inset;
    },

    initial : function(vTheme) {
      return {
        hideFocus : true,
        border: null,
        paddingTop : 0,
        paddingRight : 3,
        paddingBottom : 0,
        paddingLeft : 3,
        allowStretchY : false,
        allowStretchX : true,
        font : this.font,
        width : qx.constant.Core.AUTO,
        height : qx.constant.Core.AUTO
      }
    },

    state : function(vTheme, vStates) {
      //var vResult = vTheme.stateFrom("label", vStates);
      var vResult = {};
      vResult.border = vStates.rwt_BORDER ? this.border : this.border_default;
      vResult.paddingTop = vStates.rwt_BORDER ? 1 : 0;
      vResult.paddingRight = vStates.rwt_BORDER ? 4 : 3;
      vResult.paddingBottom = vStates.rwt_BORDER ? 1 : 0;
      vResult.paddingLeft = vStates.rwt_BORDER ? 4 : 3;
      return vResult;
    }
  },

  "text-area" : {
    initial : function(vTheme) {
      return qx.lang.Object.mergeWith(vTheme.initialFrom("text-field"), {
        overflow : qx.constant.Core.AUTO,

        // gecko automatically defines a marginTop/marginBottom of 1px. We need to reset these values.
        marginTop : 0,
        marginBottom : 0
      });
    },

    state : function(vTheme, vStates) {
      return vTheme.stateFrom("text-field", vStates);
    }
  },










  /*
  ---------------------------------------------------------------------------
    COMBOBOX
  ---------------------------------------------------------------------------
   */

  "combo-box" : {
    setup : function() {
      this.bgcolor = new qx.renderer.color.Color("white");
      this.border = qx.renderer.border.BorderPresets.getInstance().inset;
    },

    initial : function(vTheme) {
      return {
        minWidth : 40,
        width : 120,
        height : qx.constant.Core.AUTO,
        border : this.border,
        backgroundColor : this.bgcolor,
        allowStretchY : false
      }
    }
  },

  "combo-box-list" : {
    initial : function(vTheme) {
      return qx.lang.Object.mergeWith(vTheme.initialFrom("list"), {
        top : 0,
        right : 0,
        bottom : 0,
        left : 0,
        border : null,
        overflow : qx.constant.Style.OVERFLOW_VERTICAL
      });
    }
  },

  "combo-box-popup" : {
    initial : function(vTheme) {
      return qx.lang.Object.mergeWith(vTheme.initialFrom("list"), {
        height : qx.constant.Core.AUTO,
        maxHeight : 150,
        border : qx.renderer.border.BorderPresets.getInstance().shadow
      });
    }
  },

  "combo-box-text-field" : {
    setup : function() {
      this.bgcolor = new qx.renderer.color.Color("transparent");
    },

    initial : function(vTheme) {
      return qx.lang.Object.mergeWith(vTheme.initialFrom("text-field"), {
        border : qx.renderer.border.BorderPresets.getInstance().none,
        width : qx.constant.Core.FLEX,
        backgroundColor : this.bgcolor
      });
    }
  },

  "combo-box-button" : {
    initial : function(vTheme) {
      return qx.lang.Object.mergeWith(vTheme.initialFrom("button"), {
        height : null,
        allowStretchY : true
      });
    },

    state : function(vTheme, vStates) {
      return qx.lang.Object.mergeWith(vTheme.stateFrom("button", vStates), {
        backgroundColor : new qx.renderer.color.Color("#f8f8ff"),
        paddingTop : 0,
        paddingRight : 3,
        paddingBottom : 0,
        paddingLeft : 2
      });
    }
  },







  /*
  ---------------------------------------------------------------------------
    TREE
  ---------------------------------------------------------------------------
   */

  "tree-element" : {
    initial : function(vTheme) {
      return {
        height : 16,
        verticalChildrenAlign : qx.constant.Layout.ALIGN_MIDDLE
      }
    }
  },

  "tree-element-icon" : {
    initial : function(vTheme) {
      return {
        width : 16,
        height : 16
      }
    }
  },

  "tree-element-label" : {
    setup : function() {
      this.bgcolor_selected = new qx.renderer.color.ColorObject("#316ac5");
      this.color_selected = new qx.renderer.color.ColorObject("highlighttext");
    },

    initial : function(vTheme) {
      return qx.lang.Object.mergeWith(vTheme.initialFrom("label"), {
        cursor : qx.constant.Core.DEFAULT,
        marginLeft : 3,
        height : 15,
        paddingTop : 2,
        paddingRight : 2,
        paddingBottom : 2,
        paddingLeft : 2,
        allowStretchY : false
      });
    },

    state : function(vTheme, vStates) {
      return qx.lang.Object.mergeWith(vTheme.stateFrom("label", vStates), {
        backgroundColor : vStates.selected ? this.bgcolor_selected : null,
        color : vStates.selected ? this.color_selected : null
      });
    }
  },

  "tree-folder" : {
    initial : function(vTheme) {
      return vTheme.initialFrom("tree-element");
    },

    state : function(vTheme, vStates) {
      return vTheme.stateFrom("tree-element", vStates);
    }
  },

  "tree-folder-icon" : {
    initial : function(vTheme) {
      return {
        width : 16,
        height : 16
      }
    }
  },

  "tree-folder-label" : {
    initial : function(vTheme) {
      return vTheme.initialFrom("tree-element-label");
    },

    state : function(vTheme, vStates) {
      return vTheme.stateFrom("tree-element-label", vStates);
    }
  },

  "tree" : {
    initial : function(vTheme) {
      return vTheme.initialFrom("tree-folder");
    },

    state : function(vTheme, vStates) {
      return vTheme.stateFrom("tree-folder", vStates);
    }
  },

  "tree-icon" : {
    initial : function(vTheme) {
      return vTheme.initialFrom("tree-folder-icon");
    },

    state : function(vTheme, vStates) {
      return vTheme.stateFrom("tree-folder-icon", vStates);
    }
  },

  "tree-label" : {
    initial : function(vTheme) {
      return vTheme.initialFrom("tree-folder-label");
    },

    state : function(vTheme, vStates) {
      return vTheme.stateFrom("tree-folder-label", vStates);
    }
  },

  "tree-container" : {
    setup : function() {
      this.border_default = qx.renderer.border.BorderPresets.getInstance().none;
      this.border = qx.renderer.border.BorderPresets.getInstance().inset;
    },
  
    initial : function(vTheme) {
      return {
        backgroundColor : new qx.renderer.color.ColorObject("white"),
        verticalChildrenAlign : qx.constant.Layout.ALIGN_TOP
      }
    },
    
    state : function(vTheme, vStates) {
      return {
        border : vStates.rwt_BORDER ? this.border : this.border_default
      }
    }
  },

  "tree-folder-container" : {
    initial : function(vTheme) {
      return {
        height : qx.constant.Core.AUTO,
        verticalChildrenAlign : qx.constant.Layout.ALIGN_TOP
      }
    }
  },







  /*
  ---------------------------------------------------------------------------
    LISTVIEW
  ---------------------------------------------------------------------------
   */

  "list-view" : {
    initial : function(vTheme) {
      return {
        cursor : qx.constant.Core.DEFAULT,
        overflow: qx.constant.Style.OVERFLOW_HIDDEN
      }
    }
  },

  "list-view-pane" : {
    initial : function(vTheme) {
      return {
        width : qx.constant.Core.FLEX,
        horizontalSpacing : 1,
        overflow : qx.constant.Style.OVERFLOW_HIDDEN
      }
    }
  },

  "list-view-header" : {
    setup : function() {
      this.border = new qx.renderer.border.Border;
      this.border.setBottom(1, "solid", "#e2e2e2");

      this.bgcolor = new qx.renderer.color.ColorObject("#f2f2f2");
    },

    initial : function(vTheme) {
      return {
        height : qx.constant.Core.AUTO,
        overflow: qx.constant.Style.OVERFLOW_HIDDEN,
        border : this.border,
        backgroundColor : this.bgcolor
      }
    }
  },

  "list-view-header-cell" : {
    setup : function() {
      this.border_hover = new qx.renderer.border.Border;
      this.border_hover.setBottom(2, "solid", "#F9B119");

      this.bgcolor_hover = new qx.renderer.color.Color("white");
    },

    initial : function(vTheme) {
      return {
        overflow : qx.constant.Style.OVERFLOW_HIDDEN,
        paddingTop : 2,
        paddingRight : 6,
        paddingBottom : 2,
        paddingLeft : 6,
        spacing : 4
      };
    },

    state : function(vTheme, vStates) {
      if (vStates.over) {
        return {
          backgroundColor : this.bgcolor_hover,
          paddingBottom : 0,
          border : this.border_hover
        };
      }
      else {
        return {
          backgroundColor : null,
          paddingBottom : 2,
          border : null
        };
      }
    }
  },

  "list-view-header-separator" : {
    setup : function() {
      this.bgcolor = new qx.renderer.color.ColorObject("#D6D5D9");
    },

    initial : function(vTheme) {
      return {
        backgroundColor : this.bgcolor,
        width : 1,
        marginTop : 1,
        marginBottom : 1
      };
    }
  },

  "list-view-content-cell" : {
    setup : function() {
      this.bgcolor_selected = new qx.renderer.color.ColorObject("#316ac5");
      this.color_selected = new qx.renderer.color.ColorObject("highlighttext");
    },

    state : function(vTheme, vStates) {
      return {
        backgroundColor : vStates.selected ? this.bgcolor_selected : null,
        color : vStates.selected ? this.color_selected : null
      };
    }
  },

  "list-view-content-cell-image" : {
    initial : function(vTheme) {
      return {
        paddingLeft : 6,
        paddingRight : 6
      };
    },

    state : function(vTheme, vStates) {
      return vTheme.stateFrom("list-view-content-cell", vStates);
    }
  },

  "list-view-content-cell-text" : {
    initial : function(vTheme) {
      return qx.lang.Object.mergeWith(vTheme.initialFrom("htmlcontainer"), {
        overflow: qx.constant.Style.OVERFLOW_HIDDEN,
        paddingLeft : 6,
        paddingRight : 6
      });
    },

    state : function(vTheme, vStates) {
      return qx.lang.Object.mergeWith(vTheme.stateFrom("htmlcontainer", vStates), vTheme.stateFrom("list-view-content-cell", vStates));
    }
  },

  "list-view-content-cell-html" : {
    initial : function(vTheme) {
      return vTheme.initialFrom("list-view-content-cell-text");
    },

    state : function(vTheme, vStates) {
      return vTheme.stateFrom("list-view-content-cell-text", vStates);
    }
  },

  "list-view-content-cell-icon-html" : {
    initial : function(vTheme) {
      return vTheme.initialFrom("list-view-content-cell-text");
    },

    state : function(vTheme, vStates) {
      return vTheme.stateFrom("list-view-content-cell-text", vStates);
    }
  },

  "list-view-content-cell-link" : {
    initial : function(vTheme) {
      return vTheme.initialFrom("list-view-content-cell-text");
    },

    state : function(vTheme, vStates) {
      return vTheme.stateFrom("list-view-content-cell-text", vStates);
    }
  },







  /*
  ---------------------------------------------------------------------------
    TABVIEW
  ---------------------------------------------------------------------------
   */

  "tab-view" : {
    setup : function() {
      this.border_default = qx.renderer.border.BorderPresets.getInstance().none;
      this.border = qx.renderer.border.BorderPresets.getInstance().inset;
    },
    initial : function(vTheme) {
      return {
        hideFocus : true,
        spacing : -1
      };
    },
    state: function( vTheme, vStates ) {
      return {
        border : vStates.rwt_BORDER ? this.border : this.border_default
      }
    }
  },

  "tab-view-bar" : {
    initial : function(vTheme) {
      return {
        height : "auto"
      };
    }
  },

  "tab-view-pane" : {
    setup : function() {
      this.border = new qx.renderer.border.Border(1, "solid", "#aca899");
    },

    initial : function(vTheme) {
      return {
        height : "1*",
        border : this.border,
        paddingTop : 10,
        paddingRight : 10,
        paddingBottom : 10,
        paddingLeft : 10
      };
    }
  },

  "tab-view-page" : {
    initial : function(vTheme) {
      return {
        top : 0,
        right : 0,
        bottom : 0,
        left : 0
      };
    }
  },

  "tab-view-button" : {
    setup : function() {
      this.border_top_normal = new qx.renderer.border.Border(1, "solid", "#aca899");
      this.border_top_normal.setBottomWidth(0);

      this.border_top_checked = new qx.renderer.border.Border(1, "solid", "#aca899");
      this.border_top_checked.setBottomWidth(0);
      this.border_top_checked.setTop(3, "solid", "#fec83c");

      this.border_bottom_normal = new qx.renderer.border.Border(1, "solid", "#aca899");
      this.border_bottom_normal.setTopWidth(0);

      this.border_bottom_checked = new qx.renderer.border.Border(1, "solid", "#aca899");
      this.border_bottom_checked.setTopWidth(0);
      this.border_bottom_checked.setBottom(3, "solid", "#fec83c");
    },

    initial : function(vTheme) {
      return vTheme.initialFrom("atom");
    },

    state : function(vTheme, vStates) {
      var vReturn;

      if (vStates.checked) {
        vReturn = {
          zIndex : 1,
          paddingTop : 2,
          paddingBottom : 4,
          paddingLeft : 7,
          paddingRight : 8,
          border : vStates.barTop ? this.border_top_checked : this.border_bottom_checked,
          marginTop : 0,
          marginBottom : 0,
          marginRight : -1,
          marginLeft : -2
        }

        if (vStates.alignLeft) {
          if (vStates.firstChild) {
            vReturn.paddingLeft = 6;
            vReturn.paddingRight = 7;
            vReturn.marginLeft = 0;
          }
        }
        else {
          if (vStates.lastChild) {
            vReturn.paddingLeft = 8;
            vReturn.paddingRight = 5;
            vReturn.marginRight = 0;
          }
        }
      }
      else {
        vReturn = {
          zIndex : 0,
          paddingTop : 2,
          paddingBottom : 2,
          paddingLeft : 5,
          paddingRight : 6,
          marginRight : 1,
          marginLeft : 0
        }

        if (vStates.alignLeft) {
          if (vStates.firstChild) {
            vReturn.paddingLeft = 6;
            vReturn.paddingRight = 5;
          }
        }
        else {
          if (vStates.lastChild) {
            vReturn.paddingLeft = 6;
            vReturn.paddingRight = 5;
            vReturn.marginRight = 0;
          }
        }

        if (vStates.barTop) {
          vReturn.border = this.border_top_normal;
          vReturn.marginTop = 3;
          vReturn.marginBottom = 1;
        }
        else {
          vReturn.border = this.border_bottom_normal;
          vReturn.marginTop = 1;
          vReturn.marginBottom = 3;
        }
      }

      return vReturn;
    }
  },



  /*
  ---------------------------------------------------------------------------
    FIELDSET
  ---------------------------------------------------------------------------
   */

  "field-set" : {
    setup : function() {
      this.bgcolor = new qx.renderer.color.ColorObject( "#f8f8ff" );
    },
    initial : function(vTheme) {
      return {
        backgroundColor : this.bgcolor
      }
    }
  },

  "field-set-legend" : {
    setup : function() {
      this.bgcolor = new qx.renderer.color.ColorObject( "#f8f8ff" );
      this.color_disabled = org.eclipse.rap.rwt.DefaultAppearanceTheme.colorGrayText;
    },
    initial : function(vTheme) {
      return qx.lang.Object.mergeWith(vTheme.initialFrom( "atom" ), {
        top : 1,
        left : 10,
        backgroundColor : this.bgcolor,
        paddingRight : 3,
        paddingLeft : 4
      } );
    },
    state : function(vTheme, vStates) {
      color : vStates.disabled ? this.color_disabled : null;
    }
  },

  "field-set-frame" : {
    setup : function() {
      // TODO [rst] share border objects
      this.border
        = new qx.renderer.border.Border( 1,
                                         qx.constant.Style.BORDER_SOLID,
                                         "#aca899" );
    },
    initial : function(vTheme) {
      return {
        top : 8,
        left : 2,
        right : 2,
        bottom : 2,
        border : this.border
      }
    }
  },

  "check-box-field-set-legend" : {
    setup : function() {
      this.bgcolor = new qx.renderer.color.ColorObject("#ece9d8");
    },

    initial : function(vTheme) {
      return qx.lang.Object.mergeWith(vTheme.initialFrom("atom"), {
        top : 1,
        left : 10,
        backgroundColor : this.bgcolor,
        paddingRight : 3
      });
    }
  },

  "radio-button-field-set-legend" : {
    initial : function(vTheme) {
      return vTheme.initialFrom("check-box-field-set-legend");
    }
  },







  /*
  ---------------------------------------------------------------------------
    SPINNER
  ---------------------------------------------------------------------------
   */

  "spinner" : {
    setup : function() {
      this.bgcolor = new qx.renderer.color.Color("white");
    },

    initial : function(vTheme) {
      return {
        backgroundColor : this.bgcolor
      }
    },
    state : function( vTheme, vStates ) {
      return {
        border : vStates.rwt_BORDER
               ? qx.renderer.border.BorderPresets.getInstance().inset 
               : qx.renderer.border.BorderPresets.getInstance().none
      }
    }
  },

  "spinner-field" : {
    initial : function(vTheme) {
      return qx.lang.Object.mergeWith(vTheme.initialFrom("text-field"), {
        width : qx.constant.Core.FLEX,
        border : qx.renderer.border.BorderPresets.getInstance().none
      });
    },

    state : function(vTheme, vStates) {
      return vTheme.stateFrom("text-field", vStates);
    }
  },

  "spinner-button-up" : {
    initial : function(vTheme) {
      return qx.lang.Object.mergeWith(vTheme.initialFrom("image"), {
        height: qx.constant.Core.FLEX,
        width: 16,
        backgroundColor: new qx.renderer.color.ColorObject("#ece9d8")
      });
    },
    state : function(vTheme, vStates) {
      var vReturn = qx.lang.Object.mergeWith(vTheme.stateFrom("button", vStates), {
        paddingTop : 0,
        paddingRight : 0,
        paddingBottom: 0,
        paddingLeft : 3
      } );
      if( vStates.rwt_FLAT ) {
        vReturn.border = qx.renderer.border.BorderPresets.getInstance().none;
      }
      return vReturn;
    }
  },

  "spinner-button-down" : {
    initial : function(vTheme) {
      return qx.lang.Object.mergeWith(vTheme.initialFrom("image"), {
        height: qx.constant.Core.FLEX,
        width: 16,
        backgroundColor: new qx.renderer.color.ColorObject("#ece9d8")
      });
    },
    state : function(vTheme, vStates) {
      var vReturn = qx.lang.Object.mergeWith(vTheme.stateFrom("button", vStates), {
        paddingTop : 0,
        paddingRight : 0,
        paddingBottom: 0,
        paddingLeft : 3
      } );
      if( vStates.rwt_FLAT ) {
        vReturn.border = qx.renderer.border.BorderPresets.getInstance().none;
      }
      return vReturn;
    }
  },

  /*
  ---------------------------------------------------------------------------
    COLORSELECTOR
  ---------------------------------------------------------------------------
   */

  "colorselector" : {
    setup : function() {
      this.border = qx.renderer.border.BorderPresets.getInstance().outset;
    },

    initial : function(vTheme) {
      return {
        border : this.border,
        width: qx.constant.Core.AUTO,
        height: qx.constant.Core.AUTO
      }
    },

    state : function(vTheme, vStates) {

    }
  },

  /*
  ---------------------------------------------------------------------------
    TABLE
  ---------------------------------------------------------------------------
   */

  "table" : {
    setup: function() {
      this.font = new qx.renderer.font.Font( 11, org.eclipse.rap.rwt.DefaultAppearanceTheme.systemFontName );
    },
    
    initial: function( vTheme ) {
      return {
        backgroundColor: new qx.renderer.color.ColorObject("white"),
        font: this.font        
      }
    },

    state: function( vTheme, vStates ) {
      return {
        border : vStates.rwt_BORDER 
          ? qx.renderer.border.BorderPresets.getInstance().inset 
          : qx.renderer.border.BorderPresets.getInstance().none
      }
    }
  },
  
  "table-column" : {
    setup: function() {
      this.border = new qx.renderer.border.Border;
      this.border.set({ rightColor:"#d6d2c2", rightStyle :qx.constant.Style.BORDER_SOLID, rightWidth:1,
      bottomColor:"#d6d2c2", bottomStyle :qx.constant.Style.BORDER_SOLID, bottomWidth:2 });

      this.mouseOverBorder = new qx.renderer.border.Border;
      this.mouseOverBorder.set({ rightColor:"#d6d2c2", rightStyle :qx.constant.Style.BORDER_SOLID, rightWidth:1,
      bottomColor:"#F9B119", bottomStyle :qx.constant.Style.BORDER_SOLID, bottomWidth:2 });

      this.background = new qx.renderer.color.ColorObject( "#f8f8ff" );
      this.mouseOverBackground = new qx.renderer.color.ColorObject( "white" );
    },

    initial: function( vTheme ) {
      return {
        cursor: qx.constant.Core.DEFAULT,
        border: this.border,
        paddingLeft: 2,
        paddingRight: 2,
        spacing: 2,
        selectable: false,
        backgroundColor: this.background
      }
    },

    state: function( vTheme, vStates ) {
      var vResult;
      if( vStates.disabled ) {
        vResult = {
          backgroundColor : this.background,
          border : this.border
        }
      } else {
        vResult = {
          backgroundColor : vStates.mouseover ? this.mouseOverBackground : this.background,
          border : vStates.mouseover ? this.mouseOverBorder : this.border
        }
      }
      return vResult;
    }
  },

  /*
  ---------------------------------------------------------------------------
    SPLITPANE
  ---------------------------------------------------------------------------
   */

  "splitpane" :
  {
  
    setup : function() {
      this.border_none = qx.renderer.border.BorderPresets.getInstance().none;
      this.border_inset = qx.renderer.border.BorderPresets.getInstance().inset;
    },
    initial : function(vTheme)
    {
      return {
        overflow : "hidden"
      }
    },
    state : function(vTheme, vStates) {
      return {
        border: vStates.rwt_BORDER ? this.border_inset : this.border_none
      }
    }
  },

  "splitpane-glasspane" :
  {
    setup : function() {
      this.background = new qx.renderer.color.ColorObject("threedshadow");
    },

    initial : function(vTheme)
    {
      return {
        zIndex : 1e7,
        backgroundColor : this.background
      }
    },

    state : function(vTheme, vStates) {
      return {
        opacity : vStates.visible ? 0.2 : 0
      }
    }
  },

  "splitpane-splitter" :
  {
    state : function(vTheme, vStates)
    {
      return {
        cursor : vStates.disabled
                   ? null
                   : vStates.horizontal
                     ? "col-resize"
                     : "row-resize"
      };
    }
  },

  "splitpane-slider" :
  {
    initial : function(vTheme)
    {
      return {
        opacity: 0.5,
        zIndex : 1e8
      }
    },

    state : function(vTheme, vStates)
    {
      return {
        backgroundColor: vStates.dragging ? "threeddarkshadow" : null
      }
    }
  },

  "splitpane-knob" :
  {
    state : function(vTheme, vStates)
    {
      var vReturn = {
        opacity: vStates.dragging ? 0.5 : 1.0
      }

      if (vStates.horizontal)
      {
        vReturn.top = "33%";
        vReturn.left = null;
        vReturn.marginLeft = -6;
        vReturn.marginTop = 0;
        vReturn.cursor = "col-resize";
      }
      else if (vStates.vertical)
      {
        vReturn.top = null;
        vReturn.left = "33%";
        vReturn.marginTop = -6;
        vReturn.marginLeft = 0;
        vReturn.cursor = "row-resize";
      }

      return vReturn;
    }
  },
  
  "c-tab-item" : {
    setup : function() {
      var color = new qx.renderer.color.ColorObject( "#c0c0c0" ); ;
      var solid = qx.constant.Style.BORDER_SOLID;
      
      this.border_top = new qx.renderer.border.Border();
      this.border_top.setRight( 1, solid, color );

      this.border_top_checked = new qx.renderer.border.Border();
      this.border_top_checked.setLeft( 1, "outset" );
      this.border_top_checked.setTop( 1, "outset" );
      this.border_top_checked.setRight( 1, solid, color );
      
      this.border_bottom = new qx.renderer.border.Border();
      this.border_bottom.setLeft( 1, solid, color );
      this.border_bottom.setRight( 1, solid, color );

      this.border_bottom_checked = new qx.renderer.border.Border();
      this.border_bottom_checked.setTop( 1, solid, color );
      this.border_bottom_checked.setLeft( 1, solid, color );
      this.border_bottom_checked.setRight( 1, solid, color );
    },

    initial : function( vTheme ) {
      return qx.lang.Object.mergeWith( vTheme.initialFrom( "atom" ), {
        border : this.border_top,
        paddingLeft : 4
      } );
    },

    state : function( vTheme, vStates ) {
      var vReturn = {};
      if( vStates.checked ) {
        if ( vStates.barTop ) {
          vReturn.border = this.border_top_checked;
        } else {  // bar at bottom
          vReturn.border = this.border_bottom_checked;
        }
      } else {
        if( vStates.barTop ) {
          vReturn.border = this.border_top;
        } else {
          vReturn.border = this.border_bottom;
        }
      }
      return vReturn;
    }
  },

  "c-tab-close-button" : {
    setup : function() {
      this.background_color = null; 
      this.background_color_hover = new qx.renderer.color.ColorObject( "#00008B" ); // DarkBlue
    },

    initial : function( vTheme ) {
      return qx.lang.Object.mergeWith( vTheme.initialFrom( "image" ), {
        backgroundColor : this.background_color
      } );
    },

    state : function( vTheme, vStates ) {
      var vReturn = {
        backgroundColor : vStates.over 
                        ? this.background_color_hover
                        : this.background_color
      }
      return vReturn;
    }
  },

  "composite" :
  {
    initial : function( vTheme ) {
      return {
        hideFocus : true
      };
    },
    state : function(vTheme, vStates) {
      return {
        border: vStates.rwt_BORDER ?
          qx.renderer.border.BorderPresets.getInstance().inset :
          qx.renderer.border.BorderPresets.getInstance().none
      }
    }
  },

  "coolbar" :
  {
    state : function(vTheme, vStates) {
      return {
        border: vStates.rwt_BORDER ?
          qx.renderer.border.BorderPresets.getInstance().inset :
          qx.renderer.border.BorderPresets.getInstance().none
      }
    }
  },

  "checkbox" :
  {
    initial : function(vTheme) {
    // TODO [ralf] merge with "atom"
      return {
        cursor : qx.constant.Core.DEFAULT,
        spacing : 4,
        width : qx.constant.Core.AUTO,
        height : qx.constant.Core.AUTO,
        horizontalChildrenAlign : qx.constant.Layout.ALIGN_CENTER,
        verticalChildrenAlign : qx.constant.Layout.ALIGN_MIDDLE,
        stretchChildrenOrthogonalAxis : false,
        allowStretchY : false,
        allowStretchX : false
      }
    },
    state : function(vTheme, vStates) {
      return {
        border: vStates.rwt_BORDER ?
          qx.renderer.border.BorderPresets.getInstance().inset :
          qx.renderer.border.BorderPresets.getInstance().none
      }
    }
  },

  "radiobutton" :
  {
    initial : function(vTheme) {
    // TODO [ralf] merge with "atom"
      return {
        cursor : qx.constant.Core.DEFAULT,
        spacing : 4,
        width : qx.constant.Core.AUTO,
        height : qx.constant.Core.AUTO,
        horizontalChildrenAlign : qx.constant.Layout.ALIGN_CENTER,
        verticalChildrenAlign : qx.constant.Layout.ALIGN_MIDDLE,
        stretchChildrenOrthogonalAxis : false,
        allowStretchY : false,
        allowStretchX : false
      }
    },
    state : function(vTheme, vStates) {
      return {
        border: vStates.rwt_BORDER ?
          qx.renderer.border.BorderPresets.getInstance().inset :
          qx.renderer.border.BorderPresets.getInstance().none
      }
    }
  },

  "browser" : {
    initial: function( vTheme ) {
      return {
        border: qx.renderer.border.BorderPresets.getInstance().none,
        backgroundColor: new qx.renderer.color.ColorObject( "white" )
      }
    },
    state: function( vTheme, vStates ) {
      return {
        border: vStates.rwt_BORDER 
          ? qx.renderer.border.BorderPresets.getInstance().inset 
          : qx.renderer.border.BorderPresets.getInstance().none
      }
    }
  },

  // appearance for org.eclipse.rap.rwt.widgets.Label with style RWT.SEPARATOR
  "separator" : {
    setup: function() {
      this.color_disabled = org.eclipse.rap.rwt.DefaultAppearanceTheme.colorGrayText;
      this.border_default = qx.renderer.border.BorderPresets.getInstance().none;
      this.border = qx.renderer.border.BorderPresets.getInstance().thinInset;
    },
    initial: function( vTheme ) {
      return {
        border : this.border_default
      }
    },
    state: function( vTheme, vStates ) {
      return {
        color : vStates.disabled ? this.color_disabled : null,
        border : vStates.rwt_BORDER ? this.border : this.border_default
      }
    }
  },
  
  // -----------------------------------------------------------------
  // LINK
  
  "link" : {
    setup : function() {
      this.border_default = qx.renderer.border.BorderPresets.getInstance().none;
      this.border = qx.renderer.border.BorderPresets.getInstance().thinInset;
    },
    state : function( vTheme, vStates ) {
      return {
        border : vStates.rwt_BORDER ? this.border : this.border_default
      }
    }
  },
  
  "link-text" : {
    setup : function() {
      this.font = new qx.renderer.font.Font( 11, org.eclipse.rap.rwt.DefaultAppearanceTheme.systemFontName );
      this.color_disabled = org.eclipse.rap.rwt.DefaultAppearanceTheme.colorGrayText;
    },
    initial : function( vTheme ) {
      return {
        font : this.font
      }
    },
    state : function( vTheme, vStates ) {
      return {
        color : vStates.disabled ? this.color_disabled : null
      }
    }
  },
    
  "link-ref" : {
    setup : function() {
      this.font = new qx.renderer.font.Font( 11, org.eclipse.rap.rwt.DefaultAppearanceTheme.systemFontName );
      this.font.setUnderline( true );
      this.color_default = new qx.renderer.color.ColorObject( "#00007f" );
      this.color_disabled = org.eclipse.rap.rwt.DefaultAppearanceTheme.colorGrayText;
    },
    initial : function( vTheme ) {
      return {
        font : this.font,
        cursor : "pointer"
      }
    },
    state : function( vTheme, vStates ) {
      return {
        color : vStates.disabled ? this.color_disabled : this.color_default
      }
    }
  }

  /*
  ---------------------------------------------------------------------------
    END
  ---------------------------------------------------------------------------
   */
}, qx.Super.prototype._appearances);





/*
---------------------------------------------------------------------------
  DEFER SINGLETON INSTANCE
---------------------------------------------------------------------------
 */

/**
 * Singleton Instance Getter
 */
qx.Class.getInstance = qx.lang.Function.returnInstance;



/*
---------------------------------------------------------------------------
  REGISTER TO MANAGER
---------------------------------------------------------------------------
 */

qx.manager.object.AppearanceManager.getInstance().registerAppearanceTheme(qx.Class);
