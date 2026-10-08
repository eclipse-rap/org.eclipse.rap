/*******************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.scrollbarkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.WidgetOperationHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ScrollBar;


public class ScrollBarOperationHandler extends WidgetOperationHandler<ScrollBar> {

  public ScrollBarOperationHandler( ScrollBar scrollBar ) {
    super( scrollBar );
  }

  @Override
  public void handleNotify( ScrollBar scrollBar, String eventName, JsonObject properties ) {
    if( EVENT_SELECTION.equals( eventName ) ) {
      handleNotifySelection( scrollBar );
    } else {
      super.handleNotify( scrollBar, eventName, properties );
    }
  }

  /*
   * PROTOCOL NOTIFY Selection
   */
  public void handleNotifySelection( ScrollBar scrollBar ) {
    scrollBar.notifyListeners( SWT.Selection, new Event() );
  }

}
