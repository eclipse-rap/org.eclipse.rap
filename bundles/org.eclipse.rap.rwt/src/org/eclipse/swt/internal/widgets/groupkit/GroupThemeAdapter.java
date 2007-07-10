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

package org.eclipse.swt.internal.widgets.groupkit;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.theme.*;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Control;

public class GroupThemeAdapter extends ControlThemeAdapter
  implements IGroupThemeAdapter
{

  public Font getFont( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    QxFont font = theme.getFont( "group.label.font" );
    return QxFont.createFont( font );
  }

  public Rectangle getTrimmingSize() {
    Theme theme = ThemeUtil.getTheme();
    QxBoxDimensions margin = theme.getBoxDimensions( "group.margin" );
    QxBoxDimensions padding = theme.getBoxDimensions( "group.padding" );
    QxBorder frame = theme.getBorder( "group.frame.border" );
    int left = margin.left + frame.width + padding.left;
    int top = margin.top + frame.width + padding.top;
    int right = margin.right + frame.width + padding.right;
    int bottom = margin.bottom + frame.width + padding.bottom;
    return new Rectangle( left, top, left + right, top + bottom );
  }
}
