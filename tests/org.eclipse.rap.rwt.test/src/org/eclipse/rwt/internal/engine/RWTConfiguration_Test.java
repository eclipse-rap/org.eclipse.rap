/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;


public class RWTConfiguration_Test extends TestCase {
  private RWTConfigurationImpl configuration;

  public void testConfigure() {
    configure();

    assertTrue( configuration.getContextDirectory().exists() );
    assertTrue( configuration.getClassDirectory().exists() );
    assertTrue( configuration.getLibraryDirectory().exists() );
  }
  
  public void testConfigureParamPathNotNull() {
    try {
      configuration.configure( null );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testUnconfigured() {
    try {
      configuration.getContextDirectory();
      fail();
    } catch( IllegalStateException expected ) {
    }
    try {
      configuration.getClassDirectory();
      fail();
    } catch( IllegalStateException expected ) {
    }
    try {
      configuration.getLibraryDirectory();
      fail();
    } catch( IllegalStateException expected ) {
    }
    try {
      configuration.getLifeCycle();
      fail();
    } catch( IllegalStateException expected ) {
    }
    try {
      configuration.getResourcesDeliveryMode();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  public void testConfigurationDefaultForGetLifeCycle() {
    configure();
    String lifeCycle = configuration.getLifeCycle();
    
    assertEquals( RWTConfiguration.LIFE_CYCLE_DEFAULT, lifeCycle );
  }
  
  public void testGetLifeCycle() {
    configure();
    String expected = "lifeCycleName";
    System.setProperty( RWTConfiguration.PARAM_LIFE_CYCLE, expected );
    
    String lifeCycle = configuration.getLifeCycle();
    
    assertEquals( expected, lifeCycle );
  }
  
  public void testConfigurationDefaultForGetResources() {
    configure();
    String resources = configuration.getResourcesDeliveryMode();
    
    assertEquals( RWTConfiguration.RESOURCES_DELIVER_FROM_DISK, resources );
  }

  public void testGetResources() {
    configure();
    System.setProperty( RWTConfiguration.PARAM_RESOURCES,
                        RWTConfiguration.RESOURCES_DELIVER_BY_SERVLET );
    
    String resources = configuration.getResourcesDeliveryMode();
    
    assertEquals( RWTConfiguration.RESOURCES_DELIVER_BY_SERVLET, resources );
  }
  
  protected void setUp() {
    Fixture.createWebContextDirectories();
    configuration = new RWTConfigurationImpl();
  }
  
  protected void tearDown() {
    Fixture.deleteWebContextDirectories();
    System.getProperties().remove( RWTConfiguration.PARAM_LIFE_CYCLE );
    System.getProperties().remove( RWTConfiguration.PARAM_RESOURCES );
  }
  
  private void configure() {
    String path = Fixture.WEB_CONTEXT_DIR.getPath();
    configuration.configure( path );
  }
}