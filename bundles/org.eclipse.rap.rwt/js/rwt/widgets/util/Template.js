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
};

rwt.widgets.util.Template.prototype = {

  hasCellLayout : true,

  createContainer : function( element ) {
    if( !element || typeof element.nodeName !== "string" ) {
      throw new Error( "Not a valid target for TemplateContainer:" + element );
    }
    return {
      "element" : element,
      "template" : this,
      "cellElements" : []
    };
  },

  getCellElement : function( container, cell ) {
    return container.cellElements[ cell ] || null;
  },

  render : function( options ) {
    if( !options.container || options.container.template !== this ) {
      throw new Error( "No valid TemplateContainer: " + options.container );
    }
    this._item = options.item;
    this._createElements( options ); // TODO [tb] : create lazy while rendering content
    this._renderAllBounds( options );
  },

  getCellCount : function() {
    return this._cells.length;
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

  _createElements : function( options ) {
    var elements = options.container.cellElements;
    for( var i = 0; i < this._cells.length; i++ ) {
      if( elements[ i ] == null && this._hasContent( options.item, i ) ) {
        var element = document.createElement( "div" );
        element.style.overflow = "hidden";
        element.style.position = "absolute";
        options.container.element.appendChild( element );
        options.container.cellElements[ i ] = element;
      }
    }
  },

  _hasContent : function( item, cell ) {
    var index = this._cells[ cell ].bindingIndex;
    switch( this._cells[ cell ].type ) {
      case "text":
        return this.hasText( cell );
      case "image":
        return item.getImage( index ) !== null;
      default:
        return false;
    }
  },

  _renderAllBounds : function( options ) {
    for( var i = 0; i < this._cells.length; i++ ) {
      var element = options.container.cellElements[ i ];
      if( element ) {
        element.style.left = this._getCellLeft( options, i ) + "px";
        element.style.top = this._getCellTop( options, i ) + "px";
        element.style.width = this._getCellWidth( options, i ) + "px";
        element.style.height = this._getCellHeight( options, i ) + "px";
      }
    }
  },

  _getCellLeft : function( options, cell ) {
    var cellData = this._cells[ cell ];
    return   cellData.left !== undefined
           ? cellData.left
           : options.bounds[ 2 ] - cellData.width - cellData.right;
  },

  _getCellTop : function( options, cell ) {
    var cellData = this._cells[ cell ];
    return   cellData.top !== undefined
           ? cellData.top
           : options.bounds[ 3 ] - cellData.height - cellData.bottom;
  },

  _getCellWidth : function( options, cell ) {
    var cellData = this._cells[ cell ];
    return   cellData.width !== undefined
           ? cellData.width
           : options.bounds[ 2 ] - cellData.left - cellData.right;
  },

  _getCellHeight : function( options, cell ) {
    var cellData = this._cells[ cell ];
    return   cellData.height !== undefined
           ? cellData.height
           : options.bounds[ 3 ] - cellData.top - cellData.bottom;
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
