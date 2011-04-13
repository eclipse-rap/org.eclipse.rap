/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.graphics.TextSizeProbeResults.ProbeResult;


final class TextSizeDataBase {

  static Point lookup( FontData font, String string, int wrapWidth ) {
    Point result = null;
    if( TextSizeProbeResults.getInstance().containsProbeResult( font ) ) {
      Integer key = getKey( font, string, wrapWidth );
      result = RWTFactory.getTextSizeStorageRegistry().obtain().lookupTextSize( key );
    } else {
      TextSizeProbeStore.addProbeRequest( font );
    }
    return result;
  }

  static void store( FontData fontData, String string, int wrapWidth, Point calculatedTextSize ) {
    if( !TextSizeProbeResults.getInstance().containsProbeResult( fontData ) ) {
      String msg = "Font not probed yet: " + fontData.toString();
      throw new IllegalStateException( msg );
    }
    ITextSizeStorage registry = RWTFactory.getTextSizeStorageRegistry().obtain();
    Integer key = getKey( fontData, string, wrapWidth );
    registry.storeTextSize( key, calculatedTextSize );
  }

  static Integer getKey( FontData fontData, String string, int wrapWidth ) {
    TextSizeProbeResults instance = TextSizeProbeResults.getInstance();
    ProbeResult probeResult = instance.getProbeResult( fontData );
    String probeText = probeResult.getProbe().getText();
    Point probeSize = probeResult.getSize();
    int hashCode = 1;
    hashCode = 31 * hashCode + probeText.hashCode();
    hashCode = 31 * hashCode + probeSize.hashCode();
    hashCode = 31 * hashCode + fontData.hashCode();
    hashCode = 31 * hashCode + string.hashCode();
    hashCode = 31 * hashCode + wrapWidth;
    return new Integer( hashCode );
  }
  
  
  private TextSizeDataBase() {
    // prevent instantiation
  }
}