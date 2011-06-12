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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.http.*;

import org.eclipse.rwt.internal.engine.*;
import org.eclipse.rwt.internal.engine.configurables.AdapterManagerConfigurable;
import org.eclipse.rwt.internal.engine.configurables.PhaseListenerRegistryConfigurable;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.rwt.internal.resources.SystemProps;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

public class Fixture {
  public final static File TEMP_DIR = new File( System.getProperty( "java.io.tmpdir" ) );
  public static final File WEB_CONTEXT_DIR = new File( TEMP_DIR, "testapp" );
  public static final File WEB_CONTEXT_RWT_RESOURCES_DIR
    = new File( WEB_CONTEXT_DIR, ResourceManagerImpl.RESOURCES );
  public static final String IMAGE1 = "resources/images/image1.gif";
  public static final String IMAGE2 = "resources/images/image2.gif";
  public static final String IMAGE3 = "resources/images/image3.gif";
  public static final String IMAGE_100x50 = "resources/images/test-100x50.png";
  public static final String IMAGE_50x100 = "resources/images/test-50x100.png";
  
  private static final String SYS_PROP_USE_PERFORMANCE_OPTIMIZATIONS
    = "usePerformanceOptimizations";
  
  static {
    ThemeManagerHelper.replaceStandardResourceLoader();
    setIgnoreResourceRegistration( usePerformanceOptimizations() );
    setIgnoreResourceDeletion( usePerformanceOptimizations() );
    setIgnoreServiceHandlerRegistration( usePerformanceOptimizations() );
  }
  
  private static TestServletContext servletContext;
  
  ////////////////////////////////////////////
  // Methods to control global servlet context
  
  public static TestServletContext createServletContext() {
    servletContext = new TestServletContext();
    Fixture.useTestResourceManager();
    return getServletContext();
  }
  
  public static TestServletContext getServletContext() {
    return servletContext;
  }

  public static void disposeOfServletContext() {
    servletContext = null;
  }
  public static void setInitParameter( String name, String value ) {
    ensureServletContext();
    servletContext.setInitParameter( name, value );
  }
  
  public static void triggerServletContextInitialized() {
    ensureServletContext();
    ServletContextEvent event = new ServletContextEvent( servletContext );
    new RWTServletContextListener().contextInitialized( event );
  }
  
  public static void triggerServletContextDestroyed() {
    ServletContextEvent event = new ServletContextEvent( servletContext );
    new RWTServletContextListener().contextDestroyed( event );
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
    createWebContextDirectories();
    triggerServletContextInitialized();
  }
  
