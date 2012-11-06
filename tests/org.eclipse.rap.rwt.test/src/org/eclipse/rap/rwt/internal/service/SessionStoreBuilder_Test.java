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
import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.SingletonManager;
import org.eclipse.rap.rwt.internal.application.ApplicationContext;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointUtil;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.theme.ThemeUtil;
import org.eclipse.rap.rwt.lifecycle.IEntryPointFactory;
import org.eclipse.rap.rwt.service.ISessionStore;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestSession;


public class SessionStoreBuilder_Test extends TestCase {
  
  private static final String CUSTOM_THEME_ID = "custom.theme.id";
  
  private ServletContext servletContext;
  private HttpSession httpSession;
  private TestRequest request;
  private ApplicationConfiguration configuration;
  private ApplicationContext applicationContext;

  public void testBuild() {
    registerEntryPoint( null );
    
    SessionStoreBuilder builder = new SessionStoreBuilder( applicationContext, request );
    ISessionStore sessionStore = builder.buildSessionStore();
    
    assertSame( httpSession, sessionStore.getHttpSession() );
    assertEquals( sessionStore, httpSession.getAttribute( SessionStoreImpl.ATTR_SESSION_STORE ) );
    assertEquals( applicationContext, ApplicationContextUtil.get( sessionStore ) );
    assertSingletonManagerIsInstalled( sessionStore );
    assertEquals( RWT.DEFAULT_THEME_ID, sessionStore.getAttribute( ThemeUtil.CURR_THEME_ATTR ) );
  }

  public void testBuildWithExistingThemeId() {
    Theme theme = mock( Theme.class );
    when( theme.getId() ).thenReturn( CUSTOM_THEME_ID );
    applicationContext.getThemeManager().registerTheme( theme );
    HashMap<String, String> properties = new HashMap<String,String>();
    properties.put( WebClient.THEME_ID, CUSTOM_THEME_ID );
    registerEntryPoint( properties );
    
    SessionStoreBuilder builder = new SessionStoreBuilder( applicationContext, request );
    ISessionStore sessionStore = builder.buildSessionStore();
    
    assertEquals( CUSTOM_THEME_ID, sessionStore.getAttribute( ThemeUtil.CURR_THEME_ATTR ) );
  }
  
  public void testBuildWithNonExistingThemeId() {
    HashMap<String, String> properties = new HashMap<String,String>();
    properties.put( WebClient.THEME_ID, "does.not.exist" );
    registerEntryPoint( properties );
    
    SessionStoreBuilder builder = new SessionStoreBuilder( applicationContext, request );
    try {
      builder.buildSessionStore();
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  @Override
  protected void setUp() throws Exception {
    httpSession = new TestSession();
    request = new TestRequest();
    request.setSession( httpSession );
    servletContext = httpSession.getServletContext();
    configuration = mock( ApplicationConfiguration.class );
    applicationContext = new ApplicationContext( configuration, servletContext );
  }

  private void registerEntryPoint( HashMap<String, String> properties ) {
    EntryPointManager entryPointManager = applicationContext.getEntryPointManager();
    IEntryPointFactory factory = mock( IEntryPointFactory.class );
    entryPointManager.registerByPath( EntryPointUtil.DEFAULT_PATH, factory, properties );
  }

  private void assertSingletonManagerIsInstalled( ISessionStore sessionStore ) {
    Enumeration<String> attributeNames = sessionStore.getAttributeNames();
    boolean found = false;
    while( !found && attributeNames.hasMoreElements() ) {
      String attributeName = attributeNames.nextElement();
      if( sessionStore.getAttribute( attributeName ) instanceof SingletonManager ) {
        found = true;
      }
    }
    if( !found ) {
      fail( "No SingletonManager found in session store" );
    }
  }
}
