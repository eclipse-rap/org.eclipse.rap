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

import org.eclipse.rwt.application.ApplicationConfiguration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;


class ApplicationConfiguratorTracker
  extends ServiceTracker<ApplicationConfiguration, ApplicationConfiguration>
{

  private final ApplicationLauncherImpl applicationLauncher;

  ApplicationConfiguratorTracker( BundleContext context,
                                  ApplicationLauncherImpl applicationLauncher )
  {
    super( context, ApplicationConfiguration.class.getName(), null );
    this.applicationLauncher = applicationLauncher;
  }

  @Override
  public ApplicationConfiguration addingService( ServiceReference<ApplicationConfiguration> ref ) {
    return applicationLauncher.addConfigurator( ref );
  }

  @Override
  public void removedService( ServiceReference<ApplicationConfiguration> reference,
                              ApplicationConfiguration service )
  {
    applicationLauncher.removeConfigurator( service );
  }
}
