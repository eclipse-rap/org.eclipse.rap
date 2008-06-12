/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import java.text.MessageFormat;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeProbeStore.IProbeResult;


final class TextSizeDataBase {

  public static Point lookup( final Font font,
                              final String string,
                              final int wrapWidth )
  {
    Point result = null;
    if( TextSizeProbeStore.getInstance().containsProbeResult( font ) ) {
      Integer key = getKey( font, string, wrapWidth );
      result = TextSizeStorageRegistry.obtain().lookupTextSize( key );
    } else {
      TextSizeProbeStore.addProbeRequest( font );
    }
    return result;
  }

  public static void store( final Font font,
                            final String string,
                            final int wrapWidth,
                            final Point calculatedTextSize )
  {
    if( !TextSizeProbeStore.getInstance().containsProbeResult( font ) ) {
      String txt = "Font ''{0}'' not probed yet.";
      Object[] args = new Object[] { font.toString() };
      String msg = MessageFormat.format( txt, args );
      throw new IllegalStateException( msg );
    }
    ITextSizeStorage registry = TextSizeStorageRegistry.obtain();
    Integer key = getKey( font, string, wrapWidth );
    registry.storeTextSize( key, calculatedTextSize );
  }

  // for test purposes only
  static void reset() {
    ITextSizeStorage registry = TextSizeStorageRegistry.obtain();
    if( registry instanceof DefaultTextSizeStorage ) {
      ( ( DefaultTextSizeStorage )registry ).resetStringSizes();
    }
  }


  // for test purposes only
  static Integer getKey( final Font font,
                         final String string,
                         final int wrapWidth )
  {
    TextSizeProbeStore instance = TextSizeProbeStore.getInstance();
    IProbeResult probeResult = instance.getProbeResult( font );
    String probeText = probeResult.getProbe().getString();
    Point probeSize = probeResult.getSize();
    FontData probeFontData = font.getFontData()[ 0 ];
    int hashCode = 1;
    hashCode = 31 * hashCode + probeText.hashCode();
    hashCode = 31 * hashCode + probeSize.hashCode();
    hashCode = 31 * hashCode + probeFontData.hashCode();
    hashCode = 31 * hashCode + string.hashCode();
    hashCode = 31 * hashCode + wrapWidth;
    return new Integer( hashCode );
  }
}