/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.custom.ccombokit;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Control;


public final class CComboThemeAdapter extends ControlThemeAdapter {

  public Rectangle getPadding( final Control control ) {
    Rectangle result = getCssBoxDimensions( "Text", "padding", control );
    // TODO [if] Move to fragment. These two pixels are hard-coded in qooxdoo
    // TextField.js to emulate IE hard-coded margin.
    result.y += 1;
    result.height += 2;
    return result;
  }
  
  public int getButtonWidth( final Control control ) {
    return getCssDimension( "CCombo-Button", "width", control );
  }
}
