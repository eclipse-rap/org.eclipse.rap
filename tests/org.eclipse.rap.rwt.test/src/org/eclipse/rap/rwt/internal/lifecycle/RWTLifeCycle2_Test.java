/*******************************************************************************
 * Copyright (c) 2010, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getUISession;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.engine.RWTServlet;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.textsize.TextSizeUtil;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestLogger;
import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.eclipse.rap.rwt.testfixture.internal.TestResponse;
import org.eclipse.rap.rwt.testfixture.internal.TestServletContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/*
 * Tests in here are separated from RWTLifeCycle_Test because they need
 * different setUp/tearDown implementations.
 */
public class RWTLifeCycle2_Test {
  private static final String TEST_SESSION_ATTRIBUTE = "testSessionAttr";
  private static final String EXCEPTION_MSG = "Error in readAndDispatch";

  private static String maliciousButtonId;
  private static boolean createUIEntered;
  private static boolean createUIExited;
  private static java.util.List<Object> eventLog;
  private static PhaseId currentPhase;

  private HttpSession session;

  @Before
  public void setUp() {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();

    maliciousButtonId = null;
    createUIEntered = false;
    createUIExited = false;
    eventLog = new LinkedList<Object>();
    registerTestLogger();
  }

  @After
  public void tearDown() {
    session = null;
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }

  @Test
  public void testSessionShutdownAfterExceptionInUIThread() throws Exception {
    Class<? extends EntryPoint> entryPoint = ExceptionInReadAndDispatchEntryPoint.class;
    getApplicationContext().getEntryPointManager().register( "/test", entryPoint, null );
    // send initial request - response is index.html
    runRWTServlet( newGetRequest() );
    runRWTServlet( newPostRequest( 0 ) );
    assertNull( session.getAttribute( TEST_SESSION_ATTRIBUTE ) );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    assertEquals( 0, eventLog.size() );

    // send 'application startup' request - response is JavaScript to create
    // client-side representation of what was created in EntryPoint#createUI
    runRWTServlet( newPostRequest( 1 ) );
    assertNull( session.getAttribute( TEST_SESSION_ATTRIBUTE ) );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    assertEquals( 0, eventLog.size() );

    // send 'malicious button click' - response is HTTP 500
    TestRequest request = newPostRequest( 2 );
    Fixture.fakeNotifyOperation( maliciousButtonId, ClientMessageConst.EVENT_SELECTION, null );
    try {
      runRWTServlet( request );
      fail();
    } catch( RuntimeException e ) {
      assertEquals( EXCEPTION_MSG, e.getMessage() );
    }

    assertNotNull( session.getAttribute( TEST_SESSION_ATTRIBUTE ) );
    assertTrue( createUIEntered );
    assertTrue( createUIExited );
    assertEquals( 1, eventLog.size() );
    assertTrue( eventLog.get( 0 ) instanceof Event );
    assertNull( getUISession() );
  }

  @Test
  public void testSessionShutdownAfterExceptionInInitialRequest() {
    Class<? extends EntryPoint> entryPoint = ExceptionInCreateUIEntryPoint.class;
    getApplicationContext().getEntryPointManager().register( "/test", entryPoint, null );

    // send initial request - response creates UI
    try {
      runRWTServlet( newPostRequest( 0 ) );
      fail();
    } catch( Exception expected ) {
      assertEquals( "/ by zero", expected.getMessage() );
    }

    assertTrue( createUIEntered );
    assertTrue( createUIExited );
    assertEquals( 1, eventLog.size() );
    assertTrue( eventLog.get( 0 ) instanceof Event );
    assertNull( getUISession() );
  }

  /*
   * 353053: ContextUtil doesn't support getProperty on Request proxy
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=353053
   */
  @Test
  public void testSessionShutdownWithStringMeasurementInDisplayDispose() throws Exception {
    Class<? extends EntryPoint> entryPoint = StringMeasurementInDisplayDisposeEntryPoint.class;
    getApplicationContext().getEntryPointManager().register( "/test", entryPoint, null );
    runRWTServlet( newPostRequest( 0 ) );

    runRWTServlet( newShutdownRequest() );
    assertEquals( 0, eventLog.size() );
  }

  @Test
  public void testEventProcessingOnSessionShutdown() throws Exception {
    Class<? extends EntryPoint> entryPoint = EventProcessingOnSessionShutdownEntryPoint.class;
    getApplicationContext().getEntryPointManager().register( "/test", entryPoint, null );
    // send 'application startup' request - creates initial widgets from EntryPoint#createUI
    runRWTServlet( newPostRequest( 0 ) );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    // send 'shutdown' request
    runRWTServlet( newShutdownRequest() );
    assertTrue( createUIExited );
    assertEquals( 1, eventLog.size() );
  }

