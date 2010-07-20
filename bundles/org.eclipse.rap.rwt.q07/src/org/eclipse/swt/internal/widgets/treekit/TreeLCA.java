/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
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
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.widgets.*;

public final class TreeLCA extends AbstractWidgetLCA {

  // Property names used by preserve mechanism
  private static final String PROP_SELECTION_LISTENERS = "selectionListeners";
  static final String PROP_HEADER_HEIGHT = "headerHeight";
  static final String PROP_HEADER_VISIBLE = "headerVisible";
  static final String PROP_COLUMN_COUNT = "columnCount";
  static final String PROP_TREE_COLUMN = "treeColumn";
  static final String PROP_ITEM_HEIGHT = "itemHeight";
  static final String PROP_TOP_ITEM_INDEX = "topItemIndex";
  static final String PROP_SCROLL_LEFT = "scrollLeft";
  static final String PROP_HAS_H_SCROLL_BAR = "hasHScrollBar";
  static final String PROP_HAS_V_SCROLL_BAR = "hasVScrollBar";
  static final String PROP_ITEM_METRICS = "itemMetrics";
  static final String PROP_LINES_VISIBLE = "linesVisible";
  
  private static final Integer DEFAULT_SCROLL_LEFT = new Integer( 0 );

  public void preserveValues( final Widget widget ) {
    Tree tree = ( Tree )widget;
    ControlLCAUtil.preserveValues( ( Control )widget );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( tree );
    adapter.preserve( PROP_SELECTION_LISTENERS,
                      Boolean.valueOf( SelectionEvent.hasListener( tree ) ) );
    adapter.preserve( PROP_HEADER_HEIGHT,
                      new Integer( tree.getHeaderHeight() ) );
    adapter.preserve( PROP_HEADER_VISIBLE,
                      Boolean.valueOf( tree.getHeaderVisible() ) );
    adapter.preserve( PROP_LINES_VISIBLE,
                      Boolean.valueOf( tree.getLinesVisible() ) );
    preserveItemMetrics( tree );
    adapter.preserve( PROP_COLUMN_COUNT, new Integer( tree.getColumnCount() ) );
    adapter.preserve( PROP_TREE_COLUMN, getTreeColumn( tree ) );
    adapter.preserve( PROP_ITEM_HEIGHT, new Integer( tree.getItemHeight() ) );
    adapter.preserve( PROP_SCROLL_LEFT, getScrollLeft( tree ) );
    adapter.preserve( PROP_TOP_ITEM_INDEX, new Integer( getTopItemIndex( tree ) ) );
    adapter.preserve( PROP_HAS_H_SCROLL_BAR, hasHScrollBar( tree ) );
    adapter.preserve( PROP_HAS_V_SCROLL_BAR, hasVScrollBar( tree ) );
    WidgetLCAUtil.preserveCustomVariant( tree );
  }

