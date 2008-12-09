/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.widgets.Sash", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function() {
    this.base( arguments );
    this.setOverflow( null );
    this.setHtmlProperty( "unselectable", "on" );
    this.addEventListener( "changeWidth", this._onChangeSize, this );
    this.addEventListener( "changeHeight", this._onChangeSize, this );
    this._slider = new qx.ui.layout.CanvasLayout();
    this._slider.setAppearance( "sash-slider" );
    this._slider.setVisibility( false );    
    // Fix IE Styling issues
    org.eclipse.swt.WidgetUtil.fixIEBoxHeight( this._slider );
    this.add( this._slider );
    this._sliderHandle = new qx.ui.layout.CanvasLayout();
    this._sliderHandle.setStyleProperty( "backgroundPosition", "center center" );
    this._sliderHandle.setAppearance( "sash-handle" );
    this._sliderHandle.setVisibility( false ); 
    // Fix IE Styling issues
    org.eclipse.swt.WidgetUtil.fixIEBoxHeight( this._sliderHandle );
    this.add( this._sliderHandle );
    this._handle = new qx.ui.layout.CanvasLayout();
    this._handle.setStyleProperty( "backgroundPosition", "center center" );
    this._handle.setAppearance( "sash-handle" );
    // Fix IE Styling issues
    org.eclipse.swt.WidgetUtil.fixIEBoxHeight( this._handle );
    this.add( this._handle );
    this.initOrientation();
    this._bufferZIndex = null;
  },

  destruct : function() {
    this.removeEventListener( "changeWidth", this._onChangeSize, this );
    this.removeEventListener( "changeHeight", this._onChangeSize, this );
    this._removeStyle( this.getOrientation() );
    this._disposeObjects( "_slider", "_handle", "_sliderHandle" );
  },

  properties : {

    appearance : {
      refine : true,
      init : "sash"
    },

    orientation : {
      check : [ "horizontal", "vertical" ],
      apply : "_applyOrientation",
      init : "horizontal",
      nullable : true
    }

  },

  members : {
    
    _onChangeSize : function( evt ) {
      this._handle.setWidth( this.getWidth() );
      this._handle.setHeight( this.getHeight() );
    },

    _onMouseDownX : function( evt ) {
      if( evt.isLeftButtonPressed() ) {
        if( this.getEnabled() ) {
          this._commonMouseDown();
          this._dragOffset = evt.getPageX();
          this._minMove = - this.getLeft() - this._frameOffset;
          this._maxMove = this.getParent().getWidth() - this.getLeft()
                              - this.getWidth() - this._frameOffset;
        }
      }
    },

    _onMouseDownY : function( evt ) {
      if( evt.isLeftButtonPressed() ) {
        if( this.getEnabled() ) {
          this._commonMouseDown();
          this._dragOffset = evt.getPageY();
          this._minMove = - this.getTop() - this._frameOffset;
          this._maxMove = this.getParent().getHeight() - this.getTop()
                              - this.getHeight() - this._frameOffset;
        }
      }
    },

    _commonMouseDown : function() {
      this.setCapture( true );
      this.getTopLevelWidget().setGlobalCursor( this.getCursor() );
      // Used to subtract border width
      // Note: Assumes that the Sash border has equal width on all four edges
      this._frameOffset = this.getFrameWidth() / 2;
      this._slider.setLeft( 0 - this._frameOffset );
      this._slider.setTop( 0 - this._frameOffset );
      this._slider.setWidth( this.getWidth() );
      this._slider.setHeight( this.getHeight() );
      this._sliderHandle.setLeft( 0 );
      this._sliderHandle.setTop( 0 );
      this._sliderHandle.setWidth( this.getWidth() );
      this._sliderHandle.setHeight( this.getHeight() );
      this._bufferZIndex = this.getZIndex();
      this.setZIndex( 1e7 );
      this._slider.show();
      this._sliderHandle.show();
    },

    _onMouseUpX : function( evt ) {
      if( this.getCapture() ) {
        this._commonMouseUp();
      }
    },

    _onMouseUpY : function( evt ) {
      if( this.getCapture() ) {
        this._commonMouseUp();
      }
    },

    _commonMouseUp : function() {
      // TODO [rst] Clarify what the getOffsetLeft() does
      var leftOffset = this._slider.getLeft() + this._frameOffset;
      var topOffset = this._slider.getTop() + this._frameOffset;
      this._slider.hide();
      this._sliderHandle.hide();
      this.setCapture( false );
      this.getTopLevelWidget().setGlobalCursor( null );
      if( this._bufferZIndex != null ) {
        this.setZIndex( this._bufferZIndex );
      }
      // notify server
      if( leftOffset != 0 || topOffset != 0 ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        org.eclipse.swt.EventUtil.doWidgetSelected( id, 
                                                    this.getLeft() + leftOffset, 
                                                    this.getTop() + topOffset, 
                                                    this.getWidth(), 
                                                    this.getHeight() );
      }
    },

    _onMouseMoveX : function( evt ) {
      if( this.getCapture() ) {
        var toMove = evt.getPageX() - this._dragOffset;
        this._slider.setLeft( this._normalizeMove( toMove ) );
        this._sliderHandle.setLeft( this._normalizeMove( toMove ) );
      }
    },

    _onMouseMoveY : function( evt ) {
      if( this.getCapture() ) {
        var toMove = evt.getPageY() - this._dragOffset;
        this._slider.setTop( this._normalizeMove( toMove ) );
        this._sliderHandle.setTop( this._normalizeMove( toMove ) );
      }
    },

    _normalizeMove : function( toMove ) {
      var result = toMove;
      if( result < this._minMove ) {
        result = this._minMove;
      }
      if( result > this._maxMove ) {
        result = this._maxMove;
      }
      return result;
    },

    _applyOrientation : function( value, old ) {
      this._removeStyle( old );
      this._setStyle( value );
    },

    _setStyle : function( style ) {
      if( style == "horizontal" ) {
        this.addEventListener( "mousedown", this._onMouseDownY, this );
        this.addEventListener( "mousemove", this._onMouseMoveY, this );
        this.addEventListener( "mouseup", this._onMouseUpY, this );
        this.addState( "horizontal" );
        this._handle.addState( "horizontal" );
        this._sliderHandle.addState( "horizontal" );
      } else if( style == "vertical" ) {
        this.addEventListener( "mousemove", this._onMouseMoveX, this );
        this.addEventListener( "mousedown", this._onMouseDownX, this );
        this.addEventListener( "mouseup", this._onMouseUpX, this );
        this.addState( "vertical" );
        this._handle.addState( "vertical" );
        this._sliderHandle.addState( "vertical" );
      }
    },

    _removeStyle : function( style ) {
      if( style == "horizontal" ) {
        this.removeEventListener( "mousedown", this._onMouseDownY, this );
        this.removeEventListener( "mousemove", this._onMouseMoveY, this );
        this.removeEventListener( "mouseup", this._onMouseUpY, this );
        this.removeState( "horizontal" );
        this._handle.removeState( "horizontal" );
        this._sliderHandle.removeState( "horizontal" );
      } else if( style == "vertical" ) {
        this.removeEventListener( "mousedown", this._onMouseDownX, this );
        this.removeEventListener( "mousemove", this._onMouseMoveX, this );
        this.removeEventListener( "mouseup", this._onMouseUpX, this );
        this.removeState( "vertical" );
        this._handle.removeState( "vertical" );
        this._sliderHandle.removeState( "vertical" );
      }
    }
  }
});
