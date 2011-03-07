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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.graphics.ImageData;


public class ImageDataFactoryInstance {
  private final ImageDataCache imageDataCache;
  
  ImageDataFactoryInstance() {
    imageDataCache = new ImageDataCache();
  }

  ImageData findImageData( final InternalImage internalImage ) {
    ImageData result;
    // Note [rst]: We don't need to synchronize access here. Since the creation
    //             of ImageData is deterministic, at worst it is done more than
    //             once when accessed concurrently.
    result = imageDataCache.getImageData( internalImage );
    if( result == null ) {
      result = createImageData( internalImage );
      if( result != null ) {
        imageDataCache.putImageData( internalImage, result );
      }
    }
    return result;
  }

  private static ImageData createImageData( InternalImage internalImage ) {
    ImageData result = null;
    String imagePath = internalImage.getResourceName();
    try {
      IResourceManager manager = ResourceManager.getInstance();
      InputStream inputStream = manager.getRegisteredContent( imagePath );
      if( inputStream != null ) {
        try {
          result = new ImageData( inputStream );
        } finally {
          inputStream.close();
        }
      }
    } catch( final IOException shouldNotHappen ) {
      String message = "Failed to close input stream";
      throw new RuntimeException( message, shouldNotHappen );
    }
    return result;
  }

  void clear() {
    imageDataCache.clear();
  }
}
