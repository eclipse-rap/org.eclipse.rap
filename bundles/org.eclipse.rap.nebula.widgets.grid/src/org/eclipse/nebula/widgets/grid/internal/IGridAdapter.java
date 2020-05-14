/*******************************************************************************
 * Copyright (c) 2012, 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid.internal;

import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;


public interface IGridAdapter {

  void invalidateTopIndex();
  int getIndentationWidth();

  int getCellLeft( int index );
  int getCellWidth( int index );
  int getCheckBoxOffset( int index );
  int getCheckBoxWidth( int index );
  int getImageOffset( int index );
  int getImageWidth( int index );
  int getTextOffset( int index );
  int getTextWidth( int index );

  int getRowHeaderImageOffset();
  int getRowHeaderImageWidth();
  int getRowHeaderTextOffset();
  int getRowHeaderTextWidth();

  int getItemIndex( GridItem item );

  void doRedraw();

  int getFixedColumns();
  boolean isFixedColumn( GridColumn column );

  int getTreeColumn();
  int getSelectionType();
  GridColumn getRowHeadersColumn();

}
