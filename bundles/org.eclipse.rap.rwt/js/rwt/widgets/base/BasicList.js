/*******************************************************************************
 * Copyright: 2004, 2012 1&1 Internet AG, Germany, http://www.1und1.de,
 *                       and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

rwt.qx.Class.define( "rwt.widgets.base.BasicList", {
  extend : rwt.widgets.base.Scrollable,

  construct : function( multiSelection ) {
    this.base( arguments, new rwt.widgets.base.VerticalBoxLayout() );
    this.setAppearance( "list" );
    this.setTabIndex( 1 );
    this._manager = new rwt.widgets.util.SelectionManager( this._clientArea );
    this.addEventListener( "mouseover", this._onmouseover );
    this.addEventListener( "mousedown", this._onmousedown );
    this.addEventListener( "mouseup", this._onmouseup );
    this.addEventListener( "keypress", this._onkeypress );
    this.addEventListener( "keypress", this._onkeyinput );
    this.initOverflow();
    this.initTabIndex();
    this._pressedString = "";
    this._lastKeyPress = 0;
    this._itemWidth = 0;
    this._itemHeight = 0;
    this._markupEnabled = false;
    var selMgr = this.getManager();
    selMgr.setMultiSelection( multiSelection );
    selMgr.setDragSelection( false );
    this.addEventListener( "dblclick", this._ondblclick, this );
  },

  destruct : function() {
    this.removeEventListener( "dblclick", this._ondblclick, this );
    this._disposeObjects("_manager" );
  },

  members : {

    setMarkupEnabled : function( value ) {
      this._markupEnabled = value;
    },

    getManager : function() {
      return this._manager;
    },

    getPreferredWidth: function() {
      var result = 0;
      var items = this.getItems();
      for( var i = 0; i < items.length; i++ ) {
        var paddingWidth = items[ i ].getPaddingLeft() + items[ i ].getPaddingRight();
        var itemWidth = items[ i ].getPreferredBoxWidth() + paddingWidth;
        result = Math.max( result, itemWidth );
      }
      result += this._vertScrollBar.getWidth();
      return result;
    },

    isRelevantEvent : function( evt ) {
      var target = evt.getTarget();
      while( target != null && target !== this ) {
        target = target.getParent();
      }
      return target === this;
    },

    getListItemTarget : function( vItem ) {
      while( vItem != null && vItem.getParent() != this._clientArea ) {
        vItem = vItem.getParent();
      }
      return vItem;
    },

    getSelectedItem : function() {
      return this._manager.getSelectedItems()[0] || null;
    },

    getSelectedItems : function() {
      return this._manager.getSelectedItems();
    },

    _onmouseover : function( event ) {
      var vItem = this.getListItemTarget( event.getTarget() );
      if( vItem ) {
        this._manager.handleMouseOver( vItem, event );
      }
    },

    _onmousedown : function( event ) {
      var vItem = this.getListItemTarget( event.getTarget() );
      if( vItem ) {
        this._manager.handleMouseDown( vItem, event );
      }
    },

    _onmouseup : function( event ) {
      var vItem = this.getListItemTarget( event.getTarget() );
      if( vItem ) {
        this._manager.handleMouseUp( vItem, event );
      }
    },

    _onclick : function( event ) {
      var vItem = this.getListItemTarget( event.getTarget() );
      if( vItem ) {
        this._manager.handleClick( vItem, event );
      }
    },

    _ondblclick : function( event ) {
      var vItem = this.getListItemTarget( event.getTarget() );
      if( vItem ) {
        this._manager.handleDblClick( vItem, event );
      }
    },

    _onkeypress : function( event ) {
      // Give control to selectionManager
      this._manager.handleKeyPress( event );
    },

    _onkeyinput : function( event ) {
      // Fix for bug# 288344
      if( !event.isAltPressed() && !event.isCtrlPressed() ) {
        if( event.getCharCode() !== 0 ) {
          // Reset string after a second of non pressed key
          if( ( ( new Date() ).valueOf() - this._lastKeyPress ) > 1000 ) {
            this._pressedString = "";
          }
          // Combine keys the user pressed to a string
          this._pressedString += String.fromCharCode( event.getCharCode() );
          // Find matching item
          var matchedItem = this.findString( this._pressedString, null );
          if( matchedItem ) {
            var oldVal = this._manager._getChangeValue();
            // Temporary disable change event
            var oldFireChange = this._manager.getFireChange();
            this._manager.setFireChange( false );
            // Reset current selection
            this._manager._deselectAll();
            // Update manager
            this._manager.setItemSelected( matchedItem, true );
            this._manager.setAnchorItem( matchedItem );
            this._manager.setLeadItem( matchedItem );
            // Scroll to matched item
            matchedItem.scrollIntoView();
            // Recover event status
            this._manager.setFireChange( oldFireChange );
            // Dispatch event if there were any changes
            if( oldFireChange && this._manager._hasChanged( oldVal ) ) {
              this._manager._dispatchChange();
            }
          }
          // Store timestamp
          this._lastKeyPress = ( new Date() ).valueOf();
          event.preventDefault();
        }
      }
    },

    findString : function( vText, vStartIndex ) {
      return this._findItem( vText, vStartIndex || 0 );
    },

    _findItem : function( vUserValue, vStartIndex ) {
      var vAllItems = this.getItems();
      // If no startIndex given try to get it by current selection
      if( vStartIndex == null ) {
        vStartIndex = vAllItems.indexOf( this.getSelectedItem() );
        if (vStartIndex == -1) {
          vStartIndex = 0;
        }
      }
      // Mode #1: Find all items after the startIndex
      for( var i = vStartIndex; i < vAllItems.length; i++ ) {
        if( vAllItems[ i ].matchesString( vUserValue ) ) {
          return vAllItems[i];
        }
      }
      // Mode #2: Find all items before the startIndex
      for( var i = 0; i < vStartIndex; i++ ) {
        if( vAllItems[ i ].matchesString( vUserValue ) ) {
          return vAllItems[i];
        }
      }
      return null;
    },

    setItems : function( value ) {
      var items = this._escapeItems( value );
      // preserve selection and focused item
      var manager = this.getManager();
      var oldLeadItem = manager.getLeadItem();
      var oldAnchorItem = manager.getAnchorItem();
      var oldSelection = manager.getSelectedItems();
      // exchange/add/remove items
      var oldItems = this.getItems();
      for( var i = 0; i < items.length; i++ ) {
        if( i < oldItems.length ) {
          oldItems[ i ].setLabel( items[ i ] );
        } else {
          // TODO [rh] optimize this: context menu should be handled by the List
          //      itself for all its ListItems
          var item = new rwt.widgets.ListItem();
          item.addEventListener( "mouseover", this._onListItemMouseOver, this );
          item.addEventListener( "mouseout", this._onListItemMouseOut, this );
          // prevent items from being drawn outside the list
          item.setWidth( this._itemWidth );
          item.setHeight( this._itemHeight );
          item.setContextMenu( this.getContextMenu() );
          item.setTabIndex( null );
          item.setLabel( items[ i ] );
          if( i % 2 === 0 ) {
            item.addState( "even" );
          }
          if( this._customVariant !== null ) {
            item.addState( this._customVariant );
          }
          this._clientArea.add( item );
        }
      }
      var child = null;
      while( this._clientArea.getChildrenLength() > items.length ) {
        child = this._clientArea.getLastChild();
        child.removeEventListener( "mouseover", this._onListItemMouseOver, this );
        child.removeEventListener( "mouseout", this._onListItemMouseOut, this );
        // [if] Workaround for bug:
        // 278361: [Combo] Overlays text after changing items
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=278361
        // Items are not removed from DOM if the _isDisplayable property is false.
        child._isDisplayable = true;
        child.destroy();
      }
      // restore previous selection and focusItem
      manager.setSelectedItems( oldSelection );
      manager.setLeadItem( oldLeadItem );
      if( manager.getMultiSelection() ) {
        manager.setAnchorItem( oldAnchorItem );
      }
      this._updateScrollDimension();
    },

    _escapeItems : function( items ) {
      var result = items;
      if( !this._markupEnabled ) {
        var EncodingUtil = rwt.util.Encoding;
        for( var i = 0; i < result.length; i++ ) {
          result[ i ] = EncodingUtil.replaceNewLines( result[ i ], " " );
          result[ i ] = EncodingUtil.escapeText( result[ i ], false );
          result[ i ] = EncodingUtil.replaceWhiteSpaces( result[ i ] );
        }
      }
      return result;
    },

    getItems : function() {
      return this.getManager().getItems();
    },

    getItemsCount : function() {
      return this.getItems().length;
    },

    getItemIndex : function( item ) {
      return this._clientArea.indexOf( item );
    },

    /**
     * Sets the single selection for the List to the item specified by the given
     * itemIndex (-1 to clear selection).
     */
    selectItem : function( itemIndex ) {
      if( itemIndex == -1 ) {
        this.getManager().deselectAll();
      } else {
        var item = this.getItems()[ itemIndex ];
        this.getManager().setSelectedItem( item );
        // avoid warning message. scrollIntoView works only for visible widgets
        // the assumtion is that if 'this' is visible, the item to scroll into
        // view is also visible
        if ( this._clientArea.isCreated() && this._clientArea.isDisplayable() ) {
          this.getManager().scrollItemIntoView( item, true );
        }
      }
    },

    /**
     * Sets the multi selection for the List to the items specified by the given
     * itemIndices array (empty array to clear selection).
     */
    selectItems : function( itemIndices ) {
      var manager = this.getManager();
      manager.deselectAll();
      for( var i = 0; i < itemIndices.length; i++ ) {
        var item = this.getItems()[ itemIndices[ i ] ];
        manager.setItemSelected( item, true );
      }
    },

    /**
     * Sets the focused item the List to the item specified by the given
     * itemIndex (-1 for no focused item).
     */
    focusItem : function( itemIndex ) {
      if( itemIndex == -1 ) {
        this.getManager().setLeadItem( null );
      } else {
        var items = this.getItems();
        this.getManager().setLeadItem( items[ itemIndex ] );
      }
    },

    selectAll : function() {
      if( this.getManager().getMultiSelection() === true ) {
        this.getManager().selectAll();
      }
    },

    setItemDimensions : function( width, height ) {
      this._itemWidth = width;
      this._itemHeight = height;
      var items = this.getItems();
      for( var i = 0; i < items.length; i++ ) {
        items[ i ].setWidth( this._itemWidth );
        items[ i ].setHeight( this._itemHeight );
      }
      this._vertScrollBar.setIncrement( height );
      this._updateScrollDimension();
    },

    _updateScrollDimension : function() {
      var itemCount = this.getItems().length;
      this._horzScrollBar.setMaximum( this._itemWidth );
      this._vertScrollBar.setMaximum( this._itemHeight * itemCount );
    },

    setCustomVariant : function( value ) {
      if( this._customVariant !== null ) {
        var oldState = this._customVariant;
        this._clientArea.forEachChild( function() {
          this.removeState( oldState );
        } );
      }
      this._clientArea.forEachChild( function() {
        this.addState( value );
      } );
      this.base( arguments, value );
    },

    _onListItemMouseOver : function( evt ) {
      evt.getTarget().addState( "over" );
    },

    _onListItemMouseOut : function( evt ) {
      evt.getTarget().removeState( "over" );
    }

  }

} );
