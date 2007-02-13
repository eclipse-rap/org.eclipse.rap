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

package org.eclipse.rap.rwt.widgets;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.*;
import org.eclipse.rap.rwt.internal.graphics.FontSizeEstimation;

/**
 * <p>Due to limitations of the JavaScript library, the current WRAP behavior 
 * of a MULI line text is always as if WRAP was set.</p> 
 */
public class Text extends Control {

  private String text = "";

  public Text( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  public void setText( final String text ) {
    checkWidget();
    if( text == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    this.text = text;
  }

  public String getText() {
    checkWidget();
    return text;
  }

  public String getLineDelimiter() {
    return "\n";
  }
  
  public Point computeSize( final int wHint, 
                            final int hHint, 
                            final boolean changed ) 
  {
    checkWidget();
    int height = 0, width = 0;
    if( wHint == RWT.DEFAULT || hHint == RWT.DEFAULT ) {
      boolean wrap = ( style & RWT.MULTI ) != 0 && ( style & RWT.WRAP ) != 0;
      int wrapWidth = 0;
      if( wrap && wHint != RWT.DEFAULT ) {
        wrapWidth = wHint;
      }
      Point extent = FontSizeEstimation.textExtent( getText(),
                                                    wrapWidth,
                                                    getFont() );
      if( extent.x != 0 ) {
        width = extent.x + 12;
      }
      if( extent.y != 0 ) {
        height = extent.y + 6;
      }
    }
    if( width == 0 ) {
      width = DEFAULT_WIDTH;
    }
    if( height == 0 ) {
      height = DEFAULT_HEIGHT;
    }
    if( wHint != RWT.DEFAULT ) {
      width = wHint;
    }
    if( hHint != RWT.DEFAULT ) {
      height = hHint;
    }
//    Rectangle trim = computeTrim( 0, 0, width, height );
//    return new Point( trim.width, trim.height );
    return new Point( width, height );
  }

  private static int checkStyle( final int style ) {
    int result = style;
    if( ( result & RWT.SINGLE ) != 0 && ( result & RWT.MULTI ) != 0 ) {
      result &= ~RWT.MULTI;
    }
    result = checkBits( result, RWT.LEFT, RWT.CENTER, RWT.RIGHT, 0, 0, 0 );
    if( ( result & RWT.SINGLE ) != 0 ) {
      result &= ~( RWT.H_SCROLL | RWT.V_SCROLL | RWT.WRAP );
    }
    if( ( result & RWT.WRAP ) != 0 ) {
      result |= RWT.MULTI;
      result &= ~RWT.H_SCROLL;
    }
    if( ( result & RWT.MULTI ) != 0 ) {
      result &= ~RWT.PASSWORD;
    }
    if( ( result & ( RWT.SINGLE | RWT.MULTI ) ) != 0 ) {
      return result;
    }
    if( ( style & ( RWT.H_SCROLL | RWT.V_SCROLL ) ) != 0 ) {
      return result | RWT.MULTI;
    }
    return result | RWT.SINGLE;
  }
}
