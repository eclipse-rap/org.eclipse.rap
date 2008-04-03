/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Widget;


public class ThemeAdapterUtil {

  public static Color getColor( final Widget widget, final String key ) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( widget );
    QxColor color = theme.getColor( key, variant );
    return QxColor.createColor( color );
  }

  public static Font getFont( final Widget widget, final String key ) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( widget );
    QxFont font = theme.getFont( key, variant );
    return QxFont.createFont( font );
  }

  public static int getBorderWidth( final Widget widget, final String key ) {
    // TODO [rst] Ensure borders always have the same size for all four edges
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( widget );
    QxBorder border = theme.getBorder( key, variant );
    return border.width;
  }

  public static int getDimension( final Widget widget, final String key ) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( widget );
    QxDimension dim = theme.getDimension( key, variant );
    return dim.value;
  }

  public static Rectangle getBoxDimensions( final Widget widget,
                                            final String key )
  {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( widget );
    QxBoxDimensions boxdim = theme.getBoxDimensions( key, variant );
    return QxBoxDimensions.createRectangle( boxdim );
  }
}
