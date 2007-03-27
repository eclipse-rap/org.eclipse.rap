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
 * org.eclipse.rap.rwt.TableColumn.
 */
qx.OO.defineClass(
  "org.eclipse.rap.rwt.widgets.TableColumn",
  qx.ui.basic.Atom,
  function( parent ) {
    qx.ui.basic.Atom.call( this );
    this.setAppearance( "table-column" );
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    // Add this column to the list of coluimns maintained by the table
    this._table = parent;
    this._table._addColumn( this );
    // Register mouse-listener add control the 'mouseover' appearance state
    this.addEventListener( "mouseover", this._onMouseOver, this );
    this.addEventListener( "mouseout",  this._onMouseOut, this );
  }
);

qx.Proto.dispose = function() {
  if ( this.getDisposed() ) {
    return true;
  }
  this.removeEventListener( "mouseover", this._onMouseOver, this );
  this.removeEventListener( "mouseout",  this._onMouseOut, this );
  return qx.ui.basic.Atom.prototype.dispose.call( this );
}

/**
 * This listener function is added and removed server-side
 */
qx.Proto.onClick = function( evt ) {
  org.eclipse.rap.rwt.EventUtil.widgetSelected( evt );
}

qx.Proto._onMouseOver = function( evt ) {
  this.addState( "mouseover" );
}

qx.Proto._onMouseOut = function( evt ) {
  this.removeState( "mouseover" );
}
