/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;

import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;
import com.w4t.util.browser.Ie6;

public class ToolItemLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    ToolItem item = new ToolItem( tb, SWT.CHECK );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    Boolean hasListeners;
    hasListeners
    = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    assertEquals( null, adapter.getPreserved( Props.IMAGE ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );

    RWTFixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {};
    item.addSelectionListener( selectionListener );

    item.setText( "some text" );
    item.setImage( Image.find( RWTFixture.IMAGE1 ) );
    item.setSelection( true );

    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( item );
    hasListeners
    = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    assertEquals( Image.find( RWTFixture.IMAGE1 ),
                  adapter.getPreserved( Props.IMAGE ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( "selection" ) );
    display.dispose();
  }

  public void testCheckItemSelected() throws IOException {
    final boolean[] wasEventFired = { false };
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    ToolBar toolBar = new ToolBar( shell, SWT.FLAT );
    final ToolItem item = new ToolItem( toolBar, SWT.CHECK );
    item.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        wasEventFired[ 0 ] = true;
        assertEquals( null, event.item );
        assertSame( item, event.getSource() );
        assertEquals( true, event.doit );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, item.getSelection() );
      }
    } );
    shell.open();

    String displayId = DisplayUtil.getAdapter( display ).getId();
    String toolItemId = WidgetUtil.getId( item );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( toolItemId + ".selection", "true" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, toolItemId );
    new RWTLifeCycle().execute();
    assertEquals( true, wasEventFired[ 0 ] );
  }

  public void testRenderChanges() throws IOException {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    final ToolItem item = new ToolItem( tb, SWT.CHECK );
    shell.open();
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( item );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    ToolItemLCA itemLCA = new ToolItemLCA();
    item.setText( "benny" );
    itemLCA.renderChanges( item );
    assertTrue( Fixture.getAllMarkup().endsWith( "setLabel( \"benny\" );" ) );

    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    item.setSelection( true );
    itemLCA.renderChanges( item );
    assertTrue( Fixture.getAllMarkup().endsWith( "setChecked( true );" ) );


    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    itemLCA.renderChanges( item );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testReadData() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    ToolBar tb = new ToolBar( shell, SWT.FLAT );
    final ToolItem item = new ToolItem( tb, SWT.CHECK );
    String itemId = WidgetUtil.getId( item );
    // read changed selection
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, itemId );
    Fixture.fakeRequestParam( itemId + ".selection", "true" );
    WidgetUtil.getLCA( item ).readData( item );
    assertEquals( Boolean.TRUE, Boolean.valueOf( item.getSelection() ) );

    // XXX: is there a way to clear the request params?
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, itemId );
    Fixture.fakeRequestParam( itemId + ".selection", "false" );
    WidgetUtil.getLCA( item ).readData( item );
    assertEquals( Boolean.FALSE, Boolean.valueOf(item.getSelection() ) );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6( true, true ) );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