  public void readData( final Widget widget ) {
    Tree tree = ( Tree )widget;
    readSelection( tree );
    readScrollLeft( tree );
    readTopItemIndex( tree );
    processWidgetSelectedEvent( tree );
    processWidgetDefaultSelectedEvent( tree );
    ControlLCAUtil.processMouseEvents( tree );
    ControlLCAUtil.processKeyEvents( tree );
    ControlLCAUtil.processMenuDetect( tree );
    WidgetLCAUtil.processHelp( tree );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Tree tree = ( Tree )widget;
    JSWriter writer = JSWriter.getWriterFor( tree );
    writer.newWidget( "org.eclipse.rwt.widgets.Tree" );
    ControlLCAUtil.writeStyleFlags( tree );
    if( ( tree.getStyle() & SWT.MULTI ) != 0 ) {
      writer.set( "hasMultiSelection", true );
    }
    if( ( tree.getStyle() & SWT.FULL_SELECTION ) != 0 ) {
      writer.set( "hasFullSelection", true );
    } else {
      Rectangle textMargin = getTreeAdapter( tree ).getTextMargin();
      writer.set( "selectionPadding", new int[]{
        textMargin.x,
        textMargin.width - textMargin.x
      } );
    }
    if( ( tree.getStyle() & SWT.CHECK ) != 0 ) {
      writer.set( "hasCheckBoxes", true );
      writer.set( "checkBoxMetrics", new Object[]{
        new Integer( getTreeAdapter( tree ).getCheckLeft() ),
        new Integer( getTreeAdapter( tree ).getCheckWidth() )
      } );
    }
    if( ( tree.getStyle() & SWT.VIRTUAL ) != 0 ) {
      writer.set( "isVirtual", true );
    }
    writeIndentionWidth( tree );    
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Tree tree = ( Tree )widget;
    ControlLCAUtil.writeChanges( tree );
    writeItemHeight( tree );
    writeItemMetrics( tree );
    // NOTE : Client currently requires itemMetrics before columnCount
    writeColumnCount( tree );  
    writeLinesVisible( tree );
    writeTreeColumn( tree );   
    writeTopItem( tree );
    writeScrollBars( tree );
    updateSelectionListener( tree );
    writeHeaderHeight( tree );
    writeHeaderVisible( tree );
    writeScrollLeft( tree );
    WidgetLCAUtil.writeCustomVariant( tree );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
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
      int detail = "check".equals( detailStr )
                                              ? SWT.CHECK
                                              : SWT.NONE;
      int eventType = SelectionEvent.WIDGET_SELECTED;
      int stateMask
        = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
      SelectionEvent event = new SelectionEvent( tree,
                                                 treeItem,
                                                 eventType,
                                                 bounds,
                                                 stateMask,
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
      String itemId = request.getParameter( eventName + ".item" );
      Item treeItem = ( Item )WidgetUtil.find( tree, itemId );
      int eventType = SelectionEvent.WIDGET_DEFAULT_SELECTED;
      SelectionEvent event = new SelectionEvent( tree, treeItem, eventType );
      event.stateMask
        = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
      event.processEvent();
    }
  }

  /////////////////////////////////////////////
  // Helping methods to read client-side state

  private static void readSelection( final Tree tree ) {
    String value = WidgetLCAUtil.readPropertyValue( tree, "selection" );
    if( value != null ) {
      String[] values = value.split( "," );
      TreeItem[] selectedItems = new TreeItem[ values.length ];
      boolean validItemFound = false;
      for( int i = 0; i < values.length; i++ ) {
        selectedItems[ i ] = ( TreeItem )WidgetUtil.find( tree, values[ i ] );
        if( selectedItems[ i ] != null ) {
          validItemFound = true;
        }
      }
      if( !validItemFound ) {
        selectedItems = new TreeItem[ 0 ];
      }
      tree.setSelection( selectedItems );
    }
  }

  private static void readScrollLeft( final Tree tree ) {
    String left = WidgetLCAUtil.readPropertyValue( tree, "scrollLeft" );
    if( left != null ) {
      final ITreeAdapter treeAdapter = getTreeAdapter( tree );
      treeAdapter.setScrollLeft( parsePosition( left ) );
    }
  }

