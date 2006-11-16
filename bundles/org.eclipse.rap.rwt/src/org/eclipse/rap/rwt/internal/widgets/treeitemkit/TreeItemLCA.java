/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.widgets.treeitemkit;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.ItemLCAUtil;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.TreeItem;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.engine.service.ContextProvider;


public final class TreeItemLCA extends AbstractWidgetLCA {

  // tree item functions as defined in org.eclipse.rap.rwt.TreeUtil
  private static final String CREATE_TREE_ITEM 
    = "org.eclipse.rap.rwt.TreeUtil.createTreeItem";

  public void preserveValues( final Widget widget ) {
    TreeItem treeItem = ( TreeItem )widget;
    ItemLCAUtil.preserve( treeItem );
  }

  public void readData( final Widget widget ) {
  }
  
  public void processAction( final Widget widget ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_WIDGET_SELECTED );
    if( WidgetUtil.getId( widget ).equals( id ) ) {
      TreeItem treeItem = ( TreeItem )widget;
      Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
      SelectionEvent event = new SelectionEvent( treeItem.getParent(), 
                                                 treeItem,
                                                 SelectionEvent.WIDGET_SELECTED,
                                                 bounds,
                                                 true,
                                                 RWT.NONE );
      event.processEvent();
    }
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    TreeItem treeItem = ( TreeItem )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    Object[] args = new Object[] { 
      treeItem.getParent(), 
      WidgetUtil.getId( treeItem ), 
      treeItem.getParentItem()
    };
    writer.callStatic( CREATE_TREE_ITEM, args );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    TreeItem treeItem = ( TreeItem )widget;
    JSWriter writer = JSWriter.getWriterFor( treeItem );
    writer.set( Props.TEXT, "label", treeItem.getText() );
  }
  
  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
}
