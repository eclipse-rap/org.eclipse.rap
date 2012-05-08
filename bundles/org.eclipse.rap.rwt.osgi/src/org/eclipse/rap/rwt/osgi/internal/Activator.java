/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import org.eclipse.rap.rwt.osgi.ApplicationLauncher;
import org.osgi.framework.*;


public class Activator implements BundleActivator {

  private ApplicationLauncherImpl applicationLauncher;
  private ServiceRegistration<ApplicationLauncher> applicationLauncherRegistration;
  private HttpTracker httpTracker;
  private ApplicationConfigurationTracker configurationTracker;

  public void start( BundleContext context ) {
    registerApplicationLauncher( context );
    openHttpServiceTracker( context );
    openConfiguratorTracker( context );
  }

  public void stop( BundleContext context ) {
    configurationTracker.close();
    httpTracker.close();
    applicationLauncherRegistration.unregister();
    applicationLauncher.deactivate();
    configurationTracker = null;
    httpTracker = null;
    applicationLauncher = null;
  }

  @SuppressWarnings( "unchecked" )
  private void registerApplicationLauncher( BundleContext context ) {
    applicationLauncher = new ApplicationLauncherImpl( context );
    String name = ApplicationLauncher.class.getName();
    ServiceRegistration<?> registration = registerService( context, name );
    applicationLauncherRegistration = ( ServiceRegistration<ApplicationLauncher> )registration;
  }

  private ServiceRegistration< ? > registerService( BundleContext context, String name ) {
    return context.registerService( name, applicationLauncher, null );
  }

  private void openConfiguratorTracker( BundleContext context ) {
    configurationTracker = new ApplicationConfigurationTracker( context, applicationLauncher );
    configurationTracker.open();
  }

  private void openHttpServiceTracker( BundleContext context ) {
    httpTracker = new HttpTracker( context, applicationLauncher );
    httpTracker.open();
  }
}
