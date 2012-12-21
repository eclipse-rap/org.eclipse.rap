/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
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
    this.addEventListener( "mousedown", this._onMouseDown );
    this.addEventListener( "mouseover", this._onMouseOver );
    this.addEventListener( "mouseout", this._onMouseOut );
    // TODO [tb] : optional: implement keyboard control
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

    addMenuItemAt : function( menuItem, index ) {
      // seperator does not have this function:
      if( menuItem.setParentMenu ) {
        // it is essential that this happens before the menuItem is added
        menuItem.setParentMenu( this );
      }
      this.addAt( menuItem, index );
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

    setOpenItem : function( item ) {
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
        subMenu.show();
      } else {
        this._openItem = null;
      }
      this.dispatchSimpleEvent( "changeOpenItem" );
    },

    getOpenItem : function() {
      return this._openItem;
    }

  }

});

