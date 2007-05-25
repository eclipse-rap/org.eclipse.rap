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

package org.eclipse.swt.internal.widgets.tableitemkit;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.tablekit.TableLCAUtil;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;
import com.w4t.engine.service.ContextProvider;

public final class TableItemLCA extends AbstractWidgetLCA {

  private static final String PROP_TOP = "top";
  private static final String PROP_TEXTS = "texts";
  private static final String PROP_CHECKED = "checked";
  
  public void preserveValues( final Widget widget ) {
    TableItem item = ( TableItem )widget;
    ItemLCAUtil.preserve( item );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    adapter.preserve( PROP_TOP, new Integer( item.getBounds().y ) );
    adapter.preserve( PROP_CHECKED, Boolean.valueOf( item.getChecked() ) );
    preserveTexts( item );
  }

  public void readData( final Widget widget ) {
    TableItem item = ( TableItem )widget;
    String value = WidgetLCAUtil.readPropertyValue( widget, "checked" );
    if( value != null ) {
      item.setChecked( Boolean.valueOf( value ).booleanValue() );
    }
    if( WidgetLCAUtil.wasEventSent( item, JSConst.EVENT_WIDGET_SELECTED ) ) {
      Table parent = item.getParent();
      int detail = getWidgetSelectedDetail();
      int id = SelectionEvent.WIDGET_SELECTED;
      SelectionEvent event = new SelectionEvent( parent, 
                                                 item, 
                                                 id, 
                                                 new Rectangle( 0, 0, 0, 0 ), 
                                                 "", 
                                                 true, 
                                                 detail );
      event.processEvent();
    }
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    TableItem item = ( TableItem )widget;
    JSWriter writer = JSWriter.getWriterFor( item );
    Table parent = item.getParent();
    int index = parent.indexOf( item );
    Object[] args = new Object[] { parent, new Integer( index ) };
    writer.newWidget( "org.eclipse.swt.widgets.TableItem", args );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    TableItem item = ( TableItem )widget;
    JSWriter writer = JSWriter.getWriterFor( item );
    if( itemContentChanged( item ) ) {
      int columnCount = item.getParent().getColumnCount();
      if( columnCount == 0 ) {
        columnCount = 1;
      }
      String[] texts = new String[ columnCount ];
      for( int i = 0; i < columnCount; i++ ) {
        // TODO [rh] for some reason doesn't work with escapeText
//        texts[ i ] = WidgetLCAUtil.escapeText( item.getText( i ), false );
        texts[ i ] = encodeHTML( item.getText( i ) );
      }
      writer.set( "texts", new Object[] { texts } );
    }
    writeChecked( item );
  }

  private void writeChecked( final TableItem item )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( item );
    Boolean newValue = Boolean.valueOf( item.getChecked() );
    writer.set( PROP_CHECKED, "checked", newValue, Boolean.FALSE );
  }
  
  private static String encodeHTML( final String text ) {
    String result = text.replaceAll( "\"", "&#034;" );
    result = result.replaceAll( ">", "&#062;" );
    result = result.replaceAll( "<", "&#060;" );
    return result;
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
  
  ////////////////////////////////////
  // Change detection for item content
  
  static boolean itemContentChanged( final TableItem item ) {
    boolean result;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    if( adapter.isInitialized() ) {
      result = false;
      Table table = item.getParent();
      int columnCount = table.getColumnCount();
      if( columnCount == 0 ) {
        columnCount = 1;
      }
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
    } else {
      result = true;
    }
    return result;
  }
  
  //////////////////
  // ReadData helper
  
  private static int getWidgetSelectedDetail() {
    int result = SWT.NONE;
    HttpServletRequest request = ContextProvider.getRequest();
    String value = request.getParameter( JSConst.EVENT_WIDGET_SELECTED_DETAIL );
    if( "check".equals( value ) ) {
      result = SWT.CHECK;
    }
    return result;
  }
}
