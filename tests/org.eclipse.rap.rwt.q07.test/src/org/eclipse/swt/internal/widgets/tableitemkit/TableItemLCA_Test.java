/*******************************************************************************
 * Copyright (c) 2007-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tableitemkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class TableItemLCA_Test extends TestCase {

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakePhase( PhaseId.RENDER );
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  public void testPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.BORDER );
    new TableColumn( table, SWT.CENTER );
    new TableColumn( table, SWT.CENTER );
    new TableColumn( table, SWT.CENTER );
    TableItem item1 = new TableItem( table, SWT.NONE );
    TableItem item2 = new TableItem( table, SWT.NONE );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item1 );
//    Object top = adapter.getPreserved( TableItemLCA.PROP_TOP );
//    assertEquals( new Integer( item1.getBounds().y ), top );
    Object checked = adapter.getPreserved( TableItemLCA.PROP_CHECKED );
    assertEquals( Boolean.FALSE, checked );
    Object grayed = adapter.getPreserved( TableItemLCA.PROP_GRAYED );
    assertEquals( Boolean.FALSE, grayed );
    Object index = adapter.getPreserved( TableItemLCA.PROP_INDEX );
    assertEquals( new Integer( 0 ), index );
    Object selected = adapter.getPreserved( TableItemLCA.PROP_SELECTED );
    assertEquals( Boolean.FALSE, selected );
    Object focused = adapter.getPreserved( TableItemLCA.PROP_FOCUSED );
    assertEquals( Boolean.FALSE, focused );
    String[] texts1 = TableItemLCA.getTexts( item1 );
    String[] texts2
     = ( String[] )adapter.getPreserved( TableItemLCA.PROP_TEXTS );
    assertEquals( texts1[ 0 ], texts2[ 0 ] );
    assertEquals( texts1[ 1 ], texts2[ 1 ] );
    assertEquals( texts1[ 2 ], texts2[ 2 ] );
    Image[] images1 = TableItemLCA.getImages( item1 );
    Image[] images2
     = ( Image[] )adapter.getPreserved( TableItemLCA.PROP_IMAGES );
    assertEquals( images1[ 0 ], images2[ 0 ] );
    assertEquals( images1[ 1 ], images2[ 1 ] );
    assertEquals( images1[ 2 ], images2[ 2 ] );
    Font[] fonts1 = TableItemLCA.getFonts( item1 );
    Font[] fonts2 = ( Font[] )adapter.getPreserved( TableItemLCA.PROP_FONT );
    assertEquals( fonts1[ 0 ], fonts2[ 0 ] );
    assertEquals( fonts1[ 1 ], fonts2[ 1 ] );
    assertEquals( fonts1[ 2 ], fonts2[ 2 ] );
    Color[] backgrounds1 = TableItemLCA.getBackgrounds( item1 );
    Color[] backgrounds2
     = ( Color[] )adapter.getPreserved( TableItemLCA.PROP_BACKGROUND );
    assertEquals( backgrounds1[ 0 ], backgrounds2[ 0 ] );
    assertEquals( backgrounds1[ 1 ], backgrounds2[ 1 ] );
    assertEquals( backgrounds1[ 2 ], backgrounds2[ 2 ] );
    Color[] foregrounds1 = TableItemLCA.getForegrounds( item1 );
    Color[] foregrounds2
     = ( Color[] )adapter.getPreserved( TableItemLCA.PROP_FOREGROUND );
    assertEquals( foregrounds1[ 0 ], foregrounds2[ 0 ] );
    assertEquals( foregrounds1[ 1 ], foregrounds2[ 1 ] );
    assertEquals( foregrounds1[ 2 ], foregrounds2[ 2 ] );
    RWTFixture.clearPreserved();
    item1.setText( 0, "item11" );
    item1.setText( 1, "item12" );
    item1.setText( 2, "item13" );
    Font font1 = Graphics.getFont( "font1", 10, 1 );
    item1.setFont( 0, font1 );
    Font font2 = Graphics.getFont( "font1", 8, 1 );
    item1.setFont( 1, font2 );
    Font font3 = Graphics.getFont( "font1", 6, 1 );
    item1.setFont( 2, font3 );
    Image image1 = Graphics.getImage( RWTFixture.IMAGE1 );
    Image image2 = Graphics.getImage( RWTFixture.IMAGE2 );
    Image image3 = Graphics.getImage( RWTFixture.IMAGE3 );
    item1.setImage( new Image[]{
      image1, image2, image3
    } );
    Color background1 = Graphics.getColor( 234, 230, 54 );
    item1.setBackground( 0, background1 );
    Color background2 = Graphics.getColor( 145, 222, 134 );
    item1.setBackground( 1, background2 );
    Color background3 = Graphics.getColor( 143, 134, 34 );
    item1.setBackground( 2, background3 );
    Color foreground1 = Graphics.getColor( 77, 77, 54 );
    item1.setForeground( 0, foreground1 );
    Color foreground2 = Graphics.getColor( 156, 45, 134 );
    item1.setForeground( 1, foreground2 );
    Color foreground3 = Graphics.getColor( 88, 134, 34 );
    item1.setForeground( 2, foreground3 );
    table.setSelection( 0 );
    ITableAdapter tableAdapter
    = ( ITableAdapter )table.getAdapter( ITableAdapter.class );
    tableAdapter.setFocusIndex( 0 );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( item1 );
//    top = adapter.getPreserved( TableItemLCA.PROP_TOP );
    checked = adapter.getPreserved( TableItemLCA.PROP_CHECKED );
    grayed = adapter.getPreserved( TableItemLCA.PROP_GRAYED );
    index = adapter.getPreserved( TableItemLCA.PROP_INDEX );
    selected = adapter.getPreserved( TableItemLCA.PROP_SELECTED );
    focused = adapter.getPreserved( TableItemLCA.PROP_FOCUSED );
//    assertEquals( new Integer( item1.getBounds().y ), top );
    assertEquals( Boolean.FALSE, checked );
    assertEquals( Boolean.FALSE, grayed );
    assertEquals( Boolean.TRUE, selected );
    assertEquals( new Integer( 0 ), index );
    assertEquals( Boolean.TRUE, focused );
    texts2 = ( String[] )adapter.getPreserved( TableItemLCA.PROP_TEXTS );
    assertEquals( "item11", texts2[ 0 ] );
    assertEquals( "item12", texts2[ 1 ] );
    assertEquals( "item13", texts2[ 2 ] );
    images2 = ( Image[] )adapter.getPreserved( TableItemLCA.PROP_IMAGES );
    assertEquals( image1, images2[ 0 ] );
    assertEquals( image2, images2[ 1 ] );
    assertEquals( image3, images2[ 2 ] );
    fonts2 = ( Font[] )adapter.getPreserved( TableItemLCA.PROP_FONT );
    assertEquals( font1, fonts2[ 0 ] );
    assertEquals( font2, fonts2[ 1 ] );
    assertEquals( font3, fonts2[ 2 ] );
    backgrounds2
     = ( Color[] )adapter.getPreserved( TableItemLCA.PROP_BACKGROUND );
    assertEquals( background1, backgrounds2[ 0 ] );
    assertEquals( background2, backgrounds2[ 1 ] );
    assertEquals( background3, backgrounds2[ 2 ] );
    foregrounds2
     = ( Color[] )adapter.getPreserved( TableItemLCA.PROP_FOREGROUND );
    assertEquals( foreground1, foregrounds2[ 0 ] );
    assertEquals( foreground2, foregrounds2[ 1 ] );
    assertEquals( foreground3, foregrounds2[ 2 ] );
    RWTFixture.clearPreserved();
    // text
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( item2 );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    RWTFixture.clearPreserved();
    item2.setText( "some text" );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( item2 );
    assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    RWTFixture.clearPreserved();
    // image
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( item2 );
    assertEquals( null, adapter.getPreserved( Props.IMAGE ) );
    RWTFixture.clearPreserved();
    Image image = Graphics.getImage( RWTFixture.IMAGE1 );
    item2.setImage( image );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( item2 );
    assertSame( image, adapter.getPreserved( Props.IMAGE ) );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testCheckPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.CHECK );
    TableItem item = new TableItem( table, SWT.NONE );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    Object checked = adapter.getPreserved( TableItemLCA.PROP_CHECKED );
    assertEquals( Boolean.FALSE, checked );
    Object grayed = adapter.getPreserved( TableItemLCA.PROP_GRAYED );
    assertEquals( Boolean.FALSE, grayed );
    RWTFixture.clearPreserved();
    item.setChecked( true );
    item.setGrayed( true );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( item );
    checked = adapter.getPreserved( TableItemLCA.PROP_CHECKED );
    grayed = adapter.getPreserved( TableItemLCA.PROP_GRAYED );
    assertEquals( Boolean.TRUE, checked );
    assertEquals( Boolean.TRUE, grayed );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testItemTextWithoutColumn() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    // Ensure that even though there are no columns, the first text of an item
    // will be rendered
    Fixture.fakeResponseWriter();
    TableItemLCA tableItemLCA = new TableItemLCA();
    RWTFixture.markInitialized( item );
    tableItemLCA.preserveValues( item );
    item.setText( "newText" );
    tableItemLCA.renderChanges( item );
    String expected = "w.setTexts( [ \"newText\" ] )";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  public void testWidgetSelectedWithCheck() {
    final SelectionEvent[] events = new SelectionEvent[ 1 ];
    Display display = new Display();
    Shell shell = new Shell( display );
    final Table table = new Table( shell, SWT.CHECK );
    TableItem item1 = new TableItem( table, SWT.NONE );
    final TableItem item2 = new TableItem( table, SWT.NONE );
    table.setSelection( 0 );
    table.addSelectionListener( new SelectionListener() {

      public void widgetSelected( final SelectionEvent event ) {
        events[ 0 ] = event;
      }

      public void widgetDefaultSelected( final SelectionEvent event ) {
        fail( "unexpected event: widgetDefaultSelected" );
      }
    } );
    // Simulate request that comes in after item2 was checked (but not selected)
    RWTFixture.fakeNewRequest();
    String displayId = DisplayUtil.getId( display );
    String item2Id = WidgetUtil.getId( item2 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( item2Id + ".checked", "true" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, item2Id );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_DETAIL, "check" );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertNotNull( "SelectionEvent was not fired", events[ 0 ] );
    assertEquals( table, events[ 0 ].getSource() );
    assertEquals( item2, events[ 0 ].item );
    assertEquals( true, events[ 0 ].doit );
    assertEquals( 0, events[ 0 ].x );
    assertEquals( 0, events[ 0 ].y );
    assertEquals( 0, events[ 0 ].width );
    assertEquals( 0, events[ 0 ].height );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( item1, table.getSelection()[ 0 ] );
  }

  public void testDisposeSelected() {
    final boolean[] executed = { false };
    Display display = new Display();
    Shell shell = new Shell( display );
    final Table table = new Table( shell, SWT.CHECK );
    new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    new TableItem( table, SWT.NONE );
    table.setSelection( 2 );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent e ) {
        table.remove( 1, 2 );
        executed[ 0 ] = true;
      }
    } );
    RWTFixture.fakeNewRequest();
    String displayId = DisplayUtil.getId( display );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    String buttonId = WidgetUtil.getId( button );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertTrue( executed[ 0 ] );
  }

  public void testDispose() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display );
    final Table table = new Table( shell, SWT.CHECK );
    TableItem itemOnlyDisposed = new TableItem( table, SWT.NONE );
    TableItem itemWithTableDisposed = new TableItem( table, SWT.NONE );
    RWTFixture.markInitialized( table );
    RWTFixture.markInitialized( itemOnlyDisposed );
    RWTFixture.markInitialized( itemWithTableDisposed );
    // Test that when a single items is disposed, its JavaScript dispose
    // function is called
    itemOnlyDisposed.dispose();
    AbstractWidgetLCA lca = WidgetUtil.getLCA( itemWithTableDisposed );
    Fixture.fakeResponseWriter();
    lca.renderDispose( itemOnlyDisposed );
    String expected = "var wm = org.eclipse.swt.WidgetManager.getInstance();"
                      + "var w = wm.findWidgetById( \"w3\" );"
                      + "w.dispose();";
    assertEquals( expected, Fixture.getAllMarkup() );
    // Test that when the whole Tables is dipsosed of, the TableItems dispose
    // function is *not* called
    table.dispose();
    lca = WidgetUtil.getLCA( itemWithTableDisposed );
    Fixture.fakeResponseWriter();
    lca.renderDispose( itemWithTableDisposed );
    assertEquals( "", Fixture.getAllMarkup() );
    assertTrue( itemWithTableDisposed.isDisposed() );
  }

  public void testWriteChangesForVirtualItem() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.VIRTUAL );
    table.setItemCount( 100 );
    // Ensure that nothing is written for an item that is virtual and whose
    // cached was false and remains unchanged while processing the life cycle
    TableItem item = table.getItem( 0 );
    table.clear( 0 );
    TableItemLCA lca = new TableItemLCA();
    Fixture.fakeResponseWriter();
    RWTFixture.markInitialized( item );
    // Ensure that nothing else than the 'checked' property gets preserved
    lca.preserveValues( item );
    IWidgetAdapter itemAdapter = WidgetUtil.getAdapter( item );
    assertEquals( Boolean.FALSE,
                  itemAdapter.getPreserved( TableItemLCA.PROP_CACHED ) );
    assertNull( itemAdapter.getPreserved( TableItemLCA.PROP_TEXTS ) );
    assertNull( itemAdapter.getPreserved( TableItemLCA.PROP_IMAGES ) );
    assertNull( itemAdapter.getPreserved( TableItemLCA.PROP_CHECKED ) );
    // ... and no markup is generated for a uncached item that was already
    // uncached when entering the life cycle
    lca.renderChanges( item );
    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testGetForegrounds() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Color red = display.getSystemColor( SWT.COLOR_RED );
    // simple case: no explicit colors at all
    Color[] foregrounds = TableItemLCA.getForegrounds( item );
    assertEquals( null, foregrounds[ 0 ] );
    // set foreground on table but not on item
    table.setForeground( red );
    foregrounds = TableItemLCA.getForegrounds( item );
    assertEquals( null, foregrounds[ 0 ] );
    // clear foreground on table and set on item
    table.setForeground( null );
    item.setForeground( red );
    foregrounds = TableItemLCA.getForegrounds( item );
    assertEquals( red, foregrounds[ 0 ] );
  }

  public void testGetBackground() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Table table = new Table( shell, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Color red = display.getSystemColor( SWT.COLOR_RED );
    // simple case: no explicit colors at all
    Color[] backgrounds = TableItemLCA.getBackgrounds( item );
    assertEquals( null, backgrounds[ 0 ] );
    // set background on table but not on item
    table.setBackground( red );
    backgrounds = TableItemLCA.getBackgrounds( item );
    assertEquals( null, backgrounds[ 0 ] );
    // clear background on table and set on item
    table.setBackground( null );
    item.setBackground( red );
    backgrounds = TableItemLCA.getBackgrounds( item );
    assertEquals( red, backgrounds[ 0 ] );
  }
}
