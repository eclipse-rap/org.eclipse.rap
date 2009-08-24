/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
 
 qx.Class.define( "org.eclipse.swt.widgets.ProgressBar", {
  extend : qx.ui.layout.CanvasLayout,
  
  construct : function() {
    this.base( arguments );

    this.setOverflow( "hidden" );
    this.setAppearance( "progressbar" );
    
    this._indicator = new qx.ui.layout.CanvasLayout();
    this._indicator.setParent( this );
    this._indicator.setLeft( 0 );
    this._indicator.setAppearance( "progressbar-indicator" );
    
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
    this._indicator.dispose();
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
        this._indicator.setWidth( this.getWidth() - 2 );
        var newHeight
          =  ( this._selection / this._maximum ) * ( this.getHeight() - 2 );
        this._indicator.setHeight( newHeight );
        this._indicator.setTop( this.getHeight() - newHeight - 2 );
      } else {
        this._indicator.setTop( 0 );
        this._indicator.setHeight( this.getHeight() - 2 );
        var newWidth
          =  ( this._selection / this._maximum ) * ( this.getWidth() - 2 );
        this._indicator.setWidth( newWidth );
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

    setState : function( state ) {
      if( state == "error" ) {
        this._indicator.removeState( "paused" );
        this._indicator.addState( "error" );
      } else if( state == "paused" ) {
        this._indicator.removeState( "error" );
        this._indicator.addState( "paused" );
      } else {
        this._indicator.removeState( "error" );
        this._indicator.removeState( "paused" );
      }
    },

    _initIndeterminedHorizontal : function() {
      this._indicator.setTop( 0 );
      this._indicator.setLeft( 
       -org.eclipse.swt.widgets.ProgressBar.UNDETERMINED_SIZE );
      this._indicator.setWidth( 
        org.eclipse.swt.widgets.ProgressBar.UNDETERMINED_SIZE );
      if( this.getHeight() != null ) {
        this._indicator.setHeight( this.getHeight() - 2 );
      }   
      this._timer = new qx.client.Timer( 120 );
      this._timer.addEventListener( "interval", this._moveHorizontal, this );
      this._timer.start();
    },
    
    _initIndeterminedVertical : function() {
      this._indicator.setTop(
        -org.eclipse.swt.widgets.ProgressBar.UNDETERMINED_SIZE );
      this._indicator.setLeft( 0 ); 
      this._indicator.setWidth( this.getWidth() - 2 ); 
      this._indicator.setHeight(
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
      this._indicator.setHeight( this.getHeight() - 2 );
      if( this._indicator.getLeft() >= this.getWidth() ) {
        this._indicator.setLeft( 
          -org.eclipse.swt.widgets.ProgressBar.UNDETERMINED_SIZE );
      } else { 
        this._indicator.setLeft( this._indicator.getLeft() + 2 );
      }
    },
    
    _moveVertical : function() {
      this._indicator.setWidth( this.getWidth() - 2 );
      if(    this._indicator.getTop() 
          <= -org.eclipse.swt.widgets.ProgressBar.UNDETERMINED_SIZE ) 
      {
        this._indicator.setTop( this.getHeight() );
      } else { 
        this._indicator.setTop( this._indicator.getTop() - 2 );
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
