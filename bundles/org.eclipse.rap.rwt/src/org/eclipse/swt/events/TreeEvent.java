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

package org.eclipse.swt.events;


import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.EventUtil;
import org.eclipse.swt.widgets.*;

import com.w4t.Adaptable;

/**
 * Instances of this class are sent as a result of
 * trees being expanded and collapsed.
 *
 * @see TreeListener
 */

public final class TreeEvent extends SelectionEvent {

  public static final int TREE_EXPANDED = SWT.Expand;
  public static final int TREE_COLLAPSED = SWT.Collapse;
  
  private static final Class LISTENER = TreeListener.class;

  public TreeEvent( final Widget widget,
                    final Item item,
                    final int id )
  {
    super( widget, item, id );
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
  
  protected boolean allowProcessing() {
    return EventUtil.isAccessible( widget );
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

