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

package org.eclipse.swt.internal.widgets.buttonkit;

import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.rwt.theme.IControlThemeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;


public final class ButtonThemeAdapter implements IControlThemeAdapter {

  // Width of check boxes and radio buttons
  static final int CHECK_WIDTH = 13;

  // Height of check boxes and radio buttons
  static final int CHECK_HEIGHT = 13;

  public int getBorderWidth( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( control );
    QxBorder qxBorder;
    if( ( control.getStyle() & SWT.BORDER ) != 0 ) {
      qxBorder = theme.getBorder( "button.BORDER.border", variant );
    } else if( ( control.getStyle() & SWT.FLAT ) != 0 ) {
      qxBorder = theme.getBorder( "button.FLAT.border", variant );
    } else {
      qxBorder = theme.getBorder( "button.border", variant );
    }
    // TODO [rst] Ensure that borders have the same size for all four sides
    return qxBorder.width;
  }

  public Point getSize( final Button button ) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( button );
    int width = 0, height = 0;
    QxBoxDimensions padding = theme.getBoxDimensions( "button.padding", variant );
    QxDimension spacing = theme.getDimension( "button.spacing", variant );
    int style = button.getStyle();
    Image image = button.getImage();
    String text = button.getText();
    if( image != null ) {
      Rectangle bounds = image.getBounds();
      width = bounds.width;
      height = bounds.height;
      if( text.length() > 0 ) {
        width += spacing.value;
      }
    }
    if( text.length() > 0 ) {
      Point extent = TextSizeDetermination.stringExtent( button.getFont(), text );
      height = Math.max( height, extent.y );
      width += extent.x;
    }
    if( ( style & ( SWT.CHECK | SWT.RADIO ) ) != 0 ) {
      width += CHECK_WIDTH;
      height = Math.max( height, CHECK_HEIGHT + 3 );
      // TODO remove this adjustment
      width += 12;
      height += 4;
    }
    if( ( style & ( SWT.PUSH | SWT.TOGGLE ) ) != 0 ) {
      width += padding.left + padding.right;
      height += padding.top + padding.bottom;
    }
    return new Point( width, height );
  }

  public Color getForeground( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( control );
    QxColor color;
    if( ( control.getStyle() & ( SWT.CHECK | SWT.RADIO ) ) != 0 ) {
      color = theme.getColor( "button.CHECK.foreground", variant );
    } else {
      color = theme.getColor( "button.foreground", variant );
    }
    return QxColor.createColor( color );
  }

  public Color getBackground( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( control );
    QxColor color;
    if( ( control.getStyle() & ( SWT.CHECK | SWT.RADIO ) ) != 0 ) {
      color = theme.getColor( "button.CHECK.background", variant );
    } else {
      color = theme.getColor( "button.background", variant );
    }
    return QxColor.createColor( color );
  }

  public Font getFont( final Control control ) {
    Theme theme = ThemeUtil.getTheme();
    String variant = WidgetUtil.getVariant( control );
    QxFont font;
    if( ( control.getStyle() & ( SWT.PUSH | SWT.TOGGLE ) ) != 0 ) {
      font = theme.getFont( "button.font", variant );
    } else {
      font = theme.getFont( "widget.font", variant );
    }
    return QxFont.createFont( font );
  }
}
