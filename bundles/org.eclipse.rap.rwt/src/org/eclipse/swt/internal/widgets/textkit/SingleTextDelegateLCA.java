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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.Text;

final class SingleTextDelegateLCA extends AbstractTextDelegateLCA {
  
  private static final String PROP_SELECTION_LISTENER = "selectionListener";

  private final static JSListenerInfo JS_SELECTION_LISTENER_INFO
    = new JSListenerInfo( JSConst.QX_EVENT_KEYDOWN,
                          "org.eclipse.swt.TextUtil.widgetDefaultSelected",
                          JSListenerType.ACTION );

  void preserveValues( final Text text ) {
    ControlLCAUtil.preserveValues( text );
    TextLCAUtil.preserveValues( text );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    adapter.preserve( PROP_SELECTION_LISTENER,
                      Boolean.valueOf( SelectionEvent.hasListener( text ) ) );
  }

  /* (intentionally non-JavaDoc'ed)
   * readData does not explicitly handle modifyEvents. They are fired implicitly
   * by updating the text property in TextLCAUtil.readText( Text ).
   */
  void readData( final Text text ) {
    // order is crucial: first read text then read what part of it is selected
    TextLCAUtil.readText( text );
    TextLCAUtil.readSelection( text );
    ControlLCAUtil.processSelection( text, null, false );
  }

  void renderInitialization( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.newWidget( "qx.ui.form.TextField" );
    ControlLCAUtil.writeStyleFlags( text );
    TextLCAUtil.writeNoSpellCheck( text );
  }

  void renderChanges( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    ControlLCAUtil.writeChanges( text );
    String newValue = text.getText();
    if( WidgetLCAUtil.hasChanged( text, TextLCAUtil.PROP_TEXT, newValue, "" ) ) 
    {
      writer.set( "value", TextLCAUtil.stripNewlines( newValue ) );
    }
    TextLCAUtil.writeReadOnly( text );
    TextLCAUtil.writeSelection( text );
    TextLCAUtil.writeTextLimit( text );
    TextLCAUtil.writeModifyListener( text );
    writeSelectionListener( text );
  }
  
  void renderDispose( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.dispose();
  }
  
  private static void writeSelectionListener( final Text text )
    throws IOException
  {
    if( ( text.getStyle() & SWT.READ_ONLY ) == 0 ) {
      JSWriter writer = JSWriter.getWriterFor( text );
      writer.updateListener( JS_SELECTION_LISTENER_INFO,
                             PROP_SELECTION_LISTENER,
                             SelectionEvent.hasListener( text ) );
    }
  }
}