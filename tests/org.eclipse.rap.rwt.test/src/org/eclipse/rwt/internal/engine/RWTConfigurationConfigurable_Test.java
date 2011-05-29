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


public class RWTConfigurationConfigurable_Test extends TestCase {
  private TestServletContext servletContext;
  private RWTConfigurationConfigurable configurable;
  private ApplicationContext applicationContext;

  public void testConfigure() {
    configurable.configure( applicationContext );
    
    assertEquals( getRealPath(), getConfiguration().getContextDirectory() );
  }
  
  public void testReset() {
    configurable.configure( applicationContext );
    
    configurable.reset( applicationContext );
    
    try {
      getConfiguration().getLibraryDirectory();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }
  
  protected void setUp() {
    servletContext = new TestServletContext();
    configurable = new RWTConfigurationConfigurable( servletContext );
    applicationContext = new ApplicationContext();
  }
  
  private RWTConfiguration getConfiguration() {
    return applicationContext.getConfiguration();
  }
  
  private File getRealPath() {
    return new File( servletContext.getRealPath( "/" ) );
  }
}