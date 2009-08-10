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

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;


final class MultiTextLCA extends AbstractTextDelegateLCA {

  void preserveValues( final Text text ) {
    ControlLCAUtil.preserveValues( text );
    TextLCAUtil.preserveValues( text );
    TextLCAUtil.preserveVerifyAndModifyListener( text );
    WidgetLCAUtil.preserveCustomVariant( text );
  }

  /* (intentionally non-JavaDoc'ed)
   * readData does not explicitly handle modifyEvents. They are fired implicitly
   * by updating the text property in TextLCAUtil.readText( Text ).
   */
  void readData( final Text text ) {
    TextLCAUtil.readTextAndSelection( text );
    ControlLCAUtil.processMouseEvents( text );
    ControlLCAUtil.processKeyEvents( text );
    WidgetLCAUtil.processHelp( text );
  }

  void renderInitialization( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.newWidget( "qx.ui.form.TextArea" );
    TextLCAUtil.writeInitialize( text );
    ControlLCAUtil.writeStyleFlags( text );
    WidgetLCAUtil.writeStyleFlag( text, SWT.MULTI, "MULTI" );
    TextLCAUtil.writeWrap( text );
    TextLCAUtil.writeAlignment( text );
  }

  void renderChanges( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    ControlLCAUtil.writeChanges( text );
    writer.set( TextLCAUtil.PROP_TEXT, "value", text.getText(), "" );
    TextLCAUtil.writeReadOnly( text );
    TextLCAUtil.writeSelection( text );
    TextLCAUtil.writeTextLimit( text );
    TextLCAUtil.writeVerifyAndModifyListener( text );
    WidgetLCAUtil.writeCustomVariant( text );
  }

  void renderDispose( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.dispose();
  }

}
