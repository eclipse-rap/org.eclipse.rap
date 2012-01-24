/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.jstest.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.rwt.jstest.TestContribution;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.theme.QxAppearanceWriter;
import org.eclipse.rwt.internal.theme.Theme;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.swt.internal.widgets.displaykit.ClientResourcesAdapter;


@SuppressWarnings( "restriction" )
public class RWTContribution implements TestContribution {

  private static final String APPEARANCE_NAME = "appearance.js";
  private static final String THEME_NAME = "default-theme.js";

  private static final String JSON_PARSER_NAME = "json2.js";

  public String getName() {
    return "rwt";
  }

  public String[] getResources() {
    List<String> result = new ArrayList<String>();
    String[] clientResources = ClientResourcesAdapter.getRegisteredClientResources();
    for( String resource : clientResources ) {
      result.add( resource );
    }
    result.add( JSON_PARSER_NAME );
    result.add( APPEARANCE_NAME );
    result.add( THEME_NAME );
    return toArray( result );
  }

  public InputStream getResourceAsStream( String resource ) throws IOException {
    InputStream result;
    if( APPEARANCE_NAME.equals( resource ) ) {
      String appearanceCode = getAppearanceCode();
      result = new ByteArrayInputStream( appearanceCode.getBytes( "UTF-8" ) );
    } else if( THEME_NAME.equals( resource ) ) {
      String themeCode = getThemeCode();
      result = new ByteArrayInputStream( themeCode.getBytes( "UTF-8" ) );
    } else {
      result = ClientResourcesAdapter.getResourceAsStream( resource );
    }
    return result;
  }

  private String getAppearanceCode() {
    ThemeManager themeManager = RWTFactory.getThemeManager();
    List<String> customAppearances = themeManager.getAppearances();
    return QxAppearanceWriter.createQxAppearanceTheme( customAppearances );
  }

  private String getThemeCode() {
    return "document.write( '<script src=\""
           + getThemeLocation()
           + "\" type=\"text/javascript\"></script>' );";
  }

  private String getThemeLocation() {
    ThemeManager themeManager = RWTFactory.getThemeManager();
    Theme defaultTheme = themeManager.getTheme( ThemeManager.FALLBACK_THEME_ID );
    return defaultTheme.getRegisteredLocation();
  }

  private static String[] toArray( List<String> list ) {
    String[] array = new String[ list.size() ];
    list.toArray( array );
    return array;
  }

}
