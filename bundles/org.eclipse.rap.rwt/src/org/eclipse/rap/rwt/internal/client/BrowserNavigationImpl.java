/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Ralf Zahn (ARS) - browser history support (Bug 283291)
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.client;

import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.getClientMessage;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.readEventPropertyValueAsString;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.eclipse.rap.rwt.client.service.BrowserNavigationEvent;
import org.eclipse.rap.rwt.client.service.BrowserNavigationListener;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.rap.rwt.lifecycle.PhaseEvent;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.service.UISessionEvent;
import org.eclipse.rap.rwt.service.UISessionListener;
import org.eclipse.swt.SWT;


public final class BrowserNavigationImpl
  implements BrowserNavigation, PhaseListener, UISessionListener
{

  private final static String TYPE = "rwt.client.BrowserNavigation";
  private final static String PROP_NAVIGATION_LISTENER = "Navigation";
  private final static String PROP_ENTRIES = "entries";
  private final static String METHOD_ADD_TO_HISTORY = "addToHistory";
  private static final String EVENT_HISTORY_NAVIGATED_STATE = "state";

  private final UISession uiSession;
  private final List<HistoryEntry> entriesToAdd;
  private final Collection<BrowserNavigationListener> listeners;
  private boolean hasNavigationListener;

  public BrowserNavigationImpl() {
    entriesToAdd = new ArrayList<HistoryEntry>();
    listeners = new LinkedHashSet<BrowserNavigationListener>();
    getApplicationContext().getLifeCycleFactory().getLifeCycle().addPhaseListener( this );
    uiSession = ContextProvider.getUISession();
    uiSession.addUISessionListener( this );
  }

  //////////
  // History

  public void pushState( String state, String text ) {
    ParamCheck.notNullOrEmpty( state, "state" );
    entriesToAdd.add( new HistoryEntry( state, text ) );
  }

  public void addBrowserNavigationListener( BrowserNavigationListener listener ) {
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    listeners.add( listener );
  }

  public void removeBrowserNavigationListener( BrowserNavigationListener listener ) {
    if( listener == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    listeners.remove( listener );
  }

  ////////////////
  // PhaseListener

  public void afterPhase( PhaseEvent event ) {
    if( uiSession == ContextProvider.getUISession() ) {
      if( event.getPhaseId() == PhaseId.PREPARE_UI_ROOT && isStartup() ) {
        processNavigationEvent();
      } else if( event.getPhaseId() == PhaseId.READ_DATA ) {
        preserveNavigationListener();
      } else if( event.getPhaseId() == PhaseId.RENDER ) {
        renderNavigationListener();
        renderAdd();
      }
    }
  }

  public void beforePhase( PhaseEvent event ) {
    if( uiSession == ContextProvider.getUISession() ) {
      if( event.getPhaseId() == PhaseId.PROCESS_ACTION && !isStartup() ) {
        processNavigationEvent();
      }
    }
  }

  public PhaseId getPhaseId() {
    return PhaseId.ANY;
  }

  ////////////////////
  // UISessionListener

  public void beforeDestroy( UISessionEvent event ) {
    getApplicationContext().getLifeCycleFactory().getLifeCycle().removePhaseListener( this );
  }

  //////////////////
  // Helping methods

  private static boolean isStartup() {
    JsonValue initializeHeader = getClientMessage().getHeader( ClientMessageConst.RWT_INITIALIZE );
    return JsonValue.TRUE.equals( initializeHeader );
  }

  private void processNavigationEvent() {
    if( ProtocolUtil.wasEventSent( TYPE, PROP_NAVIGATION_LISTENER ) ) {
      String state = readEventPropertyValueAsString( TYPE,
                                                     PROP_NAVIGATION_LISTENER,
                                                     EVENT_HISTORY_NAVIGATED_STATE );
      BrowserNavigationEvent event = new BrowserNavigationEvent( this, state );
      notifyListeners( event );
    }
  }

  void notifyListeners( BrowserNavigationEvent event ) {
    BrowserNavigationListener[] listeners = getListeners();
    for( BrowserNavigationListener listener : listeners ) {
      listener.navigated( event );
    }
  }

  private BrowserNavigationListener[] getListeners() {
    return listeners.toArray( new BrowserNavigationListener[ listeners.size() ] );
  }

  private void preserveNavigationListener() {
    hasNavigationListener = !listeners.isEmpty();
  }

  private boolean getPreservedNavigationListener() {
    return hasNavigationListener;
  }

  private void renderNavigationListener() {
    boolean actual = !listeners.isEmpty();
    boolean preserved = getPreservedNavigationListener();
    if( preserved != actual ) {
      ProtocolMessageWriter protocolWriter = ContextProvider.getProtocolWriter();
      protocolWriter.appendListen( TYPE, PROP_NAVIGATION_LISTENER, actual );
    }
  }

  private void renderAdd() {
    if( !entriesToAdd.isEmpty() ) {
      JsonObject parameters = new JsonObject().add( PROP_ENTRIES, entriesAsArray() );
      ProtocolMessageWriter protocolWriter = ContextProvider.getProtocolWriter();
      protocolWriter.appendCall( TYPE, METHOD_ADD_TO_HISTORY, parameters );
      entriesToAdd.clear();
    }
  }

  private JsonArray entriesAsArray() {
    HistoryEntry[] entries = getEntries();
    JsonArray result = new JsonArray();
    for( int i = 0; i < entries.length; i++ ) {
      result.add( new JsonArray().add( entries[ i ].state ).add( entries[ i ].title ) );
    }
    return result;
  }

  HistoryEntry[] getEntries() {
    return entriesToAdd.toArray( new HistoryEntry[ entriesToAdd.size() ] );
  }

  ////////////////
  // Inner classes

  final class HistoryEntry {
    final String state;
    final String title;

    HistoryEntry( String state, String title ) {
      this.state = state;
      this.title = title;
    }
  }

}
