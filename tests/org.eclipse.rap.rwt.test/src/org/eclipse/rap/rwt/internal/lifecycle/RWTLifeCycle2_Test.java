/*******************************************************************************
 * Copyright (c) 2010, 2012 Innoopract Informationssysteme GmbH.
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

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.util.LinkedList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.engine.RWTServlet;
import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.RequestParams;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestLogger;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.TestResponse;
import org.eclipse.rap.rwt.testfixture.TestServletContext;
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


/*
 * Tests in here are separated from RWTLifeCycle_Test because they need
 * different setUp/tearDown implementations.
 */
public class RWTLifeCycle2_Test extends TestCase {
  private static final String TEST_SESSION_ATTRIBUTE = "testSessionAttr";
  private static final String EXCEPTION_MSG = "Error in readAndDispatch";

  private static String maliciousButtonId;
  private static boolean createUIEntered;
  private static boolean createUIExited;
  private static Shell testShell;
  private static java.util.List<Object> eventLog;
  private static PhaseId currentPhase;

  private HttpSession session;

  @Override
  protected void setUp() throws Exception {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();

    maliciousButtonId = null;
    createUIEntered = false;
    createUIExited = false;
    testShell = null;
    eventLog = new LinkedList<Object>();
    registerTestLogger();
  }

  @Override
  protected void tearDown() throws Exception {
    session = null;
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }

  public void testSessionRestartAfterExceptionInUIThread() throws Exception {
    TestRequest request;
    Class<? extends EntryPoint> entryPoint = ExceptionInReadAndDispatchEntryPoint.class;
    RWTFactory.getEntryPointManager().register( "/test", entryPoint, null );
    // send initial request - response is index.html
    request = newGetRequest();
    runRWTDelegate( request );
    request = newPostRequest( true );
    runRWTDelegate( request );
    assertNull( session.getAttribute( TEST_SESSION_ATTRIBUTE ) );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    assertEquals( 0, eventLog.size() );

    // send 'application startup' request - response is JavaScript to create
    // client-side representation of what was created in EntryPoint#createUI
    request = newPostRequest( false );
    runRWTDelegate( request );
    assertNull( session.getAttribute( TEST_SESSION_ATTRIBUTE ) );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    assertEquals( 0, eventLog.size() );

    // send 'malicious button click' - response is HTTP 500
    request = newPostRequest( false );
    Fixture.fakeNotifyOperation( maliciousButtonId, ClientMessageConst.EVENT_SELECTION, null );
    try {
      runRWTDelegate( request );
      fail();
    } catch( RuntimeException e ) {
      assertEquals( EXCEPTION_MSG, e.getMessage() );
    }
    assertNotNull( session.getAttribute( TEST_SESSION_ATTRIBUTE ) );
    assertTrue( createUIEntered );
    assertTrue( createUIExited );
    assertEquals( 0, eventLog.size() );

    // send 'refresh' request - session is restarted
    request = newPostRequest( true );
    runRWTDelegate( request );
    assertEquals( 1, eventLog.size() );
    assertTrue( eventLog.get( 0 ) instanceof Event );
  }

  public void testSessionRestartAfterExceptionInInitialRequest() throws Exception {
    TestRequest request;
    Class<? extends EntryPoint> entryPoint = ExceptionInCreateUIEntryPoint.class;
    RWTFactory.getEntryPointManager().register( "/test", entryPoint, null );
    // send initial request - response creates ui
    request = newPostRequest( true );
    try {
      runRWTDelegate( request );
      fail();
    } catch( Exception expected ) {
      assertEquals( "/ by zero", expected.getMessage() );
    }
    assertTrue( createUIEntered );
    assertTrue( createUIExited );
    assertEquals( 0, eventLog.size() );

    // send 'refresh' request - session is restarted
    request = newPostRequest( true );
    runRWTDelegate( request );
    assertEquals( 1, eventLog.size() );
    assertTrue( eventLog.get( 0 ) instanceof Event );
  }

