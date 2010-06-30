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

import org.eclipse.rwt.internal.theme.ResourceLoader;
import org.eclipse.rwt.internal.theme.Theme;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.internal.theme.ThemeTestUtil;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rwt.internal.theme.css.StyleSheet;


public class ThemesTestUtil {

  public static final String BUSINESS_PATH = "theme/business/business.css";

  public static final String FANCY_PATH = "theme/fancy/fancy.css";

  static final ResourceLoader RESOURCE_LOADER
    = ThemeTestUtil.createResourceLoader( ThemesTestUtil.class );

  private static final String BUNDLE_ID = "org.eclipse.rap.rwt.themes.test";

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

  public static void createAndActivateTheme( final String path, 
                                             final String themeId ) 
  {
    StyleSheet styleSheet;
    try {
      styleSheet = CssFileReader.readStyleSheet( path, RESOURCE_LOADER );
    } catch( IOException e ) {
      throw new RuntimeException( "Failed to read stylesheet from " + path, e );
    }
    Theme theme = new Theme( themeId, "Test Theme", styleSheet );
    ThemeManager.resetInstance();
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.registerTheme( theme );
    themeManager.initialize();
    ThemeUtil.setCurrentThemeId( themeId );  
  }
  
}
