/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;


public class ConfigurationReader_Test extends TestCase {
  
  private final static File TEST_CONFIG_POOLS 
    = new File( Fixture.TEMP_DIR, "w4t_pools.xml" );
  private final static File TEST_CONFIG_PARTIAL
    = new File( Fixture.TEMP_DIR, "w4t_partial.xml" );
  private final static File TEST_XSD
    = new File( Fixture.TEMP_DIR, "W4T.xsd" );
  
  protected void setUp() throws Exception {
    ConfigurationReader.setConfigurationFile( null );
    Fixture.copyTestResource( "resources/w4t_partial.xml", 
                              TEST_CONFIG_PARTIAL );
  }
  
  protected void tearDown() throws Exception {
    System.getProperties().remove( "startUpForm" );
    System.getProperties().remove( "compatibilityMode" );
    
    if( TEST_CONFIG_POOLS.exists() ) {
      TEST_CONFIG_POOLS.delete();
    }
    if( TEST_CONFIG_PARTIAL.exists() ) {
      TEST_CONFIG_PARTIAL.delete();
    }
    if( TEST_XSD.exists() )  {
      TEST_XSD.delete();
    }
    ConfigurationReader.setConfigurationFile( null );
  }
  
  public void testConfigurationReading() throws Exception {
    IConfiguration application = ConfigurationReader.getConfiguration();
    
    // initialization
    IInitialization initialization = application.getInitialization();
    String startUpForm = initialization.getStartUpForm();
    assertEquals( "com.w4t.administration.Startup", startUpForm );
    String lifeCycle = initialization.getLifeCycle();
    assertEquals( "com.w4t.engine.lifecycle.standard.LifeCycle_Standard",
                  lifeCycle );
    String errorPage = initialization.getErrorPage();
    assertEquals( "com.w4t.administration.DefaultErrorForm", errorPage );
    String adminStartupForm = initialization.getAdministrationStartupForm();
    assertEquals(  "com.w4t.administration.Startup", adminStartupForm );
    String messagePage = initialization.getMessagePage();
    assertEquals( "com.w4t.administration.DefaultMessageForm", messagePage );
    String workDirectory = initialization.getWorkDirectory();
    assertEquals( "WEB-INF/classes/", workDirectory );
    long closingTimeout = initialization.getClosingTimeout();
    assertEquals( 3600000, closingTimeout );
    long skimmerFrequency = initialization.getSkimmerFrequenzy();
    assertEquals( 60000, skimmerFrequency );
    boolean directMonitoringAccess = initialization.isDirectMonitoringAccess();
    assertEquals( true, directMonitoringAccess );
    boolean compression = initialization.isCompression();
    assertEquals( true, compression );
    boolean processTime = initialization.isProcessTime();
    assertEquals( false, processTime );
    String nsSubmitters = initialization.getNoscriptSubmitters();
    assertEquals( IInitialization.NOSCRIPT_SUBMITTERS_CREATE, nsSubmitters );
    String resources = initialization.getResources();
    assertEquals( IInitialization.RESOURCES_DELIVER_FROM_DISK, resources );
    long maxSessionUnboundToForceGC
      = initialization.getMaxSessionUnboundToForceGC();
    assertEquals( 0, maxSessionUnboundToForceGC );
    String handleMissingI18NResource
      = initialization.getHandleMissingI18NResource();
    assertEquals( IInitialization.HANDLE_MISSING_I18N_RESOURCE_EMPTY,
                  handleMissingI18NResource );    
    
    // file upload
    IFileUpload fileUpload = application.getFileUpload();
    long maxUploadSize = fileUpload.getMaxUploadSize();
    assertEquals( 4194304, maxUploadSize );
    long maxMemorySize = fileUpload.getMaxMemorySize();
    assertEquals( 524288, maxMemorySize );
  }
  
  public void testPartialInitializationFile() throws Exception {
    ConfigurationReader.setConfigurationFile( TEST_CONFIG_PARTIAL );
    IConfiguration application = ConfigurationReader.getConfiguration();
    
    IInitialization initialization = application.getInitialization();
    String startUpForm = initialization.getStartUpForm();
    assertEquals( "com.w4t.FakeStartup", startUpForm );
    String lifeCycle = initialization.getLifeCycle();
    assertEquals( IInitialization.LIFE_CYCLE_DEFAULT, lifeCycle );
  }
  
  public void testConfigurationOverridingWithSystemProps() throws Exception {
    String startupFormProp = "trallala";
    System.setProperty( "startUpForm", startupFormProp );
    String compatibilityModeProp = "unknown";
    System.setProperty( "compatibilityMode", compatibilityModeProp );
    
    ConfigurationReader.setConfigurationFile( TEST_CONFIG_PARTIAL );
    IConfiguration application = ConfigurationReader.getConfiguration();
    
    IInitialization initialization = application.getInitialization();
    String startUpForm = initialization.getStartUpForm();
    assertEquals( startupFormProp, startUpForm );
    
    String lifeCycle = initialization.getLifeCycle();
    assertEquals( IInitialization.LIFE_CYCLE_DEFAULT, lifeCycle );
  }
}
