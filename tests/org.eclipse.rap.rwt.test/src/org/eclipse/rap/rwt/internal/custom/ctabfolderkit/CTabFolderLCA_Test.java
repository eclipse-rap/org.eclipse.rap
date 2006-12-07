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

package org.eclipse.rap.rwt.internal.custom.ctabfolderkit;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.custom.CTabFolder;
import org.eclipse.rap.rwt.custom.CTabItem;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.SelectionListener;
import org.eclipse.rap.rwt.internal.custom.ctabfolderkit.CTabFolderLCA;
import org.eclipse.rap.rwt.internal.custom.ctabitemkit.CTabItemLCA;
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.ILifeCycleAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.widgets.*;


public class CTabFolderLCA_Test extends TestCase {
  
  public void testLCA() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    CTabFolder folder = new CTabFolder( shell, RWT.NONE );
    CTabItem item = new CTabItem( folder, RWT.NONE );
    
    assertSame( CTabFolderLCA.class,
                folder.getAdapter( ILifeCycleAdapter.class ).getClass() );
    assertSame( CTabItemLCA.class,
                item.getAdapter( ILifeCycleAdapter.class ).getClass() );
  }
  
  public void testPreserveValues() {
    SelectionListener selectionListener = new SelectionListener() {
      public void widgetSelected( final SelectionEvent event ) {
      }
    };
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    CTabFolder folder = new CTabFolder( shell, RWT.NONE );
    Label label = new Label( folder, RWT.NONE );
    folder.setTopRight( label, RWT.FILL );
    RWTFixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( folder );
    Boolean hasListeners;
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Object selectionIndex = adapter.getPreserved( Props.SELECTION_INDEX );
    assertEquals( new Integer( folder.getSelectionIndex() ), selectionIndex );
    Object width = adapter.getPreserved( "width" );
    assertEquals( new Integer( folder.getBounds().width ), width );
    Object minVisible = adapter.getPreserved( Props.MINIMIZE_VISIBLE );
    assertEquals( Boolean.valueOf( folder.getMinimizeVisible() ), minVisible );
    Object maxVisible = adapter.getPreserved( Props.MAXIMIZE_VISIBLE );
    assertEquals( Boolean.valueOf( folder.getMaximizeVisible() ), maxVisible );
    Object tabHeight = adapter.getPreserved( Props.TAB_HEIGHT );
    assertEquals( new Integer( folder.getTabHeight() ), tabHeight );
    Object topRight = adapter.getPreserved( Props.TOP_RIGHT );
    assertSame( topRight, folder.getTopRight() );
    Object minimized = adapter.getPreserved( Props.MINIMIZED );
    assertEquals( Boolean.valueOf( folder.getMinimized() ), minimized );
    Object maximized = adapter.getPreserved( Props.MAXIMIZED );
    assertEquals( Boolean.valueOf( folder.getMaximized() ), maximized );
    Object topRightAlign = adapter.getPreserved( Props.TOP_RIGHT_ALIGNMENT );
    assertEquals( topRightAlign, new Integer( folder.getTopRightAlignment() ) );
    RWTFixture.clearPreserved();
    folder.addSelectionListener( selectionListener );
    RWTFixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( folder );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
  }
  
//  public void testMinimizeMaximize() {
//    fail( "Missing test case for reading min/max changes in readData" );
//  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
