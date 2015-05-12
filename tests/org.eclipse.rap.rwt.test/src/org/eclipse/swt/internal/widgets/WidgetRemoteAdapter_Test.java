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
import static org.junit.Assert.*;
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
  public void testPreserve() {
    Object value = new Object();

    adapter.preserve( "prop", value );

    assertSame( value, adapter.getPreserved( "prop" ) );
  }

  @Test
  public void testClearPreserve() {
    Object value = new Object();

    adapter.preserve( "prop", value );
    adapter.clearPreserved();

    assertNull( adapter.getPreserved( "prop" ) );
  }

  @Test
  public void testCachedVariant() {
    adapter.setCachedVariant( "foo" );

    assertEquals( "foo", adapter.getCachedVariant() );
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
  public void testSetParent() {
    Composite parent = mock( Composite.class );

    adapter.setParent( parent );

    assertSame( parent, adapter.getParent() );
  }

  @Test
  public void testSerializableFields() throws Exception {
    adapter.setInitialized( true );

    WidgetRemoteAdapter deserializedAdapter = serializeAndDeserialize( adapter );

    assertEquals( adapter.getId(), deserializedAdapter.getId() );
    assertTrue( deserializedAdapter.isInitialized() );
  }

  @Test
  public void testNonSerializableFields() throws Exception {
    String property = "foo";
    adapter.setCachedVariant( "cachedVariant" );
    adapter.addRenderRunnable( mock( Runnable.class ) );
    adapter.preserve( property, "bar" );

    WidgetRemoteAdapter deserializedAdapter = serializeAndDeserialize( adapter );

    assertNull( deserializedAdapter.getCachedVariant() );
    assertEquals( 0, deserializedAdapter.getRenderRunnables().length );
    assertNull( deserializedAdapter.getPreserved( property ) );
  }

}
