/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
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

import org.eclipse.rwt.internal.engine.ApplicationContext;
import org.eclipse.rwt.resources.IResource;


public final class ResourceRegistry {

  private ResourceRegistry() {
    // prevent instantiation
  }
  
  public static void add( final IResource resource ) {
    getInstance().add( resource );
  }
  
  public static IResource[] get() {
    return getInstance().get();
  }
  
  public static void clear() {
    getInstance().clear();
  }
  
  private static ResourceRegistryInstance getInstance() {
    Class singletonType = ResourceRegistryInstance.class;
    Object singleton = ApplicationContext.getSingleton( singletonType );
    return ( ResourceRegistryInstance )singleton;
  }
}
