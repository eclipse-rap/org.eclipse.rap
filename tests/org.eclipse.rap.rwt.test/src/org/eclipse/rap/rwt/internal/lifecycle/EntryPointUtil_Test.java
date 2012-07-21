/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.branding.TestBranding;
import org.eclipse.rap.rwt.internal.lifecycle.EntryPointUtil;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.RequestParams;
import org.eclipse.rap.rwt.lifecycle.IEntryPoint;
import org.eclipse.rap.rwt.lifecycle.IEntryPointFactory;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;


public class EntryPointUtil_Test extends TestCase {

  private IEntryPoint entryPoint;
  private IEntryPointFactory entryPointFactory;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    entryPoint = mockEntryPoint();
    entryPointFactory = mockEntryPointFactory( entryPoint );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testGetCurrentEntryPoint_withDefaultEntryPoint() {
    RWTFactory.getEntryPointManager().registerByName( EntryPointUtil.DEFAULT, entryPointFactory );

    IEntryPoint result = EntryPointUtil.getCurrentEntryPoint();

    verify( entryPointFactory ).create();
    assertSame( entryPoint, result );
  }

  public void testGetCurrentEntryPoint_withStartupParameter() {
    RWTFactory.getEntryPointManager().registerByName( "foo", entryPointFactory );
    Fixture.fakeRequestParam( RequestParams.STARTUP, "foo" );

    IEntryPoint result = EntryPointUtil.getCurrentEntryPoint();

    verify( entryPointFactory ).create();
    assertSame( entryPoint, result );
  }

  public void testGetCurrentEntryPoint_withServletPath() {
    RWTFactory.getEntryPointManager().registerByPath( "/foo", entryPointFactory, null );
    fakeServletPath( "/foo" );

    IEntryPoint result = EntryPointUtil.getCurrentEntryPoint();

    verify( entryPointFactory ).create();
    assertSame( entryPoint, result );
  }

  public void testGetCurrentEntryPoint_withBranding() {
    RWTFactory.getEntryPointManager().registerByName( "foo", entryPointFactory );
    // register a branding with the default servlet name ("rap")
    RWTFactory.getBrandingManager().register( new TestBranding( "rap", null, "foo" ) );

    IEntryPoint result = EntryPointUtil.getCurrentEntryPoint();

    verify( entryPointFactory ).create();
    assertSame( entryPoint, result );
  }

  public void testGetCurrentEntryPoint_servletPathOverridesBranding() {
    RWTFactory.getEntryPointManager().registerByName( "foo", entryPointFactory );
    RWTFactory.getBrandingManager().register( new TestBranding( "rap", null, "foo" ) );
    IEntryPoint entryPointByPath = mockEntryPoint();
    IEntryPointFactory entryPointFactoryByPath = mockEntryPointFactory( entryPointByPath );
    RWTFactory.getEntryPointManager().registerByPath( "/rap", entryPointFactoryByPath, null );

    IEntryPoint result = EntryPointUtil.getCurrentEntryPoint();

    // Servlet path takes precedence over branding
    verify( entryPointFactory, times( 0 ) ).create();
    verify( entryPointFactoryByPath ).create();
    assertSame( entryPointByPath, result );
  }

  public void testGetCurrentEntryPoint_parameterOverridesServletPath() {
    RWTFactory.getEntryPointManager().registerByName( "foo", entryPointFactory );
    IEntryPoint entryPointByPath = mockEntryPoint();
    IEntryPointFactory entryPointFactoryByPath = mockEntryPointFactory( entryPointByPath );
    RWTFactory.getEntryPointManager().registerByPath( "/bar", entryPointFactoryByPath, null );
    fakeServletPath( "/bar" );
    Fixture.fakeRequestParam( RequestParams.STARTUP, "foo" );

    IEntryPoint result = EntryPointUtil.getCurrentEntryPoint();

    // Request parameter takes precedence over servlet path
    verify( entryPointFactoryByPath, times( 0 ) ).create();
    verify( entryPointFactory ).create();
    assertSame( entryPoint, result );
  }

  public void testGetCurrentEntryPoint_withNonExistingEntryPointName() {
    Fixture.fakeRequestParam( RequestParams.STARTUP, "foo" );

    try {
      EntryPointUtil.getCurrentEntryPoint();
      fail();
    } catch( IllegalArgumentException expected ) {
      assertEquals( "Entry point not found: foo", expected.getMessage() );
    }
  }

  public void testGetCurrentEntryPoint_withNonExistingDefaultEntryPoint() {
    try {
      EntryPointUtil.getCurrentEntryPoint();
      fail();
    } catch( IllegalArgumentException expected ) {
      assertEquals( "Entry point not found: default", expected.getMessage() );
    }
  }

  public void testGetCurrentEntryPointProperties_withDefaultEntryPoint() {
    RWTFactory.getEntryPointManager().registerByName( EntryPointUtil.DEFAULT, entryPointFactory );

    Map<String, String> properties = EntryPointUtil.getCurrentEntryPointProperties();

    assertTrue( properties.isEmpty() );
  }

  public void testGetCurrentEntryPointParameter_withStartupParameter() {
    RWTFactory.getEntryPointManager().registerByName( "foo", entryPointFactory );
    Fixture.fakeRequestParam( RequestParams.STARTUP, "foo" );

    Map<String, String> properties = EntryPointUtil.getCurrentEntryPointProperties();

    assertTrue( properties.isEmpty() );
  }

  public void testGetCurrentEntryPointProperties_withServletPath() {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put( "test", "true" );
    RWTFactory.getEntryPointManager().registerByPath( "/foo", entryPointFactory, parameters );
    fakeServletPath( "/foo" );

    Map<String, String> properties = EntryPointUtil.getCurrentEntryPointProperties();

    assertEquals( "true", properties.get( "test" ) );
  }

  private static void fakeServletPath( String string ) {
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setServletPath( string );
  }

  private static IEntryPointFactory mockEntryPointFactory( IEntryPoint entryPoint ) {
    IEntryPointFactory entryPointFactory = mock( IEntryPointFactory.class );
    when( entryPointFactory.create() ).thenReturn( entryPoint );
    return entryPointFactory;
  }

  private static IEntryPoint mockEntryPoint() {
    IEntryPoint entryPoint = mock( IEntryPoint.class );
    when( Integer.valueOf( entryPoint.createUI() ) ).thenReturn( Integer.valueOf( 0 ) );
    return entryPoint;
  }

}
