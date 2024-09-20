/*******************************************************************************
 * Copyright (c) 2011, 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.resources;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.RWTProperties;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.theme.ThemeManager;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ClientResources_Test {

  private static final String RAP_CLIENT_JS_PATH = SWT.getVersion() + "/rap-client.js";

  private ClientResources clientResources;
  private ResourceManager resourceManager;
  private ThemeManager themeManager;
  private ApplicationContextImpl applicationContext;

  @Before
  public void setUp() {
    Fixture.setUp( true );
    applicationContext = getApplicationContext();
    resourceManager = applicationContext.getResourceManager();
    themeManager = applicationContext.getThemeManager();
    clientResources = new ClientResources( applicationContext );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRegisterResources() {
    clientResources.registerResources();

    assertFalse( resourceManager.isRegistered( "qx/lang/Core.js" ) );
    assertTrue( resourceManager.isRegistered( RAP_CLIENT_JS_PATH ) );
    Theme defaultTheme = themeManager.getTheme( RWT.DEFAULT_THEME_ID );
    assertTrue( resourceManager.isRegistered( "rap-" + defaultTheme.getJsId() + ".json" ) );
  }

  @Test
  public void testRegisterResourcesDebug() {
    System.setProperty( RWTProperties.DEVELOPMEMT_MODE, "true" );
    clientResources.registerResources();

    assertTrue( resourceManager.isRegistered( RAP_CLIENT_JS_PATH ) );
    assertFalse( resourceManager.isRegistered( "rwt/runtime/System.js" ) );
    Theme defaultTheme = themeManager.getTheme( RWT.DEFAULT_THEME_ID );
    assertTrue( resourceManager.isRegistered( "rap-" + defaultTheme.getJsId() + ".json" ) );
  }

  @Test
  public void testIncludesDebugSettings() throws IOException {
    System.setProperty( RWTProperties.DEVELOPMEMT_MODE, "true" );
    clientResources.registerResources();

    String content = getRegisteredContent( RAP_CLIENT_JS_PATH );

    // Pattern: .*qxvariants\W*=\W*\{\W*\"qx.debug\"\W*:\W*\"on\"\W*\};.*
    String patternString = ".*qxvariants\\W*=\\W*\\{\\W*\\\"qx.debug\\\"\\W*:\\W*\\\"on\\\"\\W*\\};.*";
    Pattern pattern = Pattern.compile(patternString, Pattern.MULTILINE);
    Matcher matcher = pattern.matcher(content);
    assertTrue( matcher.find());
  }

  @Test
  public void testRegisteredContent() throws IOException {
    System.getProperties().remove( RWTProperties.DEVELOPMEMT_MODE );
    clientResources.registerResources();
    String clientJs = getRegisteredContent( RAP_CLIENT_JS_PATH );

    assertTrue( clientJs.contains( "qx.Class.define(\"rwt.runtime.System\"" ) );
    assertTrue( clientJs.contains( "AppearanceManager.getInstance().setCurrentTheme( {" ) );
    assertFalse( clientJs.contains( "/****" ) );
    assertFalse( clientJs.contains( "Copyright" ) );
  }

  @Test
  public void testRegisteredContentDebug() throws IOException {
    System.setProperty( RWTProperties.DEVELOPMEMT_MODE, "true" );
    clientResources.registerResources();
    String clientJs = getRegisteredContent( RAP_CLIENT_JS_PATH );

    assertTrue( clientJs.contains( "qx.Class.define( \"rwt.runtime.System\"" ) );
    assertTrue( clientJs.contains( "AppearanceManager.getInstance().setCurrentTheme( {" ) );
    assertTrue( clientJs.contains( "/****" ) );
    assertTrue( clientJs.contains( "Copyright" ) );
  }

  private String getRegisteredContent( String name ) throws IOException {
    InputStream inputStream = resourceManager.getRegisteredContent( name );
    try {
      return TestUtil.readContent( inputStream, "UTF-8" );
    } finally {
      inputStream.close();
    }
  }

}
