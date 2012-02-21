/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
var appearances = {
// BEGIN TEMPLATE //

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
      };
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
      result.backgroundColor = tv.getCssColor( "ExpandItem-Header", "background-color" );
      result.cursor = tv.getCssCursor( "ExpandItem-Header", "cursor" );
      result.backgroundImage = tv.getCssImage( "ExpandItem-Header", "background-image" );
      result.backgroundGradient = tv.getCssGradient( "ExpandItem-Header", "background-image" );
      result.textShadow = tv.getCssShadow( "ExpandItem-Header", "text-shadow" );
      return result;
    }
  }

// END TEMPLATE //
};
