/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;


public class ImageDataCache {
  
  /** Maximum size of image data that is being cached */
  private static final int MAX_DATA_SIZE = 1024;
  
  /** Maximum number of elements to hold in the image data cache */
  private static final int MAX_FILL_SIZE = 100;
  
  private final Map cache;

  public ImageDataCache() {
    cache = new HashMap( MAX_FILL_SIZE / 4 );
  }

  /**
   * Retrieves a copy of the cached image data for the given image.
   * 
   * @param image the image whose image data are to be retrieved
   * @return a secure copy of the cached image data, or <code>null</code> if
   *         no image data have been cached for the given image
   */
  public ImageData getImageData( final Image image ) {
    if( image == null ) {
      throw new NullPointerException( "null argument" );
    }
    ImageData cached;
    synchronized( cache ) {
      cached = ( ImageData )cache.get( image );
    }
    return cached != null ? ( ImageData )cached.clone() : null;
  }

  /**
   * Stores the given image data for the given image in the cache.
   * 
   * @param image the image whose image data to store
   * @param imageData the image data to be stored
   */
  public synchronized void putImageData( final Image image,
                                         final ImageData imageData )
  {
    if( image == null || imageData == null ) {
      throw new NullPointerException( "null argument" );
    }
    if( imageData.data.length <= MAX_DATA_SIZE ) {
      synchronized( cache ) {
        if( cache.size() > MAX_FILL_SIZE ) {
          // TODO [rst] Implement replacement strategy (LRU or LFU)
//          cache.remove( key );
        }
        cache.put( image, imageData );
      }
    }
  }
}
