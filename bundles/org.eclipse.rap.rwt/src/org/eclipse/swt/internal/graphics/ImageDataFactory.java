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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.graphics.ImageData;


/**
 * This class provides ImageData for internal images. Small image data objects
 * are being cached.
 */
final class ImageDataFactory {

  private static final ImageDataCache imageDataCache = new ImageDataCache();

  public static ImageData findImageData( final InternalImage internalImage ) {
    ImageData imageData;
    // Note [rst]: We don't need to synchronize access here. Since the creation
    //             of ImageData is deterministic, at worst it is done more than
    //             once when accessed concurrently.
    imageData = imageDataCache.getImageData( internalImage );
    if( imageData == null ) {
      imageData = createImageData( internalImage );
      if( imageData != null ) {
        imageDataCache.putImageData( internalImage, imageData );
      }
    }
    return imageData;
  }

  private static ImageData createImageData( final InternalImage internalImage )
  {
    ImageData imageData = null;
    String imagePath = internalImage.getResourceName();
    try {
      IResourceManager manager = ResourceManager.getInstance();
      InputStream inputStream = manager.getRegisteredContent( imagePath );
      if( inputStream != null ) {
        try {
          imageData = new ImageData( inputStream );
        } finally {
          inputStream.close();
        }
      }
    } catch( final IOException shouldNotHappen ) {
      String message = "Failed to close input stream";
      throw new RuntimeException( message, shouldNotHappen );
    }
    return imageData;
  }

  static void clear() {
    imageDataCache.clear();
  }

  private ImageDataFactory() {
    // prevent instantiation
  }
}
