/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

public interface ITreeAdapter {

  void setScrollLeft( final int left );
  int getScrollLeft();

  void setTopItemIndex( final int topItemIndex );
  int getTopItemIndex();

  boolean hasHScrollBar();
  boolean hasVScrollBar();

  boolean isCached( final TreeItem item );
  void checkAllData( Tree tree );
  Point getItemImageSize( final int index );
  int getCellLeft( final int index );
  int getCellWidth( final int index );
  int getTextOffset( final int index );
  int getTextMaxWidth( final int index );
  int getCheckWidth();
  int getImageOffset( final int index );
  int getIndentionWidth();
  int getCheckLeft();
  Rectangle getTextMargin();
  int getColumnLeft( TreeColumn column );
}