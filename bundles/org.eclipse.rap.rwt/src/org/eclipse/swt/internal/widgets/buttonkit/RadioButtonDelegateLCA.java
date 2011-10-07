/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.buttonkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.events.DeselectionEvent;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.widgets.Button;


final class RadioButtonDelegateLCA extends ButtonDelegateLCA {

  void preserveValues( Button button ) {
    ButtonLCAUtil.preserveValues( button );
  }

  void readData( Button button ) {
    // [if] The selection event is based on the request "selection" parameter
    // and not on the selection event, because it is not possible to fire the
    // same event (Id) from javascript for two widgets (selected and unselected
    // radio button) at the same time.
    if( ButtonLCAUtil.readSelection( button ) ) {
      processSelectionEvent( button );
    }
    ControlLCAUtil.processMouseEvents( button );
    ControlLCAUtil.processKeyEvents( button );
    ControlLCAUtil.processMenuDetect( button );
    WidgetLCAUtil.processHelp( button );
  }

  void renderInitialization( Button button ) throws IOException {
    ButtonLCAUtil.renderInitialization( button );
  }

  void renderChanges( Button button ) throws IOException {
    ButtonLCAUtil.renderChanges( button );
  }

  private static void processSelectionEvent( Button button ) {
    if( SelectionEvent.hasListener( button ) ) {
      int type = SelectionEvent.WIDGET_SELECTED;
      SelectionEvent event;
      if( button.getSelection() ) {
        event = new SelectionEvent( button, null, type );
      } else {
        event = new DeselectionEvent( button, null, type );
      }
      event.stateMask = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
      event.processEvent();
    }
  }
}
