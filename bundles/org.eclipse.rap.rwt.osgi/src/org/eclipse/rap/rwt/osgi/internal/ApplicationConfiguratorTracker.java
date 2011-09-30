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

import org.eclipse.rwt.application.ApplicationConfigurator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;


class ApplicationConfiguratorTracker
  extends ServiceTracker<ApplicationConfigurator, ApplicationConfigurator>
{

  private final ApplicationLauncherImpl applicationLauncher;

  ApplicationConfiguratorTracker( BundleContext context,
                                  ApplicationLauncherImpl applicationLauncher )
  {
    super( context, ApplicationConfigurator.class.getName(), null );
    this.applicationLauncher = applicationLauncher;
  }

  @Override
  public ApplicationConfigurator addingService( ServiceReference<ApplicationConfigurator> ref ) {
    return applicationLauncher.addConfigurator( ref );
  }

  @Override
  public void removedService( ServiceReference<ApplicationConfigurator> reference,
                              ApplicationConfigurator service )
  {
    applicationLauncher.removeConfigurator( service );
  }
}
