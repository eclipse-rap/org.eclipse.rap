/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.PhaseListenerRegistry;
import org.eclipse.rwt.lifecycle.*;


public class PhaseListenerRegistryConfigurable_Test extends TestCase {
  private PhaseListenerRegistryConfigurable configurable;
  private ApplicationContext applicationContext;

  public static class TestPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;

    public void beforePhase( PhaseEvent event ) {
    }

    public void afterPhase( PhaseEvent event ) {
    }

    public PhaseId getPhaseId() {
      return null;
    }
  }
  
  public void testGetPhaseListenerNames() {
    String[] defaultNames = configurable.getPhaseListenerNames();
    setPhaseListenerInitParameter();
    String[] testNames = configurable.getPhaseListenerNames();
    
    assertSame( PhaseListenerRegistryConfigurable.DEFAULT_PHASE_LISTENERS, defaultNames );
    assertEquals( 1, testNames.length );
    assertEquals( TestPhaseListener.class.getName(), testNames[ 0 ] );
  }
  
  public void testConfigure() {
    setPhaseListenerInitParameter();
    
    configurable.configure( applicationContext );
    
    assertEquals( 1, getRegistry().get().length );
    assertTrue( getRegistry().get()[ 0 ] instanceof TestPhaseListener );
  }

  public void testConfigureWithUnknownPhaseListener() {
    Fixture.setInitParameter( RWTServletContextListener.PHASE_LISTENERS_PARAM, "unknown" );
    
    try {
      configurable.configure( applicationContext );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testReset() {
    setPhaseListenerInitParameter();
    configurable.configure( applicationContext );
    PhaseListenerRegistry registry = getRegistry();
    
    configurable.reset( applicationContext );
    
    assertEquals( 0, registry.get().length );
  }
  
  protected void setUp() {
    ServletContext servletContext = Fixture.createServletContext();
    configurable = new PhaseListenerRegistryConfigurable( servletContext );
    applicationContext = new ApplicationContext();
  }
  
  protected void tearDown() {
    Fixture.setInitParameter( RWTServletContextListener.PHASE_LISTENERS_PARAM, null );
    Fixture.disposeOfServletContext();
  }

  private void setPhaseListenerInitParameter() {
    String name = TestPhaseListener.class.getName();
    Fixture.setInitParameter( RWTServletContextListener.PHASE_LISTENERS_PARAM, name );
  }

  private PhaseListenerRegistry getRegistry() {
    return applicationContext.getPhaseListenerRegistry();
  }
}