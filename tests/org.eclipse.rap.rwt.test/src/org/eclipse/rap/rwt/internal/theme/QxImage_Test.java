/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.eclipse.rap.rwt.internal.theme.ThemeTestUtil.RESOURCE_LOADER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class QxImage_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testIllegalArguments() {
    try {
      QxImage.valueOf( null, null );
      fail( "Must throw NPE" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      QxImage.valueOf( "", null );
      fail( "Must throw NPE" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      QxImage.valueOf( "", RESOURCE_LOADER );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      QxImage.createGradient( null, new float[] {}, true );
      fail( "Must throw NPE" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      QxImage.createGradient( new String[] {}, null, true );
      fail( "Must throw NPE" );
    } catch( NullPointerException e ) {
      // expected
    }
  }

  @Test
  public void testNotExisting() {
    try {
      QxImage.valueOf( "not-existing.png", RESOURCE_LOADER );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testNone() {
    assertSame( QxImage.NONE, QxImage.valueOf( "none", null ) );
    assertSame( QxImage.NONE, QxImage.valueOf( "none", RESOURCE_LOADER ) );
    assertTrue( QxImage.NONE.none );
    assertNull( QxImage.NONE.path );
    assertNull( QxImage.NONE.loader );
    assertNull( QxImage.NONE.gradientColors );
    assertNull( QxImage.NONE.gradientPercents );
    assertTrue( QxImage.NONE.vertical );
    assertEquals( 0, QxImage.NONE.width );
    assertEquals( 0, QxImage.NONE.height );
  }

  @Test
  public void testCreateImage() {
    QxImage qxImage = QxImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    assertFalse( qxImage.none );
    assertEquals( Fixture.IMAGE_50x100, qxImage.path );
    assertSame( RESOURCE_LOADER, qxImage.loader );
    assertNull( qxImage.gradientColors );
    assertNull( qxImage.gradientPercents );
    assertTrue( qxImage.vertical );
    assertEquals( 50, qxImage.width );
    assertEquals( 100, qxImage.height );
    assertFalse( qxImage.isGradient() );
  }

  @Test
  public void testCreateVerticalGradient() {
    String[] gradientColors = new String[] { "#FF0000", "#00FF00", "#0000FF" };
    float[] gradientPercents = new float[] { 0f, 50f, 100f };
    QxImage qxImage = QxImage.createGradient( gradientColors, gradientPercents, true );
    assertSame( gradientColors, qxImage.gradientColors );
    assertSame( gradientPercents, qxImage.gradientPercents );
    assertTrue( qxImage.vertical );
    assertTrue( qxImage.none );
    assertNull( qxImage.path );
    assertNull( qxImage.loader );
    assertEquals( 0, qxImage.width );
    assertEquals( 0, qxImage.height );
    assertTrue( qxImage.isGradient() );
  }

  @Test
  public void testCreateHorizontalGradient() {
    String[] gradientColors = new String[] { "#FF0000", "#00FF00", "#0000FF" };
    float[] gradientPercents = new float[] { 0f, 50f, 100f };
    QxImage qxImage = QxImage.createGradient( gradientColors, gradientPercents, false );
    assertSame( gradientColors, qxImage.gradientColors );
    assertSame( gradientPercents, qxImage.gradientPercents );
    assertFalse( qxImage.vertical );
    assertTrue( qxImage.none );
    assertNull( qxImage.path );
    assertNull( qxImage.loader );
    assertEquals( 0, qxImage.width );
    assertEquals( 0, qxImage.height );
    assertTrue( qxImage.isGradient() );
  }

  @Test
  public void testDefaultString() {
    assertEquals( "none", QxImage.NONE.toDefaultString() );
    assertEquals( "", QxImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER ).toDefaultString() );
  }

  @Test
  public void testHashCode() {
    assertEquals( -1526341861, QxImage.NONE.hashCode() );
    QxImage qxImage1 = QxImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    QxImage qxImage2 = QxImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    assertEquals( qxImage1, qxImage2 );
    assertEquals( qxImage1.hashCode(), qxImage2.hashCode() );
    String[] gradientColors = new String[] { "#FF0000", "#00FF00", "#0000FF" };
    float[] gradientPercents = new float[] { 0f, 50f, 100f };
    QxImage gradient1 = QxImage.createGradient( gradientColors, gradientPercents, true );
    QxImage gradient2 = QxImage.createGradient( gradientColors, gradientPercents, true );
    assertEquals( gradient1, gradient2 );
    assertEquals( gradient1.hashCode(), gradient2.hashCode() );
  }

  @Test
  public void testHashCode_GradientWithMoreColors() {
    String[] gradientColors1
      = new String[] { "#FFFFFF", "#00AA00", "#00AA00", "#00AA00", "#FFFFFF" };
    float[] gradientPercents1 = new float[] { 0f, 48f, 52f, 56f, 100f };
    String[] gradientColors2
      = new String[] { "#FFFFFF", "#AA0000", "#AA0000", "#AA0000", "#FFFFFF" };
    float[] gradientPercents2 = new float[] { 0f, 48f, 52f, 56f, 100f };
    QxImage gradient1 = QxImage.createGradient( gradientColors1, gradientPercents1, true );
    QxImage gradient2 = QxImage.createGradient( gradientColors2, gradientPercents2, true );
    assertFalse( gradient1.hashCode() == gradient2.hashCode() );
  }

  @Test
  public void testIsGradientFalseForNone() {
    QxImage nonImage = QxImage.NONE;
    assertFalse( nonImage.isGradient() );
  }

  @Test
  public void testGetResourceName() {
    QxImage image = QxImage.NONE;
    assertNull( image.getResourcePath() );
    image = QxImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    assertEquals( "themes/images/ba873d77.png", image.getResourcePath() );
  }

  @Test
  public void testCreateSWTImageFromNone() throws IOException {
    QxImage image = QxImage.NONE;
    try {
      QxImage.createSwtImage( image );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testCreateSWTImageFromGradient() throws IOException {
    String[] gradientColors = new String[] { "#FF0000", "#00FF00", "#0000FF" };
    float[] gradientPercents = new float[] { 0f, 50f, 100f };
    QxImage gradient = QxImage.createGradient( gradientColors, gradientPercents, true );
    try {
      QxImage.createSwtImage( gradient );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testCreateSWTImage() throws IOException {
    Display display = new Display();
    QxImage image = QxImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    Image swtImage = QxImage.createSwtImage( image );
    assertNotNull( swtImage );
    assertSame( display, swtImage.getDevice() );
  }

  @Test
  public void testGetResourcePath() {
    QxImage image = QxImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );

    assertTrue( image.getResourcePath().startsWith( "themes/images/" ) );
  }

  @Test
  public void testResourcePathsDiffer() {
    QxImage image1 = QxImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    QxImage image2 = QxImage.valueOf( Fixture.IMAGE_100x50, RESOURCE_LOADER );

    assertFalse( image1.getResourcePath().equals( image2.getResourcePath() ) );
  }

  @Test
  public void testGetResourcePathWithNone() {
    assertNull( QxImage.NONE.getResourcePath() );
  }

  @Test
  public void testGetResourcePathWithGradient() {
    assertNull( createGradient().getResourcePath() );
  }

  @Test
  public void testGetResourceAsStream() throws IOException {
    QxImage image = QxImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    InputStream inputStream = image.getResourceAsStream();

    assertTrue( inputStream.available() > 0 );
    inputStream.close();
  }

  @Test
  public void testGetResourceAsStreamWithNone() throws IOException {
    assertNull( QxImage.NONE.getResourceAsStream() );
  }

  @Test
  public void testGetResourceAsStreamWithGradient() throws IOException {
    assertNull( createGradient().getResourceAsStream() );
  }

  private static QxImage createGradient() {
    String[] gradientColors = new String[] { "#FF0000", "#0000FF" };
    float[] gradientPercents = new float[] { 0f, 100f };
    return QxImage.createGradient( gradientColors, gradientPercents, false );
  }

}
