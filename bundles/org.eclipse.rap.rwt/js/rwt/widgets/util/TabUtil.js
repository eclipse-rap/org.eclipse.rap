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

rwt.qx.Class.define( "rwt.widgets.util.TabUtil", {

  statics : {
    createTabItem : function( id, parentId, index ) {
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      var tabFolder = widgetManager.findWidgetById( parentId );
      var tabItem = new rwt.widgets.TabItem();
      tabItem.setTabIndex( null );
      tabItem.setLabel( "(empty)" );
      tabItem.getLabelObject().setMode( "html" );
      tabItem.setLabel( "" );
      tabItem.setEnableElementFocus( false );
      tabItem.addEventListener( "changeFocused", rwt.widgets.util.TabUtil._onTabItemChangeFocus );
      tabItem.addEventListener( "changeChecked", rwt.widgets.util.TabUtil._onTabItemSelected );
      tabItem.addEventListener( "click", rwt.widgets.util.TabUtil._onTabItemClick );
      tabFolder.getBar().addAt( tabItem, index );
      var tabViewPage = new rwt.widgets.base.TabFolderPage( tabItem );
      tabFolder.getPane().add( tabViewPage );
      widgetManager.add( tabViewPage, id + "pg" );
      return tabItem;
    },

    releaseTabItem : function( tabItem ) {
      var tabFolder = tabItem.getParent().getParent();
      tabItem.removeEventListener( "changeFocused", rwt.widgets.util.TabUtil._onTabItemChangeFocus );
      tabItem.removeEventListener( "changeChecked", rwt.widgets.util.TabUtil._onTabItemSelected );
      tabItem.removeEventListener( "click", rwt.widgets.util.TabUtil._onTabItemClick );
      var widgetManager = rwt.remote.WidgetManager.getInstance();
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
      if( !rwt.remote.EventUtil.getSuspended() && tab.getChecked() ) {
        var widgetManager = rwt.remote.WidgetManager.getInstance();
        var itemId = widgetManager.findIdByWidget( tab );
        var server = rwt.remote.Server.getInstance();
        var folder = tab.getParent().getParent();
        server.getRemoteObject( folder ).notify( "Selection", {
          "item" : itemId
        } );
      }
    },

    onTabFolderKeyPress : function( evt ) {
      var folder = evt.getTarget();
      if( folder.classname == "rwt.widgets.TabFolder" ) {
        var manager = folder.getBar().getManager();
        var item = manager.getSelected();
        if( item != null ) {
          switch( evt.getKeyIdentifier() ) {
            case "Left":
              manager.selectPrevious( item );
              rwt.widgets.util.TabUtil.markTabItemFocused( folder, evt.getTarget() );
              evt.stopPropagation();
              break;
            case "Right":
              manager.selectNext( item );
              rwt.widgets.util.TabUtil.markTabItemFocused( folder, evt.getTarget() );
              evt.stopPropagation();
              break;
          }
        }
      }
    },

    onTabFolderChangeFocused : function( evt ) {
      var folder = evt.getTarget();
      var item = folder.getBar().getManager().getSelected();
      rwt.widgets.util.TabUtil.markTabItemFocused( folder, item );
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
