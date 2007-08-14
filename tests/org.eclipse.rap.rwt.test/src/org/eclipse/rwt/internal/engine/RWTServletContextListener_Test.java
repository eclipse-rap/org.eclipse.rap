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

package org.eclipse.rwt.internal.engine;

import javax.servlet.ServletContextEvent;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.Fixture.*;
import org.eclipse.rwt.internal.IInitialization;
import org.eclipse.rwt.internal.browser.Ie6up;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.RWTFixture.TestResourceManager;
import org.eclipse.swt.RWTFixture.TestResourceManagerFactory;
import org.eclipse.swt.widgets.*;


public class RWTServletContextListener_Test extends TestCase {
  
  private static final String ENTRYPOINT 
    = RWTServletContextListener.ENTRYPOINT_PARAM;
  private static final String RESOURCE_MANAGER_FACTORY
    = RWTServletContextListener.RESOURCE_MANAGER_FACTORY_PARAM;
  private static final String ADAPTER_FACTORY
    = RWTServletContextListener.ADAPTER_FACTORY_PARAM;
  private static final String PHASE_LISTENER_PARAM 
    = RWTServletContextListener.PHASE_LISTENER_PARAM;
  private static final String RESOURCE_PARAM 
    = RWTServletContextListener.RESOURCE_PARAM;

  private static String phaseListenerLog = "";

  private String savedLifeCycle;
  
  public static class TestEntryPointWithShell implements IEntryPoint {
    Composite shell;
    public Display createUI() {
      Display display = new Display();
      shell = new Shell( display , SWT.NONE );
      return display;
    }
  }
  
