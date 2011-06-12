/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import org.eclipse.rwt.engine.Configurator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

class ConfiguratorTracker extends ServiceTracker< Configurator, Configurator > {
  private final RWTServiceImpl rwtService;
  
  ConfiguratorTracker( BundleContext context, RWTServiceImpl rwtService ) {
    super( context, Configurator.class.getName(), null );
    this.rwtService = rwtService;
  }
  
  @Override
  public Configurator addingService( ServiceReference<Configurator> ref ) {
    rwtService.addConfigurator( ref );
    return super.addingService( ref );
  }
  
  @Override
  public void removedService( ServiceReference<Configurator> reference, Configurator service ) {
    rwtService.removeConfigurator( service );
  }
}