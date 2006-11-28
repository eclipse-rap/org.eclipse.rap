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

package org.eclipse.rap.rwt.internal.widgets.buttonkit;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.internal.widgets.ControlLCAUtil;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.engine.service.ContextProvider;


public class RadioButtonDelegateLCA extends ButtonDelegateLCA {

  private static final String CREATE_RADIO 
    = "org.eclipse.rap.rwt.ButtonUtil.createRadioButton";
  private static final String WIDGET_SELECTED 
    = "org.eclipse.rap.rwt.ButtonUtil.radioSelected";
  
  private final JSListenerInfo JS_LISTENER_INFO 
    = new JSListenerInfo( JSConst.QX_EVENT_CHANGE_SELECTED, 
                          WIDGET_SELECTED, 
                          JSListenerType.ACTION );
  
  public void delegateProcessAction( final Widget widget ) {
    Button button = ( Button )widget;
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_WIDGET_SELECTED );
    if( WidgetUtil.getId( button ).equals( id ) ) {
      Control[] children = button.getParent().getChildren();
      for( int i = 0; i < children.length; i++ ) {
        Control child = children[ i ];
        if( ( child instanceof Button )
            && ( ( child.getStyle() & RWT.RADIO ) != 0 ) )
        {
          ( ( Button )child ).setSelection( false );
        }
      }
      button.setSelection( true );
      ControlLCAUtil.processSelection( ( Button )widget, null );
    }
  }

  public void delegateRenderInitialization( final Widget widget ) 
    throws IOException {
    
    JSWriter writer = JSWriter.getWriterFor( widget );
    Button button = ( Button )widget;
    Object[] args = new Object[] {
      WidgetUtil.getId( button ),
      button.getParent(),
      button.getSelection() ? "true" : null
    };
    writer.callStatic( CREATE_RADIO, args );
  }

  public void delegateRenderChanges( final Widget widget ) throws IOException {
    Button button = ( Button )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
    // bounds of the widget that was clicked -> In the SelectionEvent
    // for Button the bounds are undefined
    writer.updateListener( "manager" ,
                           JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( button ) );
    ControlLCAUtil.writeChanges( button );
    writer.set( Props.TEXT, JSConst.QX_FIELD_LABEL, button.getText() );
    if( button.getImage() != null ) {
      writer.set( Props.IMAGE,
                  JSConst.QX_FIELD_ICON, 
                  Image.getPath( button.getImage() ) );
    }
  }
}
