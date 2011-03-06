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

import org.eclipse.rwt.internal.engine.RWTContext;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;


/**
 * This class provides access to shared Image instances.
 */
public final class ImageFactory {

  public static Image findImage( final String path ) {
    IResourceManager manager = ResourceManager.getInstance();
    return findImage( path, manager.getContextLoader() );
  }

  public static Image findImage( final String path,
                                 final ClassLoader imageLoader )
  {
    return getInstance().findImage( path, imageLoader );
  }

  public static Image findImage( final String path,
                                 final InputStream inputStream )
  {
    return getInstance().findImage( path, inputStream );
  }

  public static Image createImage( final Device device,
                                   final String key,
                                   final InputStream inputStream )
  {
    return getInstance().createImage( device, key, inputStream );
  }

  public static String getImagePath( final Image image ) {
    return getInstance().getImagePath( image );
  }

  static void clear() {
    getInstance().clear();
  }

  private static ImageFactoryInstance getInstance() {
    Object singleton = RWTContext.getSingleton( ImageFactoryInstance.class );
    return ( ImageFactoryInstance )singleton;
  }
  
  private ImageFactory() {
    // prevent instantiation
  }
}