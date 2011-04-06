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
import org.eclipse.rwt.internal.IConfiguration;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.lifecycle.ILifeCycle;
import org.eclipse.rwt.service.ISessionStore;


public class LifeCycleFactoryInstance {
  private LifeCycle globalLifeCycle;

  public ILifeCycle getLifeCycle() {
    ISessionStore session = ContextProvider.getSession();
    String id = LifeCycle.class.getName();
    ILifeCycle result = ( ILifeCycle )session.getAttribute( id );
    if( result == null ) {
      result = loadLifeCycle();
      session.setAttribute( id, result );
    }
    return result;
  }
  
  public void destroy() {
    globalLifeCycle = null;
  }
  
  private ILifeCycle loadLifeCycle() {
    LifeCycle result = globalLifeCycle;
    if( result == null ) {
      IConfiguration configuration = ConfigurationReader.getConfiguration();
      String lifeCycleClassName = configuration.getLifeCycle();
      ClassLoader classLoader = LifeCycleFactoryInstance.class.getClassLoader();
      result = ( LifeCycle )ClassUtil.newInstance( classLoader, lifeCycleClassName );
      if( result.getScope().equals( Scope.APPLICATION ) ) {
        globalLifeCycle = result;
      }
    }
    return result;
  }

  private LifeCycleFactoryInstance() {
    // prevent instance creation
  }
}