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

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.textsize.TextSizeProbeResults.ProbeResult;
import org.eclipse.rwt.internal.textsize.TextSizeProbeStore.Probe;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;


public class TextSizeDetermination {
  private static final int STRING_EXTENT = 0;
  private static final int TEXT_EXTENT = 1;
  private static final int MARKUP_EXTENT = 2;


  private TextSizeDetermination() {
    // prevent instance creation
  }

  public static Point stringExtent( Font font, String string ) {
    Point result;
    if( string.length() == 0 ) {
      result = new Point( 0, getCharHeight( font ) );
    } else {
      result = doMeasurement( font, string, SWT.DEFAULT, STRING_EXTENT );
    }
    return result;
  }

  public static Point textExtent( Font font, String string, int wrapWidth ) {
    return internalExtent( font, string, wrapWidth, TEXT_EXTENT );
  }

  public static Point markupExtent( Font font, String string, int wrapWidth ) {
    return internalExtent( font, string, wrapWidth, MARKUP_EXTENT );
  }

  private static Point internalExtent( Font font, String string, int wrapWidth, int mode ) {
    // TODO [fappel]: replace with decent implementation
    Point result;
    int estimationMode = mode;
    if( wrapWidth <= 0 ) {
      result = doMeasurement( font, string, wrapWidth, estimationMode );
    } else {
      Point testSize = doMeasurement( font, string, wrapWidth, estimationMode );
      if( testSize.x <= wrapWidth ) {
        result = testSize;
      } else {
        result = TextSizeEstimation.textExtent( font, string, wrapWidth );
        BigDecimal height = new BigDecimal( result.y );
        BigDecimal charHeight = new BigDecimal( TextSizeEstimation.getCharHeight( font ) );
        int rows = height.divide( charHeight, 0, BigDecimal.ROUND_HALF_UP ).intValue();
        result.y = getCharHeight( font ) * rows;
      }
    }
    return result;
  }


  private static Point doMeasurement( Font font, String string, int wrapWidth, int mode ) {
    String toMeasure = string;
    if( mode != MARKUP_EXTENT ) {
      boolean expandNewLines = mode == TEXT_EXTENT;
      toMeasure = TextSizeDeterminationFacade.createMeasureString( string, expandNewLines );
    }
    FontData fontData = font.getFontData()[ 0 ];
    Point result = TextSizeDataBase.lookup( fontData, toMeasure, wrapWidth );
    if( result == null ) {
      switch( mode ) {
        case MARKUP_EXTENT: {
          result = TextSizeEstimation.textExtent( font, toMeasure, wrapWidth );
        }
        break;
        case TEXT_EXTENT: {
          result = TextSizeEstimation.textExtent( font, string, wrapWidth );
          break;
        }
        case STRING_EXTENT: {
          result = TextSizeEstimation.stringExtent( font, string );
        }
        break;
        default: {
          throw new IllegalStateException( "Unknown estimation mode." );
        }
      }
      MeasurementUtil.addItemToMeasure( new MeasurementItem( toMeasure, fontData, wrapWidth ) );
    }
    // TODO [rst] Still returns wrong result for texts that contain only
    //            whitespace (and possibly more that one line)
    if( result.y == 0 ) {
      result.y = getCharHeight( font );
    }
    return result;
  }

  public static int getCharHeight( Font font ) {
    int result;
    FontData fontData = font.getFontData()[ 0 ];
    TextSizeProbeResults probeStore = TextSizeProbeResults.getInstance();
    if( probeStore.containsProbeResult( fontData ) ) {
      ProbeResult probeResult = probeStore.getProbeResult( fontData );
      result = probeResult.getSize().y;
    } else {
      TextSizeProbeStore.addProbeRequest( fontData );
      result = TextSizeEstimation.getCharHeight( font );
    }
    return result;
  }

  public static float getAvgCharWidth( Font font ) {
    float result;
    TextSizeProbeResults probeStore = TextSizeProbeResults.getInstance();
    FontData fontData = font.getFontData()[ 0 ];
    if( probeStore.containsProbeResult( fontData ) ) {
      ProbeResult probeResult = probeStore.getProbeResult( fontData );
      result = probeResult.getAvgCharWidth();
    } else {
      TextSizeProbeStore.addProbeRequest( fontData );
      result = TextSizeEstimation.getAvgCharWidth( font );
    }
    return result;
  }

  public static void readStartupProbes() {
    Probe[] probeList = RWTFactory.getTextSizeProbeStore().getProbeList();
    MeasurementHandler.readProbedFonts( probeList );
  }

  public static int getProbeCount() {
    return RWTFactory.getTextSizeProbeStore().getProbeList().length;
  }

  public static String getStartupProbeCode() {
    return TextSizeDeterminationFacade.getStartupProbeCode();
  }
}