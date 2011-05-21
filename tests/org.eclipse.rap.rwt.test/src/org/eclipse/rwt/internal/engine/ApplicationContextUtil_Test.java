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
package org.eclipse.rwt.internal.engine;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.SessionStoreImpl;
import org.eclipse.rwt.service.ISessionStore;


public class ApplicationContextUtil_Test extends TestCase {
  
  public void testCreateContext() {
    Fixture.createServiceContext();
    ServletContext servletContext = getServletContext();
    
    ApplicationContext applicationContext = ApplicationContextUtil.createContext( servletContext );
    RWTServletContextListener.registerConfigurables( servletContext, applicationContext );
    applicationContext.activate();

    assertNotNull( applicationContext );
    assertSame( applicationContext.getInstance( EntryPointManager.class ), 
                ApplicationContextUtil.getInstance().getInstance( EntryPointManager.class ) );
    ApplicationContextUtil.deregisterApplicationContext( servletContext );
  }
  
  public void testDeregisterApplicationContext() {
    Fixture.createServiceContext();
    ServletContext servletContext = getServletContext();
    ApplicationContextUtil.deregisterApplicationContext( servletContext );
    try {
      ApplicationContextUtil.getInstance().getInstance( EntryPointManager.class );
      fail( "After deregistration there must be no context available." );
    } catch( IllegalStateException expected ) {
    }
  }

  public void testRegisterApplicationContext() {
    TestServletContext servletContext = new TestServletContext();
    ApplicationContext applicationContext = new ApplicationContext();
    
    ApplicationContextUtil.registerApplicationContext( servletContext, applicationContext );
    ApplicationContext found = ApplicationContextUtil.getApplicationContext( servletContext );
    assertSame( applicationContext, found );
    
    ApplicationContextUtil.deregisterApplicationContext( servletContext );
    assertNull( ApplicationContextUtil.getApplicationContext( servletContext ) );
  }
  
  public void testRegisterApplicationContextOnSessionStore() {
    SessionStoreImpl sessionStore = new SessionStoreImpl( new TestSession() );
    ApplicationContext applicationContext = new ApplicationContext();

    ApplicationContextUtil.registerApplicationContext( sessionStore, applicationContext );
    ApplicationContext found = ApplicationContextUtil.getApplicationContext( sessionStore );
    assertSame( applicationContext, found );
    
    ApplicationContextUtil.deregisterApplicationContext( sessionStore );
    assertNull( ApplicationContextUtil.getApplicationContext( sessionStore ) );
  }
  
  public void testRunWithInstance() {
    ApplicationContext applicationContext = new ApplicationContext();
    final ApplicationContext[] found = new ApplicationContext[ 1 ];
    Runnable runnable = new Runnable() {
      public void run() {
        found[0] = ApplicationContextUtil.getInstance();
      }
    };

    boolean before = ApplicationContextUtil.hasContext();
    ApplicationContextUtil.runWithInstance( applicationContext, runnable );
    boolean after = ApplicationContextUtil.hasContext();

    assertFalse( before );
    assertSame( applicationContext, found[ 0 ] );
    assertFalse( after );
  }

  public void testRunWithInstanceWithException() {
    final RuntimeException expected = new RuntimeException();
    Runnable runnable = new Runnable() {
      public void run() {
        throw expected;
      }
    };
    
    boolean before = ApplicationContextUtil.hasContext();
    RuntimeException actual = runWithExceptionExpected( runnable );
    boolean after = ApplicationContextUtil.hasContext();
    
    assertFalse( before );
    assertSame( expected, actual );
    assertFalse( after );
  }
  
  public void testParamApplicationContextNotNull() {
    try {
      ApplicationContextUtil.runWithInstance( null, new Runnable() {
        public void run() {}
      } );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testParamRunnableNotNull() {
    try {
      ApplicationContextUtil.runWithInstance( new ApplicationContext(), null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRunWithInstanceWithNestedCall() {
    final ApplicationContext applicationContext = new ApplicationContext();
    Runnable runnable = new Runnable() {
      public void run() {
        ApplicationContextUtil.runWithInstance( applicationContext, this );
      }
    };
    
    try {
      ApplicationContextUtil.runWithInstance( applicationContext, runnable );
      fail( "Nested calls in same thread of runWithInstance are not allowed" );
    } catch( IllegalStateException expected ) {
    }
  }
  
  public void testGetInstanceWithoutContextProviderRegistration() {
    try {
      ApplicationContextUtil.getInstance();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  private static ServletContext getServletContext() {
    ISessionStore session = ContextProvider.getSession();
    HttpSession httpSession = session.getHttpSession();
    return httpSession.getServletContext();
  }

  private static RuntimeException runWithExceptionExpected( Runnable runnable ) {
    RuntimeException result = null;
    try {
      ApplicationContextUtil.runWithInstance( new ApplicationContext(), runnable );
      fail();
    } catch( RuntimeException runtimeException ) {
      result = runtimeException;
    }
    return result;
  }
}
