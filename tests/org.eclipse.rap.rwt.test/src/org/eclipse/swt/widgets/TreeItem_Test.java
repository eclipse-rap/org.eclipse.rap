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

package org.eclipse.swt.widgets;

import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;

public class TreeItem_Test extends TestCase {

  public void testConstructor() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    assertSame( display, item.getDisplay() );
    assertEquals( "", item.getText() );
    assertSame( item, tree.getItem( tree.getItemCount() - 1 ) );
    try {
      new TreeItem( ( TreeItem )null, SWT.NONE );
      fail( "Must not allow null-parent" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      new TreeItem( ( Tree )null, SWT.NONE );
      fail( "Must not allow null-parent" );
    } catch( NullPointerException e ) {
      // expected
    }
  }
  
  public void testRemoveAll() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item1 = new TreeItem( tree, SWT.NONE );
    TreeItem item11 = new TreeItem( item1, SWT.NONE );
    TreeItem item111 = new TreeItem( item11, SWT.NONE );
    TreeItem item2 = new TreeItem( tree, SWT.NONE );
    item1.removeAll();
    assertEquals( false, item1.isDisposed() );
    assertEquals( true, item11.isDisposed() );
    assertEquals( true, item111.isDisposed() );
    assertEquals( 0, item1.getItemCount() );
    assertEquals( false, item2.isDisposed() );
  }
  
  public void testFont() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    Font treeFont = Font.getFont( "BeautifullyCraftedTreeFont", 15, SWT.BOLD );
    tree.setFont( treeFont );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    
    assertSame( treeFont, item.getFont() );
    
    Font itemFont = Font.getFont( "ItemFont", 40, SWT.NORMAL );
    item.setFont( itemFont );
    assertSame( itemFont, item.getFont() );
    
    item.setFont( null );
    assertSame( treeFont, item.getFont() );
  }
  
  public void testChecked() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Tree tree = new Tree( shell, SWT.NONE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    Tree checkedTree = new Tree( shell, SWT.CHECK );
    TreeItem checkedItem = new TreeItem( checkedTree, SWT.NONE );
    // Ensure that checked-property on a treeItem cannot be changed when tree 
    // is missing CHECK style
    assertEquals( false, item.getChecked() );
    item.setChecked( true );
    assertEquals( false, item.getChecked() );
    // The check-property for a treeItem on a tree with style CHECK may be
    // changed
    assertEquals( false, checkedItem.getChecked() );
    checkedItem.setChecked( true );
    assertEquals( true, checkedItem.getChecked() );
  }

  public void testBackgroundColor() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.SINGLE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    // initial background color should match the parents one
    assertEquals( tree.getBackground(),  item.getBackground());
    // change the colors
    Color green = Color.getColor( 0, 255, 0 );
    item.setBackground( green );
    assertEquals( green, item.getBackground() );
  }
  
  public void testForegroundColor() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.SINGLE );
    TreeItem item = new TreeItem( tree, SWT.NONE );
    // initial foreground color should match the parents one
    assertEquals( tree.getForeground(),  item.getForeground());
    // change the colors
    Color green = Color.getColor( 0, 255, 0 );
    item.setForeground( green );
    assertEquals( green, item.getForeground() );
  }
  
  public void testClear() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.SINGLE | SWT.CHECK );
    TreeItem item = new TreeItem( tree, SWT.NONE );

    assertEquals( "", item.getText() );
    assertEquals( null, item.getImage() );
    assertEquals( Boolean.FALSE, Boolean.valueOf( item.getChecked() ) );

    item.setText( "foo" );
    item.setImage( Image.find( RWTFixture.IMAGE1 ) );
    item.setChecked( true );

    assertEquals( "foo", item.getText() );
    assertEquals( Image.find( RWTFixture.IMAGE1 ), item.getImage() );
    assertEquals( Boolean.TRUE, Boolean.valueOf( item.getChecked() ) );

    item.clear();

    assertEquals( "", item.getText() );
    assertEquals( null, item.getImage() );
    assertEquals( Boolean.FALSE, Boolean.valueOf( item.getChecked() ) );
  }
  
  public void testClearAll() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Tree tree = new Tree( shell, SWT.SINGLE | SWT.CHECK );
    TreeItem root = new TreeItem( tree, SWT.NONE );
    
    for (int i=0; i<2; i++) {
      TreeItem item0 = new TreeItem (root, 0);
      item0.setText ("Item " + i);
      for (int j=0; j<2; j++) {
        TreeItem item1 = new TreeItem (item0, 0);
        item1.setText ("Item " + i + " " + j);
        for (int k=0; k<2; k++) {
          TreeItem item2 = new TreeItem (item1, 0);
          item2.setText ("Item " + i + " " + j + " " + k);
        } 
      }
    }
    
    for (int i=0; i<2; i++) {
      TreeItem item0 = root.getItem( i );
      assertEquals( "Item " + i, item0.getText() );
      for (int j=0; j<2; j++) {
        TreeItem item1 = item0.getItem( j );
        assertEquals( "Item " + i + " " + j, item1.getText() );
        for (int k=0; k<2; k++) {
          TreeItem item2 = item1.getItem( k );
          assertEquals( "Item " + i + " " + j + " " + k, item2.getText() );
        } 
      }
    }

    root.clearAll( false );

    for (int i=0; i<2; i++) {
      TreeItem item0 = root.getItem( i );
      assertEquals( "", item0.getText() );
      for (int j=0; j<2; j++) {
        TreeItem item1 = item0.getItem( j );
        assertEquals( "Item " + i + " " + j, item1.getText() );
        for (int k=0; k<2; k++) {
          TreeItem item2 = item1.getItem( k );
          assertEquals( "Item " + i + " " + j + " " + k, item2.getText() );
        } 
      }
    }
    
    root.clearAll( true );

    for (int i=0; i<2; i++) {
      TreeItem item0 = root.getItem( i );
      assertEquals( "", item0.getText() );
      for (int j=0; j<2; j++) {
        TreeItem item1 = item0.getItem( j );
        assertEquals( "", item1.getText() );
        for (int k=0; k<2; k++) {
          TreeItem item2 = item1.getItem( k );
          assertEquals( "", item2.getText() );
        } 
      }
    }    
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
