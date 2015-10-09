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

import static org.eclipse.rap.rwt.testfixture.internal.Fixture.getProtocolMessage;
import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class ControlRemoteAdapter_Test {

  private ControlRemoteAdapter adapter;

  @Rule
  public TestContext context = new TestContext();

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
  public void testPreserveTabIndex() {
    adapter.preserveTabIndex( 3 );

    assertTrue( adapter.hasPreservedTabIndex() );
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
  public void testPreserveOrientation() {
    adapter.preserveOrientation( SWT.RIGHT_TO_LEFT );

    assertTrue( adapter.hasPreservedOrientation() );
    assertEquals( SWT.RIGHT_TO_LEFT, adapter.getPreservedOrientation() );
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

    assertTrue( adapter.hasPreservedBackgroundImage() );
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

    assertTrue( adapter.hasPreservedActiveKeys() );
    assertSame( keys, adapter.getPreservedActiveKeys() );
  }

  @Test
  public void testPreserveCancelKeys() {
    String[] keys = new String[] { "foo" };

    adapter.preserveCancelKeys( keys );

    assertTrue( adapter.hasPreservedCancelKeys() );
    assertSame( keys, adapter.getPreservedCancelKeys() );
  }

  // TODO: renderChildren is tested by ControlLCAUtil

  @Test
  public void testRenderBounds_initial() {
    adapter.renderBounds( mockControlAdapterWithBounds( new Rectangle( 0, 0, 0, 0 ) ) );

    JsonArray expected = JsonArray.readFrom( "[ 0, 0, 0, 0 ]" );
    assertEquals( expected, getProtocolMessage().findSetProperty( adapter.getId(), "bounds" ) );
  }

  @Test
  public void testRenderBounds_initial_withNonZeroBounds() {
    adapter.renderBounds( mockControlAdapterWithBounds( new Rectangle( 1, 2, 3, 4 ) ) );

    JsonArray expected = JsonArray.readFrom( "[ 1, 2, 3, 4 ]" );
    assertEquals( expected, getProtocolMessage().findSetProperty( adapter.getId(), "bounds" ) );
  }

  @Test
  public void testRenderBounds_unpreserved() {
    adapter.setInitialized( true );

    adapter.renderBounds( mockControlAdapterWithBounds( new Rectangle( 0, 1, 2, 3 ) ) );

    assertNull( getProtocolMessage().findSetOperation( adapter.getId(), "bounds" ) );
  }

  @Test
  public void testRenderBounds_preserved_unchanged() {
    adapter.setInitialized( true );
    adapter.preserveBounds( new Rectangle( 0, 0, 0, 0 ) );

    adapter.renderBounds( mockControlAdapterWithBounds( new Rectangle( 0, 0, 0, 0 ) ) );

    assertNull( getProtocolMessage().findSetOperation( adapter.getId(), "bounds" ) );
  }

  @Test
  public void testRenderBounds_preserved_changed() {
    adapter.setInitialized( true );
    adapter.preserveBounds( new Rectangle( 0, 0, 0, 0 ) );

    adapter.renderBounds( mockControlAdapterWithBounds( new Rectangle( 1, 2, 3, 4 ) ) );

    JsonArray expected = JsonArray.readFrom( "[ 1, 2, 3, 4 ]" );
    assertEquals( expected, getProtocolMessage().findSetProperty( adapter.getId(), "bounds" ) );
  }

  @Test
  public void testRenderBounds_preservedTwice_unchanged() {
    adapter.setInitialized( true );
    adapter.preserveBounds( new Rectangle( 0, 0, 0, 0 ) );
    adapter.preserveBounds( new Rectangle( 1, 2, 3, 4 ) );

    adapter.renderBounds( mockControlAdapterWithBounds( new Rectangle( 0, 0, 0, 0 ) ) );

    assertNull( getProtocolMessage().findSetOperation( adapter.getId(), "bounds" ) );
  }

  @Test
  public void testDefaults() {
    checkDefaults();
  }

  @Test
  public void testClearPreserved() {
    preserveEverything();

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
    preserveEverything();

    adapter = serializeAndDeserialize( adapter );

    checkDefaults();
  }

  private void preserveEverything() {
    adapter.markPreserved( 1 );
    adapter.preserveParent( mock( Composite.class ) );
    adapter.preserveChildren( new Control[] { mock( Control.class ) } );
    adapter.preserveBounds( new Rectangle( 1, 2, 3, 4 ) );
    adapter.preserveTabIndex( 3 );
    adapter.preserveToolTipText( "foo" );
    adapter.preserveMenu( mock( Menu.class ) );
    adapter.preserveVisible( true );
    adapter.preserveEnabled( true );
    adapter.preserveForeground( mock( Color.class ) );
    adapter.preserveBackground( mock( Color.class ) );
    adapter.preserveBackgroundTransparency( true );
    adapter.preserveBackgroundImage( mock( Image.class ) );
    adapter.preserveFont( mock( Font.class ) );
    adapter.preserveCursor( mock( Cursor.class ) );
    adapter.preserveActiveKeys( new String[] { "foo" } );
    adapter.preserveCancelKeys( new String[] { "foo" } );
  }

  private void checkDefaults() {
    assertFalse( adapter.hasPreserved( 1 ) );
    assertFalse( adapter.hasPreservedParent() );
    assertNull( adapter.getPreservedParent() );
    assertFalse( adapter.hasPreservedTabIndex() );
    assertEquals( 0, adapter.getPreservedTabIndex() );
    assertFalse( adapter.hasPreservedToolTipText() );
    assertNull( adapter.getPreservedToolTipText() );
    assertFalse( adapter.hasPreservedMenu() );
    assertNull( adapter.getPreservedMenu() );
    assertFalse( adapter.hasPreservedVisible() );
    assertFalse( adapter.getPreservedVisible() );
    assertFalse( adapter.hasPreservedEnabled() );
    assertFalse( adapter.getPreservedEnabled() );
    assertFalse( adapter.hasPreservedOrientation() );
    assertEquals( SWT.LEFT_TO_RIGHT, adapter.getPreservedOrientation() );
    assertFalse( adapter.hasPreservedForeground() );
    assertNull( adapter.getPreservedForeground() );
    assertFalse( adapter.hasPreservedBackground() );
    assertNull( adapter.getPreservedBackground() );
    assertFalse( adapter.getPreservedBackgroundTransparency() );
    assertFalse( adapter.hasPreservedBackgroundImage() );
    assertNull( adapter.getPreservedBackgroundImage() );
    assertFalse( adapter.hasPreservedFont() );
    assertNull( adapter.getPreservedFont() );
    assertFalse( adapter.hasPreservedCursor() );
    assertNull( adapter.getPreservedCursor() );
    assertFalse( adapter.hasPreservedActiveKeys() );
    assertNull( adapter.getPreservedActiveKeys() );
    assertFalse( adapter.hasPreservedCancelKeys() );
    assertNull( adapter.getPreservedCancelKeys() );
  }

  private static IControlAdapter mockControlAdapterWithBounds( Rectangle value ) {
    IControlAdapter controlAdapter = mock( IControlAdapter.class );
    when(controlAdapter.getBounds()).thenReturn( value );
    return controlAdapter;
  }

}
