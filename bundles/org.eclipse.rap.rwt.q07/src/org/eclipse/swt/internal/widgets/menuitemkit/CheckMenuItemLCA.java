/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.menuitemkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.MenuItem;


final class CheckMenuItemLCA extends MenuItemDelegateLCA {

  private static final String ITEM_TYPE_CHECK = "check";

  void preserveValues( final MenuItem menuItem ) {
    ItemLCAUtil.preserve( menuItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuItem );
    boolean hasListener = SelectionEvent.hasListener( menuItem );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListener ) );
    adapter.preserve( MenuItemLCAUtil.PROP_SELECTION,
                      Boolean.valueOf( menuItem.getSelection() ) );
    MenuItemLCAUtil.preserveEnabled( menuItem );
    WidgetLCAUtil.preserveCustomVariant( menuItem );
    WidgetLCAUtil.preserveHelpListener( menuItem );
  }

  void readData( final MenuItem menuItem ) {
    String paramValue = WidgetLCAUtil.readPropertyValue( menuItem, "selection" );
    if( paramValue != null ) {
      menuItem.setSelection( Boolean.valueOf( paramValue ).booleanValue() );
    }
    ControlLCAUtil.processSelection( menuItem, null, false );
    WidgetLCAUtil.processHelp( menuItem );
    MenuItemLCAUtil.processArmEvent( menuItem );
  }

  void renderInitialization( final MenuItem menuItem ) throws IOException {
    MenuItemLCAUtil.newItem( menuItem,
                             "org.eclipse.rwt.widgets.MenuItem",
                             ITEM_TYPE_CHECK );
  }

  void renderChanges( final MenuItem menuItem ) throws IOException {
    MenuItemLCAUtil.writeImageAndText( menuItem );
    MenuItemLCAUtil.writeSelectionListener( menuItem );
    MenuItemLCAUtil.writeSelection( menuItem );
    MenuItemLCAUtil.writeEnabled( menuItem );
    WidgetLCAUtil.writeCustomVariant( menuItem );
    WidgetLCAUtil.writeHelpListener( menuItem );
  }
}
