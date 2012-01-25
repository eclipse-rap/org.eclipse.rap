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
package org.eclipse.rwt.internal.lifecycle;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.branding.TestBranding;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.IEntryPointFactory;


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

  public void testGetCurrentEntryPointName_default() {
    assertEquals( EntryPointUtil.DEFAULT, EntryPointUtil.getCurrentEntryPointName() );
  }

  public void testGetCurrentEntryPointName_withStartupParameter() {
    Fixture.fakeRequestParam( RequestParams.STARTUP, "foo" );

    assertEquals( "foo", EntryPointUtil.getCurrentEntryPointName() );
  }

  public void testGetCurrentEntryPointName_withBranding() {
    // register a branding with the default servlet name ("rap")
    TestBranding branding = new TestBranding( "rap", null, "foo" );
    RWTFactory.getBrandingManager().register( branding );

    assertEquals( "foo", EntryPointUtil.getCurrentEntryPointName() );
  }

  public void testGetCurrentEntryPointName_isCached() {
    Fixture.fakeRequestParam( RequestParams.STARTUP, "foo" );
    EntryPointUtil.getCurrentEntryPointName();
    Fixture.fakeRequestParam( RequestParams.STARTUP, "bar" );

    assertEquals( "foo", EntryPointUtil.getCurrentEntryPointName() );
  }

  public void testGetCurrentEntryPoint() {
    RWTFactory.getEntryPointManager().register( "foo", entryPointFactory );
    Fixture.fakeRequestParam( RequestParams.STARTUP, "foo" );

    IEntryPoint returnedEntryPoint = EntryPointUtil.getCurrentEntryPoint();

    verify( entryPointFactory ).create();
    assertSame( entryPoint, returnedEntryPoint );
  }

  public void testGetCurrentEntryPoint_isCached() {
    RWTFactory.getEntryPointManager().register( "foo", entryPointFactory );
    Fixture.fakeRequestParam( RequestParams.STARTUP, "foo" );
    EntryPointUtil.getCurrentEntryPoint();
    Fixture.fakeRequestParam( RequestParams.STARTUP, "bar" );

    IEntryPoint returnedEntryPoint = EntryPointUtil.getCurrentEntryPoint();

    verify( entryPointFactory, times( 2 ) ).create();
    assertSame( entryPoint, returnedEntryPoint );
  }

  public void testGetEntryPoint() {
    RWTFactory.getEntryPointManager().register( "foo", entryPointFactory );

    IEntryPoint returnedEntryPoint = EntryPointUtil.getEntryPoint( "foo" );

    verify( entryPointFactory ).create();
    assertSame( entryPoint, returnedEntryPoint );
  }

  public void testGetEntryPoint_withNullName() {
    try {
      EntryPointUtil.getEntryPoint( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testGetEntryPoint_withNonExistingEntryPointName() {
    try {
      EntryPointUtil.getEntryPoint( "does.not.exist" );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
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
