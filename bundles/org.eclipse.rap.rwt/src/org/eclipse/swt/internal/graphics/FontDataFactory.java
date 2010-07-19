/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import java.util.Hashtable;
import java.util.Map;

import org.eclipse.swt.graphics.FontData;


/**
 * This class provides shared FontData.
 */
public final class FontDataFactory {

  private static final Map fontDataCache = new Hashtable();

  public static FontData findFontData( final String name,
                                       final int height,
                                       final int style )
  {
    FontData fontData;
    // Note [rst]: We don't need to synchronize get and put here. Since the
    //             creation of FontData is deterministic, concurrent access
    //             can at worst lead to one FontData instance overwriting the
    //             other. In this rare case, two equal instances would be in use
    //             in the system.
    int hashCode = ResourceFactory.fontHashCode( name, height, style );
    Object key = new Integer( hashCode );
    fontData = ( FontData )fontDataCache.get( key );
    if( fontData == null ) {
      fontData = new FontData( name, height, style );
      fontDataCache.put( key, fontData );
    }
    return fontData;
  }

  public static FontData findFontData( final FontData fontData ) {
    return findFontData( fontData.getName(),
                         fontData.getHeight(),
                         fontData.getStyle() );
  }

  static void clear() {
    fontDataCache.clear();
  }

  private FontDataFactory() {
    // prevent instantiation
  }
}
