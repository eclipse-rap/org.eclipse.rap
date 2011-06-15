/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.engine.ResourceLoader;


public class QxCursor_Test extends TestCase {

  private static final ResourceLoader RESOURCE_LOADER
    = ThemeTestUtil.createResourceLoader( Fixture.class );

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
    QxCursor cursor = QxCursor.valueOf( Fixture.IMAGE_50x100,
                                        RESOURCE_LOADER );
    assertEquals( Fixture.IMAGE_50x100, cursor.value );
    assertEquals( RESOURCE_LOADER, cursor.loader );
    assertTrue( cursor.isCustomCursor() );
  }

  public void testDefaultString() {
    QxCursor cursor = QxCursor.valueOf( "crosshair" );
    assertEquals( "crosshair", cursor.toDefaultString() );
    cursor = QxCursor.valueOf( Fixture.IMAGE_50x100,
                               RESOURCE_LOADER );
    assertEquals( "", cursor.toDefaultString() );
  }

  public void testHashCode() {
    QxCursor cursor1 = QxCursor.valueOf( "crosshair" );
    QxCursor cursor2 = QxCursor.valueOf( "crosshair" );
    assertEquals( cursor1, cursor2 );
    assertEquals( cursor1.hashCode(), cursor2.hashCode() );

    cursor1 = QxCursor.valueOf( Fixture.IMAGE_50x100,
                                RESOURCE_LOADER );
    cursor2 = QxCursor.valueOf( Fixture.IMAGE_50x100,
                                RESOURCE_LOADER );
    assertEquals( cursor1, cursor2 );
    assertEquals( cursor1.hashCode(), cursor2.hashCode() );
  }
}
