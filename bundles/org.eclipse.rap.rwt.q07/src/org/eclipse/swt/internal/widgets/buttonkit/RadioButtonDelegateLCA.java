/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.buttonkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Button;


final class RadioButtonDelegateLCA extends ButtonDelegateLCA {

  static final String TYPE_POOL_ID
    = RadioButtonDelegateLCA.class.getName();

  void preserveValues( final Button button ) {
    ControlLCAUtil.preserveValues( button );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    boolean hasListeners = SelectionEvent.hasListener( button );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListeners ) );
    ButtonLCAUtil.preserveValues( button );
    WidgetLCAUtil.preserveCustomVariant( button );
  }

  void readData( final Button button ) {
    // [if] The selection event is based on the request "selection" parameter
    // and not on the selection event, because it is not possible to fire the
    // same event (Id) from javascript for two widgets (selected and unselected
    // radio button) at the same time.
    if( ButtonLCAUtil.readSelection( button ) ) {
      processSelectionEvent( button );
    }
    ControlLCAUtil.processMouseEvents( button );
    ControlLCAUtil.processKeyEvents( button );
  }

  void renderInitialization( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    writer.newWidget( "org.eclipse.swt.widgets.RadioButton" );
    ControlLCAUtil.writeStyleFlags( button );
    WidgetLCAUtil.writeStyleFlag( button, SWT.RADIO, "RADIO" );
  }

  void renderChanges( final Button button ) throws IOException {
    ControlLCAUtil.writeChanges( button );
    ButtonLCAUtil.writeSelection( button );
    ButtonLCAUtil.writeText( button );
    ButtonLCAUtil.writeImage( button );
    ButtonLCAUtil.writeAlignment( button );
    WidgetLCAUtil.writeCustomVariant( button );
    writeListener( button );
  }

  void renderDispose( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    writer.dispose();
  }

  String getTypePoolId( final Button button ) {
//    return TYPE_POOL_ID;
    return null;
  }

  void createResetHandlerCalls( final String typePoolId ) throws IOException {
// TODO [fappel]: check why removal of listener doesn't work. Seems as if
//                manager is removed already (maybe dispose call?)...
//    JSWriter writer = JSWriter.getWriterForResetHandler();
//    writer.removeListener( JS_PROP_MANAGER,
//                           JS_LISTENER_INFO.getEventType(),
//                           JS_LISTENER_INFO.getJSListener() );
    ButtonLCAUtil.resetAlignment();
    ButtonLCAUtil.resetText();
    ButtonLCAUtil.resetSelection();
    ControlLCAUtil.resetChanges();
    ControlLCAUtil.resetStyleFlags();
  }

  private void writeListener( final Button button ) throws IOException {
    boolean hasListener = SelectionEvent.hasListener( button );
    Boolean newValue = Boolean.valueOf( hasListener );
    String prop = Props.SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( button, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( button );
      writer.set( "hasSelectionListener", newValue );
    }
  }

  private void processSelectionEvent( final Button button ) {
    if( SelectionEvent.hasListener( button ) ) {
      Rectangle bounds  = WidgetLCAUtil.readBounds( button,
                                                    button.getBounds() );
      int type = SelectionEvent.WIDGET_SELECTED;
      SelectionEvent event = new SelectionEvent( button,
                                                 null,
                                                 type,
                                                 bounds,
                                                 null,
                                                 true,
                                                 SWT.NONE );
      event.processEvent();
    }
  }
}
