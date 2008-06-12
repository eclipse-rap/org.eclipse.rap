/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.groupkit;

import org.eclipse.rwt.internal.theme.ThemeAdapterUtil;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Control;


public final class GroupThemeAdapter extends ControlThemeAdapter {

  public Font getFont( final Control control ) {
    return ThemeAdapterUtil.getFont( control, "group.label.font" );
  }

  public Color getBackground( final Control control ) {
    return ThemeAdapterUtil.getColor( control, "group.background" );
  }

  public Rectangle getTrimmingSize( final Control control ) {
    Rectangle margin = ThemeAdapterUtil.getBoxDimensions( control, "group.margin" );
    Rectangle padding = ThemeAdapterUtil.getBoxDimensions( control, "group.padding" );
    int frameWidth = ThemeAdapterUtil.getBorderWidth( control, "group.frame.border" );
    int left = margin.x + padding.x + frameWidth;
    int top = margin.y + padding.y + frameWidth;
    Font font = control.getFont();
    top = Math.max( top, TextSizeDetermination.getCharHeight( font ) );
    int width = margin.width + padding.width + frameWidth * 2;
    int height = margin.height + padding.height + frameWidth * 2;
    return new Rectangle( left, top, width, height );
  }
}
