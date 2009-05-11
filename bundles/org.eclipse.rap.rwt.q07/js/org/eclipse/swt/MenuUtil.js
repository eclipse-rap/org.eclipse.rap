/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.MenuUtil", {

  statics : {

    MENU_LISTENER_KEY : "hasMenuListener",

    setLabelMode : function( menuItem ) {
      // Note: called directly after creating the menuItem instance, therefore
      // it is not necessary to check getLabelObject and/or preserve its label
      menuItem.setLabel( "(empty)" );
      // TODO [rh] for some reason this workaround doesn't work for
      //      qx.ui.menubar.Button, the labelObject is still null
      if( menuItem.getLabelObject() != null ) {
        menuItem.getLabelObject().setMode( qx.constant.Style.LABEL_MODE_HTML );
      }
      menuItem.setLabel( "" );
    },

    // Event listener for "contextmenu" event
    contextMenu : function( evt ) {
      var widget = evt.getTarget();
      var contextMenu = widget.getContextMenu();
      if( contextMenu != null ) {
        contextMenu.setLocation( evt.getPageX(), evt.getPageY() );
        contextMenu.setOpener( this );
        contextMenu.show();
      }
    },

    // Called to open a popup menu from server side
    showMenu : function( menu, x, y ) {
      if( menu != null ) {
        menu.setLocation( x, y );
        menu.show();
      }
    },

    checkMenuItemSelected : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var id = wm.findIdByWidget( evt.getTarget() );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( id + ".selection", evt.getTarget().getChecked() );
      }
    },

    checkMenuItemSelectedAction : function( evt ) {
      org.eclipse.swt.MenuUtil.checkMenuItemSelected( evt );
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        org.eclipse.swt.EventUtil.widgetSelected( evt );
      }
    },

    radioMenuItemSelected : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var id = wm.findIdByWidget( evt.getTarget() );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( id + ".selection", evt.getTarget().getChecked() );
      }
    },

    radioMenuItemSelectedAction : function( evt ) {
      org.eclipse.swt.MenuUtil.radioMenuItemSelected( evt );
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        org.eclipse.swt.EventUtil.widgetSelected( evt );
      }
    },

    createRadioManager : function( menuItem ) {
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var name = wm.findIdByWidget( menuItem ) + "RadioMgr";
      var manager = new qx.ui.selection.RadioManager( name );
      menuItem.setManager( manager );
    },

    assignRadioManager : function( firstMenuItem, menuItem ) {
      var manager = firstMenuItem.getManager();
      menuItem.setManager( manager );
    },

    disposeRadioMenuItem : function( menuItem ) {
      var manager = menuItem.getManager();
      manager.remove( menuItem );
      menuItem.setDisplay( false );
      menuItem.dispose();
      if( manager.getItems().length == 0 ) {
        manager.dispose();
      }
    },

    initialize : function( menu ) {
      menu.addEventListener( "beforeAppear",
                             org.eclipse.swt.MenuUtil._menuShown );
      menu.addEventListener( "disappear",
                             org.eclipse.swt.MenuUtil._menuHidden );
    },

    setMenuListener : function( menu, isset ) {
      menu.setUserData( org.eclipse.swt.MenuUtil.MENU_LISTENER_KEY, isset );
    },

    /*
     * Called when menu is about to show. Sends menu event and shows only a
     * preliminary item until the response is received.
     */
    _menuShown : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        if( this.getUserData( org.eclipse.swt.MenuUtil.MENU_LISTENER_KEY ) ) {
          // create preliminary item
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
          // TODO [rst] Ensure minimal width for preliminary item, remove
          //            when menu width is calculated on the client
          if( this.getWidth() < 60 ) {
            this.setWidth( 60 );
          }
          this.setDisplay( true );
          // send event
          var wm = org.eclipse.swt.WidgetManager.getInstance();
          var id = wm.findIdByWidget( this );
          var req = org.eclipse.swt.Request.getInstance();
          req.addEvent( "org.eclipse.swt.events.menuShown", id );
          req.send();
        } else {
          var items = this.getLayout().getChildren();
          this.setDisplay( items.length != 0 );
        }
      }
    },

    /*
     * Called after menu has disappeared.
     */
    _menuHidden : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        if( this.getUserData( org.eclipse.swt.MenuUtil.MENU_LISTENER_KEY ) ) {
          var wm = org.eclipse.swt.WidgetManager.getInstance();
          var id = wm.findIdByWidget( this );
          var req = org.eclipse.swt.Request.getInstance();
          req.addEvent( "org.eclipse.swt.events.menuHidden", id );
          req.send();
        }
      }
    },

    /*
     * Hides preliminary item and reveals the menu. Called by the response to a
     * menu shown event.
     */
    unhideMenu : function( menu, reveal ) {
      // [ad] Fix for Bug 216907 Context menu with no menu items
      // If there are no menu items, the menu will be hide.
      if( reveal ) {
        var items = menu.getLayout().getChildren();
        for( var i = 0; i < items.length; i++ ) {
          var item = items[ i ];
          item.setDisplay( true );
        }
        var preItem = menu.getUserData( "preItem" );
        if( preItem ) {
          preItem.setDisplay( false );
        }
        menu.show();
      } else {
        menu.hide();
      }
    }
  }
});
