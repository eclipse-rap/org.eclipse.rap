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
import org.eclipse.rap.rwt.events.TreeEvent;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.internal.widgets.ItemLCAUtil;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.TreeItem;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.engine.service.ContextProvider;


public final class TreeItemLCA extends AbstractWidgetLCA {

  public static final String PROP_FONT = "font";
  public static final String PROP_CHECKED = "checked";
  public static final String PROP_EXPANDED = "expanded";

  // Expanded/collapsed state constants, used by readData 
  private static final String STATE_COLLAPSED = "collapsed";
  private static final String STATE_EXPANDED = "expanded";
  
  public void preserveValues( final Widget widget ) {
    TreeItem treeItem = ( TreeItem )widget;
    ItemLCAUtil.preserve( treeItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( treeItem );
    adapter.preserve( PROP_FONT, treeItem.getFont() );
    adapter.preserve( PROP_CHECKED, Boolean.valueOf( treeItem.getChecked() ) );
    adapter.preserve( TreeItemLCA.PROP_EXPANDED, 
                      Boolean.valueOf( treeItem.getExpanded() ) );
  }

  public void readData( final Widget widget ) {
    TreeItem treeItem = ( TreeItem )widget;
    String value = WidgetLCAUtil.readPropertyValue( widget, "checked" );
    if( value != null ) {
      treeItem.setChecked( Boolean.valueOf( value ).booleanValue() );
    }
    // TODO [rh] TreeEvent behave different from SWT: SWT-style is to send
    //      the event and afterwards set the expanded property of the item
    String state = WidgetLCAUtil.readPropertyValue( widget, "state" );
    if( STATE_EXPANDED.equals( state ) || STATE_COLLAPSED.equals( state ) ) {
      treeItem.setExpanded( STATE_EXPANDED.equals( state ) );
    }
    processTreeExpandedEvent( widget );
    processTreeCollapsedEvent( widget );
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    TreeItem treeItem = ( TreeItem )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    Object parent;
    if( treeItem.getParentItem() == null ) {
      parent = treeItem.getParent();
    } else {
      parent = treeItem.getParentItem();
    }
    Object[] args = new Object[] { 
      parent
    };
    writer.newWidget( "org.eclipse.rap.rwt.widgets.TreeItem", args );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    TreeItem treeItem = ( TreeItem )widget;
    ItemLCAUtil.writeText( treeItem );
    writeImage( treeItem );
    WidgetLCAUtil.writeFont( treeItem, treeItem.getFont() );
    writeExpanded( treeItem );
    writeChecked( treeItem );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    // safely remove tree item from tree
    writer.call( "destroy", null );
    writer.dispose();
  }

  ///////////////////////////////////
  // Helping methods to write changes
  
  // TODO [rh] workaround for qx bug #260 (TreeFullControl doesn't update icon 
  //      when it is changed) 
  private static void writeImage( final TreeItem treeItem ) throws IOException {
    Image image = treeItem.getImage();
    WidgetLCAUtil.writeImage( treeItem, Props.IMAGE, "image", image );
  }

  private static void writeExpanded( final TreeItem treeItem ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( treeItem );
    Boolean newValue = Boolean.valueOf( treeItem.getExpanded() );
    writer.set( PROP_EXPANDED, "open", newValue, Boolean.FALSE );
  }

  private static void writeChecked( final TreeItem treeItem ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( treeItem );
    Boolean newValue = Boolean.valueOf( treeItem.getChecked() );
    writer.set( PROP_CHECKED, "checked", newValue, Boolean.FALSE );
  }

  private static void processTreeExpandedEvent( final Widget widget ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_TREE_EXPANDED );
    if( WidgetUtil.getId( widget ).equals( id ) ) {
      TreeItem treeItem = ( TreeItem )widget;
      TreeEvent event = new TreeEvent( treeItem.getParent(), 
                                       treeItem,
                                       TreeEvent.TREE_EXPANDED );
      event.processEvent();
    }
  }

  private static void processTreeCollapsedEvent( final Widget widget ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_TREE_COLLAPSED );
    if( WidgetUtil.getId( widget ).equals( id ) ) {
      TreeItem treeItem = ( TreeItem )widget;
      TreeEvent event = new TreeEvent( treeItem.getParent(), 
                                       treeItem,
                                       TreeEvent.TREE_COLLAPSED );
      event.processEvent();
    }
  }
}
