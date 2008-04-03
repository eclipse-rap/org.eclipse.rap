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

package org.eclipse.swt.internal.widgets.menukit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.IMenuAdapter;
import org.eclipse.swt.widgets.Menu;

final class PopupMenuLCA extends MenuDelegateLCA {

  private static final String PROP_ENABLED = "enabled";

  private static final String SHOW_MENU 
    = "org.eclipse.swt.MenuUtil.showMenu";
  
  void preserveValues( final Menu menu ) {
    // TODO [rh] extract method and move to MenuLCAUtil
    IWidgetAdapter adapter = WidgetUtil.getAdapter( menu );
    adapter.preserve( PROP_ENABLED, Boolean.valueOf( menu.getEnabled() ) );
    MenuLCAUtil.preserveMenuListener( menu );
    MenuLCAUtil.preserveWidth( menu );
  }
  
  void readData( final Menu menu ) {
    MenuLCAUtil.readMenuEvent( menu );
  }
  
  void renderInitialization( final Menu menu ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menu );
    writer.newWidget( "qx.ui.menu.Menu" );
    writer.call( "addToDocument", null );
    WidgetLCAUtil.writeCustomVariant( menu );
  }

  void renderChanges( final Menu menu ) throws IOException {
    writeShow( menu );
    MenuLCAUtil.writeEnabled( menu );
    MenuLCAUtil.writeMenuListener( menu );
    MenuLCAUtil.writeUnhideMenu( menu );
    MenuLCAUtil.writeWidth( menu );
  }

  private static void writeShow( final Menu menu ) throws IOException {
    if( menu.isVisible() ) {
      JSWriter writer = JSWriter.getWriterFor( menu );
      IMenuAdapter adapter 
        = ( IMenuAdapter )menu.getAdapter( IMenuAdapter.class );
      Point location = adapter.getLocation();
      Object[] args = new Object[] {
        menu,
        new Integer( location.x ),
        new Integer( location.y )
      };
      writer.callStatic( SHOW_MENU, args );
      menu.setVisible( false );  
    }
  }
}
