/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.template;

import static org.eclipse.rap.rwt.remote.JsonMapping.toJson;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.template.ImageCell.ScaleMode;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ImageCell_Test {

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

  @Test
  public void testHasImageType() {
    ImageCell cell = new ImageCell( template );

    String type = cell.getType();

    assertEquals( "image", type );
  }

  @Test
  public void testSetImage() {
    ImageCell cell = new ImageCell( template );
    Image image = createImage( Fixture.IMAGE1 );

    cell.setImage( image );

    assertSame( image, cell.getImage() );
  }

  @Test
  public void testSetImage_acceptsNull() {
    ImageCell cell = new ImageCell( template );
    cell.setImage( createImage( Fixture.IMAGE1 ) );

    cell.setImage( null );

    assertNull( cell.getImage() );
  }

  @Test
  public void testSetImage_returnsCell() {
    ImageCell cell = new ImageCell( template );
    Image image = createImage( Fixture.IMAGE1 );

    ImageCell actualCell = cell.setImage( image );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetScaleMode() {
    ImageCell cell = new ImageCell( template );

    cell.setScaleMode( ScaleMode.FILL );

    assertSame( ScaleMode.FILL, cell.getScaleMode() );
  }

  @Test( expected = NullPointerException.class )
  public void testSetScaleMode_failsWithNull() {
    ImageCell cell = new ImageCell( template );

    cell.setScaleMode( ( ScaleMode )null );
  }

  @Test
  public void testSetScaleMode_returnsCell() {
    ImageCell cell = new ImageCell( template );

    ImageCell actualCell = cell.setScaleMode( ScaleMode.FILL );

    assertSame( cell, actualCell );
  }

  @Test
  public void testToJson_containsType() {
    ImageCell cell = new ImageCell( template );

    JsonObject json = cell.toJson();

    assertEquals( "image", json.get( "type" ).asString() );
  }

  @Test
  public void testToJson_doesNotContainOtherPropertiesByDefault() {
    ImageCell cell = new ImageCell( template );

    JsonObject json = cell.toJson();

    assertEquals( 1, json.size() );
  }

  @Test
  public void testToJson_containsImage() {
    ImageCell cell = new ImageCell( template );
    Image image = createImage( Fixture.IMAGE1 );

    cell.setImage( image );
    JsonObject json = cell.toJson();

    assertEquals( toJson( image ), json.get( "image" ) );
  }

  @Test
  public void testToJson_containsScaleMode() {
    ImageCell cell = new ImageCell( template );

    cell.setScaleMode( ScaleMode.FILL );
    JsonObject json = cell.toJson();

    assertEquals( "FILL", json.get( "scaleMode" ).asString() );
  }

  private Image createImage( String name ) {
    ClassLoader loader = Fixture.class.getClassLoader();
    return new Image( display, loader.getResourceAsStream( name ) );
  }

}
