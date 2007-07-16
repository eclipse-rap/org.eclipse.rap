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

package org.eclipse.swt.internal.theme;

import junit.framework.TestCase;


public class QxBoolean_Test extends TestCase {

  public void testConstructor() throws Exception {
    QxBoolean qxBoolean;
    try {
      qxBoolean = new QxBoolean( null );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      qxBoolean = new QxBoolean( "" );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      qxBoolean = new QxBoolean( "foo" );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    qxBoolean = new QxBoolean( "true" );
    assertTrue( qxBoolean.value );
    qxBoolean = new QxBoolean( "yes" );
    assertTrue( qxBoolean.value );
    qxBoolean = new QxBoolean( "on" );
    assertTrue( qxBoolean.value );
    qxBoolean = new QxBoolean( "false" );
    assertFalse( qxBoolean.value );
    qxBoolean = new QxBoolean( "no" );
    assertFalse( qxBoolean.value );
    qxBoolean = new QxBoolean( "off" );
    assertFalse( qxBoolean.value );
  }

  public void testDefaultString() throws Exception {
    QxBoolean qxBoolean;
    qxBoolean = new QxBoolean( "true" );
    assertEquals( "true", qxBoolean.toDefaultString() );
    qxBoolean = new QxBoolean( "false" );
    assertEquals( "false", qxBoolean.toDefaultString() );
  }
}
