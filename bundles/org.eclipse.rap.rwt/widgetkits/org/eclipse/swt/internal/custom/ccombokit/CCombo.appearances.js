/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
appearances = {
// BEGIN TEMPLATE //

  "ccombo" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "CCombo", "border" );
      result.backgroundColor = tv.getCssColor( "CCombo", "background-color" );
      result.backgroundGradient = tv.getCssGradient( "CCombo", "background-image" );
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
      result.textColor = tv.getCssColor( "CCombo", "color" );
      result.font = tv.getCssFont( "CCombo", "font" );
      result.backgroundColor = tv.getCssColor( "CCombo", "background-color" );
      result.shadow = tv.getCssShadow( "CCombo-List", "box-shadow" );
      result.textShadow = tv.getCssShadow( "CCombo", "text-shadow" );
      return result;
    }
  },

  "ccombo-field" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.font = tv.getCssFont( "CCombo", "font" );
      // [if] Do not apply top/bottom paddings on the client
      var cssPadding = tv.getCssBoxDimensions( "CCombo-Field", "padding" );
      result.paddingRight = cssPadding[ 1 ];
      result.paddingLeft = cssPadding[ 3 ];
      result.width = null;
      result.height = null;
      result.left = 0;
      result.right = tv.getCssDimension( "CCombo-Button", "width" );
      result.top = 0;
      result.bottom = 0;
      result.textColor = tv.getCssColor( "CCombo", "color" );
      result.textShadow = tv.getCssShadow( "CCombo", "text-shadow" );
      return result;
    }
  },

  "ccombo-button" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      var border = tv.getCssBorder( "CCombo-Button", "border" );
      var borderLeft = tv.getCssBorder( "CCombo-Button", "border-left" );
      result.border = org.eclipse.rwt.Border.mergeBorders( border, null, null, null, borderLeft );
      result.width = tv.getCssDimension( "CCombo-Button", "width" );
      result.height = null;
      result.top = 0;
      result.bottom = 0;
      result.right = 0;
      result.icon = tv.getCssImage( "CCombo-Button-Icon", "background-image" );
      if( result.icon === org.eclipse.swt.theme.ThemeValues.NONE_IMAGE ) {
        result.icon = tv.getCssImage( "CCombo-Button", "background-image" );
      } else {
        result.backgroundImage = tv.getCssImage( "CCombo-Button", "background-image" );
      }
      result.backgroundGradient = tv.getCssGradient( "CCombo-Button", "background-image" );
      // TODO [rst] rather use button.bgcolor?
      result.backgroundColor = tv.getCssColor( "CCombo-Button", "background-color" );
      result.cursor = tv.getCssCursor( "CCombo-Button", "cursor" );
      return result;
    }
  }

// END TEMPLATE //
};