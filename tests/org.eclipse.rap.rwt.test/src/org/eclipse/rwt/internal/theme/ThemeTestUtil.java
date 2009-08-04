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

import java.io.*;

import org.eclipse.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.w3c.css.sac.CSSException;


/**
 * Provides static utility methods for theming tests.
 */
public final class ThemeTestUtil {

  private ThemeTestUtil() {
    // prevent instantiation
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

  public static StyleSheet getStyleSheet( final String fileName )
    throws CSSException, IOException
  {
    StyleSheet result = null;
    ClassLoader classLoader = ThemeTestUtil.class.getClassLoader();
    InputStream inStream = classLoader.getResourceAsStream( "resources/theme/"
                                                            + fileName );
    if( inStream != null ) {
      try {
        CssFileReader reader = new CssFileReader();
        result = reader.parse( inStream, fileName, null );
      } finally {
        inStream.close();
      }
    }
    return result;
  }

  public static StyleSheet createStyleSheet( final String css )
    throws CSSException, IOException
  {
    StyleSheet result = null;
    byte[] bytes = css.getBytes( "UTF-8" );
    InputStream inStream = new ByteArrayInputStream( bytes );
    if( inStream != null ) {
      try {
        CssFileReader reader = new CssFileReader();
        result = reader.parse( inStream, "css", null );
      } finally {
        inStream.close();
      }
    }
    return result;
  }

  public static void registerCustomTheme( final String themeId,
                                          final String cssCode,
                                          final ResourceLoader loader )
    throws IOException
  {
    final String cssFileName = themeId + ".css";
    ResourceLoader wrappedLoader = new ResourceLoader() {

      public InputStream getResourceAsStream( final String resourceName )
        throws IOException
      {
        InputStream result = null;
        if( cssFileName.equals( resourceName ) ) {
          byte[] buf = cssCode.getBytes( "UTF-8" );
          result = new ByteArrayInputStream( buf );
        } else {
          if( loader != null ) {
            result = loader.getResourceAsStream( resourceName );
          }
        }
        return result;
      }
    };
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    manager.registerTheme( themeId, "Custom Theme", cssFileName, wrappedLoader );
  }
}
