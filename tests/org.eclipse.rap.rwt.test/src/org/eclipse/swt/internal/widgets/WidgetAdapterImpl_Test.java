/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.rap.rwt.internal.lifecycle.DisposedWidgets;
import org.eclipse.rap.rwt.internal.lifecycle.IRenderRunnable;
import org.eclipse.rap.rwt.internal.lifecycle.UITestUtilAdapter;
import org.eclipse.rap.rwt.internal.protocol.IClientObjectAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
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
  public void testGetId_ignoresCustomIdWhenUITestDisabled() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "default-id" );

    adapter.setCustomId( "custom-id" );

    assertEquals( "default-id", adapter.getId() );
  }

  @Test
  public void testGetId_returnsCustomIdWhenUITestEnabled() {
    UITestUtilAdapter.setUITestEnabled( true );
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "default-id" );

    adapter.setCustomId( "custom-id" );

    assertEquals( "custom-id", adapter.getId() );
  }

  @Test
  public void testSetCustomId_failsAfterWidgetIsInitialised() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "default-id" );
    adapter.setInitialized( true );

    try {
      adapter.setCustomId( "custom-id" );
      fail();
    } catch( IllegalStateException expected ) {
      assertEquals( "Widget is already initialized", expected.getMessage() );
    }
  }

  @Test
  public void testSetCustomId_failsWithIllegalId() {
    UITestUtilAdapter.setUITestEnabled( true );
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "default-id" );

    try {
      adapter.setCustomId( "#!pf" );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertEquals( "The widget id contains illegal characters: #!pf", expected.getMessage() );
    }
  }

  @Test
  public void testSetRenderRunnable() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "id" );
    IRenderRunnable runnable = mock( IRenderRunnable.class );

    adapter.setRenderRunnable( runnable );

    assertSame( runnable, adapter.getRenderRunnable() );
  }

  @Test
  public void testSetRenderRunnable_cannotBeCalledTwice() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "id" );
    adapter.setRenderRunnable( mock( IRenderRunnable.class ) );
    IRenderRunnable otherRenderRunnable = mock( IRenderRunnable.class );

    try {
      adapter.setRenderRunnable( otherRenderRunnable );
    } catch( IllegalStateException expected ) {
    }
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
  public void testGetGCForClient() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "id" );

    Adaptable gc = adapter.getGCForClient();

    assertNotNull( gc );
  }

  @Test
  public void testGetGCForClient_hasClientObjectAdapter() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "id" );

    Adaptable gc = adapter.getGCForClient();

    assertNotNull( gc.getAdapter( IClientObjectAdapter.class ) );
  }

  @Test
  public void testGetGCForClient_hasNoAdapterForInvalidClass() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "id" );

    Adaptable gc = adapter.getGCForClient();

    assertNull( gc.getAdapter( WidgetAdapter.class ) );
  }

  @Test
  public void testGetGCForClient_adapterHasIdWithGcPrefix() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "id" );
    Adaptable gc = adapter.getGCForClient();

    String id = gc.getAdapter( IClientObjectAdapter.class ).getId();

    assertTrue( id.startsWith( "gc" ) );
  }

  @Test
  public void testGetGCForClient_hasSameClientObjectAdapter() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl( "id" );
    Adaptable gc = adapter.getGCForClient();

    IClientObjectAdapter adapter1 = gc.getAdapter( IClientObjectAdapter.class );
    IClientObjectAdapter adapter2 = gc.getAdapter( IClientObjectAdapter.class );

    assertSame( adapter1, adapter2 );
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
    adapter.setRenderRunnable( mock( IRenderRunnable.class ) );
    adapter.preserve( property, "bar" );

    WidgetAdapterImpl deserializedAdapter = Fixture.serializeAndDeserialize( adapter );

    assertNull( deserializedAdapter.getCachedVariant() );
    assertNull( deserializedAdapter.getRenderRunnable() );
    assertNull( deserializedAdapter.getPreserved( property ) );
  }

}
