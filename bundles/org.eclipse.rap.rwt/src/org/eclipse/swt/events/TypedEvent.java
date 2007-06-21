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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.internal.lifecycle.CurrentPhase;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.SetDataEvent;
import org.eclipse.swt.internal.widgets.IDisplayAdapter.IFilterEntry;
import org.eclipse.swt.widgets.*;

import com.w4t.engine.lifecycle.PhaseId;
import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.IServiceStateInfo;
import com.w4t.event.Event;


/**
 * TODO [rh] JavaDoc
 */
// TODO [rh] SWT TypedEvent has fields display, widget and time, revise this 
public abstract class TypedEvent extends Event {

  public Widget widget;
  
  private static final String ATTR_SCHEDULED_EVENT_LIST 
    = TypedEvent.class.getName() + "_scheduledEventList";

  // TODO [rh] event order is preliminary
  private static final Class[] EVENT_ORDER = {
    ControlEvent.class,
    ActivateEvent.class,
    DisposeEvent.class,
    SetDataEvent.class,
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
  
  /**
   * a field for application use
   */
  public Object data;
  
  public TypedEvent( final Object source, final int id ) {
    super( source, id );
    this.widget = (Widget) source;
  }
  
  public final void processEvent() {
    if(    PhaseId.PREPARE_UI_ROOT.equals( CurrentPhase.get() ) 
        || PhaseId.PROCESS_ACTION.equals( CurrentPhase.get() ) ) 
    {
      // TODO [fappel]: changes of the event fields in the filter handler
      //                methods should be forwarded to this event...
      if( !isFiltered( processFilters() ) ) {
        super.processEvent();
      }
    } else {
      addToScheduledEvents( this );
    }
  }

  ////////////////////////////////////h
  // methods for filter implementation 
  
  private org.eclipse.swt.widgets.Event processFilters() {
    IFilterEntry[] filters = getFilterEntries();
    org.eclipse.swt.widgets.Event result
      = new org.eclipse.swt.widgets.Event();
    result.widget = widget;
    result.type = getID();
    for( int i = 0; !isFiltered( result ) && i < filters.length; i++ ) {
      if( filters[ i ].getType() == result.type ) {
        filters[ i ].getListener().handleEvent( result );
      }
    }
    return result;
  }

  private boolean isFiltered( final org.eclipse.swt.widgets.Event event ) {
    return event.type == SWT.None;
  }

  private IFilterEntry[] getFilterEntries() {
    Display display = Display.getCurrent();
    IDisplayAdapter adapter 
      = ( IDisplayAdapter )display.getAdapter( IDisplayAdapter.class );
    return adapter.getFilters();
  }

  ///////////////////////////////////////////////
  // Methods to maintain list of scheduled events
  
  private static void addToScheduledEvents( final TypedEvent event ) {
    getScheduledEventList().add( event );
  }
  
  private static TypedEvent[] getScheduledEvents() {
    List list = getScheduledEventList();
    TypedEvent[] result = new TypedEvent[ list.size() ];
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
    TypedEvent[] scheduledEvents = getScheduledEvents();
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
  private static boolean isSourceEnabled( final TypedEvent event ) {
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
//        + widget + " time=" + time + 
        + " data=" 
        + data
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
