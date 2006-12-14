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
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.engine.service.ContextProvider;


public class RadioToolItemDelegateLCA extends ToolItemDelegateLCA {
  // tool item functions as defined in org.eclipse.rap.rwt.ToolItemUtil
  private static final String CREATE_RADIO 
    = "org.eclipse.rap.rwt.ToolItemUtil.createToolItemRadioButton";
  // radio functions as defined in org.eclipse.rap.rwt.ButtonUtil
  private static final String WIDGET_SELECTED 
    = "org.eclipse.rap.rwt.ButtonUtil.radioSelected";
  
  private final JSListenerInfo JS_LISTENER_INFO 
  = new JSListenerInfo( JSConst.QX_EVENT_CHANGE_SELECTED, 
                        WIDGET_SELECTED, 
                        JSListenerType.ACTION );
  
  public void delegateProcessAction( final Widget widget ) {
    ToolItem button = ( ToolItem )widget;
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_WIDGET_SELECTED );
    if( WidgetUtil.getId( button ).equals( id ) ) {
      Control[] children = button.getParent().getChildren();
      for( int i = 0; i < children.length; i++ ) {
        Widget child = children[ i ];
        if( ( child instanceof ToolItem )
            && ( ( child.getStyle() & RWT.RADIO ) != 0 ) )
        {
          ( ( ToolItem )child ).setSelection( false );
        }
      }
      button.setSelection( true );
      processSelection( widget, null );
    }
  }

  public void delegateRenderInitialization( final Widget widget ) 
    throws IOException {
    
    JSWriter writer = JSWriter.getWriterFor( widget );
    ToolItem toolItem = ( ToolItem )widget;
    ToolBar bar = toolItem.getParent();
    int myIndex = bar.indexOf( toolItem );
    ToolItem neighbour = null;
    if ( myIndex > 0 ) {
      neighbour = bar.getItem( myIndex - 1 );
      if( ( neighbour.getStyle() & RWT.RADIO ) == 0 ) {
        neighbour = null;
      }
    }
    
    Object[] args = new Object[] {
      WidgetUtil.getId( toolItem ),
      toolItem.getParent(),
      toolItem.getSelection() ? "true" : null,
      neighbour
    };
    writer.callStatic( CREATE_RADIO, args );
  }

  public void delegateRenderChanges( final Widget widget ) throws IOException {
    ToolItem button = ( ToolItem )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
    // bounds of the widget that was clicked -> In the SelectionEvent
    // for ToolItem the bounds are undefined
    writer.updateListener( "manager" ,
                           JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    writer.set( Props.TEXT, JSConst.QX_FIELD_LABEL, button.getText() );
    if( button.getImage() != null ) {
      writer.set( Props.IMAGE,
                  JSConst.QX_FIELD_ICON,
                  Image.getPath( button.getImage() ) );
    }
  }
}
