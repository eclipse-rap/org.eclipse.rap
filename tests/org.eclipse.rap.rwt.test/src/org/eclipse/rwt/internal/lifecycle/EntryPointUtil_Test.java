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

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testFindEntryPoint_default() {
    assertEquals( EntryPointManager.DEFAULT, EntryPointUtil.findEntryPoint() );
  }

  public void testFindEntryPoint_withStartupParameter() {
    Fixture.fakeRequestParam( RequestParams.STARTUP, "foo" );

    assertEquals( "foo", EntryPointUtil.findEntryPoint() );
  }

  public void testFindEntryPoint_withBranding() {
    // register a branding with the default servlet name ("rap")
    TestBranding branding = new TestBranding( "rap", null, "foo" );
    RWTFactory.getBrandingManager().register( branding );

    assertEquals( "foo", EntryPointUtil.findEntryPoint() );
  }

  public void testCreateUI() {
    IEntryPoint entryPoint = mockEntryPoint( 23 );
    IEntryPointFactory entryPointFactory = mockEntryPointFactory( entryPoint );
    RWTFactory.getEntryPointManager().register( "foo", entryPointFactory );

    int returnValue = EntryPointUtil.createUI( "foo" );

    verify( entryPointFactory ).create();
    verify( entryPoint ).createUI();
    assertEquals( 23, returnValue );
    assertEquals( "foo", EntryPointUtil.getCurrentEntryPoint() );
  }

  public void testCreateUI_withDefaultEntryPoint() {
    IEntryPoint entryPoint = mockEntryPoint( 23 );
    IEntryPointFactory entryPointFactory = mockEntryPointFactory( entryPoint );
    RWTFactory.getEntryPointManager().register( EntryPointManager.DEFAULT, entryPointFactory );

    int returnValue = EntryPointUtil.createUI( EntryPointManager.DEFAULT );

    verify( entryPointFactory ).create();
    verify( entryPoint ).createUI();
    assertEquals( 23, returnValue );
    assertEquals( EntryPointManager.DEFAULT, EntryPointUtil.getCurrentEntryPoint() );
  }

  public void testCreateUI_withNullName() {
    try {
      EntryPointUtil.createUI( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testCreateUI_withNonExistingEntryPointName() {
    try {
      EntryPointUtil.createUI( "does.not.exist" );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  private static IEntryPointFactory mockEntryPointFactory( IEntryPoint entryPoint ) {
    IEntryPointFactory entryPointFactory = mock( IEntryPointFactory.class );
    when( entryPointFactory.create() ).thenReturn( entryPoint );
    return entryPointFactory;
  }

  private static IEntryPoint mockEntryPoint( int returnValue ) {
    IEntryPoint entryPoint = mock( IEntryPoint.class );
    when( Integer.valueOf( entryPoint.createUI() ) ).thenReturn( Integer.valueOf( returnValue ) );
    return entryPoint;
  }

}
