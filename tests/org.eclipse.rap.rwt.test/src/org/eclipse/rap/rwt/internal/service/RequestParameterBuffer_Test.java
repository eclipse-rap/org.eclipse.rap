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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;


public class RequestParameterBuffer_Test extends TestCase {

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testStore() {
    Map<String, String[]> originalParameters = new HashMap<String, String[]>();
    originalParameters.put( "key", new String[] { "value" } );

    RequestParameterBuffer.store( originalParameters );

    Map bufferedParameters = RequestParameterBuffer.getBufferedParameters();
    assertNotSame( originalParameters, bufferedParameters );
    assertArrayEquals( new String[]{ "value" }, ( String[] )bufferedParameters.get( "key" ) );
  }

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

  public void testMerge() {
    Map<String, String[]> parameters = new HashMap<String, String[]>();
    parameters.put( "key1", new String[] { "value1" } );
    parameters.put( "key2", new String[] { "value2" } );
    RequestParameterBuffer.store( parameters );

    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( "key2", "value2a" );
    Fixture.fakeRequestParam( "key3", "value3" );
    RequestParameterBuffer.merge();

    assertEquals( "value1", ContextProvider.getRequest().getParameter( "key1" ) );
    assertEquals( "value2a", ContextProvider.getRequest().getParameter( "key2" ) );
    assertEquals( "value3", ContextProvider.getRequest().getParameter( "key3" ) );
  }

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
