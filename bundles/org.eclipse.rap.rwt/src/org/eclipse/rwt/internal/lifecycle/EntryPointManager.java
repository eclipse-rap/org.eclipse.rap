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
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.lifecycle.DefaultEntryPointFactory;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.IEntryPointFactory;


public class EntryPointManager {

  private final Map<String, IEntryPointFactory> entryPointsByPath;
  private final Map<String, IEntryPointFactory> entryPointsByName;

  public EntryPointManager() {
    entryPointsByPath = new HashMap<String, IEntryPointFactory>();
    entryPointsByName = new HashMap<String, IEntryPointFactory>();
  }

  public void registerByPath( String path, Class<? extends IEntryPoint> type ) {
    ParamCheck.notNull( path, "path" );
    checkValidPath( path );
    doRegisterByPath( path, new DefaultEntryPointFactory( type ) );
  }

  public void registerByPath( String path, IEntryPointFactory entryPointFactory ) {
    ParamCheck.notNull( path, "path" );
    ParamCheck.notNull( entryPointFactory, "entryPointFactory" );
    checkValidPath( path );
    doRegisterByPath( path, entryPointFactory );
  }

  public void registerByName( String name, Class<? extends IEntryPoint> type ) {
    ParamCheck.notNull( name, "name" );
    doRegisterByName( name, new DefaultEntryPointFactory( type ) );
  }

  public void registerByName( String name, IEntryPointFactory entryPointFactory ) {
    ParamCheck.notNull( name, "name" );
    ParamCheck.notNull( entryPointFactory, "entryPointFactory" );
    doRegisterByName( name, entryPointFactory );
  }

  public void deregisterAll() {
    synchronized( entryPointsByPath ) {
      entryPointsByPath.clear();
    }
    synchronized( entryPointsByName ) {
      entryPointsByName.clear();
    }
  }

  public IEntryPointFactory getFactoryByPath( String path ) {
    IEntryPointFactory result;
    synchronized( entryPointsByPath ) {
      result = entryPointsByPath.get( path );
    }
    return result;
  }

  public IEntryPointFactory getFactoryByName( String name ) {
    IEntryPointFactory result;
    synchronized( entryPointsByName ) {
      result = entryPointsByName.get( name );
    }
    return result;
  }

  public Collection<String> getServletPaths() {
    Collection<String> result;
    synchronized( entryPointsByPath ) {
      result = new ArrayList<String>( entryPointsByPath.keySet() );
    }
    return result;
  }

  private void doRegisterByPath( String key, IEntryPointFactory entryPointFactory ) {
    synchronized( entryPointsByPath ) {
      checkPathAvailable( key );
      entryPointsByPath.put( key, entryPointFactory );
    }
  }

  private void doRegisterByName( String key, IEntryPointFactory entryPointFactory ) {
    synchronized( entryPointsByName ) {
      checkNameAvailable( key );
      entryPointsByName.put( key, entryPointFactory );
    }
  }

  private void checkValidPath( String path ) {
    if( !path.startsWith( "/" ) ) {
      throw new IllegalArgumentException( "Path must start with '/': " + path );
    }
    if( path.endsWith( "/" ) ) {
      throw new IllegalArgumentException( "Path must not end with '/': " + path );
    }
    if( path.length() > 0 && path.substring( 1 ).contains( "/" ) ) {
      throw new IllegalArgumentException( "Nested paths not yet supported: " + path );
    }
  }

  private void checkPathAvailable( String key ) {
    if( entryPointsByPath.containsKey( key ) ) {
      String message = "Entry point already registered for path " + key;
      throw new IllegalArgumentException( message );
    }
  }

  private void checkNameAvailable( String key ) {
    if( entryPointsByName.containsKey( key ) ) {
      String message = "Entry point already registered for name: " + key;
      throw new IllegalArgumentException( message );
    }
  }

}