  /*
   * 353053: ContextUtil doesn't support getProperty on Request proxy
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=353053
   */
  public void testSessionRestartWithStringMeasurementInDisplayDispose() throws Exception {
    TestRequest request;
    Class<? extends EntryPoint> entryPoint = StringMeasurementInDisplayDisposeEntryPoint.class;
    RWTFactory.getEntryPointManager().register( "/test", entryPoint, null );
    // send initial request - response creates ui
    request = newPostRequest( true );
    runRWTDelegate( request );

    // send 'refresh' request - session is restarted
    request = newPostRequest( true );
    runRWTDelegate( request );
    assertEquals( 0, eventLog.size() );
  }

  public void testEventProcessingOnSessionRestart() throws Exception {
    TestRequest request;
    Class<? extends EntryPoint> entryPoint = EventProcessingOnSessionRestartEntryPoint.class;
    RWTFactory.getEntryPointManager().register( "/test", entryPoint, null );
    // send 'application startup' request - response is JavaScript to create
    // client-side representation of what was created in EntryPoint#createUI
    request = newPostRequest( true );
    runRWTDelegate( request );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    // send 'restart' request
    request = newPostRequest( true );
    runRWTDelegate( request );
    assertTrue( createUIExited );
    assertEquals( 1, eventLog.size() );
  }

  /*
   * Bug 225167: [Display] dispose() causes an IllegalStateException (The
   *             context has been disposed)
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=225167
   */
  public void testSessionInvalidateWithDisposeInFinally() throws Exception {
    TestRequest request;
    Class<? extends EntryPoint> clazz = TestSessionInvalidateWithDisposeInFinallyEntryPoint.class;
    RWTFactory.getEntryPointManager().register( "/test", clazz, null );
    // send initial request - response creates ui
    request = newPostRequest( true );
    runRWTDelegate( request );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    // send another initial request to restart session
    request = newPostRequest( true );
    runRWTDelegate( request );
    assertTrue( createUIExited );
    assertEquals( PhaseId.PROCESS_ACTION, currentPhase );
    assertEquals( 0, eventLog.size() );
  }

  /*
   * 354368: Occasional exception on refresh (F5)
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=354368
   */
  public void testClearUISessionOnSessionRestart() throws Exception {
    TestRequest request;
    Class<? extends EntryPoint> entryPointClass = TestEntryPoint.class;
    RWTFactory.getEntryPointManager().register( "/test", entryPointClass, null );
    // send initial request - response creates ui
    request = newPostRequest( true );
    runRWTDelegate( request );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    // send a request that closes the main shell
    request = newPostRequest( false );
    Fixture.fakeNotifyOperation( getId( testShell ), ClientMessageConst.EVENT_CLOSE, null );
    runRWTDelegate( request );
    assertTrue( createUIExited );
    // send a request after the createUI has been exited
    request = newPostRequest( false );
    runRWTDelegate( request );
    // send another initial request to restart session
    request = newPostRequest( true );
    runRWTDelegate( request );
    // ensures that no exceptions has been thrown
  }

  public void testGetRequestDoesNotClearUISession() throws Exception {
    Class<? extends EntryPoint> entryPoint = TestEntryPoint.class;
    RWTFactory.getEntryPointManager().register( "/test", entryPoint, null );
    // inital GET request
    runRWTDelegate( newGetRequest() );
    // inital POST request starts the UI thread
    runRWTDelegate( newPostRequest( true ) );
    ContextProvider.getUISession().setAttribute( "dummy", Boolean.TRUE );

    // subsequent GET request should not run the lifecycle
    runRWTDelegate( newGetRequest() );

    assertEquals( Boolean.TRUE, ContextProvider.getUISession().getAttribute( "dummy" ) );
  }

  public void testGetRequestAlwaysReturnsHtml() throws Exception {
    Class<? extends EntryPoint> entryPoint = TestEntryPoint.class;
    RWTFactory.getEntryPointManager().register( "/test", entryPoint, null );
    // inital GET request
    runRWTDelegate( newGetRequest() );
    // inital POST request starts the UI thread
    runRWTDelegate( newPostRequest( true ) );

    // subsequent GET request should not run the lifecycle
    TestResponse response = runRWTDelegate( newGetRequest() );

    assertEquals( "text/html; charset=UTF-8", response.getContentType() );
  }

