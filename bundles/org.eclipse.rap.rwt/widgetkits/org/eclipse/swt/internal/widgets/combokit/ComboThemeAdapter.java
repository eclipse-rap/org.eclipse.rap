/*******************************************************************************
 * Copyright (c) 2007, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.combokit;

import org.eclipse.rap.rwt.internal.theme.CssBoxDimensions;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;
import org.eclipse.swt.widgets.Control;


public class ComboThemeAdapter extends ControlThemeAdapterImpl {

  public CssBoxDimensions getFieldPadding( Control control ) {
    return getCssBoxDimensions( "Combo-Field", "padding", control );
  }

  public CssBoxDimensions getListItemPadding( Control control ) {
    return getCssBoxDimensions( "Combo-List-Item", "padding", control );
  }

  public int getButtonWidth( Control control ) {
    return getCssDimension( "Combo-Button", "width", control );
  }

}
