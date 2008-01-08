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


import java.math.BigDecimal;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeProbeStore.IProbe;
import org.eclipse.swt.internal.graphics.TextSizeProbeStore.IProbeResult;


public class TextSizeDetermination {

  private static final String JS_CALCULATOR
    = TextSizeDetermination.class.getName() + ".hasJSCalculator";
  private static final String CALCULATION_ITEMS
    = TextSizeDetermination.class.getName() + ".CalculationItems";
  private static final ICalculationItem[] EMTY_ITEMS
    = new ICalculationItem[ 0 ];
  private static final int STRING_EXTENT = 0;
  private static final int TEXT_EXTENT = 1;


  public interface ICalculationItem {
    Font getFont();
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
    // TODO [fappel]: replace with decent implementation
    Point result;
    if( wrapWidth <= 0 ) {
      result = doMeasurement( font, string, wrapWidth, TEXT_EXTENT );
      // TODO [rst] Still returns wrong result for texts that contain only
      //            whitespace ( and possibly more that one line )
      if( result.y == 0 ) {
        result.y = getCharHeight( font );
      }
    } else {
      Point testSize = doMeasurement( font, string, wrapWidth, TEXT_EXTENT );
      if( testSize.x < wrapWidth ) {
        result = testSize;
      } else {
        result = TextSizeEstimation.textExtent( font, string, wrapWidth );
        BigDecimal height = new BigDecimal( result.y );
        BigDecimal charHeight
          = new BigDecimal( TextSizeEstimation.getCharHeight( font ) );
        int rows
          = height.divide( charHeight, 0, BigDecimal.ROUND_HALF_UP ).intValue();
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
    boolean expandLineDelimitors = estimationMode == TEXT_EXTENT;
    String toMeasure = createMeasureString( string, expandLineDelimitors );
    Point result = TextSizeDataBase.lookup( font, toMeasure, wrapWidth );
    if( result == null ) {
      switch( estimationMode ) {
        case TEXT_EXTENT: {
          result = TextSizeEstimation.textExtent( font, string, wrapWidth );
        }
        break;
        case STRING_EXTENT: {
          result = TextSizeEstimation.stringExtent( font, string );
        }
        break;
        default: {
          throw new IllegalStateException( "Unknown estimation mode." );
        }
      }
      addCalculationItem( font, toMeasure, wrapWidth );
    }
    return result;
  }

  public static int getCharHeight( final Font font ) {
    int result;
    TextSizeProbeStore probeStore = TextSizeProbeStore.getInstance();
    if( probeStore.containsProbeResult( font ) ) {
      IProbeResult probeResult = probeStore.getProbeResult( font );
      result = probeResult.getSize().y;
    } else {
      TextSizeProbeStore.addProbeRequest( font );
      result = TextSizeEstimation.getCharHeight( font );
    }
    return result;
  }

  public static float getAvgCharWidth( final Font font ) {
    float result;
    TextSizeProbeStore probeStore = TextSizeProbeStore.getInstance();
    if( probeStore.containsProbeResult( font ) ) {
      IProbeResult probeResult = probeStore.getProbeResult( font );
      result = probeResult.getAvgCharWidth();
    } else {
      TextSizeProbeStore.addProbeRequest( font );
      result = TextSizeEstimation.getAvgCharWidth( font );
    }
    return result;
  }

  public static String writeStartupJSProbe() {
    StringBuffer result = new StringBuffer();
    IProbe[] probeList = TextSizeProbeStore.getProbeList();
    if( probeList.length > 0 ) {
      result.append( "org.eclipse.swt.FontSizeCalculation.probe(" );
      result.append( "[ " );
      for( int i = 0; i < probeList.length; i++ ) {
        IProbe probe = probeList[ i ];
        result.append( probe.getJSProbeParam() );
        if( i < probeList.length - 1 ) {
          result.append( ", " );
        }
      }
      result.append( " ] );" );
    }
    return result.toString();
  }

  public static void readStartupProbes() {
    IProbe[] probeList = TextSizeProbeStore.getProbeList();
    TextSizeDeterminationHandler.readProbedFonts( probeList );
  }
  
  public static int getProbeCount() {
    return TextSizeProbeStore.getProbeList().length;
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

  private static void addCalculationItem( final Font font,
                                          final String string,
                                          final int wrapWidth )
  {
    ICalculationItem[] oldItems = getCalculationItems();
    boolean mustAdd = true;
    for( int i = 0; mustAdd && i < oldItems.length; i++ ) {
      FontData oldFontData = oldItems[ i ].getFont().getFontData()[ 0 ];
      mustAdd = !(    oldItems[ i ].getString().equals( string )
                   && oldFontData.equals( font.getFontData()[ 0 ] ) );
    }
    if( mustAdd ) {
      ICalculationItem[] newItems = new ICalculationItem[ oldItems.length + 1 ];
      System.arraycopy( oldItems, 0, newItems, 0, oldItems.length );
      newItems[ oldItems.length ] = new ICalculationItem() {
        public Font getFont() {
          return font;
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
      if( stateInfo.getAttribute( JS_CALCULATOR ) == null ) {
        stateInfo.setAttribute( JS_CALCULATOR, new Object() );
        TextSizeDeterminationHandler.register();
      }
    }
  }

  // TODO [rst] Perform also TAB expansion to match the default of GC#textExtent( String )
  private static String createMeasureString( final String string,
                                             final boolean expandLineDelimitors )
  {
    // TODO [fappel]: revise this - text escape may cause inaccurate
    //                calculations
    String result = WidgetLCAUtil.escapeText( string, true );
    String newLineReplacement = expandLineDelimitors ? "<br/>" : " ";
    result = WidgetLCAUtil.replaceNewLines( result, newLineReplacement );
    return result;
  }
}