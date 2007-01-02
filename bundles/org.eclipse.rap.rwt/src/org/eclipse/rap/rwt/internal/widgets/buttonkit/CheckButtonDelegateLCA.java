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
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.internal.widgets.ControlLCAUtil;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.Button;

final class CheckButtonDelegateLCA extends ButtonDelegateLCA {

  private static final String SELECTED_ITEM = "selectedItem";
  // radio functions as defined in org.eclipse.rap.rwt.CheckUtil
  private static final String WIDGET_SELECTED 
    = "org.eclipse.rap.rwt.ButtonUtil.checkSelected";
  private final JSListenerInfo JS_LISTENER_INFO 
    = new JSListenerInfo( JSConst.QX_EVENT_CHANGE_CHECKED,
                          WIDGET_SELECTED,
                          JSListenerType.ACTION );

  void readData( final Button button ) {
    if( WidgetUtil.wasEventSent( button, JSConst.EVENT_WIDGET_SELECTED ) ) {
      String value = WidgetUtil.readPropertyValue( button, SELECTED_ITEM );
      button.setSelection( new Boolean( value ).booleanValue() );
      ControlLCAUtil.processSelection( button, null, true );
    }
  }
  
  void renderInitialization( final Button button )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( button );
    writer.newWidget( "qx.ui.form.CheckBox" );
    writer.set( "checked", button.getSelection() );
  }

  // TODO [rh] qooxdoo checkBox cannot display images, should we ignore
  //      setImage() calles when style is RWT.CHECK?
  void renderChanges( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
    // bounds of the widget that was clicked -> In the SelectionEvent
    // for Button the bounds are undefined
    writer.updateListener( JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    ControlLCAUtil.writeChanges( button );
    writer.set( Props.TEXT, JSConst.QX_FIELD_LABEL, button.getText(), "" );
    writeAlignment( button );
  }
}