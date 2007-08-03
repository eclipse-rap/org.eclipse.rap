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
import org.eclipse.swt.widgets.Event;

import com.w4t.Adaptable;


/**
 * Instances of this class are sent as a result of
 * operations being performed on shells.
 *
 * @see ShellListener
 */
public class ShellEvent extends TypedEvent {

  public static final int SHELL_CLOSED = SWT.Close;
  public static final int SHELL_ACTIVATED = SWT.Activate;
  public static final int SHELL_DEACTIVATED = SWT.Deactivate;
  
  private static final Class LISTENER = ShellListener.class;
  
  public boolean doit;
  
  public ShellEvent( final Object source, final int id ) {
    super( source, id );
  }
  
  public ShellEvent( final Event event ) {
    this( event.widget, event.type );
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
  
  protected boolean allowProcessing() {
    boolean result;
    if( getID() == SHELL_CLOSED ) {
      result = EventUtil.isAccessible( widget );
    } else {
      result = true;
    }
    return result;
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