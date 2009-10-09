/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.MenuManager", {
  type : "singleton",
  extend : qx.util.manager.Object,

  members :  {
    
    // Note: This function is called on alle mousedown and mouseup events,
    //       and also on "esc" and "tab" key-events and on window-blur.    
    update : function( target, eventType ) {
      var menus = this.getAll();
      var isMouseDown = eventType == "mousedown";
      var isMouseUp = eventType == "mouseup";
      var isMouseEvent = isMouseDown || isMouseUp; 
      var eventHasTarget = target != null;
      var targetHasMenu = eventHasTarget && target.getMenu && target.getMenu();
      var targetMenuBar = this._getMenuBar( target );

      for ( var hash in menus ) {
        var menu = menus[ hash ];                
        var hide = false;
        if ( menu.getAutoHide() ) {
          if ( !eventHasTarget || !isMouseEvent ) {
            hide = true; 
          } else {
            var targetIsOpener = menu.getOpener() === target;
            var isContextMenu = 
              !( menu.getOpener() instanceof org.eclipse.rwt.widgets.MenuItem );
            var isSameMenuBar = 
              targetMenuBar != null && targetMenuBar == this._getMenuBar( menu );  
            if(    isMouseDown 
                && !isSameMenuBar || ( isContextMenu && targetIsOpener ) 
            ) {
                hide = true;             
            } else {
              if( !( targetHasMenu || targetIsOpener ) ) {
                if (    ( !menu.isSubElement( target ) && isMouseDown )   
                     || ( menu.isSubElement( target, true) && isMouseUp )  
                ) {
                  hide = true;
                }
              }
            }
          } 
        } 
        if( hide ) menu.hide();
      }
    },
    
    _getMenuBar : function( widget ) {
      var menu = null;
      var menuBar = null;
      if( widget instanceof org.eclipse.rwt.widgets.MenuItem ) {
        menu = widget.getParentMenu();
      } else {
        if(    widget instanceof org.eclipse.rwt.widgets.Menu
            || widget instanceof org.eclipse.rwt.widgets.MenuBar
        ) {
          menu = widget;
        }
      }
      while ( menuBar == null && menu != null ) {
        if( menu instanceof org.eclipse.rwt.widgets.MenuBar ) {
          menuBar = menu;
        } else {
          var hasOpener = 
            menu.getOpener() instanceof org.eclipse.rwt.widgets.MenuItem;          
          var menu = hasOpener ? menu.getOpener().getParentMenu() : null;
        }
      }      
      return menuBar;
    }
    
  }
} );
