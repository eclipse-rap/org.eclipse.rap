/*******************************************************************************
 * Copyright (c) 2007, 2016 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
/*jshint unused:false */
var appearances = {
// BEGIN TEMPLATE //

 "link" : {
    style : function( states ) {
      var tv = new rwt.theme.ThemeValues( states );
      return {
        cursor : "default",
        padding : tv.getCssBoxDimensions( "Link", "padding" ),
        font : tv.getCssFont( "Link", "font" ),
        border : tv.getCssBorder( "Link", "border" ),
        textColor : tv.getCssColor( "Link", "color" ),
        backgroundColor : tv.getCssColor( "Link", "background-color" ),
        backgroundImage : tv.getCssImage( "Link", "background-image" ),
        backgroundRepeat : tv.getCssIdentifier( "Link", "background-repeat" ),
        backgroundPosition : tv.getCssIdentifier( "Link", "background-position" ),
        textShadow : tv.getCssShadow( "Link", "text-shadow" )
      };
    }
  },

  "link-hyperlink" : {
    style : function( states ) {
      var tv = new rwt.theme.ThemeValues( states );
      return {
        textColor : tv.getCssColor( "Link-Hyperlink", "color" ),
        textShadow : tv.getCssShadow( "Link-Hyperlink", "text-shadow" ),
        textDecoration : tv.getCssIdentifier( "Link-Hyperlink", "text-decoration" ),
        cursor : states.disabled ? "default" : "pointer"
      };
    }
  }

// END TEMPLATE //
};
