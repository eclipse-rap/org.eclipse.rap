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
 * org.eclipse.rap.rwt.Table.
 * @event itemselected
 */
qx.OO.defineClass(
  "org.eclipse.rap.rwt.widgets.Table",
  qx.ui.layout.CanvasLayout,
  function( id ) {
    qx.ui.layout.CanvasLayout.call( this );
    this.setAppearance( "table" );
    // TODO [rh] this is preliminary and can be removed once a tabOrder is 
    //      available
    this.setTabIndex( 1 );
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    // Draw grid lines?
    this._linesVisible = false;
    // Internally used fields for resizing
    this._resizeStartX = 0;
    this._resizeColumn = null;
    // Internally used fields to manage visible rows and scrolling
    this._topIndex = 0;
    this._itemHeight = 0;
    this._rows = new Array();
    this._items = new Array();
    // Conains all item which are currently selected
    this._selected = new Array();
    // An item only used to draw the area where no actual items are but that
    // needs to be drawn since the table bounds are grater than the number of
    // items
    this._emptyItem = new org.eclipse.rap.rwt.widgets.TableItem( this, -1 );
    //
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    // Construct a column area where columns go can be scrolled in
    this._columnArea = new qx.ui.layout.CanvasLayout();
    this._columnArea.setTop( 0 );
    this._columnArea.setLeft( 0 );
    this._columnArea.addEventListener( "mousemove", this._onColumnAreaMouseMove, this );
    this._columnArea.addEventListener( "mouseout", this._onColumnAreaMouseOut, this );
    this._columnArea.addEventListener( "mousedown", this._onColumnAreaMouseDown, this );
    this._columnArea.addEventListener( "mouseup", this._onColumnAreaMouseUp, this );
    this.add( this._columnArea ); 
    // Vertical line that is shown while columns are resized
    this._resizeLine = null;
    // Construct client area in which the table items will live
    this._clientArea = new qx.ui.layout.CanvasLayout();
    this._clientArea.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    this._clientArea.setTop( 20 );
    this._clientArea.setLeft( 0 );
    this._clientArea.addEventListener( "mousewheel", this._onClientAreaMouseWheel, this );
    this._clientArea.addEventListener( "click", this._onClientAreaClick, this );
    this._clientArea.addEventListener( "appear", this._onClientAppear, this );
    // Create horizontal scrollBar
    this._horzScrollBar = new qx.ui.core.ScrollBar( true );
    this._horzScrollBar.setMergeEvents( true );
    this.add( this._horzScrollBar );
    this._horzScrollBar.setHeight( this._horzScrollBar.getPreferredBoxHeight() );
    this._horzScrollBar.addEventListener( "changeValue", this._onHorzScrollBarChangeValue, this );
    // Create vertical scrollBar
    this._vertScrollBar = new qx.ui.core.ScrollBar( false );
    this._vertScrollBar.setMergeEvents( true );
    this.add( this._vertScrollBar );
    this._vertScrollBar.setWidth( this._vertScrollBar.getPreferredBoxWidth() );
    this._vertScrollBar.addEventListener( "changeValue", this._onVertScrollBarChangeValue, this );
    // Listen to size changes to adjust client area size    
    this.addEventListener( "changeWidth", this._onChangeSize, this );
    this.addEventListener( "changeHeight", this._onChangeSize, this );
    this.addEventListener( "changeEnabled", this._onChangeEnabled, this );
    // Listen to send event of request to report current state
    var req = org.eclipse.rap.rwt.Request.getInstance();
    widgetManager.add( this._clientArea, id + "_clientArea",  false );
    this.add( this._clientArea );
  }
);

qx.OO.addProperty( { name: "font", 
                     type : "object", 
                     instance : "qx.renderer.font.Font", 
                     convert : qx.renderer.font.FontCache, 
                     allowMultipleArguments : true } );

qx.Proto._modifyFont = function( propValue, propOldValue, propData )
{
  if( propValue ) {
    propValue._applyWidget( this );
  } else if( propOldValue ) {
    propOldValue._resetWidget( this );
  }
  return true;
}

qx.Class.RESIZE_LINE_COLOR = "#D6D5D9";

qx.Class.CURSOR_RESIZE_HORIZONTAL 
  = (    qx.core.Client.getInstance().isGecko() 
      && (    qx.core.Client.getInstance().getMajor() > 1 
           || qx.core.Client.getInstance().getMinor() >= 8 ) )
  ? "ew-resize" 
  : "e-resize";

