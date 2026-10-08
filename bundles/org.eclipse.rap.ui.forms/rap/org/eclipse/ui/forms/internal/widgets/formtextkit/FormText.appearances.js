/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which
 * is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

appearances = {
// BEGIN TEMPLATE //

  "formtext" : {
    style : function( states ) {
      var tv = new rwt.theme.ThemeValues( states );
      return {
        textColor       : tv.getCssColor( "FormText", "color" ),
        backgroundColor : tv.getCssColor( "FormText", "background-color" ),
        font            : tv.getCssFont( "FormText", "font" ),
        border          : tv.getCssBorder( "FormText", "border" )
      }
    }
  },

  "formtext-text" : {
    style : function( states ) {
      var tv = new rwt.theme.ThemeValues( states );
      return {
        cursor          : "default"
      }
    }
  },

  "formtext-image" : {
    style : function( states ) {
      var tv = new rwt.theme.ThemeValues( states );
      return {
      }
    }
  },

  "formtext-bullet" : {
    style : function( states ) {
      var tv = new rwt.theme.ThemeValues( states );
      return {
      }
    }
  },

  "formtext-hyperlink" : {
    style : function( states ) {
      var tv = new rwt.theme.ThemeValues( states );
      return {
        cursor          : "pointer"
      }
    }
  }

// END TEMPLATE //
};
