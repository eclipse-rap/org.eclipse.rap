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
import static org.eclipse.swt.internal.events.EventLCAUtil.getEventMask;
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
  private WidgetRemoteAdapter adapter;

  @Rule
  public TestContext context = new TestContext();

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
  public void testMarkPreserved() {
    adapter.markPreserved( 3 );

    // bit logic does not affect other properties
    assertFalse( adapter.hasPreserved( 0 ) );
    assertFalse( adapter.hasPreserved( 1 ) );
    assertFalse( adapter.hasPreserved( 2 ) );
    assertTrue( adapter.hasPreserved( 3 ) );
  }

  @Test
  public void testMarkPreserved_isCleared() {
    adapter.markPreserved( 3 );

    adapter.clearPreserved();

    assertFalse( adapter.hasPreserved( 3 ) );
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
    assertEquals( 0, DisposedWidgets.getAll().size() );

    // dispose initialized widget: must be present in list of disposed widgets
    widget = new Shell( display );
    WidgetRemoteAdapter adapter = ( WidgetRemoteAdapter )WidgetUtil.getAdapter( widget );
    adapter.setInitialized( true );
    widget.dispose();

    assertTrue( widget.isDisposed() );
    assertEquals( 1, DisposedWidgets.getAll().size() );
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

    assertFalse( adapter.hasPreservedData() );
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
  public void testPreserveListeners() {
    adapter.preserveListeners( 23 );

    assertTrue( adapter.hasPreservedListeners() );
    assertEquals( 23, adapter.getPreservedListeners() );
  }

  @Test
  public void testPreserveListeners_isCleared() {
    adapter.preserveListeners( 23 );

    adapter.clearPreserved();

    assertFalse( adapter.hasPreservedListeners() );
    assertEquals( 0, adapter.getPreservedListeners() );
  }

  @Test
  public void testPreserveListeners_isTransient() throws Exception {
    adapter.preserveListeners( getEventMask( 23 ) );

    adapter = serializeAndDeserialize( adapter );

    assertFalse( adapter.hasPreservedListeners() );
    assertEquals( 0, adapter.getPreservedListeners() );
  }

  @Test
  public void testPreserveVariant() {
    adapter.preserveVariant( "foo" );

    assertTrue( adapter.hasPreservedVariant() );
    assertEquals( "foo", adapter.getPreservedVariant() );
  }

  @Test
  public void testPreserveVariant_isCleared() {
    adapter.preserveVariant( "foo" );

    adapter.clearPreserved();

    assertFalse( adapter.hasPreservedVariant() );
    assertNull( adapter.getPreservedVariant() );
  }

  @Test
  public void testPreserveVariant_isTransient() throws Exception {
    adapter.preserveVariant( "foo" );

    adapter = serializeAndDeserialize( adapter );

    assertFalse( adapter.hasPreservedVariant() );
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
