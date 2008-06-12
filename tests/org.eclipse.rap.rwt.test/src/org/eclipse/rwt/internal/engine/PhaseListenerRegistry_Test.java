/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.engine;

import javax.servlet.ServletContextEvent;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.Fixture.TestServletContext;
import org.eclipse.rwt.internal.AdapterFactoryRegistry;
import org.eclipse.rwt.internal.IInitialization;
import org.eclipse.rwt.internal.browser.Ie6up;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.RWTFixture;


public class PhaseListenerRegistry_Test extends TestCase {

  private String savedLifeCycle;

  protected void setUp() throws Exception {
    Fixture.setUp();
    savedLifeCycle = System.getProperty( IInitialization.PARAM_LIFE_CYCLE );
    System.setProperty( IInitialization.PARAM_LIFE_CYCLE,
                        RWTLifeCycle.class.getName() );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
    AdapterFactoryRegistry.clear();
    PhaseListenerRegistry.clear();
    RWTFixture.deregisterResourceManager();
    if( savedLifeCycle != null ) {
      System.setProperty( IInitialization.PARAM_LIFE_CYCLE, savedLifeCycle );
    }
  }
  
  public void testDefaultInitialization() throws Exception {
    // ensures that the default phase listeners are registered
    Fixture.fakeBrowser( new Ie6up( true, true ) );
    Fixture.fakeResponseWriter();
    Fixture.createContext();
    RWTServletContextListener listener = new RWTServletContextListener();
    TestServletContext servletContext = new TestServletContext();
    listener.contextInitialized( new ServletContextEvent( servletContext ) );
    assertEquals( true, findPhaseListener( CurrentPhase.Listener.class ) );
    assertEquals( true, 
                  findPhaseListener( PreserveWidgetsPhaseListener.class ) );
    // clean up
    Fixture.removeContext();
  }
  
  public void testAddAndRemoveAndGetAndClear() {
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
