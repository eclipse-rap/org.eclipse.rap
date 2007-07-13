/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.internal.graphics.FontSizeProbeStore.IProbeResult;


final class FontSizeDataBase {

  public static Point lookup( final Font font, 
                              final String string,
                              final int wrapWidth )
  {
    Point result = null;
    if( FontSizeProbeStore.getInstance().containsProbeResult( font ) ) {
      Integer key = getKey( font, string, wrapWidth );
      result = FontSizeStorageRegistry.obtain().lookupStringSize( key );
    } else {
      FontSizeProbeStore.addProbeRequest( font );
    }
    return result;
  }

  public static void store( final Font font,
                            final String string,
                            int wrapWidth, 
                            final Point calculatedTextSize )
  {
    if( !FontSizeProbeStore.getInstance().containsProbeResult( font ) ) {
      String txt = "Font ''{0}'' not probed yet.";
      String msg = MessageFormat.format( txt, new Object[] { font.toString() } );
      throw new IllegalStateException( msg );
    }
    IFontSizeStorage registry = FontSizeStorageRegistry.obtain();
    Integer key = getKey( font, string, wrapWidth );
    registry.storeStringSize( key, calculatedTextSize );
  }

  // for test purposes only
  static void reset() {
    IFontSizeStorage registry = FontSizeStorageRegistry.obtain();
    if( registry instanceof DefaultFontSizeStorage ) {
      ( ( DefaultFontSizeStorage )registry ).resetStringSizes();
    }
  }
  
  
  private static Integer getKey( final Font font,
                                 final String string, 
                                 final int wrapWidth )
  {
    FontSizeProbeStore instance = FontSizeProbeStore.getInstance();
    IProbeResult probeResult = instance.getProbeResult( font );
    String probeText = probeResult.getProbe().getString();
    Point probeSize = probeResult.getSize();
    FontData probFontData = font.getFontData()[ 0 ];
    // TODO [fappel]: check hashcode calculation
    int hashCode =   probeText.hashCode()
                   ^ probeSize.hashCode()
                   ^ probFontData.hashCode() 
                   ^ string.hashCode()
                   ^ wrapWidth;
    return new Integer( hashCode );
  }
}