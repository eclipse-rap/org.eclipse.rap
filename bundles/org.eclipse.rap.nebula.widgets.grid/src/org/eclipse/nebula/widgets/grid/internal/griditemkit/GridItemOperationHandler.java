/*******************************************************************************
 * Copyright (c) 2013, 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid.internal.griditemkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveProperty;

import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.nebula.widgets.grid.internal.IGridAdapter;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.internal.protocol.WidgetOperationHandler;


@SuppressWarnings( "restriction" )
public class GridItemOperationHandler extends WidgetOperationHandler<GridItem> {

  private static final String PROP_CELL_CHECKED = "cellChecked";
  private static final String PROP_EXPANDED = "expanded";
  private static final String PROP_HEIGHT = "height";

  public GridItemOperationHandler( GridItem item ) {
    super( item );
  }

  @Override
  public void handleSet( GridItem item, JsonObject properties ) {
    handleSetChecked( item, properties );
    handleSetExpanded( item, properties );
    handleSetHeight( item, properties );
  }

  /*
   * PROTOCOL SET checked
   *
   * @param checked ([boolean]) array with item checked states (by column)
   */
  public void handleSetChecked( GridItem item, JsonObject properties ) {
    JsonValue value = properties.get( PROP_CELL_CHECKED );
    if( value != null ) {
      JsonArray arrayValue = value.asArray();
      int offset = getGridAdapter( item ).getRowHeadersColumn() != null ? 1 : 0;
      for( int i = offset; i < arrayValue.size(); i++ ) {
        item.setChecked( i - offset, arrayValue.get( i ).asBoolean() );
      }
    }
  }

  /*
   * PROTOCOL SET expanded
   *
   * @param expanded (boolean) true if the item was expanded, false otherwise
   */
  public void handleSetExpanded( final GridItem item, JsonObject properties ) {
    final JsonValue expanded = properties.get( PROP_EXPANDED );
    if( expanded != null ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          item.setExpanded( expanded.asBoolean() );
          preserveProperty( item, PROP_EXPANDED, item.isExpanded() );
        }
      } );
    }
  }

  /*
   * PROTOCOL SET height
   *
   * @param height (int) the actual item height measured by the client
   */
  public void handleSetHeight( GridItem item, JsonObject properties ) {
    JsonValue value = properties.get( PROP_HEIGHT );
    if( value != null ) {
      item.setHeight( value.asInt() );
    }
  }

  private static IGridAdapter getGridAdapter( GridItem item ) {
    return item.getParent().getAdapter( IGridAdapter.class );
  }

}
