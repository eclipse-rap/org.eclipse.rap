/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
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
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.resources.SystemProps;
import org.eclipse.rwt.internal.resources.TestUtil;
import org.eclipse.rwt.resources.IResourceManager;


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

    assertTrue( resourceManager.isRegistered( "rap-client.js" ) );
    assertFalse( resourceManager.isRegistered( "qx/lang/Core.js" ) );
  }

  public void testRegisterResourcesDebug() {
    System.setProperty( SystemProps.CLIENT_LIBRARY_VARIANT,
                        SystemProps.DEBUG_CLIENT_LIBRARY_VARIANT );
    clientResources.registerResources();

    assertTrue( resourceManager.isRegistered( "rap-client.js" ) );
    assertFalse( resourceManager.isRegistered( "qx/lang/Core.js" ) );
  }

  public void testRegisteredContent() throws IOException {
    clientResources.registerResources();
    String clientJs = getRegisteredContent( "rap-client.js", "UTF-8" );

    assertTrue( clientJs.contains( "qx.Class.define(\"qx.lang.Core\");" ) );
    assertTrue( clientJs.contains( "qx.Theme.define(\"" ) );
    assertFalse( clientJs.contains( "/****" ) );
    assertFalse( clientJs.contains( "Copyright" ) );
  }

  public void testRegisteredContentDebug() throws IOException {
    System.setProperty( SystemProps.CLIENT_LIBRARY_VARIANT,
                        SystemProps.DEBUG_CLIENT_LIBRARY_VARIANT );
    clientResources.registerResources();
    String clientJs = getRegisteredContent( "rap-client.js", "UTF-8" );

    assertTrue( clientJs.contains( "qx.Class.define(\"qx.lang.Core\");" ) );
    assertTrue( clientJs.contains( "qx.Theme.define( \"" ) );
    assertTrue( clientJs.contains( "/****" ) );
    assertTrue( clientJs.contains( "Copyright" ) );
  }

  public void testRegisterResourcesWithCustomContextLoader() {
    URLClassLoader contextLoader = new URLClassLoader( new URL[ 0 ] );
    resourceManager.setContextLoader( contextLoader );
    clientResources.registerResources();
    assertSame( contextLoader, resourceManager.getContextLoader() );
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
