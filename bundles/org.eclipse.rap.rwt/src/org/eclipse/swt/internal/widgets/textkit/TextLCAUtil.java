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

package org.eclipse.swt.internal.widgets.textkit;

import java.io.IOException;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.Text;



final class TextLCAUtil {

  private static final Pattern NEWLINE_PATTERN
    = Pattern.compile( "\\r\\n|\\r|\\n" );
  static final String PROP_TEXT = "text";
  static final String PROP_TEXT_LIMIT = "textLimit";
  static final String PROP_SELECTION = "selection";
  static final String PROP_MODIFY_LISTENER = "modifyListener";
  static final String PROP_READONLY = "readonly";

  private static final Integer DEFAULT_TEXT_LIMIT
    = new Integer( Text.MAX_TEXT_LIMIT );
  private static final Point DEFAULT_SELECTION
    = new Point( 0, 0 );

  private static final String JS_PROP_MAX_LENGTH = "maxLength";
  private static final String JS_PROP_READ_ONLY = "readOnly";
  private static final String JS_PROP_VALUE = "value";
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

  private TextLCAUtil() {
    // prevent instantiation
  }

  static void preserveValues( final Text text ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    adapter.preserve( PROP_TEXT, text.getText() );
    adapter.preserve( PROP_TEXT_LIMIT, new Integer( text.getTextLimit() ) );
    adapter.preserve( PROP_SELECTION, text.getSelection() );
    boolean hasListener = ModifyEvent.hasListener( text );
    adapter.preserve( PROP_MODIFY_LISTENER, Boolean.valueOf( hasListener ) );
    adapter.preserve( PROP_READONLY, Boolean.valueOf( ! text.getEditable() ) );
  }

  static void readText( final Text text ) {
    String newText = WidgetLCAUtil.readPropertyValue( text, "text" );
    if( newText != null ) {
      text.setText( newText );
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

  static void writeModifyListener( final Text text ) throws IOException {
    if( ( text.getStyle() & SWT.READ_ONLY ) == 0 ) {
      JSWriter writer = JSWriter.getWriterFor( text );
      boolean hasListener = ModifyEvent.hasListener( text );
      writer.updateListener( JS_MODIFY_LISTENER_INFO,
                             PROP_MODIFY_LISTENER,
                             hasListener );
      writer.updateListener( JS_BLUR_LISTENER_INFO,
                             PROP_MODIFY_LISTENER,
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
    if( WidgetLCAUtil.hasChanged( text, PROP_TEXT, newValue, "" ) )
    {
      writer.set( JS_PROP_VALUE, stripNewlines( newValue ) );
    }
  }

  static void resetText() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( JS_PROP_VALUE );
  }

  /**
   * Returns the given string with all newlines replaced with spaces.
   */
  static String stripNewlines( final String text ) {
    return NEWLINE_PATTERN.matcher( text ).replaceAll( " " );
  }
}
