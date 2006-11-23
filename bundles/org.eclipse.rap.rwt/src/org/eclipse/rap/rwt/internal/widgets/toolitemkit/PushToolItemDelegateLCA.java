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
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.ToolItem;
import org.eclipse.rap.rwt.widgets.Widget;

public class PushToolItemDelegateLCA extends ToolItemDelegateLCA {
  // tool item functions as defined in org.eclipse.rap.rwt.ToolItemUtil
  private static final String CREATE_PUSH = 
    "org.eclipse.rap.rwt.ToolItemUtil.createToolItemPush";

  private final static JSListenerInfo JS_LISTENER_INFO = 
    new JSListenerInfo( JSConst.QX_EVENT_EXECUTE,
                        JSConst.JS_WIDGET_SELECTED,
                        JSListenerType.ACTION );

  public void delegateProcessAction( final Widget widget ) {
    processSelection( ( ToolItem )widget, null );
  }

  public void delegateRenderChanges( final Widget widget ) throws IOException {
    ToolItem push = ( ToolItem )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
    // bounds of the widget that was clicked -> In the SelectionEvent
    // for Button the bounds are undefined
    writer.updateListener( JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( push ) );
    writer.set( Props.TEXT, JSConst.QX_FIELD_LABEL, push.getText() );
    if( push.getImage() != null ) {
      writer.set( Props.IMAGE,
                  JSConst.QX_FIELD_ICON,
                  Image.getPath( push.getImage() ) );
    }
  }

  public void delegateRenderInitialization( final Widget widget ) 
    throws IOException {
    
    JSWriter writer = JSWriter.getWriterFor( widget );
    ToolItem push = ( ToolItem )widget;
    Object[] args = new Object[]{
      WidgetUtil.getId( push ),
      push.getParent()};
    writer.callStatic( CREATE_PUSH, args );
  }
}
