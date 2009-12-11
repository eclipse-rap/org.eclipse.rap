/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.rwt.internal.resources.JsConcatenator;
import org.eclipse.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.rwt.resources.IResourceManager;

public final class TestResourceManager
  implements IResourceManager, Adaptable
{

  private ClassLoader loader = Thread.currentThread().getContextClassLoader();

  public Object getAdapter( final Class adapter ) {
    return new JsConcatenator() {
      public void startJsConcatenation() {
      }
      public String getContent() {
        return "";
      }
      public String getLocation() {
        return "";
      }
    };
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

  public boolean isRegistered( final String name ) {
    return false;
  }

  public void register( final String name ) {
  }

  public void register( final String name, final InputStream is ) {
  }

  public void register( final String name, final String charset ) {
  }

  public void register( final String name,
                        final String charset,
                        final RegisterOptions options )
  {
  }

  public void register( String name,
                        InputStream is,
                        String charset,
                        RegisterOptions options )
  {
  }
  
  public boolean unregister( String name ) {
    return false;
  }

  public void setContextLoader( final ClassLoader contextLoader ) {
    loader = contextLoader;
  }

  public InputStream getRegisteredContent( final String name ) {
    return null;
  }
}