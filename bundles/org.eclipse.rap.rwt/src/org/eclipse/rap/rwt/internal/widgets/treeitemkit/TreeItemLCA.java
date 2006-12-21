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
import org.eclipse.rap.rwt.events.TreeEvent;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.TreeItem;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.engine.service.ContextProvider;


public final class TreeItemLCA extends AbstractWidgetLCA {

  // Expanded/collapsed state constants, used by readData 
  private static final String STATE_COLLAPSED = "collapsed";
  private static final String STATE_EXPANDED = "expanded";
  
  // tree item functions as defined in org.eclipse.rap.rwt.TreeUtil
  private static final String CREATE_TREE_ITEM 
    = "org.eclipse.rap.rwt.TreeUtil.createTreeItem";

  public void preserveValues( final Widget widget ) {
    TreeItem treeItem = ( TreeItem )widget;
    ItemLCAUtil.preserve( treeItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( treeItem );
    adapter.preserve( Props.EXPANDED, 
                      Boolean.valueOf( treeItem.getExpanded() ) );
  }

  public void readData( final Widget widget ) {
    String state = WidgetUtil.readPropertyValue( widget, "state" );
    if( STATE_EXPANDED.equals( state ) || STATE_COLLAPSED.equals( state ) ) {
      TreeItem treeItem = ( TreeItem )widget;
      treeItem.setExpanded( STATE_EXPANDED.equals( state ) );
    }
  }
  
  public void processAction( final Widget widget ) {
    processWidgetSelectedEvent( widget );
    if( !processTreeExpandedEvent( widget ) ) {
      processTreeCollapsedEvent( widget );
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
    writer.set( Props.TEXT, JSConst.QX_FIELD_LABEL, treeItem.getText() );
    if( treeItem.getImage() != null ) {
      writer.set( Props.IMAGE,
                  JSConst.QX_FIELD_ICON, 
                  Image.getPath( treeItem.getImage() ) );
    }
    writeExpanded( treeItem );
  }

  private static void writeExpanded( final TreeItem treeItem ) 
    throws IOException 
  {
    Boolean newValue = Boolean.valueOf( treeItem.getExpanded() );
    Boolean defValue = Boolean.FALSE;
    if( WidgetUtil.hasChanged( treeItem, Props.EXPANDED, newValue, defValue ) ) 
    {
      JSWriter writer = JSWriter.getWriterFor( treeItem );
      writer.set( "open", treeItem.getExpanded() );
    }
  }
  
  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  /////////////////////////////////////
  // Helping methods to dispatch events
  
  private static boolean processWidgetSelectedEvent( final Widget widget ) {
    boolean result = false;
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
      result = true;
    }
    return result;
  }

  private static boolean processTreeExpandedEvent( final Widget widget ) {
    boolean result = false;
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_TREE_EXPANDED );
    if( WidgetUtil.getId( widget ).equals( id ) ) {
      TreeItem treeItem = ( TreeItem )widget;
      TreeEvent event = new TreeEvent( treeItem.getParent(), 
                                       treeItem,
                                       TreeEvent.TREE_EXPANDED,
                                       true,
                                       RWT.NONE );
      event.processEvent();
      result = true;
    }
    return result;
  }

  private static boolean processTreeCollapsedEvent( final Widget widget ) {
    boolean result = false;
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_TREE_COLLAPSED );
    if( WidgetUtil.getId( widget ).equals( id ) ) {
      TreeItem treeItem = ( TreeItem )widget;
      TreeEvent event = new TreeEvent( treeItem.getParent(), 
                                       treeItem,
                                       TreeEvent.TREE_COLLAPSED,
                                       true,
                                       RWT.NONE );
      event.processEvent();
      result = true;
    }
    return result;
  }
}
