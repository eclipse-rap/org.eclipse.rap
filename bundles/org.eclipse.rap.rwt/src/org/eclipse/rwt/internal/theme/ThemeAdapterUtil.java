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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Widget;


public class ThemeAdapterUtil {

  public static Color getColor( Widget widget, String key) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( widget );
    QxColor color = theme.getColor( key, variant );
    return QxColor.createColor( color );
  }

  public static Font getFont( Widget widget, String key) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( widget );
    QxFont font = theme.getFont( key, variant );
    return QxFont.createFont( font );
  }
}
