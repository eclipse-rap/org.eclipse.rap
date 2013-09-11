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
  public void testCreateTextCellFailsWithNullTemplateButWithText() {
    Cell cell = Cells.createTextCell( null, "text" );

    assertNotNull( cell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateTextCellFailsWithNullTemplate() {
    Cell cell = Cells.createTextCell( null );

    assertNotNull( cell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateTextCellFailsWithNullText() {
    Cell cell = Cells.createTextCell( new RowTemplate(), null );

    assertNotNull( cell );
  }

  @Test
  public void testCreateTextCellUsesTextTypeWithText() {
    CellImpl cell = ( CellImpl )Cells.createTextCell( new RowTemplate(), "text" );

    String type = cell.getType();

    assertEquals( type, Cells.TYPE_TEXT );
  }

  @Test
  public void testCreateTextCellUsesTextType() {
    CellImpl cell = ( CellImpl )Cells.createTextCell( new RowTemplate() );

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

    Object text = cell.getAttributes().get( Cells.PROPERTY_DEFAULT_TEXT );
    assertEquals( "foo", text );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateImageCellFailsWithNullTemplateButWithImage() {
    Cell cell = Cells.createImageCell( null, createImage( Fixture.IMAGE_100x50 ) );

    assertNotNull( cell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateImageCellFailsWithNullTemplate() {
    Cell cell = Cells.createImageCell( null );

    assertNotNull( cell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateImageCellFailsWithNullImage() {
    Cell cell = Cells.createImageCell( new RowTemplate(), null );

    assertNotNull( cell );
  }

  @Test
  public void testCreateImageCellUsesImageTypeWithImage() {
    CellImpl cell = ( CellImpl )Cells.createImageCell( new RowTemplate(), createImage( Fixture.IMAGE_100x50 ) );

    String type = cell.getType();

    assertEquals( Cells.TYPE_IMAGE, type );
  }

  @Test
  public void testCreateImageCellUsesImageType() {
    CellImpl cell = ( CellImpl )Cells.createImageCell( new RowTemplate() );

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

    Object attribtue = cell.getAttributes().get( Cells.PROPERTY_DEFAULT_IMAGE );
    assertSame( image, attribtue );
  }

  private Image createImage( String name ) {
    ClassLoader loader = Fixture.class.getClassLoader();
    return new Image( display, loader.getResourceAsStream( name ) );
  }
}
