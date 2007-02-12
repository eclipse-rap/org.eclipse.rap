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

package org.eclipse.rap.rwt.internal.graphics;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Font;
import org.eclipse.rap.rwt.graphics.Point;

public class FontSizeEstimation {

  public static Point stringExtent( String string, Font font ) {    
    if ( string == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    int width = getLineWidth( string, font );
    int height = getCharHeight( font ) + 2;
    return new Point( width, height );
  }
  
  /**
   * respects line breaks and wrap
   */
  public static Point textExtent( final String string,
                                  final int wrapWidth,
                                  Font font )
  {
    if ( string == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    int lineCount = 0;
    int maxWidth = 0;
    String[] lines = string.split( "\n" );
    for( int i = 0; i < lines.length; i++ ) {
      String line = lines[ i ];
      int width = getLineWidth( line, font );
      if( wrapWidth > 0 && getLineWidth( line, font ) > wrapWidth ) {
        // line too long
        int index = 0;
        int nextIndex = 0;
        while( ( nextIndex = line.indexOf( ' ', index ) ) != -1 ) {
          String subStr = line.substring( 0, nextIndex );
          int newWidth = getLineWidth( subStr, font );
          if( newWidth > wrapWidth ) {
            maxWidth = Math.max( maxWidth, width );
            lineCount++;
            if( index <= line.length() ) {
              line = line.substring( index, line.length() );
            } else {
              line = "";
            }
          }
          width = newWidth;
          index = nextIndex + 1;
        }
        lineCount++;
      }
      maxWidth = Math.max( maxWidth, width );
      lineCount++;
    }
    int height = Math.round( getCharHeight( font ) * 1.3f * lineCount );
    return new Point( maxWidth, height );
  }
  
  public static int getCharHeight( Font font ) {
    // at 72 dpi, 1 pt == 1 px
    return font.getSize();
  }
  
  public static float getAvgCharWidth( Font font ) {
    return font.getSize() * 0.45f;
  }
  
  private static int getLineWidth( String string, Font font ) {
    return Math.round( getAvgCharWidth( font ) * string.length() );
  }
}
