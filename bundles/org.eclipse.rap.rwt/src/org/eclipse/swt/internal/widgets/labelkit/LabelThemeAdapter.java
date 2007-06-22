/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.labelkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.theme.*;
import org.eclipse.swt.internal.widgets.controlkit.IControlThemeAdapter;
import org.eclipse.swt.widgets.Control;


public class LabelThemeAdapter implements IControlThemeAdapter {

  public int getBorderWidth( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    QxBorder border;
    if( ( control.getStyle() & SWT.BORDER ) != 0 ) {
      border = theme.getBorder( "label.BORDER.border" );
    } else {
      border = theme.getBorder( "label.border" );
    }
    return border.width;
  }

  public QxColor getForeground( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    return theme.getColor( "widget.foreground" );
  }

  public QxColor getBackground( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    return theme.getColor( "widget.background" );
  }

  public QxFont getFont( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    return theme.getFont( "widget.font" );
  }
}