  public void testPostRequestReturnsJsonAfterSessionTimeout() throws Exception {
    Class<? extends EntryPoint> entryPoint = TestEntryPoint.class;
    RWTFactory.getEntryPointManager().register( "/test", entryPoint, null );
    // inital GET request
    runRWTDelegate( newGetRequest() );
    // inital POST request starts the UI thread
    runRWTDelegate( newPostRequest( true ) );

    // next POST request - simulate session timeout by not providing session id
    TestResponse response = runRWTDelegate( newPostRequest( false ) );

    assertEquals( "application/json; charset=UTF-8", response.getContentType() );
  }

  /*
   * Ensures that there is no deadlock when synchronizing on the session store in session store
   * listener beforeDestroy method.
   * see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=372946
   */
  public void testGetLockOnUISession() throws Exception {
    Class<? extends EntryPoint> entryPoint = EntryPointWithSynchronizationOnUISession.class;
    RWTFactory.getEntryPointManager().register( "/test", entryPoint, null );
    // inital POST request starts the UI thread
    runRWTDelegate( newPostRequest( true ) );

    // simulate session restart
    runRWTDelegate( newPostRequest( true ) );
  }

  private static TestResponse runRWTDelegate( final HttpServletRequest request )
    throws Exception
  {
    final Exception[] exception = { null };
    final TestResponse[] response = { new TestResponse() };
    Runnable runnable = new Runnable() {
      public void run() {
        synchronized( this ) {
          //
        }
        try {
          RWTServlet delegate = new RWTServlet();
          delegate.doPost( request, response[ 0 ] );
        } catch( Exception e ) {
          exception[ 0 ] = e;
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
    if( exception[ 0 ] != null ) {
      throw exception[ 0 ];
    }
    return response[ 0 ];
  }

  private TestRequest newGetRequest() {
    Fixture.fakeNewGetRequest();
    TestRequest result = ( TestRequest )ContextProvider.getRequest();
    result.setServletPath( "/test" );
    result.setSession( session );
    return result;
  }

  private TestRequest newPostRequest( boolean initialize) {
    Fixture.fakeNewRequest();
    TestRequest result = ( TestRequest )ContextProvider.getRequest();
    result.setServletPath( "/test" );
    result.setSession( session );
    if( initialize ) {
      Fixture.fakeHeadParameter( RequestParams.RWT_INITIALIZE, "true" );
    }
    return result;
  }

  private void registerTestLogger() {
    session = ContextProvider.getUISession().getHttpSession();
    ServletContext servletContext = session.getServletContext();
    TestServletContext servletContextImpl = ( TestServletContext )servletContext;
    servletContextImpl.setLogger( new TestLogger() {
      public void log( String message, Throwable throwable ) {
        if( throwable != null ) {
          throwable.printStackTrace();
        }
      }
    } );
  }

  public static final class ExceptionInReadAndDispatchEntryPoint implements EntryPoint {
    public int createUI() {
      createUIEntered = true;
      Display display = new Display();
      try {
        display.addListener( SWT.Dispose, new Listener() {
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
    @SuppressWarnings("unused")
    public int createUI() {
      createUIEntered = true;
      Display display = new Display();
      try {
        display.addListener( SWT.Dispose, new Listener() {
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
    public int createUI() {
      Display display = new Display();
      display.addListener( SWT.Dispose, new Listener() {
        public void handleEvent( Event event ) {
          try {
            Graphics.stringExtent( event.display.getSystemFont(), "foo" );
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

  public static final class EventProcessingOnSessionRestartEntryPoint implements EntryPoint {
    public int createUI() {
      createUIEntered = true;
      try {
        Display display = new Display();
        final Shell shell = new Shell( display );
        shell.addDisposeListener( new DisposeListener() {
          public void widgetDisposed( DisposeEvent event ) {
            eventLog.add(  event );
          }
        } );
        UISession uiSession = RWT.getUISession();
        uiSession.addUISessionListener( new UISessionListener() {
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
    public int createUI() {
      Display display = new Display();
      Shell shell = new Shell( display );
      final UISession uiSession = RWT.getUISession();
      uiSession.addUISessionListener( new UISessionListener() {
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

    public int createUI() {
      createUIEntered = true;
      try {
        Display display = new Display();
        Shell shell = new Shell( display );
        shell.setSize( 100, 100 );
        shell.layout();
        shell.open();
        testShell = shell;
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
