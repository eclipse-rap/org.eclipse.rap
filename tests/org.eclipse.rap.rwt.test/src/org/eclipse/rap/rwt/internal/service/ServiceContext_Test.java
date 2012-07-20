/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.application.ApplicationContext;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceContext;
import org.eclipse.rap.rwt.internal.service.SessionStoreImpl;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.widgets.Display;


public class ServiceContext_Test extends TestCase {

  private SessionStoreImpl sessionStore;
  private ApplicationContext applicationContext;

  @Override
  protected void setUp() {
    ApplicationConfiguration applicationConfiguration = mock( ApplicationConfiguration.class );
    ServletContext servletContext = mock( ServletContext.class );
    when( servletContext.getRealPath( anyString() ) ).thenReturn( "" );
    applicationContext = new ApplicationContext( applicationConfiguration, servletContext );
    Fixture.setSkipResourceRegistration( true );
    sessionStore = new SessionStoreImpl( new TestSession() );
  }

  @Override
  protected void tearDown() throws Exception {
    if( ContextProvider.hasContext() ) {
      Fixture.disposeOfServiceContext();
    }
    Fixture.resetSkipResourceRegistration();
  }

  public void testGetApplicationContext() {
    ServiceContext context = createContext( applicationContext );

    ApplicationContext foundInContext = context.getApplicationContext();
    ApplicationContext foundInSession = ApplicationContextUtil.get( sessionStore );
    assertSame( applicationContext, foundInContext );
    assertSame( applicationContext, foundInSession );
  }

  public void testGetApplicationContextWithNullSessionStore() {
    sessionStore = null;
    ServiceContext context = createContext( applicationContext );

    ApplicationContext found = context.getApplicationContext();

    assertSame( applicationContext, found );
  }

  public void testGetApplicationContextFromSessionStore() {
    ServiceContext context = createContext();
    applicationContext.activate();
    ApplicationContextUtil.set( sessionStore, applicationContext );

    ApplicationContext found = context.getApplicationContext();

    assertSame( applicationContext, found );
  }

  public void testGetApplicationContextFromSessionStoreWithDeactivatedApplicationContext() {
    ServiceContext context = createContext();
    ApplicationContextUtil.set( sessionStore, applicationContext );

    ApplicationContext found = context.getApplicationContext();

    assertNull( found );
  }

  public void testGetApplicationContextOnDisposedServiceContext() {
    ServiceContext context = createContext( null );
    context.dispose();

    try {
      context.getApplicationContext();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  public void testGetApplicationContextFromBackgroundThread() throws Throwable {
    ServiceContext serviceContext = createContext( applicationContext );
    ContextProvider.setContext( serviceContext );
    final ApplicationContext[] backgroundApplicationContext = { null };
    final Display display = new Display();
    Runnable runnable = new Runnable() {
      public void run() {
        UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
          public void run() {
            backgroundApplicationContext[ 0 ] = ApplicationContextUtil.getInstance();
          }
        } );
      }
    };

    Fixture.runInThread( runnable );

    assertSame( ApplicationContextUtil.getInstance(), backgroundApplicationContext[ 0 ] );
  }

  private ServiceContext createContext( ApplicationContext applicationContext ) {
    ServiceContext result = createContext();
    ServletContext servletContext = result.getRequest().getSession().getServletContext();
    ApplicationContextUtil.set( servletContext, applicationContext );
    return result;
  }

  private ServiceContext createContext() {
    TestRequest request = new TestRequest();
    TestResponse response = new TestResponse();
    HttpSession session = new TestSession();
    if( sessionStore != null ) {
      session = sessionStore.getHttpSession();
    }
    request.setSession( session );
    return createContext( request, response );
  }

  private ServiceContext createContext( TestRequest request, TestResponse response ) {
    return new ServiceContext( request, response, sessionStore );
  }
}