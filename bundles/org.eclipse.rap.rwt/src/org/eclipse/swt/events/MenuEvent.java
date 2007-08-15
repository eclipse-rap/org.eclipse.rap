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

import org.eclipse.rwt.Adaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.EventUtil;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;


/**
 * Instances of this class are sent as a result of
 * menus being shown and hidden.
 *
 * @see MenuListener
 */
public final class MenuEvent extends TypedEvent {

  public static final int MENU_SHOWN = SWT.Show;
  public static final int MENU_HIDDEN = SWT.Hide;
  private static final Class LISTENER = MenuListener.class;

  public MenuEvent( final Widget widget, final int id ) {
    super( widget, id );
  }
  
  public MenuEvent( final Event event ) {
    this( event.widget, event.type );
  }
  
  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case MENU_SHOWN:
        ( ( MenuListener )listener ).menuShown( this );
      break;
      case MENU_HIDDEN:
        ( ( MenuListener )listener ).menuHidden( this );
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

  public static boolean hasListener( final Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }

  public static void addListener( final Adaptable adaptable,
                                  final MenuListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable,
                                     final MenuListener listener )
  {
    removeListener( adaptable, LISTENER, listener );
  }

  public static Object[] getListeners( final Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
}