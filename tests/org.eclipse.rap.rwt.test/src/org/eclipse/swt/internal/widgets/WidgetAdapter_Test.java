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

import static org.mockito.Mockito.mock;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rap.rwt.internal.lifecycle.DisposedWidgets;
import org.eclipse.rap.rwt.internal.lifecycle.IRenderRunnable;
import org.eclipse.rap.rwt.internal.protocol.IClientObjectAdapter;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;


public class WidgetAdapter_Test extends TestCase {

  private Display display;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testGetAdapterForDisplay() {
    Object adapter1 = display.getAdapter( IWidgetAdapter.class );
    assertTrue( adapter1 instanceof IWidgetAdapter );
    Object adapter2 = display.getAdapter( IWidgetAdapter.class );
    assertSame( adapter1, adapter2 );
    display.dispose();
    display = new Display();
    Object adapter3 = display.getAdapter( IWidgetAdapter.class );
    assertTrue( adapter3 != adapter2 );
  }

  public void testGetAdapterForShell() {
    Composite shell = new Shell( display, SWT.NONE );
    Object adapter1 = shell.getAdapter( IWidgetAdapter.class );
    assertTrue( adapter1 instanceof IWidgetAdapter );
    shell = new Shell( display, SWT.NONE );
    Object adapter2 = shell.getAdapter( IWidgetAdapter.class );
    assertTrue( adapter1 != adapter2 );
  }

  public void testGetAdapterForButton() {
    Composite shell = new Shell( display, SWT.NONE );
    Button button1 = new Button( shell, SWT.PUSH );
    Object adapter1 = button1.getAdapter( IWidgetAdapter.class );
    assertTrue( adapter1 instanceof IWidgetAdapter );
    Button button2 = new Button( shell, SWT.PUSH );
    Object adapter2 = button2.getAdapter( IWidgetAdapter.class );
    assertTrue( adapter1 != adapter2 );
  }

  public void testId() {
    IWidgetAdapter adapter1 = display.getAdapter( IWidgetAdapter.class );
    display.dispose();
    display = new Display();
    IWidgetAdapter adapter2 = display.getAdapter( IWidgetAdapter.class );
    assertEquals( adapter1.getId(), adapter2.getId() );
  }

  public void testCustomIdWithoutUITestEnabled() {
    Shell shell = new Shell( display, SWT.NONE );

    shell.setData( WidgetUtil.CUSTOM_WIDGET_ID, "myShell" );

    assertFalse( WidgetUtil.getId( shell ).equals( "myShell" ) );
  }

