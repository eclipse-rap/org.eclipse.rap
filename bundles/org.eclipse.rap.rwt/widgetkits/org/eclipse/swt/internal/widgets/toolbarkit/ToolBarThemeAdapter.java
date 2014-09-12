/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.toolbarkit;

import org.eclipse.rap.rwt.internal.theme.WidgetMatcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;
import org.eclipse.swt.widgets.Control;


public class ToolBarThemeAdapter extends ControlThemeAdapterImpl {

  @Override
  protected void configureMatcher( WidgetMatcher matcher ) {
    super.configureMatcher( matcher );
    matcher.addStyle( "FLAT", SWT.FLAT );
  }

  public Rectangle getItemBorder( Control control ) {
    return getCssBorder( "ToolItem", control );
  }

  public Rectangle getItemPadding( Control control ) {
    return getCssBoxDimensions( "ToolItem", "padding", control );
  }

  public Rectangle getToolBarPadding( Control control ) {
    return getCssBoxDimensions( "ToolBar", "padding", control );
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

  public Point getDropDownImageDimension( Control control ) {
    return getCssImageDimension( "ToolItem-DropDownIcon", "background-image", control );
  }

}
