
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
 * org.eclipse.swt.widgets.TableItem.
 */
qx.Class.define( "org.eclipse.swt.widgets.TableItem", {
  extend : qx.core.Object,

  construct : function( parent, index ) {
    this.base( arguments );
    this._parent = parent;
    this._checked = false;
    this._grayed = false;
    this._texts = new Array();
    this._images = new Array();
    this._imageWidths = new Array();
    // HACK: Table needs one 'emptyItem' (draws the remaining space that is not 
    //       occupied by actual items) and a 'virtualItem' (represents a not
    //       yet resolved item) 
    // Those have an index of -1
    if ( index >= 0 ) {
      parent._addItem( this, index );
    }
  },
  
  destruct : function() {
    if( !this._parent.isDisposed() ) {
      this._parent._removeItem( this );
    }
    org.eclipse.swt.WidgetManager.getInstance().remove( this );
  },

  statics : {
    // Constants used to render outermost div element
    DIV_START_OPEN : "<div unselectable=\"on\" ",
    DIV_START_CLOSE : ">",
    DIV_END : "</div>",
    DIV_STYLE : 
        "position:absolute;" 
      + "overflow:hidden;"
      + "white-space:nowrap;" 
      + "cursor:default;" 
      + "top:0px;"
      + ( qx.core.Client.getInstance().isGecko() ? "-moz-user-select:none;" : "" ),

    // Constants usd to render img element that holds the item image
    IMG_START : "<img style=\"vertical-align:middle\" ",
    IMG_SRC_OPEN : "src=\"",
    IMG_SRC_CLOSE : "\"",
    IMG_END : " />",
    
    // Constants used to render span element that holds the item text
    SPAN_START : "<span unselectable=\"on\" style=\"vertical-align:middle\">",
    SPAN_END : "</span>",

    PX : "px;",
    
    NBSP : "&nbsp;",
     
    LINE_BORDER : "border-right:1px solid #eeeeee;"
  },
  
  members : {

    getTable : function() {
      return this._parent;
    },
    
    setChecked : function( value ) {
      if( this._checked != value ) {
        this._checked = value;
        this.getTable().updateItem( this, true );
      }
    },
    
    getChecked : function() {
      return this._checked;
    },
    
    setGrayed : function( value ) {
      if( this._grayed != value ) {
        this._grayed = value;
        this.getTable().updateItem( this, true );
      }
    },
    
    getGrayed : function() {
      return this._grayed;
    },
    
    setSelection : function( value ) {
      if( value ) {
        this.getTable()._selectItem( this );
      } else {
        this.getTable()._unselectItem( this );
      }
    },
    
    focus : function() {
      this.getTable().setFocusedItem( this );
    },

    setTexts : function( texts ) {
      this._texts = texts;
      this.getTable().updateItem( this, true );
    },
    
    setImages : function( images, imageWidths ) {
      this._images = images;
      this._imageWidths = imageWidths;
      this.getTable().updateItem( this, true );
    },
    
    /**
     * Called by Table when updating visible rows to obtain HTML markup that 
     * represents the item.
     */
    _getMarkup : function() {
      var markup = new Array();
      var table = this.getTable();
      var height = table.getItemHeight();
      var image = null;
      var text = "";
      var columnCount = table.getColumnCount();
      var leftOffset = 0;
      if( table.hasCheckBoxes() ) {
        leftOffset = org.eclipse.swt.widgets.Table.CHECK_WIDTH;
      }
      if( columnCount == 0 ) {
        var defaultWidth = table.getDefaultColumnWidth() - leftOffset;
        markup.push( this._getStartCellMarkup( left, defaultWidth, height ) );
        if( this._images && this._images.length > 0 ) {
          markup.push( this._getImageMarkup( this._images[ 0 ] ) );
        }
        if( this._texts[ 0 ] !== undefined ) {
          text = this._texts[ 0 ];
        }
        markup.push( this._getTextMarkup( text ) );
        markup.push( this._getEndCellMarkup() );
      } else {
        for( var i = 0; i < columnCount; i++ ) {
          var column = table.getColumn( i );
          var left = column.getLeft();
          var width = column.getWidth();
          // Is first column (current visual order)?
          if( column.getLeft() == 0 ) { 
            width -= leftOffset;
          } else {
            left -= leftOffset;
          }
          markup.push( this._getStartCellMarkup( left, width, height ) );
          if( this._images && this._images[ i ] ) {
            image = this._images[ i ];
            markup.push( this._getImageMarkup( image ) );
          }
          if( this._texts[ i ] !== undefined ) {
            text = this._texts[ i ];
          }
          markup.push( this._getTextMarkup( text ) );
          markup.push( this._getEndCellMarkup() );
        }
      }
      return markup.join( "" );
    },
    
    _getStartCellMarkup : function( left, width, height ) {
      var result 
        = org.eclipse.swt.widgets.TableItem.DIV_START_OPEN 
        + "style=\"" 
        + org.eclipse.swt.widgets.TableItem.DIV_STYLE 
        + this._borderMarkup() 
        + "left:"  + left + org.eclipse.swt.widgets.TableItem.PX 
        + "width:" + width + org.eclipse.swt.widgets.TableItem.PX 
        + "height:" + height + org.eclipse.swt.widgets.TableItem.PX
        + "\"" 
        + org.eclipse.swt.widgets.TableItem.DIV_START_CLOSE
      return result;
    },
    
    _getEndCellMarkup : function() {
      return org.eclipse.swt.widgets.TableItem.DIV_END;      
    },
    
    _getImageMarkup : function( image ) {
      var result = "";
      if( image != null ) {
        result 
          = org.eclipse.swt.widgets.TableItem.IMG_START
          + org.eclipse.swt.widgets.TableItem.IMG_SRC_OPEN
          + image 
          + org.eclipse.swt.widgets.TableItem.IMG_SRC_CLOSE
          + org.eclipse.swt.widgets.TableItem.IMG_END;
      }
      return result;
    },

    _getTextMarkup : function( text ) {
      var result;
      if( text == "" ) {
        result = org.eclipse.swt.widgets.TableItem.NBSP;
      } else {
        result
          = org.eclipse.swt.widgets.TableItem.SPAN_START
          + text 
          + org.eclipse.swt.widgets.TableItem.SPAN_END;
      }
      return result;
    },
    
    _borderMarkup : function() {
      return ( // those brackets seems to be ABSOLUTELY NECESSARY!
          this.getTable().getLinesVisible() 
        ? org.eclipse.swt.widgets.TableItem.LINE_BORDER 
        : "" );
    }
  }
});
