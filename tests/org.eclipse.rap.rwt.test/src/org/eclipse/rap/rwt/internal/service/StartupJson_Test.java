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
package org.eclipse.rap.rwt.internal.service;

import static org.eclipse.rap.rwt.internal.service.StartupJson.DISPLAY_TYPE;
import static org.eclipse.rap.rwt.internal.service.StartupJson.METHOD_LOAD_ACTIVE_THEME;
import static org.eclipse.rap.rwt.internal.service.StartupJson.METHOD_LOAD_FALLBACK_THEME;
import static org.eclipse.rap.rwt.internal.service.StartupJson.PROPERTY_URL;
import static org.eclipse.rap.rwt.internal.service.StartupJson.THEME_STORE_TYPE;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.resources.IResourceManager;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.swt.internal.widgets.displaykit.ClientResources;

import junit.framework.TestCase;


public class StartupJson_Test extends TestCase {

  private ClientResources clientResources;

  @Override
  protected void setUp() {
    Fixture.setUp();
    Fixture.useDefaultResourceManager();
    IResourceManager resourceManager = RWTFactory.getResourceManager();
    clientResources = new ClientResources( resourceManager, RWTFactory.getThemeManager() );
  }

  @Override
  protected void tearDown() {
    Fixture.tearDown();
  }

  public void testStartupJsonContent_Url() {
    String content = StartupJson.get();

    Message message = new Message( content );
    assertEquals( "rap", message.findHeadProperty( PROPERTY_URL ) );
  }

  public void testStartupJsonContent_CreateDisplay() {
    String content = StartupJson.get();

    Message message = new Message( content );
    assertNotNull( message.findCreateOperation( "w1" ) );
  }

  public void testStartupJsonContent_LoadFallbackTheme() {
    clientResources.registerResources();

    String content = StartupJson.get();

    Message message = new Message( content );
    CallOperation operation
      = message.findCallOperation( THEME_STORE_TYPE, METHOD_LOAD_FALLBACK_THEME );
    assertNotNull( operation );
    String expected = "rwt-resources/rap-rwt.theme.Fallback.json";
    assertEquals( expected, operation.getProperty( PROPERTY_URL ) );
  }

  public void testStartupJsonContent_LoadActiveTheme() {
    clientResources.registerResources();

    String content = StartupJson.get();

    Message message = new Message( content );
    CallOperation operation
      = message.findCallOperation( THEME_STORE_TYPE, METHOD_LOAD_ACTIVE_THEME );
    assertNotNull( operation );
    String expected = "rwt-resources/rap-rwt.theme.Default.json";
    assertEquals( expected, operation.getProperty( PROPERTY_URL ) );
  }

  public void testSendStartupJson() throws IOException {
    Fixture.fakeNewGetRequest();

    StartupJson.send();

    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    assertEquals( "application/json; charset=UTF-8", response.getHeader( "Content-Type" ) );
    assertTrue( response.getContent().indexOf( DISPLAY_TYPE ) != -1 );
  }

}
