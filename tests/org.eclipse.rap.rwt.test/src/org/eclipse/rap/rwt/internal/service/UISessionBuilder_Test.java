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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.SingletonManager;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.client.ClientSelector;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.theme.ThemeUtil;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class UISessionBuilder_Test {

  private static final String CUSTOM_THEME_ID = "custom.theme.id";

  private ServletContext servletContext;
  private HttpSession httpSession;
  private TestRequest request;
  private ApplicationConfiguration configuration;
  private ApplicationContextImpl applicationContext;
  private ServiceContext serviceContext;

  @Before
  public void setUp() {
    httpSession = new TestSession();
    request = new TestRequest();
    request.setSession( httpSession );
    HttpServletResponse response = mock( HttpServletResponse.class );
    servletContext = httpSession.getServletContext();
    configuration = mock( ApplicationConfiguration.class );
    applicationContext = new ApplicationContextImpl( configuration, servletContext );
    applicationContext.getThemeManager().registerTheme( createCustomTheme( CUSTOM_THEME_ID ) );
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
  public void testFailsWithNonExistingThemeId() {
    HashMap<String, String> properties = new HashMap<String,String>();
    properties.put( WebClient.THEME_ID, "does.not.exist" );
    registerEntryPoint( properties );

    UISessionBuilder builder = new UISessionBuilder( serviceContext );
    try {
      builder.buildUISession();
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testUISessionContainsConnectionId() {
    registerEntryPoint( null );
    request.setParameter( "cid", "foo" );

    UISessionBuilder builder = new UISessionBuilder( serviceContext );
    UISessionImpl uiSession = builder.buildUISession();

    assertEquals( "foo", uiSession.getConnectionId() );
  }

  @Test
  public void testUISessionContainsNullConnectionId() {
    registerEntryPoint( null );

    UISessionBuilder builder = new UISessionBuilder( serviceContext );
    UISessionImpl uiSession = builder.buildUISession();

    assertNull( uiSession.getConnectionId() );
  }

  @Test
  public void testClientIsSelected() {
    registerEntryPoint( null );

    UISessionBuilder builder = new UISessionBuilder( serviceContext );
    UISession uiSession = builder.buildUISession();

    ClientSelector clientSelector = applicationContext.getClientSelector();
    assertNotNull( clientSelector.getSelectedClient( uiSession ) );
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

}
