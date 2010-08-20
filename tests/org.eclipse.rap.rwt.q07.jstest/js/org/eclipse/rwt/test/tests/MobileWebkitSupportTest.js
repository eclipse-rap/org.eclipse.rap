/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.MobileWebkitSupportTest", {
  extend : qx.core.Object,

  members : {

    TARGETENGINE : [ "webkit" ],
    TARGETPLATFORM : [ "iphone", "ipad" ],
    
    testTabHighlightHidden : function() {
      var head = document.childNodes[ 0 ].childNodes[ 0 ];
      var headertext = head.innerHTML;
      var expected = "* { -webkit-tap-highlight-color: rgba(0,0,0,0); }";
      assertTrue( headertext.indexOf( expected ) != -1 );
    },
    
    testFakeTouchEvents : function() {
      var div = document.createElement( "div" );
      document.body.appendChild( div );
      var log = [];
      var logger = function( event ) {
        log.push( event.type );
      }
      div.ontouchstart = logger;  
      div.ontouchmove = logger;
      div.ontouchend = logger;
      div.ontouchcancel = logger;
      this.touch( div, "touchstart" );
      this.touch( div, "touchmove" );
      this.touch( div, "touchend" );
      this.touch( div, "touchcancel" );
      var expected = [ "touchstart", "touchmove", "touchend", "touchcancel" ];
      assertEquals( expected, log );
      document.body.removeChild( div );
    },
    
    testFakeTouchEventsTargets : function() {
      var div = document.createElement( "div" );
      document.body.appendChild( div );
      var log = [];
      var logger = function( event ) {
        log.push( event.target );
      }
      div.ontouchstart = logger;  
      div.ontouchmove = logger;
      div.ontouchend = logger;
      div.ontouchcancel = logger;
      this.touch( div, "touchstart" );
      this.touch( div, "touchmove" );
      this.touch( div, "touchend" );
      this.touch( div, "touchcancel" );
      var expected = [ div, div, div, div ];
      assertEquals( expected, log );
      document.body.removeChild( div );
    },

    testCreateTouch : function() {
      var div = document.createElement( "div" );
      var touch = this.createTouch( div, 3, 6 );
      assertTrue( touch instanceof Touch );
      assertEquals( 3, touch.screenX );
      assertEquals( 6, touch.screenY );
      assertIdentical( div, touch.target );
    },

    testCreateTouchList : function() {
      var touches = [ new Touch(), new Touch() ];
      var list = this.createTouchList( touches );
      assertTrue( list instanceof TouchList );
      assertEquals( 2, list.length );
      assertIdentical( touches[ 0 ], list.item( 0 ) );
      assertIdentical( touches[ 1 ], list.item( 1 ) );
    },

    testCreateTouchEvent : function() {
      var list = this.createTouchList( [] );
      var event = this.createTouchEvent( "touchstart", list );
      assertEquals( "touchstart", event.type );
      assertIdentical( list, event.touches );
    },

    testFakeTouchEventsTouchNumber : function() {
      var div = document.createElement( "div" );
      document.body.appendChild( div );
      var log = [];
      var logger = function( event ) {
        log.push( event.touches.length );
      }
      div.ontouchstart = logger;  
      div.ontouchmove = logger;
      div.ontouchend = logger;
      div.ontouchcancel = logger;
      this.touch( div, "touchstart" );
      this.touch( div, "touchmove", 0 );
      this.touch( div, "touchend", 3 );
      this.touch( div, "touchcancel",4  );
      var expected = [ 1, 0, 3, 4 ];
      assertEquals( expected, log );
      document.body.removeChild( div );
    },

    testFakeTouchEventsTouchList : function() {
      var div = document.createElement( "div" );
      var touches = [ new Touch(), new Touch() ];
      document.body.appendChild( div );
      var log = [];
      var logger = function( event ) {
        log.push( event.touches );
      }
      div.ontouchstart = logger;  
      div.ontouchmove = logger;
      div.ontouchend = logger;
      div.ontouchcancel = logger;
      this.touch( div, "touchstart", touches );
      assertEquals( 2, log[ 0 ].length );
      assertEquals( touches[ 0 ], log[ 0 ].item( 0 ) );
      assertEquals( touches[ 1 ], log[ 0 ].item( 1 ) );
      document.body.removeChild( div );
    },

    testFakeGestureEvent : function() {
      var div = document.createElement( "div" );
      var touches = [ new Touch(), new Touch() ];
      document.body.appendChild( div );
      var log = [];
      var logger = function( event ) {
        log.push( event.type );
      }
      div.ontouchstart = logger;  
      div.ontouchmove = logger;
      div.ontouchend = logger;
      div.ontouchcancel = logger;
      div.ongesturestart = logger;
      div.ongesturechange = logger;
      div.ongestureend = logger;
      this.gesture( div, "gesturestart" );
      this.gesture( div, "gesturechange" );
      this.gesture( div, "gestureend" );
      var expected = [
        "touchstart",
        "gesturestart",
        "touchstart",
        "gesturechange",
        "touchmove",
        "gestureend",
        "touchend",
      ];
      assertEquals( expected, log );
      document.body.removeChild( div );
    },

    testFakeGestureEventTouche : function() {
      var div = document.createElement( "div" );
      var touches = [ new Touch(), new Touch() ];
      document.body.appendChild( div );
      var log = [];
      var logger = function( event ) {
        log.push( event.touches.length );
      }
      div.ontouchstart = logger;  
      div.ontouchmove = logger;
      div.ontouchend = logger;
      this.gesture( div, "gesturestart" );
      this.gesture( div, "gesturechange" );
      this.gesture( div, "gestureend" );
      var expected = [ 1, 2, 2, 2 ];
      assertEquals( expected, log );
      document.body.removeChild( div );
    },

    /////////
    // Helper
    
    createTouch : function( target, x, y ) {
      var constr = function() {
        this.screenX = x;
        this.screenY = y;
        this.target = target;
      };
      constr.prototype = Touch.prototype;
      return new constr();
    },
    
    createTouchList : function( touches ) {
      // Note: "TouchList.prototype.constructor.apply" does NOT work. 
      var args = [];
      for( var i = 0; i < touches.length; i++ ) {
        args.push( "touches[ " + i + "]" );
      }
      return eval( "new TouchList(" + args.join() + ")" ); 
    },
    
    createTouchEvent : function( type, touchList ) {
      // Note: the screen/client values are not used in real touch-events.
      var result = document.createEvent( "TouchEvent" );
      result.initTouchEvent(
        type,
        true, //canBubble
        true, //cancelable
        window, //view
        0, //detail
        0, //screenX
        0, //screenY
        0, //clientX
        0, //clientY
        false, //ctrlKey
        false, //altKey
        false, //shiftKey
        false, //metaKey
        touchList, //touches
        touchList, //targetTouches
        touchList, //changedTouches
        0, //scale
        0 //rotation
      );
      return result;
    },

	  createGestureEvent : function( type, target ) {
	    // Note: the screen/client values are not used in real touch-events.
      var result = document.createEvent( "GestureEvent" );
      result.initGestureEvent(
        type,
        true, // canBubble,
        true, // cancelable
        window, // view
        0, // detail
        0, // screenX
        0, // screenY
        0, // clientX
        0, // clientY
        false, // ctrlKey
        false, // boolean altKey
        false, // shiftKey
        false, // metaKey
        target,
        0, // scale,
        0 // rotation
      );
      return result;
    },
    
    touch : function( node, type, touchesNumberOrArray ) {
      //var rect = node.getBoundingClientRect();
      var touches;
      if( touchesNumberOrArray instanceof Array ) {
        touches = touchesNumberOrArray;
      } else {
        touches = []; 
        var number = 1;
        if( typeof touchesNumberOrArray === "number" ) {
          number = touchesNumberOrArray;
        }
        while( touches.length < number ) { 
          touches.push( new Touch() );
        }
      }
      var touchList = this.createTouchList( touches );
      var event = this.createTouchEvent( type, touchList );
      node.dispatchEvent( event );
    },
    
    gesture : function( node, type ) {
      var event = this.createGestureEvent( type, node )
      var touchType = "";
      switch( type ) {
        case "gesturestart":
          touchType = "touchstart";
        break;
        case "gesturechange":
          touchType = "touchmove";
        break;
        case "gestureend":
          touchType = "touchend";
        break;
      }
      if( type === "gesturestart" ) {
        this.touch( node, touchType, 1 );
      }
      node.dispatchEvent( event );
      this.touch( node, touchType, 2 );
      // NOTE: there should actually be two "touchend" (unless the user
      // raises both finger exactly at once), but due to a bug webkit always
      // reports all touches to have ended, even if only one of several ended.
    }

  }

} );