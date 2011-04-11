/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;


public class ImageFactoryInstance {
  private final Map cache;
  private final Object cacheLock;
  
  private ImageFactoryInstance() {
    cache = new HashMap();
    cacheLock = new Object();
  }

  Image findImage( final String path, final ClassLoader imageLoader ) {
    Image result;
    synchronized( cacheLock ) {
      result = ( Image )cache.get( path );
      if( result == null ) {
        result = createImage( path, imageLoader );
        cache.put( path, result );
      }
    }
    return result;
  }

  Image findImage( final String path, final InputStream inputStream ) {
    Image result;
    synchronized( cacheLock ) {
      result = ( Image )cache.get( path );
      if( result == null ) {
        result = createImage( null, path, inputStream );
        cache.put( path, result );
      }
    }
    return result;
  }

  Image createImage( final Device device,
                     final String key,
                     final InputStream inputStream )
  {
    InternalImage internalImage
      = InternalImageFactory.findInternalImage( key, inputStream );
    return createImageInstance( device, internalImage );
  }

  String getImagePath( final Image image ) {
    String result = null;
    if( image != null ) {
      String resourceName = image.internalImage.getResourceName();
      result = ResourceManager.getInstance().getLocation( resourceName );
    }
    return result;
  }

  private Image createImage( final String path, final ClassLoader imageLoader )
  {
    InputStream inputStream = getInputStream( path, imageLoader );
    return createImage( null, path, inputStream );
  }

  private static Image createImageInstance( Device device, InternalImage internalImage ) {
    Class[] paramTypes = new Class[] { Device.class, InternalImage.class };
    Object[] paramValues = new Object[] { device, internalImage };
    return ( Image )ClassUtil.newInstance( Image.class, paramTypes, paramValues );
  }

  private static InputStream getInputStream( String path, ClassLoader imageLoader ) {
    IResourceManager manager = ResourceManager.getInstance();
    ClassLoader loaderBuffer = manager.getContextLoader();
    if( imageLoader != null ) {
      manager.setContextLoader( imageLoader );
    }
    InputStream result;
    try {
      result = manager.getResourceAsStream( path );
    } finally {
      manager.setContextLoader( loaderBuffer );
    }
    return result;
  }

  void clear() {
    synchronized( cacheLock ) {
      cache.clear();
    }
  }
}
