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
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.lifecycle.ILifeCycle;
import org.eclipse.rwt.lifecycle.PhaseListener;


public class LifeCycleFactory_Test extends TestCase {

  private static class ApplicationScopedLifeCycle extends LifeCycle {
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

  private static class SessionScopedLifeCycle extends LifeCycle {
    public void addPhaseListener( PhaseListener listener ) {
    }
    public void removePhaseListener( PhaseListener listener ) {
    }
    public void execute() throws ServletException {
    }
    public Scope getScope() { 
      return Scope.SESSION; 
    }
  }
  
  private LifeCycleFactory lifeCycleFactory;
  
  public void testGetLifeCycleWithApplicationScopedLifeCycle() {
    System.setProperty( "lifecycle", ApplicationScopedLifeCycle.class.getName() );
    ILifeCycle lifeCycle1 = lifeCycleFactory.getLifeCycle();
    ILifeCycle lifeCycle2 = lifeCycleFactory.getLifeCycle();
    assertTrue( lifeCycle1 instanceof ApplicationScopedLifeCycle );
    assertSame( lifeCycle1, lifeCycle2 );
  }
  
  public void testGetLifeCycleWithSessionScopedLifeCycle() {
    System.setProperty( "lifecycle", SessionScopedLifeCycle.class.getName() );
    ILifeCycle lifeCycleForSession1 = lifeCycleFactory.getLifeCycle();
    newSession();
    ILifeCycle lifeCycleForSession2 = lifeCycleFactory.getLifeCycle();
    assertTrue( lifeCycleForSession1 instanceof SessionScopedLifeCycle );
    assertNotSame( lifeCycleForSession1, lifeCycleForSession2 );
  }
  
  public void testGetLifeCycleWithSessionScopedLifeCycleReturnsSameForOneSession() {
    System.setProperty( "lifecycle", SessionScopedLifeCycle.class.getName() );
    ILifeCycle lifeCycle1 = lifeCycleFactory.getLifeCycle();
    ILifeCycle lifeCycle2 = lifeCycleFactory.getLifeCycle();
    assertSame( lifeCycle1, lifeCycle2 );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    lifeCycleFactory = RWTFactory.getLifeCycleFactory();
  }
  
  protected void tearDown() throws Exception {
    System.getProperties().remove( "lifecycle" );
    Fixture.tearDown();
  }

  private static void newSession() {
    Fixture.disposeOfServiceContext();
    Fixture.createServiceContext();
  }
}
