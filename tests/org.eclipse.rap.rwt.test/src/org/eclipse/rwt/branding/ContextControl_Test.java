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
package org.eclipse.rwt.branding;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.engine.*;
import org.eclipse.rwt.internal.engine.ApplicationContext;
import org.eclipse.rwt.internal.engine.RWTDelegate;


public class ContextControl_Test extends TestCase {
  private static final String SERVLET_NAME = "servletName";
  
  private ServletContext servletContext;
  private Configurator configurator;
  private ContextControl contextControl;

  public void testCreateServlet() {
    HttpServlet servlet1 = contextControl.createServlet();
    HttpServlet servlet2 = contextControl.createServlet();
    
    assertTrue( servlet1 instanceof RWTDelegate );
    assertTrue( servlet2 instanceof RWTDelegate );
    assertNotSame( servlet1, servlet2 );
  }
  
  public void testStartContext() {
    contextControl.startContext();
    
    checkContexthasBeenConfigured();
    checkApplicationContextHasBeenRegistered();
  }
  
  public void testStopContext() {
    contextControl.startContext();
  
    contextControl.stopContext();
    checkApplicationContextHasBeenDeregistered();
  }

  public void testGetDefaultServletNames() {
    contextControl.startContext();
    
    String[] servletNames = contextControl.getServletNames();
    
    assertEquals( 0, servletNames.length );
  }
  
  public void testGetServletNames() {
    startContextWithBrandingConfiguration();

    String[] servletNames = contextControl.getServletNames();
    
    assertEquals( 1, servletNames.length );
    assertEquals( SERVLET_NAME, servletNames[ 0 ] );
  }
  
  protected void setUp() {
    servletContext = mock( ServletContext.class );
    when( servletContext.getRealPath( "/" ) )
      .thenReturn( Fixture.WEB_CONTEXT_RWT_RESOURCES_DIR.getPath() );
    configurator = mock( Configurator.class );
    contextControl = new ContextControl( servletContext, configurator );
  }
  

  private void checkApplicationContextHasBeenRegistered() {
    verify( servletContext ).setAttribute( any( String.class ), any( ApplicationContext.class ) );
  }

  private void checkContexthasBeenConfigured() {
    verify( configurator ).configure( any( Context.class ) );
  }

  private void checkApplicationContextHasBeenDeregistered() {
    verify( servletContext ).removeAttribute( any( String.class ) );
  }
  
  private void startContextWithBrandingConfiguration() {
    configurator = new Configurator() {
      public void configure( Context context ) {
        context.addBranding( new AbstractBranding() {
          public String getServletName() {
            return SERVLET_NAME;
          }
        } );
      }
    };
    contextControl = new ContextControl( servletContext, configurator );
    contextControl.startContext();
  }
}