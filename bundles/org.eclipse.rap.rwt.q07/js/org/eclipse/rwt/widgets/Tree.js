/*******************************************************************************
 * Copyright (c) 2010, 2011 Innoopract Informationssysteme GmbH.
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
    this._isVirtual = false;
    this._hasMultiSelection = false;
    // Internal State:
    this._hasSelectionListeners = false;
    this._topItem = null;
    this._leadItem = null;
    this._topItemIndex = 0;
    this._selection = [];
    this._focusItem = null;
    this._renderQueue = {};
    this._resizeLine = null;
    this._selectionTimestamp = null;
    this._delayedSelection = false;
    // Layout:
    this._headerHeight = 0;
    this._itemHeight = 16;
    // Timer & Border
    this._mergeEventsTimer = new qx.client.Timer( 50 );
    this._sendRequestTimer = null;
    this._vertGridBorder = null;
    this._horzGridBorder = null;
    // Subwidgets
    this._rowContainer = new org.eclipse.rwt.widgets.TreeRowContainer();
    this._columnArea = new qx.ui.layout.CanvasLayout();
    this._dummyColumn = new qx.ui.basic.Atom();
    this._horzScrollBar = new org.eclipse.rwt.widgets.ScrollBar( true );
    this._vertScrollBar = new org.eclipse.rwt.widgets.ScrollBar( false );
    this._hasScrollBarsSelectionListener = false;
    this._vertGridLines = [];
    this.add( this._columnArea );
    this.add( this._rowContainer );
    this.add( this._horzScrollBar );
    this.add( this._vertScrollBar );
    this._cellToolTip = null;
    // Configure:
    this._config = this._rowContainer.getRenderConfig();
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
    this._rowContainer = null;
    this._columnArea = null;
    this._horzScrollBar = null;
    this._vertScrollBar = null;
    this._topItem = null;
    this._leadItem = null;
    this._focusItem = null;
    this._resizeLine = null;
    if( this._cellToolTip ) {
      this._cellToolTip.destroy();
      this._cellToolTip = null;
    }
  },

  members : {

    /////////////////////
    // Contructor helpers
    
    _registerListeners : function() {
      this._rootItem.addEventListener( "update", this._onItemUpdate, this );
      this.addEventListener( "mousedown", this._onMouseDown, this );
      this.addEventListener( "mouseup", this._onMouseUp, this );
      this.addEventListener( "mouseout", this._onMouseOut, this );
      this.addEventListener( "keypress", this._onKeyPress, this );
      this._rowContainer.addEventListener( "mousewheel", this._onClientAreaMouseWheel, this );
      this._mergeEventsTimer.addEventListener( "interval", this._updateTopItemIndex, this );
      this._horzScrollBar.addEventListener( "changeValue", this._onHorzScrollBarChangeValue, this );
      this._vertScrollBar.addEventListener( "changeValue", this._onVertScrollBarChangeValue, this );
      this._rowContainer.setSelectionProvider( this.isItemSelected, this );
      this._rowContainer.setPostRenderFunction( this._vertScrollBar.autoEnableMerge, 
                                                this._vertScrollBar );
    },
    
    _configureScrollBars : function() {
      var dragBlocker = function( event ) { event.stopPropagation(); };
      this._horzScrollBar.setZIndex( 1e8 );
      this._horzScrollBar.setVisibility( false );
      this._horzScrollBar.setLeft( 0 );
      this._horzScrollBar.setMergeEvents( false );
      this._horzScrollBar.addEventListener( "dragstart", dragBlocker );
      this._vertScrollBar.setZIndex( 1e8 );
      this._vertScrollBar.setVisibility( false );
      this._vertScrollBar.setIncrement( 16 );
      this._vertScrollBar.setMergeEvents( false );
      this._vertScrollBar.addEventListener( "dragstart", dragBlocker );
    },
    
    _configureAreas : function() {
      this._columnArea.setOverflow( "hidden" );
      this._columnArea.addEventListener( "appear", this._onColumnAreaAppear, this );
      this._columnArea.setTop( 0 );
      this._columnArea.setLeft( 0 );
      // NOTE: Need to use setDisplay here instead of setVisibility,
      // otherwise the appear event would be fired when the widget
      // is not yet ready to be scrolled (see _onColumnAreaAppear)
      this._columnArea.setDisplay( false );
      // TODO [tb] : Find a cleaner solution to block drag-events
      var dragBlocker = function( event ) { event.stopPropagation(); };
      this._columnArea.addEventListener( "dragstart", dragBlocker );
      this._dummyColumn.setAppearance( "tree-column" );
      this._dummyColumn.setHeight( "100%" );
      this._dummyColumn.setLabel( "&nbsp;" );
      this._dummyColumn.addState( "dummy" );
      this._columnArea.add( this._dummyColumn );
    },
    
    _createSendRequestTimer : function() {
      if( this._sendRequestTimer === null ) {
        var timer = new qx.client.Timer( 400 );
        var req = org.eclipse.swt.Request.getInstance();
        timer.addEventListener( "interval", req.send, req );
        req.addEventListener( "send", timer.stop, timer );
        this._sendRequestTimer = timer;
      }
    },
    
    /////////////////////////////////
    // API for server - initial setup
    
    // NOTE : It is assumed that these setters are called only once and before
    // rendering any content (i.e. directly after the contructor) 
    
    setCheckBoxMetrics : function( left, width ) {
      this._config.checkBoxLeft = left;
      this._config.checkBoxWidth = width;
    },
    
    setHasCheckBoxes : function( value ) {
      this._config.hasCheckBoxes = value;
    },
    
    setHasFullSelection : function( value ) {
      this._config.fullSelection = value;
    },

    setHasMultiSelection : function( value ) {
      this._hasMultiSelection = value;
    },

    setHasNoScroll : function( value ) {
      if( value ) {
        this._rowContainer.removeEventListener( "mousewheel", this._onClientAreaMouseWheel, this );
      }
    },

    setIndentionWidth : function( offset ) {
      this._config.indentionWidth = offset;
    },
    
    setSelectionPadding : function( left, right ) {
      this._config.selectionPadding = [ left, right ];
    },
    
    setIsVirtual : function( value ) {
      this._isVirtual = value;
      if( value ) {
        this._createSendRequestTimer();
      }
    },

    ///////////////////////////
    // API for server - general
    
    setItemCount : function( value ) {
      this._rootItem.setItemCount( value );
    },
    
    setHeaderVisible : function( value ) {
      this._columnArea.setDisplay( value );
      this._layoutX();
      this._layoutY();
    },

    setHeaderHeight : function( value ) {
      this._headerHeight = value;
      this._layoutX();
      this._layoutY();
    },

    setItemHeight : function( height ) {
      this._itemHeight = height;
      this._vertScrollBar.setIncrement( height );
      this._rowContainer.setRowHeight( height );
      this._scheduleUpdate( true );
    },
    
    setColumnCount : function( count ) {
      this._config.columnCount = count;
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
      this._config.itemLeft[ columnIndex ] = left;
      this._config.itemWidth[ columnIndex ] = width;
      this._config.itemImageLeft[ columnIndex ] = imageLeft;
      this._config.itemImageWidth[ columnIndex ] = imageWidth;
      this._config.itemTextLeft[ columnIndex ] = textLeft;
      this._config.itemTextWidth[ columnIndex ] = textWidth;
      this._scheduleUpdate();
      this._renderGridVertical();
      this._updateScrollWidth();
    },
        
    setTreeColumn : function( columnIndex ) {
      this._config.treeColumn = columnIndex;
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
      if( !horzVisible ) {
        this._horzScrollBar.setValue( 0 );
      }
      this._horzScrollBar.setVisibility( horzVisible );
      if( !vertVisible ) {
        this._vertScrollBar.setValue( 0 );
      }
      this._vertScrollBar.setVisibility( vertVisible );
      this._layoutX();
      this._layoutY();
    },
    
    setHasSelectionListeners : function( value ) {
      this._hasSelectionListeners = value;
    },
    
    setAlignment : function( column, value ) {
      this._config.alignment[ column ] = value;
      this._scheduleUpdate();
    },
    
    setLinesVisible : function( value ) {
      this._config.linesVisible = value;
      if( value ) {
        this.addState( "linesvisible" );
      } else {
        this.removeState( "linesvisible" );
      }
      this._rowContainer.setRowLinesVisible( value );
      this._scheduleUpdate();
      this._renderGridHorizontal();
      this._renderGridVertical();
    },
    
    // TODO [tb] : set the variant directly instead of using only state
    addState : function( state ) {
      this.base( arguments, state );
      if( state.slice( 0, 8 ) === "variant_" ) {
        this._config.variant = state;
      }
    },
        
    removeState : function( state ) {
      if( this._config.variant === state ) {
        this._config.variant = null;
      }
      this.base( arguments, state );
    },
        
    ////////////////////////
    // API for Tests and DND
    
    getRenderConfig : function() {
      return this._config;
    },

    getRootItem : function() {
      return this._rootItem;
    },
    
    isFocusItem : function( item ) { 
      return this._focusItem === item;
    },

    isItemSelected : function( item ) {
      return this._selection.indexOf( item ) != -1;
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
      this._rowContainer.renderAll();
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
      var top = this._rowContainer.getTop();
      this._resizeLine._renderRuntimeTop( top );
      var left = x - 2 - this._horzScrollBar.getValue();
      this._resizeLine._renderRuntimeLeft( left );
      var height = this._rowContainer.getHeight();
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
      if( event.getData() === "remove" ) {
        var oldItem = event.getRelatedTarget();
        this._deselectItem( oldItem, false );
        if( this._topItem === oldItem ) {
          this._topItem = null;
        }
        if( this._leadItem === oldItem ) {
          this._leadItem = null;
        }
        if( this._focusItem === oldItem ) {
          this._focusItem = null;
        }
      }
      this._sendItemUpdate( item, event );
      this._renderItemUpdate( item, event );
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
      if( this._inServerResponse() ) {
        this._topItem = null;
        this._scheduleUpdate();
      } else {
        this._sendTopItemIndexChange();
        this._updateTopItem( oldIndex, true );
      }
    },

    _onHorzScrollBarChangeValue : function() {
      this._rowContainer.setScrollLeft( this._horzScrollBar.getValue() );
      if( this._columnArea.isSeeable() ) {
        this._columnArea.setScrollLeft( this._horzScrollBar.getValue() );
      }
      this._renderGridVertical();
      this._sendScrollLeftChange();
    },

    _onMouseDown : function( event ) {
      this._delayedSelection = false;
      var target = event.getOriginalTarget();
      if( target instanceof org.eclipse.rwt.widgets.TreeRow ) {
        this._onRowMouseDown( target, event );
      }
    },
    
    _onMouseUp : function( event ) {
      if( this._delayedSelection ) {
        this._onMouseDown( event );
      }
    },

    _onRowMouseDown : function( row, event ) {
      // TODO [tb] : maybe this abstraction could also be done in TreeRowContainer
      var item = this._rowContainer.findItemByRow( row );
      if( item != null ) {
        if( row.isExpandSymbolTarget( event ) && item.hasChildren() ) {
          var expanded = !item.isExpanded();
          if( !expanded ) {
            this._deselectVisibleChildren( item );
          }
          item.setExpanded( expanded );
        } else if( row.isCheckBoxTarget( event ) ) {
          item.setChecked( !item.isChecked() );
          this._sendItemCheckedChange( item );
        } else if( row.isSelectionClick( event, this._config.fullSelection ) ) {
          this._onSelectionClick( event, item );
        }
      }
    },

    _onSelectionClick : function( event, item ) {
      // NOTE: Using a listener for "dblclick" does not work because the
      //       item is re-rendered on mousedown which prevents the dom-event.
      var doubleClick = this._isDoubleClicked( event, item );
      if( doubleClick ) {
        this._sendSelectionEvent( item, true, null );
      } else {
        if( this._hasMultiSelection ) {
          if( !this._delayMultiSelect( event, item ) ) {
            this._multiSelectItem( event, item );          
          }
        } else {
          this._singleSelectItem( item );            
        }
      }      
    },
    
    _delayMultiSelect : function( event, item ) {
      if( this._isDragSource() && this.isItemSelected( item ) && event.getType() === "mousedown" ) {
        this._delayedSelection = true;
      }
      return this._delayedSelection;
    },

    _onMouseOut : function( event ) {
      this._delayedSelection = false;
    },

    _onClientAreaMouseWheel : function( event ) {
      event.preventDefault();
      event.stopPropagation();
      var change = event.getWheelDelta() * this._itemHeight * 2;
      this._vertScrollBar.setValue( this._vertScrollBar.getValue() - change );
    },

    _onKeyPress : function( event ) {
      if( this._focusItem != null ) {
        switch( event.getKeyIdentifier() ) {
          case "Enter":
            this._handleKeyEnter( event );
          break;
          case "Space":
            this._handleKeySpace( event );
          break;
          case "Up":
            this._handleKeyUp( event );
          break;
          case "Down":
            this._handleKeyDown( event );
          break;
          case "PageUp":
            this._handleKeyPageUp( event );
          break;
          case "PageDown":
            this._handleKeyPageDown( event );
          break;
          case "Home":
            this._handleKeyHome( event );
          break;
          case "End":
            this._handleKeyEnd( event );
          break;
          case "Left":
            this._handleKeyLeft( event );
          break;
          case "Right":
            this._handleKeyRight( event );
          break;
        }
      }
    },

    _onColumnAreaAppear : function() {
      this._columnArea.setScrollLeft( this._horzScrollBar.getValue() );
    },
    
    _handleKeyEnter : function( event ) {
      this._sendSelectionEvent( this._focusItem, true, null );
    },
    
    _handleKeySpace : function( event ) {
      var itemIndex = this._findIndexByItem( this._focusItem );
      this._handleKeyboardSelect( event, this._focusItem, itemIndex );
    },
    
    _handleKeyUp : function( event ) {
      var item = this._focusItem.getPreviousItem();
      if( item != null ) {
        var itemIndex = this._findIndexByItem( item );
        this._handleKeyboardSelect( event, item, itemIndex );
      }
    },
    
    _handleKeyDown : function( event ) {
      var item = this._focusItem.getNextItem();
      if( item != null ) {
        var itemIndex = this._findIndexByItem( item );
        this._handleKeyboardSelect( event, item, itemIndex );
      }
    },
    
    _handleKeyPageUp : function( event ) {
      var oldIndex = this._findIndexByItem( this._focusItem );
      var offset = this._rowContainer.getChildrenLength() - 2;
      var newIndex = Math.max( 0, oldIndex - offset );
      var item = this._findItemByIndex( newIndex );
      var itemIndex = this._findIndexByItem( item );
      this._handleKeyboardSelect( event, item, itemIndex );
    },
    
    _handleKeyPageDown : function( event ) {
      var oldIndex = this._findIndexByItem( this._focusItem );
      var offset = this._rowContainer.getChildrenLength() - 2;
      var max = this.getRootItem().getVisibleChildrenCount() - 1;
      var newIndex = Math.min( max, oldIndex + offset );
      var item = this._findItemByIndex( newIndex, this._topItem, this._topItemIndex );
      var itemIndex = this._findIndexByItem( item );
      this._handleKeyboardSelect( event, item, itemIndex );
    },
    
    _handleKeyHome : function( event ) {
      var item = this.getRootItem().getChild( 0 );
      this._handleKeyboardSelect( event, item, 0 );
    },
    
    _handleKeyEnd : function( event ) {
      var item = this.getRootItem().getLastChild();
      var itemIndex = this.getRootItem().getVisibleChildrenCount() - 1;
      this._handleKeyboardSelect( event, item, itemIndex );
    },

    _handleKeyLeft : function( event ) {
      if( this._focusItem.isExpanded() ) {
        this._focusItem.setExpanded( false );
      } else if( !this._focusItem.getParent().isRootItem() ) {
        var item = this._focusItem.getParent();
        var itemIndex = this._findIndexByItem( item );
        this._handleKeyboardSelect( event, item, itemIndex, true );
      }
    },
    
    _handleKeyRight : function( event ) {
      if( this._focusItem.hasChildren() ) {
        if( !this._focusItem.isExpanded() ) {
          this._focusItem.setExpanded( true );
        } else {
          var item = this._focusItem.getChild( 0 )
          var itemIndex = this._findIndexByItem( item );
          this._handleKeyboardSelect( event, item, itemIndex, true );
        }
      }
    },
    
    _handleKeyboardSelect : function( event, item, itemIndex, suppressMulti ) { 
      this._scrollIntoView( itemIndex );
      if( this._hasMultiSelection && !suppressMulti ) {
        this._multiSelectItem( event, item );
      } else {
        this._singleSelectItem( item );            
      }
    },

    /////////////////
    // render content
    
    _renderItemUpdate : function( item, event ) {
      if( item.isDisplayable() ) {      
        switch( event.getData() ) {
          case "expanded":
          case "collapsed":
            this._scheduleUpdate( true ); 
          break;
          case "add":
          case "remove":
            // NOTE: the added/removed item is a child of this item
            if( item.isExpanded() ) {
              this._scheduleUpdate( true ); 
            } else {
              this._scheduleItemUpdate( item );
            }
          break;
          default:
            if( this._inServerResponse() ) {
              this._scheduleItemUpdate( item );
            } else {
              this._rowContainer.renderItem( item );
            }  
          break;
        }
      }
    },
    
    _scheduleItemUpdate : function( item ) {
      this._renderQueue[ item.toHashCode() ] = item;
      this.addToQueue( "updateRows" );
    },
    
    _scheduleUpdate : function( scrollHeight ) {
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
      if( this._topItem === null ) {
        this._updateTopItem();
      }
      if( changes[ "updateRows" ] ) {
        if( this._renderQueue[ "allItems" ] ) {
          this._rowContainer.renderAll();
        } else {
          this._rowContainer.renderItemQueue( this._renderQueue );
        }
        this._renderQueue = {};
      }
    },

    //////////// 
    // scrolling
    
    _updateScrollHeight : function() {
      var itemCount = this.getRootItem().getVisibleChildrenCount();
      var height = itemCount * this._itemHeight;
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
    
    _updateTopItem : function( oldIndex, render ) {
      if( typeof oldIndex == "number" ) {
        this._topItem = this._findItemByIndex( this._topItemIndex, this._topItem, oldIndex );
      } else {
        this._topItem = this._findItemByIndex( this._topItemIndex );
      }
      this._rowContainer.setTopItem( this._topItem, this._topItemIndex, render );
    },
    
    _updateScrollWidth : function() {
      var width = this._getItemWidth();
      this._rowContainer.setRowWidth( this._getRowWidth() );
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
      var dummyWidth = Math.max( 0, areaWidth - dummyLeft );
      if( this._vertScrollBar.getVisibility() ) {
        dummyWidth = Math.max( dummyWidth, this._vertScrollBar.getWidth() );
      }
      this._dummyColumn.setLeft( dummyLeft );
      this._dummyColumn.setWidth( dummyWidth );
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

    _scrollIntoView : function( index ) {
      if( index < this._topItemIndex ) {
        this.setTopItemIndex( index );
      } else if( index > ( this._topItemIndex + this._rowContainer.getChildrenLength() - 2 ) ) {
        this.setTopItemIndex( index - this._rowContainer.getChildrenLength() + 2 );
      }
    },
    
    setHasScrollBarsSelectionListener : function( value ) {
      this._hasScrollBarsSelectionListener = value;
      if( value ) {
        this._createSendRequestTimer();
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
        this._sendSelectionEvent( item, false, null );
      }
    },

    _sendItemCheckedChange : function( item ) {
      if( !this._inServerResponse() ) {
        var req = org.eclipse.swt.Request.getInstance();
        var wm = org.eclipse.swt.WidgetManager.getInstance();   
        var itemId = wm.findIdByWidget( item );
        req.addParameter( itemId + ".checked", item.isChecked() );
        this._sendSelectionEvent( item, false, "check" );
      }
    },

    _sendTopItemIndexChange : function() {
      var req = org.eclipse.swt.Request.getInstance();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( this );
      req.addParameter( id + ".topItemIndex", this._topItemIndex );
      if( this._isVirtual || this._hasScrollBarsSelectionListener ) {
        this._sendRequestTimer.start();
      }
    },
    
    _sendScrollLeftChange : function() {
      // TODO [tb] : There should be a check for _inServerResponse,
      // but currently this is needed to sync the value with the 
      // server when the scrollbars are hidden by the server. Should be 
      // improved here and in table with new scrollbar implementation.
      var req = org.eclipse.swt.Request.getInstance();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( this );
      req.addParameter( id + ".scrollLeft", this._horzScrollBar.getValue() );
      if( this._isVirtual || this._hasScrollBarsSelectionListener ) {
        this._sendRequestTimer.start();
      }
    },
    
    _sendItemUpdate : function( item, event ) {
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

    _sendSelectionEvent : function( item, defaultSelected, detail ) {
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
        if( detail != null ) {
          req.addParameter( eventName + ".detail", detail );
        }
        req.send();
      }      
    },

    _isDoubleClicked : function( event, item ) {
      var result = false;
      var mousedown = event.getType() === "mousedown";
      var leftClick = event.getButton() === "left";
      if(    leftClick
          && mousedown
          && this.isFocusItem( item ) 
          && this._selectionTimestamp != null ) 
      {
        var stamp = new Date();
        var diff = org.eclipse.swt.EventUtil.DOUBLE_CLICK_TIME;
        if( stamp.getTime() - this._selectionTimestamp.getTime() < diff ) {
          result = true;
        }
      }
      if( mousedown && leftClick && !result ) {
        this._selectionTimestamp = new Date();
      } else if( mousedown ) {
        this._selectionTimestamp = null;
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
      if( event instanceof qx.event.type.MouseEvent && event.isRightButtonPressed() ) {
        if( !this.isItemSelected( item ) ) {
          this._singleSelectItem( item );
        }
      } else if( event.isCtrlPressed() ) {
        if( event instanceof qx.event.type.KeyEvent && item != this._focusItem  ) {
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
      var currentItem = this._leadItem != null ? this._leadItem : this._focusItem;
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
        currentItem = currentItem.getNextItem();
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
        this._rowContainer.renderItem( item );
      }
    },
    
    _deselectItem : function( item, render ) {
      if( this.isItemSelected( item ) ) {
        this._selection.splice( this._selection.indexOf( item ), 1 );
      }
      if( render ) {
        this._rowContainer.renderItem( item );
      }
    },

    _deselectAll : function() {
      var oldSelection = this._selection;
      this._selection = [];
      for( var i = 0; i < oldSelection.length; i++ ) {
        this._rowContainer.renderItem( oldSelection[ i ] );
      }
    },

    _deselectVisibleChildren : function( item ) {
      var currentItem = item.getNextItem();
      var finalItem = item.getNextItem( true );
      while( currentItem !== finalItem ) {
        this._deselectItem( currentItem, false );
        currentItem = currentItem.getNextItem();
      } 
    },

    _applyFocused : function( newValue, oldValue ) {
      this.base( arguments, newValue, oldValue );
      this._config.focused = newValue;
      this._scheduleUpdate();
    },

    _applyEnabled : function( newValue, oldValue ) {
      this.base( arguments, newValue, oldValue );
      this._config.enabled = newValue;
      this._scheduleUpdate();
    },

    _applyTextColor : function( newValue, oldValue ) {
      this.base( arguments, newValue, oldValue );
      this._config.textColor = newValue;
      this._scheduleUpdate();
    },

    _applyFont : function( newValue, oldValue ) {
      this.base( arguments, newValue, oldValue );
      this._config.font = newValue;
      this._scheduleUpdate();
    },

    ////////////////////////////
    // internal layout & theming

    _applyBackgroundColor : function( newValue ) {
      this._rowContainer.setBackgroundColor( newValue );
    },

    _applyBackgroundImage : function( newValue ) {
      this._rowContainer.setBackgroundImage( newValue );
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
      if( this._config.linesVisible ) {
        for( var columnNr = 0; columnNr < this._config.columnCount; columnNr++ ) {
          lineNr = this._renderVerticalGridline( columnNr, lineNr );          
        }
      }
      while( this._vertGridLines.length > lineNr ) {
        this._vertGridLines.pop().destroy();
      }
    },

    _renderVerticalGridline : function( columnNr, lineNr ) {
      var newLineNr = lineNr;
      var clientWidth = this._rowContainer.getWidth();
      var left = this._config.itemLeft[ columnNr ] + this._config.itemWidth[ columnNr ] - 1;
      left -= this._horzScrollBar.getValue(); 
      if( left > 0 && left < clientWidth ) {
        var line = this._getVerticalGridline( lineNr );
        line.setLeft( left );
        line.setTop( this._rowContainer.getTop() );
        line.setHeight( this._rowContainer.getHeight() );
        newLineNr++
      }
      return newLineNr;
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
      var border = this._config.linesVisible ? this._getHorizontalGridBorder() : null;
      this._rowContainer.setRowBorder( border );
    },

    _getHorizontalGridBorder : function() {
      if( this._horzGridBorder === null ) { 
        this._horzGridBorder = this._getGridBorder( { "horizontal" : true } );
      }
      return this._horzGridBorder;
    },

    _getVerticalGridBorder : function() {
      if( this._vertGridBorder === null ) {
        this._vertGridBorder = this._getGridBorder( { "vertical" : true } );
      }
      return this._vertGridBorder;
    },

    _getGridBorder : function( state ) {
      var tvGrid = new org.eclipse.swt.theme.ThemeValues( state );
      var gridColor = tvGrid.getCssColor( "Tree-GridLine", "color" );
      tvGrid.dispose();
      var borderWidths = [ 0, 0, 0, 0 ];
      gridColor = gridColor == "undefined" ? "transparent" : gridColor;
      if( state.horizontal ) {
        borderWidths[ 2 ] = 1;
      } else if( state.vertical ) {
        borderWidths[ 1 ] = 1;
      }
      return new org.eclipse.rwt.Border( borderWidths, "solid", gridColor );
    },

    _layoutX : function() {
      var width = this.getWidth() - this.getFrameWidth();
      if( this._columnArea.getDisplay() ) {
        this._columnArea.setWidth( width );
      }
      if( this._vertScrollBar.getVisibility() ) {
        width -= this._vertScrollBar.getWidth();
        this._vertScrollBar.setLeft( width );
      }
      this._horzScrollBar.setWidth( width );
      this._rowContainer.setWidth( width );
      this._updateScrollWidth();
      this._renderGridVertical(); // TODO [tb] : optimize calls
    },

    _layoutY : function() {
      var height = this.getHeight() - this.getFrameHeight();
      var top = 0;
      if( this._columnArea.getDisplay() ) {
        top = this._headerHeight;
        height -= this._headerHeight;
        this._columnArea.setHeight( this._headerHeight );
      }
      if( this._horzScrollBar.getVisibility() ) {
        height -= this._horzScrollBar.getHeight();
        this._horzScrollBar.setTop( top + height );
      }
      height = Math.max( 0, height );
      this._vertScrollBar.setHeight( height );
      this._vertScrollBar.setTop( top );
      this._rowContainer.setTop( top );
      this._rowContainer.setHeight( height );
      this._renderGridVertical();
      this._scheduleUpdate();
    },

    _getItemWidth : function() {
      var result = 0;
      if( this._config.itemLeft.length > 0 ) {
        var columnCount = Math.max( 1, this._config.columnCount );
        for( var i = 0; i < columnCount; i++ ) {
          result = Math.max( result, this._config.itemLeft[ i ] + this._config.itemWidth[ i ] );
        }
      }
      return result;
    },

    _getRowWidth : function() {
      var width = this._rowContainer.getWidth()
      var result = Math.max( this._getItemWidth(), width );
      return result;
    },

    ///////////////
    // model-helper

    // TODO [tb] : can this be optimized to create less virtual placeholder items? 
    _findItemByIndex : function( index, startItem, startIndex ) {
      var result;
      var computedStartItem = startItem ? startItem : this.getRootItem().getChild( 0 );
      var computedStartIndex = startIndex ? startIndex : 0;
      if( index >= computedStartIndex ) {
        result = this._findItemByIndexForwards( index, computedStartItem, computedStartIndex );
      } else {
        result = this._findItemByIndexBackwards( index, computedStartItem, computedStartIndex );
      }
      return result;
    },

    _findItemByIndexForwards : function( index, startItem, startIndex ) {
      var i = startIndex;
      var item = startItem;
      while( i != index && item != null ) {
        var siblingIndex = i + item.getVisibleChildrenCount() + 1;
        if( siblingIndex <= index ) {
          i = siblingIndex;
          item = item.getNextItem( true );
        } else {
          item = item.getNextItem();
          i++;
        } 
      }
      return item;
    },

    _findItemByIndexBackwards : function( index, startItem, startIndex ) {
      var i = startIndex;
      var item = startItem;
      while( i != index && item != null ) {
        if( item.hasPreviousSibling() ) {
          var previous = item.getPreviousSibling();
          var prevSiblingIndex = i - ( previous.getVisibleChildrenCount() + 1 );
          if( prevSiblingIndex >= index ) {
            i = prevSiblingIndex;
            item = previous;              
          } else {
            item = item.getPreviousItem();
            i--;
          }
        } else {
          item = item.getPreviousItem();
          i--;
        }
      }
      return item;
    },

    _findIndexByItem : function( item ) {
      if( this._topItem === null ) {
        this._updateTopItem();
      }
      var forwardsItem = this._topItem;
      var backwardsItem = this._topItem;
      var forwardsIndex = this._topItemIndex;
      var backwardsIndex = this._topItemIndex;
      while( forwardsItem !== item && backwardsItem !== item ) {
        if( forwardsItem != null ) {
          forwardsItem = forwardsItem.getNextItem();
          forwardsIndex++;
        }
        if( backwardsItem != null ) {
          backwardsItem = backwardsItem.getPreviousItem();
          backwardsIndex--;
        }
        if( backwardsItem === null && forwardsItem === null ) {
          throw "Tree._findIndexByItem failed!";
        }
      }
      var result;
      if( forwardsItem === item ) {
        result = forwardsIndex;
      } else {
        result = backwardsIndex;
      }
      return result;
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

    //////////////
    // misc helper

    _inServerResponse : function() {
      return org.eclipse.swt.EventUtil.getSuspended();      
    },

    _isDragSource : function() {
      return this.hasEventListeners( "dragstart" ); 
    },

    ////////////////////////
    // Cell tooltip handling

    setEnableCellToolTip : function( value ) {
      if( value ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        this._cellToolTip = new org.eclipse.swt.widgets.TableCellToolTip( id );
        this._rowContainer.addEventListener( "mousemove", this._onClientAreaMouseMove, this );
        this._rowContainer.setToolTip( this._cellToolTip );
      } else {
        this._rowContainer.removeEventListener( "mousemove", this._onClientAreaMouseMove, this );
        this._rowContainer.setToolTip( null );
        this._cellToolTip.destroy();
        this._cellToolTip = null;
      }
    },

    _onClientAreaMouseMove : function( evt ) {
      if( this._cellToolTip != null ) {
        var pageX = evt.getPageX();
        var pageY = evt.getPageY();
        if( this._rowContainer.getHoverItem() ) {
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var itemId = widgetManager.findIdByWidget( this._rowContainer.getHoverItem() );
          var columns = this.getColumns();
          var columnIndex = columns.length == 0 ? 0 : -1;
          var element = this._rowContainer.getElement();
          var leftOffset = qx.bom.element.Location.getLeft( element );
          for( var i = 0; columnIndex == -1 && i < columns.length; i++ ) {
            var pageLeft = leftOffset + this._config.itemLeft[ i ];
            if( pageX >= pageLeft && pageX < pageLeft + this._config.itemWidth[ i ] ) {
              columnIndex = i;
            }
          }
          this._cellToolTip.setCell( itemId, columnIndex );
        }
      }
    },

    /** Only called by server-side */
    setCellToolTipText : function( text ) {
      if( this._cellToolTip != null ) {
        this._cellToolTip.setText( text );
      }
    }
    
  }
} );