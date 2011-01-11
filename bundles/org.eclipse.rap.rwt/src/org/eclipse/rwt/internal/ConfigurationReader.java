/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing implementation
 ******************************************************************************/
package org.eclipse.rwt.internal;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;


/**
 * This is a helping class that reads configuration values from system
 * properties.
 */
public class ConfigurationReader {
  
  private static IConfiguration configuration = null;
  private static final Map values = new HashMap();
  private static IEngineConfig engineConfig = null;
    
  private static final class ConfigurationImpl implements IConfiguration {

    public String getLifeCycle() {
      String defaultValue = IConfiguration.LIFE_CYCLE_DEFAULT;
      return getConfigValue( IConfiguration.PARAM_LIFE_CYCLE, defaultValue );
    }
    
    public boolean isCompression() {
      String value = getConfigValue( IConfiguration.PARAM_COMPRESSION,
      "false" );
      return Boolean.valueOf( value ).booleanValue();
    }
    
    public String getResources() {
      String defaultValue = IConfiguration.RESOURCES_DELIVER_FROM_DISK;
      return getConfigValue( "resources", defaultValue );
    }
  }

  public static IConfiguration getConfiguration() {
    if( configuration == null ) {
      configuration = new ConfigurationImpl();
    }
    return configuration;
  }

  public static IEngineConfig getEngineConfig() {
    return engineConfig;
  }

  public static void setEngineConfig( final IEngineConfig engineConfig )
    throws FactoryConfigurationError
  {
    ConfigurationReader.engineConfig = engineConfig;
  }

  public static void reset() {
    values.clear();
    configuration = null;
  }

  //////////////////
  // helping methods

  private static String getConfigValue( final String tagName,
                                        final String defaultValue )
  {
    if( !values.containsKey( tagName ) ) {
      String result = "";
      if( System.getProperty( tagName ) != null ) {
        result = System.getProperty( tagName );
      } else {
        result = defaultValue;
      }
      values.put( tagName, result );
    }
    return ( String )values.get( tagName );
  }
}