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

import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Display;


/**
 * TODO [rh] JavaDoc
 */
public final class EntryPointManager {

  public static final String DEFAULT = "default";

  private static final Map registry = new HashMap();
  
  
  private EntryPointManager() {
    // prevent instantiation
  }
  
  public static synchronized void register( final String name, 
                                            final Class clazz ) 
  {
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
  
  public static synchronized void deregister( final String name ) {
    ParamCheck.notNull( name, "name" );
    if( !registry.containsKey( name ) ) {
      String text = "An entry point named ''{0}'' does not exist.";
      String msg = MessageFormat.format( text, new Object[] { name } );
      throw new IllegalArgumentException( msg );
    }
    registry.remove( name );
  }
  
  public static synchronized Display createUI( final String name ) {
    ParamCheck.notNull( name, "name" );
    if( !registry.containsKey( name ) ) {
      String text = "An entry point named ''{0}'' does not exist.";
      String msg = MessageFormat.format( text, new Object[] { name } );
      throw new IllegalArgumentException( msg );
    }
    Class clazz = ( Class )registry.get( name );
    IEntryPoint entryPoint;
    try {
      entryPoint = ( IEntryPoint )clazz.newInstance();
    } catch( Exception e ) {
      String text = "Failed to instantiate ''{0}''.";
      Object[] args = new Object[] { clazz.getName() };
      String msg = MessageFormat.format( text, args );
      throw new EntryPointInstantiationException( msg, e ) ;
    }
    Display result = entryPoint.createUI();
    if( result == null ) {
      String text = "The entry point ''{0}'' did not return a display.";
      Object[] args = new Object[] { clazz.getName() };
      String msg = MessageFormat.format( text, args );
      throw new IllegalStateException( msg );
    }
    return result;
  }
}
