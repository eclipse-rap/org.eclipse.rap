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

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.swt.graphics.Image;


public class ImageCell extends Cell<ImageCell> {

  public static enum ScaleMode {
    // display without scaling
    NONE,
    // scaled keeping aspect ratio until touches cell from inside
    FIT,
 // scaled keeping aspect ratio until touches cell from outside
    FILL
  }

  static final String TYPE_IMAGE = "image";
  static final String PROPERTY_DEFAULT_IMAGE = "defaultImage";
  static final String PROPERTY_SCALE_MODE = "scaleMode";
  private Image image;
  private ScaleMode scaleMode;

  public ImageCell( RowTemplate template ) {
    super( template, TYPE_IMAGE );
  }

  // binding index wins. Only if no binding index is set the default will be used
  public ImageCell setDefaultImage( Image image ) {
    this.image = image;
    checkNotNull( image, "Image" );
    return this;
  }

  Image getImage() {
    return image;
  }

  public ImageCell setScaleMode( ScaleMode scaleMode ) {
    this.scaleMode = scaleMode;
    checkNotNull( scaleMode, "ScaleMode" );
    return this;
  }

  ScaleMode getScaleMode() {
    return scaleMode;
  }

  @Override
  protected JsonObject toJson() {
    JsonObject json = super.toJson();
    if( image != null ) {
      json.add( PROPERTY_DEFAULT_IMAGE, ProtocolUtil.getJsonForImage( image ) );
    }
    if( scaleMode != null ) {
      json.add( PROPERTY_SCALE_MODE, scaleMode.name() );
    }
    return json;
  }

  private void checkNotNull( Object value, String name ) {
    if( value == null ) {
      throw new IllegalArgumentException( name + " must not be null" );
    }
  }

}
