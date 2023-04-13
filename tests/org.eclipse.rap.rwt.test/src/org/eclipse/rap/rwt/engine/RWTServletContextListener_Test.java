/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.engine.RWTServletContextListener.ENTRY_POINTS_PARAM;
import static org.eclipse.rap.rwt.engine.RWTServletContextListener.RWT_SERVLET_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rap.rwt.internal.lifecycle.TestEntryPoint;
import org.eclipse.rap.rwt.testfixture.internal.TestServletContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class RWTServletContextListener_Test {

  private RWTServletContextListener rwtServletContextListener;
  private ServletContext servletContext;
  private ServletContextEvent contextInitializedEvent;

  @Before
  public void setUp() {
    rwtServletContextListener = new RWTServletContextListener();
    servletContext = new TestServletContext();
    contextInitializedEvent = new ServletContextEvent( servletContext );
  }

  @Test
  public void testResourceManagerIsInitialized() {
    String className = TestConfiguration.class.getName();
    servletContext.setInitParameter( ApplicationConfiguration.CONFIGURATION_PARAM, className );

    rwtServletContextListener.contextInitialized( contextInitializedEvent );

    assertResourceManagerIsRegistered();
  }

  @Test
  public void testEntryPointInitialization() {
    String className = TestEntryPoint.class.getName();
    servletContext.setInitParameter( ENTRY_POINTS_PARAM, className );

    rwtServletContextListener.contextInitialized( contextInitializedEvent );

    assertEntryPointIsRegistered();
    assertEntryPointPath( "/rap" );
  }

  @Test
  public void testEntryPointInitializationWithServletMapping() {
    String className = TestEntryPoint.class.getName();
    servletContext.setInitParameter( ENTRY_POINTS_PARAM, className );
    setServletMapping( "/foo" );

    rwtServletContextListener.contextInitialized( contextInitializedEvent );

    assertEntryPointIsRegistered();
    assertEntryPointPath( "/foo" );
  }

  @Test
  public void testEntryPointInitializationWithEmptyServletMapping() {
    String className = TestEntryPoint.class.getName();
    servletContext.setInitParameter( ENTRY_POINTS_PARAM, className );
    setServletMapping( "" );

    rwtServletContextListener.contextInitialized( contextInitializedEvent );

    assertEntryPointIsRegistered();
    assertEntryPointPath( "/" );
  }

  @Test
  public void testEntryPointInitializationWithNonExistingClassName() {
    String className = "does.not.Exist";
    servletContext.setInitParameter( ENTRY_POINTS_PARAM, className );

    try {
      rwtServletContextListener.contextInitialized( contextInitializedEvent );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testConfiguration() {
    String className = TestConfiguration.class.getName();
    servletContext.setInitParameter( ApplicationConfiguration.CONFIGURATION_PARAM, className );

    rwtServletContextListener.contextInitialized( contextInitializedEvent );

    assertEntryPointIsRegistered();
    assertEntryPointPath( "/test" );
  }

  @Ignore
  public void testConfigurationWithThreadContextClassLoader() throws ClassNotFoundException {
    // See bug 367033
    // use a class name that cannot be found by RWT's class loader
    servletContext.setInitParameter( ApplicationConfiguration.CONFIGURATION_PARAM, "foo.Config" );
    final ClassLoader previousContextClassLoader = Thread.currentThread().getContextClassLoader();
    // set a context class loader that can find the class
    ClassLoader contextClassLoader = mockClassLoader( "foo.Config", TestConfiguration.class );

    Thread.currentThread().setContextClassLoader( contextClassLoader );
    try {
      rwtServletContextListener.contextInitialized( contextInitializedEvent );
    } finally {
      Thread.currentThread().setContextClassLoader( previousContextClassLoader );
    }

    verify( contextClassLoader ).loadClass( "foo.Config" );
  }

  private static ClassLoader mockClassLoader( final String className, final Class clazz ) {
    return spy( new ClassLoader() {
      @Override
      protected Class<?> findClass( String name ) throws ClassNotFoundException {
        if( className.equals( name ) ) {
          return clazz;
        }
        return super.findClass( name );
      }
    } );
  }

  private void assertResourceManagerIsRegistered() {
    ApplicationContextImpl applicationContext = ApplicationContextImpl.getFrom( servletContext );
    assertNotNull( applicationContext.getResourceManager() );
  }

  private void assertEntryPointIsRegistered() {
    ApplicationContextImpl applicationContext = ApplicationContextImpl.getFrom( servletContext );
    EntryPointManager entryPointManager = applicationContext.getEntryPointManager();
    assertEquals( 1, entryPointManager.getServletPaths().size() );
  }

  private void assertEntryPointPath( String path ) {
    ApplicationContextImpl applicationContext = ApplicationContextImpl.getFrom( servletContext );
    EntryPointManager entryPointManager = applicationContext.getEntryPointManager();
    String[] servletPaths = entryPointManager.getServletPaths().toArray( new String[ 0 ] );
    assertEquals( path, servletPaths[ 0 ] );
  }

  private void setServletMapping( String path ) {
    servletContext.addServlet( RWT_SERVLET_NAME, mock( Servlet.class ) );
    servletContext.getServletRegistration( RWT_SERVLET_NAME ).addMapping( path );
  }

  private static class TestConfiguration implements ApplicationConfiguration {
    @Override
    public void configure( Application application ) {
      application.addEntryPoint( "/test", TestEntryPoint.class, null );
    }
  }

}
