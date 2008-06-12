/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import java.io.*;

import junit.framework.TestCase;


public class Theme_Test extends TestCase {

  private static final ResourceLoader DUMMY_LOADER = new ResourceLoader() {

    public InputStream getResourceAsStream( final String resourceName )
      throws IOException
    {
      return null;
    }
  };

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
    QxBorder qxBorder1 = QxBorder.valueOf( "1 solid blue" );
    QxBorder qxBorder2 = QxBorder.valueOf( "2 solid green" );
    QxColor qxColor1 = QxColor.valueOf( "#fffaf0" );
    defTheme.setValue( "foo.border", qxBorder1 );
    assertEquals( qxBorder1, defTheme.getValue( "foo.border" ) );
    try {
      defTheme.setValue( "foo.border", qxBorder2 );
      fail( "IAE expected when value is redefined" );
    } catch( final IllegalArgumentException e ) {
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
    QxBorder qxBorder = QxBorder.valueOf( "1 solid blue" );
    QxBoxDimensions qxBoxDimensions = QxBoxDimensions.valueOf( "0 1 2 3" );
    QxColor qxColor = QxColor.valueOf( "#fffaf0" );
    QxDimension qxDimension = QxDimension.valueOf( "5" );
    theme.setValue( "foo.border", qxBorder );
    theme.setValue( "foo.boxdim", qxBoxDimensions );
    theme.setValue( "foo.color", qxColor );
    theme.setValue( "foo.dimension", qxDimension );
    assertEquals( qxBorder, theme.getBorder( "foo.border", null ) );
    assertEquals( qxBoxDimensions, theme.getBoxDimensions( "foo.boxdim", null ) );
    assertEquals( qxColor, theme.getValue( "foo.color" ) );
    assertEquals( qxDimension, theme.getValue( "foo.dimension" ) );
    try {
      theme.getBorder( "foo.color", null );
      fail( "Typed getter must fail for wrong types" );
    } catch( final IllegalArgumentException e ) {
      // expected
    }
  }

  public void testGetKeys() throws Exception {
    QxBorder qxBorder1 = QxBorder.valueOf( "1 solid blue" );
    QxBorder qxBorder2 = QxBorder.valueOf( "2 solid green" );
    QxColor qxColor1 = QxColor.valueOf( "#fffaf0" );
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
    QxBorder qxBorder1 = QxBorder.valueOf( "1 solid blue" );
    QxBorder qxBorder2 = QxBorder.valueOf( "2 solid green" );
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

  public void testLoadFromFileInvalidArguments() throws Exception {
    try {
      Theme.loadFromFile( null, null, null, null );
      fail( "Null arguments should throw NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
//    try {
//      Theme.loadFromFile( "", null, null, null );
//      fail( "Empty name should throw IllegalArgumentException" );
//    } catch( IllegalArgumentException e ) {
//      // expected
//    }
  }

  public void testVariants() throws Exception {
    Theme defTheme = new Theme( "Default Theme" );
    defTheme.setValue( "test.border", QxBorder.NONE );
    defTheme.setValue( "test.color", QxColor.BLACK );
    String[] defKeys = defTheme.getKeys();
    assertNotNull( defKeys );
    assertEquals( 2, defKeys.length );
    String[] defKeysWithVariants = defTheme.getKeysWithVariants();
    assertNotNull( defKeysWithVariants );
    assertEquals( 2, defKeysWithVariants.length );
    Theme theme = new Theme( "Custom Theme", defTheme );
    theme.setValue( "test.border", QxBorder.valueOf( "2px red" ) );
    theme.setValue( "test.color", "myvariant", QxColor.WHITE );
    String[] keys = theme.getKeys();
    assertNotNull( keys );
    assertEquals( 2, keys.length );
    String[] keysWithVariants = theme.getKeysWithVariants();
    assertNotNull( keysWithVariants );
    assertEquals( 3, keysWithVariants.length );
    assertTrue( theme.hasKey( "test.color" ) );
    assertFalse( theme.definesKey( "test.color" ) );
    assertEquals( QxColor.BLACK, defTheme.getColor( "test.color", null ) );
    assertEquals( QxColor.BLACK, theme.getColor( "test.color", null ) );
    assertEquals( QxColor.BLACK, theme.getColor( "test.color", "foovariant" ) );
    assertEquals( QxColor.WHITE, theme.getColor( "test.color", "myvariant" ) );
    assertEquals( QxBorder.NONE, defTheme.getBorder( "test.border", null ) );
    assertEquals( QxBorder.valueOf( "2px red" ),
                  theme.getBorder( "test.border", null ) );
    assertEquals( QxBorder.valueOf( "2px red" ),
                  theme.getBorder( "test.border", "myvariant" ) );
  }

  public void testLoadFromFile() throws Exception {
    Theme defTheme = new Theme( "Default Theme" );
    defTheme.setValue( "test.border", QxBorder.NONE );
    defTheme.setValue( "test.background", QxColor.BLACK );
    String themeContent = "test.border = blue\n"
                          + "mybutton/test.border = red\n"
                          + "mybutton/test.background\n";
    InputStream instr = new ByteArrayInputStream( themeContent.getBytes() );
    Theme theme
      = Theme.loadFromFile( "Test Theme", defTheme, instr, DUMMY_LOADER );
    assertTrue( theme.hasKey( "test.border" ) );
    assertTrue( theme.hasKey( "test.background" ) );
    assertTrue( theme.definesKey( "test.border" ) );
    assertFalse( theme.definesKey( "test.background" ) );
  }
}
