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

package org.eclipse.rap.rwt.custom;

import java.util.Arrays;
import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.SelectionListener;
import org.eclipse.rap.rwt.layout.FillLayout;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.engine.lifecycle.PhaseId;


public class CTabFolder_Test extends TestCase {

  public void testHierarchy() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    CTabFolder folder = new CTabFolder( shell, RWT.NONE );
    assertEquals( 0, folder.getItemCount() );
    assertTrue( Arrays.equals( new CTabItem[ 0 ], folder.getItems() ) );

    CTabItem item = new CTabItem( folder, RWT.NONE );
    assertTrue( Composite.class.isAssignableFrom( folder.getClass() ) );
    assertSame( folder, item.getParent() );
    assertSame( display, item.getDisplay() );
    assertEquals( 1, folder.getItemCount() );
    assertSame( item, folder.getItem( 0 ) );
    assertSame( item, folder.getItems()[ 0 ] );
    assertEquals( 0, folder.indexOf( item ) );
    Control control = new Label( folder, RWT.NONE );
    item.setControl( control );
    assertSame( control, item.getControl() );
    try {
      item.setControl( shell );
      fail( "Wrong parent." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
  }

  public void testDispose() {
    final StringBuffer  log = new StringBuffer();
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    CTabFolder folder1 = new CTabFolder( shell, RWT.NONE );
    folder1.addSelectionListener( new SelectionListener() {
      public void widgetSelected( final SelectionEvent event ) {
        log.append( "selectionEvent" );
      }
    } );
    CTabItem item1 = new CTabItem( folder1, RWT.NONE );
    CTabItem item2 = new CTabItem( folder1, RWT.NONE );
    folder1.setSelection( item2 );
    CTabItem item3 = new CTabItem( folder1, RWT.NONE );
    
    item3.dispose();
    assertEquals( true, item3.isDisposed() );
    assertEquals( 2, folder1.getItemCount() );
    assertEquals( -1, folder1.indexOf( item3 ) );
    
    folder1.dispose();
    assertEquals( true, item1.isDisposed() );
    assertEquals( 0, folder1.getItemCount() );
    
    // Ensure that no SelectionEvent is sent when disposing of a CTabFolder
    assertEquals( "", log.toString() );
  }

  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    CTabFolder folder1 = new CTabFolder( shell, RWT.NONE );
    assertEquals( RWT.NONE, folder1.getStyle() );
    
    CTabFolder folder2 = new CTabFolder( shell, -1 );
    assertEquals( RWT.NONE, folder2.getStyle() );
  }
  
  public void testSelectionIndex() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    CTabFolder folder = new CTabFolder( shell, RWT.NONE );
    
    // Test folder without items: initial value must be -1 / null
    assertEquals( -1, folder.getSelectionIndex() );
    assertEquals( null, folder.getSelection() );
    // Setting a selection index out of range must simply be ignored
    folder.setSelection( 2 );
    assertEquals( -1, folder.getSelectionIndex() );
    assertEquals( null, folder.getSelection() );
    folder.setSelection( -2 );
    assertEquals( -1, folder.getSelectionIndex() );
    assertEquals( null, folder.getSelection() );
    folder.setSelection( 0 );
    assertEquals( -1, folder.getSelectionIndex() );
    assertEquals( null, folder.getSelection() );
    
    // Add an item -> must not change selection index
    CTabItem item1 = new CTabItem( folder, RWT.NONE );
    assertEquals( -1, folder.getSelectionIndex() );
    assertEquals( null, folder.getSelection() );
    
    folder.setSelection( 0 );
    assertEquals( 0, folder.getSelectionIndex() );
    assertSame( item1, folder.getSelection() );

    CTabItem item2 = new CTabItem( folder, RWT.NONE );
    folder.setSelection( item2 );
    assertEquals( 1, folder.getSelectionIndex() );
    assertSame( item2, folder.getSelection() );
    
    item1.dispose();
    assertSame( item2, folder.getSelection() );
    assertEquals( 0, folder.getSelectionIndex() );
    
