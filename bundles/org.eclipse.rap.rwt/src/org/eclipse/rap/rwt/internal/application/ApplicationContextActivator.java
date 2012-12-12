/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.application;

import org.eclipse.swt.internal.widgets.displaykit.ClientResources;


public class ApplicationContextActivator {

  // TODO [fappel]: this flag is used to skip resource registration. Think about
  //                a less intrusive solution.
  // [rst] made public to allow access from testfixture in OSGi (bug 391510)
  public static boolean skipResoureRegistration;
  // TODO [fappel]: this flag is used to skip resource deletion. Think about
  //                a less intrusive solution.
  // [rst] made public to allow access from testfixture in OSGi (bug 391510)
  public static boolean skipResoureDeletion;

  private final ApplicationContextImpl applicationContext;

  ApplicationContextActivator( ApplicationContextImpl applicationContext ) {
    this.applicationContext = applicationContext;
  }

  void activate() {
    ApplicationContextUtil.runWith( applicationContext, new Runnable() {
      public void run() {
        activateInstances();
      }
    } );
  }

  private void activateInstances() {
    applicationContext.getStartupPage().activate();
    applicationContext.getLifeCycleFactory().activate();
    // Note: order is crucial here
    applicationContext.getThemeManager().activate();
    if( !skipResoureRegistration ) {
      new ClientResources( applicationContext.getResourceManager(),
                           applicationContext.getThemeManager() ).registerResources();
    }
    applicationContext.getResourceRegistry().registerResources();
    applicationContext.getClientSelector().activate();
  }

  void deactivate() {
    ApplicationContextUtil.runWith( applicationContext, new Runnable() {
      public void run() {
        deactivateInstances();
      }
    } );
  }

  private void deactivateInstances() {
    applicationContext.getStartupPage().deactivate();
    applicationContext.getLifeCycleFactory().deactivate();
    applicationContext.getServiceManager().clear();
    applicationContext.getThemeManager().deactivate();
    if( !skipResoureDeletion ) {
      applicationContext.getResourceDirectory().deleteDirectory();
    }
  }

}
