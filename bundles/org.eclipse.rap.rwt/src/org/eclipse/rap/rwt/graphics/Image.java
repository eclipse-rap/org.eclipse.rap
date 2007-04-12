/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.graphics;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;
import javax.imageio.ImageIO;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.resources.ResourceManager;
import com.w4t.IResourceManager;


public final class Image {
  
  private static final Map images = new HashMap();
  
  private int width;
  
  private int height;

  private Image () {
    // prevent instantiation from outside
    width = -1;
    height = -1;
  }
  
  public static synchronized Image find( final String path ) {
    return find( path, null );
  }
  
  public static synchronized Image find( final String path, 
                                         final ClassLoader imageLoader )
  {
    if( path == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    Image result;
    if( images.containsKey( path ) ) {
      result = ( Image )images.get( path );
    } else {
      result = createImage( path, imageLoader );
    }
    return result;
  }
  
  public static synchronized String getPath ( final Image image ) {
    String result = null;
    Iterator it = images.entrySet().iterator();
    boolean next = true;
    while( next && it.hasNext() ) {
      Map.Entry entry = ( Map.Entry )it.next();
      if( entry.getValue().equals( image ) ) {
        result = ( String )entry.getKey();
        next = false;
      }
    }
    return result;
  }
  
  public static synchronized int size(){
    return images.size();
  }
  
  public static synchronized void clear(){
    // TODO: [GR]can we deregister ressources?
//    Iterator it = images.keySet().iterator();
//    while ( it.hasNext() ) {
//      manager.deregister((String)it.next());
//    }
    images.clear();
  }
  
  ///////////////////////
  // Public Image methods
  
  public Rectangle getBounds() {
    Rectangle result = null;
//    TODO [rst] Uncomment if constructor provided
//    if( isDisposed() ) {
//      RWT.error( RWT.ERROR_GRAPHIC_DISPOSED );
//    }
    if( width != -1 && height != -1 ) {
      result = new Rectangle(0, 0, width, height);
    } else {
      // TODO [rst] check types
      RWT.error( RWT.ERROR_INVALID_IMAGE );
    }
    return result;
  }
  
  //////////////////
  // Helping methods
  
  private static Image createImage( final String path, 
                                    final ClassLoader imageLoader )
  {
    Image image = new Image();
    IResourceManager manager = ResourceManager.getInstance();
    ClassLoader loaderBuffer = manager.getContextLoader();
    manager.setContextLoader( imageLoader );
    try {
      manager.register( path );
      InputStream inputStream = manager.getResourceAsStream( path );
      Point size = readImageSize( inputStream );
      if( size != null ) {
        image.width = size.x;
        image.height = size.y;
      }
    } finally {
      manager.setContextLoader( loaderBuffer );
    }
    images.put( path, image );
    return image;
  }
  
  /**
   * @return an array whose first element is the image <em>width</em> and
   *         second is the <em>height</em>, <code>null</code> if the bounds
   *         could not be read.
   */
  private static Point readImageSize( final InputStream input ) {
    Point result = null;
    try {
      BufferedImage image = ImageIO.read( input );
      if( image != null ) {
        int width = image.getWidth();
        int height = image.getHeight();
        result = new Point( width, height );
      }
    } catch( Exception e ) {
      // ImageReader throws IllegalArgumentExceptions for some files
      // TODO [rst] log exception
    }
    return result;
  }
}