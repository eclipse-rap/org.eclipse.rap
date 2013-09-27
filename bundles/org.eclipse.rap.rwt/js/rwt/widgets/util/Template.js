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
    if( typeof this._cells[ cell ].bindingIndex === "number" ) {
      var index = this._cells[ cell ].bindingIndex;
      return this._item.hasText( index );
    } else {
      return false;
    }
  },

  getText : function( cell, arg ) {
    if( typeof this._cells[ cell ].bindingIndex === "number" ) {
      var index = this._cells[ cell ].bindingIndex;
      return this._item.getText( index, arg );
    } else {
      return "";
    }
  },

  getCellForeground : function(){},
  getCellBackground : function(){},
  getCellFont : function(){},

};

}());
