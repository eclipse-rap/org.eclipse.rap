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

import java.text.MessageFormat;

import org.eclipse.rwt.internal.*;
import org.eclipse.rwt.internal.service.ContextProvider;
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
  
  public ILifeCycle loadLifeCycle() {
    LifeCycle result = globalLifeCycle;
    if( result == null ) {
      String lifeCycleClassName = null;
      try {
        IConfiguration configuration = ConfigurationReader.getConfiguration();
        lifeCycleClassName = configuration.getLifeCycle();
        Class lifeCycleClass = Class.forName( lifeCycleClassName );
        result = ( LifeCycle )lifeCycleClass.newInstance();
        if( result.getScope().equals( Scope.APPLICATION ) ) {
          globalLifeCycle = result;
        }
      } catch( Exception ex ) {
        String text = "Could not load life cycle implementation {0}: {1}";
        Object[] args = new Object[] { lifeCycleClassName, ex.toString() };
        String msg = MessageFormat.format( text, args );
        throw new IllegalStateException( msg );
      }
    }
    return result;
  }

 private LifeCycleFactoryInstance() {
    // prevent instance creation
  }
}