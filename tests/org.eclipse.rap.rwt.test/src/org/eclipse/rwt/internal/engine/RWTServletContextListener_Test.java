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
import org.eclipse.rwt.engine.Configurator;
import org.eclipse.rwt.engine.Context;
import org.eclipse.rwt.internal.engine.configurables.*;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;


public class RWTServletContextListener_Test extends TestCase {
  
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

  public static class TestEntryPoint implements IEntryPoint {
    public int createUI() {
      return 0;
    }
  }
  
  public static class TestPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;
    
    public void beforePhase( final PhaseEvent event ) {
    }
    
    public void afterPhase( final PhaseEvent event ) {
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
  
  public static class TestConfigurator implements Configurator {
    public void configure( Context context ) {
      context.addEntryPoint( "ep", TestEntryPoint.class );
      context.addPhaseListener( new TestPhaseListener() );
      context.addResource( new TestResource() );
      context.addBranding( new TestBranding() );
    }
  }
  
  public void testResourceManagerInitialization() {
    Fixture.triggerServletContextInitialized();
    Fixture.createServiceContext();
    
    assertTrue( RWT.getResourceManager() instanceof TestResourceManager );
  }

  public void testEntryPointInitialization() {
    setEntryPointInitParameter();
    triggerServletContextInitialized();
    Fixture.createServiceContext();
    
    checkEntryPointHasBeenRegistered();
  }
    
  public void testPhaseListenerInitialization()  {
    setPhaseListenerInitParameter();
    triggerServletContextInitialized();
    Fixture.createServiceContext();
    
    checkPhaseListenerHasBeenRegistered();
  }

  private void triggerServletContextInitialized() {
    ServletContext servletContext = Fixture.getServletContext();
    ServletContextEvent event = new ServletContextEvent( servletContext );
    new RWTServletContextListener().contextInitialized( event );

  }

  public void testResourceInitialization() {
    setResourceInitParameter();
    triggerServletContextInitialized();
    Fixture.createServiceContext();
    
    checkResourceHasBeenRegistered();
  }

  public void testBrandingInitialization() {
    setBrandingInitParameter();
    triggerServletContextInitialized();
    Fixture.createServiceContext();
    
    checkBrandingHasBeenRegistered();
  }
  
  public void testCreateConfigurables() {
    TestServletContext context = new TestServletContext();
    
    Configurable[] configurables = new ConfigurablesProvider().createConfigurables( context );
    
    assertEquals( 9, configurables.length );
  }
  
  public void testConfigurationLifeCyle() {
    TestConfigurable configurable = new TestConfigurable();

    initializeServletContext( configurable );
    destroyServletContext();

    assertTrue( configurable.isConfigured() );
    assertTrue( configurable.isReset() );
  }
  
  public void testConfigurator() {
    setConfiguratorInitParameter();
    triggerServletContextInitialized();
    Fixture.createServiceContext();
    
    checkEntryPointHasBeenRegistered();
    checkPhaseListenersHaveBeenRegistered();
    checkResourceHasBeenRegistered();
    checkBrandingHasBeenRegistered();
  }

  protected void tearDown() {
    if( Fixture.getServletContext() != null ) {
      Fixture.triggerServletContextDestroyed();
      Fixture.disposeOfServiceContext();
      Fixture.disposeOfServletContext();
    }
  }

  private void checkEntryPointHasBeenRegistered() {
    assertEquals( 1, RWTFactory.getEntryPointManager().getEntryPoints().length );
  }

  private void checkPhaseListenerHasBeenRegistered() {
    assertEquals( 1, RWTFactory.getPhaseListenerRegistry().get().length );
    assertTrue( RWTFactory.getPhaseListenerRegistry().get()[ 0 ] instanceof TestPhaseListener );
  }
  
  private void checkResourceHasBeenRegistered() {
    assertTrue( RWTFactory.getResourceRegistry().get()[ 0 ] instanceof TestResource );
    assertEquals( 1, RWTFactory.getResourceRegistry().get().length );
  }
  
  private void checkBrandingHasBeenRegistered() {
    AbstractBranding[] allBrandings = RWTFactory.getBrandingManager().getAll();
    assertEquals( 1, allBrandings.length );
    assertEquals( TestBranding.class, allBrandings[ 0 ].getClass() );
  }

  private void checkPhaseListenersHaveBeenRegistered() {
    assertEquals( 3, RWTFactory.getPhaseListenerRegistry().get().length );
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
    ConfigurablesProvider configurablesProvider = new ConfigurablesProvider() {
      Configurable[] createConfigurables( ServletContext servletContext ) {
        Configurable[] configurables = super.createConfigurables( servletContext );
        Configurable[] result = new Configurable[ configurables.length + 1 ];
        System.arraycopy( configurables, 0, result, 0, configurables.length );
        result[ configurables.length ] = testConfigurable;
        return result;
      }
    };
    return new RWTServletContextListener( configurablesProvider );
  }

  private void setEntryPointInitParameter() {
    String name = EntryPointManagerConfigurable.ENTRY_POINTS_PARAM;
    String value = TestEntryPoint.class.getName();
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
  
  private void setConfiguratorInitParameter() {
    String name = ContextConfigurable.CONFIGURATOR_PARAM;
    String value = TestConfigurator.class.getName();
    Fixture.setInitParameter( name, value );
  }
}