/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import javax.servlet.ServletContext;

import org.eclipse.rwt.engine.Configurator;
import org.eclipse.rwt.internal.engine.configurables.*;
import org.eclipse.rwt.internal.util.ClassUtil;

class ConfigurablesProvider {
  private static final String CONFIGURABLES
    = ConfigurablesProvider.class.getName() + "#CONFIGURABLES";

  Configurable[] createConfigurables( ServletContext servletContext ) {
    Configurable[] result;
    if( hasConfiguratorParam( servletContext ) ) {
      result = getContextConfigurable( servletContext );
    } else {
      result = getCompatibilityConfigurables( servletContext );
    }
    return result;
  }

  private Configurable[] getContextConfigurable( ServletContext servletContext ) {
    String clazzName = servletContext.getInitParameter( ContextConfigurable.CONFIGURATOR_PARAM );
    ClassLoader loader = getClass().getClassLoader();
    Configurator configurator = ( Configurator )ClassUtil.newInstance( loader, clazzName );
    return new Configurable[] {
      new ContextConfigurable( configurator, servletContext )
    };
  }

  private boolean hasConfiguratorParam( ServletContext servletContext ) {
    return null != servletContext.getInitParameter( ContextConfigurable.CONFIGURATOR_PARAM );
  }

  private Configurable[] getCompatibilityConfigurables( ServletContext servletContext ) {
    Configurable[] result;
    result = new Configurable[]{
      new RWTConfigurationConfigurable( servletContext ),
      new EntryPointManagerConfigurable( servletContext ),
      new BrandingManagerConfigurable( servletContext ),
      new SettingStoreManagerConfigurable( servletContext ),
      new PhaseListenerRegistryConfigurable( servletContext ),
      new AdapterManagerConfigurable( servletContext ),
      new ResourceRegistryConfigurable( servletContext ),
      new ServiceManagerConfigurable(),
      new ThemeManagerConfigurable( servletContext )
    };
    return result;
  }

  static void bufferConfigurables( Configurable[] configurables, ServletContext servletContext ) {
    servletContext.setAttribute( CONFIGURABLES, configurables );
  }

  static Configurable[] getConfigurables( ServletContext servletContext ) {
    return ( Configurable[] )servletContext.getAttribute( CONFIGURABLES );
  }

  static void removeConfigurables( ServletContext servletContext ) {
    servletContext.removeAttribute( CONFIGURABLES );
  }

  static void removeConfigurables( ServletContext servletContext, ApplicationContext appContext ) {
    Configurable[] configurables = getConfigurables( servletContext );
    for( int i = 0; i < configurables.length; i++ ) {
      appContext.removeConfigurable( configurables[ i ] );
    }
    removeConfigurables( servletContext );
  }

  static void addConfigurables( Configurable[] configurables, ApplicationContext appContext ) {
    for( int i = 0; i < configurables.length; i++ ) {
      appContext.addConfigurable( configurables[ i ] );
    }
  }
}