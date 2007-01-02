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

package org.eclipse.rap.rwt.internal.widgets.menukit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.JSWriter;
import org.eclipse.rap.rwt.widgets.Menu;
import org.eclipse.rap.rwt.widgets.Widget;


public final class MenuLCA extends AbstractWidgetLCA {

  private static final MenuBarLCA MENU_BAR_LCA 
    = new MenuBarLCA();
  private static final PopupMenuLCA POPUP_MENU_LCA 
    = new PopupMenuLCA();
  private static final DropDownMenuLCA DROP_DOWN_MENU_LCA 
    = new DropDownMenuLCA();

  public void preserveValues( final Widget widget ) {
    getDelegateLCA( widget ).preserveValues( ( Menu )widget );
  }
  
  public void readData( final Widget widget ) {
    getDelegateLCA( widget ).readData( ( Menu )widget );
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    getDelegateLCA( widget ).renderInitialization( ( Menu )widget );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    getDelegateLCA( widget ).renderChanges( ( Menu )widget );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  private static MenuDelegateLCA getDelegateLCA( final Widget widget ) {
    MenuDelegateLCA result;
    Menu menu = ( Menu )widget;
    int style = menu.getStyle();
    if( ( style & RWT.BAR ) != 0 ) {
      result = MENU_BAR_LCA;
    } else if( ( style & RWT.DROP_DOWN ) != 0 ) {
      result = DROP_DOWN_MENU_LCA;
    } else {
      result = POPUP_MENU_LCA;
    }
    return result;
  }
}
