/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
 
 qx.Class.define( "org.eclipse.swt.widgets.Composite", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function() {
    this.base( arguments );
    this.setAppearance( "composite" );
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    this.setHideFocus( true ); 
    this.addEventListener( "mouseover", this._onMouseOver, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
  },
  
  destruct : function() {
    this.removeEventListener( "mouseover", this._onMouseOver, this );
    this.removeEventListener( "mouseout", this._onMouseOut, this );
  },
  
  members : {
    
    _onMouseOver : function( evt ) {
      this.addState( "over" );
    },
    
    _onMouseOut : function( evt ) {
      this.removeState( "over" );
    }
    
  }
} );