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

qx.OO.defineClass( "org.eclipse.rap.rwt.MenuUtil" );

org.eclipse.rap.rwt.MenuUtil.contextMenu = function( evt ) {
  var widget = evt.getTarget();
  var contextMenu = widget.getContextMenu();
  if( contextMenu != null ) {
    contextMenu.setLocation( evt.getPageX(), evt.getPageY() );
    contextMenu.show();
  }
};

org.eclipse.rap.rwt.MenuUtil.showMenu = function( menu , x , y ) {
  if( menu != null ) {
    menu.setLocation( x, y );
    menu.show();
  }
};

