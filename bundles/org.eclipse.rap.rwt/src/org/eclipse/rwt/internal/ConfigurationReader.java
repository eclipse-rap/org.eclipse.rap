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

import org.eclipse.rwt.internal.engine.IEngineConfig;


public class ConfigurationReader {
  private IConfiguration configuration;
  private IEngineConfig engineConfig;
  
  
  private static class ConfigurationImpl implements IConfiguration {
    private final Map values;

    private ConfigurationImpl() {
      values = new HashMap();
    }

    public String getLifeCycle() {
      String defaultValue = IConfiguration.LIFE_CYCLE_DEFAULT;
      return getConfigValue( IConfiguration.PARAM_LIFE_CYCLE, defaultValue );
    }
    
    public String getResources() {
      String defaultValue = IConfiguration.RESOURCES_DELIVER_FROM_DISK;
      return getConfigValue( IConfiguration.PARAM_RESOURCES, defaultValue );
    }
    
    private String getConfigValue( String tagName, String defaultValue ) {
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
  
  public IConfiguration getConfiguration() {
    if( configuration == null ) {
      configuration = new ConfigurationImpl();
    }
    return configuration;
  }

  public IEngineConfig getEngineConfig() {
    return engineConfig;
  }

  public void setEngineConfig( IEngineConfig engineConfig ) {
    this.engineConfig = engineConfig;
  }
}