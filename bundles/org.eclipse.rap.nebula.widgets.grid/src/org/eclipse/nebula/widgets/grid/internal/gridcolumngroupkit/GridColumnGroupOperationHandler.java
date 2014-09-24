/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid.internal.gridcolumngroupkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_COLLAPSE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_EXPAND;

import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.internal.protocol.WidgetOperationHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;


@SuppressWarnings( "restriction" )
public class GridColumnGroupOperationHandler extends WidgetOperationHandler<GridColumnGroup> {

  private static final String PROP_EXPANDED = "expanded";

  public GridColumnGroupOperationHandler( GridColumnGroup group ) {
    super( group );
  }

  @Override
  public void handleSet( GridColumnGroup group, JsonObject properties ) {
    handleSetExpanded( group, properties );
  }

  @Override
  public void handleNotify( GridColumnGroup group, String eventName, JsonObject properties ) {
    if( EVENT_EXPAND.equals( eventName ) ) {
      handleNotifyExpand( group, properties );
    } else if( EVENT_COLLAPSE.equals( eventName ) ) {
      handleNotifyCollapse( group, properties );
    } else {
      super.handleNotify( group, eventName, properties );
    }
  }

  /*
   * PROTOCOL SET expanded
   *
   * @param expanded (boolean) true if the group was expanded, false otherwise
   */
  public void handleSetExpanded( final GridColumnGroup group, JsonObject properties ) {
    final JsonValue expanded = properties.get( PROP_EXPANDED );
    if( expanded != null ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          group.setExpanded( expanded.asBoolean() );
//          preserveProperty( group, PROP_EXPANDED, group.getExpanded() );
        }
      } );
    }
  }

  /*
   * PROTOCOL NOTIFY Expand
   */
  @SuppressWarnings( "unused" )
  public void handleNotifyExpand( GridColumnGroup group, JsonObject properties ) {
    group.notifyListeners( SWT.Expand, new Event() );
  }

  /*
   * PROTOCOL NOTIFY Collapse
   */
  @SuppressWarnings( "unused" )
  public void handleNotifyCollapse( GridColumnGroup group, JsonObject properties ) {
    group.notifyListeners( SWT.Collapse, new Event() );
  }

}
