/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.treeitemkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.browser.Ie6;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.ITreeItemAdapter;
import org.eclipse.swt.widgets.*;


public class TreeItemLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    RWTFixture.markInitialized( display );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    new TreeColumn( tree, SWT.NONE, 0 );
    new TreeColumn( tree, SWT.NONE, 1 );
    new TreeColumn( tree, SWT.NONE, 2 );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    treeItem.setText( "qwert" );
    new TreeItem( treeItem, SWT.NONE, 0 );
    Image image = Graphics.getImage( RWTFixture.IMAGE1 );
    treeItem.setImage( image );
    treeItem.setExpanded( true );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( treeItem );
    String[] texts = ( String[] )adapter.getPreserved( TreeItemLCA.PROP_TEXTS );
    assertEquals( "qwert", texts[0] );
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( TreeItemLCA.PROP_EXPANDED ) );
    Image[] images = ( Image[] )adapter.getPreserved( TreeItemLCA.PROP_IMAGES );
    assertEquals( image, images[ 0 ] );
    Object selection = adapter.getPreserved( TreeItemLCA.PROP_SELECTION );
    assertEquals( Boolean.FALSE, selection );
    ITreeItemAdapter itemAdapter
      = ( ITreeItemAdapter )treeItem.getAdapter( ITreeItemAdapter.class );
    Object background = adapter.getPreserved( TreeItemLCA.PROP_BACKGROUND );
    assertEquals( itemAdapter.getUserBackgound(), background );
    Object foreground = adapter.getPreserved( TreeItemLCA.PROP_FOREGROUND );
    assertEquals( itemAdapter.getUserForegound(), foreground );
    Font[] fonts = ( Font[] )adapter.getPreserved( TreeItemLCA.PROP_FONT );
    assertNull( fonts );
    Color[] backgrounds
      = ( Color[] )adapter.getPreserved( TreeItemLCA.PROP_CELL_BACKGROUNDS );
    assertNull( backgrounds );
    Color[] foregrounds
      = ( Color[] )adapter.getPreserved( TreeItemLCA.PROP_CELL_FOREGROUNDS );
    assertNull( foregrounds );
    Object materialized = adapter.getPreserved( TreeItemLCA.PROP_MATERIALIZED );
    assertEquals( Boolean.TRUE, materialized );
    RWTFixture.clearPreserved();
    treeItem.setText( 0, "item11" );
    treeItem.setText( 1, "item12" );
    treeItem.setText( 2, "item13" );
    treeItem.setImage( 0, Graphics.getImage( RWTFixture.IMAGE1 ) );
    treeItem.setImage( 1, Graphics.getImage( RWTFixture.IMAGE2 ) );
    treeItem.setImage( 2, Graphics.getImage( RWTFixture.IMAGE3 ) );
    tree.setSelection( treeItem );
    background = Graphics.getColor( 234, 113, 34 );
    treeItem.setBackground( ( Color )background );
    foreground = Graphics.getColor( 122, 232, 45 );
    treeItem.setForeground( ( Color )foreground );
    Font font1 = Graphics.getFont( "font1", 10, 1 );
    treeItem.setFont( 0, font1 );
    Font font2 = Graphics.getFont( "font1", 8, 1 );
    treeItem.setFont( 1, font2 );
    Font font3 = Graphics.getFont( "font1", 6, 1 );
    treeItem.setFont( 2, font3 );
    Color background1 = Graphics.getColor( 234, 230, 54 );
    treeItem.setBackground( 0, background1 );
    Color background2 = Graphics.getColor( 145, 222, 134 );
    treeItem.setBackground( 1, background2 );
    Color background3 = Graphics.getColor( 143, 134, 34 );
    treeItem.setBackground( 2, background3 );
    Color foreground1 = Graphics.getColor( 77, 77, 54 );
    treeItem.setForeground( 0, foreground1 );
    Color foreground2 = Graphics.getColor( 156, 45, 134 );
    treeItem.setForeground( 1, foreground2 );
    Color foreground3 = Graphics.getColor( 88, 134, 34 );
    treeItem.setForeground( 2, foreground3 );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( treeItem );
    texts = ( String[] )adapter.getPreserved( TreeItemLCA.PROP_TEXTS );
    assertEquals( "item11", texts[ 0 ] );
    assertEquals( "item12", texts[ 1 ] );
    assertEquals( "item13", texts[ 2 ] );
    images = ( Image[] )adapter.getPreserved( TreeItemLCA.PROP_IMAGES );
    assertEquals( Graphics.getImage( RWTFixture.IMAGE1 ), images[ 0 ] );
    assertEquals( Graphics.getImage( RWTFixture.IMAGE2 ), images[ 1 ] );
    assertEquals( Graphics.getImage( RWTFixture.IMAGE3 ), images[ 2 ] );
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( TreeItemLCA.PROP_SELECTION ) );
    assertEquals( background,
                  adapter.getPreserved( TreeItemLCA.PROP_BACKGROUND ) );
    assertEquals( foreground,
                  adapter.getPreserved( TreeItemLCA.PROP_FOREGROUND ) );
    fonts = ( Font[] )adapter.getPreserved( TreeItemLCA.PROP_CELL_FONTS );
    assertEquals( font1, fonts[ 0 ] );
    assertEquals( font2, fonts[ 1 ] );
    assertEquals( font3, fonts[ 2 ] );
    backgrounds = ( Color[] )adapter.getPreserved( TreeItemLCA.PROP_CELL_BACKGROUNDS );
    assertEquals( background1, backgrounds[ 0 ] );
    assertEquals( background2, backgrounds[ 1 ] );
    assertEquals( background3, backgrounds[ 2 ] );
    foregrounds = ( Color[] )adapter.getPreserved( TreeItemLCA.PROP_CELL_FOREGROUNDS );
    assertEquals( foreground1, foregrounds[ 0 ] );
    assertEquals( foreground2, foregrounds[ 1 ] );
    assertEquals( foreground3, foregrounds[ 2 ] );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testCheckPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.CHECK );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( treeItem );
    assertEquals( Boolean.FALSE,
                  adapter.getPreserved( TreeItemLCA.PROP_CHECKED ) );
    assertEquals( Boolean.FALSE,
                  adapter.getPreserved( TreeItemLCA.PROP_GRAYED ) );
    RWTFixture.clearPreserved();
    treeItem.setChecked( true );
    treeItem.setGrayed( true );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( treeItem );
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( TreeItemLCA.PROP_CHECKED ) );
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( TreeItemLCA.PROP_GRAYED ) );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testMaterializedPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    RWTFixture.markInitialized( display );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( treeItem );
    Object materialized = adapter.getPreserved( TreeItemLCA.PROP_MATERIALIZED );
    assertEquals( Boolean.FALSE, materialized );
    RWTFixture.clearPreserved();
    display.dispose();
  }

  public void testTreeEvent() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    final Tree tree = new Tree( shell, SWT.NONE );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    final TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    new TreeItem( treeItem, SWT.NONE );
    final StringBuffer log = new StringBuffer();
    TreeListener listener = new TreeListener() {

      public void treeCollapsed( final TreeEvent event ) {
        assertEquals( tree, event.getSource() );
        assertEquals( treeItem, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "collapsed" );
      }

      public void treeExpanded( final TreeEvent event ) {
        assertEquals( tree, event.getSource() );
        assertEquals( treeItem, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "expanded" );
      }
    };
    tree.addTreeListener( listener );
    String treeItemId = WidgetUtil.getId( treeItem );
    Fixture.fakeRequestParam( treeItemId + ".state", "expanded" );
    Fixture.fakeRequestParam( JSConst.EVENT_TREE_EXPANDED, treeItemId );
    RWTFixture.readDataAndProcessAction( treeItem );
    assertEquals( "expanded", log.toString() );
    log.setLength( 0 );
    Fixture.fakeRequestParam( JSConst.EVENT_TREE_EXPANDED, null );
    Fixture.fakeRequestParam( treeItemId + ".state", "collapsed" );
    Fixture.fakeRequestParam( JSConst.EVENT_TREE_COLLAPSED, treeItemId );
    RWTFixture.readDataAndProcessAction( treeItem );
    assertEquals( "collapsed", log.toString() );
  }

  public void testExpandCollapse() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    new TreeItem( treeItem, SWT.NONE );
    treeItem.setExpanded( false );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( treeItem );
    String treeItemId = WidgetUtil.getId( treeItem );
    Fixture.fakeRequestParam( treeItemId + ".state", "expanded" );
    lca.readData( treeItem );
    assertEquals( true, treeItem.getExpanded() );
    Fixture.fakeRequestParam( treeItemId + ".state", "collapsed" );
    lca.readData( treeItem );
    assertEquals( false, treeItem.getExpanded() );
  }

  public void testChecked() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.CHECK );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    String displayId = DisplayUtil.getId( display );
    String treeItemId = WidgetUtil.getId( treeItem );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( treeItemId + ".checked", "true" );
    RWTFixture.executeLifeCycleFromServerThread( );
    assertEquals( true, treeItem.getChecked() );
  }

  public void testRenderChanges() throws IOException {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    shell.open();
    RWTFixture.markInitialized( display );
    RWTFixture.markInitialized( shell );
    RWTFixture.markInitialized( tree );
    RWTFixture.markInitialized( treeItem );
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    TreeItemLCA tiLCA = new TreeItemLCA();
    treeItem.setBackground( display.getSystemColor( SWT.COLOR_RED ) );
    tiLCA.renderChanges( treeItem );
    String expected;
    expected = "w.setBackgroundColor( \"#ff0000\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    Fixture.fakeResponseWriter();
    RWTFixture.clearPreserved();
    RWTFixture.preserveWidgets();
    treeItem.setForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    tiLCA.renderChanges( treeItem );
    expected = "w.setTextColor( \"#00ff00\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  public void testTextEscape() throws Exception {
    Fixture.fakeResponseWriter();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeColumn col1 = new TreeColumn( tree, SWT.NONE );
    col1.setText( "<x>&Col1" );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    item1.setText( "<x>&Item1" );
    shell.open();
    TreeItemLCA lca = new TreeItemLCA();
    lca.renderChanges( item1 );
    String expected = "w.setTexts( [ \"&lt;x&gt;&amp;Item1\" ] );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
    Fixture.fakeBrowser( new Ie6( true, true ) );
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
