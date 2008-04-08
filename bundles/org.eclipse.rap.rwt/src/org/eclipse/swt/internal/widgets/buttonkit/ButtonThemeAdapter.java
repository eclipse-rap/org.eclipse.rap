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

import org.eclipse.rwt.internal.theme.ThemeAdapterUtil;
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
    String key;
    if( ( control.getStyle() & SWT.BORDER ) != 0 ) {
      key = "button.BORDER.border";
    } else if( ( control.getStyle() & SWT.FLAT ) != 0 ) {
      key = "button.FLAT.border";
    } else {
      key = "button.border";
    }
    return ThemeAdapterUtil.getBorderWidth( control, key );
  }

  public Point getSize( final Button button ) {
    int width = 0, height = 0;
    Rectangle padding
      = ThemeAdapterUtil.getBoxDimensions( button, "button.padding" );
    int spacing = ThemeAdapterUtil.getDimension( button, "button.spacing" );
    int style = button.getStyle();
    Image image = button.getImage();
    String text = button.getText();
    if( image != null ) {
      Rectangle bounds = image.getBounds();
      width = bounds.width;
      height = bounds.height;
      if( text.length() > 0 ) {
        width += spacing;
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
      width += padding.width;
      height += padding.height;
    }
    return new Point( width, height );
  }

  public Color getForeground( final Control control ) {
    int style = control.getStyle();
    Button button = ( Button )control;
    String key;
    if( ( style & ( SWT.CHECK | SWT.RADIO ) ) != 0 ) {
      key = "button.CHECK.foreground";
    } else if( ( style & ( SWT.FLAT & SWT.TOGGLE ) ) != 1
               && button.getSelection() )
    {
      key = "button.FLAT.pressed.foreground";
    } else {
      key = "button.foreground";
    }
    return ThemeAdapterUtil.getColor( control, key );
  }

  public Color getBackground( final Control control ) {
    int style = control.getStyle();
    Button button = ( Button )control;
    String key;
    if( ( control.getStyle() & ( SWT.CHECK | SWT.RADIO ) ) != 0 ) {
      key = "button.CHECK.background";
    } else if( ( style & ( SWT.FLAT & SWT.TOGGLE ) ) != 1
               && button.getSelection() )
    {
      key = "button.FLAT.pressed.background";
    } else {
      key = "button.background";
    }
    return ThemeAdapterUtil.getColor( control, key );
  }

  public Font getFont( final Control control ) {
    String key;
    if( ( control.getStyle() & ( SWT.PUSH | SWT.TOGGLE ) ) != 0 ) {
      key = "button.font";
    } else {
      key = "widget.font";
    }
    return ThemeAdapterUtil.getFont( control, key );
  }
}
