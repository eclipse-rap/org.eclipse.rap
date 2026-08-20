/*******************************************************************************
 * Copyright (c) 2008, 2024 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 *    EclipseSource - ongoing implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.servlet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.osgi.ApplicationLauncher;
import org.eclipse.rap.rwt.osgi.ApplicationReference;

import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import org.osgi.service.servlet.runtime.HttpServiceRuntime;

public class HttpServiceTracker extends ServiceTracker<HttpServiceRuntime, HttpServiceRuntime> {

  private ApplicationLauncherTracker applicationLauncherTracker;
  private ApplicationLauncher applicationLauncher;
  private ApplicationReference applicationReference;

  public HttpServiceTracker( BundleContext context ) {
    super( context, HttpServiceRuntime.class.getName(), null );
  }

  @Override
  public HttpServiceRuntime addingService( ServiceReference<HttpServiceRuntime> reference ) {
	HttpServiceRuntime result = context.getService( reference );
    applicationLauncherTracker = new ApplicationLauncherTracker( context );
    applicationLauncherTracker.open();
    applicationReference = startApplication( reference, result );
    return result;
  }

  @Override
  public void removedService( ServiceReference<HttpServiceRuntime> reference, HttpServiceRuntime service ) {
    applicationReference.stopApplication();
    applicationLauncherTracker.close();
    super.removedService( reference, service );
  }

  private ApplicationReference startApplication( ServiceReference<HttpServiceRuntime> httpServiceReference,
		  HttpServiceRuntime service )
  {
    ApplicationConfiguration configuration
      = new WorkbenchApplicationConfiguration( httpServiceReference );
    String contextDirectory = findContextPath().toString();
    return applicationLauncher.launch( configuration, service, null, contextDirectory );
  }

  private static IPath findContextPath() {
    Bundle bundle = Platform.getBundle( PlatformUI.PLUGIN_ID );
    IPath stateLocation = Platform.getStateLocation( bundle );
    return stateLocation.append( "context" );
  }

  private class ApplicationLauncherTracker
    extends ServiceTracker<ApplicationLauncher, ApplicationLauncher>
  {

    private ApplicationLauncherTracker( BundleContext context ) {
      super( context, ApplicationLauncher.class.getName(), null );
    }

    @Override
    public ApplicationLauncher addingService( ServiceReference<ApplicationLauncher> reference ) {
      ApplicationLauncher result = super.addingService( reference );
      applicationLauncher = context.getService( reference );
      HttpServiceTracker.super.open();
      return result;
    }

    @Override
    public void removedService( ServiceReference<ApplicationLauncher> reference,
                                ApplicationLauncher service )
    {
      applicationLauncher = null;
      super.removedService( reference, service );
    }
  }
}
