/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import static org.junit.Assert.assertArrayEquals;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;


public class RequestParameterBuffer_Test extends TestCase {

  public void testStore() {
    Map<String, String[]> parameters = new HashMap<String, String[]>();
    parameters.put( "key", new String[] { "value" } );

    RequestParameterBuffer.store( parameters );
    Map bufferedParameters = RequestParameterBuffer.getBufferedParameters();

    assertNotSame( parameters, bufferedParameters );
    assertArrayEquals( new String[]{ "value" }, ( String[] )bufferedParameters.get( "key" ) );

    // ensure that merge() only works once per session
    parameters = new HashMap<String, String[]>();
    parameters.put( "anotherKey", new String[] { "anotherValue" } );
    RequestParameterBuffer.store( parameters );

    assertArrayEquals( new String[] { "value" }, (String[])bufferedParameters.get( "key" ) );
    assertNull( bufferedParameters.get( "anotherKey" ) );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

}
