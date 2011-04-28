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
    this.text = text;
    this.fontData = fontData;
  }

  FontData getFontData() {
    return fontData;
  }

  String getText() {
    return text;
  }

  // TODO [fappel]: add tests and rewrite generated code
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( fontData == null ) ? 0 : fontData.hashCode() );
    result = prime * result + ( ( text == null ) ? 0 : text.hashCode() );
    return result;
  }

  // TODO [fappel]: add tests and rewrite generated code
  public boolean equals( Object obj ) {
    if( this == obj ) {
      return true;
    }
    if( obj == null ) {
      return false;
    }
    if( getClass() != obj.getClass() ) {
      return false;
    }
    Probe other = ( Probe )obj;
    if( fontData == null ) {
      if( other.fontData != null ) {
        return false;
      }
    } else if( !fontData.equals( other.fontData ) ) {
      return false;
    }
    if( text == null ) {
      if( other.text != null ) {
        return false;
      }
    } else if( !text.equals( other.text ) ) {
      return false;
    }
    return true;
  }
}