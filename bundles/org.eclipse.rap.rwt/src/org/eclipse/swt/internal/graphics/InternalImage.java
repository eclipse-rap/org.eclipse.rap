/*******************************************************************************
 * Copyright (c) 2010 EclipseSource
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;


/**
 * Instances of this class hold the data associated with a particular image.
 */
public final class InternalImage {

  private final String resourceName;
  private final int width;
  private final int height;

  InternalImage( final String resourceName, final int width, final int height ) {
    if( resourceName == null ) {
      throw new NullPointerException( "resourceName is null" );
    }
    if( width <= 0 || height <= 0 ) {
      throw new IllegalArgumentException( "Illegal size" );
    }
    this.resourceName = resourceName;
    this.width = width;
    this.height = height;
  }

  public Rectangle getBounds() {
    return new Rectangle( 0, 0, width, height );
  }

  public ImageData getImageData() {
    return RWTFactory.getImageDataFactory().findImageData( this );
  }

  public String getResourceName() {
    return resourceName;
  }
}
