/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.graphics.ResourceFactory;

/**
 * Instances of this class manage resources that define how text looks when it
 * is displayed.
 * <p>
 * To create fonts, it is recommended to use one of the <code>getFont</code>
 * methods in class <code>Graphics</code> by providing a name, size and style
 * information or a <code>FontData</code> object which encapsulates this data.
 * </p>
 * 
 * @see FontData
 * @see Graphics
 * @see org.eclipse.rwt.graphics.Graphics#getFont(FontData)
 * @see org.eclipse.rwt.graphics.Graphics#getFont(String, int, int)
 * @since 1.0
 */
public final class Font extends Resource {

  private final FontData[] fontData;

  // used by ResourceFactory#getFont()
  private Font( final FontData data ) {
    super( null );
    this.fontData = new FontData[] { data };
  }

  /**
   * Constructs a new font given a device and font data
   * which describes the desired font's appearance.
   * <p>
   * You must dispose the font when it is no longer required.
   * </p>
   * 
   * <p><strong>Note</strong>, this constructor is provided for convenience when
   * single-sourcing code with SWT. For RWT, the recommended way to create fonts
   * is to use one of the <code>getFont</code> methods in class 
   * <code>Graphics</code>.
   * </p>
   *
   * @param device the device to create the font on
   * @param fontData the FontData that describes the desired font (must not be null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
   *    <li>ERROR_NULL_ARGUMENT - if the fd argument is null</li>
   * </ul>
   * @exception SWTError <ul>
   *    <li>ERROR_NO_HANDLES - if a font could not be created from the given font data</li>
   * </ul>
   *
   * @since 1.3
   */
  public Font( final Device device, final FontData fontData ) {
    this( device, new FontData[] { fontData } );
  }

  /**
   * Constructs a new font given a device and an array
   * of font data which describes the desired font's
   * appearance.
   * <p>
   * You must dispose the font when it is no longer required.
   * </p>
   *
   * <p><strong>Note</strong>, this constructor is provided for convenience when
   * single-sourcing code with SWT. For RWT, the recommended way to create fonts
   * is to use one of the <code>getFont</code> methods in class 
   * <code>Graphics</code>.
   * </p>
   *
   * @param device the device to create the font on
   * @param fontData the array of FontData that describes the desired font (must not be null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
   *    <li>ERROR_NULL_ARGUMENT - if the fds argument is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the length of fds is zero</li>
   *    <li>ERROR_NULL_ARGUMENT - if any fd in the array is null</li>
   * </ul>
   * @exception SWTError <ul>
   *    <li>ERROR_NO_HANDLES - if a font could not be created from the given font data</li>
   * </ul>
   *
   * @since 1.3
   */
  public Font( final Device device, final FontData[] fontData ) {
    super( device );
    if( device == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( fontData == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( fontData.length == 0 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    for( int i = 0; i < fontData.length; i++ ) {
      if( fontData[ i ] == null ) {
        SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
    }
    this.fontData = new FontData[ fontData.length ];
    System.arraycopy( fontData, 0, this.fontData, 0, fontData.length );
  }

  /**
   * Constructs a new font given a device, a font name,
   * the height of the desired font in points, and a font
   * style.
   * <p>
   * You must dispose the font when it is no longer required.
   * </p>
   *
   * @param device the device to create the font on
   * @param name the name of the font (must not be null)
   * @param height the font height in points
   * @param style a bit or combination of NORMAL, BOLD, ITALIC
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
   *    <li>ERROR_NULL_ARGUMENT - if the name argument is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the height is negative</li>
   * </ul>
   * @exception SWTError <ul>
   *    <li>ERROR_NO_HANDLES - if a font could not be created from the given arguments</li>
   * </ul>
   */
  public Font( final Device device,
               final String name,
               final int height,
               final int style )
  {
    this( device, new FontData( name, 
                                height, 
                                ResourceFactory.checkFontStyle( style ) ) );
  }

  /**
   * Returns an array of <code>FontData</code>s representing the receiver.
   * <!--
   * On Windows, only one FontData will be returned per font. On X however,
   * a <code>Font</code> object <em>may</em> be composed of multiple X
   * fonts. To support this case, we return an array of font data objects.
   * -->
   *
   * @return an array of font data objects describing the receiver
   *
   * <!--
   * @exception SWTException <ul>
   *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
   * </ul>
   * -->
   */
  public FontData[] getFontData() {
    if( isDisposed() ) {
      SWT.error( SWT.ERROR_GRAPHIC_DISPOSED );
    }
    // We support only fontData arrays with one element, so we use this
    // knowledge to create the defensive copy a little faster
    return new FontData[] { fontData[ 0 ] };
  }

  public boolean equals( final Object object ) {
    boolean result;
    if( object == this ) {
      result = true;
    } else if( object instanceof Font ) {
      Font font = ( Font )object;
      result = font.fontData[ 0 ].equals( fontData[ 0 ] );
    } else {
      result = false;
    }
    return result;
  }

  public int hashCode() {
    return fontData[ 0 ].hashCode();
  }

  /**
   * Returns a string containing a concise, human-readable
   * description of the receiver.
   *
   * @return a string representation of the receiver
   */
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append( "Font {" );
    if( fontData.length > 0 ) {
      buffer.append( fontData[ 0 ].getName() );
      buffer.append( "," );
      buffer.append( fontData[ 0 ].getHeight() );
      buffer.append( "," );
      int style = fontData[ 0 ].getStyle();
      String styleName;
      if( ( style & SWT.BOLD ) != 0 && ( style & SWT.ITALIC ) != 0 ) {
        styleName = "BOLD|ITALIC";
      } else if( ( style & SWT.BOLD ) != 0 ) {
        styleName = "BOLD";
      } else if( ( style & SWT.ITALIC ) != 0 ) {
        styleName = "ITALIC";
      } else {
        styleName = "NORMAL";
      }
      buffer.append( styleName );
    }
    buffer.append( "}" );
    return buffer.toString();
  }
}
