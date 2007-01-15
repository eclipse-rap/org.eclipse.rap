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
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.internal.widgets.ControlLCAUtil;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;

final class PushButtonDelegateLCA extends ButtonDelegateLCA {

  private final static JSListenerInfo JS_LISTENER_INFO = 
    new JSListenerInfo( JSConst.QX_EVENT_EXECUTE,
                        JSConst.JS_WIDGET_SELECTED,
                        JSListenerType.ACTION );

  void readData( final Button button ) {
    // TODO [rh] clarify whether bounds should be sent (last parameter)
    ControlLCAUtil.processSelection( button, null, true );
  }
  
  void renderInitialization( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    writer.newWidget( "qx.ui.form.Button" );
    ControlLCAUtil.writeStyleFlags( button );
  }

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
    writeImage( button );
  }
  
  private static void writeImage( final Button button ) throws IOException {
    if( WidgetUtil.hasChanged( button, Props.IMAGE, button.getImage(), null ) ) {
      String imagePath;
      if( button.getImage() == null ) {
        imagePath = "";
      } else {
        imagePath = Image.getPath( button.getImage() );
      }
      JSWriter writer = JSWriter.getWriterFor( button );
      writer.set( JSConst.QX_FIELD_ICON, imagePath );
    }
  }
}
