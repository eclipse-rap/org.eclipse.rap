/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CellImpl_Test {

  private Display display;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWithoutTemplate() {
    new CellImpl( null, "foo", SWT.NONE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWithoutType() {
    new CellImpl( new RowTemplate(), null, SWT.NONE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsEmptyType() {
    new CellImpl( new RowTemplate(), "", SWT.NONE );
  }

  @Test
  public void testHasAllAllowedStyles() {
    List<String> styles = Arrays.asList( CellImpl.ALLOWED_STYLES );

    assertTrue( styles.contains( "TOP" ) );
    assertTrue( styles.contains( "BOTTOM" ) );
    assertTrue( styles.contains( "LEFT" ) );
    assertTrue( styles.contains( "RIGHT" ) );
    assertTrue( styles.contains( "CENTER" ) );
    assertTrue( styles.contains( "FILL" ) );
    assertTrue( styles.contains( "WRAP" ) );
  }

  @Test
  public void testAddsItselfToTemplate() {
    RowTemplate template = new RowTemplate();

    Cell cell = new CellImpl( template, "foo", SWT.NONE );

    List<Cell> cells = template.getCells();
    assertEquals( cells.size(), 1 );
    assertSame( cell, cells.get( 0 ) );
  }

  @Test
  public void testHasType() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    String type = cell.getType();

    assertEquals( type, "foo" );
  }

  @Test
  public void testNameIsNullByDefault() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Object name = cell.getAttributes().get( CellImpl.PROPERTY_NAME );

    assertNull( name );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetNameFailsWithNullName() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setName( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetNameFailsWithEmptyName() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setName( "" );
  }

  @Test
  public void testSetsStyle() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.LEFT | SWT.FILL );

    int style = cell.getStyle();

    assertEquals( SWT.LEFT | SWT.FILL, style );
  }

  @Test
  public void testSetsName() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setName( "bar" );

    String name = ( String )cell.getAttributes().get( CellImpl.PROPERTY_NAME );
    assertEquals( name, "bar" );
  }

  @Test
  public void testSetNameReturnsCell() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Cell actualCell = cell.setName( "bar" );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSelectableIsNullByDefault() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Object selectable = cell.getAttributes().get( CellImpl.PROPERTY_SELECTABLE );

    assertNull( selectable );
  }

  @Test
  public void testSetsSelectable() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setSelectable( true );

    Boolean selectable = ( Boolean )cell.getAttributes().get( CellImpl.PROPERTY_SELECTABLE );
    assertTrue( selectable.booleanValue() );
  }

  @Test
  public void testSetSelectableReturnsCell() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Cell actualCell = cell.setSelectable( true );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetsForeground() {
    Color color = new Color( display, 100, 100, 100 );
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setForeground( color );

    Color actualColor = ( Color )cell.getAttributes().get( CellImpl.PROPERTY_FOREGROUND );
    assertSame( color, actualColor );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetForegroundFailsWithNull() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setForeground( null );
  }

  @Test
  public void testSetForegroundReturnsCell() {
    Color color = new Color( display, 100, 100, 100 );
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Cell actualCell = cell.setForeground( color );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetsBackground() {
    Color color = new Color( display, 100, 100, 100 );
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setBackground( color );

    Color actualColor = ( Color )cell.getAttributes().get( CellImpl.PROPERTY_BACKGROUND );
    assertSame( color, actualColor );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetBackgroundFailsWithNull() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setBackground( null );
  }

  @Test
  public void testSetBackgroundReturnsCell() {
    Color color = new Color( display, 100, 100, 100 );
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Cell actualCell = cell.setBackground( color );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetsFont() {
    Font font = new Font( display, "Arial", 22, SWT.NONE );
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setFont( font );

    Font actualFont = ( Font )cell.getAttributes().get( CellImpl.PROPERTY_FONT );
    assertSame( font, actualFont );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetFontFailsWithNull() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setFont( null );
  }

  @Test
  public void testSetFontReturnsCell() {
    Font font = new Font( display, "Arial", 22, SWT.NONE );
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Cell actualCell = cell.setFont( font );

    assertSame( cell, actualCell );
  }

  @Test
  public void testBindingIndexIsNullByDefault() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Object index = cell.getAttributes().get( CellImpl.PROPERTY_BINDING_INDEX );

    assertNull( index );
  }

  @Test
  public void testSetsBindingIndex() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setBindingIndex( 1 );

    Integer index = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_BINDING_INDEX );
    assertEquals( Integer.valueOf( 1 ), index );
  }

  @Test
  public void testSetsBindingIndexToZero() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setBindingIndex( 0 );

    Integer index = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_BINDING_INDEX );
    assertEquals( Integer.valueOf( 0 ), index );
  }

  @Test
  public void testSetBindingIndexReturnsCell() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Cell actualCell = cell.setBindingIndex( 1 );

    assertSame( cell, actualCell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetBindingIndexFailsWithNegativeBindingIndex() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setBindingIndex( -1 );
  }

  @Test
  public void testLeftIsNullByDefault() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Object left = cell.getAttributes().get( CellImpl.PROPERTY_LEFT );

    assertNull( left );
  }

  @Test
  public void testSetsLeft() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setLeft( 23 );

    Integer left = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_LEFT );
    assertEquals( Integer.valueOf( 23 ), left );
  }

  @Test
  public void testSetsLeftToNegative() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setLeft( -1 );

    Integer left = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_LEFT );
    assertEquals( Integer.valueOf( -1 ), left );
  }

  @Test
  public void testSetsLeftToZero() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setLeft( 0 );

    Integer left = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_LEFT );
    assertEquals( Integer.valueOf( 0 ), left );
  }

  @Test
  public void testSetLeftReturnsCell() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Cell actualCell = cell.setLeft( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testRightIsNullByDefault() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Object right = cell.getAttributes().get( CellImpl.PROPERTY_RIGHT );

    assertNull( right );
  }

  @Test
  public void testSetsRight() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setRight( 23 );

    Integer right = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_RIGHT );
    assertEquals( Integer.valueOf( 23 ), right );
  }

  @Test
  public void testSetsRightToNegative() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setRight( -1 );

    Integer right = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_RIGHT );
    assertEquals( Integer.valueOf( -1 ), right );
  }

  @Test
  public void testSetsRightToZero() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setRight( 0 );

    Integer right = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_RIGHT );
    assertEquals( Integer.valueOf( 0 ), right );
  }

  @Test
  public void testSetRightReturnsCell() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Cell actualCell = cell.setRight( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testTopIsNullByDefault() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Object top = cell.getAttributes().get( CellImpl.PROPERTY_TOP );

    assertNull( top );
  }

  @Test
  public void testSetsTop() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setTop( 23 );

    Integer top = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_TOP );
    assertEquals( Integer.valueOf( 23 ), top );
  }

  @Test
  public void testSetsTopToNegative() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setTop( -1 );

    Integer top = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_TOP );
    assertEquals( Integer.valueOf( -1 ), top );
  }

  @Test
  public void testSetsTopToZero() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setTop( 0 );

    Integer top = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_TOP );
    assertEquals( Integer.valueOf( 0 ), top );
  }

  @Test
  public void testSetTopReturnsCell() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Cell actualCell = cell.setTop( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testBottomIsNullByDefault() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Object bottom = cell.getAttributes().get( CellImpl.PROPERTY_BOTTOM );

    assertNull( bottom );
  }

  @Test
  public void testSetsBottom() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setBottom( 23 );

    Integer bottom = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_BOTTOM );
    assertEquals( Integer.valueOf( 23 ), bottom );
  }

  @Test
  public void testSetsBottomToNegative() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setBottom( -1 );

    Integer bottom = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_BOTTOM );
    assertEquals( Integer.valueOf( -1 ), bottom );
  }

  @Test
  public void testSetsBottomToZero() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setBottom( 0 );

    Integer bottom = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_BOTTOM );
    assertEquals( Integer.valueOf( 0 ), bottom );
  }

  @Test
  public void testSetBottomReturnsCell() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Cell actualCell = cell.setBottom( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testWidthIsNullByDefault() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Object width = cell.getAttributes().get( CellImpl.PROPERTY_WIDTH );

    assertNull( width );
  }

  @Test
  public void testSetsWidth() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setWidth( 23 );

    Integer width = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_WIDTH );
    assertEquals( Integer.valueOf( 23 ), width );
  }

  @Test
  public void testSetsWidthToZero() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setWidth( 0 );

    Integer width = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_WIDTH );
    assertEquals( Integer.valueOf( 0 ), width );
  }

  @Test
  public void testSetWidthReturnsCell() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Cell actualCell = cell.setWidth( 23 );

    assertSame( cell, actualCell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetWidthFailsWithNegativeValue() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setWidth( -23 );
  }

  @Test
  public void testHeightIsNullByDefault() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Object height = cell.getAttributes().get( CellImpl.PROPERTY_HEIGHT );

    assertNull( height );
  }

  @Test
  public void testSetsHeight() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setHeight( 23 );

    Integer height = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_HEIGHT );
    assertEquals( Integer.valueOf( 23 ), height );
  }

  @Test
  public void testSetsHeightToZero() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setHeight( 0 );

    Integer height = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_HEIGHT );
    assertEquals( Integer.valueOf( 0 ), height );
  }

  @Test
  public void testSetHeightReturnsCell() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    Cell actualCell = cell.setHeight( 23 );

    assertSame( cell, actualCell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetHeightFailsWithNegativeValue() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setHeight( -23 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Left_Right_Width() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setLeft( 10 );
    cell.setRight( 10 );

    cell.setWidth( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Width_Right_Left() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setWidth( 10 );
    cell.setRight( 10 );

    cell.setLeft( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Width_Left_Right() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setLeft( 10 );
    cell.setWidth( 10 );

    cell.setRight( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Top_Bottom_Height() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setTop( 10 );
    cell.setBottom( 10 );

    cell.setHeight( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Bottom_Height_Top() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setBottom( 10 );
    cell.setHeight( 10 );

    cell.setTop( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Height_Top_Bottom() {
    Cell cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.setHeight( 10 );
    cell.setTop( 10 );

    cell.setBottom( 10 );
  }

  @Test
  public void testAddsAttribute() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );
    Object attribute = new Object();

    cell.addAttribute( "foo", attribute );

    Map<String, Object> attributes = cell.getAttributes();
    assertEquals( 1, attributes.size() );
    assertSame( attribute, attributes.get( "foo" ) );
  }

  @Test
  public void testAddsAllAttribute() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );
    Object attribute1 = new Object();
    Object attribute2 = new Object();

    cell.addAttribute( "foo", attribute1 );
    cell.addAttribute( "bar", attribute2 );

    Map<String, Object> attributes = cell.getAttributes();
    assertEquals( 2, attributes.size() );
    assertSame( attribute1, attributes.get( "foo" ) );
    assertSame( attribute2, attributes.get( "bar" ) );
  }

  @Test
  public void testAttributesAreSafeCopy() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );
    Map<String, Object> attributes = cell.getAttributes();

    cell.addAttribute( "foo", new Object() );

    assertEquals( 0, attributes.size() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddAttributeFailsWithNullKey() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );
    Object attribute = new Object();

    cell.addAttribute( null, attribute );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddAttributeFailsWithEmptyKey() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );
    Object attribute = new Object();

    cell.addAttribute( "", attribute );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddAttributeFailsWithNullValue() {
    CellImpl cell = new CellImpl( new RowTemplate(), "foo", SWT.NONE );

    cell.addAttribute( "foo", null );
  }
}
