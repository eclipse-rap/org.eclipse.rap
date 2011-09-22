/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.testfixture.internal.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;

import org.eclipse.rwt.engine.Configurator;
import org.eclipse.rwt.engine.Context;
import org.eclipse.rwt.internal.engine.Configurable;
import org.eclipse.rwt.internal.engine.ConfigurablesProvider;
import org.eclipse.rwt.internal.engine.ContextConfigurable;
import org.eclipse.rwt.internal.engine.RWTServletContextListener;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.lifecycle.IEntryPoint;

@SuppressWarnings("restriction")
public class RWTStartup {
  
  public static ServletContextListener createServletContextListener( 
    Class<? extends IEntryPoint> entryPointClass ) 
  {
    Configurator configurator = new SimpleLifeCycleConfigurator( entryPointClass );
    ConfigurablesProvider configurablesProvider = new CustomConfigurablesProvider( configurator );
    return new RWTServletContextListener( configurablesProvider );
  }
  
  private static class CustomConfigurablesProvider extends ConfigurablesProvider {
    private final Configurator configurator;

    private CustomConfigurablesProvider( Configurator configurator ) {
      this.configurator = configurator;
    }

    public Configurable[] createConfigurables( ServletContext servletContext ) {
      return new Configurable[]{
        new ContextConfigurable( configurator, servletContext )
      };
    }
  }

  private static class SimpleLifeCycleConfigurator implements Configurator {
    private final Class<? extends IEntryPoint> entryPointClass;
    
    private SimpleLifeCycleConfigurator( Class<? extends IEntryPoint> entryPointClass ) {
      this.entryPointClass = entryPointClass;
    }
    
    public void configure( Context context ) {
      context.setLifeCycleMode( Context.LifeCycleMode.THREADLESS );
      context.addEntryPoint( EntryPointManager.DEFAULT, entryPointClass );
    }
  }

}