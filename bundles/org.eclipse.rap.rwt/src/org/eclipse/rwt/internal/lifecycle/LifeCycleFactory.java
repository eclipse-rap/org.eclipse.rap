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

import org.eclipse.rwt.internal.IConfiguration;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.lifecycle.ILifeCycle;


public class LifeCycleFactory {
  private static final String ATTR_SESSION_LIFE_CYCLE
    = LifeCycle.class.getName() + "#sessionLifeCycle";
  
  private LifeCycle applicationScopedLifeCycle;

  public ILifeCycle getLifeCycle() {
    ILifeCycle result;
    if( applicationScopedLifeCycle != null ) {
      result = applicationScopedLifeCycle;
    } else {
      result = getSessionLifeCycle();
    }
    if( result == null ) {
      result = loadLifeCycle();
    }
    return result;
  }

  public void destroy() {
    applicationScopedLifeCycle = null;
  }

  private ILifeCycle loadLifeCycle() {
    LifeCycle result = instantiateLifeCycle();
    if( Scope.APPLICATION.equals( result.getScope() ) ) {
      applicationScopedLifeCycle = result;
    }else {
      setSessionLifeCycle( result );
    }
    return result;
  }

  private LifeCycle instantiateLifeCycle() {
    String className = getLifeCycleClassName();
    ClassLoader classLoader = getClass().getClassLoader();
    return ( LifeCycle )ClassUtil.newInstance( classLoader, className );
  }

  private static void setSessionLifeCycle( ILifeCycle result ) {
    ContextProvider.getSession().setAttribute( ATTR_SESSION_LIFE_CYCLE, result );
  }

  private static ILifeCycle getSessionLifeCycle() {
    return ( ILifeCycle )ContextProvider.getSession().getAttribute( ATTR_SESSION_LIFE_CYCLE );
  }
  
  private static String getLifeCycleClassName() {
    IConfiguration configuration = RWTFactory.getConfigurationReader().getConfiguration();
    return configuration.getLifeCycle();
  }
}