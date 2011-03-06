/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import org.eclipse.rwt.internal.engine.RWTContext;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManagerFactory;


public final class ResourceManager {
  private IResourceManagerFactory factory;
  private IResourceManager instance;
  

  public static void register( final IResourceManagerFactory factory ) {
    getSingleton().doRegister( factory ); 
  }
  
  public static void disposeOfResourceManagerFactory() {
    getSingleton().doDisposeOfResourceManagerFactory();
  }

  public synchronized static IResourceManager getInstance() {
    return getSingleton().doGetInstance();
  }

  private void doRegister( final IResourceManagerFactory factory ) {
    ParamCheck.notNull( factory, "factory" );
    
    if( this.factory != null ) {
      String msg = "There is already an IResourceManagerFactory registered.";
      throw new IllegalStateException( msg );
    }
    this.factory = factory;
  }

  
  private void doDisposeOfResourceManagerFactory() {
    this.factory = null;
    this.instance = null;
  }

  private IResourceManager doGetInstance() {
    if( instance == null ) {
      instance = factory.create();
    }
    return instance;
  }

  private static ResourceManager getSingleton() {
    return ( ResourceManager )RWTContext.getSingleton( ResourceManager.class );
  }

  private ResourceManager() {
    // prevent instance creation
  }
}