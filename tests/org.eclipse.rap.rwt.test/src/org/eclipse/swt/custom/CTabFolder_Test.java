/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.custom;

import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.custom.ICTabFolderAdapter;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;


public class CTabFolder_Test extends TestCase {

  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

    assertEquals( false, folder.getMRUVisible() );
    assertEquals( false, folder.getMaximizeVisible() );
    assertEquals( false, folder.getMinimizeVisible() );
    assertEquals( false, folder.getMaximized() );
    assertEquals( false, folder.getMinimized() );
    assertEquals( false, folder.getSingle() );
    assertEquals( SWT.TOP, folder.getTabPosition() );
    assertEquals( null, folder.getToolTipText() );
    assertEquals( 20, folder.getMinimumCharacters() );
    assertEquals( false, folder.getBorderVisible() );
    assertNotNull( folder.getSelectionBackground() );
  }

  public void testHierarchy() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    assertEquals( 0, folder.getItemCount() );
    assertTrue( Arrays.equals( new CTabItem[ 0 ], folder.getItems() ) );

    CTabItem item = new CTabItem( folder, SWT.NONE );
    assertTrue( Composite.class.isAssignableFrom( folder.getClass() ) );
    assertSame( folder, item.getParent() );
    assertSame( display, item.getDisplay() );
    assertEquals( 1, folder.getItemCount() );
    assertSame( item, folder.getItem( 0 ) );
    assertSame( item, folder.getItems()[ 0 ] );
    assertEquals( 0, folder.indexOf( item ) );
    Control control = new Label( folder, SWT.NONE );
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
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final StringBuffer  log = new StringBuffer();
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    CTabFolder folder1 = new CTabFolder( shell, SWT.NONE );
    folder1.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        log.append( "selectionEvent" );
      }
    } );
    CTabItem item1 = new CTabItem( folder1, SWT.NONE );
    CTabItem item2 = new CTabItem( folder1, SWT.NONE );
    folder1.setSelection( item2 );
    CTabItem item3 = new CTabItem( folder1, SWT.NONE );

    item3.dispose();
    assertEquals( true, item3.isDisposed() );
    assertEquals( 2, folder1.getItemCount() );
    assertEquals( -1, folder1.indexOf( item3 ) );

    folder1.dispose();
    assertEquals( true, folder1.isDisposed() );
    assertEquals( true, item1.isDisposed() );
    assertEquals( 0, folder1.getItemCount() );

    // Ensure that no SelectionEvent is sent when disposing of a CTabFolder
    assertEquals( "", log.toString() );
  }

  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder1 = new CTabFolder( shell, SWT.NONE );
    assertEquals( SWT.TOP | SWT.MULTI, folder1.getStyle() );
    assertEquals( SWT.TOP, folder1.getTabPosition() );
    assertEquals( false, folder1.getSingle() );

    CTabFolder folder2 = new CTabFolder( shell, -1 );
    assertTrue( ( folder2.getStyle() & SWT.MULTI ) != 0 );
    assertTrue( ( folder2.getStyle() & SWT.TOP ) != 0 );

    int styles = SWT.TOP | SWT.BOTTOM | SWT.SINGLE | SWT.MULTI;
    CTabFolder folder3 = new CTabFolder( shell, styles );
    assertTrue( ( folder3.getStyle() & SWT.MULTI ) != 0 );
    assertTrue( ( folder3.getStyle() & SWT.TOP ) != 0 );

    styles = SWT.BOTTOM | SWT.SINGLE;
    CTabFolder folder4 = new CTabFolder( shell, styles );
    assertTrue( ( folder4.getStyle() & SWT.SINGLE ) != 0 );
    assertTrue( ( folder4.getStyle() & SWT.BOTTOM ) != 0 );
    assertEquals( SWT.BOTTOM, folder4.getTabPosition() );
    assertEquals( true, folder4.getSingle() );

    CTabFolder folder5 = new CTabFolder( shell, SWT.BORDER );
    assertEquals( true, folder5.getBorderVisible() );

    CTabFolder folder6 = new CTabFolder( shell, SWT.NONE );
    assertEquals( false, folder6.getBorderVisible() );
  }

  public void testSelectionIndex() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );

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
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    assertEquals( -1, folder.getSelectionIndex() );
    assertEquals( null, folder.getSelection() );

    folder.setSelection( 0 );
    assertEquals( 0, folder.getSelectionIndex() );
    assertSame( item1, folder.getSelection() );

    // Test that there is no way to unset a selection
    folder.setSelection( 0 );
    folder.setSelection( -1 );
    assertEquals( 0, folder.getSelectionIndex() );
    assertSame( item1, folder.getSelection() );

    CTabItem item2 = new CTabItem( folder, SWT.NONE );
    folder.setSelection( item2 );
    assertEquals( 1, folder.getSelectionIndex() );
    assertSame( item2, folder.getSelection() );

    item1.dispose();
    assertSame( item2, folder.getSelection() );
    assertEquals( 0, folder.getSelectionIndex() );

    item2.dispose();
    assertEquals( null, folder.getSelection() );
  }

  public void testSelectionWithControl() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.MULTI );
    folder.setSize( 100, 200 );
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    Control control1 = new Label( folder, SWT.NONE );
    item1.setControl( control1 );
    CTabItem item2 = new CTabItem( folder, SWT.NONE );
    Control control2 = new Label( folder, SWT.NONE );
    item2.setControl( control2 );
    CTabItem item3 = new CTabItem( folder, SWT.NONE );
    shell.open();

    folder.setSelection( item1 );
    assertEquals( true, item1.getControl().getVisible() );
    assertEquals( folder.getClientArea(), item1.getControl().getBounds() );

    folder.setSelection( item2 );
    assertEquals( false, item1.getControl().getVisible() );
    assertEquals( true, item2.getControl().getVisible() );
    assertEquals( folder.getClientArea(), item2.getControl().getBounds() );

    folder.setSelection( item3 );
    assertEquals( false, item2.getControl().getVisible() );
  }

  public void testSelectionWithEvent() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    final CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    final CTabItem item1 = new CTabItem( folder, SWT.NONE );
    CTabItem item2 = new CTabItem( folder, SWT.NONE );
    folder.addSelectionListener( new SelectionAdapter() {
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
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
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
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    // test getter/setter
    folder.setMinimizeVisible( false );
    assertEquals( false, folder.getMinimizeVisible() );
    folder.setMaximizeVisible( false );
    assertEquals( false, folder.getMinimizeVisible() );
  }

  public void testResize() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    folder.setMinimizeVisible( true );
    folder.setMaximizeVisible( true );
    new CTabItem( folder, SWT.NONE );
    // set initial size and store position of min/max button
    folder.setSize( 200, 200 );
    shell.layout();
    Object adapter = folder.getAdapter( ICTabFolderAdapter.class );
    ICTabFolderAdapter folderAdapter = ( ICTabFolderAdapter )adapter;
    Rectangle oldMinBounds = folderAdapter.getMinimizeRect();
    Rectangle oldMaxBounds = folderAdapter.getMaximizeRect();
    // resize folder: must move min/max buttons
    folder.setSize( 150, folder.getSize().y );
    Rectangle newMinBounds = folderAdapter.getMinimizeRect();
    Rectangle newMaxBounds = folderAdapter.getMaximizeRect();
    assertTrue( newMinBounds.x < oldMinBounds.x );
    assertTrue( newMaxBounds.x < oldMaxBounds.x );
  }

  public void testLayout() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    assertEquals( CTabFolderLayout.class, folder.getLayout().getClass() );

    folder.setLayout( new FillLayout() );
    assertEquals( CTabFolderLayout.class, folder.getLayout().getClass() );
  }

  public void testTabHeight() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    // Test initial value
    assertTrue( folder.getTabHeight() > 0 );
    folder.setTabHeight( 30 );
    assertEquals( 30, folder.getTabHeight() );
    folder.setTabHeight( SWT.DEFAULT );
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
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    ToolBar toolBar = new ToolBar( folder, SWT.NONE );
    // Test initial value
    assertEquals( null, folder.getTopRight() );
    // set toolbar
    folder.setTopRight( toolBar );
    assertSame( toolBar, folder.getTopRight() );
    folder.setTopRight( toolBar, SWT.FILL );
    assertSame( toolBar, folder.getTopRight() );
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
      folder.setTopRight( toolBar, SWT.LEFT );
      fail( "setTopRight must check for legal alignment values" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // Set invisible topRight control
    ToolBar invisibleToolBar = new ToolBar( folder, SWT.NONE );
    invisibleToolBar.setVisible( false );
    folder.setTopRight( invisibleToolBar );
    assertEquals( false, invisibleToolBar.isVisible() );
  }

  public void testSelectionForegroundAndBackground() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.MULTI );

    // Set some background color
    Color red = display.getSystemColor( SWT.COLOR_RED );
    folder.setSelectionBackground( red );
    assertEquals( red, folder.getSelectionBackground() );

    // Reset to background to default (pass null as parameter)
    folder.setSelectionBackground( null );
    assertNotNull( folder.getSelectionBackground() );

    // Set some foreground color
    Color white = display.getSystemColor( SWT.COLOR_WHITE );
    folder.setSelectionForeground( white );
    assertEquals( white, folder.getSelectionForeground() );

    // Reset to foreground to default (pass null as parameter)
    folder.setSelectionForeground( null );
    assertNotNull( folder.getSelectionForeground() );
  }

  public void testChevronVisibilityWithSingleStyle() {
    RWTFixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    shell.setSize( 150, 150 );
    CTabFolder folder = new CTabFolder( shell, SWT.SINGLE | SWT.CLOSE | SWT.BORDER );
    folder.setSize( 100, 100 );
    folder.addCTabFolder2Listener( new CTabFolder2Adapter() );
    shell.layout();

    // Chevron must be visible when there are no items
    assertEquals( 0, folder.getItemCount() );  // ensure test condition
    assertEquals( true, getChevronVisible( folder ) );

    // Behave as SWT does even if it may be a bug
    // Chevron is visible but its bounds are zero if there is only *one* item
    // wich is *selected*
    CTabItem item = new CTabItem( folder, SWT.NONE );
    item.setText( "item" );
    Label label = new Label( folder, SWT.NONE );
    item.setControl( label );
    folder.setSelection( item );
    assertSame( item, folder.getSelection() );
    assertEquals( 1, folder.getItemCount() );
    assertEquals( true, getChevronVisible( folder ) );
    assertEquals( new Rectangle( 0, 0, 0, 0 ), getChevronRect( folder ) );

    // Chevron must again be visible after the last item was removed
    item.dispose();
    label.dispose();
    assertEquals( 0, folder.getItemCount() );
    assertEquals( true, getChevronVisible( folder ) );

    // Chevron must be visible when there is more than one item regardless of
    // selection
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    CTabItem item2 = new CTabItem( folder, SWT.NONE );
    assertTrue( folder.getItemCount() > 1 ); // test precondition
    assertEquals( null, folder.getSelection() ); // test precondition
    assertEquals( true, getChevronVisible( folder ) );
    folder.setSelection( 0 );
    assertEquals( 0, folder.getSelectionIndex() );  // test precondition
    assertEquals( true, getChevronVisible( folder ) );
    // Clean up
    item1.dispose();
    item2.dispose();
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }

  private static Rectangle getChevronRect( final CTabFolder folder ) {
    Object adapter = folder.getAdapter( ICTabFolderAdapter.class );
    ICTabFolderAdapter folderAdapter = ( ICTabFolderAdapter )adapter;
    return folderAdapter.getChevronRect();
  }

  private static boolean getChevronVisible( final CTabFolder folder ) {
    Object adapter = folder.getAdapter( ICTabFolderAdapter.class );
    ICTabFolderAdapter folderAdapter = ( ICTabFolderAdapter )adapter;
    return folderAdapter.getChevronVisible();
  }
}
