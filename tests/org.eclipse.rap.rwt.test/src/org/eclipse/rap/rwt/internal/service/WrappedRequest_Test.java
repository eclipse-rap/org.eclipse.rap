/*******************************************************************************
 * Copyright (c) 2002, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class WrappedRequest_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testAdditionalParameters() {
    TestRequest original = new TestRequest();
    String p0 = "p0";
    String v0 = "v0";
    original.setParameter( p0, v0 );
    String p1 = "p1";
    String v1a = "v1a";
    String v1b = "v1b";
    original.addParameter( p1, v1a );
    original.addParameter( p1, v1b );

    Map<String, String[]> paramMap = new HashMap<String, String[]>();
    String p2 = "p2";
    String v2 = "v2";
    String p3 = "p3";
    String v3a = "v3a";
    String v3b = "v3b";
    paramMap.put( p2, new String[] { v2 } );
    paramMap.put( p3, new String[] { v3a, v3b } );
    WrappedRequest wrapper = new WrappedRequest( original, paramMap );

    assertEquals( v0, wrapper.getParameter( p0 ) );
    assertEquals( v1a, wrapper.getParameter( p1 ) );
    assertEquals( v2, wrapper.getParameter( p2 ) );
    assertEquals( v3a, wrapper.getParameter( p3 ) );

    Enumeration parameterNames = wrapper.getParameterNames();
    Set<Object> names = new HashSet<Object>();
    while( parameterNames.hasMoreElements() ) {
      names.add( parameterNames.nextElement() );
    }
    assertTrue( names.contains( p0 ) );
    assertTrue( names.contains( p1 ) );
    assertTrue( names.contains( p2 ) );
    assertTrue( names.contains( p3 ) );

    assertEquals( v0, wrapper.getParameterValues( p0 )[ 0 ] );
    assertEquals( v1a, wrapper.getParameterValues( p1 )[ 0 ] );
    assertEquals( v1b, wrapper.getParameterValues( p1 )[ 1 ] );
    assertEquals( v2, wrapper.getParameterValues( p2 )[ 0 ] );
    assertEquals( v3a, wrapper.getParameterValues( p3 )[ 0 ] );
    assertEquals( v3b, wrapper.getParameterValues( p3 )[ 1 ] );

    Map parameterMap = wrapper.getParameterMap();
    assertEquals( v0, ( ( String[] )parameterMap.get( p0 ) )[ 0 ] );
    assertEquals( v1a, ( ( String[] )parameterMap.get( p1 ) )[ 0 ] );
    assertEquals( v1b, ( ( String[] )parameterMap.get( p1 ) )[ 1 ] );
    assertEquals( v2, ( ( String[] )parameterMap.get( p2 ) )[ 0 ] );
    assertEquals( v3a, ( ( String[] )parameterMap.get( p3 ) )[ 0 ] );
    assertEquals( v3b, ( ( String[] )parameterMap.get( p3 ) )[ 1 ] );

    try {
      parameterMap.clear();
      fail();
    } catch( UnsupportedOperationException usoe ) {
    }
  }

  @Test
  public void testStartupRequestWithParameter() throws Exception {
    ApplicationContextImpl applicationContext = getApplicationContext();
    applicationContext.getEntryPointManager().register( "/rap", DefaultEntryPoint.class, null );
    TestRequest getRequest = Fixture.fakeNewGetRequest();
    getRequest.setParameter( "param", "value" );
    ServiceHandler handler = getApplicationContext().getServiceManager().getHandler();
    handler.service( ContextProvider.getRequest(), ContextProvider.getResponse() );

    TestRequest uiRequest = Fixture.fakeNewRequest();
    uiRequest.setParameter( "param", null );
    Fixture.fakeHeadParameter( RequestParams.RWT_INITIALIZE, "true" );
    handler.service( ContextProvider.getRequest(), ContextProvider.getResponse() );

    assertEquals( "value", ContextProvider.getRequest().getParameter( "param" ) );
  }

  public static final class DefaultEntryPoint implements EntryPoint {
    public int createUI() {
      Display display = new Display();
      if( display.readAndDispatch() ) {
        display.sleep();
      }
      return 0;
    }
  }

}
