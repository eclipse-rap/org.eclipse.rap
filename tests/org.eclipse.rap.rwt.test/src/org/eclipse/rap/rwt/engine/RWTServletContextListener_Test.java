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
package org.eclipse.rap.rwt.engine;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.lifecycle.TestEntryPoint;
import org.eclipse.rap.rwt.lifecycle.PhaseEvent;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestServletContext;


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
    servletContext.setInitParameter( ApplicationConfiguration.CONFIGURATION_PARAM, className );

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
    servletContext.setInitParameter( ApplicationConfiguration.CONFIGURATION_PARAM, className );

    rwtServletContextListener.contextInitialized( contextInitializedEvent );

    assertEntryPointIsRegistered();
    assertPhaseListenersAreRegistered();
  }

  @SuppressWarnings( "unchecked" )
  public void testConfiguratorWithThreadContextClassLoader() throws ClassNotFoundException {
    // See bug 367033
    // use a class name that cannot be found by RWT's class loader
    servletContext.setInitParameter( ApplicationConfiguration.CONFIGURATION_PARAM, "not.Existing" );
    ClassLoader previousContextClassLoader = Thread.currentThread().getContextClassLoader();
    Class configuratorClass = TestConfigurator.class;
    // set a context class loader that can find the class
    ClassLoader contextClassLoader = mock( ClassLoader.class );
    when( contextClassLoader.loadClass( anyString() ) ).thenReturn( configuratorClass );

    Thread.currentThread().setContextClassLoader( contextClassLoader );
    try {
      rwtServletContextListener.contextInitialized( contextInitializedEvent );
    } finally {
      Thread.currentThread().setContextClassLoader( previousContextClassLoader );
    }

    verify( contextClassLoader ).loadClass( "not.Existing" );
  }

  private void assertResourceManagerIsRegistered() {
    ApplicationContextImpl applicationContext = ApplicationContextUtil.get( servletContext );
    assertNotNull( applicationContext.getResourceManager() );
  }

  private void assertEntryPointIsRegistered() {
    ApplicationContextImpl applicationContext = ApplicationContextUtil.get( servletContext );
    EntryPointManager entryPointManager = applicationContext.getEntryPointManager();
    assertEquals( 1, entryPointManager.getServletPaths().size() );
  }

  private void assertPhaseListenersAreRegistered() {
    ApplicationContextImpl applicationContext = ApplicationContextUtil.get( servletContext );
    assertEquals( 2, applicationContext.getPhaseListenerRegistry().getAll().length );
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

  private static class TestConfigurator implements ApplicationConfiguration {
    public void configure( Application configuration ) {
      configuration.addEntryPoint( "/test", TestEntryPoint.class, null );
      configuration.addPhaseListener( mock( PhaseListener.class ) );
    }
  }
}
