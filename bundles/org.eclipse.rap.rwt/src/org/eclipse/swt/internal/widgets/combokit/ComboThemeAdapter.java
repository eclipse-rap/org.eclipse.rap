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

package org.eclipse.swt.internal.widgets.combokit;

import org.eclipse.rwt.internal.theme.ThemeAdapterUtil;
import org.eclipse.rwt.theme.IControlThemeAdapter;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Control;


public final class ComboThemeAdapter implements IControlThemeAdapter {

  public int getBorderWidth( final Control control ) {
    return 2;
  }

  public Color getForeground( final Control control ) {
    return ThemeAdapterUtil.getColor( control, "list.foreground" );
  }

  public Color getBackground( final Control control ) {
    return ThemeAdapterUtil.getColor( control, "list.background" );
  }

  public Font getFont( final Control control ) {
    return ThemeAdapterUtil.getFont( control, "widget.font" );
  }

  public Rectangle getPadding( final Control control ) {
    return ThemeAdapterUtil.getBoxDimensions( control, "text.SINGLE.padding" );
  }
}
