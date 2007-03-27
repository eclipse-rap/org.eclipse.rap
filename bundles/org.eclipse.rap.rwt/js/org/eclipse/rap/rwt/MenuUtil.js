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

// Event listener for "contextmenu" event
org.eclipse.rap.rwt.MenuUtil.contextMenu = function( evt ) {
  var widget = evt.getTarget();
  var contextMenu = widget.getContextMenu();
  if( contextMenu != null ) {
    contextMenu.setLocation( evt.getPageX(), evt.getPageY() );
    contextMenu.setOpener( this );
    contextMenu.show();
  }
}

// Called to open a popup menu from server side
org.eclipse.rap.rwt.MenuUtil.showMenu = function( menu , x , y ) {
  if( menu != null ) {
    menu.setLocation( x, y );
    menu.show();
  }
}

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

org.eclipse.rap.rwt.MenuUtil.setMenuListener = function( menu, isset ) {
  if( isset ) {
    menu.addEventListener( "beforeAppear", org.eclipse.rap.rwt.MenuUtil._menuShown );
    menu.addEventListener( "disappear", org.eclipse.rap.rwt.MenuUtil._menuHidden );
  } else {
    menu.removeEventListener( "beforeAppear", org.eclipse.rap.rwt.MenuUtil._menuShown );
    menu.removeEventListener( "disappear", org.eclipse.rap.rwt.MenuUtil._menuHidden );
  }
}

/*
 * Called when menu is about to show. Sends menu event and shows only a
 * preliminary item until the response is received.
 */
org.eclipse.rap.rwt.MenuUtil._menuShown = function( evt ) {
  // create preliminary item  
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var preItem = this.getUserData( "preItem" );
    if( !preItem ) {
      preItem = new qx.ui.menu.Button();
      preItem.setLabel( "..." );
      preItem.setEnabled( false );
      this.add( preItem );
      this.setUserData( "preItem", preItem );
    }
    // hide all but the preliminary item
    var items = this.getLayout().getChildren();
    for( var i = 0; i < items.length; i++ ) {
      var item = items[ i ];
      item.setDisplay( false );
    }
    preItem.setDisplay( true );
    // send event
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = wm.findIdByWidget( this );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addEvent( "org.eclipse.rap.rwt.events.menuShown", id );
    req.send();
  }
}

/*
 * Called after menu has disappeared.
 */
org.eclipse.rap.rwt.MenuUtil._menuHidden = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = wm.findIdByWidget( this );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addEvent( "org.eclipse.rap.rwt.events.menuHidden", id );
    req.send();
  }
}

/*
 * Hides preliminary item and reveals the menu. Called by the response to a
 * menu shown event.
 */
org.eclipse.rap.rwt.MenuUtil.unhideMenu = function( menu ) {
  var items = menu.getLayout().getChildren();
  for( var i = 0; i < items.length; i++ ) {
    var item = items[ i ];
    item.setDisplay( true );
  }
  var preItem = menu.getUserData( "preItem" );
  if( preItem ) {
    preItem.setDisplay( false );
  }
}
