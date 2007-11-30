/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.resources;

import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManagerFactory;


public final class ResourceManager {
  
  private static IResourceManagerFactory factory;
  private static IResourceManager _instance;
  
  private ResourceManager() {
    // prevent instance creation
  }

  public static void register( final IResourceManagerFactory factory ) {
    ParamCheck.notNull( factory, "factory" );
    if( ResourceManager.factory != null ) {
      String msg = "There is already an IResourceManagerFactory registered.";
      throw new IllegalStateException( msg );
    }
    ResourceManager.factory = factory; 
  }

  public synchronized static IResourceManager getInstance() {
    if( _instance == null ) {
      _instance = factory.create();
    }
    return _instance;
  }  
}
