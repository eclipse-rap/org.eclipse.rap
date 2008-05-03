/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.rwt.internal.theme.css.*;
import org.w3c.css.sac.CSSException;


/**
 * Experimental adapter for the old theming backend to css frontend.
 */
public final class ThemeCssAdapter {

  public static Theme loadThemeFromCssFile( final String name,
                                            final Theme defaultTheme,
                                            final InputStream inputStream,
                                            final ResourceLoader loader,
                                            final String uri,
                                            final ThemeProperty[] properties )
    throws CSSException, IOException
  {
    Theme result = new Theme( name, defaultTheme );
    CssFileReader reader = new CssFileReader();
    StyleSheet styleSheet = reader.parse( inputStream, uri );

    for( int i = 0; i < properties.length; i++ ) {
      ThemeProperty property = properties[ i ];
      if( property.cssElements.length > 0 && property.cssProperty != null ) {
        String[] variants = new String[ 0 ];
        for( int j = 0; j < property.cssElements.length; j++ ) {
          String[] newVar = styleSheet.getVariants( property.cssElements[ 0 ] );
          if( newVar.length > 0 ) {
            String[] oldVar = variants;
            variants = new String[ variants.length + newVar.length ];
            System.arraycopy( oldVar, 0, variants, 0, oldVar.length );
            System.arraycopy( newVar, 0, variants, oldVar.length, newVar.length );
          }
        }
        StylableElement element = createDummyElement( property, null );
        QxType value
          = styleSheet.getValue( property.cssProperty, element, loader );
        if( value != null ) {
//          System.out.println( property.name + " := " + value );
          result.setValue( property.name, value );
//        } else {
//          System.out.println( property.name + " not found" );
        }
        for( int j = 0; j < variants.length; j++ ) {
          String variant = variants[ j ];
          StylableElement vElement = createDummyElement( property, variant );
          QxType vValue
            = styleSheet.getValue( property.cssProperty, vElement, loader );
          if( vValue != null && !vValue.equals( value ) ) {
//            System.out.println( variant + "/" + property.name + " := " + vValue );
            result.setValue( property.name, variant, vValue );
          }
        }
      } else {
        System.err.println( "Property without CSS support: " + property.name );
      }
    }
    return result;
  }

  private static StylableElement createDummyElement( final ThemeProperty property,
                                                     final String variant )
  {
    StylableElement result = new StylableElement( property.cssElements[ 0 ] );
    if( property.cssSelectors.length > 0 ) {
      String selector = property.cssSelectors[ 0 ];
      Pattern pattern = Pattern.compile( "\\[([A-Z]+)\\]|:([a-z]+)|.+" );
      Matcher matcher = pattern.matcher( selector );
      while( matcher.find() ) {
        String style = matcher.group( 1 );
        String state = matcher.group( 2 );
        if( style != null ) {
          result.setAttribute( style );
        } else if( state != null ) {
          result.setPseudoClass( state );
        } else {
          System.err.println( "Garbage found in css-selectors attribute: "
                              + matcher.group() );
        }
      }
    }
    if( variant != null ) {
      result.setClass( variant );
    }
    return result;
  }
}
