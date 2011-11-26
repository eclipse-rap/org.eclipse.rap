/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.application;

import static org.mockito.Mockito.mock;

import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.TestServletContext;
import org.eclipse.rwt.application.ApplicationConfiguration.OperationMode;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.engine.RWTServlet;
import org.eclipse.rwt.internal.engine.RWTClusterSupport;
import org.eclipse.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rwt.internal.lifecycle.SimpleLifeCycle;
import org.eclipse.rwt.lifecycle.ILifeCycle;


public class ApplicationConfigurationImpl_Test extends TestCase {
  
  private TestServletContext servletContext;
  private ApplicationContext applicationContext;
  private ApplicationConfigurationImpl applicationConfiguration;
  
  public void testDefaultOperationMode() {
    applicationContext.activate();
    
    ILifeCycle lifeCycle = applicationContext.getLifeCycleFactory().getLifeCycle();
    assertSame( SimpleLifeCycle.class, lifeCycle.getClass() );
  }
  
  public void testSetOperationModeWithNullArgument() {
    try {
      applicationConfiguration.setOperationMode( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testSetOperationModeToSWTCompatibility() {
    applicationConfiguration.setOperationMode( OperationMode.SWT_COMPATIBILITY );
    applicationContext.activate();
    
    ILifeCycle lifeCycle = applicationContext.getLifeCycleFactory().getLifeCycle();
    assertSame( RWTLifeCycle.class, lifeCycle.getClass() );
  }

  public void testSetOperationModeToJEECompatibility() {
    applicationConfiguration.setOperationMode( OperationMode.JEE_COMPATIBILITY );
    applicationContext.activate();
    
    ILifeCycle lifeCycle = applicationContext.getLifeCycleFactory().getLifeCycle();
    assertSame( SimpleLifeCycle.class, lifeCycle.getClass() );
  }
  
  public void testSetOperationModeToSessionFailover() {
    servletContext.setVersion( 3, 0 );
    servletContext.addServlet( "rwtServlet", new RWTServlet() );

    applicationConfiguration.setOperationMode( OperationMode.SESSION_FAILOVER );
    applicationContext.activate();
    
    ILifeCycle lifeCycle = applicationContext.getLifeCycleFactory().getLifeCycle();
    assertSame( SimpleLifeCycle.class, lifeCycle.getClass() );
    assertFilterRegistered( RWTClusterSupport.class );
  }

  public void testSetOperationModeToSessionFailoverWithMissingRWTServlet() {
    servletContext.setVersion( 3, 0 );
    servletContext.addServlet( "fooServlet", mock( Servlet.class ) );
    
    try {
      applicationConfiguration.setOperationMode( OperationMode.SESSION_FAILOVER );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }
  
  public void testSetOperationModeToSessionFailoverWithInsufficientServletVersion() {
    servletContext.setVersion( 2, 6 );
    servletContext.addServlet( "rwtServlet", new RWTServlet() );
    
    try {
      applicationConfiguration.setOperationMode( OperationMode.SESSION_FAILOVER );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }
  
  protected void setUp() throws Exception {
    ApplicationConfigurator configurator = mock( ApplicationConfigurator.class );
    servletContext = new TestServletContext();
    applicationContext = new ApplicationContext( configurator, servletContext );
    applicationConfiguration = new ApplicationConfigurationImpl( applicationContext, configurator );
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