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
package org.eclipse.rwt.internal.engine;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.branding.BrandingManager;


public class BrandingManagerConfigurable_Test extends TestCase {
  private BrandingManagerConfigurable configurable;
  private ApplicationContext applicationContext;

  public static class TestBranding extends AbstractBranding {
  }

  public void testConfigure() {
    setBrandingInitParameter();
    
    applicationContext.activate();
    
    assertEquals( 1, getBrandings().length );
    assertEquals( TestBranding.class, getBrandings()[ 0 ].getClass() );
  }
  
  public void testConfigureWithUnknownBrandingType() {
    Fixture.setInitParameter( RWTServletContextListener.BRANDINGS_PARAM, "unknown" );
    
    try {
      applicationContext.activate();
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testReset() {
    setBrandingInitParameter();
    applicationContext.activate();
    BrandingManager brandingManager = applicationContext.getBrandingManager();
     
    applicationContext.deactivate();
    
    assertEquals( 0, brandingManager.getAll().length );
  }
  
  protected void setUp() {
    ServletContext servletContext = Fixture.createServletContext();
    configurable = new BrandingManagerConfigurable( servletContext );
    applicationContext = new ApplicationContext( new Class[] { BrandingManager.class } );
    applicationContext.addConfigurable( configurable );
  }
  
  protected void tearDown() {
    Fixture.setInitParameter( RWTServletContextListener.BRANDINGS_PARAM, null );
  }
  
  private AbstractBranding[] getBrandings() {
    return applicationContext.getBrandingManager().getAll();
  }
  
  private void setBrandingInitParameter() {
    String brandingType = TestBranding.class.getName();
    Fixture.setInitParameter( RWTServletContextListener.BRANDINGS_PARAM, brandingType );
  }
}