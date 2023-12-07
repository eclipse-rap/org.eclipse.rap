/*******************************************************************************
 * Copyright (c) 2007, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.textsize;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.RWTProperties;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;


final class TextSizeStorageUtil {

  static Point lookup( FontData fontData, String string, int wrapWidth, int mode ) {
    Point result = null;
    if( ProbeResultStore.getInstance().containsProbeResult( fontData ) ) {
      TextSizeStorage textSizeStorage = getTextSizeStorage();
      Integer key = getKey( fontData, string, wrapWidth, mode );
      result = textSizeStorage.lookupTextSize( key );
      if( result == null && wrapWidth > 0 ) {
        key = getKey( fontData, string, SWT.DEFAULT, mode );
        Point notWrappedSize = textSizeStorage.lookupTextSize( key );
        if( notWrappedSize != null && notWrappedSize.x <= wrapWidth ) {
          result = notWrappedSize;
        }
      }
    } else {
      MeasurementUtil.getMeasurementOperator().addProbeToMeasure( fontData );
    }
    return result;
  }

  static void store( FontData fontData,
                     String string,
                     int wrapWidth,
                     int mode,
                     Point measuredTextSize )
  {
    checkFontExists( fontData );
    Integer key = getKey( fontData, string, wrapWidth, mode );
    getTextSizeStorage().storeTextSize( key, measuredTextSize );
  }

  static Integer getKey( FontData fontData, String string, int wrapWidth, int mode ) {
    ProbeResultStore instance = ProbeResultStore.getInstance();
    ProbeResult probeResult = instance.getProbeResult( fontData );
    String probeText = probeResult.getProbe().getText();
    Point probeSize = probeResult.getSize();
    int hashCode = 1;
    hashCode = 31 * hashCode + probeText.hashCode();
    hashCode = 31 * hashCode + probeSize.hashCode();
    hashCode = 31 * hashCode + fontData.hashCode();
    hashCode = 31 * hashCode + string.hashCode();
    hashCode = 31 * hashCode + wrapWidth;
    hashCode = 31 * hashCode + mode;
    return Integer.valueOf( hashCode );
  }

  static TextSizeStorage getTextSizeStorage() {
    TextSizeStorage storage = ( (UISessionImpl) RWT.getUISession() ).getTextSizeStorage();
    if( storage == null ) {
        storage = getApplicationContext().getTextSizeStorage();
    }
    return storage;
  }

  private static void checkFontExists( FontData fontData ) {
    if( !ProbeResultStore.getInstance().containsProbeResult( fontData ) ) {
      String msg = "Font not probed yet: " + fontData.toString();
      throw new IllegalStateException( msg );
    }
  }

  private TextSizeStorageUtil() {
    // prevent instantiation
  }
}
