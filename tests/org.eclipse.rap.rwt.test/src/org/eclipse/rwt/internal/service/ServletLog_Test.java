/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.Fixture.TestLogger;
import org.eclipse.rwt.Fixture.TestServletContext;
import org.eclipse.rwt.internal.service.ServletLog;
import org.eclipse.swt.RWTFixture;


public class ServletLog_Test extends TestCase {

  private static final String LOG_MESSAGE = "gabagabahey";

  public void testLogWithContext() {
    final String[] message = { null };
    final Throwable[] throwable= { null };
    RWTFixture.setUp();
    HttpSession session = RWT.getSessionStore().getHttpSession();
    ServletContext servletContext = session.getServletContext();
    TestServletContext testServletContext
      = ( TestServletContext )servletContext;
    testServletContext.setLogger( new TestLogger() {
      public void log( final String msg, final Throwable thr ) {
        message[ 0 ] = msg;
        throwable[ 0 ] = thr;
      }
    } );
    // use servlet log
    RuntimeException exception = new RuntimeException();
    ServletLog.log( LOG_MESSAGE, exception );
    // ensure the 'real' servlet log was used
    assertEquals( LOG_MESSAGE, message[ 0 ] );
    assertSame( exception, throwable[ 0 ] );
    // clean up
    RWTFixture.tearDown();
  }

  public void testLogWithoutContext() {
    PrintStream bufferedSystemErr = System.err;
    ByteArrayOutputStream capturedSystemErr = new ByteArrayOutputStream();
    System.setErr( new PrintStream( capturedSystemErr ) );
    RuntimeException exception = new RuntimeException();
    ServletLog.log( LOG_MESSAGE, exception );
    assertTrue( capturedSystemErr.size() > 0 );
    System.setErr( bufferedSystemErr );
  }
  
  public void testLogWithoutContextWithNullArguments() {
    PrintStream bufferedSystemErr = System.err;
    ByteArrayOutputStream capturedSystemErr = new ByteArrayOutputStream();
    System.setErr( new PrintStream( capturedSystemErr ) );
    try {
      ServletLog.log( LOG_MESSAGE, null );
    } catch( NullPointerException e ) {
      fail( "Must handle null arguments" );
    }
    try {
      ServletLog.log( null, new RuntimeException() );
    } catch( NullPointerException e ) {
      fail( "Must handle null arguments" );
    }
    System.setErr( bufferedSystemErr );
  }
}