  /*
   * Bug 225167: [Display] dispose() causes an IllegalStateException (The
   *             context has been disposed)
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=225167
   */
  @Test
  public void testSessionInvalidateWithDisposeInFinally() throws Exception {
    Class<? extends EntryPoint> clazz = TestSessionInvalidateWithDisposeInFinallyEntryPoint.class;
    getApplicationContext().getEntryPointManager().register( "/test", clazz, null );
    // send initial request - response creates UI
    runRWTServlet( newPostRequest( 0 ) );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    // send shutdown request
    runRWTServlet( newShutdownRequest() );
    assertTrue( createUIExited );
    assertEquals( PhaseId.PROCESS_ACTION, currentPhase );
    assertEquals( 0, eventLog.size() );
  }

  @Test
  public void testGetRequestAlwaysReturnsHtml() throws Exception {
    Class<? extends EntryPoint> entryPoint = TestEntryPoint.class;
    getApplicationContext().getEntryPointManager().register( "/test", entryPoint, null );
    // initial GET request
    runRWTServlet( newGetRequest() );
    // initial POST request starts the UI thread
    runRWTServlet( newPostRequest( 0 ) );

    // subsequent GET request should not run the lifecycle
    TestResponse response = runRWTServlet( newGetRequest() );

    assertEquals( "text/html; charset=UTF-8", response.getContentType() );
  }

  @Test
  public void testPostRequestReturnsJsonAfterSessionTimeout() throws Exception {
    Class<? extends EntryPoint> entryPoint = TestEntryPoint.class;
    getApplicationContext().getEntryPointManager().register( "/test", entryPoint, null );
    // initial GET request
    runRWTServlet( newGetRequest() );
    // initial POST request starts the UI thread
    runRWTServlet( newPostRequest( 0 ) );

    // next POST request - simulate session timeout by not providing session id
    TestResponse response = runRWTServlet( newPostRequest( 1 ) );

    assertEquals( "application/json; charset=UTF-8", response.getContentType() );
  }

  /*
   * Ensures that there is no deadlock when synchronizing on the session store in session store
   * listener beforeDestroy method.
   * see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=372946
   */
  @Test
  public void testGetLockOnUISession() throws Exception {
    Class<? extends EntryPoint> entryPoint = EntryPointWithSynchronizationOnUISession.class;
    getApplicationContext().getEntryPointManager().register( "/test", entryPoint, null );
    runRWTServlet( newPostRequest( 0 ) );

    runRWTServlet( newShutdownRequest() );
  }

