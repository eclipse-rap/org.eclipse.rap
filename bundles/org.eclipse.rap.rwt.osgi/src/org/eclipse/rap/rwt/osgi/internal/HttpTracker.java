/*******************************************************************************
 * Copyright (c) 2011, 2024 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.servlet.runtime.HttpServiceRuntime;
import org.osgi.util.tracker.ServiceTracker;


class HttpTracker extends ServiceTracker<HttpServiceRuntime, HttpServiceRuntime> {

  private final ApplicationLauncherImpl applicationLauncher;

  HttpTracker( BundleContext context, ApplicationLauncherImpl applicationLauncher ) {
    super( context, HttpServiceRuntime.class.getName(), null );
    this.applicationLauncher = applicationLauncher;
  }

  @Override
  public HttpServiceRuntime addingService( ServiceReference<HttpServiceRuntime> reference ) {
    return applicationLauncher.addHttpService( reference );
  }

  @Override
  public void removedService( ServiceReference<HttpServiceRuntime> reference, HttpServiceRuntime service ) {
    applicationLauncher.removeHttpService( service );
  }
}