  public static void disposeOfApplicationContext() {
    triggerServletContextDestroyed();
    disposeOfServletContext();
    // TODO [ApplicationContext]: At the time beeing this improves RWTAllTestSuite performance by 
    //      50% on my machine without causing any test to fail. However this has a bad smell
    //      with it, so I introduced a flag that can be switch on for fast tests on local machines 
    //      and switched of for the integration build tests. Think about a less intrusive solution.
    if( !usePerformanceOptimizations() ) {
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

  public static void createServiceContext( HttpServletResponse response, 
                                           HttpServletRequest request ) 
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
    ThemeManagerHelper.resetThemeManagerIfNeeded();
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
    registerLifeCycleAdapterFactory();
    useTestResourceManager();
    registerCurrentPhaseListener();
    setSystemProperties();
    createApplicationContext();
    createServiceContext();
  }

  private static void registerLifeCycleAdapterFactory() {
    String factory = TestLifeCycleAdapterFactory.class.getName();
    String adaptable1 = Widget.class.getName();
    String adaptable2 = Display.class.getName();
    String split = RWTServletContextListener.PARAMETER_SPLIT;
    String separator = RWTServletContextListener.PARAMETER_SEPARATOR;
    String value = factory + split + adaptable1 + separator + factory + split + adaptable2;
    setInitParameter( AdapterManagerConfigurable.ADAPTER_FACTORIES_PARAM, value );
  }

  private static void registerCurrentPhaseListener() {
    String initParam = PhaseListenerRegistryConfigurable.PHASE_LISTENERS_PARAM;
    setInitParameter( initParam, CurrentPhase.Listener.class.getName() );
  }
  
  public static void useDefaultResourceManager() {
    ApplicationContextHelper.useDefaultResourceManager();
  }
  
  public static void useTestResourceManager() {
    ApplicationContextHelper.useTestResourceManager();
  }

  public static void tearDown() {
    disposeOfServiceContext();
    disposeOfApplicationContext();
    disposeOfServletContext();
    unsetSystemProperties();
  }
  

  ////////////////////
  // LifeCycle helpers
  
  public static void readDataAndProcessAction( Display display ) {
    IDisplayLifeCycleAdapter displayLCA = DisplayUtil.getLCA( display );
    fakePhase( PhaseId.READ_DATA );
    displayLCA.readData( display );
    Fixture.preserveWidgets();
    fakePhase( PhaseId.PROCESS_ACTION );
    while( Display.getCurrent().readAndDispatch() ) {
    }
  }

  public static void readDataAndProcessAction( Widget widget ) {
    AbstractWidgetLCA widgetLCA = WidgetUtil.getLCA( widget );
    fakePhase( PhaseId.READ_DATA );
    widgetLCA.readData( widget );
    fakePhase( PhaseId.PROCESS_ACTION );
    while( Display.getCurrent().readAndDispatch() ) {
    }
  }

  public static void markInitialized( Widget widget ) {
    Object adapter = widget.getAdapter( IWidgetAdapter.class );
    WidgetAdapter widgetAdapter = ( WidgetAdapter )adapter;
    widgetAdapter.setInitialized( true );
  }

  public static void markInitialized( Display display ) {
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

  public static void fakeRequestParam( String key, String value ) {
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

  public static void fakePhase( PhaseId phase ) {
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
  
  public static boolean usePerformanceOptimizations() {
    return Boolean.getBoolean( SYS_PROP_USE_PERFORMANCE_OPTIMIZATIONS );
  }

  public static void setIgnoreServiceHandlerRegistration( boolean usePerformanceOptimizations ) {
    ApplicationContextHelper.setIgnoreServiceHandlerRegistration( usePerformanceOptimizations );
  }

  public static void setIgnoreResourceDeletion( boolean usePerformanceOptimizations ) {
    ApplicationContextHelper.setIgnoreResoureDeletion( usePerformanceOptimizations );
  }

  public static void setIgnoreResourceRegistration( boolean usePerformanceOptimizations ) {
    ApplicationContextHelper.setIgnoreResoureRegistration( usePerformanceOptimizations );
  }

  public static void copyTestResource( String resourceName, File destination ) throws IOException {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream is = loader.getResourceAsStream( resourceName );
    if( is == null ) {
      throw new IllegalArgumentException( "Resource could not be found: " + resourceName );
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

  public static Thread[] startThreads( int threadCount, Runnable runnable ) {
    List<Thread> threads = new ArrayList<Thread>();
    for( int i = 0; i < threadCount; i++ ) {
      Thread thread = new Thread( runnable );
      thread.setDaemon( true );
      thread.start();
      threads.add( thread );
      Thread.yield();
    }
    Thread[] result = new Thread[ threads.size() ];
    threads.toArray( result );
    return result;
  }

  public static void joinThreads( Thread[] threads ) throws InterruptedException {
    for( int i = 0; i < threads.length; i++ ) {
      Thread thread = threads[ i ];
      thread.join();
    }
  }

  public static void delete( File toDelete ) {
    ApplicationContextUtil.delete( toDelete );
  }

  public static byte[] serialize( Object object ) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream( outputStream );
    objectOutputStream.writeObject( object );
    return outputStream.toByteArray();
  }
  
  public static Object deserialize( byte[] bytes ) throws IOException, ClassNotFoundException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream( bytes );
    ObjectInputStream objectInputStream = new ObjectInputStream( inputStream );
    return objectInputStream.readObject();
  }
  
  @SuppressWarnings("unchecked")
  public static <T> T serializeAndDeserialize( T instance ) 
    throws IOException, ClassNotFoundException 
  {
    byte[] bytes = serialize( instance );
    return ( T )deserialize( bytes );
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

  private Fixture() {
    // prevent instantiation
  }
}