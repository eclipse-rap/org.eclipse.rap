/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import org.eclipse.rwt.Adaptable;
import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.Fixture.*;
import org.eclipse.rwt.internal.AdapterManager;
import org.eclipse.rwt.internal.AdapterManagerImpl;
import org.eclipse.rwt.internal.browser.Ie6;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.JsConcatenator;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManagerFactory;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.internal.widgets.WidgetAdapterFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


public final class RWTFixture {

  public static final class TestResourceManagerFactory
    implements IResourceManagerFactory
  {
    public IResourceManager create() {
      return new TestResourceManager();
    }
  }

  public final static class TestResourceManager
    implements IResourceManager, Adaptable
  {

    private ClassLoader loader = Thread.currentThread().getContextClassLoader();

    public Object getAdapter( final Class adapter ) {
      return new JsConcatenator() {
        public void startJsConcatenation() {
        }
        public String getContent() {
          return "";
        }
        public String getLocation() {
          return "";
        }
      };
    }

    public String getCharset( final String name ) {
      return null;
    }

    public ClassLoader getContextLoader() {
      return loader;
    }

    public String getLocation( final String name ) {
      return null;
    }

    public URL getResource( final String name ) {
      URL result = null;
      if( loader != null ) {
        result = loader.getResource( name );
      }
      return result;
    }

    public InputStream getResourceAsStream( final String name ) {
      InputStream result = null;
      if( loader != null ) {
        result = loader.getResourceAsStream( name );
      }
      return result;
    }

    public Enumeration getResources( final String name ) throws IOException {
      Enumeration result = null;
      if( loader != null ) {
        result = loader.getResources( name );
      }
      return result;
    }

    public boolean isRegistered( final String name ) {
      return false;
    }

    public void register( final String name ) {
    }

    public void register( final String name, final InputStream is ) {
    }

    public void register( final String name, final String charset ) {
    }

    public void register( final String name,
                          final String charset,
                          final RegisterOptions options )
    {
    }

    public void register( String name,
                          InputStream is,
                          String charset,
                          RegisterOptions options )
    {
    }

    public void setContextLoader( final ClassLoader contextLoader ) {
      loader = contextLoader;
    }

  }

  public static class TestEntryPoint implements IEntryPoint {
    public Display createUI() {
      return new Display();
    }
  }

  public static final String IMAGE1 = "resources/images/image1.gif";
  public static final String IMAGE2 = "resources/images/image2.gif";
  public static final String IMAGE3 = "resources/images/image3.gif";
  public static final String IMAGE_100x50 = "resources/images/test-100x50.png";
  public static final String IMAGE_50x100 = "resources/images/test-50x100.png";

  private static LifeCycleAdapterFactory lifeCycleAdapterFactory;
  private static WidgetAdapterFactory widgetAdapterFactory;
  private static PhaseListener currentPhaseListener
    = new CurrentPhase.Listener();

  private RWTFixture() {
    // prevent instantiation
  }

  public static void setUp() {
    // standard setup
    Fixture.setUp();

    ThemeManager.getInstance().initialize();
    registerAdapterFactories();
    PhaseListenerRegistry.add( currentPhaseListener );

    // registration of mockup resource manager
    registerResourceManager();

    fakeUIThread();
  }

  public static void fakeUIThread() {
    RWTLifeCycle.setThread( Thread.currentThread() );
  }

  public static void setUpWithoutResourceManager() {
    // standard setup
    Fixture.setUp();
    LifeCycleServiceHandler.configurer = null;

    // registration of adapter factories
    registerAdapterFactories();
  }

  public static void tearDown() {
    removeUIThread();

    // deregistration of mockup resource manager
    deregisterResourceManager();

    // deregistration of adapter factories
    deregisterAdapterFactories();
// TODO [rst] Keeping the ThemeManager initialized speeds up TestSuite
//    ThemeManager.getInstance().deregisterAll();

    // clear Graphics resources
    ResourceFactory.clear();

    // standard teardown
    Fixture.tearDown();
  }

  public static void removeUIThread() {
    RWTLifeCycle.setThread( null );
  }

  public static void registerAdapterFactories() {
    AdapterManager manager = AdapterManagerImpl.getInstance();
    lifeCycleAdapterFactory = new LifeCycleAdapterFactory();
    manager.registerAdapters( lifeCycleAdapterFactory, Display.class );
    manager.registerAdapters( lifeCycleAdapterFactory, Widget.class );
    widgetAdapterFactory = new WidgetAdapterFactory();
    manager.registerAdapters( widgetAdapterFactory, Display.class );
    manager.registerAdapters( widgetAdapterFactory, Widget.class );
  }

  public static void deregisterAdapterFactories() {
    AdapterManager manager = AdapterManagerImpl.getInstance();
    manager.deregisterAdapters( widgetAdapterFactory, Display.class );
    manager.deregisterAdapters( widgetAdapterFactory, Widget.class );
    manager.deregisterAdapters( lifeCycleAdapterFactory, Display.class );
    manager.deregisterAdapters( lifeCycleAdapterFactory, Widget.class );
  }

  public static void registerResourceManager() {
    ResourceManager.register( new TestResourceManagerFactory() );
    // clear Graphics resources
    ResourceFactory.clear();
  }

  public static void deregisterResourceManager() {
    Fixture.setPrivateField( ResourceManager.class, null, "_instance", null );
    Fixture.setPrivateField( ResourceManager.class, null, "factory", null );
  }

  public static void preserveWidgets() {
    PreserveWidgetsPhaseListener listener = new PreserveWidgetsPhaseListener();
    PhaseEvent event = new PhaseEvent( new RWTLifeCycle(), PhaseId.READ_DATA );
    listener.afterPhase( event );
  }

  public static void clearPreserved() {
    PreserveWidgetsPhaseListener listener = new PreserveWidgetsPhaseListener();
    PhaseEvent event = new PhaseEvent( new RWTLifeCycle(), PhaseId.RENDER );
    listener.afterPhase( event );
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

  public static void fakeNewRequest() {
    HttpSession session = ContextProvider.getRequest().getSession();
    TestRequest request = new TestRequest();
    request.setSession( session );
    TestResponse response = new TestResponse();
    ServiceContext serviceContext = new ServiceContext( request, response );
    serviceContext.setStateInfo( new ServiceStateInfo() );
    ContextProvider.disposeContext();
    ContextProvider.setContext( serviceContext );
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6( true, true ) );
  }

  public static void fakePhase( final PhaseId phase ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setAttribute( CurrentPhase.class.getName() + "#value",
                            phase );
  }

  public static void readDataAndProcessAction( final Widget widget ) {
    AbstractWidgetLCA widgetLCA = WidgetUtil.getLCA( widget );
    RWTFixture.fakePhase( PhaseId.READ_DATA );
    widgetLCA.readData( widget );
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = widget.getDisplay();
    IDisplayLifeCycleAdapter displayLCA = DisplayUtil.getLCA( display );
    displayLCA.processAction( display );
  }

  public static void readDataAndProcessAction( final Display display ) {
    IDisplayLifeCycleAdapter displayLCA = DisplayUtil.getLCA( display );
    RWTFixture.fakePhase( PhaseId.READ_DATA );
    displayLCA.readData( display );
    preserveWidgets();
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    displayLCA.processAction( display );

  }

  public static void fakeContext() {
    TestRequest request = new TestRequest();
    TestResponse response = new TestResponse();
    TestSession session = new TestSession();
    request.setSession( session );
    ServiceContext context = new ServiceContext( request, response );
    ContextProvider.setContext( context );
  }
}
