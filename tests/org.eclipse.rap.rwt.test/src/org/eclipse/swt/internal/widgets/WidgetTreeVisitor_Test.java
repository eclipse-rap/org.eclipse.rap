/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class WidgetTreeVisitor_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display , SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testTreeVisitor() {
    Control control1 = new Button( shell, SWT.PUSH );
    Composite composite = new Composite( shell, SWT.NONE );
    final Control control2 = new Button( composite, SWT.PUSH );
    Control control3 = new Button( composite, SWT.PUSH );
    Tree tree = new Tree( composite, SWT.NONE );
    TreeColumn treeColumn = new TreeColumn( tree, SWT.NONE );
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
      treeColumn,
      treeItem1,
      subTreeItem1,
      treeItem2
    };
    final int[] count = { 0 };
    WidgetTreeVisitor.accept( shell, new WidgetTreeVisitor() {
      @Override
      public boolean visit( Widget widget ) {
        assertSame( widget, elements[ count[ 0 ] ] );
        count[ 0 ]++;
        return true;
      }
      @Override
      public boolean visit( Composite composite ) {
        assertSame( composite, elements[ count[ 0 ] ] );
        count[ 0 ]++;
        return true;
      }
    } );
    assertEquals( 10, count[ 0 ] );
    count[ 0 ] = 0;
    WidgetTreeVisitor.accept( shell, new WidgetTreeVisitor() {
      @Override
      public boolean visit( Composite composite ) {
        count[ 0 ]++;
        return false;
      }
    } );
    assertEquals( 1, count[ 0 ] );
    count[ 0 ] = 0;
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {
      @Override
      public boolean doVisit( Widget widget ) {
        assertSame( widget, elements[ count[ 0 ] ] );
        count[ 0 ]++;
        return true;
      }
    } );
    assertEquals( 10, count[ 0 ] );
    count[ 0 ] = 0;
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {
      @Override
      public boolean doVisit( Widget widget ) {
        count[ 0 ]++;
        return widget != treeItem1;
      }
    } );
    assertEquals( 9, count[ 0 ] );
  }

  @Test
  public void testTreeVisitorWithTable() {
    Table table = new Table( shell, SWT.NONE );
    TableItem item1 = new TableItem( table, SWT.NONE );
    TableColumn column1 = new TableColumn( table, SWT.NONE );
    TableItem item2 = new TableItem( table, SWT.NONE );
    TableColumn column2 = new TableColumn( table, SWT.NONE );
    Control tableControl = new Button( table, SWT.PUSH );
    final int[] count = { 0 };
    final Object[] elements = new Object[]{
      shell, table, column1, column2, item1, item2, tableControl
    };
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {
      @Override
      public boolean doVisit( Widget widget ) {
        assertSame( elements[ count[ 0 ] ], widget );
        count[ 0 ]++;
        return widget != shell;
      }
    } );
    assertEquals( 1, count[ 0 ] );
    count[ 0 ] = 0;
    // Ensure that table item are visited in this order: first TableColumn,
    // then TableItem; regardless in which order they were constructed
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {
      @Override
      public boolean doVisit( Widget widget ) {
        assertSame( elements[ count[ 0 ] ], widget );
        count[ 0 ]++;
        return true;
      }
    } );
    assertEquals( elements.length, count[ 0 ] );
  }

  @Test
  public void testTreeVisitorWithToolBar() {
    ToolBar toolBar = new ToolBar( shell, SWT.NONE );
    ToolItem toolItem = new ToolItem( toolBar, SWT.NONE );
    final int[] count = {
      0
    };
    final Object[] elements = new Object[]{
      shell, toolBar, toolItem
    };
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {
      @Override
      public boolean doVisit( Widget widget ) {
        assertSame( widget, elements[ count[ 0 ] ] );
        count[ 0 ]++;
        return widget != shell;
      }
    } );
    assertEquals( 1, count[ 0 ] );
    count[ 0 ] = 0;
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {
      @Override
      public boolean doVisit( Widget widget ) {
        assertSame( elements[ count[ 0 ] ], widget );
        count[ 0 ]++;
        return true;
      }
    } );
    assertEquals( elements.length, count[ 0 ] );
  }

  @Test
  public void testTreeVisitorWithMenus() {
    Menu menuBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menuBar );
    Menu shellMenu = new Menu( shell );
    Text text = new Text( shell, SWT.NONE );
    Menu textMenu = new Menu( text );
    final int[] count = { 0 };
    final Object[] elements = new Object[]{
      shell, menuBar, shellMenu, textMenu, text
    };
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {
      @Override
      public boolean doVisit( Widget widget ) {
        assertSame( widget, elements[ count[ 0 ] ] );
        count[ 0 ]++;
        return widget != shell;
      }
    } );
    assertEquals( 1, count[ 0 ] );
    count[ 0 ] = 0;
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {
      @Override
      public boolean doVisit( Widget widget ) {
        assertSame( elements[ count[ 0 ] ], widget );
        count[ 0 ]++;
        return true;
      }
    } );
    assertEquals( 5, count[ 0 ] );
  }

  @Test
  public void testTreeVisitorWithDecoration() {
    Control control1 = new Button( shell, SWT.PUSH );
    Decorator decoration1 = new Decorator( control1, SWT.RIGHT );
    Composite composite = new Composite( shell, SWT.NONE );
    Control control2 = new Button( composite, SWT.PUSH );
    Decorator decoration2 = new Decorator( control2, SWT.RIGHT );
    final int[] count = { 0 };
    final Object[] elements = new Object[]{
      shell, control1, decoration1, composite, control2, decoration2
    };
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {
      @Override
      public boolean doVisit( Widget widget ) {
        assertSame( widget, elements[ count[ 0 ] ] );
        count[ 0 ]++;
        return widget != shell;
      }
    } );
    assertEquals( 1, count[ 0 ] );
    count[ 0 ] = 0;
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {
      @Override
      public boolean doVisit( Widget widget ) {
        assertSame( widget, elements[ count[ 0 ] ] );
        count[ 0 ]++;
        return true;
      }
    } );
    assertEquals( 6, count[ 0 ] );
  }

  @Test
  public void testTreeVisitorWithDragSource() {
    DragSource compositeDragSource = new DragSource( shell, SWT.NONE );
    Text text = new Text( shell, SWT.NONE );
    DragSource controlDragSource = new DragSource( text, SWT.NONE );
    final int[] count = { 0 };
    final Object[] elements = new Object[]{
      shell, compositeDragSource, text, controlDragSource
    };
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {
      @Override
      public boolean doVisit( Widget widget ) {
        assertSame( elements[ count[ 0 ] ], widget );
        count[ 0 ]++;
        return true;
      }
    } );
    assertEquals( 4, count[ 0 ] );
  }

  @Test
  public void testTreeVisitorWithToolTip() {
    Control control = new Label( shell, SWT.NONE );
    ToolTip toolTip = new ToolTip( shell, SWT.NONE );
    final int[] count = { 0 };
    final Object[] elements = new Object[]{
      shell, control, toolTip
    };
    WidgetTreeVisitor.accept( shell, new AllWidgetTreeVisitor() {
      @Override
      public boolean doVisit( Widget widget ) {
        assertSame( elements[ count[ 0 ] ], widget );
        count[ 0 ]++;
        return true;
      }
    } );

    assertEquals( 3, count[ 0 ] );
  }

  @Test
  public void testWithCustomWidget() {
    // Custom widgets may override getChildren, see bug 363844
    Composite customWidget = new Composite( shell, SWT.NONE ) {
      @Override
      public Control[] getChildren() {
        return new Control[ 0 ];
      }
    };
    Control innerLabel = new Label( customWidget, SWT.NONE );

    final List<Widget> log = new ArrayList<Widget>();
    WidgetTreeVisitor.accept( customWidget, new AllWidgetTreeVisitor() {
      @Override
      public boolean doVisit( Widget widget ) {
        log.add( widget );
        return true;
      }
    } );

    assertTrue( log.contains( customWidget ) );
    assertTrue( log.contains( innerLabel ) );
  }

}
