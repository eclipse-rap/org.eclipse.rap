/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.treekit;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Control;


public final class TreeThemeAdapter extends ControlThemeAdapter {

  public int getCheckBoxWidth( final Control control ) {
    return getCssDimension( "Tree-Checkbox", "width", control );
  }

  public int getHeaderBorderBottomWidth( final Control control ) {
    return getCssBorderWidth( "TreeColumn", "border-bottom", control );
  }

  public Rectangle getHeaderPadding( final Control control ) {
    return getCssBoxDimensions( "TreeColumn", "padding", control );
  }
}