qx.Proto.dispose = function() {
  if( this.isDisposed() ) {
    return;
  }
  this.removeEventListener( "changeWidth", this._onChangeSize, this );
  this.removeEventListener( "changeHeight", this._onChangeSize, this );
  this.removeEventListener( "changeEnabled", this._onChangeEnabled, this );
  this._emptyItem.dispose();
  var req = org.eclipse.rap.rwt.Request.getInstance();
  if( this._horzScrollBar ) {
    this._horzScrollBar.removeEventListener( "changeValue", this._onHorzScrollBarChangeValue, this );
    this._horzScrollBar.dispose();
    this._horzScrollBar = null;
  }
  if( this._vertScrollBar )  {
    this._vertScrollBar.removeEventListener( "changeValue", this._onVertScrollBarChangeValue, this );
    this._vertScrollBar.dispose();
    this._vertScrollBar = null;
  }
  if( this._clientArea ) {
    this._clientArea.removeEventListener( "mousewheel", this._onClientAreaMouseWheel, this );
    this._clientArea.removeEventListener( "click", this._onClientAreaClick, this );
    this._clientArea.removeEventListener( "appear", this._onClientAppear, this );
    this._clientArea.dispose();
    this._clientArea = null;
  }
  if( this._columnArea ) {
    this._columnArea.removeEventListener( "mousemove", this._onColumnAreaMouseMove, this );
    this._columnArea.removeEventListener( "mousedown", this._onColumnAreaMouseDown, this );
    this._columnArea.removeEventListener( "mouseup", this._onColumnAreaMouseUp, this );
    this._columnArea.dispose();
    this._columnArea = null;
  }
  if( this._resizeLine ) {
    this._resizeLine.setParent( null );
    this._resizeLine.dispose();
  }
  if( this._rows ) {
    for( var i = 0; i < this._rows.length; i++ ) {
      this._rows[ this._rows.length - 1 ].dispose();
    }
    this._rows = null;
  }
  return qx.ui.layout.CanvasLayout.prototype.dispose.call( this );  
}

qx.Proto.setHeaderHeight = function( value ) {
  this._columnArea.setHeight( value );
  var columns = this._columnArea.getChildren();
  for( var i = 0; i < columns.length; i++ ) {
    columns[ i ].setHeight( value );
  }
  this._updateClientAreaSize();
}

qx.Proto.setHeaderVisible = function( value ) {
  this._columnArea.setVisibility( value );  
  this._topIndex = 0;
  this._vertScrollBar.setValue( 0 );
  this._horzScrollBar.setValue( 0 );
  this._updateClientAreaSize();
  this._updateRowCount();
  this._updateRows();  
}

qx.Proto.setItemHeight = function( value ) {
  this._itemHeight = value;  
  this._updateScrollHeight();
  if( this._updateRowCount() ) {
    this._updateRows();    
  }
}

qx.Proto.setTopIndex = function( value ) {
  var scrollPos = value * this._itemHeight;
  this._vertScrollBar.setValue( scrollPos );
  this._internalSetTopIndex( value );
}

qx.Proto._internalSetTopIndex = function( value ) {
  if( this._topIndex != value ) {
    this._topIndex = value;
    this._updateRows();
  }
}

qx.Proto.getColumn = function( index ) {
  return this._columnArea.getChildren()[ index ];  
}

qx.Proto.getColumnCount = function() {
  return this._columnArea.getChildrenLength();
}

qx.Proto.getColumnsWidth = function() {
  var result = 0;
  var columns = this._columnArea.getChildren();
  for( var i = 0; i < columns.length; i++ ) {
    result += columns[ i ].getWidth();
  }
  return result;
}

qx.Proto.setLinesVisible = function( value ) {
  this._linesVisible = value;
  this._updateRows();
}

qx.Proto.getLinesVisible = function() {
  return this._linesVisible;
}

/** 
 * Select all items that are specified in the given value. Value is an array
 * of int's that denote the indices of items to be selected
 */
qx.Proto.setSelection = function( value ) {
  while( this._selected.length > 0 ) {
    this._unselectItem( this._selected[ 0 ] );
  }
  for( var i = 0; i < value.length; i++ ) {
    this._selectItem( this._items[ value[ i ] ] );
  }
}

/////////////////////////////////
// React when enabled was changed