  private static void readTopItemIndex( final Tree tree ) {
    String topItemIndex = WidgetLCAUtil.readPropertyValue( tree, "topItemIndex" );
    if( topItemIndex != null ) {
      final ITreeAdapter treeAdapter = getTreeAdapter( tree );
      int newIndex = parsePosition( topItemIndex );
      treeAdapter.setTopItemIndex( newIndex );
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

  //////////////////////////////////////////////////////////////
  // Helping methods to write JavaScript for changed properties

  private static void writeItemHeight( final Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Integer newValue = new Integer( tree.getItemHeight( ) );
    if( WidgetLCAUtil.hasChanged( tree, PROP_ITEM_HEIGHT, newValue ) ) {
      writer.set( PROP_ITEM_HEIGHT, "itemHeight", newValue, new Integer( 16 ) );
    }
  }  
  
  public static void writeItemMetrics( final Tree tree )
    throws IOException 
  {
    ItemMetrics[] itemMetrics = getItemMetrics( tree );
    if( hasItemMetricsChanged( tree, itemMetrics ) ) {
      JSWriter writer = JSWriter.getWriterFor( tree );
      for( int i = 0; i < itemMetrics.length; i++ ) {
        Object[] args = new Object[] {
          new Integer( i ),
          new Integer( itemMetrics[ i ].left ),
          new Integer( itemMetrics[ i ].width ),
          new Integer( itemMetrics[ i ].imageLeft ),
          new Integer( itemMetrics[ i ].imageWidth ),
          new Integer( itemMetrics[ i ].textLeft ),
          new Integer( itemMetrics[ i ].textWidth )
        };
        writer.set( "itemMetrics", args );
      }
    }
  }
  public static void writeIndentionWidth( final Tree tree )
  throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( tree );
    ITreeAdapter treeAdapter = getTreeAdapter( tree );
    writer.set( "indentionWidth", treeAdapter.getIndentionWidth() );
  }
  
  private static void writeHeaderHeight( final Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Integer newValue = new Integer( tree.getHeaderHeight() );
    writer.set( PROP_HEADER_HEIGHT, "headerHeight", newValue, null );
  }  

  private void writeColumnCount( final Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Integer newValue = new Integer( tree.getColumnCount() );
    if( WidgetLCAUtil.hasChanged( tree, PROP_COLUMN_COUNT, newValue ) ) {
      writer.set( PROP_COLUMN_COUNT, "columnCount", newValue, new Integer( 0 ) );
    }
  }

  private void writeTreeColumn( final Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Integer newValue = getTreeColumn( tree );
    if( WidgetLCAUtil.hasChanged( tree, PROP_TREE_COLUMN, newValue ) ) {
      writer.set( PROP_TREE_COLUMN, "treeColumn", newValue, new Integer( 0 ) );
    }
  }
  
  private void writeTopItem( final Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Integer newValue = new Integer( getTopItemIndex( tree ) );
    if( WidgetLCAUtil.hasChanged( tree, PROP_TOP_ITEM_INDEX, newValue ) ) {
      writer.set( PROP_TOP_ITEM_INDEX, "topItemIndex", newValue, new Integer( 0 ) );
    }
  }

  private static void writeHeaderVisible( final Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Boolean newValue = Boolean.valueOf( tree.getHeaderVisible() );
    writer.set( PROP_HEADER_VISIBLE, "headerVisible", newValue, Boolean.FALSE );
  }

  private void writeScrollLeft( final Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Integer newValue = getScrollLeft( tree );
    writer.set( PROP_SCROLL_LEFT, "scrollLeft", newValue, DEFAULT_SCROLL_LEFT );
  }

  private static void writeScrollBars( final Tree tree ) throws IOException {
    boolean hasHChanged = WidgetLCAUtil.hasChanged( tree,
                                                    PROP_HAS_H_SCROLL_BAR,
                                                    hasHScrollBar( tree ),
                                                    Boolean.FALSE );
    boolean hasVChanged = WidgetLCAUtil.hasChanged( tree,
                                                    PROP_HAS_V_SCROLL_BAR,
                                                    hasVScrollBar( tree ),
                                                    Boolean.FALSE );
    if( hasHChanged || hasVChanged ) {
      boolean scrollX = hasHScrollBar( tree ).booleanValue();
      boolean scrollY = hasVScrollBar( tree ).booleanValue();
      JSWriter writer = JSWriter.getWriterFor( tree );
       writer.set( "scrollBarsVisible", new boolean[]{ scrollX, scrollY } );
    }
  }
  
  private static void writeLinesVisible( final Tree tree ) throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Boolean newValue = Boolean.valueOf( tree.getLinesVisible() );
    writer.set( PROP_LINES_VISIBLE, "linesVisible", newValue, Boolean.FALSE );
  }

