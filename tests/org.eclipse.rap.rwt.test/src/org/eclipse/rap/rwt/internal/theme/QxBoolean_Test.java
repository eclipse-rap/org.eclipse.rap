/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;


public class QxBoolean_Test {

  @Test
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

  @Test
  public void testCreate() {
    assertTrue( QxBoolean.valueOf( "true" ).value );
    assertTrue( QxBoolean.valueOf( "yes" ).value );
    assertTrue( QxBoolean.valueOf( "on" ).value );
    assertFalse( QxBoolean.valueOf( "false" ).value );
    assertFalse( QxBoolean.valueOf( "no" ).value );
    assertFalse( QxBoolean.valueOf( "off" ).value );
  }

  @Test
  public void testDefaultString() {
    assertEquals( "true", QxBoolean.valueOf( "yes" ).toDefaultString() );
    assertEquals( "false", QxBoolean.valueOf( "no" ).toDefaultString() );
  }

}
