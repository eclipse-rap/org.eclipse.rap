/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
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

    this.setOverflow( "hidden" );
    this.setAppearance( "progressbar" );
    
    this._bar = new qx.ui.layout.CanvasLayout();
    this._bar.setParent( this );
    this._bar.setLeft( 0 );
    this._bar.setAppearance( "progressbar-bar" );
    
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

  statics : {
    UNDETERMINED_SIZE : 40,
    FLAG_UNDETERMINED : 2,
    FLAG_HORIZONTAL : 256,
    FLAG_VERTICAL : 512
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
      if( this._isUndetermined() ) {
        this._move();
      } else if( this._isVertical() ) {
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
      if( this._isUndetermined() ) {
        if( this._isHorizontal() ) {
          this._initIndeterminedHorizontal();
        } else if( this._isVertical() ) {
          this._initIndeterminedVertical();
        } else {
          this._initIndeterminedHorizontal();
        }        
      }
    },
    
    _initIndeterminedHorizontal : function() {
      this._bar.setTop( 0 );
      this._bar.setLeft( 
       -org.eclipse.swt.widgets.ProgressBar.UNDETERMINED_SIZE );
      this._bar.setWidth( 
        org.eclipse.swt.widgets.ProgressBar.UNDETERMINED_SIZE );
      this._bar.setHeight( this.getHeight() - 2 );
      this._timer = new qx.client.Timer( 120 );
      this._timer.addEventListener( "interval", this._moveHorizontal, this );
      this._timer.start();
    },
    
    _initIndeterminedVertical : function() {
      this._bar.setTop(
        -org.eclipse.swt.widgets.ProgressBar.UNDETERMINED_SIZE );
      this._bar.setLeft( 0 ); 
      this._bar.setWidth( this.getWidth() - 2 ); 
      this._bar.setHeight(
        org.eclipse.swt.widgets.ProgressBar.UNDETERMINED_SIZE );
      this._timer = new qx.client.Timer( 120 );
      this._timer.addEventListener( "interval", this._moveVertical, this );
      this._timer.start();
    },
    
    _move : function() {
      if( this._isHorizontal() ) {
        this._moveHorizontal();
      } else if( this._isVertical() ) {
        this._moveVertical();
      } else {
        this._moveHorizontal();
      }
    },
    
    _moveHorizontal : function() {
      this._bar.setHeight( this.getHeight() - 2 );
      if( this._bar.getLeft() >= this.getWidth() ) {
        this._bar.setLeft( 
          -org.eclipse.swt.widgets.ProgressBar.UNDETERMINED_SIZE );
      } else { 
        this._bar.setLeft( this._bar.getLeft() + 2 );
      }
    },
    
    _moveVertical : function() {
      this._bar.setWidth( this.getWidth() - 2 );
      if(    this._bar.getTop() 
          <= -org.eclipse.swt.widgets.ProgressBar.UNDETERMINED_SIZE ) 
      {
        this._bar.setTop( this.getHeight() );
      } else { 
        this._bar.setTop( this._bar.getTop() - 2 );
      }
    },
    
    _isUndetermined : function() {
      var masked = 
        this._flag & org.eclipse.swt.widgets.ProgressBar.FLAG_UNDETERMINED;
      return masked != 0;
    },
    
    _isHorizontal : function() {
      var masked
        = this._flag & org.eclipse.swt.widgets.ProgressBar.FLAG_HORIZONTAL;
      return masked != 0;
    },
    
    _isVertical : function() {
      var masked
        = this._flag & org.eclipse.swt.widgets.ProgressBar.FLAG_VERTICAL;
      return masked != 0;
    }
  }
});
