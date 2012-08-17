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

qx.Class.define( "org.eclipse.swt.TabUtil", {

  statics : {
    createTabItem : function( id, parentId, index ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var tabFolder = widgetManager.findWidgetById( parentId );
      var tabItem = new qx.ui.pageview.tabview.Button();
      tabItem.setTabIndex( null );
      tabItem.setLabel( "(empty)" );
      tabItem.getLabelObject().setMode( "html" );
      tabItem.setLabel( "" );
      tabItem.setEnableElementFocus( false );
      tabItem.addEventListener( "changeFocused", org.eclipse.swt.TabUtil._onTabItemChangeFocus );
      tabItem.addEventListener( "changeChecked", org.eclipse.swt.TabUtil._onTabItemSelected );
      tabItem.addEventListener( "click", org.eclipse.swt.TabUtil._onTabItemClick );
      tabFolder.getBar().addAt( tabItem, index );
      var tabViewPage = new qx.ui.pageview.tabview.Page( tabItem );
      tabFolder.getPane().add( tabViewPage );
      widgetManager.add( tabViewPage, id + "pg" );
      return tabItem;
    },

    releaseTabItem : function( tabItem ) {
      var tabFolder = tabItem.getParent().getParent();
      tabItem.removeEventListener( "changeFocused", org.eclipse.swt.TabUtil._onTabItemChangeFocus );
      tabItem.removeEventListener( "changeChecked", org.eclipse.swt.TabUtil._onTabItemSelected );
      tabItem.removeEventListener( "click", org.eclipse.swt.TabUtil._onTabItemClick );
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var itemId = widgetManager.findIdByWidget( tabItem );
      widgetManager.dispose( itemId + "pg" );
      widgetManager.dispose( itemId );
    },

    _onTabItemChangeFocus : function( evt ) {
      // Focus the tabFolder the item belongs to when the item is focused
      if( evt.getTarget().getFocused() ) {
        evt.getTarget().getParent().getParent().focus();
      }
    },

    _onTabItemClick : function( evt ) {
      // Focus the tabFolder the item belongs to when the item is clicked
      var folder = evt.getTarget().getParent().getParent();
      if( !folder.getFocused() ) {
        folder.focus();
      }
    },

    _onTabItemSelected : function( evt ) {
      var tab = evt.getTarget();
      if( !org.eclipse.swt.EventUtil.getSuspended() && tab.getChecked() ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        // TODO [rst] Add item parameter in doWidgetSelected
        var itemId = widgetManager.findIdByWidget( tab );
        var req = org.eclipse.swt.Server.getInstance();
        req.addParameter( "org.eclipse.swt.events.widgetSelected.item", itemId );
        var id = widgetManager.findIdByWidget( tab.getParent().getParent() );
        org.eclipse.swt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
      }
    },

    onTabFolderKeyPress : function( evt ) {
      var folder = evt.getTarget();
      if( folder.classname == "qx.ui.pageview.tabview.TabView" ) {
        var manager = folder.getBar().getManager();
        var item = manager.getSelected();
        if( item != null ) {
          switch( evt.getKeyIdentifier() ) {
            case "Left":
              manager.selectPrevious( item );
              org.eclipse.swt.TabUtil.markTabItemFocused( folder, evt.getTarget() );
              evt.stopPropagation();
              break;
            case "Right":
              manager.selectNext( item );
              org.eclipse.swt.TabUtil.markTabItemFocused( folder, evt.getTarget() );
              evt.stopPropagation();
              break;
          }
        }
      }
    },

    onTabFolderChangeFocused : function( evt ) {
      var folder = evt.getTarget();
      var item = folder.getBar().getManager().getSelected();
      org.eclipse.swt.TabUtil.markTabItemFocused( folder, item );
    },

    markTabItemFocused : function( folder, item ) {
      var items = folder.getBar().getManager().getItems();
      for( var i = 0; i < items.length; i++ ) {
        items[i].removeState( "focused" );
      }
      // add state to the selected item if the tabFolder is focused
      if( item != null && folder.getFocused() ) {
        item.addState( "focused" );
      }
    }
  }
});
