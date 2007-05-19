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

import com.w4t.Adaptable;


/**
 * Instances of this class are sent as a result of
 * operations being performed on shells.
 *
 * @see ShellListener
 */
// TODO [rh] should we support the 'doit' flag, at least for shellClosed?
public class ShellEvent extends TypedEvent {

  public static final int SHELL_CLOSED = 0;
  public static final int SHELL_ACTIVATED = 1;
  public static final int SHELL_DEACTIVATED = 2;
  
  private static final Class LISTENER = ShellListener.class;
  
  public ShellEvent( final Object source, final int id ) {
    super( source, id );
  }

  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case SHELL_CLOSED:
        ( ( ShellListener )listener ).shellClosed( this );
        break;
      case SHELL_ACTIVATED:
        ( ( ShellListener )listener ).shellActivated( this );
        break;
      case SHELL_DEACTIVATED:
        ( ( ShellListener )listener ).shellDeactivated( this );
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
                                  final ShellListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable,
                                     final ShellListener listener )
  {
    removeListener( adaptable, LISTENER, listener );
  }

  public static Object[] getListeners( final Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
}