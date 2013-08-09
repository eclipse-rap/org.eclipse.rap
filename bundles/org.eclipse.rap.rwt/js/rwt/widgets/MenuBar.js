/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "rwt.widgets.MenuBar", {
  extend : rwt.widgets.base.HorizontalBoxLayout,

  construct : function() {
    this.base( arguments );
    this._hoverItem = null;
    this._openItem = null;
    this._active = false;
    this._lastActive = null;
    this._lastFocus = null;
    this._mnemonics = false;
    this.addEventListener( "mousedown", this._onMouseDown );
    this.addEventListener( "mouseover", this._onMouseOver );
    this.addEventListener( "mouseout", this._onMouseOut );
    this.addEventListener( "keydown", this._onKeyDown );
    this.addEventListener( "keypress", this._onKeyPress );
  },

  destruct : function() {
    this._hoverItem = null;
    this._openItem = null;
    this._lastActive = null;
    this._lastFocus = null;
    this._active = false;
    this._mnemonics = false;
    rwt.widgets.util.MenuManager.getInstance().remove( this );
  },

  properties : {

    appearance :  {
      refine : true,
      init : "toolbar"
    }

  },

  events : {
    "changeOpenItem" : "rwt.event.Event"
  },

  members : {

    setActive : function( active ) {
      if( this.isDisposed() ) {
        return;
      }
      if( this._active != active ) {
        this._active = active;
        if( active ) {
          this._activate();
        } else {
          this._deactivate();
        }
      }
    },

    getActive : function() {
      return this._active;
    },

    setMnemonics : function( value ) {
      if( this.isDisposed() ) {
        return;
      }
      if( this._mnemonics !== value ) {
        this._mnemonics = value;
        var items = this.getChildren();
        for( var i = 0; i < items.length; i++ ) {
          if( items[ i ].renderText ) {
            items[ i ].renderText();
          }
        }
      }
    },

    getMnemonics : function() {
      return this._mnemonics;
    },

    addMenuItemAt : function( menuItem, index ) {
      // seperator does not have this function:
      if( menuItem.setParentMenu ) {
        // it is essential that this happens before the menuItem is added
        menuItem.setParentMenu( this );
      }
      this.addAt( menuItem, index );
    },

    setHoverItem : function( item ) {
      var newHover = item ? item : this._openItem;
      if( this._hoverItem != null && this._hoverItem != item ) {
        this._hoverItem.removeState( "over" );
      }
      if( newHover != null ) {
        newHover.addState( "over" );
        if( this._openItem != null && this._openItem != newHover ) {
          this.setOpenItem( newHover );
        }
      }
      this._hoverItem = newHover;
    },

    openByMnemonic : function( item ) {
      this.setOpenItem( item, true );
      this.setHoverItem( null, true );
    },

    setOpenItem : function( item, byMnemonic ) {
      var oldItem = this._openItem;
      if( oldItem != null && oldItem.getMenu() != null ) {
        oldItem.setSubMenuOpen( false );
        oldItem.getMenu().hide();
      }
      if( item != null && item != oldItem && item.getMenu() != null ) {
        this._openItem = item;
        this.setActive( true );
        item.addState( "pressed" );
        var subMenu = item.getMenu();
        item.setSubMenuOpen( true );
        subMenu.setOpener( item );
        var itemNode = item.getElement();
        // the position is relative to the document, therefore we need helper
        subMenu.setTop( rwt.html.Location.getTop( itemNode ) + itemNode.offsetHeight );
        subMenu.setLeft( rwt.html.Location.getLeft( itemNode ) );
        subMenu.setMnemonics( byMnemonic === true );
        subMenu.show();
      } else {
        this._openItem = null;
      }
      this.dispatchSimpleEvent( "changeOpenItem" );
    },

    getOpenItem : function() {
      return this._openItem;
    },

    getOpener : rwt.util.Functions.returnNull,


    // from original Menu, needed for the menu-manager:
    isSubElement : function( vElement, vButtonsOnly ) {
      var result = false;
      if (    ( vElement.getParent() === this )
           || ( ( !vButtonsOnly ) && ( vElement === this ) ) ) {
        result = true;
      }
      if( !result ) {
        var a = this.getChildren(), l=a.length;
        for ( var i = 0; i < l; i++ ) {
          if (    this.hasSubmenu( a[ i ] )
               && a[ i ].getMenu().isSubElement( vElement, vButtonsOnly ) )
          {
            result = true;
          }
        }
      }
      return result;
    },

    hasSubmenu : function( item ) {
      return item && item.getMenu && item.getMenu();
    },

    //Overwritten, called by MenuManager
    hide : function() {
      this.setActive( false );
    },

    getAutoHide : rwt.util.Functions.returnTrue,

    _activate : function() {
      var focusRoot = this.getFocusRoot();
      rwt.widgets.util.MenuManager.getInstance().add( this );
      if( this._openItem == null ) {
        this.setHoverItem( this.getFirstChild() );
      }
      if( focusRoot ) {
        this._lastActive = focusRoot.getActiveChild();
        this._lastFocus = focusRoot.getFocusedChild();
        focusRoot.setActiveChild( this );
      }
    },

    _deactivate : function() {
      var focusRoot = this.getFocusRoot();
      rwt.widgets.util.MenuManager.getInstance().remove( this );
      this.setMnemonics( false );
      this.setOpenItem( null );
      this.setHoverItem( null );
      if( focusRoot ) {
        focusRoot.setActiveChild( this._lastActive );
        focusRoot.setFocusedChild( this._lastFocus );
      }
    },

    _setMnemonics : function( value ) {
      var items = this.getChildren();
      for( var i = 0; i < items.length; i++ ) {
        if( items[ i ].renderText ) {
          items[ i ].renderText();
        }
      }
    },

    _onKeyDown :function( event ) {
      if( this._mnemonics ) {
        var keyCode = event.getKeyCode();
        var isChar =    !isNaN( keyCode )
                     && rwt.event.EventHandlerUtil.isAlphaNumericKeyCode( keyCode );
        if( isChar ) {
          var event = {
            "type" : "trigger",
            "charCode" : keyCode,
            "success" : false
          };
          var items = this.getChildren();
          for( var i = 0; i < items.length; i++ ) {
            if( items[ i ].handleMnemonic ) {
              items[ i ].handleMnemonic( event );
            }
          }
        }
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
//        case "Left":
//          this._handleKeyLeft( event );
//        break;
//        case "Right":
//          this._handleKeyRight( event );
//        break;
//        case "Enter":
//          this._handleKeyEnter( event );
//        break;
      }
    },

    _handleKeyDown : function( event ) {
      if( this.hasSubmenu( this._hoverItem ) ) {
        this.setOpenItem( this._hoverItem );
        this._openItem.getMenu().hoverFirstItem();
        event.preventDefault();
        event.stopPropagation();
      }
    },

    _handleKeyUp : function( event ) {
      if( this.hasSubmenu( this._hoverItem ) ) {
        this.setOpenItem( this._hoverItem );
        this._openItem.getMenu().hoverFirstItem( true );
        event.preventDefault();
        event.stopPropagation();
      }
    },

    _onMouseOver : function( event ) {
      var target = event.getTarget();
      var hoverItem = target == this ? null : target;
      this.setHoverItem( hoverItem );
    },

    _onMouseOut : function( event ) {
      var target = event.getTarget();
      var related = event.getRelatedTarget();
      if( target == this || !this.contains( related ) ) {
        this.setHoverItem( null );
      }
    },

    _onMouseDown : function( event ) {
      var target = event.getTarget();
      if( target != this ) {
        this.setOpenItem( target );
      }
    }

  }

});

