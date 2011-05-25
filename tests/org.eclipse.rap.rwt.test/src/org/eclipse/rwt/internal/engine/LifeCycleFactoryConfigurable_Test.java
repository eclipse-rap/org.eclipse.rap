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

import junit.framework.TestCase;

import org.eclipse.rwt.internal.ConfigurationReader;
import org.eclipse.rwt.internal.lifecycle.LifeCycleFactory;


public class LifeCycleFactoryConfigurable_Test extends TestCase {
  private static final ConfigurationReader CONFIGURATION_READER = new ConfigurationReader();
  
  private TestApplicationContext context;

  private LifeCycleFactoryConfigurable configurable;

  private final class TestApplicationContext extends ApplicationContext {
    private ConfigurationReader injected;

    public LifeCycleFactory getLifeCycleFactory() {
      return new LifeCycleFactory() {
        public void setConfigurationReader( ConfigurationReader configurationReader ) {
          injected = configurationReader;
        }
      };
    }

    public ConfigurationReader getConfigurationReader() {
      return CONFIGURATION_READER;
    }

    public Object getInjected() {
      return injected;
    }
  }

  public void testConfigure() {
    configurable.configure( context );

    assertSame( CONFIGURATION_READER, context.getInjected() );
  }
  
  public void testReset() {
    configurable.configure( context );

    configurable.reset( context );
    
    assertSame( null, context.getInjected() );
  }
  
  protected void setUp() {
    configurable = new LifeCycleFactoryConfigurable();
    context = new TestApplicationContext();
  }
}