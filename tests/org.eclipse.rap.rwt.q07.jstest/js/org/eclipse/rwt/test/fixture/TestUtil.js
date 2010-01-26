/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.fixture.TestUtil", {
  type : "static",

  statics : {
    
    //////
    // DOM
    
   getElementBounds : function( node ) {
      var style = node.style;
      if( style.cssText.indexOf( "%" ) != -1 ) {
        throw( "getElementBounds does not support '%'!" );
      }
      var ret = {
        top : parseInt( style.top ),
        left : parseInt( style.left ),
        width : parseInt( style.width ),
        height : parseInt( style.height )        
      };
      try {
        var ps = node.parentNode.style;
        if( ps.cssText.indexOf( "%" ) != -1 ) {
          throw( "getElementBounds does not support '%'!" );
        }
        ret.right =   parseInt( ps.width ) 
                    - parseInt( ps.borderLeftWidth || 0 ) 
                    - parseInt( ps.borderRightWidth || 0 ) 
                    - ( ret.left + ret.width )
        ret.bottom =   parseInt( ps.height ) 
                     - parseInt( ps.borderTopWidth || 0 ) 
                     - parseInt( ps.borderBottomWidth || 0 ) 
                     - ( ret.top + ret.height );
      } catch( e ) {
        this.printStackTrace();
        throw( " could not get bounds: no parentNode!" );
      }
      return ret;
    },
    
    hasElementOpacity : function( node ) {
      return node.style.cssText.search( /opacity/i  ) != -1;
    },
    
    getCssBackgroundImage : function( node ) {
      var ret = "";
      if( node.style.filter && node.style.filter.indexOf( "src='" ) != -1 ) {
        var filter = node.style.filter;
        var startStr = filter.indexOf( "src='" ) + 5;
        var stopStr = filter.indexOf( "'", startStr );
        ret = filter.slice( startStr, stopStr );
      } else if(   node.style.backgroundImage 
                && node.style.backgroundImage.indexOf( 'url(' ) != -1 ) 
      {
        ret = node.style.backgroundImage.slice( 4, -1 );              
      }
      // Webkit re-writes the url in certain situations: 
      if(    ret.length > 0 
          && ( ret == document.URL ) ) {
        ret = "";
      }
      return ret;
    },
        
    getCssBackgroundColor : function( widget ) {      
      var inner = widget._getTargetNode().style.backgroundColor;
      var outer = widget.getElement().style.backgroundColor;
      return ( ( inner || outer ) || null );      
    },    
    
    getElementSelectable : function( node ) {
      return node.style.cssText.search( "user-select: none" ) == -1;
    },
    
    /////////////////////////////
    // Event handling - DOM layer
    
    click : function( widget ) {
      this.clickDOM( widget._getTargetNode() );      
    },
    
    clickDOM : function( node ) {
      var left = qx.event.type.MouseEvent.buttons.left;
      this.fakeMouseEventDOM( node, "mousedown", left );
      this.fakeMouseEventDOM( node, "mouseup", left );
      this.fakeMouseEventDOM( node, "click", left );
    },
      
    rightClick : function( widget ) {
      var right = qx.event.type.MouseEvent.buttons.right;
      var node = widget._getTargetNode();
      this.fakeMouseEventDOM( node, "mousedown", right );
      this.fakeMouseEventDOM( node, "mouseup", right );
      this.fakeMouseEventDOM( node, "click", right );
      this.fakeMouseEventDOM( node, "contextmenu", right );      
    },

    fakeMouseEventDOM : function( node, type, button, left, top ) {
      if( typeof node == "undefined" ) {
        throw( "Error in fakeMouseEventDOM: node not defined! " );
      }
      if( typeof left == "undefined" ) {
        left = 0;
      }
      if( typeof top == "undefined" ) {
        top = 0;
      }
      var clientX = left;
      var clientY = top;
      if( qx.core.Client.getEngine() == "mshtml" ) {
        clientX -= qx.bom.Viewport.getScrollLeft(window);
        clientY -= qx.bom.Viewport.getScrollTop(window);
      }
      var domEvent = {
        "type" : type,
        "target" : node,
        "button" : button,
        "pageX" : left,
        "pageY" : top,
        "clientX" : clientX,
        "clientY" : clientY,
        "screenX" : left,
        "screenY" : top
      } 
      qx.event.handler.EventHandler.getInstance().__onmouseevent( domEvent );
    },

    /////////////////////////////
    // Event handling - Qooxdoo
        
    mouseOver : function( widget ) {
      this.fakeMouseEvent( widget, "mouseover" );
    },
    
    mouseMove : function( widget ) {
      this.fakeMouseEvent( widget, "mousemove" );
    },
    
    mouseOut : function( widget ) {
      this.fakeMouseEvent( widget, "mouseout" );
    },
    
    mouseFromTo : function( from, to ) {
      this.mouseMove( from );
      this.mouseOut( from );
      this.mouseOver( to );      
      this.mouseMove( to );
    },
    
    fakeMouseClick : function( widget, left, top ) {
      this.fakeMouseEvent( widget, "mousedown", left, top );
      this.fakeMouseEvent( widget, "mouseup", left, top );
      this.fakeMouseEvent( widget, "click", left, top );
    },
    
    fakeMouseEvent : function( widget, type, left, top ) {
      if( !widget._isCreated ) {
        throw( "Error in testUtil.fakeMouseEvent: widget is not created" );
      }
      var left = left ? left : 0;
      var top = top ? top : 0; 
      var domEv = {
        "type" : type,
        screenX : left,
        screenY : top,
        clientX : left,
        clientY : top,
        pageX : left,
        pageY : top
      };
      var ev = new qx.event.type.MouseEvent(
        type, 
        domEv,
        widget._getTargetNode(), 
        widget, 
        widget, 
        null
      );
      ev.setButton( qx.event.type.MouseEvent.C_BUTTON_LEFT ); 
      widget.dispatchEvent( ev );
    },
    
    press : function( widget, key, checkActive ) {
      this.fakeKeyEvent( widget, "keydown", key, checkActive );
      this.fakeKeyEvent( widget, "keypress", key, checkActive );
      this.fakeKeyEvent( widget, "keyinput", key, checkActive );
      this.fakeKeyEvent( widget, "keyup", key, checkActive );
    },    
    
    fakeKeyEvent : function( widget, type, key, checkActive  ) {
      if( !widget._isCreated ) {
        throw( "Error in fakeKeyEvent: " + widget + " is not created" );
      }
      if( !checkActive || this.isActive( widget ) ) {
        var domEv = {
          "type" : type,
          preventDefault : function(){}
        };
        var ev = new qx.event.type.KeyEvent(
          type, 
          domEv, 
          widget._getTargetNode(), 
          widget, 
          widget, 
          null, 
          "", 
          key 
        );      
        widget.dispatchEvent( ev );
      } else {
        widget.warn( type + " not possible: " + widget.__dbKey + " not focused!" );
      }
    },
    
    ////////////////
    // client-server
        
    initRequestLog : function() {
      var server = org.eclipse.rwt.test.fixture.RAPServer.getInstance();
      org.eclipse.rwt.test.fixture.TestUtil.clearRequestLog();
      server.setRequestHandler( function( message ) {
        org.eclipse.rwt.test.fixture.TestUtil._requestLog.push( message );
        return "";
      } );
    },
    
    getRequestLog : function() {
      return this._requestLog;
    },
    
    getRequestsSend : function() {
      return this._requestLog.length;
    },
    
    clearRequestLog : function() {
      org.eclipse.swt.Request.getInstance().send();        
      this._requestLog = [];
    },

    getMessage : function(){
      return this.getRequestLog()[ 0 ];
    },    
    
    ////////
    // Timer
    
    /**
     * Kills the actual timer-functionality, as it could cause problems
     * with debugging, calls to "once" are only logged
     */   
    prepareTimerUse : function() {
      qx.client.Timer.prototype._applyEnabled = function(){};
      qx.client.Timer._onceCallsLog = [];
      qx.client.Timer.once = function( func, obj, timeout ) {
        var source = arguments.callee.caller;
        this._onceCallsLog.push( [ func, obj, timeout, source ] );
      } 
    },
    
    getTimerOnceLog : function() {
      return qx.client.Timer._onceCallsLog;
    },
     
    clearTimerOnceLog : function() {
      qx.client.Timer._onceCallsLog = [];
    }, 
    
    forceTimerOnce : function() {
      // TODO [tb] : sort order by time
      var log = qx.client.Timer._onceCallsLog;
      for( var i = 0; i < log.length; i++ ) {
        log[ i ][ 0 ].call( log[ i ][ 1 ] );
      }
      qx.client.Timer._onceCallsLog = [];
    },
    
    forceInterval : function( timer ) {
      if( !timer.getEnabled() ) {
        throw( "Timer is not running!" );
      }
      // this only works if the timer is enabled:
      timer._oninterval();
    },
    
    ////////
    // Misc
    
    isFocused : function( widget ) {
      return widget == qx.event.handler.EventHandler.getInstance().getFocusRoot().getFocusedChild(); 
    },
    
    isActive: function( widget ) {
      return widget == qx.event.handler.EventHandler.getInstance().getFocusRoot().getActiveChild(); 
    },
    
    flush : function() {
      qx.ui.core.Widget.flushGlobalQueues();
    },
    
    getDocument : function() {
      return qx.ui.core.ClientDocument.getInstance();
    },
    
    preventFlushs : function( value ) {
      // this only works if the TestRunner-function"_disableAutoFlush" 
      // has been called previously. (Happens in TestRunner.run)
      qx.ui.core.Widget.__allowFlushs = !value;
    }
     
  }
  
});