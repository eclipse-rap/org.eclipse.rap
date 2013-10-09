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
import static org.junit.Assert.assertSame;

import org.eclipse.rap.rwt.internal.template.ImageCell.ScaleMode;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ImageCell_Test {

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

  @Test
  public void testHasImageType() {
    ImageCell cell = new ImageCell( new RowTemplate() );

    String type = cell.getType();

    assertEquals( ImageCell.TYPE_IMAGE, type );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetDefaultImageFailsWithNullImage() {
    ImageCell cell = new ImageCell( new RowTemplate() );

    cell.setDefaultImage( null );
  }

  @Test
  public void testSetsDefaultImage() {
    ImageCell cell = new ImageCell( new RowTemplate() );
    Image image = createImage( Fixture.IMAGE1 );

    cell.setDefaultImage( image );

    Object actualImage = cell.getAdapter( CellData.class ).getAttributes().get( ImageCell.PROPERTY_DEFAULT_IMAGE );
    assertSame( image, actualImage );
  }

  @Test
  public void testSetsDefaultImageReturnsCell() {
    ImageCell cell = new ImageCell( new RowTemplate() );
    Image image = createImage( Fixture.IMAGE1 );

    ImageCell actualCell = cell.setDefaultImage( image );

    assertSame( cell, actualCell );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetScaleModeFailsWithNullStyle() {
    ImageCell cell = new ImageCell( new RowTemplate() );

    cell.setScaleMode( ( ScaleMode )null );
  }

  @Test
  public void testSetsModeAsString() {
    ImageCell cell = new ImageCell( new RowTemplate() );

    cell.setScaleMode( ScaleMode.STRETCH );

    Object actualMode = cell.getAdapter( CellData.class ).getAttributes().get( ImageCell.PROPERTY_SCALE_MODE );
    assertEquals( ScaleMode.STRETCH.name(), actualMode );
  }

  @Test
  public void testSetScaleModeReturnsCell() {
    ImageCell cell = new ImageCell( new RowTemplate() );

    ImageCell actualCell = cell.setScaleMode( ScaleMode.STRETCH );

    assertSame( cell, actualCell );
  }

  private Image createImage( String name ) {
    ClassLoader loader = Fixture.class.getClassLoader();
    return new Image( display, loader.getResourceAsStream( name ) );
  }
}
