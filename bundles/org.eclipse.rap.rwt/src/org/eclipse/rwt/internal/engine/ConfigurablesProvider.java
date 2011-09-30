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

import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.internal.engine.configurables.AdapterManagerConfigurable;
import org.eclipse.rwt.internal.engine.configurables.BrandingManagerConfigurable;
import org.eclipse.rwt.internal.engine.configurables.EntryPointManagerConfigurable;
import org.eclipse.rwt.internal.engine.configurables.PhaseListenerRegistryConfigurable;
import org.eclipse.rwt.internal.engine.configurables.RWTConfigurationConfigurable;
import org.eclipse.rwt.internal.engine.configurables.ResourceRegistryConfigurable;
import org.eclipse.rwt.internal.engine.configurables.ServiceManagerConfigurable;
import org.eclipse.rwt.internal.engine.configurables.SettingStoreManagerConfigurable;
import org.eclipse.rwt.internal.engine.configurables.ThemeManagerConfigurable;
import org.eclipse.rwt.internal.util.ClassUtil;

public class ConfigurablesProvider {
  private static final String CONFIGURABLES
    = ConfigurablesProvider.class.getName() + "#CONFIGURABLES";

  public Configurable[] createConfigurables( ServletContext servletContext ) {
    Configurable[] result;
    if( hasConfiguratorParam( servletContext ) ) {
      result = getContextConfigurable( servletContext );
    } else {
      result = getCompatibilityConfigurables( servletContext );
    }
    return result;
  }

  private static Configurable[] getContextConfigurable( ServletContext servletContext ) {
    String className = servletContext.getInitParameter( ApplicationConfigurable.CONFIGURATOR_PARAM );
    ClassLoader loader = ConfigurablesProvider.class.getClassLoader();
    ApplicationConfigurator configurator = newConfigurator( className, loader );
    return new Configurable[] {
      new ApplicationConfigurable( configurator, servletContext )
    };
  }

  private static ApplicationConfigurator newConfigurator( String className, ClassLoader loader ) {
    return ( ApplicationConfigurator )ClassUtil.newInstance( loader, className );
  }

  private static boolean hasConfiguratorParam( ServletContext servletContext ) {
    return null != servletContext.getInitParameter( ApplicationConfigurable.CONFIGURATOR_PARAM );
  }

  private static Configurable[] getCompatibilityConfigurables( ServletContext servletContext ) {
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