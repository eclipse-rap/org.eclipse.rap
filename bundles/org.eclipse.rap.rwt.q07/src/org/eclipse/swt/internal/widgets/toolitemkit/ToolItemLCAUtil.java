/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;


final class ToolItemLCAUtil {

  private ToolItemLCAUtil() {
    // prevent instantiation
  }

  static void preserveValues( final ToolItem toolItem ) {
    ItemLCAUtil.preserve( toolItem );
    WidgetLCAUtil.preserveEnabled( toolItem, toolItem.getEnabled() );
    WidgetLCAUtil.preserveToolTipText( toolItem, toolItem.getToolTipText() );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( toolItem );
    preserveVisible( toolItem );
    boolean hasListener = SelectionEvent.hasListener( toolItem );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListener ) );
    adapter.preserve( Props.BOUNDS, toolItem.getBounds() );
  }

  static void processSelection( final ToolItem toolItem ) {
    if( WidgetLCAUtil.wasEventSent( toolItem, JSConst.EVENT_WIDGET_SELECTED ) )
    {
      Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
      SelectionEvent event
        = ToolItemLCAUtil.newSelectionEvent( toolItem, bounds, SWT.NONE );
      event.processEvent();
    }
  }

  static SelectionEvent newSelectionEvent( final Widget widget,
                                           final Rectangle bounds,
                                           final int detail )
  {
    return new SelectionEvent( widget,
                               null,
                               SelectionEvent.WIDGET_SELECTED,
                               bounds,
                               null,
                               true,
                               detail );
  }

  public static void writeVisible( final ToolItem item ) throws IOException {
    Object adapter = item.getAdapter( IToolItemAdapter.class );
    IToolItemAdapter tia = ( IToolItemAdapter )adapter;
    Boolean newValue = Boolean.valueOf( tia.getVisible() );
    Boolean defValue = Boolean.TRUE;
    JSWriter writer = JSWriter.getWriterFor( item );
    writer.set( Props.VISIBLE, JSConst.QX_FIELD_VISIBLE, newValue, defValue );
  }

  public static void preserveVisible( final ToolItem item ) {
    Object itemadapter = item.getAdapter( IToolItemAdapter.class );
    IToolItemAdapter tia = ( IToolItemAdapter )itemadapter;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    adapter.preserve( Props.VISIBLE, Boolean.valueOf( tia.getVisible() ) );
  }

  public static void writeBounds( final ToolItem toolItem ) throws IOException {
    Rectangle bounds = toolItem.getBounds();
    // [rst] Chevron-button is created as a separate widget on the client side
    if( ( toolItem.getStyle() & SWT.DROP_DOWN ) != 0 ) {
      bounds.width -= 15; // ToolItem#DROP_DOWN_ARROW_WIDTH
    }
    WidgetLCAUtil.writeBounds( toolItem, toolItem.getParent(), bounds );
  }
}
