/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.MenuItem;


final class PushMenuItemLCA extends MenuItemDelegateLCA {

  private static final String ITEM_TYPE_PUSH = "push";
  private static final String ITEM_TYPE_CASCADE = "cascade";

  void preserveValues( MenuItem menuItem ) {
    ItemLCAUtil.preserve( menuItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menuItem );
    boolean hasListener = SelectionEvent.hasListener( menuItem );
    adapter.preserve( Props.SELECTION_LISTENERS, Boolean.valueOf( hasListener ) );
    MenuItemLCAUtil.preserveEnabled( menuItem );
    WidgetLCAUtil.preserveCustomVariant( menuItem );
    WidgetLCAUtil.preserveHelpListener( menuItem );
  }

  void readData( MenuItem menuItem ) {
    ControlLCAUtil.processSelection( menuItem, null, false );
    WidgetLCAUtil.processHelp( menuItem );
    MenuItemLCAUtil.processArmEvent( menuItem );
  }

  void renderInitialization( MenuItem menuItem ) throws IOException {
    String type = ( menuItem.getStyle() & SWT.CASCADE ) != 0 ? ITEM_TYPE_CASCADE : ITEM_TYPE_PUSH;
    MenuItemLCAUtil.newItem( menuItem, "org.eclipse.rwt.widgets.MenuItem", type );
  }

  void renderChanges( MenuItem menuItem ) throws IOException {
    MenuItemLCAUtil.writeImageAndText( menuItem );
    MenuItemLCAUtil.writeSelectionListener( menuItem );
    MenuItemLCAUtil.writeEnabled( menuItem );
    WidgetLCAUtil.writeCustomVariant( menuItem );
    WidgetLCAUtil.writeHelpListener( menuItem );
  }
}
