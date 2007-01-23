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
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.Text;
import com.w4t.W4TContext;
import com.w4t.util.browser.Mozilla;


final class TextLCAUtil {
  
  static final String PROP_TEXT = "text";

  private TextLCAUtil() {
    // prevent instantiation
  }

  static void preserveText( final Text text ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    adapter.preserve( PROP_TEXT, text.getText() );
  }

  static void readText( final Text text ) {
    String newText = WidgetUtil.readPropertyValue( text, "text" );
    if( newText != null ) {
      text.setText( newText );
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
    if( W4TContext.getBrowser() instanceof Mozilla ) {
      Object[] args = new Object[] { "spellcheck", Boolean.FALSE };
      writer.call( "setHtmlAttribute", args );
    }
  }

  static void writeModifyListeners( Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.addListener( JSConst.QX_EVENT_BLUR, JSConst.JS_TEXT_MODIFIED );
    writer.addListener( JSConst.QX_EVENT_INPUT, JSConst.JS_TEXT_MODIFIED );
  }

  /**
   * Returns the given string with all newlines replaced with spaces.
   */
  static String stripNewlines( final String text ) {
    return text.replaceAll( "\n", " " );
  }
}
