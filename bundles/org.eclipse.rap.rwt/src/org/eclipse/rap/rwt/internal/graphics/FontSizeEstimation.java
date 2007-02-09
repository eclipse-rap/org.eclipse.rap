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

  private static FontSizeEstimation instance;
  private final Font font;

  private FontSizeEstimation( Font font ) {
    this.font = font;
    // prevent instantiation
  }
  
  public static FontSizeEstimation getInstance( Font font ) {
    if( instance == null ) {
      instance = new FontSizeEstimation( font );
    }
    return instance;
  }
  
  public Point stringExtent( String string ) {    
    if ( string == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    int width = getLineWidth( string );
    int height = getCharHeight() + 2;
    return new Point( width, height );
  }
  
  /**
   * respects line breaks and wrap
   */
  public Point textExtent( final String string, final int wrapWidth ) {
    if ( string == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    int lineCount = 0;
    int maxWidth = 0;
    String[] lines = string.split( "\n" );
    for( int i = 0; i < lines.length; i++ ) {
      String line = lines[ i ];
      int width = getLineWidth( line );
      if( wrapWidth > 0 && getLineWidth( line ) > wrapWidth ) {
        // line too long
        int index = 0;
        int nextIndex = 0;
        while( ( nextIndex = line.indexOf( ' ', index ) ) != -1 ) {
          String subStr = line.substring( 0, nextIndex );
          int newWidth = getLineWidth( subStr );
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
    int height = Math.round( getCharHeight() * 1.3f * lineCount );
    return new Point( maxWidth, height );
  }
  
  public int getCharHeight() {
    // at 72 dpi, 1 pt == 1 px
    return font.getSize();
  }
  
  public float getAvgCharWidth() {
    return font.getSize() * 0.45f;
  }
  
  private int getLineWidth( String string ) {
    return Math.round( getAvgCharWidth() * string.length() );
  }
}
