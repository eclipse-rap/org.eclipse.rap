/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets.hyperlinkkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.ControlOperationHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.forms.widgets.Hyperlink;


@SuppressWarnings("restriction")
public class HyperlinkOperationHandler extends ControlOperationHandler<Hyperlink> {

  public HyperlinkOperationHandler( Hyperlink hyperlink ) {
    super( hyperlink );
  }

  @Override
  public void handleNotify( Hyperlink hyperlink, String eventName, JsonObject properties ) {
    if( EVENT_DEFAULT_SELECTION.equals( eventName ) ) {
      handleNotifyDefaultSelection( hyperlink, properties );
    } else {
      super.handleNotify( hyperlink, eventName, properties );
    }
  }

  /*
   * PROTOCOL NOTIFY DefaultSelection
   *
   * @param altKey (boolean) true if the ALT key was pressed
   * @param ctrlKey (boolean) true if the CTRL key was pressed
   * @param shiftKey (boolean) true if the SHIFT key was pressed
   */
  public void handleNotifyDefaultSelection( Hyperlink hyperlink, JsonObject properties ) {
    Event event = createSelectionEvent( SWT.DefaultSelection, properties );
    hyperlink.notifyListeners( SWT.DefaultSelection, event );
  }

}
