
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
    this.setAppearance( "tree-container" );

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
    this._tree.setHideNode( true );
    this._tree.setRootOpenClose( true );
    this._tree.setUseDoubleClick( false );  // true supresses dblclick events !
    this._tree.setOverflow( qx.constant.Style.OVERFLOW_AUTO );

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
    this._tree.addEventListener( "appear", this._updateLayout, this );    
    // TODO [rst] Find out why this is not the default appearance
    this._tree.setAppearance( "tree" );
    
    // listen for scroll events to move column area
    this._tree.__onscroll = qx.lang.Function.bindEvent( this._onTreeScroll, this );
    this._tree.addEventListener( "changeElement", this._onTreeElementChange, this._tree );
    
    this.add( this._tree );

    this._rwtStyle = style;
    this._selectionListeners = false;
    this._treeListeners = false;
    this._hasFocus = false;

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
    	var el = this._tree.getElement();
	    if (el) {
	      // remove inline event
	      if (qx.core.Variant.isSet("qx.client", "mshtml")) {
	        el.detachEvent("onscroll", this._tree.__onscroll);
	      } else {
	        el.removeEventListener("scroll", this._tree.__onscroll, false);
	      }
	      delete this.__onscroll;
	    }
      var manager = this._tree.getManager();
      manager.removeEventListener( "changeSelection", this._onChangeSelection, this );
      this._tree.removeEventListener( "treeOpenWithContent", this._onItemExpanded, this );
      this._tree.removeEventListener( "treeClose", this._onItemCollapsed, this );
      this._tree.removeEventListener( "contextmenu", this._onContextMenu, this );
      this._tree.removeEventListener( "focus", this._onFocusIn, this );
      this._tree.removeEventListener( "blur", this._onFocusOut, this );
      this._tree.removeEventListener( "appear", this._updateLayout, this );
      this._tree.removeEventListener( "changeElement", this._onTreeElementChange, this._tree );
      this._tree.dispose();
      this._tree = null;
    }
  },
  
  members : {
  	
  	// delegater
  	setBackgroundColor : function( color ) {
  		this._tree.setBackgroundColor( color );
  	},
  	
	  _onTreeElementChange : function( evt ) {
	    var value = evt.getValue();
	    if (value)
	      {
	        // Register inline event
	        if (qx.core.Variant.isSet("qx.client", "mshtml")) {
	          value.attachEvent("onscroll", this.__onscroll);
	        } else {
	          value.addEventListener("scroll", this.__onscroll, false);
	        }
	      }
	  },
	  
	  _onTreeScroll : function( e ) {
	    var target = e.target;
	    if( e.target == null ) {
	    	target = e.srcElement;
	    }
	    this._columnArea.setLeft( 0 - target.scrollLeft );
	    // inform server about scroll pos
	    if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var treeId = wm.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( treeId + ".scrollLeft", target.scrollLeft );
        req.addParameter( treeId + ".scrollTop", target.scrollTop );
      }
    },
  
    _addColumn : function( column ) {
      column.setHeight( this._columnArea.getHeight() );
      this._hookColumnMove( column );
      column.addEventListener( "changeWidth", this._onColumnChangeSize, this );
      this._columnArea.add( column );
      this._columns.push( column );
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
      if( this._columns.length > 0 ) {
	    for(var i=0; i<this._columns.length; i++) {
	      width += this._columns[ i ].getWidth();
	    }
      } else {
        width = this._tree.getWidth();
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
      var left = x - 2 + this._columnArea.getLeft();
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
    
    showItem : function( itemOrEvent ) {
    	var item;
      if(!(itemOrEvent instanceof org.eclipse.swt.widgets.TreeItem )) {
      	item = itemOrEvent.getTarget();
      } else {
        item = itemOrEvent;
      }
      if( !item.isCreated() ) {
      	item.addEventListener( "appear", this.showItem, this );
      	return;
      }
      item.scrollIntoView();
    },
    
    _updateLayout : function() {
      if( !this._tree.isCreated() ) {
        this._tree.addEventListener( "appear",
                                 this._updateLayout, this );
        return;
      }
      this._columnArea.setWidth( this.getWidth() );
      this._columnArea.setHeight( this.getColumnAreaHeight() );
      this._tree.setWidth( this.getWidth() );
      this._tree.setHeight( this.getHeight() - this.getColumnAreaHeight() );
      this._tree.setTop( this.getColumnAreaHeight() );
      
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
            result += wm.findIdByWidget(item);
            result += ",";
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