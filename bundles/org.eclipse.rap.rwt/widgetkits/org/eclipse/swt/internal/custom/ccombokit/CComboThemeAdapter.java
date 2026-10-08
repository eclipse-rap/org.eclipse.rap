/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.custom.ccombokit;

import org.eclipse.rap.rwt.theme.BoxDimensions;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;
import org.eclipse.swt.widgets.Control;


public class CComboThemeAdapter extends ControlThemeAdapterImpl {

  public BoxDimensions getFieldPadding( Control control ) {
    return getCssBoxDimensions( "CCombo-Field", "padding", control ).dimensions;
  }

  public BoxDimensions getListItemPadding( Control control ) {
    return getCssBoxDimensions( "CCombo-List-Item", "padding", control ).dimensions;
  }

  public int getButtonWidth( Control control ) {
    return getCssDimension( "CCombo-Button", "width", control );
  }

}
