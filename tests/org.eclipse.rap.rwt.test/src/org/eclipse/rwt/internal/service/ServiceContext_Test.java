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
import org.eclipse.rwt.internal.engine.RWTContext;
import org.eclipse.rwt.internal.engine.RWTContextUtil;


public class ServiceContext_Test extends TestCase {
  
  private SessionStoreImpl sessionStore;

  public void testGetRWTContext() {
    RWTContext rwtContext = new RWTContext();
    ServiceContext context = createContext( rwtContext );
    
    RWTContext foundInContext = context.getRWTContext();
    RWTContext foundInSession = RWTContextUtil.getRWTContext( sessionStore );
    assertSame( rwtContext, foundInContext );
    assertSame( rwtContext, foundInSession );
  }

  public void testGetRWTContextWithNullSessionStore() {
    RWTContext rwtContext = new RWTContext();
    sessionStore = null;
    ServiceContext context = createContext( rwtContext );
    
    RWTContext found = context.getRWTContext();

    assertSame( rwtContext, found );
  }
  
  public void testGetRWTContextFromSessionStore() {
    RWTContext rwtContext = new RWTContext();
    ServiceContext context = createContext();
    RWTContextUtil.registerRWTContext( sessionStore, rwtContext );

    RWTContext found = context.getRWTContext();
    assertSame( rwtContext, found );
  }
  
  public void testGetRWTContextOnDisposedServiceContext() {
    ServiceContext context = createContext( null );
    context.dispose();
    
    try {
      context.getRWTContext();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }
  
  protected void setUp() {
    sessionStore = new SessionStoreImpl( new TestSession() );
  }

  private ServiceContext createContext( RWTContext rwtContext ) {
    TestRequest request = new TestRequest();
    TestResponse response = new TestResponse();
    HttpSession session = new TestSession();
    if( sessionStore != null ) {
      session = sessionStore.getHttpSession();
    }
    request.setSession( session );
    ServletContext servletContext = session.getServletContext();
    RWTContextUtil.registerRWTContext( servletContext, rwtContext );
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