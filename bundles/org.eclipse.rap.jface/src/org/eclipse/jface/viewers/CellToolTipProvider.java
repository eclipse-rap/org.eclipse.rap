/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.jface.viewers;

import org.eclipse.swt.internal.widgets.ICellToolTipAdapter;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.internal.widgets.ICellToolTipProvider;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

/**
 * Support for table/tree tool-tips in RAP
 */
final class CellToolTipProvider implements ICellToolTipProvider {
  private ColumnViewer viewer;

  CellToolTipProvider( final ColumnViewer viewer ) {
    this.viewer = viewer;
  }

  static void attach( final ColumnViewer viewer,
                      final CellLabelProvider labelProvider )
  {
    ICellToolTipAdapter adapter = getAdapter( viewer );
    if( labelProvider != null ) {
      CellToolTipProvider provider = new CellToolTipProvider( viewer );
      adapter.setCellToolTipProvider( provider );
    } else {
      adapter.setCellToolTipProvider( null );
    }
  }

  private static ICellToolTipAdapter getAdapter( final ColumnViewer viewer ) {
    ICellToolTipAdapter result = null;
    if( viewer instanceof TableViewer ) {
      Table table = ( ( TableViewer )viewer ).getTable();
      result = ( ICellToolTipAdapter )table.getAdapter( ITableAdapter.class );
    } else if( viewer instanceof TreeViewer ) {
      Tree tree = ( ( TreeViewer )viewer ).getTree();
      result = ( ICellToolTipAdapter )tree.getAdapter( ITreeAdapter.class );
    }
    return result;
  }

  public void getToolTipText( final Item item, final int columnIndex ) {
    Object element =  item.getData();
    ViewerColumn column = viewer.getViewerColumn( columnIndex );
    CellLabelProvider labelProvider = column.getLabelProvider();
    if( labelProvider != null ) {
      String text = labelProvider.getToolTipText( element );
      ICellToolTipAdapter adapter = getAdapter( viewer );
      adapter.setToolTipText( text );
    }
  }

}
