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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.Button;

final class CheckButtonDelegateLCA extends ButtonDelegateLCA {

  // check box event listner function, defined in org.eclipse.swt.ButtonUtil
  private static final String WIDGET_SELECTED 
    = "org.eclipse.swt.ButtonUtil.checkSelected";
  
  private final JSListenerInfo JS_LISTENER_INFO 
    = new JSListenerInfo( JSConst.QX_EVENT_CHANGE_CHECKED,
                          WIDGET_SELECTED,
                          JSListenerType. STATE_AND_ACTION );

  void preserveValues( final Button button ) {
    ButtonLCAUtil.preserveValues( button );
  }

  void readData( final Button button ) {
    ButtonLCAUtil.readSelection( button );
    ControlLCAUtil.processSelection( button, null, true );
  }
  
  void renderInitialization( final Button button )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( button );
    writer.newWidget( "qx.ui.form.CheckBox" );
    writer.set( JSConst.QX_FIELD_APPEARANCE, "checkbox" );
    ControlLCAUtil.writeStyleFlags( button );
  }

  // TODO [rh] qooxdoo checkBox cannot display images, should we ignore
  //      setImage() calls when style is SWT.CHECK?
  void renderChanges( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
    // bounds of the widget that was clicked -> In the SelectionEvent
    // for Button the bounds are undefined
    writer.updateListener( JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    ControlLCAUtil.writeChanges( button );
    ButtonLCAUtil.writeSelection( button );
    ButtonLCAUtil.writeText( button );
    ButtonLCAUtil.writeAlignment( button );
  }

  void renderDispose( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    writer.dispose();
  }
}