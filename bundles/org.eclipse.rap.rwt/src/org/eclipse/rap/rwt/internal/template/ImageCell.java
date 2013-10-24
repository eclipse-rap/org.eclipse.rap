/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.template;

import org.eclipse.swt.graphics.Image;


public class ImageCell extends Cell<ImageCell> {

  public enum ScaleMode {
    // display without scaling
    NONE,
    // scaled keeping aspect ratio until touches cell from inside
    FIT,
    // scaled not keeping aspect ratio
    STRETCH
  }

  static final String TYPE_IMAGE = "image";
  static final String PROPERTY_DEFAULT_IMAGE = "defaultImage";
  static final String PROPERTY_SCALE_MODE = "scaleMode";

  public ImageCell( RowTemplate template ) {
    super( template, TYPE_IMAGE );
  }

  // binding index wins. Only if no binding index is set the default will be used
  public ImageCell setDefaultImage( Image image ) {
    checkNotNull( image, "Image" );
    addAttribute( PROPERTY_DEFAULT_IMAGE, image );
    return this;
  }

  public ImageCell setScaleMode( ScaleMode scaleMode ) {
    checkNotNull( scaleMode, "ScaleMode" );
    addAttribute( PROPERTY_SCALE_MODE, scaleMode.name() );
    return this;
  }

  private void checkNotNull( Object value, String name ) {
    if( value == null ) {
      throw new IllegalArgumentException( name + " must not be null" );
    }
  }
}
