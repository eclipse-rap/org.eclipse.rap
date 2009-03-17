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
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Button;

final class CheckButtonDelegateLCA extends ButtonDelegateLCA {

  static final String TYPE_POOL_ID
    = CheckButtonDelegateLCA.class.getName();
  private static final String QX_TYPE = "org.eclipse.swt.widgets.CheckBox";

  static final String PROP_GRAYED = "grayed";

  void preserveValues( final Button button ) {
    ControlLCAUtil.preserveValues( button );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    boolean hasListeners = SelectionEvent.hasListener( button );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListeners ) );
    adapter.preserve( PROP_GRAYED, Boolean.valueOf( button.getGrayed() ) );
    ButtonLCAUtil.preserveValues( button );
    WidgetLCAUtil.preserveCustomVariant( button );
  }

  void readData( final Button button ) {
    ButtonLCAUtil.readSelection( button );
    ControlLCAUtil.processSelection( button, null, true );
    ControlLCAUtil.processMouseEvents( button );
    ControlLCAUtil.processKeyEvents( button );
  }

  void renderInitialization( final Button button )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( button );
    writer.newWidget( QX_TYPE );
    ControlLCAUtil.writeStyleFlags( button );
    WidgetLCAUtil.writeStyleFlag( button, SWT.CHECK, "CHECK" );
  }

  void renderChanges( final Button button ) throws IOException {
    // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
    // bounds of the widget that was clicked -> In the SelectionEvent
    // for Button the bounds are undefined
    ControlLCAUtil.writeChanges( button );
    ButtonLCAUtil.writeSelection( button );
    ButtonLCAUtil.writeText( button );
    ButtonLCAUtil.writeAlignment( button );
    ButtonLCAUtil.writeImage( button );
    writeGrayed( button );
    WidgetLCAUtil.writeCustomVariant( button );
    writeListener( button );
  }

  void renderDispose( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    writer.dispose();
  }

  String getTypePoolId( final Button button ) {
    // Disabled pooling, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=226099
//    return TYPE_POOL_ID;
    return null;
  }

  void createResetHandlerCalls( final String typePoolId ) throws IOException {
    ButtonLCAUtil.resetAlignment();
    ButtonLCAUtil.resetText();
    ButtonLCAUtil.resetSelection();
    ControlLCAUtil.resetChanges();
    ControlLCAUtil.resetStyleFlags();
  }

  private static void writeGrayed( final Button button ) throws IOException {
    Boolean newValue = Boolean.valueOf( button.getGrayed() );
    String prop = PROP_GRAYED;
    if( WidgetLCAUtil.hasChanged( button, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( button );
      writer.set( prop, newValue );
    }
  }

  private static void writeListener( final Button button ) throws IOException {
    boolean hasListener = SelectionEvent.hasListener( button );
    Boolean newValue = Boolean.valueOf( hasListener );
    String prop = Props.SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( button, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( button );
      writer.set( "hasSelectionListener", newValue );
    }
  }
}