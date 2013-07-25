/*******************************************************************************
 * Copyright (c) 2007, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Rüdiger Herrmann - bug 335112
 ******************************************************************************/

var appearances = {
// BEGIN TEMPLATE //

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
        source : rwt.remote.Connection.RESOURCE_PATH + "widget/rap/cursors/move.gif"
      };
    }
  },

  "cursor-dnd-copy" : {
    style : function( states ) {
      return {
        source : rwt.remote.Connection.RESOURCE_PATH + "widget/rap/cursors/copy.gif"
      };
    }
  },

  "cursor-dnd-alias" : {
    style : function( states ) {
      return {
        source : rwt.remote.Connection.RESOURCE_PATH + "widget/rap/cursors/alias.gif"
      };
    }
  },

  "cursor-dnd-nodrop" : {
    style : function( states ) {
      return {
        source : rwt.remote.Connection.RESOURCE_PATH + "widget/rap/cursors/nodrop.gif"
      };
    }
  },

  "client-document" : {
    style : function( states ) {
      var tv = new rwt.theme.ThemeValues( states );
      return {
        font : tv.getCssFont( "*", "font" ),
        textColor : "black",
        backgroundColor : "white"
      };
    }
  },

  "client-document-blocker" : {
    style : function( states ) {
      var tv = new rwt.theme.ThemeValues( states );
      var result = {
        cursor : "default",
        animation : tv.getCssAnimation( "Shell-DisplayOverlay", "animation" ),
        backgroundColor : tv.getCssColor( "Shell-DisplayOverlay", "background-color" ),
        backgroundImage : tv.getCssImage( "Shell-DisplayOverlay", "background-image" ),
        opacity : tv.getCssFloat( "Shell-DisplayOverlay", "opacity" )
      };
      if(    result.backgroundImage == null
          && result.backgroundColor == "undefined" ) {
        // A background image or color is always needed for mshtml to
        // block the events successfully.
        result.backgroundImage = "static/image/blank.gif";
      }
      return result;
    }
  },

  "atom" : {
    style : function( states ) {
      return {
        cursor : "default",
        spacing : 4,
        width : "auto",
        height : "auto",
        horizontalChildrenAlign : "center",
        verticalChildrenAlign : "middle"
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
    }
  },

  // this applies to a qooxdoo rwt.widgets.base.Atom that represents an RWT Label


  "htmlcontainer" : {
    include : "label"
  },

  "popup" : {
  },

  "iframe" : {
    style : function( states ) {
      return { };
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
      return {};
    }
  },

  "resizer-frame" : {
    style : function( states ) {
      var tv = new rwt.theme.ThemeValues( states );
      return {
        border : tv.getCssNamedBorder( "shadow" )
      };
    }
  },

  "widget-tool-tip" : {
    include : "popup",

    style : function( states ) {
      var tv = new rwt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "Widget-ToolTip", "border" );
      result.animation = tv.getCssAnimation( "Widget-ToolTip", "animation" );
      result.padding = tv.getCssBoxDimensions( "Widget-ToolTip", "padding" );
      result.textColor = tv.getCssColor( "Widget-ToolTip", "color" );
      result.font = tv.getCssFont( "Widget-ToolTip", "font" );
      result.backgroundColor = tv.getCssColor( "Widget-ToolTip", "background-color" );
      result.backgroundImage = tv.getCssImage( "Widget-ToolTip", "background-image" );
      result.backgroundGradient = tv.getCssGradient( "Widget-ToolTip", "background-image" );
      result.opacity = tv.getCssFloat( "Widget-ToolTip", "opacity" );
      result.shadow = tv.getCssShadow( "Widget-ToolTip", "box-shadow" );
      return result;
    }
  }

// END TEMPLATE //
};