qx.Proto._onChangeEnabled = function( evt ) {
  var enabled = this.getEnabled();
  this._horzScrollBar.setEnabled( enabled );
  this._vertScrollBar.setEnabled( enabled );
  this._columnArea.setEnabled( enabled );
  var columns = this._columnArea.getChildren();
  for( var i = 0; i < columns.length; i++ ) {
    columns[ i ].setEnabled( enabled );
  }
  this._updateRows();
}

//////////////////////////////////////////////////////////
// Mouse listeners for column area - control column resize

qx.Proto._onColumnAreaMouseMove = function( evt ) {
  if( this._resizeColumn == null ) {
    var resizeColumn = this._getResizeColumn( evt.getPageX() );
    if( resizeColumn == null ) {
      this.getTopLevelWidget().setGlobalCursor( null );
    } else {
      this.getTopLevelWidget().setGlobalCursor( org.eclipse.rap.rwt.widgets.Table.CURSOR_RESIZE_HORIZONTAL );    
    }
  } else {
    var absColumnLeft 
      = this._resizeColumn.getLeft() 
      + this._resizeColumn.getParent().getLeft();
    var position = absColumnLeft + this._getResizeColumnWidth( evt.getPageX() )
    // min column width is 5 px
    if( position < absColumnLeft + 5 ) { 
      position = absColumnLeft + 5 ;
    }
    this._showResizeLine( position );
  }
}

qx.Proto._onColumnAreaMouseOut = function( evt ) {
  if( this._resizeColumn == null ) {
    this.getTopLevelWidget().setGlobalCursor( null );    
  }
}

qx.Proto._onColumnAreaMouseDown = function( evt ) {
  this._resizeColumn = this._getResizeColumn( evt.getPageX() );
  if( this._resizeColumn != null ) {
    var position = this._resizeColumn.getLeft() + this._resizeColumn.getWidth();
    this._showResizeLine( position );
    this._resizeStartX = evt.getPageX();
    this._columnArea.setCapture( true );
  }
}

qx.Proto._onColumnAreaMouseUp = function( evt ) {
  if( this._resizeColumn != null ) {
    this._hideResizeLine();
    this.getTopLevelWidget().setGlobalCursor( null );
    this._columnArea.setCapture( false );
    this._updateRows();
    this._doResizeColumn( this._resizeColumn, this._getResizeColumnWidth( evt.getPageX() ) );
    this._resizeColumn = null; 
  }
}

////////////////////////////
// Listeners for client area

qx.Proto._onClientAreaClick = function( evt ) {
  var target = evt.getTarget();
  if( target instanceof qx.ui.embed.HtmlEmbed ) {
    var rowIndex = this._clientArea.indexOf( target );
    var itemIndex = this._topIndex + rowIndex;
    if( itemIndex >= 0 && itemIndex < this._items.length ) {
      this._selectItem( this._items[ itemIndex ] );
      this._updateSelectionParam();
      this.createDispatchDataEvent( "itemselected", this._items[ itemIndex ] );
    }
  }
}

qx.Proto._updateSelectionParam = function() {
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var tableId = widgetManager.findIdByWidget( this );
  var req = org.eclipse.rap.rwt.Request.getInstance();
  var selectedIds = "";
  for( var i = 0; i < this._selected.length; i++ ) {
    var itemId = widgetManager.findIdByWidget( this._selected[ i ] );
    if( selectedIds != "" ) {
      selectedIds += ",";
    }
    selectedIds += itemId;
  }
  req.addParameter( tableId + ".selection", selectedIds );  
}

qx.Proto._onClientAreaMouseWheel = function( evt ) {
  var change = evt.getWheelDelta() * this._itemHeight * 2;
  this._vertScrollBar.setValue( this._vertScrollBar.getValue() - change );
}

qx.Proto._onChangeSize = function( evt ) {
  this._updateClientAreaSize();
  this._updateRowCount();
  this._updateRows();
}

qx.Proto._onClientAppear = function( evt ) {
  this._updateRowCount();
  this._updateRows();
}

////////////////////////
// Scroll bar listeners

qx.Proto._onVertScrollBarChangeValue = function() {
  // Calculate new topIndex
  var newTopIndex = 0;
  if( this._itemHeight != 0 ) {
    var scrollTop 
      = this._clientArea.isCreated() ? this._vertScrollBar.getValue() : 0;
    newTopIndex = Math.floor( scrollTop / this._itemHeight );
  }
  // update topIndex request parameter
  if( newTopIndex != this._topIndex ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( this );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addParameter( id + ".topIndex", this._topIndex );
  }
  // set new topIndex -> rows are updateded if necessary
  this._internalSetTopIndex( newTopIndex );  
}

