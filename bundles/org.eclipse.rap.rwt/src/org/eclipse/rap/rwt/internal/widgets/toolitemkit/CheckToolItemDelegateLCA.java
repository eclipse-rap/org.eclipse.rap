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
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.ToolItem;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.engine.service.ContextProvider;

public class CheckToolItemDelegateLCA extends ToolItemDelegateLCA {

  private static final String SELECTED_ITEM = "selectedItem"; 
  // check functions as defined in org.eclipse.rap.rwt.ButtonUtil
  private static final String WIDGET_SELECTED = 
    "org.eclipse.rap.rwt.ButtonUtil.checkSelected";
  // tool item functions as defined in org.eclipse.rap.rwt.ToolItemUtil
  private static final String CREATE_CHECK = 
    "org.eclipse.rap.rwt.ToolItemUtil.createToolItemCheckUtil";
  private final JSListenerInfo JS_LISTENER_INFO = 
    new JSListenerInfo( JSConst.QX_EVENT_CHANGE_CHECKED,
                        WIDGET_SELECTED,
                        JSListenerType.ACTION );

  public void delegateProcessAction( final Widget widget ) {
    ToolItem check = ( ToolItem )widget;
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_WIDGET_SELECTED );
    if( WidgetUtil.getId( check ).equals( id ) ) {
      String value = WidgetUtil.readPropertyValue( widget, SELECTED_ITEM );
      check.setSelection( new Boolean( value ).booleanValue() );
      processSelection( widget, null );
    }
  }

  public void delegateRenderInitialization( final Widget widget )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( widget );
    ToolItem check = ( ToolItem )widget;
    Object[] args = new Object[] {
      WidgetUtil.getId( check ),
      check.getParent()
    };
    writer.callStatic( CREATE_CHECK, args );
    writer.set( "checked", check.getSelection() );
  }

  public void delegateRenderChanges( final Widget widget ) throws IOException {
    ToolItem check = ( ToolItem )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
    // bounds of the widget that was clicked -> In the SelectionEvent
    // for ToolItem the bounds are undefined
    writer.updateListener( JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( check ) );
    writer.set( Props.TEXT, JSConst.QX_FIELD_LABEL, check.getText() );
    if( check.getImage()!=null ){
      writer.set( Props.IMAGE, JSConst.QX_FIELD_ICON, Image.getPath( check.getImage() ) );
    }
  }
}
