/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
 
 qx.Class.define( "org.eclipse.swt.widgets.ProgressBar", {
  extend : qx.ui.layout.CanvasLayout,
  
  construct : function() {
    this.base( arguments );
    
    var borderColor = "#c0c0c0";
    var border = new qx.renderer.border.Border( 1, "solid", borderColor );
    this.setBorder( border );
    this.setOverflow( "hidden" );
    
    this._bar = new qx.ui.layout.CanvasLayout();
    this._bar.setParent( this );
    this._bar.setBackgroundColor( "#0080C0" );
    this._bar.setLeft( 0 ); 
    
    this._minimum = 0;
    this._maximum = 100;
    this._selection = 0;
    this._flag = 0;
  },
  
  destruct : function() {
    if( this._timer != null ) {
      this._timer.stop();
      this._timer.dispose();
    }
    this._timer = null;
  },

  members : {
    setMinimum : function( minimum ) {
      this._minimum = minimum;
    },
    
    setMaximum : function( maximum ) {
      this._maximum = maximum;
    },
    
    setSelection : function( selection ) {
      this._selection = selection;
      if( ( this._flag & 2 ) != 0 ) {
        this._move();
      } else if( ( this._flag & 512 ) != 0 ) {
        this._bar.setWidth( this.getWidth() - 2 );
        var newHeight
          =  ( this._selection / this._maximum ) * ( this.getHeight() - 2 );
        this._bar.setHeight( newHeight );
        this._bar.setTop( this.getHeight() - newHeight - 2 );
      } else {
        this._bar.setTop( 0 );
        this._bar.setHeight( this.getHeight() - 2 );
        var newWidth
          =  ( this._selection / this._maximum ) * ( this.getWidth() - 2 );
        this._bar.setWidth( newWidth );
      }
    },
    
    setFlag : function( flag ) {
      this._flag = flag;
      if( ( this._flag & 2 ) != 0 ) {
        this._bar.setTop( 0 );
        this._bar.setLeft( -40 );
        this._bar.setWidth( 40 );
        this._bar.setHeight( this.getHeight() - 2 );
        this._timer = new qx.client.Timer( 120 );
        this._timer.addEventListener( "interval", this._move, this );
        this._timer.start();
      }
    },
    
    _move : function() {
      this._bar.setHeight( this.getHeight() - 2 );
      if( this._bar.getLeft() >= this.getWidth() ) {
        this._bar.setLeft( -40 );
      } else { 
        this._bar.setLeft( this._bar.getLeft() + 2 );
      }
    }
  }
});