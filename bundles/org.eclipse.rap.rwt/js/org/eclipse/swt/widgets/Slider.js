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

/**
 * This class provides the client-side counterpart for
 * org.eclipse.swt.widgets.Slider.
 */
qx.Class.define( "org.eclipse.swt.widgets.Slider", {
  extend : org.eclipse.swt.widgets.AbstractSlider,

  construct : function( style ) {
    this.base( arguments, qx.lang.String.contains( style, "horizontal" ) );
    this._hasSelectionListener = false;
    this._requestScheduled = false;
    this.addEventListener( "contextmenu", this._onContextMenu, this );
    this.addEventListener( "keypress", this._onKeyPress, this );
  },

  statics : {

    SEND_DELAY : 50,
    
    _isNoModifierPressed : function( evt ) {
      return    !evt.isCtrlPressed() 
             && !evt.isShiftPressed() 
             && !evt.isAltPressed() 
             && !evt.isMetaPressed();      
    }

  },

  members : {

    _configureAppearance : function() {
      this.setAppearance( "slider" );
      this._thumb.setAppearance( "slider-thumb" );
      this._minButton.setAppearance( "slider-min-button" );
      this._maxButton.setAppearance( "slider-max-button" );
    },

    setSelection : function( value ) {
      this._setSelection( value );
    },

    setMinimum : function( value ) {
      this._setMinimum( value );
    },

    setMaximum : function( value ) {
      this._setMaximum( value );
    },

    setIncrement : function( value ) {
      this._setIncrement( value );
    },

    setPageIncrement : function( value ) {
      this._setPageIncrement( value );
    },

    setThumb : function( value ) {
      this._setThumb( value );
    },
    
    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },
    
    //////////////
    // Overwritten
    
    _setSelection : function( value ) {
      this.base( arguments, value );
      this._scheduleSendChanges();
    },

    ////////////
    // Internals
    
    _onContextMenu : function( evt ) {
      var menu = this.getContextMenu();
      if( menu != null ) {
        menu.setLocation( evt.getPageX(), evt.getPageY() );
        menu.setOpener( this );
        menu.show();
        evt.stopPropagation();
      }
    },
    
    _onKeyPress : function( evt ) {
      var keyIdentifier = evt.getKeyIdentifier();
      var sel;
      if( org.eclipse.swt.widgets.Slider._isNoModifierPressed( evt ) ) {
        switch( keyIdentifier ) {
          case "Left":
            sel = this._selection - this._increment;
            evt.preventDefault();
            evt.stopPropagation();
            break;
          case "Down":
            if( this._horizontal ) {
              sel = this._selection - this._increment;  
            } else {
              sel = this._selection + this._increment;
            }
            evt.preventDefault();
            evt.stopPropagation();
            break;
          case "Right":
            sel = this._selection + this._increment;
            evt.preventDefault();
            evt.stopPropagation();
            break;
          case "Up": 
            if( this._horizontal ) {
              sel = this._selection + this._increment;
            } else {
              sel = this._selection - this._increment;    
            }
            evt.preventDefault();
            evt.stopPropagation();
            break; 
          case "Home":
            sel = this._minimum;
            evt.preventDefault();
            evt.stopPropagation();
            break;
          case "End":
            sel = this._maximum;
            evt.preventDefault();
            evt.stopPropagation();
            break;
          case "PageDown":
            if( this._horizontal ) {
              sel = this._selection - this._pageIncrement;
            } else {
              sel = this._selection + this._pageIncrement;
            }
            evt.preventDefault();
            evt.stopPropagation();
            break;
          case "PageUp":
            if( this._horizontal ) {
              sel = this._selection + this._pageIncrement;
            } else {
              sel = this._selection - this._pageIncrement;   
            }
            evt.preventDefault();
            evt.stopPropagation();
            break;
        }
        if( sel != undefined ) {
          if( sel < this._minimum ) {
            sel = this._minimum;
          } 
          if( sel > this._maximum ) {
            sel = this._maximum;
          }
          this.setSelection( sel );
          if( this._readyToSendChanges ) {
            this._readyToSendChanges = false;
            // Send changes
            qx.client.Timer.once( this._sendChanges,
                                  this,
                                  org.eclipse.swt.widgets.Slider.SEND_DELAY );
          }
        }
      }
    },
    
    _onMouseWheel : function( evt ) {
      if( this.getFocused() ) {
        this.base( arguments, evt );
        if( this._readyToSendChanges ) {
          this._readyToSendChanges = false;
          // Send changes
          qx.client.Timer.once( this._sendChanges, this, 500 );
        }
      }
    },

    // TODO [tb] : refactor to use only this for scheduling
    _scheduleSendChanges : function() {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        if( !this._requestScheduled ) {
          this._requestScheduled = true;
          // Send changes
          qx.client.Timer.once( this._sendChanges,
                                this,
                                org.eclipse.swt.widgets.Slider.SEND_DELAY );
          
        }
      }      
    },

    _sendChanges : function() {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var req = org.eclipse.swt.Request.getInstance();
      var id = widgetManager.findIdByWidget( this );
      req.addParameter( id + ".selection", this._selection );
      if( this._hasSelectionListener ) {
        req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
        org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
        req.send();
      }
      this._requestScheduled = false;
    }
        
  }

} );
