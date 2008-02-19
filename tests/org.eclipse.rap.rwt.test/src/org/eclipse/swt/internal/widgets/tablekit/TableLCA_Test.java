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

package org.eclipse.swt.internal.widgets.tablekit;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.internal.widgets.ItemHolder;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;


public class TableLCA_Test extends TestCase {

  public void testSetDataEvent() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display );
    final Table table = new Table( shell, SWT.VIRTUAL );
    table.setItemCount( 10 );
    table.addListener( SWT.SetData, new Listener() {
      public void handleEvent( final Event event ) {
        assertSame( table.getItem( 1 ), event.item );
        assertEquals( 1, event.index );
        log.append( "SetDataEvent" );
      }
    } );
    String displayId = DisplayUtil.getId( display );
    String tableId = WidgetUtil.getId( table );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_SET_DATA, tableId );
    Fixture.fakeRequestParam( JSConst.EVENT_SET_DATA_INDEX, "1" );
    RWTFixture.executeLifeCycleFromServerThread( );
    
    
    assertEquals( 1, ItemHolder.getItems( table ).length );
    assertEquals( "SetDataEvent", log.toString() );
    String tableItemCtor = "org.eclipse.swt.widgets.TableItem";
    assertTrue( Fixture.getAllMarkup().indexOf( tableItemCtor ) != -1 );
  }
  
  public void testGetMeasureItemWithoutColumnsVirtual() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final String[] data = new String[ 1000 ];
    for( int i = 0; i < data.length; i++ ) {
      data[ i ] = "";
    }
    Listener setDataListener = new Listener() {
      public void handleEvent( final Event event ) {
        TableItem item = ( TableItem )event.item;
        int index = item.getParent().indexOf( item );
        item.setText( data[ index  ] );
      }
    };
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setSize( 100, 100 );
    Table table = new Table( shell, SWT.VIRTUAL );
    table.addListener( SWT.SetData, setDataListener );
    table.setItemCount( data.length );
    table.setSize( 90, 90 );
    shell.open();
    
    int resolvedItemCount;
    TableItem measureItem;
    // Test with items that all have the same width
    resolvedItemCount = countResolvedItems( table );
    measureItem = TableLCAUtil.getMeasureItem( table );
    assertNotNull( measureItem );
    assertEquals( resolvedItemCount, countResolvedItems( table ) );
    
    // Test with items that have ascending length
    data[ 0 ] = "a";
    for( int i = 1; i < data.length; i++ ) {
      data[ i ] = data[ i - 1 ] + "a";
    }
    table.getItem( 100 ).getText();  // resolves item
    resolvedItemCount = countResolvedItems( table );
    measureItem = TableLCAUtil.getMeasureItem( table );
    int measureItemIndex = measureItem.getParent().indexOf( measureItem );
    assertEquals( 100, measureItemIndex );
    assertEquals( resolvedItemCount, countResolvedItems( table ) );
  }
  
  public void testRedraw() {
    final Table[] table = { null };
    Display display = new Display();
    final Shell shell = new Shell( display );
    shell.setSize( 100, 100 );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        table[ 0 ] = new Table( shell, SWT.VIRTUAL );
        table[ 0 ].setItemCount( 500 );
        table[ 0 ].setSize( 90, 90 );
        assertFalse( isItemVirtual( table[ 0 ], 0 ) );
        table[ 0 ].clearAll();
        table[ 0 ].redraw();
      }
    } );
    shell.open();
    String displayId = DisplayUtil.getId( display );
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    String buttonId = WidgetUtil.getId( button );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId  );
    RWTFixture.executeLifeCycleFromServerThread( );
    
    assertFalse( isItemVirtual( table[ 0 ], 0  ) );
  }
  
  public void testNoUnwantedResolveItems() {
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setSize( 100, 100 );
    final Table table = new Table( shell, SWT.VIRTUAL );
    table.setSize( 90, 90 );
    table.setItemCount( 1000 );
    shell.open();
    
    String displayId = DisplayUtil.getId( display );
    String tableId = WidgetUtil.getId( table );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_SET_DATA, tableId );
    Fixture.fakeRequestParam( JSConst.EVENT_SET_DATA_INDEX, "500,501,502,503" );
    Fixture.fakeRequestParam( tableId + ".topIndex", "500" );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void beforePhase( final PhaseEvent event ) {
        table.redraw();
      }
      public void afterPhase( final PhaseEvent event ) {
      }
      public PhaseId getPhaseId() {
        return PhaseId.PROCESS_ACTION;
      }
    } );
    RWTFixture.executeLifeCycleFromServerThread();

    assertTrue( isItemVirtual( table, 499 ) );
    assertTrue( isItemVirtual( table, 800 ) );
    assertTrue( isItemVirtual( table, 999 ) );
  }

  public void testClearVirtual() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    lifeCycle.addPhaseListener( new PreserveWidgetsPhaseListener() );
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setSize( 100, 100 );
    shell.setLayout( new FillLayout() );
    final Table table = new Table( shell, SWT.VIRTUAL );
    table.setItemCount( 100 );
    shell.layout();
    shell.open();
    ITableAdapter adapter 
      = ( ITableAdapter )table.getAdapter( ITableAdapter.class );
    // precondition: all items are resolved (TableItem#cached == true)
    // resolve all items and ensure
    for( int i = 0; i < table.getItemCount(); i++ ) {
      table.getItem( i ).getText();
    }
    assertFalse( adapter.isItemVirtual( table.getItemCount() - 1 ) );
    // 
    String displayId = DisplayUtil.getId( display );
    final int lastItemIndex = table.getItemCount() - 1;
    String lastItemId = WidgetUtil.getId( table.getItem( lastItemIndex ) );
    // fake one request that would initialize the UI
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    RWTFixture.executeLifeCycleFromServerThread();
    // run actual request
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void beforePhase( final PhaseEvent event ) {
        table.clear( lastItemIndex );
      }
      public void afterPhase( final PhaseEvent event ) {
      }
      public PhaseId getPhaseId() {
        return PhaseId.PROCESS_ACTION;
      }
    } );
    RWTFixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    String expected 
      = "var w = wm.findWidgetById( \"" 
      + lastItemId 
      + "\" );w.clear()";
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  private static int countResolvedItems( final Table table ) {
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    int result = 0;
    TableItem[] createdItems = tableAdapter.getCreatedItems();
    for( int i = 0; i < createdItems.length; i++ ) {
      int index = table.indexOf( createdItems[ i ] );
      if( !tableAdapter.isItemVirtual( index ) ) {
        result++;
      }
    }
    return result;
  }
  
  private static boolean isItemVirtual( final Table table, final int index ) {
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    return tableAdapter.isItemVirtual( index );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