qx.Proto._onHorzScrollBarChangeValue = function() {
  var value = this._horzScrollBar.getValue();
  this._columnArea.setLeft( 0 - value );
  for( var i = 0; i < this._rows.length; i++ ) {
    this._rows[ i ].setLeft( 0 - value );
  }
}

///////////////////////
// TableItem management

qx.Proto._addItem = function( item, index ) {
  // TODO [rh] see if this can be optimized
  this._items = qx.lang.Array.insertAt( this._items, item ,index ); 
  this._updateScrollHeight();
  if( this._isItemVisible( item ) ) {
    this._updateRows();
  }
}

qx.Proto._removeItem = function( item ) {
  var wasItemVisible = this._isItemVisible( item );
  qx.lang.Array.remove( this._items, item );
  this._unselectItem( item );
  this._updateScrollHeight();
  if( wasItemVisible ) {
    this._updateRows();
  }
}

qx.Proto._selectItem = function( item ) {
  // TODO [rh] fix this once multi-selection is implemented
  if( this._selected.length > 0 ) {
    this._unselectItem( this._selected[ 0 ] );
  }
  // end of hack
  this._selected.push( item );
  this._updateItem( item );
}

qx.Proto._unselectItem = function( item ) {
  qx.lang.Array.remove( this._selected, item );
  this._updateItem( item );
}

qx.Proto._isItemSelected = function( item ) {
  return qx.lang.Array.contains( this._selected, item );
}

qx.Proto._isItemVisible = function( item ) {
  var itemIndex = this._items.indexOf( item );
  return itemIndex >= this._topIndex && itemIndex <= this._topIndex + this._rows.length;
}

qx.Proto._updateItem = function( item ) {
  var row = this._getRowFromItem( item );
  if( row != null ) {
    this._updateRow( row, item );
  }
}

qx.Proto._getRowFromItem = function( item ) {
  var result = null;
  var itemIndex = this._items.indexOf( item );
  if(    itemIndex >= this._topIndex 
      && itemIndex <= this._topIndex + this._rows.length )
  {
    result = this._rows[ itemIndex - this._topIndex ];
  }
  return result;
}

/////////////////////////
// TableColumn management

qx.Proto._addColumn = function( column ) {
  column.setHeight( this._columnArea.getHeight() );
  column.addEventListener( "changeLeft", this._onColumnChangeSize, this );
  column.addEventListener( "changeWidth", this._onColumnChangeSize, this );
  this._columnArea.add( column );
  this._updateScrollWidth();
}

qx.Proto._removeColumn = function( column ) {
  this._columnArea.remove( column );
  column.removeEventListener( "changeLeft", this._onColumnChangeSize, this );
  column.removeEventListener( "changeWidth", this._onColumnChangeSize, this );
  this._updateScrollWidth();
  this._updateRows();
}

qx.Proto._onColumnChangeSize = function( evt ) {
  this._updateScrollWidth();
  this._updateRows();
}

///////////////////////////////////////////////////////////
// Event handling methods - added and remove by server-side

qx.Proto.onItemSelected = function( evt ) {
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var id = widgetManager.findIdByWidget( evt.getData() );
  org.eclipse.rap.rwt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
}

///////////////////////////////////////////
// UI Update upon scroll, size changes, etc

qx.Proto._updateScrollHeight = function() {
  this._vertScrollBar.setMaximum( this._items.length * this._itemHeight );
}

qx.Proto._updateScrollWidth = function() {
  var width = this.getColumnsWidth();
  this._horzScrollBar.setMaximum( width );
}

