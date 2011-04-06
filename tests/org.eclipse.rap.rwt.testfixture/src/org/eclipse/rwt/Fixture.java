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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.*;

import org.eclipse.rwt.internal.AdapterFactoryRegistry;
import org.eclipse.rwt.internal.engine.*;
import org.eclipse.rwt.internal.engine.RWTServletContextListener.ContextDestroyer;
import org.eclipse.rwt.internal.engine.RWTServletContextListener.ContextInitializer;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.SystemProps;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.internal.theme.ThemeManagerInstance;
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

    // TODO [RWTContext]: Replacing ThemeManagerInstance improves performance
    //                    of RWTAllTestSuite. Think about a less 
    //                    intrusive solution.
    RWTContextUtil.replace( ThemeManagerInstance.class,
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
  
  
  ////////////////////////////////
  // Methods to control RWTContext
  
  public static void createRWTContext() {
    ensureServletContext();
    createRWTContext( new ContextInitializer( servletContext ) );
  }

  public static void createRWTContext( Runnable initializer ) {
    createWebContextDirectories();
    ensureServletContext();
    RWTContext rwtContext = RWTContextUtil.createRWTContext();
    RWTContextUtil.registerRWTContext( servletContext, rwtContext );
    RWTContextUtil.runWithInstance( rwtContext, initializer );
  }
  
  public static void disposeOfRWTContext() {
    ContextDestroyer destroyer = new ContextDestroyer( servletContext );
    disposeOfRWTContext( destroyer );
  }

  public static void disposeOfRWTContext( Runnable destroyer ) {
    RWTContext rwtContext = RWTContextUtil.getRWTContext( servletContext );
    RWTContextUtil.runWithInstance( rwtContext, destroyer );
    RWTContextUtil.deregisterRWTContext( servletContext );
    disposeOfServletContext();
    // TODO [RWTContext]: At the time beeing this improves RWTAllTestSuite
    //                    performance by 50% on my machine without causing
    //                    any test to fail. However this has a bad smell
    //                    with it, so I introduced a flag that can be switch
    //                    on for fast tests on local machines and switched
    //                    of for the integration build tests. Think about
    //                    a less intrusive solution.
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
    createRWTContext();
    createServiceContext();
    AdapterFactoryRegistry.register();
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
    disposeOfRWTContext();
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
    displayLCA.processAction( display );
  }

  public static void readDataAndProcessAction( final Widget widget ) {
    AbstractWidgetLCA widgetLCA = WidgetUtil.getLCA( widget );
    fakePhase( PhaseId.READ_DATA );
    widgetLCA.readData( widget );
    fakePhase( PhaseId.PROCESS_ACTION );
    Display display = widget.getDisplay();
    IDisplayLifeCycleAdapter displayLCA = DisplayUtil.getLCA( display );
    displayLCA.processAction( display );
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
    PreserveWidgetsPhaseListener listener = new PreserveWidgetsPhaseListener();
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    PhaseEvent event = new PhaseEvent( lifeCycle, PhaseId.READ_DATA );
    listener.afterPhase( event );
  }

  public static void clearPreserved() {
    PreserveWidgetsPhaseListener listener = new PreserveWidgetsPhaseListener();
    ILifeCycle lifeCycle = LifeCycleFactory.getLifeCycle();
    PhaseEvent event = new PhaseEvent( lifeCycle, PhaseId.RENDER );
    listener.afterPhase( event );
  }
  
  public static String getAllMarkup() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    JavaScriptResponseWriter writer = stateInfo.getResponseWriter();
    StringWriter recorder = new StringWriter();
    writer.printContents( new PrintWriter( recorder ) );
    return recorder.getBuffer().toString();
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
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setResponseWriter( new JavaScriptResponseWriter() );
  }

  public static void fakePhase( final PhaseId phase ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setAttribute( CurrentPhase.class.getName() + "#value", phase );
  }
  
  public static void executeLifeCycleFromServerThread() {
    IUIThreadHolder threadHolder = registerCurrentThreadAsUIThreadHolder();
    Thread serverThread = fakeRequestThread( threadHolder );
    simulateRequest( threadHolder, serverThread );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    while( RWTLifeCycle.getSessionDisplay().readAndDispatch() ) {
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

  private static void simulateRequest( IUIThreadHolder threadHolder,
                                       Thread serverThread )
  {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    synchronized( threadHolder.getLock() ) {
      serverThread.start();
      try {
        lifeCycle.sleep();
      } catch( ThreadDeath e ) {
        throw new RuntimeException( e );
      }
    }
  }

  private static Thread fakeRequestThread( final IUIThreadHolder threadHolder )
  {
    final RWTLifeCycle lifeCycle
      = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
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
    session.setAttribute( RWTLifeCycle.UI_THREAD, result );
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
    } catch( IllegalStateException noRWTContextAvailable ) {
    }
    return result;
  }

  private Fixture() {
    // prevent instantiation
  }
}
