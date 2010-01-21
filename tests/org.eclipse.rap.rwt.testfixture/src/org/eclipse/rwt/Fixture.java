/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt;

import java.io.*;
import java.lang.reflect.Field;

import javax.servlet.http.*;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.*;
import org.eclipse.rwt.internal.branding.BrandingManager;
import org.eclipse.rwt.internal.browser.Browser;
import org.eclipse.rwt.internal.browser.Ie6;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.xml.sax.SAXException;

public class Fixture {

  public final static File TEMP_DIR 
    = new File( System.getProperty( "java.io.tmpdir" ) );
  public final static File CONTEXT_DIR = new File( TEMP_DIR, "testapp" );
  
  public static final String IMAGE1 = "resources/images/image1.gif";
  public static final String IMAGE2 = "resources/images/image2.gif";
  public static final String IMAGE3 = "resources/images/image3.gif";
  public static final String IMAGE_100x50 = "resources/images/test-100x50.png";
  public static final String IMAGE_50x100 = "resources/images/test-50x100.png";

  private static LifeCycleAdapterFactory lifeCycleAdapterFactory;
  private static PhaseListener currentPhaseListener
    = new CurrentPhase.Listener();
  
  private Fixture() {
    // prevent instantiation
  }

  public static void setUp() {
    // standard setup
    commonSetUp();
    System.setProperty( IInitialization.PARAM_LIFE_CYCLE,
                        RWTLifeCycle.class.getName() );
  
    ThemeManager.getInstance().initialize();
    registerAdapterFactories();
    PhaseListenerRegistry.add( Fixture.currentPhaseListener );
  
    // registration of mockup resource manager
    registerResourceManager();
  
    SettingStoreManager.register( new MemorySettingStoreFactory() );
  }

  public static void setUpWithoutResourceManager() {
    // standard setup
    commonSetUp();
    System.setProperty( IInitialization.PARAM_LIFE_CYCLE,
                        RWTLifeCycle.class.getName() );
  
    // registration of adapter factories
    registerAdapterFactories();
  }

  private static void commonSetUp() {
    // disable js-versioning by default to make comparison easier
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "false" );
    clearSingletons();
    try {
      ConfigurationReader.setConfigurationFile( null );
    } catch( Throwable shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
    
    TestResponse response = new TestResponse();
    TestRequest request = new TestRequest();
    request.setSession( new TestSession() );
    fakeContextProvider( response, request );
  }

  public static void tearDown() {
    // deregistration of mockup resource manager
    deregisterResourceManager();
  
    // deregistration of adapter factories
    deregisterAdapterFactories();
    AdapterFactoryRegistry.clear();
  
    // Keep the ThemeManager initialized to speed up the TestSuite
  
    // clear Graphics resources
    ResourceFactory.clear();
  
    // remove all registered entry points
    String[] entryPoints = EntryPointManager.getEntryPoints();
    for( int i = 0; i < entryPoints.length; i++ ) {
      EntryPointManager.deregister( entryPoints[ i ] );
    }
    // standard teardown
    HttpSession session = ContextProvider.getRequest().getSession();
    ContextProvider.disposeContext();
    session.invalidate();
    clearSingletons();
    System.getProperties().remove( IInitialization.PARAM_LIFE_CYCLE );
  
    AbstractBranding[] all = BrandingManager.getAll();
    for( int i = 0; i < all.length; i++ ) {
      BrandingManager.deregister( all[ i ] );
    }
  
    LifeCycleFactory.destroy();
  
    PhaseListenerRegistry.clear();
  }

  public static void clearSingletons() {
    setPrivateField( ResourceManagerImpl.class, null, "_instance", null );
    setPrivateField( LifeCycleFactory.class, null, "globalLifeCycle", null );
    setPrivateField( SettingStoreManager.class, null, "factory", null ); 
  }

