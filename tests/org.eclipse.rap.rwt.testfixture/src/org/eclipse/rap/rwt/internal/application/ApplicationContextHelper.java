/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.application;

import org.eclipse.rap.rwt.internal.application.ApplicationContext;
import org.eclipse.rap.rwt.testfixture.internal.TestResourceManager;


public class ApplicationContextHelper {

  public static void setSkipResoureRegistration( boolean ignore ) {
    ApplicationContext.skipResoureRegistration = ignore;
  }

  public static void setSkipResoureDeletion( boolean ignore ) {
    ApplicationContext.skipResoureDeletion = ignore;
  }

  public static void useDefaultResourceManager() {
    ApplicationContext.testResourceManager = null;
  }

  public static void useTestResourceManager() {
    ApplicationContext.testResourceManager = new TestResourceManager();
  }
}
