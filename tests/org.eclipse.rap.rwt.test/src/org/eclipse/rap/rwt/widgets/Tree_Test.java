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

package org.eclipse.rap.rwt.widgets;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.graphics.Image_Test;

public class Tree_Test extends TestCase {

  public void testGetItemsAndGetItemCount() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Tree tree = new Tree( shell, RWT.NONE );
    assertEquals( 0, tree.getItemCount() );
    assertEquals( 0, tree.getItems().length );
    TreeItem item = new TreeItem( tree, RWT.NONE );
    assertEquals( 1, tree.getItemCount() );
    assertEquals( 1, tree.getItems().length );
    assertSame( item, tree.getItems()[ 0 ] );
  }
  
  public void testImage() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Tree tree = new Tree( shell, RWT.NONE );
    assertEquals( 0, Image.size() );
    TreeItem item1 = new TreeItem( tree, RWT.NONE );
    item1.setImage(Image.find( Image_Test.IMAGE1 ) );
    assertSame( Image.find( Image_Test.IMAGE1 ), item1.getImage() );
    assertEquals( 1, Image.size() );
    TreeItem item2 = new TreeItem( tree, RWT.NONE );
    item2.setImage(Image.find( Image_Test.IMAGE2 ) );
    assertSame( Image.find( Image_Test.IMAGE2 ), item2.getImage() );
    assertEquals( 2, Image.size() );
  }

  public void testItemHierarchy() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Tree tree = new Tree( shell, RWT.NONE );
    TreeItem item = new TreeItem( tree, RWT.NONE );
    TreeItem subItem = new TreeItem( item, RWT.NONE );
    assertEquals( 1, item.getItems().length );
    assertEquals( null, item.getParentItem() );
    assertEquals( tree, item.getParent() );
    assertEquals( subItem, item.getItems()[ 0 ] );
    assertEquals( tree, subItem.getParent() );
    assertEquals( item, subItem.getParentItem() );
    assertEquals( 0, subItem.getItems().length );
  }

  public void testDispose() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Tree tree = new Tree( shell, RWT.NONE );
    TreeItem item = new TreeItem( tree, RWT.NONE );
    TreeItem subItem = new TreeItem( item, RWT.NONE );
    tree.dispose();
    assertEquals( true, item.isDisposed() );
    assertEquals( true, subItem.isDisposed() );
    assertEquals( 0, tree.getItemCount() );
    assertEquals( 0, item.getItemCount() );
    assertEquals( 0, subItem.getItemCount() );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
