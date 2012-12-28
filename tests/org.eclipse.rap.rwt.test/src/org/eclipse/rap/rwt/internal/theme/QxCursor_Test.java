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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class QxCursor_Test {

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

  @Test
  public void testNotExisting() {
    try {
      QxCursor.valueOf( "not-existing.cur", RESOURCE_LOADER );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testPredefinedCursor() {
    QxCursor cursor = QxCursor.valueOf( "crosshair" );
    assertEquals( "crosshair", cursor.value );
    assertNull( cursor.loader );
    assertFalse( cursor.isCustomCursor() );
  }

  @Test
  public void testCustomCursor() {
    QxCursor cursor = QxCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    assertEquals( Fixture.IMAGE_50x100, cursor.value );
    assertEquals( RESOURCE_LOADER, cursor.loader );
    assertTrue( cursor.isCustomCursor() );
  }

  @Test
  public void testDefaultString() {
    QxCursor cursor = QxCursor.valueOf( "crosshair" );
    assertEquals( "crosshair", cursor.toDefaultString() );
    cursor = QxCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    assertEquals( "", cursor.toDefaultString() );
  }

  @Test
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

  @Test
  public void testGetResourcePath() {
    QxCursor image = QxCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );

    assertTrue( image.getResourcePath().startsWith( "themes/cursors/" ) );
  }

  @Test
  public void testResourcePathsDiffer() {
    QxCursor image1 = QxCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    QxCursor image2 = QxCursor.valueOf( Fixture.IMAGE_100x50, RESOURCE_LOADER );

    assertFalse( image1.getResourcePath().equals( image2.getResourcePath() ) );
  }

  @Test
  public void testGetResourcePathWithPredefined() {
    assertNull( QxCursor.valueOf( "crosshair" ).getResourcePath() );
  }

  @Test
  public void testGetResourceAsStream() throws IOException {
    QxCursor image = QxCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    InputStream inputStream = image.getResourceAsStream();

    assertTrue( inputStream.available() > 0 );
    inputStream.close();
  }

  @Test
  public void testGetResourceAsStreamWithPredefined() throws IOException {
    assertNull( QxCursor.valueOf( "crosshair" ).getResourceAsStream() );
  }

}
