/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.internal.service.ServletLog;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;


public class FontUtil {

  public static final double LINE_HEIGHT_FACTOR = 1.4;
  private static final Map<Integer, Integer> VERTICAL_OFFSET_MAP = new HashMap<>();
  static {
    VERTICAL_OFFSET_MAP.put( Integer.valueOf(7), Integer.valueOf(2) );
    VERTICAL_OFFSET_MAP.put( Integer.valueOf(8), Integer.valueOf(2) );
    VERTICAL_OFFSET_MAP.put( Integer.valueOf(9), Integer.valueOf(2) );
    VERTICAL_OFFSET_MAP.put( Integer.valueOf(10), Integer.valueOf(4) );
    VERTICAL_OFFSET_MAP.put( Integer.valueOf(11), Integer.valueOf(4) );
    VERTICAL_OFFSET_MAP.put( Integer.valueOf(12), Integer.valueOf(6) );
    VERTICAL_OFFSET_MAP.put( Integer.valueOf(13), Integer.valueOf(6) );
    VERTICAL_OFFSET_MAP.put( Integer.valueOf(14), Integer.valueOf(6) );
    VERTICAL_OFFSET_MAP.put( Integer.valueOf(15), Integer.valueOf(9) );
  }

  public static FontData getData( Font font ) {
    return font.getFontData()[ 0 ];
  }

  /**
   * + * returns the vertical offset needed for a given character height to simulate Windows
   * behavior in + * RAP + * + * @param charHeight + * @return vertical offset in px (default 0 when
   * charHeight not in between 7 and 15) +
   */
  public static int getVerticalOffset( int charHeight ) {
    Integer charHeightInteger = Integer.valueOf( charHeight );
    if( VERTICAL_OFFSET_MAP.containsKey( charHeightInteger ) ) {
      return VERTICAL_OFFSET_MAP.get( charHeightInteger ).intValue();
    }
    ServletLog.log( "charHeight must be between 7 and 15 for vertival offset estimation", null );
    return 0;
  }

  private FontUtil() {
    // prevent instance creation
  }
}
