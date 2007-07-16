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
package org.eclipse.swt.graphics;

import java.awt.image.BufferedImage;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import javax.imageio.ImageIO;
import org.eclipse.swt.SWT;
import org.eclipse.swt.resources.ResourceManager;
import com.w4t.IResourceManager;


public final class Image extends Resource {
  
  private static final Map images = new HashMap();
  
  private int width;
  private int height;

  private Image () {
    // prevent instantiation from outside
    width = -1;
    height = -1;
  }
  
  public static synchronized Image find( final String path ) {
    IResourceManager manager = ResourceManager.getInstance();
    return find( path, manager.getContextLoader() );
  }
  
  public static synchronized Image find( final String path, 
                                         final ClassLoader imageLoader )
  {
    if( path == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( "".equals( path ) ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    Image result;
    if( images.containsKey( path ) ) {
      result = ( Image )images.get( path );
    } else {
      result = createImage( path, imageLoader );
    }
    return result;
  }
  
  public static synchronized Image find( final String path,
                                         final InputStream inputStream )
  {
    if( path == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( "".equals( path ) ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    Image result;
    if( images.containsKey( path ) ) {
      result = ( Image )images.get( path );
    } else {
      result = createImage( path, inputStream );
    }
    return result;
  }
  
  public static synchronized String getPath( final Image image ) {
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
//      SWT.error( SWT.ERROR_GRAPHIC_DISPOSED );
//    }
    if( width != -1 && height != -1 ) {
      result = new Rectangle( 0, 0, width, height );
    } else {
      // TODO [rst] check types
      SWT.error( SWT.ERROR_INVALID_IMAGE );
    }
    return result;
  }
  
  //////////////////
  // Helping methods
  
  private static Image createImage( final String path, 
                                    final ClassLoader imageLoader )
  {
    Image result;
    IResourceManager manager = ResourceManager.getInstance();
    ClassLoader loaderBuffer = manager.getContextLoader();
    if( imageLoader != null ) {
      manager.setContextLoader( imageLoader );
    }
    try {
      InputStream inputStream = manager.getResourceAsStream( path );
      result = createImage( path, inputStream );
    } finally {
      manager.setContextLoader( loaderBuffer );
    }
    return result;
  }

  private static Image createImage( final String path, 
                                    final InputStream inputStream )
  {
    if( inputStream == null ) {
      String txt = "Image ''{0}'' cannot be found.";
      String msg = MessageFormat.format( txt, new Object[] { path } );
      SWT.error( SWT.ERROR_INVALID_ARGUMENT, 
                 new IllegalArgumentException( msg ), 
                 msg );
    }
    Image result = new Image();
    
    ////////////////////////////////////////////////////////////////////////////
    // TODO: [fappel] Image size calculation and resource registration both
    //                read the input stream. Because of this I use a workaround
    //                with a BufferedInputStream. Resetting it after reading the
    //                image size enables the ResourceManager to reuse it for
    //                registration. Note that the order is crucial here, since
    //                the ResourceManager seems to close the stream (shrug).
    //                It would be nice to find a solution without reading the
    //                stream twice.
    
    IResourceManager manager = ResourceManager.getInstance();
    BufferedInputStream bis = new BufferedInputStream( inputStream );
    bis.mark( Integer.MAX_VALUE );
    Point size = readImageSize( bis );
    if( size != null ) {
      result.width = size.x;
      result.height = size.y;
    }
    try {
      bis.reset();
    } catch( final IOException shouldNotHappen ) {
      String txt = "Could not reset input stream while reading image ''{0}''.";
      String msg = MessageFormat.format( txt, new Object[] { path } );
      throw new RuntimeException( msg, shouldNotHappen );
    }
    manager.register( path, bis );
    
    ////////////////////////////////////////////////////////////////////////////
    
    images.put( path, result );
    return result;
  }
  
  /**
   * @return an array whose first element is the image <em>width</em> and
   *         second is the <em>height</em>, <code>null</code> if the bounds
   *         could not be read.
   */
  private static Point readImageSize( final InputStream input ) {
    Point result = null;
    boolean cacheBuffer = ImageIO.getUseCache();
    try {
      // [fappel]: We don't use caching since it sometimes causes problems
      //           if the application is deployed at a servlet container. This
      //           does not have any memories or performance impacts, since
      //           a image is a value object that is loaded only once in
      //           an application.
      ImageIO.setUseCache( false );
      // TODO [fappel]: To use BufferedImage on Mac Os the following 
      //                system property has to be set: java.awt.headless=true.
      //                Put this info in a general documentation
      BufferedImage image = ImageIO.read( input );
      if( image != null ) {
        int width = image.getWidth();
        int height = image.getHeight();
        result = new Point( width, height );
      }
    } catch( Exception e ) {
      // ImageReader throws IllegalArgumentExceptions for some files
      // TODO [rst] log exception
      e.printStackTrace();
    } finally {
      ImageIO.setUseCache( cacheBuffer );
    }
    return result;
  }
}