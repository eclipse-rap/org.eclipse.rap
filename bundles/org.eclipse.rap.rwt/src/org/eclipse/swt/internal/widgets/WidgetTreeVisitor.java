/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Rüdiger Herrmann - bug 335112
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Widget;


public class WidgetTreeVisitor {

  public static abstract class AllWidgetTreeVisitor extends WidgetTreeVisitor {

    @Override
    public final boolean visit( Widget widget ) {
      return doVisit( widget );
    }

    @Override
    public final boolean visit( Composite composite ) {
      return doVisit( composite );
    }

    public abstract boolean doVisit( Widget widget );
  }

  // TODO [rh] all SWT Menu have shell as their parent
  //      we should visit the menus as part of visiting shell, not on each
  //      control -> could lead to visiting one menu multiple times
  public static void accept( Widget root, WidgetTreeVisitor visitor ) {
    if( root instanceof Composite ) {
      Composite composite = ( Composite )root;
      if( visitor.visit( composite ) ) {
        handleMenus( composite, visitor );
        handleDragDrop( root, visitor );
        handleDecorator( root, visitor );
        handleItems( root, visitor );
        handleScrollBars( root, visitor );
        handleChildren( composite, visitor );
        handleToolTips( root, visitor );
      }
    } else {
      if( visitor.visit( root ) ) {
        handleDragDrop( root, visitor );
        handleDecorator( root, visitor );
        handleItems( root, visitor );
        handleScrollBars( root, visitor );
      }
    }
  }

  /**
   * Visit a widget that is not a Composite.
   *
   * @param widget the widget that is visited
   * @return whether items should be visited
   */
  public boolean visit( Widget widget ) {
    return true;
  }

  /**
   * Visit a Composite.
   *
   * @param composite the composite that is visited
   * @return whether children and items should be visited
   */
  public boolean visit( Composite composite ) {
    return true;
  }

  private static void handleMenus( Composite composite, WidgetTreeVisitor visitor ) {
    MenuHolder menuHolder = composite.getAdapter( MenuHolder.class );
    if( menuHolder != null ) {
      for( Menu menu : menuHolder ) {
        accept( menu, visitor );
      }
    }
  }

  private static void handleDragDrop( Widget widget, WidgetTreeVisitor visitor ) {
    if( widget instanceof Control ) {
      Widget dragSource = ( Widget )widget.getData( DND.DRAG_SOURCE_KEY );
      if( dragSource != null ) {
        visitor.visit( dragSource );
      }
      Widget dropTarget = ( Widget )widget.getData( DND.DROP_TARGET_KEY );
      if( dropTarget != null ) {
        visitor.visit( dropTarget );
      }
    }
  }

  private static void handleDecorator( Widget root, WidgetTreeVisitor visitor ) {
    Decorator[] decorators = Decorator.getDecorators( root );
    for( Decorator decorator : decorators ) {
      visitor.visit( decorator );
    }
  }

  private static void handleItems( Widget root, WidgetTreeVisitor visitor ) {
    IItemHolderAdapter<Item> itemHolder = root.getAdapter( IItemHolderAdapter.class );
    if( itemHolder != null ) {
      for( Item item : itemHolder.getItems() ) {
        accept( item, visitor );
      }
    }
  }

  private static void handleScrollBars( Widget root, WidgetTreeVisitor visitor ) {
    if( root instanceof Scrollable ) {
      Scrollable scrollable = ( Scrollable )root;
      ScrollBar horizontalBar = scrollable.getHorizontalBar();
      if( horizontalBar != null ) {
        accept( horizontalBar, visitor );
      }
      ScrollBar verticalBar = scrollable.getVerticalBar();
      if( verticalBar != null ) {
        accept( verticalBar, visitor );
      }
    }
  }

  private static void handleChildren( Composite composite, WidgetTreeVisitor visitor ) {
    ICompositeAdapter adapter = composite.getAdapter( ICompositeAdapter.class );
    for( Control child : adapter.getChildren() ) {
      accept( child, visitor );
    }
  }

  private static void handleToolTips( Widget root, WidgetTreeVisitor visitor ) {
    IShellAdapter adapter = root.getAdapter( IShellAdapter.class );
    if( adapter != null ) {
      ToolTip[] toolTips = adapter.getToolTips();
      for( ToolTip toolTip : toolTips ) {
        visitor.visit( toolTip );
      }
    }
  }

}
