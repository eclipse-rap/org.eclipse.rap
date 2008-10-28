/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.graphics;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.*;

import javax.imageio.ImageIO;

import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;


public final class ResourceFactory {

  private static final Map colors = new HashMap();
  private static final Map fonts = new HashMap();
  private static final Map images = new HashMap();
  private static final Map cursors = new HashMap();
  private static final ImageDataCache imageDataCache = new ImageDataCache();

  /////////
  // Colors

  public static Color getColor( final RGB rgb ) {
    if( rgb == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return getColor( rgb.red, rgb.green, rgb.blue );
  }

  public static Color getColor( final int red,
                                final int green,
                                final int blue )
  {
    if(    red > 255
        || red < 0
        || green > 255
        || green < 0
        || blue > 255
        || blue < 0 )
    {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    int colorNr = red | ( green << 8 ) | ( blue << 16 );
    return getColor( colorNr );
  }

  /**
   * <strong>Note:</strong> this is <em>not</em> a shortcut for
   * <code>getColor(int, int, int)</code>.
   *
   * @param value the integer value that represents the color internally
   */
  public static Color getColor( final int value ) {
    Color result;
    Integer key = new Integer( value );
    synchronized( colors ) {
      if( colors.containsKey( key ) ) {
        result = ( Color )colors.get( key );
      } else {
        result = createColorInstance( value );
        colors.put( key, result );
      }
    }
    return result;
  }


  ////////
  // Fonts

  public static Font getFont( final String name,
                              final int height,
                              final int style )
  {
    validateFontParams( name, height );
    int checkedStyle = checkFontStyle( style );
    Font result;
    Integer key = new Integer( fontHashCode( name, height, checkedStyle ) );
    synchronized( Font.class ) {
      result = ( Font )fonts.get( key );
      if( result == null ) {
        FontData fontData = new FontData( name, height, checkedStyle );
        result = createFontInstance( fontData );
        fonts.put( key, result );
      }
    }
    return result;
  }

  public static int fontHashCode( final String name,
                                  final int height,
                                  final int style )
  {
    return name.hashCode() ^ ( height << 2 ) ^ style;
  }


  /////////
  // Images

  public static synchronized Image findImage( final String path ) {
    IResourceManager manager = ResourceManager.getInstance();
    return findImage( path, manager.getContextLoader() );
  }

  public static synchronized Image findImage( final String path,
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

  public static synchronized Image findImage( final String path,
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

  public static synchronized Image findImage( final ImageData imageData ) {
    if( imageData == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    Image result;
    ImageLoader loader = new ImageLoader();
    loader.data = new ImageData[] { imageData };
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    int type = imageData.type != SWT.IMAGE_UNDEFINED
                                                    ? imageData.type
                                                    : SWT.IMAGE_PNG;
    loader.save( outputStream, type );
    byte[] byteArray = outputStream.toByteArray();
    InputStream inputStream = new ByteArrayInputStream( byteArray );
    String path = "resources/generated/"
                  + hashCode( byteArray )
                  + getImageFileExtension( type );
    if( images.containsKey( path ) ) {
      result = ( Image )images.get( path );
    } else {
      result = createImage( path, inputStream );
    }
    return result;
  }

  public static synchronized String getImagePath( final Image image ) {
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

  public static ImageData getImageData( final Image image ) {
    ImageData result = imageDataCache.getImageData( image );
    if( result == null ) {
      IResourceManager manager = ResourceManager.getInstance();
      String imagePath = getImagePath( image );
      if( imagePath != null ) {
        try {
          InputStream inputStream = manager.getRegisteredContent( imagePath );
          if( inputStream != null ) {
            try {
              result = new ImageData( inputStream );
            } finally {
              inputStream.close();
            }
          }
        } catch( IOException e ) {
          // failed to close input stream - should not happen
          throw new RuntimeException( e );
        }
      }
      if( result != null ) {
        imageDataCache.putImageData( image, result );
      }
    }
    return result;
  }

  ////////
  // Cursors

  public static Cursor getCursor( final int style ) {
    Cursor result;
    Integer key = new Integer( style );
    synchronized( Cursor.class ) {
      result = ( Cursor )cursors.get( key );
      if( result == null ) {
        result = createCursorInstance( style );
        cursors.put( key, result );
      }
    }
    return result;
  }

  ///////////////
  // Test helpers

  public static void clear() {
    colors.clear();
    fonts.clear();
    images.clear();
    cursors.clear();
  }

  static int colorsCount() {
    return colors.size();
  }

  static int fontsCount() {
    return fonts.size();
  }

  static int imagesCount() {
    return images.size();
  }

  static int cursorsCount() {
    return cursors.size();
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
    Image result;

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
      result = createImageInstance( size.x, size.y );
    } else {
      result = createImageInstance( -1, -1 );
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
    } catch( final Exception e ) {
      // ImageReader throws IllegalArgumentExceptions for some files
      // TODO [rst] log exception
      e.printStackTrace();
    } finally {
      ImageIO.setUseCache( cacheBuffer );
    }
    return result;
  }

  //////////////////
  // Helping methods

  private static void validateFontParams( final String name, final int height )
  {
    if( name == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( height < 0 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
  }

  private static int checkFontStyle( final int style ) {
    int result = SWT.NORMAL;
    if( ( style & SWT.BOLD ) != 0 ) {
      result |= SWT.BOLD;
    }
    if( ( style & SWT.ITALIC ) != 0 ) {
      result |= SWT.ITALIC;
    }
    return result;
  }

  private static String getImageFileExtension( final int type ) {
    String result;
    switch( type ) {
      case SWT.IMAGE_BMP:
      case SWT.IMAGE_BMP_RLE:
      case SWT.IMAGE_OS2_BMP:
        result = ".bmp";
      break;
      case SWT.IMAGE_GIF:
        result = ".gif";
      break;
      case SWT.IMAGE_ICO:
        result = ".ico";
      break;
      case SWT.IMAGE_JPEG:
        result = ".jpg";
      break;
      case SWT.IMAGE_PNG:
        result = ".png";
      break;
      default:
        result = "";
      break;
    }
    return result;
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

  ////////////////////
  // Instance creation

  private static Color createColorInstance( final int colorNr ) {
    Color result = null;
    try {
      Class colorClass = Color.class;
      Class[] classes = colorClass.getDeclaredClasses();
      Class colorExtClass = classes[ 0 ];
      Class[] paramList = new Class[] { int.class };
      Constructor constr = colorExtClass.getDeclaredConstructor( paramList );
      constr.setAccessible( true );
      Object[] args = new Object[] { new Integer( colorNr ) };
      result = ( Color )constr.newInstance( args );
    } catch( final Exception e ) {
      throw new RuntimeException( "Failed to instantiate Color", e );
    }
    return result;
  }

  private static Font createFontInstance( final FontData fontData ) {
    Font result = null;
    try {
      Class fontClass = Font.class;
      Class[] paramList = new Class[] { FontData.class };
      Constructor constr = fontClass.getDeclaredConstructor( paramList );
      constr.setAccessible( true );
      result = ( Font )constr.newInstance( new Object[] { fontData } );
    } catch( final Exception e ) {
      throw new RuntimeException( "Failed to instantiate Font", e );
    }
    return result;
  }

  private static Image createImageInstance( final int width, final int height )
  {
    Image result = null;
    try {
      Class fontClass = Image.class;
      Class[] paramList = new Class[] { int.class, int.class };
      Constructor constr = fontClass.getDeclaredConstructor( paramList );
      constr.setAccessible( true );
      result = ( Image )constr.newInstance( new Object[] {
        new Integer( width ), new Integer( height )
      } );
    } catch( final Exception e ) {
      throw new RuntimeException( "Failed to instantiate Image", e );
    }
    return result;
  }

  private static Cursor createCursorInstance( final int style ) {
    Cursor result = null;
    try {
      Class cursorClass = Cursor.class;
      Class[] paramList = new Class[] { int.class };
      Constructor constr = cursorClass.getDeclaredConstructor( paramList );
      constr.setAccessible( true );
      result = ( Cursor )constr.newInstance( new Object[] {
        new Integer( style )
      } );
    } catch( final Exception e ) {
      throw new RuntimeException( "Failed to instantiate Cursor", e );
    }
    return result;
  }

  private ResourceFactory() {
    // prevent instantiation
  }
}
