/*******************************************************************************
 * Copyright (c) 2011, 2014 Frank Appel and others.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import javax.servlet.ServletContext;

import org.eclipse.rap.rwt.internal.SingletonManager;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.lifecycle.TestEntryPoint;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class ApplicationRunner_Test {

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

  @Test( expected = NullPointerException.class )
  public void testConstructor_failsWithNullConfiguration() {
    new ApplicationRunner( null, mock( ServletContext.class ) );
  }

  @Test( expected = NullPointerException.class )
  public void testConstructor_failsWithNullServletContext() {
    new ApplicationRunner( mock( ApplicationConfiguration.class ), null );
  }

  @Test
  public void testStart_runsApplicationConfiguration() {
    applicationRunner.start();

    verify( configuration ).configure( any( Application.class ) );
  }

  @Test
  public void testStart_registersApplicationContext() {
    applicationRunner.start();

    verify( servletContext ).setAttribute( any( String.class ), any( ApplicationContextImpl.class ) );
  }

  @Test
  public void testStart_installsSingletonManager() {
    applicationRunner.start();

    ArgumentCaptor<ApplicationContextImpl> captor
      = ArgumentCaptor.forClass( ApplicationContextImpl.class );
    verify( servletContext ).setAttribute( any( String.class ), captor.capture() );
    ApplicationContextImpl applicationContext = captor.getValue();
    assertNotNull( SingletonManager.getInstance( applicationContext ) );
  }

  @Test
  public void testStart_failsWithBrokenConfiguration() {
    Exception exception  = new IllegalStateException();
    simulateExceptionInConfiguration( exception );

    try {
      applicationRunner.start();
      fail();
    } catch( IllegalStateException actual ) {
      assertSame( exception, actual );
    }
  }

  @Test
  public void testStart_deregistersApplicationContextIfFailed() {
    simulateExceptionInConfiguration( new IllegalStateException() );

    try {
      applicationRunner.start();
    } catch( IllegalStateException expected ) {
      // expected, ignore
    }

    verify( servletContext ).removeAttribute( any( String.class ) );
  }

  @Test
  public void testStop_deregistersApplicationContext() {
    applicationRunner.start();

    applicationRunner.stop();

    verify( servletContext ).removeAttribute( any( String.class ) );
  }

  @Test
  public void testStop_deregistersApplicationContext_evenAfterFailedStart() {
    simulateExceptionInConfiguration( new IllegalStateException() );
    try {
      applicationRunner.start();
    } catch( IllegalStateException expected ) {
      // ignore
    }

    applicationRunner.stop();

    verify( servletContext, times( 2 ) ).removeAttribute( any( String.class ) );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testGetServletPaths() {
    applicationRunner.start();

    Collection<String> servletPaths = applicationRunner.getServletPaths();

    assertTrue( servletPaths.isEmpty() );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testGetServletPaths_withEntryPoint() {
    simulateEntryPointInConfiguration( "/foo" );
    applicationRunner.start();

    Collection<String> servletPaths = applicationRunner.getServletPaths();

    assertEquals( 1, servletPaths.size() );
    assertTrue( servletPaths.contains( "/foo" ) );
  }

  private void simulateEntryPointInConfiguration( final String servletPath ) {
    Answer answer = new Answer<Object>() {
      public Object answer( InvocationOnMock invocation ) throws Throwable {
        Application application = ( Application )invocation.getArguments()[ 0 ];
        application.addEntryPoint( servletPath, TestEntryPoint.class, null );
        return null;
      }
    };
    doAnswer( answer ).when( configuration ).configure( any( Application.class ) );
  }

  private void simulateExceptionInConfiguration( Exception exception ) {
    doThrow( exception ).when( configuration ).configure( any( Application.class ) );
  }

}
