/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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
package org.eclipse.rwt.engine;

import static org.mockito.Mockito.mock;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestServletContext;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.internal.application.ApplicationContext;
import org.eclipse.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.PhaseEvent;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;


public class RWTServletContextListener_Test extends TestCase {

  private RWTServletContextListener rwtServletContextListener;
  private ServletContext servletContext;
  private ServletContextEvent contextInitializedEvent;

  @Override
  protected void setUp() throws Exception {
    Fixture.useTestResourceManager();
    rwtServletContextListener = new RWTServletContextListener();
    servletContext = new TestServletContext();
    contextInitializedEvent = new ServletContextEvent( servletContext );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.useDefaultResourceManager();
  }

  public void testResourceManagerIsInitialized() {
    String className = TestConfigurator.class.getName();
    servletContext.setInitParameter( ApplicationConfigurator.CONFIGURATOR_PARAM, className );

    rwtServletContextListener.contextInitialized( contextInitializedEvent );

    assertResourceManagerIsRegistered();
  }

  public void testEntryPointInitialization() {
    String className = TestEntryPoint.class.getName();
    servletContext.setInitParameter( RWTServletContextListener.ENTRY_POINTS_PARAM, className );

    rwtServletContextListener.contextInitialized( contextInitializedEvent );

    assertEntryPointIsRegistered();
  }

  public void testEntryPointInitializationWithNonExistingClassName() {
    String className = "does.not.Exist";
    servletContext.setInitParameter( RWTServletContextListener.ENTRY_POINTS_PARAM, className );

    try {
      rwtServletContextListener.contextInitialized( contextInitializedEvent );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testConfigurator() {
    String className = TestConfigurator.class.getName();
    servletContext.setInitParameter( ApplicationConfigurator.CONFIGURATOR_PARAM, className );

    rwtServletContextListener.contextInitialized( contextInitializedEvent );

    assertEntryPointIsRegistered();
    assertPhaseListenersAreRegistered();
    assertResourceIsRegistered();
  }

  private void assertResourceManagerIsRegistered() {
    ApplicationContext applicationContext = ApplicationContextUtil.get( servletContext );
    assertNotNull( applicationContext.getResourceManager() );
  }

  private void assertEntryPointIsRegistered() {
    ApplicationContext applicationContext = ApplicationContextUtil.get( servletContext );
    EntryPointManager entryPointManager = applicationContext.getEntryPointManager();
    assertEquals( 1, entryPointManager.getServletPaths().size() );
  }

  private void assertPhaseListenersAreRegistered() {
    ApplicationContext applicationContext = ApplicationContextUtil.get( servletContext );
    assertEquals( 2, applicationContext.getPhaseListenerRegistry().getAll().length );
  }

  private void assertResourceIsRegistered() {
    ApplicationContext applicationContext = ApplicationContextUtil.get( servletContext );
    IResource[] resources = applicationContext.getResourceRegistry().get();
    assertEquals( 1, resources.length );
    assertEquals( TestResource.class, resources[ 0 ].getClass() );
  }

  public static class TestEntryPoint implements IEntryPoint {
    public int createUI() {
      return 0;
    }
  }

  public static class TestPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1L;

    public void beforePhase( PhaseEvent event ) {
    }

    public void afterPhase( PhaseEvent event ) {
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
      return true;
    }

    public boolean isJSLibrary() {
      return false;
    }
  }

  private static class TestConfigurator implements ApplicationConfigurator {
    public void configure( ApplicationConfiguration configuration ) {
      configuration.addEntryPoint( "/test", TestEntryPoint.class );
      configuration.addPhaseListener( mock( PhaseListener.class ) );
      configuration.addResource( new TestResource() );
    }
  }
}
