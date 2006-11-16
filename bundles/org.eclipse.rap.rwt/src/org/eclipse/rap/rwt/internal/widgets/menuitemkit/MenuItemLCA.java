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

package org.eclipse.rap.rwt.internal.widgets.menuitemkit;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.MenuItem;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.engine.service.ContextProvider;


public class MenuItemLCA extends AbstractWidgetLCA {

  private static final JSListenerInfo JS_LISTENER_INFO 
    = new JSListenerInfo( JSConst.QX_EVENT_EXECUTE, 
                          JSConst.JS_WIDGET_SELECTED, 
                          JSListenerType.ACTION );

  public void preserveValues( final Widget widget ) {
    MenuItem menuItem = ( MenuItem )widget;
    ItemLCAUtil.preserve( menuItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    boolean hasListener = SelectionEvent.hasListener( menuItem );
    adapter.preserve( Props.SELECTION_LISTENERS, 
                      Boolean.valueOf( hasListener ) );
  }
  
  public void readData( final Widget widget ) {
  }

  public void processAction( final Widget widget ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_WIDGET_SELECTED );
    if( WidgetUtil.getId( widget ).equals( id ) ) {
      SelectionEvent event = new SelectionEvent( widget, 
                                                 null,
                                                 SelectionEvent.WIDGET_SELECTED,
                                                 new Rectangle( 0, 0, 0, 0 ),
                                                 true,
                                                 RWT.NONE);
      event.processEvent();
    }
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    MenuItem menuItem = ( MenuItem )widget;
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    // the top-level items on a menu bar are always rendered as PUSH regardless
    // of their actual style
    if( isTopLevelMenuBarItem( menuItem ) ) {
      writer.newWidget( "qx.ui.menu.MenuBarButton" );
    } else if( ( menuItem.getStyle() & ( RWT.PUSH | RWT.CASCADE ) ) != 0 ) { 
      writer.newWidget( "qx.ui.menu.MenuButton" );
    } else if( ( menuItem.getStyle() & RWT.CHECK ) != 0 ) {
      // TODO [rh] preliminary: no idea if that actually works
      writer.newWidget( "qx.ui.menu.MenuCheckBox" );
    } else if( ( menuItem.getStyle() & RWT.RADIO ) != 0 ) {
      // TODO [rh] preliminary: no idea if that actually works
      writer.newWidget( "qx.ui.menu.MenuRadioButton" );
    } else if( ( menuItem.getStyle() & RWT.SEPARATOR ) != 0 ) {
      writer.newWidget( "qx.ui.menu.MenuSeparator" );
    }
    writer.call( menuItem.getParent(), "add", new Object[] { menuItem } );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    MenuItem menuItem = ( MenuItem )widget;
    if( ( menuItem.getStyle() & RWT.SEPARATOR ) == 0 ) {
      writer.set( Props.TEXT, "label", menuItem.getText() );
      // TODO [rh] the JSConst.JS_WIDGET_SELECTED does unnecessarily send
      //      bounds of the widget that was clicked -> In the SelectionEvent 
      //      for MenuItem the bounds are undefined
      writer.updateListener( JS_LISTENER_INFO, 
                             Props.SELECTION_LISTENERS, 
                             SelectionEvent.hasListener( menuItem ) );
    }
  }

  public void renderDispose( final Widget widget ) throws IOException {
  }

  private static boolean isTopLevelMenuBarItem( final MenuItem menuItem ) {
    return ( menuItem.getParent().getStyle() & RWT.BAR ) != 0;
  }
}
