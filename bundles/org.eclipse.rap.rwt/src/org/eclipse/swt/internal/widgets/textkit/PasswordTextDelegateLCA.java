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

import org.eclipse.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rwt.lifecycle.JSWriter;
import org.eclipse.swt.widgets.Text;

final class PasswordTextDelegateLCA extends AbstractTextDelegateLCA {

  static final String TYPE_POOL_ID
    = PasswordTextDelegateLCA.class.getName();

  private static final String QX_TYPE = "qx.ui.form.PasswordField";

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
    TextLCAUtil.readSelection( text );
  }

  void renderInitialization( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.newWidget( QX_TYPE );
    ControlLCAUtil.writeStyleFlags( text );
  }

  void renderChanges( final Text text ) throws IOException {
    ControlLCAUtil.writeChanges( text );
    TextLCAUtil.writeText( text );
    TextLCAUtil.writeReadOnly( text );
    TextLCAUtil.writeSelection( text );
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
    TextLCAUtil.resetSelection();
    TextLCAUtil.resetTextLimit();
    TextLCAUtil.resetReadOnly();
    TextLCAUtil.resetText();
    ControlLCAUtil.resetChanges();
    ControlLCAUtil.resetStyleFlags();
  }
}
