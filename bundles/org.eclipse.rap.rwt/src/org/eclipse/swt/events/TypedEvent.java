/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.events;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.rwt.internal.events.RWTEvent;
import org.eclipse.rap.rwt.internal.lifecycle.CurrentPhase;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.service.IServiceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;


/**
 * This is the super class for all typed event classes provided
 * by RWT. Typed events contain particular information which is
 * applicable to the event occurrence.
 *
 * @see org.eclipse.swt.widgets.Event
 */
public class TypedEvent extends RWTEvent {
  private static final long serialVersionUID = 1L;

  private static final String ATTR_SCHEDULED_EVENT_LIST
    = TypedEvent.class.getName() + "#scheduledEventList";

  protected Event sourceEvent;

  /**
   * the display where the event occurred
   *
   * @since 1.2
   */
  public Display display;

  /**
   * the widget that issued the event
   */
  public Widget widget;

  /**
   * the time that the event occurred.
   * 
   * NOTE: This field is an unsigned integer and should
   * be AND'ed with 0xFFFFFFFFL so that it can be treated
   * as a signed long.
   * 
   * @since 2.0
   */ 
  public int time;
  
  /**
   * a field for application use
   */
  public Object data;

  /**
   * Constructs a new instance of this class based on the
   * information in the argument.
   *
   * @param event the low level event to initialize the receiver with
   */
  public TypedEvent( Event event ) {
    super( event.widget, event.type );
    display = event.display;
    widget = event.widget;
    data = event.data;
    time = event.time;
    sourceEvent = event;
  }

  /**
   * Constructs a new instance of this class.
   *
   * @param source the object that fired the event
   *
   * @since 1.3
   */
  public TypedEvent( Object source ) {
    this( source, SWT.None );
  }

  /**
   * Constructs a new instance of this class.
   *
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed
   * from application code.
   * </p>
   */
  public TypedEvent( Object source, int id ) {
    super( source, id );
    widget = ( Widget )source;
    display = widget.getDisplay();
  }

  @Override
  public Object getSource() {
    // [rh] introduced to get rid of discouraged access warning when
    // application code accesses getSource() which is defined in
    // org.eclipse.rwt.internal.events.Event
    return super.getSource();
  }

  /**
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed
   * from application code.
   * </p>
   */
  public final void processEvent() {
    // TODO: [fappel] In case of session invalidation there's no phase.
    //                So no event processing should take place, this situation
    //                may improve with the new readAndDispatch mechanism in
    //                place.
    PhaseId currentPhase = CurrentPhase.get();
    if( currentPhase != null ) {
      if(    PhaseId.PREPARE_UI_ROOT.equals( currentPhase )
          || PhaseId.PROCESS_ACTION.equals( currentPhase ) )
      {
        // TODO [fappel]: changes of the event fields in the filter handler
        //                methods should be forwarded to this event...
        sourceEvent.widget.notifyListeners( sourceEvent.type, sourceEvent );
      } else {
        addToScheduledEvents( this );
      }
    }
  }

  ///////////////////////////////////////////////
  // Methods to maintain list of scheduled events

  private static void addToScheduledEvents( TypedEvent event ) {
    getScheduledEventList().add( event );
  }

  @SuppressWarnings("unchecked")
  private static List<TypedEvent> getScheduledEventList() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    List<TypedEvent> result
      = ( List<TypedEvent> )serviceStore.getAttribute( ATTR_SCHEDULED_EVENT_LIST );
    if( result == null ) {
      result = new ArrayList<TypedEvent>();
      serviceStore.setAttribute( ATTR_SCHEDULED_EVENT_LIST, result );
    }
    return result;
  }

  ///////////////////////////////
  // toString & getName from SWT

  // this implementation is extended by subclasses
  @Override
  public String toString() {
    return getName() + "{" + widget + " time=" + time + " data=" + data + "}";
  }

  private String getName() {
    String result = getClass().getName();
    int index = result.lastIndexOf( '.' );
    if( index != -1 ) {
      result = result.substring( index + 1, result.length() );
    }
    return result;
  }
}
