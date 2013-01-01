/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.RWTProperties;
import org.eclipse.rap.rwt.internal.resources.TestUtil;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.theme.ThemeManager;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ClientResources_Test {

  private ClientResources clientResources;
  private ResourceManager resourceManager;
  private ThemeManager themeManager;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.useDefaultResourceManager();
    resourceManager = RWT.getApplicationContext().getResourceManager();
    themeManager = getApplicationContext().getThemeManager();
    clientResources = new ClientResources( resourceManager, themeManager );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRegisterResources() {
    clientResources.registerResources();

    assertFalse( resourceManager.isRegistered( "qx/lang/Core.js" ) );
    assertTrue( resourceManager.isRegistered( "rap-client.js" ) );
    Theme defaultTheme = themeManager.getTheme( RWT.DEFAULT_THEME_ID );
    assertTrue( resourceManager.isRegistered( "rap-" + defaultTheme.getJsId() + ".json" ) );
  }

  @Test
  public void testRegisterResourcesDebug() {
    System.setProperty( RWTProperties.DEVELOPMEMT_MODE, "true" );
    clientResources.registerResources();

    assertTrue( resourceManager.isRegistered( "rap-client.js" ) );
    assertFalse( resourceManager.isRegistered( "rwt/runtime/System.js" ) );
    Theme defaultTheme = themeManager.getTheme( RWT.DEFAULT_THEME_ID );
    assertTrue( resourceManager.isRegistered( "rap-" + defaultTheme.getJsId() + ".json" ) );
  }

  @Test
  public void testRegisteredContent() throws IOException {
    System.getProperties().remove( RWTProperties.DEVELOPMEMT_MODE );
    clientResources.registerResources();
    String clientJs = getRegisteredContent( "rap-client.js" );

    assertTrue( clientJs.contains( "qx.Class.define(\"rwt.runtime.System\"" ) );
    assertTrue( clientJs.contains( "AppearanceManager.getInstance().setCurrentTheme( {" ) );
    assertFalse( clientJs.contains( "/****" ) );
    assertFalse( clientJs.contains( "Copyright" ) );
    assertTrue( clientJs.contains( "{this.JSON={}}" ) );
  }

  @Test
  public void testRegisteredContentDebug() throws IOException {
    System.setProperty( RWTProperties.DEVELOPMEMT_MODE, "true" );
    clientResources.registerResources();
    String clientJs = getRegisteredContent( "rap-client.js" );

    assertTrue( clientJs.contains( "qx.Class.define( \"rwt.runtime.System\"" ) );
    assertTrue( clientJs.contains( "AppearanceManager.getInstance().setCurrentTheme( {" ) );
    assertTrue( clientJs.contains( "/****" ) );
    assertTrue( clientJs.contains( "Copyright" ) );
    assertTrue( clientJs.contains( "this.JSON={}" ) );
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
