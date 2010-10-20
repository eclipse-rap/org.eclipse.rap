/*******************************************************************************
 *  Copyright: 2004-2010 1&1 Internet AG, Germany, http://www.1und1.de,
 *                       and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

/**
 * This class extends qx.ui.form.List to make its API more SWT-like.
 */
qx.Class.define( "org.eclipse.swt.widgets.List", {
  extend : qx.ui.layout.VerticalBoxLayout,

  construct : function( multiSelection ) {
    this.base( arguments );
    this.setAppearance( "list" );
    this.setOverflow( "hidden" );
    this.setTabIndex( 1 );
    this._manager = new qx.ui.selection.SelectionManager( this );
    this.addEventListener( "mouseover", this._onmouseover );
    this.addEventListener( "mousedown", this._onmousedown );
    this.addEventListener( "mouseup", this._onmouseup );
    this.addEventListener( "click", this._onclick );
    this.addEventListener( "dblclick", this._ondblclick );
    this.addEventListener( "keydown", this._onkeydown );
    this.addEventListener( "keypress", this._onkeypress );
    this.addEventListener( "keypress", this._onkeyinput );
    this.initOverflow();
    this.initTabIndex();    
    // Should changeSelection events passed to the server-side?
    // state == no, action == yes
    this._changeSelectionNotification = "state";
    this._topIndex = 0;
    var selMgr = this.getManager();
    selMgr.addEventListener( "changeLeadItem", this._onChangeLeadItem, this );
    selMgr.addEventListener( "changeSelection", this._onSelectionChange, this );
    selMgr.setMultiSelection( multiSelection );
    selMgr.setDragSelection( false );
    this.addEventListener( "focus", this._onFocusIn, this );
    this.addEventListener( "blur", this._onFocusOut, this );
    this.addEventListener( "click", this._onClick, this );
    this.addEventListener( "dblclick", this._onDblClick, this );
    this.addEventListener( "appear", this._onAppear, this );
    // Listen to send event of request to report topIndex
    var req = org.eclipse.swt.Request.getInstance();
    req.addEventListener( "send", this._onSendRequest, this );
  },
  
  destruct : function() {
    var req = org.eclipse.swt.Request.getInstance();
    req.removeEventListener( "send", this._onSendRequest, this );
    var selMgr = this.getManager();
    selMgr.removeEventListener( "changeLeadItem", this._onChangeLeadItem, this );
    selMgr.removeEventListener( "changeSelection", this._onSelectionChange, this );
    this.removeEventListener( "focus", this._onFocusIn, this );
    this.removeEventListener( "blur", this._onFocusOut, this );
    this.removeEventListener( "click", this._onClick, this );
    this.removeEventListener( "dblclick", this._onDblClick, this );
    this.removeEventListener( "appear", this._onAppear, this );
    this._disposeObjects("_manager" );
  },

  members : {

    _pressedString : "",
    _lastKeyPress : 0,

    getManager : function() {
      return this._manager;
    },

    getListItemTarget : function( vItem ) {
      while( vItem != null && vItem.getParent() != this ) {
        vItem = vItem.getParent();
      }
      return vItem;
    },

    getSelectedItem : function() {
      return this.getSelectedItems()[0] || null;
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

    _onkeydown : function( event ) {
      // Execute action on press <ENTER>
      if( event.getKeyIdentifier() == "Enter" && !event.isAltPressed() ) {
        var items = this.getSelectedItems();
        for( var i = 0; i < items.length; i++ ) {
          items[i].createDispatchEvent( "action" );
        }
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
      return this._findItem( vText, vStartIndex || 0, "String" );
    },
    
    _findItem : function( vUserValue, vStartIndex, vType ) {
      var vAllItems = this.getChildren();
      // If no startIndex given try to get it by current selection
      if( vStartIndex == null ) {
        vStartIndex = vAllItems.indexOf( this.getSelectedItem() );
        if (vStartIndex == -1) {
          vStartIndex = 0;
        }
      }
      var methodName = "matches" + vType;
      // Mode #1: Find all items after the startIndex
      for( var i = vStartIndex; i < vAllItems.length; i++ ) {
        if( vAllItems[ i ][ methodName ]( vUserValue ) ) {
          return vAllItems[i];
        }
      }
      // Mode #2: Find all items before the startIndex
      for( var i = 0; i < vStartIndex; i++ ) {
        if( vAllItems[ i ][ methodName ]( vUserValue ) ) {
          return vAllItems[i];
        }
      }
      return null;
    },
  
    setItems : function( items ) {
      // preserve selection and focused item
      var manager = this.getManager();
      var oldLeadItem = manager.getLeadItem();
      var oldAnchorItem = manager.getAnchorItem();
      var oldSelection = manager.getSelectedItems();
      // exchange/add/remove items
      var oldItems = this.getChildren();
      for( var i = 0; i < items.length; i++ ) {
        if( i < oldItems.length ) {
          oldItems[ i ].setLabel( items[ i ] );
        } else {
          // TODO [rh] optimize this: context menu should be handled by the List
          //      itself for all its ListItems
          var item = new qx.ui.form.ListItem();
          item.addEventListener( "mouseover", this._onListItemMouseOver, this );
          item.addEventListener( "mouseout", this._onListItemMouseOut, this );
          // [if] Omit the focused item outline border - see bug 286902
          item.setStyleProperty( "outline", "0px none" );
          item.handleStateChange = function() {};
          // prevent items from being drawn outside the list
          item.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
          item.setContextMenu( this.getContextMenu() );
          item.setTabIndex( null );
          item.setLabel( "(empty)" );
          item.getLabelObject().setMode( qx.constant.Style.LABEL_MODE_HTML );
          item.setLabel( items[ i ] );
          if( i % 2 == 0 ) {
            item.addState( "even" );
          }
          this.add( item );
        }
      }
      var child = null;
      while( this.getChildrenLength() > items.length ) {
        child = this.getLastChild();
        this.remove( child );
        child.removeEventListener( "mouseover", this._onListItemMouseOver, this );
        child.removeEventListener( "mouseout", this._onListItemMouseOut, this );
        child.dispose();
      }
      // restore previous selection and focusItem
      manager.setSelectedItems( oldSelection );
      manager.setLeadItem( oldLeadItem );
      if( manager.getMultiSelection() ) {
        manager.setAnchorItem( oldAnchorItem );
      }
    },

    /**
     * Sets the single selection for the List to the item specified by the given 
     * itemIndex (-1 to clear selection).
     */
    selectItem : function( itemIndex ) {
      if( itemIndex == -1 ) {
        this.getManager().deselectAll();
      } else {
        var item = this.getChildren()[ itemIndex ];
        this.getManager().setSelectedItem( item );
        // avoid warning message. scrollIntoView works only for visible widgets
        // the assumtion is that if 'this' is visible, the item to scroll into
        // view is also visible
        if ( this.isCreated() && this.isDisplayable() ) {
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
        var item = this.getChildren()[ itemIndices[ i ] ];
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
        var items = this.getManager().getItems();
        this.getManager().setLeadItem( items[ itemIndex ] );
      }
    },

    selectAll : function() {
      if( this.getManager().getMultiSelection() == true ) {
        this.getManager().selectAll();
      }
    },

    setChangeSelectionNotification : function( value ) {
      this._changeSelectionNotification = value;
    },
    
    setTopIndex : function( value ) {
      this._topIndex = value;
      this._applyTopIndex( value );
    },
    
    addState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        var items = this.getManager().getItems();
        for( var i = 0; i < items.length; i++ ) {
          items[ i ].addState( state );
        }
      }
    },

    removeState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        var items = this.getManager().getItems();
        for( var i = 0; i < items.length; i++ ) {
          items[ i ].removeState( state );
        }
      }
    },

    _applyTopIndex : function( newIndex ) {
      var items = this.getManager().getItems();
      if( items.length > 0 && items[ 0 ].isCreated() ) {
        var itemHeight = this.getManager().getItemHeight( items[ 0 ] );
        if( itemHeight > 0 ) {
          this.setScrollTop( newIndex * itemHeight );
        }
      }
    },
    
    _getTopIndex : function() {
      var topIndex = 0;
      var scrollTop = this.getScrollTop();
      var items = this.getManager().getItems();
      if( items.length > 0 ) {
        var itemHeight = this.getManager().getItemHeight( items[ 0 ] );
        if( itemHeight > 0 ) {
          topIndex = Math.round( scrollTop / itemHeight );
        }
      }
      return topIndex;
    },
    
    _onAppear : function( evt ) {
      // [ad] Fix for Bug 277678 
      // when #showSelection() is called for invisible widget
      this._applyTopIndex( this._topIndex );
    },
    
    _onSendRequest : function( evt ) {
      var topIndex = this._isCreated ? this._getTopIndex() : 0;
      if( this._topIndex != topIndex ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( id + ".topIndex", topIndex );
        this._topIndex = topIndex;
      }
    },

    _getSelectionIndices : function() {
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( this );
      var selectionIndices = "";
      var selectedItems = this.getManager().getSelectedItems();
      for( var i = 0; i < selectedItems.length; i++ ) {
        var index = this.indexOf( selectedItems[ i ] );
        // TODO [rh] find out why sometimes index == -1, cannot be reproduced
        //      in standalone qooxdoo application
        if( index >= 0 ) {
          if( selectionIndices != "" ) {
            selectionIndices += ",";
          }
          selectionIndices += String( index );
        }
      }
      return selectionIndices;
    },

    _onChangeLeadItem : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var id = wm.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        var focusIndex = this.indexOf( this.getManager().getLeadItem() );
        req.addParameter( id + ".focusIndex", focusIndex );
      }
    },

    _onClick : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        this._updateSelectedItemState();
        if( !this.__clicksSuspended ) {
          this._suspendClicks();
//          TODO [rst] Replaced by _onSelectionChange, the stub remains here for
//                     mouse listeners
//          var wm = org.eclipse.swt.WidgetManager.getInstance();
//          var id = wm.findIdByWidget( this );
//          var req = org.eclipse.swt.Request.getInstance();
//          req.addParameter( id + ".selection", this._getSelectionIndices() );
//          if( this._changeSelectionNotification == "action" ) {
//            req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
//            req.send();
//          }
        }
      }
    },

    _onDblClick : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        if( this._changeSelectionNotification == "action" ) {
          var wm = org.eclipse.swt.WidgetManager.getInstance();
          var id = wm.findIdByWidget( this );
          var req = org.eclipse.swt.Request.getInstance();
          req.addEvent( "org.eclipse.swt.events.widgetDefaultSelected", id );
          org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
          req.send();
        }
      }
    },

    _onSelectionChange : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var id = wm.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( id + ".selection", this._getSelectionIndices() );
        if( this._changeSelectionNotification == "action" ) {
          req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
          org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
          req.send();
        }
      }
      this._updateSelectedItemState();
    },

    /**
     * Suspends the processing of click events to avoid sending multiple
     * widgetSelected events to the server.
     */
    _suspendClicks : function() {
      this.__clicksSuspended = true;
      qx.client.Timer.once( this._enableClicks, 
                            this, 
                            org.eclipse.swt.EventUtil.DOUBLE_CLICK_TIME );
    },

    _enableClicks : function() {
      this.__clicksSuspended = false;
    },

    _onFocusIn : function( evt ) {
      this._updateSelectedItemState();
    },

    _onFocusOut : function( evt ) {
      this._updateSelectedItemState();
    },
    
    _onListItemMouseOver : function( evt ) {
      evt.getTarget().addState( "over" );
    },
    
    _onListItemMouseOut : function( evt ) {
      evt.getTarget().removeState( "over" );
    },

    _updateSelectedItemState : function() {
      var selectedItems = this.getManager().getSelectedItems();
      // Set a flag that signals unfocused state on every item.
      // Note: Setting a flag that signals focused state would not work as the
      // list is reused by other widgets e.g. ComboBox, whose items would then
      // appear as unfocused by default.
      for( var i = 0; i < selectedItems.length; i++ ) {
        if( this.getFocused() ) {
          selectedItems[ i ].removeState( "parent_unfocused" );
        } else {
          selectedItems[ i ].addState( "parent_unfocused" );
        }
      }
    }

  }
} );
