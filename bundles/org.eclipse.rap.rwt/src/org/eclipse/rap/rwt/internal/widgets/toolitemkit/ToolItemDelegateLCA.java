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
import javax.servlet.http.HttpServletRequest;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.lifecycle.JSConst;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.widgets.Item;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.engine.service.ContextProvider;


public abstract class ToolItemDelegateLCA {
  
  void renderInitialization( final Widget widget ) throws IOException {
    delegateRenderInitialization( widget );
  }

  public void processAction( final Widget widget ) {
    delegateProcessAction( widget );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    delegateRenderChanges( widget );
  }
  
  void processSelection( final Widget widget, final Item item ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_WIDGET_SELECTED );
    if( WidgetUtil.getId( widget ).equals( id ) ) {
      Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
      SelectionEvent event = newSelectionEvent( widget, bounds ,RWT.NONE);
      event.processEvent();
    }
  }

  abstract public void delegateRenderChanges( final Widget widget )
    throws IOException;

  abstract public void delegateProcessAction( final Widget widget );

  abstract public void delegateRenderInitialization( final Widget widget )
    throws IOException;
  
  
  /////////////////
  // helper methods
  
  final SelectionEvent newSelectionEvent( final Widget widget,
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
