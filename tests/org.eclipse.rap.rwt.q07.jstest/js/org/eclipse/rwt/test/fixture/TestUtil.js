/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
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
      try {
        var ps = node.parentNode.style;
      } catch( e ) {
        throw( "Could not get bounds: no parentNode!" );
      }
      var space = {}; 
      space.width =   this._parseLength( ps.width ) 
                    - this._parseLength( ps.borderLeftWidth || 0 ) 
                    - this._parseLength( ps.borderRightWidth || 0 ); 
      space.height =   this._parseLength( ps.height ) 
                     - this._parseLength( ps.borderTopWidth || 0 ) 
                     - this._parseLength( ps.borderBottomWidth || 0 );
      var result = {};
      result.width = this._parseLength( style.width );
      result.height = this._parseLength( style.height );
      if( style.right && !style.left ) {
        result.right = this._parseLength( style.right );
        result.left = space.width - ( result.right + result.width );
      } else {
        result.left = this._parseLength( style.left );
        result.right = space.width - ( result.left + result.width );
      } 
      if( style.bottom && !style.top ) {
        result.bottom = this._parseLength( style.bottom );
        result.top = space.height - ( result.bottom + result.height );
      } else {
        result.top = this._parseLength( style.top );
        result.bottom = space.height - ( result.top + result.height );
      } 
      return result;
    },
    
    getElementLayout : function( node ) {
      var bounds = this.getElementBounds( node );
      return [ bounds.left, bounds.top, bounds.width, bounds.height ];
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
    
    _createFakeDomEvent : function( target, type, mod ) {
      var result = {
        "preventDefault" : function(){},
        "stopPropagation" : function(){},
        "pageX" : 0,
        "pageY" : 0,
        "type" : type,
        "target" : target,
        "relatedTarget" : null,
        "ctrlKey" : ( qx.event.type.DomEvent.CTRL_MASK & mod ) != 0,
        "altKey" :  ( qx.event.type.DomEvent.ALT_MASK  & mod ) != 0,
        "shiftKey" : ( qx.event.type.DomEvent.SHIFT_MASK  & mod ) != 0
      };
      return result;       
    },
    
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
      var outEvent = this._createFakeMouseEventDOM( fromNode, "mouseout", 0 );
      outEvent.relatedTarget = toNode;
      this.fireFakeDomEvent( outEvent );
      var overEvent = this._createFakeMouseEventDOM( toNode, "mouseover", 0 );
      overEvent.relatedTarget = fromNode;
      this.fireFakeDomEvent( overEvent );
    },
      
    fakeMouseEventDOM : function( target, type, button, left, top, mod, filter ) {
      if( typeof target === "undefined" ) {
        throw( "Error in fakeMouseEventDOM: target not defined! " );
      }
      var domEvent = this._createFakeMouseEventDOM( target, type, button, left, top, mod );
      if( filter === true && this.isMobileWebkit() ) {
        delete domEvent.originalEvent;
      }
      this.fireFakeDomEvent( domEvent );
    },

    _createFakeMouseEventDOM : function( target, type, button, left, top, mod ) {
      // TODO [tb] : refactor to not overwrite paramters? 
      if( typeof left === "undefined" ) {
        left = 0;
      }
      if( typeof button === "undefined" ) {
        button = qx.event.type.MouseEvent.buttons.left;
      }
      if( typeof top === "undefined" ) {
        top = 0;
      }
      if( typeof mod === "undefined" ) {
        mod = 0;
      }
      if( typeof filter === "undefined" ) {
        filter = false;
      }
      var clientX = left;
      var clientY = top;
      if( org.eclipse.rwt.Client.getEngine() == "mshtml" ) {
        clientX -= qx.bom.Viewport.getScrollLeft( window );
        clientY -= qx.bom.Viewport.getScrollTop( window );
      }
      var which = null;
      switch( button ) {
        case qx.event.type.MouseEvent.buttons.left:
          which = 1;
        break;
        case qx.event.type.MouseEvent.buttons.middle:
          which = 2;
        break;
        case qx.event.type.MouseEvent.buttons.right:
          which = 3;
        break;
      }
      var domEvent = this._createFakeDomEvent( target, type, mod );
      domEvent.which = which;
      domEvent.button = button;
      domEvent.pageX = left;
      domEvent.pageY = top;
      domEvent.clientX = clientX;
      domEvent.clientY = clientY;
      domEvent.screenX = left;
      domEvent.screenY = top;
      domEvent.ctrlKey = ( qx.event.type.DomEvent.CTRL_MASK & mod ) != 0;
      domEvent.altKey = ( qx.event.type.DomEvent.ALT_MASK  & mod ) != 0;
      domEvent.shiftKey = ( qx.event.type.DomEvent.SHIFT_MASK  & mod ) != 0;
      if( this.isMobileWebkit() ) {
        domEvent.originalEvent = {};
      }
      return domEvent; 
    },

    fireFakeDomEvent : function( domEvent ) {
      var type = domEvent.type; 
      var handler = org.eclipse.rwt.EventHandler;
      switch( type ) {
        case "mousedown":
        case "mouseup":
        case "mousemove":
        case "mouseover":
        case "mouseout":
        case "contextmenu":
        case "mousewheel":
        case "DOMMouseScroll":
        case "click":
        case "dblclick":
          handler.__onmouseevent( domEvent );
        break;
        case "keydown":
        case "keypress":
        case "keyup":
          handler.__onKeyEvent( domEvent );
        break;
        default:
          throw "fireFakeDomEvent: Unkown dom-event " + domEvent.type;
        break;
      }
    },
    
    _identifierToKeycodeMap : {
      "Shift" : 16, 
      "Control" : 17,
      "Alt" : 18, 
      "CapsLock" : 20,
      "Meta" : 224, 
      "Left" : 37, 
      "Up" : 38, 
      "Right" : 39,
      "Down" : 40, 
      "PageUp" : 33, 
      "PageDown" : 34, 
      "End" : 35, 
      "Home" : 36, 
      "Insert" : 45, 
      "Delete" : 46, 
      "F1" : 112, 
      "F2" : 113, 
      "F3" : 114, 
      "F4" : 115, 
      "F5" : 116, 
      "F6" : 117, 
      "F7" : 118, 
      "F8" : 119, 
      "F9" : 120, 
      "F10" : 121, 
      "F11" : 122, 
      "F12" : 123, 
      "NumLock" : 144, 
      "PrintScreen" : 44, 
      "Scroll" : 145, 
      "Pause" : 19, 
      "Win" : 91, 
      "Apps" : 93,
      "Enter" : qx.core.Variant.select("qx.client", {
        "default" : null,
        "gecko" : 13
      } )
    },
    
    _printableIdentifierToKeycodeMap : {
      "Backspace" : 8, 
      "Tab" : 9,
      "Escape" : 27,
      "Space" : 32, 
      "Enter" : qx.core.Variant.select("qx.client", {
        "default" : 13,
        "gecko" : null
      } )
    },
    
    pressOnce : function( target, key, mod ) {
      this.keyDown( target, key, mod );
      this.keyUp( target, key, mod );
    },
    
    keyDown : function( target, key, mod ) {
      this.fireFakeKeyDomEvent( target, "keydown", key, mod );
      if( this._sendKeyPress( key ) ) { 
        this.fireFakeKeyDomEvent( target, "keypress", key, mod );
      }
    },
    
    keyHold : function( target, key, mod ) {
      if( this._sendKeyDownOnHold( key ) ) {
        this.fireFakeKeyDomEvent( target, "keydown", key, mod );
      }
      if( this._sendKeyPress( key ) ) { 
        this.fireFakeKeyDomEvent( target, "keypress", key, mod );
      }
    },
    
    keyUp : function( target, key, mod ) {
      this.fireFakeKeyDomEvent( target, "keyup", key, mod );
    },
    
    _sendKeyDownOnHold : qx.core.Variant.select("qx.client", {
      "default" : function( key ) {
        return true;
      },
      "opera" : function( key ) {
        return false;
      }
    } ),

    _sendKeyPress : qx.core.Variant.select("qx.client", { 
      "gecko|opera" : function( key ) {
        return !this._isModifier( key );
      },
      "default" : function( key ) {
        return this._isPrintable( key ); 
      } 
    } ),
    
    createFakeDomKeyEvent : function( target, type, stringOrKeyCode, mod ) {
      var domEvent = this._createFakeDomEvent( target, type, mod );
      domEvent.keyCode = this._getKeyCode( type, stringOrKeyCode );
      domEvent.charCode = this._getCharCode( type, stringOrKeyCode );
      domEvent.isChar = stringOrKeyCode === "string"; // not always correct 
      return domEvent;      
    },

    fireFakeKeyDomEvent : function( target, type, stringOrKeyCode, mod ) {
      var domEvent = this.createFakeDomKeyEvent( target, 
                                                 type, 
                                                 stringOrKeyCode, 
                                                 mod );
      this.fireFakeDomEvent( domEvent );
    },

    _getKeyCode : qx.core.Variant.select("qx.client", { 
      "default" : function( type, stringOrKeyCode ) {
        var result;
        // NOTE [tb] : This is called for non-printable keypress only in opera
        if( type === "keypress" && this._isPrintable( stringOrKeyCode ) ) {
          result = this._convertToCharCode( stringOrKeyCode );
        } else {
          result = this._convertToKeyCode( stringOrKeyCode );
        }
        return result;
      },
      "gecko" : function( type, stringOrKeyCode ) {
        var result;
        if( type === "keypress" && this._isPrintable( stringOrKeyCode ) ) {
          result = 0;
        } else {
          result = this._convertToKeyCode( stringOrKeyCode );          
        }
        return result;
      }
    } ),

    _getCharCode : qx.core.Variant.select("qx.client", { 
      "default" : function( type, stringOrKeyCode ) {
        return undefined;
      },
      "gecko|webkit" : function( type, stringOrKeyCode ) {
        // NOTE [tb] : this is never called with keypress for webkit
        var result;
        if( type === "keypress" && this._isPrintable( stringOrKeyCode ) ) {
          result = this._convertToCharCode( stringOrKeyCode );
        } else {
          result = 0
        }
        return result;
      }
    } ),
    
    _isPrintable : function( stringOrKeyCode ) {
      var util = org.eclipse.rwt.EventHandlerUtil;
      var keyCodeMap = util._keyCodeToIdentifierMap;
      var idMap = this._printableIdentifierToKeycodeMap;
      var isChar =    typeof stringOrKeyCode === "string" 
                   && stringOrKeyCode.length === 1;
      var isPrintableKeyCode =    typeof stringOrKeyCode === "number"
                               && keyCodeMap[ stringOrKeyCode ] === undefined;
      var isPrintableIdentifier =    typeof stringOrKeyCode === "string"
                                  && idMap[ stringOrKeyCode ] != null;
      return isChar || isPrintableKeyCode || isPrintableIdentifier;               
    },
    
    _isModifier : function( key ) {
      var keyCode = this._convertToKeyCode( key );
      return keyCode >= 16 && keyCode <= 20 && keyCode !== 19;  
    },

    _convertToKeyCode : function( stringOrKeyCode ) {
      var result;
      if( typeof stringOrKeyCode === "string" && stringOrKeyCode.length > 1 ) {
        result = this._identifierToKeycodeMap[ stringOrKeyCode ];
        if( result == null ) { // result may be null or undefined
          result = this._printableIdentifierToKeycodeMap[ stringOrKeyCode ];
          if( result == null ) {
            throw "_convertToKeyCode: unkown identifier " + stringOrKeyCode;
          }
        } 
      } else if( typeof stringOrKeyCode === "string" ) {
        result = stringOrKeyCode.toUpperCase().charCodeAt( 0 ); // should match
      } else {
        result = stringOrKeyCode;
      }
      return result;
    },

    _convertToCharCode : function( stringOrKeyCode ) {
      var result;
      if( typeof stringOrKeyCode === "string" && stringOrKeyCode.length > 1 ) {
        // Note: In this case keycode matches charcode
        result = this._printableIdentifierToKeycodeMap[ stringOrKeyCode ];
        if( result == null ) {
          throw "_convertToCharCode: not printable: " + stringOrKeyCode;
        }
      } else if( typeof stringOrKeyCode === "string" ) {
        result = stringOrKeyCode.charCodeAt( 0 );
      } else {
        result = stringOrKeyCode; // works if its printable
      }
      return result;
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
      // TODO [tb] : Event order differs on MAC OS
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
      if( !widget._isCreated ) {
        throw( "Error in testUtil.fakeMouseEvent: widget is not created" );
      }
      var target = widget._getTargetNode();
      var type =   qx.core.Variant.isSet( "qx.client", "gecko" ) 
                 ? "DOMMouseScroll" 
                 : "mousewheel"
      var domEvent = 
      this._createFakeMouseEventDOM( target, type, 0, 0, 0, 0 );
      this._addWheelDelta( domEvent, value );
      this.fireFakeDomEvent( domEvent );
    },
    
    _addWheelDelta : qx.core.Variant.select( "qx.client", {
      "default" : function( event, value ) {
        event.wheelDelta = value * 120;
      },
      "gecko" : function( event, value ) {
        event.detail = value * -3;
      }
    } ),

    fakeMouseEvent : function( widget, type, left, top ) {
      if( !widget._isCreated ) {
        throw( "Error in testUtil.fakeMouseEvent: widget is not created" );
      }
      var button = qx.event.type.MouseEvent.buttons.left;
      var target = widget._getTargetNode();
      this.fakeMouseEventDOM( target, type, button, left, top, 0 );
    },

    press : function( widget, key, checkActive, mod ) {
      var target = widget._getTargetNode();
      if( checkActive !== true && !this.isActive( widget ) ) {
        widget.focus();
      }
      this.pressOnce( target, key, mod );      
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

    _fakeKeyEvent : function( widget, type, key, checkActive, mod ) {
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

    resetEventHandler : function() {
      var keyHandler = org.eclipse.rwt.EventHandlerUtil;
      keyHandler._lastKeyCode = null;
      keyHandler._lastUpDownType = {};
      org.eclipse.rwt.EventHandler.setCaptureWidget( null );
    },

    ////////////////
    // client-server
    
    _requestLog : [],
    _response : null,
    _errorPage : null,
    
    initRequestLog : function() {
      var server = org.eclipse.rwt.test.fixture.RAPServer.getInstance();
      org.eclipse.rwt.test.fixture.TestUtil.clearRequestLog();
      server.setRequestHandler( function( message ) {
        testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil._requestLog.push( message );
        if( testUtil._response !== null ) {
          testUtil._response();
          testUtil._response = null;
        }
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

    scheduleResponse : function( func ) {
      this._response = func;
    },

    initErrorPageLog : function() {
      var handler = org.eclipse.rwt.ErrorHandler;
      org.eclipse.rwt.test.fixture.TestUtil.clearErrorPage();
      handler.processJavaScriptError = function( error ) {
        testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        testUtil._errorPage = error.message;
        throw error;
      };
    },

    clearErrorPage : function() {
      this._errorPage = null;
    },
    
    getErrorPage : function() {
      return this._errorPage;
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

    isMobileWebkit : function() {
      return org.eclipse.rwt.Client.isMobileSafari(); 
    },

    isFocused : function( widget ) {
      return widget == org.eclipse.rwt.EventHandler.getFocusRoot().getFocusedChild(); 
    },
    
    isActive: function( widget ) {
      return widget == org.eclipse.rwt.EventHandler.getFocusRoot().getActiveChild(); 
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
    
    emptyDragCache : function() {
      qx.event.handler.DragAndDropHandler.__dragCache = null;
    },

    cleanUpKeyUtil : function() {
      org.eclipse.rwt.KeyEventUtil.getInstance().setKeyBindings( {} );
      var instance = org.eclipse.rwt.KeyEventUtil.getInstance()._getDelegate();
      if( instance instanceof org.eclipse.rwt.AsyncKeyEventUtil ) {
        instance.cancelEvent();
      }
    },

    /**
     * Delays exection of next test function so events may be fired.
     * To be used with multi-part tests, i.e. a test that is composed
     * of several funtions in an array, guarenteed to be called in the given
     * order.
     */
    delayTest : function( time ) {
      org.eclipse.rwt.test.TestRunner.getInstance().pause( time );
    },
    
    /**
     * All given values will be passed on to the next test-functions as
     * arguments. This is true until either this function is called again
     * or the test-instance is disposed.
     */
    store : function() {
      var runner = org.eclipse.rwt.test.TestRunner.getInstance();
      runner.setArguments( arguments );
    }
     
  }
  
});