/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;

import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.RWTProperties;
import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.resources.TestUtil;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.resources.IResourceManager;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class ClientResources_Test extends TestCase {

  private ClientResources clientResources;
  private IResourceManager resourceManager;

  protected void setUp() {
    Fixture.setUp();
    Fixture.useDefaultResourceManager();
    resourceManager = RWTFactory.getResourceManager();
    clientResources = new ClientResources( resourceManager, RWTFactory.getThemeManager() );
  }

  protected void tearDown() {
    Fixture.tearDown();
  }

  public void testRegisterResources() {
    clientResources.registerResources();

    assertFalse( resourceManager.isRegistered( "qx/lang/Core.js" ) );
    assertTrue( resourceManager.isRegistered( "rap-client.js" ) );
    Theme defaultTheme = RWTFactory.getThemeManager().getTheme( RWT.DEFAULT_THEME_ID );
    assertTrue( resourceManager.isRegistered( "rap-" + defaultTheme.getJsId() + ".js" ) );
  }

  public void testRegisterResourcesDebug() {
    System.setProperty( RWTProperties.CLIENT_LIBRARY_VARIANT,
                        RWTProperties.DEBUG_CLIENT_LIBRARY_VARIANT );
    clientResources.registerResources();

    assertTrue( resourceManager.isRegistered( "rap-client.js" ) );
    assertFalse( resourceManager.isRegistered( "rwt/runtime/System.js" ) );
    Theme defaultTheme = RWTFactory.getThemeManager().getTheme( RWT.DEFAULT_THEME_ID );
    assertTrue( resourceManager.isRegistered( "rap-" + defaultTheme.getJsId() + ".js" ) );
  }

  public void testRegisteredContent() throws IOException {
    clientResources.registerResources();
    String clientJs = getRegisteredContent( "rap-client.js", "UTF-8" );

    assertTrue( clientJs.contains( "qx.Class.define(\"rwt.runtime.System\"" ) );
    assertTrue( clientJs.contains( "AppearanceManager.getInstance().setCurrentTheme({" ) );
    assertFalse( clientJs.contains( "/****" ) );
    assertFalse( clientJs.contains( "Copyright" ) );
    assertTrue( clientJs.contains( "{this.JSON={}}" ) );
  }

  public void testRegisteredContentDebug() throws IOException {
    System.setProperty( RWTProperties.CLIENT_LIBRARY_VARIANT,
                        RWTProperties.DEBUG_CLIENT_LIBRARY_VARIANT );
    clientResources.registerResources();
    String clientJs = getRegisteredContent( "rap-client.js", "UTF-8" );

    assertTrue( clientJs.contains( "qx.Class.define( \"rwt.runtime.System\"" ) );
    assertTrue( clientJs.contains( "AppearanceManager.getInstance().setCurrentTheme( {" ) );
    assertTrue( clientJs.contains( "/****" ) );
    assertTrue( clientJs.contains( "Copyright" ) );
    assertTrue( clientJs.contains( "this.JSON = {};" ) );
  }

  private String getRegisteredContent( String name, String encoding ) throws IOException {
    InputStream inputStream = resourceManager.getRegisteredContent( name );
    String result;
    try {
      result = TestUtil.readContent( inputStream, encoding );
    } finally {
      inputStream.close();
    }
    return result;
  }

}
