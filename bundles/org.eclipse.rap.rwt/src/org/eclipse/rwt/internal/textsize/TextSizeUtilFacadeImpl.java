/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH and others.
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;


public final class TextSizeUtilFacadeImpl extends TextSizeUtilFacade {
  static final String PROPERTY_STRINGS = "strings";
  static final String METHOD_MEASURE_STRINGS = "measureStrings";
  static final String PROPERTY_FONTS = "fonts";
  static final String METHOD_PROBE = "probe";

  public Object[] getStartupProbeObjectInternal() {
    Object[] result = null;
    Probe[] probeList = RWTFactory.getProbeStore().getProbes();
    if( probeList.length > 0 ) {
      result = new Object[ probeList.length ];
      for( int i = 0; i < probeList.length; i++ ) {
        result[ i ] = createProbeParamObject( probeList[ i ] );
      }
    }
    return result;
  }

  // TODO [rst] Perform also TAB expansion to match the default of GC#textExtent( String )
  public String createMeasurementStringInternal( String string, boolean expandNewLines ) {
    // TODO [fappel]: revise this - text escape may cause inaccurate calculations
    return expandNewLines ? string : WidgetLCAUtil.replaceNewLines( string, " " );
  }

  public void writeStringMeasurementsInternal() {
    MeasurementItem[] items = MeasurementOperator.getInstance().getItems();
    if( items.length > 0 ) {
      Object[] itemsObject = new Object[ items.length ];
      for( int i = 0; i < items.length; i++ ) {
        itemsObject[ i ] = createItemParamObject( items[ i ] );
      }
      callDisplayMethod( METHOD_MEASURE_STRINGS, PROPERTY_STRINGS, itemsObject );
    }
  }

  public void writeFontProbingInternal() {
    Probe[] probeList = MeasurementOperator.getInstance().getProbes();
    if( probeList.length > 0 ) {
      Object[] probesObject = new Object[ probeList.length ];
      for( int i = 0; i < probeList.length; i++ ) {
        probesObject[ i ] = createProbeParamObject( probeList[ i ] );
      }
      callDisplayMethod( METHOD_PROBE, PROPERTY_FONTS, probesObject );
    }
  }

  private static void callDisplayMethod( String method, String property, Object value ) {
    Display display = Display.getCurrent();
    if( display != null ) {
      IClientObject clientObject = ClientObjectFactory.getForDisplay( display );
      Map<String, Object> args = new HashMap<String, Object>();
      args.put( property, value );
      clientObject.call( method, args );
    }
  }

  static Object createItemParamObject( MeasurementItem item ) {
    Object[] result = new Object[ 7 ];
    result[ 0 ] = new Integer( item.hashCode() );
    result[ 1 ] = item.getTextToMeasure();
    FontData fontData = item.getFontData();
    result[ 2 ] = WidgetLCAUtil.parseFontName( fontData.getName() );
    result[ 3 ] = new Integer( fontData.getHeight() );
    result[ 4 ] = Boolean.valueOf( ( fontData.getStyle() & SWT.BOLD ) != 0 );
    result[ 5 ] = Boolean.valueOf( ( fontData.getStyle() & SWT.ITALIC ) != 0 );
    result[ 6 ] = new Integer( item.getWrapWidth() );
    return result;
  }

  static Object createProbeParamObject( Probe probe ) {
    Object[] result = new Object[ 6 ];
    FontData fontData = probe.getFontData();
    result[ 0 ] = new Integer( fontData.hashCode() );
    result[ 1 ] = probe.getText();
    result[ 2 ] = WidgetLCAUtil.parseFontName( fontData.getName() );
    result[ 3 ] = new Integer( fontData.getHeight() );
    result[ 4 ] = Boolean.valueOf( ( fontData.getStyle() & SWT.BOLD ) != 0 );
    result[ 5 ] = Boolean.valueOf( ( fontData.getStyle() & SWT.ITALIC ) != 0 );
    return result;
  }
}