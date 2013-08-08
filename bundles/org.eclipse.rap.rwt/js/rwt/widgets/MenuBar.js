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
  },

  destruct : function() {
    this._hoverItem = null;
    this._openItem = null;
    this._lastActive = null;
    this._lastFocus = null;
    this._active = false;
    this._mnemonics = false;
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
      if( this._hoverItem != null && this._hoverItem != item ) {
        this._hoverItem.removeState( "over" );
      }
      if( item != null ) {
        item.addState( "over" );
        if( this._openItem != null && this._openItem != item ) {
          this.setOpenItem( item );
        }
      }
      this._hoverItem = item;
    },

    openByMnemonic : function( item ) {
      this.setOpenItem( item, true );
    },

    setOpenItem : function( item, byMnemonic ) {
      var oldItem = this._openItem;
      if( oldItem != null && oldItem.getMenu() != null ) {
        oldItem.setSubMenuOpen( false );
        oldItem.getMenu().hide();
      }
      if( item != null && item != oldItem && item.getMenu() != null ) {
        this._openItem = item;
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

    //Overwritten, called by PopupManager
    hide : function() {
      this.setActive( false );
    },

    getAutoHide : rwt.util.Functions.returnTrue,

    _activate : function() {
      var focusRoot = this.getFocusRoot();
      rwt.widgets.util.PopupManager.getInstance().add( this );
      this.setHoverItem( this.getFirstChild() );
      if( focusRoot ) {
        this._lastActive = this.getFocusRoot().getActiveChild();
        this._lastFocus = this.getFocusRoot().getFocusedChild();
        this.getFocusRoot().setActiveChild( this );
      }
    },

    _deactivate : function() {
      var focusRoot = this.getFocusRoot();
      rwt.widgets.util.PopupManager.getInstance().remove( this );
      this.setMnemonics( false );
      if( focusRoot ) {
        this.setHoverItem( null );
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

