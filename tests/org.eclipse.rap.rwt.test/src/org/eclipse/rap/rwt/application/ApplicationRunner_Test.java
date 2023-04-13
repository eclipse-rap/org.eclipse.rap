/*******************************************************************************
 * Copyright (c) 2011, 2015 Frank Appel and others.
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;

import org.eclipse.rap.rwt.internal.SingletonManager;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.service.ApplicationContext;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


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
  public void testGetApplicationContext_beforeStart() {
    ApplicationContext result = applicationRunner.getApplicationContext();

    assertNull( result );
  }

  @Test
  public void testGetApplicationContext_afterStart() {
    applicationRunner.start();

    ApplicationContext result = applicationRunner.getApplicationContext();

    assertNotNull( result );
  }

  @Test
  public void testGetApplicationContext_afterStop() {
    applicationRunner.start();
    applicationRunner.stop();

    ApplicationContext result = applicationRunner.getApplicationContext();

    assertNull( result );
  }

  private void simulateExceptionInConfiguration( Exception exception ) {
    doThrow( exception ).when( configuration ).configure( any( Application.class ) );
  }

}
