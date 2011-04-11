/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 *   Ralf Zahn (ARS) - browser history support (Bug 283291)
 ******************************************************************************/
package org.eclipse.rwt.internal.widgets;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.*;
import org.eclipse.rwt.events.BrowserHistoryEvent;
import org.eclipse.rwt.events.BrowserHistoryListener;
import org.eclipse.rwt.internal.AdapterManagerImpl;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.events.*;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.util.EncodingUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.service.SessionStoreEvent;
import org.eclipse.rwt.service.SessionStoreListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public final class BrowserHistory
  implements IBrowserHistory, PhaseListener, Adaptable, SessionStoreListener
{

  private static final long serialVersionUID = 1L;
  
  private static final String EVENT_HISTORY_NAVIGATED
    = "org.eclipse.rwt.events.historyNavigated";
  private static final String EVENT_HISTORY_NAVIGATED_ENTRY_ID
    = "org.eclipse.rwt.events.historyNavigated.entryId";
  private static final String ADD_TO_HISTORY
    = "qx.client.History.getInstance().addToHistory( {0}, {1} );";

  private final Display display;
  private IEventAdapter eventAdapter;

  public BrowserHistory() {
    this.display = Display.getCurrent();
    RWTFactory.getLifeCycleFactory().getLifeCycle().addPhaseListener( this );
    RWT.getSessionStore().addSessionStoreListener( this );
  }
  
  //////////////////
  // IBrowserHistory

  public void createEntry( final String id, final String text ) {
    if( null == id ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( id.length() == 0 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    String quotedId = "\"" + EncodingUtil.escapeDoubleQuoted( id ) + "\"";
    String quotedText = text;
    if( quotedText != null ) {
      quotedText = "\"" + EncodingUtil.escapeDoubleQuoted( text ) + "\"";
    }
    String[] args = new String[]{ quotedId, quotedText };
    JSExecutor.executeJS( MessageFormat.format( ADD_TO_HISTORY, args ) );
  }

  public void addBrowserHistoryListener( final BrowserHistoryListener listener ) {
    if( null == listener ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    BrowserHistoryEvent.addListener( this, listener );
  }

  public void removeBrowserHistoryListener( final BrowserHistoryListener lsnr ) {
    if( null == lsnr ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    BrowserHistoryEvent.removeListener( this, lsnr );
  }
  
  ////////////////
  // PhaseListener

  public void afterPhase( final PhaseEvent event ) {
  }

  public void beforePhase( final PhaseEvent event ) {
    if( display == RWTLifeCycle.getSessionDisplay() ) {
      HttpServletRequest request = ContextProvider.getRequest();
      String isEvent = request.getParameter( EVENT_HISTORY_NAVIGATED );
      if( Boolean.valueOf( isEvent ).booleanValue() ) {
        String entryId = request.getParameter( EVENT_HISTORY_NAVIGATED_ENTRY_ID );
        Event evt = new BrowserHistoryEvent( this, entryId );
        evt.processEvent();
      }
    }
  }

  public PhaseId getPhaseId() {
    return PhaseId.PROCESS_ACTION;
  }

  ////////////
  // Adaptable
  
  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IEventAdapter.class ) {
      if( eventAdapter == null ) {
        eventAdapter = new EventAdapter();
      }
      result = eventAdapter;
    } else {
      result = AdapterManagerImpl.getInstance().getAdapter( this, adapter );
    }
    return result;
  }

  ///////////////////////
  // SessionStoreListener
  
  public void beforeDestroy( final SessionStoreEvent event ) {
    RWTFactory.getLifeCycleFactory().getLifeCycle().removePhaseListener( this );
  }
}
