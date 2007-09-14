/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.combokit;

import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.theme.IControlThemeAdapter;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Control;

public class ComboThemeAdapter implements IControlThemeAdapter {

  public int getBorderWidth( final Control control ) {
    return 2;
  }

  public Color getForeground( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    QxColor color = theme.getColor( "list.foreground" );
    return QxColor.createColor( color );
  }

  public Color getBackground( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    QxColor color = theme.getColor( "list.background" );
    return QxColor.createColor( color );
  }

  public Font getFont( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    QxFont font = theme.getFont( "widget.font" );
    return QxFont.createFont( font );
  }

  public Rectangle getPadding( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    QxBoxDimensions padding = theme.getBoxDimensions( "text.SINGLE.padding" );
    return QxBoxDimensions.createRectangle( padding );
  }
}
