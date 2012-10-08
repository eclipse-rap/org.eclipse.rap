/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.toolitemkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.readEventPropertyValue;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.widgets.ToolItem;

final class DropDownToolItemLCA extends ToolItemDelegateLCA {

  @Override
  void preserveValues( ToolItem toolItem ) {
    ToolItemLCAUtil.preserveValues( toolItem );
  }

  @Override
  void readData( ToolItem toolItem ) {
    String eventName = ClientMessageConst.EVENT_SELECTED;
    if( WidgetLCAUtil.wasEventSent( toolItem, eventName ) ) {
      String detail = readEventPropertyValue( toolItem,
                                              eventName,
                                              ClientMessageConst.EVENT_PARAM_DETAIL );
      if( "arrow".equals( detail ) ) {
        Rectangle bounds = toolItem.getBounds();
        bounds.y += bounds.height;
        int stateMask = EventLCAUtil.readStateMask( toolItem, eventName );
        SelectionEvent event = new SelectionEvent( toolItem,
                                                   null,
                                                   SelectionEvent.WIDGET_SELECTED,
                                                   bounds,
                                                   stateMask,
                                                   null,
                                                   true,
                                                   SWT.ARROW );
        event.processEvent();
      } else {
        ToolItemLCAUtil.processSelection( toolItem );
      }
    }
  }

  @Override
  void renderInitialization( ToolItem toolItem ) throws IOException {
    ToolItemLCAUtil.renderInitialization( toolItem );
  }

  @Override
  void renderChanges( ToolItem toolItem ) throws IOException {
    ToolItemLCAUtil.renderChanges( toolItem );
  }
}
