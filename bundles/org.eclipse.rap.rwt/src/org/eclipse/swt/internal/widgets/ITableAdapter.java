/*******************************************************************************
 * Copyright (c) 2007, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public interface ITableAdapter {

  int getCheckWidthWithMargin();
  int getCheckWidth();
  int getCheckLeft();
  int getItemImageWidth( int columnIndex );

  int getFocusIndex();
  void setFocusIndex( int focusIndex );

  int getColumnLeftOffset( int columnIndex );
  int getLeftOffset();
  void setLeftOffset( int leftOffset );

  void checkData();
  void checkData( int index );

  int getColumnLeft( TableColumn column );
  int getDefaultColumnWidth();

  boolean isItemVirtual( int index );
  TableItem[] getCachedItems();
  TableItem[] getCreatedItems();

  TableItem getMeasureItem();

  int getFixedColumns();
  boolean isFixedColumn( TableColumn column );
}