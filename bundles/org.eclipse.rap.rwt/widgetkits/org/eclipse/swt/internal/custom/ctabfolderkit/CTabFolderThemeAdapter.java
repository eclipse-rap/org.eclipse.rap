/*******************************************************************************
 * Copyright (c) 2008, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.custom.ctabfolderkit;

import org.eclipse.rap.rwt.internal.theme.CssBoxDimensions;
import org.eclipse.rap.rwt.internal.theme.CssColor;
import org.eclipse.rap.rwt.internal.theme.CssDimension;
import org.eclipse.rap.rwt.internal.theme.CssFont;
import org.eclipse.rap.rwt.internal.theme.CssType;
import org.eclipse.rap.rwt.internal.theme.SimpleSelector;
import org.eclipse.rap.rwt.internal.theme.ThemeUtil;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;


public class CTabFolderThemeAdapter extends ControlThemeAdapterImpl {

  public Color getBackground( CTabFolder folder ) {
    return getCssColor( "CTabItem", "background-color", folder );
  }

  public Color getForeground( CTabFolder folder ) {
    return getCssColor( "CTabItem", "color", folder );
  }

  public Color getSelectedBackground( CTabFolder folder ) {
    CssType cssValue = ThemeUtil.getCssValue( "CTabItem",
                                              "background-color",
                                              SimpleSelector.SELECTED );
    return CssColor.createColor( ( CssColor )cssValue );
  }

  public Color getSelectedForeground( CTabFolder folder ) {
    CssType cssValue = ThemeUtil.getCssValue( "CTabItem",
                                              "color",
                                              SimpleSelector.SELECTED );
    return CssColor.createColor( ( CssColor )cssValue );
  }

  public Rectangle getItemPadding( boolean selected ) {
    SimpleSelector selector = selected
                              ? SimpleSelector.SELECTED
                              : SimpleSelector.DEFAULT;
    CssType cssValue = ThemeUtil.getCssValue( "CTabItem", "padding", selector );
    return CssBoxDimensions.createRectangle( ( CssBoxDimensions )cssValue );
  }

  public int getItemSpacing( boolean selected ) {
    SimpleSelector selector = selected
                              ? SimpleSelector.SELECTED
                              : SimpleSelector.DEFAULT;
    CssType cssValue = ThemeUtil.getCssValue( "CTabItem", "spacing", selector );
    return ( ( CssDimension )cssValue ).value;
  }

  public Font getItemFont( boolean selected ) {
    SimpleSelector selector = selected
                              ? SimpleSelector.SELECTED
                              : SimpleSelector.DEFAULT;
    CssType cssValue = ThemeUtil.getCssValue( "CTabItem", "font", selector );
    return CssFont.createFont( ( CssFont )cssValue );
  }
}
