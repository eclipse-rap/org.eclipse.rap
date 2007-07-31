/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.tablekit;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;


public final class TableLCAUtil {

  // Constants used by item metrics
  private static final Integer ZERO = new Integer( 0 );
  private static final String PROP_ITEM_METRICS = "itemMetrics";

  // Constants used by alignment
  private static final String PROP_ALIGNMENT = "alignment";
  private static final Integer DEFAULT_ALIGNMENT = new Integer( SWT.LEFT );

  ////////////////////////////
  // Column and Item alignment
  
  public static void preserveAlignment( final TableColumn column ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( column );
    adapter.preserve( PROP_ALIGNMENT, new Integer( column.getAlignment() ) );
  }
  
  public static boolean hasAlignmentChanged( final Table table ) {
    boolean result = false;
    TableColumn[] columns = table.getColumns();
    for( int i = 0; !result && i < columns.length; i++ ) {
      if( hasAlignmentChanged( columns[ i ] ) ) {
        result = true;
      }
    }
    return result;
  }
  
  public static boolean hasAlignmentChanged( final TableColumn column ) {
    return WidgetLCAUtil.hasChanged( column, 
                                     PROP_ALIGNMENT, 
                                     new Integer( column.getAlignment() ), 
                                     DEFAULT_ALIGNMENT );
  }
  
  ///////////////
  // Item metrics
  
  public static void preserveItemMetrics( final Table table ) {
    IWidgetAdapter adapter1 = WidgetUtil.getAdapter( table );
    adapter1.preserve( PROP_ITEM_METRICS, getItemMetrics( table ) );
  }
  
  public static boolean hasItemMetricsChanged( final Table table ) {
    ItemMetrics[] itemMetrics = getItemMetrics( table );
    return hasItemMetricsChanged( table, itemMetrics );
  }

  public static void writeItemMetrics( final Table table ) 
    throws IOException 
  {
    ItemMetrics[] itemMetrics = getItemMetrics( table );
    if( hasItemMetricsChanged( table, itemMetrics ) ) {
      JSWriter writer = JSWriter.getWriterFor( table );
      for( int i = 0; i < itemMetrics.length; i++ ) {
        Object[] args = new Object[] { 
          new Integer( i ), 
          itemMetrics[ i ].imageLeft, 
          itemMetrics[ i ].imageWidth,
          itemMetrics[ i ].textLeft,
          itemMetrics[ i ].textWidth
        };
        writer.set( "itemMetrics", args );
      }
    }
  }
  
  //////////////////
  // Helping methods

  private static boolean hasItemMetricsChanged( final Table table, 
                                                final ItemMetrics[] metrics  ) 
  {
    return WidgetLCAUtil.hasChanged( table, PROP_ITEM_METRICS, metrics );
  }

  private static ItemMetrics[] getItemMetrics( final Table table ) {
    int columnCount = Math.max( 1, table.getColumnCount() );
    ItemMetrics[] result = new ItemMetrics[ columnCount ];
    for( int i = 0; i < columnCount; i++ ) {
      result[ i ] = new ItemMetrics();
    }
    TableItem measureItem = getMeasureItem( table );
    if( measureItem != null ) {
      int checkWidth = getCheckWidth( table );
      for( int i = 0; i < columnCount; i++ ) {
        Rectangle bounds = measureItem.getBounds( i );
        Rectangle imageBounds = measureItem.getImageBounds( i );
        Rectangle textBounds = measureItem.getTextBounds( i );
        int imageWidth = imageBounds.width; 
        int imageLeft = imageBounds.x - checkWidth;
        if( imageLeft + imageWidth > bounds.x + bounds.width ) {
          imageWidth = imageLeft - bounds.x + bounds.width;
        }
        int textLeft = textBounds.x - checkWidth;
        result[ i ].imageLeft = new Integer( imageLeft );
        result[ i ].imageWidth = new Integer( imageWidth );
        result[ i ].textLeft = new Integer( textLeft );
        result[ i ].textWidth = new Integer( textBounds.width );
      }
    }
    return result;
  }

  private static int getCheckWidth( final Table table ) {
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    return tableAdapter.getCheckWidth();
  }

  static TableItem getMeasureItem( final Table table ) {
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    TableItem[] items = table.getItems();
    TableItem result = null;
    if( table.getColumnCount() == 0 ) {
      // Find item with longest text because the imaginary only column stretches 
      // as wide as the longest item (images cannot differ in width)
      for( int i = 0; i < items.length; i++ ) {
        if( !tableAdapter.isItemVirtual( items[ i ] ) ) {
          if( result == null ) {
            result = items[ i ];
          } else {
            result = max( result, items[ i ] );
          }
        }
      }
    } else {
      // Find the first non-virtual item
      for( int i = 0; result == null && i < items.length; i++ ) {
        if( !tableAdapter.isItemVirtual( items[ i ] ) ) {
          result = items[ i ];
        }
      }
    }
    return result;
  }

  private static TableItem max( final TableItem item1, final TableItem item2 ) {
    TableItem result;
    if( item1.getText( 0 ).length() > item2.getText( 0 ).length() ) {
      result = item1;
    } else {
      result = item2;
    }
    return result;
  }

  private TableLCAUtil() {
    // prevent instantiation
  }

  /////////////////
  // Inner classes
  
  private static final class ItemMetrics {
    Integer imageLeft = ZERO;
    Integer imageWidth = ZERO;
    Integer textLeft = ZERO;
    Integer textWidth = ZERO;
    
    public boolean equals( final Object obj ) {
      boolean result;
      if( obj == this ) {
        result = true;
      } else  if( obj instanceof ItemMetrics ) {
        ItemMetrics other = ( ItemMetrics )obj;
        result =  other.imageLeft.equals( imageLeft ) 
               && other.imageWidth.equals( imageWidth )
               && other.textLeft.equals( textLeft )
               && other.textWidth.equals( textWidth );
      } else {
        result = false;
      }
      return result;
    }
  }
}
