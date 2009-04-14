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

package org.eclipse.swt.internal.events;

import org.eclipse.rwt.Adaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

/**
 * Instances of this class are sent as a result of controls being activated 
 * or deactivated.
 * 
 * <p>This class is <em>not</em> intended to be used by clients.</p>
 * 
 * @see ActivateListener
 * @since 1.0
 */
public final class ActivateEvent extends TypedEvent {

  private static final long serialVersionUID = 1L;

  public static final int ACTIVATED = SWT.Activate;
  public static final int DEACTIVATED = SWT.Deactivate;
  
  private static final Class LISTENER = ActivateListener.class;

  public ActivateEvent( Event event ) {
    this( ( Control )event.widget, event.type );
  }
  
  public ActivateEvent( final Control source, final int id ) {
    super( source, id );
  }

  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case ACTIVATED:
        ( ( ActivateListener )listener ).activated( this );
      break;
      case DEACTIVATED:
        ( ( ActivateListener )listener ).deactivated( this );
      break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected Class getListenerType() {
    return LISTENER;
  }
  
  protected boolean allowProcessing() {
    return true;
  }

  public static void addListener( final Adaptable adaptable, 
                                  final ActivateListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable, 
                                     final ActivateListener listener )
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