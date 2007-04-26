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

package org.eclipse.swt.internal.widgets.tablecolumnkit;

import java.io.IOException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;

public final class TableColumnLCA extends AbstractWidgetLCA {

  // Property names to preserve values
  private static final String PROP_LEFT = "left";
  private static final String PROP_WIDTH = "width";
  private static final String PROP_Z_INDEX = "zIndex";
  private static final String PROP_FONT = "font";
  private static final String PROP_SELECTION_LISTENERS = "selectionListeners";
  
  private static final Integer DEFAULT_LEFT = new Integer( 0 );
  
  private static final JSListenerInfo SELECTION_LISTENER
    = new JSListenerInfo( "click", "this.onClick", JSListenerType.ACTION );
  
  public void preserveValues( final Widget widget ) {
    TableColumn column = ( TableColumn )widget;
    ItemLCAUtil.preserve( column );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( column );
    adapter.preserve( PROP_Z_INDEX, new Integer( getZIndex( column ) ) );
    adapter.preserve( PROP_LEFT, new Integer( getLeft( column ) ) );
    adapter.preserve( PROP_WIDTH, new Integer( column.getWidth() ) );
    adapter.preserve( PROP_FONT, column.getParent().getFont() );
    adapter.preserve( PROP_SELECTION_LISTENERS, 
                      Boolean.valueOf( SelectionEvent.hasListener( column ) ) );
  }
  
  public void readData( final Widget widget ) {
    final TableColumn column = ( TableColumn )widget;
    // Though there is sent an event parameter called 
    // org.eclipse.swt.events.controlResized
    // we will ignore it since setting the new width itself fires the 
    // desired controlResized-event
    String value = WidgetLCAUtil.readPropertyValue( column, "width" );
    if( value != null ) {
      // TODO [rh] HACK: force width to have changed when client-side changes
      //      it. Since this is done while a column resize we must re-layout
      //      all columns including the resized one.
      final int newWidth = Integer.parseInt( value );
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          column.setWidth( newWidth );
        }
      } );
    }
    ControlLCAUtil.processSelection( column, null, false );
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    TableColumn column = ( TableColumn )widget;
    JSWriter writer = JSWriter.getWriterFor( column );
    Object[] args = new Object[] { column.getParent() };
    writer.newWidget( "org.eclipse.swt.widgets.TableColumn", args );
    // Keep this order: initialize must be called after setParent  
  }

  public void renderChanges( final Widget widget ) throws IOException {
    TableColumn column = ( TableColumn )widget;
    ItemLCAUtil.writeChanges( column );
    writeLeft( column );
    writeWidth( column );
    writeZIndex( column );
    WidgetLCAUtil.writeFont( column, column.getParent().getFont() );
    writeSelectionListener( column );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    TableColumn column = ( TableColumn )widget;
    JSWriter writer = JSWriter.getWriterFor( column );
    writer.dispose();
  }

  //////////////////////////////////////////
  // Helping method to write JavaScript code
  
  private static void writeLeft( final TableColumn column ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( column );
    Integer newValue = new Integer( getLeft( column ) );
    writer.set( PROP_LEFT, "left", newValue, DEFAULT_LEFT );
  }

  private static void writeWidth( final TableColumn column ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( column );
    Integer newValue = new Integer( column.getWidth() );
    writer.set( PROP_WIDTH, "width", newValue, null );
  }

  private static void writeZIndex( final TableColumn column ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( column );
    Integer newValue = new Integer( getZIndex( column ) );
    writer.set( PROP_Z_INDEX, "zIndex", newValue, null );
  }

  // TODO [rh] selection event is also fired when resizing columns!
  private static void writeSelectionListener( final TableColumn column )
    throws IOException
  {
    // TODO [rh] dispose of selection listener when widget is disposed of
    JSWriter writer = JSWriter.getWriterFor( column );
    writer.updateListener( SELECTION_LISTENER, 
                           PROP_SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( column ) );
  }

  //////////////////////////////////////////////////
  // Helping methods to obtain calculated properties
  
  private static int getLeft( final TableColumn column ) {
    int result = 0;
    Table table = column.getParent();
    int index = table.indexOf( column );
    for( int i = 0; i < index; i++ ) {
      result += table.getColumn( i ).getWidth();
    }
    return result;
  }
  
  private static int getZIndex( final TableColumn column ) {
    return ControlLCAUtil.getZIndex( column.getParent() ) + 1;
  }
}
