/*******************************************************************************
 * Copyright (c) 2010, 2011 Innoopract Informationssysteme GmbH.
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
package org.eclipse.rwt.internal.lifecycle;

import java.util.Enumeration;
import java.util.LinkedList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.engine.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManagerFactory;
import org.eclipse.rwt.service.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

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
  private static java.util.List eventLog;
  private static PhaseId currentPhase;

  private TestSession session;

  public static class FakeResourceManagerFactory
    implements IResourceManagerFactory
  {

    public IResourceManager create() {
      return new TestResourceManager() {
        public Enumeration getResources( String name ) {
          return null;
        }
      };
    }
  }

  public static final class ExceptionInReadAndDispatchEntryPoint 
    implements IEntryPoint 
  {
    public int createUI() {
      createUIEntered = true;
      Display display = new Display();
      try {
        display.addListener( SWT.Dispose, new Listener() {
          public void handleEvent( final Event event ) {
            eventLog.add( event );
          }
        } );
        Shell shell = new Shell( display );
        shell.setLayout( new FillLayout() );
        Button maliciousButton = new Button( shell, SWT.PUSH );
        maliciousButton.addSelectionListener( new SelectionAdapter() {
          public void widgetSelected( SelectionEvent e ) {
            HttpSession httpSession = RWT.getSessionStore().getHttpSession();
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
  
  public static final class EventProcessingOnSessionRestartEntryPoint 
    implements IEntryPoint 
  {
    public int createUI() {
      createUIEntered = true;
      try {
        Display display = new Display();
        final Shell shell = new Shell( display );
        shell.addDisposeListener( new DisposeListener() {
          public void widgetDisposed( final DisposeEvent event ) {
            eventLog.add(  event );
          }
        } );
        ISessionStore sessionStore = RWT.getSessionStore();
        sessionStore.addSessionStoreListener( new SessionStoreListener() {
          public void beforeDestroy( final SessionStoreEvent event ) {
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

  public static final class TestSessionInvalidateWithDisposeInFinallyEntryPoint
    implements IEntryPoint
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
          SessionSingletonBase.getInstance( this.getClass() );
        } catch( Throwable thr ) {
          eventLog.add( thr );
        }
        display.dispose();
      }
      return 0;
    }
  }
  
  public void testSessionRestartAfterExceptionInUIThread() throws Exception {
    TestRequest request;
    RWTFactory.getEntryPointManager().register( EntryPointManager.DEFAULT, ExceptionInReadAndDispatchEntryPoint.class );
    // send initial request - response is index.html
    request = newRequest();
    request.setParameter( RequestParams.STARTUP, "default" );
    runRWTDelegate( request );
    request = newRequest();
    assertNull( session.getAttribute( TEST_SESSION_ATTRIBUTE ) );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    assertEquals( 0, eventLog.size() );
    
    // send 'application startup' request - response is JavaScript to create
    // client-side representation of what was created in IEntryPoint#createUI
    request = newRequest();
    request.setParameter( RequestParams.UIROOT, "w1" );
    runRWTDelegate( request );
    assertNull( session.getAttribute( TEST_SESSION_ATTRIBUTE ) );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    assertEquals( 0, eventLog.size() );
    
    // send 'malicious button click' - response is HTTP 500
    request = newRequest();
    request.setParameter( RequestParams.UIROOT, "w1" );
    request.setParameter( JSConst.EVENT_WIDGET_SELECTED, maliciousButtonId );
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
    
    // send 'refresh' request - session is restarted, response is index.html
    request = newRequest();
    request.setParameter( RequestParams.STARTUP, "default" );
    runRWTDelegate( request );
    assertEquals( 1, eventLog.size() );
    assertTrue( eventLog.get( 0 ) instanceof Event );
  }
  
  public void testEventProcessingOnSessionRestart() throws Exception {
    TestRequest request;
    Class entryPoint = EventProcessingOnSessionRestartEntryPoint.class;
    RWTFactory.getEntryPointManager().register( EntryPointManager.DEFAULT, entryPoint );
    // send initial request - response is index.html
    request = newRequest();
    request.setParameter( RequestParams.STARTUP, "default" );
    runRWTDelegate( request );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    // send 'application startup' request - response is JavaScript to create
    // client-side representation of what was created in IEntryPoint#createUI
    request = newRequest();
    request.setParameter( RequestParams.UIROOT, "w1" );
    runRWTDelegate( request );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    // send 'restart' request
    request = newRequest();
    request.setParameter( RequestParams.STARTUP, "default" );
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
    Class clazz = TestSessionInvalidateWithDisposeInFinallyEntryPoint.class;
    RWTFactory.getEntryPointManager().register( EntryPointManager.DEFAULT, clazz );
    // send initial request - response is index.html
    request = newRequest();
    request.setParameter( RequestParams.STARTUP, "default" );
    runRWTDelegate( request );
    assertTrue( createUIEntered );
    // send 'application startup' request - response is JavaScript to create
    // client-side representation of what was created in IEntryPoint#createUI
    request = newRequest();
    request.setParameter( RequestParams.UIROOT, "w1" );
    runRWTDelegate( request );
    assertTrue( createUIEntered );
    assertFalse( createUIExited );
    // send 'restart' request
    request = newRequest();
    request.setParameter( RequestParams.STARTUP, "default" );
    runRWTDelegate( request );
    assertTrue( createUIExited );
    assertEquals( PhaseId.PROCESS_ACTION, currentPhase );
    assertEquals( 0, eventLog.size() );
  }
  
  private static TestResponse runRWTDelegate( final HttpServletRequest request ) 
    throws Exception 
  {
    final Exception[] exception = { null };
    final TestResponse[] response = { newResponse() };
    Runnable runnable = new Runnable() {
      public void run() {
        synchronized( this ) {
          //
        }
        try {
          RWTDelegate delegate = new RWTDelegate();
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

  private static TestResponse newResponse() {
    TestResponse result = new TestResponse();
    TestServletOutputStream outputStream = new TestServletOutputStream();
    result.setOutputStream( outputStream );
    return result;
  }
  
  private TestRequest newRequest() {
    TestRequest result = new TestRequest();
    result.setSession( session );
    result.setParameter( LifeCycleServiceHandler.RWT_INITIALIZE, "true" );
    return result;
  }
  
  protected void setUp() throws Exception {
    Fixture.setSystemProperties();
    registerResourceManagerFactory();
    Fixture.createApplicationContext();
    Fixture.createServiceContext();

    maliciousButtonId = null;
    createUIEntered = false;
    createUIExited = false;
    eventLog = new LinkedList();
    registerTestLogger();
  }

  private void registerTestLogger() {
    session = ( TestSession )ContextProvider.getSession().getHttpSession();
    ServletContext servletContext = session.getServletContext();
    TestServletContext servletContextImpl
      = ( TestServletContext )servletContext;
    servletContextImpl.setLogger( new TestLogger() {
      public void log( final String message, final Throwable throwable ) {
        if( throwable != null ) {
          throwable.printStackTrace();
        }
      }
    } );
  }

  private void registerResourceManagerFactory() {
    // [fappel]: This solves a class loader problem caused by the project 
    //           dependencies:
    //           ServiceManager tries to load a configured service handler 
    //           which is not reachable from the q07 project. The 
    //           service.xml is located in the fixture project and the 
    //           handler implementation is in the rwt.test project, which
    //           is not on this tests classpath. Note that this problem
    //           didn't show up running the RWTAllTestSuite, since
    //           in that case all projects are available on the classpath.
    String initParam = RWTServletContextListener.RESOURCE_MANAGER_FACTORY_PARAM;
    String initValue = FakeResourceManagerFactory.class.getName();
    Fixture.setInitParameter( initParam, initValue );
  }

  protected void tearDown() throws Exception {
    session = null;
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
    Fixture.unsetSystemProperties();
  }
}
