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

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.rwt.TestServletContext;
import org.eclipse.rwt.internal.ConfigurationReader;


public class ConfigurationReaderConfigurable_Test extends TestCase {
  private TestServletContext servletContext;
  private ConfigurationReaderConfigurable configurable;
  private ApplicationContext applicationContext;

  public void testConfigure() {
    applicationContext.activate();
    
    assertNotNull( getConfigurationReader().getEngineConfig() );
    assertEquals( getRealPath(), getConfigurationReader().getEngineConfig().getServerContextDir() );
  }
  
  public void testReset() {
    applicationContext.activate();
    ConfigurationReader reader = getConfigurationReader();
    
    applicationContext.deactivate();
    
    assertNull( reader.getEngineConfig() );
  }
  
  protected void setUp() {
    servletContext = new TestServletContext();
    configurable = new ConfigurationReaderConfigurable( servletContext );
    applicationContext = new ApplicationContext( new Class[] { ConfigurationReader.class } );
    applicationContext.addConfigurable( configurable );
  }
  
  private ConfigurationReader getConfigurationReader() {
    return applicationContext.getConfigurationReader();
  }
  
  private File getRealPath() {
    return new File( servletContext.getRealPath( "/" ) );
  }
}