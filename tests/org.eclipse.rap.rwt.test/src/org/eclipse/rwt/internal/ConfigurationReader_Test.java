/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal;


import org.eclipse.rwt.internal.ConfigurationReader;
import org.eclipse.rwt.internal.IConfiguration;

import junit.framework.TestCase;


public class ConfigurationReader_Test extends TestCase {

  private IConfiguration configuration;

  public void testConfigurationDefaultForGetLifeCycle() {
    String lifeCycle = configuration.getLifeCycle();
    assertEquals( IConfiguration.LIFE_CYCLE_DEFAULT, lifeCycle );
  }
  
  public void testConfigurationDefaultForGetResources() {
    String resources = configuration.getResources();
    assertEquals( IConfiguration.RESOURCES_DELIVER_FROM_DISK, resources );
  }
  
  protected void setUp() throws Exception {
    configuration = new ConfigurationReader().getConfiguration();
  }
}
