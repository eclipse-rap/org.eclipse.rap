/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.jstest.internal;

import java.util.Map;

import org.eclipse.rap.rwt.jstest.TestContribution;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


public class Activator implements BundleActivator {

  private static Activator instance;
  private HttpServiceTracker httpTracker;
  private ContributionServiceTracker contributionTracker;
  private ServiceRegistration<ApplicationConfiguration> rapAppConfigService;

  public void start( BundleContext context ) throws Exception {
    registerRapApplication( context );
    startHttpTracker( context );
    startContributionTracker( context );
    instance = this;
  }

  public void stop( BundleContext context ) throws Exception {
    instance = null;
    stopContributionTracker();
    stopHttpTracker();
    unregisterRapApplication();
  }

  public static Map<String, TestContribution> getContributions() {
    return instance.contributionTracker.getContributions();
  }

  private void startContributionTracker( BundleContext context ) {
    contributionTracker = new ContributionServiceTracker( context );
    contributionTracker.open();
  }

  private void stopContributionTracker() {
    contributionTracker.close();
  }

  private void startHttpTracker( BundleContext context ) {
    httpTracker = new HttpServiceTracker( context );
    httpTracker.open();
  }

  private void stopHttpTracker() {
    httpTracker.close();
  }

  private void registerRapApplication( BundleContext context ) {
    ApplicationConfiguration configurator = new RapTestApplicationConfigurator();
    rapAppConfigService = context.registerService( ApplicationConfiguration.class,
                                                   configurator,
                                                   null );
  }

  private void unregisterRapApplication() {
    rapAppConfigService.unregister();
  }

}
