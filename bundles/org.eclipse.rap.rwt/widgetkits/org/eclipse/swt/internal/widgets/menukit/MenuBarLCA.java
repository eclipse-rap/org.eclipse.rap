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

import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Menu;


final class MenuBarLCA extends MenuDelegateLCA {

  private static final String PROP_PARENT = "parent";

  void preserveValues( Menu menu ) {
    MenuLCAUtil.preserveValues( menu );
    preserveProperty( menu, PROP_PARENT, getParent( menu ) );
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
    renderProperty( menu, PROP_PARENT, getParent( menu ), null );
    renderBounds( menu );
  }

  private static void renderBounds( Menu menu ) {
    Decorations parent = getParent( menu );
    if( parent != null ) {
      // Bounds are preserved in ShellLCA#preserveMenuBounds
      WidgetLCAUtil.renderBounds( menu, getBounds( menu ) );
    }
  }

  //////////////////
  // Helping methods

  private static Decorations getParent( Menu menu ) {
    Decorations result = null;
    if( menu.getParent().getMenuBar() == menu ) {
      result = menu.getParent();
    }
    return result;
  }

  private static Rectangle getBounds( Menu menu ) {
    Rectangle result = new Rectangle( 0, 0, 0, 0 );
    Decorations parent = getParent( menu );
    if( parent != null ) {
      IShellAdapter shellAdapter = parent.getAdapter( IShellAdapter.class );
      result = shellAdapter.getMenuBounds();
    }
    return result;
  }
}
