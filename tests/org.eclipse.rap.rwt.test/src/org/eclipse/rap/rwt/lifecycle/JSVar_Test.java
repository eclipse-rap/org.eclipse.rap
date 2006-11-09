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

package org.eclipse.rap.rwt.lifecycle;

import junit.framework.TestCase;


public class JSVar_Test extends TestCase {
  
  public void testConstructor() {
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
}
