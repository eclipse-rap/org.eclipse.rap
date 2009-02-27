/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.events;

import org.eclipse.rwt.Adaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Control;

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


  public ShowEvent( final Control source, final int id ) {
    super( source, id );
  }

  protected void dispatchToObserver( final Object listener ) {
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

  public static void addListener( final Adaptable adaptable, 
                                  final ShowListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable, 
                                     final ShowListener listener )
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