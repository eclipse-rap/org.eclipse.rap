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

import java.text.*;
import java.util.Locale;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Point;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.graphics.FontSizeEstimation;
import com.w4t.util.SessionLocale;


// TODO [rh] style WRAP not yet supported
// TODO cut/copy/past not implemented
// TODO SelectionListener: widgetSelected is fired whenever the value changes
public class Spinner extends Composite {
  
  private static final int UP_DOWN_HEIGHT = 18;
  
  private int digits = 0;
  private int increment = 1;
  private int maximum = 100;
  private int minimum = 0;
  private int pageIncrement = 10;
  private int selection = 0;

  public Spinner( final Composite parent, final int style ) {
    super( parent, checkStyle( style ) );
  }

  // TODO [rh] the qooxdoo Spinner widget does not provide decimal places
  public int getDigits () {
    checkWidget ();
    return digits;
  }

//  public void setDigits( final int value ) {
//    checkWidget();
//  }

  /////////////////////////////////////////
  // Methods to control range and increment 
  
  public int getIncrement () {
    checkWidget ();
    return increment;
  }

  public void setIncrement( final int value ) {
    checkWidget();
    if( value >= 1 ) {
      increment = value;
    }
  }
  
  public int getMinimum () {
    checkWidget ();
    return minimum;
  }
  
  public void setMinimum( final int value ) {
    checkWidget();
    if( value >= 0 && value <= maximum ) {
      minimum = value;
      if( selection < minimum ) {
        selection = minimum;
      }
    }
  }
  
  public int getMaximum () {
    checkWidget ();
    return maximum;
  }

  public void setMaximum( final int value ) {
    checkWidget();
    if( value >= 0 && value >= minimum ) {
      maximum = value;
      if( selection > maximum ) {
        selection = maximum;
      }
    } 
  }
  
  public int getPageIncrement () {
    checkWidget ();
    return pageIncrement;
  }
  
  public void setPageIncrement( final int value ) {
    checkWidget();
    if( value >= 1 ) {
      pageIncrement = value;
    } 
  }

  public int getSelection () {
    checkWidget();
    return selection;
  }
  
  
  public void setSelection( final int value ) {
    checkWidget();
    selection = Math.min( Math.max( minimum, value ), maximum );
  }
  
  public void setValues( final int selection,
                         final int minimum,
                         final int maximum,
                         final int digits,
                         final int increment,
                         final int pageIncrement )
  {
    setMinimum( minimum );
    setMaximum( maximum );
    // setDigits( digits ) - ignore since we cannot (yet) handle digits
    setIncrement( increment );
    setPageIncrement( pageIncrement );
    setSelection( selection );
  }

  ///////////////////
  // Size calculation
  
  public Point computeSize( final int wHint,
                            final int hHint,
                            final boolean changed )
  {
    checkWidget();
    int width = 0;
    int height = 0;
    if( wHint == RWT.DEFAULT || hHint == RWT.DEFAULT ) {
      String string = String.valueOf( maximum );
      if( digits > 0 ) {
        StringBuffer buffer = new StringBuffer();
        buffer.append( string );
        buffer.append( getDecimalSeparator() );
        int count = digits - string.length();
        while( count >= 0 ) {
          buffer.append( "0" );
          count--;
        }
        string = buffer.toString();
      }
      Point textSize = FontSizeEstimation.stringExtent( string, getFont() );
      width = textSize.y;
      height = textSize.x;
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
    Rectangle trim = computeTrim( 0, 0, width, height );
    if( hHint == RWT.DEFAULT ) {
      int upDownHeight = UP_DOWN_HEIGHT + 2 * getBorderWidth();
      trim.height = Math.max( trim.height, upDownHeight );
    }
    return new Point( trim.width, trim.height );
  }

  public Rectangle computeTrim( final int x,
                                final int y,
                                final int width,
                                final int height )
  {
    checkWidget();
    Rectangle result = new Rectangle( x, y, width, height );
    int margins = 1;
    result.x -= margins;
    result.width += margins;
    if( ( style & RWT.BORDER ) != 0 ) {
      result.x -= 1;
      result.y -= 1;
      result.width += 2;
      result.height += 2;
    }
    result.width += ScrollBar.SCROLL_BAR_WIDTH;
    return result;
  }
  
  //////////////////
  // Helping methods
  
  String getDecimalSeparator () {
    Locale locale;
    if( SessionLocale.isSet() ) {
      locale = SessionLocale.get();
    } else {
      locale = Locale.getDefault();
    }
    NumberFormat numberFormat = NumberFormat.getCurrencyInstance( locale );
    DecimalFormat format = ( DecimalFormat )numberFormat;
    DecimalFormatSymbols formatSymbols = format.getDecimalFormatSymbols();
    char decimalSeparator = formatSymbols.getDecimalSeparator();
    return String.valueOf( decimalSeparator );
  }

  private static int checkStyle( final int style ) {
    /*
    * Even though it is legal to create this widget
    * with scroll bars, they serve no useful purpose
    * because they do not automatically scroll the
    * widget's client area.  The fix is to clear
    * the SWT style.
    */
    return style & ~( RWT.H_SCROLL | RWT.V_SCROLL );
  }
}
