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

package org.eclipse.rap.rwt.internal.widgets.toolitemkit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.internal.widgets.ItemLCAUtil;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.ToolItem;

final class PushToolItemDelegateLCA extends ToolItemDelegateLCA {
  
  // tool item functions as defined in org.eclipse.rap.rwt.ToolItemUtil
  private static final String CREATE_PUSH 
    = "org.eclipse.rap.rwt.ToolItemUtil.createPush";

  private final static JSListenerInfo JS_LISTENER_INFO 
    = new JSListenerInfo( JSConst.QX_EVENT_EXECUTE,
                          JSConst.JS_WIDGET_SELECTED,
                          JSListenerType.ACTION );

  void preserveValues( final ToolItem toolItem ) {
    ToolItemLCAUtil.preserveValues( toolItem );
  }
  
  void readData( final ToolItem toolItem ) {
    ToolItemLCAUtil.processSelection( toolItem );
  }

  void renderInitialization( final ToolItem toolItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    Object[] args = new Object[]{
      WidgetUtil.getId( toolItem ),
      toolItem.getParent(),
      Boolean.valueOf( ( toolItem.getParent().getStyle() & RWT.FLAT ) != 0 )
    };
    writer.callStatic( CREATE_PUSH, args );
  }
  
  void renderChanges( final ToolItem toolItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( toolItem );
    ItemLCAUtil.writeChanges( toolItem );
    WidgetLCAUtil.writeFont( toolItem, toolItem.getParent().getFont() );
    // TODO [rh] could be optimized in that way, that qooxdoo forwards the
    //      right-click on a toolbar item to the toolbar iteself if the toolbar
    //      item does not have a context menu assigned
    WidgetLCAUtil.writeMenu( toolItem, toolItem.getParent().getMenu() );
    // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
    // bounds of the widget that was clicked -> In the SelectionEvent
    // for Button the bounds are undefined
    writer.updateListener( JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( toolItem ) );
    WidgetLCAUtil.writeToolTip( toolItem, toolItem.getToolTipText() );
  }
}
