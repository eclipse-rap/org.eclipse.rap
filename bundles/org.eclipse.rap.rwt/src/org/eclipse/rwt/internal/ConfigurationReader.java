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

import javax.xml.parsers.FactoryConfigurationError;

import org.eclipse.rwt.internal.engine.RWTContext;


/**
 * This is a helping class that reads configuration values from system
 * properties.
 */
public class ConfigurationReader {

  public static IConfiguration getConfiguration() {
    return getInstance().getConfiguration();
  }

  public static IEngineConfig getEngineConfig() {
    return getInstance().getEngineConfig();
  }
  
  public static void setEngineConfig( final IEngineConfig engineConfig )
    throws FactoryConfigurationError
  {
    getInstance().setEngineConfig( engineConfig );
  }

  private static ConfigurationReaderInstance getInstance() {
    Class singletonType = ConfigurationReaderInstance.class;
    Object singleton = RWTContext.getSingleton( singletonType );
    return ( ConfigurationReaderInstance )singleton;
  }
  
  private ConfigurationReader() {
    // prevent instance creation
  }
}