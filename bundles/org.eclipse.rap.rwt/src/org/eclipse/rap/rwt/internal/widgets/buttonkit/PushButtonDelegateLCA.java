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

package org.eclipse.rap.rwt.internal.widgets.buttonkit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.Button;

final class PushButtonDelegateLCA extends ButtonDelegateLCA {

  private final static JSListenerInfo JS_LISTENER_INFO = 
    new JSListenerInfo( JSConst.QX_EVENT_EXECUTE,
                        JSConst.JS_WIDGET_SELECTED,
                        JSListenerType.ACTION );

  void preserveValues( final Button button ) {
    ButtonLCAUtil.preserveValues( button );
  }

  void readData( final Button button ) {
    ControlLCAUtil.processSelection( button, null, false );
    ButtonLCAUtil.readSelection( button );
  }
  
  void renderInitialization( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    writer.newWidget( "qx.ui.form.Button" );
    ControlLCAUtil.writeStyleFlags( button );
    if( ( button.getStyle() & RWT.TOGGLE ) != 0 ) {
      writer.call( "addState", new Object[] { "rwt_TOGGLE" } );
      writer.addListener( JSConst.QX_EVENT_EXECUTE,
                          "org.eclipse.rap.rwt.ButtonUtil.onToggleExecute" );
    }
  }

  // TODO [rh] highligh default button (e.g. with thick border as in Windows)
  void renderChanges( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
    // bounds of the widget that was clicked -> In the SelectionEvent
    // for Button the bounds are undefined
    writer.updateListener( JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    ControlLCAUtil.writeChanges( button );
    writeSelection( button );
    ButtonLCAUtil.writeText( button );
    ButtonLCAUtil.writeAlignment( button );
    ButtonLCAUtil.writeImage( button );
    ButtonLCAUtil.writeDefault( button );
  }

  void renderDispose( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    // TODO [rst] Is it legible to access any methods after dispose?
    if( ( button.getStyle() & RWT.TOGGLE ) != 0 ) {
      writer.removeListener( JSConst.QX_EVENT_EXECUTE,
                             "org.eclipse.rap.rwt.ButtonUtil.onToggleExecute" );
    }
    writer.dispose();
  }
  
  private void writeSelection( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    String property = ButtonLCAUtil.PROP_SELECTION;
    Boolean newValue = Boolean.valueOf( button.getSelection() ); 
    Boolean defValue = Boolean.FALSE;
    if( WidgetLCAUtil.hasChanged( button, property, newValue, defValue ) ) {
      writer.call( "setState", new Object[] { "checked", newValue } );
    }
  }
}
