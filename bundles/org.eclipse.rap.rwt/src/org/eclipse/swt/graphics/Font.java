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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.SWT;

// TODO [bm] Javadoc
// TODO [rh] font property (according and LCA functinality) for the following
//      widget missing: TableItem, TreeColumn
public final class Font extends Resource {
  
  private static final Map fonts = new HashMap();
  
  /**
   * TODO [fappel]: comment
   */
  public static Font getFont( final String name, 
                              final int height, 
                              final int style ) 
  {
    validate( name, height );
    int checkedStyle = checkStyle( style );
    Font result;
    FontData fontData = new FontData( name, height, checkedStyle );
    synchronized( Font.class ) {
      result = ( Font )fonts.get( fontData );
      if( result == null ) {
        result = new Font( fontData );
        fonts.put( fontData, result );
      }
    }
    return result;
  }
  
  /**
   * TODO [fappel]: comment
   */
  public static Font getFont( final FontData data ) {
    return getFont( data.getName(), data.getHeight(), data.getStyle() );
  }


  private final FontData[] fontData;
  
  private Font( final FontData data ) {
    this.fontData = new FontData[] { data };
  }
  
  /**
   * TODO [fappel]: comment
   */
  public FontData[] getFontData() {
    FontData[] result = new FontData[ fontData.length ];
    System.arraycopy( fontData, 0, result, 0, fontData.length );
    return result;
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

  //////////////////
  // Helping methods
  
  private static void validate( final String name, final int height ) {
    if( name == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( height < 0 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
  }
  
  private static int checkStyle( final int style ) {
    int result = SWT.NORMAL;
    if( ( style & SWT.BOLD ) != 0 ) {
      result |= SWT.BOLD;
    }
    if( ( style & SWT.ITALIC ) != 0 ) {
      result |= SWT.ITALIC;
    }
    return result;
  }
}
