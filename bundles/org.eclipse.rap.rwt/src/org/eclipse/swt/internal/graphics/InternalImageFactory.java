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

import java.io.*;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.util.SharedInstanceBuffer;
import org.eclipse.rwt.internal.util.SharedInstanceBuffer.IInstanceCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.*;


public class InternalImageFactory {
  private final SharedInstanceBuffer<String,InternalImage> cache;
  
  public InternalImageFactory() {
    cache = new SharedInstanceBuffer<String,InternalImage>();
  }

  // TODO [rst] If we do not rely on the fact that there is only one
  //            InternalImage instance, we could loose synchronization as in
  //            ImageDataFactory.
  public InternalImage findInternalImage( final String fileName ) {
    return cache.get( fileName, new IInstanceCreator<InternalImage>() {
        public InternalImage createInstance() {
          return createInternalImage( fileName );
        }
      } );
  }

  public InternalImage findInternalImage( InputStream stream ) {
    final BufferedInputStream bufferedStream = new BufferedInputStream( stream );
    final ImageData imageData = readImageData( bufferedStream );
    final String path = createGeneratedImagePath( imageData );
    return cache.get( path, new IInstanceCreator<InternalImage>() {
      public InternalImage createInstance() {
        return createInternalImage( path, bufferedStream, imageData );
      }
    } );
  }

  public InternalImage findInternalImage( final ImageData imageData ) {
    final String path = createGeneratedImagePath( imageData );
    return cache.get( path, new IInstanceCreator<InternalImage>() {
      public InternalImage createInstance() {
        InputStream stream = createInputStream( imageData );
        return createInternalImage( path, stream, imageData );
      }
    } );
  }

  InternalImage findInternalImage( String key, final InputStream inputStream ) {
    return cache.get( key, new IInstanceCreator<InternalImage>() {
      public InternalImage createInstance() {
        BufferedInputStream bufferedStream = new BufferedInputStream( inputStream );
        ImageData imageData = readImageData( bufferedStream );
        String path = createGeneratedImagePath( imageData );
        return createInternalImage( path, bufferedStream, imageData );
      }
    } );
  }

  static ImageData readImageData( InputStream stream ) throws SWTException {
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
    ImageData result = new ImageData( stream );
    try {
      stream.reset();
    } catch( IOException shouldNotHappen ) {
      String msg = "Could not reset input stream after reading image";
      throw new RuntimeException( msg, shouldNotHappen );
    }
    return result;
  }

  static InputStream createInputStream( ImageData imageData ) {
    ImageLoader imageLoader = new ImageLoader();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    imageLoader.data = new ImageData[] { imageData };
    imageLoader.save( outputStream, getOutputFormat( imageData ) );
    byte[] bytes = outputStream.toByteArray();
    return new ByteArrayInputStream( bytes );
  }

  private static InternalImage createInternalImage( String fileName ) {
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

  private static InternalImage createInternalImage( InputStream stream ) {
    InputStream bufferedStream = new BufferedInputStream( stream );
    ImageData imageData = readImageData( bufferedStream );
    String path = createGeneratedImagePath( imageData );
    return createInternalImage( path, bufferedStream, imageData );
  }

  private static InternalImage createInternalImage( String path,
                                                    InputStream stream,
                                                    ImageData imageData )
  {
    RWT.getResourceManager().register( path, stream );
    return new InternalImage( path, imageData.width, imageData.height );
  }

  private static int getOutputFormat( ImageData imageData ) {
    int result = imageData.type;
    if( imageData.type == SWT.IMAGE_UNDEFINED ) {
      result = SWT.IMAGE_PNG;
    }
    return result;
  }

  private static String createGeneratedImagePath( ImageData data ) {
    int hashCode = getHashCode( data );
    return "generated/" + Integer.toHexString( hashCode );
  }

  // TODO [rh] improve test coverage, getHashCode seems to be tested at most indirectly
  private static int getHashCode( ImageData imageData ) {
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
        result = result * 29 + imageData.palette.redMask;
        result = result * 29 + imageData.palette.greenMask;
        result = result * 29 + imageData.palette.blueMask;
      } else {
        RGB[] rgb = imageData.palette.getRGBs();
        for( int i = 0; i < rgb.length; i++ ) {
          result = result * 37 + rgb[ i ].red;
          result = result * 37 + rgb[ i ].green;
          result = result * 37 + rgb[ i ].blue;
        }
      }
    }
    result = result * 41 + imageData.alpha;
    result = result * 41 + imageData.transparentPixel;
    result = result * 41 + imageData.type;
    result = result * 41 + imageData.bytesPerLine;
    result = result * 41 + imageData.scanlinePad;
    result = result * 41 + imageData.maskPad;
    return result;
  }
}