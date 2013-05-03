/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.menukit;

import java.io.IOException;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.IMenuAdapter;
import org.eclipse.swt.widgets.Menu;


final class PopupMenuLCA extends MenuDelegateLCA {

  private static final String METHOD_SHOW_MENU = "showMenu";

  @Override
  void preserveValues( Menu menu ) {
    MenuLCAUtil.preserveValues( menu );
  }

  @Override
  void readData( Menu menu ) {
    MenuLCAUtil.readMenuEvent( menu );
    WidgetLCAUtil.processHelp( menu );
  }

  @Override
  void renderInitialization( Menu menu ) throws IOException {
    MenuLCAUtil.renderInitialization( menu );
  }

  @Override
  void renderChanges( Menu menu ) throws IOException {
    MenuLCAUtil.renderChanges( menu );
    renderShow( menu );
    MenuLCAUtil.renderUnhideItems( menu );
  }

  private static void renderShow( Menu menu ) {
    if( menu.isVisible() ) {
      IMenuAdapter adapter = menu.getAdapter( IMenuAdapter.class );
      Point location = adapter.getLocation();
      IClientObject clientObject = ClientObjectFactory.getClientObject( menu );
      JsonObject parameters = new JsonObject()
        .add( "x", location.x )
        .add( "y", location.y );
      clientObject.call( METHOD_SHOW_MENU, parameters );
      menu.setVisible( false );
    }
  }

}
