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

qx.Class.define( "org.eclipse.swt.TabUtil", {

  statics : {
    createTabItem : function( id, parentId, index ) {
      var tabButton = new qx.ui.pageview.tabview.Button();
      tabButton.setTabIndex( -1 );
      tabButton.setLabel( "(empty)" );
      tabButton.getLabelObject().setMode( "html" ); 
      tabButton.setLabel( "" );
      tabButton.setEnableElementFocus( false );
      tabButton.addEventListener( "changeFocused", 
                                  org.eclipse.swt.TabUtil._onTabItemChangeFocus );
      tabButton.addEventListener( "click", 
                                  org.eclipse.swt.TabUtil._onTabItemClick );
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var tabView = widgetManager.findWidgetById( parentId );
      tabView.getBar().addAt( tabButton, index );
      var tabViewPage = new qx.ui.pageview.tabview.Page( tabButton );
      tabView.getPane().add( tabViewPage );
      widgetManager.add( tabButton, id );
      widgetManager.add( tabViewPage, id + "pg" );
    },
    
    releaseTabItem : function( itemId ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var tabButton = widgetManager.findWidgetById( itemId );
      tabButton.removeEventListener( "changeFocused", 
                                     org.eclipse.swt.TabUtil._onTabItemChangeFocus );
      tabButton.removeEventListener( "click", 
                                     org.eclipse.swt.TabUtil._onTabItemClick );
      widgetManager.dispose( itemId + "pg" );
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

    onTabFolderKeyPress : function( evt ) {
      var folder = evt.getTarget();
      if( folder.classname == "qx.ui.pageview.tabview.TabView" ) {
        var manager = folder.getBar().getManager();
        var item = manager.getSelected();
        if( item != null ) {
          switch( evt.getKeyIdentifier() ) {
            case "Left":
              manager.selectPrevious( item );
              org.eclipse.swt.TabUtil.markTabItemFocused( folder, 
                                                          evt.getTarget() );
              evt.stopPropagation();
              break;
            case "Right":
              manager.selectNext( item );
              org.eclipse.swt.TabUtil.markTabItemFocused( folder, 
                                                          evt.getTarget() );
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
    },

    tabSelected : function( evt ) {
      var tab = evt.getTarget();
      if( !org_eclipse_rap_rwt_EventUtil_suspend && tab.getChecked() ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        // TODO [rst] Add item parameter in doWidgetSelected
        var itemId = widgetManager.findIdByWidget( tab );
        req.addParameter( "org.eclipse.swt.events.widgetSelected.item", 
                          itemId );
        var id = widgetManager.findIdByWidget( tab.getParent().getParent() );
        org.eclipse.swt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
      }
    }
  }
});
