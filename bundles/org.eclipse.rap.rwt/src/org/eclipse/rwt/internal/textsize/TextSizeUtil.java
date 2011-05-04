/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;


import java.math.BigDecimal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.FontUtil;


public class TextSizeUtil {
  private static final int STRING_EXTENT = 0;
  private static final int TEXT_EXTENT = 1;

  public static Point stringExtent( Font font, String string ) {
    Point result;
    if( isEmptyString( string ) ) {
      result = createSizeForEmptyString( font );
    } else {
      result = determineTextSize( font, string, SWT.DEFAULT, STRING_EXTENT );
    }
    return result;
  }

  public static Point textExtent( Font font, String text, int wrapWidth ) {
    Point result = determineTextSize( font, text, wrapWidth, TEXT_EXTENT );
    // TODO [fappel]: replace with decent implementation
    if( wrapWidth > 0 && result.x > wrapWidth ) {
      result = improveHeightDetermination( font, text, wrapWidth );
    }
    return result;
  }

  public static int getCharHeight( Font font ) {
    int result;
    if( containsProbeResult( font ) ) {
      result = lookupCharHeight( font );
    } else {
      result = estimateCharHeight( font );
      addProbeToMeasure( font );
    }
    return result;
  }

  public static float getAvgCharWidth( Font font ) {
    float result;
    if( containsProbeResult( font ) ) {
      result = lookupAvgCharWidth( font );
    } else {
      result = estimateAvgCharWidth( font );
      addProbeToMeasure( font );
    }
    return result;
  }
  
  //////////////////
  // helping methods
  
  private static Point createSizeForEmptyString( Font font ) {
    return new Point( 0, getCharHeight( font ) );
  }

  private static boolean isEmptyString( String string ) {
    return string.length() == 0;
  }

  private static Point determineTextSize( Font font, String string, int wrapWidth, int mode ) {
    Point result = lookup( font, string, wrapWidth, mode );
    if( result == null ) {
      result = estimate( font, string, wrapWidth, mode );
      addItemToMeasure( font, string, wrapWidth, mode );
    }
    
    // TODO [rst] Still returns wrong result for texts that contain only
    //            whitespace (and possibly more that one line)
    if( isHeightZero( result ) ) {
      result = adjustHeightForWhitespaceTexts( font, result );
    }
    return result;
  }

  private static boolean isHeightZero( Point result ) {
    return result.y == 0;
  }

  private static Point lookup( Font font, String string, int wrapWidth, int mode ) {
    String measurementString = createMeasurementString( string, mode );
    FontData fontData = FontUtil.getData( font );
    return TextSizeStorageUtil.lookup( fontData, measurementString, wrapWidth );
  }

  private static Point estimate( Font font, String string, int wrapWidth, int mode ) {
    Point result;
    switch( mode ) {
      case STRING_EXTENT: {
        result = TextSizeEstimation.stringExtent( font, string );
      }
      break;
      case TEXT_EXTENT: {
        result = TextSizeEstimation.textExtent( font, string, wrapWidth );
      }
      break;
      default: {
        throw new IllegalStateException( "Unknown estimation mode." );
      }
    }
    return result;
  }

  private static void addItemToMeasure( Font font, String string, int wrapWidth, int mode ) {
    String measurementString = createMeasurementString( string, mode );
    MeasurementUtil.addItemToMeasure( measurementString, font, wrapWidth );
  }
  
  private static String createMeasurementString( String string, int mode ) {
    boolean expandNewLines = mode == TEXT_EXTENT;
    return TextSizeUtilFacade.createMeasurementString( string, expandNewLines );
  }
  
  private static Point improveHeightDetermination( Font font, String text, int wrapWidth ) {
    Point result = TextSizeEstimation.textExtent( font, text, wrapWidth );
    BigDecimal height = new BigDecimal( result.y );
    BigDecimal charHeight = new BigDecimal( TextSizeEstimation.getCharHeight( font ) );
    int rows = height.divide( charHeight, 0, BigDecimal.ROUND_HALF_UP ).intValue();
    result.y = getCharHeight( font ) * rows; // use the real char height if available...
    return result;
  }
  
  private static Point adjustHeightForWhitespaceTexts( Font font, Point result ) {
    return new Point( result.x, getCharHeight( font ) );
  }

  private static void addProbeToMeasure( Font font ) {
    MeasurementOperator.getInstance().addProbeToMeasure( FontUtil.getData( font ) );
  }

  private static int estimateCharHeight( Font font ) {
    return TextSizeEstimation.getCharHeight( font );
  }

  private static int lookupCharHeight( Font font ) {
    return getProbeResult( font ).getSize().y;
  }

  private static boolean containsProbeResult( Font font ) {
    return ProbeResultStore.getInstance().containsProbeResult( FontUtil.getData( font ) );
  }

  private static float estimateAvgCharWidth( Font font ) {
    return TextSizeEstimation.getAvgCharWidth( font );
  }

  private static float lookupAvgCharWidth( Font font ) {
    return getProbeResult( font ).getAvgCharWidth();
  }

  private static ProbeResult getProbeResult( Font font ) {
    FontData data = FontUtil.getData( font );
    return ProbeResultStore.getInstance().getProbeResult( data );
  }

  private TextSizeUtil() {
    // prevent instance creation
  }
}