  public static class TestPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;
    public void beforePhase( final PhaseEvent event ) {
      phaseListenerLog += "before";
    }
    public void afterPhase( final PhaseEvent event ) {
      phaseListenerLog += "after";
    }
    public PhaseId getPhaseId() {
      return PhaseId.ANY;
    }
  }
  
  public static class TestResource implements IResource {
    public String getCharset() {
      return null;
    }
    public ClassLoader getLoader() {
      return null;
    }
    public String getLocation() {
      return null;
    }
    public RegisterOptions getOptions() {
      return null;
    }
    public boolean isExternal() {
      return false;
    }
    public boolean isJSLibrary() {
      return false;
    }
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    savedLifeCycle = System.getProperty( IInitialization.PARAM_LIFE_CYCLE );
    System.setProperty( IInitialization.PARAM_LIFE_CYCLE, 
                        RWTLifeCycle.class.getName() );
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
    AdapterFactoryRegistry.clear();
    if( savedLifeCycle != null ) {
      System.setProperty( IInitialization.PARAM_LIFE_CYCLE, savedLifeCycle );
    }
  }
  
  public void testAdapterFactoryRegistryInitialization() {
    RWTServletContextListener listener = new RWTServletContextListener();
    Fixture.TestServletContext servletContext = new TestServletContext();
    String factoryName = TestAdapterFactory.class.getName();
    String adaptableName = TestAdaptable.class.getName();
    String factoryParams =   "\n  "
                           + factoryName
                           + "#" 
                           + adaptableName
                           + ",\n  \t "
                           + factoryName
                           + "#"
                           + adaptableName
                           + "  \n";
    servletContext.setInitParameter( ADAPTER_FACTORY, 
                                     factoryParams );
    listener.contextInitialized( new ServletContextEvent( servletContext ) );
    AdapterFactoryRegistry.register();
    TestAdaptable testAdaptable = new TestAdaptable();
    Runnable runnable = ( Runnable )testAdaptable.getAdapter( Runnable.class );
    assertNotNull( runnable );
    deregisterResourceManager();
  }
  
  public void testResourceManagerInitialization() {
    RWTServletContextListener listener = new RWTServletContextListener();
    Fixture.TestServletContext servletContext = new TestServletContext();
    String factoryName = TestResourceManagerFactory.class.getName();
    servletContext.setInitParameter( RESOURCE_MANAGER_FACTORY, 
                                     factoryName );
    listener.contextInitialized( new ServletContextEvent( servletContext ) );
    assertTrue( ResourceManager.getInstance() instanceof TestResourceManager );
    deregisterResourceManager();
  }

  public void testEntryPointInitialization() {
    // org.eclipse.swt: TestEntryPoint
    RWTServletContextListener listener = new RWTServletContextListener();
    Fixture.TestServletContext servletContext = new TestServletContext();
    servletContext.setInitParameter( ENTRYPOINT, 
                                     TestEntryPointWithShell.class.getName() );
    listener.contextInitialized( new ServletContextEvent( servletContext ) );
    Display display = EntryPointManager.createUI( EntryPointManager.DEFAULT );
    assertNotNull( display );
    display.dispose();
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
    deregisterResourceManager();
    AdapterFactoryRegistry.clear();
    
    // org.eclipse.swt: TestEntryPoint#param1
    String entryPointParam
      = TestEntryPointWithShell.class.getName() + "#param1";
    servletContext.setInitParameter( ENTRYPOINT, entryPointParam );
    listener.contextInitialized( new ServletContextEvent( servletContext ) );
    display = EntryPointManager.createUI( "param1" );
    assertNotNull( display );
    display.dispose();
    EntryPointManager.deregister( "param1" );
    deregisterResourceManager();
    AdapterFactoryRegistry.clear();

    // org.eclipse.swt: TestEntryPoint#param1
    entryPointParam =    "\n  "
                       + TestEntryPointWithShell.class.getName() 
                       + ",\n  \t "
                       + TestEntryPointWithShell.class.getName() 
                       + "#param1"
                       + "  \n";
    servletContext.setInitParameter( ENTRYPOINT, entryPointParam );
    listener.contextInitialized( new ServletContextEvent( servletContext ) );
    display = EntryPointManager.createUI( EntryPointManager.DEFAULT );
    assertNotNull( display );
    display.dispose();
    display = EntryPointManager.createUI( "param1" );
    assertNotNull( display );
    display.dispose();
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
    EntryPointManager.deregister( "param1" );
    deregisterResourceManager();
    AdapterFactoryRegistry.clear();

    // org.eclipse.swt: <null>
    servletContext.setInitParameter( ENTRYPOINT, null );
    listener.contextInitialized( new ServletContextEvent( servletContext ) );
    deregisterResourceManager();
  }
  
  public void testDestroyed() {
    // org.eclipse.swt: TestEntryPoint
    RWTServletContextListener listener = new RWTServletContextListener();
    TestServletContext servletContext = new TestServletContext();
    servletContext.setInitParameter( ENTRYPOINT, 
                                     TestEntryPointWithShell.class.getName() );
    listener.contextInitialized( new ServletContextEvent( servletContext ) );
    listener = new RWTServletContextListener();
    listener.contextDestroyed( new ServletContextEvent( servletContext ) );
    try {
      EntryPointManager.createUI( EntryPointManager.DEFAULT );
      fail( "contextDestroyed did not deregister entry point" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    deregisterResourceManager();
  }
  
  public void testPhaseListenerInitialization() throws Exception  {
    // Reading the phaseListeners from servlet context must pass without a 
    // context (session, request, etc) being present
    ContextProvider.disposeContext();
    // read phase listener from servlet context init parameter
    RWTServletContextListener listener = new RWTServletContextListener();
    TestServletContext servletContext = new TestServletContext();
    servletContext.setInitParameter( PHASE_LISTENER_PARAM,
                                     TestPhaseListener.class.getName() );
    String factoryName = TestResourceManagerFactory.class.getName();
    servletContext.setInitParameter( RESOURCE_MANAGER_FACTORY, 
                                     factoryName );
    listener.contextInitialized( new ServletContextEvent( servletContext ) );
    
    // Prepare and execute a life cycle to ensure that the phase listener
    // was loaded and gets executed
    TestResponse response = new TestResponse();
    TestRequest request = new TestRequest();
    request.setSession( new TestSession() );
    Fixture.fakeContextProvider( response, request );
    EntryPointManager.register( EntryPointManager.DEFAULT, 
                                TestEntryPointWithShell.class );
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6up( true, true ) );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    phaseListenerLog = "";
    lifeCycle.execute();
    assertTrue( phaseListenerLog.length() > 0 );
    
    // Ensure that phase listeners are removed when context is destroyed
    listener = new RWTServletContextListener();
    listener.contextDestroyed( new ServletContextEvent( servletContext ) );
    phaseListenerLog = "";
    ThemeManager.getInstance().initialize();
    lifeCycle.execute();
// TODO [rst] Keeping the ThemeManager initialized speeds up TestSuite
//    ThemeManager.getInstance().deregisterAll();
    assertEquals( "", phaseListenerLog );
    deregisterResourceManager();
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }

  public void testResourceInitialization() throws Exception  {
    ContextProvider.disposeContext();
    // read phase listener from servlet context init parameter
    RWTServletContextListener listener = new RWTServletContextListener();
    TestServletContext servletContext = new TestServletContext();
    servletContext.setInitParameter( RESOURCE_PARAM,
                                     TestResource.class.getName() );
    String factoryName = TestResourceManagerFactory.class.getName();
    servletContext.setInitParameter( RESOURCE_MANAGER_FACTORY, 
                                     factoryName );
    listener.contextInitialized( new ServletContextEvent( servletContext ) );
    
    // Prepare and execute a life cycle to ensure that the phase listener
    // was loaded and gets executed
    TestResponse response = new TestResponse();
    TestRequest request = new TestRequest();
    request.setSession( new TestSession() );
    Fixture.fakeContextProvider( response, request );
    EntryPointManager.register( EntryPointManager.DEFAULT, 
                                TestEntryPointWithShell.class );
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6up( true, true ) );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.execute();

    assertTrue( ResourceRegistry.get()[ 0 ] instanceof TestResource );
    assertTrue( ResourceRegistry.get().length == 1 );
    
    // Ensure that phase listeners are removed when context is destroyed
    listener = new RWTServletContextListener();
    listener.contextDestroyed( new ServletContextEvent( servletContext ) );

    assertTrue( ResourceRegistry.get().length == 0 );

    deregisterResourceManager();
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }

  private void deregisterResourceManager() {
    Fixture.setPrivateField( ResourceManager.class, null, "_instance", null );
    Fixture.setPrivateField( ResourceManager.class, null, "factory", null );
  }
}