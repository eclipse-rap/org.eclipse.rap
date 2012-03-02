/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.IMenuAdapter;
import org.eclipse.swt.widgets.Menu;


final class PopupMenuLCA extends MenuDelegateLCA {

  private static final String METHOD_SHOW_MENU = "showMenu";

  void preserveValues( Menu menu ) {
    MenuLCAUtil.preserveValues( menu );
  }

  void readData( Menu menu ) {
    MenuLCAUtil.readMenuEvent( menu );
    WidgetLCAUtil.processHelp( menu );
  }

  void renderInitialization( Menu menu ) throws IOException {
    MenuLCAUtil.renderInitialization( menu );
  }

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
      Map<String, Object> args = new HashMap<String, Object>();
      args.put( "x", Integer.valueOf( location.x ) );
      args.put( "y", Integer.valueOf( location.y ) );
      clientObject.call( METHOD_SHOW_MENU, args );
      menu.setVisible( false );
    }
  }
}
