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

import org.eclipse.rwt.RWT;
import org.eclipse.swt.*;
import org.eclipse.swt.internal.graphics.ResourceFactory;

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
public final class Image extends Resource {

  /**
   * <b>IMPORTANT:</b> This field is <em>not</em> part of the SWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by SWT. It is not available on all
   * platforms and should never be accessed from application code.
   */
  public String resourceName;
  
  private int width;
  private int height;

  /* This constructor is called by ResourceFactory#createImage() */
  private Image( final String resourceName, final int width, final int height )
  {
    super( null );
    this.resourceName = resourceName;
    this.width = width;
    this.height = height;
  }

  /**
   * Constructs an instance of this class by loading its representation
   * from the specified input stream. Throws an error if an error
   * occurs while loading the image, or if the result is an image
   * of an unsupported type.  Application code is still responsible
   * for closing the input stream.
   * <p>
   * This constructor is provided for convenience when loading a single
   * image only. If the stream contains multiple images, only the first
   * one will be loaded. To load multiple images, use 
   * <code>ImageLoader.load()</code>.
   * </p><p>
   * This constructor may be used to load a resource as follows:
   * </p>
   * <pre>
   *     static Image loadImage (Display display, Class clazz, String string) {
   *          InputStream stream = clazz.getResourceAsStream (string);
   *          if (stream == null) return null;
   *          Image image = null;
   *          try {
   *               image = new Image (display, stream);
   *          } catch (SWTException ex) {
   *          } finally {
   *               try {
   *                    stream.close ();
   *               } catch (IOException ex) {}
   *          }
   *          return image;
   *     }
   * </pre>
   *
   * <p><strong>Note</strong>, this constructor is provided for convenience when
   * single-sourcing code with SWT. For RWT, the recommended way to create images 
   * is to use one of the <code>Graphics#getImage()</code> methods.
   * </p>
   * 
   * @param device the device on which to create the image
   * @param stream the input stream to load the image from
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
   *    <li>ERROR_NULL_ARGUMENT - if the stream is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_IO - if an IO error occurs while reading from the stream</li>
   *    <li>ERROR_INVALID_IMAGE - if the image stream contains invalid data </li>
   *    <li>ERROR_UNSUPPORTED_DEPTH - if the image stream describes an image with an unsupported depth</li>
   *    <li>ERROR_UNSUPPORTED_FORMAT - if the image stream contains an unrecognized format</li>
   * </ul>
   * @exception SWTError <ul>
   *    <li>ERROR_NO_HANDLES if a handle could not be obtained for image creation</li>
   * </ul>
   * @see org.eclipse.rwt.graphics.Graphics#getImage(String)
   * @see org.eclipse.rwt.graphics.Graphics#getImage(String, ClassLoader)
   * @see org.eclipse.rwt.graphics.Graphics#getImage(String, java.io.InputStream)
   * @since 1.3
   */
  public Image( final Device device, final InputStream stream ) {
    super( device );
    if( device == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( stream == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    init( stream );
  }

  /**
   * Constructs an instance of this class by loading its representation
   * from the file with the specified name. Throws an error if an error
   * occurs while loading the image, or if the result is an image
   * of an unsupported type.
   * <p>
   * This constructor is provided for convenience when loading
   * a single image only. If the specified file contains
   * multiple images, only the first one will be used.
   *
   * @param device the device on which to create the image
   * @param filename the name of the file to load the image from
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
   *    <li>ERROR_NULL_ARGUMENT - if the file name is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_IO - if an IO error occurs while reading from the file</li>
   *    <li>ERROR_INVALID_IMAGE - if the image file contains invalid data </li>
   *    <li>ERROR_UNSUPPORTED_DEPTH - if the image file describes an image with an unsupported depth</li>
   *    <li>ERROR_UNSUPPORTED_FORMAT - if the image file contains an unrecognized format</li>
   * </ul>
   * @exception SWTError <ul>
   *    <li>ERROR_NO_HANDLES if a handle could not be obtained for image creation</li>
   * </ul>
   * @see org.eclipse.rwt.graphics.Graphics#getImage(String)
   * @see org.eclipse.rwt.graphics.Graphics#getImage(String, ClassLoader)
   * @see org.eclipse.rwt.graphics.Graphics#getImage(String, java.io.InputStream)
   * @since 1.3
   */
  public Image( final Device device, final String fileName ) {
    super( device );
    if( device == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( fileName == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    init( fileName );
  }

  private void init( final String fileName ) {
    try {
      FileInputStream stream = new FileInputStream( fileName );
      try {
        init( stream );
      } finally {
        stream.close();
      }
    } catch( IOException e ) {
      throw new SWTException( SWT.ERROR_IO, e.getMessage() );
    }
  }
  
  private void init( final InputStream stream ) {
    resourceName = "image-" + String.valueOf( hashCode() );
    Point size = ResourceFactory.registerImage( resourceName, stream );
    if( size == null ) {
      throw new SWTException( SWT.ERROR_UNSUPPORTED_FORMAT );
    }
    width = size.x;
    height = size.y;
  }

  ///////////////////////
  // Public Image methods

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
    if( isDisposed() ) {
      SWT.error( SWT.ERROR_GRAPHIC_DISPOSED );
    }
    if( width != -1 && height != -1 ) {
      result = new Rectangle( 0, 0, width, height );
    } else {
      // TODO [rst] check types
      SWT.error( SWT.ERROR_INVALID_IMAGE );
    }
    return result;
  }

  ///////////
  // Disposal
  
  void destroy() {
    RWT.getResourceManager().unregister( resourceName );
  }
}