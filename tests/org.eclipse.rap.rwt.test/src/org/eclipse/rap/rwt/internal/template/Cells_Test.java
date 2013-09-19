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
import org.eclipse.swt.SWT;
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
    Cell cell = Cells.createTextCell( null, SWT.NONE, "text" );

    assertNotNull( cell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateTextCellFailsWithNullTemplate() {
    Cell cell = Cells.createTextCell( null, SWT.NONE );

    assertNotNull( cell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateTextCellFailsWithNullText() {
    Cell cell = Cells.createTextCell( new RowTemplate(), SWT.NONE, null );

    assertNotNull( cell );
  }

  @Test
  public void testCreateTextCellUsesTextTypeWithText() {
    CellImpl cell = ( CellImpl )Cells.createTextCell( new RowTemplate(), SWT.NONE, "text" );

    String type = cell.getType();

    assertEquals( type, Cells.TYPE_TEXT );
  }

  @Test
  public void testCreateTextCellUsesTextType() {
    CellImpl cell = ( CellImpl )Cells.createTextCell( new RowTemplate(), SWT.NONE );

    String type = cell.getType();

    assertEquals( type, Cells.TYPE_TEXT );
  }

  @Test
  public void testCreateTextCellSetsStyle() {
    CellImpl cell = ( CellImpl )Cells.createTextCell( new RowTemplate(), SWT.TOP );

    int style = cell.getStyle();

    assertEquals( SWT.TOP, style );
  }

  @Test
  public void testCreateTextCellWithTextSetsStyle() {
    CellImpl cell = ( CellImpl )Cells.createTextCell( new RowTemplate(), SWT.TOP, "text" );

    int style = cell.getStyle();

    assertEquals( SWT.TOP, style );
  }

  @Test
  public void testCreateTextCellReturnsCell() {
    Cell cell = Cells.createTextCell( new RowTemplate(), SWT.NONE, "text" );

    assertNotNull( cell );
  }

  @Test
  public void testCreateTextCellSetsTextAsAttribute() {
    CellImpl cell = ( CellImpl )Cells.createTextCell( new RowTemplate(), SWT.NONE, "foo" );

    Object text = cell.getAttributes().get( Cells.PROPERTY_DEFAULT_TEXT );
    assertEquals( "foo", text );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateImageCellFailsWithNullTemplateButWithImage() {
    Cell cell = Cells.createImageCell( null, SWT.NONE, createImage( Fixture.IMAGE_100x50 ) );

    assertNotNull( cell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateImageCellFailsWithNullTemplate() {
    Cell cell = Cells.createImageCell( null, SWT.NONE );

    assertNotNull( cell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateImageCellFailsWithNullImage() {
    Cell cell = Cells.createImageCell( new RowTemplate(), SWT.NONE, null );

    assertNotNull( cell );
  }

  @Test
  public void testCreateImageCellUsesImageTypeWithImage() {
    CellImpl cell = ( CellImpl )Cells.createImageCell( new RowTemplate(),
                                                       SWT.NONE,
                                                       createImage( Fixture.IMAGE_100x50 ) );

    String type = cell.getType();

    assertEquals( Cells.TYPE_IMAGE, type );
  }

  @Test
  public void testCreateImageCellUsesImageType() {
    CellImpl cell = ( CellImpl )Cells.createImageCell( new RowTemplate(), SWT.NONE );

    String type = cell.getType();

    assertEquals( Cells.TYPE_IMAGE, type );
  }

  @Test
  public void testCreateImageCellSetsStyle() {
    CellImpl cell = ( CellImpl )Cells.createImageCell( new RowTemplate(), SWT.TOP );

    int style = cell.getStyle();

    assertEquals( SWT.TOP, style );
  }

  @Test
  public void testCreateImageCellWithImageSetsStyle() {
    CellImpl cell = ( CellImpl )Cells.createImageCell( new RowTemplate(),
                                                       SWT.TOP,
                                                       createImage( Fixture.IMAGE_100x50 ) );

    int style = cell.getStyle();

    assertEquals( SWT.TOP, style );
  }

  @Test
  public void testCreateImageCellReturnsCell() {
    Cell cell = Cells.createImageCell( new RowTemplate(),
                                       SWT.NONE,
                                       createImage( Fixture.IMAGE_100x50 ) );

    assertNotNull( cell );
  }

  @Test
  public void testCreateImageCellSetsImageAsAttribute() {
    Image image = createImage( Fixture.IMAGE_100x50 );

    CellImpl cell = ( CellImpl )Cells.createImageCell( new RowTemplate(), SWT.NONE, image );

    Object attribtue = cell.getAttributes().get( Cells.PROPERTY_DEFAULT_IMAGE );
    assertSame( image, attribtue );
  }

  private Image createImage( String name ) {
    ClassLoader loader = Fixture.class.getClassLoader();
    return new Image( display, loader.getResourceAsStream( name ) );
  }
}
