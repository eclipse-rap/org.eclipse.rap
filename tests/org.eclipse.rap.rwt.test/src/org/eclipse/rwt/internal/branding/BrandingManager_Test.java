/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSoure - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.branding;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.service.RWTStartupPageConfigurer;
import org.eclipse.rwt.internal.util.URLHelper;


public class BrandingManager_Test extends TestCase {
  
  public void testRegisterWithNullArgument() {
    BrandingManager brandingManager = new BrandingManager();
    try {
      brandingManager.register( null );
      fail( "register: must not allow null parameter" );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRegister() {
    BrandingManager brandingManager = new BrandingManager();
    TestBranding branding = new TestBranding();
    brandingManager.register( branding );
    assertEquals( 1, brandingManager.getAll().length );
    assertSame( branding, brandingManager.getAll()[ 0 ] );
  }
  
  public void testDeregisterWithNullArgument() {
    BrandingManager brandingManager = new BrandingManager();
    try {
      brandingManager.deregister( null );
      fail( "deregister: must not allow null parameter" );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testDeregister() {
    BrandingManager brandingManager = new BrandingManager();
    // Test legal usage
    TestBranding branding = new TestBranding();
    brandingManager.register( branding );
    brandingManager.deregister( branding );
    assertEquals( 0, brandingManager.getAll().length );
    // De-register an unregistered branding: must be silently ignored
    brandingManager.register( branding );
    brandingManager.deregister( new TestBranding() );
    assertEquals( 1, brandingManager.getAll().length );
  }
  
  public void testFindBrandingWithNullServletName() {
    BrandingManager brandingManager = new BrandingManager();
    try {
      brandingManager.find( null, "" );
      fail( "findBranding: must not allow null-servletName" );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testFindWhenNoBrandingRegistered() {
    BrandingManager brandingManager = new BrandingManager();
    // Test without any registered branding  
    assertNotNull( brandingManager.find( "myServlet", null ) );
    assertNotNull( brandingManager.find( "myServlet", "myEP" ) );
  }

  public void testFindWithServletNameAndDefaultEntryPoint() {
    BrandingManager brandingManager = new BrandingManager();
    AbstractBranding actualBranding;
    // Test branding with servlet name and only a default EP
    TestBranding branding = new TestBranding( "myServlet", null, "myEP" );
    brandingManager.register( branding );
    actualBranding = brandingManager.find( "myServlet", "myEP" );
    assertSame( branding, actualBranding );
    actualBranding = brandingManager.find( "myServlet", null );
    assertSame( branding, actualBranding );
    actualBranding = brandingManager.find( "myServlet", "" );
    assertSame( branding, actualBranding );
    actualBranding = brandingManager.find( "myServlet", "anotherEP" );
    assertSame( branding, actualBranding );
  }
  
  public void testFind() {
    BrandingManager brandingManager = new BrandingManager();
    TestBranding branding;
    AbstractBranding actualBranding;
    // Test branding with servlet name, no default EP and this list of EP's:
    // - myEP
    branding = new TestBranding( "myServlet", new String[] { "myEP" }, null );
    brandingManager.register( branding );
    actualBranding = brandingManager.find( "myServlet", "myEP" );
    assertSame( branding, actualBranding );
    try {
      brandingManager.find( "myServlet", "anotherEP" );
      fail( "Should not find a matching branding" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // clean up
    brandingManager.deregister( branding );
    
    // Test two brandings
    // 1 - serlvet name: brand1 with default entry point 'default1'
    // 2 - serlvet name: brand2 with default entry point 'default2'
    AbstractBranding branding1 
      = new TestBranding( "brand1", new String[] { "default1" }, "default1" );
    brandingManager.register( branding1 );
    AbstractBranding branding2 
      = new TestBranding( "brand2", new String[] { "default2" }, "default2" );
    brandingManager.register( branding2 );
    actualBranding = brandingManager.find( "brand1", null );
    assertSame( branding1, actualBranding );
    actualBranding = brandingManager.find( "brand1", "default1" );
    assertSame( branding1, actualBranding );
    actualBranding = brandingManager.find( "brand2", null );
    assertSame( branding2, actualBranding );
    actualBranding = brandingManager.find( "brand2", "default2" );
    assertSame( branding2, actualBranding );
    try {
      brandingManager.find( "brand2", "default1" );
      fail( "Servlet 'brand2' with EP 'default1' shouldn't be allowed" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // clean up
    brandingManager.deregister( branding1 );
    brandingManager.deregister( branding2 );
    
    // Test one branding with the following attributes: 
    // servlet: myServlet, EP list: listedEP, default EP: defEP
    branding = new TestBranding( "myServlet", new String[] { "listedEP" }, "defEP" );
    brandingManager.register( branding );
    actualBranding = brandingManager.find( "myServlet", "defEP" );
    assertSame( branding, actualBranding );
    actualBranding = brandingManager.find( "myServlet", null );
    assertSame( branding, actualBranding );
    actualBranding = brandingManager.find( "myServlet", "" );
    assertSame( branding, actualBranding );
    actualBranding = brandingManager.find( "myServlet", "listedEP" );
    assertSame( branding, actualBranding );
    // clean up
    brandingManager.deregister( branding );
  }
  
  public void testFindWhenBrandingWithDefaultServletNameIsRegistered() {
    BrandingManager brandingManager = new BrandingManager();
    String defaultServletName = BrandingManager.DEFAULT_SERVLET_NAME;
    TestBranding branding = new TestBranding( defaultServletName, null, "defEP" );
    brandingManager.register( branding );
    AbstractBranding actualBranding = brandingManager.find( defaultServletName, "defEP" );
    assertSame( branding, actualBranding );
  }
  
  public void testRegisterResources() throws IOException {
    Fixture.fakeResponseWriter();
    String servletName = URLHelper.getServletName();
    TestBranding branding = new TestBranding( servletName, null, "default" );
    RWTFactory.getBrandingManager().register( branding );
    RWTStartupPageConfigurer configurer;
    // check precondition
    assertEquals( 0, branding.registerResourcesCallCount );
    // access branding for the first time: registerResources must be called
    configurer = RWTStartupPageConfigurer.getInstance();
    configurer.getTemplate();
    assertEquals( 1, branding.registerResourcesCallCount );
    // access branding another time: registerResources must *not* be called
    configurer.getTemplate();
    assertEquals( 1, branding.registerResourcesCallCount );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
