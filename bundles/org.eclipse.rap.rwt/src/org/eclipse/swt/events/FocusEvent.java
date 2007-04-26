/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.events;

import org.eclipse.swt.widgets.Control;
import com.w4t.Adaptable;

/**
 * Instances of this class are sent as a result of widgets gaining and losing
 * focus.
 * 
 * @see FocusListener
 */
public final class FocusEvent extends RWTEvent {

  private static final int FOCUS_GAINED = 0;
  private static final int FOCUS_LOST = 1;
  private static final Class LISTENER = FocusListener.class;

  public static FocusEvent focusGained( final Control control ) {
    return new FocusEvent( control, FOCUS_GAINED );
  }

  public static FocusEvent focusLost( final Control control ) {
    return new FocusEvent( control, FOCUS_LOST );
  }
  
  private FocusEvent( final Control source, final int id ) {
    super( source, id );
  }

  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case FOCUS_GAINED:
        ( ( FocusListener )listener ).focusGained( this );
      break;
      case FOCUS_LOST:
        ( ( FocusListener )listener ).focusLost( this );
        break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected Class getListenerType() {
    return LISTENER;
  }

  public static void addListener( final Adaptable adaptable, 
                                  final FocusListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable, 
                                     final FocusListener listener )
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
