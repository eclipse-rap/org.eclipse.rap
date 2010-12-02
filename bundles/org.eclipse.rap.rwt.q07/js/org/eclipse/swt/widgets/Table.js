/*******************************************************************************
 * Copyright (c) 2007, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
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
    // Should the selected item be hightlighted?
    this._alwaysHideSelection = false;
    this._hideSelection = qx.lang.String.contains( style, "hideSelection" );
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
    // indicates that the horizontal scoll bar was changed
    this._leftOffsetChanged = false;
    // indicates that there are selection listener attached to the scrollbars on
    // the server side
    this._hasScrollBarsSelectionListener = false;
    // indicates that the next request can be sent
    this._readyToSendChanges = true;
    this._resolveItemsFor = null;
    // Internally used fields to manage visible rows and scrolling
    this._itemHeight = 0;
    this._rows = new Array();
    this._items = new Array();
    this._gridLines = new Array();
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
    this._itemLeft = new Array();
    this._itemWidth = new Array();
    this._itemImageLeft = new Array();
    this._itemImageWidth = new Array();
    this._itemTextLeft = new Array();
    this._itemTextWidth = new Array();
    //
    // Construct a column area where columns can be scrolled in
    this._columnArea = new qx.ui.layout.CanvasLayout();
    this._columnArea.setTop( 0 );
    this._columnArea.setLeft( 0 );
    this._columnArea.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    // [if] The zIndex adjustment is needed, because of the cell editors.
    // Whitout it, the cell editor control is positioned on the top of columns
    // headers area when scrolling
    this._columnArea.setZIndex( 20000 );
    // Create a dummy column header that is placed after the last column
    // The dummy column is always the first child in _columnArea
    var dummyColumn = new qx.ui.basic.Atom();
    // set invisible label (otherwise border is not rendered in IE)
    dummyColumn.setLabel( "&nbsp;" );
    dummyColumn.getLabelObject().setMode( qx.constant.Style.LABEL_MODE_HTML );
    dummyColumn.setAppearance( "table-column" );
    dummyColumn.addState( "dummy" );
    this._columnArea.add( dummyColumn );
    this.add( this._columnArea );
    // Construct client area in which the table items will live
    this._clientArea = new qx.ui.layout.CanvasLayout();
    this._clientArea.setAppearance( "table-client-area" );
    this._clientArea.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    this._clientArea.setTop( 20 );
    this._clientArea.setLeft( 0 );
    this._clientArea.addEventListener( "mousewheel", this._onClientAreaMouseWheel, this );
    this._clientArea.addEventListener( "appear", this._onClientAppear, this );
    // Create horizontal scrollBar
    this._horzScrollBar = new org.eclipse.rwt.widgets.ScrollBar( true );
    this._horzScrollBar.setZIndex( 1e8 );
    this._horzScrollBar.setMergeEvents( true );
    this.add( this._horzScrollBar );
    this._horzScrollBar.setHeight( this._horzScrollBar.getPreferredBoxHeight() );
    this._horzScrollBar.addEventListener( "changeValue", this._onHorzScrollBarChangeValue, this );
    // Create vertical scrollBar
    this._vertScrollBar = new org.eclipse.rwt.widgets.ScrollBar( false );
    this._vertScrollBar.setZIndex( 1e8 );
    this._vertScrollBar.setMergeEvents( false );
    this.add( this._vertScrollBar );
    this._vertScrollBar.setWidth( this._vertScrollBar.getPreferredBoxWidth() );
    this._vertScrollBar.addEventListener( "changeValue", this._onVertScrollBarChangeValue, this );
    // Listen to size changes to adjust client area size
    this.addEventListener( "changeWidth", this._onChangeSize, this );
    this.addEventListener( "changeHeight", this._onChangeSize, this );
    this.addEventListener( "changeEnabled", this._onChangeEnabled, this );
    // Listen to table focus and adjust selected rows state
    this.addEventListener( "focus", this._onFocusIn, this );
    this.addEventListener( "blur", this._onFocusOut, this );
    // Keyboard navigation
    this._keyboardSelecionChanged = false;
    this.addEventListener( "keydown", this._onKeyDown, this );
    this.addEventListener( "keypress", this._onKeyPress, this );
    this.addEventListener( "keyup", this._onKeyUp, this );
    // Listen to send event of request to report current state
    var req = org.eclipse.swt.Request.getInstance();
    req.addEventListener( "send", this._onSendRequest, this );
    //
    var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
    widgetManager.add( this._clientArea, id + "_clientArea", false );
    this.add( this._clientArea );
    // Cell tooltip
    this._cellToolTip = null;
    if( qx.lang.String.contains( style, "enableCellToolTip" ) ) {
      this._cellToolTip = new org.eclipse.swt.widgets.TableCellToolTip();
      this._cellToolTip.setTableId( id );
      this._clientArea.addEventListener( "mousemove", this._onClientAreaMouseMove, this );
      this._clientArea.setToolTip( this._cellToolTip );
    }
    // Disable scrolling (see bug 279460)
    qx.ui.core.Widget.disableScrolling( this );
    // Fix for bug Bug 297202
    this._clickDelayed = false;
    this.addEventListener( "dragstart", this._onDragStart, this );    
  },

  destruct : function() {
    var req = org.eclipse.swt.Request.getInstance();
    req.removeEventListener( "send", this._onSendRequest, this );
    this.removeEventListener( "changeWidth", this._onChangeSize, this );
    this.removeEventListener( "changeHeight", this._onChangeSize, this );
    this.removeEventListener( "changeEnabled", this._onChangeEnabled, this );
    this.removeEventListener( "focus", this._onFocusIn, this );
    this.removeEventListener( "blur", this._onFocusOut, this );
    this.removeEventListener( "keydown", this._onKeyDown, this );
    this.removeEventListener( "keypress", this._onKeyPress, this );
    this.removeEventListener( "keyup", this._onKeyUp, this );
    this.removeEventListener( "dragstart", this._onDragStart, this );    
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
    for( var i = 0; i < this._gridLines.length; i++ ) {
      this._gridLines[ i ].removeEventListener( "mousedown", this._onRowMouseDown, this );
      this._gridLines[ i ].removeEventListener( "mouseup", this._onRowMouseUp, this );
      this._gridLines[ i ].dispose();
      this._gridLines[ i ] = null;
    }
    if( this._clientArea ) {
      this._clientArea.removeEventListener( "mousewheel", this._onClientAreaMouseWheel, this );
      this._clientArea.removeEventListener( "appear", this._onClientAppear, this );
      if( this._cellToolTip ) {
        this._clientArea.removeEventListener( "mousemove", this._onClientAreaMouseMove, this );
      }
      org.eclipse.swt.WidgetManager.getInstance().remove( this._clientArea );
      this._clientArea.dispose();
      this._clientArea = null;
    }
    if( this._cellToolTip ) {
      this._cellToolTip.dispose();
      this._cellToolTip = null;
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
    this._itemLeft = null;
    this._itemWidth = null;
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
  
  properties : {
    
    checkWidth : {
      check : "Integer",
      init : 21,
      themeable : true
    },
    
    checkHeight : {
      check : "Integer",
      init : 13,
      themeable : true
    }
    
  },

  statics : {

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

    // This is needed to sort out events on nested widgets (cell editors)
    // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=262155
    _isRelevantEvent : function( evt ) {
      var target = evt.getTarget();
      var dummyColumn = this._columnArea.getChildren()[ 0 ];
      return    target === this
             || target === dummyColumn
             || target instanceof org.eclipse.swt.widgets.TableRow
             || target instanceof org.eclipse.swt.widgets.TableColumn;
    },

    setCursor : function( value ) {
      this._columnArea.setCursor( value );
      this._clientArea.setCursor( value );
      var columns = this._columnArea.getChildren();
      for( var i = 0; i < columns.length; i++ ) {
        columns[ i ].setCursor( value );
      }
      for( var i = 0; i < this._rows.length; i++ ) {
        this._rows[ i ].setCursor( value );
      }
      if( this._checkBoxes !== null ) {
        for( var i = 0; i < this._checkBoxes.length; i++ ) {
          this._checkBoxes[ i ].setCursor( value );
        }
      }
    },

    resetCursor : function() {
      this._columnArea.resetCursor();
      this._clientArea.resetCursor();
      var columns = this._columnArea.getChildren();
      for( var i = 0; i < columns.length; i++ ) {
        columns[ i ].resetCursor();
      }
      for( var i = 0; i < this._rows.length; i++ ) {
        this._rows[ i ].resetCursor();
      }
      if( this._checkBoxes !== null ) {
        for( var i = 0; i < this._checkBoxes.length; i++ ) {
          this._checkBoxes[ i ].resetCursor();
        }
      }
    },

    setTextColor : function( color ) {
      this._clientArea.setTextColor( color );
    },

    resetTextColor : function() {
      this._clientArea.resetTextColor();
    },

    setBackgroundImage : function( image ) {
      this._clientArea.setBackgroundImage( image );
    },

    resetBackgroundImage : function() {
      this._clientArea.resetBackgroundImage();
    },

    setBackgroundColor : function( color ) {
      this._clientArea.setBackgroundColor( color );
    },

    resetBackgroundColor : function() {
      this._clientArea.resetBackgroundColor();
    },

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
      this._updateClientAreaSize();
      this._updateGridLines();
    },

    setItemHeight : function( value ) {
      this._itemHeight = value;
      this._updateScrollHeight();
      if( this._updateRowCount() ) {
        this._updateRows();
      }
      this._vertScrollBar.setIncrement( value );
    },

    getItemHeight : function() {
      return this._itemHeight;
    },

    setAlwaysHideSelection : function( value ) {
      this._alwaysHideSelection = value;
    },
    
    shouldHideSelection : function() {
       return    this._alwaysHideSelection
              || ( this._hideSelection && !this.getFocused() );
    },

    setItemMetrics : function( columnIndex, left, width, imageLeft, imageWidth, textLeft, textWidth ) {
      this._itemLeft[ columnIndex ] = left;
      this._itemWidth[ columnIndex ] = width;
      this._itemImageLeft[ columnIndex ] = imageLeft;
      this._itemImageWidth[ columnIndex ] = imageWidth;
      this._itemTextLeft[ columnIndex ] = textLeft;
      this._itemTextWidth[ columnIndex ] = textWidth;
    },

    setScrollBarsVisibile : function( horzVisible, vertVisible ) {
      if( !horzVisible ) {
        this._horzScrollBar.setValue( 0 );
      }
      this._horzScrollBar.setVisibility( horzVisible );
      if( !vertVisible ) {
        this._vertScrollBar.setValue( 0 );
      }
      this._vertScrollBar.setVisibility( vertVisible );
      this._updateClientAreaSize();
    },

    updateRows : function() {
      this._updateRows();
    },

    getItemLeft : function( columnIndex ) {
      return this._itemLeft[ columnIndex ];
    },

    getItemWidth : function( columnIndex ) {
      return this._itemWidth[ columnIndex ];
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
    
    /** Only called by server-side */
    setLeftOffset : function( value ) {
      this._horzScrollBar.setValue( value );
      this._leftOffsetChanged = false;
    },

    setBorderWidth : function( value ) {
      this._borderWidth = value;
    },

    getColumn : function( index ) {
      return this._columnArea.getChildren()[ index + 1 ];
    },

    getColumnCount : function() {
      return this._columnArea.getChildrenLength() - 1;
    },

    getColumns : function() {
      return this._columnArea.getChildren().slice( 1 );
    },

    getColumnsWidth : function() {
      var result = 0;
      var columns = this._columnArea.getChildren();
      for( var i = 1; i < columns.length; i++ ) {
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
      if( value ) {
        this.addState( "linesvisible" );
      } else {
        this.removeState( "linesvisible" );
      }
      for( var i = 0; i < this._rows.length; i++ ) {
        this._rows[ i ].setLinesVisible( value );
      }
      this._updateGridLines();
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
        // This function is called from server-side and from within Table.js
        // org.eclipse.swt.EventUtil.getSuspended() is used to distinguish the caller
        if( org.eclipse.swt.EventUtil.getSuspended() ) {
          this._selectionStart = -1;
        } else {
          var req = org.eclipse.swt.Request.getInstance();
          var id = org.eclipse.swt.WidgetManager.getInstance().findIdByWidget( this );
          req.addParameter( id + ".focusIndex", value );
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
    
    _onCheckBoxOver : function( evt ) {
      var rowIndex = this._checkBoxes.indexOf( evt.getTarget() );
      var checkBox = this._checkBoxes[ rowIndex ];
      checkBox.addState( "over" );
      this._rows[ rowIndex ].addState( "over" );
    },
    
    _onCheckBoxOut : function( evt ) {
      var rowIndex = this._checkBoxes.indexOf( evt.getTarget() );
      var checkBox = this._checkBoxes[ rowIndex ];
      checkBox.removeState( "over" );
      this._rows[ rowIndex ].removeState( "over" );
    },
    
    // Note: [rst] This function is wired with the mousedown event. Using the
    //             click event causes problems because click is issued on the
    //             release of the mouse button and it is not fired at all if the
    //             mouse has been moved to another element between down and up.
    //             See https://bugs.eclipse.org/bugs/show_bug.cgi?id=257338
    _onRowMouseDown : function( evt ) {
      var row = this._getRowFromEvent( evt );
      if( row != null ) {        
        var itemIndex = this._topIndex + this._rows.indexOf( row );
        if( this._isDragSource() && this._isItemSelected( itemIndex ) ) {
          if( this._isValidMouseEvent( row, itemIndex ) ) {
            this.setFocusIndex( itemIndex );
            this._clickDelayed = true;
          }        
        } else {
          this._rowClicked( evt, row );
        }
      }
    },
    
    _onRowMouseUp : function( evt ) {
      if( this._clickDelayed ) {
        this._clickDelayed = false;
        var row = this._getRowFromEvent( evt );
        if( row != null ) {
          this._rowClicked( evt, row );
        }
      }
    },
    
    _getRowFromEvent : function( evt ) {
      var target = evt.getTarget();
      var result = null;
      if( target instanceof org.eclipse.swt.widgets.TableRow ) {
        result = target;
      } else {
        result = this._getRowAtPoint( evt.getPageX(), evt.getPageY() );        
      }
      return result;
    },

    _isDragSource : function() {
      return org.eclipse.rwt.DNDSupport.getInstance().isDragSource( this );
    },
    
    _onDragStart : function( event ) {
      this._clickDelayed = false;
    },
    
    _isValidMouseEvent : function( targetRow, itemIndex ) {
      var result =    itemIndex >= 0
                   && itemIndex < this._itemCount
                   && this._items[ itemIndex ]
                   && this._suspendClicksOnRow != targetRow;
      return result;      
    },
    
    // Note: [tb] An exception is made if the table is a dragSource (bug 297202)
    _rowClicked : function( evt, row ) {
      var itemIndex = this._topIndex + this._rows.indexOf( row );
      if( this._isValidMouseEvent( row, itemIndex ) ) {
        this._suspendClicksOnRow = row;
        qx.client.Timer.once( this._resumeClicks,
                              this,
                              org.eclipse.swt.EventUtil.DOUBLE_CLICK_TIME );
        if( this._multiSelect ) {
          this._multiSelectRowClicked( evt, itemIndex );
        } else {
          this._setSingleSelection( itemIndex );
        }
        this.setFocusIndex( itemIndex );
        this._makeItemFullyVisible( itemIndex );
        this._updateSelectionParam();
        this.createDispatchDataEvent( "itemselected", itemIndex );
      }
    },

    _multiSelectRowClicked : function( evt, itemIndex ) {
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
            = this._selectionStart !== -1
            ? this._selectionStart
            : this._focusIndex;
          if( selectionStart !== -1 ) {
            var start = Math.min( selectionStart, itemIndex );
            var end = Math.max( selectionStart, itemIndex );
            for( var i = start; i <= end; i++ ) {
              this._selectItem( i );
            }
          } else {
            this._selectItem( itemIndex );
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
        var target = evt.getTarget();
        var contextMenu = this.getContextMenu();
        if( contextMenu !== null ) {
          contextMenu.setLocation( evt.getPageX(), evt.getPageY() );
          contextMenu.setOpener( this );
          contextMenu.show();
        }
      }
    },
    
    _onRowChangeOverState : function( evt ) {
      var row = evt.getTarget();
      if( row.hasState( "over" ) ) {
        for( var i = 0; i < this._rows.length; i++ ) {
          if( this._rows[ i ] !== row ) {
            this._rows[ i ].removeState( "over" );
          }
        }
      }
      var itemIndex = row.getItemIndex();
      if( itemIndex != -1 ) {
        this._renderItem( row, this._items[ itemIndex ] );
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
      if( this._isRelevantEvent( evt ) ) {
        evt.preventDefault();
        evt.stopPropagation();
        var change = evt.getWheelDelta() * this._itemHeight * 2;
        this._vertScrollBar.setValue( this._vertScrollBar.getValue() - change );
      }
    },

    _onChangeSize : function( evt ) {
      this._updateClientAreaSize();
    },

    _onClientAppear : function( evt ) {
      this._updateRowCount();
      this._updateRows();
      this._updateGridLines();
    },

    ///////////////////////
    // Keyboard navigation

    _onKeyDown : function( evt ) {
      if( this._isRelevantEvent( evt ) ) {
        switch( evt.getKeyIdentifier() ) {
          case "Space":
            this._toggleCheckState( this._focusIndex );
            break;
          case "Enter":
            // in sync with SWT: fire defaultSelection when <Return> is pressed,
            // regardless which modifier-key(s) are held down
            var itemDefaultSelected = -1;
            var topSelectedItem = -1;
            for( var i = 0; i < this._selected.length; i++ ) {
              if( this._focusIndex === this._selected[ i ] ) {
                itemDefaultSelected = this._selected[ i ];
              }
              if( topSelectedItem === -1 ) {
                topSelectedItem = this._selected[ i ];
              } else {
                topSelectedItem = Math.min( topSelectedItem, this._selected[ i ] );
              }
            }
            if( itemDefaultSelected === -1 ) {
              itemDefaultSelected = topSelectedItem;
            }
            this.createDispatchDataEvent( "itemdefaultselected",
                                          itemDefaultSelected );
            break;
        }
      }
    },

    _onKeyPress : function( evt ) {
      var keyIdentifier = evt.getKeyIdentifier();
      if(    this._isRelevantEvent( evt )
          && org.eclipse.swt.widgets.Table._isNoModifierPressed( evt )
          && (    keyIdentifier === "Up"
               || keyIdentifier === "Down"
               || keyIdentifier === "PageUp"
               || keyIdentifier === "PageDown"
               || keyIdentifier === "Home"
               || keyIdentifier === "End" ) )
      {
        evt.preventDefault();
        evt.stopPropagation();
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
              // Sets the topIndex so the selection to be visible at the
              // bottom or on the top according to gotoIndex
              topIndex = gotoIndex >= this._topIndex + this._getFullyVisibleRowCount()
                       ? gotoIndex - this._getFullyVisibleRowCount() + 1
                       : gotoIndex;
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
            // Fix for bug #282425:
            // If gotoIndex points to one of the items of the last page and 
            // PageDown is used, the top index is set so the last page to be 
            // visible
            var lastPageTopIndex
              = this._itemCount - this._getFullyVisibleRowCount();
            if(    gotoIndex > lastPageTopIndex
                && gotoIndex < this._itemCount
                && keyIdentifier === "PageDown" )
            {
              topIndex = lastPageTopIndex;
            }
            this._internalSetTopIndex( topIndex, true );
          }
          this.setFocusIndex( gotoIndex );
          this._keyboardSelecionChanged = true;
        }
      }
      // Prevent Left and Right key events from bubbling
      if(    this._isRelevantEvent( evt )
          && org.eclipse.swt.widgets.Table._isNoModifierPressed( evt )
          && (    keyIdentifier === "Left"
               || keyIdentifier === "Right" ) )
      {
        evt.preventDefault();
        evt.stopPropagation();
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
      if( this._readyToSendChanges && this._hasScrollBarsSelectionListener ) {
        this._readyToSendChanges = false;
        // Send changes
        qx.client.Timer.once( this._sendChanges, this, 500 );
      }
    },

    _onHorzScrollBarChangeValue : function() {
      this._columnArea.setLeft( 0 - this._horzScrollBar.getValue() );
      this._updateRowBounds();
      this._updateGridLines();
      this._leftOffsetChanged = true;
      if( this._readyToSendChanges && this._hasScrollBarsSelectionListener ) {
        this._readyToSendChanges = false;
        // Send changes
        qx.client.Timer.once( this._sendChanges, this, 500 );
      }
    },
    
    setHasScrollBarsSelectionListener : function( value ) {
      this._hasScrollBarsSelectionListener = value;
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
      this._items.splice( itemIndex, 1 );
      if( itemIndex === this._focusIndex ) {
        this._focusIndex = -1;
      }
      // Order is crucial here: first deselect item then adjust indices
      this._deselectItem( itemIndex, false );
      this._adjustSelectedIndices( itemIndex );
      this._updateScrollHeight();
    },

    _selectItem : function( itemIndex ) {
      if( !this._isItemSelected( itemIndex ) ) {
        this._selected.push( itemIndex );
        // Call updateItem with contentChanged = true to override potentially
        // set background colors, see https://bugs.eclipse.org/bugs/237134
        this.updateItem( itemIndex, true );
      }
    },

    _deselectItem : function( itemIndex, update ) {
      // remove itemIndex from array of selected itemIndices
      var indexInSelection = this._selected.indexOf( itemIndex );
      if( indexInSelection != -1 ) {
        this._selected.splice( indexInSelection, 1 );
        // update item if requested
        if( update ) {
          this.updateItem( itemIndex, true );
        }
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

    _adjustSelectedIndices : function( itemIndex ) {
      for( var i = 0; i < this._selected.length; i++ ) {
        var index = this._selected[ i ];
        if( itemIndex < index ) {
          this._selected[ i ] = index - 1;
        }
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
    
    _isItemHovered : function( itemIndex ) {
      var result = false;
      var rowIndex = this._getRowIndexFromItemIndex( itemIndex );
      if( rowIndex >= 0 ) {
        var row = this._rows[ rowIndex ];
        result = row.hasState( "over" ) && row.hasHoverColorsDefined();
      }
      return result;
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
      this._columnArea.remove( column );
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
        if( this._checkBoxes !== null ) {
          width += this.getCheckWidth();
        }
      } else {
        width = this.getColumnsWidth();
      }
      this._horzScrollBar.setMaximum( width );
      this._updateColumnHeader();
      this._updateGridLines();
    },

    _updateColumnHeader : function() {
      var clientWidth = this.getWidth() - 2 * this._borderWidth;
      var dummyColumn = this._columnArea.getChildren()[ 0 ];
      var vertScrollBarWidth = this._vertScrollBar.getVisibility()
                               ? this._vertScrollBar.getWidth()
                               : 0;
      var headerWidth;
      if( this.getColumnCount() === 0 ) {
        var columnsWidth = this.getDefaultColumnWidth();
        if( this._checkBoxes !== null ) {
          columnsWidth += this.getCheckWidth();
        }
        headerWidth = Math.max( columnsWidth + vertScrollBarWidth, clientWidth );
        dummyColumn.setLeft( 0 );
        dummyColumn.setWidth( headerWidth );
      } else {
        var columnsWidth = this.getColumnsWidth();
        headerWidth = Math.max( columnsWidth + vertScrollBarWidth, clientWidth );
        dummyColumn.setLeft( columnsWidth );
        dummyColumn.setWidth( Math.max( headerWidth - columnsWidth, 0 ) );
      }
      this._columnArea.setWidth( headerWidth );
    },

    _updateClientAreaSize : function() {
      var top = 0;
      if( this._columnArea.getVisibility() ) {
        top = this._columnArea.getHeight();
      }
      var horzScrollBarHeight = this._horzScrollBar.getVisibility()
                                ? this._horzScrollBar.getHeight()
                                : 0;
      var vertScrollBarWidth = this._vertScrollBar.getVisibility()
                               ? this._vertScrollBar.getWidth()
                               : 0;
      var clientHeight = this.getHeight() - top - horzScrollBarHeight - ( 2 * this._borderWidth );
      var clientWidth = this.getWidth() - vertScrollBarWidth - ( 2 * this._borderWidth );
      // vertical scrollBar
      this._vertScrollBar.setLeft( this.getWidth() - vertScrollBarWidth - ( 2 * this._borderWidth ) );
      this._vertScrollBar.setTop( top );
      this._vertScrollBar.setHeight( clientHeight );
      // horizontal scrollBar
      this._horzScrollBar.setLeft( 0 );
      this._horzScrollBar.setTop( this.getHeight() - horzScrollBarHeight - ( 2 * this._borderWidth ) );
      this._horzScrollBar.setWidth( clientWidth );
      // client area
      this._clientArea.setTop( top );
      this._clientArea.setHeight( clientHeight );
      this._clientArea.setWidth( clientWidth );
      this._updateColumnHeader();
      this._updateGridLines();
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
              checkBox.removeEventListener( "mouseover", 
                                            this._onCheckBoxOver, 
                                            this );
              checkBox.removeEventListener( "mouseout", 
                                            this._onCheckBoxOut, 
                                            this );
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
              var newRow = new org.eclipse.swt.widgets.TableRow();
              this._hookRowEventListener( newRow );
              newRow.setLinesVisible( this._linesVisible );
              this._clientArea.add( newRow );
              this._rows.push( newRow );
              if( this._checkBoxes != null ) {
                var checkBox = new qx.ui.basic.Image();
                checkBox.addEventListener( "click", this._onCheckBoxClick, this );
                checkBox.addEventListener( "mouseover", 
                                           this._onCheckBoxOver, 
                                           this );
                checkBox.addEventListener( "mouseout", 
                                           this._onCheckBoxOut, 
                                           this );
                checkBox.setAppearance( "table-check-box" );
                this._clientArea.add( checkBox );
                this._checkBoxes.push( checkBox );
              }
            }
          }
          // Re-calculate the position and size for each row
          this._updateRowBounds();
          this._updateRowTop();
          this._updateRowsState();
          result = true;
        }
      }
      return result;
    },

    _hookRowEventListener : function( row ) {
      row.addEventListener( "mousedown", this._onRowMouseDown, this );
      row.addEventListener( "mouseup", this._onRowMouseUp, this );
      row.addEventListener( "dblclick", this._onRowDblClick, this );
      row.addEventListener( "contextmenu", this._onRowContextMenu, this );
      row.addEventListener( "changeOverState", this._onRowChangeOverState, this );
    },

    _unhookRowEventListener : function( row ) {
      row.removeEventListener( "mousedown", this._onRowMouseDown, this );
      row.removeEventListener( "mouseup", this._onRowMouseUp, this );
      row.removeEventListener( "dblclick", this._onRowDblClick, this );
      row.removeEventListener( "contextmenu", this._onRowContextMenu, this );
      row.removeEventListener( "changeOverState", this._onRowChangeOverState, this );
    },

    _updateRowTop : function() {
      var checkBoxOffset = this._itemHeight / 2 - this.getCheckHeight() / 2;
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
      var checkHeight = 0;
      var checkWidth = 0;
      if( this._checkBoxes !== null ) {
        checkWidth = this.getCheckWidth();
        checkHeight = this.getCheckHeight();
      }
      var width;
      if( this.getColumnCount() === 0 ) {
        // see bug 290565 and bug 290879
        width = this.getDefaultColumnWidth() + checkWidth;
      } else {
        width = this.getColumnsWidth();
      }
      if( this._clientArea.getWidth() > width ) {
        width = this._clientArea.getWidth();
      }
      for( var i = 0; i < this._rows.length; i++ ) {
        if( this._checkBoxes !== null ) {
          var checkBox = this._checkBoxes[ i ];
          checkBox.setLeft( left );
          checkBox.setWidth( checkWidth );
          checkBox.setHeight( checkHeight );
        }
        var row = this._rows[ i ];
        row.setLeft( left );
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
      var start = ( new Date() ).getTime();
      for( var i = 0; i < this._rows.length; i++ ) {
        this._updateRow( i, this._getItemIndexFromRowIndex( i ) );
      }
      this._vertScrollBar.autoEnableMerge( ( new Date() ).getTime() - start );
    },

    _updateRow : function( rowIndex, itemIndex ) {
      var row = this._rows[ rowIndex ];
      if( itemIndex >= 0 && itemIndex < this._itemCount ) {
        var item = this._items[ itemIndex ];
        if( item === undefined || ( item !== null && !item.getCached() ) ) {
          this._scheduleResolveItems();
          this._renderItem( row, this._virtualItem );
          row.setItemIndex( -1 );
        } else {
          this._renderItem( row, item );
          row.setItemIndex( itemIndex );
        }
      } else {
        this._renderItem( row, this._emptyItem );
        row.setItemIndex( -1 );
      }
      this._updateRowState( rowIndex, itemIndex );
    },

    _renderItem : function( row, item ) {
      if( row.isCreated() ) {
        item._render( row );
      } else {
        var listener = function() {
          item._render( row );
          row.removeEventListener( "create", listener );
        };
        row.addEventListener( "create", listener );
      }
    },
    
    _updateRowsState : function() {
      for( var i = 0; i < this._rows.length; i++ ) {
        this._updateRowState( i, this._getItemIndexFromRowIndex( i ) );
      }
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
        if( this._isItemSelected( itemIndex ) && !this.shouldHideSelection() ) {
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
      if( this.getFocused() ) {
        row.removeState( "parent_unfocused" );
      } else {
        row.addState( "parent_unfocused" );
      }
    },
    
    _scheduleResolveItems : function() {
      if( this._resolveItemsFor === null ) {
        qx.client.Timer.once( this._sendResolveItemsRequest, this, 500 );
      }
      this._resolveItemsFor = this._topIndex;
    },

    _sendResolveItemsRequest : function( evt ) {
      var scrollDiff = Math.abs( this._topIndex - this._resolveItemsFor );
      this._resolveItemsFor = null;
      if( scrollDiff > this._rows.length - 2 ) {
        this._scheduleResolveItems();         
      } else {
        var unresolved = [];
        for( var i = 0; i < this._rows.length; i++ ) {
          var index = this._getItemIndexFromRowIndex( i );
          if( index >= 0 && index < this._itemCount ) {
            var item = this._items[ index ];
            if( item === undefined || ( item !== null && !item.getCached() ) ) {
              unresolved.push( index );
            }
          }
        }
        if( unresolved.length > 0 ) {
          var indices = unresolved.join( "," );
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var id = widgetManager.findIdByWidget( this );
          var req = org.eclipse.swt.Request.getInstance();
          req.addParameter( "org.eclipse.swt.events.setData.index", indices );
          req.addEvent( "org.eclipse.swt.events.setData", id );
          req.send();
        }
      }
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

    //////////////////////
    // Vertical gridlines

    /*
     * Updates x-position and height of vertical grid lines.
     */
    _updateGridLines : function() {
      if( this.getLinesVisible() ) {
        var columns = this.getColumns();
        this._showGridLines( columns.length );
        var height = this._clientArea.getHeight();
        var offset = this._columnArea.getLeft();
        for( var i = 0; i < columns.length; i++ ) {
          var line = this._gridLines[ i ];
          var left = offset + columns[ i ].getLeft() + columns[ i ].getWidth();
          line.setSpace( left - 1, 2, 0, height );
          line.removeStyleProperty( "visibility" );
        }
      } else {
        this._showGridLines( 0 );
      }
    },

    _showGridLines : function( count ) {
      // create missing gridlines
      for( var i = this._gridLines.length; i < count; i++ ) {
        var line = new qx.ui.basic.Terminator();
        line.setAppearance( "table-gridline-vertical" );
        line.addState( "vertical" );
        line.setZIndex( 1e5 );
        line.addEventListener( "mousedown", this._onRowMouseDown, this );
        line.addEventListener( "mouseup", this._onRowMouseUp, this );
        this._gridLines.push( line );
        this._clientArea.add( line );
      }
      // hide superflous gridlines
      for( var i = count; i < this._gridLines.length; i++ ) {
        var line = this._gridLines[ i ];
        line.setStyleProperty( "visibility", "hidden" );
      }
    },

    _getRowAtPoint : function( pageX, pageY ) {
      var result = null;
      for( var i = 0; result === null && i < this._rows.length; i++ ) {
        var row = this._rows[ i ];
        var element = row.getElement();
        var pageLeft = qx.bom.element.Location.getLeft( element );
        var pageTop = qx.bom.element.Location.getTop( element );
        if(    pageX >= pageLeft
            && pageX < pageLeft + row.getWidth()
            && pageY >= pageTop
            && pageY < pageTop + row.getHeight() )
        {
          result = row;
        }
      }
      return result;
    },

    //////////////////////////////////////////////////////////
    // Focus tracking - may change appearance of selected row

    _onFocusIn : function( evt ) {
      this._updateRowsState();
    },

    _onFocusOut : function( evt ) {
      this._updateRowsState();
    },

    ////////////////////////////////////////////////////////////
    // Event handling methods - added and removed by server-side

    onItemSelected : function( evt ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( evt.getTarget() );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
      org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
      req.addParameter( "org.eclipse.swt.events.widgetSelected.index",
                        evt.getData() );
      req.send();
    },

    onItemDefaultSelected : function( evt ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( evt.getTarget() );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.widgetDefaultSelected", id );
      org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
      req.addParameter( "org.eclipse.swt.events.widgetSelected.index",
                        evt.getData() );
      req.send();
    },

    onItemChecked : function( evt ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( evt.getTarget() );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
      org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
      req.addParameter( "org.eclipse.swt.events.widgetSelected.index",
                        evt.getData() );
      req.addParameter( "org.eclipse.swt.events.widgetSelected.detail",
                        "check" );
      req.send();
    },

    _onSendRequest : function( evt ) {
      if( this._topIndexChanged || this._leftOffsetChanged ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        if( this._topIndexChanged ) {
          req.addParameter( id + ".topIndex", this._topIndex );
          this._topIndexChanged = false;
        }
        if( this._leftOffsetChanged ) {
          req.addParameter( id + ".leftOffset", this._horzScrollBar.getValue() );
          this._leftOffsetChanged = false;
        }
      }
    },
    
    _sendChanges : function() {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        var req = org.eclipse.swt.Request.getInstance();
        req.send();
      }
      this._readyToSendChanges = true;
    },

    ////////////////////////
    // Cell tooltip handling

    _onClientAreaMouseMove : function( evt ) {
      if( this._cellToolTip != null ) {
        var pageX = evt.getPageX();
        var pageY = evt.getPageY();
        var row = this._getRowAtPoint( pageX, pageY );
        var rowIndex = this._rows.indexOf( row );
        var itemIndex = this._getItemIndexFromRowIndex( rowIndex );
        var columnIndex = -1;
        var columns = this.getColumns();
        for( var i = 0; columnIndex == -1 && i < columns.length; i++ ) {
          var element = this._clientArea.getElement();
          var pageLeft = qx.bom.element.Location.getLeft( element )
                       + this._itemLeft[ i ];
          if(    pageX >= pageLeft
              && pageX < pageLeft + this._itemWidth[ i ] )
          {
            columnIndex = i;
          }
        }
        this._cellToolTip.setCell( itemIndex, columnIndex );
      }
    },

    /** Only called by server-side */
    setCellToolTipText : function( text ) {
      if( this._cellToolTip != null ) {
        this._cellToolTip.setText( text );
      }
    }

  }
});
