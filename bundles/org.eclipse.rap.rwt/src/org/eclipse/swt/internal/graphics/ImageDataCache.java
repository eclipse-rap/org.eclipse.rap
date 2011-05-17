/*******************************************************************************
 * Copyright (c) 2008, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.ImageData;


/**
 * Cache for small image data, mainly for decorator images.
 */
final class ImageDataCache {

  /** Maximum size of image data that is being cached */
  private static final int MAX_DATA_SIZE = 1024;

  private final Map cache;
  private final Object cacheLock;

  public ImageDataCache() {
    cacheLock = new Object();
    cache = new HashMap( 25 );
  }

  public ImageData getImageData( final InternalImage internalImage ) {
    if( internalImage == null ) {
      throw new NullPointerException( "internalImage" );
    }
    ImageData cached;
    synchronized( cacheLock ) {
      cached = ( ImageData )cache.get( internalImage );
    }
    return cached != null ? ( ImageData )cached.clone() : null;
  }

  public void putImageData( final InternalImage internalImage,
                            final ImageData imageData )
  {
    if( internalImage == null ) {
      throw new NullPointerException( "internalImage" );
    }
    if( imageData == null ) {
      throw new NullPointerException( "imageData" );
    }
    if( imageData.data.length <= MAX_DATA_SIZE ) {
      synchronized( cacheLock ) {
        // TODO [rst] Implement replacement strategy (LRU or LFU)
        cache.put( internalImage, imageData.clone() );
      }
    }
  }
}
