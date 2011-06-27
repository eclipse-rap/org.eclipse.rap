/*******************************************************************************
 * Copyright (c) 2008, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.examples.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator {

  private static Activator plugin;
  private ExampleContributionsTracker serviceTracker;

  public void start( BundleContext context ) throws Exception {
    plugin = this;
    serviceTracker = new ExampleContributionsTracker( context );
    serviceTracker.open();
  }

  public void stop( BundleContext context ) throws Exception {
    serviceTracker.close();
    serviceTracker = null;
    plugin = null;
  }

  public ExampleContributionsTracker getExampleContributions() {
    return serviceTracker;
  }

  public static Activator getDefault() {
    return plugin;
  }
}
