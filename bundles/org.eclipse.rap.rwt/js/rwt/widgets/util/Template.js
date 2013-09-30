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
  this._item = null;
  this._parseCells();
};

rwt.widgets.util.Template.prototype = {

  hasCellLayout : rwt.util.Functions.returnTrue,

  configure : function( item ) {
    this._item = item;
  },

  getCellCount : function() {
    return this._cells.length;
  },

  getCellLeft : function( cell ) {
    return this._cells[ cell ].left;
  },

  getCellTop : function( cell ) {
    return this._cells[ cell ].top;
  },

  getCellWidth : function( cell ) {
    return this._cells[ cell ].width;
  },

  getCellHeight : function( cell ) {
    return this._cells[ cell ].height;
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

  getCellBackground : function(){},

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
    }
  }

};

}());
