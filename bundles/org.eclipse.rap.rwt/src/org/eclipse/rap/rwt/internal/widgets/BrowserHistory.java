/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Ralf Zahn (ARS) - browser history support (Bug 283291)
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.widgets;

import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.readEventPropertyValueAsString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.rap.rwt.IBrowserHistory;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.BrowserHistoryEvent;
import org.eclipse.rap.rwt.events.BrowserHistoryListener;
import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.RequestParams;
import org.eclipse.rap.rwt.lifecycle.PhaseEvent;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.lifecycle.PhaseListenerUtil;
import org.eclipse.rap.rwt.service.ISessionStore;
import org.eclipse.rap.rwt.service.SessionStoreEvent;
import org.eclipse.rap.rwt.service.SessionStoreListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;


public final class BrowserHistory
  implements IBrowserHistory, PhaseListener, SessionStoreListener
{

  private final static String TYPE = "rwt.BrowserHistory";
  private final static String BROWSER_HISTORY_ID = "bh";
  private final static String PROP_NAVIGATION_LISTENER = "navigation";
  private final static String PROP_ENTRIES = "entries";
  private final static String METHOD_ADD = "add";
  private static final String ATTR_HAS_NAVIGATION_LISTENER
    = BrowserHistory.class.getName() + ".hasNavigationListener";
  private static final String EVENT_HISTORY_NAVIGATED = "historyNavigated";
  private static final String EVENT_HISTORY_NAVIGATED_ENTRY_ID = "entryId";

  private final Display display;
  private final Collection<HistoryEntry> entriesToAdd;
  private final Collection<BrowserHistoryListener> listeners;
  private boolean created;

  public BrowserHistory() {
    display = Display.getCurrent();
    entriesToAdd = new ArrayList<HistoryEntry>();
    listeners = new LinkedList<BrowserHistoryListener>();
    RWTFactory.getLifeCycleFactory().getLifeCycle().addPhaseListener( this );
    RWT.getSessionStore().addSessionStoreListener( this );
  }

  //////////////////
  // IBrowserHistory

  public void createEntry( String id, String text ) {
    if( id == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( id.length() == 0 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    entriesToAdd.add( new HistoryEntry( id, text ) );
  }

  public void addBrowserHistoryListener( BrowserHistoryListener listener ) {
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    listeners.add( listener );
  }

  public void removeBrowserHistoryListener( BrowserHistoryListener listener ) {
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    listeners.remove( listener );
  }

  ////////////////
  // PhaseListener

  public void afterPhase( PhaseEvent event ) {
    Display sessionDisplay = LifeCycleUtil.getSessionDisplay();
    if( display == sessionDisplay ) {
      if( PhaseListenerUtil.isPrepareUIRoot( event ) && isStartup() ) {
        processNavigationEvent();
      } else if( PhaseListenerUtil.isReadData( event ) ) {
        preserveNavigationListener();
      } else if( PhaseListenerUtil.isRender( event ) ) {
        renderCreate();
        renderNavigationListener();
        renderAdd();
      }
    }
  }

  public void beforePhase( PhaseEvent event ) {
    Display sessionDisplay = LifeCycleUtil.getSessionDisplay();
    if( display == sessionDisplay ) {
      if( PhaseListenerUtil.isProcessAction( event ) && !isStartup() ) {
        processNavigationEvent();
      }
    }
  }

  public PhaseId getPhaseId() {
    return PhaseId.ANY;
  }

  ///////////////////////
  // SessionStoreListener

  public void beforeDestroy( SessionStoreEvent event ) {
    RWTFactory.getLifeCycleFactory().getLifeCycle().removePhaseListener( this );
  }

  //////////////////
  // Helping methods

  private static boolean isStartup() {
    return "true".equals( ProtocolUtil.readHeadPropertyValue( RequestParams.RWT_INITIALIZE ) );
  }

  private void processNavigationEvent() {
    if( ProtocolUtil.wasEventSent( BROWSER_HISTORY_ID, EVENT_HISTORY_NAVIGATED ) ) {
      String entryId = readEventPropertyValueAsString( BROWSER_HISTORY_ID,
                                                       EVENT_HISTORY_NAVIGATED,
                                                       EVENT_HISTORY_NAVIGATED_ENTRY_ID );
      BrowserHistoryEvent event = new BrowserHistoryEvent( this, entryId );
      BrowserHistoryListener[] listener = getListeners();
      for( int i = 0; i < listener.length; i++ ) {
        listener[ i ].navigated( event );
      }
    }
  }

  private BrowserHistoryListener[] getListeners() {
    return listeners.toArray( new BrowserHistoryListener[ listeners.size() ] );
  }

  private void preserveNavigationListener() {
    boolean hasListener = !listeners.isEmpty();
    ISessionStore sessionStore = display.getAdapter( IDisplayAdapter.class ).getSessionStore();
    sessionStore.setAttribute( ATTR_HAS_NAVIGATION_LISTENER, Boolean.valueOf( hasListener ) );
  }

  private boolean getPreservedNavigationListener() {
    boolean result = false;
    ISessionStore sessionStore = display.getAdapter( IDisplayAdapter.class ).getSessionStore();
    Boolean preserved = ( Boolean )sessionStore.getAttribute( ATTR_HAS_NAVIGATION_LISTENER );
    if( preserved != null ) {
      result = preserved.booleanValue();
    }
    return result;
  }

  private void renderCreate() {
    if( !created ) {
      ProtocolMessageWriter protocolWriter = ContextProvider.getProtocolWriter();
      protocolWriter.appendCreate( BROWSER_HISTORY_ID, TYPE );
      created = true;
    }
  }

  private void renderNavigationListener() {
    boolean actual = !listeners.isEmpty();
    boolean preserved = getPreservedNavigationListener();
    if( preserved != actual ) {
      ProtocolMessageWriter protocolWriter = ContextProvider.getProtocolWriter();
      protocolWriter.appendListen( BROWSER_HISTORY_ID, PROP_NAVIGATION_LISTENER, actual );
    }
  }

  private void renderAdd() {
    if( !entriesToAdd.isEmpty() ) {
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put( PROP_ENTRIES, entriesAsArray() );
      ProtocolMessageWriter protocolWriter = ContextProvider.getProtocolWriter();
      protocolWriter.appendCall( BROWSER_HISTORY_ID, METHOD_ADD, properties );
      entriesToAdd.clear();
    }
  }

  private Object[] entriesAsArray() {
    HistoryEntry[] entries = getEntries();
    Object[][] result = new Object[ entries.length ][ 2 ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ][ 0 ] = entries[ i ].id;
      result[ i ][ 1 ] = entries[ i ].text;
    }
    return result;
  }

  HistoryEntry[] getEntries() {
    return entriesToAdd.toArray( new HistoryEntry[ entriesToAdd.size() ] );
  }

  ////////////////
  // Inner classes

  final class HistoryEntry {
    final String id;
    final String text;

    HistoryEntry( String id, String text ) {
      this.id = id;
      this.text = text;
    }
  }
}
