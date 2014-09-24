/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.nebula.widget.grid.demo.examples;

import org.eclipse.rap.examples.IExampleContribution;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


public class Activator implements BundleActivator {

  private static final String EXAMPLE_CONTRIB = IExampleContribution.class.getName();

  private ServiceRegistration<?> registration;

  public void start( BundleContext context ) throws Exception {
    NebulaGridExampleContribution contribution = new NebulaGridExampleContribution();
    registration = context.registerService( EXAMPLE_CONTRIB, contribution, null );
  }

  public void stop( BundleContext context ) throws Exception {
    registration.unregister();
  }
}
