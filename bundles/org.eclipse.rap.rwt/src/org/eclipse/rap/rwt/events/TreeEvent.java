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

package org.eclipse.rap.rwt.events;


import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.widgets.Item;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.Adaptable;

/**
 * Instances of this class are sent as a result of
 * trees being expanded and collapsed.
 *
 * @see TreeListener
 */

public final class TreeEvent extends SelectionEvent {

  public static final int TREE_EXPANDED = 0;
  public static final int TREE_COLLAPSED = 1;
  
  private static final Class LISTENER = TreeListener.class;

  public TreeEvent( final Widget widget,
                    final Item item,
                    final int id,
                    final boolean doit,
                    final int detail )
  {
    super( widget, item, id, new Rectangle( 0, 0, 0, 0 ), doit, detail );
  }

  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case TREE_EXPANDED:
        ( ( TreeListener )listener ).treeExpanded( this );
      break;
      case TREE_COLLAPSED:
        ( ( TreeListener )listener ).treeCollapsed( this );
      break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected Class getListenerType() {
    return LISTENER;
  }

  public static void addListener( final Adaptable adaptable, 
                                  final TreeListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable, 
                                     final TreeListener listener )
  {
    removeListener( adaptable, LISTENER, listener );
  }
  
  public static boolean hasListener( final Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }
  
  public static Object[] getListeners( final Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
}

