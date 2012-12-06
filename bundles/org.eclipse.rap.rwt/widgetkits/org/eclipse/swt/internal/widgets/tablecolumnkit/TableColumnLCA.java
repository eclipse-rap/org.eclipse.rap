/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tablecolumnkit;

import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.readCallPropertyValueAsString;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.internal.widgets.IControlAdapter;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;


public final class TableColumnLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.GridColumn";

  static final String PROP_INDEX = "index";
  static final String PROP_LEFT = "left";
  static final String PROP_WIDTH = "width";
  static final String PROP_RESIZABLE = "resizable";
  static final String PROP_MOVEABLE = "moveable";
  static final String PROP_ALIGNMENT = "alignment";
  static final String PROP_FIXED = "fixed";
  static final String PROP_SELECTION_LISTENER = "Selection";

  private static final int ZERO = 0;
  private static final String DEFAULT_ALIGNMENT = "left";

  @Override
  public void preserveValues( Widget widget ) {
    TableColumn column = ( TableColumn )widget;
    WidgetLCAUtil.preserveToolTipText( column, column.getToolTipText() );
    WidgetLCAUtil.preserveCustomVariant( column );
    WidgetLCAUtil.preserveFont( column, getFont( column ) );
    ItemLCAUtil.preserve( column );
    preserveProperty( column, PROP_INDEX, getIndex( column ) );
    preserveProperty( column, PROP_LEFT, getLeft( column ) );
    preserveProperty( column, PROP_WIDTH, column.getWidth() );
    preserveProperty( column, PROP_RESIZABLE, column.getResizable() );
    preserveProperty( column, PROP_MOVEABLE, column.getMoveable() );
    preserveProperty( column, PROP_ALIGNMENT, getAlignment( column ) );
    preserveProperty( column, PROP_FIXED, isFixed( column ) );
    preserveListener( column, PROP_SELECTION_LISTENER, column.isListening( SWT.Selection ) );
  }

  public void readData( Widget widget ) {
    final TableColumn column = ( TableColumn )widget;
    String methodName = "resize";
    if( ProtocolUtil.wasCallSend( getId( column ), methodName ) ) {
      // TODO [rh] HACK: force width to have changed when client-side changes
      //      it. Since this is done while a column resize we must re-layout
      //      all columns including the resized one.
      String width = readCallPropertyValueAsString( getId( column ), methodName, "width" );
      final int newWidth = NumberFormatUtil.parseInt( width );
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          column.setWidth( newWidth );
        }
      } );
    }
    methodName = "move";
    if( ProtocolUtil.wasCallSend( getId( column ), methodName ) ) {
      String left = readCallPropertyValueAsString( getId( column ), methodName, "left" );
      final int newLeft = NumberFormatUtil.parseInt( left );
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          moveColumn( column, newLeft );
        }
      } );
    }
    ControlLCAUtil.processSelection( column, null, false );
    ControlLCAUtil.processDefaultSelection( column, null );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    TableColumn column = ( TableColumn )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( column );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( column.getParent() ) );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    TableColumn column = ( TableColumn )widget;
    WidgetLCAUtil.renderToolTip( column, column.getToolTipText() );
    WidgetLCAUtil.renderCustomVariant( column );
    WidgetLCAUtil.renderFont( column, getFont( column ) );
    ItemLCAUtil.renderChanges( column );
    renderProperty( column, PROP_INDEX, getIndex( column ), ZERO );
    renderProperty( column, PROP_LEFT, getLeft( column ), ZERO );
    renderProperty( column, PROP_WIDTH, column.getWidth(), ZERO );
    renderProperty( column, PROP_RESIZABLE, column.getResizable(), true );
    renderProperty( column, PROP_MOVEABLE, column.getMoveable(), false );
    renderProperty( column, PROP_ALIGNMENT, getAlignment( column ), DEFAULT_ALIGNMENT );
    renderProperty( column, PROP_FIXED, isFixed( column ), false );
    renderListener( column, PROP_SELECTION_LISTENER, column.isListening( SWT.Selection ), false );
  }

  //////////////////////////////////////////////////
  // Helping methods to obtain calculated properties

  private static int getIndex( TableColumn column ) {
    return column.getParent().indexOf( column );
  }

  private static int getLeft( TableColumn column ) {
    ITableAdapter adapter = column.getParent().getAdapter( ITableAdapter.class );
    return adapter.getColumnLeft( column );
  }

  private static String getAlignment( TableColumn column ) {
    int alignment = column.getAlignment();
    String result = "left";
    if( ( alignment & SWT.CENTER ) != 0 ) {
      result = "center";
    } else if( ( alignment & SWT.RIGHT ) != 0 ) {
      result = "right";
    }
    return result;
  }

  private static Font getFont( TableColumn column ) {
    Table table = column.getParent();
    IControlAdapter adapter = table.getAdapter( IControlAdapter.class );
    return adapter.getUserFont();
  }

  private static boolean isFixed( TableColumn column ) {
    ITableAdapter adapter = column.getParent().getAdapter( ITableAdapter.class );
    return adapter.isFixedColumn( column );
  }

  /////////////////////////////////
  // Helping methods to move column

  static void moveColumn( TableColumn column, int newLeft ) {
    Table table = column.getParent();
    int targetColumn = findMoveTarget( table, newLeft );
    int[] columnOrder = table.getColumnOrder();
    int index = table.indexOf( column );
    int orderIndex = arrayIndexOf( columnOrder, index );
    columnOrder = arrayRemove( columnOrder, orderIndex );
    if( orderIndex < targetColumn ) {
      targetColumn--;
    }
    if( isFixed( column ) || isFixed( table.getColumn( targetColumn ) ) ) {
      targetColumn = table.indexOf( column );
    }
    columnOrder = arrayInsert( columnOrder, targetColumn, index );
    if( Arrays.equals( columnOrder, table.getColumnOrder() ) ) {
      // TODO [rh] HACK mark left as changed
      TableColumn[] columns = table.getColumns();
      for( int i = 0; i < columns.length; i++ ) {
        IWidgetAdapter adapter = WidgetUtil.getAdapter( columns[ i ] );
        adapter.preserve( PROP_LEFT, null );
      }
    } else {
      table.setColumnOrder( columnOrder );
      // [if] HACK mark left as changed - see bug 336340
      IWidgetAdapter adapter = WidgetUtil.getAdapter( column );
      adapter.preserve( PROP_LEFT, null );
    }
  }

  /* (intentionally non-JavaDoc'ed)
   * Returns the index in the columnOrder array at which the moved column
   * should be inserted (moving remaining columns to the right). A return
   * value of columnCount indicates that the moved column should be inserted
   * after the right-most column.
   */
  private static int findMoveTarget( Table table, int newLeft ) {
    int result = -1;
    TableColumn[] columns = table.getColumns();
    int[] columnOrder = table.getColumnOrder();
    if( newLeft < 0 ) {
      result = 0;
    } else {
      for( int i = 0; result == -1 && i < columns.length; i++ ) {
        TableColumn column = columns[ columnOrder [ i ] ];
        int left = getLeft( column );
        int width = column.getWidth();
        if( isFixed( column ) ) {
          left += getLeftOffset( column );
        }
        if( newLeft >= left && newLeft <= left + width ) {
          result = i;
          if( newLeft >= left + width / 2 && result < columns.length && !isFixed( column ) ) {
            result++;
          }
        }
      }
    }
    // Column was moved right of the right-most column
    if( result == -1 ) {
      result = columns.length;
    }
    return result;
  }

  private static int getLeftOffset( TableColumn column ) {
    ITableAdapter adapter = column.getParent().getAdapter( ITableAdapter.class );
    return adapter.getLeftOffset();
  }

  private static int arrayIndexOf( int[] array, int value ) {
    int result = -1;
    for( int i = 0; result == -1 && i < array.length; i++ ) {
      if( array[ i ] == value ) {
        result = i;
      }
    }
    return result;
  }

  private static int[] arrayRemove( int[] array, int index ) {
    int length = array.length;
    int[] result = new int[ length - 1 ];
    System.arraycopy( array, 0, result, 0, index );
    if( index < length - 1 ) {
      System.arraycopy( array, index + 1, result, index, length - index - 1 );
    }
    return result;
  }

  private static int[] arrayInsert( int[] array, int index, int value ) {
    int length = array.length;
    int[] result = new int[ length + 1 ];
    System.arraycopy( array, 0, result, 0, length );
    System.arraycopy( result, index, result, index + 1, length - index );
    result[ index ] = value;
    return result;
  }
}
