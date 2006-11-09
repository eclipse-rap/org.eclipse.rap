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

package org.eclipse.rap.rwt.internal.widgets.treeitemkit;

import java.io.IOException;
import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.SelectionListener;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.internal.widgets.treekit.TreeLCA;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;
import com.w4t.util.browser.Ie6;

public class TreeItemLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Tree tree = new Tree( shell, RWT.NONE );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    TreeItem treeItem = new TreeItem( tree, RWT.NONE );
    treeItem.setText( "qwert" );
    TreeItem subTreeItem = new TreeItem( treeItem, RWT.NONE );
    new TreeLCA().preserveValues( tree );
    new TreeItemLCA().preserveValues( treeItem );
    new TreeItemLCA().preserveValues( subTreeItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( treeItem );
    assertEquals( "qwert", adapter.getPreserved( Props.TEXT ) );
  }

  public void testSelectionEvent() throws IOException {
    final Object[] selectedTree = {
      null
    };
    final Object[] selectedTreeItem = {
      null
    };
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Tree tree = new Tree( shell, RWT.NONE );
    tree.addSelectionListener( new SelectionListener() {

      public void widgetSelected( final SelectionEvent event ) {
        selectedTree[ 0 ] = event.getSource();
        selectedTreeItem[ 0 ] = event.item;
      }
    } );
    TreeItem treeItem = new TreeItem( tree, RWT.NONE );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    String treeItemId = WidgetUtil.getId( treeItem );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, treeItemId );
    new RWTLifeCycle().execute();
    assertSame( tree, selectedTree[ 0 ] );
    assertSame( treeItem, selectedTreeItem[ 0 ] );
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
