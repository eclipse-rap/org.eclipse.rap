/******************************************************************************* 
* Copyright (c) 2010 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.rwt.themes.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.rwt.internal.theme.AbstractThemeAdapter;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.ResourceLoader;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.rwt.internal.theme.Theme;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.internal.theme.ThemeTestUtil;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Widget;


public class ThemesTestUtil {

  static final ResourceLoader RESOURCE_LOADER
    = ThemeTestUtil.createResourceLoader( ThemesTestUtil.class );

  public static final String BUSINESS_THEME_ID 
    = "org.eclipse.rap.design.example.business.theme";
  public static final String BUSINESS_PATH = "theme/business/business.css";
  public static final String FANCY_THEME_ID 
    = "org.eclipse.rap.design.example.fancy.theme";
  public static final String FANCY_PATH = "theme/fancy/fancy.css";
  private static final String BUNDLE_ID 
    = "org.eclipse.rap.rwt.themes.test";
  
  /*
   * add theme to class path
   */
  static {
    ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
    if( systemClassLoader instanceof URLClassLoader ) {
      URLClassLoader classLoader = ( URLClassLoader ) systemClassLoader;
      // get URLs and extract path for the design folder
      URL[] urls = classLoader.getURLs();
      String path = null;
      for( int i = 0; i < urls.length && path == null; i++ ) {
        String tempPath = urls[ i ].getPath();    
        if( tempPath.indexOf( "org.eclipse.rap.design.example" ) != -1
            && tempPath.indexOf( BUNDLE_ID ) == -1 ) 
        {
          int indexOfBin = tempPath.indexOf( "bin" );
          if( indexOfBin != -1 ) {            
            String protocol = urls[ i ].getProtocol();
            path = protocol + ":" + tempPath.substring( 0, indexOfBin );
          }
        }
      }
      // add design folder to classpath    
      if( path != null ) {
        Class clazz = URLClassLoader.class;
        try {
          Class[] params = new Class[] { URL.class };
          Method method = clazz.getDeclaredMethod( "addURL", params );
          method.setAccessible( true );
          Object[] url = new Object[] { new URL( path ) };
          method.invoke( classLoader, url );
        } catch( final Throwable e ) {
          e.printStackTrace();
        } 
      }
    }
  }
  
  /**
   * Converts SWT.Color to HEX String.
   * @param color the SWT Color object
   * @return a HEX String representation without # 
   */
  public static String getHexStringFromColor( final Color color ) {
    String result = null;
    int blue = color.getBlue();
    int green = color.getGreen();
    int red = color.getRed();
    String redHex = Integer.toHexString( red );
    String greenHex = Integer.toHexString( green );
    String blueHex = Integer.toHexString( blue );
    if( red < 10 ) {
      redHex = "0" + redHex;
    } 
    if( green < 10 ) {
      greenHex = "0" + greenHex;
    }
    if( blue < 10 ) {
      blueHex = "0" + blueHex;
    }
    result = redHex + greenHex + blueHex;
    return result;
  }
  
  /*
   * Little Helper Method to get a QxType for a widget's property
   */
  public static QxType getCssValue( final Widget widget,
                                    final SimpleSelector selector,
                                    final String property )
  {
    String primaryElement = AbstractThemeAdapter.getPrimaryElement( widget );
    QxType cssValue = ThemeUtil.getCssValue( primaryElement, 
                                             property, 
                                             selector );
    return cssValue;
  }
  
  /*
   * Little Helper Method to get a QxType for a widget's property
   */
  public static QxType getCssValueForElement( final Widget widget,
                                              final SimpleSelector selector,
                                              final String property,
                                              final String element )
  {
    return ThemeUtil.getCssValue( element, property, selector );
  }
  
  /*
   * registers (if not already registered) and active a given theme
   * TODO [rst] Rather use #createAndActivateTheme as this is more specific
   */
  public static void activateTheme( final String id, final String path ) {
    ThemeManager themeManager = ThemeManager.getInstance();
    if( themeManager.getTheme( id ) == null ) {
      try {
        StyleSheet styleSheet
          = CssFileReader.readStyleSheet( path, RESOURCE_LOADER );
        Theme theme = new Theme( id, "Test Theme", styleSheet );
        themeManager.registerTheme( theme );
      } catch( IOException e ) {
        e.printStackTrace();
      }  
    }
    ThemeUtil.setCurrentThemeId( id );  
  }

  public static void createAndActivateTheme( final String path ) {
    ThemeManager themeManager = ThemeManager.getInstance();
    String id = "test.theme.id";
    StyleSheet styleSheet;
    try {
      styleSheet = CssFileReader.readStyleSheet( path, RESOURCE_LOADER );
    } catch( IOException e ) {
      throw new RuntimeException( "Failed to read stylesheet from " + path, e );
    }
    Theme theme = new Theme( id, "Test Theme", styleSheet );
    themeManager.registerTheme( theme );
    ThemeUtil.setCurrentThemeId( id );  
  }
  
}
