/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.textkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

final class SingleTextLCA extends AbstractTextDelegateLCA {

  // Property names to preserve values
  static final String PROP_MESSAGE = "message";

  void preserveValues( final Text text ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( text );
    ControlLCAUtil.preserveValues( text );
    TextLCAUtil.preserveValues( text );
    TextLCAUtil.preservePasswordMode( text );
    TextLCAUtil.preserveVerifyAndModifyListener( text );
    TextLCAUtil.preserveSelectionListener( text );
    WidgetLCAUtil.preserveCustomVariant( text );
    adapter.preserve( PROP_MESSAGE, text.getMessage() );
  }

  /* (intentionally non-JavaDoc'ed)
   * readData does not explicitly handle modifyEvents. They are fired implicitly
   * by updating the text property in TextLCAUtil.readText( Text ).
   */
  void readData( final Text text ) {
    TextLCAUtil.readTextAndSelection( text );
    ControlLCAUtil.processSelection( text, null, false );
    ControlLCAUtil.processMouseEvents( text );
    ControlLCAUtil.processKeyEvents( text );
    ControlLCAUtil.processMenuDetect( text );
    WidgetLCAUtil.processHelp( text );
  }

  void renderInitialization( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.newWidget( "org.eclipse.rwt.widgets.Text",
                      new Object[]{ Boolean.FALSE } );
    WidgetLCAUtil.writeStyleFlag( text, SWT.SINGLE, "SINGLE" );
    TextLCAUtil.writeInitialize( text );
    ControlLCAUtil.writeStyleFlags( text );
    TextLCAUtil.writeAlignment( text );
  }

  void renderChanges( final Text text ) throws IOException {
    ControlLCAUtil.writeChanges( text );
    TextLCAUtil.writePasswordMode( text );
    TextLCAUtil.writeText( text, true );
    TextLCAUtil.writeReadOnly( text );
    TextLCAUtil.writeSelection( text );
    TextLCAUtil.writeTextLimit( text );
    TextLCAUtil.writeVerifyAndModifyListener( text );
    TextLCAUtil.writeSelectionListener( text );
    WidgetLCAUtil.writeCustomVariant( text );
    writeMessage( text );
  }

  void renderDispose( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.callStatic( "org.eclipse.swt.TextUtil.disposeMessageLabel",
                       new Object[] { text } );
    writer.dispose();
  }

  ///////////////////////////////////////////
  // Helping methods to write JavaScript code

  private static void writeMessage( final Text text ) throws IOException {
    String newValue = text.getMessage();
    if( WidgetLCAUtil.hasChanged( text, PROP_MESSAGE, newValue, "" ) ) {
      JSWriter writer = JSWriter.getWriterFor( text );
      writer.callStatic( "org.eclipse.swt.TextUtil.setMessage",
                         new Object[] { text, newValue } );
    }
  }

}