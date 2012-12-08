/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.eclipse.rap.rwt.internal.theme.ThemeTestUtil.RESOURCE_LOADER;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;


public class QxCursor_Test extends TestCase {

  @Override
  protected void setUp() {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() {
    Fixture.tearDown();
  }

  public void testIllegalArguments() {
    try {
      QxCursor.valueOf( null, null );
      fail( "Must throw NPE" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      QxCursor.valueOf( "", null );
      fail( "Must throw NPE" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      QxCursor.valueOf( "", RESOURCE_LOADER );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      QxCursor.valueOf( "alabala" );
      fail( "Must throw IAE" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testNotExisting() {
    try {
      QxCursor.valueOf( "not-existing.cur", RESOURCE_LOADER );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testPredefinedCursor() {
    QxCursor cursor = QxCursor.valueOf( "crosshair" );
    assertEquals( "crosshair", cursor.value );
    assertNull( cursor.loader );
    assertFalse( cursor.isCustomCursor() );
  }

  public void testCustomCursor() {
    QxCursor cursor = QxCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    assertEquals( Fixture.IMAGE_50x100, cursor.value );
    assertEquals( RESOURCE_LOADER, cursor.loader );
    assertTrue( cursor.isCustomCursor() );
  }

  public void testDefaultString() {
    QxCursor cursor = QxCursor.valueOf( "crosshair" );
    assertEquals( "crosshair", cursor.toDefaultString() );
    cursor = QxCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    assertEquals( "", cursor.toDefaultString() );
  }

  public void testHashCode() {
    QxCursor cursor1 = QxCursor.valueOf( "crosshair" );
    QxCursor cursor2 = QxCursor.valueOf( "crosshair" );
    assertEquals( cursor1, cursor2 );
    assertEquals( cursor1.hashCode(), cursor2.hashCode() );

    cursor1 = QxCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    cursor2 = QxCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    assertEquals( cursor1, cursor2 );
    assertEquals( cursor1.hashCode(), cursor2.hashCode() );
  }

  public void testGetResourcePath() {
    QxCursor image = QxCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );

    assertTrue( image.getResourcePath().startsWith( "themes/cursors/" ) );
  }

  public void testResourcePathsDiffer() {
    QxCursor image1 = QxCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    QxCursor image2 = QxCursor.valueOf( Fixture.IMAGE_100x50, RESOURCE_LOADER );

    assertFalse( image1.getResourcePath().equals( image2.getResourcePath() ) );
  }

  public void testGetResourcePathWithPredefined() {
    assertNull( QxCursor.valueOf( "crosshair" ).getResourcePath() );
  }

  public void testGetResourceAsStream() throws IOException {
    QxCursor image = QxCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    InputStream inputStream = image.getResourceAsStream();

    assertTrue( inputStream.available() > 0 );
    inputStream.close();
  }

  public void testGetResourceAsStreamWithPredefined() throws IOException {
    assertNull( QxCursor.valueOf( "crosshair" ).getResourceAsStream() );
  }

}
