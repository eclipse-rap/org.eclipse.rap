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

package org.eclipse.rap.rwt.internal.widgets.combokit;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.Combo;
import org.eclipse.rap.rwt.widgets.Widget;
import com.w4t.engine.service.ContextProvider;

public class ComboLCA extends AbstractWidgetLCA {
  
  private static final String SELECTED_ITEM = "selectedItem";
  // Constants for ComboUtil.js
  private static final String WIDGET_SELECTED = 
    "org.eclipse.rap.rwt.ComboUtil.widgetSelected";
  private static final String CREATE_COMBOBOX_ITEMS = 
    "org.eclipse.rap.rwt.ComboUtil.createComboBoxItems";
  
  private final JSListenerInfo JS_LISTENER_INFO 
    = new JSListenerInfo( JSConst.QX_EVENT_CHANGE_SELECTED, 
                          WIDGET_SELECTED, 
                          JSListenerType.ACTION );

  public void preserveValues( final Widget widget ) {
    Combo combo = ( Combo )widget;
    ControlLCAUtil.preserveValues( combo );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( SelectionEvent.hasListener( combo ) ) );
  }
  
  public void readData( final Widget widget ) {
    // TODO Auto-generated method stub
  }
  
  public void processAction( Widget widget ) {
    Combo combo = ( Combo )widget;
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_WIDGET_SELECTED );
    if( WidgetUtil.getId( combo ).equals( id ) ) {
      String value = WidgetUtil.readPropertyValue( widget, SELECTED_ITEM );
      combo.setSelectionIndex( new Integer( value ).intValue() );
      ControlLCAUtil.processSelection( ( Combo )widget, null );
    }
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.newWidget( "qx.ui.form.ComboBox" );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Combo combo = ( Combo )widget;
    JSWriter writer = JSWriter.getWriterFor( combo );
    ControlLCAUtil.writeBounds( combo );
    ControlLCAUtil.writeToolTip( combo );
    ControlLCAUtil.writeMenu( combo );
    Object[] params = new Object[]{
      WidgetUtil.getId( combo ),
      combo.getItems()
    };
    writer.callStatic( CREATE_COMBOBOX_ITEMS,
                       params );
    writer.updateListener( JS_LISTENER_INFO,
                           Props.SELECTION_LISTENERS,
                           SelectionEvent.hasListener( combo ) );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    // TODO Auto-generated method stub
  }
}
