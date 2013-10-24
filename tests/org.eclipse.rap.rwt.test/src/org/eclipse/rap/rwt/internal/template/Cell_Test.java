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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.internal.template.Cell.CellAlignment;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Cell_Test {

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

  @Test( expected = NullPointerException.class )
  public void testFailsWithoutTemplate() {
    new TestCell( null, "foo" );
  }

  @Test( expected = NullPointerException.class )
  public void testFailsWithoutType() {
    new TestCell( new RowTemplate(), null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsEmptyType() {
    new TestCell( new RowTemplate(), "" );
  }

  @Test
  public void testAddsItselfToTemplate() {
    RowTemplate template = new RowTemplate();

    Cell<?> cell = new TestCell( template, "foo" );

    List<Cell<?>> cells = template.getCells();
    assertEquals( cells.size(), 1 );
    assertSame( cell, cells.get( 0 ) );
  }

  @Test
  public void testHasType() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    String type = cell.getType();

    assertEquals( type, "foo" );
  }

  @Test
  public void testNameIsNullByDefault() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    Object name = getAttributes( cell ).get( Cell.PROPERTY_NAME );

    assertNull( name );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetNameFailsWithNullName() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    cell.setName( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetNameFailsWithEmptyName() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    cell.setName( "" );
  }

  @Test
  public void testSetsName() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setName( "bar" );

    String name = ( String )getAttributes( cell ).get( Cell.PROPERTY_NAME );
    assertEquals( name, "bar" );
  }

  @Test
  public void testSetNameReturnsCell() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    Cell<?> actualCell = cell.setName( "bar" );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSelectableIsNullByDefault() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    Object selectable = getAttributes( cell ).get( Cell.PROPERTY_SELECTABLE );

    assertNull( selectable );
  }

  @Test
  public void testSetsSelectable() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setSelectable( true );

    Boolean selectable = ( Boolean )getAttributes( cell ).get( Cell.PROPERTY_SELECTABLE );
    assertTrue( selectable.booleanValue() );
  }

  @Test
  public void testSetSelectableReturnsCell() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    Cell<?> actualCell = cell.setSelectable( true );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetsForeground() {
    Color color = new Color( display, 100, 100, 100 );
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setForeground( color );

    Color actualColor = ( Color )getAttributes( cell ).get( Cell.PROPERTY_FOREGROUND );
    assertSame( color, actualColor );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetForegroundFailsWithNull() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setForeground( null );
  }

  @Test
  public void testSetForegroundReturnsCell() {
    Color color = new Color( display, 100, 100, 100 );
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    Cell<?> actualCell = cell.setForeground( color );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetsBackground() {
    Color color = new Color( display, 100, 100, 100 );
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setBackground( color );

    Color actualColor = ( Color )getAttributes( cell ).get( Cell.PROPERTY_BACKGROUND );
    assertSame( color, actualColor );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetBackgroundFailsWithNull() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setBackground( null );
  }

  @Test
  public void testSetBackgroundReturnsCell() {
    Color color = new Color( display, 100, 100, 100 );
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    Cell<?> actualCell = cell.setBackground( color );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetsFont() {
    Font font = new Font( display, "Arial", 22, SWT.BOLD );
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setFont( font );

    Font actualFont = ( Font )getAttributes( cell ).get( Cell.PROPERTY_FONT );
    assertSame( font, actualFont );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetFontFailsWithNull() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setFont( null );
  }

  @Test
  public void testSetFontReturnsCell() {
    Font font = new Font( display, "Arial", 22, SWT.BOLD );
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    Cell<?> actualCell = cell.setFont( font );

    assertSame( cell, actualCell );
  }

  @Test
  public void testBindingIndexIsNullByDefault() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    Object index = getAttributes( cell ).get( Cell.PROPERTY_BINDING_INDEX );

    assertNull( index );
  }

  @Test
  public void testSetsBindingIndex() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setBindingIndex( 1 );

    Integer index = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_BINDING_INDEX );
    assertEquals( Integer.valueOf( 1 ), index );
  }

  @Test
  public void testSetsBindingIndexToZero() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setBindingIndex( 0 );

    Integer index = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_BINDING_INDEX );
    assertEquals( Integer.valueOf( 0 ), index );
  }

  @Test
  public void testSetBindingIndexReturnsCell() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    Cell<?> actualCell = cell.setBindingIndex( 1 );

    assertSame( cell, actualCell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetBindingIndexFailsWithNegativeBindingIndex() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    cell.setBindingIndex( -1 );
  }

  @Test
  public void testLeftIsNullByDefault() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    Object left = getAttributes( cell ).get( Cell.PROPERTY_LEFT );

    assertNull( left );
  }

  @Test
  public void testSetsLeft() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setLeft( 23 );

    Integer left = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_LEFT );
    assertEquals( Integer.valueOf( 23 ), left );
  }

  @Test
  public void testSetsLeftToNegative() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setLeft( -1 );

    Integer left = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_LEFT );
    assertEquals( Integer.valueOf( -1 ), left );
  }

  @Test
  public void testSetsLeftToZero() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setLeft( 0 );

    Integer left = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_LEFT );
    assertEquals( Integer.valueOf( 0 ), left );
  }

  @Test
  public void testSetLeftReturnsCell() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    Cell<?> actualCell = cell.setLeft( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testRightIsNullByDefault() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    Object right = getAttributes( cell ).get( Cell.PROPERTY_RIGHT );

    assertNull( right );
  }

  @Test
  public void testSetsRight() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setRight( 23 );

    Integer right = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_RIGHT );
    assertEquals( Integer.valueOf( 23 ), right );
  }

  @Test
  public void testSetsRightToNegative() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setRight( -1 );

    Integer right = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_RIGHT );
    assertEquals( Integer.valueOf( -1 ), right );
  }

  @Test
  public void testSetsRightToZero() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setRight( 0 );

    Integer right = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_RIGHT );
    assertEquals( Integer.valueOf( 0 ), right );
  }

  @Test
  public void testSetRightReturnsCell() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    Cell<?> actualCell = cell.setRight( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testTopIsNullByDefault() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    Object top = getAttributes( cell ).get( Cell.PROPERTY_TOP );

    assertNull( top );
  }

  @Test
  public void testSetsTop() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setTop( 23 );

    Integer top = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_TOP );
    assertEquals( Integer.valueOf( 23 ), top );
  }

  @Test
  public void testSetsTopToNegative() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setTop( -1 );

    Integer top = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_TOP );
    assertEquals( Integer.valueOf( -1 ), top );
  }

  @Test
  public void testSetsTopToZero() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setTop( 0 );

    Integer top = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_TOP );
    assertEquals( Integer.valueOf( 0 ), top );
  }

  @Test
  public void testSetTopReturnsCell() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    Cell<?> actualCell = cell.setTop( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testBottomIsNullByDefault() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    Object bottom = getAttributes( cell ).get( Cell.PROPERTY_BOTTOM );

    assertNull( bottom );
  }

  @Test
  public void testSetsBottom() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setBottom( 23 );

    Integer bottom = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_BOTTOM );
    assertEquals( Integer.valueOf( 23 ), bottom );
  }

  @Test
  public void testSetsBottomToNegative() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setBottom( -1 );

    Integer bottom = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_BOTTOM );
    assertEquals( Integer.valueOf( -1 ), bottom );
  }

  @Test
  public void testSetsBottomToZero() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setBottom( 0 );

    Integer bottom = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_BOTTOM );
    assertEquals( Integer.valueOf( 0 ), bottom );
  }

  @Test
  public void testSetBottomReturnsCell() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    Cell<?> actualCell = cell.setBottom( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testWidthIsNullByDefault() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    Object width = getAttributes( cell ).get( Cell.PROPERTY_WIDTH );

    assertNull( width );
  }

  @Test
  public void testSetsWidth() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setWidth( 23 );

    Integer width = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_WIDTH );
    assertEquals( Integer.valueOf( 23 ), width );
  }

  @Test
  public void testSetsWidthToZero() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setWidth( 0 );

    Integer width = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_WIDTH );
    assertEquals( Integer.valueOf( 0 ), width );
  }

  @Test
  public void testSetWidthReturnsCell() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    Cell<?> actualCell = cell.setWidth( 23 );

    assertSame( cell, actualCell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetWidthFailsWithNegativeValue() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    cell.setWidth( -23 );
  }

  @Test
  public void testHeightIsNullByDefault() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    Object height = getAttributes( cell ).get( Cell.PROPERTY_HEIGHT );

    assertNull( height );
  }

  @Test
  public void testSetsHeight() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setHeight( 23 );

    Integer height = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_HEIGHT );
    assertEquals( Integer.valueOf( 23 ), height );
  }

  @Test
  public void testSetsHeightToZero() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setHeight( 0 );

    Integer height = ( Integer )getAttributes( cell ).get( Cell.PROPERTY_HEIGHT );
    assertEquals( Integer.valueOf( 0 ), height );
  }

  @Test
  public void testSetHeightReturnsCell() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    Cell<?> actualCell = cell.setHeight( 23 );

    assertSame( cell, actualCell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetHeightFailsWithNegativeValue() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    cell.setHeight( -23 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Left_Right_Width() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    cell.setLeft( 10 );
    cell.setRight( 10 );

    cell.setWidth( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Width_Right_Left() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    cell.setWidth( 10 );
    cell.setRight( 10 );

    cell.setLeft( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Width_Left_Right() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    cell.setLeft( 10 );
    cell.setWidth( 10 );

    cell.setRight( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Top_Bottom_Height() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    cell.setTop( 10 );
    cell.setBottom( 10 );

    cell.setHeight( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Bottom_Height_Top() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    cell.setBottom( 10 );
    cell.setHeight( 10 );

    cell.setTop( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Height_Top_Bottom() {
    Cell<?> cell = new TestCell( new RowTemplate(), "foo" );

    cell.setHeight( 10 );
    cell.setTop( 10 );

    cell.setBottom( 10 );
  }

  @Test
  public void testAddsAttribute() {
    TestCell cell = new TestCell( new RowTemplate(), "foo" );
    Object attribute = new Object();

    cell.addAttribute( "foo", attribute );

    Map<String, Object> attributes = getAttributes( cell );
    assertEquals( 1, attributes.size() );
    assertSame( attribute, attributes.get( "foo" ) );
  }

  @Test
  public void testAddsAllAttribute() {
    TestCell cell = new TestCell( new RowTemplate(), "foo" );
    Object attribute1 = new Object();
    Object attribute2 = new Object();

    cell.addAttribute( "foo", attribute1 );
    cell.addAttribute( "bar", attribute2 );

    Map<String, Object> attributes = getAttributes( cell );
    assertEquals( 2, attributes.size() );
    assertSame( attribute1, attributes.get( "foo" ) );
    assertSame( attribute2, attributes.get( "bar" ) );
  }

  @Test
  public void testAttributesAreSafeCopy() {
    TestCell cell = new TestCell( new RowTemplate(), "foo" );
    Map<String, Object> attributes = getAttributes( cell );

    cell.addAttribute( "foo", new Object() );

    assertEquals( 0, attributes.size() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddAttributeFailsWithNullKey() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );
    Object attribute = new Object();

    cell.addAttribute( null, attribute );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddAttributeFailsWithEmptyKey() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );
    Object attribute = new Object();

    cell.addAttribute( "", attribute );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddAttributeFailsWithNullValue() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.addAttribute( "foo", null );
  }

  @Test
  @SuppressWarnings( "unchecked" )
  public void testHasCellData() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    Object data = cell.getAdapter( CellData.class );

    assertNotNull( data );
  }

  @Test
  @SuppressWarnings( "unchecked" )
  public void testCellDataIsSameInstance() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    Object data = cell.getAdapter( CellData.class );
    Object data2 = cell.getAdapter( CellData.class );

    assertSame( data, data2 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetAlignmentFailsWithNullAlignment() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setAlignment( ( CellAlignment )null );
  }

  @Test
  @SuppressWarnings( "unchecked" )
  public void testSetsAlignmentAsStringArray() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    cell.setAlignment( CellAlignment.LEFT, CellAlignment.RIGHT );

    CellData data = ( CellData )cell.getAdapter( CellData.class );
    String[] actualAlignment = ( String[] )data.getAttributes().get( Cell.PROPERTY_ALIGNMENT );
    assertArrayEquals( new String[] { "LEFT", "RIGHT" }, actualAlignment );
  }

  @Test
  public void testSetAlignmentReturnsCell() {
    Cell cell = new TestCell( new RowTemplate(), "foo" );

    Cell actualCell = cell.setAlignment( CellAlignment.LEFT, CellAlignment.RIGHT );

    assertSame( cell, actualCell );
  }

  private Map<String, Object> getAttributes( Cell<?> cell ) {
    return cell.getAdapter( CellData.class ).getAttributes();
  }

}
