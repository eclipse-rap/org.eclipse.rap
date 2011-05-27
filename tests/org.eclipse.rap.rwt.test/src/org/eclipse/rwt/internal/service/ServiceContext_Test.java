/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.engine.ApplicationContext;
import org.eclipse.rwt.internal.engine.ApplicationContextUtil;


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
    ApplicationContext applicationContext = new ApplicationContext();
    ServiceContext context = createContext();
    ApplicationContextUtil.set( sessionStore, applicationContext );

    ApplicationContext found = context.getApplicationContext();
    assertSame( applicationContext, found );
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
  
  protected void setUp() {
    sessionStore = new SessionStoreImpl( new TestSession() );
  }

  private ServiceContext createContext( ApplicationContext applicationContext ) {
    TestRequest request = new TestRequest();
    TestResponse response = new TestResponse();
    HttpSession session = new TestSession();
    if( sessionStore != null ) {
      session = sessionStore.getHttpSession();
    }
    request.setSession( session );
    ServletContext servletContext = session.getServletContext();
    ApplicationContextUtil.set( servletContext, applicationContext );
    return createContext( request, response );
  }

  private ServiceContext createContext() {
    return createContext( new TestRequest(), new TestResponse() );
  }

  private ServiceContext createContext( TestRequest request,
                                        TestResponse response )
  {
    return new ServiceContext( request, response, sessionStore );
  }
}