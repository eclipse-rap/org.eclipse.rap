/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import org.eclipse.swt.graphics.FontData;

class MeasurementItem {

  private final int wrapWidth;
  private final FontData fontData;
  private final String string;

  MeasurementItem( String textToMeasure, FontData fontData, int wrapWidth ) {
    this.wrapWidth = wrapWidth;
    this.fontData = fontData;
    this.string = textToMeasure;
  }

  FontData getFontData() {
    return fontData;
  }

  String getTextToMeasure() {
    return string;
  }

  int getWrapWidth() {
    return wrapWidth;
  }

  // TODO [fappel]: add tests and rewrite generated code
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( fontData == null ) ? 0 : fontData.hashCode() );
    result = prime * result + ( ( string == null ) ? 0 : string.hashCode() );
    result = prime * result + wrapWidth;
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
    MeasurementItem other = ( MeasurementItem )obj;
    if( fontData == null ) {
      if( other.fontData != null ) {
        return false;
      }
    } else if( !fontData.equals( other.fontData ) ) {
      return false;
    }
    if( string == null ) {
      if( other.string != null ) {
        return false;
      }
    } else if( !string.equals( other.string ) ) {
      return false;
    }
    if( wrapWidth != other.wrapWidth ) {
      return false;
    }
    return true;
  }
}