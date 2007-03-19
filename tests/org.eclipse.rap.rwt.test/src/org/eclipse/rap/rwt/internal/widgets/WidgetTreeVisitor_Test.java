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

package org.eclipse.rap.rwt.internal.widgets;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.rap.rwt.widgets.*;

public class WidgetTreeVisitor_Test extends TestCase {

  public void testTreeVisitor() {
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Control control1 = new Button( shell, RWT.PUSH );
    Composite composite = new Composite( shell, RWT.NONE );
    final Control control2 = new Button( composite, RWT.PUSH );
    Control control3 = new Button( composite, RWT.PUSH );
    Tree tree = new Tree( composite, RWT.NONE );
    final TreeItem treeItem1 = new TreeItem( tree, RWT.NONE );
    TreeItem treeItem2 = new TreeItem( tree, RWT.NONE );
    TreeItem subTreeItem1 = new TreeItem( treeItem1, RWT.NONE );
    final Object[] elements = new Object[]{
      shell,
      control1,
      composite,
      control2,
      control3,
      tree,
      treeItem1,
      subTreeItem1,
      treeItem2
    };
    final int[] count = {
      0
    };
    WidgetTreeVisitor.accept( shell, new WidgetTreeVisitor() {

      public boolean visit( final Widget widget ) {
        assertSame( widget, elements[ count[ 0 ] ] );
        count[ 0 ]++;
        return true;
      }

      public boolean visit( final Composite composite ) {
        assertSame( composite, elements[ count[ 0 ] ] );
        count[ 0 ]++;
        return true;
      }
    } );
    assertEquals( 9, count[ 0 ] );
    count[ 0 ] = 0;
    WidgetTreeVisitor.accept( shell, new WidgetTreeVisitor() {

      public boolean visit( final Composite composite ) {
        count[ 0 ]++;
        return false;
      }
    } );
    assertEquals( 1, count[ 0 ] );
    count[ 0 ] = 0;
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {

      public boolean doVisit( final Widget widget ) {
        assertSame( widget, elements[ count[ 0 ] ] );
        count[ 0 ]++;
        return true;
      }
    } );
    assertEquals( 9, count[ 0 ] );
    count[ 0 ] = 0;
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {

      public boolean doVisit( final Widget widget ) {
        count[ 0 ]++;
        return widget != treeItem1;
      }
    } );
    assertEquals( 8, count[ 0 ] );
  }

  public void testTreeVisitorWithTable() {
    Display display = new Display();
    final Shell shell = new Shell( display , RWT.NONE );
    Table table = new Table( shell, RWT.NONE );
    TableItem tableItem = new TableItem( table, RWT.NONE );
    TableColumn tableColumn = new TableColumn( table, RWT.NONE );
    final int[] count = {
      0
    };
    // Ensure that regards in which order columns and or items are created
    // the order is first column then items
    final Object[] elements = new Object[]{
      shell, table, tableColumn, tableItem
    };
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {
      public boolean doVisit( final Widget widget ) {
        assertSame( widget, elements[ count[ 0 ] ] );
        count[ 0 ]++;
        return widget != shell;
      }
    } );
    assertEquals( 1, count[ 0 ] );
    count[ 0 ] = 0;
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {
      public boolean doVisit( final Widget widget ) {
        assertSame( elements[ count[ 0 ] ], widget );
        count[ 0 ]++;
        return true;
      }
    } );
    assertEquals( 4, count[ 0 ] );
  }

  public void testTreeVisitorWithMenus() {
    Display display = new Display();
    final Shell shell = new Shell( display , RWT.NONE );
    Menu menuBar = new Menu( shell, RWT.BAR );
    shell.setMenuBar( menuBar );
    Menu shellMenu = new Menu( shell );
    Text text = new Text( shell, RWT.NONE );
    Menu textMenu = new Menu( text );
    final int[] count = {
      0
    };
    final Object[] elements = new Object[]{
      shell, menuBar, shellMenu, textMenu, text
    };
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {

      public boolean doVisit( final Widget widget ) {
        assertSame( widget, elements[ count[ 0 ] ] );
        count[ 0 ]++;
        return widget != shell;
      }
    } );
    assertEquals( 1, count[ 0 ] );
    count[ 0 ] = 0;
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {

      public boolean doVisit( final Widget widget ) {
        assertSame( elements[ count[ 0 ] ], widget );
        count[ 0 ]++;
        return true;
      }
    } );
    assertEquals( 5, count[ 0 ] );
  }

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
