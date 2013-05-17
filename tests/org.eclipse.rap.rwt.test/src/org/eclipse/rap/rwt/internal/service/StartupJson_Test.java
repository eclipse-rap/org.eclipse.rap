/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.internal.service.StartupJson.DISPLAY_TYPE;
import static org.eclipse.rap.rwt.internal.service.StartupJson.METHOD_LOAD_ACTIVE_THEME;
import static org.eclipse.rap.rwt.internal.service.StartupJson.METHOD_LOAD_FALLBACK_THEME;
import static org.eclipse.rap.rwt.internal.service.StartupJson.PROPERTY_URL;
import static org.eclipse.rap.rwt.internal.service.StartupJson.THEME_STORE_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.theme.ThemeTestUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.swt.internal.widgets.displaykit.ClientResources;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class StartupJson_Test {

  private static final String CUSTOM_THEME_ID = "custom.theme.id";

  private ClientResources clientResources;
  private ApplicationContextImpl applicationContext;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.useDefaultResourceManager();
    applicationContext = getApplicationContext();
    clientResources = new ClientResources( applicationContext );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testStartupJsonContent_Url() {
    JsonObject content = StartupJson.get();

    Message message = new Message( content );
    assertEquals( "rap", message.getHead().get( PROPERTY_URL ).asString() );
  }

  @Test
  public void testStartupJsonContent_CreateDisplay() {
    JsonObject content = StartupJson.get();

    Message message = new Message( content );
    assertNotNull( message.findCreateOperation( "w1" ) );
  }

  @Test
  public void testStartupJsonContent_LoadFallbackTheme() {
    clientResources.registerResources();

    JsonObject content = StartupJson.get();

    Message message = new Message( content );
    CallOperation operation
      = message.findCallOperation( THEME_STORE_TYPE, METHOD_LOAD_FALLBACK_THEME );
    assertNotNull( operation );
    String expected = "rwt-resources/rap-rwt.theme.Fallback.json";
    assertEquals( expected, operation.getProperty( PROPERTY_URL ).asString() );
  }

  @Test
  public void testStartupJsonContent_LoadActiveTheme_DefaultTheme() {
    clientResources.registerResources();

    JsonObject content = StartupJson.get();

    Message message = new Message( content );
    CallOperation operation
      = message.findCallOperation( THEME_STORE_TYPE, METHOD_LOAD_ACTIVE_THEME );
    assertNotNull( operation );
    String expected = "rwt-resources/rap-rwt.theme.Default.json";
    assertEquals( expected, operation.getProperty( PROPERTY_URL ).asString() );
  }

  @Test
  public void testStartupJsonContent_LoadActiveTheme_CustomTheme() {
    ThemeTestUtil.registerTheme( new Theme( CUSTOM_THEME_ID, null, null ) );
    HashMap<String, String> properties = new HashMap<String,String>();
    properties.put( WebClient.THEME_ID, CUSTOM_THEME_ID );
    registerEntryPoint( properties );
    clientResources.registerResources();

    JsonObject content = StartupJson.get();

    Message message = new Message( content );
    CallOperation operation
      = message.findCallOperation( THEME_STORE_TYPE, METHOD_LOAD_ACTIVE_THEME );
    assertNotNull( operation );
    String expected = "rwt-resources/rap-rwt.theme.Custom_1465393d.json";
    assertEquals( expected, operation.getProperty( PROPERTY_URL ).asString() );
  }

  @Test
  public void testStartupJsonContent_initializeClientMessages() {
    clientResources.registerResources();

    JsonObject content = StartupJson.get();

    Message message = new Message( content );
    JsonValue clientMessages = message.findSetProperty( "rwt.client.ClientMessages", "messages" );
    assertFalse( clientMessages.asObject().isEmpty() );
  }

  @Test
  public void testStartupJsonContent_doesNotInitializeClientMessagesForNonWebClient() {
    clientResources.registerResources();
    Fixture.fakeClient( mock( Client.class ) );

    JsonObject content = StartupJson.get();

    Message message = new Message( content );
    assertNull( message.findSetOperation( "rwt.client.ClientMessages", "messages" ) );
  }

  @Test
  public void testSendStartupJson() throws IOException {
    Fixture.fakeNewGetRequest();
    TestResponse response = ( TestResponse )ContextProvider.getResponse();

    StartupJson.send( response );

    assertEquals( "application/json; charset=UTF-8", response.getHeader( "Content-Type" ) );
    assertTrue( response.getContent().indexOf( DISPLAY_TYPE ) != -1 );
  }

  private void registerEntryPoint( HashMap<String, String> properties ) {
    EntryPointManager entryPointManager = getApplicationContext().getEntryPointManager();
    EntryPointFactory factory = mock( EntryPointFactory.class );
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, factory, properties );
  }

}
