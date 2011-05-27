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
    
    configurable.configure( applicationContext );
    
    assertEquals( 1, getBrandings().length );
    assertEquals( TestBranding.class, getBrandings()[ 0 ].getClass() );
  }
  
  public void testConfigureWithUnknownBrandingType() {
    Fixture.setInitParameter( BrandingManagerConfigurable.BRANDINGS_PARAM, "unknown" );
    
    try {
      configurable.configure( applicationContext );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testReset() {
    setBrandingInitParameter();
    configurable.configure( applicationContext );
    BrandingManager brandingManager = applicationContext.getBrandingManager();
     
    configurable.reset( applicationContext );
    
    assertEquals( 0, brandingManager.getAll().length );
  }
  
  protected void setUp() {
    ServletContext servletContext = Fixture.createServletContext();
    configurable = new BrandingManagerConfigurable( servletContext );
    applicationContext = new ApplicationContext();
  }
  
  protected void tearDown() {
    Fixture.setInitParameter( BrandingManagerConfigurable.BRANDINGS_PARAM, null );
    Fixture.disposeOfServletContext();
  }
  
  private AbstractBranding[] getBrandings() {
    return applicationContext.getBrandingManager().getAll();
  }
  
  private void setBrandingInitParameter() {
    String brandingType = TestBranding.class.getName();
    Fixture.setInitParameter( BrandingManagerConfigurable.BRANDINGS_PARAM, brandingType );
  }
}