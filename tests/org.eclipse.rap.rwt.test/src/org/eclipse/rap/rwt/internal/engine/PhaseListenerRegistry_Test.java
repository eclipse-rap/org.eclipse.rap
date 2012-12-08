/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
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
package org.eclipse.rap.rwt.internal.engine;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.lifecycle.PhaseListenerRegistry;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.lifecycle.PhaseEvent;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class PhaseListenerRegistry_Test extends TestCase {

  private static class TestPhaseListener implements PhaseListener {
    public void afterPhase( PhaseEvent event ) {
    }
    public void beforePhase( PhaseEvent event ) {
    }
    public PhaseId getPhaseId() {
      return null;
    }
  }

  private PhaseListenerRegistry phaseListenerRegistry;
  
  public void testAdd() {
    PhaseListener phaseListener = new TestPhaseListener();
    phaseListenerRegistry.add( phaseListener );
    assertEquals( phaseListener, phaseListenerRegistry.getAll()[ 0 ] );
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
    assertEquals( 0, phaseListenerRegistry.getAll().length );
  }
  
  public void testRemoveAll() {
    PhaseListener phaseListener = new TestPhaseListener();
    phaseListenerRegistry.add( phaseListener );
    phaseListenerRegistry.removeAll();
    assertEquals( 0, phaseListenerRegistry.getAll().length );
  }
  
  public void testRemoveWithNullArgument() {
    try {
      phaseListenerRegistry.remove( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  protected void setUp() {
    phaseListenerRegistry = new PhaseListenerRegistry();
  }
  
  protected void tearDown()  {
    ensureServiceContextHasBeenDisposed();
  }

  private void ensureServiceContextHasBeenDisposed() {
    if( ContextProvider.hasContext() ) {
      Fixture.disposeOfServiceContext();
    }
  }
}