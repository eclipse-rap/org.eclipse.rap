
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
    
    // HACK: Table needs one 'emptyItem' to draw the remaining space that is
    //       not occupied by actual items. This item has an index of -1
    if ( index != -1 ) {
      parent._addItem( this, index );
      this._texts = new Array();
      this._images = new Array();
      this._imageWidths = new Array();
    }
  },
  
  destruct : function() {
    if( !this._parent.isDisposed() ) {
      this._parent._removeItem( this );
    }
    org.eclipse.swt.WidgetManager.getInstance().remove( this );
  },

  statics : {
    DIV_START_OPEN : "<div ",
    DIV_START_CLOSE : ">",
    DIV_END : "</div>",

    PX : "px;",

    CONST_STYLE : 
        "position:absolute;" 
      + "overflow:hidden;"
      + "white-space:nowrap;" 
      + "cursor:default;" 
      + "top:0px;"
      + ( qx.core.Client.getInstance().isGecko() ? "-moz-user-select:none;" : "" ),

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

    setTexts : function( texts ) {
      this._texts = texts;
      this.getTable().updateItem( this, true );
    },
    
    setImages : function( images, imageWidths ) {
      this._images = images;
      this._imageWidths = imageWidths;
      this.getTable().updateItem( this, true );
    },
    
    getCheckImage : function() {
      var result;
      if( this._grayed ) {
        if( this._checked ) {
          result = "widget/table/check_gray_on.gif";
        } else {
          result = "widget/table/check_gray_off.gif";
        }
      } else {
        if( this._checked ) {
          result = "widget/table/check_white_on.gif";
        } else {
          result = "widget/table/check_white_off.gif";
        }
      }
      return result;
    },
    
    /**
     * Called by Table when updating visible rows to obtain HTML markup that 
     * represents the item.
     */
    _getMarkup : function() {
      var markup = new Array();
      var table = this.getTable();
      var height = this.getTable()._itemHeight;
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
        if( this._texts && this._texts.length > 0 ) {
          text = this._texts[ 0 ];
        }
        markup.push( this._getTextMarkup( text ) );
        markup.push( this._getEndCellMarkup() );
      } else {
        for( var i = 0; i < columnCount; i++ ) {
          var column = table.getColumn( i );
          var left = column.getLeft();
          var width = column.getWidth();
          if( i == 0 ) {
            width -= leftOffset;
          } else {
            left -= leftOffset;
          }
          markup.push( this._getStartCellMarkup( left, width, height ) );
          if( this._images && this._images[ i ] ) {
            image = this._images[ i ];
            markup.push( this._getImageMarkup( image ) );
          }
          if( this._texts ) {
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
        + org.eclipse.swt.widgets.TableItem.CONST_STYLE 
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
          = "<img style=\"vertical-align:middle\" "
          + "src=\"" 
          + image 
          + "\" />";
      }
      return result;
    },

    _getTextMarkup : function( text ) {
      var result;
      if( text == "" ) {
        result = "&nbsp;";
      } else {
        result
          = "<span style=\"vertical-align:middle\">"
          + text 
          + "</span>";
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
