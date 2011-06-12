/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.eclipse.rwt.engine.ContextControl;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.rwt.resources.IResourceManager;


public class TestResourceManager implements IResourceManager {
  private ClassLoader loader = Thread.currentThread().getContextClassLoader();

  private final Set<String> registeredResources;

  public TestResourceManager() {
    registeredResources = new HashSet<String>();
  }

  public String getCharset( final String name ) {
    return null;
  }

  public ClassLoader getContextLoader() {
    return loader;
  }

  public String getLocation( final String name ) {
    return ResourceManagerImpl.RESOURCES + "/" + name;
  }

  public URL getResource( final String name ) {
    URL result = null;
    if( loader != null ) {
      result = loader.getResource( name );
    }
    return result;
  }

  public InputStream getResourceAsStream( final String name ) {
    InputStream result = null;
    if( loader != null ) {
      result = loader.getResourceAsStream( name );
    }
    return result;
  }

  public Enumeration getResources( final String name ) throws IOException {
    Enumeration result = null;
    if( loader != null ) {
      result = loader.getResources( name );
    }
    return result;
  }

  public boolean isRegistered( String name ) {
    return registeredResources.contains( name );
  }

  public void register( String name ) {
    createResourcesDirectory();
    registeredResources.add( name );
  }

  public void register( String name, InputStream is ) {
    createResourcesDirectory();
    registeredResources.add( name );
  }

  public void register( String name, String charset ) {
    createResourcesDirectory();
    registeredResources.add( name );
  }

  public void register( String name, String charset, RegisterOptions options ) {
    createResourcesDirectory();
    registeredResources.add( name );
  }

  public void register( String name, InputStream is, String charset, RegisterOptions options ) {
    createResourcesDirectory();
    registeredResources.add( name );
  }
  
  public boolean unregister( String name ) {
    return registeredResources.remove( name );
  }

  public void setContextLoader( final ClassLoader contextLoader ) {
    loader = contextLoader;
  }

  public InputStream getRegisteredContent( final String name ) {
    return null;
  }

  private void createResourcesDirectory() {
    if( registeredResources.isEmpty() ) {
      File contextDirectory = RWTFactory.getConfiguration().getContextDirectory();
      File file = new File( contextDirectory, ContextControl.RESOURCES );
      if( !file.exists() ) {
        file.mkdirs();
      }
    }
  }
}