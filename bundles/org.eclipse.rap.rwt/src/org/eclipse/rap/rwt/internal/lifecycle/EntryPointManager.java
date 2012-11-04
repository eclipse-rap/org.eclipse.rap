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
package org.eclipse.rap.rwt.internal.lifecycle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.rap.rwt.lifecycle.DefaultEntryPointFactory;
import org.eclipse.rap.rwt.lifecycle.IEntryPoint;
import org.eclipse.rap.rwt.lifecycle.IEntryPointFactory;


public class EntryPointManager {

  private final Map<String, EntryPointRegistration> entryPointsByPath;

  public EntryPointManager() {
    entryPointsByPath = new HashMap<String, EntryPointRegistration>();
  }

  public void registerByPath( String path,
                              Class<? extends IEntryPoint> type,
                              Map<String, String> properties )
  {
    ParamCheck.notNull( path, "path" );
    checkValidPath( path );
    doRegisterByPath( path, new DefaultEntryPointFactory( type ), properties );
  }


  public void registerByPath( String path,
                              IEntryPointFactory entryPointFactory,
                              Map<String, String> properties )
  {
    ParamCheck.notNull( path, "path" );
    ParamCheck.notNull( entryPointFactory, "entryPointFactory" );
    checkValidPath( path );
    doRegisterByPath( path, entryPointFactory, properties );
  }

  public void deregisterAll() {
    synchronized( entryPointsByPath ) {
      entryPointsByPath.clear();
    }
  }

  public EntryPointRegistration getRegistrationByPath( String path ) {
    synchronized( entryPointsByPath ) {
      return entryPointsByPath.get( path );
    }
  }

  public Collection<String> getServletPaths() {
    Collection<String> result;
    synchronized( entryPointsByPath ) {
      result = new ArrayList<String>( entryPointsByPath.keySet() );
    }
    return result;
  }

  private void doRegisterByPath( String key,
                                 IEntryPointFactory factory,
                                 Map<String, String> properties )
  {
    synchronized( entryPointsByPath ) {
      checkPathAvailable( key );
      entryPointsByPath.put( key, new EntryPointRegistration( factory, properties ) );
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

  private void checkPathAvailable( String path ) {
    if( entryPointsByPath.containsKey( path ) ) {
      throw new IllegalArgumentException( "Entry point already registered for path " + path );
    }
  }

}
