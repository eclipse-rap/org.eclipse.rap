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

import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.internal.SerializableCompatibility;

class MeasurementItem implements SerializableCompatibility {
  private static final long serialVersionUID = 1L;

  private final int wrapWidth;
  private final FontData fontData;
  private final String string;

  MeasurementItem( String textToMeasure, FontData fontData, int wrapWidth ) {
    ParamCheck.notNull( textToMeasure, "textToMeasure" );
    ParamCheck.notNull( fontData, "fontData" );
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

  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + fontData.hashCode();
    result = prime * result + string.hashCode();
    result = prime * result + wrapWidth;
    return result;
  }
  
  public boolean equals( Object object ) {
    boolean result = false;
    if( object != null && getClass() == object.getClass() ) {
      if( this == object ) {
        result = true;
      } else {
        MeasurementItem other = ( MeasurementItem )object;
        result =    fontData.equals( other.fontData )
                 && string.equals( other.string )
                 && wrapWidth == other.wrapWidth;
      }
    }
    return result;
  }
}