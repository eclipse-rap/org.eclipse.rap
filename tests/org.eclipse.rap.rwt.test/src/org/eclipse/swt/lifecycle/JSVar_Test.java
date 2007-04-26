/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.lifecycle;

import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;


public class JSVar_Test extends TestCase {
  
  public void testConstructor() {
    RWTFixture.fakeNewRequest();
    JSVar var1Request1 = new JSVar();
    JSVar var2Request1 = new JSVar();
    assertFalse( var1Request1.toString().equals( var2Request1.toString() ) );
    RWTFixture.fakeNewRequest();
    JSVar var1Request2 = new JSVar();
    assertEquals( var1Request2.toString(), var1Request1.toString() );
    try {
      new JSVar( null );
      fail( "Must not allow null variable name" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      new JSVar( "" );
      fail( "Must not allow empty variable name" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testToString() {
    JSVar var = new JSVar( "x" );
    assertEquals( "x", var.toString() );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
