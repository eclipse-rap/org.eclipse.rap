/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

namespace( "org.eclipse.rwt.test.fixture" );

org.eclipse.rwt.test.fixture.TestUtil = {

  //////
  // DOM

  getElementBounds : function( node ) {
    var style = node.style;
    var ps;
    try {
      ps = node.parentNode.style;
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
    var result = value ? parseInt( value, 10 ) : 0;
    if(    result !== 0
        && typeof value == "string"
        && value.indexOf( "px" ) == -1 )
    {
      throw "getElementBounds only supports \"px\" but found " + value;
    }
    return result;
  },

  getElementFont : function( element ) {
    var font = element.style.font;
    if( font === "" || typeof font !== "string" ) {
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
    return node.style.opacity !== "" && parseInt( node.style.opacity, 10 ) < 1;
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

  getCssBackgroundColor : function( target ) {
    var result;
    if( target instanceof rwt.widgets.base.Widget ) {
      var inner = target._getTargetNode().style.backgroundColor;
      var outer = target.getElement().style.backgroundColor;
      result = ( ( inner || outer ) || null );
    } else {
      result = target.style.backgroundColor;
    }
    if( result === "" || result === "transparent" || result === "rgba(0, 0, 0, 0)" ) {
      result = null;
    }
    return result;
  },

  hasCssBorder : function( node ) {
    var result = false;
    var edge = [ "Top", "Left", "Bottom", "Right" ];
    for( var i=0; i < 4; i++ ) {
      if( !result ) {
        var width = parseInt( node.style[ "border" + edge[ i ] + "Width" ], 10 );
        var color = node.style[ "border" + edge[ i ] + "Color" ];
        var style = node.style[ "border" + edge[ i ] + "Style" ];
        var hasWidth = !isNaN( width ) && width > 0;
        var hasColor = color !== "transparent";
        var hasStyle = style !== "" && style !== "none";
        result = hasWidth && hasColor && hasStyle;
      }
    }
    return result;
  },

  getElementSelectable : function( node ) {
    return node.style.cssText.search( "user-select: none" ) == -1;
  },

  getCssGradient : function( element ) {
    var result = "";
    var background = element.style.background;
    var start = background.indexOf( "gradient(" );
    if( start !== -1 ) {
      var end = -1;
      var level = 0;
      for( var i = start + "gradient".length; i < background.length; i++ ) {
        if( background.charAt( i ) === "(" ) {
          level++;
        } else if( background.charAt( i ) === ")" ) {
          level--;
        }
        if( level === 0 ) {
          end = i + 1;
          break;
        }
      }
      if( end !== -1 ) {
        result = background.slice( start, end );
      } else {
        result = background.slice( start );
      }
    } else if( background.indexOf( "url(\"data:" ) !== -1 ) {
      start = background.indexOf( "url(\"data:" );
      var end = background.indexOf( "\")", start );
      if( end !== -1 ) {
        result = background.slice( start, end + 2 );
      }
    }
    return result;
  },

  /////////////////////////////
  // Event handling - DOM layer

  _createFakeDomEvent : function( target, type, mod ) {
    if( target == null ) {
      throw new Error( "Can not fire fake dom event on target " + target );
    }
    var result = {
      "preventDefault" : function(){},
      "stopPropagation" : function(){},
      "pageX" : 0,
      "pageY" : 0,
      "type" : type,
      "target" : target,
      "relatedTarget" : null,
      "ctrlKey" : ( rwt.event.DomEvent.CTRL_MASK & mod ) !== 0,
      "altKey" :  ( rwt.event.DomEvent.ALT_MASK  & mod ) !== 0,
      "shiftKey" : ( rwt.event.DomEvent.SHIFT_MASK  & mod ) !== 0
    };
    return result;
  },

  clickDOM : function( node, left, top ) {
    var button = rwt.event.MouseEvent.buttons.left;
    this.fakeMouseEventDOM( node, "mousedown", button, left, top );
    this.fakeMouseEventDOM( node, "mouseup", button, left, top );
    this.fakeMouseEventDOM( node, "click", button, left, top );
  },

  shiftClickDOM : function( node ) {
    var left = rwt.event.MouseEvent.buttons.left;
    var mod = rwt.event.DomEvent.SHIFT_MASK;
    this.fakeMouseEventDOM( node, "mousedown", left, 0, 0, mod );
    this.fakeMouseEventDOM( node, "mouseup", left, 0, 0, mod );
    this.fakeMouseEventDOM( node, "click", left, 0, 0, mod );
  },

  ctrlClickDOM : function( node ) {
    var left = rwt.event.MouseEvent.buttons.left;
    var mod = rwt.event.DomEvent.CTRL_MASK;
    this.fakeMouseEventDOM( node, "mousedown", left, 0, 0, mod );
    this.fakeMouseEventDOM( node, "mouseup", left, 0, 0, mod );
    this.fakeMouseEventDOM( node, "click", left, 0, 0, mod );
  },

  hoverFromTo : function( fromNode, toNode ) {
    fromNode = this._toElement( fromNode );
    toNode = this._toElement( toNode );
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
    return domEvent;
  },

  _createFakeMouseEventDOM : function( target, type, button, left, top, mod ) {
    // TODO [tb] : refactor to not overwrite paramters?
    if( typeof left === "undefined" ) {
      left = 0;
    }
    if( typeof button === "undefined" ) {
      button = rwt.event.MouseEvent.buttons.left;
    }
    if( typeof top === "undefined" ) {
      top = 0;
    }
    if( typeof mod === "undefined" ) {
      mod = 0;
    }
    var clientX = left;
    var clientY = top;
    var which = null;
    switch( button ) {
      case rwt.event.MouseEvent.buttons.left:
        which = 1;
      break;
      case rwt.event.MouseEvent.buttons.middle:
        which = 2;
      break;
      case rwt.event.MouseEvent.buttons.right:
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
    domEvent.ctrlKey = ( rwt.event.DomEvent.CTRL_MASK & mod ) !== 0;
    domEvent.altKey = ( rwt.event.DomEvent.ALT_MASK & mod ) !== 0;
    domEvent.shiftKey = ( rwt.event.DomEvent.SHIFT_MASK & mod ) !== 0;
    if( this.isMobileWebkit() ) {
      domEvent.originalEvent = {};
    }
    return domEvent;
  },

  fireFakeDomEvent : function( domEvent ) {
    var type = domEvent.type;
    var handler = rwt.event.EventHandler;
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
    "Enter" : rwt.util.Variant.select("qx.client", {
      "default" : null,
      "gecko" : 13
    } ),
    "Escape" : 27
  },

  _printableIdentifierToKeycodeMap : {
    "Backspace" : 8,
    "Tab" : 9,
    "Escape" : 27,
    "Space" : 32,
    "Enter" : rwt.util.Variant.select("qx.client", {
      "default" : 13,
      "gecko" : null
    } )
  },

  pressOnce : function( target, key, mod ) {
    this.keyDown( target, key, mod );
    this.keyUp( target, key, mod );
  },

  keyDown : function( target, key, mod ) {
    var event = this.fireFakeKeyDomEvent( target, "keydown", key, mod );
    if( this._sendKeyPress( key, event ) ) {
      this.fireFakeKeyDomEvent( target, "keypress", key, mod );
    }
  },

  keyHold : function( target, key, mod ) {
    if( this._sendKeyDownOnHold( key ) ) {
      var event = this.fireFakeKeyDomEvent( target, "keydown", key, mod );
    }
    if( this._sendKeyPress( key, event ) ) {
      this.fireFakeKeyDomEvent( target, "keypress", key, mod );
    }
  },

  keyUp : function( target, key, mod ) {
    this.fireFakeKeyDomEvent( target, "keyup", key, mod );
  },

  _sendKeyDownOnHold : rwt.util.Variant.select("qx.client", {
    "default" : function() {
      return true;
    },
    "opera" : function() {
      return false;
    }
  } ),

  _sendKeyPress : rwt.util.Variant.select("qx.client", {
    "gecko|opera" : function( key ) {
      return !this._isModifier( key );
    },
    "default" : function( key, keyDownEvent ) {
      var wasStopped =   keyDownEvent
                       ? rwt.event.EventHandlerUtil.wasStopped( keyDownEvent )
                       : false;
      return this._isPrintable( key ) && !wasStopped;
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
    return domEvent;
  },

  _getKeyCode : rwt.util.Variant.select("qx.client", {
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
        result = this._isEscape( stringOrKeyCode ) ? 27 : 0;
      } else {
        result = this._convertToKeyCode( stringOrKeyCode );
      }
      return result;
    }
  } ),

  _getCharCode : rwt.util.Variant.select("qx.client", {
    "default" : function() {
      return undefined;
    },
    "gecko|webkit" : function( type, stringOrKeyCode ) {
      // NOTE [tb] : this is never called with keypress for webkit
      var result;
      if(    type === "keypress"
          && this._isPrintable( stringOrKeyCode )
          && !this._isEscape( stringOrKeyCode )
      ) {
        result = this._convertToCharCode( stringOrKeyCode );
      } else {
        result = 0;
      }
      return result;
    }
  } ),

  _isPrintable : function( stringOrKeyCode ) {
    var util = rwt.event.EventHandlerUtil;
    var keyCodeMap = util._keyCodeToIdentifierMap;
    var idMap = this._printableIdentifierToKeycodeMap;
    var isChar =    typeof stringOrKeyCode === "string"
                 && stringOrKeyCode.length === 1;
    var isPrintableKeyCode =    typeof stringOrKeyCode === "number"
                             && keyCodeMap[ stringOrKeyCode ] === undefined;
    var isPrintableIdentifier =    typeof stringOrKeyCode === "string"
                                && idMap[ stringOrKeyCode ] != null;
    var result = isChar || isPrintableKeyCode || isPrintableIdentifier;
    if( rwt.client.Client.isWebkit() ) {
      if( stringOrKeyCode === 27 || stringOrKeyCode === "Escape" ) {
        result = false;
      }
    }
    if(    ( stringOrKeyCode === 9 || stringOrKeyCode === "Tab" )
        || ( stringOrKeyCode === 8 || stringOrKeyCode === "Backspace") )
    {
      result = false;
    }
    return result;
  },

  _isEscape : function( stringOrKeyCode ) {
    return stringOrKeyCode === 27 || stringOrKeyCode === "Escape";
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
          result = 0;
        }
      }
    } else if( typeof stringOrKeyCode === "string" ) {
      var charCode = stringOrKeyCode.toUpperCase().charCodeAt( 0 );
      if(    ( charCode >= 65 && charCode <= 90 )
          || ( charCode >= 97 && charCode <= 122 )
          || ( charCode >= 48 && charCode <= 57 )
      ) {
        result = stringOrKeyCode.toUpperCase().charCodeAt( 0 ); // should match
      } else {
        result = 0; // unkown
      }
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

  click : function( widget, left, top ) {
    this.clickDOM( this._toElement( widget ), left, top );
  },

  doubleClick : function( widget ) {
    var node = this._toElement( widget );
    this.clickDOM( node );
    this.clickDOM( node );
    var left = rwt.event.MouseEvent.buttons.left;
    this.fakeMouseEventDOM( node, "dblclick", left );
  },

  shiftClick : function( widget ) {
    var node = this._toElement( widget );
    this.shiftClickDOM( node );
  },

  ctrlClick : function( widget ) {
    var node = this._toElement( widget );
    this.ctrlClickDOM( node );
  },

  rightClick : function( widget ) {
    var right = rwt.event.MouseEvent.buttons.right;
    var node = this._toElement( widget );
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
      throw( "Error in TestUtil.fakeMouseEvent: widget is not created" );
    }
    var target = widget._getTargetNode();
    var type =   rwt.util.Variant.isSet( "qx.client", "gecko" )
               ? "DOMMouseScroll"
               : "mousewheel";
    var domEvent =
    this._createFakeMouseEventDOM( target, type, 0, 0, 0, 0 );
    this._addWheelDelta( domEvent, value );
    this.fireFakeDomEvent( domEvent );
  },

  _addWheelDelta : rwt.util.Variant.select( "qx.client", {
    "default" : function( event, value ) {
      event.wheelDelta = value * 120;
    },
    "gecko" : function( event, value ) {
      event.detail = value * -3;
    }
  } ),

  fakeMouseEvent : function( target, type, left, top ) {
    var button = rwt.event.MouseEvent.buttons.left;
    var node = this._toElement( target );
    this.fakeMouseEventDOM( node, type, button, left, top, 0 );
  },

  press : function( widget, key, checkActive, mod ) {
    var target = this._toElement( widget );
    if( checkActive !== true && !this.isActive( widget ) ) {
      widget.focus();
    }
    this.pressOnce( target, key, mod );
  },

  shiftPress : function( widget, key, checkActive ) {
    var mod = rwt.event.DomEvent.SHIFT_MASK;
    this.press( widget, key, checkActive, mod );
  },

  ctrlPress : function( widget, key, checkActive ) {
    var mod = rwt.event.DomEvent.CTRL_MASK;
    this.press( widget, key, checkActive, mod );
  },

  altPress : function( widget, key, checkActive ) {
    var mod = rwt.event.DomEvent.ALT_MASK;
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
        "ctrlKey" : ( rwt.event.DomEvent.CTRL_MASK & mod ) !== 0,
        "altKey" :  ( rwt.event.DomEvent.ALT_MASK  & mod ) !== 0,
        "shiftKey" : ( rwt.event.DomEvent.SHIFT_MASK  & mod ) !== 0,
        preventDefault : function(){}
      };
      var ev = new rwt.event.KeyEvent(
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
    var keyHandler = rwt.event.EventHandlerUtil;
    keyHandler._lastKeyCode = null;
    keyHandler._lastUpDownType = {};
    rwt.event.EventHandler.setCaptureWidget( null );
    rwt.event.EventHandler.setBlockKeyEvents( false );
  },

  _toElement : function( target ) {
    if( target instanceof rwt.widgets.base.Widget ) {
      return target._getTargetNode();
    }
    if( target instanceof rwt.util.RWTQuery ) {
      return target.get( 0 );
    }
    if( target.$el ) {
      return target.$el.get( 0 );
    }
    return target;
  },

  ////////////////
  // client-server

  _requestLog : [],
  _requestCounter : 1,
  _response : null,
  _errorPage : null,

  initRequestLog : function() {
    var server = org.eclipse.rwt.test.fixture.FakeServer.getInstance();
    org.eclipse.rwt.test.fixture.TestUtil.clearRequestLog();
    server.setRequestHandler( function( message ) {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil._requestCounter++;
      TestUtil._requestLog.push( message );
      if( TestUtil._response !== null ) {
        TestUtil._response();
        TestUtil._response = null;
      }
      var response =    "{ \"head\" : { \"requestCounter\" : "
                      + TestUtil._requestCounter
                      + " }, \"operations\" : [] }";
      return response;
    } );
  },

  setIgnoreSendRequests : function( value ) {
    rwt.remote.Connection.getInstance()._ignoreSend = value;
  },

  getRequestLog : function() {
    return this._requestLog;
  },

  getRequestsSend : function() {
    return this._requestLog.length;
  },

  clearRequestLog : function() {
    var server = rwt.remote.Connection.getInstance();
    if( server.getMessageWriter() && server.getMessageWriter().hasOperations() ) {
      server.send();
    }
    this._requestLog = [];
  },

  getMessageObject : function( arg ) {
    var index = typeof arg === "number" ? arg : 0;
    return new org.eclipse.rwt.test.fixture.Message( this._requestLog[ index ] );
  },

  getLastMessage : function() {
    return this.getMessageObject( this._requestLog.length - 1 );
  },

  getMessages : function() {
    var result = [];
    for( var i = 0; i < this._requestLog.length; i++ ) {
      result.push( this.getMessageObject( i ) );
    }
    return result;
  },

  scheduleResponse : function( func ) {
    this._response = func;
  },

  initErrorPageLog : function() {
    org.eclipse.rwt.test.fixture.TestUtil.clearErrorPage();
    rwt.runtime.ErrorHandler.showErrorPage = function( content ) {
      org.eclipse.rwt.test.fixture.TestUtil._errorPage = content;
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
    rwt.client.Timer.prototype._applyEnabled = function(){};
    rwt.client.Timer._onceCallsLog = [];
    rwt.client.Timer.once = function( func, obj, timeout ) {
      var source = arguments.callee.caller;
      this._onceCallsLog.push( [ func, obj, timeout, source ] );
    };
  },

  getTimerOnceLog : function() {
    return rwt.client.Timer._onceCallsLog;
  },

  clearTimerOnceLog : function() {
    rwt.client.Timer._onceCallsLog = [];
  },

  forceTimerOnce : function() {
    // TODO [tb] : sort order by time
    var log = rwt.client.Timer._onceCallsLog;
    for( var i = 0; i < log.length; i++ ) {
      log[ i ][ 0 ].call( log[ i ][ 1 ] );
    }
    rwt.client.Timer._onceCallsLog = [];
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
    var manager = rwt.theme.AppearanceManager.getInstance();
    var base = manager.getCurrentTheme().appearances;
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
    var manager = rwt.theme.AppearanceManager.getInstance();
    var base = manager.getCurrentTheme().appearances;
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
    var manager = rwt.theme.AppearanceManager.getInstance();
    manager.__cache[ manager.getCurrentTheme().name ] = {};
  },

  ////////
  // Misc

  isMobileWebkit : function() {
    return    rwt.client.Client.isMobileSafari()
           || rwt.client.Client.isAndroidBrowser()
           || rwt.client.Client.isMobileChrome();
  },

  isFocused : function( widget ) {
    return widget == rwt.event.EventHandler.getFocusRoot().getFocusedChild();
  },

  isActive: function( widget ) {
    return widget == rwt.event.EventHandler.getFocusRoot().getActiveChild();
  },

  flush : function( inResponse ) {
    if( inResponse ) {
      rwt.remote.EventUtil.setSuspended( true );
      rwt.widgets.base.Widget.flushGlobalQueues();
      rwt.remote.EventUtil.setSuspended( false );
    } else {
      rwt.widgets.base.Widget.flushGlobalQueues();
    }
  },

  fakeResponse : function( value ) {
    rwt.remote.EventUtil.setSuspended( value );
  },

  getDocument : function() {
    return rwt.widgets.base.ClientDocument.getInstance();
  },

  preventFlushs : function( value ) {
    // this only works if the TestRunner-function"_disableAutoFlush"
    // has been called previously. (Happens in TestRunner.run)
    rwt.widgets.base.Widget.__allowFlushs = !value;
  },

  emptyDragCache : function() {
    rwt.event.DragAndDropHandler.__dragCache = null;
  },

  cleanUpKeyUtil : function() {
    var support =  rwt.remote.KeyEventSupport.getInstance();
    support.setKeyBindings( {} );
    support.setCancelKeys( {} );
    support._currentKeyCode = -1;
    support._bufferedEvents = [];
    support._keyEventRequestRunning = false;
    support._ignoreNextKeypress = false;
  },

  /**
   * Delays exection of next test function so events may be fired.
   * To be used with multi-part tests, i.e. a test that is composed
   * of several funtions in an array, guarenteed to be called in the given
   * order.
   */
  delayTest : function( time ) {
    this._waitUntil = ( new Date() ).getTime() + time;
  },

  shouldContinueTest : function() {
    var until = org.eclipse.rwt.test.fixture.TestUtil._waitUntil || 0;
    return until < ( new Date() ).getTime();
  },

  /**
   * All given values will be passed on to the next test-functions as
   * arguments. This is true until either this function is called again
   * or the test-instance is disposed.
   */
  store : function() {
    this._stored = arguments;
  },

  getStored : function() {
    return this._stored;
  },

  /**
   * Ensures that the given object has no other objects as a fields
   */
  hasNoObjects : function( object, ownProperty ) {
    var result = true;
    for( var key in object ) {
      if( object[ key ] instanceof Object ) {
        if( !ownProperty || object.hasOwnProperty( key ) ) {
          result = false;
        }
      }
    }
    return result;
  },

  skipAnimations : function() {
    var queue = rwt.animation.Animation._queue;
    while( queue.length > 0 ) {
      queue[ 0 ].skip();
    }
  },

  ///////////////////
  // Protocol ralated

  addToRegistry : function( id, object ) {
    rwt.remote.ObjectRegistry.add(
      id,
      object,
      rwt.remote.HandlerRegistry.getHandler( object.classname )
    );
  },

  createShellByProtocol : function( id ) {
    rwt.remote.EventUtil.setSuspended( true );
    rwt.remote.MessageProcessor.processOperation( {
      "target" : id ? id : "w2",
      "action" : "create",
      "type" : "rwt.widgets.Shell",
      "properties" : {
        "style" : [ "BORDER" ],
        "visibility" : true,
        "bounds" : [ 10, 10, 100, 100 ]
      }
    } );
    rwt.remote.EventUtil.setSuspended( false );
    return rwt.remote.ObjectRegistry.getObject( id ? id : "w2" );
  },

  createWidgetByProtocol : function( id, parentId, type ) {
    rwt.remote.EventUtil.setSuspended( true );
    rwt.remote.MessageProcessor.processOperation( {
      "target" : id,
      "action" : "create",
      "type" : type ? type : "rwt.widgets.Composite",
      "properties" : {
        "style" : [ "BORDER" ],
        "parent" : parentId ? parentId : "w2",
        "bounds" : [ 10, 10, 10, 10 ]
      }
    } );
    rwt.remote.EventUtil.setSuspended( false );
    return rwt.remote.ObjectRegistry.getObject( id );
  },

  protocolListen : function( id, properties ) {
    var processor = rwt.remote.MessageProcessor;
    processor.processOperation( {
      "target" : id,
      "action" : "listen",
      "properties" : properties
    } );
  },

  protocolCall : function( id, method, properties ) {
    var processor = rwt.remote.MessageProcessor;
    processor.processOperation( {
      "target" : id,
      "action" : "call",
      "method" : method,
      "properties" : properties
    } );
  },

  protocolSet : function( id, properties ) {
    rwt.remote.EventUtil.setSuspended( true );
    var processor = rwt.remote.MessageProcessor;
    processor.processOperation( {
      "target" : id,
      "action" : "set",
      "properties" : properties
    } );
    rwt.remote.EventUtil.setSuspended( false );
  },

  fakeListener : function( widget, type, value ) {
    var remoteObject = rwt.remote.RemoteObjectFactory.getRemoteObject( widget );
    remoteObject._.listen[ type ] = value;
  },

  resetObjectRegistry : function() {
    var map = rwt.remote.ObjectRegistry._map;
    for( var id in map ) { // replacing _map would not be enough, must also be remove id from object
      if( id !== "w1" ) {
        rwt.remote.ObjectRegistry.remove( id );
      }
    }
    rwt.remote.ObjectRegistry._callbacks = {};
    rwt.remote.RemoteObjectFactory._db = {};
  },

  resetSendListener : function() {
    var connection = rwt.remote.Connection.getInstance();
    connection.__listeners[ "send" ] = {};
   },

  resetWindowManager : function() {
    var manager = rwt.widgets.base.Window.getDefaultWindowManager();
    manager._objects = {};
  },

  resetSingletons : function() {
    var exceptions = [
      rwt.widgets.base.WidgetToolTip,
      rwt.widgets.util.ToolTipManager,
      rwt.remote.Connection,
      rwt.remote.KeyEventSupport,
      rwt.theme.ThemeStore,
      rwt.theme.AppearanceManager,
      rwt.runtime.System,
      rwt.widgets.base.ClientDocument,
      org.eclipse.rwt.test.fixture.FakeServer
    ];
    rwt.runtime.Singletons._clearExcept( exceptions );
  },

  getXMLHttpRequests : function() {
    return org.eclipse.rwt.test.fixture.NativeRequestMock.history;
  },

  clearXMLHttpRequests : function() {
    org.eclipse.rwt.test.fixture.NativeRequestMock.history = [];
  },

  getLogger : function() {
    var result = {
      _log : [],
      getLog : function() {
        return this._log;
      }
    };
    result.log = function( item ) {
      result._log.push( item );
    };
    return result;
  }

};
