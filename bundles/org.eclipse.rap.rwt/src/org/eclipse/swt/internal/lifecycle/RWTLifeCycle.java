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

package org.eclipse.swt.internal.lifecycle;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.internal.engine.AdapterFactoryRegistry;
import org.eclipse.swt.internal.engine.PhaseListenerRegistry;
import org.eclipse.swt.widgets.Control;

import com.w4t.ParamCheck;
import com.w4t.engine.lifecycle.*;
import com.w4t.engine.service.*;

/**
 * TODO: [fappel] comment
 * <p></p>
 */
public class RWTLifeCycle extends LifeCycle {
  
  private static final String REDRAW_CONTROLS
    = RWTLifeCycle.class.getName() + ".RedrawWidgets";
  private static final String CURRENT_THREAD
    = RWTLifeCycle.class.getName() + "CurrentThread";
  private static final String INITIALIZED
    = RWTLifeCycle.class.getName() + "Initialized";
  private final static Logger LOGGER 
    = Logger.getLogger( RWTLifeCycle.class.getName() );

  private final static IPhase[] PHASES = new IPhase[] {
    new PrepareUIRoot(),
    new ReadData(),
    new ProcessAction(),
    new Render()
  };
  
  private final Set listeners;
  
  public static Thread getThread() {
    Thread result = null;
    if( ContextProvider.hasContext() ) {
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      // in case of a faked context...
      if( stateInfo != null ) {
        result = ( Thread )stateInfo.getAttribute( CURRENT_THREAD );
      }
    }
    return result;
  }
  
  public static void setThread( final Thread thread ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setAttribute( CURRENT_THREAD, thread );
  }
  
  public RWTLifeCycle() {
    listeners = new HashSet();
    listeners.addAll( Arrays.asList( PhaseListenerRegistry.get() ) );    
  }
  
  public void execute() throws IOException {
    try {
      initialize();
      PhaseId current = PhaseId.PREPARE_UI_ROOT;
      while( current != null ) {
        PhaseId next;
        beforePhaseExecution( current );
        try {
          next = PHASES[ current.getOrdinal() - 1 ].execute();
        } finally {
          afterPhaseExecution( current );        
        }
        current = next;
      }
    } catch( final Throwable t ) {
      // TODO: [fappel] introduce proper exception handling
      t.printStackTrace();
      if( t instanceof RuntimeException ) {
        throw ( RuntimeException )t;
      }
      String msg = "An error occured while executing RWTLifeCycle.";
      throw new RuntimeException( msg, t );
    } finally {
      cleanUp();
    }
  }

  public void addPhaseListener( final PhaseListener listener ) {
    ParamCheck.notNull( listener, "listener" );
    synchronized( listeners ) {
      listeners.add( listener );
    }
  }

  public void removePhaseListener( final PhaseListener listener ) {
    ParamCheck.notNull( listener, "listener" );
    synchronized( listeners ) {
      listeners.remove( listener );
    }
  }
  
  public Scope getScope() {
    return Scope.APPLICATION;
  }
  
  //////////////////
  // helping methods
  
  
  private void initialize() {
    ISessionStore session = ContextProvider.getSession();
    if( session.getAttribute( INITIALIZED ) == null ) {
      AdapterFactoryRegistry.register();
      session.setAttribute( INITIALIZED, Boolean.TRUE );
    }    
    Thread current = Thread.currentThread();
    ContextProvider.getStateInfo().setAttribute( CURRENT_THREAD, current );
    UICallBackManager.getInstance().notifyUIThreadStart();
  }
  
  private void cleanUp() {
    UICallBackManager.getInstance().notifyUIThreadEnd();
    ContextProvider.getStateInfo().setAttribute( CURRENT_THREAD, null );
  }

  private void afterPhaseExecution( final PhaseId current ) {
    PhaseListener[] phaseListeners = getPhaseListeners();
    PhaseEvent evt = new PhaseEvent( this, current );
    for( int i = 0; i < phaseListeners.length; i++ ) {
      PhaseId listenerId = phaseListeners[ i ].getPhaseId();
      if( mustNotify( current, listenerId ) ) {
        try {
          phaseListeners[ i ].afterPhase( evt );
        } catch( final Throwable thr ) {
          String text = "Could not execute PhaseListener after phase ''{0}''.";
          String msg = MessageFormat.format( text, new Object[] { current } );
          LOGGER.log( Level.SEVERE, msg, thr );
        }
      }
    }
    if( current == PhaseId.PROCESS_ACTION ) {
      UICallBackManager.getInstance().processRunnablesInUIThread();
      doRedrawFake();
    }
  }

  private void beforePhaseExecution( final PhaseId current ) {
    PhaseListener[] phaseListeners = getPhaseListeners();
    PhaseEvent evt = new PhaseEvent( this, current );
    for( int i = 0; i < phaseListeners.length; i++ ) {
      PhaseId listenerId = phaseListeners[ i ].getPhaseId();
      if( mustNotify( current, listenerId ) ) {
        try {
          phaseListeners[ i ].beforePhase( evt );
        } catch( final Throwable thr ) {
          String text = "Could not execute PhaseListener before phase ''{0}''.";
          String msg = MessageFormat.format( text, new Object[] { current } );
          LOGGER.log( Level.SEVERE, msg, thr );
        }
      }
    }
  }

  private static boolean mustNotify( final PhaseId currentId, 
                                     final PhaseId listenerId ) 
  {
    return    listenerId == PhaseId.ANY 
           || listenerId == PHASES[ currentId.getOrdinal() - 1 ].getPhaseID();
  }

  private PhaseListener[] getPhaseListeners() {
    synchronized( listeners ) {
      PhaseListener[] result = new PhaseListener[ listeners.size() ];
      listeners.toArray( result );
      return result;
    }
  }

  public static void fakeRedraw( final Control control,
                                 final boolean redraw )
  {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Set set = ( Set )stateInfo.getAttribute( REDRAW_CONTROLS );
    if( set == null ) {
      set = new HashSet();
      stateInfo.setAttribute( REDRAW_CONTROLS, set );
    }
    if( redraw ) {
      set.add( control );
    } else {
      set.remove( control );
    }
  }
  
  private void doRedrawFake() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Set set = ( Set )stateInfo.getAttribute( REDRAW_CONTROLS );
    if( set != null ) {
      Object[] controls = set.toArray();
      for( int i = 0; i < controls.length; i++ ) {
        int evtId = ControlEvent.CONTROL_RESIZED;
        Control control = ( ( Control )controls[ i ] );
        ControlEvent evt = new ControlEvent( control, evtId );
        evt.processEvent();
      }
    }
  }
}