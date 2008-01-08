/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.graphics;

import org.eclipse.rwt.internal.lifecycle.CommonPatterns;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;

class TextSizeEstimation {

  /**
   * Estimates the size of a given text. Line breaks are not respected.
   * @param font the font to perform the estimation for
   * @param string the text whose size to estimate
   * @return the estimated size
   */
  static Point stringExtent( final Font font, final String string ) {
    if ( string == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    int width = getLineWidth( string, font );
    int height = getCharHeight( font ) + 2;
    return new Point( width, height );
  }
  
  /**
   * Estimates the size of a given text, respecting line breaks and wrapping at
   * a given width.
   * @param font the font to perform the estimation for
   * @param string the text whose size to estimate
   * @param wrapWidth the width to wrap at in pixels, 0 stands for no wrapping
   * 
   * @return the estimated size
   */
  static Point textExtent( final Font font,
                           final String string,
                           final int wrapWidth )
  {
    if ( string == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    int lineCount = 0;
    int maxWidth = 0;
    String[] lines = CommonPatterns.NEWLINE_PATTERN.split( string, -1 );
    for( int i = 0; i < lines.length; i++ ) {
      String line = lines[ i ];
      lineCount++;
      int width = getLineWidth( line, font );
      if( wrapWidth > 0 ) {
        boolean done = false;
        while( !done ) {
          int index = getLongestMatch( line, wrapWidth, font );
          if( index == 0 || index == line.length() ) {
            // line fits or cannot be wrapped
            done = true;
          } else {
            // wrap line
            String substr = line.substring( 0, index );
            width = getLineWidth( substr, font );
            maxWidth = Math.max( maxWidth, width );
            line = line.substring( index, line.length() );
            lineCount++;
          }
        }
      }
      maxWidth = Math.max( maxWidth, width );
    }
    int height = Math.round( getCharHeight( font ) * 1.25f * lineCount );
    return new Point( maxWidth, height );
  }
  
  /**
   * Returns the character height in pixels. The returned value is only a rough
   * estimation.
   * 
   * @param font the font to perform the estimation for
   * @return the estimated character height in pixels
   */
  static int getCharHeight( final Font font ) {
    // at 72 dpi, 1 pt == 1 px
    return font.getFontData()[ 0 ].getHeight();
  }
  
  /**
   * Returns the average character weight in pixels. The returned value is only
   * a rough estimation that does not take the font family into account.
   * 
   * @param font the font to perform the estimation for
   * @return the estimated average character width in pixels
   */
  static float getAvgCharWidth( final Font font ) {
    float result;
    TextSizeProbeStore probeStore = TextSizeProbeStore.getInstance();
    if( probeStore.containsProbeResult( font ) ) {
      // we can improve char width estimations in case that we already have the
      // specified font probed.
      result = probeStore.getProbeResult( font ).getAvgCharWidth();
    } else {
      result = font.getFontData()[ 0 ].getHeight() * 0.48f;
      if( ( font.getFontData()[ 0 ].getStyle() & SWT.BOLD ) != 0 ) {
        result *= 1.45;
      }
    }
    return result;
  }
  
  /**
   * Returns the length of the longest substring, whose width is smaller or
   * equal to wrapWidth. If there is no such substring, zero is returned. The
   * result is never negative.
   */
  private static int getLongestMatch( final String string,
                                      final int wrapWidth,
                                      final Font font )
  {
    int result = 0;
    if( getLineWidth( string, font ) < wrapWidth ) {
      result = string.length();
    } else {
      String subStr = nextSubLine( string, 0 );
      boolean done = false;
      while( !done && getLineWidth( subStr, font ) <= wrapWidth ) {
        result = subStr.length();
        // loop prevention (see bug 182754)
        if( subStr.length() == string.length() ) {
          done = true;
        } else {
          subStr = nextSubLine( string, subStr.length() + 1 );
        }
      }
    }
    return result;
  }
  
  /**
   * Returns the next substring that can be wrapped.
   */
  private static String nextSubLine( final String line,
                                     final int startIndex )
  {
    String result = line;
    int index = line.indexOf( ' ', startIndex );
    if( index != -1 ) {
      result = line.substring( 0, index );
    }
    return result;
  }
  
  /**
   * Returns the width of a given string in pixels. Line breaks are ignored.
   */
  private static int getLineWidth( final String line, final Font font ) {
    return Math.round( getAvgCharWidth( font ) * line.length() );
  }
}
