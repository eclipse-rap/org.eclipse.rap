/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
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
 * org.eclipse.swt.widgets.Table.
 * @event itemselected
 * @event itemdefaultselected
 * @event itemchecked
 */
qx.Class.define( "org.eclipse.swt.widgets.Table", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( id, style ) {
    this.base( arguments );
    this.setAppearance( "table" );
    this.setHideFocus( true );
    // TODO [rh] this is preliminary and can be removed once a tabOrder is
    //      available
    this.setTabIndex( 1 );
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    // Denotes the row that received the last click-event to swallow unwanted 
    // click-events while double-clicking
    this._suspendClicksOnRow = null;
    // Draw grid lines?
    this._linesVisible = false;
    this._borderWidth = 0;
    // Default column width is used when there are no columns specified
    this._defaultColumnWidth = 0;
    // The item index that is currently displayed in the first visible row
    this._topIndex = 0;
    this._topIndexChanging = false;
    // indicates that topIndex was changed client-side (e.g. by scrolling)
    this._topIndexChanged = false;
    // Internally used fields to manage visible rows and scrolling
    this._itemHeight = 0;
    this._rows = new Array();
    this._items = new Array();
    this._itemCount = 0;
    this._unresolvedItems = null;
    this._checkBoxes = null;
    if( qx.lang.String.contains( style, "check" ) ) {
      this._checkBoxes = new Array();
    }
    // Determine multi-selection
    this._multiSelect = qx.lang.String.contains( style, "multi" );
    // Conains all itemIndices which are currently selected
    this._selected = new Array();
    // Most recent item selected by ctrl-click or ctrl+shift-click (only 
    // relevant for multi-selection)
    this._selectionStart = -1;
    // Denotes the index of the focused TableItem or -1 if none is focused
    this._focusIndex = -1;
    // An item only used to draw the area where no actual items are but that
    // needs to be drawn since the table bounds are grater than the number of
    // items
    this._emptyItem = new org.eclipse.swt.widgets.TableItem( this, -1 );
    // An item used to represent a virtual item while it is being resolved, 
    // that is a request is sent to the server to obtain the actual values
    this._virtualItem = new org.eclipse.swt.widgets.TableItem( this, -1 );
    this._virtualItem.setTexts ( [ "..." ] );
    // One resize line shown while resizing a column, provided for all columns  
    this._resizeLine = null;
    // left and width values for the item-image and -text part for each column
    this._itemImageLeft = new Array();
    this._itemImageWidth = new Array();
    this._itemTextLeft = new Array();
    this._itemTextWidth = new Array();
    //
    // Construct a column area where columns can be scrolled in
    this._columnArea = new qx.ui.layout.CanvasLayout();
    this._columnArea.setTop( 0 );
    this._columnArea.setLeft( 0 );
    this.add( this._columnArea );
    // Construct client area in which the table items will live
    this._clientArea = new qx.ui.layout.CanvasLayout();
    this._clientArea.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    this._clientArea.setTop( 20 );
    this._clientArea.setLeft( 0 );
    this._clientArea.addEventListener( "mousewheel", this._onClientAreaMouseWheel, this );
    this._clientArea.addEventListener( "appear", this._onClientAppear, this );
    this._clientArea.setHtmlProperty( "id", "client-area" );
    // Create horizontal scrollBar
    this._horzScrollBar = new qx.ui.basic.ScrollBar( true );
    this._horzScrollBar.setMergeEvents( true );
    this.add( this._horzScrollBar );
    this._horzScrollBar.setHeight( this._horzScrollBar.getPreferredBoxHeight() );
    this._horzScrollBar.addEventListener( "changeValue", this._onHorzScrollBarChangeValue, this );
    // Create vertical scrollBar
    this._vertScrollBar = new qx.ui.basic.ScrollBar( false );
    this._vertScrollBar.setMergeEvents( true );
    this.add( this._vertScrollBar );
    this._vertScrollBar.setWidth( this._vertScrollBar.getPreferredBoxWidth() );
    this._vertScrollBar.addEventListener( "changeValue", this._onVertScrollBarChangeValue, this );
    // Listen to size changes to adjust client area size
    this.addEventListener( "changeWidth", this._onChangeSize, this );
    this.addEventListener( "changeHeight", this._onChangeSize, this );
    this.addEventListener( "changeEnabled", this._onChangeEnabled, this );
    // Keyboard navigation
    this._keyboardSelecionChanged = false;
    // TODO [rh] key events in Safari not working properly, see
    //   https://bugs.eclipse.org/bugs/show_bug.cgi?id=235531 
    //   http://bugzilla.qooxdoo.org/show_bug.cgi?id=785
    if( !qx.core.Variant.isSet( "qx.client", "webkit" ) ) {
      this.addEventListener( "keydown", this._onKeyDown, this );
      this.addEventListener( "keypress", this._onKeyPress, this );
      this.addEventListener( "keyup", this._onKeyUp, this );
    }
    // Listen to send event of request to report current state
    var req = org.eclipse.swt.Request.getInstance();
    req.addEventListener( "send", this._onSendRequest, this );
    //
    var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
    widgetManager.add( this._clientArea, id + "_clientArea", false );
    this.add( this._clientArea );
  },

  destruct : function() {
    var req = org.eclipse.swt.Request.getInstance();
    req.removeEventListener( "send", this._onSendRequest, this );
    this.removeEventListener( "changeWidth", this._onChangeSize, this );
    this.removeEventListener( "changeHeight", this._onChangeSize, this );
    this.removeEventListener( "changeEnabled", this._onChangeEnabled, this );
    // TODO [rh] key events in Safari not working properly, see
    //   https://bugs.eclipse.org/bugs/show_bug.cgi?id=235531 
    //   http://bugzilla.qooxdoo.org/show_bug.cgi?id=785
    if( !qx.core.Variant.isSet( "qx.client", "webkit" ) ) {
      this.removeEventListener( "keydown", this._onKeyDown, this );
      this.removeEventListener( "keypress", this._onKeyPress, this );
      this.removeEventListener( "keyup", this._onKeyUp, this );
    }
    this._virtualItem.dispose();
    this._emptyItem.dispose();
    // For performance reasons, when disposing a a Table, the server-side LCA 
    // does *not* dispose of each TableItem, instead this is done here in one 
    // batch without updating the Tables state as it is in disposal anyway
    for( var i = 0; i < this._items.length; i++ ) {
      if( this._items[ i ] ) {
        this._items[ i ].dispose();
      }
    }
    this._items = null;
    var req = org.eclipse.swt.Request.getInstance();
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
    if( this._clientArea ) {
      this._clientArea.removeEventListener( "mousewheel", this._onClientAreaMouseWheel, this );
      this._clientArea.removeEventListener( "appear", this._onClientAppear, this );
      this._clientArea.dispose();
      this._clientArea = null;
    }
    if( this._columnArea ) {
      this._columnArea.dispose();
      this._columnArea = null;
    }
    if( this._resizeLine ) {
      this._resizeLine.setParent( null );
      this._resizeLine.dispose();
    }
    if( this._rows ) {
      for( var i = 0; i < this._rows.length; i++ ) {
        this._rows[ i ].dispose();
      }
      this._rows = null;
    }
    if( this._checkBoxes !== null ) {
      for( var i = 0; i < this._checkBoxes.length; i++ ) {
        this._checkBoxes[ i ].dispose();
      }
      this._checkBoxes = null;
    }
    this._itemImageLeft = null;
    this._itemImageWidth = null;
    this._itemTextLeft = null;
    this._itemTextWidth = null;
  },
  
  events : {
    "itemselected" : "qx.event.type.DataEvent",
    "itemdefaultselected" : "qx.event.type.DataEvent",
    "itemchecked" : "qx.event.type.DataEvent"
  },

  statics : {
    CHECK_WIDTH : 21,
    CHECK_IMAGE_WIDTH : 13,
    CHECK_IMAGE_HEIGHT : 13,
    
    ////////////////////////////////////
    // Helper to determine modifier keys
    
    _isShiftOnlyPressed : function( evt ) {
      return    evt.isShiftPressed() 
             && !evt.isCtrlPressed() 
             && !evt.isAltPressed() 
             && !evt.isMetaPressed();      
    },
    
    _isCtrlOnlyPressed : function( evt ) {
      return    evt.isCtrlOrCommandPressed() 
             && !evt.isShiftPressed() 
             && !evt.isAltPressed();
    },
    
    _isCtrlShiftOnlyPressed : function( evt ) {
      return    evt.isCtrlOrCommandPressed() 
             && evt.isShiftPressed() 
             && !evt.isAltPressed();
    },
    
    _isMetaOnlyPressed : function( evt ) {
      return    evt.isAltPressed() 
             && !evt.isShiftPressed() 
             && !evt.isCtrlPressed();
    },
    
    _isNoModifierPressed : function( evt ) {
      return    !evt.isCtrlPressed() 
             && !evt.isShiftPressed() 
             && !evt.isAltPressed() 
             && !evt.isMetaPressed();      
    }
     
  },
  
  members : {

    setHeaderHeight : function( value ) {
      this._columnArea.setHeight( value );
      var columns = this._columnArea.getChildren();
      for( var i = 0; i < columns.length; i++ ) {
        columns[ i ].setHeight( value );
      }
      this._updateClientAreaSize();
    },

    setHeaderVisible : function( value ) {
      this._columnArea.setVisibility( value );
      this._topIndex = 0;
      this._vertScrollBar.setValue( 0 );
      this._horzScrollBar.setValue( 0 );
      this._updateClientAreaSize();
    },

    setItemHeight : function( value ) {
      this._itemHeight = value;
      this._updateScrollHeight();
      if( this._updateRowCount() ) {
        this._updateRows();
      }
    },
    
    getItemHeight : function() {
      return this._itemHeight;  
    },
    
    setItemMetrics : function( columnIndex, imageLeft, imageWidth, textLeft, textWidth ) {
      this._itemImageLeft[ columnIndex ] = imageLeft;
      this._itemImageWidth[ columnIndex ] = imageWidth;
      this._itemTextLeft[ columnIndex ] = textLeft;
      this._itemTextWidth[ columnIndex ] = textWidth;
    },
    
    getItemImageLeft : function( columnIndex ) {
      return this._itemImageLeft[ columnIndex ];
    },
    
    getItemImageWidth : function( columnIndex ) {
      return this._itemImageWidth[ columnIndex ];
    },
    
    getItemTextLeft : function( columnIndex ) {
      return this._itemTextLeft[ columnIndex ];
    },
    
    getItemTextWidth : function( columnIndex ) {
      return this._itemTextWidth[ columnIndex ];
    },
    
    /** Only called by server-side */
    setTopIndex : function( value ) {
      this._internalSetTopIndex( value, true );
      this._topIndexChanged = false;
    },

    _internalSetTopIndex : function( value, updateVertScrollBar ) {
      if( this._topIndex !== value ) {
      	this._topIndexChanging = true;
      	if( updateVertScrollBar ) {
	        this._vertScrollBar.setValue( value * this._itemHeight );
      	}
      	var delta = value - this._topIndex;
        this._topIndex = value;
        this._scrollRowsVertical( delta );
        this._topIndexChanged = true;
        this._topIndexChanging = false;
      }
    },
    
    setBorderWidth : function( value ) {
      this._borderWidth = value;
    },

    getColumn : function( index ) {
      return this._columnArea.getChildren()[ index ];
    },

    getColumnCount : function() {
      return this._columnArea.getChildrenLength();
    },
    
    getColumns : function() {
      return this._columnArea.getChildren();  
    },
    
    getColumnsWidth : function() {
      var result = 0;
      var columns = this._columnArea.getChildren();
      for( var i = 0; i < columns.length; i++ ) {
        result += columns[ i ].getWidth();
      }
      return result;
    },

    setDefaultColumnWidth : function( value ) {
      this._defaultColumnWidth = value;
      this._updateScrollWidth();
    },

    getDefaultColumnWidth : function() {
      return this._defaultColumnWidth;
    },

    setLinesVisible : function( value ) {
      this._linesVisible = value;
      for( var i = 0; i < this._rows.length; i++ ) {
        this._rows[ i ].setLinesVisible( value );        
      }
      this._updateRows();
    },

    getLinesVisible : function() {
      return this._linesVisible;
    },
    
    hasCheckBoxes : function() {
      return this._checkBoxes !== null;
    },
    
    setFocusIndex : function( value ) {
      if( value !== this._focusIndex ) {
        var oldFocusIndex = this._focusIndex;
        this._focusIndex = value;
        // update previously focused item
        if( oldFocusIndex !== -1 ) {
          this.updateItem( oldFocusIndex, false );
        }
        // update new focused item
        if( this._focusIndex !== -1 ) {
          this.updateItem( this._focusIndex, false );
        }
      }
    },
    
    getFocusIndex : function() {
    	return this._focusIndex;
    },
    
    setItemCount : function( value ) {
      this._itemCount = value;
      this._updateScrollHeight();
      this._updateRows();
    },
    
    _onChangeEnabled : function( evt ) {
      this._updateRows();
    },
    
    ////////////////////////////
    // Listeners for client area

    _onCheckBoxClick : function( evt ) {
      var rowIndex = this._checkBoxes.indexOf( evt.getTarget() );
      var itemIndex = this._getItemIndexFromRowIndex( rowIndex );
      this._toggleCheckState( itemIndex );
    },
    
    _onRowClick : function( evt ) {
      var row = evt.getTarget();
      var rowIndex = this._rows.indexOf( row );
      var itemIndex = this._topIndex + rowIndex;
      if(    itemIndex >= 0 
          && itemIndex < this._itemCount
          && this._items[ itemIndex ]
          && this._suspendClicksOnRow != row ) 
      {
        this._suspendClicksOnRow = row;
        qx.client.Timer.once( this._resumeClicks, 
                              this,
                              org.eclipse.swt.EventUtil.DOUBLE_CLICK_TIME );
        if( this._multiSelect ) {
          this._onMultiSelectRowClick( evt, itemIndex )
        } else {
          this._setSingleSelection( itemIndex );
        }
        this.setFocusIndex( itemIndex );
        this._makeItemFullyVisible( itemIndex );        
        this._updateSelectionParam();
        this.createDispatchDataEvent( "itemselected", itemIndex );
      }
    },
    
    _onMultiSelectRowClick : function( evt, itemIndex ) {
      if( evt.isRightButtonPressed() ) {
        if( !this._isItemSelected( itemIndex ) ) {
          this._setSingleSelection( itemIndex );
          this._selectionStart = -1;
        }
      } else {
        if( org.eclipse.swt.widgets.Table._isCtrlOnlyPressed( evt ) ) {
          if( this._isItemSelected( itemIndex ) ) {
            this._deselectItem( itemIndex, true );
          } else {
            this._selectItem( itemIndex );
          }
        }
        if(    org.eclipse.swt.widgets.Table._isShiftOnlyPressed( evt ) 
            || org.eclipse.swt.widgets.Table._isCtrlShiftOnlyPressed( evt ) ) 
        {
          if(    org.eclipse.swt.widgets.Table._isShiftOnlyPressed( evt ) 
              && this._selectionStart !== -1 ) 
          {
            this._clearSelection();
          }
          var selectionStart
            = this._selectionStart !== - 1 
            ? this._selectionStart 
            : this._focusIndex;
          if( selectionStart !== -1 ) {
            var start = Math.min( selectionStart, itemIndex );
            var end = Math.max( selectionStart, itemIndex );
            for( var i = start; i <= end; i++ ) {
              if( !this._isItemSelected( i ) ) {
                this._selectItem( i );
              }
            }
          }
        } 
        if(    org.eclipse.swt.widgets.Table._isNoModifierPressed( evt )
            || org.eclipse.swt.widgets.Table._isMetaOnlyPressed( evt ) )
        {
          this._setSingleSelection( itemIndex );
        }
        
        if(    org.eclipse.swt.widgets.Table._isCtrlOnlyPressed( evt ) 
            || org.eclipse.swt.widgets.Table._isCtrlShiftOnlyPressed( evt ) ) 
        {
          this._selectionStart = itemIndex;
        } else {
          this._selectionStart = -1;
        }
      }
    },
    
    _setSingleSelection : function( itemIndex ) {
      this._clearSelection();
      this._selectItem( itemIndex );
    },
    
    _resumeClicks : function() {
      this._suspendClicksOnRow = null;
    },
    
    _onRowDblClick : function( evt ) {
      var rowIndex = this._rows.indexOf( evt.getTarget() );
      var itemIndex = this._getItemIndexFromRowIndex( rowIndex );
      if( itemIndex !== -1 ) {
        this.createDispatchDataEvent( "itemdefaultselected", itemIndex );
      }
    },
    
    _onRowContextMenu : function( evt ) {
      if(    org.eclipse.swt.widgets.Table._isNoModifierPressed( evt ) 
          || org.eclipse.swt.widgets.Table._isMetaOnlyPressed( evt ) ) 
      {
        // TODO [rh] avoid this call if item already selected
        this._onRowClick( evt );
        var target = evt.getTarget();
        var contextMenu = this.getContextMenu();
        if( contextMenu !== null ) {
          contextMenu.setLocation( evt.getPageX(), evt.getPageY() );
          contextMenu.setOpener( this );
          contextMenu.show();
        }
      }
    },
    
    _toggleCheckState : function( itemIndex ) {
      if( this._checkBoxes != null ) {
        var item = this._items[ itemIndex ];
        if( itemIndex >= 0 && itemIndex < this._itemCount && item ) {
          item.setChecked( !item.getChecked() );
          // Reflect changed check-state in case there is no server-side listener 
          // If changed item is currently not visible, omit update
          var rowIndex = this._getRowIndexFromItemIndex( itemIndex );
          if( rowIndex !== -1 ) {
	          this._updateRow( rowIndex, itemIndex );
          }
          this._updateCheckParam( item );
          this.createDispatchDataEvent( "itemchecked", itemIndex );
        }
      }
    },
    
    _updateSelectionParam : function() {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var tableId = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      var selectedIndices = "";
      for( var i = 0; i < this._selected.length; i++ ) {
        if( selectedIndices !== "" ) {
          selectedIndices += ",";
        }
        selectedIndices += this._selected[ i ].toString();
      }
      req.addParameter( tableId + ".selection", selectedIndices );
    },
    
    _updateCheckParam : function( item ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( item );
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( id + ".checked", item.getChecked() );
    },

    _onClientAreaMouseWheel : function( evt ) {
      var change = evt.getWheelDelta() * this._itemHeight * 2;
      this._vertScrollBar.setValue( this._vertScrollBar.getValue() - change );
    },

    _onChangeSize : function( evt ) {
      this._updateClientAreaSize();
    },

    _onClientAppear : function( evt ) {
      this._updateRowCount();
      this._updateRows();
    },
    
    ///////////////////////
    // Keyboard navigation
    
    _onKeyDown : function( evt ) {
      switch( evt.getKeyIdentifier() ) {
        case "Space":
          this._toggleCheckState( this._focusIndex );
          break;
        case "Enter":
          // in sync with SWT: fire defaultSelection when <Return> is pressed, 
          // regardless which modifier-key(s) are held down
          if( this._focusIndex !== -1 ) {
            var itemIndex = this._getItemIndexFromRowIndex( this._focusIndex );
            if( itemIndex !== -1 ) {
              this.createDispatchDataEvent( "itemdefaultselected", itemIndex );
            }
          }
          break;
      }
    },
    
    _onKeyPress : function( evt ) {
      var keyIdentifier = evt.getKeyIdentifier();
      if(    org.eclipse.swt.widgets.Table._isNoModifierPressed( evt ) 
          && (    keyIdentifier === "Up" 
               || keyIdentifier === "Down"
               || keyIdentifier === "PageUp"
               || keyIdentifier === "PageDown"
               || keyIdentifier === "Home" 
               || keyIdentifier === "End" ) ) 
      {
        var gotoIndex = this._calcGotoIndex( this._focusIndex, keyIdentifier );
        if(    gotoIndex !== this._focusIndex
            && gotoIndex >= 0 
            && gotoIndex < this._itemCount )
        {
          var oldFocusIndex = this._focusIndex;
          this._setSingleSelection( gotoIndex );
          // Make the just selected item visible
          // TODO [rh] similar code as in _makeItemFullyVisible(), unite
          if( !this._isItemFullyVisible( gotoIndex ) ) {
            var topIndex;
            // If last item was selected, try to set topIndex such that as 
            // much items as possible are shown  
            if( gotoIndex === this._itemCount - 1 ) {
              // not exactly sure why but +1 takes care that the selected item 
              // is fully visible
              topIndex = gotoIndex - this._getFullyVisibleRowCount() + 1;
            } else {
              // Move topIndex the same distance as the selection was moved
              topIndex = this._topIndex - oldFocusIndex + gotoIndex;
            }
            // Fix for bug #233964:
            // Ensure that the topIndex does not exceed the range of items
            // this would cause a VIRTUAL table to redraw the wrong items as
            // it relies on the topIndex to determine currently visible items 
            if( topIndex < 0 ) {
              topIndex = 0;
            } else if( topIndex > this._itemCount ) {
            	topIndex = this._itemCount;
            }
            this._internalSetTopIndex( topIndex, true );
          }
          this.setFocusIndex( gotoIndex );
          this._keyboardSelecionChanged = true;
        }
      }
    },
    
    _calcGotoIndex : function( currentIndex, keyIdentifier ) {
      var result = currentIndex;
      switch( keyIdentifier ) {
        case "Home":
          result = 0; 
          break;
        case "End":
          result = this._itemCount - 1;
          break;
        case "Up":
          result = currentIndex - 1;
          break;
        case "Down":
          result = currentIndex + 1;
          break;
        case "PageUp":
          result = currentIndex - this._getFullyVisibleRowCount();
          if( result < 0 ) {
            result = 0; 
          }
          break;
        case "PageDown":
          result = currentIndex + this._getFullyVisibleRowCount();
          if( result > this._itemCount - 1 ) {
            result = this._itemCount - 1; 
          }
          break;
      }
      return result;
    },
    
    _getFullyVisibleRowCount : function() {
      return Math.floor( this._clientArea.getHeight() / this._itemHeight );
    },
    
    _onKeyUp : function( evt ) {
      if( this._keyboardSelecionChanged ) {
        this._keyboardSelecionChanged = false;
        this._updateSelectionParam();
        this.createDispatchDataEvent( "itemselected", this._focusIndex );
      }      
    },

    ////////////////////////
    // Scroll bar listeners
    
    _onVertScrollBarChangeValue : function() {
    	// Prevent _internalSetTopIndex() from being called when we are currently
    	// changing the topIndex. _internalSetTopIndex() calls
    	// _vertScrollBar.setValue() which fires this event handler
    	if( !this._topIndexChanging ) {
	      // Calculate new topIndex
	      var newTopIndex = 0;
	      if( this._itemHeight !== 0 ) {
	        var scrollTop 
	          = this._clientArea.isCreated()
	          ? this._vertScrollBar.getValue()
	          : 0;
	        newTopIndex = Math.floor( scrollTop / this._itemHeight );
	      }
	      // set new topIndex -> rows are updateded if necessary
	      this._internalSetTopIndex( newTopIndex, false );
    	}
    },

    _onHorzScrollBarChangeValue : function() {
      this._columnArea.setLeft( 0 - this._horzScrollBar.getValue() );
      this._updateRowBounds();
    },

    ///////////////////////
    // TableItem management

    _addItem : function( item, index ) {
      if( this._items[ index ] ) {
        this._items.splice( index, 0, item );
      } else {
        this._items[ index ] = item;
      }
      this._updateScrollHeight();
    },

    _removeItem : function( item ) {
      var itemIndex = this._items.indexOf( item );
      var wasItemVisible = this._isItemVisible( itemIndex );
      this._items.splice( itemIndex, 1 );
      if( itemIndex === this._focusIndex ) {
        this._focusIndex = -1;
      }
      this._deselectItem( itemIndex, false );
      this._updateScrollHeight();
      if( wasItemVisible ) {
        this._updateRows();
      }
    },

    _selectItem : function( itemIndex ) {
      this._selected.push( itemIndex );
      this.updateItem( itemIndex, false );
    },

    _deselectItem : function( itemIndex, update ) {
      // remove itemIndex from array of selected itemIndices
      this._selected.splice( this._selected.indexOf( itemIndex ), 1 );
      // update item if requested
      if( update ) {
        this.updateItem( itemIndex, true );
      }
    },

    _isItemSelected : function( itemIndex ) {
      return this._selected.indexOf( itemIndex ) !== -1;
    },
    
    _clearSelection : function() {
      while( this._selected.length > 0 ) {
        this._deselectItem( this._selected[ 0 ], true );
      }
    },
    
    _resetSelectionStart : function() {
      this._selectionStart = -1;
    },

    _isItemVisible : function( itemIndex ) {
      return    itemIndex >= this._topIndex 
             && itemIndex < this._topIndex + this._rows.length;
    },
    
    _isItemFullyVisible : function( itemIndex ) {
      return    itemIndex >= this._topIndex 
             && itemIndex < this._topIndex + this._getFullyVisibleRowCount();
    },
    
    _makeItemFullyVisible : function( itemIndex ) {
      var rowIndex = this._getRowIndexFromItemIndex( itemIndex );
      var row = rowIndex === -1 ? null : this._rows[ rowIndex ];
      if(    row !== null 
          && row.getTop() + row.getHeight() > this._clientArea.getHeight() ) 
      {
        this._internalSetTopIndex( this._topIndex + 1, true );
      }
    },

    updateItem : function( itemIndex, contentChanged ) {
      var rowIndex = this._getRowIndexFromItemIndex( itemIndex );
      if( rowIndex !== -1 ) {
        if( contentChanged ) {
          this._updateRow( rowIndex, itemIndex ); // implicitly calls _updateRowState
        } else {
          this._updateRowState( rowIndex, itemIndex );
        }
      }
    },

    _getRowIndexFromItemIndex : function( itemIndex ) {
      var result = -1;
      if(    itemIndex >= this._topIndex 
          && itemIndex < this._topIndex + this._rows.length ) 
      {
        result = itemIndex - this._topIndex;
      }
      return result;
    },

    _getItemIndexFromRowIndex : function( rowIndex ) {
    	var result = this._topIndex + rowIndex;
    	if( result < 0 || result > this._itemCount - 1 ) {
    		result = -1;
    	}
    	return result
    },

    /////////////////////////
    // TableColumn management
    
    _addColumn : function( column ) {
      column.setHeight( this._columnArea.getHeight() );
      this._hookColumnMove( column );
      column.addEventListener( "changeWidth", this._onColumnChangeSize, this );
      this._columnArea.add( column );
      this._updateScrollWidth();
    },
    
    _hookColumnMove : function( column ) {
      column.addEventListener( "changeLeft", this._onColumnChangeSize, this );
    },
    
    _unhookColumnMove : function( column ) {
      column.removeEventListener( "changeLeft", this._onColumnChangeSize, this );
    },

    _removeColumn : function( column ) {
      this._unhookColumnMove( column );
      column.removeEventListener( "changeWidth", this._onColumnChangeSize, this );
      this._updateScrollWidth();
      this._updateRows();
    },

    _onColumnChangeSize : function( evt ) {
      this._updateScrollWidth();
    },

    ///////////////////////////////////////////
    // UI Update upon scroll, size changes, etc
    
    _updateScrollHeight : function() {
      var height = this._itemHeight + this._itemCount * this._itemHeight;
      // Without the check, it may cause an error in FF when unloading doc 
      if( !this._vertScrollBar.getDisposed() ) {
        this._vertScrollBar.setMaximum( height );
      }
    },

    _updateScrollWidth : function() {
      var width;
      if( this.getColumnCount() === 0 ) {
        width = this.getDefaultColumnWidth();
      } else {
        width = this.getColumnsWidth();
      }
      this._horzScrollBar.setMaximum( width );
    },

    _updateClientAreaSize : function() {
      var top = 0;
      if( this._columnArea.getVisibility() ) {
        top = this._columnArea.getHeight();
      }
      var clientHeight = this.getHeight() - top - this._horzScrollBar.getHeight() - ( 2 * this._borderWidth );
      var clientWidth = this.getWidth() - this._vertScrollBar.getWidth() - ( 2 * this._borderWidth );
      //
      this._columnArea.setWidth( clientWidth );
      // vertical scrollBar
      this._vertScrollBar.setLeft( this.getWidth() - this._vertScrollBar.getWidth() - ( 2 * this._borderWidth ) );
      this._vertScrollBar.setTop( top );
      this._vertScrollBar.setHeight( clientHeight );
      // horizontal scrollBar
      this._horzScrollBar.setLeft( 0 );
      this._horzScrollBar.setTop( this.getHeight() - this._horzScrollBar.getHeight() - ( 2 * this._borderWidth ) );
      this._horzScrollBar.setWidth( clientWidth );
      // client area
      this._clientArea.setTop( top );
      this._clientArea.setHeight( clientHeight );
      this._clientArea.setWidth( clientWidth );
      // Adjust number of rows and update rows if necessary
      if( this._updateRowCount() ) {
        this._updateRows();
      } else {
        this._updateRowBounds();
        this._updateRowTop();
      }
    },
    
    _updateRowCount : function() {
      var result = false;
      if( this._clientArea.isCreated() ) {
        var newRowCount = 0;
        // TODO [rh] this._clientArea.getHeight() might be negavive
        //      This happens when a Table is placed on a unselected CTabItem 
        //      and then the tab item gets selected and thus the table becomes 
        //      visible
        var clientAreaHeight = this._clientArea.getHeight();
        if( this._itemHeight !== 0 && clientAreaHeight > 0 ) {
          newRowCount = Math.ceil( clientAreaHeight / this._itemHeight );
        }
        if( newRowCount != this._rows.length ) {
          // Remove trailing rows if rowCount was decreased
          while( this._rows.length > newRowCount ) {
            if( this._checkBoxes != null ) {
              var checkBox = this._checkBoxes.shift();
              checkBox.removeEventListener( "changeChecked", this._onCheckBoxClick, this );
              checkBox.setParent( null );
              checkBox.dispose();
            }
            var row = this._rows.shift();
            this._unhookRowEventListener( row );
            row.setParent( null );
            row.dispose();
          }
          // Append rows if rowCount was increased
          if( this._rows.length < newRowCount ) {
            while( this._rows.length < newRowCount ) {
              if( this._checkBoxes != null ) {
                var checkBox = new qx.ui.basic.Image();
                checkBox.addEventListener( "click", this._onCheckBoxClick, this );
                checkBox.setAppearance( "table-check-box" );
                this._clientArea.add( checkBox );
                this._checkBoxes.push( checkBox );
              }
              var newRow = new org.eclipse.swt.widgets.TableRow();
              this._hookRowEventListener( newRow );
              newRow.setLinesVisible( this._linesVisible );
              this._clientArea.add( newRow );
              this._rows.push( newRow );
            }
          }
          // Re-calculate the position and size for each row
          this._updateRowBounds();
          this._updateRowTop();
          result = true;
        }
      }
      return result;
    },
    
    _hookRowEventListener : function( row ) {
      row.addEventListener( "click", this._onRowClick, this );
      row.addEventListener( "dblclick", this._onRowDblClick, this );
      row.addEventListener( "contextmenu", this._onRowContextMenu, this );
    },
    
    _unhookRowEventListener : function( row ) {
      row.removeEventListener( "click", this._onRowClick, this );
      row.removeEventListener( "dblclick", this._onRowDblClick, this );
      row.removeEventListener( "contextmenu", this._onRowContextMenu, this );
    },
    
    _updateRowTop : function() {
      var checkBoxOffset
        = this._itemHeight 
        / 2 
        - org.eclipse.swt.widgets.Table.CHECK_IMAGE_HEIGHT 
        / 2;
      var top = 0;
      for( var i = 0; i < this._rows.length; i++ ) {
        if( this._checkBoxes !== null ) {
          this._checkBoxes[ i ].setTop( top + checkBoxOffset );
        }
        this._rows[ i ].setTop( top );
        top += this._itemHeight;
      }
    },
    
    _updateRowBounds : function() {
      var left = 0 - this._horzScrollBar.getValue();
      // TODO [rh] make themeable
      var checkImageWidth = 0;
      var checkImageHeight = 0;
      var checkBoxWidth = 0;
      if( this._checkBoxes !== null ) {
        // TODO [rh] move to theme, needs to be in sync with TableItem#CHECK_WIDTH
        checkBoxWidth = org.eclipse.swt.widgets.Table.CHECK_WIDTH;
        checkImageHeight = org.eclipse.swt.widgets.Table.CHECK_IMAGE_HEIGHT;
      }
      var width = this.getColumnsWidth() - checkBoxWidth;
      if( this._clientArea.getWidth() > width ) {
        width = this._clientArea.getWidth();
      }
      for( var i = 0; i < this._rows.length; i++ ) {
        if( this._checkBoxes !== null ) {
          var checkBox = this._checkBoxes[ i ];
          checkBox.setLeft( left );
          checkBox.setWidth( checkBoxWidth );
          checkBox.setHeight( checkImageHeight );
        }
        var row = this._rows[ i ];
        row.setLeft( left + checkBoxWidth );
        row.setWidth( width );
        row.setHeight( this._itemHeight );
      }
    },
    
    _scrollRowsVertical : function( delta ) {
      if( Math.abs( delta ) > this._rows.length ) {
        this._updateRows();
      } else {
        // reorder array
        var newRows = new Array();
        var newCheckBoxes = this._checkBoxes !== null ? new Array() : null; 
        var length = this._rows.length;
        for( var i = 0; i < length; i++ ) {
          var sourceIndex = ( length + i + delta )  % length;
      	  newRows.push( this._rows[ sourceIndex ] );
          if( this._checkBoxes !== null ) {
            newCheckBoxes.push( this._checkBoxes[ sourceIndex ] );
          }
        }
        this._rows = newRows;
        this._checkBoxes = newCheckBoxes;
        // move rows
        this._updateRowTop();
        // replace items in stale row
        for( var i = 0; i < length; i++ ) {
          var newItemIndex = this._getItemIndexFromRowIndex( i );
          var currentItemIndex = this._rows[ i ].getItemIndex();
          if( currentItemIndex !== newItemIndex ) {
            this._updateRow( i, newItemIndex );
          }
        }
      }
    },
    
    _updateRows : function() {
      for( var i = 0; i < this._rows.length; i++ ) {
        this._updateRow( i, this._getItemIndexFromRowIndex( i ) );
      }
    },
    
    _updateRow : function( rowIndex, itemIndex ) {
      var row = this._rows[ rowIndex ];
      var item = this._items[ itemIndex ];
      if( itemIndex === -1 ) {
        row.setHtml( this._emptyItem._getMarkup() );
        row.setItemIndex( -1 );
      } else {
	      if( item === undefined || ( item !== null && !item.getCached() ) ) {
	        this._resolveItem( this._topIndex + rowIndex );
	        row.setHtml( this._virtualItem._getMarkup() );
          row.setItemIndex( -1 );
	      } else {
	        row.setHtml( item._getMarkup() );
          row.setItemIndex( itemIndex );
	      }
      }
      this._updateRowState( rowIndex, itemIndex );
    },
    
    _updateRowState : function( rowIndex, itemIndex ) {
    	var row = this._rows[ rowIndex ];
    	if( itemIndex === -1 ) {
        row.removeState( "selected" );
        row.removeState( "itemFocused" );
        if( this._checkBoxes !== null ) {
        	this._checkBoxes[ rowIndex ].setVisibility( false );
        }
    	} else {
	      if( this._isItemSelected( itemIndex ) ) {
	        row.addState( "selected" );
	      } else {
	        row.removeState( "selected" );
	      }
	      if( this._focusIndex === itemIndex ) {
	        row.addState( "itemFocused" );
	      } else {
	        row.removeState( "itemFocused" );
	      }
        if( this._checkBoxes !== null ) {
        	var item = this._items[ itemIndex ];
        	var checkBox = this._checkBoxes[ rowIndex ];
		      if( item !== null && item !== undefined ) {
		        if( item.getChecked() ) {
		          checkBox.addState( "checked" );
		        } else {
		          checkBox.removeState( "checked" );
		        }
		        if( item.getGrayed() ) {
		          checkBox.addState( "grayed" );
		        } else {
		          checkBox.removeState( "grayed" );
		        }
		        checkBox.setVisibility( true );
          } else {
		        checkBox.setVisibility( false );
          }
        }
    	}
    },
    
    _resolveItem : function( itemIndex ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        if( this._unresolvedItems === null ) {
          this._unresolvedItems = new Array();
          qx.client.Timer.once( this._sendResolveItemsRequest, this, 30 );
        } 
        this._unresolvedItems.push( itemIndex );
      }
    },
    
    _sendResolveItemsRequest : function( evt ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      var indices = this._unresolvedItems.join( "," );
      req.addParameter( "org.eclipse.swt.events.setData.index", indices );
      req.addEvent( "org.eclipse.swt.events.setData", id );
      req.send();
      this._unresolvedItems = null;
    },

    //////////////////////////////////////////////////////////////
    // Show and hide the resize line used by column while resizing

    _showResizeLine : function( x ) {
      if( this._resizeLine === null ) {
        this._resizeLine = new qx.ui.basic.Terminator();
        this._resizeLine.setAppearance( "table-column-resizer" );
        this.add( this._resizeLine );
        qx.ui.core.Widget.flushGlobalQueues();
      }
      var top = this._clientArea.getTop();
      this._resizeLine._renderRuntimeTop( top );
      var left = x - 2 - this._horzScrollBar.getValue();
      this._resizeLine._renderRuntimeLeft( left );
      var height = this._clientArea.getHeight();
      this._resizeLine._renderRuntimeHeight( height );
      this._resizeLine.removeStyleProperty( "visibility" );
    },

    _hideResizeLine : function() {
      this._resizeLine.setStyleProperty( "visibility", "hidden" );
    },
    
    ////////////////////////////////////////////////////////////
    // Event handling methods - added and removed by server-side

    onItemSelected : function( evt ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( evt.getTarget() );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
      req.addParameter( "org.eclipse.swt.events.widgetSelected.index",
                        evt.getData() );
      req.send();
    },

    onItemDefaultSelected : function( evt ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( evt.getTarget() );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.widgetDefaultSelected", id );
      req.addParameter( "org.eclipse.swt.events.widgetSelected.index",
                        evt.getData() );
      req.send();
    },

    onItemChecked : function( evt ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( evt.getTarget() );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
      req.addParameter( "org.eclipse.swt.events.widgetSelected.index",
                        evt.getData() );
      req.addParameter( "org.eclipse.swt.events.widgetSelected.detail", 
                        "check" );
      req.send();
    },

    _onSendRequest : function( evt ) {
      if( this._topIndexChanged ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( id + ".topIndex", this._topIndex );
      }
    }
  }
});
