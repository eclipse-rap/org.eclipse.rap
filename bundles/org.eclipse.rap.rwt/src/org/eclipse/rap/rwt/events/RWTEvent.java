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
import org.eclipse.rap.rwt.internal.lifecycle.CurrentPhase;
import com.w4t.engine.lifecycle.PhaseId;
import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.IServiceStateInfo;
import com.w4t.event.Event;


/**
 * TODO [rh] JavaDoc
 */
public abstract class RWTEvent extends Event {

  private static final String ATTR_SCHEDULED_EVENT_LIST 
    = RWTEvent.class.getName() + "_scheduledEventList";
  
  private static final Class[] EVENT_ORDER = {
    ControlEvent.class,
    ActivateEvent.class,
    DisposeEvent.class,
    TreeEvent.class,
    SelectionEvent.class,
    ShellEvent.class,
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
    if( PhaseId.PROCESS_ACTION.equals( CurrentPhase.get() ) ) {
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
      if( eventClass.equals( scheduledEvents[ i ].getClass() ) ) {
        scheduledEvents[ i ].processEvent();
      }
    }
  }
}
