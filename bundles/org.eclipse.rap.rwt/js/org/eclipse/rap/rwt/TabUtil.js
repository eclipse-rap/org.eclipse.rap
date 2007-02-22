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

qx.OO.defineClass( "org.eclipse.rap.rwt.TabUtil" );

org.eclipse.rap.rwt.TabUtil.createTabItem = function( id, parent ) {
  var tabButton = new qx.ui.pageview.tabview.Button();
  tabButton.setTabIndex( -1 );
  // TODO [rh] remove event listener on dispose
  tabButton.addEventListener( "keypress", 
                              org.eclipse.rap.rwt.TabUtil._onTabItemKeyPress );
  var tabView
    = org.eclipse.rap.rwt.WidgetManager.getInstance().findWidgetById( parent );
  tabView.getBar().add( tabButton );
  tabButton.tabView = tabView;
  var tabViewPage = new qx.ui.pageview.tabview.Page( tabButton );
  tabView.getPane().add( tabViewPage );
  
  org.eclipse.rap.rwt.WidgetManager.getInstance().add( tabButton, id );
  org.eclipse.rap.rwt.WidgetManager.getInstance().add( tabViewPage, id + "pg" );
}

org.eclipse.rap.rwt.TabUtil._onTabItemKeyPress = function( evt ) {
  /*
  var item = evt.getTarget();
  switch( evt.getKeyIdentifier() ) {
    case "Left":
      var prev = item.getPreviousActiveSibling();
      if( prev && prev != this ) {
        prev.setChecked( true );
      }
      evt.stopPropagation();
      break;
    case "Right":
      var next = this.getNextActiveSibling();
      if( next && next != this ) {
        next.setChecked( true );
      }
      evt.stopPropagation();
      break;
  }
  */
}

org.eclipse.rap.rwt.TabUtil.onTabFolderKeyPress = function( evt ) {
  var folder = evt.getTarget();
  if( folder.classname != "qx.ui.pageview.tabview.TabView" ) {
    return;
  }
org.eclipse.rap.rwt.Request.getInstance().debug( "folder: " + folder );  
  var manager = folder.getBar().getManager();
  var item = manager.getSelected();
folder.debug( "item: " + item )  ;
  if( item == null ) {
    return;
  }
  switch( evt.getKeyIdentifier() ) {
    case "Left":
      manager.selectPrevious( item );
      evt.stopPropagation();
      break;
    case "Right":
      manager.selectNext( item );
      evt.stopPropagation();
      break;
  }
}

org.eclipse.rap.rwt.TabUtil._onTabItemChangeFocus = function( evt ) {
  // Focus the tabFolder the item belongs to when the item is focused
  if( evt.getTarget().getFocused() ) {
    evt.getTarget().getParent().getParent().focus();
  }
}

/**
 * Listener for change of property enabled, passes enablement to children
 * TODO: [rst] Once qx can properly disable a TabView, this listener can be removed
 */
org.eclipse.rap.rwt.TabUtil.onChangeEnabled = function( evt ) {
  var enabled = evt.getData();
  var items = this._bar.getChildren();
  for( var i = 0; i < items.length; i++ ) {
    var item = items[ i ];
    item.setEnabled( enabled );
  }
}

org.eclipse.rap.rwt.TabUtil.tabSelected = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var req = org.eclipse.rap.rwt.Request.getInstance();
    var id = widgetManager.findIdByWidget( evt.getTarget() );
    req.addParameter( id + ".checked", evt.getTarget().getChecked() );
  }
}

org.eclipse.rap.rwt.TabUtil.tabSelectedAction = function( evt ) {
  org.eclipse.rap.rwt.TabUtil.tabSelected( evt );
  if( !org_eclipse_rap_rwt_EventUtil_suspend && evt.getTarget().getChecked() ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( evt.getTarget().tabView );
    org.eclipse.rap.rwt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
  }
}