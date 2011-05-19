/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.widgets.AbstractSlider", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( horizontal ) {
    this.base( arguments );
    this.setOverflow( "hidden" );
    this._horizontal = horizontal;
    // properties (using virtual units):
    this._selection = 0;
    this._minimum = 0;
    this._maximum = 100;
    this._increment = 1;
    this._pageIncrement = 10;
    this._thumbLength = 10;
    // state:
    this._pxStep = 1.38; // ratio of virtual units to real (pixel) length
    this._thumbDragOffset = 0; 
    this._autoRepeat = ""; // string indicating to auto-repeat an action  
    this._mouseOffset = 0; // horizontal or vertical offset to slider start    
    this._delayTimer = new qx.client.Timer( 250 ); // delay auto-repeated actions
    this._repeatTimer = new qx.client.Timer( 100 ); // for auto-repeated actions
    // subwidgets:
    this._thumb = new org.eclipse.rwt.widgets.BasicButton( "push" );
    this._minButton = new org.eclipse.rwt.widgets.BasicButton( "push" );
    this._maxButton = new org.eclipse.rwt.widgets.BasicButton( "push" );
    this.add( this._thumb );
    this.add( this._minButton );
    this.add( this._maxButton );
    this._configureSubwidgets();
    this._configureAppearance();
    this._setStates();
    this._registerListeners();
  },

  destruct : function() {
    this._delayTimer.stop();
    this._delayTimer.dispose();
    this._delayTimer = null;
    this._repeatTimer.stop();
    this._repeatTimer.dispose();
    this._repeatTimer = null;
    this._thumb = null;
    this._minButton = null;
    this._maxButton = null;    
  },

  members : {
    
    ////////////
    // protected

    _setSelection : function( value ) {
      var newSelection = this._limitSelection( value );
      if( newSelection !== this._selection ) {
        this._selection = newSelection;
        this._selectionChanged();
      }
    },

    _setMinimum : function( value ) {
      this._minimum = value;
      this._updateThumbSize();
    },

    _setMaximum : function( value ) {
      this._maximum = value;
      this._updateThumbSize();
    },

    _setIncrement : function( value ) {
      this._increment = value;
    },

    _setPageIncrement : function( value ) {
      this._pageIncrement = value;
    },

    _setThumb : function( value ) {
      this._thumbLength = value;
      this._updateThumbSize();
    },
    
    ////////////////
    // Eventhandlers

    _registerListeners : function() {
      this._repeatTimer.addEventListener( "interval", this._onRepeatTimerInterval, this );
      this._delayTimer.addEventListener( "interval", this._repeatTimerStart, this );
      this.addEventListener( "changeWidth", this._onChangeSize, this );
      this.addEventListener( "changeHeight", this._onChangeSize, this );
      this.addEventListener( "changeEnabled", this._onChangeEnabled, this );
      this.addEventListener( "mousedown", this._onMouseDown, this );
      this.addEventListener( "mouseup", this._onMouseUp, this );
      this.addEventListener( "mouseout",  this._onMouseOut, this );
      this.addEventListener( "mouseover",  this._onMouseOver, this );
      this.addEventListener( "mousemove", this._onMouseMove, this );
      this.addEventListener( "mousewheel", this._onMouseWheel, this );
      this._thumb.addEventListener( "mousedown", this._onThumbMouseDown, this );
      this._thumb.addEventListener( "mousemove", this._onThumbMouseMove, this );
      this._thumb.addEventListener( "mouseup", this._onThumbMouseUp, this );
      this._minButton.addEventListener( "mousedown", this._onMinButtonMouseEvent, this );
      this._maxButton.addEventListener( "mousedown", this._onMaxButtonMouseEvent, this );
      this._minButton.addEventListener( "stateOverChanged", this._onMinButtonMouseEvent, this );
      this._maxButton.addEventListener( "stateOverChanged", this._onMaxButtonMouseEvent, this );

    },
    
    _selectionChanged : function() {
      this._updateThumbPosition();
      if( this._autoRepeat !== "" && !this._repeatTimer.isEnabled() ) {
        this._delayTimer.start();
      }
    },
    
    _onChangeSize : function( event ) {
      this._updateThumbSize();
    },

    _onChangeEnabled : function( event ) {
      this._thumb.setVisibility( event.getValue() );
    },
    
    _onMouseWheel : function( event ) {
      if ( event.getTarget() === this ) {
        event.preventDefault();
        event.stopPropagation();
        var data = event.getWheelDelta();
        var change = ( data / Math.abs( data ) ) * this._increment;
        var sel = this._selection - change;
        if( sel < this._minimum ) {
          sel = this._minimum;
        } 
        if( sel > ( this._maximum - this._thumbWidth ) ) {
          sel = this._maximum - this._thumbWidth;
        } 
        this._setSelection( sel );
      }
    },

    _onMouseDown : function( event ) {
      if( event.isLeftButtonPressed() ) {
        this._mouseOffset = this._getMouseOffset( event );
        this._handleLineMouseDown();
      }
    },

    _onMouseUp : function( event ) {
      this.setCapture( false );
      this._autoRepeat = "";
      this._delayTimer.stop();
      this._repeatTimer.stop();
    },

    _onMouseOver : function( event ) {
      var target = event.getOriginalTarget();
      if ( target === this && this._autoRepeat.slice( 0, 4 ) === "line" ) {
        this.setCapture( false );
        this._repeatTimerStart();
      }
    },

    _onMouseOut : function( event ) {
      var target = event.getRelatedTarget();
      var outOfSlider = target !== this && !this.contains( target );
      if( outOfSlider && this._autoRepeat.slice( 0, 4 ) === "line" ) {
        this.setCapture( true );
        this._delayTimer.stop();
        this._repeatTimer.stop();
      }
    },

    _onMouseMove : function( event ) {
      this._mouseOffset = this._getMouseOffset( event );
    },

    _onMinButtonMouseEvent : function( event ) {
      event.stopPropagation();
      if( this._minButton.hasState( "pressed" ) ) {
        this._autoRepeat = "minButton";
        this._setSelection( this._selection - this._increment );
      } else {
        this._autoRepeat = "";
      }
    },

    _onMaxButtonMouseEvent : function( event ) {
      event.stopPropagation();
      if( this._maxButton.hasState( "pressed" ) ) {
        this._autoRepeat = "maxButton";
        this._setSelection( this._selection + this._increment );
      } else {
        this._autoRepeat = "";
      }
    },

    _onThumbMouseDown : function( event ) {
      event.stopPropagation();
      this._thumb.addState( "pressed" );
      if( event.isLeftButtonPressed() ) {
        var mousePos = this._getMouseOffset( event );
        this._thumbDragOffset = mousePos - this._getThumbPosition();
        this._thumb.setCapture( true );
      }
    },

    _onThumbMouseMove : function( event ) {
      event.stopPropagation();
      if( this._thumb.getCapture() ) {
        var mousePos = this._getMouseOffset( event );
        var newSelection 
          = this._getSelectionFromPosition( mousePos - this._thumbDragOffset );
        this._setSelection( newSelection );
      }
    },

    _onThumbMouseUp : function( event ) {
      if( this._thumb.hasState( "pressed" ) ) {
        event.stopPropagation();
        this._repeatTimer.stop();
        this._thumb.setCapture( false );
        this._thumb.removeState( "pressed" );
      }
    },

    ////////////
    // Internals

    _configureSubwidgets : function() {
      if( this._horizontal ) {
        this._thumb.setHeight( "100%" );
        this._minButton.setHeight( "100%" );
        this._maxButton.setHeight( "100%" );
        this._maxButton.setRight( 0 );
      } else {
        this._thumb.setWidth( "100%" );
        this._minButton.setWidth( "100%" );
        this._maxButton.setWidth( "100%" );
        this._maxButton.setBottom( 0 );
      }
      org.eclipse.swt.WidgetUtil.fixIEBoxHeight( this._thumb );
      org.eclipse.swt.WidgetUtil.fixIEBoxHeight( this._minButton );
      org.eclipse.swt.WidgetUtil.fixIEBoxHeight( this._maxButton );
      this._minButton.setTabIndex( null );
      this._maxButton.setTabIndex( null );
    },
    
    _setStates : function() {
      var style = this._horizontal ? "rwt_HORIZONTAL" : "rwt_VERTICAL";
      var state = this._horizontal ? "horizontal" : "vertical";
      this.addState( style ); 
      this._minButton.addState( style );
      this._minButton.addState( state );
      this._maxButton.addState( style );
      this._maxButton.addState( state );
      this._thumb.addState( style );
      // We need to render appearance now because valid layout values
      // (i.e. a number) might be needed by the constructor
      this._renderAppearance();
      this._minButton._renderAppearance();
      this._maxButton._renderAppearance();
      this._thumb._renderAppearance();
    },

    // overwritten:
    _visualizeFocus : function() {
      this.base( arguments );
      this._thumb.addState( "focused" );
    },
    
    // overwritten:
    _visualizeBlur : function() {
      this.base( arguments );
      this._thumb.removeState( "focused" );
    },

    _repeatTimerStart : function() {
      this._delayTimer.stop();
      if( this._autoRepeat !== "" ) {
        this._repeatTimer.start();
      }
    },

    _onRepeatTimerInterval : function( event ) {
      switch( this._autoRepeat ) {
        case "minButton":
          this._setSelection( this._selection - this._increment );
        break;
        case "maxButton":
          this._setSelection( this._selection + this._increment );
        break;
        case "linePlus":
        case "lineMinus":
          this._handleLineMouseDown();
        break;
      }
    },

    _handleLineMouseDown : function() {
      var mode;
      var thumbHalf = this._getThumbSize() / 2;
      var pxSel = this._getThumbPosition() + thumbHalf;
      var newSelection;
      if( this._mouseOffset > pxSel ) {
        newSelection = this._selection + this._pageIncrement;
        mode = "linePlus";
      } else {
        mode = "lineMinus";
        newSelection = this._selection - this._pageIncrement;
      }
      if( this._autoRepeat === "" || this._autoRepeat === mode ) {
        this._autoRepeat = mode;
        var thumbMove = this._pageIncrement * this._pxStep + thumbHalf;
        this._setSelection( newSelection );
      }
    },

    _updateThumbPosition : function() {
      var pos = this._getMinButtonWidth(); 
      pos += this._pxStep * ( this._selection - this._minimum );
      if( this._horizontal ) {
        this._thumb.setLeft( pos );
      } else {
        this._thumb.setTop( pos );
      }
    },

    _updateThumbSize : function() {
      var newSize =   this._thumbLength * this._getLineSize()
                    / ( this._maximum - this._minimum );
      newSize = Math.round( newSize );
      if( this._horizontal ) {
        this._thumb.setWidth( newSize );
      } else {
        this._thumb.setHeight( newSize );
      }
      this._updateStepsize();
    },

    _updateStepsize : function() {
      var numSteps = this._maximum - this._minimum - this._thumbLength;
      if( numSteps != 0 ) {
        var numPixels = this._getLineSize() - this._getThumbSize()
        this._pxStep = numPixels / numSteps;
      } else {
        this._pxStep = 0;
      }
      this._selection = this._limitSelection( this._selection );
      this._updateThumbPosition();
    },
    
    //////////
    // Helpers

    _getSelectionFromPosition : function( position ) {
      var buttonSize = this._getMinButtonWidth();
      var sel = ( position - buttonSize ) / this._pxStep + this._minimum;
      return this._limitSelection( Math.round( sel ) );
    },

    _limitSelection : function( value ) {
      var result = value;
      if( value >= ( this._maximum - this._thumbLength ) ) {
        result = this._maximum - this._thumbLength;
      } 
      if( result <= this._minimum ) {
        result = this._minimum;
      }
      return result;
    },

    _getMouseOffset : function( mouseEvent ) {
      var location = qx.bom.element.Location;
      var result;
      if( this._horizontal ) {
        result = mouseEvent.getPageX() - location.getLeft( this.getElement() );
      } else {
        result = mouseEvent.getPageY() - location.getTop( this.getElement() );
      }
      return result;
    },
    
    _getThumbPosition : function() {
      var result;
      if( this._horizontal ) {
        result = this._thumb.getLeft();
      } else {
        result = this._thumb.getTop();
      }
      return result;
    },
    
    _getThumbSize : function() {
      var result;
      if( this._horizontal ) {
        result = this._thumb.getWidth();
      } else {
        result = this._thumb.getHeight();
      }
      return result;
    },

    _getLineSize : function() {
      var buttonSize = this._getMinButtonWidth() + this._getMaxButtonWidth();
      var result =   this._getSliderSize()
                   - this.getFrameWidth()
                   - buttonSize;
      return result;
    },

    _getSliderSize : function() {
      var result;
      if( this._horizontal ) {
        result = this.getWidth();
      } else {
        result = this.getHeight();
      }
      return result;
    },
    
    _getMinButtonWidth : function() {
      var result;
      if( this._horizontal ) {
        result = this._minButton.getWidth();
      } else {
        result = this._minButton.getHeight();
      }
      return result;
    },
    
    _getMaxButtonWidth : function() {
      var result;
      if( this._horizontal ) {
        result = this._maxButton.getWidth();
      } else {
        result = this._maxButton.getHeight();
      }
      return result;
    }

  }
} );