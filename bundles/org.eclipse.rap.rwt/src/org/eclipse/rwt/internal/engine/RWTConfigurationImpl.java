/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import java.io.File;

import org.eclipse.rwt.internal.util.ParamCheck;


public class RWTConfigurationImpl implements RWTConfiguration {
  private static final String PATH_PREFIX = "WEB-INF" + File.separator;
  private static final String CLASSES_PATH = PATH_PREFIX + "classes";
  private static final String LIB_PATH = PATH_PREFIX + "lib";

  private File contextDirectory;
  private String resourcesDeliveryMode;
  private String lifeCycle;

  public void configure( String realPath ) {
    ParamCheck.notNull( realPath, "realPath" );
    contextDirectory = new File( realPath );
  }
  
  public void reset() {
    contextDirectory = null;
  }
  
  public File getContextDirectory() {
    checkConfigured();
    return contextDirectory;
  }

  public File getLibraryDirectory() {
    checkConfigured();
    return new File( contextDirectory, LIB_PATH );
  }

  public File getClassDirectory() {
    checkConfigured();
    return new File( contextDirectory, CLASSES_PATH );
  }

  public String getLifeCycle() {
    checkConfigured();
    if( lifeCycle == null ) {
      lifeCycle = getConfigValue( PARAM_LIFE_CYCLE, LIFE_CYCLE_DEFAULT );
    }
    return lifeCycle;
  }

  public String getResourcesDeliveryMode() {
    checkConfigured();
    if( resourcesDeliveryMode == null ) {
     resourcesDeliveryMode = getConfigValue( PARAM_RESOURCES, RESOURCES_DELIVER_FROM_DISK );
    }
    return resourcesDeliveryMode;
  }
  
  private String getConfigValue( String tagName, String defaultValue ) {
    String result = System.getProperty( tagName );
    if( result == null ) {
      result = defaultValue;
    }
    return result;
  }
  
  private void checkConfigured() {
    if( contextDirectory == null ) {
      throw new IllegalStateException( "RWTConfigurationImpl has not been configured." );
    }
  }
}