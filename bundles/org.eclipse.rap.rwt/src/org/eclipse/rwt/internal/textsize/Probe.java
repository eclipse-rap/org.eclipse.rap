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
package org.eclipse.rwt.internal.textsize;

import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.swt.graphics.FontData;

class Probe {
  final static String DEFAULT_PROBE_STRING;
  static {
    StringBuffer result = new StringBuffer();
    for( int i = 33; i < 122; i++ ) {
      if( i != 34 && i != 39 ) {
        result.append( ( char ) i );
      }
    }
    DEFAULT_PROBE_STRING = result.toString();
  }

  private final String text;
  private final FontData fontData;

  Probe( FontData fontData ) {
    this( DEFAULT_PROBE_STRING, fontData );
  }
  
  Probe( String text, FontData fontData ) {
    ParamCheck.notNull( text, "text" );
    ParamCheck.notNull( fontData, "fontData" );
    this.text = text;
    this.fontData = fontData;
  }

  FontData getFontData() {
    return fontData;
  }

  String getText() {
    return text;
  }

  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + fontData.hashCode();
    result = prime * result + text.hashCode();
    return result;
  }

  public boolean equals( Object obj ) {
    boolean result = false;
    if( obj != null && getClass() == obj.getClass() ) {
      if( this == obj ) {
        result = true;
      } else {
        Probe other = ( Probe )obj;
        result =    fontData.equals( other.fontData )
                 && text.equals( other.text );
      }
    }
    return result;
  }
}