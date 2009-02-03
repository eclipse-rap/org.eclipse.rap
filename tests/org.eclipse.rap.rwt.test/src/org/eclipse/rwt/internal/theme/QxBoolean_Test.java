/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;


public class QxBoolean_Test extends TestCase {

  public void testIllegalArguments() {
    try {
      QxBoolean.valueOf( null );
      fail( "Must throw NPE" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      QxBoolean.valueOf( "" );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      QxBoolean.valueOf( "foo" );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      QxBoolean.valueOf( "True" );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testCreate() {
    assertTrue( QxBoolean.valueOf( "true" ).value );
    assertTrue( QxBoolean.valueOf( "yes" ).value );
    assertTrue( QxBoolean.valueOf( "on" ).value );
    assertFalse( QxBoolean.valueOf( "false" ).value );
    assertFalse( QxBoolean.valueOf( "no" ).value );
    assertFalse( QxBoolean.valueOf( "off" ).value );
  }

  public void testSame() {
    assertSame( QxBoolean.TRUE, QxBoolean.valueOf( "true" ) );
    assertSame( QxBoolean.FALSE, QxBoolean.valueOf( "no" ) );
    assertNotSame( QxBoolean.TRUE, QxBoolean.FALSE );
  }

  public void testDefaultString() {
    assertEquals( "true", QxBoolean.TRUE.toDefaultString() );
    assertEquals( "false", QxBoolean.FALSE.toDefaultString() );
  }
}
