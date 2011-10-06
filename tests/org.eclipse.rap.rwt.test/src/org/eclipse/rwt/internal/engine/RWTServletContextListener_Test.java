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

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestResourceManager;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.PhaseEvent;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;


public class RWTServletContextListener_Test extends TestCase {

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

  public static class TestConfigurator implements ApplicationConfigurator {
    public void configure( ApplicationConfiguration configuration ) {
      configuration.addEntryPoint( "ep", TestEntryPoint.class );
      configuration.addPhaseListener( new TestPhaseListener() );
      configuration.addResource( new TestResource() );
      configuration.addBranding( new TestBranding() );
    }
  }

  public void testResourceManagerInitialization() {
    Fixture.triggerServletContextInitialized();

    checkTestResourceManagerHasBeenRegistered();
  }

  public void testEntryPointInitialization() {
    setEntryPointInitParameter();
    
    triggerServletContextInitialized();

    checkEntryPointHasBeenRegistered();
  }

  public void testEntryPointInitializationWithNonExistingClassName() {
    Fixture.setInitParameter( RWTServletContextListener.ENTRY_POINTS_PARAM, "does.not.Exist" );
    
    try {
      triggerServletContextInitialized();
      fail();
    } catch( IllegalArgumentException expected ) {
    }
    Fixture.createServiceContext();
  }

  public void testConfigurator() {
    setConfiguratorInitParameter();
    
    triggerServletContextInitialized();

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

  private void triggerServletContextInitialized() {
    ServletContext servletContext = Fixture.getServletContext();
    ServletContextEvent event = new ServletContextEvent( servletContext );
    new RWTServletContextListener().contextInitialized( event );
  }

  private void checkTestResourceManagerHasBeenRegistered() {
    Fixture.createServiceContext();
    assertTrue( RWT.getResourceManager() instanceof TestResourceManager );
  }

  private void checkEntryPointHasBeenRegistered() {
    Fixture.createServiceContext();
    assertEquals( 1, RWTFactory.getEntryPointManager().getEntryPoints().length );
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
    assertEquals( 3, RWTFactory.getPhaseListenerRegistry().getAll().length );
  }

  private void setEntryPointInitParameter() {
    String name = RWTServletContextListener.ENTRY_POINTS_PARAM;
    String value = TestEntryPoint.class.getName();
    Fixture.setInitParameter( name, value );
  }

  private void setConfiguratorInitParameter() {
    String name = ApplicationConfigurator.CONFIGURATOR_PARAM;
    String value = TestConfigurator.class.getName();
    Fixture.setInitParameter( name, value );
  }
}