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
package org.eclipse.rwt.internal.lifecycle;

import org.eclipse.rwt.internal.engine.RWTContext;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.service.ISessionStore;


public final class EntryPointManager {
  public static final String DEFAULT = "default";
  static final String CURRENT_ENTRY_POINT
    = EntryPointManager.class.getName() + ".CurrentEntryPointName";

  public static void register( final String name, final Class clazz ) {
    getInstance().register( name, clazz );
  }
  
  public static void deregister( final String name ) {
    getInstance().deregister( name );
  }
  
  public static int createUI( final String name ) {
    return getInstance().createUI( name );
  }
  
  public static String[] getEntryPoints() {
    return getInstance().getEntryPoints();
  }

  public static String getCurrentEntryPoint() {
    ISessionStore session = ContextProvider.getSession();
    return ( String )session.getAttribute( CURRENT_ENTRY_POINT );
  }
  
  private static EntryPointManagerInstance getInstance() {
    Class singletonType = EntryPointManagerInstance.class;
    Object singleton = RWTContext.getSingleton( singletonType );
    return ( EntryPointManagerInstance )singleton;
  }

  private EntryPointManager() {
    // prevent instantiation
  }
}
