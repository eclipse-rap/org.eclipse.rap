/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define("org.eclipse.rwt.widgets.Menu", {  
  extend : qx.ui.popup.Popup,

  construct : function() {
    this.base( arguments );
    this._layout = null;
    this._preItem = null;
    this._hasListener = false;
    this._maxCellWidths = null;
    this._menuLayoutScheduled = false;
    this._opener = null;
    this._hoverItem = null;
    this._openTimer = null;
    this._closeTimer = null;
    this._openItem = null;
    this._itemsHiddenFlag = false;
    this._hoverFirstItemFlag = false;
    this.setHeight( "auto" );
    this.setWidth( "auto" );
    this._maxCellWidths = [ null, null, null, null ];
    this._layout = new qx.ui.layout.VerticalBoxLayout();
    this._layout.set( {
      top : 0,
      right : 0,
      bottom : 0,
      left : 0,
      anonymous : true
    } );    
    this.add( this._layout );
    this.addEventListener( "mousedown", this._unhoverSubMenu );
    this.addEventListener( "mouseout", this._onMouseOut );
    this.addEventListener( "mouseover", this._onMouseOver );
    this.addEventListener( "keypress", this._onKeyPress );    
    this._openTimer = new qx.client.Timer( 250 );
    this._openTimer.addEventListener( "interval", this._onopentimer, this );
    this._closeTimer = new qx.client.Timer( 250 );
    this._closeTimer.addEventListener( "interval", this._onclosetimer, this );
    this.addToDocument();    
  },

  destruct : function() {
    this._disposeObjects( "_openTimer", "_closeTimer", "_preItem" );
    this._disposeFields( "_lastActive", 
                         "_lastFocus", 
                         "_layout", 
                         "_opener", 
                         "_hoverItem", 
                         "_openItem" );
  },
  
  statics : {
    
    contextMenuHandler : function( event ) {
      var widget = event.getTarget();
      var contextMenu = widget.getContextMenu();
      if( contextMenu != null ) {
        contextMenu.setLocation( event.getPageX(), event.getPageY() );
        contextMenu.setOpener( this );
        contextMenu.show();
      }
    },
    
    getAllowContextMenu : function( target, domTarget ) {
      var result = false;
      switch( target.classname ) {
        case "qx.ui.form.TextField":
        case "qx.ui.form.TextArea":
        case "qx.ui.form.PasswordField":
          // NOTE: "enabled" can be "inherit", so it is not always a boolean
          if( target.getEnabled() != false && !target.getReadOnly() ) {
            var tagName =   typeof domTarget.tagName == "string" 
                          ? domTarget.tagName 
                          : "";
            if( tagName.toUpperCase() != "DIV" ) {
              result = target.getContextMenu() == null;
            }
          }
        break;
      }
      return result;
    }
    
  },
  
  properties :  {
    appearance : {
      refine : true,
      init : "menu"
    }
  },

  members : {

    /////////
    // Opener
    
    setOpener : function( value ) {
      this._opener = value;  
    },
    
    getOpener : function( value ) {
      return this._opener;  
    },    
    
    // Overwritten:
    getFocusRoot : function() {
      var root = null;
      if ( this._opener ) {
        root = this._opener.getFocusRoot();
      } else if( this._hasParent ) {
        root = this.getParent().getFocusRoot();
      }
      return root;
    },
    
    /////////
    // Layout
    
    addMenuItemAt : function( menuItem, index ) {
      // seperator does not have this function:
      if( menuItem.setParentMenu ) {
        // it is essential that this happens before the menuItem is added
        menuItem.setParentMenu( this ); 
      }
      this._layout.addAt( menuItem, index );
    },
            
    scheduleMenuLayout : function() {
      if( this._menuLayoutScheduled != true ) {
        this._menuLayoutScheduled = true;
        var children = this._layout.getChildren();
        var length = children.length;
        for( var i = 0; i < length; i++ ) {
          children[ i ]._invalidatePreferredInnerWidth();
          children[ i ].addToQueue( "layoutX" );
        }
        this.addToQueue( "menuLayout" );
      }
    },
    
    _layoutPost : function( changes ) {
      this.base( arguments );
      if( changes.menuLayout ) {
        this._menuLayoutScheduled = false;
        if( this.isSeeable() ) {        
          this._afterAppear(); // recomputes the location
        }
      }
    },
       
    getMaxCellWidth : function( cell ) {
      if( this._maxCellWidths[ cell ] == null ) {
        var max = 0;
        var children = this._layout.getChildren();
        var length = children.length;
        for( var i = 0; i < length; i++ ) {
          if( children[ i ].getPreferredCellWidth ) {
            max = Math.max( max, children[ i ].getPreferredCellWidth( cell ) );
          }
        }
        this._maxCellWidths[ cell ] = max;
      }
      if(    cell == 0 
          && this._maxCellWidths[ 0 ] == 0
          && this.getMaxCellWidth( 1 ) == 0 ) {
        this._maxCellWidths[ cell ] = 13;
      }      
      return this._maxCellWidths[ cell ];
    },
    
    invalidateMaxCellWidth : function( cell ) {
      this._maxCellWidths[ cell ] = null;      
    },
    
    invalidateAllMaxCellWidths : function() {
      for( var i = 0; i < 4; i++ ) {
        this._maxCellWidths[ i ] = null;
      }      
    },
    
    // needed for the menu-manager:
    isSubElement : function( vElement, vButtonsOnly ) {
      var ret = false;
      if (    ( vElement.getParent() === this._layout ) 
           || ( ( !vButtonsOnly ) && ( vElement === this ) ) ) {
        ret = true;
      }
      if( !ret ) {
        var a = this._layout.getChildren(), l=a.length; 
        for ( var i = 0; i < l; i++ ) {
          if (    this.hasSubmenu( a[ i ] )  
               && a[ i ].getMenu().isSubElement( vElement, vButtonsOnly ) ) 
          {
            ret = true;
          }
        }
      }
      return ret;
    },    
        
    ////////
    // Hover
    
    setHoverItem : function( value, fromKeyEvent ) {
      var newHover = value ? value : this._openItem;
      if( this._hoverItem && this._hoverItem != newHover ) {
        this._hoverItem.removeState( "over" );
      }      
      if( newHover ) {        
        newHover.addState( "over" );
      }        
      this._hoverItem = newHover;
      if( !fromKeyEvent ) {       
        // handle open timer
        this._openTimer.setEnabled( false );
        if( this.hasSubmenu( newHover ) && ( this._openItem != newHover ) ) {
          this._openTimer.setEnabled( true );
        } 
        // handle close tiemr
        if( this._openItem ) { 
          if( this._openItem == newHover || newHover == null ) {
            this._closeTimer.setEnabled( false );
          } else if( newHover != null ) {
            this._closeTimer.setEnabled( true );
          }
        }
      }
    },
    
    getHoverItem : function( value ) {
      return this._hoverItem;
    },
    
    hoverFirstItem : function() {
      if( this._isDisplayable && !this._itemsHiddenFlag ) {
        this.setHoverItem( null, true );        
        this._hoverNextItem();        
        this.removeState( "hoverFristItem" );
      } else {
        this.addState( "hoverFristItem" );
      }
    },
        
    _hoverNextItem : function() {
      // About _hoverNext/Previous:
      // the index used for the array of visible children can have
      // "-1" as a valid value (as returned by indexOf), meaning a position 
      // between the last and the first item. This is value is needed when no 
      // item is hovered or the index-position is wrapping around. 
      var current;
      var next = null;
      var children = this._layout.getVisibleChildren();
      var index = children.indexOf( this._hoverItem );
      var startIndex = index;
      do {        
        index++;
        if( index > children.length ) {
          index = -1;
        }
        current = index >= 0 ? children[ index ] : null;
        if(   current 
           && current.isEnabled() 
           && current.classname == "org.eclipse.rwt.widgets.MenuItem" )
        {
          next = current;
        }          
      } while( !next && ( index != startIndex ) );
      this.setHoverItem( next, true );
    },

    _hoverPreviousItem : function() {
      var current;
      var prev = null;
      var children = this._layout.getVisibleChildren();
      var index = children.indexOf( this._hoverItem );
      var startIndex = index;
      do {        
        index--;
        if( index < -1 ) {
          index = children.length;
        }
        current = index >= 0 ? children[ index ] : null;
        if(   current 
           && current.isEnabled() 
           && current.classname == "org.eclipse.rwt.widgets.MenuItem" )
        {
          prev = current;
        }          
      } while( !prev && ( index != startIndex ) );
      this.setHoverItem( prev, true );
    },

    //////////////////
    // Pop-Up handling
      
    // overwritten:
    _makeActive : function() {
      this._lastActive = this.getFocusRoot().getActiveChild();
      this._lastFocus = this.getFocusRoot().getFocusedChild();
      this.getFocusRoot().setActiveChild(this);
    },

    // overwritten:
    _makeInactive : function() {
      var vRoot = this.getFocusRoot();
      vRoot.setActiveChild( this._lastActive );
      vRoot.setFocusedChild( this._lastFocus );
    },
    
    
    _beforeAppear : function() {
      // original qooxdoo code:
      qx.ui.layout.CanvasLayout.prototype._beforeAppear.call(this);
      org.eclipse.rwt.MenuManager.getInstance().add(this);
      this.bringToFront();
      this._makeActive();
      this._menuShown();
    },
    
    _beforeDisappear : function() {
      // original qooxdoo code: (1 line) 
      qx.ui.layout.CanvasLayout.prototype._beforeDisappear.call(this);
      org.eclipse.rwt.MenuManager.getInstance().remove(this);
      if( this.getFocusRoot() ) {
        // if the menu is disposed while visible, it might not have a focusRoot
        this._makeInactive();
      }
      this.setOpenItem( null );
      this.setHoverItem( null );
      if( this._opener instanceof org.eclipse.rwt.widgets.MenuItem ) {   
        var parentMenu = this._opener.getParentMenu();
        if( parentMenu instanceof org.eclipse.rwt.widgets.MenuBar ) {
          this._opener.removeState( "pressed" );
          if( parentMenu.getOpenItem() == this._opener ) {
            parentMenu.setOpenItem( null );
          }        
        }
      }   
      this._menuHidden();
    },

    
    //////////
    // Submenu 
            
    hasSubmenu : function( item ) {
      return item && item.getMenu && item.getMenu();
    },
            
   _onopentimer : function( event ) {
      this._openTimer.stop();
      this.setOpenItem( this._hoverItem );
    },

    _onclosetimer : function( event ) {
      this._closeTimer.stop();
      this.setOpenItem( null );
    },
    
    setOpenItem : function( item ) {
      if( this._openItem && this._openItem.getMenu() ) {
        this._openItem.setSubMenuOpen( false );
        var oldMenu = this._openItem.getMenu();
        oldMenu.hide();        
      }
      this._openItem = item;
      // in theory an item could have lost it's assigned menu (by eval-code)
      // since the timer has been started/the item opend, so check for it
      if( item && item.getMenu() ) {
        var subMenu = item.getMenu(); 
        item.setSubMenuOpen( true );
        subMenu.setOpener( item );
        var itemNode = item.getElement();
        var thisNode = this.getElement();
        // the position is relative to the document, therefore we need helper
        subMenu.setTop( qx.bom.element.Location.getTop( itemNode ) - 2 );                         
        subMenu.setLeft(   qx.bom.element.Location.getLeft( thisNode ) 
                         + qx.html.Dimension.getBoxWidth( thisNode ) 
                         - 3 );
        subMenu.show();        
      }
    },
    
    /////////////////
    // Event-handling
    
    _onMouseOut : function( event ) {
      var target = event.getTarget();
      var related = event.getRelatedTarget();
      if ( target == this || ( related != this && !this.contains( related ) ) )
      {
        this.setHoverItem( null );
      }        
    },
    
   _onMouseOver : function( event ) {
     var target = event.getTarget();
     if( target != this ) {
       this.setHoverItem( target );
     }
     this._unhoverSubMenu();
   },
   
   _unhoverSubMenu : function() {
     if( this._openItem ) {
       var subMenu = this._openItem.getMenu();
       subMenu.setOpenItem( null );
       subMenu.setHoverItem( null );
     }
   },
   
    _onKeyPress : function( event ) {
      switch( event.getKeyIdentifier() ) {
        case "Up":
          this._handleKeyUp( event );
        break;
        case "Down":
          this._handleKeyDown( event );
        break;
        case "Left":
          this._handleKeyLeft( event );
        break;
        case "Right":
          this._handleKeyRight( event );
        break;
        case "Enter":
          this._handleKeyEnter( event );
        break;        
      }
    },
     
    _handleKeyUp : function( event ) {
      if( this._openItem ) {
       this._openItem.getMenu()._hoverPreviousItem();
      } else {
        this._hoverPreviousItem();
      }        
      event.preventDefault();
      event.stopPropagation();
    },
    
    _handleKeyDown : function( event ) {
      if( this._openItem ) {
       this._openItem.getMenu()._hoverNextItem();
      } else {
        this._hoverNextItem();
      }        
      event.preventDefault();
      event.stopPropagation();
    },
    
    _handleKeyLeft : function( event ) {
      var parentMenu = this._opener ? this._opener.getParentMenu() : null;
      if( parentMenu instanceof org.eclipse.rwt.widgets.Menu ) {
        var hover = this._opener; 
        parentMenu.setOpenItem( null );
        parentMenu.setHoverItem( hover, true );
        event.preventDefault();
        event.stopPropagation();
      }
    },
    
    _handleKeyRight : function( event ) {
      if( this.hasSubmenu( this._hoverItem ) ) {
        this._onopentimer();
        this.setHoverItem( null, true );
        this._openItem.getMenu().hoverFirstItem();
        event.preventDefault();
        event.stopPropagation();
      }
    },
    
    _handleKeyEnter : function( event ) {
      if( this.hasSubmenu( this._hoverItem ) ) {
        this._onopentimer();
        this.setHoverItem( null, true );
        this._openItem.getMenu().hoverFirstItem();
      } else if( this._hoverItem ){
        this._hoverItem.execute();
        org.eclipse.rwt.MenuManager.getInstance().update();
      }      
      event.preventDefault();
      event.stopPropagation();
    },
       
   ////////////////
   // Client-Server 
    
    setHasMenuListener : function( value ) {
      this._hasListener = value;
    },
            
   _menuShown : function() {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        if( this._hasListener ) {
          // create preliminary item              
          if( this._preItem == null ) {
            this._preItem = new org.eclipse.rwt.widgets.MenuItem( "push" );
            this._preItem.setText( "..." );
            this._preItem.setEnabled( false );
            this.addMenuItemAt( this._preItem, 0 );
          }
          // hide all but the preliminary item
          var items = this._layout.getChildren();
          for( var i = 0; i < items.length; i++ ) {
            var item = items[ i ];
            item.setDisplay( false );
          }
          this._preItem.setDisplay( true );
          this._itemsHiddenFlag = true;
          if( this.getWidth() < 60 ) {
            this.setWidth( 60 );
          }
          //this.setDisplay( true ); //wouldn't be called if display was false
          // send event
          var wm = org.eclipse.swt.WidgetManager.getInstance();
          var id = wm.findIdByWidget( this );
          var req = org.eclipse.swt.Request.getInstance();
          req.addEvent( "org.eclipse.swt.events.menuShown", id );
          req.send();
        } else {
          var display = this._layout.getChildren().length != 0;
          //no items and no listener to add some:
          this.setDisplay( display );
          if( display ) {
            if( this._hoverFirstItemFlag ) {
              this.hoverFirstItem();
            }
          }
        }
      }
    },

    _menuHidden : function() {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        if( this._hasListener ) {
          var wm = org.eclipse.swt.WidgetManager.getInstance();
          var id = wm.findIdByWidget( this );
          var req = org.eclipse.swt.Request.getInstance();
          req.addEvent( "org.eclipse.swt.events.menuHidden", id );
          req.send();
        }
      }
    },

    unhideItems : function( reveal ) {
      if( reveal ) { 
        var items = this._layout.getChildren();
        for( var i = 0; i < items.length; i++ ) {
          items[ i ].setDisplay( true );
        }        
        if( this._preItem ) {
          this._preItem.setDisplay( false );
        }
        this._itemsHiddenFlag = false;      
        if( this._hoverFirstItemFlag ) {
          this.hoverFirstItem();
        }
      } else {
        this.hide();
      }
      this._hoverFirstItemFlag = false;
    },
    
    // Called to open a popup menu from server side
    showMenu : function( menu, x, y ) {
      if( menu != null ) {
        menu.setLocation( x, y );
        menu.show();
      }
    }
  }
  
});
