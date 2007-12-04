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
package org.eclipse.swt.custom;

import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.custom.ICTabFolderAdapter;
import org.eclipse.swt.widgets.*;


public class CTabItem_Test extends TestCase {

  public void testCreation() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.MULTI );
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    CTabItem item = new CTabItem( folder, SWT.NONE );
    assertEquals( null, folder.getSelection() );
    assertSame( folder, item.getParent() );
    assertSame( display, item.getDisplay() );
  }
  
  public void testInitialState() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    CTabItem item = new CTabItem( folder, SWT.NONE );

    assertEquals( null, item.getToolTipText() );
    assertEquals( "", item.getText() );
    assertEquals( null, item.getControl() );
    assertEquals( null, item.getImage() );
  }
  
  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    assertEquals( SWT.NONE, item1.getStyle() );
    
    CTabItem item2 = new CTabItem( folder, SWT.LEFT );
    assertEquals( SWT.NONE, item2.getStyle() );
    
    // TODO [rh] Different from SWT: SWT doesn't return CLOSE even though it was
    //      set in constructor. SWT currently relies on the behavior tested 
    //      below to calulate the width of a CTabItem 
    CTabItem item3 = new CTabItem( folder, SWT.CLOSE );
    assertTrue( ( item3.getStyle() & SWT.CLOSE ) != 0 );
  }
  
  public void testBounds() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.MULTI | SWT.TOP );
    folder.setSize( 150, 80 );
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    shell.layout();
    
    assertTrue( item1.getBounds().width > 0 );
    assertTrue( item1.getBounds().height > 0 );
    
    CTabItem item2 = new CTabItem( folder, SWT.NONE );
    assertTrue( item1.getBounds().width > 0 );
    assertTrue( item1.getBounds().height > 0 );
    assertTrue( item2.getBounds().width > 0 );
    assertTrue( item2.getBounds().height > 0 );
    int item1Right = item1.getBounds().x + item1.getBounds().width;
    assertTrue( item2.getBounds().x >= item1Right );
  }
  
  public void testDisplay() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.NONE );
    CTabItem item = new CTabItem( folder, SWT.NONE );
    assertSame( display, item.getDisplay() );
    assertSame( folder.getDisplay(), item.getDisplay() );
  }

  public void testSetControl() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    CTabFolder folder = new CTabFolder( shell, SWT.MULTI | SWT.TOP );
    folder.setSize( 80, 50 );
    CTabItem item1 = new CTabItem( folder, SWT.NONE );
    Control item1Control = new Label( folder, SWT.NONE );
    item1Control.setSize( 1, 1 );
    shell.open();
    
    // Set control for unselected item
    folder.setSelection( -1 );
    item1.setControl( item1Control );
    assertSame( item1Control, item1.getControl() );
    assertEquals( false, item1Control.getVisible() );
    assertEquals( new Point( 1, 1 ), item1Control.getSize() );
    
    // Reset control: must set its visibility to false
    item1Control.setVisible( true );
    item1.setControl( null );
    assertEquals( null, item1.getControl() );
    assertEquals( false, item1Control.getVisible() );
    
    // Set control for selected item
    CTabItem item2 = new CTabItem( folder, SWT.NONE );
    Control item2Control = new Label( folder, SWT.NONE );
    folder.setSelection( 1 );
    item2.setControl( item2Control );
    assertSame( item2Control, item2.getControl() );
    assertEquals( true, item2Control.getVisible() );
    assertEquals( folder.getClientArea(), item2Control.getBounds() );
    
    // Try to set disposed of control 
    try {
      Control control = new Label( folder, SWT.NONE );
      control.dispose();
      CTabItem item = new CTabItem( folder, SWT.NONE );
      item.setControl( control );
      fail( "setControl must not accept disposed of controls" );
    } catch( IllegalArgumentException e ) {
      // expected
    }

    // Try to set control with wrong parent 
    try {
      Control control = new Label( shell, SWT.NONE );
      CTabItem item = new CTabItem( folder, SWT.NONE );
      item.setControl( control );
      String msg 
        = "setControl must only accept controls whose parent is the folder";
      fail( msg );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testShowClose() {
    CTabFolder folder;
    CTabItem item;
    ICTabFolderAdapter adapter;
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    // Test with folder that was created with SWT.CLOSE
    folder = new CTabFolder( shell, SWT.CLOSE );
    adapter 
      = ( ICTabFolderAdapter )folder.getAdapter( ICTabFolderAdapter.class );
    item = new CTabItem( folder, SWT.NONE );
    assertTrue( adapter.showItemClose( item ) );
    item = new CTabItem( folder, SWT.CLOSE );
    assertTrue( adapter.showItemClose( item ) );
    // Test with folder that was created without SWT.CLOSE
    folder = new CTabFolder( shell, SWT.NONE );
    adapter 
      = ( ICTabFolderAdapter )folder.getAdapter( ICTabFolderAdapter.class );
    item = new CTabItem( folder, SWT.NONE );
    assertFalse( adapter.showItemClose( item ) );
    item = new CTabItem( folder, SWT.CLOSE );
    assertTrue( adapter.showItemClose( item ) );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
