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
package org.eclipse.rap.rwt.template;

import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.getJsonForColor;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.getJsonForFont;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.template.Cell;
import org.eclipse.rap.rwt.template.Template;
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
  private Template template;

  @Before
  public void setUp() {
    Fixture.setUp();
    template = new Template();
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
    new TestCell( template, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsEmptyType() {
    new TestCell( template, "" );
  }

  @Test
  public void testAddsItselfToTemplate() {
    Cell<?> cell = new TestCell( template, "foo" );

    List<Cell<?>> cells = template.getCells();
    assertEquals( cells.size(), 1 );
    assertSame( cell, cells.get( 0 ) );
  }

  @Test
  public void testHasType() {
    Cell cell = new TestCell( template, "foo" );

    String type = cell.getType();

    assertEquals( type, "foo" );
  }

  @Test
  public void testSetName() {
    Cell cell = new TestCell( template, "foo" );

    cell.setName( "bar" );

    assertEquals( "bar", cell.getName() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetName_failsWithNullName() {
    Cell<?> cell = new TestCell( template, "foo" );

    cell.setName( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetName_failsWithEmptyName() {
    Cell<?> cell = new TestCell( template, "foo" );

    cell.setName( "" );
  }

  @Test
  public void testSetName_returnsCell() {
    Cell<?> cell = new TestCell( template, "foo" );

    Cell<?> actualCell = cell.setName( "bar" );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetSelectable() {
    Cell cell = new TestCell( template, "foo" );

    cell.setSelectable( true );

    assertTrue( cell.isSelectable() );
  }

  @Test
  public void testSetSelectable_returnsCell() {
    Cell<?> cell = new TestCell( template, "foo" );

    Cell<?> actualCell = cell.setSelectable( true );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetForeground() {
    Color color = new Color( display, 100, 100, 100 );
    Cell cell = new TestCell( template, "foo" );

    cell.setForeground( color );

    assertSame( color, cell.getForeground() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetForeground_failsWithNull() {
    Cell cell = new TestCell( template, "foo" );

    cell.setForeground( null );
  }

  @Test
  public void testSetForeground_returnsCell() {
    Color color = new Color( display, 100, 100, 100 );
    Cell<?> cell = new TestCell( template, "foo" );

    Cell<?> actualCell = cell.setForeground( color );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetBackground() {
    Color color = new Color( display, 100, 100, 100 );
    Cell cell = new TestCell( template, "foo" );

    cell.setBackground( color );

    assertSame( color, cell.getBackground() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetBackground_failsWithNull() {
    Cell cell = new TestCell( template, "foo" );

    cell.setBackground( null );
  }

  @Test
  public void testSetBackground_returnsCell() {
    Color color = new Color( display, 100, 100, 100 );
    Cell<?> cell = new TestCell( template, "foo" );

    Cell<?> actualCell = cell.setBackground( color );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetFont() {
    Font font = new Font( display, "Arial", 22, SWT.BOLD );
    Cell cell = new TestCell( template, "foo" );

    cell.setFont( font );

    assertSame( font, cell.getFont() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetFont_failsWithNull() {
    Cell cell = new TestCell( template, "foo" );

    cell.setFont( null );
  }

  @Test
  public void testSetFont_returnsCell() {
    Font font = new Font( display, "Arial", 22, SWT.BOLD );
    Cell<?> cell = new TestCell( template, "foo" );

    Cell<?> actualCell = cell.setFont( font );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetBindingIndex() {
    Cell cell = new TestCell( template, "foo" );

    cell.setBindingIndex( 1 );

    assertEquals( 1, cell.getBindingIndex() );
  }

  @Test
  public void testSetBindingIndex_acceptsZero() {
    Cell cell = new TestCell( template, "foo" );

    cell.setBindingIndex( 0 );

    assertEquals( 0, cell.getBindingIndex() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetBindingIndex_failsWithNegativeValue() {
    Cell cell = new TestCell( template, "foo" );

    cell.setBindingIndex( -1 );
  }

  @Test
  public void testSetBindingIndex_returnsCell() {
    Cell<?> cell = new TestCell( template, "foo" );

    Cell<?> actualCell = cell.setBindingIndex( 1 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetLeft() {
    Cell cell = new TestCell( template, "foo" );

    cell.setLeft( 23 );

    assertEquals( Integer.valueOf( 23 ), cell.getLeft() );
  }

  @Test
  public void testSetLeft_acceptsZero() {
    Cell cell = new TestCell( template, "foo" );

    cell.setLeft( 0 );

    assertEquals( Integer.valueOf( 0 ), cell.getLeft() );
  }

  @Test
  public void testSetLeft_acceptsNegativeValues() {
    Cell cell = new TestCell( template, "foo" );

    cell.setLeft( -1 );

    assertEquals( Integer.valueOf( -1 ), cell.getLeft() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetLeft_failsWith_Width_Right() {
    Cell<?> cell = new TestCell( template, "foo" );
    cell.setWidth( 10 );
    cell.setRight( 10 );

    cell.setLeft( 10 );
  }

  @Test
  public void testSetLeft_returnsCell() {
    Cell<?> cell = new TestCell( template, "foo" );

    Cell<?> actualCell = cell.setLeft( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetRight() {
    Cell cell = new TestCell( template, "foo" );

    cell.setRight( 23 );

    Integer right = cell.getRight();
    assertEquals( Integer.valueOf( 23 ), right );
  }

  @Test
  public void testSetRight_acceptsZero() {
    Cell cell = new TestCell( template, "foo" );

    cell.setRight( 0 );

    Integer right = cell.getRight();
    assertEquals( Integer.valueOf( 0 ), right );
  }

  @Test
  public void testSetRight_acceptsNegativeValues() {
    Cell cell = new TestCell( template, "foo" );

    cell.setRight( -1 );

    Integer right = cell.getRight();
    assertEquals( Integer.valueOf( -1 ), right );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetRight_failsWith_Width_Left() {
    Cell<?> cell = new TestCell( template, "foo" );
    cell.setLeft( 10 );
    cell.setWidth( 10 );

    cell.setRight( 10 );
  }

  @Test
  public void testSetRight_returnsCell() {
    Cell<?> cell = new TestCell( template, "foo" );

    Cell<?> actualCell = cell.setRight( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetTop() {
    Cell cell = new TestCell( template, "foo" );

    cell.setTop( 23 );

    assertEquals( Integer.valueOf( 23 ), cell.getTop() );
  }

  @Test
  public void testSetTop_acceptsZero() {
    Cell cell = new TestCell( template, "foo" );

    cell.setTop( 0 );

    assertEquals( Integer.valueOf( 0 ), cell.getTop() );
  }

  @Test
  public void testSetTop_acceptsNegativeValues() {
    Cell cell = new TestCell( template, "foo" );

    cell.setTop( -1 );

    assertEquals( Integer.valueOf( -1 ), cell.getTop() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetTop_failsWith_Bottom_Height() {
    Cell<?> cell = new TestCell( template, "foo" );
    cell.setBottom( 10 );
    cell.setHeight( 10 );

    cell.setTop( 10 );
  }

  @Test
  public void testSetTop_returnsCell() {
    Cell<?> cell = new TestCell( template, "foo" );

    Cell<?> actualCell = cell.setTop( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetBottom() {
    Cell cell = new TestCell( template, "foo" );

    cell.setBottom( 23 );

    assertEquals( Integer.valueOf( 23 ), cell.getBottom() );
  }

  @Test
  public void testSetBottom_acceptsNegativeValues() {
    Cell cell = new TestCell( template, "foo" );

    cell.setBottom( -1 );

    assertEquals( Integer.valueOf( -1 ), cell.getBottom() );
  }

  @Test
  public void testSetBottom_acceptsZero() {
    Cell cell = new TestCell( template, "foo" );

    cell.setBottom( 0 );

    assertEquals( Integer.valueOf( 0 ), cell.getBottom() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetBottom_failsWith_Height_Top() {
    Cell<?> cell = new TestCell( template, "foo" );
    cell.setHeight( 10 );
    cell.setTop( 10 );

    cell.setBottom( 10 );
  }

  @Test
  public void testSetBottom_returnsCell() {
    Cell<?> cell = new TestCell( template, "foo" );

    Cell<?> actualCell = cell.setBottom( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetWidth() {
    Cell cell = new TestCell( template, "foo" );

    cell.setWidth( 23 );

    assertEquals( Integer.valueOf( 23 ), cell.getWidth() );
  }

  @Test
  public void testSetWidth_acceptsZero() {
    Cell cell = new TestCell( template, "foo" );

    cell.setWidth( 0 );

    assertEquals( Integer.valueOf( 0 ), cell.getWidth() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetWidth_failsWithNegativeValue() {
    Cell<?> cell = new TestCell( template, "foo" );

    cell.setWidth( -23 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetWidth_failsWith_Left_Right() {
    Cell<?> cell = new TestCell( template, "foo" );
    cell.setLeft( 10 );
    cell.setRight( 10 );

    cell.setWidth( 10 );
  }

  @Test
  public void testSetWidth_returnsCell() {
    Cell<?> cell = new TestCell( template, "foo" );

    Cell<?> actualCell = cell.setWidth( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetHeight() {
    Cell cell = new TestCell( template, "foo" );

    cell.setHeight( 23 );

    assertEquals( Integer.valueOf( 23 ), cell.getHeight() );
  }

  @Test
  public void testSetHeight_acceptsZero() {
    Cell cell = new TestCell( template, "foo" );

    cell.setHeight( 0 );

    assertEquals( Integer.valueOf( 0 ), cell.getHeight() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetHeight_failsWithNegativeValues() {
    Cell<?> cell = new TestCell( template, "foo" );

    cell.setHeight( -23 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetHeight_failsWith_Top_Bottom() {
    Cell<?> cell = new TestCell( template, "foo" );
    cell.setTop( 10 );
    cell.setBottom( 10 );

    cell.setHeight( 10 );
  }

  @Test
  public void testSetHeight_returnsCell() {
    Cell<?> cell = new TestCell( template, "foo" );

    Cell<?> actualCell = cell.setHeight( 23 );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetHorizontalAlignment() {
    Cell cell = new Cell( template, "foo" ) {};

    cell.setHorizontalAlignment( SWT.CENTER );

    assertEquals( SWT.CENTER, cell.getHorizontalAlignment() );
  }

  @Test
  public void testSetHorizontalAlignment_returnsCell() {
    Cell cell = new TestCell( template, "foo" );

    Cell actualCell = cell.setHorizontalAlignment( SWT.CENTER );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetVerticalAlignment() {
    Cell cell = new Cell( template, "foo" ) {};

    cell.setVerticalAlignment( SWT.CENTER );

    assertEquals( SWT.CENTER, cell.getVerticalAlignment() );
  }

  @Test
  public void testSetVerticalAlignment_returnsCell() {
    Cell cell = new TestCell( template, "foo" );

    Cell actualCell = cell.setVerticalAlignment( SWT.CENTER );

    assertSame( cell, actualCell );
  }

  @Test
  public void testToJson_containsType() {
    Cell cell = new Cell( template, "foo" ) {};

    JsonObject json = cell.toJson();

    assertEquals( "foo", json.get( "type" ).asString() );
  }

  @Test
  public void testToJson_doesNotContainOtherPropertiesByDefault() {
    Cell cell = new Cell( template, "foo" ) {};

    JsonObject json = cell.toJson();

    assertEquals( 1, json.size() );
  }

  @Test
  public void testToJson_containsLeft() {
    Cell cell = new Cell( template, "foo" ) {};

    cell.setLeft( 23 );
    JsonObject json = cell.toJson();

    assertEquals( 23, json.get( "left" ).asInt() );
  }

  @Test
  public void testToJson_containsRight() {
    Cell cell = new Cell( template, "foo" ) {};

    cell.setRight( 23 );
    JsonObject json = cell.toJson();

    assertEquals( 23, json.get( "right" ).asInt() );
  }

  @Test
  public void testToJson_containsTop() {
    Cell cell = new Cell( template, "foo" ) {};

    cell.setTop( 23 );
    JsonObject json = cell.toJson();

    assertEquals( 23, json.get( "top" ).asInt() );
  }

  @Test
  public void testToJson_containsBottom() {
    Cell cell = new Cell( template, "foo" ) {};

    cell.setBottom( 23 );
    JsonObject json = cell.toJson();

    assertEquals( 23, json.get( "bottom" ).asInt() );
  }

  @Test
  public void testToJson_containsWidth() {
    Cell cell = new Cell( template, "foo" ) {};

    cell.setWidth( 23 );
    JsonObject json = cell.toJson();

    assertEquals( 23, json.get( "width" ).asInt() );
  }

  @Test
  public void testToJson_containsHeight() {
    Cell cell = new Cell( template, "foo" ) {};

    cell.setHeight( 23 );
    JsonObject json = cell.toJson();

    assertEquals( 23, json.get( "height" ).asInt() );
  }

  @Test
  public void testToJson_containsBindingIndex() {
    Cell cell = new Cell( template, "foo" ) {};

    cell.setBindingIndex( 23 );
    JsonObject json = cell.toJson();

    assertEquals( 23, json.get( "bindingIndex" ).asInt() );
  }

  @Test
  public void testToJson_containsSelectable() {
    Cell cell = new Cell( template, "foo" ) {};

    cell.setSelectable( true );
    JsonObject json = cell.toJson();

    assertTrue( json.get( "selectable" ).asBoolean() );
  }

  @Test
  public void testToJson_containsName() {
    Cell cell = new Cell( template, "foo" ) {};

    cell.setName( "bar" );
    JsonObject json = cell.toJson();

    assertEquals( "bar", json.get( "name" ).asString() );
  }

  @Test
  public void testToJson_containsForeground() {
    Cell cell = new Cell( template, "foo" ) {};
    Color color = new Color( display, 1, 2, 3 );

    cell.setForeground( color );
    JsonObject json = cell.toJson();

    assertEquals( getJsonForColor( color, false ), json.get( "foreground" ) );
  }

  @Test
  public void testToJson_containsBackground() {
    Cell cell = new Cell( template, "foo" ) {};
    Color color = new Color( display, 1, 2, 3 );

    cell.setBackground( color );
    JsonObject json = cell.toJson();

    assertEquals( getJsonForColor( color, false ), json.get( "background" ) );
  }

  @Test
  public void testToJson_containsFont() {
    Cell cell = new Cell( template, "foo" ) {};
    Font font = new Font( display, "Helvetica", 12, SWT.BOLD );

    cell.setFont( font );
    JsonObject json = cell.toJson();

    assertEquals( getJsonForFont( font ), json.get( "font" ) );
  }

  @Test
  public void testToJson_containsHorizontalAlignment() {
    Cell cell = new Cell( template, "foo" ) {};

    cell.setHorizontalAlignment( SWT.CENTER );
    JsonObject json = cell.toJson();

    assertEquals( "CENTER", json.get( "horizontalAlignment" ).asString() );
  }

  @Test
  public void testToJson_containsVerticalAlignment() {
    Cell cell = new Cell( template, "foo" ) {};

    cell.setVerticalAlignment( SWT.CENTER );
    JsonObject json = cell.toJson();

    assertEquals( "CENTER", json.get( "verticalAlignment" ).asString() );
  }

  @Test
  public void testToJson_horizontalAlignmentFormat() {
    assertEquals( "LEFT", hAlignToJson( SWT.LEFT ) );
    assertEquals( "RIGHT", hAlignToJson( SWT.RIGHT ) );
    assertEquals( "CENTER", hAlignToJson( SWT.CENTER ) );
    assertEquals( "LEFT", hAlignToJson( SWT.BEGINNING ) );
    assertEquals( "RIGHT", hAlignToJson( SWT.END ) );
    assertEquals( "LEFT", hAlignToJson( SWT.LEFT | SWT.TOP ) );
  }

  @Test
  public void testToJson_verticalAlignmentFormat() {
    assertEquals( "TOP", vAlignToJson( SWT.TOP ) );
    assertEquals( "BOTTOM", vAlignToJson( SWT.BOTTOM ) );
    assertEquals( "CENTER", vAlignToJson( SWT.CENTER ) );
    assertEquals( "TOP", vAlignToJson( SWT.BEGINNING ) );
    assertEquals( "BOTTOM", vAlignToJson( SWT.END ) );
    assertEquals( "TOP", vAlignToJson( SWT.LEFT | SWT.TOP ) );
  }

  private String vAlignToJson( int alignment ) {
    Cell cell = new Cell( template, "foo" ) {};
    cell.setVerticalAlignment( alignment );
    JsonObject json = cell.toJson();
    return json.get( "verticalAlignment" ).asString();
  }

  private String hAlignToJson( int alignment ) {
    Cell cell = new Cell( template, "foo" ) {};
    cell.setHorizontalAlignment( alignment );
    JsonObject json = cell.toJson();
    return json.get( "horizontalAlignment" ).asString();
  }

}
