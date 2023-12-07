/*******************************************************************************
 * Copyright (c) 2012, 2023 EclipseSource and others.
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
import static org.eclipse.rap.rwt.internal.service.StartupJson.PROPERTY_STARTUP_PARAMETERS;
import static org.eclipse.rap.rwt.internal.service.StartupJson.PROPERTY_URL;
import static org.eclipse.rap.rwt.internal.service.StartupJson.THEME_STORE_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.protocol.Operation.CallOperation;
import org.eclipse.rap.rwt.internal.resources.ClientResources;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.theme.ThemeTestUtil;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.eclipse.rap.rwt.testfixture.internal.TestResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class StartupJson_Test {

  private static final String CUSTOM_THEME_ID = "custom.theme.id";

  private ClientResources clientResources;
  private ApplicationContextImpl applicationContext;

  @Before
  public void setUp() {
    Fixture.setUp( true );
    applicationContext = getApplicationContext();
    clientResources = new ClientResources( applicationContext );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGet_url() {
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setServletPath( "/foo" );

    JsonObject content = StartupJson.get();

    TestMessage message = new TestMessage( content );
    assertEquals( "foo", message.getHead().get( PROPERTY_URL ).asString() );
  }

  @Test
  public void testGet_url_withRootServletPath() {
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setServletPath( "" );

    JsonObject content = StartupJson.get();

    TestMessage message = new TestMessage( content );
    assertEquals( "./", message.getHead().get( PROPERTY_URL ).asString() );
  }

  @Test
  public void testGet_createDisplay() {
    JsonObject content = StartupJson.get();

    TestMessage message = new TestMessage( content );
    assertNotNull( message.findCreateOperation( "w1" ) );
  }

  @Test
  public void testGet_createDisplay_withoutStartupParameters() {
    JsonObject content = StartupJson.get();

    TestMessage message = new TestMessage( content );
    JsonObject properties = message.findCreateOperation( "w1" ).getProperties();
    assertNull( properties.get( PROPERTY_STARTUP_PARAMETERS ) );
  }

  @Test
  public void testGet_createDisplay_withStartupParameters() {
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.addParameter( "foo", "1" );
    request.addParameter( "bar", "2" );

    JsonObject content = StartupJson.get();

    TestMessage message = new TestMessage( content );
    JsonObject properties = message.findCreateOperation( "w1" ).getProperties();
    String startupParameters = properties.get( PROPERTY_STARTUP_PARAMETERS ).asString();
    assertTrue( startupParameters.contains( "foo=1" ) );
    assertTrue( startupParameters.contains( "bar=2" ) );
  }

  @Test
  public void testGet_createDisplay_withStartupParametersAndMultipleValues() {
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.addParameter( "foo", "1" );
    request.addParameter( "foo", "3" );
    request.addParameter( "bar", "2" );

    JsonObject content = StartupJson.get();

    TestMessage message = new TestMessage( content );
    JsonObject properties = message.findCreateOperation( "w1" ).getProperties();
    String startupParameters = properties.get( PROPERTY_STARTUP_PARAMETERS ).asString();
    assertTrue( startupParameters.contains( "foo=1" ) );
    assertTrue( startupParameters.contains( "foo=3" ) );
    assertTrue( startupParameters.contains( "bar=2" ) );
  }

  @Test
  public void testGet_loadFallbackTheme() {
    clientResources.registerResources();

    JsonObject content = StartupJson.get();

    TestMessage message = new TestMessage( content );
    CallOperation operation
      = message.findCallOperation( THEME_STORE_TYPE, METHOD_LOAD_FALLBACK_THEME );
    assertNotNull( operation );
    String expected = "rwt-resources/rap-rwt.theme.Fallback.json";
    assertEquals( expected, operation.getParameters().get( PROPERTY_URL ).asString() );
  }

  @Test
  public void testGet_loadActiveTheme_DefaultTheme() {
    clientResources.registerResources();

    JsonObject content = StartupJson.get();

    TestMessage message = new TestMessage( content );
    CallOperation operation
      = message.findCallOperation( THEME_STORE_TYPE, METHOD_LOAD_ACTIVE_THEME );
    assertNotNull( operation );
    String expected = "rwt-resources/rap-rwt.theme.Default.json";
    assertEquals( expected, operation.getParameters().get( PROPERTY_URL ).asString() );
  }

  @Test
  public void testGet_loadActiveTheme_CustomTheme() {
    ThemeTestUtil.registerTheme( new Theme( CUSTOM_THEME_ID, null, null ) );
    HashMap<String, String> properties = new HashMap<String,String>();
    properties.put( WebClient.THEME_ID, CUSTOM_THEME_ID );
    registerEntryPoint( properties );
    clientResources.registerResources();

    JsonObject content = StartupJson.get();

    TestMessage message = new TestMessage( content );
    CallOperation operation
      = message.findCallOperation( THEME_STORE_TYPE, METHOD_LOAD_ACTIVE_THEME );
    assertNotNull( operation );
    String expected = "rwt-resources/rap-rwt.theme.Custom_1465393d.json";
    assertEquals( expected, operation.getParameters().get( PROPERTY_URL ).asString() );
  }

  @Test
  public void testSend() throws IOException {
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
