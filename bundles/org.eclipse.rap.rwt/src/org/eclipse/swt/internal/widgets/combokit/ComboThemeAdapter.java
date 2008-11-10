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

package org.eclipse.swt.internal.widgets.combokit;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;


public final class ComboThemeAdapter extends ControlThemeAdapter {

  public int getBorderWidth( final Control control ) {
    return 2;
  }

  public Color getForeground( final Combo combo ) {
    return getCssColor( "List", "color", combo );
  }

  public Color getBackground( final Combo combo ) {
    return getCssColor( "List", "background-color", combo );
  }

  public Rectangle getPadding( final Combo combo ) {
    Rectangle result = getCssBoxDimensions( "Text", "padding", combo );
    // TODO [if] Move to fragment. These two pixels are hard-coded in qooxdoo
    // TextField.js to emulate IE hard-coded margin.
    result.y += 1;
    result.height += 2;
    return result;
  }
}
