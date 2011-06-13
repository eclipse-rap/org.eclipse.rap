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

import org.eclipse.rap.rwt.osgi.RWTService;
import org.osgi.framework.*;


public class Activator implements BundleActivator {
  private RWTServiceImpl rwtService;
  private ServiceRegistration< RWTService > rwtServiceRegistration;
  private HttpTracker httpTracker;
  private ConfiguratorTracker configuratorTracker;
  private RWTServiceObserverTracker rwtServiceObserverTracker;

  public void start( BundleContext context ) {
    registerRWTService( context );
    openHttpServiceTracker( context );
    openConfiguratorTracker( context );
    openRWTServiceObserverTracker( context );
  }

  public void stop( BundleContext context ) {
    rwtServiceObserverTracker.close();
    configuratorTracker.close();
    httpTracker.close();
    rwtServiceRegistration.unregister();
    rwtService.deactivate();
    rwtServiceObserverTracker = null;
    configuratorTracker = null;
    httpTracker = null;
    rwtService = null;
  }
  
  @SuppressWarnings( "unchecked" )
  private void registerRWTService( BundleContext context ) {
    rwtService = new RWTServiceImpl( context );
    String name = RWTService.class.getName();
    ServiceRegistration< ? > registration = context.registerService( name, rwtService, null );
    rwtServiceRegistration = ( ServiceRegistration< RWTService > )registration;
  }

  private void openConfiguratorTracker( BundleContext context ) {
    configuratorTracker = new ConfiguratorTracker( context, rwtService );
    configuratorTracker.open();
  }

  private void openHttpServiceTracker( BundleContext context ) {
    httpTracker = new HttpTracker( context, rwtService );
    httpTracker.open();
  }

  private void openRWTServiceObserverTracker( BundleContext context ) {
    rwtServiceObserverTracker = new RWTServiceObserverTracker( context, rwtService );
    rwtServiceObserverTracker.open();
  }
}