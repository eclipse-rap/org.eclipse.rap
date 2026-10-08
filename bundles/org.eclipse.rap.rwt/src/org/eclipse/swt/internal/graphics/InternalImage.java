/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;

import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.SerializableCompatibility;


/**
 * Instances of this class hold the data associated with a particular image.
 */
public final class InternalImage implements SerializableCompatibility {

  private final String resourceName;
  private final int width;
  private final int height;
  private final boolean external;

  InternalImage( String resourceName, int width, int height, boolean external ) {
    ParamCheck.notNull( resourceName, "resourceName" );
    if( width <= 0 || height <= 0 ) {
      throw new IllegalArgumentException( "Illegal size" );
    }
    this.resourceName = resourceName;
    this.width = width;
    this.height = height;
    this.external = external;
  }

  public Rectangle getBounds() {
    return new Rectangle( 0, 0, width, height );
  }

  public ImageData getImageData() {
    return getApplicationContext().getImageDataFactory().findImageData( this );
  }

  public String getResourceName() {
    return resourceName;
  }

  public boolean isExternal() {
    return external;
  }

}
