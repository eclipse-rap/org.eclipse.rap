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

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.lifecycle.UICallBack;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.eclipse.swt.widgets.Display;


public class ServiceContext_Test extends TestCase {

  private UISessionImpl uiSession;
  private ApplicationContextImpl applicationContext;

  @Override
  protected void setUp() {
    ApplicationConfiguration applicationConfiguration = mock( ApplicationConfiguration.class );
    ServletContext servletContext = mock( ServletContext.class );
    when( servletContext.getRealPath( anyString() ) ).thenReturn( "" );
    applicationContext = new ApplicationContextImpl( applicationConfiguration, servletContext );
    Fixture.setSkipResourceRegistration( true );
    uiSession = new UISessionImpl( new TestSession() );
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

    ApplicationContextImpl foundInContext = context.getApplicationContext();
    ApplicationContextImpl foundInSession = ApplicationContextUtil.get( uiSession );
    assertSame( applicationContext, foundInContext );
    assertSame( applicationContext, foundInSession );
  }

  public void testGetApplicationContextWithNullUISession() {
    uiSession = null;
    ServiceContext context = createContext( applicationContext );

    ApplicationContextImpl found = context.getApplicationContext();

    assertSame( applicationContext, found );
  }

  public void testGetApplicationContextFromUISession() {
    ServiceContext context = createContext();
    applicationContext.activate();
    ApplicationContextUtil.set( uiSession, applicationContext );

    ApplicationContextImpl found = context.getApplicationContext();

    assertSame( applicationContext, found );
  }

  public void testGetApplicationContextFromUISessionWithDeactivatedApplicationContext() {
    ServiceContext context = createContext();
    ApplicationContextUtil.set( uiSession, applicationContext );

    ApplicationContextImpl found = context.getApplicationContext();

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
    final ApplicationContextImpl[] backgroundApplicationContext = { null };
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