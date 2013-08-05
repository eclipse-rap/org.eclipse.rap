/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var SWT = rwt.scripting.SWT;

rwt.qx.Class.createNamespace( "rwt.scripting", {} );

rwt.scripting.EventProxy = function( eventType, originalTarget, originalEvent ) {
  this.widget = rwt.scripting.WidgetProxyFactory.getWidgetProxy( originalTarget );
  this.type = eventType;
  switch( eventType ) {
    case SWT.KeyDown:
    case SWT.KeyUp:
      initKeyEvent( this, originalEvent );
    break;
    case SWT.MouseDown:
    case SWT.MouseUp:
    case SWT.MouseMove:
    case SWT.MouseEnter:
    case SWT.MouseExit:
    case SWT.MouseDoubleClick:
      initMouseEvent( this, originalEvent );
    break;
    case SWT.Verify:
      initVerifyEvent( this, originalEvent );
    break;
    case SWT.Paint:
      initPaintEvent( this, originalTarget );
    break;
  }
};

rwt.scripting.EventProxy.prototype = {

  /**
   * An object representing the widget that issued the event.
   * It has setter and getter named after the properties used in the RAP protocol.
   * Only a subset of getter is currently supported.
   * Setting properties might result in server and client getting out-of-sync in RAP 1.5,
   * unless it is a property that can be changed by user-input (e.g. selection).
   */
  widget : null,

  /**
   * depending on the event, a flag indicating whether the operation should be
   * allowed. Setting this field to false will cancel the operation.
   * Currently only effective on key events for Text or Text-like widgets.
   */
  doit : true,

  /**
   * depending on the event, the character represented by the key that was
   * typed. This is the final character that results after all modifiers have
   * been applied. For non-printable keys (like arrow-keys) this field is not set.
   * Changing its value has no effect.
   */
  character : '\u0000',

  /**
   * depending on the event, the key code of the key that was typed, as defined
   * by the key code constants in class <code>SWT</code>. When the character
   * field of the event is ambiguous, this field contains the unaffected value
   * of the original character. For example, typing Shift+M or M result in different
   * characters ( 'M' and 'm' ), but the same keyCode (109, character code for 'm').
   */
  keyCode : 0,

  /**
   * the type of event, as defined by the event type constants in class <code>SWT</code>.
   * Currently supports SWT.KeyDown
   */
  type : 0,

  /**
   * depending on the event, the state of the keyboard modifier keys and mouse
   * masks at the time the event was generated.
   */
  stateMask : 0,

  /**
   * the button that was pressed or released; 1 for the first button, 2 for the
   * second button, and 3 for the third button, etc.
   */
  button : 0,

  /**
   * x coordinate of the pointer at the time of the event
   */
  x : 0,

  /**
   * y coordinate of the pointer at the time of the event
   */
  y : 0,

  /**
   * depending on the event, the range of text being modified. Setting these
   * fields has no effect.
   */
  start : 0,
  end : 0,

  /**
   * depending on the event, the new text that will be inserted.
   * Setting this field will change the text that is about to
   * be inserted or deleted.
   */
  text : "",

  /**
   * the graphics context to use when painting.
   * It supports a subset of the HTML5 Canvas API (http://www.w3.org/TR/2dcontext/):
   * Fields:
   *  - strokeStyle
   *  - fillStyle
   *  - lineWidth
   *  - lineJoin
   *  - lineCap
   *  - miterLimit
   *  - globalAlpha
   * Methods:
   *  - save
   *  - restore
   *  - beginPath
   *  - closePath
   *  - clearRect (Limitation: in IE 7+8 arguments are ignored, the entire canvas is cleared)
   *  - stroke
   *  - fill
   *  - moveTo
   *  - lineTo
   *  - quadraticCurveTo
   *  - bezierCurveTo
   *  - rect
   *  - arc
   *  - drawImage
   *  - createLinearGradient (Limitations: In IE 7+8, the gradient can be only be drawn either
   *                          vertically or horizontally. Calls to "addColorStop" must be in the
   *                          order of the offsets and can not overwrite previous colorsStops)
   *
   * More methods may be supported on specific browser.
   */
  gc : null

};

rwt.scripting.EventProxy.disposeEventProxy = function( eventProxy ) {
  eventProxy.widget = null;
};

rwt.scripting.EventProxy.wrapAsProto = function( object ) {
    WrapperHelper.prototype = object;
    var result = new WrapperHelper();
    WrapperHelper.prototype = null;
    return result;
  };

rwt.scripting.EventProxy.postProcessEvent = function( event, wrappedEvent, originalEvent ) {
  var SWT = rwt.scripting.SWT;
  switch( event.type ) {
    case SWT.Verify:
      postProcessVerifyEvent( event, wrappedEvent, originalEvent );
    break;
    case SWT.KeyDown:
    case SWT.KeyUp:
      postProcessKeyEvent( event, wrappedEvent, originalEvent );
    break;
  }
};

