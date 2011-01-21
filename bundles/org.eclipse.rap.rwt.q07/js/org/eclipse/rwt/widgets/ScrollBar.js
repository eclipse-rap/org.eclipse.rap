/*******************************************************************************
 * Copyright (c) 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.widgets.ScrollBar", {
  extend : org.eclipse.swt.widgets.AbstractSlider,

  construct : function( horizontal ) {
    this.base( arguments, horizontal );
    this._idealValue = 0;
    this._selectionFactor = 1;
    this._lastDispatchedValue = 0;
    this._mergeEvents = false;
    this._renderSum = 0;
    this._renderSamples = 0;
    this._eventTimerId = null;
    this._setMinimum( 0 );
    this._minThumbSize = org.eclipse.rwt.widgets.ScrollBar.MIN_THUMB_SIZE;
    this.setIncrement( 20 );
    this.setAppearance( "slider" );
    this._thumb.setAppearance( "slider-thumb" );
    this._minButton.setAppearance( "slider-min-button" );
    this._maxButton.setAppearance( "slider-max-button" );
    this.addEventListener( "click", this._stopEvent, this );
    this.addEventListener( "dblclick", this._stopEvent, this );
    this._eventTimer = null;
    this._setStates();
  },

  destruct : function() {
    if( this._eventTimer != null ) {
      this._eventTimer.dispose()
      this._eventTimer = null;
    }
  },

  statics : {
    BAR_WIDTH : 15,
    MERGE_THRESHOLD : 4,
    MIN_THUMB_SIZE : 8,
    
    _nativeWidth : null,
    
    getNativeScrollBarWidth : function() {
      if( this._nativeWidth === null ) {
        var dummy = document.createElement( "div" );
        dummy.style.width = "100px";
        dummy.style.height = "100px";
        dummy.style.overflow = "scroll";
        dummy.style.visibility = "hidden";
        document.body.appendChild( dummy );
        this._nativeWidth = dummy.offsetWidth - dummy.clientWidth;
        document.body.removeChild(dummy);
      }
      return this._nativeWidth;
    }
  },
  
  events: {
    "changeValue" : "qx.event.type.Event"
  },

  members : {
    
    //////
    // API

    setValue : function( value ) {
      this._idealValue = value;
      this._setSelection( value * this._selectionFactor );
    },
    
    getValue : function( value ) {
      return this._selection / this._selectionFactor;
    },

    setMaximum : function( value ) {
      this._setMaximum( value );
      if( this._idealValue !== null ) {
        this._setSelection( this._idealValue );
      }
      this._updateThumbLength();
    },
    
    getMaximum : function() {
      return this._maximum;
    },
    
    setIncrement : function( value ) {
      this._setIncrement( value );
      this._updatePageIncrement();
    },

    setMergeEvents : function( value ) {
      if( !value && this._mergeEvents ) {
        this.warn( "mergeEvents can not be set to false" );
      } else if( value ) {
        this._mergeEvents = true;
        this._eventTimer = new qx.client.Timer( 125 );
        this._eventTimer.addEventListener( "interval", 
                                           this._dispatchValueChanged,
                                           this );
      }
    },

    getMergeEvents : function() {
      return this._mergeEvents;
    },

    autoEnableMerge : function( renderTime ) { 
      if( !this._mergeEvents && renderTime > 0 ) {
        this._renderSamples++;
        this._renderSum += renderTime;
        var avg = this._renderSum / this._renderSamples;
        var result = false;
        if( this._renderSamples > 2 ) {
          result = avg > 200;
        } else {
          result = renderTime > 700;
        }
        if( result ) {
          this.setMergeEvents( true );
        }
      }
    },  

    //////////////
    // Overwritten

    _onChangeSize : function() {
      this.base( arguments );
      this._updateThumbLength();
      this._updatePageIncrement();
      if( this._idealValue !== null ) {
        this._setSelection( this._idealValue );
      }
    },
    
    _updateThumbSize : function() {
      this.base( arguments );
      var size = this._getThumbSize();
      if( size < this._minThumbSize ) {
        this.addToQueue( "minThumbSize" );
      } else {
        this._selectionFactor = 1;
      }
    },

    _layoutPost : function( changes ) {
      this.base( arguments, changes );
      if( changes[ "minThumbSize" ] ) {
        if( this._maximum > 0 && this._getLineSize() > 0 ) {
          var size = this._getThumbSize();
          if( size < this._minThumbSize ) {
            var idealLength = this._getSliderSize();
            var newLength 
              = this._minThumbSize * this._maximum / this._getLineSize();
            this._setThumb( newLength );
            this._selectionFactor = ( this._maximum - newLength ) / ( this._maximum - idealLength );
          }
        }
      }
    },    

    _setSelection : function( value ) {
      if( value !== this._idealValue ) {
        this._idealValue = null;
      }
      this.base( arguments, value );
    },
    
    _selectionChanged : function() {
      this.base( arguments );
      if( this._getMergeCurrentEvent() ) {
        this._eventTimer.stop();
        this._eventTimer.start();
      } else {
        if( this._mergeEvents ) {
          this._eventTimer.stop();
        }
        this._dispatchValueChanged();
      }
    },
    
    ////////////
    // Internals

    _updateThumbLength : function() {
      this._setThumb( this._getSliderSize() );      
    },

    _updatePageIncrement : function() {
      this._setPageIncrement( this._getSliderSize() - this._increment );      
    },

    _stopEvent : function( event ) {
      event.stopPropagation();
      event.preventDefault();
    },

    _getMergeCurrentEvent : function() {
      var result = false;
      if( this._mergeEvents ) {
        var mergeThreshold = org.eclipse.rwt.widgets.ScrollBar.MERGE_THRESHOLD;
        var diff = Math.abs( this._lastDispatchedValue - this._selection );
        if( diff >= this._increment * mergeThreshold ) {
          result = true;
        }
      }
      return result;
    },
    
    _dispatchValueChanged : function() {
      this._lastDispatchedValue = this._selection;
      this.createDispatchEvent( "changeValue" );
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
    },

    _computePreferredInnerWidth : function() {
      return this._horizontal ? 0 : this._getScrollBarWidth();
    },

    _computePreferredInnerHeight : function() {
      return this._horizontal ? this._getScrollBarWidth() : 0;
    },

    _getScrollBarWidth : function() {
      return org.eclipse.rwt.widgets.ScrollBar.BAR_WIDTH;
    },
    
    _updateStepsize : function() {
      var oldValue = this._selection;
      this.base( arguments );
      if( oldValue !== this._selection ) {
        this._dispatchValueChanged();      
      }
    }

  }

} );
