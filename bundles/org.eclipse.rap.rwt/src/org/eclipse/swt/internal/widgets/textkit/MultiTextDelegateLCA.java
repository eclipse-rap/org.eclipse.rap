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

import org.eclipse.swt.lifecycle.ControlLCAUtil;
import org.eclipse.swt.lifecycle.JSWriter;
import org.eclipse.swt.widgets.Text;

// TODO [rh] bring selection for multi-line text to work. Currently there
//      occur JavaScript errors. (see readSelection, writeSelection)
final class MultiTextDelegateLCA extends AbstractTextDelegateLCA {

  private static final String QX_TYPE = "qx.ui.form.TextArea";

  static final String TYPE_POOL_ID
    = MultiTextDelegateLCA.class.getName();

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
  }

  void renderInitialization( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.newWidget( QX_TYPE );
    ControlLCAUtil.writeStyleFlags( text );
    TextLCAUtil.writeNoSpellCheck( text );
//    TODO [rst] Disabled writing of wrap state since it only works in Opera and
//               also interferes with object pooling in IE.
//    TextLCAUtil.writeWrap( text );
  }

  void renderChanges( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    ControlLCAUtil.writeChanges( text );
    writer.set( TextLCAUtil.PROP_TEXT, "value", text.getText(), "" );
    TextLCAUtil.writeReadOnly( text );
//    TextLCAUtil.writeSelection( text );
    TextLCAUtil.writeTextLimit( text );
    TextLCAUtil.writeModifyListener( text );
  }

  void renderDispose( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.dispose();
  }

  String getTypePoolId( final Text text ) throws IOException {
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
}
