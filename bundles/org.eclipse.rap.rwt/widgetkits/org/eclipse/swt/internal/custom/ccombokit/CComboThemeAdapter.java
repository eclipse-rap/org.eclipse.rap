/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.custom.ccombokit;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;
import org.eclipse.swt.widgets.Control;


public final class CComboThemeAdapter extends ControlThemeAdapterImpl {

  public Rectangle getFieldPadding( Control control ) {
    return getCssBoxDimensions( "CCombo-Field", "padding", control );
  }

  public Rectangle getListItemPadding( Control control ) {
    return getCssBoxDimensions( "List-Item", "padding", control );
  }

  public int getButtonWidth( Control control ) {
    return getCssDimension( "CCombo-Button", "width", control );
  }

}
