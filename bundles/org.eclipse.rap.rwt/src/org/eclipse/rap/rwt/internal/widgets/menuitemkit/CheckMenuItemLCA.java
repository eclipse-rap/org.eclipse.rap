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


final class CheckMenuItemLCA extends MenuItemDelegateLCA {

  private static final String PROP_SELECTION = "selection";
  
  private static final JSListenerInfo JS_LISTENER_INFO 
    = new JSListenerInfo( JSConst.QX_EVENT_EXECUTE, 
                          "org.eclipse.rap.rwt.MenuUtil.checkMenuItemSelected", 
                          JSListenerType.STATE_AND_ACTION );

  void preserveValues( final MenuItem menuItem ) {
    ItemLCAUtil.preserve( menuItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuItem );
    boolean hasListener = SelectionEvent.hasListener( menuItem );
    adapter.preserve( Props.SELECTION_LISTENERS, 
                      Boolean.valueOf( hasListener ) );
    adapter.preserve( PROP_SELECTION, 
                      Boolean.valueOf( menuItem.getSelection() ) );
  }

  void readData( final MenuItem menuItem ) {
    String paramValue = WidgetLCAUtil.readPropertyValue( menuItem, "selection" );
    if( paramValue != null ) {
      menuItem.setSelection( Boolean.valueOf( paramValue ).booleanValue() );
    }
    ControlLCAUtil.processSelection( menuItem, null, false );
  }

  void renderInitialization( final MenuItem menuItem ) throws IOException {
    newItem( menuItem, "qx.ui.menu.MenuCheckBox" );
  }

  void renderChanges( final MenuItem menuItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    // TODO [rh] qooxdoo does not handle check menu items with images, should
    //      we already ignore them when calling MenuItem#setImage()?
    ItemLCAUtil.writeChanges( menuItem );
    writer.updateListener( JS_LISTENER_INFO, 
                           Props.SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( menuItem ) );
    writer.set( PROP_SELECTION, 
                "checked", 
                Boolean.valueOf( menuItem.getSelection() ), 
                Boolean.FALSE );
  }
  
  void renderDispose( final MenuItem menuItem ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menuItem );
    writer.dispose();
  }
}
