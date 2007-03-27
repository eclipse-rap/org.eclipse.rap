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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.rap.rwt.browser.LocationEvent;
import org.eclipse.rap.rwt.custom.CTabFolderEvent;
import org.eclipse.rap.rwt.internal.lifecycle.CurrentPhase;
import org.eclipse.rap.rwt.widgets.Control;
import com.w4t.engine.lifecycle.PhaseId;
import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.IServiceStateInfo;
import com.w4t.event.Event;


/**
 * TODO [rh] JavaDoc
 */
// TODO [rh] event class hierarchy out of sync with SWT. If RWTEvent should be
//      the counterpart for TypedEvent in SWT, it lacks the public fields for
//      widget, display, etc.
// TODO [rh] SWT TypedEvent has fields display, widget and time, revise this 
public abstract class RWTEvent extends Event {

  private static final String ATTR_SCHEDULED_EVENT_LIST 
    = RWTEvent.class.getName() + "_scheduledEventList";

  // TODO [rh] event order is preliminary
  private static final Class[] EVENT_ORDER = {
    ControlEvent.class,
    ActivateEvent.class,
    DisposeEvent.class,
    ModifyEvent.class,
    TreeEvent.class,
    CTabFolderEvent.class,
    FocusEvent.class,
    SelectionEvent.class,
    LocationEvent.class,
    ShellEvent.class,
    MenuEvent.class
  };
  
  public static void processScheduledEvents() {
    for( int i = 0; i < EVENT_ORDER.length; i++ ) {
      processEventClass( EVENT_ORDER[ i ] );
    }
    clearScheduledEventList();
  }
  
  public RWTEvent( final Object source, final int id ) {
    super( source, id );
  }
  
  public final void processEvent() {
    if(    PhaseId.PREPARE_UI_ROOT.equals( CurrentPhase.get() ) 
        || PhaseId.PROCESS_ACTION.equals( CurrentPhase.get() ) ) 
    {
      super.processEvent();
    } else {
      addToScheduledEvents( this );
    }
  }

  ///////////////////////////////////////////////
  // Methods to maintain list of scheduled events
  
  private static void addToScheduledEvents( final RWTEvent event ) {
    getScheduledEventList().add( event );
  }
  
  private static RWTEvent[] getScheduledEvents() {
    List list = getScheduledEventList();
    RWTEvent[] result = new RWTEvent[ list.size() ];
    list.toArray( result );
    return result;
  }
  
  private static List getScheduledEventList() {
    List result;
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    result = ( List )stateInfo.getAttribute( ATTR_SCHEDULED_EVENT_LIST );
    if( result == null ) {
      result = new ArrayList();
      stateInfo.setAttribute( ATTR_SCHEDULED_EVENT_LIST, result );
    }
    return result;
  }
  
  private static void clearScheduledEventList() {
    getScheduledEventList().clear();
  }

  private static void processEventClass( final Class eventClass ) {
    RWTEvent[] scheduledEvents = getScheduledEvents();
    for( int i = 0; i < scheduledEvents.length; i++ ) {
      if(    eventClass.equals( scheduledEvents[ i ].getClass() ) 
          && isSourceEnabled( scheduledEvents[ i ] ) ) 
      {
        scheduledEvents[ i ].processEvent();
      }
    }
  }

  ///////////////////////////////////
  // Check enablement of event source
  
  // TODO [rh] preliminary: maybe it would be better to handle this in the
  //      respective LCAs
  private static boolean isSourceEnabled( final RWTEvent event ) {
    boolean result = true;
    if( event.getSource() instanceof Control ) {
      Control control = ( Control ) event.getSource();
      result = control.getEnabled();
    }
    return result;
  }
  
  //////////////////////////////
  // toString & getName from SWT 
  
  // this implementation is extended by subclasses
  public String toString() {
    return getName()
        + "{"
//        TODO [rst] uncomment when these public fields are implemented
//        + widget + " time=" + time + " data=" + data
        + "}";
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
