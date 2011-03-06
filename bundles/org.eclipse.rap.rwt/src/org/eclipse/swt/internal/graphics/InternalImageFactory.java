/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
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

import java.io.BufferedInputStream;
import java.io.InputStream;

import org.eclipse.rwt.internal.engine.RWTContext;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.ImageData;

/**
 * This class creates, caches and provides access to shared instances of
 * InternalImage.
 */
public final class InternalImageFactory {

  public static InternalImage findInternalImage( final String fileName ) {
    return getInstance().findInternalImage( fileName );
  }

  public static InternalImage findInternalImage( final InputStream stream ) {
    return getInstance().findInternalImage( stream );
  }

  public static InternalImage findInternalImage( final ImageData imageData ) {
    return getInstance().findInternalImage( imageData );
  }

  public static InternalImage findInternalImage( final String key,
                                                 final InputStream inputStream )
  {
    return getInstance().findInternalImage( key, inputStream );
  }

  static void registerResource( final String path, final InputStream stream ) {
    getInstance().registerResource( path, stream );
  }

  static ImageData readImageData( final BufferedInputStream stream )
    throws SWTException
  {
    return getInstance().readImageData( stream );
  }

  static InputStream createInputStream( final ImageData imageData ) {
    return getInstance().createInputStream( imageData );
  }

  static void clear() {
    getInstance().clear();
  }

  private static InternalImageFactoryInstance getInstance() {
    Class singletonType = InternalImageFactoryInstance.class;
    Object singleton = RWTContext.getSingleton( singletonType );
    return ( InternalImageFactoryInstance )singleton;
  }
  
  private InternalImageFactory() {
    // prevent instantiation
  }
}
