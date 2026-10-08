/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.swt.graphics.ImageData;


/**
 * Cache for small image data, mainly for decorator images.
 */
final class ImageDataCache {

  /** Maximum size of image data that is being cached */
  private static final int MAX_DATA_SIZE = 1024;

  private final Map<InternalImage,ImageData> cache;
  private final Object cacheLock;

  ImageDataCache() {
    cacheLock = new Object();
    cache = new HashMap<InternalImage,ImageData>( 25 );
  }

  ImageData getImageData( InternalImage internalImage ) {
    ParamCheck.notNull( internalImage, "internalImage" );
    ImageData cached;
    synchronized( cacheLock ) {
      cached = cache.get( internalImage );
    }
    return cached != null ? ( ImageData )cached.clone() : null;
  }

  void putImageData( InternalImage internalImage, ImageData imageData ) {
    ParamCheck.notNull( internalImage, "internalImage" );
    ParamCheck.notNull( imageData, "imageData" );
    if( imageData.data.length <= MAX_DATA_SIZE ) {
      synchronized( cacheLock ) {
        // TODO [rst] Implement replacement strategy (LRU or LFU)
        cache.put( internalImage, ( ImageData )imageData.clone() );
      }
    }
  }
}