var initKeyEvent = function( event, originalEvent ) {
  var charCode = originalEvent.getCharCode();
  var SWT = rwt.scripting.SWT;
  if( charCode !== 0 ) {
    event.character = String.fromCharCode( charCode );
    // TODO [tb] : keyCode will be off when character is not a-z
    event.keyCode = event.character.toLowerCase().charCodeAt( 0 );
  } else {
    var keyCode = getLastKeyCode();
    switch( keyCode ) {
      case 16:
        event.keyCode = SWT.SHIFT;
      break;
      case 17:
        event.keyCode = SWT.CTRL;
      break;
      case 18:
        event.keyCode = SWT.ALT;
      break;
      case 224:
        event.keyCode = SWT.COMMAND;
      break;
      default:
        event.keyCode = keyCode;
      break;
    }
  }
  setStateMask( event, originalEvent );
};

var initMouseEvent = function( event, originalEvent ) {
  var target = originalEvent.getTarget()._getTargetNode();
  var offset = rwt.html.Location.get( target, "scroll" );
  event.x = originalEvent.getPageX() - offset.left;
  event.y = originalEvent.getPageY() - offset.top;
  if( originalEvent.isLeftButtonPressed() ) {
    event.button = 1;
  } else if( originalEvent.isRightButtonPressed() ) {
    event.button = 3;
  } if( originalEvent.isMiddleButtonPressed() ) {
    event.button = 2;
  }
  setStateMask( event, originalEvent );
};

var initPaintEvent = function( event, target ) {
  var gc = rwt.scripting.WidgetProxyFactory._getGCFor( target );
  event.gc = gc.getNativeContext();
};

var initVerifyEvent = function( event, originalEvent ) {
  var text = originalEvent.getTarget();
  if( text.classname === "rwt.widgets.Text" ) {
    var keyCode = getLastKeyCode();
    var newValue = text.getComputedValue();
    var oldValue = text.getValue();
    var oldSelection = text.getSelection();
    var diff = getDiff( newValue, oldValue, oldSelection, keyCode );
    if(    diff[ 0 ].length === 1
        && diff[ 1 ] === diff[ 2 ]
        && diff[ 0 ] === originalEvent.getData()
    ) {
      event.keyCode = keyCode;
      event.character = diff[ 0 ];
    }
    event.text = diff[ 0 ];
    event.start = diff[ 1 ];
    event.end = diff[ 2 ];
  }
};

var getLastKeyCode = function() {
  // NOTE : While this is a private field, this mechanism must be integrated with
  // KeyEventSupport anyway to support the doit flag better.
  return rwt.remote.KeyEventSupport.getInstance()._currentKeyCode;
};

var getDiff = function( newValue, oldValue, oldSel, keyCode ) {
  var start;
  var end;
  var text;
  if( newValue.length >= oldValue.length || oldSel[ 0 ] !== oldSel[ 1 ] ) {
    start = oldSel[ 0 ];
    end = oldSel[ 1 ];
    text = newValue.slice( start, newValue.length - ( oldValue.length - oldSel[ 1 ] ) );
  } else {
    text = "";
    if(    oldSel[ 0 ] === oldSel[ 1 ]
        && keyCode === 8 // backspace
        && ( oldValue.length - 1 ) === newValue.length
    ) {
      start = oldSel[ 0 ] - 1;
      end = oldSel[ 0 ];
    } else {
      start = oldSel[ 0 ];
      end = start + oldValue.length - newValue.length;
    }
  }
  return [ text, start, end ];
};

var setStateMask = function( event, originalEvent ) {
  event.stateMask |= originalEvent.isShiftPressed() ? SWT.SHIFT : 0;
  event.stateMask |= originalEvent.isCtrlPressed() ? SWT.CTRL : 0;
  event.stateMask |= originalEvent.isAltPressed() ? SWT.ALT : 0;
  event.stateMask |= originalEvent.isMetaPressed() ? SWT.COMMAND : 0;
};

var postProcessVerifyEvent = function( event, wrappedEvent, originalEvent ) {
  var widget = originalEvent.getTarget();
  if( wrappedEvent.doit !== false ) {
    if( event.text !== wrappedEvent.text && event.text !== "" ) {
      // insert replacement text
      originalEvent.preventDefault();
      var currentText = widget.getValue();
      var textLeft = currentText.slice( 0, event.start );
      var textRight = currentText.slice( event.end, currentText.length );
      var carret = textLeft.length + wrappedEvent.text.length;
      widget.setValue( textLeft + wrappedEvent.text + textRight );
      widget.setSelection( [ carret, carret ] );
    }
  } else {
    // undo any change
    originalEvent.preventDefault();
    widget._renderValue();
    widget._renderSelection();
  }
};

var postProcessKeyEvent = function( event, wrappedEvent, originalEvent ) {
  if( wrappedEvent.doit === false ) {
    originalEvent.preventDefault();
  }
};

var WrapperHelper = function(){};

}());
