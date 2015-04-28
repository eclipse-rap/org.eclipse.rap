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
package org.eclipse.swt.internal.widgets.groupkit;

import org.eclipse.rap.rwt.internal.textsize.TextSizeUtil;
import org.eclipse.rap.rwt.internal.theme.CssBoxDimensions;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;
import org.eclipse.swt.widgets.Group;


public class GroupThemeAdapter extends ControlThemeAdapterImpl {

  public CssBoxDimensions getFramePadding( Group group ) {
    return getCssBoxDimensions( "Group-Frame", "padding", group );
  }

  public CssBoxDimensions getFrameMargin( Group group ) {
    return getCssBoxDimensions( "Group-Frame", "margin", group );
  }

  /**
   * Returns the size of the trimming of the given group control not including
   * the control's border size.
   */
  public Rectangle getTrimmingSize( Group group ) {
    CssBoxDimensions margin = getFrameMargin( group );
    CssBoxDimensions padding = getFramePadding( group );
    Rectangle frameWidth = getCssBorder( "Group-Frame", group );
    int left = margin.left + padding.left + frameWidth.x;
    int top = margin.top + padding.top + frameWidth.y;
    top = Math.max( top, TextSizeUtil.getCharHeight( group.getFont() ) );
    int width = margin.getWidth() + padding.getWidth() + frameWidth.width;
    int height = margin.getHeight() + padding.getHeight() + frameWidth.height;
    return new Rectangle( left, top, width, height );
  }

  public Rectangle getHeaderTrimmingSize( Group group ) {
    CssBoxDimensions margin = getCssBoxDimensions( "Group-Label", "margin", group );
    CssBoxDimensions padding = getCssBoxDimensions( "Group-Label", "padding", group );
    int left = margin.left + padding.left;
    int top = margin.top + padding.top;
    int width = margin.getWidth() + padding.getWidth();
    int height = margin.getHeight() + padding.getHeight();
    return new Rectangle( left, top, width, height );
  }

}
