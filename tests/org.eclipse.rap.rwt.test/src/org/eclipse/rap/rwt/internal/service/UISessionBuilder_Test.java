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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.EntryPointFactory;
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.SingletonManager;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.client.ClientSelector;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.theme.ThemeUtil;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestSession;


public class UISessionBuilder_Test extends TestCase {

  private static final String CUSTOM_THEME_ID = "custom.theme.id";

  private ServletContext servletContext;
  private HttpSession httpSession;
  private TestRequest request;
  private ApplicationConfiguration configuration;
  private ApplicationContextImpl applicationContext;

  public void testUISessionReferencesApplicationContext() {
    registerEntryPoint( null );

    UISessionBuilder builder = new UISessionBuilder( applicationContext, request );
    UISession uiSession = builder.buildUISession();

    assertEquals( applicationContext, ApplicationContextUtil.get( uiSession ) );
  }

  public void testUISessionIsAttachedToHttpSession() {
    registerEntryPoint( null );

    UISessionBuilder builder = new UISessionBuilder( applicationContext, request );
    UISession uiSession = builder.buildUISession();

    assertSame( httpSession, uiSession.getHttpSession() );
    assertEquals( uiSession, httpSession.getAttribute( UISessionImpl.ATTR_SESSION_STORE ) );
  }

  public void testSingletonManagerIsInstalled() {
    registerEntryPoint( null );

    UISessionBuilder builder = new UISessionBuilder( applicationContext, request );
    UISession uiSession = builder.buildUISession();

    assertSingletonManagerIsInstalled( uiSession );
  }

  public void testDefaultThemeIsSelected() {
    registerEntryPoint( null );

    UISessionBuilder builder = new UISessionBuilder( applicationContext, request );
    UISession uiSession = builder.buildUISession();

    assertEquals( RWT.DEFAULT_THEME_ID, uiSession.getAttribute( ThemeUtil.CURR_THEME_ATTR ) );
  }

  public void testCustomThemeIsSelected() {
    Theme theme = mock( Theme.class );
    when( theme.getId() ).thenReturn( CUSTOM_THEME_ID );
    applicationContext.getThemeManager().registerTheme( theme );
    HashMap<String, String> properties = new HashMap<String,String>();
    properties.put( WebClient.THEME_ID, CUSTOM_THEME_ID );
    registerEntryPoint( properties );

    UISessionBuilder builder = new UISessionBuilder( applicationContext, request );
    UISession uiSession = builder.buildUISession();

    assertEquals( CUSTOM_THEME_ID, uiSession.getAttribute( ThemeUtil.CURR_THEME_ATTR ) );
  }

  public void testFailsWithNonExistingThemeId() {
    HashMap<String, String> properties = new HashMap<String,String>();
    properties.put( WebClient.THEME_ID, "does.not.exist" );
    registerEntryPoint( properties );

    UISessionBuilder builder = new UISessionBuilder( applicationContext, request );
    try {
      builder.buildUISession();
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testClientIsSelected() {
    registerEntryPoint( null );

    UISessionBuilder builder = new UISessionBuilder( applicationContext, request );
    UISession uiSession = builder.buildUISession();

    ClientSelector clientSelector = applicationContext.getClientSelector();
    assertNotNull( clientSelector.getSelectedClient( uiSession ) );
  }

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    httpSession = new TestSession();
    request = new TestRequest();
    request.setSession( httpSession );
    servletContext = httpSession.getServletContext();
    configuration = mock( ApplicationConfiguration.class );
    applicationContext = new ApplicationContextImpl( configuration, servletContext );
    applicationContext.getClientSelector().activate();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private void registerEntryPoint( HashMap<String, String> properties ) {
    EntryPointManager entryPointManager = applicationContext.getEntryPointManager();
    EntryPointFactory factory = mock( EntryPointFactory.class );
    entryPointManager.register( EntryPointManager.DEFAULT_PATH, factory, properties );
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
}
