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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rwt.internal.lifecycle.SimpleLifeCycle;
import org.eclipse.rwt.lifecycle.ILifeCycle;


public class ApplicationConfigurationImpl_Test extends TestCase {
  
  private ApplicationConfigurationImpl applicationConfiguration;
  private ApplicationContext applicationContext;

  public void testUseJEECompatibility() {
    applicationConfiguration.useJEECompatibilityMode();

    applicationContext.activate();
    
    ILifeCycle lifeCycle = applicationContext.getLifeCycleFactory().getLifeCycle();
    assertSame( SimpleLifeCycle.class, lifeCycle.getClass() );
  }
  
  public void testStandardLifecycleConfiguration() {
    applicationContext.activate();
    
    ILifeCycle lifeCycle = applicationContext.getLifeCycleFactory().getLifeCycle();
    assertSame( RWTLifeCycle.class, lifeCycle.getClass() );
  }
  
  protected void setUp() throws Exception {
    ApplicationConfigurator configurator = mock( ApplicationConfigurator.class );
    ServletContext servletContext = mock( ServletContext.class );
    when( servletContext.getRealPath( anyString() ) ).thenReturn( "" );
    applicationContext = new ApplicationContext( configurator, servletContext );
    applicationConfiguration = new ApplicationConfigurationImpl( applicationContext, configurator );
  }
}