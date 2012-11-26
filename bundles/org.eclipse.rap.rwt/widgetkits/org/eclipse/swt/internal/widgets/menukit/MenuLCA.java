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

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Widget;


public final class MenuLCA extends AbstractWidgetLCA {

  private static final MenuBarLCA MENU_BAR_LCA = new MenuBarLCA();
  private static final PopupMenuLCA POPUP_MENU_LCA = new PopupMenuLCA();
  private static final DropDownMenuLCA DROP_DOWN_MENU_LCA = new DropDownMenuLCA();

  public void preserveValues( Widget widget ) {
    getDelegateLCA( widget ).preserveValues( ( Menu )widget );
  }

  public void readData( Widget widget ) {
    getDelegateLCA( widget ).readData( ( Menu )widget );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    getDelegateLCA( widget ).renderInitialization( ( Menu )widget );
  }

  public void renderChanges( Widget widget ) throws IOException {
    getDelegateLCA( widget ).renderChanges( ( Menu )widget );
  }

  public void renderDispose( Widget widget ) throws IOException {
    // TODO [tb] : The menu can currently not be destroyed automatically on the client
    ClientObjectFactory.getClientObject( widget ).destroy();
  }

  private static MenuDelegateLCA getDelegateLCA( Widget widget ) {
    MenuDelegateLCA result;
    Menu menu = ( Menu )widget;
    int style = menu.getStyle();
    if( ( style & SWT.BAR ) != 0 ) {
      result = MENU_BAR_LCA;
    } else if( ( style & SWT.DROP_DOWN ) != 0 ) {
      result = DROP_DOWN_MENU_LCA;
    } else {
      result = POPUP_MENU_LCA;
    }
    return result;
  }
}
