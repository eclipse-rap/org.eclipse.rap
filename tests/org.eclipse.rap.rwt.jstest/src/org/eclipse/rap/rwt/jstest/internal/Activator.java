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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator {

  private HttpServiceTracker tracker;

  public void start( BundleContext context ) throws Exception {
    tracker = new HttpServiceTracker( context );
    tracker.open();
  }
  
  public void stop( BundleContext context ) throws Exception {
    tracker.close();
  }
  
}
