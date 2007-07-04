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

package org.eclipse.swt.internal.widgets.buttonkit;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.Button;

final class PushButtonDelegateLCA extends ButtonDelegateLCA {

  static final String PREFIX_TYPE_POOL_ID
    = PushButtonDelegateLCA.class.getName();
  private static final String TYPE_POOL_ID_BORDER
    = PREFIX_TYPE_POOL_ID + "_BORDER";
  private static final String TYPE_POOL_ID_FLAT
    = PREFIX_TYPE_POOL_ID + "_FLAT";
  private static final String QX_TYPE = "qx.ui.form.Button";
  
  private static final String JS_FUNC_BUTTON_UTIL_ON_TOGGLE_EXECUTE
    = "org.eclipse.swt.ButtonUtil.onToggleExecute";
  private static final String JS_FUNC_REMOVE_STATE = "removeState";
  private static final String JS_FUNC_ADD_STATE = "addState";
  private static final Object[] PARAM_CHECKED = new Object[] { "checked" };
  private static final Object[] PARAM_TOGGLE = new Object[] { "rwt_TOGGLE" };
  private static final Object[] PARAM_NULL = new Object[] { null };
  
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
    writer.newWidget( QX_TYPE );
    ButtonLCAUtil.writeLabelMode( button );
    ControlLCAUtil.writeStyleFlags( button );
    if( ( button.getStyle() & SWT.TOGGLE ) != 0 ) {
      writer.call( JS_FUNC_ADD_STATE, PARAM_TOGGLE );
      writer.addListener( JSConst.QX_EVENT_EXECUTE,
                          JS_FUNC_BUTTON_UTIL_ON_TOGGLE_EXECUTE );
    }
    // TODO [fappel]: Workaround: icon reset does not work with buttons and
    //                IE. Because of this set icon explicitly to null.
    writer.call( "setIcon", PARAM_NULL );
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
    writer.dispose();
  }
  
  private void writeSelection( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    String property = ButtonLCAUtil.PROP_SELECTION;
    Boolean newValue = Boolean.valueOf( button.getSelection() ); 
    Boolean defValue = Boolean.FALSE;
    if( WidgetLCAUtil.hasChanged( button, property, newValue, defValue ) ) {
      if( newValue.booleanValue() ) {
        writer.call( JS_FUNC_ADD_STATE, PARAM_CHECKED );
      } else {
        writer.call( JS_FUNC_REMOVE_STATE, PARAM_CHECKED );
      }
    }
  }

  String getTypePoolId( final Button button ) throws IOException {
    return ButtonLCAUtil.getTypePoolId( button, 
                                        TYPE_POOL_ID_BORDER, 
                                        TYPE_POOL_ID_FLAT );
  }

  void createResetHandlerCalls( final String typePoolId ) throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.removeListener( JS_LISTENER_INFO.getEventType(),
                           JS_LISTENER_INFO.getJSListener() );
    writer.removeListener( JSConst.QX_EVENT_EXECUTE,
                           JS_FUNC_BUTTON_UTIL_ON_TOGGLE_EXECUTE );
    writer.call( JS_FUNC_REMOVE_STATE, PARAM_CHECKED );
    // reseting the default button in case of dispose is done by the JSWriter
//    ButtonLCAUtil.resetImage();
    ButtonLCAUtil.resetAlignment();
    ButtonLCAUtil.resetText();
    ControlLCAUtil.resetChanges();
  }
}
