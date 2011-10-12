/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
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
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
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

  // this applies to a qooxdoo qx.ui.basic.Atom that represents an RWT Label
  

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
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : tv.getCssNamedBorder( "shadow" )
      };
    }
  }
}

} );
