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
package org.eclipse.rap.rwt.jstest.internal;

import org.eclipse.rwt.application.ApplicationConfigurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


public class Activator implements BundleActivator {

  private HttpServiceTracker tracker;
  private ServiceRegistration<ApplicationConfigurator> rapAppConfigService;

  public void start( BundleContext context ) throws Exception {
    registerRapApplication( context );
    startHttpTracker( context );
  }

  public void stop( BundleContext context ) throws Exception {
    stopHttpTracker();
    unregisterRapApplication();
  }

  private void startHttpTracker( BundleContext context ) {
    tracker = new HttpServiceTracker( context );
    tracker.open();
  }

  private void stopHttpTracker() {
    tracker.close();
  }

  private void registerRapApplication( BundleContext context ) {
    ApplicationConfigurator configurator = new RapTestApplicationConfigurator();
    rapAppConfigService = context.registerService( ApplicationConfigurator.class,
                                                   configurator,
                                                   null );
  }

  private void unregisterRapApplication() {
    rapAppConfigService.unregister();
  }

}
