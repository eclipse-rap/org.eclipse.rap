/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ToolItem;

final class DropDownToolItemLCA extends ToolItemDelegateLCA {

  private static final String PARAM_DROPDOWN = "dropDown";

  void preserveValues( final ToolItem toolItem ) {
    ToolItemLCAUtil.preserveValues( toolItem );
    ToolItemLCAUtil.preserveImages( toolItem );
  }

  void readData( final ToolItem toolItem ) {
    if( WidgetLCAUtil.wasEventSent( toolItem, JSConst.EVENT_WIDGET_SELECTED ) ) 
    {
      HttpServletRequest request = ContextProvider.getRequest();
      String detail
        = request.getParameter( JSConst.EVENT_WIDGET_SELECTED_DETAIL );
      if( "arrow".equals( detail ) ) {
        Rectangle bounds = toolItem.getBounds();
        bounds.y += bounds.height;
        SelectionEvent event
        = ToolItemLCAUtil.newSelectionEvent( toolItem, bounds, SWT.ARROW );
        event.processEvent();
      } else {
        ToolItemLCAUtil.processSelection( toolItem );
      }
    }
  }

  void renderInitialization( final ToolItem toolItem ) throws IOException {
    ToolItemLCAUtil.renderInitialization( toolItem, PARAM_DROPDOWN );
  }

  void renderChanges( final ToolItem toolItem ) throws IOException {
    ToolItemLCAUtil.renderChanges( toolItem );
  }
}
