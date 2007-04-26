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

package org.eclipse.swt.internal.widgets.treeitemkit;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.treekit.TreeLCA;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;
import com.w4t.util.browser.Ie6;

public class TreeItemLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    treeItem.setText( "qwert" );
    TreeItem subTreeItem = new TreeItem( treeItem, SWT.NONE );
    treeItem.setExpanded( true );
    new TreeLCA().preserveValues( tree );
    new TreeItemLCA().preserveValues( treeItem );
    new TreeItemLCA().preserveValues( subTreeItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( treeItem );
    assertEquals( "qwert", adapter.getPreserved( Props.TEXT ) );
    assertEquals( Boolean.TRUE, 
                  adapter.getPreserved( TreeItemLCA.PROP_EXPANDED ) );
  }
    
  public void testTreeEvent() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
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
    Composite shell = new Shell( display , SWT.NONE );
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
  
  public void testChecked() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.CHECK );
    TreeItem treeItem = new TreeItem( tree, SWT.NONE );
    
    String displayId = DisplayUtil.getId( display );
    String treeItemId = WidgetUtil.getId( treeItem );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( treeItemId + ".checked", "true" );
    new RWTLifeCycle().execute();
    assertEquals( true, treeItem.getChecked() );
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
