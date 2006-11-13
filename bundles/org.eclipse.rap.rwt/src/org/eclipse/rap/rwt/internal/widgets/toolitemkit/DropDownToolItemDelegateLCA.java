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
import org.eclipse.rap.rwt.internal.widgets.ControlLCAUtil;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.Button;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.engine.service.ContextProvider;

public class DropDownToolItemDelegateLCA extends ToolItemDelegateLCA {

  private static final String SELECTED_ITEM = "selectedItem";
  // radio functions as defined in org.eclipse.rap.rwt.ButtonUtil
  private static final String WIDGET_SELECTED = 
    "org.eclipse.rap.rwt.ButtonUtil.widgetSelected";
  private final JSListenerInfo JS_LISTENER_INFO = 
    new JSListenerInfo( JSConst.QX_EVENT_CHANGE_CHECKED,
                        WIDGET_SELECTED,
                        JSListenerType.ACTION );

  public void delegateProcessAction( final Widget widget ) {
    Button button = ( Button )widget;
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_WIDGET_SELECTED );
    if( WidgetUtil.getId( button ).equals( id ) ) {
      String value = WidgetUtil.readPropertyValue( widget, SELECTED_ITEM );
      button.setSelection( new Boolean( value ).booleanValue() );
      ControlLCAUtil.processSelection( ( Button )widget, null );
    }
  }

  public void delegateRenderInitialization( final Widget widget )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "qx.ui.form.CheckBox" );
    Button button = ( Button )widget;
    writer.set( "checked", button.getSelection() );
  }

  public void delegateRenderChanges( final Widget widget ) throws IOException {
    Button button = ( Button )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
    // bounds of the widget that was clicked -> In the SelectionEvent
    // for Button the bounds are undefined
    writer.updateListener( JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    ControlLCAUtil.writeBounds( button );
    ControlLCAUtil.writeToolTip( button );
    ControlLCAUtil.writeMenu( button );
    writer.set( Props.TEXT, "label", button.getText() );
  }
}
