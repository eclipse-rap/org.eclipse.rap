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
package org.eclipse.swt.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.rwt.graphics.Graphics;

/**
 * Instances of this class manage resources that define how text looks when 
 * it is displayed. 
 * 
 * <p>Fonts may be constructed using one of the <code>getFont</code> methods 
 * in class <code>Graphics</code> by providing a name, size and style information 
 * or a <code>FontData</code> object which encapsulates this data.
 * </p>
 *
 * @see FontData
 * @see Graphics
 * 
 * @since 1.0
 */
public final class Font extends Resource {

  private final FontData[] fontData;

  // prevent instance creation
  private Font( final FontData data ) {
    this.fontData = new FontData[] { data };
  }

  // TODO [bm] javadoc - revise this
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
    // We support only fontData arrays with one element, so we use this
    // knowledge to create the defensive copy a little faster
    return new FontData[] { fontData[ 0 ] };
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
