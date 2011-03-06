/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.lifecycle.*;


public class PhaseListenerRegistry_Test extends TestCase {

  public void testDefaultInitialization() {
    Fixture.setServletContextListener( new RWTServletContextListener() );
    Fixture.triggerServletContextInitialized();
    Fixture.createServiceContext();
    
    assertEquals( true, findPhaseListener( CurrentPhase.Listener.class ) );
    assertEquals( true, 
                  findPhaseListener( PreserveWidgetsPhaseListener.class ) );
    // clean up
    Fixture.disposeOfServiceContext();
  }
  
  public void testAddAndRemoveAndGetAndClear() {
    initializeWithoutPhaseListener();
    Fixture.createRWTContext();
    Fixture.createServiceContext();
    PhaseListener phaseListener = new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void afterPhase( final PhaseEvent event ) {
      }
      public void beforePhase( final PhaseEvent event ) {
      }
      public PhaseId getPhaseId() {
        return null;
      }
    };
    // add & get
    PhaseListenerRegistry.add( phaseListener );
    assertEquals( phaseListener, PhaseListenerRegistry.get()[ 0 ] );
    
    // clear
    PhaseListenerRegistry.clear();
    assertEquals( 0, PhaseListenerRegistry.get().length );
    
    // remove
    PhaseListenerRegistry.add( phaseListener );
    PhaseListenerRegistry.remove( phaseListener );
    assertEquals( 0, PhaseListenerRegistry.get().length );
    
    // cleanUp
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfRWTContext();
  }

  private void initializeWithoutPhaseListener() {
    String initParam = RWTServletContextListener.PHASE_LISTENERS_PARAM;
    Fixture.setInitParameter( initParam, "none" );
  }
  
  private static boolean findPhaseListener( final Class phaseListenerClass ) {
    boolean result = false;
    PhaseListener[] phaseListeners = PhaseListenerRegistry.get();
    for( int i = 0; !result && i < phaseListeners.length; i++ ) {
      if( phaseListeners[ i ].getClass().equals( phaseListenerClass  ) ) {
        result = true;
      }
    }
    return result;
  }
}
