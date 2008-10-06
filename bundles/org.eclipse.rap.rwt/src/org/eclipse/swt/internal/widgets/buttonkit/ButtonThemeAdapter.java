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

package org.eclipse.swt.internal.widgets.buttonkit;

import org.eclipse.rwt.internal.theme.ThemeAdapterUtil;
import org.eclipse.rwt.theme.IControlThemeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;


public final class ButtonThemeAdapter implements IControlThemeAdapter {

  private static final Point CHECK_SIZE = new Point( 13, 13 );
  private static final int CHECK_SPACING = 4;

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

  public int getBorderWidth( final Control control ) {
    String key;
    if( ( control.getStyle() & ( SWT.PUSH | SWT.TOGGLE ) ) != 0 ) {
      if( ( control.getStyle() & SWT.BORDER ) != 0 ) {
        key = "button.BORDER.border";
      } else if( ( control.getStyle() & SWT.FLAT ) != 0 ) {
        key = "button.FLAT.border";
      } else {
        key = "button.border";
      }
    } else {
      if( ( control.getStyle() & SWT.BORDER ) != 0 ) {
        key = "control.BORDER.border";
      } else {
        key = "control.border";        
      }
    }
    return ThemeAdapterUtil.getBorderWidth( control, key );
  }

  public Rectangle getPadding( final Button button ) {
    Rectangle result
      = ThemeAdapterUtil.getBoxDimensions( button, "button.padding" );
    // TODO [rst] Additional padding for PUSH and TOGGLE buttons, remove when
    //            CSS theming is in place
    if( ( button.getStyle() & ( SWT.PUSH | SWT.TOGGLE ) ) != 0 ) {
      result.x += 1;
      result.y += 1;
      result.width += 2;
      result.height += 2;
    }
    return result;
  }

  public int getSpacing( final Button button ) {
    return ThemeAdapterUtil.getDimension( button, "button.spacing" );
  }

  public Point getCheckSize() {
    return CHECK_SIZE;
  }

  public int getCheckSpacing() {
    return CHECK_SPACING;
  }
}
