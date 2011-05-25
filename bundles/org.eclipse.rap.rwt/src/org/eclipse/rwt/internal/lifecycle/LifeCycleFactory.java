/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import org.eclipse.rwt.internal.ConfigurationReader;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.lifecycle.ILifeCycle;


public class LifeCycleFactory {
  private ConfigurationReader configurationReader;
  private LifeCycle lifeCycle;

  public ILifeCycle getLifeCycle() {
    return lifeCycle;
  }

  public void setConfigurationReader( ConfigurationReader configurationReader ) {
    this.configurationReader = configurationReader;
  }

  public void activate() {
    lifeCycle = instantiateLifeCycle();
  }

  public void deactivate() {
    lifeCycle = null;
  }

  //////////////////
  // helping methods
  
  private LifeCycle instantiateLifeCycle() {
    String className = getLifeCycleClassName();
    ClassLoader classLoader = getClass().getClassLoader();
    return ( LifeCycle )ClassUtil.newInstance( classLoader, className );
  }
  
  private String getLifeCycleClassName() {
    return configurationReader.getConfiguration().getLifeCycle();
  }
}