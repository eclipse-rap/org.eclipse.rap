/*******************************************************************************
 * Copyright (c) 2007, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class LifeCycleFactory_Test {
  private ApplicationContextImpl applicationContext;
  private LifeCycleFactory lifeCycleFactory;

  @Before
  public void setUp() {
    Fixture.setUp();
    applicationContext = getApplicationContext();
    lifeCycleFactory = new LifeCycleFactory( applicationContext );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testActivateDeactivateCycle() {
    lifeCycleFactory.configure( TestLifeCycle.class );
    LifeCycle beforeActivate = lifeCycleFactory.getLifeCycle();
    lifeCycleFactory.activate();
    LifeCycle afterActivate = lifeCycleFactory.getLifeCycle();
    LifeCycle secondCall = lifeCycleFactory.getLifeCycle();
    lifeCycleFactory.deactivate();
    LifeCycle afterDeactivate = lifeCycleFactory.getLifeCycle();

    assertNull( beforeActivate );
    assertTrue( afterActivate instanceof TestLifeCycle );
    assertSame( afterActivate, secondCall );
    assertNull( afterDeactivate );
  }

  @Test
  public void testActivateAfterDeactivate() {
    lifeCycleFactory.configure( TestLifeCycle.class );
    lifeCycleFactory.activate();

    lifeCycleFactory.deactivate();
    lifeCycleFactory.activate();

    Class<?> lifeCycleClass = lifeCycleFactory.getLifeCycle().getClass();
    assertSame( SimpleLifeCycle.class, lifeCycleClass );
  }

  @Test
  public void testConfigure() {
    lifeCycleFactory.configure( TestLifeCycle.class );
    lifeCycleFactory.activate();

    Class<?> lifeCycleClass = lifeCycleFactory.getLifeCycle().getClass();

    assertSame( TestLifeCycle.class, lifeCycleClass );
  }

  @Test
  public void testDefaultLifeCycle() {
    lifeCycleFactory.activate();

    Class<?> lifeCycleClass = lifeCycleFactory.getLifeCycle().getClass();

    assertSame( SimpleLifeCycle.class, lifeCycleClass );
  }

  @Test
  public void testGetLifeCycle() {
    lifeCycleFactory.configure( TestLifeCycle.class );
    lifeCycleFactory.activate();

    TestLifeCycle lifeCycle = ( TestLifeCycle )lifeCycleFactory.getLifeCycle();

    assertEquals( applicationContext, lifeCycle.applicationContext );
  }

  private static class TestLifeCycle extends LifeCycle {
    final ApplicationContextImpl applicationContext;
    public TestLifeCycle( ApplicationContextImpl applicationContext ) {
      super( applicationContext );
      this.applicationContext = applicationContext;
    }
    @Override
    public void execute() {
    }
    @Override
    public void requestThreadExec( Runnable runnable ) {
    }
    @Override
    public void sleep() {
    }
  }

}
