/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.internal.lifecycle.DisposedWidgets;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class WidgetRemoteAdapter_Test {

  private Display display;

  @Rule
  public TestContext context = new TestContext();

  private WidgetRemoteAdapter adapter;

  @Before
  public void setUp() {
    display = new Display();
    adapter = new WidgetRemoteAdapter( "id" );
  }

  @Test
  public void testGetId() {
    String id = adapter.getId();

    assertEquals( "id", id );
  }

  @Test
  public void testSetParent() {
    Composite parent = mock( Composite.class );

    adapter.setParent( parent );

    assertSame( parent, adapter.getParent() );
  }

  @Test
  public void testIsInitialized_isFalseByDefault() {
    boolean initialized = adapter.isInitialized();

    assertFalse( initialized );
  }

  @Test
  public void testSetInitialized() {
    adapter.setInitialized( true );
    boolean initialized = adapter.isInitialized();

    assertTrue( initialized );
  }

  @Test
  public void testPreserveProperty() {
    Object value = new Object();

    adapter.preserve( "prop", value );

    assertSame( value, adapter.getPreserved( "prop" ) );
  }

  @Test
  public void testPreserveProperty_isCleared() {
    adapter.preserve( "prop", new Object() );

    adapter.clearPreserved();

    assertNull( adapter.getPreserved( "prop" ) );
  }

  @Test
  public void testPreserveProperty_isTransient() throws Exception {
    adapter.preserve( "prop", new Object() );

    adapter = serializeAndDeserialize( adapter );

    assertNull( adapter.getPreserved( "prop" ) );
  }

  @Test
  public void testGetPreservedListener_initiallyFalse() {
    assertFalse( adapter.getPreservedListener( 1 ) );
    assertFalse( adapter.getPreservedListener( 23 ) );
    assertFalse( adapter.getPreservedListener( 64 ) );
  }

  @Test
  public void testGetPreservedListener_setAfterPreserving() {
    adapter.preserveListener( 1, true );
    adapter.preserveListener( 23, false );
    adapter.preserveListener( 42, true );
    adapter.preserveListener( 64, false );

    assertTrue( adapter.getPreservedListener( 1 ) );
    assertFalse( adapter.getPreservedListener( 23 ) );
    assertTrue( adapter.getPreservedListener( 42 ) );
    assertFalse( adapter.getPreservedListener( 64 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetPreservedListener_failsForIllegalValues() {
    adapter.preserveListener( 65, false );
  }

  @Test
  public void testGetPreservedListener_resetAfterClear() {
    adapter.preserveListener( 23, true );
    adapter.preserveListener( 42, false );

    adapter.clearPreserved();

    assertFalse( adapter.getPreservedListener( 23 ) );
    assertFalse( adapter.getPreservedListener( 42 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testPreserveListener_failsWithIllegalValues() {
    adapter.preserveListener( 65, true );
  }

  @Test
  public void testPreserveListener_isTransient() throws Exception {
    adapter.preserveListener( 23, true );

    adapter = serializeAndDeserialize( adapter );

    assertFalse( adapter.getPreservedListener( 23 ) );
  }

  @Test
  public void testGetRenderRunnables_initial() {
    assertEquals( 0, adapter.getRenderRunnables().length );
  }

  @Test
  public void testAddRenderRunnable_single() {
    Runnable runnable = mock( Runnable.class );

    adapter.addRenderRunnable( runnable );

    Runnable[] renderRunnables = adapter.getRenderRunnables();
    assertEquals( 1, renderRunnables.length );
    assertSame( runnable, renderRunnables[ 0 ] );
  }

  @Test
  public void testAddRenderRunnable_multiple() {
    Runnable runnable1 = mock( Runnable.class );
    Runnable runnable2 = mock( Runnable.class );

    adapter.addRenderRunnable( runnable1 );
    adapter.addRenderRunnable( runnable2 );

    Runnable[] renderRunnables = adapter.getRenderRunnables();
    assertEquals( 2, renderRunnables.length );
    assertSame( runnable1, renderRunnables[ 0 ] );
    assertSame( runnable2, renderRunnables[ 1 ] );
  }

  @Test
  public void testRenderRunnable_isTransient() throws Exception {
    adapter.addRenderRunnable( mock( Runnable.class ) );

    adapter = serializeAndDeserialize( adapter );

    assertEquals( 0, adapter.getRenderRunnables().length );
  }

  @Test
  public void testMarkDisposed() {
    // dispose un-initialized widget: must not occur in list of disposed widgets
    Widget widget = new Shell( display );
    widget.dispose();
    assertTrue( widget.isDisposed() );
    assertEquals( 0, DisposedWidgets.getAll().length );

    // dispose initialized widget: must be present in list of disposed widgets
    widget = new Shell( display );
    WidgetRemoteAdapter adapter = ( WidgetRemoteAdapter )WidgetUtil.getAdapter( widget );
    adapter.setInitialized( true );
    widget.dispose();

    assertTrue( widget.isDisposed() );
    assertEquals( 1, DisposedWidgets.getAll().length );
  }

  @Test
  public void testPreserveData() {
    Object[] data = { "foo" };

    adapter.preserveData( data );

    assertTrue( adapter.hasPreservedData() );
    assertSame( data, adapter.getPreservedData() );
  }

  @Test
  public void testPreserveData_isCleared() {
    adapter.preserveData( new Object[] { "foo" } );

    adapter.clearPreserved();

    assertNull( adapter.getPreservedData() );
  }

  @Test
  public void testPreserveData_isTransient() throws Exception {
    adapter.preserveData( new Object[] { "foo" } );

    adapter = serializeAndDeserialize( adapter );

    assertFalse( adapter.hasPreservedData() );
    assertNull( adapter.getPreservedData() );
  }

  @Test
  public void testPreserveVariant() {
    adapter.preserveVariant( "foo" );

    assertEquals( "foo", adapter.getPreservedVariant() );
  }

  @Test
  public void testPreserveVariant_isCleared() {
    adapter.preserveVariant( "foo" );

    adapter.clearPreserved();

    assertNull( adapter.getPreservedVariant() );
  }

  @Test
  public void testPreserveVariant_isTransient() throws Exception {
    adapter.preserveVariant( "foo" );

    adapter = serializeAndDeserialize( adapter );

    assertNull( adapter.getPreservedVariant() );
  }

  @Test
  public void testSerializableFields() throws Exception {
    adapter.setInitialized( true );

    WidgetRemoteAdapter deserializedAdapter = serializeAndDeserialize( adapter );

    assertEquals( adapter.getId(), deserializedAdapter.getId() );
    assertTrue( deserializedAdapter.isInitialized() );
  }

}
