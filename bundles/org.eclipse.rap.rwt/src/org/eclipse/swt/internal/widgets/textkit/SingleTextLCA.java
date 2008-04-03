/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.widgets.Text;

final class SingleTextLCA extends AbstractTextDelegateLCA {

  static final String TYPE_POOL_ID
    = SingleTextLCA.class.getName();

  void preserveValues( final Text text ) {
    ControlLCAUtil.preserveValues( text );
    TextLCAUtil.preserveValues( text );
    TextLCAUtil.preserveSelectionListener( text );
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
    WidgetLCAUtil.writeCustomVariant( text );
    ControlLCAUtil.writeStyleFlags( text );
    TextLCAUtil.writeHijack( text );
    TextLCAUtil.writeAlignment( text );
  }

  void renderChanges( final Text text ) throws IOException {
    ControlLCAUtil.writeChanges( text );
    TextLCAUtil.writeText( text );
    TextLCAUtil.writeReadOnly( text );
    TextLCAUtil.writeSelection( text );
    TextLCAUtil.writeTextLimit( text );
    TextLCAUtil.writeVerifyAndModifyListener( text );
    TextLCAUtil.writeSelectionListener( text );
  }

  void renderDispose( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.dispose();
  }

  String getTypePoolId( final Text text ) {
    return TYPE_POOL_ID;
  }

  void createResetHandlerCalls( final String typePoolId ) throws IOException {
    TextLCAUtil.resetSelection();
    TextLCAUtil.resetModifyListener();
    TextLCAUtil.resetSelectionListener();
    TextLCAUtil.resetTextLimit();
    TextLCAUtil.resetReadOnly();
    TextLCAUtil.resetText();
    ControlLCAUtil.resetChanges();
    ControlLCAUtil.resetStyleFlags();
  }
}