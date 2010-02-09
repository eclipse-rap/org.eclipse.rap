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

import org.eclipse.rwt.lifecycle.JSWriter;
import org.eclipse.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.MenuItem;


final class BarMenuItemLCA extends MenuItemDelegateLCA {

  private static final String ITEM_TYPE_BAR = "bar";
  
  void preserveValues( final MenuItem menuItem ) {
    ItemLCAUtil.preserve( menuItem );
    MenuItemLCAUtil.preserveEnabled( menuItem );
    WidgetLCAUtil.preserveCustomVariant( menuItem );
    WidgetLCAUtil.preserveHelpListener( menuItem );
  }

  void readData( final MenuItem menuItem ) {
    WidgetLCAUtil.processHelp( menuItem );
  }

  void renderInitialization( final MenuItem menuItem ) throws IOException {
    MenuItemLCAUtil.newItem( menuItem, 
                             "org.eclipse.rwt.widgets.MenuItem", 
                             ITEM_TYPE_BAR );
  }

  // TODO [tb] the client-side menuItem supports images, but the menubar 
  //           ignores them in its layout, so it is currently not rendered  
  void renderChanges( final MenuItem menuItem ) throws IOException {
    writeText( menuItem );
    MenuItemLCAUtil.writeEnabled( menuItem );
    WidgetLCAUtil.writeCustomVariant( menuItem );
    WidgetLCAUtil.writeHelpListener( menuItem );
  }

  private static void writeText( final MenuItem menuItem ) throws IOException {
    String text = menuItem.getText();
    if( WidgetLCAUtil.hasChanged( menuItem, Props.TEXT, text ) ) {
      JSWriter writer = JSWriter.getWriterFor( menuItem );
      text = WidgetLCAUtil.escapeText( text, true );
      writer.set( "text", text );
    }
  }
}
