/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH.
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

import java.io.IOException;

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.textsize.TextSizeProbeStore.Probe;
import org.eclipse.rwt.internal.util.EncodingUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;


public final class TextSizeDeterminationFacadeImpl extends TextSizeDeterminationFacade {
  private static final String FUNCTION_MEASURE_STRINGS
    = "org.eclipse.swt.FontSizeCalculation.measureStrings";
  private static final String FUNCTION_PROBE
    = "org.eclipse.swt.FontSizeCalculation.probe";

  public String getStartupProbeCodeInternal() {
    StringBuffer result = new StringBuffer();
    Probe[] probeList = RWTFactory.getTextSizeProbeStore().getProbeList();
    if( probeList.length > 0 ) {
      result.append( FUNCTION_PROBE );
      result.append( "( [ " );
      for( int i = 0; i < probeList.length; i++ ) {
        Probe probe = probeList[ i ];
        result.append( createProbeParamFragment( probe ) );
        result.append( getParamFragmentSeparator( i, probeList.length ) );
      }
      result.append( " ] );" );
    }
    return result.toString();
  }

  // TODO [rst] Perform also TAB expansion to match the default of GC#textExtent( String )
  public String createMeasureStringInternal( final String string, final boolean expandNewLines ) {
    // TODO [fappel]: revise this - text escape may cause inaccurate calculations
    String result = WidgetLCAUtil.escapeText( string, true );
    String newLineReplacement = expandNewLines ? "<br/>" : " ";
    return WidgetLCAUtil.replaceNewLines( result, newLineReplacement );
  }

  public MeasurementItem[] writeStringMeasurementsInternal() throws IOException {
    MeasurementItem[] items = MeasurementUtil.getItemsToMeasure();
    if( items.length > 0 ) {
      StringBuffer param = new StringBuffer();
      param.append( "[ " );
      for( int i = 0; i < items.length; i++ ) {
        param.append( createItemParamFragment( items[ i ] ) );
        param.append( getParamFragmentSeparator( i, items.length ) );
      }
      param.append( " ]" );
      writeFunctionCall( FUNCTION_MEASURE_STRINGS, param );
    }
    return items;
  }

  public Probe[] writeFontProbingInternal() throws IOException {
    Probe[] requests = TextSizeProbeStore.getProbesToMeasure();
    if( requests.length > 0 ) {
      StringBuffer param = new StringBuffer();
      param.append( "[ " );
      for( int i = 0; i < requests.length; i++ ) {
        param.append( createProbeParamFragment( requests[ i ] ) );
        param.append( getParamFragmentSeparator( i, requests.length ) );
      }
      param.append( " ]" );
      writeFunctionCall( FUNCTION_PROBE, param );
    }
    return requests;
  }

  static String createItemParamFragment( MeasurementItem item ) {
    StringBuffer result = new StringBuffer();
    result.append( "[ " );
    result.append( item.hashCode() );
    result.append( ", \"" );
    String textToMeasure = item.getTextToMeasure();
    textToMeasure = EncodingUtil.escapeDoubleQuoted( textToMeasure );
    textToMeasure = EncodingUtil.escapeLeadingTrailingSpaces( textToMeasure );
    result.append( textToMeasure );
    result.append( "\", " );
    result.append( createFontParam( item.getFontData() ) );
    result.append( ", " );
    result.append( item.getWrapWidth() );
    result.append( " ]" );
    return result.toString();
  }

  static String createProbeParamFragment( Probe probe ) {
    FontData fontData = probe.getFontData();
    StringBuffer result = new StringBuffer();
    result.append( "[ " );
    result.append( fontData.hashCode() );
    result.append( ", \"" );
    result.append( probe.getText() );
    result.append( "\", " );
    result.append( createFontParam( fontData ) );
    result.append( " ]" );
    return result.toString();
  }

  private static String createFontParam( FontData fontData ) {
    StringBuffer result = new StringBuffer();
    String[] names = WidgetLCAUtil.parseFontName( fontData.getName() );
    result.append( "[ " );
    for( int i = 0; i < names.length; i++ ) {
      result.append( "\"" );
      result.append( names [ i ] );
      result.append( "\"" );
      if( i < names.length - 1 ) {
        result.append( ", " );
      }
    }
    result.append( " ], " );
    result.append( fontData.getHeight() );
    result.append( ", " );
    result.append( ( fontData.getStyle() & SWT.BOLD ) != 0 );
    result.append( ", " );
    result.append( ( fontData.getStyle() & SWT.ITALIC ) != 0 );
    return result.toString();
  }
  
  private String getParamFragmentSeparator( int currentIndex, int lengthCount ) {
    String result = "";
    if( isNotLast( currentIndex, lengthCount ) ) {
      result = ", ";
    }
    return result;
  }

  private boolean isNotLast( int currentIndex, int lengthCount ) {
    return currentIndex < lengthCount - 1;
  }

  private void writeFunctionCall( String functionName, StringBuffer param ) throws IOException {
    getWriter().callStatic( functionName, new Object[] { new JSVar( param.toString() ) } );
  }

  private JSWriter getWriter() {
    return JSWriter.getWriterForResetHandler();
  }
}