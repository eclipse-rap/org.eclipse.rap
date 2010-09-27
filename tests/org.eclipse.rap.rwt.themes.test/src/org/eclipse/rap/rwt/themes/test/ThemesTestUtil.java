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
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.rwt.internal.theme.ResourceLoader;
import org.eclipse.rwt.internal.theme.Theme;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rwt.internal.theme.css.StyleSheet;


public class ThemesTestUtil {

  public static final String CLASSIC_PATH = "theme/classic.css";
  
  public static final String BUSINESS_PATH = "theme/business/business.css";

  public static final String FANCY_PATH = "theme/fancy/fancy.css";
  
  public static final String DEFAULT_PREFIX = "org/eclipse/swt/internal/";

  static final ResourceLoader RESOURCE_LOADER
    = createResourceLoader( ThemesTestUtil.class );

  private static final String BUNDLE_ID = "org.eclipse.rap.rwt.themes.test";

  /*
   * add theme to class path
   */
  static {
    addBundleToClassPath( "org.eclipse.rap.design.example" );
    addBundleToClassPath( "org.eclipse.rap.rwt.theme.classic" );
  }

  private static void addBundleToClassPath( final String bundleId ) {
    ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
    if( systemClassLoader instanceof URLClassLoader ) {
      URLClassLoader classLoader = ( URLClassLoader ) systemClassLoader;
      String path = getBundlePath( classLoader, bundleId );
      addFolderToClassPath( classLoader, path );
    }
  }

  private static String getBundlePath( final URLClassLoader classLoader, 
                                       final String bundleId ) 
  {
    URL[] urls = classLoader.getURLs();
    String path = null;
    for( int i = 0; i < urls.length && path == null; i++ ) {
      String tempPath = urls[ i ].getPath();    
      if( tempPath.indexOf( bundleId ) != -1
          && tempPath.indexOf( BUNDLE_ID ) == -1 ) 
      {
        int indexOfBin = tempPath.indexOf( "bin" );
        if( indexOfBin != -1 ) {            
          String protocol = urls[ i ].getProtocol();
          path = protocol + ":" + tempPath.substring( 0, indexOfBin );
        }
      }
    }
    return path;
  }

  private static void addFolderToClassPath( final URLClassLoader classLoader,
                                            final String path )
  {
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

  public static void createAndActivateTheme( final String path, 
                                             final String themeId ) 
  {
    StyleSheet styleSheet;
    try {
      styleSheet = CssFileReader.readStyleSheet( path, RESOURCE_LOADER );
    } catch( final IOException e ) {
      throw new RuntimeException( "Failed to read stylesheet from " + path, e );
    }
    Theme theme = new Theme( themeId, "Test Theme", styleSheet );
    ThemeManager.resetInstance();
    ThemeManager themeManager = ThemeManager.getInstance();
    themeManager.registerTheme( theme );
    themeManager.initialize();
    ThemeUtil.setCurrentThemeId( themeId );  
  }
  
  public static ResourceLoader createResourceLoader( final Class clazz ) {
    final ClassLoader classLoader = clazz.getClassLoader();
    ResourceLoader resLoader = new ResourceLoader() {
      public InputStream getResourceAsStream( final String resourceName )
        throws IOException
      {
        return classLoader.getResourceAsStream( resourceName );
      }
    };
    return resLoader;
  }
  
}
