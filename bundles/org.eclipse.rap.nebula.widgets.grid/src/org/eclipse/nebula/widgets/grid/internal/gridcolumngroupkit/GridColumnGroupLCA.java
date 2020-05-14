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
package org.eclipse.nebula.widgets.grid.internal.gridcolumngroupkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.hasChanged;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.createRemoteObject;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.remote.JsonMapping.toJson;

import java.io.IOException;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.nebula.widgets.grid.internal.IGridAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;


@SuppressWarnings( "restriction" )
public class GridColumnGroupLCA extends WidgetLCA<GridColumnGroup> {

  public static final GridColumnGroupLCA INSTANCE = new GridColumnGroupLCA();

  private static final String TYPE = "rwt.widgets.GridColumnGroup";
  private static final String[] ALLOWED_STYLES = new String[] { "TOGGLE" };

  private static final String PROP_LEFT = "left";
  private static final String PROP_WIDTH = "width";
  private static final String PROP_HEIGHT = "height";
  private static final String PROP_VISIBLE = "visibility";
  private static final String PROP_FONT = "font";
  private static final String PROP_EXPANDED = "expanded";
  private static final String PROP_HEADER_WORD_WRAP = "headerWordWrap";
  private static final String PROP_FIXED = "fixed";
  private static final String PROP_EXPAND_LISTENER = "Expand";
  private static final String PROP_COLLAPSE_LISTENER = "Collapse";

  private static final int ZERO = 0;

  @Override
  public void renderInitialization( GridColumnGroup group ) throws IOException {
    RemoteObject remoteObject = createRemoteObject( group, TYPE );
    remoteObject.setHandler( new GridColumnGroupOperationHandler( group ) );
    remoteObject.set( "parent", getId( group.getParent() ) );
    remoteObject.set( "style", createJsonArray( getStyles( group, ALLOWED_STYLES ) ) );
    // Always render listen for Expand and Collapse, currently required for columns
    // visibility update.
    remoteObject.listen( PROP_EXPAND_LISTENER, true );
    remoteObject.listen( PROP_COLLAPSE_LISTENER, true );
  }

  @Override
  public void preserveValues( GridColumnGroup group ) {
    ItemLCAUtil.preserve( group );
    preserveProperty( group, PROP_LEFT, getLeft( group ) );
    preserveProperty( group, PROP_WIDTH, getWidth( group ) );
    preserveProperty( group, PROP_HEIGHT, getHeight( group ) );
    preserveProperty( group, PROP_VISIBLE, isVisible( group ) );
    preserveProperty( group, PROP_FONT, group.getHeaderFont() );
    preserveProperty( group, PROP_EXPANDED, group.getExpanded() );
    preserveProperty( group, PROP_HEADER_WORD_WRAP, group.getHeaderWordWrap() );
    preserveProperty( group, PROP_FIXED, isFixed( group ) );
  }

  @Override
  public void renderChanges( GridColumnGroup group ) throws IOException {
    WidgetLCAUtil.renderCustomVariant( group );
    ItemLCAUtil.renderChanges( group );
    renderProperty( group, PROP_LEFT, getLeft( group ), ZERO );
    renderProperty( group, PROP_WIDTH, getWidth( group ), ZERO );
    renderProperty( group, PROP_HEIGHT, getHeight( group ), ZERO );
    renderProperty( group, PROP_VISIBLE, isVisible( group ), true );
    renderFont( group, PROP_FONT, group.getHeaderFont() );
    renderProperty( group, PROP_EXPANDED, group.getExpanded(), true );
    renderProperty( group, PROP_HEADER_WORD_WRAP, group.getHeaderWordWrap(), false );
    renderProperty( group, PROP_FIXED, isFixed( group ), false );
  }

  //////////////////////////////////////////////
  // Helping methods to render widget properties

  private static void renderFont( GridColumnGroup group, String property, Font newValue ) {
    if( hasChanged( group, property, newValue, group.getParent().getFont() ) ) {
      getRemoteObject( group ).set( property, toJson( newValue ) );
    }
  }

  //////////////////
  // Helping methods

  private static int getLeft( GridColumnGroup group ) {
    Grid grid = group.getParent();
    int result = grid.getItemHeaderWidth();
    int[] columnOrder = grid.getColumnOrder();
    boolean found = false;
    for( int i = 0; i < columnOrder.length && !found; i++ ) {
      GridColumn currentColumn = grid.getColumn( columnOrder[ i ] );
      if( currentColumn.getColumnGroup() == group ) {
        found = true;
      } else if( currentColumn.isVisible() ) {
        result += currentColumn.getWidth();
      }
    }
    return result;
  }

  private static int getWidth( GridColumnGroup group ) {
    int result = 0;
    GridColumn[] columns = group.getColumns();
    for( int i = 0; i < columns.length; i++ ) {
      if( columns[ i ].isVisible() ) {
        result += columns[ i ].getWidth();
      }
    }
    return result;
  }

  private static int getHeight( GridColumnGroup group ) {
    return group.getParent().getGroupHeaderHeight();
  }

  private static boolean isVisible( GridColumnGroup group ) {
    boolean result = false;
    GridColumn[] columns = group.getColumns();
    for( int i = 0; i < columns.length && !result; i++ ) {
      if( columns[ i ].isVisible() ) {
        result = true;
      }
    }
    return result;
  }

  private static boolean isFixed( GridColumnGroup group ) {
    boolean result = false;
    IGridAdapter gridAdapter = getGridAdapter( group );
    for( GridColumn column : group.getColumns() ) {
      if( gridAdapter.isFixedColumn( column ) ) {
        result = true;
      }
    }
    return result;
  }

  private static IGridAdapter getGridAdapter( GridColumnGroup group ) {
    return group.getParent().getAdapter( IGridAdapter.class );
  }

}
