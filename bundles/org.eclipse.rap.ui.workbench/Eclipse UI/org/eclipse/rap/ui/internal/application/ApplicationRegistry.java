/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.ui.internal.application;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.rwt.internal.lifecycle.EntryPointUtil;


/*
 * Registers all available applications as entrypoints.
 */
public final class ApplicationRegistry {

  private static Map appEntryPointMapping = new HashMap();

  public static IApplication createApplication() {
    IApplication application;
    String currentEntryPointName = EntryPointUtil.getCurrentEntryPointName();
    Class clazz = ( Class )appEntryPointMapping.get( currentEntryPointName );
    try {
      application = ( IApplication )clazz.newInstance();
    } catch( Exception exception ) {
      String message = "Failed to create application " + currentEntryPointName;
      throw new IllegalArgumentException( message, exception );
    }
    return application;
  }

  public static void addMapping( String applicationParameter, Class clazz ) {
    appEntryPointMapping.put( applicationParameter, clazz );
  }
  
  public static void clear() {
    appEntryPointMapping.clear();
  }

  private ApplicationRegistry() {
    // prevent instantiation
  }

}
