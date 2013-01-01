/*******************************************************************************
 * Copyright (c) 2008, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rap.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.engine.ThemeManagerHelper;
import org.w3c.css.sac.CSSException;


public final class ThemeTestUtil {

  public static final ResourceLoader RESOURCE_LOADER = createResourceLoader( Fixture.class );

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
    return createStyleSheet( css, RESOURCE_LOADER );
  }

  public static StyleSheet createStyleSheet( String css, ResourceLoader loader )
    throws CSSException, IOException
  {
    StyleSheet result = null;
    byte[] bytes = css.getBytes( "UTF-8" );
    InputStream inStream = new ByteArrayInputStream( bytes );
    try {
      result = CssFileReader.readStyleSheet( inStream, "css", loader );
    } finally {
      inStream.close();
    }
    return result;
  }

  public static void setCustomTheme( String css ) throws IOException {
    registerTheme( "customTestTheme", css, null );
    ThemeUtil.setCurrentThemeId( ContextProvider.getUISession(), "customTestTheme" );
  }

  public static void registerTheme( String themeId, String cssCode, ResourceLoader loader )
    throws IOException
  {
    registerTheme( createTheme( themeId, cssCode, loader ) );
  }

  public static void registerTheme( Theme theme ) {
    ThemeManagerHelper.resetThemeManager();
    ThemeManager manager = getApplicationContext().getThemeManager();
    manager.initialize();
    manager.registerTheme( theme );
    manager.activate();
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

  public static void setCurrentThemeId( String themeId ) {
    ThemeUtil.setCurrentThemeId( ContextProvider.getUISession(), themeId );
  }
}
