/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.addons.camera;

import java.io.Serializable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;

/**
 * <p>
 * The <code>CameraOptions</code> is used to configure a <code>Camera</code>
 * object when taking a picture via {@link Camera#takePicture(CameraOptions)}.
 * </p>
 *
 * @see Camera
 * @since 4.4
 */
@SuppressWarnings("serial")
public class CameraOptions implements Serializable {

  public static final CameraOptions NONE = new CameraOptions();
  private Point resolution;
  private float quality;

  public CameraOptions() {
    quality = 1.0F;
  }
  
  /**
   * <p>
   * Defines the preferred resolution for images. When possible images will have
   * the configured size when sending them to the server.
   * </p>
   *
   * @param x Preferred width. Must be positive.
   * @param y Preferred height. Must be positive.
   */
  public void setResolution( int x, int y ) {
    if( x <= 0 || y <= 0 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT, null, "Values must be positive" );
    }
    resolution = new Point( x, y );
  }

  /**
   * <p>
   * Returns the preferred resolution.
   * </p>
   */
  public Point getResolution() {
    return resolution;
  }

  /**
   * <p>
   * The quality of the resulting image, expressed as a value from 0.0 to 1.0. The value 0.0 represents the maximum
   * compression (or lowest quality) while the value 1.0 represents the least compression (or best quality).
   * </p>
   */
  public void setCompressionQuality( float quality ) {
    if( quality < 0 || quality > 1 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT, 
                 null, 
                 "Compression Quality must be >= 0 and <= 1 but was " + quality );
    }
    this.quality = quality;
  }

  /**
   * <p>
   * Returns the compression quality of the resulting image. The default value is 1.0.
   * </p>
   */
  public float getCompressionQuality() {
    return quality;
  }
  
}
