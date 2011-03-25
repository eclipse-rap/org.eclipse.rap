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
import org.eclipse.rwt.internal.service.RWTStartupPageConfigurer;
import org.eclipse.rwt.internal.util.URLHelper;


public class BrandingManager_Test extends TestCase {
  
  public static class TestBranding extends AbstractBranding {

    private String servletName;
    private String[] entryPoints;
    private String defaultEntryPoint;
    private int registerResourcesCallCount;

    public TestBranding() {
    }
    
    public TestBranding( final String servletName, 
                         final String[] entryPoints, 
                         final String defaultEntryPoint ) 
    {
      this.servletName = servletName;
      this.entryPoints = entryPoints;
      this.defaultEntryPoint = defaultEntryPoint;
    }

    public String[] getEntryPoints() {
      return entryPoints;
    }

    public String getServletName() {
      return servletName;
    }

    public String getDefaultEntryPoint() {
      return defaultEntryPoint;
    }

    public void registerResources() throws IOException {
      registerResourcesCallCount++;
    }
  }
  
  public void testRegister() {
    // Test with illegal parameters
    try {
      BrandingManager.register( null );
      fail( "register: must not allow null parameter" );
    } catch( NullPointerException e ) {
      // expected
    }
    // Test legal usage
    TestBranding branding = new TestBranding();
    BrandingManager.register( branding );
    assertEquals( 1, BrandingManager.getAll().length );
    assertSame( branding, BrandingManager.getAll()[ 0 ] );
  }

  public void testDeregister() {
    // Test with illegal parameters
    try {
      BrandingManager.deregister( null );
      fail( "register: must not allow null parameter" );
    } catch( NullPointerException e ) {
      // expected
    }
    // Test legal usage
    TestBranding branding = new TestBranding();
    BrandingManager.register( branding );
    BrandingManager.deregister( branding );
    assertEquals( 0, BrandingManager.getAll().length );
    // De-register an unregistered branding: must be silently ignored
    BrandingManager.register( branding );
    BrandingManager.deregister( new TestBranding() );
    assertEquals( 1, BrandingManager.getAll().length );
  }
  
  public void testGet() {
    AbstractBranding branding;
    AbstractBranding actualBranding;
    // Test without any registered branding  
    actualBranding = BrandingManager.get( "myServlet", null );
    assertNotNull( actualBranding );
    actualBranding = BrandingManager.get( "myServlet", "myEP" );
    assertNotNull( actualBranding );
    // Test branding with servlet name and only a default EP
    branding = new TestBranding( "myServlet", null, "myEP" );
    BrandingManager.register( branding );
    actualBranding = BrandingManager.get( "myServlet", "myEP" );
    assertSame( branding, actualBranding );
    actualBranding = BrandingManager.get( "myServlet", null );
    assertSame( branding, actualBranding );
    actualBranding = BrandingManager.get( "myServlet", "" );
    assertSame( branding, actualBranding );
    actualBranding = BrandingManager.get( "myServlet", "anotherEP" );
    assertSame( branding, actualBranding );
    // clean up
    BrandingManager.deregister( branding );

    // Test branding with servlet name, no default EP and this list of EP's:
    // - myEP
    branding = new TestBranding( "myServlet", new String[] { "myEP" }, null );
    BrandingManager.register( branding );
    actualBranding = BrandingManager.get( "myServlet", "myEP" );
    assertSame( branding, actualBranding );
    try {
      BrandingManager.get( "myServlet", "anotherEP" );
      fail( "Should not find a matching branding" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // clean up
    BrandingManager.deregister( branding );
    
    // Test two brandings
    // 1 - serlvet name: brand1 with default entry point 'default1'
    // 2 - serlvet name: brand2 with default entry point 'default2'
    AbstractBranding branding1 
      = new TestBranding( "brand1", new String[] { "default1" }, "default1" );
    BrandingManager.register( branding1 );
    AbstractBranding branding2 
      = new TestBranding( "brand2", new String[] { "default2" }, "default2" );
    BrandingManager.register( branding2 );
    actualBranding = BrandingManager.get( "brand1", null );
    assertSame( branding1, actualBranding );
    actualBranding = BrandingManager.get( "brand1", "default1" );
    assertSame( branding1, actualBranding );
    actualBranding = BrandingManager.get( "brand2", null );
    assertSame( branding2, actualBranding );
    actualBranding = BrandingManager.get( "brand2", "default2" );
    assertSame( branding2, actualBranding );
    try {
      BrandingManager.get( "brand2", "default1" );
      fail( "Servlet 'brand2' with EP 'default1' shouldn't be allowed" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // clean up
    BrandingManager.deregister( branding1 );
    BrandingManager.deregister( branding2 );
    
    // Test one branding with the following attributes: 
    // servlet: myServlet, EP list: listedEP, default EP: defEP
    branding 
      = new TestBranding( "myServlet", new String[] { "listedEP" }, "defEP" );
    BrandingManager.register( branding );
    actualBranding = BrandingManager.get( "myServlet", "defEP" );
    assertSame( branding, actualBranding );
    actualBranding = BrandingManager.get( "myServlet", null );
    assertSame( branding, actualBranding );
    actualBranding = BrandingManager.get( "myServlet", "" );
    assertSame( branding, actualBranding );
    actualBranding = BrandingManager.get( "myServlet", "listedEP" );
    assertSame( branding, actualBranding );
    // clean up
    BrandingManager.deregister( branding );
    
    // Test defining a branding with the default servlet name
    String defaultServletName = BrandingManager.DEFAULT_SERVLET_NAME;
    branding = new TestBranding( defaultServletName, null, "defEP" );
    BrandingManager.register( branding );
    actualBranding = BrandingManager.get( defaultServletName, "defEP" );
    assertSame( branding, actualBranding );
  }
  
  public void testRegisterResources() throws IOException {
    Fixture.fakeResponseWriter();
    String servletName = URLHelper.getSerlvetName();
    TestBranding branding = new TestBranding( servletName, null, "default" );
    BrandingManager.register( branding );
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
