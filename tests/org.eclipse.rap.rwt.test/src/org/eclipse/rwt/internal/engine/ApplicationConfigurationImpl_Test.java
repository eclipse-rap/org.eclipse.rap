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

import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rwt.internal.lifecycle.SimpleLifeCycle;
import org.eclipse.rwt.lifecycle.ILifeCycle;


public class ApplicationConfigurationImpl_Test extends TestCase {
  
  private ApplicationConfigurationImpl context;
  private ApplicationContext applicationContext;

  public void testSetLifeCycleModeWithNullArgument() {
    try {
      context.setLifeCycleMode( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testSetLifeCycleModeToThreadless() {
    context.setLifeCycleMode( ApplicationConfiguration.LifeCycleMode.THREADLESS );

    applicationContext.activate();
    ILifeCycle lifeCycle = applicationContext.getLifeCycleFactory().getLifeCycle();
    
    assertSame( SimpleLifeCycle.class, lifeCycle.getClass() );
  }
  
  public void testSetLifeCycleModeToThreaded() {
    context.setLifeCycleMode( ApplicationConfiguration.LifeCycleMode.THREADED );
    
    applicationContext.activate();
    ILifeCycle lifeCycle = applicationContext.getLifeCycleFactory().getLifeCycle();
    
    assertSame( RWTLifeCycle.class, lifeCycle.getClass() );
  }
  
  protected void setUp() throws Exception {
    applicationContext = new ApplicationContext();
    setupRWTConfiguration();
    ApplicationConfigurator configurator = mock( ApplicationConfigurator.class );
    context = new ApplicationConfigurationImpl( applicationContext, configurator );
  }

  private void setupRWTConfiguration() {
    RWTConfiguration configuration = applicationContext.getConfiguration();
    RWTConfigurationImpl configurationImpl = ( RWTConfigurationImpl )configuration;
    configurationImpl.configure( "" );
  }
}
