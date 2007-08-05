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
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.tablekit.TableLCAUtil;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;

import com.w4t.engine.service.ContextProvider;

public final class TableItemLCA extends AbstractWidgetLCA {

  private static final String PROP_TOP = "top";
  private static final String PROP_TEXTS = "texts";
  private static final String PROP_IMAGES = "images";
  private static final String PROP_CHECKED = "checked";
  private static final String PROP_GRAYED = "grayed";
  private static final String PROP_SELECTED = "selected";
  private static final String PROP_FOCUSED = "focused";
  private static final String PROP_FONT = "font";
  private static final String PROP_BACKGROUND = "background";
  private static final String PROP_FOREGROUND = "foreground";
  
  public void preserveValues( final Widget widget ) {
    TableItem item = ( TableItem )widget;
    ItemLCAUtil.preserve( item );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    adapter.preserve( PROP_TOP, new Integer( item.getBounds().y ) );
    adapter.preserve( PROP_CHECKED, Boolean.valueOf( item.getChecked() ) );
    adapter.preserve( PROP_GRAYED, Boolean.valueOf( item.getGrayed() ) );
    adapter.preserve( PROP_TEXTS, getTexts( item ) );
    adapter.preserve( PROP_IMAGES, getImages( item ) );
    adapter.preserve( PROP_SELECTED, Boolean.valueOf( isSelected( item ) ) );
    adapter.preserve( PROP_FOCUSED, Boolean.valueOf( isFocused( item ) ) );
    adapter.preserve( PROP_FONT, getFonts( item ) );
    adapter.preserve( PROP_BACKGROUND, getBackgrounds( item ) );
    adapter.preserve( PROP_FOREGROUND, getForegrounds( item ) );
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
    String defaultSelectedParam = JSConst.EVENT_WIDGET_DEFAULT_SELECTED;
    if( WidgetLCAUtil.wasEventSent( item, defaultSelectedParam ) ) {
      Table parent = item.getParent();
      int id = SelectionEvent.WIDGET_DEFAULT_SELECTED;
      SelectionEvent event = new SelectionEvent( parent, item, id );
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
    Table table = item.getParent();
    boolean needUpdate = false;
    needUpdate |= writeTexts( item );
    needUpdate |= writeImages( item );
    needUpdate |= writeFont( item );
    needUpdate |= writeBackground( item );
    needUpdate |= writeForeground( item );
    needUpdate |= writeChecked( item );
    needUpdate |= writeGrayed( item );
    needUpdate |= writeSelection( item );
    if( isVisible( item ) ) {
      needUpdate |= TableLCAUtil.hasItemMetricsChanged( table );
      needUpdate |= TableLCAUtil.hasAlignmentChanged( table );
    }
    if( needUpdate ) {
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.call( "update", null );
    }
    writeFocused( item );
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

  public void createResetHandlerCalls( final String typePoolId ) throws IOException {
  }
  
  public String getTypePoolId( final Widget widget ) throws IOException {
    return null;
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

  ///////////////////////
  // RenderChanges helper
  
  private static boolean writeTexts( final TableItem item ) throws IOException {
    String[] texts = getTexts( item );
    boolean result = WidgetLCAUtil.hasChanged( item, PROP_TEXTS, texts );
    if( result ) {
      for( int i = 0; i < texts.length; i++ ) {
        // TODO [rh] for some reason doesn't work with escapeText
//        texts[ i ] = WidgetLCAUtil.escapeText( item.getText( i ), false );
        texts[ i ] = encodeHTML( item.getText( i ) );
      }
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( "texts", new Object[] { texts } );
    }
    return result;
  }
  
  private static boolean writeImages( final TableItem item ) throws IOException 
  {
    Image[] images = getImages( item );
    boolean result = WidgetLCAUtil.hasChanged( item, PROP_IMAGES, images );
    if( result ) {
      JSWriter writer = JSWriter.getWriterFor( item );
      String[] imagePaths = new String[ images.length ];
      for( int i = 0; i < imagePaths.length; i++ ) {
        imagePaths[ i ] = Image.getPath( images[ i ] );
      }
      writer.set( "images", new Object[] { imagePaths } );
    }
    return result;
  }

  private static boolean writeFont( final TableItem item ) throws IOException {
    Font[] fonts = getFonts( item );
    Font[] defValue = new Font[ fonts.length ];
    for( int i = 0; i < defValue.length; i++ ) {
      Font parentFont = item.getParent().getFont();
      defValue[ i ] = parentFont;
    }
    boolean result 
      = WidgetLCAUtil.hasChanged( item, PROP_FONT, fonts, defValue );
    if( result ) {
      String[] css = new String[ fonts.length ];
      for( int i = 0; i < fonts.length; i++ ) {
        css[ i ] = toCss( fonts[ i ] );
System.out.println( css[ i ] );      
      }
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( "fonts", new Object[] { css } );
    }
    return result;
  }

  private boolean writeBackground( final TableItem item ) throws IOException {
    Color[] backgrounds = getBackgrounds( item );
    Color parentBackground = item.getParent().getBackground();
    Color[] defValue = new Color[ getColumnCount( item ) ];
    for( int i = 0; i < defValue.length; i++ ) {
      defValue[ i ] = parentBackground;
    }
    JSWriter writer = JSWriter.getWriterFor( item );
    return writer.set( PROP_BACKGROUND, "backgrounds", backgrounds, defValue ); 
  }

  private boolean writeForeground( final TableItem item ) throws IOException {
    Color[] foregrounds = getForegrounds( item );
    Color parentForeground = item.getParent().getForeground();
    Color[] defValue = new Color[ getColumnCount( item ) ];
    for( int i = 0; i < defValue.length; i++ ) {
      defValue[ i ] = parentForeground;
    }
    JSWriter writer = JSWriter.getWriterFor( item );
    return writer.set( PROP_FOREGROUND, "foregrounds", foregrounds, defValue ); 
  }
  
  private static boolean writeChecked( final TableItem item )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( item );
    Boolean newValue = Boolean.valueOf( item.getChecked() );
    return writer.set( PROP_CHECKED, "checked", newValue, Boolean.FALSE );
  }
  
  private static boolean writeGrayed( final TableItem item ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( item );
    Boolean newValue = Boolean.valueOf( item.getGrayed() );
    return writer.set( PROP_GRAYED, "grayed", newValue, Boolean.FALSE );
  }
  
  private static boolean writeSelection( final TableItem item ) 
    throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( item );
    Boolean newValue = Boolean.valueOf( isSelected( item ) );
    return writer.set( PROP_SELECTED, "selection", newValue, Boolean.FALSE );
  }

  // TODO [rh] check if necessary to honor focusIndex == -1, would mean to
  //      call jsTable.setFocusedItem( null ) in TableLCA
  private static void writeFocused( final TableItem item ) throws IOException 
  {
    Boolean newValue = Boolean.valueOf( isFocused( item ) );
    Boolean defValue = Boolean.FALSE;
    if(    newValue.booleanValue() 
        && WidgetLCAUtil.hasChanged( item, PROP_FOCUSED, newValue, defValue ) ) 
    {
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.call( "focus", null );
    }
  }

  private static String encodeHTML( final String text ) {
    String result = text.replaceAll( "\"", "&#034;" );
    result = result.replaceAll( ">", "&#062;" );
    result = result.replaceAll( "<", "&#060;" );
    return result;
  }
  
  private static String toCss( final Font font ) {
    StringBuffer result = new StringBuffer();
    FontData fontData = font.getFontData()[ 0 ];
    if( ( fontData.getStyle() & SWT.ITALIC ) != 0 ) {
      result.append( "italic " );
    }
    if( ( fontData.getStyle() & SWT.BOLD ) != 0 ) {
      result.append( "bold " );
    }
    result.append( fontData.getHeight() );
    result.append( "px " );
    // TODO [rh] preliminary: low budget font-name-escaping
    String escapedName = fontData.getName().replaceAll( "\"", "" );
    result.append( escapedName );
    return result.toString();
  }

  //////////////////////
  // Item data accessors
  
  private static String[] getTexts( final TableItem item ) {
    int columnCount = getColumnCount( item );
    String[] result = new String[ columnCount ];
    for( int i = 0; i < columnCount; i++ ) {
      result[ i ] = item.getText( i );
    }
    return result;
  }

  private static Image[] getImages( final TableItem item ) {
    int columnCount = getColumnCount( item );
    Image[] result = new Image[ columnCount ];
    for( int i = 0; i < columnCount; i++ ) {
      result[ i ] = item.getImage( i );
    }
    return result;
  }
  
  private static Font[] getFonts( final TableItem item ) {
    int columnCount = getColumnCount( item );
    Font[] result = new Font[ columnCount ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = item.getFont();
    }
    return result;
  }
  
  private static Color[] getBackgrounds( final TableItem item ) {
    int columnCount = getColumnCount( item );
    Color[] result = new Color[ columnCount ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = item.getBackground();
    }
    return result;
  }

  private static Color[] getForegrounds( final TableItem item ) {
    int columnCount = getColumnCount( item );
    Color[] result = new Color[ columnCount ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = item.getForeground();
    }
    return result;
  }

  private static int getColumnCount( final TableItem item ) {
    return Math.max( 1, item.getParent().getColumnCount() );
  }
  
  private static boolean isSelected( final TableItem item ) {
    Table table = item.getParent();
    int index = table.indexOf( item );
    return index != -1 && table.isSelected( index );
  }

  private static boolean isFocused( final TableItem item ) {
    Table table = item.getParent();
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    int focusIndex = tableAdapter.getFocusIndex();
    return focusIndex != -1 && item == table.getItem( focusIndex ); 
  }

  private static boolean isVisible( final TableItem item ) {
    Object adapter = item.getParent().getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    return tableAdapter.isItemVisible( item );
  }
}
