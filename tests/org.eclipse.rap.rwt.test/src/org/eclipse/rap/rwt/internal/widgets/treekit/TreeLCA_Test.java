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

package org.eclipse.rap.rwt.internal.widgets.treekit;

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
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.Fixture;
import com.w4t.engine.requests.RequestParams;
import com.w4t.util.browser.Ie6;

public class TreeLCA_Test extends TestCase {

  public void testPreserveValues() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Tree tree = new Tree( shell, RWT.NONE );
    tree.addSelectionListener( new SelectionListener() {
      public void widgetSelected( final SelectionEvent event ) {
      }
    } );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    new TreeItem( tree, RWT.NONE );
    new TreeLCA().preserveValues( tree );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( tree );
    Rectangle bounds = ( Rectangle )adapter.getPreserved( Props.BOUNDS );
    assertEquals( tree.getBounds(), bounds );
    IWidgetAdapter treeAdapter = WidgetUtil.getAdapter( tree );
    Object hasListeners = treeAdapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
  }

  public void testSelectionEvent() throws IOException {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    final Tree tree = new Tree( shell, RWT.NONE );
    final TreeItem treeItem = new TreeItem( tree, RWT.NONE );
    tree.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    tree.addSelectionListener( new SelectionListener() {

      public void widgetSelected( final SelectionEvent event ) {
        log.append( "itemSelected" );
        assertEquals( tree, event.getSource() );
        assertEquals( treeItem, event.item );
        assertEquals( true, event.doit );
        // ensure same behaviour as SWT: bounds are undefined in tree selection
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
      }
    } );
    String treeItemId = WidgetUtil.getId( treeItem );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, treeItemId );
    new RWTLifeCycle().execute();
    assertEquals( "itemSelected", log.toString() );
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
