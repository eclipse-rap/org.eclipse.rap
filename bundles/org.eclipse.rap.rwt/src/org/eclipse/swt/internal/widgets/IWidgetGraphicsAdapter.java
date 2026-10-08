/*******************************************************************************
 * Copyright (c) 2009, 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which
 * is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;


public interface IWidgetGraphicsAdapter {

  Color[] getBackgroundGradientColors();
  int[] getBackgroundGradientPercents();
  boolean isBackgroundGradientVertical();
  void setBackgroundGradient( Color[] gradientColors,
                              int[] percents,
                              boolean vertical );

  int getRoundedBorderWidth();
  Color getRoundedBorderColor();
  Rectangle getRoundedBorderRadius();
  void setRoundedBorder( int width,
                         Color color,
                         int topLeftRadius,
                         int topRightRadius,
                         int bottomRightRadius,
                         int bottomLeftRadius );

}
