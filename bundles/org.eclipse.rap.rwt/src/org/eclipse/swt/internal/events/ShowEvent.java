/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.events;

import org.eclipse.rap.rwt.Adaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;


/**
 * Instances of this class are sent as a result of controls being shown.
 *
 * <p>This class is <em>not</em> intended to be used by clients.</p>
 *
 * @see ShowListener
 * @since 1.2
 */
public final class ShowEvent extends TypedEvent {

  private static final long serialVersionUID = 1L;

  public static final int SHOWN = SWT.Show;
  public static final int HIDDEN = SWT.Hide;

  private static final Class LISTENER = ShowListener.class;
  private static final int[] EVENT_TYPES = { SHOWN, HIDDEN };

  public ShowEvent( Control source, int id ) {
    super( source, id );
  }

  public ShowEvent( Event event ) {
    super( event );
  }

  protected void dispatchToObserver( Object listener ) {
    switch( getID() ) {
      case SHOWN:
        ( ( ShowListener )listener ).controlShown( this );
      break;
      case HIDDEN:
        ( ( ShowListener )listener ).controlHidden( this );
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

  public static void addListener( Adaptable adaptable, ShowListener listener ) {
    addListener( adaptable, EVENT_TYPES, listener );
  }

  public static void removeListener( Adaptable adaptable, ShowListener listener ) {
    removeListener( adaptable, EVENT_TYPES, listener );
  }

  public static boolean hasListener( Adaptable adaptable ) {
    return hasListener( adaptable, EVENT_TYPES );
  }

  public static Object[] getListeners( Adaptable adaptable ) {
    return getListener( adaptable, EVENT_TYPES );
  }
}
