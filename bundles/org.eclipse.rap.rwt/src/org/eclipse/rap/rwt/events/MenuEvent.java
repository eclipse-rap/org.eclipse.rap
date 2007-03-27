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

import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.Adaptable;

public class MenuEvent extends RWTEvent {

  public static final int MENU_SHOWN = 0;
  public static final int MENU_HIDDEN = 1;
  private static final Class LISTENER = MenuListener.class;

  public MenuEvent( final Widget widget, final int id ) {
    super( widget, id );
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