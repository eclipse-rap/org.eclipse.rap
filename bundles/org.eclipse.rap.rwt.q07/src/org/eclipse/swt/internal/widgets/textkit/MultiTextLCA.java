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

import org.eclipse.rwt.internal.browser.Mozilla;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.widgets.Text;


// TODO [rh] bring selection for multi-line text to work. Currently there
//      occur JavaScript errors. (see readSelection, writeSelection)
final class MultiTextLCA extends AbstractTextDelegateLCA {

  static final String TYPE_POOL_ID
    = MultiTextLCA.class.getName();

  void preserveValues( final Text text ) {
    ControlLCAUtil.preserveValues( text );
    TextLCAUtil.preserveValues( text );
  }

  /* (intentionally non-JavaDoc'ed)
   * readData does not explicitly handle modifyEvents. They are fired implicitly
   * by updating the text property in TextLCAUtil.readText( Text ).
   */
  void readData( final Text text ) {
    // order is crucial: first read text then read what part of it is selected
    TextLCAUtil.readText( text );
//    TextLCAUtil.readSelection( text );
    ControlLCAUtil.processMouseEvents( text );
  }

  void renderInitialization( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.newWidget( "qx.ui.form.TextArea" );
    WidgetLCAUtil.writeCustomVariant( text );
    ControlLCAUtil.writeStyleFlags( text );
    MultiTextLCA.writeNoSpellCheck( text );
//    TODO [rst] Disabled writing of wrap state since it only works in Opera and
//               also interferes with object pooling in IE.
    TextLCAUtil.writeWrap( text );
    TextLCAUtil.writeHijack( text );
    TextLCAUtil.writeAlignment( text );
  }

  void renderChanges( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    ControlLCAUtil.writeChanges( text );
    writer.set( TextLCAUtil.PROP_TEXT, "value", text.getText(), "" );
    TextLCAUtil.writeReadOnly( text );
//    TextLCAUtil.writeSelection( text );
    TextLCAUtil.writeTextLimit( text );
    TextLCAUtil.writeVerifyAndModifyListener( text );
  }

  void renderDispose( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.dispose();
  }

  String getTypePoolId( final Text text ) {
    return TYPE_POOL_ID;
  }

  void createResetHandlerCalls( final String typePoolId ) throws IOException {
    TextLCAUtil.resetModifyListener();
    TextLCAUtil.resetTextLimit();
    TextLCAUtil.resetReadOnly();
    TextLCAUtil.resetText();
    ControlLCAUtil.resetChanges();
    ControlLCAUtil.resetStyleFlags();
  }

  //////////////////
  // Helping methods

  private static void writeNoSpellCheck( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    if( ContextProvider.getBrowser() instanceof Mozilla ) {
      writer.set( "spellCheck", false );
    }
  }
}
