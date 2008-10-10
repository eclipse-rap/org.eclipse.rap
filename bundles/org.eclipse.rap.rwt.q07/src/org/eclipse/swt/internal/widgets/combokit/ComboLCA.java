/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

/**
 * Life cycle adapter for Combo widgets.
 */
public class ComboLCA extends AbstractWidgetLCA {

  private static final String QX_TYPE = "org.eclipse.swt.widgets.Combo";

  private static final String[] DEFAUT_ITEMS = new String[ 0 ];
  private static final Integer DEFAULT_SELECTION = new Integer( -1 );

  // Must be in sync with appearance "list-item"
  private static final int LIST_ITEM_PADDING = 3;

  // Constants for ComboUtil.js
  private static final String JS_FUNC_WIDGET_SELECTED =
    "org.eclipse.swt.ComboUtil.onSelectionChanged";
  private static final String JS_FUNC_SET_ITEMS = "rwt_setItems";
  private static final String JS_FUNC_SELECT = "rwt_select";
  private static final String JS_FUNC_SET_MAX_POPUP_HEIGHT
    = "rwt_setMaxPopupHeight";
  private static final String JS_FUNC_APPLY_CONTEXT_MENU
    = "rwt_applyContextMenu";

  // Property names for preserve-value facility
  private static final String PROP_ITEMS = "items";
  private static final String PROP_TEXT = "text";
  private static final String PROP_SELECTION = "selection";
  static final String PROP_EDITABLE = "editable";
  static final String PROP_VERIFY_MODIFY_LISTENER
    = "verifyModifyListener";
  static final String PROP_MAX_POPUP_HEIGHT = "maxPopupHeight";

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
    adapter.preserve( PROP_MAX_POPUP_HEIGHT,
                      new Integer( getMaxPopupHeight( combo ) ) );
    adapter.preserve( PROP_TEXT, combo.getText() );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( SelectionEvent.hasListener( combo ) ) );
    adapter.preserve( PROP_EDITABLE, Boolean.valueOf( isEditable( combo ) ) );
    boolean hasVerifyListener = VerifyEvent.hasListener( combo );
    boolean hasModifyListener = ModifyEvent.hasListener( combo );
    boolean hasListener = hasVerifyListener || hasModifyListener;
    adapter.preserve( PROP_VERIFY_MODIFY_LISTENER,
                      Boolean.valueOf( hasListener ) );
    WidgetLCAUtil.preserveCustomVariant( combo );
  }

  public void readData( final Widget widget ) {
    final Combo combo = ( Combo )widget;
    String value = WidgetLCAUtil.readPropertyValue( widget, "selectedItem" );
    if( value != null ) {
      combo.select( new Integer( value ).intValue() );
    }
    readText( combo );
    if( WidgetLCAUtil.wasEventSent( combo, JSConst.EVENT_WIDGET_SELECTED ) ) {
      ControlLCAUtil.processSelection( combo, null, true );
    }
    ControlLCAUtil.processMouseEvents( combo );
    ControlLCAUtil.processKeyEvents( combo );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( QX_TYPE );    
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Combo combo = ( Combo )widget;
    ControlLCAUtil.writeChanges( combo );
    writeItems( combo );
    writeSelectionListener( combo );
    writeSelection( combo );
    writeMaxPopupHeight( combo );
    writeEditable( combo );
    writeText( combo );
    writeVerifyAndModifyListener( combo );
    // workaround for broken context menu on qx ComboBox
    // see http://bugzilla.qooxdoo.org/show_bug.cgi?id=465
    Menu menu = combo.getMenu();
    if( WidgetLCAUtil.hasChanged( widget, Props.MENU, menu, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( widget );
      writer.call( JS_FUNC_APPLY_CONTEXT_MENU, new Object[ 0 ] );
    }
    WidgetLCAUtil.writeCustomVariant( combo );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.call( "removeAll", null );
  }

  public String getTypePoolId( final Widget widget ) {
//    TODO [rst] Enable pooling when re-parenting problems with
//               qx.ui.form.ComboBox are solved
//    return TYPE_POOL_ID;
    return null;
  }

  ///////////////////////////////////////
  // Helping methods to read client state

  private static void readText( final Combo combo ) {
    final String value = WidgetLCAUtil.readPropertyValue( combo, "text" );
    if( value != null ) {
      if( VerifyEvent.hasListener( combo ) ) {
        // setText needs to be executed in a ProcessAcction runnable as it may
        // fire a VerifyEvent whose fields (text and doit) need to be evaluated
        // before actually setting the new value
        ProcessActionRunner.add( new Runnable() {
          public void run() {
            combo.setText( value );
            // Reset preserved value in case the values wasn't set as-is as this
            // means that a VerifyListener manipulated or rejected the value
            if( !value.equals( combo.getText() ) ) {
              IWidgetAdapter adapter = WidgetUtil.getAdapter( combo );
              adapter.preserve( PROP_TEXT, null );
            }
          }
        } );
      } else {
        combo.setText( value );
      }
    }
  }

  //////////////////////////////////////////////
  // Helping methods to write changed properties

  private static void writeItems( final Combo combo ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( combo );
    String[] items = combo.getItems();
    if( WidgetLCAUtil.hasChanged( combo, PROP_ITEMS, items, DEFAUT_ITEMS ) ) {
      // Convert newlines into whitespaces
      for( int i = 0; i < items.length; i++ ) {
        items[ i ] = WidgetLCAUtil.replaceNewLines( items[ i ], " " );
        items[ i ] = WidgetLCAUtil.escapeText( items[ i ], false );
      }
      writer.call( JS_FUNC_SET_ITEMS, new Object[] { items } );
    }
  }

  private static void writeSelection( final Combo combo ) throws IOException {
    Integer newValue = new Integer( combo.getSelectionIndex() );
    Integer defValue = DEFAULT_SELECTION;
    boolean selectionChanged
      = WidgetLCAUtil.hasChanged( combo, PROP_SELECTION, newValue, defValue );
    // The 'textChanged' statement covers the following use case:
    // combo.add( "a" );  combo.select( 0 );
    // -- in a subsequent request --
    // combo.removeAll();  combo.add( "b" );  combo.select( 0 );
    // When only examining selectionIndex, a change cannot be determined
    boolean textChanged = !isEditable( combo )
                          && WidgetLCAUtil.hasChanged( combo, PROP_TEXT, combo.getText(), "" );
    if( selectionChanged || textChanged ) {
      JSWriter writer = JSWriter.getWriterFor( combo );
      writer.call( JS_FUNC_SELECT, new Object[] { newValue } );
    }
  }

  private static void writeMaxPopupHeight( final Combo combo )
    throws IOException
  {
    Integer newValue = new Integer( getMaxPopupHeight( combo ) );
    if( WidgetLCAUtil.hasChanged( combo,
                                  PROP_MAX_POPUP_HEIGHT,
                                  newValue ) )
    {
      JSWriter writer = JSWriter.getWriterFor( combo );
      writer.call( JS_FUNC_SET_MAX_POPUP_HEIGHT, new Object[] { newValue } );
    }
  }

  private static void writeEditable( final Combo combo ) throws IOException {
    boolean editable = isEditable( combo );
    if( editable ) {
      JSWriter writer = JSWriter.getWriterFor( combo );
      Boolean newValue = Boolean.valueOf( editable );
      writer.set( PROP_EDITABLE, "editable", newValue, null );
    }
  }

  private static void writeText( final Combo combo ) throws IOException {
    if( isEditable( combo ) ) {
      JSWriter writer = JSWriter.getWriterFor( combo );
      writer.set( PROP_TEXT, "value", combo.getText(), "" );
    }
  }

  private static void writeSelectionListener( final Combo combo )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( combo );
    writer.updateListener( JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( combo ) );
  }

  private static void writeVerifyAndModifyListener( final Combo combo )
    throws IOException
  {
    if( isEditable( combo ) ) {
      JSWriter writer = JSWriter.getWriterFor( combo );
      boolean hasVerifyListener = VerifyEvent.hasListener( combo );
      boolean hasModifyListener = ModifyEvent.hasListener( combo );
      boolean hasListener = hasVerifyListener || hasModifyListener;
      writer.updateListener( JS_MODIFY_LISTENER_INFO,
                             PROP_VERIFY_MODIFY_LISTENER,
                             hasListener );
      writer.updateListener( JS_BLUR_LISTENER_INFO,
                             PROP_VERIFY_MODIFY_LISTENER,
                             hasListener );
    }
  }
  
  //////////////////
  // Helping methods

  private static boolean isEditable( final Combo combo ) {
    return ( ( combo.getStyle() & SWT.READ_ONLY ) == 0 );
  }

  static int getMaxPopupHeight( final Combo combo ) {
    int visibleItemCount = combo.getVisibleItemCount();
    int charHeight = TextSizeDetermination.getCharHeight( combo.getFont() );
    int padding = 2 * LIST_ITEM_PADDING;
    return visibleItemCount * ( charHeight + padding );
  }
}
