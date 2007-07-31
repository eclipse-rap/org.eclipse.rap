
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
    // Constants usd to render img element that holds the item image
    IMG_START : "<div ",
    IMG_STYLE_OPEN : "style=\"position:absolute;overflow:hidden;",
    IMG_STYLE_CLOSE : "\"",
    IMG_CLOSE : ">",
    IMG_SRC_OPEN : "<img src=\"",
    IMG_SRC_CLOSE : "\" />",
    IMG_END : "</div>",
    
    // Constants used to render span element that holds the item text
    TEXT_OPEN : "<div ",
    TEXT_STYLE_OPEN : "style=\"position:absolute;overflow:hidden;vertical-align:middle;white-space:nowrap;",
    TEXT_STYLE_CLOSE : "\"",
    TEXT_CLOSE : ">",
    TEXT_END : "</div>",
    
    // TODO [rh] make border color themeable
    LINE_BORDER : "border-right:1px solid #eeeeee;",

    TOP : "top:",
    LEFT : "left:",
    WIDTH : "width:",
    HEIGHT : "height:",
    PX : "px;",
    
    TEXT_ALIGN : "text-align:"
     
  },
  
  members : {

    setChecked : function( value ) {
      if( this._checked != value ) {
        this._checked = value;
      }
    },
    
    getChecked : function() {
      return this._checked;
    },
    
    setGrayed : function( value ) {
      if( this._grayed != value ) {
        this._grayed = value;
      }
    },
    
    getGrayed : function() {
      return this._grayed;
    },
    
    setSelection : function( value ) {
      if( value ) {
        this._parent._selectItem( this );
      } else {
        this._parent._unselectItem( this );
      }
    },
    
    focus : function() {
      this._parent.setFocusedItem( this );
    },

    setTexts : function( texts ) {
      this._texts = texts;
    },
    
    setImages : function( images ) {
      this._images = images;
    },
    
    update : function() {
      this._parent.updateItem( this, true );
    },
    
    /**
     * Called by Table when updating visible rows to obtain HTML markup that 
     * represents the item.
     */
    _getMarkup : function() {
      var markup = new Array();
      var left = 0;
      var width = 0;
      var columnCount = this._parent.getColumnCount();
      if( columnCount == 0 ) {
        columnCount = 1;
      } 
      var leftOffset = 0;
      if( this._parent.hasCheckBoxes() ) {
        leftOffset = org.eclipse.swt.widgets.Table.CHECK_WIDTH;
      }
      for( var i = 0; i < columnCount; i++ ) {
        // Draw image
        if( this._images && this._images[ i ] ) {
          left = this._parent.getItemImageLeft( i );
          width = this._parent.getItemImageWidth( i );
          markup.push( this._getImageMarkup( this._images[ i ], left, width ) );
        }
        // Draw text
        if( this._texts[ i ] !== undefined ) {
          left = this._parent.getItemTextLeft( i );
          width = this._parent.getItemTextWidth( i );
          var align = qx.constant.Layout.ALIGN_LEFT;
          var column = this._parent.getColumn( i );
          if( column ) {
            align = column.getHorizontalChildrenAlign();
          }
          markup.push( this._getTextMarkup( this._texts[ i ], align, left, width ) );
        }
      }
      return markup.join( "" );
    },
    
    _getImageMarkup : function( image, left, width ) {
      var result = "";
      if( image != null ) {
        // TODO [rh] replace div/img markup with only a div with a bg-image
        result 
          = org.eclipse.swt.widgets.TableItem.IMG_START
          + org.eclipse.swt.widgets.TableItem.IMG_STYLE_OPEN
          + org.eclipse.swt.widgets.TableItem.TOP 
            + "0" 
            + org.eclipse.swt.widgets.TableItem.PX 
          + org.eclipse.swt.widgets.TableItem.LEFT 
            + left 
            + org.eclipse.swt.widgets.TableItem.PX 
          + org.eclipse.swt.widgets.TableItem.WIDTH 
            + width
            + org.eclipse.swt.widgets.TableItem.PX 
          + org.eclipse.swt.widgets.TableItem.HEIGHT 
            + this._parent.getItemHeight()
            + org.eclipse.swt.widgets.TableItem.PX 
          + org.eclipse.swt.widgets.TableItem.IMG_STYLE_CLOSE
          + org.eclipse.swt.widgets.TableItem.IMG_CLOSE
          + org.eclipse.swt.widgets.TableItem.IMG_SRC_OPEN
          + image 
          + org.eclipse.swt.widgets.TableItem.IMG_SRC_CLOSE
          + org.eclipse.swt.widgets.TableItem.IMG_END;
      }
      return result;
    },

    _getTextMarkup : function( text, align, left, width ) {
      var result;
      if( text == "" ) {
        result = "";
      } else {
        var border 
          = this._parent.getLinesVisible() 
          ? org.eclipse.swt.widgets.TableItem.LINE_BORDER 
          : "";
        result
          = org.eclipse.swt.widgets.TableItem.TEXT_OPEN
          + org.eclipse.swt.widgets.TableItem.TEXT_STYLE_OPEN
          + org.eclipse.swt.widgets.TableItem.TOP 
            + "0" 
            + org.eclipse.swt.widgets.TableItem.PX 
          + org.eclipse.swt.widgets.TableItem.LEFT 
            + left 
            + org.eclipse.swt.widgets.TableItem.PX 
          + org.eclipse.swt.widgets.TableItem.WIDTH 
            + width
            + org.eclipse.swt.widgets.TableItem.PX 
          + org.eclipse.swt.widgets.TableItem.HEIGHT 
            + this._parent.getItemHeight()
            + org.eclipse.swt.widgets.TableItem.PX
          + border  
          + org.eclipse.swt.widgets.TableItem.TEXT_ALIGN 
            + align
          + org.eclipse.swt.widgets.TableItem.TEXT_STYLE_CLOSE
          + org.eclipse.swt.widgets.TableItem.TEXT_CLOSE
          + text 
          + org.eclipse.swt.widgets.TableItem.TEXT_END;
      }
      return result;
    }

  }
});
