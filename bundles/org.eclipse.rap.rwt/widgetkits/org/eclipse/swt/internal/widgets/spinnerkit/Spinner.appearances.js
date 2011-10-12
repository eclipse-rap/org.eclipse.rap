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

  "spinner" : {
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.font = tv.getCssFont( "Spinner", "font" );
      result.textColor = tv.getCssColor( "Spinner", "color" );
      result.backgroundColor = tv.getCssColor( "Spinner", "background-color" );
      result.border = tv.getCssBorder( "Spinner", "border" );
      result.backgroundGradient = tv.getCssGradient( "Spinner", "background-image" );
      return result;
    }
  },

  "spinner-text-field" : {
    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      // [if] Do not apply top/bottom paddings on the client
      var cssPadding = tv.getCssBoxDimensions( "Spinner-Field", "padding" );
      result.paddingRight = cssPadding[ 1 ];
      result.paddingLeft = cssPadding[ 3 ];
      result.top = 0;
      result.left = 0;
      result.right = 0;
      result.bottom = 0;
      result.textShadow = tv.getCssShadow( "Spinner", "text-shadow" );
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
        result.backgroundImage = tv.getCssImage( "Spinner-UpButton", "background-image" );
      }
      result.backgroundGradient = tv.getCssGradient( "Spinner-UpButton", "background-image" );
      result.border = tv.getCssBorder( "Spinner-UpButton", "border" );
      result.backgroundColor = tv.getCssColor( "Spinner-UpButton", "background-color" );
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
        result.backgroundImage = tv.getCssImage( "Spinner-DownButton", "background-image" );
      }
      result.backgroundGradient = tv.getCssGradient( "Spinner-DownButton", "background-image" );
      result.border = tv.getCssBorder( "Spinner-DownButton", "border" );
      result.backgroundColor = tv.getCssColor( "Spinner-DownButton", "background-color" );
      result.cursor = tv.getCssCursor( "Spinner-DownButton", "cursor" );
      return result;
    }
  }

// END TEMPLATE //
};