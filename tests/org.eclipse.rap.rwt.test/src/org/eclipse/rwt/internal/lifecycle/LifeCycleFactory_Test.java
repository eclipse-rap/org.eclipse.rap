/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.ConfigurationReader;
import org.eclipse.rwt.internal.IConfiguration;
import org.eclipse.rwt.lifecycle.ILifeCycle;
import org.eclipse.rwt.lifecycle.PhaseListener;


public class LifeCycleFactory_Test extends TestCase {
  private LifeCycleFactory lifeCycleFactory;

  private static class TestLifeCycle extends LifeCycle {
    public void addPhaseListener( PhaseListener listener ) {
    }
    public void removePhaseListener( PhaseListener listener ) {
    }
    public void execute() {
    }
    public void requestThreadExec( Runnable runnable ) {
    }
  }
  
  public void testActivateDeactivateCycle() {
    System.setProperty( IConfiguration.PARAM_LIFE_CYCLE,  TestLifeCycle.class.getName() );

    ILifeCycle beforeActivate = lifeCycleFactory.getLifeCycle();
    lifeCycleFactory.activate();
    ILifeCycle afterActivate = lifeCycleFactory.getLifeCycle();
    ILifeCycle secondCall = lifeCycleFactory.getLifeCycle();
    lifeCycleFactory.deactivate();
    ILifeCycle afterDeactivate = lifeCycleFactory.getLifeCycle();
    
    assertNull( beforeActivate );
    assertTrue( afterActivate instanceof TestLifeCycle );
    assertSame( afterActivate, secondCall );
    assertNull( afterDeactivate );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    lifeCycleFactory = new LifeCycleFactory();
    lifeCycleFactory.setConfigurationReader( new ConfigurationReader() );
  }
  
  protected void tearDown() throws Exception {
    System.getProperties().remove( IConfiguration.PARAM_LIFE_CYCLE );
    Fixture.tearDown();
  }
}