  private static void updateSelectionListener( final Tree tree )
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( tree ) );
    String prop = PROP_SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( tree, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( tree );
      writer.set( "hasSelectionListeners", newValue );
    }
  }

  //////////////////
  // Helping methods

  private static Integer getScrollLeft( final Tree tree ) {
    ITreeAdapter treeAdapter = getTreeAdapter( tree );
    return new Integer( treeAdapter.getScrollLeft() );
  }

  private static int getTopItemIndex( final Tree tree ) {
    ITreeAdapter treeAdapter = getTreeAdapter( tree );
    return treeAdapter.getTopItemIndex();
  }
  
  private static Boolean hasHScrollBar( final Tree tree ) {
    ITreeAdapter treeAdapter = getTreeAdapter( tree );    
    return Boolean.valueOf( treeAdapter.hasHScrollBar() );
  }

  private static Boolean hasVScrollBar( final Tree tree ) {
    ITreeAdapter treeAdapter = getTreeAdapter( tree );
    return Boolean.valueOf( treeAdapter.hasVScrollBar() );
  }

  private static Integer getTreeColumn( final Tree tree ) {
    int[] values = tree.getColumnOrder();
    return new Integer( values.length > 0 ? values[ 0 ] : 0 );
  }
  
  private static ITreeAdapter getTreeAdapter( Tree tree ) {
    Object adapter = tree.getAdapter( ITreeAdapter.class );
    return ( ITreeAdapter )adapter;
  }

  /////////////////
  // Item Metrics:

  
  // TODO: merge with Table:
  static final class ItemMetrics {
    int left;
    int width;
    int imageLeft;
    int imageWidth;
    int textLeft;
    int textWidth;

    public boolean equals( final Object obj ) {
      boolean result;
      if( obj == this ) {
        result = true;
      } else  if( obj instanceof ItemMetrics ) {
        ItemMetrics other = ( ItemMetrics )obj;
        result =  other.left == left
               && other.width == width
               && other.imageLeft == imageLeft
               && other.imageWidth == imageWidth
               && other.textLeft == textLeft
               && other.textWidth == textWidth;
      } else {
        result = false;
      }
      return result;
    }

    public int hashCode() {
      String msg = "ItemMetrics#hashCode() not implemented";
      throw new UnsupportedOperationException( msg );
    }
  }


  public static ItemMetrics[] getItemMetrics( Tree tree ) {
    int columnCount = Math.max( 1, tree.getColumnCount() );
    ItemMetrics[] result = new ItemMetrics[ columnCount ];
    for( int i = 0; i < columnCount; i++ ) {
      result[ i ] = new ItemMetrics();
    }
    ITreeAdapter adapter = getTreeAdapter( tree );
    for( int i = 0; i < columnCount; i++ ) {
      result[ i ].left = adapter.getCellLeft( i );
      result[ i ].width = adapter.getCellWidth( i );
      result[ i ].imageLeft = result[ i ].left + adapter.getImageOffset( i );   
      result[ i ].imageWidth = adapter.getItemImageSize( i ).x;
      result[ i ].textLeft = result[ i ].left + adapter.getTextOffset( i );
      result[ i ].textWidth = adapter.getTextMaxWidth( i );
    }
    return result;
  }

  public static void preserveItemMetrics( final Tree tree ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( tree );
    adapter.preserve( PROP_ITEM_METRICS, getItemMetrics( tree ) );
  }

  private static boolean hasItemMetricsChanged( final Tree tree,
                                                final ItemMetrics[] metrics  )
  {
    return WidgetLCAUtil.hasChanged( tree, PROP_ITEM_METRICS, metrics );
  }  
}
