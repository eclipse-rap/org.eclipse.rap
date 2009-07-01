/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.jface.viewers;

import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.internal.widgets.ICellToolTipProvider;
import org.eclipse.swt.widgets.Table;

final class CellToolTipProvider implements ICellToolTipProvider {
  private TableViewer viewer;

  CellToolTipProvider( final TableViewer viewer ) {
    this.viewer = viewer;
  }

  static void attach( final TableViewer viewer,
                      final IBaseLabelProvider labelProvider )
  {
    TableViewer tableViewer = viewer;
    Table table = tableViewer.getTable();
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    if( labelProvider instanceof CellLabelProvider ) {
      CellToolTipProvider provider = new CellToolTipProvider( tableViewer );
      tableAdapter.setCellToolTipProvider( provider );
    } else {
      tableAdapter.setCellToolTipProvider( null );
    }
  }

  public void getToolTipText( final int itemIndex, final int columnIndex ) {
    Table table = viewer.getTable();
    Object element =  table.getItem( itemIndex ).getData();
    ViewerColumn column = viewer.getViewerColumn( columnIndex );
    CellLabelProvider labelProvider = column.getLabelProvider();
    String text = labelProvider.getToolTipText( element );
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    tableAdapter.setToolTipText( text );
  }

}
