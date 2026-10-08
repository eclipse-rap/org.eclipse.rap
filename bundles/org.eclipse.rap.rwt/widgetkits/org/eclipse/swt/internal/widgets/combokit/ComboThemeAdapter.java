/*******************************************************************************
 * Copyright (c) 2007, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.combokit;

import org.eclipse.rap.rwt.theme.BoxDimensions;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;
import org.eclipse.swt.widgets.Control;


public class ComboThemeAdapter extends ControlThemeAdapterImpl {

  public BoxDimensions getFieldPadding( Control control ) {
    return getCssBoxDimensions( "Combo-Field", "padding", control ).dimensions;
  }

  public BoxDimensions getListItemPadding( Control control ) {
    return getCssBoxDimensions( "Combo-List-Item", "padding", control ).dimensions;
  }

  public int getButtonWidth( Control control ) {
    return getCssDimension( "Combo-Button", "width", control );
  }

}
