/*******************************************************************************
 * Copyright (c) 2008, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;

import org.eclipse.swt.RWTFixture;


public class QxImage_Test extends TestCase {

  private static final ResourceLoader RESOURCE_LOADER
    = ThemeTestUtil.createResourceLoader( QxImage_Test.class );

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
      QxImage.createGradient( null, new float[] {} );
      fail( "Must throw NPE" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      QxImage.createGradient( new String[] {}, null );
      fail( "Must throw NPE" );
    } catch( NullPointerException e ) {
      // expected
    }
  }

  public void testNone() {
    assertSame( QxImage.NONE, QxImage.valueOf( "none", null ) );
    assertSame( QxImage.NONE, QxImage.valueOf( "none", RESOURCE_LOADER ) );
    assertNotSame( QxImage.NONE, QxImage.valueOf( "None", RESOURCE_LOADER ) );
    assertTrue( QxImage.NONE.none );
    assertNull( QxImage.NONE.path );
    assertNull( QxImage.NONE.loader );
    assertNull( QxImage.NONE.gradientColors );
    assertNull( QxImage.NONE.gradientPercents );
  }

  public void testCreate() {
    QxImage qxImage = QxImage.valueOf( "foo", RESOURCE_LOADER );
    assertFalse( qxImage.none );
    assertEquals( "foo", qxImage.path );
    assertSame( RESOURCE_LOADER, qxImage.loader );
    assertNull( qxImage.gradientColors );
    assertNull( qxImage.gradientPercents );

    String[] gradientColors = new String[] { "#FF0000", "#00FF00", "#0000FF" };
    float[] gradientPercents = new float[] { 0f, 50f, 100f };
    qxImage = QxImage.createGradient( gradientColors, gradientPercents );
    assertSame( gradientColors, qxImage.gradientColors );
    assertSame( gradientPercents, qxImage.gradientPercents );
    assertTrue( qxImage.none );
    assertNull( qxImage.path );
    assertNull( qxImage.loader );
  }

  public void testDefaultString() {
    assertEquals( "none", QxImage.NONE.toDefaultString() );
    assertEquals( "", QxImage.valueOf( "foo", RESOURCE_LOADER ).toDefaultString() );
  }

  public void testHashCode() {
    assertEquals( -1, QxImage.NONE.hashCode() );
    QxImage qxImage1 = QxImage.valueOf( "None", RESOURCE_LOADER );
    QxImage qxImage2 = QxImage.valueOf( "None", RESOURCE_LOADER );
    assertEquals( qxImage1.hashCode(), qxImage2.hashCode() );
  }

  public void testSize() {
    QxImage qxImage1 = QxImage.valueOf( "None", RESOURCE_LOADER );
    assertEquals( 0, qxImage1.width );
    assertEquals( 0, qxImage1.height );
    QxImage qxImage2 = QxImage.valueOf( RWTFixture.IMAGE_50x100,
                                        RESOURCE_LOADER );
    assertEquals( 50, qxImage2.width );
    assertEquals( 100, qxImage2.height );
  }
}
