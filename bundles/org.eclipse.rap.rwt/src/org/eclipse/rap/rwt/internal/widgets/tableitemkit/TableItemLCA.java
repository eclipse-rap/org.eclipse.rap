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

package org.eclipse.rap.rwt.internal.widgets.tableitemkit;

import java.io.IOException;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.ItemLCAUtil;
import org.eclipse.rap.rwt.internal.widgets.tablekit.TableLCAUtil;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.HTML;
import com.w4t.RenderUtil;

public final class TableItemLCA extends AbstractWidgetLCA {

  private static final String PROP_TOP = "top";
  private static final String PROP_TEXTS = "tests";
  private static final String PROP_BOUNDS = "bounds";
  
  public void preserveValues( final Widget widget ) {
    TableItem item = ( TableItem )widget;
    ItemLCAUtil.preserve( item );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    adapter.preserve( PROP_TOP, new Integer( item.getBounds().y ) );
    preserveTexts( item );
    preserveBounds( item );
  }

  public void readData( final Widget widget ) {
    TableItem item = ( TableItem )widget;
    if( WidgetLCAUtil.wasEventSent( item, JSConst.EVENT_WIDGET_SELECTED ) ) {
      SelectionEvent event = new SelectionEvent( item.getParent(), item, SelectionEvent.WIDGET_SELECTED );
      event.processEvent();
    }
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    TableItem item = ( TableItem )widget;
    JSWriter writer = JSWriter.getWriterFor( item );
    Table parent = item.getParent();
    int index = parent.indexOf( item );
    Object[] args = new Object[] { parent, new Integer( index ) };
    writer.newWidget( "org.eclipse.rap.rwt.widgets.TableItem", args );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    TableItem item = ( TableItem )widget;
    JSWriter writer = JSWriter.getWriterFor( item );
    if( itemContentChanged( item ) ) {
      int columnCount = item.getParent().getColumnCount();
      for( int i = 0; i < columnCount; i++ ) {
        Object[] args = new Object[] {
          new Integer( i ),
          encodeHTML( item.getText( i ) )
        };
        writer.set( "text", args );
      }
    }
  }
  
  /* (intentionally not JavaDoc'ed)
   * The client-side representation of a TableItem is not a qooxdoo widget.
   * Therefore the standard mechanism for dispoing of a widget is not used.
   */
  public void renderDispose( final Widget widget ) throws IOException {
    TableItem item = ( TableItem )widget;
    JSWriter writer = JSWriter.getWriterFor( item );
    writer.call( "dispose", null );
  }

  //////////////////////////
  // Preserve helper methods
  
  private static void preserveTexts( final TableItem item ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    String[] texts = new String[ item.getParent().getColumnCount() ];
    for( int i = 0; i < item.getParent().getColumnCount(); i++ ) {
      texts[i] = item.getText( i );
    }
    adapter.preserve( PROP_TEXTS, texts );
  }
  
  private void preserveBounds( final TableItem item ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    Rectangle[] texts = new Rectangle[ item.getParent().getColumnCount() ];
    for( int i = 0; i < item.getParent().getColumnCount(); i++ ) {
      texts[i] = item.getBounds( i );
    }
    adapter.preserve( PROP_BOUNDS, texts );
  }

  ////////////////////////////////////
  // Change detection for item content
  
  static boolean itemContentChanged( final TableItem item ) {
    boolean result;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    if( adapter.isInitialized() ) {
      result = false;
      Table table = item.getParent();
      int columnCount = table.getColumnCount();
      // Has column count changed?
      if( !result ) {
        int preservedColCount = TableLCAUtil.getPreservedColumnCount( table );
        if( preservedColCount != columnCount ) {
          result = true;
        }
      }
      // Has one of the texts changed?
      if( !result ) {
        String[] preservedTexts 
          = ( String[] )adapter.getPreserved( PROP_TEXTS );
        for( int i = 0; !result && i < columnCount; i++ ) {
          if( !item.getText( i ).equals( preservedTexts[ i ] ) ) {
            result = true;
          }
        }
      }
      // Has one of the bounds changed?
      if( !result ) {
        Rectangle[] preservedBounds 
        = ( Rectangle[] )adapter.getPreserved( PROP_BOUNDS );
        for( int i = 0; !result && i < columnCount; i++ ) {
          if( !item.getBounds( i ).equals( preservedBounds[ i ] ) ) {
            result = true;
          }
        }
      }
    } else {
      result = true;
    }
    return result;
  }
  
  private static String encodeHTML( final String text ) {
    String result = text.replaceAll( "\"", "&#034;" );
    result = result.replaceAll( ">", "&#062;" );
    result = result.replaceAll( "<", "&#060;" );
    return result;
  }
}
