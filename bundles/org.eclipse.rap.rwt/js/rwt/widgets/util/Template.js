/*******************************************************************************
 * Copyright (c) 2010, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

namespace( "rwt.widgets.util" );

(function(){

rwt.widgets.util.Template = function( cells ) {
  this._cells = cells;
  this._parseCells();
  this._item = null;
  this._dimension = null;
};

rwt.widgets.util.Template.prototype = {

  hasCellLayout : rwt.util.Functions.returnTrue,

  render : function( options ) {
    this._item = options.item;
    this._dimension = options.dimension;
  },

  getCellCount : function() {
    return this._cells.length;
  },

  getCellLeft : function( cell ) {
    var cellData = this._cells[ cell ];
    return   cellData.left !== undefined
           ? cellData.left
           : this._dimension[ 0 ] - cellData.width - cellData.right;
  },

  getCellTop : function( cell ) {
    var cellData = this._cells[ cell ];
    return   cellData.top !== undefined
           ? cellData.top
           : this._dimension[ 1 ] - cellData.height - cellData.bottom;
  },

  getCellWidth : function( cell ) {
    var cellData = this._cells[ cell ];
    return   cellData.width !== undefined
           ? cellData.width
           : this._dimension[ 0 ] - cellData.left - cellData.right;
  },

  getCellHeight : function( cell ) {
    var cellData = this._cells[ cell ];
    return   cellData.height !== undefined
           ? cellData.height
           : this._dimension[ 1 ] - cellData.top - cellData.bottom;
  },

  getCellType : function( cell ) {
    return this._cells[ cell ].type;
  },

  hasText : function( cell ) {
    if( this._isBound( cell ) ) {
      return this._item.hasText( this._getIndex( cell ) );
    } else {
      return false;
    }
  },

  getText : function( cell, arg ) {
    if( this._isBound( cell ) ) {
      return this._item.getText( this._getIndex( cell ), arg );
    } else {
      return "";
    }
  },

  getImage : function( cell, arg ) {
    if( this._isBound( cell ) ) {
      return this._item.getImage( this._getIndex( cell ), arg );
    } else {
      return null;
    }
  },

  getCellForeground : function( cell ) {
    var result = null;
    if( this._isBound( cell ) ) {
      result = this._item.getCellForeground( this._getIndex( cell ) );
    }
    if( ( result === null || result === "" ) && this._cells[ cell ].foreground ) {
      result = this._cells[ cell ].foreground;
    }
    return result;
  },

  getCellBackground : function( cell ) {
    var result = null;
    if( this._isBound( cell ) ) {
      result = this._item.getCellBackground( this._getIndex( cell ) );
    }
    if( ( result === null || result === "" ) && this._cells[ cell ].background ) {
      result = this._cells[ cell ].background;
    }
    return result;
  },


  getCellFont : function( cell ){
    var result = null;
    if( this._isBound( cell ) ) {
      result = this._item.getCellFont( this._getIndex( cell ) );
    }
    if( ( result === null || result === "" ) && this._cells[ cell ].font ) {
      result = this._cells[ cell ].font;
    }
    return result;
  },

  _isBound : function( cell ) {
    return typeof this._cells[ cell ].bindingIndex === "number";
  },

  _getIndex : function( cell ) {
    return this._cells[ cell ].bindingIndex;
  },

  _parseCells : function() {
    for( var i = 0; i < this._cells.length; i++ ) {
      if( this._cells[ i ].font ) {
        var font = this._cells[ i ].font;
        this._cells[ i ].font = rwt.html.Font.fromArray( font ).toCss();
      }
      if( this._cells[ i ].foreground ) {
        var foreground = this._cells[ i ].foreground;
        this._cells[ i ].foreground = rwt.util.Colors.rgbToRgbString( foreground );
      }
      if( this._cells[ i ].background ) {
        var background = this._cells[ i ].background;
        this._cells[ i ].background = rwt.util.Colors.rgbToRgbString( background );
      }
    }
  }

};

}());
