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

import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManagerFactory;


public class ResourceManagerProvider {
  private IResourceManagerFactory factory;
  private IResourceManager instance;
  
  public synchronized void registerFactory( IResourceManagerFactory factory ) {
    ParamCheck.notNull( factory, "factory" );
    checkNoFactoryRegistered();
    this.factory = factory;
  }
  
  public void deregisterFactory() {
    checkFactoryRegistered();
    this.factory = null;
    this.instance = null;
  }

  public synchronized IResourceManager getResourceManager() {
    if( instance == null ) {
      checkFactoryRegistered();
      instance = factory.create();
    }
    return instance;
  }

  private void checkFactoryRegistered() {
    if( factory == null ) {      
      throw new IllegalStateException( "There is no IResourceManagerFactory registered." );
    }
  }

  private void checkNoFactoryRegistered() {
    if( factory != null ) {
      throw new IllegalStateException( "There is already an IResourceManagerFactory registered." );
    }
  }
}