  public static void createContext( final boolean fake )
    throws IOException, 
           FactoryConfigurationError, 
           ParserConfigurationException, 
           SAXException
  {
    if( fake ) {
      setPrivateField( ResourceManagerImpl.class,
                       null, 
                       "_instance",
                       new TestResourceManager() );
    } else {
      createContextWithoutResourceManager();
      String webAppBase = CONTEXT_DIR.toString();
      String deliverFromDisk = IInitialization.RESOURCES_DELIVER_FROM_DISK;
      ResourceManagerImpl.createInstance( webAppBase, deliverFromDisk );
    }
  }
  
  public static void createContextWithoutResourceManager()
    throws FileNotFoundException, 
           IOException, 
           FactoryConfigurationError, 
           ParserConfigurationException, 
           SAXException
  {
    CONTEXT_DIR.mkdirs();
    File webInf = new File( CONTEXT_DIR, "WEB-INF" );
    webInf.mkdirs();
    File conf = new File( webInf, "conf" );
    conf.mkdirs();
    File classes = new File( webInf, "classes" );
    classes.mkdirs();
    File libDir = new File( webInf, "lib" );
    libDir.mkdirs();
    File w4tXml = new File( conf, "W4T.xml" );
    copyTestResource( "resources/w4t_fixture.xml", w4tXml );
    
    String webAppBase = CONTEXT_DIR.toString();
    EngineConfig engineConfig = new EngineConfig( webAppBase );
    ConfigurationReader.setEngineConfig( engineConfig );
  }

  public static void removeContext() {
    if( CONTEXT_DIR.exists() ) {
      delete( CONTEXT_DIR );
    }
  }
  
  private static void delete( final File toDelete ) {
    if( toDelete.isDirectory() ) {
      File[] children = toDelete.listFiles();
      for( int i = 0; i < children.length; i++ ) {
        delete( children[ i ] );
      }
    }
    toDelete.delete();
  }

  public static void copyTestResource( final String resourceName, 
                                       final File destination )
    throws FileNotFoundException, IOException
  {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream is = loader.getResourceAsStream( resourceName );
    try {
      OutputStream out = new FileOutputStream( destination );
      try {
        int c = is.read();
        while( c != -1 ) {
          out.write( c );
          c = is.read();
        }
      } finally {
        out.close();
      }
    } finally {
      is.close();
    }
  }
  
  public static File getWebAppBase() throws Exception {
    File result = CONTEXT_DIR;
    if( !result.exists() )  {
      createContextWithoutResourceManager();
      result = CONTEXT_DIR;
    }
    return result;
  }
  
  private static void fakeBrowser( final Browser browser ) {
    ISessionStore session = ContextProvider.getSession();
    session.setAttribute( ServiceContext.DETECTED_SESSION_BROWSER, browser );
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setDetectedBrowser( browser );
  }
  
  public static void fakeRequestParam( final String key, final String value ) {
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setParameter( key, value );
  }
  
  public static void fakeContextProvider( final HttpServletResponse response, 
                                          final HttpServletRequest request ) 
  {
    ServiceContext context = new ServiceContext( request, response );
    ServiceStateInfo stateInfo = new ServiceStateInfo();
    context.setStateInfo( stateInfo );
    ContextProvider.setContext( context );
  }
  
  public static void setPrivateField( final Class clazz, 
                                      final Object object, 
                                      final String fieldName, 
                                      final Object value ) 
  {
    Field[] fields = clazz.getDeclaredFields();
    Field field = null;
    for( int i = 0; field == null && i < fields.length; i++ ) {
      if( fields[ i ].getName().equals( fieldName ) ) {
        field = fields[ i ];
      }
    }
    if ( field == null ) {
      Assert.fail( "Private field "
                   + clazz.getName()
                   + "#"
                   + fieldName
                   + " could not be found." );
    }
    field.setAccessible( true );
    try {
      field.set( object, value );
    } catch( Exception e ) {
      e.printStackTrace();
      Assert.fail( "Failed to set value of private field "
                   + clazz.getName()
                   + "#"
                   + fieldName );
    } 
  }
  
