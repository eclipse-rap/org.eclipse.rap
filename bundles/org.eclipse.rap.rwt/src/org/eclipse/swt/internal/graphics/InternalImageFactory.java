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

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.*;

/**
 * This class creates, caches and provides access to shared instances of
 * InternalImage.
 */
public final class InternalImageFactory {

  private static final Map cache = new HashMap();
  private static final Object cacheLock = new Object();

  // TODO [rst] If we do not rely on the fact that there is only one
  //            InternalImage instance, we could loose synchronization as in
  //            ImageDataFactory.

  public static InternalImage findInternalImage( final String fileName ) {
    InternalImage internalImage;
    synchronized( cacheLock ) {
      internalImage = ( InternalImage )cache.get( fileName );
      if( internalImage == null ) {
        internalImage = createInternalImage( fileName );
        cache.put( fileName, internalImage );
      }
    }
    return internalImage;
  }

  public static InternalImage findInternalImage( final InputStream stream ) {
    InternalImage internalImage;
    BufferedInputStream bufferedStream = new BufferedInputStream( stream );
    ImageData imageData = readImageData( bufferedStream );
    String path = createGeneratedImagePath( imageData );
    synchronized( cacheLock ) {
      internalImage = ( InternalImage )cache.get( path );
      if( internalImage == null ) {
        internalImage = createInternalImage( path, bufferedStream, imageData );
        cache.put( path, internalImage );
      }
    }
    return internalImage;
  }

  public static InternalImage findInternalImage( final ImageData imageData ) {
    InternalImage internalImage;
    String path = createGeneratedImagePath( imageData );
    synchronized( cacheLock ) {
      internalImage = ( InternalImage )cache.get( path );
      if( internalImage == null ) {
        InputStream stream = createInputStream( imageData );
        internalImage = createInternalImage( path, stream, imageData );
        cache.put( path, internalImage );
      }
    }
    return internalImage;
  }

  public static InternalImage findInternalImage( final String key,
                                                 final InputStream inputStream )
  {
    InternalImage internalImage;
    synchronized( cacheLock ) {
      internalImage = ( InternalImage )cache.get( key );
      if( internalImage == null ) {
        BufferedInputStream bufferedStream = new BufferedInputStream( inputStream );
        ImageData imageData = readImageData( bufferedStream );
        String path = createGeneratedImagePath( imageData );
        internalImage = createInternalImage( path, bufferedStream, imageData );
        cache.put( key, internalImage );
      }
    }
    return internalImage;
  }

  private static InternalImage createInternalImage( final String fileName ) {
    InternalImage result;
    try {
      FileInputStream stream = new FileInputStream( fileName );
      try {
        result = createInternalImage( stream );
      } finally {
        stream.close();
      }
    } catch( IOException e ) {
      throw new SWTException( SWT.ERROR_IO, e.getMessage() );
    }
    return result;
  }

  private static InternalImage createInternalImage( final InputStream stream ) {
    BufferedInputStream bufferedStream = new BufferedInputStream( stream );
    ImageData imageData = readImageData( bufferedStream );
    String path = createGeneratedImagePath( imageData );
    return createInternalImage( path, bufferedStream, imageData );
  }

  private static InternalImage createInternalImage( final String path,
                                                    final InputStream stream,
                                                    final ImageData imageData )
  {
    registerResource( path, stream );
    return new InternalImage( path, imageData.width, imageData.height );
  }

  static void registerResource( final String path, final InputStream stream ) {
    IResourceManager manager = ResourceManager.getInstance();
    manager.register( path, stream );
  }

  static ImageData readImageData( final BufferedInputStream stream )
    throws SWTException
  {
    ////////////////////////////////////////////////////////////////////////////
    // TODO: [fappel] Image size calculation and resource registration both
    //                read the input stream. Because of this I use a workaround
    //                with a BufferedInputStream. Resetting it after reading the
    //                image size enables the ResourceManager to reuse it for
    //                registration. Note that the order is crucial here, since
    //                the ResourceManager seems to close the stream (shrug).
    //                It would be nice to find a solution without reading the
    //                stream twice.
    stream.mark( Integer.MAX_VALUE );
    ImageData data = new ImageData( stream );
    try {
      stream.reset();
    } catch( final IOException shouldNotHappen ) {
      String msg = "Could not reset input stream after reading image";
      throw new RuntimeException( msg, shouldNotHappen );
    }
    return data;
  }

  static InputStream createInputStream( final ImageData imageData ) {
    ImageLoader imageLoader = new ImageLoader();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    imageLoader.data = new ImageData[] { imageData };
    imageLoader.save( outputStream, getOutputFormat( imageData ) );
    byte[] bytes = outputStream.toByteArray();
    InputStream inputStream = new ByteArrayInputStream( bytes );
    return inputStream;
  }

  private static int getOutputFormat( final ImageData imageData ) {
    int result = imageData.type;
    if( imageData.type == SWT.IMAGE_UNDEFINED ) {
      result = SWT.IMAGE_PNG;
    }
    return result;
  }

  private static String createGeneratedImagePath( final ImageData data ) {
    int hashCode = getHashCode( data );
    return "generated/" + Integer.toHexString( hashCode );
  }

  private static int getHashCode( final ImageData imageData ) {
    int result;
    if( imageData.data  == null ) {
      result = 0;
    } else {
      result = 1;
      for( int i = 0; i < imageData.data.length; i++ ) {
        result = 31 * result + imageData.data[ i ];
      }
    }
    if( imageData.palette != null  ) {
      if( imageData.palette.isDirect ) {
        result = result * 31 + imageData.palette.redMask;
        result = result * 31 + imageData.palette.greenMask;
        result = result * 31 + imageData.palette.blueMask;
      } else {
        RGB[] rgb = imageData.palette.getRGBs();
        for( int i = 0; i < rgb.length; i++ ) {
          result = result * 31 + rgb[ i ].red;
          result = result * 31 + rgb[ i ].green;
          result = result * 31 + rgb[ i ].blue;
        }
      }
    }
    result = result * 31 + imageData.alpha;
    result = result * 31 + imageData.transparentPixel;
    result = result * 31 + imageData.type;
    return result;
  }

  static void clear() {
    synchronized( cacheLock ) {
      cache.clear();
    }
  }

  private InternalImageFactory() {
    // prevent instantiation
  }
}
