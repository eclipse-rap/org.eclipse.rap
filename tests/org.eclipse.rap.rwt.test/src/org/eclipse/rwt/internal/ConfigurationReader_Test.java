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

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.engine.RWTFactory;


public class ConfigurationReader_Test extends TestCase {

  public void testConfigurationReading() {
    IConfiguration configuration = RWTFactory.getConfigurationReader().getConfiguration();
    String lifeCycle = configuration.getLifeCycle();
    assertEquals( IConfiguration.LIFE_CYCLE_DEFAULT, lifeCycle );
    boolean compression = configuration.isCompression();
    assertEquals( false, compression );
    String resources = configuration.getResources();
    assertEquals( IConfiguration.RESOURCES_DELIVER_FROM_DISK, resources );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
