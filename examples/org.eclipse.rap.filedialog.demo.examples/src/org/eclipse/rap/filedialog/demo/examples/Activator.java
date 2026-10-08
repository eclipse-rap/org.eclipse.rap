/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.filedialog.demo.examples;

import org.eclipse.rap.examples.IExampleContribution;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


public class Activator implements BundleActivator {

  private static final String EXAMPLE_CONTRIB = IExampleContribution.class.getName();
  static final String BUNDLE_ID = "org.eclipse.rap.rwt.supplemental.filedialog.demo";

  private ServiceRegistration<?> registration;

  @Override
  public void start( BundleContext context ) throws Exception {
    FileUploadExampleContribution contribution = new FileUploadExampleContribution();
    registration = context.registerService( EXAMPLE_CONTRIB, contribution, null );
  }

  @Override
  public void stop( BundleContext context ) throws Exception {
    registration.unregister();
    registration = null;
  }

}
