/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import static org.mockito.Mockito.mock;
import junit.framework.TestCase;

import org.eclipse.rwt.engine.Configurator;
import org.eclipse.rwt.engine.Context;
import org.eclipse.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rwt.internal.lifecycle.SimpleLifeCycle;
import org.eclipse.rwt.lifecycle.ILifeCycle;


public class ContextImpl_Test extends TestCase {
  
  private ContextImpl context;
  private ApplicationContext applicationContext;

  public void testSetLifeCycleModeWithNullArgument() {
    try {
      context.setLifeCycleMode( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testSetLifeCycleModeToThreadless() {
    context.setLifeCycleMode( Context.LifeCycleMode.THREADLESS );

    applicationContext.activate();
    ILifeCycle lifeCycle = applicationContext.getLifeCycleFactory().getLifeCycle();
    
    assertSame( SimpleLifeCycle.class, lifeCycle.getClass() );
  }
  
  public void testSetLifeCycleModeToThreaded() {
    context.setLifeCycleMode( Context.LifeCycleMode.THREADED );
    
    applicationContext.activate();
    ILifeCycle lifeCycle = applicationContext.getLifeCycleFactory().getLifeCycle();
    
    assertSame( RWTLifeCycle.class, lifeCycle.getClass() );
  }
  
  protected void setUp() throws Exception {
    applicationContext = new ApplicationContext();
    setupRWTConfiguration();
    Configurator configurator = mock( Configurator.class );
    context = new ContextImpl( applicationContext, configurator );
  }

  private void setupRWTConfiguration() {
    RWTConfiguration configuration = applicationContext.getConfiguration();
    RWTConfigurationImpl configurationImpl = ( RWTConfigurationImpl )configuration;
    configurationImpl.configure( "" );
  }
}
