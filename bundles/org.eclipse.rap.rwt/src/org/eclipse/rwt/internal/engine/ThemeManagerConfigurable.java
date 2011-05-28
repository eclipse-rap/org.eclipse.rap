/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import javax.servlet.ServletContext;

import org.eclipse.rwt.internal.theme.ResourceLoader;
import org.eclipse.rwt.internal.theme.Theme;
import org.eclipse.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rwt.internal.theme.css.StyleSheet;


class ThemeManagerConfigurable implements Configurable {
  static final String THEMES_PARAM = "org.eclipse.rwt.themes";

  private final ServletContext servletContext;
  
  private static class ThemeResourceLoader implements ResourceLoader {
    public InputStream getResourceAsStream( String resourceName ) throws IOException {
      // IMPORTANT: use ClassLoader#getResourceAsStream instead of 
      // Class#getResourceAsStream to retrieve resource (see respective JavaDoc)
      return getClass().getClassLoader().getResourceAsStream( resourceName );
    }
  }
  
  private static class Declaration {
    private final String themeId;
    private final String fileName;
    
    private Declaration( String[] values ) {
      themeId = values[ 0 ];
      fileName = values[ 1 ];
    }

    private String getThemeId() {
      return themeId;
    }

    private String getFileName() {
      return fileName;
    }
  }

  ThemeManagerConfigurable( ServletContext servletContext ) {
    this.servletContext = servletContext;
  }

  public void configure( ApplicationContext context ) {
    if( hasThemeConfigurations() ) {
      registerThemes( context );
    }
  }

  public void reset( ApplicationContext context ) {
    context.getThemeManager().deactivate();
  }
  
  private void registerThemes( ApplicationContext context ) {
    String[] themeDeclarations = parseThemeDeclarations();
    for( int i = 0; i < themeDeclarations.length; i++ ) {
      registerTheme( context, themeDeclarations[ i ].trim() );
    }
  }

  private void registerTheme( ApplicationContext context, String themeDeclaration ) {
    checkThemeDeclaration( themeDeclaration );
    Declaration declaration = parseDeclaration( themeDeclaration );
    Theme theme = createTheme( declaration );
    context.getThemeManager().registerTheme( theme );
  }

  private Theme createTheme( Declaration declaration ) {
    StyleSheet styleSheet = readStyleSheet( declaration );
    String themeName = "Unnamed Theme: " + declaration.getThemeId();
    return new Theme( declaration.getThemeId(), themeName, styleSheet );
  }

  private String[] parseThemeDeclarations() {
    String value = getInitParameter();
    return value.split( RWTServletContextListener.PARAMETER_SEPARATOR );
  }
  
  private boolean hasThemeConfigurations() {
    return null != getInitParameter();
  }
  
  private String getInitParameter() {
    return servletContext.getInitParameter( ThemeManagerConfigurable.THEMES_PARAM );
  }
  
  private Declaration parseDeclaration( String declarationString ) {
    return new Declaration( declarationString.split( RWTServletContextListener.PARAMETER_SPLIT ) );
  }
  
  private StyleSheet readStyleSheet( Declaration declaration ) {
    StyleSheet result;
    try {
      result = CssFileReader.readStyleSheet( declaration.getFileName(), new ThemeResourceLoader() );
    } catch( IOException ioe ) {
      String text = "Failed to register custom theme ''{0}'' from resource ''{1}''";
      Object[] args = new Object[] { declaration.getThemeId(), declaration.getFileName() };
      String msg = MessageFormat.format( text, args );
      throw new IllegalArgumentException( msg );
    }
    return result;
  }
  
  private void checkThemeDeclaration( String declarationString ) {
    String[] parts = declarationString.split( RWTServletContextListener.PARAMETER_SPLIT );
    if( parts.length != 2 ) {
      String text = "Unvalid theme declaration detected: ''{0}''.";
      Object[] args = new Object[] { declarationString };
      String msg = MessageFormat.format( text, args );
      throw new IllegalArgumentException( msg );
    }
  }
}