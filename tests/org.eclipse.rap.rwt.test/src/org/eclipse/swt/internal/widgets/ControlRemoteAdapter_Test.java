/*******************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.junit.Before;
import org.junit.Test;


public class ControlRemoteAdapter_Test {

  private ControlRemoteAdapter adapter;

  @Before
  public void setUp() {
    adapter = new ControlRemoteAdapter( "id" );
  }

  @Test
  public void testPreserveParent() {
    Composite parent = mock( Composite.class );

    adapter.preserveParent( parent );

    assertTrue( adapter.hasPreservedParent() );
    assertSame( parent, adapter.getPreservedParent() );
  }

  @Test
  public void testPreserveChildren() {
    Control[] children = { mock( Control.class ) };

    adapter.preserveChildren( children );

    assertTrue( adapter.hasPreservedChildren() );
    assertSame( children, adapter.getPreservedChildren() );
  }

  @Test
  public void testPreserveBounds() {
    Rectangle bounds = new Rectangle( 1, 2, 3, 4 );

    adapter.preserveBounds( bounds );

    assertTrue( adapter.hasPreservedBounds() );
    assertSame( bounds, adapter.getPreservedBounds() );
  }

  @Test
  public void testPreserveTabIndex() {
    adapter.preserveTabIndex( 3 );

    assertEquals( 3, adapter.getPreservedTabIndex() );
  }

  @Test
  public void testPreserveToolTipText() {
    adapter.preserveToolTipText( "foo" );

    assertTrue( adapter.hasPreservedToolTipText() );
    assertEquals( "foo", adapter.getPreservedToolTipText() );
  }

  @Test
  public void testPreserveMenu() {
    Menu menu = mock( Menu.class );

    adapter.preserveMenu( menu );

    assertTrue( adapter.hasPreservedMenu() );
    assertSame( menu, adapter.getPreservedMenu() );
  }

  @Test
  public void testPreserveVisible() {
    adapter.preserveVisible( true );

    assertTrue( adapter.hasPreservedVisible() );
    assertTrue( adapter.getPreservedVisible() );
  }

  @Test
  public void testPreserveEnabled() {
    adapter.preserveEnabled( true );

    assertTrue( adapter.hasPreservedEnabled() );
    assertTrue( adapter.getPreservedEnabled() );
  }

  @Test
  public void testPreserveForeground() {
    Color color = mock( Color.class );

    adapter.preserveForeground( color );

    assertTrue( adapter.hasPreservedForeground() );
    assertSame( color, adapter.getPreservedForeground() );
  }

  @Test
  public void testPreserveBackground() {
    Color color = mock( Color.class );

    adapter.preserveBackground( color );

    assertTrue( adapter.hasPreservedBackground() );
    assertSame( color, adapter.getPreservedBackground() );
  }

  @Test
  public void testPreserveBackgroundTransparency() {
    adapter.preserveBackgroundTransparency( true );

    assertTrue( adapter.hasPreservedBackground() );
    assertTrue( adapter.getPreservedBackgroundTransparency() );
  }

  @Test
  public void testPreserveBackgroundImage() {
    Image image = mock( Image.class );

    adapter.preserveBackgroundImage( image );

    assertSame( image, adapter.getPreservedBackgroundImage() );
  }

  @Test
  public void testPreserveFont() {
    Font font = mock( Font.class );

    adapter.preserveFont( font );

    assertTrue( adapter.hasPreservedFont() );
    assertSame( font, adapter.getPreservedFont() );
  }

  @Test
  public void testPreserveCursor() {
    Cursor cursor = mock( Cursor.class );

    adapter.preserveCursor( cursor );

    assertTrue( adapter.hasPreservedCursor() );
    assertSame( cursor, adapter.getPreservedCursor() );
  }

  @Test
  public void testPreserveActiveKeys() {
    String[] keys = new String[] { "foo" };

    adapter.preserveActiveKeys( keys );

    assertSame( keys, adapter.getPreservedActiveKeys() );
  }

  @Test
  public void testPreserveActiveKeys_isCleared() {
    adapter.preserveActiveKeys( new String[] { "foo" } );

    adapter.clearPreserved();

    assertNull( adapter.getPreservedActiveKeys() );
  }

  @Test
  public void testPreserveActiveKeys_isTransient() throws Exception {
    adapter.preserveActiveKeys( new String[] { "foo" } );

    adapter = serializeAndDeserialize( adapter );

    assertNull( adapter.getPreservedActiveKeys() );
  }

  @Test
  public void testPreserveCancelKeys() {
    String[] keys = new String[] { "foo" };

    adapter.preserveCancelKeys( keys );

    assertSame( keys, adapter.getPreservedCancelKeys() );
  }

  @Test
  public void testPreserveCancelKeys_isCleared() {
    adapter.preserveCancelKeys( new String[] { "foo" } );

    adapter.clearPreserved();

    assertNull( adapter.getPreservedCancelKeys() );
  }

  @Test
  public void testPreserveCancelKeys_isTransient() throws Exception {
    adapter.preserveCancelKeys( new String[] { "foo" } );

    adapter = serializeAndDeserialize( adapter );

    assertNull( adapter.getPreservedCancelKeys() );
  }

  @Test
  public void testDefaults() {
    checkDefaults();
  }

  @Test
  public void testClearPreserved() {
    Color color = mock( Color.class );
    adapter.preserveParent( mock( Composite.class ) );
    adapter.preserveChildren( new Control[] { mock( Control.class ) } );
    adapter.preserveBounds( new Rectangle( 1, 2, 3, 4 ) );
    adapter.preserveTabIndex( 3 );
    adapter.preserveToolTipText( "foo" );
    adapter.preserveMenu( mock( Menu.class ) );
    adapter.preserveVisible( true );
    adapter.preserveEnabled( true );
    adapter.preserveForeground( color );
    adapter.preserveBackground( color );
    adapter.preserveBackgroundTransparency( true );
    adapter.preserveBackgroundImage( mock( Image.class ) );
    adapter.preserveFont( mock( Font.class ) );
    adapter.preserveCursor( mock( Cursor.class ) );

    adapter.clearPreserved();

    checkDefaults();
  }

  @Test
  public void testSerializeableFieldsAreRestored() throws Exception {
    ControlRemoteAdapter deserializedAdapter = serializeAndDeserialize( adapter );

    // fails if the transient fields in superclass aren't restored
    deserializedAdapter.preserve( "foo", new Object() );
  }

  @Test
  public void testTransientFields() throws Exception {
    Color color = mock( Color.class );
    adapter.preserveParent( mock( Composite.class ) );
    adapter.preserveChildren( new Control[] { mock( Control.class ) } );
    adapter.preserveBounds( new Rectangle( 1, 2, 3, 4 ) );
    adapter.preserveTabIndex( 3 );
    adapter.preserveToolTipText( "foo" );
    adapter.preserveMenu( mock( Menu.class ) );
    adapter.preserveVisible( true );
    adapter.preserveEnabled( true );
    adapter.preserveForeground( color );
    adapter.preserveBackground( color );
    adapter.preserveBackgroundTransparency( true );
    adapter.preserveBackgroundImage( mock( Image.class ) );
    adapter.preserveFont( mock( Font.class ) );
    adapter.preserveCursor( mock( Cursor.class ) );

    adapter = serializeAndDeserialize( adapter );

    checkDefaults();
  }

  @Test
  public void testPreservingDoesNotAffectOtherProperties() {
    adapter.preserveChildren( new Control[ 0 ] );
    assertFalse( adapter.hasPreservedBounds() );
    assertFalse( adapter.hasPreservedParent() );
  }

  private void checkDefaults() {
    assertFalse( adapter.hasPreservedParent() );
    assertNull( adapter.getPreservedParent() );
    assertFalse( adapter.hasPreservedChildren() );
    assertNull( adapter.getPreservedChildren() );
    assertFalse( adapter.hasPreservedBounds() );
    assertNull( adapter.getPreservedBounds() );
    assertEquals( 0, adapter.getPreservedTabIndex() );
    assertFalse( adapter.hasPreservedToolTipText() );
    assertNull( adapter.getPreservedToolTipText() );
    assertFalse( adapter.hasPreservedMenu() );
    assertNull( adapter.getPreservedMenu() );
    assertFalse( adapter.hasPreservedVisible() );
    assertFalse( adapter.getPreservedVisible() );
    assertFalse( adapter.hasPreservedEnabled() );
    assertFalse( adapter.getPreservedEnabled() );
    assertFalse( adapter.hasPreservedForeground() );
    assertNull( adapter.getPreservedForeground() );
    assertFalse( adapter.hasPreservedBackground() );
    assertNull( adapter.getPreservedBackground() );
    assertFalse( adapter.getPreservedBackgroundTransparency() );
    assertNull( adapter.getPreservedBackgroundImage() );
    assertFalse( adapter.hasPreservedFont() );
    assertNull( adapter.getPreservedFont() );
    assertFalse( adapter.hasPreservedCursor() );
    assertNull( adapter.getPreservedCursor() );
  }

}
