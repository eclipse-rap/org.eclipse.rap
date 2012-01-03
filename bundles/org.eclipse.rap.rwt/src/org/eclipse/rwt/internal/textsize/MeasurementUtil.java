/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.internal.graphics.FontUtil;


public class MeasurementUtil {

  public static int getProbeCount() {
    return RWTFactory.getProbeStore().getSize();
  }

  public static Object getStartupProbeObject() {
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

  static void addItemToMeasure( String toMeasure, Font font, int wrapWidth ) {
    FontData fontData = FontUtil.getData( font );
    MeasurementItem newItem = new MeasurementItem( toMeasure, fontData, wrapWidth );
    MeasurementOperator.getInstance().addItemToMeasure( newItem );
  }

  private MeasurementUtil() {
    // prevent instance creation
  }
}