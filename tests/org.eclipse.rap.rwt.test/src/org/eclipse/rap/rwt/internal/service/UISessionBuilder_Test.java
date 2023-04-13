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
package org.eclipse.rap.rwt.internal.service;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getProtocolWriter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.SingletonManager;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.client.ClientMessages;
import org.eclipse.rap.rwt.internal.client.ClientProvider;
import org.eclipse.rap.rwt.internal.client.ClientSelector;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.protocol.ResponseMessage;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.theme.ThemeUtil;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.internal.TestHttpSession;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class UISessionBuilder_Test {

  private static final String CUSTOM_THEME_ID = "custom.theme.id";

  private ServletContext servletContext;
  private HttpSession httpSession;
  private TestRequest request;
  private HttpServletResponse response;
  private ApplicationConfiguration configuration;
  private ApplicationContextImpl applicationContext;
  private ServiceContext serviceContext;
  private Client client;


  @Before
  public void setUp() {
    httpSession = new TestHttpSession();
    request = new TestRequest();
    request.setSession( httpSession );
    response = mock( HttpServletResponse.class );
    servletContext = httpSession.getServletContext();
    configuration = mock( ApplicationConfiguration.class );
    client = mock( Client.class );
    applicationContext = new ApplicationContextImpl( configuration, servletContext );
    applicationContext.getThemeManager().registerTheme( createCustomTheme( CUSTOM_THEME_ID ) );
    applicationContext.getClientSelector().addClientProvider( createClientProvider( client ) );
    applicationContext.activate();
    serviceContext = new ServiceContext( request, response, applicationContext );
    ContextProvider.setContext( serviceContext );
  }

  @After
  public void tearDown() {
    ContextProvider.disposeContext();
  }

  @Test
  public void testUISessionReferencesApplicationContext() {
    registerEntryPoint( null );

    UISessionBuilder builder = new UISessionBuilder( serviceContext );
    UISessionImpl uiSession = builder.buildUISession();

    assertEquals( applicationContext, uiSession.getApplicationContext() );
  }

  @Test
  public void testUISessionIsAttachedToHttpSession() {
    registerEntryPoint( null );
    httpSession = mock( HttpSession.class );
    request.setSession( httpSession );

    UISessionBuilder builder = new UISessionBuilder( serviceContext );
    UISession uiSession = builder.buildUISession();

    assertSame( httpSession, uiSession.getHttpSession() );
    verify( httpSession ).setAttribute( anyString(), same( uiSession ) );
  }

  @Test
  public void testSingletonManagerIsInstalled() {
    registerEntryPoint( null );

    UISessionBuilder builder = new UISessionBuilder( serviceContext );
    UISession uiSession = builder.buildUISession();

    assertSingletonManagerIsInstalled( uiSession );
  }

  @Test
  public void testDefaultThemeIsSelected() {
    registerEntryPoint( null );

    UISessionBuilder builder = new UISessionBuilder( serviceContext );
    UISession uiSession = builder.buildUISession();

    assertEquals( RWT.DEFAULT_THEME_ID, uiSession.getAttribute( ThemeUtil.CURR_THEME_ATTR ) );
  }

  @Test
  public void testCustomThemeIsSelected() {
    HashMap<String, String> properties = new HashMap<String,String>();
    properties.put( WebClient.THEME_ID, CUSTOM_THEME_ID );
    registerEntryPoint( properties );

    UISessionBuilder builder = new UISessionBuilder( serviceContext );
    UISession uiSession = builder.buildUISession();

    assertEquals( CUSTOM_THEME_ID, uiSession.getAttribute( ThemeUtil.CURR_THEME_ATTR ) );
  }

  @Test
  public void testConnectionIdIsGenerated() {
    registerEntryPoint( null );

    UISessionBuilder builder = new UISessionBuilder( serviceContext );
    UISessionImpl uiSession = builder.buildUISession();

    assertNotNull( uiSession.getConnectionId() );
  }

  @Test
  public void testConnectionIdIsUnique() {
    registerEntryPoint( null );

    UISessionImpl uiSession1 = new UISessionBuilder( serviceContext ).buildUISession();
    String connectionId1 = uiSession1.getConnectionId();
    ContextProvider.disposeContext();

    serviceContext = new ServiceContext( request, response, applicationContext );
    ContextProvider.setContext( serviceContext );
    UISessionImpl uiSession2 = new UISessionBuilder( serviceContext ).buildUISession();

    assertNotEquals( connectionId1, uiSession2.getConnectionId() );
  }

  @Test
  public void testConnectionIdIsRendered() {
    registerEntryPoint( null );

    UISessionBuilder builder = new UISessionBuilder( serviceContext );
    UISessionImpl uiSession = builder.buildUISession();

    ResponseMessage message = getProtocolWriter().createMessage();
    assertEquals( uiSession.getConnectionId(), message.getHead().get( "cid" ).asString() );
  }

  @Test
  public void testClientIsSelected() {
    registerEntryPoint( null );

    UISessionBuilder builder = new UISessionBuilder( serviceContext );
    UISession uiSession = builder.buildUISession();

    ClientSelector clientSelector = applicationContext.getClientSelector();
    assertNotNull( clientSelector.getSelectedClient( uiSession ) );
  }

  @Test
  public void testMeasurementOperatorIsCreated() {
    registerEntryPoint( null );

    UISessionBuilder builder = new UISessionBuilder( serviceContext );
    builder.buildUISession();

    assertNotNull( RemoteObjectRegistry.getInstance().get( "rwt.client.TextSizeMeasurement" ) );
  }

  @Test
  public void testUpdateClientMessages() {
    registerEntryPoint( null );
    ClientMessages messages = mockClientMessagesService();

    UISessionBuilder builder = new UISessionBuilder( serviceContext );
    builder.buildUISession();

    verify( messages ).update( any( Locale.class ) );
  }

  private ClientMessages mockClientMessagesService() {
    ClientMessages messagesService = mock( ClientMessages.class );
    when( client.getService( ClientMessages.class ) ).thenReturn( messagesService );
    return messagesService;
  }

  private void registerEntryPoint( HashMap<String, String> properties ) {
    EntryPointManager entryPointManager = applicationContext.getEntryPointManager();
    EntryPointFactory factory = mock( EntryPointFactory.class );
    entryPointManager.register( TestRequest.DEFAULT_SERVLET_PATH, factory, properties );
  }

  private void assertSingletonManagerIsInstalled( UISession uiSession ) {
    Enumeration<String> attributeNames = uiSession.getAttributeNames();
    boolean found = false;
    while( !found && attributeNames.hasMoreElements() ) {
      String attributeName = attributeNames.nextElement();
      if( uiSession.getAttribute( attributeName ) instanceof SingletonManager ) {
        found = true;
      }
    }
    if( !found ) {
      fail( "No SingletonManager found in session store" );
    }
  }

  private static Theme createCustomTheme( String id ) {
    Theme theme = mock( Theme.class );
    when( theme.getId() ).thenReturn( id );
    return theme;
  }

  private static ClientProvider createClientProvider( final Client client ) {
    return new ClientProvider() {
      public Client getClient() {
        return client;
      }
      public boolean accept( HttpServletRequest request ) {
        return true;
      }
    };
  }

}
