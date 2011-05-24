/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define("org.eclipse.rwt.widgets.TreeRowContainer", {  
  extend : qx.ui.layout.VerticalBoxLayout,

  construct : function() {
    this.base( arguments );
    this.setOverflow( "hidden" );
    this._scrollLeft = 0;
    this._rowHeight = 16;
    this._rowWidth = 0;
    this._rowAppearance = null;
    this._rowBorder = null;
    this._linesVisible = false;
    this._topItem = null;
    this._renderTime = null;
    this._topItemIndex = 0;
    this._items = [];
    this._hoverItem = null;
    this._hoverElement = null;
    this._config = this._createRenderConfig();
    this.addEventListener( "mouseover", this._onRowOver, this );
    this.addEventListener( "mouseout", this._onRowOver, this );
    this.addEventListener( "elementOut", this._onElementOver, this );
    this.addEventListener( "elementOver", this._onElementOver, this );
  },

  destruct : function() {
    this._rowBorder = null;
    this._topItem = null;
    this._renderTime = null;
    this._items = null;
    this._hoverItem = null;
    this._hoverElement = null;
  },
  
  statics : {
  },
  
  members : {
    
    /////////////////////
    // cunstructor helper

    _createRenderConfig : function() {
      var result = {
        "textColor" : null,
        "font" : null,
        "enabled" : true,
        "focused" : true,
        "linesVisible" : false,
        "fullSelection" : false,
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
        "itemTextWidth" : []   
      };
      return result;
    },

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
    
    /**
     * has to be set before creating any rows
     */
    setRowAppearance : function( value ) {
      this._rowAppearance = value;
    },

    setRowBorder : function( border ) {
      this._rowBorder = border;
      for( var i = 0; i < this._children.length; i++ ) {
        this._children[ i ].setBorder( border );
      }
    },
    
    setRowLinesVisible : function( value ) {
      for( var i = 0; i < this._children.length; i++ ) {
        this._children[ i ].setLinesVisible( value );
      }
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
          this.renderAll();
        } else {
          var numberOfShiftingRows = this._children.length - delta;
          var updateFromRow = forwards ? numberOfShiftingRows : 0;
          var newFirstRow = forwards ? delta : numberOfShiftingRows;
          this._switchRows( newFirstRow );
          this._updateRows( updateFromRow, delta );
        }
      } else {
        this._topItemIndex = index;
      }
    },

    renderAll : function() {
      var start = ( new Date() ).getTime();
      this._updateRows( 0, this._children.length );
      var renderTime = ( new Date() ).getTime() - start;
      if( this._postRender ) {
        this._postRender[ 0 ].call( this._postRender[ 1 ], renderTime );
      }
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

    ////////////
    // Internals

    _updateRowCount : function() {
      var height = this.getHeight()
      var rowsNeeded = Math.ceil( this.getHeight() / this._rowHeight );
      while( this._children.length < rowsNeeded ) {
        var row = new org.eclipse.rwt.widgets.TreeRow( this.getParent() );
        row.setAppearance( this._rowAppearance ); 
        row.setWidth( this._rowWidth );
        row.setHeight( this._rowHeight );
        row.setBorder( this._rowBorder );
        row.setLinesVisible( this._linesVisible );
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

    _updateRows : function( from, delta ) {
      this._updateRowsEvenState();
      var item = this._topItem;
      var to = from + delta;
      var row = 0;
      while( item != null && row < this._children.length ) {
        if( row >= from && row <= to ) {
          this._renderRow( this._children[ row ], item );
          this._items[ row ] = item;
        }
        item = item.getNextItem();
        row++;
      }
      for( var i = row; i < this._children.length; i++ ) {
        this._renderRow( this._children[ i ], null );
        this._items[ row ] = null;
      }
    },
    
    _renderRow : function( row, item ) {
       row.renderItem( item, 
                       this._config, 
                       this._isSelected( item ), 
                       this._getHoverElement( item ) );
       // TODO [tb] : onRenderVirtual
    },

    _switchRows : function( newFirstRow ) {
      var rowTemp = this._children.slice( newFirstRow );
      var itemsTemp = this._items.slice( newFirstRow );
      this._children = rowTemp.concat( this._children.slice( 0, newFirstRow ) );
      this._items = itemsTemp.concat( this._items.slice( 0, newFirstRow ) );
      this._invalidateVisibleChildren();
      var changes = { "locationY" : true };
      for( var i = 0; i < this._children.length; i++ ) {
        this.getLayoutImpl().layoutChild( this._children[ i ], changes );
      }
    },

    _onElementOver : function( event ) {
      var isOver = event.getType() === "elementOver";
      this._hoverElement = isOver ? event.getDomTarget() : null;
      var row = event.getTarget();
      var internal = row === event.getRelatedTarget();
      if( internal && row instanceof org.eclipse.rwt.widgets.TreeRow ) {
        var hoverable = row.isCheckBoxTarget( event ) || row.isExpandSymbolTarget( event );
        if( this._hoverItem !== null && hoverable ) { 
          this.renderItem( this._hoverItem );
        }
      }
    },

    _onRowOver : function( event ) {
      var target = event.getOriginalTarget();
      if( target instanceof org.eclipse.rwt.widgets.TreeRow ) {
        if( event.getType() === "mouseout" ) {
          var oldItem = this._hoverItem;
          this._hoverItem = null;
          this.renderItem( oldItem );          
        } else {
          this._hoverItem = this.findItemByRow( target );
          this._renderRow( target, this._hoverItem );
        }
      }
    },

    _getHoverElement : function( item ) {
      var result = null;
      if( this._hoverItem === item ) {
        result = this._hoverElement;
      }
      return result;
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