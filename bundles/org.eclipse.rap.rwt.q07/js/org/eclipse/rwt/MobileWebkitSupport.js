/*******************************************************************************
 * Copyright (c) 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.MobileWebkitSupport", {

  type : "static",
  
  statics : {
    _lastMouseOverTarget : null,
    _lastMouseDownTarget : null,
    _lastMouseDownPosition : null,
    _lastMouseClickTarget : null,
    _lastMouseClickTime : null,
    _mouseEnabled : true,
    _fullscreen : window.navigator.standalone,
    
    init : function() {
      if( this.isMobileWebkit() ) {
        this._hideTabHighlight();
        this._bindListeners();
        this._registerListeners();
        this._registerFilter();
      } 
    },
 
    isMobileWebkit : function() {
      var platform = qx.core.Client.getPlatform();
      var engine = qx.core.Client.getEngine();
      var isMobile = platform === "ipad" || platform === "iphone";
      return isMobile && engine === "webkit";
    },
    
    _isZoomed : function() {
      var vertical = window.orientation % 180 === 0;
      var width = vertical ? screen.width : screen.height;
      return window.innerWidth !== width;
    },
    
    _hideTabHighlight : function() {
      qx.html.StyleSheet.createElement( 
        " * { -webkit-tap-highlight-color: rgba(0,0,0,0); }"
      );
    },

    _bindListeners : function() {
       this.__onTouchEvent = qx.lang.Function.bind( this._onTouchEvent, this );
       this.__onGestureEvent 
         = qx.lang.Function.bind( this._onGestureEvent, this );
       this.__onOrientationEvent 
         = qx.lang.Function.bind( this._onOrientationEvent, this );
    },

    _registerListeners : function() {
      var target = document.body;      
      target.ontouchstart = this.__onTouchEvent;
      target.ontouchmove = this.__onTouchEvent;
      target.ontouchend = this.__onTouchEvent;
      target.ontouchcancel = this.__onTouchEvent;
      target.ongesturestart = this.__onGestureEvent;
      target.ongesturechange = this.__onGestureEvent;
      target.ongestureend = this.__onGestureEvent;
      target.onorientationchange = this.__onOrientationEvent;  
    },
    
    _registerFilter : function() {
      var eventHandler = qx.event.handler.EventHandler.getInstance();
      eventHandler.setMouseEventFilter( this._filterMouseEvents, this );
    },
    
    _filterMouseEvents : function( event ) {
      var result = typeof event.originalEvent === "object";
      if( !result ) {
        event.preventDefault();
        event.returnValue = false;
      }
      return result;
    },
    
    _onTouchEvent : function( domEvent ) {
      var type = domEvent.type;
      if( this._fullscreen ) {
        // Zoom is disabled in Fullscreen (by webkit), therefore
        // swipe or pinch gestures can be disabled completely:
        domEvent.preventDefault();
      }
      if( this._mouseEnabled ) {
        switch( type ) {
          case "touchstart":
            this._handleTouchStart( domEvent );
          break;
          case "touchend":
            this._handleTouchEnd( domEvent );
          break;
          case "touchmove":
            this._handleTouchMove( domEvent );
          break;
        }
      }
    },
    
    _getTouch : function( domEvent ) {
      var touch = domEvent.touches.item( 0 );
      if( touch === null ) {
        // Should happen at touchend (behavior seems unpredictable)
        touch = domEvent.changedTouches.item( 0 );
      }
      return touch;
    },
    
    _handleTouchStart : function( domEvent ) {
      var touch = this._getTouch( domEvent );
      var target = domEvent.target;
      var pos = [ touch.clientX, touch.clientY ];
      this._moveMouseTo( target, domEvent );
      this._lastMouseDownTarget = target;
      this._lastMouseDownPosition = pos;
      this._fireMouseEvent( "mousedown", target, domEvent, pos );
    },
        
    _handleTouchMove : function( domEvent ) {
      if( !this._isZoomed() ) {
        // Prevents swipe/scrolling when it's not useful:
        domEvent.preventDefault();
      }
      if( this._lastMouseDownPosition !== null ) {
        var oldPos = this._lastMouseDownPosition;
        var touch = this._getTouch( domEvent );
        var pos = [ touch.clientX, touch.clientY ];
        if(    Math.abs( oldPos[ 0 ] - pos[ 0 ] ) >= 9
            || Math.abs( oldPos[ 1 ] - pos[ 1 ] ) >= 9 ) {
          this._cancelMouseSession( domEvent );
        }
      }
    },
    
    _handleTouchEnd : function( domEvent ) {
      domEvent.preventDefault(); // Prevent tap-zoom
      var touch = this._getTouch( domEvent );
      var pos = [ touch.clientX, touch.clientY ];
      var target = domEvent.target;
      if( this._lastMouseDownTarget !== null ) {
        this._fireMouseEvent( "mouseup", target, domEvent, pos );
      }
      // Note: Currently this check won't work as expected because webkit
      // always reports the target from touchstart (in event and touch).
      // It stays on the speculation that this might be fixed in webkit. 
      if( this._lastMouseDownTarget === target ) {
        this._fireMouseEvent( "click", target, domEvent, pos );
        this._lastMouseDownTarget = null;
        this._lastMouseDownPosition = null;
        if( this._isDoubleClick( domEvent ) ) {
          this._lastMouseClickTarget = null;
          this._lastMouseClickTime = null;
          this._fireMouseEvent( "dblclick", target, domEvent, pos );
        } else {
          this._lastMouseClickTarget = target;
          this._lastMouseClickTime = ( new Date() ).getTime();
        }
      }
    },

    _isDoubleClick : function( domEvent ) {
      var target = domEvent.target;
      var result = false;
      if( this._lastMouseClickTarget === target ) {
        var diff = ( ( new Date() ).getTime() ) - this._lastMouseClickTime;
        result = diff < org.eclipse.swt.EventUtil.DOUBLE_CLICK_TIME;
      }
      return result; 
    },

    _onGestureEvent : function( domEvent ) {
      var type = domEvent.type;
      switch( type ) {
        case "gesturestart":
          this._disableMouse( domEvent );
        break;
        case "gestureend":
          this._enableMouse( domEvent );
        break;
      }
    },

    _onOrientationEvent : function( domEvent ) {
      // Nothing to do yet
    },

    ////////////////
    // emulate mouse

    _disableMouse : function( domEvent ) {
      // Note: Safari already does somthing similar to this (a touchevent 
      // that executes JavaScript will prevent further touch/gesture events),
      // but no in all cases, e.g. on a touchstart with two touches.
      this._cancelMouseSession( domEvent );
      this._mouseEnabled = false;
    },

    _cancelMouseSession : function( domEvent ) {
      var dummy = this._getDummyTarget();
      this._moveMouseTo( dummy, domEvent );
      if( this._lastMouseDownTarget !== null ) { 
        this._fireMouseEvent( "mouseup", dummy, domEvent, [ 0, 0 ] );
      }
      this._lastMouseDownTarget = null;
      this._lastMouseDownPosition = null;
    },

    // The target used to release the virtual mouse without consequences
    _getDummyTarget : function() {
      return qx.ui.core.ClientDocument.getInstance()._getTargetNode();
    },

    _enableMouse : function() {
      this._mouseEnabled = true;
    },

    _moveMouseTo : function( target, domEvent ) {
      var oldTarget = this._lastMouseOverTarget;
      if( oldTarget !== target ) {
        var pos = [ 0, 0 ];
        if( oldTarget !== null ) {
          this._fireMouseEvent( "mouseout", oldTarget, domEvent, pos ); 
        }
        this._lastMouseOverTarget = target;
        this._fireMouseEvent( "mouseover", target, domEvent, pos );
      }
    },

    _fireMouseEvent : function( type, target, originalEvent, coordiantes ) {
      var event = document.createEvent( "MouseEvent" );
      event.initMouseEvent( type, 
                            true, // bubbles 
                            true, //cancelable 
                            window, //view 
                            0, // detail 
                            coordiantes[ 0 ], // screenX 
                            coordiantes[ 1 ], //screenY 
                            coordiantes[ 0 ], //clientX 
                            coordiantes[ 1 ], //clientY 
                            false, //ctrlKey 
                            false, //altKey 
                            false, //shiftKey 
                            false, //metaKey 
                            qx.event.type.MouseEvent.buttons.left, 
                            null );
     event.originalEvent = originalEvent;
     target.dispatchEvent( event );
    } 

  }
    
} );

