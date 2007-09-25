/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.theme;

import org.eclipse.rwt.internal.theme.IThemeAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;

/**
 * Theme adapter for {@link Control}s. The theme adapter provides a control
 * with information on characteristics of its representation which depend on the
 * current theme.
 */
public interface IControlThemeAdapter extends IThemeAdapter {

  /**
   * Returns the width of the specified control's border.
   * 
   * @param control the control whose border width is requested
   * @return the border width in px
   */
  public int getBorderWidth ( Control control );

  /**
   * Returns the default foreground color that the specified control will use to
   * draw if no user defined foreground color has been set using
   * {@link Control#setForeground(Color)}.
   * @param control the control whose foreground color is requested
   * 
   * @return the foreground color
   */
  public Color getForeground( Control control );


  /**
   * Returns the default background color that the specified control will use if
   * no user-defined background color has been set using
   * {@link Control#setBackground(Color)}.
   * 
   * @param control the control whose background color is requested
   * @return the background color
   */
  public Color getBackground( Control control );

  /**
   * Returns the default font that the specified control will use to paint
   * textual information when no user-defined font has been set using
   * {@link Control#setFont(Font)}.
   * 
   * @param control the control whose font is requested
   * @return the font
   */
  public Font getFont( Control control );
}
