/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "rwt.widgets.base.GridCellToolTip", {
  extend : rwt.widgets.base.ToolTip,
  include : rwt.animation.VisibilityAnimationMixin,

  construct : function( grid ) {
    this.base( arguments );
    this._grid = grid;
    this._itemId = null;
    this._columnIndex = -1;
  },

  members : {

    _onshowtimer : function( evt ) {
      this._stopShowTimer();
      this._requestCellToolTipText();
    },

    setText : function( text ) {
      if( this._isValidToolTip( text ) ) {
        this.getAtom().setLabel( text );
        this.setLeft( rwt.event.MouseEvent.getPageX() + this.getMousePointerOffsetX() );
        this.setTop( rwt.event.MouseEvent.getPageY() + this.getMousePointerOffsetY() );
        this.show();
      }
    },

    setCell : function( itemId, columnIndex ) {
      if( this._itemId != itemId || this._columnIndex != columnIndex ) {
        this._itemId = itemId;
        this._columnIndex = columnIndex;
        this.hide();
        if( this._isValidCell() ) {
          this._startShowTimer();
        } else {
          this._stopShowTimer();
        }
      }
    },

    _requestCellToolTipText : function() {
      if( this._isValidCell() ) {
        var server = rwt.remote.Server.getInstance();
        this._requestedCell = this._itemId + "," + this._columnIndex;
        server.getRemoteObject( this._grid ).call( "renderToolTipText", {
          "item" : this._itemId,
          "column" : this._columnIndex
        } );
      }
    },

    _isValidCell : function() {
      return this._itemId != null && this._columnIndex != -1;
    },

    _isValidToolTip : function( text ) {
      var currentCell = this._itemId + "," + this._columnIndex;
      return text && text !== "" && currentCell === this._requestedCell;
    }

  }
});
