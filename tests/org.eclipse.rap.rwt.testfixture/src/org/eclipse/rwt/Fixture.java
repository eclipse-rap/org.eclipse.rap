/*******************************************************************************
 * Copyright (c) 2002, 2011 EclipseSource and others.
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
package org.eclipse.rwt;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.eclipse.rwt.internal.engine.*;
import org.eclipse.rwt.internal.engine.RWTServletContextListener.ContextDestroyer;
import org.eclipse.rwt.internal.engine.RWTServletContextListener.ContextInitializer;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.SystemProps;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.internal.theme.ThemeManagerHolder;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

public class Fixture {
  public final static File TEMP_DIR 
    = new File( System.getProperty( "java.io.tmpdir" ) );
  public static final File WEB_CONTEXT_DIR = new File( TEMP_DIR, "testapp" );
  public static final String IMAGE1 = "resources/images/image1.gif";
  public static final String IMAGE2 = "resources/images/image2.gif";
  public static final String IMAGE3 = "resources/images/image3.gif";
  public static final String IMAGE_100x50 = "resources/images/test-100x50.png";
  public static final String IMAGE_50x100 = "resources/images/test-50x100.png";
  
  private static final String SYS_PROP_USE_PERFORMANCE_OPTIMIZATIONS
    = "usePerformanceOptimizations";

  static {
    usePerformanceOptimizations
      = Boolean.getBoolean( SYS_PROP_USE_PERFORMANCE_OPTIMIZATIONS );

    // TODO [ApplicationContext]: Replacing ThemeManagerInstance improves performance of 
    //      RWTAllTestSuite. Think about a less intrusive solution.
    ApplicationContextUtil.replace( ThemeManagerHolder.class,
                            ThemeManagerSingletonFactory.class );
  }

  private static TestServletContext servletContext;
  private static ServletContextListener servletContextListener;
  private static boolean usePerformanceOptimizations;
  
  ////////////////////////////////////////////
  // Methods to control global servlet context
  
  public static TestServletContext createServletContext() {
    servletContext = new TestServletContext();
    return getServletContext();
  }
  
  public static TestServletContext getServletContext() {
    return servletContext;
  }

  public static void disposeOfServletContext() {
    servletContext = null;
  }
  public static void setInitParameter( final String name, final String value ) {
    ensureServletContext();
    servletContext.setInitParameter( name, value );
  }
  
  public static void triggerServletContextInitialized() {
    ensureServletContext();
    ServletContextEvent event = new ServletContextEvent( servletContext );
    servletContextListener.contextInitialized( event );
  }
  
  public static void triggerServletContextDestroyed() {
    ServletContextEvent event = new ServletContextEvent( servletContext );
    servletContextListener.contextDestroyed( event );
  }
  
  public static void setServletContextListener( ServletContextListener lsnr ) {
    servletContextListener = lsnr;
  }

  private static void ensureServletContext() {
    if( servletContext == null ) {
      createServletContext();
    }
  }
  
  
  ////////////////////////////////////////
  // Methods to control ApplicationContext
  
  public static void createApplicationContext() {
    ensureServletContext();
    createApplicationContext( new ContextInitializer( servletContext ) );
  }

  public static void createApplicationContext( Runnable initializer ) {
    createWebContextDirectories();
    ensureServletContext();
    ApplicationContext applicationContext = ApplicationContextUtil.createApplicationContext();
    ApplicationContextUtil.registerApplicationContext( servletContext, applicationContext );
    ApplicationContextUtil.runWithInstance( applicationContext, initializer );
  }
  
  public static void disposeOfApplicationContext() {
    ContextDestroyer destroyer = new ContextDestroyer( servletContext );
    disposeOfApplicationContext( destroyer );
  }

  public static void disposeOfApplicationContext( Runnable destroyer ) {
    ApplicationContext applicationContext
      = ApplicationContextUtil.getApplicationContext( servletContext );
    ApplicationContextUtil.runWithInstance( applicationContext, destroyer );
    ApplicationContextUtil.deregisterApplicationContext( servletContext );
    disposeOfServletContext();
    // TODO [ApplicationContext]: At the time beeing this improves RWTAllTestSuite performance by 
    //      50% on my machine without causing any test to fail. However this has a bad smell
    //      with it, so I introduced a flag that can be switch on for fast tests on local machines 
    //      and switched of for the integration build tests. Think about a less intrusive solution.
    if( !usePerformanceOptimizations ) {
      deleteWebContextDirectories();
    }
  }

  
  ////////////////////////////////////
  // Methods to control ServiceContext
  
  public static void createServiceContext() {
    TestRequest request = new TestRequest();
    TestResponse response = new TestResponse();
    TestSession session = createTestSession();
    request.setSession( session );
    createServiceContext( response, request );
  }

  public static void createServiceContext( final HttpServletResponse response, 
                                           final HttpServletRequest request ) 
  {
    ServiceContext context = new ServiceContext( request, response );
    ServiceStateInfo stateInfo = new ServiceStateInfo();
    context.setStateInfo( stateInfo );
    ContextProvider.setContext( context );
  }

  private static TestSession createTestSession() {
    TestSession result = new TestSession();
    if( servletContext != null ) {
      result.setServletContext( servletContext );
    }
    return result;
  }

  public static void disposeOfServiceContext() {
    resetThemeManager();
    HttpSession session = ContextProvider.getRequest().getSession();
    ContextProvider.disposeContext();
    session.invalidate();
  }

  
  /////////////////////////////////////////////////////////////////////
  // Methods to control web context directories and resource management

  public static void createWebContextDirectories() {
    WEB_CONTEXT_DIR.mkdirs();
    File webInf = new File( WEB_CONTEXT_DIR, "WEB-INF" );
    webInf.mkdirs();
    File conf = new File( webInf, "conf" );
    conf.mkdirs();
    File classes = new File( webInf, "classes" );
    classes.mkdirs();
    File libDir = new File( webInf, "lib" );
    libDir.mkdirs();
  }

  public static void deleteWebContextDirectories() {
    if( WEB_CONTEXT_DIR.exists() ) {
      delete( WEB_CONTEXT_DIR );
    }
  }
  
  
  //////////////////////////////
  // general setup and tear down

  public static void setUp() {
    registerResourceManagerFactory();
    registerCurrentPhaseListener();
    setSystemProperties();
    createApplicationContext();
    createServiceContext();
  }

  private static void registerCurrentPhaseListener() {
    String initParam = RWTServletContextListener.PHASE_LISTENERS_PARAM;
    setInitParameter( initParam, CurrentPhase.Listener.class.getName() );
  }

  public static void registerResourceManagerFactory() {
    String initParam = RWTServletContextListener.RESOURCE_MANAGER_FACTORY_PARAM;
    setInitParameter( initParam, TestResourceManagerFactory.class.getName() );
  }

  public static void tearDown() {
    disposeOfServiceContext();
    disposeOfApplicationContext();
    disposeOfServletContext();
    unsetSystemProperties();
  }
  

  ////////////////////
  // LifeCycle helpers
  
  public static void readDataAndProcessAction( final Display display ) {
    IDisplayLifeCycleAdapter displayLCA = DisplayUtil.getLCA( display );
    fakePhase( PhaseId.READ_DATA );
    displayLCA.readData( display );
    Fixture.preserveWidgets();
    fakePhase( PhaseId.PROCESS_ACTION );
    while( Display.getCurrent().readAndDispatch() ) {
    }
  }

  public static void readDataAndProcessAction( final Widget widget ) {
    AbstractWidgetLCA widgetLCA = WidgetUtil.getLCA( widget );
    fakePhase( PhaseId.READ_DATA );
    widgetLCA.readData( widget );
    fakePhase( PhaseId.PROCESS_ACTION );
    while( Display.getCurrent().readAndDispatch() ) {
    }
  }

  public static void markInitialized( final Widget widget ) {
    Object adapter = widget.getAdapter( IWidgetAdapter.class );
    WidgetAdapter widgetAdapter = ( WidgetAdapter )adapter;
    widgetAdapter.setInitialized( true );
  }

  public static void markInitialized( final Display display ) {
    Object adapter = display.getAdapter( IWidgetAdapter.class );
    WidgetAdapter widgetAdapter = ( WidgetAdapter )adapter;
    widgetAdapter.setInitialized( true );
  }

  public static void preserveWidgets() {
    Display display = LifeCycleUtil.getSessionDisplay();
    IDisplayLifeCycleAdapter displayLCA = DisplayUtil.getLCA( display );
    PhaseId bufferedPhaseId = CurrentPhase.get();
    fakePhase( PhaseId.READ_DATA );
    displayLCA.preserveValues( display );
    fakePhase( bufferedPhaseId );
  }

  public static void clearPreserved() {
    Display display = LifeCycleUtil.getSessionDisplay();
    IDisplayLifeCycleAdapter displayLCA = DisplayUtil.getLCA( display );
    PhaseId bufferedPhaseId = CurrentPhase.get();
    fakePhase( PhaseId.RENDER );
    displayLCA.clearPreserved( display );
    fakePhase( bufferedPhaseId );
  }
  
  public static String getAllMarkup() {
    TestResponse response = ( TestResponse )ContextProvider.getResponse();
    return response.getContent();
  }

  public static void fakeNewRequest( Display display ) {
    fakeNewRequest();
    fakeRequestParam( RequestParams.UIROOT, DisplayUtil.getId( display ) );
  }

  public static void fakeNewRequest() {
    HttpSession session = ContextProvider.getRequest().getSession();
    TestRequest request = new TestRequest();
    request.setSession( session );
    TestResponse response = new TestResponse();
    ServiceContext serviceContext = new ServiceContext( request, response );
    serviceContext.setStateInfo( new ServiceStateInfo() );
    ContextProvider.disposeContext();
    ContextProvider.setContext( serviceContext );
    fakeResponseWriter();
    LifeCycleServiceHandler.initializeSession();
  }

  public static void fakeRequestParam( final String key, final String value ) {
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setParameter( key, value );
  }

  public static void fakeResponseWriter() {
    PrintWriter writer;
    try {
      TestResponse testResponse = ( TestResponse )ContextProvider.getResponse();
      testResponse.clearContent();
      writer = ContextProvider.getResponse().getWriter();
    } catch( IOException exception ) {
      throw new RuntimeException( exception );
    }
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setResponseWriter( new JavaScriptResponseWriter( writer ) );
  }

  public static void fakePhase( final PhaseId phase ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setAttribute( CurrentPhase.class.getName() + "#value", phase );
  }
  
  public static void executeLifeCycleFromServerThread() {
    IUIThreadHolder threadHolder = registerCurrentThreadAsUIThreadHolder();
    Thread serverThread = fakeRequestThread( threadHolder );
    simulateRequest( threadHolder, serverThread );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    while( LifeCycleUtil.getSessionDisplay().readAndDispatch() ) {
    }
    lifeCycle.sleep();
  }
  
  
  ////////////////
  // general stuff

  public static void copyTestResource( final String resourceName, 
                                       final File destination )
    throws FileNotFoundException, IOException
  {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream is = loader.getResourceAsStream( resourceName );
    if( is == null ) {
      String msg = "Resource could not be found: " + resourceName;
      throw new IllegalArgumentException( msg );
    }
    BufferedInputStream bis = new BufferedInputStream( is );
    try {
      OutputStream out = new FileOutputStream( destination );
      BufferedOutputStream bout = new BufferedOutputStream( out );
      try {
        int c = bis.read();
        while( c != -1 ) {
          bout.write( c );
          c = bis.read();
        }
      } finally {
        bout.close();
      }
    } finally {
      bis.close();
    }
  }

  public static void unsetSystemProperties() {
    System.getProperties().remove( SystemProps.USE_VERSIONED_JAVA_SCRIPT );
  }
  
  public static void setSystemProperties() {
    // disable js-versioning by default to make comparison easier
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "false" );
  }

  public static void runInThread( final Runnable runnable ) throws Throwable {
    final Object lock = new Object();
    final Throwable[] exception = { null };
    Runnable exceptionGuard = new Runnable() {
      public void run() {
        try {
          runnable.run();
        } catch( Throwable thr ) {
          synchronized( lock ) {
            exception[ 0 ] = thr;
          }
        }
      }
    };
    Thread thread = new Thread( exceptionGuard );
    thread.setDaemon( true );
    thread.start();
    thread.join();
    synchronized( lock ) {
      if( exception[ 0 ] != null ) {
        throw exception[ 0 ];
      }
    }
  }

  public static void delete( final File toDelete ) {
    if( toDelete.exists() ) {
      doDelete( toDelete );
    }
  }

  
  //////////////////
  // helping methods
  
  private static void doDelete( final File toDelete ) {
    if( toDelete.isDirectory() ) {
      File[] children = toDelete.listFiles();
      for( int i = 0; i < children.length; i++ ) {
        delete( children[ i ] );
      }
    }
    boolean deleted = toDelete.delete();
    if( !deleted ) {
      String msg = "Could not delete: " + toDelete.getPath();
      throw new IllegalStateException( msg );
    }
  }

  private static void simulateRequest( IUIThreadHolder threadHolder, Thread serverThread ) {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    synchronized( threadHolder.getLock() ) {
      serverThread.start();
      try {
        lifeCycle.sleep();
      } catch( ThreadDeath e ) {
        throw new RuntimeException( e );
      }
    }
  }

  private static Thread fakeRequestThread( final IUIThreadHolder threadHolder ) {
    final RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    final ServiceContext context = ContextProvider.getContext();
    Thread result = new Thread( new Runnable() {
      public void run() {
        synchronized( threadHolder.getLock() ) {
          ContextProvider.setContext( context );
          try {
            try {
              lifeCycle.execute();
              lifeCycle.setPhaseOrder( null );
            } catch( IOException e ) {
              throw new RuntimeException( e );
            }
          } finally {
            ContextProvider.releaseContextHolder();
            threadHolder.notifyAll();
          }
        }
      }
    }, "ServerThread" );
    return result;
  }

  private static IUIThreadHolder registerCurrentThreadAsUIThreadHolder() {
    final IUIThreadHolder result = new IUIThreadHolder() {
      private Thread thread = Thread.currentThread();
  
      public void setServiceContext( ServiceContext serviceContext ) {
      }
      public void switchThread() {
        synchronized( getLock() ) {
          notifyAll();
          try {
            wait();
          } catch( InterruptedException e ) {
            throw new RuntimeException( e );
          }
        }
      }
      public void updateServiceContext() {
      }
      public void terminateThread() {
      }
      public Thread getThread() {
        return thread;
      }
      public Object getLock() {
        return this;
      }
    };
    ISessionStore session = ContextProvider.getSession();
    LifeCycleUtil.setUIThread( session, result );
    return result;
  }

  private static void resetThemeManager() {
    if( isThemeManagerAvailable() )
    {
      ThemeManager.resetInstance();
    }
  }

  private static boolean isThemeManagerAvailable() {
    return    getThemeManager() != null 
           && getThemeManager().getRegisteredThemeIds().length != 1;
  }

  private static ThemeManager getThemeManager() {
    ThemeManager result = null;
    try {
      result = ThemeManager.getInstance();
    } catch( IllegalStateException noApplicationContextAvailable ) {
    }
    return result;
  }

  private Fixture() {
    // prevent instantiation
  }
}
