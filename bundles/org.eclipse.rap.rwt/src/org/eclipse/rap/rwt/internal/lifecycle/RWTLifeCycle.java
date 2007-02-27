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

package org.eclipse.rap.rwt.internal.lifecycle;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.rap.rwt.internal.engine.AdapterFactoryRegistry;
import org.eclipse.rap.rwt.internal.engine.PhaseListenerRegistry;
import com.w4t.ParamCheck;
import com.w4t.engine.lifecycle.*;
import com.w4t.engine.service.ContextProvider;

/**
 * TODO: [fappel] comment
 * <p></p>
 */
public class RWTLifeCycle extends LifeCycle {
  
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
    } catch( final RuntimeException re ) {
      // TODO: [fappel] introduce proper exception handling
      re.printStackTrace();
      throw re;
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
    if( ContextProvider.getSession().getAttribute( INITIALIZED ) == null ) {
      AdapterFactoryRegistry.register();
      ContextProvider.getSession().setAttribute( INITIALIZED, Boolean.TRUE );
    }    
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
}