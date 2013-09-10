/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
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

  statics : {

    setEnabled : function( grid, value ) {
      if( value ) {
        grid._cellToolTip = new rwt.widgets.base.GridCellToolTip( grid );
        grid._rowContainer.addEventListener( "mousemove", this._onClientAreaMouseMove, grid );
        grid._rowContainer.setToolTip( grid._cellToolTip );
      } else {
        grid._rowContainer.removeEventListener( "mousemove", this._onClientAreaMouseMove, grid );
        grid._rowContainer.setToolTip( null );
        grid._cellToolTip.destroy();
        grid._cellToolTip = null;
      }
    },

    _onClientAreaMouseMove : function( evt ) {
      if( this._cellToolTip != null ) {
        var itemId = null;
        var columnIndex = -1;
        if( this._rowContainer.getHoverItem() ) {
          var widgetManager = rwt.remote.WidgetManager.getInstance();
          itemId = widgetManager.findIdByWidget( this._rowContainer.getHoverItem() );
          columnIndex = rwt.widgets.util.GridUtil.getColumnByPageX( this, evt.getPageX() );
        }
        this._cellToolTip.setCell( itemId, columnIndex );
      }
    },



  },

  members : {

    _onshowtimer : function( evt ) {
      this._stopShowTimer();
      this._requestCellToolTipText();
    },

    setText : function( text ) {
      if( this._isValidToolTip( text ) ) {
        this._label.setCellContent( 0, text );
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
        var server = rwt.remote.Connection.getInstance();
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
