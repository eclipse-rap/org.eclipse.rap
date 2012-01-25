/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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

import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.lifecycle.DefaultEntryPointFactory;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.IEntryPointFactory;


public class EntryPointManager {

  public static final String DEFAULT = "default";
  private final Map<String, IEntryPointFactory> registry;

  public EntryPointManager() {
    registry = new HashMap<String, IEntryPointFactory>();
  }

  public void register( String name, Class<? extends IEntryPoint> type ) {
    ParamCheck.notNull( type, "type" );

    register( name, new DefaultEntryPointFactory( type ) );
  }

  public void register( String name, IEntryPointFactory entryPointFactory ) {
    ParamCheck.notNull( name, "name" );
    ParamCheck.notNull( entryPointFactory, "entryPointFactory" );

    synchronized( registry ) {
      if( registry.containsKey( name ) ) {
        String msg = "Entry point already registered: " + name;
        throw new IllegalArgumentException( msg );
      }
      registry.put( name, entryPointFactory );
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

  public IEntryPointFactory getEntryPointFactory( String name ) {
    IEntryPointFactory result;
    synchronized( registry ) {
      checkNameExists( name );
      result = registry.get( name );
    }
    return result;
  }

  public String[] getEntryPoints() {
    synchronized( registry ) {
      String[] result = new String[ registry.keySet().size() ];
      registry.keySet().toArray( result );
      return result;
    }
  }

  private void checkNameExists( String name ) {
    if( !registry.containsKey( name ) ) {
      String msg = "Entry point does not exist: " + name;
      throw new IllegalArgumentException( msg );
    }
  }
}