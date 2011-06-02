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

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.util.*;
import org.eclipse.rwt.internal.util.SharedInstanceBuffer.IInstanceCreator;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;


public class ImageFactory {
  
  private final SharedInstanceBuffer<String,Image> cache;
  
  public static String getImagePath( Image image ) {
    String result = null;
    if( image != null ) {
      String resourceName = image.internalImage.getResourceName();
      result = RWT.getResourceManager().getLocation( resourceName );
    }
    return result;
  }

  public ImageFactory() {
    cache = new SharedInstanceBuffer<String,Image>();
  }

  public Image findImage( String path ) {
    return findImage( path, RWT.getResourceManager().getContextLoader() );
  }

  public Image findImage( final String path, final ClassLoader imageLoader ) {
    return cache.get( path, new IInstanceCreator<Image>() {
      public Image createInstance() {
        return createImage( path, imageLoader );
      }
    } );
  }

  public Image findImage( final String path, final InputStream inputStream ) {
    return cache.get( path, new IInstanceCreator<Image>() {
      public Image createInstance() {
        return createImage( null, path, inputStream );
      }
    } );
  }

  public Image createImage( Device device, String key, InputStream inputStream ) {
    InternalImageFactory internalImageFactory = RWTFactory.getInternalImageFactory();
    InternalImage internalImage = internalImageFactory.findInternalImage( key, inputStream );
    return createImageInstance( device, internalImage );
  }

  private Image createImage( String path, ClassLoader imageLoader ) {
    InputStream inputStream = getInputStream( path, imageLoader );
    return createImage( null, path, inputStream );
  }

  private static Image createImageInstance( Device device, InternalImage internalImage ) {
    Class[] paramTypes = new Class[] { Device.class, InternalImage.class };
    Object[] paramValues = new Object[] { device, internalImage };
    return ( Image )ClassUtil.newInstance( Image.class, paramTypes, paramValues );
  }

  private static InputStream getInputStream( String path, ClassLoader imageLoader ) {
    IResourceManager manager = RWT.getResourceManager();
    ClassLoader bufferedContextLoader = manager.getContextLoader();
    if( imageLoader != null ) {
      manager.setContextLoader( imageLoader );
    }
    InputStream result;
    try {
      result = manager.getResourceAsStream( path );
    } finally {
      manager.setContextLoader( bufferedContextLoader );
    }
    return result;
  }
}