  private static TestResponse runRWTServlet( final HttpServletRequest request )
    throws Exception
  {
    final AtomicReference<Exception> exception = new AtomicReference<>();
    final TestResponse response = new TestResponse();
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        synchronized( this ) {
          //
        }
        try {
          RWTServlet servlet = new RWTServlet();
          initServlet( servlet );
          servlet.service( request, response );
        } catch( Exception e ) {
          exception.set( e );
        }
      }
    };
    Thread thread = new Thread( runnable );
    thread.setDaemon( true );
    thread.setName( "Fake Request Thread" );
    synchronized( runnable ) {
      thread.start();
    }
    thread.join();
    if( exception.get() != null ) {
      throw exception.get();
    }
    return response;
  }

  private TestRequest newGetRequest() {
    Fixture.fakeNewGetRequest();
    TestRequest result = ( TestRequest )ContextProvider.getRequest();
    result.setServletPath( "/test" );
    result.setSession( session );
    return result;
  }

  private TestRequest newPostRequest( int count ) {
    Fixture.fakeNewRequest();
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setServletPath( "/test" );
    request.setSession( session );
    Fixture.fakeHeadParameter( "requestCounter", count );
    return request;
  }

  private TestRequest newShutdownRequest() {
    Fixture.fakeNewRequest();
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setServletPath( "/test" );
    request.setSession( session );
    Fixture.fakeHeadParameter( "shutdown", true );
    return request;
  }

  private void registerTestLogger() {
    session = ContextProvider.getUISession().getHttpSession();
    ServletContext servletContext = session.getServletContext();
    TestServletContext servletContextImpl = ( TestServletContext )servletContext;
    servletContextImpl.setLogger( new TestLogger() {
      @Override
      public void log( String message, Throwable throwable ) {
        if( throwable != null ) {
          throwable.printStackTrace();
        }
      }
    } );
  }

  private static void initServlet( HttpServlet servlet ) throws ServletException {
    ServletContext servletContext = Fixture.getServletContext();
    ServletConfig servletConfig = mock( ServletConfig.class );
    when( servletConfig.getServletContext() ).thenReturn( servletContext );
    servlet.init( servletConfig );
  }

  public static final class ExceptionInReadAndDispatchEntryPoint implements EntryPoint {
    @Override
    public int createUI() {
      createUIEntered = true;
      Display display = new Display();
      try {
        display.addListener( SWT.Dispose, new Listener() {
          @Override
          public void handleEvent( Event event ) {
            eventLog.add( event );
          }
        } );
        Shell shell = new Shell( display );
        shell.setLayout( new FillLayout() );
        Button maliciousButton = new Button( shell, SWT.PUSH );
        maliciousButton.addSelectionListener( new SelectionAdapter() {
          @Override
          public void widgetSelected( SelectionEvent e ) {
            HttpSession httpSession = RWT.getUISession().getHttpSession();
            httpSession.setAttribute( TEST_SESSION_ATTRIBUTE, new Object() );
            throw new RuntimeException( EXCEPTION_MSG );
          }
        } );
        maliciousButtonId = WidgetUtil.getId( maliciousButton );
        shell.setSize( 100, 100 );
        shell.layout();
        shell.open();
        while( !shell.isDisposed() ) {
          if( !display.readAndDispatch() ) {
            display.sleep();
          }
        }
        return 0;
      } finally {
        createUIExited = true;
      }
    }
  }

  public static final class ExceptionInCreateUIEntryPoint implements EntryPoint {
    @Override
    @SuppressWarnings("unused")
    public int createUI() {
      createUIEntered = true;
      Display display = new Display();
      try {
        display.addListener( SWT.Dispose, new Listener() {
          @Override
          public void handleEvent( Event event ) {
            eventLog.add( event );
          }
        } );
        Shell shell = new Shell( display );
        shell.setLayout( new FillLayout() );
        shell.setSize( 100, 100 );
        shell.layout();
        shell.open();
        if( !createUIExited ) {
          int divideByZero = 5 / 0;
        }
        while( !shell.isDisposed() ) {
          if( !display.readAndDispatch() ) {
            display.sleep();
          }
        }
        return 0;
      } finally {
        createUIExited = true;
      }
    }
  }

  public static final class StringMeasurementInDisplayDisposeEntryPoint implements EntryPoint {
    @Override
    public int createUI() {
      Display display = new Display();
      display.addListener( SWT.Dispose, new Listener() {
        @Override
        public void handleEvent( Event event ) {
          try {
            TextSizeUtil.stringExtent( event.display.getSystemFont(), "foo" );
          } catch( UnsupportedOperationException exception ) {
            eventLog.add( exception );
          }
        }
      } );
      Shell shell = new Shell( display );
      shell.setLayout( new FillLayout() );
      shell.setSize( 100, 100 );
      shell.layout();
      shell.open();
      while( !shell.isDisposed() ) {
        if( !display.readAndDispatch() ) {
          display.sleep();
        }
      }
      return 0;
    }
  }

  public static final class EventProcessingOnSessionShutdownEntryPoint implements EntryPoint {
    @Override
    public int createUI() {
      createUIEntered = true;
      try {
        Display display = new Display();
        final Shell shell = new Shell( display );
        shell.addDisposeListener( new DisposeListener() {
          @Override
          public void widgetDisposed( DisposeEvent event ) {
            eventLog.add( event );
          }
        } );
        UISession uiSession = RWT.getUISession();
        uiSession.addUISessionListener( new UISessionListener() {
          @Override
          public void beforeDestroy( UISessionEvent event ) {
            shell.dispose();
          }
        } );
        shell.setSize( 100, 100 );
        shell.layout();
        shell.open();
        while( !shell.isDisposed() ) {
          if( !display.readAndDispatch() ) {
            display.sleep();
          }
        }
        return 0;
      } finally {
        createUIExited = true;
      }
    }
  }

  public static final class EntryPointWithSynchronizationOnUISession implements EntryPoint {
    @Override
    public int createUI() {
      Display display = new Display();
      Shell shell = new Shell( display );
      final UISession uiSession = RWT.getUISession();
      uiSession.addUISessionListener( new UISessionListener() {
        @Override
        public void beforeDestroy( UISessionEvent event ) {
          synchronized( uiSession ) {
            uiSession.removeAttribute( "foo" );
          }
        }
      } );
      shell.setSize( 100, 100 );
      shell.open();
      while( !shell.isDisposed() ) {
        if( !display.readAndDispatch() ) {
          display.sleep();
        }
      }
      return 0;
    }
  }

  public static final class TestEntryPoint implements EntryPoint {

    @Override
    public int createUI() {
      createUIEntered = true;
      try {
        Display display = new Display();
        Shell shell = new Shell( display );
        shell.setSize( 100, 100 );
        shell.layout();
        shell.open();
        while( !shell.isDisposed() ) {
          if( !display.readAndDispatch() ) {
            display.sleep();
          }
        }
        return 0;
      } finally {
        createUIExited = true;
      }
    }
  }

  public static final class TestSessionInvalidateWithDisposeInFinallyEntryPoint
    implements EntryPoint
  {
    @Override
    public int createUI() {
      createUIEntered = true;
      Display display = new Display();
      try {
        Shell shell = new Shell( display );
        while( !shell.isDisposed() ) {
          if( !display.readAndDispatch() ) {
            display.sleep();
          }
        }
      } finally {
        createUIExited = true;
        currentPhase = CurrentPhase.get();
        try {
          // Access a session singleton to ensure that we have a valid context
          SingletonUtil.getSessionInstance( this.getClass() );
        } catch( Throwable thr ) {
          eventLog.add( thr );
        }
        display.dispose();
      }
      return 0;
    }
  }

}
