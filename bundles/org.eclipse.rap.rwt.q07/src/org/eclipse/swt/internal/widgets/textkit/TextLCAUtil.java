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
package org.eclipse.swt.internal.widgets.textkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;


final class TextLCAUtil {

  static final String PROP_TEXT = "text";
  static final String PROP_TEXT_LIMIT = "textLimit";
  static final String PROP_SELECTION = "selection";
  static final String PROP_READONLY = "readonly";
  static final String PROP_VERIFY_MODIFY_LISTENER
    = "verifyModifyListener";
  static final String PROP_SELECTION_LISTENER = "selectionListener";

  private static final Integer DEFAULT_TEXT_LIMIT
    = new Integer( Text.LIMIT );
  private static final Point DEFAULT_SELECTION
    = new Point( 0, 0 );

  private static final String JS_PROP_MAX_LENGTH = "maxLength";
  private static final String JS_PROP_READ_ONLY = "readOnly";
  private static final String JS_PROP_VALUE = "value";
  private static final String JS_PROP_TEXT_ALIGN = "textAlign";
  private static final String JS_LISTENER_ON_MOUSE_UP
    = "org.eclipse.swt.TextUtil.onMouseUp";
  private static final String JS_EVENT_MOUSE_UP = "mouseup";
  private static final JSListenerInfo JS_MODIFY_LISTENER_INFO
    = new JSListenerInfo( JSConst.QX_EVENT_KEY_UP,
                          "org.eclipse.swt.TextUtil.modifyText",
                          JSListenerType.STATE_AND_ACTION );
  private static final JSListenerInfo JS_BLUR_LISTENER_INFO
    = new JSListenerInfo( JSConst.QX_EVENT_BLUR,
                          "org.eclipse.swt.TextUtil.modifyTextOnBlur",
                          JSListenerType.ACTION );
  private final static JSListenerInfo JS_SELECTION_LISTENER_INFO
    = new JSListenerInfo( JSConst.QX_EVENT_KEYDOWN,
                          "org.eclipse.swt.TextUtil.widgetDefaultSelected",
                          JSListenerType.ACTION );

  private TextLCAUtil() {
    // prevent instantiation
  }

  static void preserveValues( final Text text ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    adapter.preserve( PROP_TEXT, text.getText() );
    adapter.preserve( PROP_TEXT_LIMIT, new Integer( text.getTextLimit() ) );
    adapter.preserve( PROP_SELECTION, text.getSelection() );
    adapter.preserve( PROP_READONLY, Boolean.valueOf( ! text.getEditable() ) );
    boolean hasVerifyListener = VerifyEvent.hasListener( text );
    boolean hasModifyListener = ModifyEvent.hasListener( text );
    boolean hasListener = hasVerifyListener || hasModifyListener;
    adapter.preserve( PROP_VERIFY_MODIFY_LISTENER,
                      Boolean.valueOf( hasListener ) );
  }

  static void readText( final Text text ) {
    final String value = WidgetLCAUtil.readPropertyValue( text, "text" );
    if( value != null ) {
      if( VerifyEvent.hasListener( text ) ) {
        // setText needs to be executed in a ProcessAction runnable as it may
        // fire a VerifyEvent whose fields (text and doit) need to be evaluated
        // before actually setting the new value
        ProcessActionRunner.add( new Runnable() {
          public void run() {
            text.setText( value );
            // Reset preserved value in case the values wasn't set as-is as this
            // means that a VerifyListener manipulated or rejected the value
            if( !value.equals( text.getText() ) ) {
              IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
              adapter.preserve( PROP_TEXT, null );
            }
          }
        } );
      } else {
        text.setText( value );
      }
    }
  }

  static void readSelection( final Text text ) {
    Point selection = text.getSelection();
    String value = WidgetLCAUtil.readPropertyValue( text, "selectionStart" );
    if( value != null ) {
      selection.x = Integer.parseInt( value );
    }
    value = WidgetLCAUtil.readPropertyValue( text, "selectionCount" );
    if( value != null ) {
      selection.y = selection.x + Integer.parseInt( value );
    }
    text.setSelection( selection );
  }

  static void writeHijack( final Text text ) throws IOException {
    // TODO [rh] workaround for
    //      https://bugs.eclipse.org/bugs/show_bug.cgi?id=201080
    //      see TextUtil.js
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.callStatic( "org.eclipse.swt.TextUtil.hijack",
                       new Object[] { text } );
  }

  static void writeReadOnly( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    Boolean newValue = Boolean.valueOf( !text.getEditable() );
    writer.set( PROP_READONLY, JS_PROP_READ_ONLY, newValue, Boolean.FALSE );
  }

