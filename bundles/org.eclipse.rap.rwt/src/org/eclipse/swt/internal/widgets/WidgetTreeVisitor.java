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

package org.eclipse.swt.internal.widgets;

import org.eclipse.swt.widgets.*;

/**
 * Utility class that provides a traversal through a widget-tree
 * using the visitor pattern.
 * 
 * <p>The traversal through the children will be skipped if the visit call
 * on the parent node returns <code>false</code>.</p> 
 */
public class WidgetTreeVisitor {

  public static abstract class AllWidgetTreeVisitor extends WidgetTreeVisitor {
    
    public final boolean visit( final Widget widget ) {
      return doVisit( widget );
    }
    
    public final boolean visit( final Composite composite ) {
      return doVisit( composite );
    }

    public abstract boolean doVisit( Widget widget );
  }
  
  // TODO [rh] all SWT Menu have shell as their parent
  //      we should visit the menus as part of visiting shell, not on each
  //      control -> could lead to visiting one menu multiple times      
  public static void accept( final Widget root, 
                             final WidgetTreeVisitor visitor ) 
  {
    if( root instanceof Composite ) {
      Composite composite = ( Composite )root;
      if( visitor.visit( composite ) ) {
        handleMenus( composite, visitor );
        handleItems( root, visitor );
        Control[] children = composite.getChildren();
        for( int i = 0; i < children.length; i++ ) {
          accept( children[ i ], visitor );
        }
      }
    } else if( ItemHolder.isItemHolder( root ) ) {
      if( visitor.visit( root ) ) {
        handleItems( root, visitor );
      }
    } else {
      visitor.visit( root );
    }
  }

  public boolean visit( final Widget widget ) {
    return true;
  }

  public boolean visit( final Composite composite ) {
    return true;
  }

  ///////////////////////////////////////////////////
  // Helping methods to visit particular hierarchies
  
  private static void handleMenus( final Composite composite,
                                   final WidgetTreeVisitor visitor ) 
  {
    if( MenuHolder.isMenuHolder( composite ) ) {
      Menu[] menus = MenuHolder.getMenus( composite );
      for( int i = 0; i < menus.length; i++ ) {
        accept( menus[ i ], visitor );
      }
    }
  }

  private static void handleItems( final Widget root, 
                                   final WidgetTreeVisitor visitor )
  {
    if( ItemHolder.isItemHolder( root ) ) {
      Item[] items = ItemHolder.getItems( root );
      for( int i = 0; i < items.length; i++ ) {
        accept( items[ i ], visitor );
      }
    }
  }
}
