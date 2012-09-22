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

import java.io.File;

import org.eclipse.rap.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.swt.internal.widgets.displaykit.ClientResources;


public class ApplicationContextActivator {

  // TODO [fappel]: this flag is used to skip resource registration. Think about
  //                a less intrusive solution.
  static boolean skipResoureRegistration;
  // TODO [fappel]: this flag is used to skip resource deletion. Think about
  //                a less intrusive solution.
  static boolean skipResoureDeletion;

  private final ApplicationContext applicationContext;

  ApplicationContextActivator( ApplicationContext applicationContext ) {
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
    applicationContext.getLifeCycleFactory().activate();
    // Note: order is crucial here
    applicationContext.getJSLibraryConcatenator().startJSConcatenation();
    applicationContext.getThemeManager().activate();
    if( !skipResoureRegistration ) {
      new ClientResources( applicationContext.getResourceManager(),
                           applicationContext.getThemeManager() ).registerResources();
    }
    applicationContext.getJSLibraryConcatenator().activate();
  }

  void deactivate() {
    ApplicationContextUtil.runWith( applicationContext, new Runnable() {
      public void run() {
        deactivateInstances();
      }
    } );
  }

  private void deactivateInstances() {
    applicationContext.getJSLibraryConcatenator().deactivate();
    applicationContext.getLifeCycleFactory().deactivate();
    applicationContext.getServiceManager().clear();
    applicationContext.getThemeManager().deactivate();
    // TODO [fappel]: think of better solution. This maps directly to the
    //                default resource manager implementation while
    //                the resource manager factory is configurable. Is
    //                the latter really necessary since the only other factory
    //                in use is for testing purpose (unfortunately API).
    if( !skipResoureDeletion ) {
      File resourcesDir = new File( applicationContext.getConfiguration().getContextDirectory(),
                                    ResourceManagerImpl.RESOURCES );
      ApplicationContextUtil.delete( resourcesDir );
    }
  }

}
