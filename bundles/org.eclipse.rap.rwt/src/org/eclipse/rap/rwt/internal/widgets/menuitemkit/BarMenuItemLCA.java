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
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.internal.widgets.ItemLCAUtil;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.MenuItem;


final class BarMenuItemLCA extends MenuItemDelegateLCA {

  private static final Object[] RWT_FLAT = new Object[] { "rwt_FLAT" };

  private static final JSListenerInfo JS_LISTENER_INFO 
    = new JSListenerInfo( JSConst.QX_EVENT_EXECUTE, 
                          JSConst.JS_WIDGET_SELECTED, 
                          JSListenerType.ACTION );

  void preserveValues( final MenuItem menuItem ) {
    ItemLCAUtil.preserve( menuItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuItem );
    boolean hasListener = SelectionEvent.hasListener( menuItem );
    adapter.preserve( Props.SELECTION_LISTENERS, 
                      Boolean.valueOf( hasListener ) );
    adapter.preserve( Props.ENABLED,
                      Boolean.valueOf( menuItem.getEnabled() ) );
  }

  void readData( final MenuItem menuItem ) {
    ControlLCAUtil.processSelection( menuItem, null, false );
  }
  
  void renderInitialization( final MenuItem menuItem ) throws IOException {
    MenuItemLCAUtil.newItem( menuItem, "qx.ui.menubar.Button" );
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    writer.call( "addState", RWT_FLAT );
  }

  // TODO [rh] qooxdoo does not handle bar menu items with images, should
  //      we already ignore them when calling MenuItem#setImage()?
  void renderChanges( final MenuItem menuItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    writer.set( Props.TEXT, JSConst.QX_FIELD_LABEL, menuItem.getText(), "" );
    writer.updateListener( JS_LISTENER_INFO, 
                           Props.SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( menuItem ) );
    MenuItemLCAUtil.writeEnabled( menuItem );
  }
  
  void renderDispose( final MenuItem menuItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    writer.dispose();
  }
}
