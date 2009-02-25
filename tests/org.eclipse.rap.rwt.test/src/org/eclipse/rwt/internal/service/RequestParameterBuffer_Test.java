/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.RWTFixture;


public class RequestParameterBuffer_Test extends TestCase {

  public void testStore() {
    Map parameters = new HashMap();
    parameters.put( "key", "value" );
    RequestParameterBuffer.store( parameters );
    Map bufferedParameters = RequestParameterBuffer.getBufferedParameters();
    assertNotSame( parameters, bufferedParameters );
    assertEquals( "value", bufferedParameters.get( "key" ) );
    // ensure that merge() only works once per session
    parameters = new HashMap();
    parameters.put( "anotherKey", "anotherValue" );
    RequestParameterBuffer.store( parameters );
    assertEquals( "value", bufferedParameters.get( "key" ) );
    assertNull( bufferedParameters.get( "anotherKey" ) );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.createContext( true );
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
    Fixture.removeContext();
  }
}
