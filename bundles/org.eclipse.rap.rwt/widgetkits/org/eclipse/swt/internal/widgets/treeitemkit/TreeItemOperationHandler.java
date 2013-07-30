/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.treeitemkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.WidgetOperationHandler;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.swt.widgets.TreeItem;


public class TreeItemOperationHandler extends WidgetOperationHandler {

  private static final String PROP_CHECKED = "checked";
  private static final String PROP_EXPANDED = "expanded";

  public TreeItemOperationHandler( TreeItem item ) {
    super( item );
  }

  @Override
  public void handleSet( JsonObject properties ) {
    handleSetChecked( properties );
    handleSetExpanded( properties );
  }

  /*
   * PROTOCOL SET checked
   *
   * @param checked (boolean) true if the item was checked, false otherwise
   */
  private void handleSetChecked( JsonObject properties ) {
    TreeItem item = ( TreeItem )widget;
    JsonValue checked = properties.get( PROP_CHECKED );
    if( checked != null ) {
      item.setChecked( checked.asBoolean() );
    }
  }

  /*
   * PROTOCOL SET expanded
   *
   * @param expanded (boolean) true if the item was expanded, false otherwise
   */
  private void handleSetExpanded( JsonObject properties ) {
    final TreeItem item = ( TreeItem )widget;
    final JsonValue expanded = properties.get( PROP_EXPANDED );
    if( expanded != null ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          item.setExpanded( expanded.asBoolean() );
          preserveProperty( item, PROP_EXPANDED, item.getExpanded() );
        }
      } );
    }
  }

}
