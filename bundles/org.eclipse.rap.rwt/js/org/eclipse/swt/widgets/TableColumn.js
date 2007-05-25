
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
 * org.eclipse.swt.TableColumn.
 */
qx.Class.define( "org.eclipse.swt.widgets.TableColumn", {
  extend : qx.ui.basic.Atom,

  construct : function( parent ) {
    this.base( arguments );
    this.setAppearance( "table-column" );
    this.setHorizontalChildrenAlign( qx.constant.Layout.ALIGN_LEFT ); 
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    // TODO [qx07] revise this: without this Table._updateScrollWidth would 
    //      accidentially calculate a width of "0auto"
    this.setWidth( 0 );
    // TODO [qx07] revise this: left seems to be null initially which breaks
    //      the markup produced by TableItem
    this.setLeft( 0 );
    // Add this column to the list of coluimns maintained by the table
    this._table = parent;
    this._table._addColumn( this );
    // Register mouse-listener add control the 'mouseover' appearance state
    this.addEventListener( "mouseover", this._onMouseOver, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
  },

  destruct : function() {
    this.removeEventListener( "mouseover", this._onMouseOver, this );
    this.removeEventListener( "mouseout", this._onMouseOut, this );
  },
  
  members : {

    /** This listener function is added and removed server-side */
    onClick : function( evt ) {
      org.eclipse.swt.EventUtil.widgetSelected( evt );
    },

    _onMouseOver : function( evt ) {
      this.addState( "mouseover" );
    },

    _onMouseOut : function(evt) {
      this.removeState( "mouseover" );
    }
  }
});
