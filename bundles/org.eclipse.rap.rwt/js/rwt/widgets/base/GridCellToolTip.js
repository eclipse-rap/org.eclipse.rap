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


namespace( "rwt.widgets.base" );

rwt.widgets.base.GridCellToolTip = {

  _cell : null,
  _requestedCell : null,
  _timer : null,
  _getTimer : function() {
    if( this._timer == null ) {
      this._timer = new rwt.client.Timer( 1000 );
      this._timer.addEventListener( "interval", this._onTimer, this );
    }
    return this._timer;
  },

  setEnabled : function( grid, value ) {
    if( value ) {
      grid._rowContainer.addEventListener( "mousemove", this._onClientAreaMouseMove, grid );
    } else {
      grid._rowContainer.removeEventListener( "mousemove", this._onClientAreaMouseMove, grid );
    }
  },

  showToolTip : function( text ) {
    if( this._isValidToolTip( text ) ) {
      var grid = this._cell[ 0 ];
      var item = rwt.remote.ObjectRegistry.getObject( this._cell[ 1 ] );
      var row = grid.getRowContainer()._findRowByItem( item );
      if( row ) {
        row.setToolTipText( text );
        rwt.widgets.base.WidgetToolTip.getInstance().show();
        rwt.widgets.util.ToolTipManager.getInstance().setCurrentToolTipTarget( row );
      }
    }
  },

  _onClientAreaMouseMove : function( evt ) {
    var itemId = null;
    var columnIndex = -1;
    if( this._rowContainer.getHoverItem() ) {
      itemId = rwt.remote.ObjectRegistry.getId( this._rowContainer.getHoverItem() );
      columnIndex = rwt.widgets.util.GridUtil.getColumnByPageX( this, evt.getPageX() );
    }
    rwt.widgets.base.GridCellToolTip._setCell( this, itemId, columnIndex );
  },

  _setCell : function( grid, itemId, columnIndex ) {
    this._cell = [ grid, itemId, columnIndex ];
    if( this._isValidCell() ) {
      this._getTimer().restart();
    } else {
      this._getTimer().stop();
    }
  },

  _isValidCell : function() {
    return    this._cell
           && this._cell[ 0 ] != null
           && this._cell[ 1 ] != null
           && this._cell[ 2 ] != -1;
  },

  _isValidToolTip : function( text ) {
    return    text
           && this._cell[ 0 ] === this._requestedCell[ 0 ]
           && this._cell[ 1 ] === this._requestedCell[ 1 ]
           && this._cell[ 2 ] === this._requestedCell[ 2 ];
  },

  _onTimer : function() {
    this._getTimer().stop();
    if( this._isValidCell() ) {
      var connection = rwt.remote.Connection.getInstance();
      connection.getRemoteObject( this._cell[ 0 ] ).call( "renderToolTipText", {
        "item" : this._cell[ 1 ],
        "column" : this._cell[ 2 ]
      } );
      this._requestedCell = this._cell;
    }
  }

};
