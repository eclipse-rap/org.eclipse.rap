/*******************************************************************************
 * Copyright (c) 2007, 2014 Innoopract Informationssysteme GmbH and others.
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

import org.junit.Test;


public class CssBoolean_Test {

  @Test( expected = NullPointerException.class )
  public void testValueOf_nullArgument() {
    CssBoolean.valueOf( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_emptyString() {
    CssBoolean.valueOf( "" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_invalidArgument() {
    CssBoolean.valueOf( "foo" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_upperCase() {
    CssBoolean.valueOf( "True" );
  }

  @Test
  public void testCreate() {
    assertTrue( CssBoolean.valueOf( "true" ).value );
    assertTrue( CssBoolean.valueOf( "yes" ).value );
    assertTrue( CssBoolean.valueOf( "on" ).value );
    assertFalse( CssBoolean.valueOf( "false" ).value );
    assertFalse( CssBoolean.valueOf( "no" ).value );
    assertFalse( CssBoolean.valueOf( "off" ).value );
  }

  @Test
  public void testDefaultString() {
    assertEquals( "true", CssBoolean.valueOf( "yes" ).toDefaultString() );
    assertEquals( "false", CssBoolean.valueOf( "no" ).toDefaultString() );
  }

}
