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
import org.eclipse.rwt.internal.lifecycle.CurrentPhase;
import org.eclipse.rwt.internal.lifecycle.PhaseListenerRegistry;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;


public class PhaseListenerRegistry_Test extends TestCase {

  private static class TestPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;
    public void afterPhase( final PhaseEvent event ) {
    }
    public void beforePhase( final PhaseEvent event ) {
    }
    public PhaseId getPhaseId() {
      return null;
    }
  }

  private PhaseListenerRegistry phaseListenerRegistry;

  protected void setUp() throws Exception {
    phaseListenerRegistry = new PhaseListenerRegistry();
  }
  
  protected void tearDown() throws Exception {
    if( ContextProvider.hasContext() ) {
      Fixture.disposeOfServiceContext();
    }
  }
  
  public void testDefaultInitialization() {
    Fixture.triggerServletContextInitialized();
    Fixture.createServiceContext();
    
    assertEquals( true, findPhaseListener( CurrentPhase.Listener.class ) );
  }
  
  public void testAdd() {
    PhaseListener phaseListener = new TestPhaseListener();
    phaseListenerRegistry.add( phaseListener );
    assertEquals( phaseListener, phaseListenerRegistry.get()[ 0 ] );
  }

  public void testAddWithNullArgument() {
    try {
      phaseListenerRegistry.add( null );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRemove() {
    PhaseListener phaseListener = new TestPhaseListener();
    phaseListenerRegistry.add( phaseListener );
    phaseListenerRegistry.remove( phaseListener );
    assertEquals( 0, phaseListenerRegistry.get().length );
  }
  
  public void testRemoveAll() {
    PhaseListener phaseListener = new TestPhaseListener();
    phaseListenerRegistry.add( phaseListener );
    phaseListenerRegistry.removeAll();
    assertEquals( 0, phaseListenerRegistry.get().length );
  }
  
  public void testRemoveWithNullArgument() {
    try {
      phaseListenerRegistry.remove( null );
    } catch( NullPointerException expected ) {
    }
  }
  
  private static boolean findPhaseListener( Class phaseListenerClass ) {
    boolean result = false;
    PhaseListener[] phaseListeners = RWTFactory.getPhaseListenerRegistry().get();
    for( int i = 0; !result && i < phaseListeners.length; i++ ) {
      if( phaseListeners[ i ].getClass().equals( phaseListenerClass  ) ) {
        result = true;
      }
    }
    return result;
  }
}