qx.Proto._updateClientAreaSize = function() {
  var top = 0;
  if( this._columnArea.getVisibility() ) {
    top = this._columnArea.getHeight();
  }
  var clientHeight = this.getHeight() - top - this._horzScrollBar.getHeight();
  var clientWidth = this.getWidth() - this._vertScrollBar.getWidth();
  //
  this._columnArea.setWidth( clientWidth );
  // vertical scrollBar
  this._vertScrollBar.setLeft( this.getWidth() - this._vertScrollBar.getWidth() );
  this._vertScrollBar.setTop( top );
  this._vertScrollBar.setHeight( clientHeight )
  // horizontal scrollBar
  this._horzScrollBar.setLeft( 0 );
  this._horzScrollBar.setTop( this.getHeight() - this._horzScrollBar.getHeight() );
  this._horzScrollBar.setWidth( clientWidth );
  // client area
  this._clientArea.setTop( top );
  this._clientArea.setHeight( clientHeight );
  this._clientArea.setWidth( clientWidth );
  // Adjust number of rows and update rows if necessary
  if( this._updateRowCount() ) {
    this._updateRows();
  }
}

qx.Proto._updateRowCount = function() {
  var result = false;
  if( this._clientArea.isCreated() ) {
    var newRowCount = 0;
    if( this._itemHeight != 0 ) {
      newRowCount = Math.ceil( this._clientArea.getHeight() / this._itemHeight );
    }
    if( newRowCount != this._rows.length ) {
      // Remove trailing rows if rowCount was decreased
      while( this._rows.length > newRowCount ) {
        var row = this._rows.shift();
        row.setParent( null );
        row.dispose();
      }
      // Append rows if rowCount was increased
      while( this._rows.length < newRowCount ) {
        var newRow = new qx.ui.embed.HtmlEmbed();
        newRow.setWrap( false );
        newRow.setHeight( this._itemHeight );
        newRow.setWidth( 1000 );
        this._clientArea.add( newRow );
        this._rows.push( newRow );
      }
      result = true;
    }
  }
  return result;
}

qx.Proto._updateRows = function() {
  var top = 0;
  for( var i = 0; i < this._rows.length; i++ ) {
    var row = this._rows[ i ];
    var item = null;
    var itemIndex = this._topIndex + i;
    if( itemIndex < this._items.length ) {    
      item = this._items[ itemIndex ];
    }
    row.setTop( top );
    this._updateRow( row, item );
    top += this._itemHeight;    
  }
}

qx.Proto._updateRow = function( row, item ) {
  var isItemSelected = false;
  if( item != null ) {    
    isItemSelected = this._isItemSelected( item );
    row.setHtml( item._getMarkup() );
  } else {
    row.setHtml( this._emptyItem._getMarkup() );
  }
  if( this.getEnabled() ) {
    if( isItemSelected ) {
      row.setStyleProperty( "backgroundColor", "#316ac5" );
    } else {
      row.removeStyleProperty( "backgroundColor" );
    }
  } else {
    row.setStyleProperty( "backgroundColor", "gray" );
  }
}

////////////////////////////////////
// Helping methods for column resize

qx.Proto._getResizeColumn = function( pageX ) {
  var result = null;
  var columnAreaX = qx.html.Location.getClientBoxLeft( this._columnArea.getElement() );  
  var columns = this._columnArea.getChildren();
  for( var i = 0; result == null && i < columns.length; i++ ) {
    var columnRight
      = qx.html.Location.getClientBoxLeft( columns[ i ].getElement() )
      + columns[ i ].getWidth();
    if( pageX >= columnRight - 5 && pageX <= columnRight ) {
      result = columns[ i ];
    }
  }
  return result;
}

// Returns the width of the column that is currently being resized
qx.Proto._getResizeColumnWidth = function( pageX ) {
  var delta = this._resizeStartX - pageX;
  return this._resizeColumn.getWidth() - delta;
}

qx.Proto._doResizeColumn = function( column, width ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( column );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addEvent( "org.eclipse.rap.rwt.events.controlResized", id );
    req.addParameter( id + ".width", width );
    req.send();
  }
}

qx.Proto._showResizeLine = function( x ) {
  var resizeLine = this._resizeLine;
  if( resizeLine == null ) {
    resizeLine = new qx.ui.basic.Terminator();
    resizeLine.setBackgroundColor( org.eclipse.rap.rwt.widgets.Table.RESIZE_LINE_COLOR );
    resizeLine.setWidth( 3 );
    this.add( resizeLine );
    qx.ui.core.Widget.flushGlobalQueues();
    this._resizeLine = resizeLine;
  }
  resizeLine._applyRuntimeLeft( x - 2 ); // -1 for the width
  resizeLine._applyRuntimeHeight( this.getHeight() );
  resizeLine.removeStyleProperty( "visibility" );
}

qx.Proto._hideResizeLine = function() {
  this._resizeLine.setStyleProperty( "visibility", "hidden" );
}
