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
package org.eclipse.rap.rwt.internal.application;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.eclipse.rap.rwt.testfixture.internal.NoOpRunnable;


public class ApplicationContextUtil_Test extends TestCase {

  private ApplicationContextImpl applicationContext;

  public void setUp() {
    applicationContext = new ApplicationContextImpl( null, null );
  }

  public void testSetToServletContext() {
    ServletContext servletContext = Fixture.createServletContext();

    ApplicationContextUtil.set( servletContext, applicationContext );

    assertSame( applicationContext, ApplicationContextUtil.get( servletContext ) );
  }

  public void testRemoveFromServletContext() {
    ServletContext servletContext = Fixture.createServletContext();
    ApplicationContextUtil.set( servletContext, applicationContext );

    ApplicationContextUtil.remove( servletContext );

    assertNull( ApplicationContextUtil.get( servletContext ) );
  }

  public void testSetToUISession() {
    UISessionImpl uiSession = new UISessionImpl( new TestSession() );
    ApplicationContextImpl applicationContext = new ApplicationContextImpl( null, null );

    ApplicationContextUtil.set( uiSession, applicationContext );

    assertSame( applicationContext, ApplicationContextUtil.get( uiSession ) );
  }

  public void testRunWith() {
    final ApplicationContextImpl[] found = new ApplicationContextImpl[ 1 ];
    Runnable runnable = new Runnable() {
      public void run() {
        found[ 0 ] = ApplicationContextUtil.getInstance();
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
      ApplicationContextUtil.runWith( null, new NoOpRunnable() );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testParamRunnableNotNull() {
    try {
      ApplicationContextUtil.runWith( applicationContext, null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testRunWithWithNestedCall() {
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
    ServletContext servletContext = Fixture.createServletContext();
    Fixture.createServiceContext();
    ApplicationContextUtil.set( servletContext, applicationContext );    

    ApplicationContextImpl found = ApplicationContextUtil.getInstance();

    assertSame( applicationContext, found );
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

  public void testApplicationContextInUISessionIsNotSerialized() throws Exception {
    UISessionImpl uiSession = new UISessionImpl( new TestSession() );
    ApplicationContextUtil.set( uiSession, applicationContext );

    UISession deserializedUiSession = Fixture.serializeAndDeserialize( uiSession );

    ApplicationContextImpl appContext = ApplicationContextUtil.get( deserializedUiSession );

    assertNull( appContext );
  }

  private RuntimeException runWithExceptionExpected( Runnable runnable ) {
    RuntimeException result = null;
    try {
      ApplicationContextUtil.runWith( applicationContext, runnable );
      fail();
    } catch( RuntimeException runtimeException ) {
      result = runtimeException;
    }
    return result;
  }
}