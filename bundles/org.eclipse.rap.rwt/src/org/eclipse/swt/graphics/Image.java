/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import java.io.*;
import java.text.MessageFormat;

import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.widgets.Display;

/**
 * Instances of this class are graphics which have been prepared
 * for display on a specific device. That is, they are to display 
 * on widgets with, for example, <code>Button.setImage()</code>.
 * 
 * <p>If loaded from a file format that supports it, an
 * <code>Image</code> may have transparency, meaning that certain
 * pixels are specified as being transparent when drawn. Examples
 * of file formats that support transparency are GIF and PNG.</p>
 * 
 * <p>In RWT, images are shared among all sessions. Therefore they
 * lack a public constructor. Images can be created using the 
 * <code>getImage()</code> methods of class <code>Graphics</code>
 *
 * @see org.eclipse.rwt.graphics.Graphics#getImage(String)
 * @see org.eclipse.rwt.graphics.Graphics#getImage(String, ClassLoader)
 * @see org.eclipse.rwt.graphics.Graphics#getImage(String, java.io.InputStream)
 */
public final class Image extends Resource implements Drawable {

  private int width;
  private int height;

  // RAP [bm]: e4-enabling hacks
  public Image( Device device, ImageData imageData ) {
    ImageLoader loader = new ImageLoader();
    loader.data = new ImageData[]{
      imageData
    };
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    int type = imageData.type != SWT.IMAGE_UNDEFINED
                                                    ? imageData.type
                                                    : SWT.IMAGE_PNG;
    loader.save( outputStream, type );
    byte[] byteArray = outputStream.toByteArray();
    InputStream inputStream = new ByteArrayInputStream( byteArray );
    String path = "resources/generated/"
                  + hashCode( byteArray )
                  + ResourceFactory.getImageFileExtension( type );
    initImage( path, inputStream );
  }

  private static int hashCode( final byte bytes[] ) {
    int result;
    if( bytes == null ) {
      result = 0;
    } else {
      result = 1;
      for( int i = 0; i < bytes.length; i++ ) {
        result = 31 * result + bytes[ i ];
      }
    }
    return result;
  }
  
  /* prevent instantiation from outside */
  private Image() {
    width = -1;
    height = -1;
  }
  private Image( final int width, final int height ) {
    this.width = width;
    this.height = height;
  }

  ///////////////////////
  // Public Image methods

  // RAP [bm]: e4-enabling hacks
  public Image( Device device, InputStream in ) {
    initImage( "generated_" + in.hashCode(), in );

  }

  // RAP [bm]: e4-enabling hacks
  public Image( Display display, int x, int y ) {
    this.width = x;
    this.height = y;
    ResourceFactory.images.put( "resources/generated/image_" + Math.random(), this );
  }

  // RAP [bm]: e4-enabling hacks
  public Image( Display d, ImageData imageData, ImageData imageData2 ) {
    this( d, imageData );
  }

  // RAP [bm]: e4-enabling hacks
  private void initImage( String path, InputStream in ) {
    IResourceManager manager = ResourceManager.getInstance();
    BufferedInputStream bis = new BufferedInputStream( in );
    bis.mark( Integer.MAX_VALUE );
    Point size = null;
    try {
      size = ResourceFactory.readImageSize( bis );
    } catch( IOException e ) {
      e.printStackTrace();
    }
    this.width = size.x;
    this.height = size.y;
    try {
      bis.reset();
    } catch( final IOException shouldNotHappen ) {
      String txt = "Could not reset input stream while reading image ''{0}''.";
      String msg = MessageFormat.format( txt, new Object[]{
        path
      } );
      throw new RuntimeException( msg, shouldNotHappen );
    }
    manager.register( path, bis );
    ResourceFactory.images.put( path, this );
  }

  /**
   * Returns the bounds of the receiver. The rectangle will always
   * have x and y values of 0, and the width and height of the
   * image.
   *
   * @return a rectangle specifying the image's bounds
   *
   * @exception SWTException <ul>
   * <!--   <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li> -->
   *    <li>ERROR_INVALID_IMAGE - if the image is not a bitmap or an icon</li>
   * </ul>
   */
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
  
  // RAP [bm]: e4-enabling hacks
  public void dispose() {
  }
  
  // RAP [bm]: e4-enabling hacks
  public ImageData getImageData() {
    return ResourceFactory.getImageData( this );
  }
  
  // RAP [bm]: e4-enabling hacks
  public boolean isDisposed() {
    return false;
  }
}