/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.toolbarkit;

import org.eclipse.rap.rwt.internal.theme.Size;
import org.eclipse.rap.rwt.internal.theme.WidgetMatcher;
import org.eclipse.rap.rwt.theme.BoxDimensions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;
import org.eclipse.swt.widgets.Control;


public class ToolBarThemeAdapter extends ControlThemeAdapterImpl {

  @Override
  protected void configureMatcher( WidgetMatcher matcher ) {
    super.configureMatcher( matcher );
    matcher.addStyle( "FLAT", SWT.FLAT );
  }

  public BoxDimensions getItemBorder( Control control ) {
    return getCssBorder( "ToolItem", control );
  }

  public BoxDimensions getItemPadding( Control control ) {
    return getCssBoxDimensions( "ToolItem", "padding", control ).dimensions;
  }

  public BoxDimensions getToolBarPadding( Control control ) {
    return getCssBoxDimensions( "ToolBar", "padding", control ).dimensions;
  }

  public int getToolBarSpacing( Control control ) {
    return getCssDimension( "ToolBar", "spacing", control );
  }

  public int getItemSpacing( Control control ) {
    return getCssDimension( "ToolItem", "spacing", control );
  }

  public int getSeparatorWidth( Control control ) {
    return getCssDimension( "ToolItem-Separator", "width", control );
  }

  public Size getDropDownImageSize( Control control ) {
    return getCssImageSize( "ToolItem-DropDownIcon", "background-image", control );
  }

}
