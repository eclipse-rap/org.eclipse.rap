/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.widgets.toolitemkit;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.ItemLCAUtil;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.ToolItem;
import org.eclipse.rap.rwt.widgets.Widget;


final class ToolItemLCAUtil {
  
  private ToolItemLCAUtil() {
    // prevent instantiation
  }

  static void preserveValues( final ToolItem toolItem ) {
    ItemLCAUtil.preserve( toolItem );
    WidgetLCAUtil.preserveToolTipText( toolItem, toolItem.getToolTipText() );
    WidgetLCAUtil.preserveFont( toolItem, toolItem.getParent().getFont() );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( toolItem );
    boolean hasListener = SelectionEvent.hasListener( toolItem );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListener ) );
  }

  static void processSelection( final ToolItem toolItem ) {
    if( WidgetLCAUtil.wasEventSent( toolItem, JSConst.EVENT_WIDGET_SELECTED ) ) 
    {
      Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
      SelectionEvent event 
        = ToolItemLCAUtil.newSelectionEvent( toolItem, bounds, RWT.NONE );
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
                               true,
                               detail );
  }
}
