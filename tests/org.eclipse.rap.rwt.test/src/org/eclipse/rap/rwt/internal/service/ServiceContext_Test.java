/*******************************************************************************
 * Copyright (c) 2011, 2013 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.internal.SingletonManager;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ServiceContext_Test {

  private UISessionImpl uiSession;
  private ApplicationContextImpl applicationContext;

  @Before
  public void setUp() {
    ApplicationConfiguration applicationConfiguration = mock( ApplicationConfiguration.class );
    ServletContext servletContext = mock( ServletContext.class );
    when( servletContext.getRealPath( anyString() ) ).thenReturn( "" );
    applicationContext = new ApplicationContextImpl( applicationConfiguration, servletContext );
    Fixture.setSkipResourceRegistration( true );
    uiSession = new UISessionImpl( new TestSession() );
  }

  @After
  public void tearDown() {
    if( ContextProvider.hasContext() ) {
      Fixture.disposeOfServiceContext();
    }
    Fixture.resetSkipResourceRegistration();
  }

  @Test
  public void testGetApplicationContext() {
    ServiceContext context = createContext( applicationContext );

    ApplicationContextImpl foundInContext = context.getApplicationContext();
    ApplicationContextImpl foundInSession = ApplicationContextUtil.get( uiSession );
    assertSame( applicationContext, foundInContext );
    assertSame( applicationContext, foundInSession );
  }

  @Test
  public void testGetApplicationContextWithNullUISession() {
    uiSession = null;
    ServiceContext context = createContext( applicationContext );

    ApplicationContextImpl found = context.getApplicationContext();

    assertSame( applicationContext, found );
  }

  @Test
  public void testGetApplicationContextFromUISession() {
    ServiceContext context = createContext();
    applicationContext.activate();
    ApplicationContextUtil.set( uiSession, applicationContext );

    ApplicationContextImpl found = context.getApplicationContext();

    assertSame( applicationContext, found );
  }

  @Test
  public void testGetApplicationContextFromUISessionWithDeactivatedApplicationContext() {
    ServiceContext context = createContext();
    ApplicationContextUtil.set( uiSession, applicationContext );

    ApplicationContextImpl found = context.getApplicationContext();

    assertNull( found );
  }

  @Test
  public void testGetApplicationContextOnDisposedServiceContext() {
    ServiceContext context = createContext( null );
    context.dispose();

    try {
      context.getApplicationContext();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  @Test
  public void testGetApplicationContextFromBackgroundThread() throws Throwable {
    SingletonManager.install( uiSession );
    ServiceContext serviceContext = createContext( applicationContext );
    ContextProvider.setContext( serviceContext );
    final ApplicationContextImpl[] backgroundApplicationContext = { null };
    final Display display = new Display();
    Runnable runnable = new Runnable() {
      public void run() {
        RWT.getUISession( display ).exec( new Runnable() {
          public void run() {
            backgroundApplicationContext[ 0 ] = ApplicationContextUtil.getInstance();
          }
        } );
      }
    };

    Fixture.runInThread( runnable );

    assertSame( ApplicationContextUtil.getInstance(), backgroundApplicationContext[ 0 ] );
  }

  private ServiceContext createContext( ApplicationContextImpl applicationContext ) {
    ServiceContext result = createContext();
    ServletContext servletContext = result.getRequest().getSession().getServletContext();
    ApplicationContextUtil.set( servletContext, applicationContext );
    return result;
  }

  private ServiceContext createContext() {
    TestRequest request = new TestRequest();
    request.setBody( Fixture.createEmptyMessage() );
    TestResponse response = new TestResponse();
    HttpSession session = new TestSession();
    if( uiSession != null ) {
      session = uiSession.getHttpSession();
    }
    request.setSession( session );
    return createContext( request, response );
  }

  private ServiceContext createContext( TestRequest request, TestResponse response ) {
    ServiceContext result = new ServiceContext( request, response, uiSession );
    result.setServiceStore( new ServiceStore() );
    return result;
  }

}
