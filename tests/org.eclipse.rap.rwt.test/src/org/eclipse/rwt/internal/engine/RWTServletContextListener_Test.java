/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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

package org.eclipse.rwt.internal.engine;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class RWTServletContextListener_Test extends TestCase {
  
  private static final String ENTRYPOINT 
    = RWTServletContextListener.ENTRY_POINTS_PARAM;
  private static final String RESOURCE_MANAGER_FACTORY
    = RWTServletContextListener.RESOURCE_MANAGER_FACTORY_PARAM;
  private static final String PHASE_LISTENER_PARAM 
    = RWTServletContextListener.PHASE_LISTENERS_PARAM;
  private static final String RESOURCE_PARAM 
    = RWTServletContextListener.RESOURCES_PARAM;
  private static final String BRANDING_PARAM
    = RWTServletContextListener.BRANDINGS_PARAM;
  private static final int ENTRY_POINT_RETURN_VALUE = -15;

  private static String phaseListenerLog = "";

  public static class TestEntryPointWithShell implements IEntryPoint {
    
    Composite shell;
    public int createUI() {
      Display display = new Display();
      shell = new Shell( display , SWT.NONE );
      return ENTRY_POINT_RETURN_VALUE;
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
  
  public static class TestBranding extends AbstractBranding {
  }
  
  public void testResourceManagerInitialization() {
    String factoryName = TestResourceManagerFactory.class.getName();
    Fixture.setInitParameter( RESOURCE_MANAGER_FACTORY, factoryName );
    Fixture.triggerServletContextInitialized();

    Fixture.createServiceContext();
    assertTrue( RWT.getResourceManager() instanceof TestResourceManager );
  }

  public void testEntryPointInitialization() {
    String entryPointName = TestEntryPointWithShell.class.getName();
    Fixture.setInitParameter( ENTRYPOINT, entryPointName );
    Fixture.triggerServletContextInitialized();

    Fixture.createServiceContext();
    int returnVal = RWTFactory.getEntryPointManager().createUI( EntryPointManager.DEFAULT );
    assertEquals( ENTRY_POINT_RETURN_VALUE, returnVal );
  }
  
  public void testEntryPointInitializationWithParam() {
    String param = TestEntryPointWithShell.class.getName() + "#param1";
    Fixture.setInitParameter( ENTRYPOINT, param );
    Fixture.triggerServletContextInitialized();

    Fixture.createServiceContext();
    int returnVal = RWTFactory.getEntryPointManager().createUI( "param1" );
    assertEquals( -15, returnVal );
  }
  
  public void testMultipleEntryPointInitializations() {
    String entryPointParam 
      =   "\n  "
        + TestEntryPointWithShell.class.getName() 
        + ",\n  \t "
        + TestEntryPointWithShell.class.getName() 
        + "#param1"
        + "  \n";
    Fixture.setInitParameter( ENTRYPOINT, entryPointParam );
    Fixture.triggerServletContextInitialized();

    Fixture.createServiceContext();
    int returnVal = RWTFactory.getEntryPointManager().createUI( EntryPointManager.DEFAULT );
    assertEquals( -15, returnVal );
    Display.getCurrent().dispose();
    returnVal = RWTFactory.getEntryPointManager().createUI( "param1" );
    assertEquals( ENTRY_POINT_RETURN_VALUE, returnVal );
  }
  
  public void testEntryPointInitializationNullParam() {
    Fixture.setInitParameter( ENTRYPOINT, null );
    Fixture.triggerServletContextInitialized();
    
    Fixture.createServiceContext();
    assertEquals( 0, RWTFactory.getEntryPointManager().getEntryPoints().length );
  }
  
  public void testDestroyed() {
    String entryPointType = TestEntryPointWithShell.class.getName();
    Fixture.setInitParameter( ENTRYPOINT, entryPointType );
    Fixture.triggerServletContextInitialized();

    Fixture.createServiceContext();
    TestServletContext servletContext = Fixture.getServletContext();
    RWTServletContextListener.deregisterEntryPoints( servletContext );
    try {
      RWTFactory.getEntryPointManager().createUI( EntryPointManager.DEFAULT );
      fail( "contextDestroyed did not deregister entry point" );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testPhaseListenerInitialization() throws Exception  {
    String phaseListenerName = TestPhaseListener.class.getName();
    Fixture.setInitParameter( PHASE_LISTENER_PARAM, phaseListenerName );
    String factoryName = TestResourceManagerFactory.class.getName();
    Fixture.setInitParameter( RESOURCE_MANAGER_FACTORY, factoryName );
    Fixture.triggerServletContextInitialized();
    
    // Prepare and execute a life cycle to ensure that the phase listener
    // was loaded and gets executed
    Fixture.createServiceContext();
    Class entryPointType = TestEntryPointWithShell.class;
    RWTFactory.getEntryPointManager().register( EntryPointManager.DEFAULT, entryPointType );
    Fixture.fakeResponseWriter();
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    phaseListenerLog = "";
    lifeCycle.execute();
    assertTrue( phaseListenerLog.length() > 0 );
    
    // Ensure that phase listeners are removed from the registry when context 
    // is destroyed
    TestServletContext servletContext = Fixture.getServletContext();
    RWTServletContextListener.deregisterPhaseListeners( servletContext );
    PhaseListener[] phaseListeners = RWTFactory.getPhaseListenerRegistry().get();
    for( int i = 0; i < phaseListeners.length; i++ ) {
      if( phaseListeners[ i ] instanceof TestPhaseListener ) {
        fail( "Failed to remove phase listener when context was destroyed" );
      }
    }
  }

  public void testResourceInitialization() throws Exception  {
    Fixture.setInitParameter( RESOURCE_PARAM, TestResource.class.getName() );
    String factoryName = TestResourceManagerFactory.class.getName();
    Fixture.setInitParameter( RESOURCE_MANAGER_FACTORY, factoryName );
    Fixture.triggerServletContextInitialized();
    
    // Prepare and execute a life cycle to ensure that the phase listener
    // was loaded and gets executed
    Fixture.createServiceContext();
    Class entryPointType = TestEntryPointWithShell.class;
    RWTFactory.getEntryPointManager().register( EntryPointManager.DEFAULT, entryPointType );
    Fixture.fakeResponseWriter();
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.execute();

    assertTrue( RWTFactory.getResourceRegistry().get()[ 0 ] instanceof TestResource );
    assertTrue( RWTFactory.getResourceRegistry().get().length == 1 );
    
    // Ensure that phase listeners are removed when context is destroyed
    TestServletContext servletContext = Fixture.getServletContext();
    RWTServletContextListener.deregisterResources( servletContext );
    
    assertTrue( RWTFactory.getResourceRegistry().get().length == 0 );
  }

  public void testBrandingInitialization() {
    String brandingType = TestBranding.class.getName();
    Fixture.setInitParameter( BRANDING_PARAM, brandingType );
    Fixture.triggerServletContextInitialized();
    
    Fixture.createServiceContext();
    AbstractBranding[] allBrandings = RWTFactory.getBrandingManager().getAll();
    assertEquals( 1, allBrandings.length );
    assertEquals( TestBranding.class, allBrandings[ 0 ].getClass() );

    TestServletContext servletContext = Fixture.getServletContext();
    RWTServletContextListener.deregisterBrandings( servletContext );
    assertEquals( 0, RWTFactory.getBrandingManager().getAll().length );
  }

  protected void setUp() throws Exception {
    Fixture.setServletContextListener( new RWTServletContextListener() );
  }

  protected void tearDown() throws Exception {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfServletContext();
    Fixture.setServletContextListener( null );
  }
}