    item2.dispose();
    assertEquals( null, folder.getSelection() );
  }
  
  public void testSelectionWithEvent() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    final CTabFolder folder = new CTabFolder( shell, RWT.NONE );
    final CTabItem item1 = new CTabItem( folder, RWT.NONE );
    CTabItem item2 = new CTabItem( folder, RWT.NONE );
    folder.addSelectionListener( new SelectionListener() {
      public void widgetSelected( final SelectionEvent event ) {
        assertSame( folder, event.getSource() );
        assertSame( item1, event.item );
        assertEquals( 0, event.detail );
        assertEquals( true, event.doit );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        log.append( "widgetSelected" );
      }
    } );
    folder.setSelection( item2 );
    item2.dispose();
    assertEquals( "widgetSelected", log.toString() );
  }
  
  public void testMinimizeMaximize() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    CTabFolder folder = new CTabFolder( shell, RWT.NONE );
    // Test initial state
    assertEquals( false, folder.getMinimized() );
    assertEquals( false, folder.getMaximized() );
    
    // set minimized to the same value it has -> nothing should change
    folder.setMinimized( false );
    assertEquals( false, folder.getMinimized() );
    assertEquals( false, folder.getMaximized() );
    
    // minimize
    folder.setMinimized( true );
    assertEquals( true, folder.getMinimized() );
    assertEquals( false, folder.getMaximized() );
    
    // set maximize to the current value -> nothing should happen
    folder.setMaximized( false );
    assertEquals( true, folder.getMinimized() );
    assertEquals( false, folder.getMaximized() );
    
    // maximize
    folder.setMaximized( true );
    assertEquals( false, folder.getMinimized() );
    assertEquals( true, folder.getMaximized() );
  }
  
  public void testMinMaxVisible() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    CTabFolder folder = new CTabFolder( shell, RWT.NONE );
    // Ensure initial state
    assertEquals( true, folder.getMinimizeVisible() );
    assertEquals( true, folder.getMaximizeVisible() );
    // test getter/setter
    folder.setMinimizeVisible( false );
    assertEquals( false, folder.getMinimizeVisible() );
    folder.setMaximizeVisible( false );
    assertEquals( false, folder.getMinimizeVisible() );
  }
  
  public void testLayout() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    CTabFolder folder = new CTabFolder( shell, RWT.NONE );
    assertEquals( CTabFolderLayout.class, folder.getLayout().getClass() );
    
    folder.setLayout( new FillLayout() );
    assertEquals( CTabFolderLayout.class, folder.getLayout().getClass() );
  }
  
  public void testTabHeight() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    CTabFolder folder = new CTabFolder( shell, RWT.NONE );
    // Test initial value
    assertTrue( folder.getTabHeight() > 0 );
    folder.setTabHeight( 30 );
    assertEquals( 30, folder.getTabHeight() );
    folder.setTabHeight( RWT.DEFAULT );
    assertTrue( folder.getTabHeight() > 0 );
    try {
      folder.setTabHeight( -2 );
      fail( "tabHeight must be DEFAULT or positive value" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testTopRight() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    CTabFolder folder = new CTabFolder( shell, RWT.NONE );
    ToolBar toolBar = new ToolBar( folder, RWT.NONE );
    // Test initial value
    assertEquals( null, folder.getTopRight() );
    // set toolbar
    folder.setTopRight( toolBar );
    assertSame( toolBar, folder.getTopRight() );
    assertEquals( RWT.RIGHT, folder.getTopRightAlignment() );
    folder.setTopRight( toolBar, RWT.FILL );
    assertSame( toolBar, folder.getTopRight() );
    assertEquals( RWT.FILL, folder.getTopRightAlignment() );
    folder.setTopRight( null );
    assertEquals( null, folder.getTopRight() );
    // Test illegal values
    try {
      folder.setTopRight( shell );
      fail( "setTopRight must check for invalid parent" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      folder.setTopRight( toolBar, RWT.LEFT );
      fail( "setTopRight must check for legal alignment values" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