  static void resetReadOnly() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( JS_PROP_READ_ONLY );
  }

  static void writeTextLimit( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    Integer newValue = new Integer( text.getTextLimit() );
    Integer defValue = DEFAULT_TEXT_LIMIT;
    if( WidgetLCAUtil.hasChanged( text, PROP_TEXT_LIMIT, newValue, defValue ) )
    {
      // Negative values are treated as 'no limit' which is achieved by passing
      // null to the client-side maxLength property
      if( newValue.intValue() < 0 ) {
        newValue = null;
      }
      writer.set( JS_PROP_MAX_LENGTH, newValue );
    }
  }

  static void resetTextLimit() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( JS_PROP_MAX_LENGTH );
  }

  // TODO [rst] Possible workaround for weird pooling problems with wrap
  //            property. See qx bug 300. This method might be replaced with a
  //            simple JSWriter#set call when this bug is fixed.
  static void writeWrap( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    Boolean value = Boolean.valueOf( ( text.getStyle() & SWT.WRAP ) != 0 );
    writer.callStatic( "org.eclipse.swt.TextUtil.setWrap",
                       new Object[] { text, value } );
  }

  static void writeSelection( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    if( !adapter.isInitialized() ) {
      writer.addListener( JS_EVENT_MOUSE_UP, JS_LISTENER_ON_MOUSE_UP );
    }
    Point newValue = text.getSelection();
    Point defValue = DEFAULT_SELECTION;
    // TODO [rh] could be optimized: when text was changed and selection is 0,0
    //      there is no need to write JavaScript since the client resets the
    //      selection as well when the new text is set.
    if( WidgetLCAUtil.hasChanged( text, PROP_SELECTION, newValue, defValue ) ) {
      Integer start = new Integer( newValue.x );
      Integer count = new Integer( text.getSelectionCount() );
      writer.callStatic( "org.eclipse.swt.TextUtil.setSelection",
                         new Object[] { text, start, count } );
    }
  }

  static void resetSelection() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.removeListener( JS_EVENT_MOUSE_UP, JS_LISTENER_ON_MOUSE_UP );
    writer.removeListener( "appear",
                           "org.eclipse.swt.TextUtil._onAppearSetSelection" );
  }

  static void writeAlignment( final Text text ) throws IOException {
    int style = text.getStyle();
    if( ( style & SWT.RIGHT ) != 0 ) {
      JSWriter writer = JSWriter.getWriterFor( text );
      writer.set( JS_PROP_TEXT_ALIGN, "right" );
    } else if( ( style & SWT.CENTER ) != 0 ) {
      JSWriter writer = JSWriter.getWriterFor( text );
      writer.set( JS_PROP_TEXT_ALIGN, "center" );
    }
  }

  static void writeVerifyAndModifyListener( final Text text )
    throws IOException
  {
    if( ( text.getStyle() & SWT.READ_ONLY ) == 0 ) {
      JSWriter writer = JSWriter.getWriterFor( text );
      boolean hasVerifyListener = VerifyEvent.hasListener( text );
      boolean hasModifyListener = ModifyEvent.hasListener( text );
      boolean hasListener = hasModifyListener || hasVerifyListener;
      writer.updateListener( JS_MODIFY_LISTENER_INFO,
                             PROP_VERIFY_MODIFY_LISTENER,
                             hasListener );
      writer.updateListener( JS_BLUR_LISTENER_INFO,
                             PROP_VERIFY_MODIFY_LISTENER,
                             hasListener );
    }
  }

  static void resetModifyListener() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.removeListener( JS_MODIFY_LISTENER_INFO.getEventType(),
                           JS_MODIFY_LISTENER_INFO.getJSListener() );
  }

  static void writeText( final Text text ) throws IOException {
    String newValue = text.getText();
    JSWriter writer = JSWriter.getWriterFor( text );
    if( WidgetLCAUtil.hasChanged( text, PROP_TEXT, newValue, "" ) ) {
      String value = WidgetLCAUtil.replaceNewLines( newValue, " " );
      writer.set( JS_PROP_VALUE, value );
    }
  }

  static void resetText() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( JS_PROP_VALUE );
  }

  static void preserveSelectionListener( final Text text ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    adapter.preserve( PROP_SELECTION_LISTENER,
                      Boolean.valueOf( hasSelectionListener( text ) ) );
  }

  static void writeSelectionListener( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.updateListener( JS_SELECTION_LISTENER_INFO,
                           PROP_SELECTION_LISTENER,
                           hasSelectionListener( text ) );
  }

  static void resetSelectionListener() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.removeListener( JS_SELECTION_LISTENER_INFO.getEventType(),
                           JS_SELECTION_LISTENER_INFO.getJSListener() );
  }

  private static boolean hasSelectionListener( final Text text ) {
    // Emulate SWT (on Windows) where a default button takes precedence over
    // a SelectionListener on a text field when both are on the same shell.
    Button defButton = text.getShell().getDefaultButton();
    boolean hasDefaultButton = defButton != null && defButton.isVisible();
    return !hasDefaultButton && SelectionEvent.hasListener( text );
  }
}
