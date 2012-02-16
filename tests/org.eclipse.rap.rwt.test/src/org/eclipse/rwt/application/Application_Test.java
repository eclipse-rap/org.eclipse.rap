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
package org.eclipse.rwt.application;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.application.ApplicationContext;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.mockito.ArgumentCaptor;


public class Application_Test extends TestCase {

  private static final String SERVLET_NAME = "servletName";

  private ServletContext servletContext;
  private ApplicationConfigurator configurator;
  private Application application;

  @Override
  protected void setUp() {
    servletContext = mock( ServletContext.class );
    when( servletContext.getRealPath( "/" ) )
      .thenReturn( Fixture.WEB_CONTEXT_RWT_RESOURCES_DIR.getPath() );
    configurator = mock( ApplicationConfigurator.class );
    application = new Application( configurator, servletContext );
  }

  public void testStart() {
    application.start();

    checkContexthasBeenConfigured();
    checkApplicationContextHasBeenRegistered();
  }

  public void testStartWithProblem() {
    createConfiguratorWithProblem();

    startWithProblem();

    checkApplicationContextHasBeenDeregistered();
  }

  public void testStop() {
    application.start();

    application.stop();

    checkApplicationContextHasBeenDeregistered();
  }

  public void testStopThatHasFailedOnStart() {
    createConfiguratorWithProblem();
    startWithProblem();

    application.stop();

    checkApplicationContextGetsDeregisteredAnyway();
  }

  public void testGetDefaultServletNames() {
    application.start();

    String[] servletNames = application.getServletNames();

    assertEquals( 0, servletNames.length );
  }

  public void testGetServletNames_withBranding() {
    startWithBrandingConfiguration();

    String[] servletNames = application.getServletNames();

    assertEquals( 1, servletNames.length );
    assertEquals( SERVLET_NAME, servletNames[ 0 ] );
  }

  public void testGetServletNames_withEntryPoint() {
    startWithEntryPointConfiguration();

    String[] servletNames = application.getServletNames();

    assertEquals( 1, servletNames.length );
    assertEquals( SERVLET_NAME, servletNames[ 0 ] );
  }

  public void testParamServletContextMustNotBeNull() {
    try {
      new Application( mock( ApplicationConfigurator.class ), null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testParamConfiguratorMustNotBeNull() {
    try {
      new Application( null, mock( ServletContext.class ) );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  private void checkApplicationContextHasBeenRegistered() {
    verify( servletContext ).setAttribute( any( String.class ), any( ApplicationContext.class ) );
  }

  private void checkContexthasBeenConfigured() {
    verify( configurator ).configure( any( ApplicationConfiguration.class ) );
  }

  private void checkApplicationContextHasBeenDeregistered() {
    verify( servletContext ).removeAttribute( any( String.class ) );
  }

  private void startWithBrandingConfiguration() {
    configurator = new ApplicationConfigurator() {
      public void configure( ApplicationConfiguration configuration ) {
        configuration.addBranding( new AbstractBranding() {
          @Override
          public String getServletName() {
            return SERVLET_NAME;
          }
        } );
      }
    };
    application = new Application( configurator, servletContext );
    application.start();
  }

  private void startWithEntryPointConfiguration() {
    application.start();
    // TODO [rst] Replace with entrypoint configuration when switched to register-by-path
    ArgumentCaptor<ApplicationContext> argument = ArgumentCaptor.forClass( ApplicationContext.class );
    verify( servletContext ).setAttribute( anyString(), argument.capture() );
    ApplicationContext applicationContext = argument.getValue();
    EntryPointManager entryPointManager = applicationContext.getEntryPointManager();
    entryPointManager.registerByPath( SERVLET_NAME, TestEntryPoint.class );
//    configurator = new ApplicationConfigurator() {
//      public void configure( ApplicationConfiguration configuration ) {
//        configuration.addEntryPoint( SERVLET_NAME, TestEntryPoint.class );
//      }
//    };
//    application = new Application( configurator, servletContext );
//    application.start();
  }

  private void createConfiguratorWithProblem() {
    doThrow( new IllegalStateException() )
      .when( configurator ).configure( any( ApplicationConfiguration.class ) );
  }

  private void startWithProblem() {
    try {
      application.start();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  private void checkApplicationContextGetsDeregisteredAnyway() {
    verify( servletContext, times( 2 ) ).removeAttribute( any( String.class ) );
  }

  static class TestEntryPoint implements IEntryPoint {
    public int createUI() {
      return 0;
    }
  }

}
