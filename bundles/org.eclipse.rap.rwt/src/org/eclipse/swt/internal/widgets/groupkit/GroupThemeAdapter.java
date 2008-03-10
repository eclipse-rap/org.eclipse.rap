/*******************************************************************************
 * Copyright (c) 2007-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.groupkit;

import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Control;


public final class GroupThemeAdapter extends ControlThemeAdapter {

  public Font getFont( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( control );
    QxFont font = theme.getFont( "group.label.font", variant );
    return QxFont.createFont( font );
  }

  public Color getBackground( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( control );
    QxColor color = theme.getColor( "group.background", variant );
    return QxColor.createColor( color );
  }

  public Rectangle getTrimmingSize( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( control );
    QxBoxDimensions margin = theme.getBoxDimensions( "group.margin", variant );
    QxBoxDimensions padding = theme.getBoxDimensions( "group.padding", variant );
    QxBorder frame = theme.getBorder( "group.frame.border", variant );
    int left = margin.left + frame.width + padding.left;
    int top = margin.top + frame.width + padding.top;
    Font font = control.getFont();
    top = Math.max( top, TextSizeDetermination.getCharHeight( font ) );
    int right = margin.right + frame.width + padding.right;
    int bottom = margin.bottom + frame.width + padding.bottom;
    return new Rectangle( left, top, left + right, top + bottom );
  }
}
