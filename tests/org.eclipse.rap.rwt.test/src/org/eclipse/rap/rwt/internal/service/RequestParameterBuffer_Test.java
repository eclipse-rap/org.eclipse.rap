/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RequestParameterBuffer_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testStore() {
    Map<String, String[]> originalParameters = new HashMap<String, String[]>();
    originalParameters.put( "key", new String[] { "value" } );

    RequestParameterBuffer.store( originalParameters );

    Map bufferedParameters = RequestParameterBuffer.getBufferedParameters();
    assertNotSame( originalParameters, bufferedParameters );
    assertArrayEquals( new String[]{ "value" }, ( String[] )bufferedParameters.get( "key" ) );
  }

  @Test
  public void testStoreOverridesBuffer() {
    // see bug 369549
    Map<String, String[]> originalParameters = new HashMap<String, String[]>();
    originalParameters.put( "key1", new String[] { "value1" } );
    originalParameters.put( "key2", new String[] { "value2" } );
    Map<String, String[]> newParameters = new HashMap<String, String[]>();
    newParameters.put( "key2", new String[] { "value2a" } );
    newParameters.put( "key3", new String[] { "value3" } );

    RequestParameterBuffer.store( originalParameters );
    RequestParameterBuffer.store( newParameters );

    Map bufferedParameters = RequestParameterBuffer.getBufferedParameters();
    assertNull( bufferedParameters.get( "key1" ) );
    assertArrayEquals( new String[] { "value2a" }, (String[])bufferedParameters.get( "key2" ) );
    assertArrayEquals( new String[] { "value3" }, (String[])bufferedParameters.get( "key3" ) );
  }

  @Test
  public void testMerge() {
    Map<String, String[]> parameters = new HashMap<String, String[]>();
    parameters.put( "key1", new String[] { "value1" } );
    parameters.put( "key2", new String[] { "value2" } );
    RequestParameterBuffer.store( parameters );

    TestRequest request = Fixture.fakeNewRequest();
    request.setParameter( "key2", "value2a" );
    request.setParameter( "key3", "value3" );
    RequestParameterBuffer.merge();

    assertEquals( "value1", ContextProvider.getRequest().getParameter( "key1" ) );
    assertEquals( "value2a", ContextProvider.getRequest().getParameter( "key2" ) );
    assertEquals( "value3", ContextProvider.getRequest().getParameter( "key3" ) );
  }

  @Test
  public void testMergeOnlyOnce() {
    Map<String, String[]> parameters = new HashMap<String, String[]>();
    parameters.put( "key1", new String[] { "value1" } );
    RequestParameterBuffer.store( parameters );

    Fixture.fakeNewRequest();
    RequestParameterBuffer.merge();
    Fixture.fakeNewRequest();
    RequestParameterBuffer.merge();

    assertNull( ContextProvider.getRequest().getParameter( "key1" ) );
  }

}
