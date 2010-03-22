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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
  
  static final ResourceLoader RESOURCE_LOADER = createResourceLoader();

  private static final String BUNDLE_ID = "org.eclipse.rap.rwt.themes.test";

  private static ResourceLoader createResourceLoader() {
    final String designBundleUrl = findDesignBundleUrl();
    ResourceLoader resourceLoader = new ResourceLoader() {

      public InputStream getResourceAsStream( String resourceName )
        throws IOException
      {
        URL url = new URL( designBundleUrl + "/" + resourceName );
        InputStream inputStream = url.openStream();
        return inputStream;
      }
    };
    return resourceLoader;
  }

  private static String findDesignBundleUrl() {
    String result = null;
    ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
    URLClassLoader classLoader = ( URLClassLoader ) systemClassLoader;
    // get URLs and extract path for the design folder
    URL[] urls = classLoader.getURLs();
    for( int i = 0; i < urls.length && result == null; i++ ) {
      String tempPath = urls[ i ].getPath();
      if( tempPath.indexOf( "org.eclipse.rap.design.example" ) != -1 ) {
        int indexOfBin = tempPath.indexOf( "/bin" );
        if( indexOfBin != -1 ) {
          String protocol = urls[ i ].getProtocol();
          result = protocol + ":" + tempPath.substring( 0, indexOfBin );
        }
      }
    }
    if( result == null ) {
      throw new RuntimeException( "Bundle not found" );
    }
    return result;
  }

  public static void createAndActivateTheme( final String path, 
                                             final String themeId ) 
  {
    ThemeManager themeManager = ThemeManager.getInstance();
    StyleSheet styleSheet;
    try {
      styleSheet = CssFileReader.readStyleSheet( path, RESOURCE_LOADER );
    } catch( IOException e ) {
      throw new RuntimeException( "Failed to read stylesheet from " + path, e );
    }
    Theme theme = new Theme( themeId, "Test Theme", styleSheet );
    themeManager.registerTheme( theme );
    ThemeUtil.setCurrentThemeId( themeId );  
  }
  
}
