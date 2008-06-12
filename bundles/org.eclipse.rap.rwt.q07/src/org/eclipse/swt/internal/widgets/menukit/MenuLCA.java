/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rwt.lifecycle.JSWriter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Widget;


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

  public void createResetHandlerCalls( final String typePoolId ) 
    throws IOException 
  {
  }
  
  public String getTypePoolId( final Widget widget ) {
    return null;
  }
  
  private static MenuDelegateLCA getDelegateLCA( final Widget widget ) {
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
