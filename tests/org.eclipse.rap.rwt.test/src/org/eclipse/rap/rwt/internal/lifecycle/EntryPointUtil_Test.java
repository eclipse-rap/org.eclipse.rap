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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
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

  public void testGetCurrentEntryPoint() {
    RWTFactory.getEntryPointManager().registerByPath( "/foo", entryPointFactory, null );
    fakeServletPath( "/foo" );

    IEntryPoint result = EntryPointUtil.getCurrentEntryPoint();

    verify( entryPointFactory ).create();
    assertSame( entryPoint, result );
  }

  public void testGetCurrentEntryPointProperties() {
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
