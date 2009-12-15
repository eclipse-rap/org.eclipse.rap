/*******************************************************************************
/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.events;

import org.eclipse.rwt.Adaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.EventUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

/**
 * Instances of this class are sent as a result of
 * a drag gesture.
 *
 * @see DragDetectListener
 * 
 * @since 1.3
 */
public class DragDetectEvent extends MouseEvent {

  private static final long serialVersionUID = 1L;

  public static final int DRAG_DETECT = SWT.DragDetect;
  
  private static final Class LISTENER = DragDetectListener.class;
  
  public DragDetectEvent( final Control control ) {
    super( control, DRAG_DETECT );
  }

  public DragDetectEvent( final Event event ) {
    super( event.widget, DRAG_DETECT );
  }

  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case DRAG_DETECT:
        ( ( DragDetectListener )listener ).dragDetected( this );
      break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected boolean allowProcessing() {
    return EventUtil.isAccessible( widget );
  }
  
  protected Class getListenerType() {
    return LISTENER;
  }

  public static boolean hasListener( final Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }
  
  public static void addListener( final Adaptable adaptable,
                                  final DragDetectListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable,
                                     final DragDetectListener listener )
  {
    removeListener( adaptable, LISTENER, listener );
  }

  public static Object[] getListeners( final Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
}
