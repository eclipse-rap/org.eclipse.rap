/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.engine.ApplicationContext;
import org.eclipse.rwt.internal.engine.ApplicationContextUtil;
import org.eclipse.rwt.internal.engine.configurables.RWTConfigurationConfigurable;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.widgets.Display;


public class ServiceContext_Test extends TestCase {
  
  private SessionStoreImpl sessionStore;

  public void testGetApplicationContext() {
    ApplicationContext applicationContext = new ApplicationContext();
    ServiceContext context = createContext( applicationContext );
    
    ApplicationContext foundInContext = context.getApplicationContext();
    ApplicationContext foundInSession = ApplicationContextUtil.get( sessionStore );
    assertSame( applicationContext, foundInContext );
    assertSame( applicationContext, foundInSession );
  }

  public void testGetApplicationContextWithNullSessionStore() {
    ApplicationContext applicationContext = new ApplicationContext();
    sessionStore = null;
    ServiceContext context = createContext( applicationContext );
    
    ApplicationContext found = context.getApplicationContext();

    assertSame( applicationContext, found );
  }
  
  public void testGetApplicationContextFromSessionStore() {
    ServiceContext context = createContext();
    ApplicationContext applicationContext = createActivatedApplicationContext( context );
    ApplicationContextUtil.set( sessionStore, applicationContext );

    ApplicationContext found = context.getApplicationContext();
    
    assertSame( applicationContext, found );
  }
  
  public void testGetApplicationContextFromSessionStoreWithDeactivatedApplicationContext() {
    ApplicationContext applicationContext = new ApplicationContext();
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
    ServiceContext serviceContext = createContext( new ApplicationContext() );
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
  
  protected void setUp() {
    sessionStore = new SessionStoreImpl( new TestSession() );
  }
  
  @Override
  protected void tearDown() throws Exception {
    if( ContextProvider.hasContext() ) {
      Fixture.disposeOfServiceContext();
    }
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

  private ApplicationContext createActivatedApplicationContext( ServiceContext context ) {
    ApplicationContext result = new ApplicationContext();
    ServletContext servletContext = context.getRequest().getSession().getServletContext();
    result.addConfigurable( new RWTConfigurationConfigurable( servletContext ) );
    result.activate();
    return result;
  }

  private ServiceContext createContext( TestRequest request, TestResponse response ) {
    return new ServiceContext( request, response, sessionStore );
  }
}