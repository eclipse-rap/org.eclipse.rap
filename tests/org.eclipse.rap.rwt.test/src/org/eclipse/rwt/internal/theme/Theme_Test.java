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

package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;


public class Theme_Test extends TestCase {

  public void testConstructor() throws Exception {
    try {
      new Theme( null );
      fail( "Null argument should throw NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      new Theme( null, null );
      fail( "Null argument should throw NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      new Theme( "" );
      fail( "Empty name should throw IllegalArgumentException" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    Theme defTheme = new Theme( "Default Theme" );
    assertNotNull( defTheme );
    Theme anotherTheme = new Theme( "Another Theme", defTheme );
    assertNotNull( anotherTheme );
  }

  public void testGetSetValues() throws Exception {
    Theme defTheme = new Theme( "Default Theme" );
    QxBorder qxBorder1 = new QxBorder( "1 solid blue" );
    QxBorder qxBorder2 = new QxBorder( "2 solid green" );
    QxColor qxColor1 = new QxColor( "#fffaf0" );
    defTheme.setValue( "foo.border", qxBorder1 );
    assertEquals( qxBorder1, defTheme.getValue( "foo.border" ) );
    try {
      defTheme.setValue( "foo.border", qxBorder2 );
      fail( "IAE expected when value is redefined" );
    } catch( final Exception e ) {
      // expected
    }
    try {
      defTheme.setValue( "foo.border", qxColor1 );
      fail( "Type of themeable property must remain unchangeable" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
    try {
      defTheme.getValue( "foo.foreground" );
      fail( "Getting an undefined property should result in an Exception" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
    defTheme.setValue( "foo.foreground", qxColor1 );
    assertEquals( qxColor1, defTheme.getValue( "foo.foreground" ) );
    Theme theme = new Theme( "Another Theme", defTheme );
    try {
      theme.setValue( "foo.bar", qxColor1 );
      fail( "Setting an undefined key in derived theme should result in IAE" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
    assertEquals( qxBorder1, theme.getValue( "foo.border" ) );
    theme.setValue( "foo.border", qxBorder2 );
    assertEquals( qxBorder2, theme.getValue( "foo.border" ) );
  }

  public void testGetSetTypedValues() throws Exception {
    Theme theme;
    theme = new Theme( "foo" );
    QxBorder qxBorder = new QxBorder( "1 solid blue" );
    QxBoxDimensions qxBoxDimensions = new QxBoxDimensions( "0 1 2 3" );
    QxColor qxColor = new QxColor( "#fffaf0" );
    QxDimension qxDimension = new QxDimension( "5" );
    theme.setValue( "foo.border", qxBorder );
    theme.setValue( "foo.boxdim", qxBoxDimensions );
    theme.setValue( "foo.color", qxColor );
    theme.setValue( "foo.dimension", qxDimension );
    assertEquals( qxBorder, theme.getBorder( "foo.border" ) );
    assertEquals( qxBoxDimensions, theme.getBoxDimensions( "foo.boxdim" ) );
    assertEquals( qxColor, theme.getValue( "foo.color" ) );
    assertEquals( qxDimension, theme.getValue( "foo.dimension" ) );
    try {
      theme.getBorder( "foo.color" );
      fail( "Typed getter must fail for wrong types" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
  }

  public void testGetKeys() throws Exception {
    QxBorder qxBorder1 = new QxBorder( "1 solid blue" );
    QxBorder qxBorder2 = new QxBorder( "2 solid green" );
    QxColor qxColor1 = new QxColor( "#fffaf0" );
    Theme defTheme = new Theme( "Default Theme" );
    defTheme.setValue( "foo.foreground", qxColor1 );
    defTheme.setValue( "foo.border", qxBorder1 );
    Theme theme = new Theme( "Another Theme", defTheme );
    theme.setValue( "foo.border", qxBorder2 );
    String[] defKeys = defTheme.getKeys();
    assertEquals( 2, defKeys.length );
    String[] keys = theme.getKeys();
    assertEquals( 2, keys.length );
  }

  public void testHasKey() throws Exception {
    Theme defTheme = new Theme( "Default Theme" );
    QxBorder qxBorder1 = new QxBorder( "1 solid blue" );
    QxBorder qxBorder2 = new QxBorder( "2 solid green" );
    assertFalse( defTheme.hasKey( "foo.border" ) );
    assertFalse( defTheme.definesKey( "foo.border" ) );
    defTheme.setValue( "foo.border", qxBorder1 );
    assertTrue( defTheme.hasKey( "foo.border" ) );
    assertTrue( defTheme.definesKey( "foo.border" ) );
    Theme theme = new Theme( "Another Theme", defTheme );
    assertTrue( theme.hasKey( "foo.border" ) );
    assertFalse( theme.definesKey( "foo.border" ) );
    theme.setValue( "foo.border", qxBorder2 );
    assertTrue( theme.hasKey( "foo.border" ) );
    assertTrue( theme.definesKey( "foo.border" ) );
  }
}
