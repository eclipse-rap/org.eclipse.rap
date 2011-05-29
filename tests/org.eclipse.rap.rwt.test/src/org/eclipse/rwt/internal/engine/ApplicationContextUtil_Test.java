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

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.service.SessionStoreImpl;
import org.eclipse.rwt.service.ISessionStore;


public class ApplicationContextUtil_Test extends TestCase {
  
  public void testSetToServletContext() {
    TestServletContext servletContext = Fixture.createServletContext();
    ApplicationContext appContext = new ApplicationContext();
    
    ApplicationContextUtil.set( servletContext, appContext );
    
    assertSame( appContext, ApplicationContextUtil.get( servletContext ) );
  }
  
  public void testRemoveFromServletContext() {
    TestServletContext servletContext = Fixture.createServletContext();
    ApplicationContextUtil.set( servletContext, new ApplicationContext() );
    
    ApplicationContextUtil.remove( servletContext );

    assertNull( ApplicationContextUtil.get( servletContext ) );
  }
  
  public void testSetToSessionStore() {
    SessionStoreImpl sessionStore = new SessionStoreImpl( new TestSession() );
    ApplicationContext applicationContext = new ApplicationContext();

    ApplicationContextUtil.set( sessionStore, applicationContext );
    
    assertSame( applicationContext, ApplicationContextUtil.get( sessionStore ) );
  }
  
  public void testRemoveFromSessionStore() {
    SessionStoreImpl sessionStore = new SessionStoreImpl( new TestSession() );
    ApplicationContextUtil.set( sessionStore, new ApplicationContext() );
    
    ApplicationContextUtil.remove( sessionStore );
    
    assertNull( ApplicationContextUtil.get( sessionStore ) );
  }
  
  public void testRunWith() {
    ApplicationContext applicationContext = new ApplicationContext();
    final ApplicationContext[] found = new ApplicationContext[ 1 ];
    Runnable runnable = new Runnable() {
      public void run() {
        found[0] = ApplicationContextUtil.getInstance();
      }
    };

    boolean before = ApplicationContextUtil.hasContext();
    ApplicationContextUtil.runWith( applicationContext, runnable );
    boolean after = ApplicationContextUtil.hasContext();

    assertFalse( before );
    assertSame( applicationContext, found[ 0 ] );
    assertFalse( after );
  }

  public void testRunWithWithException() {
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
      ApplicationContextUtil.runWith( null, new Runnable() {
        public void run() {}
      } );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testParamRunnableNotNull() {
    try {
      ApplicationContextUtil.runWith( new ApplicationContext(), null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRunWithWithNestedCall() {
    final ApplicationContext applicationContext = new ApplicationContext();
    Runnable runnable = new Runnable() {
      public void run() {
        ApplicationContextUtil.runWith( applicationContext, this );
      }
    };
    
    try {
      ApplicationContextUtil.runWith( applicationContext, runnable );
      fail( "Nested calls in same thread of runWithInstance are not allowed" );
    } catch( IllegalStateException expected ) {
    }
  }
  
  public void testGetInstance() {
    Fixture.createServiceContext();
    TestServletContext servletContext = Fixture.getServletContext();
    ApplicationContext appContext = createAndSet( servletContext );
    
    ApplicationContext found = ApplicationContextUtil.getInstance();
    
    assertSame( appContext, found );
    Fixture.disposeOfServiceContext();
  }
  
  public void testGetInstanceWithoutContextProviderRegistration() {
    try {
      ApplicationContextUtil.getInstance();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }
  
  public void testDelete() throws IOException {
    File contextDirectory = new File( Fixture.TEMP_DIR, "contextDirectory" );
    boolean directoryCreated = contextDirectory.mkdirs();
    File content = new File( contextDirectory, "context.xml" );
    boolean fileCreated = content.createNewFile();
    
    ApplicationContextUtil.delete( contextDirectory );
    
    assertTrue( directoryCreated );
    assertTrue( fileCreated );
    assertFalse( contextDirectory.exists() );
  }

  public void testApplicationContextInSessionStoreIsNotSerialized() throws Exception {
    SessionStoreImpl sessionStore = new SessionStoreImpl( new TestSession() );
    ApplicationContextUtil.set( sessionStore, new ApplicationContext() );
    byte[] bytes = Fixture.serialize( sessionStore );
    ISessionStore deserializedSessionStore = ( ISessionStore )Fixture.deserialize( bytes );
    
    ApplicationContext appContext = ApplicationContextUtil.get( deserializedSessionStore );
    
    assertNull( appContext );
  }
  
  private static RuntimeException runWithExceptionExpected( Runnable runnable ) {
    RuntimeException result = null;
    try {
      ApplicationContextUtil.runWith( new ApplicationContext(), runnable );
      fail();
    } catch( RuntimeException runtimeException ) {
      result = runtimeException;
    }
    return result;
  }
  
  private ApplicationContext createAndSet( TestServletContext servletContext ) {
    ApplicationContext result = new ApplicationContext();
    ApplicationContextUtil.set( servletContext, result );
    return result;
  }
}