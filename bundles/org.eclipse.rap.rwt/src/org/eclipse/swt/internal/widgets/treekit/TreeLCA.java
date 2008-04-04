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

package org.eclipse.swt.internal.widgets.treekit;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.widgets.*;


public final class TreeLCA extends AbstractWidgetLCA {

  // Property names used by preserve mechanism
  private static final String PROP_SELECTION_LISTENERS = "selectionListeners";
  static final String PROP_TREE_LISTENERS = "treeListeners";
  static final String PROP_HEADER_HEIGHT = "headerHeight";
  static final String PROP_HEADER_VISIBLE = "headerVisible";
  static final String PROP_COLUMN_ORDER = "columnOrder";
 
  public void preserveValues( final Widget widget ) {
    Tree tree  = ( Tree )widget;
    ControlLCAUtil.preserveValues( ( Control )widget );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( tree );
    adapter.preserve( PROP_SELECTION_LISTENERS, 
                      Boolean.valueOf( SelectionEvent.hasListener( tree ) ) );
    adapter.preserve( PROP_TREE_LISTENERS, 
                      Boolean.valueOf( TreeEvent.hasListener( tree ) ) );
    adapter.preserve( PROP_HEADER_HEIGHT, 
                      new Integer( tree.getHeaderHeight() ) );
    adapter.preserve( PROP_HEADER_VISIBLE, 
                      Boolean.valueOf( tree.getHeaderVisible() ) );
    
    int[] values = tree.getColumnOrder();
    Integer[] columnOrder = new Integer[ values.length ];
    for( int i = 0; i < values.length; i++ ) {
      columnOrder[ i ] = new Integer( values[ i ] );
    }
    adapter.preserve( PROP_COLUMN_ORDER, 
                      columnOrder );
  }
  
  public void readData( final Widget widget ) {
    Tree tree = ( Tree )widget;
    readSelection( tree );
    readScrollPosition( tree );
    processWidgetSelectedEvent( tree );
    processWidgetDefaultSelectedEvent( tree );
    ControlLCAUtil.processMouseEvents( tree );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Tree tree = ( Tree )widget;
    JSWriter writer = JSWriter.getWriterFor( tree );
    StringBuffer style = new StringBuffer();
    if( ( tree.getStyle() & SWT.MULTI ) != 0 ) {
      style.append( "multi|" );
    }
    if( ( tree.getStyle() & SWT.CHECK ) != 0 ) {
      style.append( "check|" );
    }
    if( ( tree.getStyle() & SWT.VIRTUAL ) != 0 ) {
      style.append( "virtual|" );
    }
    writer.newWidget( "org.eclipse.swt.widgets.Tree", 
                      new Object[] { style.toString() } );
    ControlLCAUtil.writeStyleFlags( tree );
    WidgetLCAUtil.writeCustomVariant( widget );
  }
  
  public void renderChanges( final Widget widget ) throws IOException {
    Tree tree = ( Tree )widget;
    ControlLCAUtil.writeChanges( tree );
    updateSelectionListener( tree );
    updateTreeListener( tree );
    writeHeaderHeight( tree );
    writeHeaderVisible( tree );
    writeColumnOrder( tree );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
  
  public void createResetHandlerCalls( final String typePoolId ) 
    throws IOException 
  {
  }
  
  public String getTypePoolId( final Widget widget ) {
    return null;
  }
  
  public void doRedrawFake( final Control control ) {
    int evtId = ControlEvent.CONTROL_RESIZED;
    ControlEvent evt = new ControlEvent( control, evtId );
    evt.processEvent();
  }
  
  private static void processWidgetSelectedEvent( final Tree tree ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String eventName = JSConst.EVENT_WIDGET_SELECTED;
    if( WidgetLCAUtil.wasEventSent( tree, eventName ) ) {
      Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
      String itemId = request.getParameter( eventName + ".item" );
      Item treeItem = ( Item )WidgetUtil.find( tree, itemId );
      String detailStr = request.getParameter( eventName + ".detail" );
      int detail = "check".equals( detailStr ) ? SWT.CHECK : SWT.NONE;
      int eventType = SelectionEvent.WIDGET_SELECTED;
      SelectionEvent event = new SelectionEvent( tree,
                                                 treeItem,
                                                 eventType,
                                                 bounds,
                                                 null,
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
      int detail = SWT.NONE;
      int eventType = SelectionEvent.WIDGET_DEFAULT_SELECTED;
      SelectionEvent event = new SelectionEvent( tree,
                                                 treeItem,
                                                 eventType ,
                                                 bounds,
                                                 null,
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

  private static void readScrollPosition( final Tree tree ) {
    String left = WidgetLCAUtil.readPropertyValue( tree, "scrollLeft" );
    String top = WidgetLCAUtil.readPropertyValue( tree, "scrollTop" );
    if( left != null && top != null ) {
      final ITreeAdapter adapter = ( ITreeAdapter )tree.getAdapter( ITreeAdapter.class );
      
      final int newScrollLeft = parsePosition( left );
      final int newScrollTop = parsePosition( top );
      
      final int oldScrollTop = adapter.getScrollTop();
      adapter.setScrollLeft( newScrollLeft );
      adapter.setScrollTop( newScrollTop );
      if(oldScrollTop != newScrollTop) {
        ProcessActionRunner.add( new Runnable() {

          public void run() {
            adapter.checkAllData( tree );
          }
          
        });
      }
    }
  }

  private static int parsePosition( final String position ) {
    int result = 0;
    try {
      result = Integer.valueOf( position ).intValue();
    } catch( NumberFormatException e ) {
      // ignore and use default value
    }
    return result;
  }

  /////////////////////////////////////////////////////////////
  // Helping methods to write JavaScript for changed properties

  private static void writeHeaderHeight( final Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Integer newValue = new Integer( tree.getHeaderHeight() );
    writer.set( PROP_HEADER_HEIGHT, "headerHeight", newValue, null );
  }

  private void writeColumnOrder( Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    int[] values = tree.getColumnOrder();
    if( values.length > 0 ) {
      Integer[] newValue = new Integer[ values.length ];
      for( int i = 0; i < values.length; i++ ) {
        newValue[ i ] = new Integer( values[ i ] );
      }
      if( WidgetLCAUtil.hasChanged( tree, PROP_COLUMN_ORDER, newValue, new Integer[] {} ) ) {
        writer.set( PROP_COLUMN_ORDER, "columnOrder", newValue, null );
      }
    }
  }

  private static void writeHeaderVisible( final Tree tree )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Boolean newValue = Boolean.valueOf( tree.getHeaderVisible() );
    writer.set( PROP_HEADER_VISIBLE, "headerVisible", newValue, Boolean.FALSE );
  }
  
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
