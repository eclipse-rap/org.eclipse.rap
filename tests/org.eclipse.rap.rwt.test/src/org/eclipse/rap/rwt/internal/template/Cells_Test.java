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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Cells_Test {

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
  public void testCreateTextCellFailsWithNullTemplate() {
    Cell cell = Cells.createTextCell( null, "text" );

    assertNotNull( cell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateTextCellFailsWithNullTemplateButWithBindingINdex() {
    Cell cell = Cells.createTextCell( null, 23 );

    assertNotNull( cell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateTextCellFailsWithNullText() {
    Cell cell = Cells.createTextCell( new RowTemplate(), null );

    assertNotNull( cell );
  }

  @Test
  public void testCreateTextCellUsesTextType() {
    CellImpl cell = ( CellImpl )Cells.createTextCell( new RowTemplate(), "text" );

    String type = cell.getType();

    assertEquals( type, Cells.TYPE_TEXT );
  }

  @Test
  public void testCreateTextCellUsesTextTypeWithBindingIndex() {
    CellImpl cell = ( CellImpl )Cells.createTextCell( new RowTemplate(), 23 );

    String type = cell.getType();

    assertEquals( type, Cells.TYPE_TEXT );
  }

  @Test
  public void testCreateTextCellReturnsCell() {
    Cell cell = Cells.createTextCell( new RowTemplate(), "text" );

    assertNotNull( cell );
  }

  @Test
  public void testCreateTextCellSetsTextAsAttribute() {
    CellImpl cell = ( CellImpl )Cells.createTextCell( new RowTemplate(), "foo" );

    Object text = cell.getAttributes().get( Cells.PROPERTY_TEXT );
    assertEquals( "foo", text );
  }

  @Test
  public void testCreateTextCellSetsBindingIndex() {
    CellImpl cell = ( CellImpl )Cells.createTextCell( new RowTemplate(), 23 );

    Integer index = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_BINDING_INDEX );
    assertEquals( 23, index.intValue() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateImageCellFailsWithNullTemplate() {
    Cell cell = Cells.createImageCell( null, createImage( Fixture.IMAGE_100x50 ) );

    assertNotNull( cell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateImageCellFailsWithNullTemplateButWithBindingIndex() {
    Cell cell = Cells.createImageCell( null, 23 );

    assertNotNull( cell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateImageCellFailsWithNullImage() {
    Cell cell = Cells.createImageCell( new RowTemplate(), null );

    assertNotNull( cell );
  }

  @Test
  public void testCreateImageCellUsesImageType() {
    CellImpl cell = ( CellImpl )Cells.createImageCell( new RowTemplate(), createImage( Fixture.IMAGE_100x50 ) );

    String type = cell.getType();

    assertEquals( Cells.TYPE_IMAGE, type );
  }

  @Test
  public void testCreateImageCellUsesImageTypeWithBindingIndex() {
    CellImpl cell = ( CellImpl )Cells.createImageCell( new RowTemplate(), 23 );

    String type = cell.getType();

    assertEquals( Cells.TYPE_IMAGE, type );
  }

  @Test
  public void testCreateImageCellReturnsCell() {
    Cell cell = Cells.createImageCell( new RowTemplate(), createImage( Fixture.IMAGE_100x50 ) );

    assertNotNull( cell );
  }

  @Test
  public void testCreateImageCellSetsImageAsAttribute() {
    Image image = createImage( Fixture.IMAGE_100x50 );

    CellImpl cell = ( CellImpl )Cells.createImageCell( new RowTemplate(), image );

    Object attribtue = cell.getAttributes().get( Cells.PROPERTY_IMAGE );
    assertSame( image, attribtue );
  }

  @Test
  public void testCreateImageCellSetsBindingIndex() {
    CellImpl cell = ( CellImpl )Cells.createImageCell( new RowTemplate(), 23 );

    Integer index = ( Integer )cell.getAttributes().get( CellImpl.PROPERTY_BINDING_INDEX );
    assertEquals( 23, index.intValue() );
  }

  private Image createImage( String name ) {
    ClassLoader loader = Fixture.class.getClassLoader();
    return new Image( display, loader.getResourceAsStream( name ) );
  }
}
