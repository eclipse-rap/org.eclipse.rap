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

import java.io.IOException;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rap.rwt.internal.lifecycle.DisposedWidgets;
import org.eclipse.rap.rwt.internal.lifecycle.IRenderRunnable;
import org.eclipse.rap.rwt.internal.protocol.IClientObjectAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class WidgetAdapter_Test {

  private Display display;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetAdapterForDisplay() {
    Object adapter1 = display.getAdapter( WidgetAdapter.class );
    assertTrue( adapter1 instanceof WidgetAdapter );
    Object adapter2 = display.getAdapter( WidgetAdapter.class );
    assertSame( adapter1, adapter2 );
    display.dispose();
    display = new Display();
    Object adapter3 = display.getAdapter( WidgetAdapter.class );
    assertTrue( adapter3 != adapter2 );
  }

  @Test
  public void testGetAdapterForShell() {
    Composite shell = new Shell( display, SWT.NONE );
    Object adapter1 = shell.getAdapter( WidgetAdapter.class );
    assertTrue( adapter1 instanceof WidgetAdapter );
    shell = new Shell( display, SWT.NONE );
    Object adapter2 = shell.getAdapter( WidgetAdapter.class );
    assertTrue( adapter1 != adapter2 );
  }

  @Test
  public void testGetAdapterForButton() {
    Composite shell = new Shell( display, SWT.NONE );
    Button button1 = new Button( shell, SWT.PUSH );
    Object adapter1 = button1.getAdapter( WidgetAdapter.class );
    assertTrue( adapter1 instanceof WidgetAdapter );
    Button button2 = new Button( shell, SWT.PUSH );
    Object adapter2 = button2.getAdapter( WidgetAdapter.class );
    assertTrue( adapter1 != adapter2 );
  }

  @Test
  public void testId() {
    WidgetAdapter adapter1 = display.getAdapter( WidgetAdapter.class );
    display.dispose();
    display = new Display();
    WidgetAdapter adapter2 = display.getAdapter( WidgetAdapter.class );
    assertEquals( adapter1.getId(), adapter2.getId() );
  }

  @Test
  public void testId_Widget() {
    Shell shell = new Shell( display, SWT.NONE );

    assertTrue(  WidgetUtil.getId( shell ).startsWith( "w" ) );
  }

  @Test
  public void testCustomIdWithoutUITestEnabled() {
    Shell shell = new Shell( display, SWT.NONE );

    shell.setData( WidgetUtil.CUSTOM_WIDGET_ID, "myShell" );

    assertFalse( WidgetUtil.getId( shell ).equals( "myShell" ) );
  }

  @Test
  public void testCustomIdAfterWidgetIsInitialised() {
    Shell shell = new Shell( display, SWT.NONE );
    Fixture.markInitialized( shell );

    try {
      shell.setData( WidgetUtil.CUSTOM_WIDGET_ID, "myShell" );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  @Test
  public void testInitializedForShell() throws IOException {
    Fixture.fakeNewRequest();
    Fixture.fakeResponseWriter();
    Composite shell = new Shell( display, SWT.NONE );
    WidgetAdapter adapter = WidgetUtil.getAdapter( shell );
    assertFalse( adapter.isInitialized() );
    DisplayUtil.getLCA( display ).render( display );
    assertTrue( adapter.isInitialized() );
  }

  @Test
  public void testInitializedForDisplay() throws IOException {
    WidgetAdapter adapter = DisplayUtil.getAdapter( display );
    assertFalse( adapter.isInitialized() );
    Fixture.fakeNewRequest();
    DisplayUtil.getLCA( display ).render( display );
    assertTrue( adapter.isInitialized() );
    Fixture.fakeNewRequest();
    DisplayUtil.getLCA( display ).render( display );
    assertTrue( adapter.isInitialized() );
  }

  @Test
  public void testRenderRunnable() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl();
    IRenderRunnable runnable = mock( IRenderRunnable.class );

    adapter.setRenderRunnable( runnable );

    assertSame( runnable, adapter.getRenderRunnable() );
  }

  @Test
  public void testSetRenderRunnableTwice() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl();
    adapter.setRenderRunnable( mock( IRenderRunnable.class ) );
    IRenderRunnable otherRenderRunnable = mock( IRenderRunnable.class );

    try {
      adapter.setRenderRunnable( otherRenderRunnable );
      fail( "Must not allow to set renderRunnable twice" );
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
  public void testSerializableFields() throws Exception {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl();
    adapter.setInitialized( true );

    WidgetAdapterImpl deserializedAdapter = Fixture.serializeAndDeserialize( adapter );

    assertEquals( adapter.getId(), deserializedAdapter.getId() );
    assertTrue( adapter.isInitialized() == deserializedAdapter.isInitialized() );
  }

  @Test
  public void testNonSerializableFields() throws Exception {
    String property = "foo";
    WidgetAdapterImpl adapter = new WidgetAdapterImpl();
    adapter.setCachedVariant( "cachedVariant" );
    adapter.setRenderRunnable( mock( IRenderRunnable.class ) );
    adapter.preserve( property, "bar" );

    WidgetAdapterImpl deserializedAdapter = Fixture.serializeAndDeserialize( adapter );

    assertNull( deserializedAdapter.getCachedVariant() );
    assertNull( deserializedAdapter.getRenderRunnable() );
    assertNull( deserializedAdapter.getPreserved( property ) );
  }

  @Test
  public void testGetGCForClient() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl();

    Adaptable gc = adapter.getGCForClient();

    assertNotNull( gc );
  }

  @Test
  public void testGetGCForClientAdapter() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl();
    Adaptable gc = adapter.getGCForClient();

    assertNotNull( gc.getAdapter( IClientObjectAdapter.class ) );
  }

  @Test
  public void testGetGCForClientAdapterWithInvalidClass() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl();
    Adaptable gc = adapter.getGCForClient();

    assertNull( gc.getAdapter( WidgetAdapter.class ) );
  }

  @Test
  public void testGetGCForClientAdapterId() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl();
    Adaptable gc = adapter.getGCForClient();

    String id = gc.getAdapter( IClientObjectAdapter.class ).getId();

    assertTrue( id.startsWith( "gc" ) );
  }

  @Test
  public void testGetGCForClientAdapterHasSameId() {
    WidgetAdapterImpl adapter = new WidgetAdapterImpl();
    Adaptable gc = adapter.getGCForClient();

    String id1 = gc.getAdapter( IClientObjectAdapter.class ).getId();
    String id2 = gc.getAdapter( IClientObjectAdapter.class ).getId();

    assertEquals( id1, id2 );
  }

  @Test
  public void testGetParent() {
    Composite shell = new Shell( display, SWT.NONE );

    WidgetAdapterImpl adapter = new WidgetAdapterImpl();
    adapter.setParent( shell );

    assertSame( shell, adapter.getParent() );
  }

  @Test
  public void testGetParentFromButton() {
    Composite shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );

    WidgetAdapter adapter = button.getAdapter( WidgetAdapter.class );

    assertSame( shell, adapter.getParent() );
  }

}
