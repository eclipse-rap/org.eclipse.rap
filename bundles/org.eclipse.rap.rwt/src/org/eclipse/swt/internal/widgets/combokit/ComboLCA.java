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

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class ComboLCA extends AbstractWidgetLCA {

  private static final String QX_TYPE = "qx.ui.form.ComboBox";

  private static final Pattern NEWLINE_PATTERN
    = Pattern.compile( "\\r\\n|\\r|\\n" );

  private static final String[] DEFAUT_ITEMS = new String[ 0 ];
  private static final Integer DEFAULT_SELECTION = new Integer( -1 );

  // Constants for ComboUtil.js
  private static final String JS_FUNC_WIDGET_SELECTED =
    "org.eclipse.swt.ComboUtil.onSelectionChanged";
  private static final String JS_FUNC_CREATE_COMBOBOX_ITEMS =
    "org.eclipse.swt.ComboUtil.createComboBoxItems";
  private static final String JS_FUNC_SELECT_COMBOBOX_ITEM =
    "org.eclipse.swt.ComboUtil.select";
  private static final String JS_FUNC_INITIALIZE =
    "org.eclipse.swt.ComboUtil.initialize";
  private static final String JS_FUNC_DEINITIALIZE
    = "org.eclipse.swt.ComboUtil.deinitialize";

  // Propery names for preserve-value facility
  private static final String PROP_ITEMS = "items";
  private static final String PROP_SELECTION = "selection";
  private static final String PROP_EDITABLE = "editable";
  private static final String PROP_MODIFY_LISTENER = "modifyListener";

  private static final JSListenerInfo JS_LISTENER_INFO
    = new JSListenerInfo( JSConst.QX_EVENT_CHANGE_SELECTED,
                          JS_FUNC_WIDGET_SELECTED,
                          JSListenerType.STATE_AND_ACTION );
  private static final JSListenerInfo JS_MODIFY_LISTENER_INFO
    = new JSListenerInfo( JSConst.QX_EVENT_KEY_UP,
                        "org.eclipse.swt.ComboUtil.modifyText",
                        JSListenerType.STATE_AND_ACTION );
  private static final JSListenerInfo JS_BLUR_LISTENER_INFO
    = new JSListenerInfo( JSConst.QX_EVENT_BLUR,
                        "org.eclipse.swt.ComboUtil.modifyTextOnBlur",
                        JSListenerType.ACTION );

//  private static final String TYPE_POOL_ID = ComboLCA.class.getName();


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
    adapter.preserve( PROP_MODIFY_LISTENER,
                      Boolean.valueOf( ModifyEvent.hasListener( combo ) ) );
    adapter.preserve( PROP_EDITABLE, Boolean.valueOf( 
                      (combo.getStyle() & SWT.READ_ONLY ) == 0 ) );
  }

  public void readData( final Widget widget ) {
    Combo combo = ( Combo )widget;
    String value = WidgetLCAUtil.readPropertyValue( widget, "selectedItem" );
    if( value != null ) {
      combo.select( new Integer( value ).intValue() );
    }
    String text = WidgetLCAUtil.readPropertyValue( widget, "text" );
    if( text != null ) {
      combo.setText( text );
    }
    if( WidgetLCAUtil.wasEventSent( combo, JSConst.EVENT_WIDGET_SELECTED ) ) {
      ControlLCAUtil.processSelection( combo, null, true );
    }
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( QX_TYPE );
    writer.callStatic( JS_FUNC_INITIALIZE, new Object[] { widget } );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    Combo combo = ( Combo )widget;
    ControlLCAUtil.writeChanges( combo );
    writeItems( combo );
    writeSelected( combo );
    writeEditable( combo );
    writeModifyListener( combo );

    // workaround for broken context menu on qx ComboBox
    // see http://bugzilla.qooxdoo.org/show_bug.cgi?id=465
    Menu menu = combo.getMenu();
    if( WidgetLCAUtil.hasChanged( widget, Props.MENU, menu , null ) ) {
      Object[] args = new Object[] { combo };
      writer.callStatic( "org.eclipse.swt.ComboUtil.applyContextMenu", args );
    }
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.callStatic( JS_FUNC_DEINITIALIZE, new Object[] { widget } );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    // TODO [rh] how to pass widget to static function in reset-handler?
//    writer.callStatic( JS_FUNC_DEINITIALIZE, new Object[] { ??? } );
    writer.call( "removeAll", null );
  }

  public String getTypePoolId( final Widget widget ) throws IOException {
//    TODO [rst] Enable pooling when re-parenting problems with
//               qx.ui.form.ComboBox are solved
//    return TYPE_POOL_ID;
    return null;
  }

  private static void writeItems( final Combo combo ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( combo );
    String[] items = combo.getItems();
    if( WidgetLCAUtil.hasChanged( combo, PROP_ITEMS, items, DEFAUT_ITEMS ) ) {
      // Convert newlines into whitespaces
      for( int i = 0; i < items.length; i++ ) {
        Matcher matcher = NEWLINE_PATTERN.matcher( items[ i ] );
        items[ i ] = matcher.replaceAll( " " );
        items[ i ] = WidgetLCAUtil.escapeText( items[ i ], false );
      }
      Object[] args = new Object[]{ combo, items };
      writer.callStatic( JS_FUNC_CREATE_COMBOBOX_ITEMS, args );
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
      Object[] args = new Object[]{ combo, newValue };
      writer.callStatic( JS_FUNC_SELECT_COMBOBOX_ITEM, args );
    }
  }
  
  private static void writeEditable( final Combo combo ) throws IOException {
    boolean editable = ( ( combo.getStyle() & SWT.READ_ONLY ) == 0 );
    if( editable ) {
      JSWriter writer = JSWriter.getWriterFor( combo );
      writer.set( PROP_EDITABLE, "editable", Boolean.valueOf( editable ), null );
    }
  }
  
  static void writeModifyListener( final Combo combo ) throws IOException {
    if( ( combo.getStyle() & SWT.READ_ONLY ) == 0 ) {
      JSWriter writer = JSWriter.getWriterFor( combo );
      boolean hasListener = ModifyEvent.hasListener( combo );
      writer.updateListener( JS_MODIFY_LISTENER_INFO,
                             PROP_MODIFY_LISTENER,
                             hasListener );
      writer.updateListener( JS_BLUR_LISTENER_INFO,
              				 PROP_MODIFY_LISTENER,
              				 hasListener );
    }
  }
}
