/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.groupkit;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.Group;


public final class GroupThemeAdapter extends ControlThemeAdapter {

  public Rectangle getFramePadding( final Group group ) {
    return getCssBoxDimensions( "Group-Frame", "padding", group );
  }

  public Rectangle getFrameMargin( final Group group ) {
    return getCssBoxDimensions( "Group-Frame", "margin", group );
  }

  /**
   * Returns the size of the trimming of the given group control not including
   * the control's border size.
   */
  public Rectangle getTrimmingSize( final Group group ) {
    Rectangle margin = getFrameMargin( group );
    Rectangle padding = getFramePadding( group );
    int frameWidth
      = getCssBorderWidth( "Group-Frame", "border", group );
    int left = margin.x + padding.x + frameWidth;
    int top = margin.y + padding.y + frameWidth;
    Font font = group.getFont();
    top = Math.max( top, Graphics.getCharHeight( font ) );
    int width = margin.width + padding.width + frameWidth * 2;
    int height = margin.height + padding.height + frameWidth * 2;
    return new Rectangle( left, top, width, height );
  }

  public Rectangle getHeaderTrimmingSize( final Group group ) {
    Rectangle margin
      = getCssBoxDimensions( "Group-Label", "margin", group );
    Rectangle padding
      = getCssBoxDimensions( "Group-Label", "padding", group );
    int left = margin.x + padding.x;
    int top = margin.y + padding.y;
    int width = margin.width + padding.width;
    int height = margin.height + padding.height;
    return new Rectangle( left, top, width, height );
  }
}
