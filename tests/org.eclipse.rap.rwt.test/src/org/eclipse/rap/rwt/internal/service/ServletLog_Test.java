/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getUISession;
import static org.eclipse.rap.rwt.testfixture.internal.ConcurrencyTestUtil.runInThread;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.servlet.ServletContext;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.testfixture.internal.TestHttpSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class ServletLog_Test {

  private static final String LOG_MESSAGE = "gabagabahey";

  private PrintStream bufferedSystemErr;
  private ByteArrayOutputStream capturedSystemErr;

  @Rule
  public TestContext context = new TestContext();

  @Before
  public void setUp() {
    bufferedSystemErr = System.err;
    capturedSystemErr = new ByteArrayOutputStream();
    System.setErr( new PrintStream( capturedSystemErr ) );
  }

  @After
  public void tearDown() {
    System.setErr( bufferedSystemErr );
  }

  @Test
  public void testLog_withContext() {
    ServletContext servletContext = spyServletContext();
    RuntimeException exception = new RuntimeException();

    ServletLog.log( LOG_MESSAGE, exception );

    verify( servletContext ).log( eq( LOG_MESSAGE ), same( exception ) );
  }

  @Test
  public void testLog_withContext_withNullException() {
    ServletContext servletContext = spyServletContext();

    ServletLog.log( LOG_MESSAGE, null );

    verify( servletContext ).log( eq( LOG_MESSAGE ) );
  }

  @Test
  public void testLog_withoutContext() throws Throwable {
    runInThread( new Runnable() {
      @Override
      public void run() {
        ServletLog.log( LOG_MESSAGE, new RuntimeException() );

        assertTrue( capturedSystemErr.toString().contains( LOG_MESSAGE ) );
        assertTrue( capturedSystemErr.toString().contains( RuntimeException.class.getName() ) );
      }
    } );
  }

  @Test
  public void testLog_withoutContext_withNullException() throws Throwable {
    runInThread( new Runnable() {
      @Override
      public void run() {
        ServletLog.log( LOG_MESSAGE, null );

        assertTrue( capturedSystemErr.toString().contains( LOG_MESSAGE ) );
      }
    } );
  }

  private static ServletContext spyServletContext() {
    TestHttpSession httpSession = ( TestHttpSession )getUISession().getHttpSession();
    ServletContext servletContext = spy( httpSession.getServletContext() );
    httpSession.setServletContext( servletContext );
    return servletContext;
  }

}
