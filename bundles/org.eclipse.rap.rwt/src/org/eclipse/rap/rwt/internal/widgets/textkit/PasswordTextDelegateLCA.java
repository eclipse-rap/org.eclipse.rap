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
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.Text;

final class PasswordTextDelegateLCA extends AbstractTextDelegateLCA {

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
    writer.newWidget( "qx.ui.form.PasswordField" );
    ControlLCAUtil.writeStyleFlags( text );
    TextLCAUtil.writeNoSpellCheck( text );
  }

  void renderChanges( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    ControlLCAUtil.writeChanges( text );
    String newValue = text.getText();
    if( WidgetLCAUtil.hasChanged( text, TextLCAUtil.PROP_TEXT, newValue, "" ) ) {
      writer.set( "value", TextLCAUtil.stripNewlines( newValue ) );
    }
    TextLCAUtil.writeReadOnly( text );
    TextLCAUtil.writeSelection( text );
    TextLCAUtil.writeTextLimit( text );
    TextLCAUtil.writeModifyListener( text );
  }
  
  void renderDispose( final Text text ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( text );
    writer.dispose();
  }
}
