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

import java.util.List;
import java.util.Map;

import org.junit.Test;


public class Cell_Test {

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWithoutTemplate() {
    new Cell( null );
  }

  @Test
  public void testAddsItselfToTemplate() {
    RowTemplate template = new RowTemplate();

    Cell cell = new Cell( template );

    List<Cell> cells = template.getCells();
    assertEquals( cells.size(), 1 );
    assertSame( cell, cells.get( 0 ) );
  }

  @Test
  public void testColumnIsNullByDefault() {
    Cell cell = new Cell( new RowTemplate() );

    Integer column = cell.getColumn();

    assertNull( column );
  }

  @Test
  public void testSetsColumn() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setColumn( 1 );

    Integer column = cell.getColumn();
    assertEquals( Integer.valueOf( 1 ), column );
  }

  @Test
  public void testSetsColumnToZero() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setColumn( 0 );

    Integer column = cell.getColumn();
    assertEquals( Integer.valueOf( 0 ), column );
  }

  @Test
  public void testSetColumnReturnsCell() {
    Cell cell = new Cell( new RowTemplate() );

    Cell actualCell = cell.setColumn( 1 );

    assertSame( cell, actualCell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetColumnFailsWithNegativeColumn() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setColumn( -1 );
  }

  @Test
  public void testLeftIsNullByDefault() {
    Cell cell = new Cell( new RowTemplate() );

    Integer left = cell.getLeft();

    assertNull( left );
  }

  @Test
  public void testSetsLeft() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setLeft( 23 );

    Integer left = cell.getLeft();
    assertEquals( Integer.valueOf( 23 ), left );
  }

  @Test
  public void testSetsLeftToNegative() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setLeft( -1 );

    Integer left = cell.getLeft();
    assertEquals( Integer.valueOf( -1 ), left );
  }

  @Test
  public void testSetsLeftToZero() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setLeft( 0 );

    Integer left = cell.getLeft();
    assertEquals( Integer.valueOf( 0 ), left );
  }

  @Test
  public void testSetLeftReturnsCell() {
    Cell cell = new Cell( new RowTemplate() );

    Cell actualCell = cell.setLeft( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testRightIsNullByDefault() {
    Cell cell = new Cell( new RowTemplate() );

    Integer right = cell.getRight();

    assertNull( right );
  }

  @Test
  public void testSetsRight() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setRight( 23 );

    Integer right = cell.getRight();
    assertEquals( Integer.valueOf( 23 ), right );
  }

  @Test
  public void testSetsRightToNegative() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setRight( -1 );

    Integer right = cell.getRight();
    assertEquals( Integer.valueOf( -1 ), right );
  }

  @Test
  public void testSetsRightToZero() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setRight( 0 );

    Integer right = cell.getRight();
    assertEquals( Integer.valueOf( 0 ), right );
  }

  @Test
  public void testSetRightReturnsCell() {
    Cell cell = new Cell( new RowTemplate() );

    Cell actualCell = cell.setRight( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testTopIsNullByDefault() {
    Cell cell = new Cell( new RowTemplate() );

    Integer top = cell.getTop();

    assertNull( top );
  }

  @Test
  public void testSetsTop() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setTop( 23 );

    Integer top = cell.getTop();
    assertEquals( Integer.valueOf( 23 ), top );
  }

  @Test
  public void testSetsTopToNegative() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setTop( -1 );

    Integer top = cell.getTop();
    assertEquals( Integer.valueOf( -1 ), top );
  }

  @Test
  public void testSetsTopToZero() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setTop( 0 );

    Integer top = cell.getTop();
    assertEquals( Integer.valueOf( 0 ), top );
  }

  @Test
  public void testSetTopReturnsCell() {
    Cell cell = new Cell( new RowTemplate() );

    Cell actualCell = cell.setTop( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testBottomIsNullByDefault() {
    Cell cell = new Cell( new RowTemplate() );

    Integer bottom = cell.getBottom();

    assertNull( bottom );
  }

  @Test
  public void testSetsBottom() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setBottom( 23 );

    Integer bottom = cell.getBottom();
    assertEquals( Integer.valueOf( 23 ), bottom );
  }

  @Test
  public void testSetsBottomToNegative() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setBottom( -1 );

    Integer bottom = cell.getBottom();
    assertEquals( Integer.valueOf( -1 ), bottom );
  }

  @Test
  public void testSetsBottomToZero() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setBottom( 0 );

    Integer bottom = cell.getBottom();
    assertEquals( Integer.valueOf( 0 ), bottom );
  }

  @Test
  public void testSetBottomReturnsCell() {
    Cell cell = new Cell( new RowTemplate() );

    Cell actualCell = cell.setBottom( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testWidthIsNullByDefault() {
    Cell cell = new Cell( new RowTemplate() );

    Integer width = cell.getWidth();

    assertNull( width );
  }

  @Test
  public void testSetsWidth() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setWidth( 23 );

    Integer width = cell.getWidth();
    assertEquals( Integer.valueOf( 23 ), width );
  }

  @Test
  public void testSetsWidthToZero() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setWidth( 0 );

    Integer width = cell.getWidth();
    assertEquals( Integer.valueOf( 0 ), width );
  }

  @Test
  public void testSetWidthReturnsCell() {
    Cell cell = new Cell( new RowTemplate() );

    Cell actualCell = cell.setWidth( 23 );

    assertSame( cell, actualCell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetWidthFailsWithNegativeValue() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setWidth( -23 );
  }

  @Test
  public void testHeightIsNullByDefault() {
    Cell cell = new Cell( new RowTemplate() );

    Integer height = cell.getHeight();

    assertNull( height );
  }

  @Test
  public void testSetsHeight() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setHeight( 23 );

    Integer height = cell.getHeight();
    assertEquals( Integer.valueOf( 23 ), height );
  }

  @Test
  public void testSetsHeightToZero() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setHeight( 0 );

    Integer height = cell.getHeight();
    assertEquals( Integer.valueOf( 0 ), height );
  }

  @Test
  public void testSetHeightReturnsCell() {
    Cell cell = new Cell( new RowTemplate() );

    Cell actualCell = cell.setHeight( 23 );

    assertSame( cell, actualCell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetHeightFailsWithNegativeValue() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setHeight( -23 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Left_Right_Width() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setLeft( 10 );
    cell.setRight( 10 );

    cell.setWidth( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Width_Right_Left() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setWidth( 10 );
    cell.setRight( 10 );

    cell.setLeft( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Width_Left_Right() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setLeft( 10 );
    cell.setWidth( 10 );

    cell.setRight( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Top_Bottom_Height() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setTop( 10 );
    cell.setBottom( 10 );

    cell.setHeight( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Bottom_Height_Top() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setBottom( 10 );
    cell.setHeight( 10 );

    cell.setTop( 10 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWith_Height_Top_Bottom() {
    Cell cell = new Cell( new RowTemplate() );

    cell.setHeight( 10 );
    cell.setTop( 10 );

    cell.setBottom( 10 );
  }

  @Test
  public void testAddsAttribute() {
    Cell cell = new Cell( new RowTemplate() );
    Object attribute = new Object();

    cell.addAttribute( "foo", attribute );

    Map<String, Object> attributes = cell.getAttributes();
    assertEquals( 1, attributes.size() );
    assertSame( attribute, attributes.get( "foo" ) );
  }

  @Test
  public void testAddsAllAttribute() {
    Cell cell = new Cell( new RowTemplate() );
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
    Cell cell = new Cell( new RowTemplate() );
    Map<String, Object> attributes = cell.getAttributes();

    cell.addAttribute( "foo", new Object() );

    assertEquals( 0, attributes.size() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddAttributeFailsWithNullKey() {
    Cell cell = new Cell( new RowTemplate() );
    Object attribute = new Object();

    cell.addAttribute( null, attribute );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddAttributeFailsWithEmptyKey() {
    Cell cell = new Cell( new RowTemplate() );
    Object attribute = new Object();

    cell.addAttribute( "", attribute );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddAttributeFailsWithNullValue() {
    Cell cell = new Cell( new RowTemplate() );

    cell.addAttribute( "foo", null );
  }
}
