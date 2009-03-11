/*******************************************************************************
 * Copyright (c) 2008, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.CommonPatterns;
import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.internal.graphics.TextSizeDetermination.ICalculationItem;
import org.eclipse.swt.internal.graphics.TextSizeProbeStore.IProbe;
import org.eclipse.swt.widgets.*;


public final class TextSizeDeterminationFacadeImpl
  extends TextSizeDeterminationFacade
{

  public static String getStartupProbeCode() {
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

  // TODO [rst] Perform also TAB expansion to match the default of
  //            GC#textExtent( String )
  public String createMeasureStringInternal( final String string,
                                             final boolean expandNewLines )
  {
    // TODO [fappel]: revise this - text escape may cause inaccurate
    //                calculations
    String result = WidgetLCAUtil.escapeText( string, true );
    String newLineReplacement = expandNewLines ? "<br/>" : " ";
    result = WidgetLCAUtil.replaceNewLines( result, newLineReplacement );
    return result;
  }

  public ICalculationItem[] writeStringMeasurementsInternal()
    throws IOException
  {
    ICalculationItem[] items = TextSizeDetermination.getCalculationItems();
    if( items.length > 0 ) {
      JSWriter writer = JSWriter.getWriterForResetHandler();
      StringBuffer param = new StringBuffer();
      param.append( "[ " );
      for( int i = 0; i < items.length; i++ ) {
        param.append( "[ " );
        ICalculationItem item = items[ i ];
        param.append( item.hashCode() );
        param.append( ", " );
        param.append( "\"" );
        String itemString = item.getString();
        itemString = CommonPatterns.escapeDoubleQuoted( itemString );
        itemString = CommonPatterns.escapeLeadingTrailingSpaces( itemString );
        param.append( itemString );
        param.append( "\", " );
        param.append( createFontParam( item.getFont() ) );
        param.append( ", " );
        param.append( item.getWrapWidth() );
        param.append( " ]" );
        if( i < items.length - 1 ) {
          param.append( ", " );
        }
      }
      param.append( " ]" );
      String funcName = "org.eclipse.swt.FontSizeCalculation.measureStrings";
      writer.callStatic( funcName,
                         new Object[] { new JSVar( param.toString() ) } );
    }
    return items;
  }

  public IProbe[] writeFontProbingInternal() throws IOException {
    IProbe[] requests = TextSizeProbeStore.getProbeRequests();
    if( requests.length > 0 ) {
      JSWriter writer = JSWriter.getWriterForResetHandler();
      StringBuffer param = new StringBuffer();
      param.append( "[ " );
      for( int i = 0; i < requests.length; i++ ) {
        IProbe probe = requests[ i ];
        param.append( probe.getJSProbeParam() );
        if( i < requests.length - 1 ) {
          param.append( ", " );
        }
      }
      param.append( " ]" );
      String funcName = "org.eclipse.swt.FontSizeCalculation.probe";
      writer.callStatic( funcName,
                         new Object[] { new JSVar( param.toString() ) } );
    }
    return requests;
  }

  public String createFontParamInternal( final Font font ) {
    // TODO [fappel]: For convenience I reused the the WidgetLCAUtil#writeFont
    //                method. This may have performance problems since a lot
    //                of buffering and some additional string operations are
    //                used. So revise this...
    StringBuffer result = new StringBuffer();
    Shell shell = new Shell( Display.getCurrent(), SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter buffer = stateInfo.getResponseWriter();
    try {
      HtmlResponseWriter htmlWriter = new HtmlResponseWriter();
      stateInfo.setResponseWriter( htmlWriter );
      WidgetLCAUtil.writeFont( label, font );
      StringBuffer js = new StringBuffer();
      for( int j = 0; j < htmlWriter.getBodySize(); j++ ) {
        js.append( htmlWriter.getBodyToken( j ) );
      }
      String fontString = js.toString();
      String[] split = fontString.split( "\\[" );
      int offset = split[ 1 ].length() - 3;
      result.append( "[" );
      result.append( split[ 1 ].substring( 0, offset ) );
    } catch( final IOException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      shell.dispose();
      stateInfo.setResponseWriter( buffer );
    }
    return result.toString();
  }
}
