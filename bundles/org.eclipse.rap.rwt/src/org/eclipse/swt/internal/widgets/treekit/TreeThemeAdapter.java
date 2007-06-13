/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.treekit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.theme.*;
import org.eclipse.swt.widgets.Control;

public class TreeThemeAdapter implements ITreeThemeAdapter {
  
  public int getBorderWidth( final Control control ) {
    return ( control.getStyle() & SWT.BORDER ) != 0 ? 2 : 0;
  }
  
  public QxColor getForeground( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    return theme.getColor( "list.foreground" );
  }
  
  public QxColor getBackground( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    return theme.getColor( "list.background" );
  }

  public QxFont getFont( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    return theme.getFont( "widget.font" );
  }
}
