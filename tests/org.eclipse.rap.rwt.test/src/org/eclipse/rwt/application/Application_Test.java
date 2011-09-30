/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.application;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.application.Application;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.engine.ApplicationContext;
import org.eclipse.rwt.internal.engine.RWTDelegate;


public class Application_Test extends TestCase {
  
  private static final String SERVLET_NAME = "servletName";
  
  private ServletContext servletContext;
  private ApplicationConfigurator configurator;
  private Application application;

  public void testCreateServlet() {
    HttpServlet servlet1 = application.createServlet();
    HttpServlet servlet2 = application.createServlet();
    
    assertTrue( servlet1 instanceof RWTDelegate );
    assertTrue( servlet2 instanceof RWTDelegate );
    assertNotSame( servlet1, servlet2 );
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
  
  public void testGetServletNames() {
    startWithBrandingConfiguration();

    String[] servletNames = application.getServletNames();
    
    assertEquals( 1, servletNames.length );
    assertEquals( SERVLET_NAME, servletNames[ 0 ] );
  }
  
  protected void setUp() {
    servletContext = mock( ServletContext.class );
    when( servletContext.getRealPath( "/" ) )
      .thenReturn( Fixture.WEB_CONTEXT_RWT_RESOURCES_DIR.getPath() );
    configurator = mock( ApplicationConfigurator.class );
    application = new Application( servletContext, configurator );
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
          public String getServletName() {
            return SERVLET_NAME;
          }
        } );
      }
    };
    application = new Application( servletContext, configurator );
    application.start();
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
}