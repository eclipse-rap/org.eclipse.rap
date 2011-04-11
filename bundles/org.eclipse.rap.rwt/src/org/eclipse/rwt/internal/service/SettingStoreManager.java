/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import org.eclipse.rwt.internal.engine.ApplicationContext;
import org.eclipse.rwt.service.ISettingStore;
import org.eclipse.rwt.service.ISettingStoreFactory;


/**
 * An SettingStoreManager manages ISettingStores with the help of a
 * registered {@link ISettingStoreFactory}.
 */
public final class SettingStoreManager {

  public synchronized static ISettingStore getStore() {
    return getInstance().getStore();
  }

  public synchronized static void register(
    final ISettingStoreFactory factory )
  {
    getInstance().register( factory );
  }

  public synchronized static boolean hasFactory() {
    return getInstance().hasFactory();
  }

  //////////////////
  // helping methods

  static boolean isValidCookieValue( final String value ) {
    return getInstance().isValidCookieValue( value );
  }
  
  private static SettingStoreManagerInstance getInstance() {
    Class singletonType = SettingStoreManagerInstance.class;
    Object singleton = ApplicationContext.getSingleton( singletonType );
    return ( SettingStoreManagerInstance )singleton;
  }
  
  private SettingStoreManager() {
    // prevent instance creation
  }
}
