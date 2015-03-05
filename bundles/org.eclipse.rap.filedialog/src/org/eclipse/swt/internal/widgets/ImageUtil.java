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
package org.eclipse.swt.internal.widgets;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class ImageUtil {

  public static Image getImage( Display display, String path ) {
    ClassLoader classLoader = ImageUtil.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream( "resources/" + path );
    Image result = null;
    if( inputStream != null ) {
      try {
        result = new Image( display, inputStream );
      } finally {
        try {
          inputStream.close();
        } catch( IOException e ) {
          // ignore
        }
      }
    }
    return result;
  }

  private ImageUtil() {
    // prevent instantiation
  }

}
