/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying material
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.application;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.rap.rwt.application.EntryPoint;


public final class EntryPointApplicationWrapper implements EntryPoint {

  private static final IApplicationContext context = new RAPApplicationContext();

  private final Class<? extends IApplication> applicationClass;

  public EntryPointApplicationWrapper( Class<? extends IApplication> applicationClass ) {
    this.applicationClass = applicationClass;
  }

  /*
   * Note [rst]: We don't call IApplication#stop(). According to the documentation, stop() is "only
   * called to force an application to exit" and "not called if an application exits normally from
   * start()".
   * See also https://bugs.eclipse.org/bugs/show_bug.cgi?id=372946
   */
  public int createUI() {
    int result = 0;
    IApplication application = createApplication();
    try {
      Object exitCode = application.start( context );
      if( exitCode instanceof Integer ) {
        result = ( ( Integer )exitCode ).intValue();
      }
    } catch( Exception exception  ) {
      String message = "Exception while executing application " + applicationClass.getName();
      throw new RuntimeException( message, exception );
    }
    return result;
  }

  private IApplication createApplication() {
    IApplication application;
    try {
      application = applicationClass.newInstance();
    } catch( Exception exception ) {
      String message = "Failed to create application " + applicationClass.getName();
      throw new IllegalArgumentException( message, exception );
    }
    return application;
  }
}
