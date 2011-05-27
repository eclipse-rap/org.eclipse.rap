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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public class RWTServletContextListener_Test extends TestCase {
  private static String phaseListenerLog = "";
  
  private static class TestConfigurable implements Configurable {
    private boolean configured;
    private boolean reset;
    public void configure( ApplicationContext context ) {
      configured = true;
    }

    public void reset( ApplicationContext context ) {
      reset = true;
    }

    boolean isConfigured() {
      return configured;
    }

    
    boolean isReset() {
      return reset;
    }
  }

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
    setResourceManagerFactoryInitParameter();
    Fixture.triggerServletContextInitialized();
    Fixture.createServiceContext();
    assertTrue( RWT.getResourceManager() instanceof TestResourceManager );
  }

  public void testEntryPointInitialization() {
    setEntryPointInitParameter();
    Fixture.triggerServletContextInitialized();
    Fixture.createServiceContext();
    assertEquals( 1, RWTFactory.getEntryPointManager().getEntryPoints().length );
  }
    
  public void testPhaseListenerInitialization()  {
    setPhaseListenerInitParameter();
    Fixture.triggerServletContextInitialized();
    Fixture.createServiceContext();
    assertEquals( 1, RWTFactory.getPhaseListenerRegistry().get().length );
    assertTrue( RWTFactory.getPhaseListenerRegistry().get()[ 0 ] instanceof TestPhaseListener );
  }

  public void testResourceInitialization() {
    setResourceInitParameter();
    Fixture.triggerServletContextInitialized();
    Fixture.createServiceContext();
    assertTrue( RWTFactory.getResourceRegistry().get()[ 0 ] instanceof TestResource );
    assertEquals( 1, RWTFactory.getResourceRegistry().get().length );
  }

  public void testBrandingInitialization() {
    setBrandingInitParameter();
    Fixture.triggerServletContextInitialized();
    Fixture.createServiceContext();
    AbstractBranding[] allBrandings = RWTFactory.getBrandingManager().getAll();
    assertEquals( 1, allBrandings.length );
    assertEquals( TestBranding.class, allBrandings[ 0 ].getClass() );
  }
  
  public void testCreateConfigurables() {
    RWTServletContextListener listener = new RWTServletContextListener();
    
    Configurable[] configurables = listener.createConfigurables( new TestServletContext() );
    
    assertEquals( 10, configurables.length );
  }
  
  public void testConfigurationLifeCyle() {
    TestConfigurable configurable = new TestConfigurable();

    initializeServletContext( configurable );
    destroyServletContext();

    assertTrue( configurable.isConfigured() );
    assertTrue( configurable.isReset() );
  }

  protected void tearDown() {
    if( Fixture.getServletContext() != null ) {
      Fixture.triggerServletContextDestroyed();
      Fixture.disposeOfServiceContext();
      Fixture.disposeOfServletContext();
    }
  }


  private void destroyServletContext() {
    Fixture.triggerServletContextDestroyed();
    Fixture.disposeOfServletContext();
  }

  private void initializeServletContext( Configurable configurable ) {
    RWTServletContextListener listener = createListener( configurable );
    ServletContext servletContext = Fixture.createServletContext();
    listener.contextInitialized( new ServletContextEvent( servletContext ) );
  }

  private RWTServletContextListener createListener( final Configurable testConfigurable ) {
    return new RWTServletContextListener() {
      protected Configurable[] createConfigurables( ServletContext servletContext ) {
        Configurable[] configurables = super.createConfigurables( servletContext );
        Configurable[] result = new Configurable[ configurables.length + 1 ];
        System.arraycopy( configurables, 0, result, 0, configurables.length );
        result[ configurables.length ] = testConfigurable;
        return result;
      }
    };
  }

  private void setResourceManagerFactoryInitParameter() {
    String name = ResourceManagerProviderConfigurable.RESOURCE_MANAGER_FACTORY_PARAM;
    String value = TestResourceManagerFactory.class.getName();
    Fixture.setInitParameter( name, value );
  }

  private void setEntryPointInitParameter() {
    String name = EntryPointManagerConfigurable.ENTRY_POINTS_PARAM;
    String value = TestEntryPointWithShell.class.getName();
    Fixture.setInitParameter( name, value );
  }
  
  private void setPhaseListenerInitParameter() {
    String name = PhaseListenerRegistryConfigurable.PHASE_LISTENERS_PARAM;
    String value = TestPhaseListener.class.getName();
    Fixture.setInitParameter( name, value );
  }

  private void setResourceInitParameter() {
    String name = ResourceRegistryConfigurable.RESOURCES_PARAM;
    String value = TestResource.class.getName();
    Fixture.setInitParameter( name, value );
  }

  private void setBrandingInitParameter() {
    String name = BrandingManagerConfigurable.BRANDINGS_PARAM;
    String value = TestBranding.class.getName();
    Fixture.setInitParameter( name, value );
  }
}