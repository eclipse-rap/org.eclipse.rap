/*******************************************************************************
 * Copyright (c) 2012, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.jstest.internal;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.rwt.internal.resources.ClientResourcesAdapter;
import org.eclipse.rap.rwt.internal.theme.AppearanceWriter;
import org.eclipse.rap.rwt.internal.theme.ThemeManager;
import org.eclipse.rap.rwt.jstest.TestContribution;


@SuppressWarnings( "restriction" )
public class RWTContribution implements TestContribution {

  private static final String APPEARANCE_NAME = "appearance.js";
  private static final String TEST_SETTINGS_RESOURCE = "/resource/TestSettings.js";
  private static final String JSON_PARSER_RESOURCE = "json2.min.js";

  public String getName() {
    return "rwt";
  }

  public String[] getResources() {
    List<String> result = new ArrayList<String>();
    result.add( TEST_SETTINGS_RESOURCE );
    result.addAll( ClientResourcesAdapter.getRegisteredClientResources() );
    result.add( JSON_PARSER_RESOURCE );
    result.add( APPEARANCE_NAME );
    return toArray( result );
  }

  public InputStream getResourceAsStream( String resource ) throws IOException {
    InputStream result;
    if( APPEARANCE_NAME.equals( resource ) ) {
      String appearanceCode = getAppearanceCode();
      result = new ByteArrayInputStream( appearanceCode.getBytes( "UTF-8" ) );
    } else if( TEST_SETTINGS_RESOURCE.equals( resource ) ) {
      result = getClass().getResourceAsStream( resource );
    } else {
      result = ClientResourcesAdapter.getResourceAsStream( resource );
    }
    return result;
  }

  private String getAppearanceCode() {
    ThemeManager themeManager = getApplicationContext().getThemeManager();
    List<String> customAppearances = themeManager.getAppearances();
    return AppearanceWriter.createAppearanceTheme( customAppearances );
  }

  private static String[] toArray( List<String> list ) {
    String[] array = new String[ list.size() ];
    list.toArray( array );
    return array;
  }

}
