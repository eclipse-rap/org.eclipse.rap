/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.internal.lifecycle.DisposedWidgets;
import org.eclipse.rap.rwt.internal.lifecycle.UITestUtilAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class WidgetAdapterImpl_Test {

  private Display display;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
    UITestUtilAdapter.setUITestEnabled( false );
  }

  @Test
  public void testIsInitialized_isFalseByDefault() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "id" );

    boolean initialized = adapter.isInitialized();

    assertFalse( initialized );
  }

  @Test
  public void testSetInitialized() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "id" );

    adapter.setInitialized( true );
    boolean initialized = adapter.isInitialized();

    assertTrue( initialized );
  }

  @Test
  public void testGetId() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "id" );

    String id = adapter.getId();

    assertEquals( "id", id );
  }

  @Test
  public void testGetRenderRunnables_initial() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "id" );

    assertEquals( 0, adapter.getRenderRunnables().length );
  }

  @Test
  public void testAddRenderRunnable_single() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "id" );
    Runnable runnable = mock( Runnable.class );

    adapter.addRenderRunnable( runnable );

    Runnable[] renderRunnables = adapter.getRenderRunnables();
    assertEquals( 1, renderRunnables.length );
    assertSame( runnable, renderRunnables[ 0 ] );
  }

  @Test
  public void testAddRenderRunnable_multiple() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "id" );
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
    Fixture.fakeNewRequest();
    Fixture.fakeResponseWriter();

    // dispose un-initialized widget: must not occur in list of disposed widgets
    Widget widget = new Shell( display );
    widget.dispose();
    assertTrue( widget.isDisposed() );
    assertEquals( 0, DisposedWidgets.getAll().length );

    // dispose initialized widget: must be present in list of disposed widgets
    widget = new Shell( display );
    WidgetAdapterImpl adapter = ( WidgetAdapterImpl )WidgetUtil.getAdapter( widget );
    adapter.setInitialized( true );
    widget.dispose();

    assertTrue( widget.isDisposed() );
    assertEquals( 1, DisposedWidgets.getAll().length );
  }

  @Test
  public void testSetParent() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "id" );
    Composite parent = mock( Composite.class );

    adapter.setParent( parent );

    assertSame( parent, adapter.getParent() );
  }

  @Test
  public void testSerializableFields() throws Exception {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "id" );
    adapter.setInitialized( true );

    WidgetAdapterImpl deserializedAdapter = Fixture.serializeAndDeserialize( adapter );

    assertEquals( adapter.getId(), deserializedAdapter.getId() );
    assertTrue( deserializedAdapter.isInitialized() );
  }

  @Test
  public void testNonSerializableFields() throws Exception {
    String property = "foo";
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "id" );
    adapter.setCachedVariant( "cachedVariant" );
    adapter.addRenderRunnable( mock( Runnable.class ) );
    adapter.preserve( property, "bar" );

    WidgetAdapterImpl deserializedAdapter = Fixture.serializeAndDeserialize( adapter );

    assertNull( deserializedAdapter.getCachedVariant() );
    assertEquals( 0, deserializedAdapter.getRenderRunnables().length );
    assertNull( deserializedAdapter.getPreserved( property ) );
  }

}
