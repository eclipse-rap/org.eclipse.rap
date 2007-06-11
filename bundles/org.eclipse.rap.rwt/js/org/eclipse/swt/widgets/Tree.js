
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

/**
 * This class encapulates the qx.ui.treefullcontrol.Tree to make it more
 * suitable for usage in RWT.
 * The style parameter mimics the RWT style flag. Possible values (strings)
 * are: multi, check
 */
qx.Class.define( "org.eclipse.swt.widgets.Tree", {
  extend : qx.ui.treefullcontrol.Tree,

  construct : function( style ) {
    var trs = qx.ui.treefullcontrol.TreeRowStructure.getInstance().standard( "" );
    qx.ui.treefullcontrol.Tree.call( this, trs );
    this.setOverflow( qx.constant.Style.OVERFLOW_AUTO );
    this.setHideNode( true );
    this.setUseTreeLines( true );
    this.setUseDoubleClick( false );  // true supresses dblclick events !

    // TODO [rh] this is only to make the tree focusable at all
    this.setTabIndex( 1 );
    this._rwtStyle = style;
    this._selectionListeners = false;
    this._treeListeners = false;
    this._hasFocus = false;
    var manager = this.getManager();
    manager.setMultiSelection( qx.lang.String.contains( style, "multi" ) );
    manager.addEventListener( "changeSelection", this._onChangeSelection, this );
    this.addEventListener( "treeOpenWithContent", this._onItemExpanded, this );
    this.addEventListener( "treeClose", this._onItemCollapsed, this );
    this.addEventListener( "contextmenu", this._onContextMenu, this );
    this.addEventListener( "focus", this._onFocusIn, this );
    this.addEventListener( "blur", this._onFocusOut, this );    
    // TODO [rst] Find out why this is not the default appearance
    this.setAppearance( "tree" );
  },

  destruct : function() {
    var manager = this.getManager();
    manager.removeEventListener( "changeSelection", this._onChangeSelection, this );
    this.removeEventListener( "treeOpenWithContent", this._onItemExpanded, this );
    this.removeEventListener( "treeClose", this._onItemCollapsed, this );
    this.removeEventListener( "focus", this._onFocusIn, this );
    this.removeEventListener( "blur", this._onFocusOut, this );
  },

  members : {
    /**
     * Are there any server-side SelectionListeners attached? If so, selecting an
     * item causes a request to be sent that informs the server-side listeners.
     */
    setSelectionListeners : function( value ) {
      this._selectionListeners = value;
    },

    /**
     * Are there any server-side TreeListeners attached? If so, expanding/collapsing
     * an item causes a request to be sent that informs the server-side listeners.
     */
    setTreeListeners : function( value ) {
      this._treeListeners = value;
    },

    getRWTStyle : function() {
      return this._rwtStyle;
    },
    
    /////////////////
    // Event Listener

    _onChangeSelection : function( evt ) {
      this._updateSelectedItemState();
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = wm.findIdByWidget( this );
        var item = this.getManager().getLeadItem();
        var selection = this._getSelectionIndices();
        if( selection != "" ) {
          req.addParameter( id + ".selection", this._getSelectionIndices() );
          // TODO [rst] Prevent selecting the root item.
          //      When first visible item is selected and arrow up is pressed the root
          //      item ( == this ) is selected which results in an invisible selection.
          if( item == this ) {
//          this.getFirstVisibleChildOfFolder().setSelected( true );
//          this.setSelected( false );
          } else {
            if ( this._selectionListeners ) {
              this._suspendClicks();
              var itemId = wm.findIdByWidget( item );
              var eventName = "org.eclipse.swt.events.widgetSelected";
              req.addEvent( eventName, id );
              req.addParameter( eventName + ".item", itemId );
              req.send();
            }
          }
        }
      }
    },

    _onItemExpanded : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var treeItemId = wm.findIdByWidget( evt.getData() );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( treeItemId + ".state", "expanded" );
        if( this._treeListeners ) {
          req.addEvent( "org.eclipse.swt.events.treeExpanded", treeItemId );
          req.send();
        }
      }
    },

    _onItemCollapsed : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var treeItemId = wm.findIdByWidget( evt.getData() );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( treeItemId + ".state", "collapsed" );
        if( this._treeListeners ) {
          req.addEvent( "org.eclipse.swt.events.treeCollapsed", treeItemId );
          req.send();
        }
      }
    },

    _onContextMenu : function( evt ) {
      var menu = this.getContextMenu();
      if( menu != null ) {
        menu.setLocation( evt.getPageX(), evt.getPageY() );
        menu.setOpener( this );
        menu.show();
        evt.stopPropagation();
      }
    },

    _onFocusIn : function( evt ) {
      this._hasFocus = true;
      this._updateSelectedItemState();
    },
    
    _onFocusOut : function( evt ) {
      this._hasFocus = false;
      this._updateSelectedItemState();
    },
    
    _updateSelectedItemState : function() {
      var selectedItems = this.getManager().getSelectedItems();
      // Set a flag that signals unfocused state on every item.
      for( var i = 0; i < selectedItems.length; i++ ) {
        var label_ = selectedItems[ i ].getLabelObject()
this.debug( "selectedItem: " + label_ );        
        if( label_ != null ) {
          if( this._hasFocus ) {
            label_.removeState( "parent_unfocused" );
          } else {
            label_.addState( "parent_unfocused" );
          }
        }
      }
    },
    
    /*
     * Handle click on tree item
     * called by org.eclipse.swt.widgets.TreeItem
     */
    _notifyItemClick : function( item ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        if( this._selectionListeners && !this._clicksSuspended ) {
          this._suspendClicks();
          var wm = org.eclipse.swt.WidgetManager.getInstance();
          var id = wm.findIdByWidget( this );
          var itemId = wm.findIdByWidget( item );
          var req = org.eclipse.swt.Request.getInstance();
          var eventName = "org.eclipse.swt.events.widgetSelected";
          req.addEvent( eventName, id );
          req.addParameter( eventName + ".item", itemId );
          req.send();
        }
      }
    },

    /*
     * Handle double click on tree item
     * called by org.eclipse.swt.widgets.TreeItem
     */
    _notifyItemDblClick : function(item) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        if( this._selectionListeners ) {
          var wm = org.eclipse.swt.WidgetManager.getInstance();
          var id = wm.findIdByWidget( this );
          var itemId = wm.findIdByWidget( item );
          var req = org.eclipse.swt.Request.getInstance();
          var eventName = "org.eclipse.swt.events.widgetDefaultSelected";
          req.addEvent( eventName, id );
          req.addParameter( eventName + ".item", itemId );
          req.send();
        }
      }
    },

    /*
     * Handle change of the check state of a tree item's check box
     * called by org.eclipse.swt.widgets.TreeItem
     */
    _notifyChangeItemCheck : function( item ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        if( this._selectionListeners ) {
          var wm = org.eclipse.swt.WidgetManager.getInstance();
          var id = wm.findIdByWidget( this );
          var itemId = wm.findIdByWidget( item );
          var req = org.eclipse.swt.Request.getInstance();
          var eventName = "org.eclipse.swt.events.widgetSelected";
          req.addEvent( eventName, id );
          req.addParameter( eventName + ".item", itemId );
          req.addParameter( eventName + ".detail", "check" );
          req.send();
        }
      }
    },

    /*
     * Returns the current selection as comma separated string
     */
    // TODO [rh] handle multi selection
    _getSelectionIndices : function() {
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var result = "";
      if( this.getManager().getMultiSelection() ) {
        var selectedItems = this.getManager().getSelectedItems();
        for( var i = 0; i < selectedItems.length; i++ ) {
          var item = selectedItems[i];
          if( item != this ) {
            if( result == "" ) {
              result += ",";
            }
            result += wm.findIdByWidget(item);
          }
        }
      } else {
        var item = this.getManager().getSelectedItem();
        if( item != this ) {
          result = wm.findIdByWidget( item );
        }
      }
      return result;
    },

    /*
     * Suspends the processing of click events to avoid sending multiple
     * widgetSelected events to the server.
     */
    _suspendClicks : function() {
      this._clicksSuspended = true;
      qx.client.Timer.once( this._enableClicks, this, 500 );
    },

    _enableClicks : function() {
      this._clicksSuspended = false;
    }
  }
});
