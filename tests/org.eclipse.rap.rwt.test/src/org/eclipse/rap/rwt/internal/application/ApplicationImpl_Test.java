/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.application;

import static org.mockito.Mockito.mock;

import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.engine.RWTServlet;
import org.eclipse.rap.rwt.internal.engine.RWTClusterSupport;
import org.eclipse.rap.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rap.rwt.internal.lifecycle.SimpleLifeCycle;
import org.eclipse.rap.rwt.lifecycle.ILifeCycle;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.rap.rwt.testfixture.TestServletContext;


public class ApplicationImpl_Test extends TestCase {

  private TestServletContext servletContext;
  private ApplicationContextImpl applicationContext;
  private ApplicationImpl application;
  private ApplicationConfiguration applicationConfiguration;

  public void testDefaultOperationMode() {
    applicationContext.activate();

    ILifeCycle lifeCycle = applicationContext.getLifeCycleFactory().getLifeCycle();
    assertSame( SimpleLifeCycle.class, lifeCycle.getClass() );
  }

  public void testSetOperationModeWithNullArgument() {
    try {
      application.setOperationMode( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testSetOperationModeToSWTCompatibility() {
    application.setOperationMode( OperationMode.SWT_COMPATIBILITY );
    applicationContext.activate();

    ILifeCycle lifeCycle = applicationContext.getLifeCycleFactory().getLifeCycle();
    assertSame( RWTLifeCycle.class, lifeCycle.getClass() );
  }

  public void testSetOperationModeToJEECompatibility() {
    application.setOperationMode( OperationMode.JEE_COMPATIBILITY );
    applicationContext.activate();

    ILifeCycle lifeCycle = applicationContext.getLifeCycleFactory().getLifeCycle();
    assertSame( SimpleLifeCycle.class, lifeCycle.getClass() );
  }

  public void testSetOperationModeToSessionFailover() {
    servletContext.setVersion( 3, 0 );
    servletContext.addServlet( "rwtServlet", new RWTServlet() );

    application.setOperationMode( OperationMode.SESSION_FAILOVER );
    applicationContext.activate();

    ILifeCycle lifeCycle = applicationContext.getLifeCycleFactory().getLifeCycle();
    assertSame( SimpleLifeCycle.class, lifeCycle.getClass() );
    assertFilterRegistered( RWTClusterSupport.class );
  }

  public void testSetOperationModeToSessionFailoverWithMissingRWTServlet() {
    servletContext.setVersion( 3, 0 );
    servletContext.addServlet( "fooServlet", mock( Servlet.class ) );

    try {
      application.setOperationMode( OperationMode.SESSION_FAILOVER );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  public void testSetOperationModeToSessionFailoverWithInsufficientServletVersion() {
    servletContext.setVersion( 2, 6 );
    servletContext.addServlet( "rwtServlet", new RWTServlet() );

    try {
      application.setOperationMode( OperationMode.SESSION_FAILOVER );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  public void testAddResource() {
    String resourceName = "resource-name";
    ResourceLoader resourceLoader = mock( ResourceLoader.class );

    application.addResource( resourceName, resourceLoader );

    assertEquals( 1, applicationContext.getResourceRegistry().getResourceRegistrations().length );
  }

  public void testAddResourceWithNullResourceName() {
    ResourceLoader resourceLoader = mock( ResourceLoader.class );

    try {
      application.addResource( null, resourceLoader );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testAddResourceWithNullResourceLoader() {
    try {
      application.addResource( "resource-name", null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Override
  protected void setUp() throws Exception {
    applicationConfiguration = mock( ApplicationConfiguration.class );
    servletContext = new TestServletContext();
    applicationContext = new ApplicationContextImpl( applicationConfiguration, servletContext );
    application = new ApplicationImpl( applicationContext, applicationConfiguration );
  }

  private void assertFilterRegistered( Class<RWTClusterSupport> filterClass ) {
    FilterRegistration[] filterRegistrations = getFilterRegistrations();
    boolean found = false;
    for( int i = 0; !found && i < filterRegistrations.length; i++ ) {
      if( filterRegistrations[ i ].getClassName().equals( filterClass.getName() ) ) {
        found = true;
      }
    }
    assertTrue( found );
  }

  private FilterRegistration[] getFilterRegistrations() {
    return servletContext.getFilterRegistrations().values().toArray( new FilterRegistration[ 0 ] );
  }
}