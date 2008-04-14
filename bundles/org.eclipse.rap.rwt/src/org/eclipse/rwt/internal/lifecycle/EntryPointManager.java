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

package org.eclipse.rwt.internal.lifecycle;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.service.ISessionStore;


public final class EntryPointManager {

  public static final String DEFAULT = "default";
  
  private static final String CURRENT_ENTRY_POINT
    = EntryPointManager.class.getName() + ".CurrentEntryPointName";

  private static final Map registry = new HashMap();
  
  
  private EntryPointManager() {
    // prevent instantiation
  }
  
  public static void register( final String name, final Class clazz ) {
    synchronized( registry ) {
      ParamCheck.notNull( name, "name" );
      ParamCheck.notNull( clazz, "clazz" );
      if( !IEntryPoint.class.isAssignableFrom( clazz ) ) {
        String text = "The argument 'clazz' must implement {0}.";
        Object[] args = new Object[] { IEntryPoint.class.getName() };
        String mag = MessageFormat.format( text, args  );
        throw new IllegalArgumentException( mag ) ;
      }
      if( registry.containsKey( name ) ) {
        String text = "An entry point named ''{0}'' already exists.";
        String msg = MessageFormat.format( text, new Object[] { name } );
        throw new IllegalArgumentException( msg );
      }
      registry.put( name, clazz );
    }
  }
  
  public static void deregister( final String name ) {
    synchronized( registry ) {
      ParamCheck.notNull( name, "name" );
      if( !registry.containsKey( name ) ) {
        String text = "An entry point named ''{0}'' does not exist.";
        String msg = MessageFormat.format( text, new Object[] { name } );
        throw new IllegalArgumentException( msg );
      }
      registry.remove( name );
    }
  }
  
  public static int createUI( final String name ) {
    IEntryPoint entryPoint;
    Class clazz;
    ParamCheck.notNull( name, "name" );
    synchronized( registry ) {
      if( !registry.containsKey( name ) ) {
        String text = "An entry point named ''{0}'' does not exist.";
        String msg = MessageFormat.format( text, new Object[] { name } );
        throw new IllegalArgumentException( msg );
      }
      clazz = ( Class )registry.get( name );
    }
    // no synchronization during instance creation to avoid lock in case
    // of expensive constructor operations
    try {
      entryPoint = ( IEntryPoint )clazz.newInstance();
    } catch( Exception e ) {
      String text = "Failed to instantiate ''{0}''.";
      Object[] args = new Object[] { clazz.getName() };
      String msg = MessageFormat.format( text, args );
      throw new EntryPointInstantiationException( msg, e ) ;
    }      
    ContextProvider.getSession().setAttribute( CURRENT_ENTRY_POINT, name );
    return entryPoint.createUI();
  }

  public static String getCurrentEntryPoint() {
    ISessionStore session = ContextProvider.getSession();
    return ( String )session.getAttribute( CURRENT_ENTRY_POINT );
  }
  
  public static String[] getEntryPoints() {
    synchronized( registry ) {
      String[] result = new String[ registry.keySet().size() ];
      registry.keySet().toArray( result );
      return result;
    }
  }
}
