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

  "button" : {
    include : "atom",

    style : function( states ) {
      // [tb] exists for compatibility with the original qooxdoo button
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
      result.opacity = tv.getCssFloat( "Button", "opacity" );
      result.textShadow = tv.getCssShadow( "Button", "text-shadow" );
      return result;
    }
  },

  "push-button" : {
    include : "button",

    style : function( states ) {
      var result = {};
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      result.animation = tv.getCssAnimation( "Button", "animation" );
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
        selectionIndicator : tv.getCssSizedImage( "Button-CheckIcon", "background-image" ),
        cursor : tv.getCssCursor( "Button", "cursor" ),
        opacity : tv.getCssFloat( "Button", "opacity" ),
        textShadow : tv.getCssShadow( "Button", "text-shadow" )
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
        selectionIndicator : tv.getCssSizedImage( "Button-RadioIcon", "background-image" ),
        cursor : tv.getCssCursor( "Button", "cursor" ),
        opacity : tv.getCssFloat( "Button", "opacity" ),
        textShadow : tv.getCssShadow( "Button", "text-shadow" )
      }
    }
  }

// END TEMPLATE //
};