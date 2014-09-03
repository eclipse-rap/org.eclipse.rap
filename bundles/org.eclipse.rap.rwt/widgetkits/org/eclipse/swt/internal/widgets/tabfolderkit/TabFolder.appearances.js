/*******************************************************************************
 * Copyright (c) 2007, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
/*jshint unused:false */
var appearances = {
// BEGIN TEMPLATE //

  "tab-view" : {
    style : function( states ) {
      var tv = new rwt.theme.ThemeValues( states );
      var result = {};
      result.textColor = tv.getCssColor( "*", "color" );
      result.font = tv.getCssFont( "TabFolder", "font" );
      result.spacing = -1;
      result.border = tv.getCssBorder( "TabFolder", "border" );
      return result;
    }
  },

  "tab-view-bar" : {
    style : function() {
      return {
        height : "auto"
      };
    }
  },

  "tab-view-pane" : {
    style : function( states ) {
      var tv = new rwt.theme.ThemeValues( states );
      var result = {};
      result.overflow = "hidden";
      result.backgroundColor = tv.getCssColor( "*", "background-color" );
      result.border = tv.getCssBorder( "TabFolder-ContentContainer", "border" );
      return result;
    }
  },

  "tab-item" : {
    style : function( states ) {
      var result = {};
      var tv = new rwt.theme.ThemeValues( states );
      var borderColor = tv.getCssNamedColor( "thinborder" );
      var top_color = tv.getCssColor( "TabItem", "border-top-color" );
      var bottom_color = tv.getCssColor( "TabItem", "border-bottom-color" );
      var checkedColorTop = [ top_color, borderColor, borderColor, borderColor ];
      var checkedColorBottom = [ borderColor, borderColor, bottom_color, borderColor ];
      var containerBorder = tv.getCssBorder( "TabFolder-ContentContainer", "border" );
      result.padding = tv.getCssBoxDimensions( "TabItem", "padding" );
      if( states.checked ) {
        result.zIndex = 1; // TODO [rst] Doesn't this interfere with our z-order?
        if( states.barTop ) {
          result.border = new rwt.html.Border( [ 3, 1, 0, 1 ], "solid", checkedColorTop );
          // Hack to hide the content containder border below the selected tab
          result.paddingBottom = result.padding[ 2 ] + containerBorder.getWidthTop() + 1;
        } else {
          result.border = new rwt.html.Border( [ 0, 1, 3, 1 ], "solid", checkedColorBottom );
          // Hack to hide the content containder border below the selected tab
          result.paddingTop = result.padding[ 0 ] + containerBorder.getWidthTop() + 1;
        }
      } else {
        result.zIndex = 0; // TODO [rst] Doesn't this interfere with our z-order?
        if( states.barTop ) {
          result.border = new rwt.html.Border( [ 1, 1, 0, 1 ], "solid", borderColor );
        } else {
          result.border = new rwt.html.Border( [ 0, 1, 1, 1 ], "solid", borderColor );
        }
      }
      var margin = tv.getCssBoxDimensions( "TabItem", "margin" );
      if( states.barBottom ) {
        margin = [ margin[ 2 ], margin[ 1 ], margin[ 0 ], margin[ 3 ] ];
      }
      result.margin = margin;
      result.textColor = tv.getCssColor( "TabItem", "color" );
      result.backgroundColor = tv.getCssColor( "TabItem", "background-color" );
      result.backgroundImage = tv.getCssImage( "TabItem", "background-image" );
      result.backgroundRepeat = tv.getCssIdentifier( "TabItem", "background-repeat" );
      result.backgroundPosition = tv.getCssIdentifier( "TabItem", "background-position" );
      result.backgroundGradient = tv.getCssGradient( "TabItem", "background-image" );
      result.textShadow = tv.getCssShadow( "TabItem", "text-shadow" );
      return result;
    }
  }

// END TEMPLATE //
};
