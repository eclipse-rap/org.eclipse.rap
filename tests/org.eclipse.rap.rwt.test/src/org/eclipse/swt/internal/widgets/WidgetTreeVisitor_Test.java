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

package org.eclipse.swt.internal.widgets;

import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.widgets.*;

public class WidgetTreeVisitor_Test extends TestCase {

  public void testTreeVisitor() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Control control1 = new Button( shell, SWT.PUSH );
    Composite composite = new Composite( shell, SWT.NONE );
    final Control control2 = new Button( composite, SWT.PUSH );
    Control control3 = new Button( composite, SWT.PUSH );
    Tree tree = new Tree( composite, SWT.NONE );
    final TreeItem treeItem1 = new TreeItem( tree, SWT.NONE );
    TreeItem treeItem2 = new TreeItem( tree, SWT.NONE );
    TreeItem subTreeItem1 = new TreeItem( treeItem1, SWT.NONE );
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
    final Shell shell = new Shell( display , SWT.NONE );
    Table table = new Table( shell, SWT.NONE );
    TableItem item1 = new TableItem( table, SWT.NONE );
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    TableItem item2 = new TableItem( table, SWT.NONE );
    TableColumn column2 = new TableColumn( table, SWT.NONE );
    Control tableControl = new Button( table, SWT.PUSH );
    final int[] count = {
      0
    };
    final Object[] elements = new Object[]{
      shell, table, column1, column2, item1, item2, tableControl 
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
    // Ensure that table item are visited in this order: first TableColumn,
    // then TableItem; regardless in which order they were constructed
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {
      public boolean doVisit( final Widget widget ) {
        assertSame( elements[ count[ 0 ] ], widget );
        count[ 0 ]++;
        return true;
      }
    } );
    assertEquals( elements.length, count[ 0 ] );
  }

  public void testTreeVisitorWithToolBar() {
    Display display = new Display();
    final Shell shell = new Shell( display , SWT.NONE );
    ToolBar toolBar = new ToolBar( shell, SWT.NONE );
    ToolItem toolItem = new ToolItem( toolBar, SWT.NONE );
    final int[] count = {
      0
    };
    final Object[] elements = new Object[]{
      shell, toolBar, toolItem
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
    assertEquals( elements.length, count[ 0 ] );
  }

  public void testTreeVisitorWithMenus() {
    Display display = new Display();
    final Shell shell = new Shell( display , SWT.NONE );
    Menu menuBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menuBar );
    Menu shellMenu = new Menu( shell );
    Text text = new Text( shell, SWT.NONE );
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
