/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;


class ApplicationConfigurationTracker
  extends ServiceTracker<ApplicationConfiguration, ApplicationConfiguration>
{

  private final ApplicationLauncherImpl applicationLauncher;

  ApplicationConfigurationTracker( BundleContext context,
                                  ApplicationLauncherImpl applicationLauncher )
  {
    super( context, ApplicationConfiguration.class.getName(), null );
    this.applicationLauncher = applicationLauncher;
  }

  @Override
  public ApplicationConfiguration addingService( ServiceReference<ApplicationConfiguration> ref ) {
    return applicationLauncher.addConfiguration( ref );
  }

  @Override
  public void removedService( ServiceReference<ApplicationConfiguration> reference,
                              ApplicationConfiguration service )
  {
    applicationLauncher.removeConfiguration( service );
  }
}
