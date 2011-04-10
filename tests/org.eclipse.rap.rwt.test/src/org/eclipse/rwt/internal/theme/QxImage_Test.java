/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class QxImage_Test extends TestCase {

  private static final ResourceLoader RESOURCE_LOADER
    = ThemeTestUtil.createResourceLoader( Fixture.class );

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

  public void testNotExisting() {
    try {
      QxImage.valueOf( "not-existing.png", RESOURCE_LOADER );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

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

  public void testDefaultString() {
    assertEquals( "none", QxImage.NONE.toDefaultString() );
    assertEquals( "", QxImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER ).toDefaultString() );
  }

  public void testHashCode() {
    assertEquals( -1, QxImage.NONE.hashCode() );
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

  public void testIsGradientFalseForNone() {
    QxImage nonImage = QxImage.NONE;
    assertFalse( nonImage.isGradient() );
  }

  public void testGetResourceName() {
    QxImage image = QxImage.NONE;
    assertNull( image.getResourceName() );
    image = QxImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    assertEquals( "themes/images/cd56ce7d", image.getResourceName() );
  }

  public void testCreateSWTImageFromNone() throws IOException {
    QxImage image = QxImage.NONE;
    try {
      QxImage.createSwtImage( image );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

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

  public void testCreateSWTImage() throws IOException {
    Fixture.setUp();
    try {
      Display display = new Display();
      QxImage image = QxImage.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
      Image swtImage = QxImage.createSwtImage( image );
      assertNotNull( swtImage );
      assertSame( display, swtImage.getDevice() );
    } finally {
      Fixture.tearDown();
    }
  }
}
