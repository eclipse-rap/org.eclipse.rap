/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;


/**
 * This class provides access to shared Image instances.
 */
public final class ImageFactory {

  private static final Map cache = new HashMap();
  private static final Object cacheLock = new Object();

  public static Image findImage( final String path ) {
    IResourceManager manager = ResourceManager.getInstance();
    return findImage( path, manager.getContextLoader() );
  }

  public static Image findImage( final String path,
                                 final ClassLoader imageLoader )
  {
    Image image;
    synchronized( cacheLock ) {
      image = ( Image )cache.get( path );
      if( image == null ) {
        image = createImage( path, imageLoader );
        cache.put( path, image );
      }
    }
    return image;
  }

  public static Image findImage( final String path,
                                 final InputStream inputStream )
  {
    Image image;
    synchronized( cacheLock ) {
      image = ( Image )cache.get( path );
      if( image == null ) {
        image = createImage( null, path, inputStream );
        cache.put( path, image );
      }
    }
    return image;
  }

  public static Image createImage( final Device device,
                                   final String key,
                                   final InputStream inputStream )
  {
    InternalImage internalImage
      = InternalImageFactory.findInternalImage( key, inputStream );
    return createImageInstance( device, internalImage );
  }

  public static String getImagePath( final Image image ) {
    String result = null;
    if( image != null ) {
      String resourceName = image.internalImage.getResourceName();
      result = ResourceManager.getInstance().getLocation( resourceName );
    }
    return result;
  }

  private static Image createImage( final String path,
                                    final ClassLoader imageLoader )
  {
    InputStream inputStream = getInputStream( path, imageLoader );
    return createImage( null, path, inputStream );
  }

  private static Image createImageInstance( final Device device,
                                            final InternalImage internalImage )
  {
    Image result;
    try {
      Class imageClass = Image.class;
      Class[] paramList = new Class[] { Device.class, InternalImage.class };
      Constructor constructor = imageClass.getDeclaredConstructor( paramList );
      constructor.setAccessible( true );
      Object[] args = new Object[] { device, internalImage };
      result = ( Image )constructor.newInstance( args );
    } catch( final Exception e ) {
      throw new RuntimeException( "Failed to instantiate Image", e );
    }
    return result;
  }

  private static InputStream getInputStream( final String path,
                                             final ClassLoader imageLoader )
  {
    IResourceManager manager = ResourceManager.getInstance();
    ClassLoader loaderBuffer = manager.getContextLoader();
    if( imageLoader != null ) {
      manager.setContextLoader( imageLoader );
    }
    InputStream inputStream;
    try {
      inputStream = manager.getResourceAsStream( path );
    } finally {
      manager.setContextLoader( loaderBuffer );
    }
    return inputStream;
  }

  static void clear() {
    synchronized( cacheLock ) {
      cache.clear();
    }
  }

  private ImageFactory() {
    // prevent instantiation
  }
}
