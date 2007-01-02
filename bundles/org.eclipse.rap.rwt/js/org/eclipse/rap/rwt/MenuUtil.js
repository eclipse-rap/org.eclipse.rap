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

org.eclipse.rap.rwt.MenuUtil.checkMenuItemSelected = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = wm.findIdByWidget( evt.getTarget() );    
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addParameter( id + ".selection", evt.getTarget().getChecked() ); 
  }
}

org.eclipse.rap.rwt.MenuUtil.checkMenuItemSelectedAction = function( evt ) {
  org.eclipse.rap.rwt.MenuUtil.checkMenuItemSelected( evt );
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    org.eclipse.rap.rwt.EventUtil.widgetSelected( evt );
  }
}

  
org.eclipse.rap.rwt.MenuUtil.radioMenuItemSelected = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = wm.findIdByWidget( evt.getTarget() );    
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addParameter( id + ".selection", evt.getTarget().getChecked() ); 
  }
}

org.eclipse.rap.rwt.MenuUtil.radioMenuItemSelectedAction = function( evt ) {
  org.eclipse.rap.rwt.MenuUtil.radioMenuItemSelected( evt );
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    org.eclipse.rap.rwt.EventUtil.widgetSelected( evt );
  }
}

org.eclipse.rap.rwt.MenuUtil.createRadioManager = function( menuItem ) {
  var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var name = wm.findIdByWidget( menuItem ) + "RadioMgr";
  var manager = new qx.manager.selection.RadioManager( name );
  menuItem.setManager( manager );
}

org.eclipse.rap.rwt.MenuUtil.assignRadioManager = function( firstMenuItem, menuItem ) {
  var manager = firstMenuItem.getManager();
  menuItem.setManager( manager );
}

org.eclipse.rap.rwt.MenuUtil.disposeRadioMenuItem = function( menuItem ) {
  var manager = menuItem.getManager();
  manager.remove( menuItem );
  menuItem.dispose();
  if( manager.getItems().length == 0 ) {
    manager.dispose();
  }
}

