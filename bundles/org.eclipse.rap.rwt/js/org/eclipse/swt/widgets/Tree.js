
/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

/**
 * This class provides the client-side counterpart for 
 * org.eclipse.swt.widgets.Tree.
 * @event itemselected
 * @event itemdefaultselected
 * @event itemchecked
 */
qx.Class.define( "org.eclipse.swt.widgets.Tree", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( style ) {
    this.base( arguments );
    
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );

    this._columnArea = new qx.ui.layout.CanvasLayout();
    this._columnArea.setTop( 0 );
    this._columnArea.setLeft( 0 );
    this._columnArea.setDisplay( false );
    this.add( this._columnArea );
    
    this._headerVisible = false;
    this._columnAreaHeight = 20;
    
    this._columns = new Array();
    this._columnOrder = new Array();
    this._columnOrder.push(0); // we have at least one column
    
    // init internal tree widget
    var trs = qx.ui.tree.TreeRowStructure.getInstance().newRow();
    this._tree = new qx.ui.tree.Tree( trs );
    this._tree.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    this._tree.setHideNode( true );
    this._tree.setRootOpenClose( true );
    this._tree.setUseDoubleClick( false );  // true supresses dblclick events !

    // TODO [rh] this is only to make the tree focusable at all
    this._tree.setTabIndex( 1 );
    var manager = this._tree.getManager();
    manager.setMultiSelection( qx.lang.String.contains( style, "multi" ) );
    manager.addEventListener( "changeSelection", this._onChangeSelection, this );
    this._tree.addEventListener( "treeOpenWithContent", this._onItemExpanded, this );
    this._tree.addEventListener( "treeClose", this._onItemCollapsed, this );
    this._tree.addEventListener( "contextmenu", this._onContextMenu, this );
    this._tree.addEventListener( "focus", this._onFocusIn, this );
    this._tree.addEventListener( "blur", this._onFocusOut, this );    
    this._tree.addEventListener( "mousewheel", this._onTreeMouseWheel, this );    
    this._tree.addEventListener( "appear", this._updateLayout, this );    
    // TODO [rst] Find out why this is not the default appearance
    this._tree.setAppearance( "tree" );
    this.add( this._tree );

    this._rwtStyle = style;
    this._selectionListeners = false;
    this._treeListeners = false;
    this._hasFocus = false;

    // Create horizontal scrollBar
    this._horzScrollBar = new qx.ui.basic.ScrollBar( true );
    this._horzScrollBar.setHeight( this._horzScrollBar.getPreferredBoxHeight() );
    this._horzScrollBar.addEventListener( "changeValue", this._onHorzScrollBarChangeValue, this );
    this.add( this._horzScrollBar );
    
    // Create vertical scrollBar
    this._vertScrollBar = new qx.ui.basic.ScrollBar( false );
    this._vertScrollBar.setWidth( this._vertScrollBar.getPreferredBoxWidth() );
    this._vertScrollBar.addEventListener( "changeValue", this._onVertScrollBarChangeValue, this );
    this.add( this._vertScrollBar );

    this.addEventListener( "changeWidth", this._onChangeSize, this );
    this.addEventListener( "changeHeight", this._onChangeSize, this );
    
  },
  
  destruct : function() {
    this.removeEventListener( "changeWidth", this._onChangeSize, this );
    this.removeEventListener( "changeHeight", this._onChangeSize, this );

    if( this._columnArea ) {
      this._columnArea.dispose();
      this._columnArea = null;
    }
    if( this._tree ) {
      var manager = this._tree.getManager();
      manager.removeEventListener( "changeSelection", this._onChangeSelection, this );
      this._tree.removeEventListener( "treeOpenWithContent", this._onItemExpanded, this );
      this._tree.removeEventListener( "treeClose", this._onItemCollapsed, this );
      this._tree.removeEventListener( "contextmenu", this._onContextMenu, this );
      this._tree.removeEventListener( "focus", this._onFocusIn, this );
      this._tree.removeEventListener( "blur", this._onFocusOut, this );
      this._tree.removeEventListener( "appear", this._updateLayout, this );
      this._tree.dispose();
      this._tree = null;
    }
    if( this._horzScrollBar ) {
      this._horzScrollBar.removeEventListener( "changeValue", this._onHorzScrollBarChangeValue, this );
      this._horzScrollBar.dispose();
      this._horzScrollBar = null;
    }
    if( this._vertScrollBar ) {
      this._vertScrollBar.removeEventListener( "changeValue", this._onVertScrollBarChangeValue, this );
      this._vertScrollBar.dispose();
      this._vertScrollBar = null;
    }
  },
  
  members : {
  	_addColumn : function( column ) {
      column.setHeight( this._columnArea.getHeight() );
      this._hookColumnMove( column );
      column.addEventListener( "changeWidth", this._onColumnChangeSize, this );
      this._columnArea.add( column );
      this._columns.push( column );
      this._updateScrollWidth();
      this._updateLayout();
      
      // inform all items about the new column
      var items = this._tree.getItems( true, false );
      if( items.length > 0 ) {
        for( var i = 0; i < items.length; i++ ) {
          if(items[ i ] instanceof org.eclipse.swt.widgets.TreeItem ) {
            items[ i ].columnAdded();
          }
        }
      }
    },
    
    _hookColumnMove : function( column ) {
      column.addEventListener( "changeLeft", this._onColumnChangeSize, this );
    },
    
    _unhookColumnMove : function( column ) {
      column.removeEventListener( "changeLeft", this._onColumnChangeSize, this );
    },
    
    _updateScrollWidth : function() {
      var width;
      if( this.getColumnCount() == 0 ) {
        width = this.getDefaultColumnWidth();
      } else {
        width = this.getColumnsWidth();
      }
      this._horzScrollBar.setMaximum( width - this._vertScrollBar.getWidth() );
    },
    
    _updateScrollHeight : function() {
    	this._vertScrollBar.setMaximum( this.getItemsHeight() );
    },
    
    getItemsHeight : function() {
    	// TODO: [bm] do we really need to calc this ourselves???
    	// TODO: [bm] review this when images are in place
    	var visibleItems = this._tree.getItems(true, false);
    	var itemsHeight = (visibleItems.length-1)*16;
    	return itemsHeight;
    },
    
    getColumnCount : function() {
      return this._columns.length;
    },
    
    getDefaultColumnWidth : function() {
      return 0;
    },
    
    getColumnsWidth : function() {
      var width = 0;
      for(var i=0; i<this._columns.length; i++) {
        width += this._columns[ i ].getWidth();
      }
      return width;
    },
    
    _removeColumn : function( column ) {
      column.removeEventListener( "changeWidth", this._onColumnChangeSize, this );
    },

    _onColumnChangeSize : function( evt ) {
      var items = this._tree.getItems( true, false );
      if( items.length > 0 ) {
        for( var i = 0; i < items.length; i++ ) {
          if(items[ i ] instanceof org.eclipse.swt.widgets.TreeItem ) {
            items[ i ].updateColumnsWidth();
          }
        }
      }
      this._updateScrollWidth();
    },

    //////////////////////////////////////////////////////////////
    // Show and hide the resize line used by column while resizing

    _showResizeLine : function( x ) {
      if( this._resizeLine == null ) {
        this._resizeLine = new qx.ui.basic.Terminator();
        this._resizeLine.setAppearance( "tree-column-resizer" );
        this.add( this._resizeLine );
        qx.ui.core.Widget.flushGlobalQueues();
      }
      var top = this._tree.getTop();
      this._resizeLine._renderRuntimeTop( top );
      var left = x - 2; // TODO: add hor scroll value
      this._resizeLine._renderRuntimeLeft( left );
      var height = this._tree.getHeight();
      this._resizeLine._renderRuntimeHeight( height );
      this._resizeLine.removeStyleProperty( "visibility" );
    },

    _hideResizeLine : function() {
      this._resizeLine.setStyleProperty( "visibility", "hidden" );
    },
        
    setHeaderHeight : function( height ) {
      this._columnAreaHeight = height;
      this._columnArea.setHeight( this._columnAreaHeight );
      var columns = this._columnArea.getChildren();
      for( var i = 0; i < columns.length; i++ ) {
        columns[ i ].setHeight( height );
      }
      this._updateLayout();
    },
    
    setHeaderVisible : function( value ) {
      this._columnArea.setDisplay( value );
      this._headerVisible = value;
      this._updateLayout();
    },
    
    getTree : function() {
      return this._tree;
    },

    getColumnAreaHeight : function() {
      if( this._headerVisible ) {
        return this._columnAreaHeight;
      }
      return 0;
    },
    
    showItem : function( item ) {
      // TODO: [bm] implement scroll to item
    },
    
    _updateLayout : function() {
      if( !this._tree.isCreated() ) {
        this._tree.addEventListener( "appear",
                                 this._updateLayout, this );
        return;
      }
      this._columnArea.setWidth( this.getWidth() );
      this._columnArea.setHeight( this.getColumnAreaHeight() );
      this._tree.setWidth( Math.max( this.getWidth(), this.getColumnsWidth() ) );
      this._tree.setHeight( Math.max( this.getHeight() - this.getColumnAreaHeight(), this.getItemsHeight() ) );
      this._tree.setTop( this.getColumnAreaHeight() );
      
      if( this._tree.getWidth() == this.getWidth() ) {
        this._horzScrollBar.setDisplay( false );
        this._horzScrollBar.setHeight( 0 );
        
      } else {
        this._horzScrollBar.setDisplay( true );
        this._horzScrollBar.setLeft( 0 );
        this._horzScrollBar.setHeight( this._horzScrollBar.getPreferredBoxHeight() );
        this._horzScrollBar.setTop( this.getHeight() - this._horzScrollBar.getPreferredBoxHeight() );
        this._horzScrollBar.setWidth( this.getWidth() );
        this._updateScrollWidth();
      }

      if( (this.getHeight() - this.getColumnAreaHeight() ) < this.getItemsHeight() ) {
        this._vertScrollBar.setDisplay( true );
       this._vertScrollBar.setWidth( this._vertScrollBar.getPreferredBoxWidth() );
       this._vertScrollBar.setLeft( this.getWidth() - this._vertScrollBar.getPreferredBoxWidth() );
       this._vertScrollBar.setHeight( this.getHeight() - this._horzScrollBar.getHeight() - this.getColumnAreaHeight() );
       this._vertScrollBar.setTop( this.getColumnAreaHeight() );

        this._updateScrollHeight();
        this._horzScrollBar.setWidth( this._horzScrollBar.getWidth() - this._vertScrollBar.getWidth() );
      } else {
        this._vertScrollBar.setDisplay( false );
       this._vertScrollBar.setWidth( 0 );
      }
    },

    _onHorzScrollBarChangeValue : function() {
      this._columnArea.setLeft( 0 - this._horzScrollBar.getValue() );
      this._tree.setLeft( 0 - this._horzScrollBar.getValue() );
    },
    
    _onVertScrollBarChangeValue : function() {
      this._tree.setTop( this.getColumnAreaHeight() - this._vertScrollBar.getValue() );
    },

    _onChangeSize : function( evt ) {
      this._updateLayout();
    },
    
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
    
    setColumnOrder : function ( order ) {
      this._columnOrder = order;
    },
    
    getColumnOrder : function() {
      return this._columnOrder;
    },
    
    /////////////////
    // Event Listener

    _onTreeMouseWheel : function( evt ) {
      var change = evt.getWheelDelta() * 16; // default item height
      this._vertScrollBar.setValue( this._vertScrollBar.getValue() - change );
    },
    
    _onChangeSelection : function( evt ) {
      this._updateSelectedItemState();
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = wm.findIdByWidget( this );
        var item = this._tree.getManager().getLeadItem();
        var selection = this._getSelectionIndices();
        if( selection != "" ) {
          req.addParameter( id + ".selection", this._getSelectionIndices() );
          // TODO [rst] Prevent selecting the root item.
          //      When first visible item is selected and arrow up is pressed the root
          //      item ( == this ) is selected which results in an invisible selection.
          if( item == this ) {
          this._tree.getFirstVisibleChildOfFolder().setSelected( true );
          this._tree.setSelected( false );
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
      this._tree.setHeight( Math.max( this.getHeight(), this.getItemsHeight() ) );
      this._updateScrollHeight();
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
      this._tree.setHeight( Math.max( this.getHeight(), this.getItemsHeight() ) );
      this._updateScrollHeight();
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
      var selectedItems = this._tree.getManager().getSelectedItems();
      // Set a flag that signals unfocused state on every item.
      for( var i = 0; i < selectedItems.length; i++ ) {
        var label_ = selectedItems[ i ].getLabelObject();
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
      if( this._tree.getManager().getMultiSelection() ) {
        var selectedItems = this._tree.getManager().getSelectedItems();
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
        var item = this._tree.getManager().getSelectedItem();
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