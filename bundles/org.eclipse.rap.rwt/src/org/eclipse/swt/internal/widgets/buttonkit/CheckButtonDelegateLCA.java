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

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.widgets.Button;


final class CheckButtonDelegateLCA extends ButtonDelegateLCA {

  static final String PROP_GRAYED = "grayed";

  void preserveValues( Button button ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    adapter.preserve( PROP_GRAYED, Boolean.valueOf( button.getGrayed() ) );
    ButtonLCAUtil.preserveValues( button );
  }

  void readData( Button button ) {
    ButtonLCAUtil.readSelection( button );
    ControlLCAUtil.processSelection( button, null, true );
    ControlLCAUtil.processMouseEvents( button );
    ControlLCAUtil.processKeyEvents( button );
    ControlLCAUtil.processMenuDetect( button );
    WidgetLCAUtil.processHelp( button );
  }

  void renderInitialization( Button button ) throws IOException {
    ButtonLCAUtil.renderInitialization( button );
  }

  void renderChanges( Button button ) throws IOException {
    // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
    // bounds of the widget that was clicked -> In the SelectionEvent
    // for Button the bounds are undefined
    ButtonLCAUtil.renderChanges( button );
    renderGrayed( button );
  }

  private static void renderGrayed( Button button ) {
    Boolean newValue = Boolean.valueOf( button.getGrayed() );
    if( WidgetLCAUtil.hasChanged( button, PROP_GRAYED, newValue, Boolean.FALSE ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( button );
      clientObject.setProperty( PROP_GRAYED, newValue );
    }
  }
}
