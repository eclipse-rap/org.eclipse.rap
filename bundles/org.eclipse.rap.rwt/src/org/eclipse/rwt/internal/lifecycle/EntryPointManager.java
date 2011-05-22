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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.service.ISessionStore;


public class EntryPointManager {
  public static final String DEFAULT = "default";

  private static final String CURRENT_ENTRY_POINT
    = EntryPointManager.class.getName() + "#currentEntryPointName";
  
  public static String getCurrentEntryPoint() {
    ISessionStore session = ContextProvider.getSession();
    return ( String )session.getAttribute( EntryPointManager.CURRENT_ENTRY_POINT );    
  }
  
  private final Map registry;

  public EntryPointManager() {
    registry = new HashMap();
  }

  public void register( String name, Class clazz ) {
    ParamCheck.notNull( name, "name" );
    ParamCheck.notNull( clazz, "clazz" );
    checkClass( clazz );
    synchronized( registry ) {
      if( registry.containsKey( name ) ) {
        String msg = "Entry point already exists: " + name;
        throw new IllegalArgumentException( msg );
      }
      registry.put( name, clazz );
    }
  }

  public void deregister( String name ) {
    ParamCheck.notNull( name, "name" );
    synchronized( registry ) {
      checkNameExists( name );
      registry.remove( name );
    }
  }
  
  public void deregisterAll() {
    synchronized( registry ) {
      registry.clear();
    }
  }


  public int createUI( String name ) {
    ParamCheck.notNull( name, "name" );
    Class clazz;
    synchronized( registry ) {
      checkNameExists( name );
      clazz = ( Class )registry.get( name );
    }
    // no synchronization during instance creation to avoid lock in case
    // of expensive constructor operations
    IEntryPoint entryPoint = ( IEntryPoint )ClassUtil.newInstance( clazz );
    setCurrentEntryPoint( name );
    return entryPoint.createUI();
  }

  public String[] getEntryPoints() {
    synchronized( registry ) {
      String[] result = new String[ registry.keySet().size() ];
      registry.keySet().toArray( result );
      return result;
    }
  }

  private static void checkClass( Class clazz ) {
    if( !IEntryPoint.class.isAssignableFrom( clazz ) ) {
      String msg = "Entry point class must implement " + IEntryPoint.class.getName();
      throw new IllegalArgumentException( msg ) ;
    }
  }

  private void checkNameExists( String name ) {
    if( !registry.containsKey( name ) ) {
      String msg = "Entry point does not exist: " + name;
      throw new IllegalArgumentException( msg );
    }
  }

  private static void setCurrentEntryPoint( String name ) {
    ISessionStore session = ContextProvider.getSession();
    session.setAttribute( EntryPointManager.CURRENT_ENTRY_POINT, name );
  }
}