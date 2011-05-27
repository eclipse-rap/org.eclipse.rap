/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.io.*;

import org.eclipse.rwt.internal.engine.ThemeManagerHelper;
import org.eclipse.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.w3c.css.sac.CSSException;


public final class ThemeTestUtil {

  private ThemeTestUtil() {
    // prevent instantiation
  }

  public static ResourceLoader createResourceLoader( Class clazz ) {
    final ClassLoader classLoader = clazz.getClassLoader();
    return new ResourceLoader() {
      public InputStream getResourceAsStream( String resourceName ) throws IOException {
        return classLoader.getResourceAsStream( resourceName );
      }
    };
  }

  public static StyleSheet getStyleSheet( String fileName ) throws CSSException, IOException {
    StyleSheet result = null;
    ClassLoader classLoader = ThemeTestUtil.class.getClassLoader();
    InputStream inStream = classLoader.getResourceAsStream( "resources/theme/" + fileName );
    if( inStream != null ) {
      try {
        result = CssFileReader.readStyleSheet( inStream, fileName, null );
      } finally {
        inStream.close();
      }
    }
    return result;
  }

  public static StyleSheet createStyleSheet( String css ) throws CSSException, IOException {
    StyleSheet result = null;
    byte[] bytes = css.getBytes( "UTF-8" );
    InputStream inStream = new ByteArrayInputStream( bytes );
    try {
      result = CssFileReader.readStyleSheet( inStream, "css", null );
    } finally {
      inStream.close();
    }
    return result;
  }

  public static void registerCustomTheme( String themeId, String cssCode, ResourceLoader loader )
    throws IOException
  {
    ThemeManagerHelper.resetThemeManager();
    Theme theme = createTheme( themeId, cssCode, loader );
    ThemeManager manager = ThemeManager.getInstance();
    manager.registerTheme( theme );
    manager.initialize();
  }

  public static Theme createTheme( String themeId, String cssCode, ResourceLoader loader )
    throws IOException
  {
    String cssFileName = themeId + ".css";
    byte[] buf = cssCode.getBytes( "UTF-8" );
    ByteArrayInputStream inStream = new ByteArrayInputStream( buf );
    StyleSheet styleSheet = CssFileReader.readStyleSheet( inStream, cssFileName, loader );
    return new Theme( themeId, "Custom Theme", styleSheet );
  }
}