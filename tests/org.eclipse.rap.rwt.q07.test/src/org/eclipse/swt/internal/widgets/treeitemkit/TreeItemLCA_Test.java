/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.treeitemkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.internal.widgets.IWidgetColorAdapter;
import org.eclipse.swt.widgets.*;

public class TreeItemLCA_Test extends TestCase {

  private Display display;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveValues() {
    Tree tree = new Tree( shell, SWT.NONE );
    Fixture.markInitialized( display );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    new TreeColumn( tree, SWT.NONE, 0 );
    new TreeColumn( tree, SWT.NONE, 1 );
    new TreeColumn( tree, SWT.NONE, 2 );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    treeItem.setText( "qwert" );
    new TreeItem( treeItem, SWT.NONE, 0 );
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    treeItem.setImage( image );
    treeItem.setExpanded( true );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( treeItem );
    String[] texts = ( String[] )adapter.getPreserved( TreeItemLCA.PROP_TEXTS );
    assertEquals( "qwert", texts[ 0 ] );
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( TreeItemLCA.PROP_EXPANDED ) );
    Image[] images = ( Image[] )adapter.getPreserved( TreeItemLCA.PROP_IMAGES );
    assertEquals( image, images[ 0 ] );
    Object selection = adapter.getPreserved( TreeItemLCA.PROP_SELECTION );
    assertEquals( Boolean.FALSE, selection );
    IWidgetColorAdapter colorAdapter
      = ( IWidgetColorAdapter )treeItem.getAdapter( IWidgetColorAdapter.class );
    Object background = adapter.getPreserved( TreeItemLCA.PROP_BACKGROUND );
    assertEquals( colorAdapter.getUserBackgound(), background );
    Object foreground = adapter.getPreserved( TreeItemLCA.PROP_FOREGROUND );
    assertEquals( colorAdapter.getUserForegound(), foreground );
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
    Fixture.clearPreserved();
    treeItem.setText( 0, "item11" );
    treeItem.setText( 1, "item12" );
    treeItem.setText( 2, "item13" );
    treeItem.setImage( 0, Graphics.getImage( Fixture.IMAGE1 ) );
    treeItem.setImage( 1, Graphics.getImage( Fixture.IMAGE2 ) );
    treeItem.setImage( 2, Graphics.getImage( Fixture.IMAGE3 ) );
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
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( treeItem );
    texts = ( String[] )adapter.getPreserved( TreeItemLCA.PROP_TEXTS );
    assertEquals( "item11", texts[ 0 ] );
    assertEquals( "item12", texts[ 1 ] );
    assertEquals( "item13", texts[ 2 ] );
    images = ( Image[] )adapter.getPreserved( TreeItemLCA.PROP_IMAGES );
    assertEquals( Graphics.getImage( Fixture.IMAGE1 ), images[ 0 ] );
    assertEquals( Graphics.getImage( Fixture.IMAGE2 ), images[ 1 ] );
    assertEquals( Graphics.getImage( Fixture.IMAGE3 ), images[ 2 ] );
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
    backgrounds
      = ( Color[] )adapter.getPreserved( TreeItemLCA.PROP_CELL_BACKGROUNDS );
    assertEquals( background1, backgrounds[ 0 ] );
    assertEquals( background2, backgrounds[ 1 ] );
    assertEquals( background3, backgrounds[ 2 ] );
    foregrounds
      = ( Color[] )adapter.getPreserved( TreeItemLCA.PROP_CELL_FOREGROUNDS );
    assertEquals( foreground1, foregrounds[ 0 ] );
    assertEquals( foreground2, foregrounds[ 1 ] );
    assertEquals( foreground3, foregrounds[ 2 ] );
    Fixture.clearPreserved();
    display.dispose();
  }

  public void testCheckPreserveValues() {
    Tree tree = new Tree( shell, SWT.CHECK );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( treeItem );
    assertEquals( Boolean.FALSE,
                  adapter.getPreserved( TreeItemLCA.PROP_CHECKED ) );
    assertEquals( Boolean.FALSE,
                  adapter.getPreserved( TreeItemLCA.PROP_GRAYED ) );
    Fixture.clearPreserved();
    treeItem.setChecked( true );
    treeItem.setGrayed( true );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( treeItem );
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( TreeItemLCA.PROP_CHECKED ) );
    assertEquals( Boolean.TRUE,
                  adapter.getPreserved( TreeItemLCA.PROP_GRAYED ) );
    Fixture.clearPreserved();
    display.dispose();
  }

  public void testPreserveVariant() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( treeItem );
    Object variant = adapter.getPreserved( TreeItemLCA.PROP_VARIANT );
    assertNull( variant );
    Fixture.clearPreserved();
    treeItem.setData( WidgetUtil.CUSTOM_VARIANT, "abc" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( treeItem );
    variant = adapter.getPreserved( TreeItemLCA.PROP_VARIANT );
    assertEquals( "abc", variant );
  }

  public void testMaterializedPreserveValues() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( treeItem );
    Object materialized = adapter.getPreserved( TreeItemLCA.PROP_MATERIALIZED );
    assertEquals( Boolean.FALSE, materialized );
    Fixture.clearPreserved();
    display.dispose();
  }
  
  public void testLcaDoesNotMaterializeItem() {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    tree.setItemCount( 100 );
    tree.setSize( 100, 100 );
    TreeItem treeItem = tree.getItem( 99 );
    shell.open();
    Fixture.executeLifeCycleFromServerThread();
    ITreeAdapter adapter 
      = ( ITreeAdapter )tree.getAdapter( ITreeAdapter.class );    
    assertFalse( adapter.isCached( treeItem ) );
  }

  public void testTreeEvent() {
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
    Fixture.fakeRequestParam( JSConst.EVENT_TREE_EXPANDED, treeItemId );
    Fixture.readDataAndProcessAction( treeItem );
    assertEquals( "expanded", log.toString() );
    log.setLength( 0 );
    Fixture.fakeRequestParam( JSConst.EVENT_TREE_EXPANDED, null );
    Fixture.fakeRequestParam( JSConst.EVENT_TREE_COLLAPSED, treeItemId );
    Fixture.readDataAndProcessAction( treeItem );
    assertEquals( "collapsed", log.toString() );
  }

  public void testExpandCollapse() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    new TreeItem( treeItem, SWT.NONE );
    treeItem.setExpanded( false );
    String treeItemId = WidgetUtil.getId( treeItem );
    Fixture.fakeRequestParam( JSConst.EVENT_TREE_EXPANDED, treeItemId );
    Fixture.readDataAndProcessAction( treeItem );
    assertEquals( true, treeItem.getExpanded() );
    Fixture.fakeRequestParam( JSConst.EVENT_TREE_COLLAPSED, treeItemId );
    Fixture.readDataAndProcessAction( treeItem );
    assertEquals( false, treeItem.getExpanded() );
  }

  public void testChecked() {
    Tree tree = new Tree( shell, SWT.CHECK );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    String treeItemId = WidgetUtil.getId( treeItem );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( treeItemId + ".checked", "true" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( true, treeItem.getChecked() );
  }

  public void testRenderChanges() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( tree );
    Fixture.markInitialized( treeItem );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    TreeItemLCA tiLCA = new TreeItemLCA();
    treeItem.setBackground( display.getSystemColor( SWT.COLOR_RED ) );
    tiLCA.renderChanges( treeItem );
    String expected;
    expected = "w.setBackground( \"#ff0000\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    treeItem.setForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    tiLCA.renderChanges( treeItem );
    expected = "w.setForeground( \"#00ff00\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }
  
  public void testRenderSelection() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    String treeRef = "wm.findWidgetById( \"" + WidgetUtil.getId( tree ) +"\" )";
    String treeItemRef 
      = "wm.findWidgetById( \"" + WidgetUtil.getId( treeItem ) +"\" )";
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( tree );
    Fixture.markInitialized( treeItem );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    TreeItemLCA tiLCA = new TreeItemLCA();
    tiLCA.renderChanges( treeItem );
    assertTrue( Fixture.getAllMarkup().indexOf( "select" ) == -1 );
    Fixture.fakeResponseWriter();
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    tree.select( treeItem );
    tiLCA.renderChanges( treeItem );
    String expectedSelect = treeRef + ";w.selectItem( " + treeItemRef + " );";
    String expectedFocus = ";w.setFocusItem( " + treeItemRef + " );";
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( expectedSelect ) != -1 );
    assertTrue( markup.indexOf( expectedFocus ) != -1 );
  }
  
  public void testRenderDeselection() throws IOException {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    tree.setSelection( treeItem );
    String treeRef = "wm.findWidgetById( \"" + WidgetUtil.getId( tree ) +"\" )";
    String treeItemRef 
      = "wm.findWidgetById( \"" + WidgetUtil.getId( treeItem ) +"\" )";
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( tree );
    Fixture.markInitialized( treeItem );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    TreeItemLCA tiLCA = new TreeItemLCA();
    tree.deselect( treeItem );
    tiLCA.renderChanges( treeItem );
    String expected = treeRef + ";w.deselectItem( " + treeItemRef + " );";
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( expected ) != -1 );
  }
  
  public void testDontRenderDeselectionOnMaterialize() throws IOException {
    Tree tree = new Tree( shell, SWT.VIRTUAL );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( tree );
    Fixture.markInitialized( treeItem );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    treeItem.getBackground(); // Materialize
    TreeItemLCA tiLCA = new TreeItemLCA();
    tiLCA.renderChanges( treeItem );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "deselectItem" ) == -1 );
  }

  public void testRenderMultiSelection() throws IOException {
    Tree tree = new Tree( shell, SWT.MULTI );
    TreeItem treeItem1 = new TreeItem( tree, SWT.NONE );
    TreeItem treeItem2 = new TreeItem( tree, SWT.NONE );
    String treeItem1Ref 
      = "wm.findWidgetById( \"" + WidgetUtil.getId( treeItem1 ) +"\" )";
    String treeItem2Ref 
      = "wm.findWidgetById( \"" + WidgetUtil.getId( treeItem2 ) +"\" )";
    shell.open();
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( tree );
    Fixture.markInitialized( treeItem1 );
    Fixture.markInitialized( treeItem2 );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    TreeItemLCA tiLCA = new TreeItemLCA();
    tree.selectAll();
    tiLCA.renderChanges( treeItem1 );
    tiLCA.renderChanges( treeItem2 );
    String expectedSelect1 = "w.selectItem( " + treeItem1Ref + " );";
    String expectedSelect2 = "w.selectItem( " + treeItem2Ref + " );";
    String expectedFocus = "w.setFocusItem( " + treeItem1Ref + " );";
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( expectedSelect1 ) != -1 );
    assertTrue( markup.indexOf( expectedFocus ) != -1 );
    assertTrue( markup.indexOf( expectedSelect2 ) != -1 );
  }

  public void testGetBoundsWithScrolling() {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem rootItem = new TreeItem( tree, 0 );
    TreeItem rootItem2 = new TreeItem( tree, 0 );
    TreeItem rootItem3 = new TreeItem( tree, 0 );
    ITreeAdapter treeAdapter = ( ITreeAdapter )tree.getAdapter( ITreeAdapter.class );
    treeAdapter.checkAllData( tree );
    assertEquals( 0, rootItem.getBounds().y );
    assertEquals( 18, rootItem2.getBounds().y );
    assertEquals( 36, rootItem3.getBounds().y );
    Fixture.fakeNewRequest();
    String treeId = WidgetUtil.getId( tree );
    Fixture.fakeRequestParam( treeId + ".scrollLeft", "0" );
    Fixture.fakeRequestParam( treeId + ".topItemIndex", "2" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( -36, rootItem.getBounds().y );
    assertEquals( -18, rootItem2.getBounds().y );
    assertEquals( 0, rootItem3.getBounds().y );
  }

  public void testTextEscape() throws Exception {
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

  public void testInitialization() throws Exception {
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    TreeItem item2 = new TreeItem( item1, SWT.NONE  );
    TreeItem item3 = new TreeItem( item1, SWT.NONE );
    String item1Id = WidgetUtil.getId( item1 );
    String treeId = WidgetUtil.getId( tree );
    TreeItemLCA lca = new TreeItemLCA();
    lca.renderInitialization( item1 );
    lca.renderInitialization( item2 );
    lca.renderInitialization( item3 );
    String expected1 = "new org.eclipse.rwt.widgets.TreeItem( "
                     + "wm.findWidgetById( \"" + treeId + "\" ), 0 );";
    String expected2 = "new org.eclipse.rwt.widgets.TreeItem( "
                     + "wm.findWidgetById( \"" + item1Id + "\" ), 0 );";
    String expected3 = "new org.eclipse.rwt.widgets.TreeItem( "
                     + "wm.findWidgetById( \"" + item1Id + "\" ), 1 );";
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( expected1 ) != -1 );
    assertTrue( markup.indexOf( expected2 ) != -1 );
    assertTrue( markup.indexOf( expected3 ) != -1 );
  }
}
