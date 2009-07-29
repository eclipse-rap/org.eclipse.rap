/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.listkit;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.IListAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

// TODO [rh] tests for selectionEvent (proper event fields and so on)
public class ListLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    List list = new List( shell, SWT.SINGLE | SWT.BORDER );
    Boolean hasListeners;
    RWTFixture.markInitialized( display );
    // selection-Listeners
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( list );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    list.addSelectionListener( selectionListener );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    // items
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    String[] items = ( String[] )adapter.getPreserved( ListLCA.PROP_ITEMS );
    assertEquals( 0, items.length );
    RWTFixture.clearPreserved();
    list.setItems( new String[]{
      "item1", "item2", "item3"
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    items = ( String[] )adapter.getPreserved( ListLCA.PROP_ITEMS );
    assertEquals( 3, items.length );
    assertEquals( "item1", items[ 0 ] );
    assertEquals( "item2", items[ 1 ] );
    assertEquals( "item3", items[ 2 ] );
    RWTFixture.clearPreserved();
    // focus_index, topIndex, selection
    list.setSelection( 2 );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    Object focusIndex = adapter.getPreserved( ListLCA.PROP_FOCUS_INDEX );
    assertEquals( new Integer( 2 ), focusIndex );
    Object topIndex = adapter.getPreserved( ListLCA.PROP_TOP_INDEX );
    assertEquals( new Integer( list.getTopIndex() ), topIndex );
    Object selection = adapter.getPreserved( Props.SELECTION_INDICES );
    assertEquals( new Integer( 2 ), selection );
    RWTFixture.clearPreserved();
    // control: enabled
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    list.setEnabled( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    RWTFixture.clearPreserved();
    // visible
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    list.setVisible( false );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    RWTFixture.clearPreserved();
    // menu
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    Menu menu = new Menu( list );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    list.setMenu( menu );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    RWTFixture.clearPreserved();
    // bound
    list.getFocusIndex();
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    list.setBounds( rectangle );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    RWTFixture.clearPreserved();
    // control_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    list.addControlListener( new ControlListener() {

      public void controlMoved( final ControlEvent e ) {
      }

      public void controlResized( final ControlEvent e ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    // z-index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    list.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    list.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    list.setFont( font );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    RWTFixture.clearPreserved();
    // tab_index
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    RWTFixture.clearPreserved();
    // tooltiptext
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( null, list.getToolTipText() );
    RWTFixture.clearPreserved();
    list.setToolTipText( "some text" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    assertEquals( "some text", list.getToolTipText() );
    RWTFixture.clearPreserved();
    // activate_listeners Focus_listeners
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    list.addFocusListener( new FocusListener() {

      public void focusGained( final FocusEvent event ) {
      }

      public void focusLost( final FocusEvent event ) {
      }
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    RWTFixture.clearPreserved();
    ActivateEvent.addListener( list, new ActivateAdapter() {
    } );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( list );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testReadDataForSingle() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    List list = new List( shell, SWT.SINGLE );
    list.add( "item1" );
    list.add( "item2" );
    list.add( "item3" );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( list );
    String listId = WidgetUtil.getId( list );
    // Test initial state for the followin tests
    assertEquals( -1, list.getSelectionIndex() );
    // Fake request that selected a single item
    Fixture.fakeRequestParam( listId + ".selection", "0" );
    lca.readData( list );
    assertEquals( 0, list.getSelectionIndex() );
    // Fake request that does not contain a selection parameter
    list.setSelection( 1 );
    Fixture.fakeRequestParam( listId + ".selection", null );
    lca.readData( list );
    assertEquals( 1, list.getSelectionIndex() );
    // Fake request that contains empty selection parameter -> must deselect all
    list.setSelection( 1 );
    Fixture.fakeRequestParam( listId + ".selection", "" );
    lca.readData( list );
    assertEquals( -1, list.getSelectionIndex() );
  }

  public void testReadDataForMulti() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    List list = new List( shell, SWT.MULTI );
    list.add( "item1" );
    list.add( "item2" );
    list.add( "item3" );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( list );
    String listId = WidgetUtil.getId( list );
    // Test initial state for the followin tests
    assertEquals( -1, list.getSelectionIndex() );
    // Fake request that selected 'item1' and 'item2'
    Fixture.fakeRequestParam( listId + ".selection", "0,1" );
    lca.readData( list );
    assertEquals( 0, list.getSelectionIndex() );
    int[] expected = new int[]{
      0, 1
    };
    assertTrue( Arrays.equals( expected, list.getSelectionIndices() ) );
    // Fake request that does not contain a selection parameter
    list.setSelection( 1 );
    Fixture.fakeRequestParam( listId + ".selection", null );
    lca.readData( list );
    assertEquals( 1, list.getSelectionIndex() );
  }

  public void testSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    final List list = new List( shell, SWT.SINGLE );
    list.add( "item1" );
    list.add( "item2" );
    list.setSelection( -1 );
    list.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( final SelectionEvent event ) {
        log.append( "selectionEvent" );
        assertSame( list, event.getSource() );
        assertEquals( 0, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( null, event.item );
        assertEquals( true, event.doit );
      }
    } );
    String listId = WidgetUtil.getId( list );
    Fixture.fakeRequestParam( listId + ".selection", "1" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, listId );
    RWTFixture.readDataAndProcessAction( list );
    assertEquals( "selectionEvent", log.toString() );
    assertEquals( 1, list.getSelectionIndex() );
  }

  public void testRenderSetItems() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    List list = new List( shell, SWT.SINGLE );
    // Ensure that changed items are rendered
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( list );
    Fixture.fakeResponseWriter();
    RWTFixture.preserveWidgets();
    AbstractWidgetLCA listLCA = WidgetUtil.getLCA( list );
    list.setItems( new String[]{
      "a"
    } );
    listLCA.renderChanges( list );
    assertTrue( Fixture.getAllMarkup().indexOf( "setItems" ) != -1 );
    // Ensure that unchanged items do not cause unnecessary JavaScript code
    RWTFixture.markInitialized( list );
    Fixture.fakeResponseWriter();
    RWTFixture.preserveWidgets();
    listLCA.renderChanges( list );
    assertTrue( Fixture.getAllMarkup().indexOf( "setItems" ) == -1 );
  }

  public void testFocusedItem() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    List list = new List( shell, SWT.NONE );
    list.add( "item0" );
    list.add( "item1" );
    list.add( "item2" );
    String listId = WidgetUtil.getId( list );
    // Test with focusIndex -1
    setFocusIndex( list, 0 );
    Fixture.fakeRequestParam( listId + ".focusIndex", "-1" );
    RWTFixture.readDataAndProcessAction( list );
    assertEquals( -1, list.getFocusIndex() );
    // Test with value focusIndex
    setFocusIndex( list, 0 );
    Fixture.fakeRequestParam( listId + ".focusIndex", "1" );
    RWTFixture.readDataAndProcessAction( list );
    assertEquals( 1, list.getFocusIndex() );
    // Test with focusIndex out of range
    setFocusIndex( list, 0 );
    Fixture.fakeRequestParam( listId + ".focusIndex", "22" );
    RWTFixture.readDataAndProcessAction( list );
    assertEquals( 0, list.getFocusIndex() );
  }

  private static void setFocusIndex( final List list, final int focusIndex ) {
    Object adapter = list.getAdapter( IListAdapter.class );
    IListAdapter listAdapter = ( IListAdapter )adapter;
    listAdapter.setFocusIndex( focusIndex );
  }
  
  public void testReadTopIndex() {
    Display display = new Display();
    Shell shell = new Shell( display );
    List list = new List( shell, SWT.MULTI );
    list.setSize( 100, 100 );
    for( int i = 0; i < 6; i++ ) {
      list.add( "Item " + i );
    }
    String listId = WidgetUtil.getId( list );
    Fixture.fakeRequestParam( listId + ".topIndex", "5" );
    ListLCA listLCA = new ListLCA();
    listLCA.readData( list );
    assertEquals( 5, list.getTopIndex() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
