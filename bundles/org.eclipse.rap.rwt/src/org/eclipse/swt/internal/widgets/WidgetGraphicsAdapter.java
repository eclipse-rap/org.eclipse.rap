/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;

public final class WidgetGraphicsAdapter implements IWidgetGraphicsAdapter {

  private WidgetGraphicsData data;

  public Color[] getBackgroundGradientColors() {
    Color[] result = null;
    if( data != null ) {
      if( data.backgroundGradientColors != null ) {
        result = ( Color[] )data.backgroundGradientColors.clone();
      }
    }
    return result;
  }

  public int[] getBackgroundGradientPercents() {
    int[] result = null;
    if( data != null ) {
      if( data.backgroundGradientPercents != null ) {
        result = ( int[] )data.backgroundGradientPercents.clone();
      }
    }
    return result;
  }

  public void setBackgroundGradient( final Color[] gradientColors,
                                     final int[] percents )
  {
    if( gradientColors != null && percents != null ) {
      if( gradientColors.length != percents.length ) {
        SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
      for( int i = 0; i < gradientColors.length; i++ ) {
        if( gradientColors[ i ] == null ) {
          SWT.error( SWT.ERROR_INVALID_ARGUMENT );
        }
      }
    }
    if( data == null ) {
      data = new WidgetGraphicsData();
    }
    data.backgroundGradientColors = null;
    if( gradientColors != null ) {
      data.backgroundGradientColors = ( Color[] )gradientColors.clone();
    }
    data.backgroundGradientPercents = null;
    if( percents != null ) {
      data.backgroundGradientPercents = ( int[] )percents.clone();
    }
  }

  public int getRoundedBorderWidth() {
    int result = 0;
    if( data != null ) {
      result = data.roundedBorderWidth;
    }
    return result;
  }

  public Color getRoundedBorderColor() {
    Color result = null;
    if( data != null ) {
      result = data.roundedBorderColor;
    }
    return result;
  }

  public Rectangle getRoundedBorderRadius() {
    Rectangle result = null;
    if( data != null ) {
      result = data.roundedBorderRadius;
    }
    return result;
  }

  public void setRoundedBorder( final int width,
                                final Color color,
                                final int topLeftRadius,
                                final int topRightRadius,
                                final int bottomRightRadius,
                                final int bottomLeftRadius ) {
    if( data == null ) {
      data = new WidgetGraphicsData();
    }
    data.roundedBorderWidth = width;
    data.roundedBorderColor = color;
    data.roundedBorderRadius = new Rectangle( topLeftRadius,
                                              topRightRadius,
                                              bottomRightRadius,
                                              bottomLeftRadius );
  }
}
