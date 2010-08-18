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
      var result = {
        top : this._parseLength( style.top ),
        left : this._parseLength( style.left ),
        width : this._parseLength( style.width ),
        height : this._parseLength( style.height )        
      };
      try {
        var ps = node.parentNode.style;
        result.right =   this._parseLength( ps.width ) 
                       - this._parseLength( ps.borderLeftWidth || 0 ) 
                       - this._parseLength( ps.borderRightWidth || 0 ) 
                       - ( result.left + result.width )
        result.bottom =   this._parseLength( ps.height ) 
                        - this._parseLength( ps.borderTopWidth || 0 ) 
                        - this._parseLength( ps.borderBottomWidth || 0 ) 
                        - ( result.top + result.height );
      } catch( e ) {
        throw( "Could not get bounds: no parentNode!" );
      }
      return result;
    },
    
    _parseLength : function( value ) {
      var result = value ? parseInt( value ) : 0;
      if(    result != 0 
          && typeof value == "string" 
          && value.indexOf( "px" ) == -1 ) 
      {
        throw "getElementBounds only supports \"px\" but found " + value;
      }
      return result;
    },
    
    getElementFont : function( element ) {
      var font = element.style.font;
      if( font == "" || typeof font != "string" ) {
        var fontData = [ 
          element.style.fontSize, 
          element.style.fontStyle,
          element.style.fontWeight,
          element.style.fontFamily
        ];
        font = fontData.join( " " );
      }
      return font;
    },
    
    hasElementOpacity : function( node ) {
      return node.style.cssText.search( /opacity/i  ) != -1;
    },
    
    getCssBackgroundImage : function( node ) {
      var result = "";
      if( node.style.filter && node.style.filter.indexOf( "src='" ) != -1 ) {
        var filter = node.style.filter;
        var startStr = filter.indexOf( "src='" ) + 5;
        var stopStr = filter.indexOf( "'", startStr );
        result = filter.slice( startStr, stopStr );
      } else if(   node.style.backgroundImage 
                && node.style.backgroundImage.indexOf( 'url(' ) != -1 ) 
      {
        result = node.style.backgroundImage.slice( 4, -1 );           
     }
      // Webkit re-writes the url in certain situations: 
      if( result.length > 0 && result == document.URL ) {
        result = "";
      }
      // Firefox writes quotation-marks into the URL
      if( result[ 0 ] == "\"" && result[ result.length -1 ] == "\"" ) {
        result = result.slice( 1, -1 );
      }
      return result;
    },
        
    getCssBackgroundColor : function( widget ) {      
      var inner = widget._getTargetNode().style.backgroundColor;
      var outer = widget.getElement().style.backgroundColor;
      return ( ( inner || outer ) || null );      
    },
    
    hasCssBorder : function( node ) {
      var result = false;
      var edge = [ "Top", "Left", "Bottom", "Right" ];
      for( var i=0; i < 4; i++ ) {
        if( !result ) {
          var width = parseInt( node.style[ "border" + edge[ i ] + "Width" ] );
          var color = node.style[ "border" + edge[ i ] + "Color" ];
          var style = node.style[ "border" + edge[ i ] + "Style" ];
          var hasWidth = !isNaN( width ) && width > 0; 
          var hasColor = color != "transparent"; 
          var hasStyle = style != "" && style != "none";
          result = hasWidth && hasColor && hasStyle; 
        }        
      } 
      return result;     
    },

    getElementSelectable : function( node ) {
      return node.style.cssText.search( "user-select: none" ) == -1;
    },
    
    /////////////////////////////
    // Event handling - DOM layer
    
    clickDOM : function( node ) {
      var left = qx.event.type.MouseEvent.buttons.left;
      this.fakeMouseEventDOM( node, "mousedown", left );
      this.fakeMouseEventDOM( node, "mouseup", left );
      this.fakeMouseEventDOM( node, "click", left );
    },
      
    shiftClickDOM : function( node ) {
      var left = qx.event.type.MouseEvent.buttons.left;
      var mod = qx.event.type.DomEvent.SHIFT_MASK;
      this.fakeMouseEventDOM( node, "mousedown", left, 0, 0, mod );
      this.fakeMouseEventDOM( node, "mouseup", left, 0, 0, mod );
      this.fakeMouseEventDOM( node, "click", left, 0, 0, mod );
    },
      
    ctrlClickDOM : function( node ) {
      var left = qx.event.type.MouseEvent.buttons.left;
      var mod = qx.event.type.DomEvent.CTRL_MASK;
      this.fakeMouseEventDOM( node, "mousedown", left, 0, 0, mod );
      this.fakeMouseEventDOM( node, "mouseup", left, 0, 0, mod );
      this.fakeMouseEventDOM( node, "click", left, 0, 0, mod );
    },
    
    hoverFromTo : function( fromNode, toNode ) {
      var outEvent = this.createfakeMouseEventDOM( "mouseout", 0 );
      outEvent.target = fromNode;
      outEvent.relatedTarget = toNode;
      this.fireFakeMouseEventDOM( outEvent );
      var overEvent = this.createfakeMouseEventDOM( "mouseover", 0 );
      overEvent.target = toNode;
      overEvent.relatedTarget = fromNode;
      this.fireFakeMouseEventDOM( overEvent );
    },
      
    fakeMouseEventDOM : function( node, type, button, left, top, mod ) {
      if( typeof node == "undefined" ) {
        throw( "Error in fakeMouseEventDOM: node not defined! " );
      }
      var domEvent 
        = this.createfakeMouseEventDOM( type, button, left, top, mod );
      domEvent.target = node;
      this.fireFakeMouseEventDOM( domEvent);
    },

    createfakeMouseEventDOM : function( type, button, left, top, mod ) {
      if( typeof left == "undefined" ) {
        left = 0;
      }
      if( typeof top == "undefined" ) {
        top = 0;
      }
      if( typeof mod == "undefined" ) {
        mod = 0;
      }
      var clientX = left;
      var clientY = top;
      if( qx.core.Client.getEngine() == "mshtml" ) {
        clientX -= qx.bom.Viewport.getScrollLeft(window);
        clientY -= qx.bom.Viewport.getScrollTop(window);
      }
      var domEvent = {
        "preventDefault" : function(){}, 
        "type" : type,
        "target" : null,
        "relatedTarget" : null,
        "which" : 1,
        "button" : button,
        "pageX" : left,
        "pageY" : top,
        "clientX" : clientX,
        "clientY" : clientY,
        "screenX" : left,
        "screenY" : top,
        "ctrlKey" : ( qx.event.type.DomEvent.CTRL_MASK & mod ) != 0,
        "altKey" :  ( qx.event.type.DomEvent.ALT_MASK  & mod ) != 0,
        "shiftKey" : ( qx.event.type.DomEvent.SHIFT_MASK  & mod ) != 0
      }
      return domEvent; 
    },

    fireFakeMouseEventDOM : function( domEvent ) {
      qx.event.handler.EventHandler.getInstance().__onmouseevent( domEvent );
    },

    fakeKeyEventDOM : function( node, eventType, stringOrKeyCode ) {
      var charCode = null;
      var keyCode = null;
      var isChar = typeof stringOrKeyCode == "string";
      if( isChar ) {
        charCode = stringOrKeyCode.charCodeAt( 0 );
      } else {
        keyCode = stringOrKeyCode;
      }
      var domEvent = {
        target : node,
        type : eventType,
        ctrlKey : false,
        altKey :  false,
        shiftKey : false,
        keyCode : keyCode,
        charCode : charCode,
        isChar: isChar,
        pageX: 0,
        pageY: 0,
        preventDefault : function(){},
        stopPropagation : function(){}
      };
      var handler = qx.event.handler.KeyEventHandler.getInstance();
      handler._idealKeyHandler( keyCode, charCode, eventType, domEvent );
    },
    
    /////////////////////////////
    // Event handling - Qooxdoo

    click : function( widget ) {
      this.clickDOM( widget._getTargetNode() );      
    },
    
    doubleClick : function( widget ) {
      var node = widget._getTargetNode();
      this.clickDOM( node );
      this.clickDOM( node );
      var left = qx.event.type.MouseEvent.buttons.left;
      this.fakeMouseEventDOM( node, "dblclick", left );
    },
    
    shiftClick : function( widget ) {
      var node = widget._getTargetNode();
      this.shiftClickDOM( node );
    },
    
    ctrlClick : function( widget ) {
      var node = widget._getTargetNode();
      this.ctrlClickDOM( node );
    },
    
    rightClick : function( widget ) {
      var right = qx.event.type.MouseEvent.buttons.right;
      var node = widget._getTargetNode();
      this.fakeMouseEventDOM( node, "mousedown", right );
      this.fakeMouseEventDOM( node, "mouseup", right );
      this.fakeMouseEventDOM( node, "click", right );
      this.fakeMouseEventDOM( node, "contextmenu", right );      
    },

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

    fakeWheel : function( widget, value ) {
      var ev = this._createQxMouseEvent( widget, "mousewheel" );
      ev.getWheelDelta = function(){ return value; };
      widget.dispatchEvent( ev );      
    },

    fakeMouseEvent : function( widget, type, left, top ) {
      var ev = this._createQxMouseEvent( widget, type, left, top );
      ev.setButton( qx.event.type.MouseEvent.C_BUTTON_LEFT ); 
      widget.dispatchEvent( ev );
    },

    _createQxMouseEvent : function( widget, type, left, top ) {
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
        pageY : top,
        preventDefault : function(){},
        stopPropagation : function(){}        
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
      return ev;
    },
    
    press : function( widget, key, checkActive, mod ) {
      this.fakeKeyEvent( widget, "keydown", key, checkActive, mod );
      this.fakeKeyEvent( widget, "keypress", key, checkActive, mod );
      this.fakeKeyEvent( widget, "keyinput", key, checkActive, mod );
      this.fakeKeyEvent( widget, "keyup", key, checkActive, mod );
    },
    
    shiftPress : function( widget, key, checkActive ) {
      var mod = qx.event.type.DomEvent.SHIFT_MASK;
      this.press( widget, key, checkActive, mod );
    },    
    
    ctrlPress : function( widget, key, checkActive ) {
      var mod = qx.event.type.DomEvent.CTRL_MASK;
      this.press( widget, key, checkActive, mod );
    },    
    
    altPress : function( widget, key, checkActive ) {
      var mod = qx.event.type.DomEvent.ALT_MASK;
      this.press( widget, key, checkActive, mod );
    },    

    fakeKeyEvent : function( widget, type, key, checkActive, mod ) {
      if( !widget._isCreated ) {
        throw( "Error in fakeKeyEvent: " + widget + " is not created" );
      }
      if( !checkActive || this.isActive( widget ) ) {
        if( typeof mod == "undefined" ) {
          mod = 0;
        }
        var domEv = {
          "type" : type,
          "ctrlKey" : ( qx.event.type.DomEvent.CTRL_MASK & mod ) != 0,
          "altKey" :  ( qx.event.type.DomEvent.ALT_MASK  & mod ) != 0,
          "shiftKey" : ( qx.event.type.DomEvent.SHIFT_MASK  & mod ) != 0,          
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
    
    //////////
    // Theming
    
    // assumes that the set appearance-theme does never change during tests     
    fakeAppearance : function( appearanceId, value ) {
      var manager = qx.theme.manager.Appearance.getInstance();
      var themeName = manager.getAppearanceTheme().name;
      var base = manager.getAppearanceTheme().appearances;
      if( typeof this._appearanceBackups[ appearanceId ] == "undefined" ) {
        if( base[ appearanceId ] ) {
          this._appearanceBackups[ appearanceId ] = base[ appearanceId ];
        }  else {
          this._appearanceBackups[ appearanceId ] = false;
        }
      }
      base[ appearanceId ] = value;
      this._clearAppearanceCache();
    },
    
    restoreAppearance : function() {
      var manager = qx.theme.manager.Appearance.getInstance();
      var base = manager.getAppearanceTheme().appearances;
      for( var appearanceId in this._appearanceBackups ) {
        var value = this._appearanceBackups[ appearanceId ];
        if( value === false ) {
          delete base[ appearanceId ];
        } else {
          base[ appearanceId ] = value;          
        }
      }
      this._appearanceBackups = {};
      this._clearAppearanceCache();
    },
    
    _appearanceBackups : {},
    
    _clearAppearanceCache : function() {
      var manager = qx.theme.manager.Appearance.getInstance()
      manager.__cache[ manager.getAppearanceTheme().name ] = {};
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
    },
    
    printStackTrace : function( qxObject ) {
      // Note: this works, regardless of current log-level
      var level = qx.log.Logger.ROOT_LOGGER._getDefaultFilter().getMinLevel();
      qx.log.Logger.ROOT_LOGGER.setMinLevel( qx.log.Logger.LEVEL_DEBUG );
      qxObject.printStackTrace();
      qx.log.Logger.ROOT_LOGGER.setMinLevel( level );
    }
     
  }
  
});