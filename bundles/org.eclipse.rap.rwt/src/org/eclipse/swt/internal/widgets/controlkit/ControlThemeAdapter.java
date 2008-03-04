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

package org.eclipse.swt.internal.widgets.controlkit;

import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.rwt.theme.IControlThemeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;


public class ControlThemeAdapter implements IControlThemeAdapter {

  public int getBorderWidth( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( control );
    QxBorder result;
    if( ( control.getStyle() & SWT.BORDER ) != 0 ) {
      result = theme.getBorder( "control.BORDER.border", variant );
    } else {
      result = theme.getBorder( "control.border", variant );
    }
    return result.width;
  }

  public Color getForeground( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( control );
    QxColor color = theme.getColor( "widget.foreground", variant );
    return QxColor.createColor( color );
  }

  public Color getBackground( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( control );
    QxColor color = theme.getColor( "widget.background", variant );
    return QxColor.createColor( color );
  }

  public Font getFont( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( control );
    QxFont font = theme.getFont( "widget.font", variant );
    return QxFont.createFont( font );
  }
}
