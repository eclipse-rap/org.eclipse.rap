/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    Rüdiger Herrmann - bug 316961: Exception handling may fail in AdapterFactoryRegistry
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal;

import org.eclipse.rwt.internal.engine.ApplicationContext;


public final class AdapterFactoryRegistry {

  public static void add( final Class factoryClass, 
                          final Class adaptableClass )
  {
    getInstance().add( factoryClass, adaptableClass );
  }

  public static void register() {
    getInstance().register();
  }

  public static void clear() {
    getInstance().clear();
  }

  private static AdapterFactoryRegistryInstance getInstance() {
    Class singletonType = AdapterFactoryRegistryInstance.class;
    Object singleton = ApplicationContext.getSingleton( singletonType );
    return ( AdapterFactoryRegistryInstance )singleton;
  }

  private AdapterFactoryRegistry() {
    // prevent instance creation
  }
}
