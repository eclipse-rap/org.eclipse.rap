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
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class RWTServletContextListener_Test extends TestCase {
  private static final String ENTRYPOINT = RWTServletContextListener.ENTRY_POINTS_PARAM;
  private static final String RESOURCE_MANAGER_FACTORY
    = RWTServletContextListener.RESOURCE_MANAGER_FACTORY_PARAM;
  private static final String PHASE_LISTENER_PARAM 
    = RWTServletContextListener.PHASE_LISTENERS_PARAM;
  private static final String RESOURCE_PARAM 
    = RWTServletContextListener.RESOURCES_PARAM;
  private static final String BRANDINGS_PARAM
    = RWTServletContextListener.BRANDINGS_PARAM;
  private static String phaseListenerLog = "";

  
  public static class TestEntryPointWithShell implements IEntryPoint {
    Composite shell;
    
    public int createUI() {
      Display display = new Display();
      shell = new Shell( display , SWT.NONE );
      return 0;
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
    Fixture.setInitParameter( ENTRYPOINT, TestEntryPointWithShell.class.getName() );
    Fixture.triggerServletContextInitialized();
    Fixture.createServiceContext();
    assertEquals( 1, RWTFactory.getEntryPointManager().getEntryPoints().length );
  }
    
  public void testPhaseListenerInitialization() throws Exception  {
    Fixture.setInitParameter( PHASE_LISTENER_PARAM, TestPhaseListener.class.getName() );
    Fixture.triggerServletContextInitialized();
    Fixture.createServiceContext();
    assertEquals( 1, RWTFactory.getPhaseListenerRegistry().get().length );
    assertTrue( RWTFactory.getPhaseListenerRegistry().get()[ 0 ] instanceof TestPhaseListener );
  }

  public void testResourceInitialization() throws Exception  {
    Fixture.setInitParameter( RESOURCE_PARAM, TestResource.class.getName() );
    Fixture.triggerServletContextInitialized();
    Fixture.createServiceContext();
    assertTrue( RWTFactory.getResourceRegistry().get()[ 0 ] instanceof TestResource );
    assertEquals( 1, RWTFactory.getResourceRegistry().get().length );
  }

  public void testBrandingInitialization() {
    String brandingType = TestBranding.class.getName();
    Fixture.setInitParameter( BRANDINGS_PARAM, brandingType );
    Fixture.triggerServletContextInitialized();
    Fixture.createServiceContext();
    AbstractBranding[] allBrandings = RWTFactory.getBrandingManager().getAll();
    assertEquals( 1, allBrandings.length );
    assertEquals( TestBranding.class, allBrandings[ 0 ].getClass() );
  }

  protected void tearDown() throws Exception {
    Fixture.triggerServletContextDestroyed();
    Fixture.disposeOfServletContext();
    Fixture.registerResourceManagerFactory();
  }
}