/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tabfolderkit;

import org.eclipse.rap.rwt.internal.theme.CssBoxDimensions;
import org.eclipse.rap.rwt.internal.theme.CssType;
import org.eclipse.rap.rwt.internal.theme.SimpleSelector;
import org.eclipse.rap.rwt.internal.theme.ThemeUtil;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;
import org.eclipse.swt.widgets.TabFolder;


public class TabFolderThemeAdapter extends ControlThemeAdapterImpl {

  public int getContentContainerBorderWidth( TabFolder folder ) {
    return getCssBorderWidth( "TabFolder-ContentContainer", "border", folder );
  }

  public Rectangle getItemPadding( boolean selected ) {
    SimpleSelector selector = selected ? SimpleSelector.SELECTED : SimpleSelector.DEFAULT;
    CssType cssValue = ThemeUtil.getCssValue( "TabItem", "padding", selector );
    return CssBoxDimensions.createRectangle( ( CssBoxDimensions )cssValue );
  }

  public Rectangle getItemMargin( boolean selected ) {
    SimpleSelector selector = selected ? SimpleSelector.SELECTED : SimpleSelector.DEFAULT;
    CssType cssValue = ThemeUtil.getCssValue( "TabItem", "margin", selector );
    return CssBoxDimensions.createRectangle( ( CssBoxDimensions )cssValue );
  }

}
