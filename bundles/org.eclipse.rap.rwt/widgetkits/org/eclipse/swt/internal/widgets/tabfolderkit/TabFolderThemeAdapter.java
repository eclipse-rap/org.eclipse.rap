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

import static org.eclipse.rap.rwt.internal.theme.ThemeUtil.getCssValue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.rwt.internal.theme.CssBorder;
import org.eclipse.rap.rwt.internal.theme.CssBoxDimensions;
import org.eclipse.rap.rwt.internal.theme.CssType;
import org.eclipse.rap.rwt.internal.theme.SimpleSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;


public class TabFolderThemeAdapter extends ControlThemeAdapterImpl {

  public Rectangle getContentContainerBorder( TabFolder folder ) {
    return getCssBorder( "TabFolder-ContentContainer", folder );
  }

  public Rectangle getItemBorder( TabItem item ) {
    SimpleSelector selector = createSelector( item );
    int top = getBorderEdgeWidth( "border-top", selector, item );
    int right = getBorderEdgeWidth( "border-right", selector, item );
    int bottom = getBorderEdgeWidth( "border-bottom", selector, item );
    int left = getBorderEdgeWidth( "border-left", selector, item );
    return new Rectangle( left, top, left + right, top + bottom );
  }

  public Rectangle getItemPadding( TabItem item ) {
    CssType cssValue = getCssValue( "TabItem", "padding", createSelector( item ) );
    return CssBoxDimensions.createRectangle( ( CssBoxDimensions )cssValue );
  }

  public Rectangle getItemMargin( TabItem item ) {
    CssType cssValue = getCssValue( "TabItem", "margin", createSelector( item ) );
    return CssBoxDimensions.createRectangle( ( CssBoxDimensions )cssValue );
  }

  private static int getBorderEdgeWidth( String edge, SimpleSelector selector, TabItem item ) {
    CssBorder borderEdge = ( CssBorder )getCssValue( "TabItem", edge, selector, item );
    return borderEdge.width;
  }

  private static SimpleSelector createSelector( TabItem item ) {
    List<String> constraints = new ArrayList<String>();
    if( isItemOnBottom( item ) ) {
      constraints.add( ":bottom" );
    }
    if( isItemSelected( item ) ) {
      constraints.add( ":selected" );
    }
    return new SimpleSelector( constraints.toArray( new String[ 0 ] ) );
  }

  private static boolean isItemOnBottom( TabItem item ) {
    return ( item.getParent().getStyle() & SWT.BOTTOM ) != 0;
  }

  private static boolean isItemSelected( TabItem item ) {
    TabFolder folder = item.getParent();
    return folder.indexOf( item ) == folder.getSelectionIndex();
  }

}
