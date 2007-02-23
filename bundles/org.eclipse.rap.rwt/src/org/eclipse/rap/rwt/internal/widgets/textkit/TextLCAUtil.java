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

package org.eclipse.rap.rwt.internal.widgets.textkit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.ModifyEvent;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.Text;
import com.w4t.W4TContext;
import com.w4t.util.browser.Mozilla;


final class TextLCAUtil {
  
  static final String PROP_TEXT = "text";
  static final String PROP_TEXT_LIMIT = "textLimit";
  static final String PROP_MODIFY_LISTENER = "modifyListener";

  private static final Integer DEFAULT_TEXT_LIMIT 
    = new Integer( Text.MAX_LIMIT_TEXT );

  private static final JSListenerInfo JS_MODIFY_LISTENER_INFO 
    = new JSListenerInfo( "keypress", 
                          "org.eclipse.rap.rwt.TextUtil.modifyText", 
                          JSListenerType.STATE_AND_ACTION );
  private static final JSListenerInfo JS_BLUR_LISTENER_INFO 
    = new JSListenerInfo( JSConst.QX_EVENT_BLUR, 
                          "org.eclipse.rap.rwt.TextUtil.modifyTextOnBlur", 
                          JSListenerType.ACTION );

  private TextLCAUtil() {
    // prevent instantiation
  }

  static void preserveValues( final Text text ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    adapter.preserve( PROP_TEXT, text.getText() );
    adapter.preserve( PROP_TEXT_LIMIT, new Integer( text.getTextLimit() ) );
    boolean hasListener = ModifyEvent.hasListener( text );
    adapter.preserve( PROP_MODIFY_LISTENER, Boolean.valueOf( hasListener ) );
  }
  
  static void readText( final Text text ) {
    String newText = WidgetLCAUtil.readPropertyValue( text, "text" );
    if( newText != null ) {
      text.setText( newText );
    }
  }
  
  static void readModifyEvent( final Text text ) {
    if( WidgetLCAUtil.wasEventSent( text, JSConst.EVENT_MODIFY_TEXT ) ) {
      ModifyEvent event = new ModifyEvent( text );
      event.processEvent();
    }
  }

  static void writeReadOnly( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    if( ( text.getStyle() & RWT.READ_ONLY ) != 0 ) {
      writer.set( "readOnly", true );
    }
  }

  static void writeNoSpellCheck( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    // TODO [rh] this should be solved in qooxdoo
    //      see http://bugzilla.qooxdoo.org/show_bug.cgi?id=291
    if( W4TContext.getBrowser() instanceof Mozilla ) {
      Object[] args = new Object[] { "spellcheck", Boolean.FALSE };
      writer.call( "setHtmlAttribute", args );
    }
  }

  static void writeTextLimit( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    Integer newValue = new Integer( text.getTextLimit() );
    writer.set( PROP_TEXT_LIMIT, "maxLength", newValue, DEFAULT_TEXT_LIMIT );

  }

  
  static void writeModifyListener( final Text text ) throws IOException {
    if( ( text.getStyle() & RWT.READ_ONLY ) == 0 ) {
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

  /**
   * Returns the given string with all newlines replaced with spaces.
   */
  static String stripNewlines( final String text ) {
    return text.replaceAll( "\n", " " );
  }
}
