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
package org.eclipse.swt.internal.graphics;


import java.math.BigDecimal;

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeProbeResults.IProbeResult;
import org.eclipse.swt.internal.graphics.TextSizeProbeStore.IProbe;


public class TextSizeDetermination {

  private static final String HAS_CALCULATOR
    = TextSizeDetermination.class.getName() + ".hasCalculator";
  private static final String CALCULATION_ITEMS
    = TextSizeDetermination.class.getName() + ".CalculationItems";
  private static final ICalculationItem[] EMTY_ITEMS = new ICalculationItem[ 0 ];
  
  private static final int STRING_EXTENT = 0;
  private static final int TEXT_EXTENT = 1;
  private static final int MARKUP_EXTENT = 2;


  public interface ICalculationItem {
    FontData getFontData();
    String getString();
    int getWrapWidth();
  }


  private TextSizeDetermination() {
    // prevent instance creation
  }

  public static Point stringExtent( final Font font, final String string ) {
    Point result;
    if( string.length() == 0 ) {
      result = new Point( 0, getCharHeight( font ) );
    } else {
      result = doMeasurement( font, string, SWT.DEFAULT, STRING_EXTENT );
    }
    return result;
  }

  public static Point textExtent( final Font font,
                                  final String string,
                                  final int wrapWidth )
  {
    return internalExtent( font, string, wrapWidth, TEXT_EXTENT );
  }

  public static Point markupExtent( final Font font,
                                    final String string,
                                    final int wrapWidth )
  {
    return internalExtent( font, string, wrapWidth, MARKUP_EXTENT );
  }

  private static Point internalExtent( final Font font,
                                       final String string,
                                       final int wrapWidth,
                                       final int estMode )
  {
    // TODO [fappel]: replace with decent implementation
    Point result;
    int estimationMode = estMode;
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


  private static Point doMeasurement( final Font font,
                                      final String string,
                                      final int wrapWidth,
                                      final int estimationMode )
  {
    String toMeasure = string;
    if( estimationMode != MARKUP_EXTENT ) {
      boolean expandNewLines = estimationMode == TEXT_EXTENT;
      toMeasure = TextSizeDeterminationFacade.createMeasureString( string, expandNewLines );
    }
    FontData fontData = font.getFontData()[ 0 ];
    Point result = TextSizeDataBase.lookup( fontData, toMeasure, wrapWidth );
    if( result == null ) {
      switch( estimationMode ) {
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
      addCalculationItem( fontData, toMeasure, wrapWidth );
    }
    // TODO [rst] Still returns wrong result for texts that contain only
    //            whitespace (and possibly more that one line)
    if( result.y == 0 ) {
      result.y = getCharHeight( font );
    }
    return result;
  }

  public static int getCharHeight( final Font font ) {
    int result;
    FontData fontData = font.getFontData()[ 0 ];
    TextSizeProbeResults probeStore = TextSizeProbeResults.getInstance();
    if( probeStore.containsProbeResult( fontData ) ) {
      IProbeResult probeResult = probeStore.getProbeResult( fontData );
      result = probeResult.getSize().y;
    } else {
      TextSizeProbeStore.addProbeRequest( fontData );
      result = TextSizeEstimation.getCharHeight( font );
    }
    return result;
  }

  public static float getAvgCharWidth( final Font font ) {
    float result;
    TextSizeProbeResults probeStore = TextSizeProbeResults.getInstance();
    FontData fontData = font.getFontData()[ 0 ];
    if( probeStore.containsProbeResult( fontData ) ) {
      IProbeResult probeResult = probeStore.getProbeResult( fontData );
      result = probeResult.getAvgCharWidth();
    } else {
      TextSizeProbeStore.addProbeRequest( fontData );
      result = TextSizeEstimation.getAvgCharWidth( font );
    }
    return result;
  }

  public static void readStartupProbes() {
    IProbe[] probeList = RWTFactory.getTextSizeProbeStore().getProbeList();
    TextSizeDeterminationHandler.readProbedFonts( probeList );
  }

  public static int getProbeCount() {
    return RWTFactory.getTextSizeProbeStore().getProbeList().length;
  }

  public static ICalculationItem[] getCalculationItems() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    ICalculationItem[] result
      = ( ICalculationItem[] )stateInfo.getAttribute( CALCULATION_ITEMS );
    if( result == null ) {
      result = EMTY_ITEMS;
    }
    return result;
  }

  private static void addCalculationItem( final FontData fontData,
                                          final String string,
                                          final int wrapWidth )
  {
    ICalculationItem[] oldItems = getCalculationItems();
    boolean mustAdd = true;
    for( int i = 0; mustAdd && i < oldItems.length; i++ ) {
      FontData oldFontData = oldItems[ i ].getFontData();
      mustAdd = !(    oldItems[ i ].getString().equals( string )
                   && oldFontData.equals( fontData )
                   && oldItems[ i ].getWrapWidth() == wrapWidth );
    }
    if( mustAdd ) {
      ICalculationItem[] newItems = new ICalculationItem[ oldItems.length + 1 ];
      System.arraycopy( oldItems, 0, newItems, 0, oldItems.length );
      newItems[ oldItems.length ] = new ICalculationItem() {
        public FontData getFontData() {
          return fontData;
        }
        public String getString() {
          return string;
        }
        public int getWrapWidth() {
          return wrapWidth;
        }
      };
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      stateInfo.setAttribute( CALCULATION_ITEMS, newItems );
      // TODO [rst] Unnecessary check? TextSizeDeterminationHandler#register
      //            ensures that code is executed only once
      if( stateInfo.getAttribute( HAS_CALCULATOR ) == null ) {
        stateInfo.setAttribute( HAS_CALCULATOR, new Object() );
        TextSizeDeterminationHandler.register();
      }
    }
  }

  public static String getStartupProbeCode() {
    return TextSizeDeterminationFacade.getStartupProbeCode();
  }
}