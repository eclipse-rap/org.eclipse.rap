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

package org.eclipse.rap.rwt.internal.widgets.treekit;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.TreeEvent;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.engine.service.ContextProvider;

public final class TreeLCA extends AbstractWidgetLCA {

  // Property names used by preserve mechanism
  private static final String PROP_SELECTION_LISTENERS = "selectionListeners";
  private static final String PROP_TREE_LISTENERS = "treeListeners";
  
  public void preserveValues( final Widget widget ) {
    Tree tree  = ( Tree )widget;
    ControlLCAUtil.preserveValues( ( Control )widget );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( tree );
    adapter.preserve( PROP_SELECTION_LISTENERS, 
                      Boolean.valueOf( SelectionEvent.hasListener( tree ) ) );
    adapter.preserve( PROP_TREE_LISTENERS, 
                      Boolean.valueOf( TreeEvent.hasListener( tree ) ) );
  }
  
  public void readData( final Widget widget ) {
    Tree tree = ( Tree )widget;
    readSelection( tree );
    processWidgetSelectedEvent( tree );
    processWidgetDefaultSelectedEvent( tree );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    StringBuffer style = new StringBuffer();
    if( ( widget.getStyle() & RWT.MULTI ) != 0 ) {
      style.append( "multi|" );
    }
    if( ( widget.getStyle() & RWT.CHECK ) != 0 ) {
      style.append( "check|" );
    }
    writer.newWidget( "org.eclipse.rap.rwt.widgets.Tree", 
                      new Object[] { style.toString() } );
    ControlLCAUtil.writeStyleFlags( widget );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    Tree tree = ( Tree )widget;
    ControlLCAUtil.writeChanges( tree );
    updateSelectionListener( tree );
    updateTreeListener( tree );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  private static void processWidgetSelectedEvent( final Tree tree ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String eventName = JSConst.EVENT_WIDGET_SELECTED;
    if( WidgetLCAUtil.wasEventSent( tree, eventName ) ) {
      Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
      String itemId = request.getParameter( eventName + ".item" );
      Item treeItem = ( Item )WidgetUtil.find( tree, itemId );
      String detailStr = request.getParameter( eventName + ".detail" );
      int detail = "check".equals( detailStr ) ? RWT.CHECK : RWT.NONE;
      int eventType = SelectionEvent.WIDGET_SELECTED;
      SelectionEvent event = new SelectionEvent( tree,
                                                 treeItem,
                                                 eventType,
                                                 bounds,
                                                 true,
                                                 detail );
      event.processEvent();
    }
  }
  
  private static void processWidgetDefaultSelectedEvent( final Tree tree ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String eventName = JSConst.EVENT_WIDGET_DEFAULT_SELECTED;
    if( WidgetLCAUtil.wasEventSent( tree, eventName ) ) {
      Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
      String itemId = request.getParameter( eventName + ".item" );
      Item treeItem = ( Item )WidgetUtil.find( tree, itemId );
      int detail = RWT.NONE;
      int eventType = SelectionEvent.WIDGET_DEFAULT_SELECTED;
      SelectionEvent event = new SelectionEvent( tree,
                                                 treeItem,
                                                 eventType ,
                                                 bounds,
                                                 true,
                                                 detail );
      event.processEvent();
    }
  }

  // //////////////////////////////////////////
  // Helping methods to read client-side state

  private static void readSelection( final Tree tree ) {
    String value = WidgetLCAUtil.readPropertyValue( tree, "selection" );
    if( value != null ) {
      String[] values = value.split( "," );
      TreeItem[] selectedItems = new TreeItem[ values.length ];
      for( int i = 0; i < values.length; i++ ) {
        selectedItems[ i ] = ( TreeItem )WidgetUtil.find( tree, values[ i ] );
      }
      tree.setSelection( selectedItems );
    }
  }

  /////////////////////////////////////////////////////////////
  // Helping methods to write JavaScript for changed properties

  private static void updateSelectionListener( final Tree tree ) 
    throws IOException 
  {
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( tree ) );
    String prop = PROP_SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( tree, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( tree );
      writer.set( "selectionListeners", newValue );
    }
  }

  private static void updateTreeListener( final Tree tree ) 
    throws IOException 
  {
    Boolean newValue = Boolean.valueOf( TreeEvent.hasListener( tree ) );
    String prop = PROP_TREE_LISTENERS;
    if( WidgetLCAUtil.hasChanged( tree, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( tree );
      writer.set( "treeListeners", newValue );
    }
  }
}
