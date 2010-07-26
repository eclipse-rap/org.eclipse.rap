/*******************************************************************************
 * Copyright (c) 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.widgets.Tree", {

  extend : qx.ui.layout.CanvasLayout,

  construct : function() {
    this.base( arguments );
    this._rootItem = new org.eclipse.rwt.widgets.TreeItem();
    this._rootItem.setExpanded( true );
    // Style-Flags:
    this._hasCheckBoxes = false;
    this._hasFullSelection = false;
    this._isVirtual = false;
    this._hasMultiSelection = false;
    // Internal State:
    this._topItem = null;
    this._leadItem = null;
    this._topItemIndex = 0;
    this._selection = [];
    this._focusItem = null;
    this._hoverItem = null;
    this._renderQueue = {};
    this._resizeLine = null;
    this._linesVisible = false;
    this._selectionTimestamp = null;
    // Metrics:
    this._headerHeight = 0;
    this._itemHeight = 16;
    this._checkBoxLeft = null;
    this._checkBoxWidth = null;
    this._hasSelectionListeners = false;
    this._selectionPadding = null;
    this._alignment = [];
    this._itemLeft = [];
    this._itemWidth = [];
    this._itemImageLeft = [];
    this._itemImageWidth = [];
    this._itemTextLeft = [];
    this._itemTextWidth = [];
    this._indentionWidth = 16;
    this._columnCount = 0;
    this._treeColumn = 0;
    // Timer & Border
    this._mergeEventsTimer = new qx.client.Timer( 50 );
    this._sendRequestTimer = null;
    this._vertGridBorder = null;
    this._horzGridBorder = null;
    // Subwidgets
    this._clientArea = new qx.ui.layout.VerticalBoxLayout();
    this._columnArea = new qx.ui.layout.CanvasLayout();
    this._dummyColumn = new qx.ui.basic.Atom();
    this._horzScrollBar = new qx.ui.basic.ScrollBar( true );
    this._vertScrollBar = new qx.ui.basic.ScrollBar( false );
    this._rows = this._clientArea.getChildren();
    this._vertGridLines = [];
    this.add( this._columnArea );
    this.add( this._clientArea );
    this.add( this._horzScrollBar );
    this.add( this._vertScrollBar );
    // Configure:
    this.setCursor( "default" );
    this.setOverflow( "hidden" );
    this.setAppearance( "tree" );
    this._configureAreas();
    this._configureScrollBars();
    this._registerListeners();
  },
  
  destruct : function() {
    this._rootItem.removeEventListener( "update", this._onItemUpdate, this );
    this._rootItem.dispose();
    this._rootItem = null;
    if( this._sendRequestTimer != null ) {
      this._sendRequestTimer.dispose();
      this._sendRequestTimer = null;
    }
    this._mergeEventsTimer.dispose();
    this._mergeEventsTimer = null;
    this._dummyColumn = null;
    this._clientArea = null;
    this._columnArea = null;
    this._horzScrollBar = null;
    this._vertScrollBar = null;
    this._rows = null;
    this._topItem = null;
    this._leadItem = null;
    this._focusItem = null;
    this._resizeLine = null;
  },

  members : {

    /////////////////////
    // Contructor helpers
    
    _registerListeners : function() {
      this._rootItem.addEventListener( "update", this._onItemUpdate, this );
      this.addEventListener( "changeTextColor", this._scheduleUpdate, this );
      this.addEventListener( "changeFont", this._scheduleUpdate, this );
      this.addEventListener( "mousedown", this._onMouseDown, this );
      this.addEventListener( "mouseover", this._onMouseOver, this );
      this.addEventListener( "mouseout", this._onMouseOut, this );
      this.addEventListener( "keypress", this._onKeyPress, this );
      this._clientArea.addEventListener( "mousewheel", 
                                         this._onClientAreaMouseWheel, 
                                         this );
      this._mergeEventsTimer.addEventListener( "interval", 
                                                this._updateTopItemIndex, 
                                                this );    
      this._horzScrollBar.addEventListener( "changeValue", 
                                            this._onHorzScrollBarChangeValue, 
                                            this );
      this._vertScrollBar.addEventListener( "changeValue", 
                                            this._onVertScrollBarChangeValue, 
                                            this );
    },
    
    _configureScrollBars : function() {
      var dragBlocker = function( event ) { event.stopPropagation(); };
      var preferredWidth = this._vertScrollBar.getPreferredBoxWidth()
      var preferredHeight = this._horzScrollBar.getPreferredBoxHeight();
      this._horzScrollBar.setVisibility( false );
      this._horzScrollBar.setLeft( 0 );
      this._horzScrollBar.setMergeEvents( false );
      this._horzScrollBar.setHeight( preferredHeight );
      this._horzScrollBar.addEventListener( "dragstart", dragBlocker );
      this._vertScrollBar.setVisibility( false );
      this._vertScrollBar.setTop( 0 );
      this._vertScrollBar.setWidth( preferredWidth );
      this._vertScrollBar.setMergeEvents( false );
      this._vertScrollBar.addEventListener( "dragstart", dragBlocker );
    },
    
    _configureAreas : function() {
      this._clientArea.setOverflow( "hidden" );
      this._columnArea.setOverflow( "hidden" );
      this._columnArea.setTop( 0 );
      this._columnArea.setLeft( 0 );
      this._columnArea.setVisibility( false );
      // TODO [tb] : Find a cleaner solution to block drag-events
      var dragBlocker = function( event ) { event.stopPropagation(); };
      this._columnArea.addEventListener( "dragstart", dragBlocker );
      this._dummyColumn.setAppearance( "tree-column" );
      this._dummyColumn.setHeight( "100%" );
      this._dummyColumn.addState( "dummy" );
      this._columnArea.add( this._dummyColumn );
    },
    
    /////////////////////////////////
    // API for server - initial setup
    
    // NOTE : It is assumed that these setters are called only once and before
    // rendering any content (i.e. directly after the contructor) 
    
    setCheckBoxMetrics : function( left, width ) {
      this._checkBoxLeft = left;
      this._checkBoxWidth = width;
    },
    
    setHasCheckBoxes : function( value ) {
      this._hasCheckBoxes = value;
    },
    
    setHasFullSelection : function( value ) {
      this._hasFullSelection = value;
    },

    setHasMultiSelection : function( value ) {
      this._hasMultiSelection = value;
    },

    setIndentionWidth : function( offset ) {
      this._indentionWidth = offset;
    },
    
    setSelectionPadding : function( left, right ) {
      this._selectionPadding = [ left, right ];
    },
    
    setIsVirtual : function( value ) {
      this._isVirtual = value;
      if( value && this._sendRequestTimer === null ) {
        var timer = new qx.client.Timer( 400 );
        var req = org.eclipse.swt.Request.getInstance();
        timer.addEventListener( "interval", req.send, req );
        req.addEventListener( "send", timer.stop, timer );
        this._sendRequestTimer = timer;
      }
    },

    ///////////////////////////
    // API for server - general
    
    setHeaderVisible : function( value ) {
      this._columnArea.setVisibility( value );
      this._layoutX();
      this._layoutY();
      this._scheduleUpdate( true );
    },

    setHeaderHeight : function( value ) {
      this._headerHeight = value; 
      this._scheduleUpdate( true );
    },

    setItemHeight : function( height ) {
      this._itemHeight = height;
      for( var i = 0; i < this._rows.length; i++ ) {
        this._rows[ i ].setHeight( height );
      }
      this._scheduleUpdate( true , true );
    },
    
    setColumnCount : function( count ) {
      this._columnCount = count;
      this._scheduleUpdate();
      this._updateScrollWidth();
      this._renderGridVertical();
    },
    
    setItemMetrics : function( columnIndex, 
                               left, 
                               width, 
                               imageLeft, 
                               imageWidth, 
                               textLeft, 
                               textWidth ) 
    {
      this._itemLeft[ columnIndex ] = left;
      this._itemWidth[ columnIndex ] = width;
      this._itemImageLeft[ columnIndex ] = imageLeft;
      this._itemImageWidth[ columnIndex ] = imageWidth;
      this._itemTextLeft[ columnIndex ] = textLeft;
      this._itemTextWidth[ columnIndex ] = textWidth;
      this._scheduleUpdate();
      this._renderGridVertical();
      this._updateScrollWidth();
    },
        
    setTreeColumn : function( columnIndex ) {
      this._treeColumn = columnIndex;
    },
    
    setTopItemIndex : function( index ) {
      this._updateScrollHeight();
      this._vertScrollBar.setValue( index * this._itemHeight );
    },

    setScrollLeft: function( value ) {
      this._horzScrollBar.setValue( value );
    },

    selectItem : function( item ) {
      this._selectItem( item, false );
      this._scheduleItemUpdate( item );
    },
    
    deselectItem : function( item ) {
      this._deselectItem( item, false );
      this._scheduleItemUpdate( item );
    },
    
    setFocusItem : function( item ) {
      this._focusItem = item;
    },

    setScrollBarsVisible : function( horzVisible, vertVisible ) {
      this._horzScrollBar.setVisibility( horzVisible );
      this._vertScrollBar.setVisibility( vertVisible );
      this._layoutX();
      this._layoutY();
    },
    
    setHasSelectionListeners : function( value ) {
      this._hasSelectionListeners = value;
    },
    
    setAlignment : function( column, value ) {
      this._alignment[ column ] = value;
      this._scheduleUpdate();
    },
    
    setLinesVisible : function( value ) {
      this._linesVisible = value;
      this._renderGridHorizontal();
      this._renderGridVertical();
    },
        
    //////////////////
    // API for TreeRow
    
    hasNoColumns : function() {
      return this._columnCount === 0;
    },
    
    getSelectionPadding : function() {
      return this._selectionPadding;
    },
    
    getItemHeight : function() {
      return this._itemHeight;
    },

    getItemLeft : function( item, columnIndex, indention ) {
      var result = this._itemLeft[ columnIndex ];
      if( indention && this.isTreeColumn( columnIndex ) ) {
        result = this._correctOffset( result, item );
      }
      return result;
    },

    getItemWidth : function( item, columnIndex, indention ) {
      var result = this._itemWidth[ columnIndex ];
      if( indention && this.isTreeColumn( columnIndex ) ) {
        result -= this.getIndentionOffset( 1 );
      }
      return result;
    },

    getItemImageLeft : function( item, columnIndex ) {
      var result = this._itemImageLeft[ columnIndex ];
      if( this.isTreeColumn( columnIndex ) ) {
        result = this._correctOffset( result, item );
      }
      return result;
    },

    getItemImageWidth : function( item, columnIndex ) {
      var result = this._itemImageWidth[ columnIndex ];
      if( this.isTreeColumn( columnIndex ) ) {
        var offset = this.getItemImageLeft( item, columnIndex );
        result = this._correctWidth( result, offset, item, columnIndex );
      }
      return result;
    },

    getItemTextLeft : function( item, columnIndex ) {
      var result = this._itemTextLeft[ columnIndex ];
      if( this.isTreeColumn( columnIndex ) ) {
        result = this._correctOffset( result, item );
      }
      return result;
    },

    getItemTextWidth : function( item, columnIndex ) {
      var result = this._itemTextWidth[ columnIndex ];
      if( this.isTreeColumn( columnIndex ) ) {
        var offset = this.getItemTextLeft( item, columnIndex );
        result = this._correctWidth( result, offset, item, columnIndex );
      }
      return result;
    },

    getIndentionOffset : function( level ) {
      // NOTE [tb] : Shoud actually add the treeColumns offsets, assumes 0 now.
      return this._indentionWidth * level;
    },

    getHasCheckBoxes : function() {
      return this._hasCheckBoxes;
    },
    
    getHasFullSelection : function() {
      return this._hasFullSelection;
    },

    getHasMultiSelection : function() {
      return this._hasMultiSelection;
    },

    getIsVirtual : function() {
      return this._isVirtual;
    },

    getCheckBoxLeft : function( item ) {
      return this._correctOffset( this._checkBoxLeft, item );
    },
    
    getCheckBoxWidth : function( item ) {
      var result = this._checkBoxWidth;
      var offset = this.getCheckBoxLeft( item );
      return this._correctWidth( result, offset, item, 0 );
    },
    
    getColumnCount : function() {
      return Math.max( 1, this._columnCount );
    },
    
    getRootItem : function() {
      return this._rootItem;
    },
    
    isTreeColumn : function( columnIndex ) {
      return columnIndex === this._treeColumn;
    },

    getTreeColumnWidth : function() {
      return this._itemWidth[ this._treeColumn ];
    },
    
    isFocusItem : function( item ) {
      return this._focusItem === item;
    },

    isHoverItem : function( item ) {
      return this._hoverItem === item;
    },

    isItemSelected : function( item ) {
      return this._selection.indexOf( item ) != -1;
    },
        
    getAlignment : function( column ) {
      return this._alignment[ column ] ? this._alignment[ column ] : "left";
    },
    
    getStatesCopy : function() {
      if( !this.__states ) {
        this.__states = {};
      }
      var clone = {};
      for( var key in this.__states ) {
        clone[ key ] = true;
      }
      return clone;      
    },
    
    //////////////////////
    // API for TableColumn
    
    _addColumn : function( column ) {
      column.setHeight( "100%" );
      column.addEventListener( "changeWidth", this._updateScrollWidth, this );
      this._hookColumnMove( column );
      this._columnArea.add( column );
    },
    
    _hookColumnMove : function( column ) {
      column.addEventListener( "changeLeft", this._updateScrollWidth, this );
    },

    _unhookColumnMove : function( column ) {
      column.removeEventListener( "changeLeft", this._updateScrollWidth, this );
    },

    _removeColumn : function( column ) {
      this._unhookColumnMove( column );
      column.removeEventListener( "changeWidth", 
                                  this._updateScrollWidth, 
                                  this );
      this._columnArea.remove( column );
      this._updateScrollWidth();
      this._updateRows();
    },

    _onColumnChangeSize : function( evt ) {
      this._updateScrollWidth();
    },

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
    
    ////////////////
    // event handler

    _onItemUpdate : function( event ) {
      var item = event.getTarget();
      if( event.getData() === "collapsed" ) {
        // TODO [tb] : Should be done on server if focusItem is synced
        if( this._isChildOf( this._focusItem, item ) ) {
          this.setFocusItem( item );
        }
      }
      this._sendItemChange( item, event );
      this._renderItemChange( item, event );
    },

    _onVertScrollBarChangeValue : function() {
      if( this._vertScrollBar._internalValueChange ) {
        // NOTE : IE can create several scroll events with one click. Using
        // this timer to merge theses events improves performance a bit.
        this._mergeEventsTimer.start();
      } else {
        this._updateTopItemIndex();
      }
    },

    _updateTopItemIndex : function() {
      this._mergeEventsTimer.stop();
      var scrollTop = this._vertScrollBar.getValue();
      var oldIndex = this._topItemIndex;
      this._topItemIndex = Math.ceil( scrollTop / this._itemHeight );
      this._updateTopItem( oldIndex );
      if( this._inServerResponse() ) {
        this._scheduleUpdate();
      } else {
        this._sendTopItemIndexChange();
        this._scrollContentVertical( oldIndex );
      }
    },

    _onHorzScrollBarChangeValue : function() {
      this._clientArea.setScrollLeft( this._horzScrollBar.getValue() );
      this._columnArea.setScrollLeft( this._horzScrollBar.getValue() );
      this._renderGridVertical();
      this._sendScrollLeftChange();
    },

    _onMouseDown : function( event ) {
      var target = event.getOriginalTarget();
      if( target instanceof org.eclipse.rwt.widgets.TreeRow ) {
        this._onRowMouseDown( target, event );
      }
    },

    _onRowMouseDown : function( row, event ) {
      var item = this._findItemByRow( row );
      if( item != null ) {
        if( row.isExpandClick( event ) && item.hasChildren() ) {
          var expanded = !item.isExpanded();
          if( !expanded ) {
            this._deselectVisibleChildren( item );
          }
          item.setExpanded( expanded );
        } else if( row.isCheckBoxClick( event ) ) {
          item.setChecked( !item.isChecked() );
        } else if( row.isSelectionClick( event ) ) {
          this._onSelectionClick( event, item );
        }
      }
    },
    
    _onSelectionClick: function( event, item ) {
      // NOTE: Using a listener for "dblclick" does not work because the
      //       item is re-rendered on mousedown which prevents the dom-event.
      var doubleClick = this._isDoubleClicked( item );
      if( doubleClick ) {
        this._selectionTimestamp = null;
        this._sendSelectionEvent( item, true );
      } else {
        this._selectionTimestamp = new Date();
        if( this._hasMultiSelection ) {
          this._multiSelectItem( event, item );
        } else {
          this._singleSelectItem( item );            
        }
      }      
    },

    _onMouseOver : function( event ) {
      // TODO [tb] : ToolTips for incomplete cells
      var target = event.getOriginalTarget();
      if( target instanceof org.eclipse.rwt.widgets.TreeRow ) {
        this._onRowOver( target );
      }
    },

    _onMouseOut : function( event ) {
      var target = event.getOriginalTarget();
      if( target instanceof org.eclipse.rwt.widgets.TreeRow ) {
        this._onRowOver( null );
      }      
    },

    _onRowOver : function( row ) {
      var oldItem = this._hoverItem;
      this._hoverItem = this._findItemByRow( row );
      this._renderItem( oldItem );
      if( row!= null ) {
        row.renderItem( this._hoverItem );
      }
    },

    _onClientAreaMouseWheel : function( event ) {
      event.preventDefault();
      event.stopPropagation();
      var change = event.getWheelDelta() * this._itemHeight * 2;
      this._vertScrollBar.setValue( this._vertScrollBar.getValue() - change );
    },

    // TODO [tb] : scrolling via keypress can continue for a while after release
    _onKeyPress : function( event ) {
      if( this._focusItem != null ) {
        var item = null;
        var multiSelect = this._hasMultiSelection;
        switch( event.getKeyIdentifier() ) {
          case "Enter":
            this._sendSelectionEvent( this._focusItem, true );
          break;
          case "Space":
            item = this._focusItem;
          break;
          case "Up":
            item = this._getPreviousItem( this._focusItem );
          break;
          case "Down":
            item = this._getNextItem( this._focusItem );
          break;
          case "PageUp":
            var oldIndex = this._findIndexByItem( this._focusItem );
            var offset = this._rows.length - 2;
            var newIndex = Math.max( 0, oldIndex - offset );
            item = this._findItemByIndex( newIndex );
          break;
          case "PageDown":
            var oldIndex = this._findIndexByItem( this._focusItem );
            var offset = this._rows.length - 2;
            var max = this.getRootItem().getVisibleChildrenCount() - 1;
            var newIndex = Math.min( max, oldIndex + offset );
            item = this._findItemByIndex( newIndex, 
                                          this._topItem, 
                                          this._topItemIndex );
          break;
          case "Home":
            item = this.getRootItem().getChild( 0 );
          break;
          case "End":
            item = this.getRootItem().getLastChild();
          break;
          case "Left":
            if( this._focusItem.isExpanded() ) {
              this._focusItem.setExpanded( false );
            } else if( !this._focusItem.getParent().isRootItem() ) {
              item = this._focusItem.getParent();
              multiSelect = false;
            }
          break;
          case "Right":
            if( this._focusItem.hasChildren() ) {
              if( !this._focusItem.isExpanded() ) {
                this._focusItem.setExpanded( true );
              } else {
                item = this._focusItem.getChild( 0 )
              }
              multiSelect = false;
            }
          break;
        }
        if( item != null ) {
          this._scrollIntoView( item );
          if( multiSelect ) {
            this._multiSelectItem( event, item );
          } else {
            this._singleSelectItem( item );            
          } 
        }
      }
    },

    /////////////////
    // render content
    
    _renderItemChange : function( item, event ) {
      if( item.isDisplayable() ) {      
        switch( event.getData() ) {
          case "expanded":
          case "collapsed":
            this._scheduleUpdate( false, true ); 
          break;
          case "add":
          case "remove":
            if( item.isExpanded() ) {
              this._scheduleUpdate( false, true ); 
            } else {
              this._scheduleItemUpdate( item );
            }
          break;
          default:
            if( this._inServerResponse() ) {
              this._scheduleItemUpdate( item );
            } else {
              this._renderItem( item );
            }  
          break;
        }
      }
    },
    
    _scheduleItemUpdate : function( item ) {
      this._renderQueue[ item.toHashCode() ] = true;
      this.addToQueue( "updateRows" );
    },
    
    _scheduleUpdate : function( rowCount, scrollHeight ) {
      if( rowCount === true ) {
        this._updateRowCount();
      }
      if( scrollHeight === true ) {
        this.addToQueue( "scrollHeight" );
      }
      this._renderQueue[ "allItems" ] = true;
      this.addToQueue( "updateRows" );
    },

    _layoutPost : function( changes ) {
      this.base( arguments, changes );
      if( changes[ "scrollHeight" ] ) {
        this._updateScrollHeight();
      }
      if( changes[ "updateRows" ] ) {
        if( this._renderQueue[ "allItems" ] ) {
          this._updateAllRows();
        } else {
          this._updateQueuedItems();
        }
        this._renderQueue = {};
      }
    },
    
    _updateRowCount : function() {
      var height = this._clientArea.getHeight()
      var rowsNeeded = Math.ceil( height / this._itemHeight );
      var rowWidth = this._getRowWidth();
      while( this._rows.length < rowsNeeded ) {
        var row = new org.eclipse.rwt.widgets.TreeRow( this );
        row.setHeight( this._itemHeight );
        row.setWidth( rowWidth );
        this._clientArea.add( row );
      }
      while( this._rows.length > rowsNeeded ) {
        this._rows[ this._rows.length - 1 ].destroy();
      }
      this._renderGridHorizontal();
    },

    _updateAllRows : function() {
      this._updateRows( 0, this._rows.length );
    },
    
    _updateQueuedItems : function() {
      var item = this._getTopItem();
      var row = 0;
      while( item != null && row < this._rows.length ) {
        if( this._renderQueue[ item.toHashCode() ] ) {
          this._rows[ row ].renderItem( item );
        }
        item = this._getNextItem( item );
        row++;
      }
    },
    
    _updateRows : function( from, delta ){
      var item = this._getTopItem();
      var to = from + delta;
      var row = 0;
      while( item != null && row < this._rows.length ) {
        if( row >= from && row <= to ) {
          this._rows[ row ].renderItem( item );
        }
        item = this._getNextItem( item );
        row++;
      }
      for( var i = row; i < this._rows.length; i++ ) {
        this._rows[ i ].renderItem( null );
      }
    },
    
    _renderItem : function( item ) {
      if( this._isCreated && item != null ) {
        var row = this._findRowByItem( item );
        if( row!= null ) {
          row.renderItem( item );
        }
      }
    },

    //////////// 
    // scrolling

    _getTopItem : function() {
      if( this._topItem === null ) {
        this._updateTopItem();
      }
      return this._topItem;
    },
    
    _updateScrollHeight : function() {
      var itemCount = this.getRootItem().getVisibleChildrenCount();
      var height = ( itemCount + 1 ) * this._itemHeight;
      if( this._columnArea.getVisibility() ) {
        height += this._headerHeight;
      }
      // recalculating topItem can be expensive, therefore this simple check:
      if( this._vertScrollBar.getMaximum() != height ) {
        // Without the check, it may cause an error in FF when unloading doc
        if( !this._vertScrollBar.getDisposed() ) {
          this._vertScrollBar.setMaximum( height );
        }
        // TODO [tb] : topItem changes only under certain conditions. Optimize?
        this._topItem = null;
      }
    },
    
    _updateTopItem : function( oldIndex ) {
      if( typeof oldIndex == "number" ) {
        this._topItem = this._findItemByIndex( this._topItemIndex, 
                                               this._topItem, 
                                               oldIndex );
      } else {
        this._topItem = this._findItemByIndex( this._topItemIndex );
      }
    },
    
    _updateScrollWidth : function() {
      var width = this._getItemWidth();
      var rowWidth = this._getRowWidth();
      for( var i = 0; i < this._rows.length; i++ ) {
        this._rows[ i ].setWidth( rowWidth );
      }
      if( !this._horzScrollBar.getDisposed() ) {
        this._horzScrollBar.setMaximum( width );
      }
      if( this._columnArea.getVisibility() ) {
        this._renderDummyColumn();
      }
    },

    _renderDummyColumn : function() {
      var dummyLeft = this._getDummyColumnLeft();
      var areaWidth = this._columnArea.getWidth()
      if( dummyLeft < areaWidth ) {
        this._dummyColumn.setVisibility( true );
        this._dummyColumn.setLeft( dummyLeft );
        this._dummyColumn.setWidth( areaWidth - dummyLeft );
      } else {
        this._dummyColumn.setVisibility( false );
      }
    },
    
    _getDummyColumnLeft : function() {
      var columns = this._columnArea.getChildren();
      var result = 0;
      for( var i = 0; i < columns.length; i++ ) {
        if( columns[ i ] !== this._dummyColumn ) {
          var left = columns[ i ].getLeft() + columns[ i ].getWidth();
          result = Math.max( result, left );
        }
      }
      return result;
   },

    _scrollIntoView : function( item ) {
      var result = false;
      var index = this._findIndexByItem( item );
      if( index < this._topItemIndex ) {
        this.setTopItemIndex( index );
        result = true;
      } else if( index > ( this._topItemIndex + this._rows.length - 2 ) ) {
        this.setTopItemIndex( index - this._rows.length + 2 );
        result = true;
      }
      return result;
    },
    
    _scrollContentVertical : function( oldTopItemIndex ) {
      var delta = this._topItemIndex - oldTopItemIndex;
      var forwards = delta > 0;
      delta = Math.abs( delta );
      if( delta >= this._rows.length ) { 
        this._updateAllRows();
      } else {
        var numberOfShiftingRows = this._rows.length - delta;
        var updateFromRow = forwards ? numberOfShiftingRows : 0;
        var newFirstRow = forwards ? delta : numberOfShiftingRows;
        this._switchRows( newFirstRow );
        this._updateRows( updateFromRow, delta );
      }
    },

    _switchRows : function( newFirstRow ) {
      // TODO [tb] : would be better placed in parent.js
      var temp = this._rows.slice( newFirstRow );
      this._rows = temp.concat( this._rows.slice( 0, newFirstRow ) );
      this._clientArea._children = this._rows;
      this._clientArea._invalidateVisibleChildren();
      var layouter = this._clientArea.getLayoutImpl();
      var changes = { "locationY" : true };
      for( var i = 0; i < this._rows.length; i++ ) {
        layouter.layoutChild( this._rows[ i ], changes );
      }
    },
        
    //////////////
    // Send events
    
    _sendSelectionChange : function( item ) {
      if( !this._inServerResponse() ) {
        var req = org.eclipse.swt.Request.getInstance();
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var id = wm.findIdByWidget( this );
        var selection = this._getSelectionIndices();        
        req.addParameter( id + ".selection", selection );
        this._sendSelectionEvent( item, false );
      }
    },
    
    _sendTopItemIndexChange : function() {
      var req = org.eclipse.swt.Request.getInstance();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( this );
      req.addParameter( id + ".topItemIndex", this._topItemIndex );
      if( this._isVirtual ) {
        this._sendRequestTimer.start();
      }
    },
    
    _sendScrollLeftChange : function() {
      if( !this._inServerResponse() ) {
        var req = org.eclipse.swt.Request.getInstance();
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var id = wm.findIdByWidget( this );
        req.addParameter( id + ".scrollLeft", this._horzScrollBar.getValue() );
      }
      if( this._isVirtual ) {
        this._sendRequestTimer.start();
      }
    },
    
    _sendItemChange : function( item, event ) {
      if( !this._inServerResponse() ) {
        switch( event.getData() ) {
          case "expanded":
            this._sendItemEvent( item, "org.eclipse.swt.events.treeExpanded" );
          break;
          case "collapsed":
            this._sendItemEvent( item, "org.eclipse.swt.events.treeCollapsed" );
          break;
          default:
        }
      }      
    },

    _sendItemEvent : function( item, type ) {
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var treeItemId = wm.findIdByWidget( item );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( type, treeItemId );
      req.send();
    },
    
    _sendSelectionEvent : function( item, defaultSelected ) {
      if( this._hasSelectionListeners ) {
        var req = org.eclipse.swt.Request.getInstance();
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var id = wm.findIdByWidget( this );
        var eventName = "org.eclipse.swt.events.widget";
        eventName += defaultSelected ? "DefaultSelected" : "Selected";
        var itemId = wm.findIdByWidget( item );
        req.addEvent( eventName, id );
        org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
        req.addParameter( eventName + ".item", itemId );
        req.send();
      }      
    },

    _isDoubleClicked : function( item ) {
      var result = false;
      if( this.isFocusItem( item ) && this._selectionTimestamp != null ) {
        var stamp = new Date();
        var diff = org.eclipse.swt.EventUtil.DOUBLE_CLICK_TIME;
        if( stamp.getTime() - this._selectionTimestamp.getTime() < diff ) {
          result = true;
        }
      }
      return result;
    },

    _getSelectionIndices : function() {
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var result = [];
      for( var i = 0; i < this._selection.length; i++ ) {
        result.push( wm.findIdByWidget( this._selection[ i ] ) );
      }
      return result.join();
    },
    
    ////////////////////
    // focus & selection
    
    _singleSelectItem : function( item ) {
      this._deselectAll();
      this._leadItem = null;
      this._selectItem( item, true );
      this._sendSelectionChange( item );
      this.setFocusItem( item );
    },
    
    _multiSelectItem : function( event, item ) {
      if( event.isCtrlPressed() ) {
        if(   event instanceof qx.event.type.KeyEvent
           && item != this._focusItem  ) {
          this.setFocusItem( item );
        } else {
          this._ctrlSelectItem( item );            
        }
      } else if( event.isShiftPressed() ) {
        if( this._focusItem != null ) {
          this._shiftSelectItem( item );
        } else {
          this._singleSelectItem( item );
        } 
      } else {
        this._singleSelectItem( item );
      }
    },

    _ctrlSelectItem : function( item ) {
      if( !this.isItemSelected( item ) ) {
        this._selectItem( item, true );
      } else {
        this._deselectItem( item, true );
      }
      this._sendSelectionChange( item );
      this.setFocusItem( item );
    },

    _shiftSelectItem : function( item ) {
      this._deselectAll();
      var currentItem 
        = this._leadItem != null ? this._leadItem : this._focusItem;
      this._leadItem = currentItem;
      var targetItem = item;
      var startIndex = this._findIndexByItem( currentItem );
      var endIndex = this._findIndexByItem( targetItem );
      if( startIndex > endIndex ) {
        var temp = currentItem;
        currentItem = targetItem;
        targetItem = temp;
      }
      this._selectItem( currentItem, true );
      while( currentItem !== targetItem ) {
        currentItem = this._getNextItem( currentItem );
        this._selectItem( currentItem, true );
      }
      this._sendSelectionChange( item );
      this.setFocusItem( item );
    },

    _selectItem : function( item, render ) {
      if( !this.isItemSelected( item ) ) {
        this._selection.push( item );
      }
      if( render ) {
        this._renderItem( item );
      }
    },
    
    _deselectItem : function( item, render ) {
      if( this.isItemSelected( item ) ) {
        this._selection.splice( this._selection.indexOf( item ), 1 );
      }
      if( render ) {
        this._renderItem( item );
      }
    },

    _deselectAll : function() {
      var oldSelection = this._selection;
      this._selection = [];
      for( var i = 0; i < oldSelection.length; i++ ) {
        this._renderItem( oldSelection[ i ] );
      }
    },

    _deselectVisibleChildren : function( item ) {
      var currentItem = this._getNextItem( item );
      var finalItem = this._getNextItem( item, true );
      while( currentItem != finalItem) {
        this._deselectItem( currentItem, false );
        currentItem = this._getNextItem( currentItem, false );
      } 
    },

    _applyFocused : function( newValue, oldValue ) {
      this.base( arguments, newValue, oldValue );
      this._scheduleUpdate();
    },

    
    ////////////////////////////
    // internal layout & theming
    
    _applyBackgroundColor : function( newValue ) {
      this._clientArea.setBackgroundColor( newValue );
    },

    _applyBackgroundImage : function( newValue ) {
      this._clientArea.setBackgroundImage( newValue );
    },

    _applyWidth : function( newValue, oldValue ) {
      this.base( arguments, newValue, oldValue );
      this._layoutX();
    },
    
    _applyHeight : function( newValue, oldValue ) {
      this.base( arguments, newValue, oldValue );
      this._layoutY();
    },
    
    _applyBorder : function( newValue, oldValue ) {
      this.base( arguments, newValue, oldValue );
      this._layoutX();
      this._layoutY();
    },
    
    _renderGridVertical : function() {
      var lineNr = 0;
      if( this._linesVisible ) {
        while( this._renderVerticalGridline( lineNr ) ) {
          lineNr++;
        }
      }
      while( this._vertGridLines.length > lineNr ) {
        this._vertGridLines.pop().destroy();
      }
    },
    
    _renderVerticalGridline : function( lineNr ) {
      var result = false;
      if( lineNr < this._columnCount ) {
        var clientWidth = this._clientArea.getWidth();
        var left = this._itemLeft[ lineNr ] + this._itemWidth[ lineNr ] - 1;
        left -= this._horzScrollBar.getValue(); 
        if( left < clientWidth ) {
          result = true;
          if( left > 0 ) {
            var line = this._getVerticalGridline( lineNr );
            line.setLeft( left );
            line.setTop( this._clientArea.getTop() );
            line.setHeight( this._clientArea.getHeight() );
          }
        }
      }
      return result;
    },
    
    _getVerticalGridline : function( number ) {
      if( typeof this._vertGridLines[ number ] === "undefined" ) {
        var line = new qx.ui.basic.Terminator();
        line.setZIndex( 1 );
        line.setWidth( 0 );
        line.setBorder( this._getVerticalGridBorder() );
        this._vertGridLines[ number ] = line;
        this.add( line );
      }
      return this._vertGridLines[ number ];
    },
    
    _renderGridHorizontal : function() {
      var border = this._linesVisible ? this._getHorizontalGridBorder() : null;
      for( var i = 0; i < this._rows.length; i++ ) {
        this._rows[ i ].setBorder( border );
      }      
    },
    
    _getHorizontalGridBorder : function() {
      if( this._horzGridBorder === null ) {
        var border = new qx.ui.core.Border( 0 );
        border.setColor( "#d0d0d0" );
        border.setWidthBottom( 1 );
        this._horzGridBorder = border; // TODO [tb] : make static or themeable
      }
      return this._horzGridBorder;
    },
    
    _getVerticalGridBorder : function() {
      if( this._vertGridBorder === null ) {
        var border = new qx.ui.core.Border( 0 );
        border.setColor( "#d0d0d0" );
        border.setWidthRight( 1 );
        this._vertGridBorder = border; // TODO [tb] : make static or themeable
      }
      return this._vertGridBorder;
    },
    
    _layoutX : function() {
      var width = this.getWidth() - this.getFrameWidth();
      if( this._vertScrollBar.getVisibility() ) {
        width -= this._vertScrollBar.getWidth();
        this._vertScrollBar.setLeft( width );
      }
      this._horzScrollBar.setWidth( width );
      if( this._columnArea.getVisibility() ) {
        this._columnArea.setWidth( width );
      }
      this._clientArea.setWidth( width );
      this._updateScrollWidth();
      this._renderGridVertical(); // TODO [tb] : optimize calls
    },
    
    _layoutY : function() {
      var height = this.getHeight() - this.getFrameHeight();
      var top = 0;
      if( this._columnArea.getVisibility() ) {
        top = this._headerHeight;
        height -= this._headerHeight;
        this._columnArea.setHeight( this._headerHeight );
      }
      if( this._horzScrollBar.getVisibility() ) {
        height -= this._horzScrollBar.getHeight();
        this._horzScrollBar.setTop( top + height );
      }
      height = Math.max( 0, height );
      // TODO [tb] : position scrollbar like table (i.e. respect header)
      this._vertScrollBar.setHeight( top + height );
      this._clientArea.setTop( top );
      this._clientArea.setHeight( height );
      this._renderGridVertical();
      this._scheduleUpdate( true );
    },
    
    _getItemWidth : function() {
      var result = 0;
      if( this._itemLeft.length > 0 ) {
        for( var i = 0; i < this.getColumnCount(); i++ ) {
          result = Math.max( result, this._itemLeft[ i ] + this._itemWidth[ i ] );
        }
      }
      return result;
    },

    _getRowWidth : function() {
      var width = this._clientArea.getWidth()
      var result = Math.max( this._getItemWidth(), width );
      return result;
    },
        
    ///////////////
    // model-helper
    
    _getNextItem : function( item, ignoreChildren ) {
      var result = null;
      if( !ignoreChildren && item.hasChildren() && item.isExpanded() ) {
        result = item.getChild( 0 );
      } else if( item.hasNextSibling() ) {
        result = item.getNextSibling();
      } else if( item.getLevel() > 0 ) {
        result = this._getNextItem( item.getParent(), true );
      }
      return result;
    },
    
    _getPreviousItem : function( item ) {
      var result = null;
      if( item.hasPreviousSibling() ) {
        result = item.getPreviousSibling();
        while( result.hasChildren() && result.isExpanded() ) {
          result = result.getLastChild();
        }        
      } else if( item.getLevel() > 0 ) {
        result = item.getParent();
      }
      return result;
    },
    
    _findItemByRow : function( targetRow ) {
      var result = null;
      if( targetRow != null ) {
        var item = this._getTopItem();
        var rowIndex = 0;
        while( item != null && result == null ) {
          if( this._rows[ rowIndex ] === targetRow ) {
            result = item;
          }
          item = this._getNextItem( item );
          rowIndex++;
        }
      }
      return result;
    },
    
    _findRowIndexByItem : function( targetItem ) {
      var item = this._getTopItem();
      var row = 0;
      while( item != targetItem && item != null && row < this._rows.length ) {
        item = this._getNextItem( item );
        row++;
      }
      return item != null && row < this._rows.length ? row : null; 
    },
    
    _findRowByItem : function( targetItem ) {
      var index = this._findRowIndexByItem( targetItem );
      return index != null ? this._rows[ index ] : null; 
    },

    _findItemByIndex : function( index, startItem, startIndex ) {
      // TODO [tb] : simplify 
      var item = startItem ? startItem : this.getRootItem().getChild( 0 );
      var i = startIndex ? startIndex : 0;
      var forwards = index >= i;
      if( forwards ) {
        while( i != index && item != null ) {
          var nextSiblingIndex = i + item.getVisibleChildrenCount() + 1;
          if( nextSiblingIndex <= index ) {
            i = nextSiblingIndex;
            item = this._getNextItem( item, true );
          } else {
            item = this._getNextItem( item, false );
            i++;
          } 
        }
      } else {
        while( i != index && item != null ) {
          if( item.hasPreviousSibling() ) {
            var previous = item.getPreviousSibling();
            var prevSiblingIndex = i - ( previous.getVisibleChildrenCount() + 1 );
            if( prevSiblingIndex >= index ) {
              i = prevSiblingIndex;
              item = previous;              
            } else {
              item = this._getPreviousItem( item );
              i--;
            }
          } else {
            item = this._getPreviousItem( item );
            i--;
          }
        }        
      }
      return item;
    },
    
    _findIndexByItem : function( targetItem ) {
      // TODO [tb] : optimize for IE6 (can produce "abort script" prompt 
      //             and jerky keyboard-navigation)
      var index = 0;
      var item = this.getRootItem().getChild( 0 );
      while( item != null && item != targetItem ) {
        item = this._getNextItem( item );
        index++;
      }
      return index;
    },
    
    _isChildOf : function( child, parent ) {
      var result = false;
      var item = child;
      while( item != null && !result ) {
        item = item.getParent();
        result = item === parent;
      }
      return result;
    },
    
    ////////////////////
    // row layout-helper
    
    _correctOffset : function( offset, item ) {
      return offset + this.getIndentionOffset( item.getLevel() + 1 );
    },
    
    _correctWidth : function( width, offset, item, column ) {
      var result = width;
      var columnEnd = this._itemLeft[ column ] + this._itemWidth[ column ];
      var elementEnd = offset + result;
      if( elementEnd > columnEnd ) {
        result = Math.max( 0, columnEnd - offset );
      }
      return result;
    },
    
    //////////////
    // misc helper
    
    _inServerResponse : function() {
      return org_eclipse_rap_rwt_EventUtil_suspend;      
    }
    
  }
} );