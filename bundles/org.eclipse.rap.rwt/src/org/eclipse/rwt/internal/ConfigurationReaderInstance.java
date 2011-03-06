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
package org.eclipse.rwt.internal;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;


public class ConfigurationReaderInstance {
  private IConfiguration configuration;
  private IEngineConfig engineConfig;
  
  
  private static final class ConfigurationImpl implements IConfiguration {

    private final Map values;

    private ConfigurationImpl() {
      values = new HashMap();
    }

    public String getLifeCycle() {
      String defaultValue = IConfiguration.LIFE_CYCLE_DEFAULT;
      return getConfigValue( IConfiguration.PARAM_LIFE_CYCLE, defaultValue );
    }
    
    public boolean isCompression() {
      String compression = IConfiguration.PARAM_COMPRESSION;
      String value = getConfigValue( compression, "false" );
      return Boolean.valueOf( value ).booleanValue();
    }
    
    public String getResources() {
      String defaultValue = IConfiguration.RESOURCES_DELIVER_FROM_DISK;
      return getConfigValue( IConfiguration.PARAM_RESOURCES, defaultValue );
    }
    
    private String getConfigValue( final String tagName,
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
  
  private ConfigurationReaderInstance() {
    // prevent instance creation
  }

  IConfiguration getConfiguration() {
    if( configuration == null ) {
      configuration = new ConfigurationImpl();
    }
    return configuration;
  }

  IEngineConfig getEngineConfig() {
    return engineConfig;
  }

  void setEngineConfig( final IEngineConfig engineConfig )
    throws FactoryConfigurationError
  {
    this.engineConfig = engineConfig;
  }
}