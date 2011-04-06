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

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.lifecycle.ILifeCycle;
import org.eclipse.rwt.lifecycle.PhaseListener;


public class LifeCycleFactory_Test extends TestCase {
  
  public final static class TestLifeCycle extends LifeCycle {
    public void addPhaseListener( PhaseListener listener ) {
    }
    public void removePhaseListener( PhaseListener listener ) {
    }
    public void execute() throws ServletException {
    }
    public Scope getScope() { 
      return Scope.APPLICATION; 
    }
  }
  
  public void testGetUserdefinedLifeCycle() {
    ILifeCycle lifeCycle1 = LifeCycleFactory.getLifeCycle();
    ILifeCycle lifeCycle2 = LifeCycleFactory.getLifeCycle();
    assertTrue( lifeCycle1 instanceof TestLifeCycle );
    assertSame( lifeCycle1, lifeCycle2 );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    System.setProperty( "lifecycle", TestLifeCycle.class.getName() );
  }
  
  protected void tearDown() throws Exception {
    System.getProperties().remove( "lifecycle" );
    Fixture.tearDown();
  }
}