  public void testCustomIdAfterWidgetIsInitialised() {
    Shell shell = new Shell( display, SWT.NONE );
    Fixture.markInitialized( shell );

    try {
      shell.setData( WidgetUtil.CUSTOM_WIDGET_ID, "myShell" );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  public void testInitializedForShell() throws IOException {
    Fixture.fakeNewRequest( display );
    Fixture.fakeResponseWriter();
    Composite shell = new Shell( display, SWT.NONE );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( shell );
    assertEquals( false, adapter.isInitialized() );
    DisplayUtil.getLCA( display ).render( display );
    assertEquals( true, adapter.isInitialized() );
  }

  public void testInitializedForDisplay() throws IOException {
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    assertEquals( false, adapter.isInitialized() );
    Fixture.fakeNewRequest( display );
    DisplayUtil.getLCA( display ).render( display );
    assertEquals( true, adapter.isInitialized() );
    Fixture.fakeNewRequest( display );
    DisplayUtil.getLCA( display ).render( display );
    assertEquals( true, adapter.isInitialized() );
  }

  public void testRenderRunnable() {
    WidgetAdapter adapter = new WidgetAdapter();
    IRenderRunnable runnable = mock( IRenderRunnable.class );

    adapter.setRenderRunnable( runnable );

    assertSame( runnable, adapter.getRenderRunnable() );
  }

  public void testSetRenderRunnableTwice() {
    WidgetAdapter adapter = new WidgetAdapter();
    adapter.setRenderRunnable( mock( IRenderRunnable.class ) );
    IRenderRunnable otherRenderRunnable = mock( IRenderRunnable.class );

    try {
      adapter.setRenderRunnable( otherRenderRunnable );
      fail( "Must not allow to set renderRunnable twice" );
    } catch( IllegalStateException expected ) {
    }
  }

  public void testMarkDisposed() {
    Fixture.fakeNewRequest( display );
    Fixture.fakeResponseWriter();

    // dispose un-initialized widget: must not occur in list of disposed widgets
    Widget widget = new Shell( display );
    widget.dispose();
    assertTrue( widget.isDisposed() );
    assertEquals( 0, DisposedWidgets.getAll().length );

    // dispose initialized widget: must be present in list of disposed widgets
    widget = new Shell( display );
    WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( widget );
    adapter.setInitialized( true );
    widget.dispose();
    assertTrue( widget.isDisposed() );
    assertEquals( 1, DisposedWidgets.getAll().length );
  }

  public void testSerializableFields() throws Exception {
    WidgetAdapter adapter = new WidgetAdapter();
    adapter.setInitialized( true );

    WidgetAdapter deserializedAdapter = Fixture.serializeAndDeserialize( adapter );

    assertEquals( adapter.getId(), deserializedAdapter.getId() );
    assertEquals( adapter.isInitialized(), deserializedAdapter.isInitialized() );
  }

  public void testNonSerializableFields() throws Exception {
    String property = "foo";
    WidgetAdapter adapter = new WidgetAdapter();
    adapter.setCachedVariant( "cachedVariant" );
    adapter.setRenderRunnable( mock( IRenderRunnable.class ) );
    adapter.preserve( property, "bar" );

    WidgetAdapter deserializedAdapter = Fixture.serializeAndDeserialize( adapter );

    assertNull( deserializedAdapter.getCachedVariant() );
    assertNull( deserializedAdapter.getRenderRunnable() );
    assertNull( deserializedAdapter.getPreserved( property ) );
  }

  public void testGetGCForClient() {
    WidgetAdapter adapter = new WidgetAdapter();

    Adaptable gc = adapter.getGCForClient();

    assertNotNull( gc );
  }

  public void testGetGCForClientAdapter() {
    WidgetAdapter adapter = new WidgetAdapter();
    Adaptable gc = adapter.getGCForClient();

    assertNotNull( gc.getAdapter( IClientObjectAdapter.class ) );
  }

  public void testGetGCForClientAdapterWithInvalidClass() {
    WidgetAdapter adapter = new WidgetAdapter();
    Adaptable gc = adapter.getGCForClient();

    assertNull( gc.getAdapter( IWidgetAdapter.class ) );
  }

  public void testGetGCForClientAdapterId() {
    WidgetAdapter adapter = new WidgetAdapter();
    Adaptable gc = adapter.getGCForClient();

    String id = gc.getAdapter( IClientObjectAdapter.class ).getId();

    assertTrue( id.startsWith( "gc" ) );
  }

  public void testGetGCForClientAdapterHasSameId() {
    WidgetAdapter adapter = new WidgetAdapter();
    Adaptable gc = adapter.getGCForClient();

    String id1 = gc.getAdapter( IClientObjectAdapter.class ).getId();
    String id2 = gc.getAdapter( IClientObjectAdapter.class ).getId();

    assertEquals( id1, id2 );
  }

  public void testGetParent() {
    Composite shell = new Shell( display, SWT.NONE );

    WidgetAdapter adapter = new WidgetAdapter();
    adapter.setParent( shell );

    assertSame( shell, adapter.getParent() );
  }

  public void testGetParentFromButton() {
    Composite shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );

    IWidgetAdapter adapter = button.getAdapter( IWidgetAdapter.class );

    assertSame( shell, adapter.getParent() );
  }

}
