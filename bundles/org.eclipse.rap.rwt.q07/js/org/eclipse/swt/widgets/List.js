/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

/**
 * This class extends qx.ui.form.List to make its API more SWT-like.
 */
qx.Class.define( "org.eclipse.swt.widgets.List", {
  extend : qx.ui.form.List,

  construct : function() {
    this.base( arguments );
    this.setMarkLeadingItem( true );
    this.rap_init();
  },
  
  destruct : function() {
    this.rap_reset();
  },

  members : {
    
    init : function( multiSelection ) {
      var manager = this.getManager();
      manager.setMultiSelection( multiSelection );
    },
    
    rap_init : function( multiSelection ) {
      // Should changeSelection events passed to the server-side?
      // state == no, action == yes
      this._changeSelectionNotification = "state";
      var selMgr = this.getManager();
      selMgr.addEventListener( "changeLeadItem", this._onChangeLeadItem, this );
      selMgr.addEventListener( "changeSelection", this._onSelectionChange, this );
      this.addEventListener( "focus", this._onFocusIn, this );
      this.addEventListener( "blur", this._onFocusOut, this );
      this.addEventListener( "click", this._onClick, this );
      this.addEventListener( "dblclick", this._onDblClick, this );
    },
    
    rap_reset : function() {
      var selMgr = this.getManager();
      selMgr.removeEventListener( "changeLeadItem", this._onChangeLeadItem, this );
      selMgr.removeEventListener( "changeSelection", this._onSelectionChange, this );
      this.removeEventListener( "focus", this._onFocusIn, this );
      this.removeEventListener( "blur", this._onFocusOut, this );
      this.removeEventListener( "click", this._onClick, this );
      this.removeEventListener( "dblclick", this._onDblClick, this );
    },
    
    /** Sets the given array of items. */
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
          // prevent items from being drawn outside the list
          item.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
          item.setContextMenu( this.getContextMenu() );
          item.setTabIndex( -1 );
          item.setLabel( "(empty)" );
          item.getLabelObject().setMode( qx.constant.Style.LABEL_MODE_HTML );
          item.setLabel( items[ i ] );
          if( i % 2 == 0 ) {
            item.addState( "even" );
          }
          this.add( item );
        }
      }
      while( this.getChildrenLength() > items.length ) {
        this.removeAt( this.getChildrenLength() - 1 );
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

    /**
     * Selects all item if the List is multi-select. Does nothing for single-
     * select Lists.
     */
    selectAll : function() {
      if( this.getManager().getMultiSelection() == true ) {
        this.getManager().selectAll();
      }
    },

    setChangeSelectionNotification : function( value ) {
      this._changeSelectionNotification = value;
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
});
