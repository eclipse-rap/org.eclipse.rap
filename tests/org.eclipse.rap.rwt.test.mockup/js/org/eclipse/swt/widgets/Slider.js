/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

/**
 * This class provides the client-side counterpart for
 * org.eclipse.swt.widgets.Slider.
 */
qx.Class.define( "org.eclipse.swt.widgets.Slider", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( style ) {
    this.base( arguments );
    this.setAppearance( "slider" );
    // Get styles
    this._horizontal = qx.lang.String.contains( style, "horizontal" );
    //
    this._hasSelectionListener = false;
    // Flag indicating that the next request can be sent
    this._readyToSendChanges = true;
    // Default values
    this._selection = 0;
    this._minimum = 0;
    this._maximum = 100;
    this._increment = 1;
    this._pageIncrement = 10;
    this._thumbWidth = 10;
    this._pxStep = 1.38;
    this._thumbPressed = false;
    // _interactionId: Indicates what element is pressed - minButton,
    // maxButton or area behind the thumb (line)
    this._interactionId;
    // _mousePos: Stores the mouse position, needed when calculating the
    // thumb translation after click on the area behind the thumb (line)
    this._mousePos;
    // Timer: used for continuous scrolling
    this._scrollTimer = new qx.client.Timer( 100 );
    this._scrollTimer.addEventListener( "interval",
                                        this._onScrollTimerInterval, 
                                        this );
    // _scrollReadyToStart: Flag for starting the scrollTimer
    this._scrollReadyToStart = false;
    // Line - the area behind the thumb
    this._line = new qx.ui.basic.Atom;
    if( this._horizontal ) {
      this._line.addState( org.eclipse.swt.widgets.Slider.STATE_HORIZONTAL );
    }
    this._line.setAppearance( "slider-line" );
    this._line.addEventListener( "mousedown", this._onLineMouseDown, this );
    this._line.addEventListener( "mouseup", 
                                 this._onInteractionMouseUpOut, 
                                 this );
    this._line.addEventListener( "mousemove", this._onLineMouseMove, this );
    this._line.addEventListener( "mouseout", 
                                 this._onInteractionMouseUpOut,
                                 this );
    this.add( this._line );
    // Thumb
    this._thumb = new qx.ui.basic.Atom;
    if( this._horizontal ) {
      this._thumb.addState( org.eclipse.swt.widgets.Slider.STATE_HORIZONTAL );
    }
    this._thumb.setAppearance( "slider-thumb" );
    this._thumb.addEventListener( "mousedown", this._onThumbMouseDown, this );
    this._thumb.addEventListener( "mousemove", this._onThumbMouseMove, this );
    this._thumb.addEventListener( "mouseup", this._onThumbMouseUp, this );
    // Fix IE Styling issues
    org.eclipse.swt.WidgetUtil.fixIEBoxHeight( this._thumb );
    this.add( this._thumb );
    // Thumb offset
    this._thumbOffset = 0;
    // Min button
    this._minButton = new qx.ui.form.Button;
    if( this._horizontal ) {
      this._minButton.addState( org.eclipse.swt.widgets.Slider.STATE_HORIZONTAL );
    } else {
      this._minButton.addState( org.eclipse.swt.widgets.Slider.STATE_VERTICAL );
    }
    this._minButton.addState( "rwt_PUSH" );
    this._minButton.setAppearance( "slider-min-button" );
    this._minButton.addEventListener( "mousedown", 
                                      this._onMinButtonMouseDown,
                                      this );
    this._minButton.addEventListener( "mouseup", 
                                      this._onInteractionMouseUpOut,
                                      this );
    this._minButton.addEventListener( "mouseout",
                                      this._onInteractionMouseUpOut, 
                                      this );
    // Fix IE Styling issues
    org.eclipse.swt.WidgetUtil.fixIEBoxHeight( this._minButton );
    this.add( this._minButton );
    // Max button
    this._maxButton = new qx.ui.form.Button;
    if( this._horizontal ) {
      this._maxButton.addState( org.eclipse.swt.widgets.Slider.STATE_HORIZONTAL );
    } else {
      this._maxButton.addState( org.eclipse.swt.widgets.Slider.STATE_VERTICAL );
    }
    this._maxButton.addState( "rwt_PUSH" );
    this._maxButton.setAppearance( "slider-max-button" );
    this._maxButton.addEventListener( "mousedown", 
                                      this._onMaxButtonMouseDown,
                                      this );
    this._maxButton.addEventListener( "mouseup", 
                                      this._onInteractionMouseUpOut,
                                      this );
    this._maxButton.addEventListener( "mouseout",
                                      this._onInteractionMouseUpOut, 
                                      this );
    // Fix IE Styling issues
    org.eclipse.swt.WidgetUtil.fixIEBoxHeight( this._maxButton );
    this.add( this._maxButton );
    // Add events listeners
    this.addEventListener( "changeWidth", this._onChangeSize, this );
    this.addEventListener( "changeHeight", this._onChangeSize, this );
    this.addEventListener( "contextmenu", this._onContextMenu, this );
    this.addEventListener( "changeEnabled", this._onChangeEnabled, this );
  },

  destruct : function() {
    this._line.removeEventListener( "mousedown", this._onLineMouseDown, this );
    this._line.removeEventListener( "mouseup", 
                                    this._onInteractionMouseUpOut,
                                    this );
    this._line.removeEventListener( "mousemove", this._onLineMouseMove, this );
    this._line.removeEventListener( "mouseout", 
                                    this._onInteractionMouseUpOut,
                                    this );
    this._minButton.removeEventListener( "mousedown",
                                         this._onMinButtonMouseDown, 
                                         this );
    this._minButton.removeEventListener( "mouseup",
                                         this._onInteractionMouseUpOut, 
                                         this );
    this._minButton.removeEventListener( "mouseout",
                                         this._onInteractionMouseUpOut, 
                                         this );
    this._maxButton.removeEventListener( "mousedown",
                                         this._onMaxButtonMouseDown, 
                                         this );
    this._maxButton.removeEventListener( "mouseup",
                                         this._onInteractionMouseUpOut, 
                                         this );
    this._maxButton.removeEventListener( "mouseout",
                                         this._onInteractionMouseUpOut, 
                                         this );
    this._scrollTimer.removeEventListener( "interval",
                                           this._onScrollTimerInterval, 
                                           this );
    this._thumb.removeEventListener( "mousedown", this._onThumbMouseDown, this );
    this._thumb.removeEventListener( "mousemove", this._onThumbMouseMove, this );
    this._thumb.removeEventListener( "mouseup", this._onThumbMouseUp, this );
    this.removeEventListener( "changeWidth", this._onChangeSize, this );
    this.removeEventListener( "changeHeight", this._onChangeSize, this );
    this.removeEventListener( "contextmenu", this._onContextMenu, this );
    this.removeEventListener( "changeEnabled", this._onChangeEnabled, this );
    if( this._scrollTimer != null ) {
      this._scrollTimer.stop();
      this._scrollTimer.dispose();
    }
    this._scrollTimer = null;
    this._disposeObjects( "_line", "_thumb", "_minButton", "_maxButton" );    
  },

  statics : {
    STATE_HORIZONTAL : "horizontal",
    STATE_VERTICAL : "vertical",
    BUTTON_WIDTH : 16,
    STATE_PRESSED : "pressed"
  },

  members : {
    _onChangeSize : function( evt ) {
      if( this._horizontal ) {
        var left = this.getWidth()
                 - org.eclipse.swt.widgets.Slider.BUTTON_WIDTH;
        this._maxButton.setLeft( left );
      } else {
        var top = this.getHeight()
                - org.eclipse.swt.widgets.Slider.BUTTON_WIDTH;
        this._maxButton.setTop( top );
      }
      this._updateLineSize();
      this._updateButtonsSize();
      this._updateThumbSize();
    },

    _onContextMenu : function( evt ) {
      var menu = this.getContextMenu();
      if( menu != null ) {
        menu.setLocation( evt.getPageX(), evt.getPageY() );
        menu.setOpener( this );
        menu.show();
        evt.stopPropagation();
      }
    },

    _onChangeEnabled : function( evt ) {
      this._thumb.setVisibility( evt.getValue() );
    },

    _onLineMouseDown : function( evt ) {
      this._interactionId = "line";
      var pxSel;
      var sel;
      var thumbMov; // Thumb movement after interaction
      if( evt.isLeftButtonPressed() ) {
        if( this._horizontal ) {
          pxSel = this._thumb.getLeft() + ( this._thumb.getWidth() ) / 2;
          this._mousePos = evt.getPageX()
                         - qx.html.Location.getClientBoxLeft( this.getElement() );
          thumbMov = this._pageIncrement * this._pxStep
                   + this._thumb.getWidth() / 2;
        } else {
          pxSel = this._thumb.getTop() + ( this._thumb.getHeight() ) / 2;
          this._mousePos = evt.getPageY()
                         - qx.html.Location.getClientBoxTop( this.getElement() );
          thumbMov = this._pageIncrement * this._pxStep
                   + this._thumb.getHeight() / 2;
        }
        if( this._mousePos > pxSel ) {
          sel = this._selection + this._pageIncrement;
        } else {
          sel = this._selection - this._pageIncrement;
        }
        // Check whether to start auto-repeat interaction
        if( Math.abs( this._mousePos - pxSel ) > thumbMov ) {
          this._scrollReadyToStart = true;
        }
        if( sel < this._minimum ) {
          sel = this._minimum;
        }
        if( sel > ( this._maximum - this._thumbWidth ) ) {
          sel = this._maximum - this._thumbWidth;
        }
        this.setSelection( sel );
      }
    },

    _onLineMouseMove : function( evt ) {
      if( this._horizontal ) {
        this._mousePos = evt.getPageX()
                       - qx.html.Location.getClientBoxLeft( this.getElement() );
      } else {
        this._mousePos = evt.getPageY()
                       - qx.html.Location.getClientBoxTop( this.getElement() );
      }
    },

    _onMinButtonMouseDown : function( evt ) {
      this._interactionId = "minButton";
      var sel;
      if( evt.isLeftButtonPressed() ) {
        this._scrollReadyToStart = true;
        sel = this._selection - this._increment;
        if( sel < this._minimum ) {
          sel = this._minimum;
        }
        if( sel > ( this._maximum - this._thumbWidth ) ) {
          sel = this._maximum - this._thumbWidth;
        }
        this.setSelection( sel );
      }
    },

    _onMaxButtonMouseDown : function( evt ) {
      this._interactionId = "maxButton";
      var sel;
      if( evt.isLeftButtonPressed() ) {
        this._scrollReadyToStart = true;
        sel = this._selection + this._increment;
        if( sel < this._minimum ) {
          sel = this._minimum;
        }
        if( sel > ( this._maximum - this._thumbWidth ) ) {
          sel = this._maximum - this._thumbWidth;
        }
        this.setSelection( sel );
      }
    },

    _onInteractionMouseUpOut : function( evt ) {
      this._scrollReadyToStart = false;
      this._scrollTimer.stop();
    },

    _scrollTimerStart : function() {
      if( this._scrollReadyToStart ) {
        this._scrollTimer.start();
      }
    },

    _onScrollTimerInterval : function( evt ) {
      var sel;
      switch( this._interactionId ) {
        case "minButton":
          sel = this._selection - this._increment;
          break;
        case "maxButton":
          sel = this._selection + this._increment;
          break;
        case "line":
          var pxSel;
          var thumbMov; // Thumb movement after interaction
          if( this._horizontal ) {
            pxSel = this._thumb.getLeft() + this._thumb.getWidth() / 2;
            thumbMov = this._pageIncrement * this._pxStep
                     + this._thumb.getWidth() / 2;
          } else {
            pxSel = this._thumb.getTop() + this._thumb.getHeight() / 2;
            thumbMov = this._pageIncrement * this._pxStep
                     + this._thumb.getHeight() / 2;
          }
          if( this._mousePos > pxSel ) {
            sel = this._selection + this._pageIncrement;
          } else {
            sel = this._selection - this._pageIncrement;
          }
          // Check whether to stop auto-repeat interaction
          if( Math.abs( this._mousePos - pxSel ) <= thumbMov ) {
            this._scrollReadyToStart = false;
            this._scrollTimer.stop();
          }
          break;
      }
      if( sel < this._minimum ) {
        sel = this._minimum;
      }
      if( sel > ( this._maximum - this._thumbWidth ) ) {
        sel = this._maximum - this._thumbWidth;
      }
      this.setSelection( sel );

      if( this._readyToSendChanges ) {
        this._readyToSendChanges = false;
        // Send changes
        qx.client.Timer.once( this._sendChanges, this, 500 );
      }
    },

    _onThumbMouseDown : function( evt ) {
      var mousePos;
      this._thumb.addState( org.eclipse.swt.widgets.Slider.STATE_PRESSED );
      this._thumbPressed = true;
      if( evt.isLeftButtonPressed() ) {
        if( this._horizontal ) {
          mousePos = evt.getPageX()
                   - qx.html.Location.getClientBoxLeft( this.getElement() );
          this._thumbOffset = mousePos - this._thumb.getLeft();
        } else {
          mousePos = evt.getPageY()
                   - qx.html.Location.getClientBoxTop( this.getElement() );
          this._thumbOffset = mousePos - this._thumb.getTop();
        }
        this._thumb.setCapture( true );
      }
    },

    _onThumbMouseMove : function( evt ) {
      var mousePos;
      if( this._thumb.getCapture() ) {
        if( this._horizontal ) {
          mousePos = evt.getPageX()
                   - qx.html.Location.getClientBoxLeft( this.getElement() );
        } else {
          mousePos = evt.getPageY()
                   - qx.html.Location.getClientBoxTop( this.getElement() );
        }
        var sel = this._getSelectionFromThumbPosition( mousePos
                - this._thumbOffset );
        if( this._selection != sel ) {
          this.setSelection( sel );
          if( this._readyToSendChanges ) {
            this._readyToSendChanges = false;
            // Send changes
            qx.client.Timer.once( this._sendChanges, this, 500 );
          }
        }
      }
    },

    _onThumbMouseUp : function( evt ) {
      this._scrollTimer.stop();
      this._thumbPressed = false;
      this._thumb.setCapture( false );
      this._thumb.removeState( org.eclipse.swt.widgets.Slider.STATE_PRESSED );
    },

    _updateThumbSize : function() {
      if( this._horizontal ) {
        this._thumb.setWidth( this._thumbWidth * this._line.getWidth()
                              / ( this._maximum - this._minimum ) );
        this._thumb.setHeight( this.getHeight() );
      } else {
        this._thumb.setWidth( this.getWidth() );
        this._thumb.setHeight( this._thumbWidth * this._line.getHeight()
                               / ( this._maximum - this._minimum ) );
      }
      this._updateStep();
    },

    _updateStep : function() {
      var padding;
      var numSteps = this._maximum - this._minimum - this._thumbWidth;
      if( numSteps != 0 ) {
        if( this._horizontal ) {
          padding = org.eclipse.swt.widgets.Slider.BUTTON_WIDTH
                  + ( this._thumb.getWidth() ) / 2;
          this._pxStep = ( this.getWidth() - 2 * padding ) / numSteps;
        } else {
          padding = org.eclipse.swt.widgets.Slider.BUTTON_WIDTH
                  + ( this._thumb.getHeight() ) / 2;
          this._pxStep = ( this.getHeight() - 2 * padding ) / numSteps;
        }
      } else {
        this._pxStep = 0;
      }
      this._updateThumbPosition();
    },

    _updateThumbPosition : function() {
      var pos;
      if( this._selection >= ( this._maximum - this._thumbWidth ) ) {
        pos = org.eclipse.swt.widgets.Slider.BUTTON_WIDTH + this._pxStep
            * ( this._maximum - this._minimum - this._thumbWidth );
        this._selection = this._maximum - this._thumbWidth;
      } else if( this._selection <= this._minimum ) {
        pos = org.eclipse.swt.widgets.Slider.BUTTON_WIDTH;
        this._selection = this._minimum;
      } else {
        pos = org.eclipse.swt.widgets.Slider.BUTTON_WIDTH + this._pxStep
            * ( this._selection - this._minimum );
      }
      if( this._horizontal ) {
        this._thumb.setLeft( pos );
      } else {
        this._thumb.setTop( pos );
      }
      if( this._readyToSendChanges ) {
        this._readyToSendChanges = false;
        // Send changes
        qx.client.Timer.once( this._sendChanges, this, 500 );
        // Starting the auto repeat functionality after a 250 ms delay
        qx.client.Timer.once( this._scrollTimerStart, this, 250 );
      }
    },

    _updateLineSize : function() {
      if( this._horizontal ) {
        this._line.setWidth( this.getWidth() - 2
                           * org.eclipse.swt.widgets.Slider.BUTTON_WIDTH );
        this._line.setHeight( this.getHeight() );
      } else {
        this._line.setWidth( this.getWidth() );
        this._line.setHeight( this.getHeight() - 2
                            * org.eclipse.swt.widgets.Slider.BUTTON_WIDTH );
      }
    },

    _updateButtonsSize : function() {
      if( this._horizontal ) {
        this._minButton.setHeight( this.getHeight() );
        this._maxButton.setHeight( this.getHeight() );
      } else {
        this._minButton.setWidth( this.getWidth() );
        this._maxButton.setWidth( this.getWidth() );
      }
    },

    _getSelectionFromThumbPosition : function( position ) {
      var sel = ( position - org.eclipse.swt.widgets.Slider.BUTTON_WIDTH )
              / this._pxStep + this._minimum;
      sel = Math.round( sel );
      var sel_final;
      if( sel < this._minimum ) {
        sel_final = this._minimum;
      } else if( sel > ( this._maximum - this._thumbWidth ) ) {
        sel_final = this._maximum - this._thumbWidth;
      } else {
        sel_final = sel;
      }
      return sel_final;
    },

    _sendChanges : function() {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = widgetManager.findIdByWidget( this );
        req.addParameter( id + ".selection", this._selection );
        if( this._hasSelectionListener ) {
          req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
          req.send();
        }
        this._readyToSendChanges = true;
      }
    },

    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },

    setSelection : function( value ) {
      this._selection = value;
      this._updateThumbPosition();
    },

    setMinimum : function( value ) {
      this._minimum = value;
      this._updateThumbSize();
    },

    setMaximum : function( value ) {
      this._maximum = value;
      this._updateThumbSize();
    },

    setIncrement : function( value ) {
      this._increment = value;
    },

    setPageIncrement : function( value ) {
      this._pageIncrement = value;
    },

    setThumb : function( value ) {
      this._thumbWidth = value;
      this._updateThumbSize();
    }
  }
} );
