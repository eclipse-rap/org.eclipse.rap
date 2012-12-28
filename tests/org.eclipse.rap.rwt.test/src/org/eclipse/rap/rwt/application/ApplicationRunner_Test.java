/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import javax.servlet.ServletContext;

import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.lifecycle.TestEntryPoint;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.Before;
import org.junit.Test;


public class ApplicationRunner_Test {

  private static final String SERVLET_PATH = "/foo";

  private ServletContext servletContext;
  private ApplicationConfiguration configuration;
  private ApplicationRunner applicationRunner;

  @Before
  public void setUp() {
    servletContext = mock( ServletContext.class );
    when( servletContext.getRealPath( "/" ) )
      .thenReturn( Fixture.WEB_CONTEXT_RWT_RESOURCES_DIR.getPath() );
    configuration = mock( ApplicationConfiguration.class );
    applicationRunner = new ApplicationRunner( configuration, servletContext );
  }

  @Test
  public void testStart() {
    applicationRunner.start();

    checkContexthasBeenConfigured();
    checkApplicationContextHasBeenRegistered();
  }

  @Test
  public void testStartWithProblem() {
    createConfiguratorWithProblem();

    startWithProblem();

    checkApplicationContextHasBeenDeregistered();
  }

  @Test
  public void testStop() {
    applicationRunner.start();

    applicationRunner.stop();

    checkApplicationContextHasBeenDeregistered();
  }

  @Test
  public void testStopThatHasFailedOnStart() {
    createConfiguratorWithProblem();
    startWithProblem();

    applicationRunner.stop();

    checkApplicationContextGetsDeregisteredAnyway();
  }

  @SuppressWarnings( "deprecation" )
  @Test
  public void testGetDefaultServletNames() {
    applicationRunner.start();

    Collection<String> servletPaths = applicationRunner.getServletPaths();

    assertTrue( servletPaths.isEmpty() );
  }

  @SuppressWarnings( "deprecation" )
  @Test
  public void testGetServletNames() {
    startWithEntryPointConfiguration();

    Collection<String> servletPaths = applicationRunner.getServletPaths();

    assertEquals( 1, servletPaths.size() );
    assertTrue( servletPaths.contains( SERVLET_PATH ) );
  }

  @Test
  public void testParamServletContextMustNotBeNull() {
    try {
      new ApplicationRunner( mock( ApplicationConfiguration.class ), null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testParamConfiguratorMustNotBeNull() {
    try {
      new ApplicationRunner( null, mock( ServletContext.class ) );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  private void checkApplicationContextHasBeenRegistered() {
    verify( servletContext ).setAttribute( any( String.class ), any( ApplicationContextImpl.class ) );
  }

  private void checkContexthasBeenConfigured() {
    verify( configuration ).configure( any( Application.class ) );
  }

  private void checkApplicationContextHasBeenDeregistered() {
    verify( servletContext ).removeAttribute( any( String.class ) );
  }

  private void startWithEntryPointConfiguration() {
    configuration = new ApplicationConfiguration() {
      public void configure( Application configuration ) {
        configuration.addEntryPoint( SERVLET_PATH, TestEntryPoint.class, null );
      }
    };
    applicationRunner = new ApplicationRunner( configuration, servletContext );
    applicationRunner.start();
  }

  private void createConfiguratorWithProblem() {
    doThrow( new IllegalStateException() )
      .when( configuration ).configure( any( Application.class ) );
  }

  private void startWithProblem() {
    try {
      applicationRunner.start();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  private void checkApplicationContextGetsDeregisteredAnyway() {
    verify( servletContext, times( 2 ) ).removeAttribute( any( String.class ) );
  }

}
