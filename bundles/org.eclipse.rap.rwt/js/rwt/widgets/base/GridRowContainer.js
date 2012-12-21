/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "rwt.widgets.base.GridRowContainer", {
  extend : rwt.widgets.base.VerticalBoxLayout,

  construct : function() {
    this.base( arguments );
    this.setOverflow( "hidden" );
    this._scrollLeft = 0;
    this._rowHeight = 16;
    this._rowWidth = 0;
    this._horzGridBorder = null;
    this._rowBorder = null;
    this._baseAppearance = null;
    this._topItem = null;
    this._vertGridLines = [];
    this._vertGridBorder = null;
    this._renderTime = null;
    this._topItemIndex = 0;
    this._items = [];
    this._asyncQueue = {};
    this._asyncTimer = new rwt.client.Timer( 0 );
    this._asyncTimer.addEventListener( "interval", this._onAsyncTimer, this );
    this._hoverItem = null;
    this._hoverElement = null;
    this._config = rwt.widgets.base.GridRowContainer.createRenderConfig();
    this.addEventListener( "mouseover", this._onRowOver, this );
    this.addEventListener( "mouseout", this._onRowOver, this );
    this.addEventListener( "elementOver", this._onElementOver, this );
  },

  destruct : function() {
    this._rowBorder = null;
    this._topItem = null;
    this._renderTime = null;
    this._items = null;
    this._hoverItem = null;
    this._hoverElement = null;
    this._asyncTimer.dispose();
    this._asyncTimer = null;
  },

  statics : {

    createRenderConfig : function() {
      var result = {
        "textColor" : null,
        "font" : null,
        "enabled" : true,
        "focused" : false,
        "linesVisible" : false,
        "fullSelection" : false,
        "hideSelection" : false,
        "alwaysHideSelection" : false,
        "variant" : null,
        "selectionPadding" : null,
        "indentionWidth" : 16,
        "hasCheckBoxes" : false,
        "checkBoxLeft" : null,
        "checkBoxWidth" : null,
        "columnCount" : 0,
        "treeColumn" : 0,
        "alignment" : [],
        "itemLeft" : [],
        "itemWidth" : [],
        "itemImageLeft" : [],
        "itemImageWidth" : [],
        "itemTextLeft" : [],
        "itemTextWidth" : [],
        "itemCellCheck" : [],
        "itemCellCheckLeft" : [],
        "itemCellCheckWidth" : []
      };
      return result;
    }

  },

  members : {

    /////////////
    // Public API

    /**
     * Returns a map with values for treeRow configuration. (see _createRenderConfig).
     * Will not be changed by TreeRow or TreeRowContainer. When doing changes renderAll must
     * be called for them take effect.
     */
    getRenderConfig : function() {
      return this._config;
    },

    /**
     * Calls this function after each complete rendering with the renderTime in ms.
     */
    setPostRenderFunction : function( func, context ) {
      this._postRender = [ func, context ];
    },

    /**
     * Calls this function with an item as the parameter. Expects a boolean as return value.
     */
    setSelectionProvider : function( func, context ) {
      this._selectionProvider = [ func, context ];
    },

    setBaseAppearance : function( value ) {
      this._baseAppearance = value;
    },

    // TODO [tb] : the rest of the setters could be refactored to "update" functions using _config.

    setRowWidth : function( width ) {
      this._rowWidth = width;
      for( var i = 0; i < this._children.length; i++ ) {
        this._children[ i ].setWidth( width );
      }
    },

    setRowHeight : function( height ) {
      this._rowHeight = height;
      for( var i = 0; i < this._children.length; i++ ) {
        this._children[ i ].setHeight( height );
      }
      this._updateRowCount();
    },

    updateRowLines : function() {
      var border = this._config.linesVisible ? this._getHorizontalGridBorder() : null;
      this._rowBorder = border;
      for( var i = 0; i < this._children.length; i++ ) {
        this._children[ i ].setBorder( border );
        this._children[ i ].setState( "linesvisible", this._config.linesVisible );
      }
    },

    _renderGridVertical : function() {
      var linesNeeded = this._config.linesVisible ? this._config.columnCount : 0;
      for( var i = 0; i < linesNeeded; i++ ) {
        this._renderVerticalGridline( i );
      }
      for( var i = linesNeeded; i < this._vertGridLines.length; i++ ) {
        this._removeGridLine( i );
      }
    },

    _renderVerticalGridline : function( column ) {
      var width = this._config.itemWidth[ column ];
      var left = this._config.itemLeft[ column ] + width - 1;
      if( width > 0 ) {
        var line = this._getVerticalGridline( column );
        line.style.left = left + "px";
        line.style.height = this.getHeight() + "px";
      } else {
        this._removeGridLine( column );
      }
    },

    _getVerticalGridline : function( column ) {
      if( typeof this._vertGridLines[ column ] === "undefined" ) {
        var line = document.createElement( "div" );
        line.style.zIndex = 1;
        line.style.position = "absolute";
        line.style.top = "0px";
        line.style.width = "0px";
        this._getVerticalGridBorder().renderElement( line );
        if( this._isCreated ) {
          this._getTargetNode().appendChild( line );
        } else {
          this.addEventListener( "appear", function() {
            this._getTargetNode().appendChild( line );
          }, this );
        }
        this._vertGridLines[ column ] = line;
      }
      return this._vertGridLines[ column ];
    },

    _removeGridLine : function( column ) {
      if( this._vertGridLines[ column ] ) {
        this._getTargetNode().removeChild( this._vertGridLines[ column ] );
        delete this._vertGridLines[ column ];
      }
    },

    _getVerticalGridBorder : function() {
      if( this._vertGridBorder === null ) {
        this._vertGridBorder = this._getGridBorder( { "vertical" : true } );
      }
      return this._vertGridBorder;
    },

    _getHorizontalGridBorder : function() {
      if( this._horzGridBorder === null ) {
        this._horzGridBorder = this._getGridBorder( { "horizontal" : true } );
      }
      return this._horzGridBorder;
    },

    _getGridBorder : function( state ) {
      var tvGrid = new rwt.theme.ThemeValues( state );
      var cssElement = rwt.util.Strings.toFirstUp( this._baseAppearance ) + "-GridLine";
      var gridColor = tvGrid.getCssColor( cssElement, "color" );
      tvGrid.dispose();
      var borderWidths = [ 0, 0, 0, 0 ];
      gridColor = gridColor == "undefined" ? "transparent" : gridColor;
      if( state.horizontal ) {
        borderWidths[ 2 ] = 1;
      } else if( state.vertical ) {
        borderWidths[ 1 ] = 1;
      }
      return new rwt.html.Border( borderWidths, "solid", gridColor );
    },

    _getRowAppearance : function() {
      return this._baseAppearance + "-row";
    },

    setTopItem : function( item, index, render ) {
      // TODO [tb] : write test for optimized render
      this._topItem = item;
      if( render ) {
        var delta = index - this._topItemIndex;
        this._topItemIndex = index;
        var forwards = delta > 0;
        delta = Math.abs( delta );
        if( delta >= this._children.length ) {
          this._renderAll( true );
        } else {
          var numberOfShiftingRows = this._children.length - delta;
          var updateFromRow = forwards ? numberOfShiftingRows : 0;
          var newFirstRow = forwards ? delta : numberOfShiftingRows;
          this._switchRows( newFirstRow );
          this._updateRows( updateFromRow, delta, true );
          this._renderBounds( true );
        }
      } else {
        this._topItemIndex = index;
      }
    },

    renderAll : function() {
      this._renderAll( false );
    },

    renderItemQueue : function( queue ) {
      for( var key in queue ) {
        var item = queue[ key ];
        var index = this._items.indexOf( item );
        if( index !== -1 ) {
          this._renderRow( this._children[ index ], item );
        }
      }
    },

    renderItem : function( item ) {
      if( this._isCreated && item != null ) {
        var row = this._findRowByItem( item );
        if( row!= null ) {
          this._renderRow( row, item );
        }
      }
    },

    setScrollLeft : function( value ) {
      this._scrollLeft = value;
      if( this.isSeeable() ) {
        this.base( arguments, value );
      }
    },

    findItemByRow : function( targetRow ) {
      var index = this._children.indexOf( targetRow );
      return index !== -1 ? this._items[ index ] : null;
    },

    getHoverItem : function() {
      return this._hoverItem;
    },

    // NOTE: Used only by TreeUtil.js
    setHoverItem : function( item ) {
      if( item ) {
        this._hoverElement = [ "other" ];
      }
      this._setHoverItem( item );
    },

    ////////////
    // Internals

    _renderAll : function( contentOnly ) {
      if( !contentOnly ) {
        this._renderGridVertical();
      }
      var start = ( new Date() ).getTime();
      this._updateRows( 0, this._children.length, contentOnly );
      this._renderBounds();
      if( this._postRender ) {
        var postRender = this._postRender;
        window.setTimeout( function() {
          var renderTime = ( new Date() ).getTime() - start;
          if( !postRender[ 1 ].isDisposed() ) {
            postRender[ 0 ].call( postRender[ 1 ], renderTime );
          }
        }, 0 );
      }
    },

    _updateRowCount : function() {
      var height = this.getHeight();
      var rowsNeeded = Math.round( ( this.getHeight() / this._rowHeight ) + 0.5 );
      while( this._children.length < rowsNeeded ) {
        var row = new rwt.widgets.base.GridRow( this.getParent() );
        row.setAppearance( this._getRowAppearance() );
        row.setZIndex( 0 );
        row.setWidth( this._rowWidth );
        row.setHeight( this._rowHeight );
        row.setBorder( this._rowBorder );
        row.setState( "linesvisible", this._config.linesVisible );
        this.add( row );
      }
      while( this._children.length > rowsNeeded ) {
        this._children[ this._children.length - 1 ].destroy();
      }
      this._items.length = this._children.length;
      this._updateRowsEvenState();
    },

    _updateRowsEvenState: function() {
      for( var i = 0; i < this._children.length; i++ ) {
        this._children[ i ].updateEvenState( this._topItemIndex + i );
      }
    },

    _findRowByItem : function( targetItem ) {
      var index = this._items.indexOf( targetItem );
      return index !== -1 ? this._children[ index ] : null;
    },

    _updateRows : function( from, delta, contentOnly ) {
      this._updateRowsEvenState();
      var item = this._topItem;
      var to = from + delta;
      var row = 0;
      while( item != null && row < this._children.length ) {
        if( row >= from && row <= to ) {
          this._renderRow( this._children[ row ], item, contentOnly );
          this._items[ row ] = item;
        }
        item = item.getNextItem();
        row++;
      }
      for( var i = row; i < this._children.length; i++ ) {
        this._renderRow( this._children[ i ], null, contentOnly );
        this._items[ i ] = null;
      }
    },

    _renderRow : function( row, item, contentOnly ) {
       row.renderItem( item,
                       this._config,
                       this._isSelected( item ),
                       this._getHoverElement( item ),
                       contentOnly );
    },

    _switchRows : function( newFirstRow ) {
      var rowTemp = this._children.slice( newFirstRow );
      var itemsTemp = this._items.slice( newFirstRow );
      this._children = rowTemp.concat( this._children.slice( 0, newFirstRow ) );
      this._items = itemsTemp.concat( this._items.slice( 0, newFirstRow ) );
      this._invalidateVisibleChildren();
    },

    _renderBounds : function( renderLocation ) {
      if( renderLocation ) {
        for( var i = 0; i < this._children.length; i++ ) {
          this._children[ i ].addToLayoutChanges( "locationY" );
        }
      }
      //this.getLayoutImpl().layoutChild( this._children[ i ], changes );
      this._flushChildrenQueue();
    },

    _onElementOver : function( event ) {
      var target = event.getTarget();
      var internal = target === event.getRelatedTarget();
      if( target instanceof rwt.widgets.base.GridRow && internal && this._hoverItem ) {
        var hoverElement = target.getTargetIdentifier( event );
        if( this._hoverElement[ 0 ] !== hoverElement[ 0 ] ) {
          this._hoverElement = hoverElement;
          this._setHoverItem( this._hoverItem );
        }
      }
    },

    _onRowOver : function( event ) {
      var target = event.getOriginalTarget();
      if( target instanceof rwt.widgets.base.GridRow ) {
        if( event.getType() === "mouseout" ) {
          this._hoverElement = null;
          this._setHoverItem( null );
        } else {
          this._hoverElement = target.getTargetIdentifier( event );
          var item = this.findItemByRow( target );
          if( item !== this._hoverItem ) { // can happen due to use of innerHTML/replacing elements
            this._setHoverItem( item );
          }
        }
      }
    },

    _setHoverItem : function( item ) {
      var oldItem = this._hoverItem;
      this._hoverItem = item;
      if( oldItem !== item ) {
        this._renderAsync( oldItem );
      }
      this._renderAsync( item );
    },

    _getHoverElement : function( item ) {
      var result = null;
      if( this._hoverItem === item ) {
        result = this._hoverElement;
      }
      return result;
    },

    _renderAsync : function( item ) {
      // async rendering needed in some cases where webkit (and possibly other browser) get confused
      // when changing dom-elements in "mouseover" events
      if( item !== null ) {
        this._asyncQueue[ item.toHashCode() ] = item;
        this._asyncTimer.start();
      }
    },

    _onAsyncTimer : function() {
      this._asyncTimer.stop();
      this.renderItemQueue( this._asyncQueue );
      this._asyncQueue = {};
    },

    _isSelected : function( item ) {
      return this._selectionProvider[ 0 ].call( this._selectionProvider[ 1 ], item );
    },

    //////////////
    // Overwritten

    _applyHeight : function( value, oldValue ) {
      this.base( arguments, value, oldValue );
      this._updateRowCount();
    },

    _afterAppear : function() {
      this.base( arguments );
      this.setScrollLeft( this._scrollLeft );
    }

  }
} );
