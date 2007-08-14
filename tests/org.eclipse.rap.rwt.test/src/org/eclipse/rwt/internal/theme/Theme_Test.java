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
  }
  
  public void testName() throws Exception {
    Theme theme;
    theme = new Theme( "f" );
    assertEquals( "F", theme.getName() );
    theme = new Theme( "foo" );
    assertEquals( "Foo", theme.getName() );
    theme = new Theme( "Foo" );
    assertEquals( "Foo", theme.getName() );
  }
  
  public void testGetSetValues() throws Exception {
    Theme theme;
    theme = new Theme( "foo" );
    QxBorder qxBorder1 = new QxBorder( "1 solid blue" );
    QxBorder qxBorder2 = new QxBorder( "2 solid green" );
    QxColor qxColor1 = new QxColor( "#fffaf0" );
    theme.setValue( "foo.border", qxBorder1 );
    assertEquals( qxBorder1, theme.getValue( "foo.border" ) );
    theme.setValue( "foo.border", qxBorder2 );
    assertEquals( qxBorder2, theme.getValue( "foo.border" ) );
    try {
      theme.setValue( "foo.border", qxColor1 );
      fail( "Type of themeable property must remain unchangeable" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      theme.getValue( "foo.foreground" );
      fail( "Getting an undefined property should result in an Exception" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    theme.setValue( "foo.foreground", qxColor1 );
    assertEquals( qxColor1, theme.getValue( "foo.foreground" ) );
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
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testGetKeys() throws Exception {
    Theme theme;
    theme = new Theme( "foo" );
    QxBorder qxBorder1 = new QxBorder( "1 solid blue" );
    QxBorder qxBorder2 = new QxBorder( "2 solid green" );
    QxColor qxColor1 = new QxColor( "#fffaf0" );
    theme.setValue( "foo.border", qxBorder1 );
    theme.setValue( "foo.border", qxBorder2 );
    theme.setValue( "foo.foreground", qxColor1 );
    String[] keys = theme.getKeys();
    assertEquals( 2, keys.length );
  }
}