  public static String getAllMarkup() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter writer = stateInfo.getResponseWriter();
    return getAllMarkup( writer );
  }
  
  public static String getAllMarkup( final HtmlResponseWriter writer ) {
    StringBuffer buffer = new StringBuffer();
    buffer.append( getHeadMarkup( writer ) );
    buffer.append( getBodyMarkup( writer ) );
    buffer.append( getFootMarkup( writer ) );
    return buffer.toString();
  }
  
  private static String getHeadMarkup( final HtmlResponseWriter writer ) {
    StringBuffer buffer = new StringBuffer();
    for( int i = 0; i < writer.getHeadSize(); i++ ) {
      buffer.append( writer.getHeadToken( i ) );
    }
    return buffer.toString();
  }
  
  private static String getFootMarkup( final HtmlResponseWriter writer ) {
    StringBuffer buffer = new StringBuffer();
    for( int i = 0; i < writer.getFootSize(); i++ ) {
      buffer.append( writer.getFootToken( i ) );
    }
    return buffer.toString();
  }
  
  private static String getBodyMarkup( final HtmlResponseWriter writer ) {
    StringBuffer buffer = new StringBuffer();
    for( int i = 0; i < writer.getBodySize(); i++ ) {
      buffer.append( writer.getBodyToken( i ) );
    }
    return buffer.toString();
  }
  
  public static void fakeResponseWriter() {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setResponseWriter( writer );
  }

  public static void fakePhase( final PhaseId phase ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setAttribute( CurrentPhase.class.getName() + "#value",
                            phase );
  }

  public static void fakeContext() {
    TestRequest request = new TestRequest();
    TestResponse response = new TestResponse();
    TestSession session = new TestSession();
    request.setSession( session );
    ServiceContext context = new ServiceContext( request, response );
    ContextProvider.setContext( context );
  }

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
    fakeBrowser( new Ie6( true, true ) );
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

  public static void registerResourceManager() {
    ResourceManager.register( new TestResourceManagerFactory() );
    // clear Graphics resources
    ResourceFactory.clear();
  }

  public static void deregisterResourceManager() {
    setPrivateField( ResourceManager.class, null, "_instance", null );
    setPrivateField( ResourceManager.class, null, "factory", null );
  }

  public static void registerAdapterFactories() {
    AdapterManager manager = AdapterManagerImpl.getInstance();
    Fixture.lifeCycleAdapterFactory = new LifeCycleAdapterFactory();
    manager.registerAdapters( Fixture.lifeCycleAdapterFactory, Display.class );
    manager.registerAdapters( Fixture.lifeCycleAdapterFactory, Widget.class );
  }

  public static void deregisterAdapterFactories() {
    AdapterManager manager = AdapterManagerImpl.getInstance();
    manager.deregisterAdapters( Fixture.lifeCycleAdapterFactory, Display.class );
    manager.deregisterAdapters( Fixture.lifeCycleAdapterFactory, Widget.class );
  }

  public static void executeLifeCycleFromServerThread() {
    final RWTLifeCycle lifeCycle
      = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    final IUIThreadHolder threadHolder = new IUIThreadHolder() {
      private Thread thread = Thread.currentThread();
  
      public void setServiceContext( ServiceContext serviceContext ) {
      }
      public void switchThread() throws InterruptedException {
        synchronized( getLock() ) {
          notifyAll();
          wait();
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
    session.setAttribute( RWTLifeCycle.UI_THREAD, threadHolder );
  
    final ServiceContext context = ContextProvider.getContext();
    Thread serverThread = new Thread( new Runnable() {
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
  
    synchronized( threadHolder.getLock() ) {
      serverThread.start();
      try {
        lifeCycle.sleep();
      } catch( ThreadDeath e ) {
        throw new RuntimeException( e );
      }
    }
  
    while( RWTLifeCycle.getSessionDisplay().readAndDispatch() ) {
    }
  
    lifeCycle.sleep();
  }
}