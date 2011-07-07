/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.menukit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.JSWriter;
import org.eclipse.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.IMenuAdapter;
import org.eclipse.swt.widgets.Menu;

final class PopupMenuLCA extends MenuDelegateLCA {

  void preserveValues( final Menu menu ) {
    MenuLCAUtil.preserveEnabled( menu );
    MenuLCAUtil.preserveMenuListener( menu );
    MenuLCAUtil.preserveWidth( menu );
    WidgetLCAUtil.preserveCustomVariant( menu );
    WidgetLCAUtil.preserveHelpListener( menu );
  }

  void readData( final Menu menu ) {
    MenuLCAUtil.readMenuEvent( menu );
    WidgetLCAUtil.processHelp( menu );
  }

  void renderInitialization( final Menu menu ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( menu );
    writer.newWidget( "org.eclipse.rwt.widgets.Menu" );
  }

  void renderChanges( final Menu menu ) throws IOException {
    WidgetLCAUtil.writeCustomVariant( menu );
    writeShow( menu );
    MenuLCAUtil.writeEnabled( menu );
    MenuLCAUtil.writeMenuListener( menu );
    MenuLCAUtil.writeUnhideMenu( menu );
    WidgetLCAUtil.writeHelpListener( menu );
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
      writer.call( "showMenu", args );
      menu.setVisible( false );
    }
  }
}
