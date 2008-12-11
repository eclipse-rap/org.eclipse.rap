/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
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
 * org.eclipse.swt.widgets.Scale.
 */
qx.Class.define( "org.eclipse.swt.widgets.Scale", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( style ) {
    this.base( arguments );
    this.setAppearance( "scale" );
    
    // Get styles
    this._horizontal = qx.lang.String.contains( style, "horizontal" );
    
    // Has selection listener
    this._hasSelectionListener = false;
    
    // Flag indicates that the next request can be sent
    this._readyToSendChanges = true;
    
    // Default values
    this._selection = 0;
    this._minimum = 0;
    this._maximum = 100;
    this._increment = 1;
    this._pageIncrement = 10;    
    this._pxStep = 1.34;
    
    // Base line
    this._line = new qx.ui.basic.Image;
    if( this._horizontal ) {
      this._line.addState( org.eclipse.swt.widgets.Scale.STATE_HORIZONTAL );
    }
    this._line.setAppearance( "scale-line" ); 
    this._line.setResizeToInner( true ); 
    this._line.addEventListener( "mousedown", this._onLineMouseDown, this );
    this.add( this._line );
    
    // Thumb
    this._thumb = new qx.ui.basic.Image;
    if( this._horizontal ) {
      this._thumb.addState( org.eclipse.swt.widgets.Scale.STATE_HORIZONTAL );
    }
    this._thumb.setAppearance( "scale-thumb" );
    this._thumb.addEventListener( "mousedown", this._onThumbMouseDown, this );
    this._thumb.addEventListener( "mousemove", this._onThumbMouseMove, this );
    this._thumb.addEventListener( "mouseup", this._onThumbMouseUp, this );
    // Fix IE Styling issues
    org.eclipse.swt.WidgetUtil.fixIEBoxHeight( this._thumb );
    this.add( this._thumb );    
    // Thumb offset
    this._thumbOffset = 0;
    
    // Min marker
    this._minMarker = new qx.ui.basic.Image;
    if( this._horizontal ) {
      this._minMarker.addState( org.eclipse.swt.widgets.Scale.STATE_HORIZONTAL );
    }
    this._minMarker.setAppearance( "scale-min-marker" );
    this.add( this._minMarker );
    
    // Max marker    
    this._maxMarker = new qx.ui.basic.Image;
    if( this._horizontal ) {
      this._maxMarker.addState( org.eclipse.swt.widgets.Scale.STATE_HORIZONTAL );
    }
    this._maxMarker.setAppearance( "scale-max-marker" );
    this.add( this._maxMarker );
    
    // Add events listeners
    if( this._horizontal ) {
      this.addEventListener( "changeWidth", this._onChangeWidth, this );
    } else {  
      this.addEventListener( "changeHeight", this._onChangeHeight, this );
    }
    this.addEventListener( "contextmenu", this._onContextMenu, this );
    this.addEventListener( "keypress", this._onKeyPress, this );
    this.addEventListener( "mousewheel", this._onMouseWheel, this );
    
    // Middle markers
    this._middleMarkers = new Array();
    this._updateMiddleMarkers();
  },
  
  destruct : function() {
    this._line.removeEventListener( "mousedown", this._onLineMouseDown, this );
    this._thumb.removeEventListener( "mousedown", this._onThumbMouseDown, this );
    this._thumb.removeEventListener( "mousemove", this._onThumbMouseMove, this );
    this._thumb.removeEventListener( "mouseup", this._onThumbMouseUp, this );
    if( this._horizontal ) {
      this.removeEventListener( "changeWidth", this._onChangeWidth, this );
    } else {  
      this.removeEventListener( "changeHeight", this._onChangeHeight, this );
    }
    this.removeEventListener( "contextmenu", this._onContextMenu, this );
    this.removeEventListener( "keypress", this._onKeyPress, this );
    this.removeEventListener( "mousewheel", this._onMouseWheel, this );
    this._disposeObjects( "_line",
                          "_thumb",
                          "_minMarker",
                          "_maxMarker" );
    // Clear and dispose markers
    for( var i = 0; i < this._middleMarkers.length; i++ ) {
      var marker = this._middleMarkers[ i ];      
      marker.dispose();
    }                      
  },  

  statics : {
    STATE_HORIZONTAL : "horizontal",
    PADDING : 8,
    MAX_MARKER_OFFSET : 12,    
    SCALE_LINE_OFFSET : 9,
    THUMB_OFFSET : 10,
    HALF_THUMB : 5,
    
    _isNoModifierPressed : function( evt ) {
      return    !evt.isCtrlPressed() 
             && !evt.isShiftPressed() 
             && !evt.isAltPressed() 
             && !evt.isMetaPressed();      
    }
  },
  
  members : {
    _onChangeWidth : function( evt ) {
      this._line.setWidth(   this.getWidth() 
                           - 2 * org.eclipse.swt.widgets.Scale.PADDING );
      this._maxMarker.setLeft(   this.getWidth() 
                               - org.eclipse.swt.widgets.Scale.MAX_MARKER_OFFSET );
      this._updateStep();
      this._updateThumbPosition();
      this._updateMiddleMarkers();
    },
    
    _onChangeHeight : function( evt ) {
      this._line.setHeight(   this.getHeight() 
                            - 2 * org.eclipse.swt.widgets.Scale.PADDING );
      this._maxMarker.setTop(   this.getHeight()
                              - org.eclipse.swt.widgets.Scale.MAX_MARKER_OFFSET );
      this._updateStep();
      this._updateThumbPosition();
      this._updateMiddleMarkers();
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
    
    _onKeyPress : function( evt ) {
      var keyIdentifier = evt.getKeyIdentifier();
      var sel;
      if( org.eclipse.swt.widgets.Scale._isNoModifierPressed( evt ) ) {
        switch( keyIdentifier ) {
          case "Left":
            sel = this._selection - this._increment;
            break
          case "Down":
            if( this._horizontal ) {
              sel = this._selection - this._increment;  
            } else {
              sel = this._selection + this._increment;
            }                     
            break;
          case "Right":
            sel = this._selection + this._increment; 
            break;
          case "Up": 
            if( this._horizontal ) {
              sel = this._selection + this._increment;
            } else {
              sel = this._selection - this._increment;    
            }                 
            break; 
          case "Home":
            sel = this._minimum;
            break;
          case "End":
            sel = this._maximum;
            break;
          case "PageDown":
            if( this._horizontal ) {
              sel = this._selection - this._pageIncrement;
            } else {
              sel = this._selection + this._pageIncrement;
            }           
            break;
          case "PageUp":
            if( this._horizontal ) {
              sel = this._selection + this._pageIncrement;
            } else {
              sel = this._selection - this._pageIncrement;   
            }            
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
            qx.client.Timer.once( this._sendChanges, this, 500 );
          }
        }
      }
    },
    
    _onMouseWheel : function( evt ) {
      var change = evt.getWheelDelta();
      var sel = this._selection - change;
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
        qx.client.Timer.once( this._sendChanges, this, 500 );
      }
    },
    
    _onLineMouseDown : function( evt ) {
      var pxSel;
      var mousePos;
      var sel;
      if( evt.isLeftButtonPressed() ){
        if( this._horizontal ) {
          pxSel
            = this._thumb.getLeft() + org.eclipse.swt.widgets.Scale.HALF_THUMB;
          mousePos
            = evt.getPageX() - qx.html.Location.getClientBoxLeft( this.getElement() );
        } else {
          pxSel 
            = this._thumb.getTop() + org.eclipse.swt.widgets.Scale.HALF_THUMB;
          mousePos
            = evt.getPageY() - qx.html.Location.getClientBoxTop( this.getElement() );
        }
        if( mousePos > pxSel ) {
          sel = this._selection + this._pageIncrement;         
        } else {
          sel = this._selection - this._pageIncrement;        
        }
        
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
          qx.client.Timer.once( this._sendChanges, this, 500 );
        }
      }
    },
    
    _onThumbMouseDown : function( evt ) {
      var mousePos;
      if( evt.isLeftButtonPressed() ){
        if( this._horizontal ) {        
          mousePos = evt.getPageX() 
            - qx.html.Location.getClientBoxLeft( this.getElement() );
          this._thumbOffset = mousePos - this._thumb.getLeft();
        } else {        
          mousePos = evt.getPageY()
            - qx.html.Location.getClientBoxTop( this.getElement() );
          this._thumbOffset = mousePos - this._thumb.getTop();  
        }
        this._thumb.setCapture(true);
      }
    },
    
    _onThumbMouseMove : function( evt ) {
      var mousePos;
      if( this._thumb.getCapture() ) {
        if( this._horizontal ) {        
          mousePos
            = evt.getPageX() 
            - qx.html.Location.getClientBoxLeft( this.getElement() );
        } else {        
          mousePos
            = evt.getPageY()
            - qx.html.Location.getClientBoxTop( this.getElement() );
        }
        var sel = this._getSelectionFromThumbPosition( mousePos - this._thumbOffset );
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
      this._thumb.setCapture( false );
    },
        
    _updateMiddleMarkers : function() {
      // Clear and dispose markers
      for( var i = 0; i < this._middleMarkers.length; i++ ) {
        var marker = this._middleMarkers[ i ];
        this.remove( marker );
//        marker.dispose();
        marker.destroy();
      }
      
      // Create and add new markets
      this._middleMarkers = new Array();
      var markersNum = Math.round( ( this._maximum - this._minimum ) / this._pageIncrement ) - 1;
      for( var i = 0; i < markersNum; i++ ) {
        var marker = new qx.ui.basic.Image;
        var pos =   org.eclipse.swt.widgets.Scale.PADDING
                  + org.eclipse.swt.widgets.Scale.HALF_THUMB
                  + ( i + 1 ) * this._pageIncrement * this._pxStep;
        if( this._horizontal ) {
          marker.addState( org.eclipse.swt.widgets.Scale.STATE_HORIZONTAL );          
          marker.setLeft( pos );
        } else {
          marker.setTop( pos );
        }
        marker.setAppearance( "scale-middle-marker" );        
        this.add( marker );       
        this._middleMarkers[ i ] = marker;
      }
    },
    
    _updateStep : function() {
      var padding =   org.eclipse.swt.widgets.Scale.PADDING
                    + org.eclipse.swt.widgets.Scale.HALF_THUMB;
      if( this._horizontal ) {
        this._pxStep
          = ( this.getWidth() - 2 * padding ) / ( this._maximum - this._minimum );
      } else {
        this._pxStep
          = ( this.getHeight() - 2 * padding ) / ( this._maximum - this._minimum );
      }
    },
    
    _updateThumbPosition : function() {
      var pos =   org.eclipse.swt.widgets.Scale.PADDING
                + this._pxStep * ( this._selection - this._minimum );
      if( this._horizontal ) {
        this._thumb.setLeft( pos );
      } else {
        this._thumb.setTop( pos );
      }
    },
    
    _getSelectionFromThumbPosition : function( position ) {
      var sel =   ( position - org.eclipse.swt.widgets.Scale.PADDING )
                / this._pxStep + this._minimum;
      sel = Math.round( sel );
      if( sel < this._minimum ) {
        sel = this._minimum;
      } 
      if( sel > this._maximum ) {
        sel = this._maximum;
      }
      return sel;
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
      this._updateStep();
      this._updateThumbPosition();
      this._updateMiddleMarkers();
    },
    
    setMaximum : function( value ) {
      this._maximum = value;
      this._updateStep();
      this._updateThumbPosition();
      this._updateMiddleMarkers();
    },
    
    setIncrement : function( value ) {
      this._increment = value;
    },
    
    setPageIncrement : function( value ) {
      this._pageIncrement = value;
      this._updateMiddleMarkers();
    }
  }
} );
