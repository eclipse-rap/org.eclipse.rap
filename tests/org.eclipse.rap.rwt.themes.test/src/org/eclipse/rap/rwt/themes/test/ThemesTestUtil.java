/*******************************************************************************
* Copyright (c) 2010, 2011 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.themes.test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.rap.rwt.testfixture.internal.engine.ThemeManagerHelper;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.theme.Theme;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rwt.resources.ResourceLoader;


@SuppressWarnings("restriction")
public class ThemesTestUtil {

  public static final String CLASSIC_PATH = "theme/classic.css";

  public static final String BUSINESS_PATH = "theme/business/business.css";

  public static final String FANCY_PATH = "theme/fancy/fancy.css";

  public static final String DEFAULT_PREFIX = "org/eclipse/swt/internal/";

  static final ResourceLoader RESOURCE_LOADER = getDefaultResourceLoader();

  private static final String BUNDLE_ID = "org.eclipse.rap.rwt.themes.test";

  /*
   * add theme to class path
   */
  static {
    addBundleToClassPath( "org.eclipse.rap.design.example" );
  }

  private static void addBundleToClassPath( String bundleId ) {
    ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
    if( systemClassLoader instanceof URLClassLoader ) {
      URLClassLoader classLoader = ( URLClassLoader ) systemClassLoader;
      String path = getBundlePath( classLoader, bundleId );
      addFolderToClassPath( classLoader, path );
    }
  }

  private static String getBundlePath( URLClassLoader classLoader, String bundleId ) {
    URL[] urls = classLoader.getURLs();
    String path = null;
    for( int i = 0; i < urls.length && path == null; i++ ) {
      String tempPath = urls[ i ].getPath();
      if( tempPath.contains( bundleId )
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

  private static void addFolderToClassPath( URLClassLoader classLoader, String path ) {
    if( path != null ) {
      Class<?> clazz = URLClassLoader.class;
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

  public static void createAndActivateTheme( String path, String themeId ) {
    StyleSheet styleSheet;
    try {
      styleSheet = CssFileReader.readStyleSheet( path, RESOURCE_LOADER );
    } catch( final IOException e ) {
      throw new RuntimeException( "Failed to read stylesheet from " + path, e );
    }
    Theme theme = new Theme( themeId, "Test Theme", styleSheet );
    ThemeManagerHelper.resetThemeManager();
    ThemeManager themeManager = RWTFactory.getThemeManager();
    themeManager.registerTheme( theme );
    themeManager.activate();
    ThemeUtil.setCurrentThemeId( themeId );
  }

  public static void cleanupThemes() {
    ThemeManager themeManager = RWTFactory.getThemeManager();
    themeManager.deactivate();
  }

  private static ResourceLoader getDefaultResourceLoader() {
    ResourceLoader result;
    try {
      String name = "STANDARD_RESOURCE_LOADER";
      Field field = ThemeManager.class.getDeclaredField( name );
      field.setAccessible( true );
      result = ( ResourceLoader )field.get( null );
    } catch( Exception e ) {
      throw new RuntimeException( "Failed to obtain default resource loader" );
    }
    return result;
  }
}
