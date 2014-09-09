/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.internal.theme.ThemeTestUtil.RESOURCE_LOADER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.service.ApplicationContext;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class CssCursor_Test {

  @Rule
  public TestContext context = new TestContext();

  private ApplicationContext applicationContext;

  @Before
  public void setUp() {
    applicationContext = getApplicationContext();
  }

  @Test( expected = NullPointerException.class )
  public void testValueOf_nullArgument() {
    CssCursor.valueOf( null, RESOURCE_LOADER );
  }

  @Test( expected = NullPointerException.class )
  public void testValueOf_nullLoader() {
    CssCursor.valueOf( "", null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_emptyString() {
    CssCursor.valueOf( "", RESOURCE_LOADER );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_invalidCursorType() {
    CssCursor.valueOf( "alabala" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testValueOf_notExistingCursorFile() {
    CssCursor.valueOf( "not-existing.cur", RESOURCE_LOADER );
  }

  @Test
  public void testPredefinedCursor() {
    CssCursor cursor = CssCursor.valueOf( "crosshair" );
    assertEquals( "crosshair", cursor.value );
    assertNull( cursor.loader );
    assertFalse( cursor.isCustomCursor() );
  }

  @Test
  public void testCustomCursor() {
    CssCursor cursor = CssCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    assertEquals( Fixture.IMAGE_50x100, cursor.value );
    assertEquals( RESOURCE_LOADER, cursor.loader );
    assertTrue( cursor.isCustomCursor() );
  }

  @Test
  public void testDefaultString() {
    CssCursor cursor = CssCursor.valueOf( "crosshair" );
    assertEquals( "crosshair", cursor.toDefaultString() );
    cursor = CssCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    assertEquals( "", cursor.toDefaultString() );
  }

  @Test
  public void testHashCode() {
    CssCursor cursor1 = CssCursor.valueOf( "crosshair" );
    CssCursor cursor2 = CssCursor.valueOf( "crosshair" );
    assertEquals( cursor1, cursor2 );
    assertEquals( cursor1.hashCode(), cursor2.hashCode() );

    cursor1 = CssCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    cursor2 = CssCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    assertEquals( cursor1, cursor2 );
    assertEquals( cursor1.hashCode(), cursor2.hashCode() );
  }

  @Test
  public void testGetResourcePath() {
    CssCursor image = CssCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );

    assertTrue( image.getResourcePath( applicationContext ).startsWith( "themes/cursors/" ) );
  }

  @Test
  public void testResourcePathsDiffer() {
    CssCursor image1 = CssCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    CssCursor image2 = CssCursor.valueOf( Fixture.IMAGE_100x50, RESOURCE_LOADER );

    String path1 = image1.getResourcePath( applicationContext );
    String path2 = image2.getResourcePath( applicationContext );
    assertFalse( path1.equals( path2 ) );
  }

  @Test
  public void testGetResourcePathWithPredefined() {
    assertNull( CssCursor.valueOf( "crosshair" ).getResourcePath( applicationContext ) );
  }

  @Test
  public void testGetResourceAsStream() throws IOException {
    CssCursor image = CssCursor.valueOf( Fixture.IMAGE_50x100, RESOURCE_LOADER );
    InputStream inputStream = image.getResourceAsStream();

    assertTrue( inputStream.available() > 0 );
    inputStream.close();
  }

  @Test
  public void testGetResourceAsStreamWithPredefined() throws IOException {
    assertNull( CssCursor.valueOf( "crosshair" ).getResourceAsStream() );
  }

}
