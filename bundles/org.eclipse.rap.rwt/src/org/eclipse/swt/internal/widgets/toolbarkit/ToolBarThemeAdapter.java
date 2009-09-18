/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.toolbarkit;

import org.eclipse.rwt.internal.theme.WidgetMatcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Control;


public class ToolBarThemeAdapter extends ControlThemeAdapter {

  protected void configureMatcher( WidgetMatcher matcher ) {
    super.configureMatcher( matcher );
    matcher.addStyle( "FLAT", SWT.FLAT );
  }

  public int getItemBorderWidth( final Control control ) {
    return getCssBorderWidth( "ToolItem", "border", control );
  }

  public Rectangle getItemPadding( final Control control ) {
    return getCssBoxDimensions( "ToolItem", "padding", control );
  }
  
  public Rectangle getToolBarPadding( final Control control ) {
    return getCssBoxDimensions( "ToolBar", "padding", control );
  }
  
  public int getToolBarSpacing( final Control control ) {
    return getCssDimension( "ToolBar", "spacing", control );
  }
  
  public int getItemSpacing( final Control control ) {
    return getCssDimension( "ToolItem", "spacing", control );
  }  
  
  public int getSeparatorWidth( final Control control ) {
    return getCssDimension( "ToolItem-Separator", "width", control );
  }  
  
  public Point getDropDownImageDimension( final Control control ) {
    return getCssImageDimension( 
      "ToolItem-DropDownIcon", "background-image", control );
  }
  
}
