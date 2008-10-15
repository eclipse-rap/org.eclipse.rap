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

package org.eclipse.swt.events;


import org.eclipse.rwt.Adaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.EventUtil;
import org.eclipse.swt.widgets.*;


/**
 * Instances of this class are sent as a result of
 * trees being expanded and collapsed.
 * 
 * <p><strong>IMPORTANT:</strong> All <code>public static</code> members of 
 * this class are <em>not</em> part of the RWT public API. They are marked 
 * public only so that they can be shared within the packages provided by RWT. 
 * They should never be accessed from application code.
 * </p>
 *
 * @see TreeListener
 */
public final class TreeEvent extends SelectionEvent {

  private static final long serialVersionUID = 1L;

  public static final int TREE_EXPANDED = SWT.Expand;
  public static final int TREE_COLLAPSED = SWT.Collapse;
  
  private static final Class LISTENER = TreeListener.class;

  /**
   * Constructs a new instance of this class based on the
   * information in the given untyped event.
   *
   * @param event the untyped event containing the information
   */
  public TreeEvent( final Event event ) {
    this( event.widget, event.item, event.type );
  }

  /**
   * Constructs a new instance of this class. 
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed 
   * from application code.
   * </p>
   */
  public TreeEvent( final Widget widget,
                    final Widget item,
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

