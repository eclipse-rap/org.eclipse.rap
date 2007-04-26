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

package org.eclipse.swt.internal.widgets.combokit;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Widget;

public class ComboLCA extends AbstractWidgetLCA {
  
  private static final Pattern NEWLINE_PATTERN = Pattern.compile( "\n" );

  private static final String[] DEFAUT_ITEMS = new String[ 0 ];
  private static final Integer DEFAULT_SELECTION = new Integer( -1 );
  
  // Constants for ComboUtil.js
  private static final String WIDGET_SELECTED = 
    "org.eclipse.swt.ComboUtil.widgetSelected";
  private static final String CREATE_COMBOBOX_ITEMS = 
    "org.eclipse.swt.ComboUtil.createComboBoxItems";
  private static final String SELECT_COMBOBOX_ITEM = 
    "org.eclipse.swt.ComboUtil.selectComboBoxItem";
  private static final String PROP_ITEMS = "items";
  private static final String PROP_SELECTION = "selection";
  
  private static final JSListenerInfo JS_LISTENER_INFO 
    = new JSListenerInfo( JSConst.QX_EVENT_CHANGE_SELECTED, 
                          WIDGET_SELECTED, 
                          JSListenerType.ACTION );


  public void preserveValues( final Widget widget ) {
    Combo combo = ( Combo )widget;
    ControlLCAUtil.preserveValues( combo );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    String[] items = combo.getItems();
    adapter.preserve( PROP_ITEMS, items );
    Integer selection = new Integer( combo.getSelectionIndex() );
    adapter.preserve( PROP_SELECTION, selection );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( SelectionEvent.hasListener( combo ) ) );
  }
  
  public void readData( final Widget widget ) {
    Combo combo = ( Combo )widget;
    String value = WidgetLCAUtil.readPropertyValue( widget, "selectedItem" );
    if( value != null ) {
      combo.select( new Integer( value ).intValue() );
    }
    if( WidgetLCAUtil.wasEventSent( combo, JSConst.EVENT_WIDGET_SELECTED ) ) {
      ControlLCAUtil.processSelection( combo, null, true );
    }
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "qx.ui.form.ComboBox" );
    writer.addListener( null,
                        "changeEnabled",
                        "org.eclipse.swt.ComboUtil.enablementChanged" );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Combo combo = ( Combo )widget;
    ControlLCAUtil.writeChanges( combo );
    writeItems( combo );
    writeSelected( combo );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  private static void writeItems( final Combo combo ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( combo );
    String[] items = combo.getItems();
    if( WidgetLCAUtil.hasChanged( combo, PROP_ITEMS, items, DEFAUT_ITEMS ) ) {
      // Convert newlines into whitespaces
      for( int i = 0; i < items.length; i++ ) {
        Matcher matcher = NEWLINE_PATTERN.matcher( items[ i ] );
        items[ i ] = matcher.replaceAll( " " );
      }
      Object[] args = new Object[]{ WidgetUtil.getId( combo ), items };
      writer.callStatic( CREATE_COMBOBOX_ITEMS, args );
    }
    writer.updateListener( JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( combo ) );
  }
  
  private static void writeSelected( final Combo combo ) throws IOException {
    Integer newValue = new Integer( combo.getSelectionIndex() );
    if( WidgetLCAUtil.hasChanged( combo,
                                  PROP_SELECTION,
                                  newValue,
                                  DEFAULT_SELECTION ) )
    {
      JSWriter writer = JSWriter.getWriterFor( combo );
      Object[] args = new Object[]{ WidgetUtil.getId( combo ), newValue };
      writer.callStatic( SELECT_COMBOBOX_ITEM, args );
    }
  }
}
