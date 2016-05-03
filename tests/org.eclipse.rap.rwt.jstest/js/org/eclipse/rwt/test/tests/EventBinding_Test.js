/*******************************************************************************
 * Copyright (c) 2012, 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function() {

var EventBinding = rwt.scripting.EventBinding;
var EventProxy = rwt.scripting.EventProxy;
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var Processor = rwt.remote.MessageProcessor;
var ObjectRegistry = rwt.remote.ObjectRegistry;
var EventHandlerUtil = rwt.event.EventHandlerUtil;

var text;
var textEl;

rwt.qx.Class.define( "org.eclipse.rap.clientscripting.EventBinding_Test", {

  extend : rwt.qx.Object,

  members : {

    testAddListener_KeyEvent : function() {
      TestUtil.flush();
      var logger = this._createLogger();

      EventBinding.addListener( text, "KeyDown", logger );
      TestUtil.press( text, "A" );

      assertEquals( 1, logger.log.length );
    },

    testAddListenerTwice_KeyEvent : function() {
      TestUtil.flush();
      var logger = this._createLogger();

      EventBinding.addListener( text, "KeyDown", logger );
      EventBinding.addListener( text, "KeyDown", logger );
      TestUtil.press( text, "A" );

      assertEquals( 2, logger.log.length );
    },

    testDisposeBindKeyEvent : function() {
      var logger = this._createLogger();
      EventBinding.addListener( text, "KeyDown", logger );

      EventBinding.removeListener( text, "KeyDown", logger );
      TestUtil.press( text, "A" );

      assertEquals( 0, logger.log.length );
    },

    testAddListener_CreatesProxyEvent : function() {
      var logger = this._createLogger();

      EventBinding.addListener( text, "KeyDown", logger );
      TestUtil.press( text, "A" );

      var event = logger.log[ 0 ];
      assertTrue( event instanceof EventProxy );
    },

    testAddListener_DisposesProxyEvent : function() {
      var logger = this._createLogger();

      EventBinding.addListener( text, "KeyDown", logger );
      TestUtil.press( text, "A" );

      var event = logger.log[ 0 ];
      assertTrue( TestUtil.hasNoObjects( event ) );
    },

    testDoItFalseKeyDown : function() {
      var listener = function( event ) {
        event.doit = false;
      };

      EventBinding.addListener( text, "KeyDown", listener );
      var domEvent = TestUtil.createFakeDomKeyEvent( text.getElement(), "keypress", "a" );
      TestUtil.fireFakeDomEvent( domEvent );

      assertTrue( EventHandlerUtil.wasStopped( domEvent ) );
    },

    testDoItFalseMouseDown : function() {
      var listener = function( event ) {
        event.doit = false;
      };

      EventBinding.addListener( text, "MouseDown", listener );
      var domEvent = TestUtil.fakeMouseEventDOM( text.getElement(), "mousedown", 1, 0, 0 );
      TestUtil.fireFakeDomEvent( domEvent );

      // SWT doesnt support preventing native selection behavior (e.g. Text widget)
      assertFalse( EventHandlerUtil.wasStopped( domEvent ) );
    },

    testDoItFalseMouseWheel : function() {
      var listener = function( event ) {
        event.doit = false;
      };

      EventBinding.addListener( text, "MouseWheel", listener );
      var domEvent = TestUtil.fakeMouseEventDOM( text.getElement(), "wheel", 1, 0, 0 );
      domEvent.deltaY = -1;
      TestUtil.fireFakeDomEvent( domEvent );

      assertTrue( EventHandlerUtil.wasStopped( domEvent ) );
    },

    testAddListener_KeyUp : function() {
      TestUtil.flush();
      var logger = this._createLogger();

      EventBinding.addListener( text, "KeyUp", logger );
      TestUtil.keyDown( textEl, "A" );
      assertEquals( 0, logger.log.length );
      TestUtil.keyUp( textEl, "A" );

      assertEquals( 1, logger.log.length );
    },


    testAddListener_FocusInEvent : function() {
      text.blur();
      var logger = this._createLogger();

      EventBinding.addListener( text, "FocusIn", logger );
      text.focus();

      assertEquals( 1, logger.log.length );
    },

    testAddListener_FocusOutEvent : function() {
      text.focus();
      var logger = this._createLogger();

      EventBinding.addListener( text, "FocusOut", logger );
      text.blur();

      assertEquals( 1, logger.log.length );
    },

    testAddListener_MouseDown : function() {
      var logger = this._createLogger();

      EventBinding.addListener( text, "MouseDown", logger );
      TestUtil.fakeMouseEventDOM( textEl, "mousedown" );

      assertEquals( 1, logger.log.length );
    },

    testAddListener_MouseUp : function() {
      var logger = this._createLogger();

      TestUtil.fakeMouseEventDOM( textEl, "mousedown" );
      EventBinding.addListener( text, "MouseUp", logger );
      TestUtil.fakeMouseEventDOM( textEl, "mouseup" );

      assertEquals( 1, logger.log.length );
    },

    testAddListener_MouseMove : function() {
      var logger = this._createLogger();

      TestUtil.fakeMouseEventDOM( textEl, "mouseover" );
      EventBinding.addListener( text, "MouseMove", logger );
      TestUtil.fakeMouseEventDOM( textEl, "mousemove" );

      assertEquals( 1, logger.log.length );
    },

    testAddListener_MouseEnter : function() {
      var logger = this._createLogger();

      EventBinding.addListener( text, "MouseEnter", logger );
      TestUtil.fakeMouseEventDOM( textEl, "mouseover" );

      assertEquals( 1, logger.log.length );
    },

    testAddListener_MouseExit : function() {
      var logger = this._createLogger();

      TestUtil.fakeMouseEventDOM( textEl, "mouseover" );
      EventBinding.addListener( text, "MouseExit", logger );
      TestUtil.fakeMouseEventDOM( textEl, "mouseout" );

      assertEquals( 1, logger.log.length );
    },

    testAddListener_Show : function() {
      var logger = this._createLogger();
      text.setVisibility( false );

      EventBinding.addListener( text, "Show", logger );
      text.setVisibility( true );

      assertEquals( 1, logger.log.length );
    },

    testAddListener_Hide : function() {
      var logger = this._createLogger();
      text.setVisibility( true );

      EventBinding.addListener( text, "Hide", logger );
      text.setVisibility( false );

      assertEquals( 1, logger.log.length );
    },

    testAddListener_Dispose : function() {
      var logger = this._createLogger();
      text.setVisibility( true );

      EventBinding.addListener( text, "Dispose", logger );
      text.destroy();

      assertEquals( 1, logger.log.length );
    },

    testAddListener_VerifyEvent : function() {
      TestUtil.flush();
      var logger = this._createLogger();

      EventBinding.addListener( text, "Verify", logger );
      this._inputText( text, "goo" );

      assertEquals( 1, logger.log.length );
    },

    testRemoveListener_VerifyEventBinding : function() {
      TestUtil.flush();
      var logger = this._createLogger();

      EventBinding.addListener( text, "Verify", logger );
      EventBinding.removeListener( text, "Verify", logger );
      this._inputText( text, "goo" );

      assertEquals( 0, logger.log.length );
      assertEquals( "goo", text.getValue() );
    },

    testVerifyEventFiredBeforeChange : function() {
      TestUtil.flush();
      text.setValue( "foo" );
      var textValue;
      var handler = function( event ) {
        textValue = event.widget.getText();
      };

      EventBinding.addListener( text, "Verify", handler );
      this._inputText( text, "bar" );

      assertEquals( "bar", text.getValue() );
      assertEquals( "foo", textValue );
    },

    testVerifyEventDoItFalse : function() {
      TestUtil.flush();
      text.setValue( "foo" );
      var handler = function( event ) {
        event.doit = false;
      };

      EventBinding.addListener( text, "Verify", handler );
      this._inputText( text, "bar" );

      assertEquals( "foo", text.getValue() );
      assertEquals( "foo", text.getComputedValue() );
    },

    testVerifyEventDoItFalseSelection : function() {
      TestUtil.flush();
      text.setValue( "fooxxx" );
      var handler = function( event ) {
        event.doit = false;
      };

      EventBinding.addListener( text, "Verify", handler );
      this._inputText( text, "foobarxxx", [ 3, 3 ] );

      assertEquals( 3, text._getSelectionStart() );
      assertEquals( 0, text._getSelectionLength() );
    },

    testVerifyBindingProtectAgainstTypeOverwrite : function() {
      TestUtil.flush();
      text.setValue( "foo" );
      var handler = function( event ) {
        event.type = "boom";
      } ;

      EventBinding.addListener( text, "Verify", handler );
      this._inputText( text, "bar" );

      assertEquals( "bar", text.getValue() );
    },

    testVerifyEventTextOverwrite : function() {
      TestUtil.flush();
      text.setValue( "foo" );
      var handler = function( event ) {
        event.text = "bar";
      };

      EventBinding.addListener( text, "Verify", handler );
      this._inputText( text, "foob", [ 3, 3 ] );

      assertEquals( "foobar", text.getValue() );
    },

    testVerifyEventSelectionAfterTextOverwrite : function() {
      TestUtil.flush();
      text.setValue( "foo" );
      var handler = function( event ) {
        event.text = "bar";
      } ;

      EventBinding.addListener( text, "Verify", handler );
      this._inputText( text, "foxo", [ 2, 2 ] );

      assertEquals( "fobaro", text.getValue() );
      assertEquals( 5, text._getSelectionStart() );
      assertEquals( 0, text._getSelectionLength() );
    },

    testVerifyEventSelectionAfterReplacementTextOverwrite : function() {
      TestUtil.flush();
      text.setValue( "foo" );
      var handler = function( event ) {
        event.text = "bar";
      } ;

      EventBinding.addListener( text, "Verify", handler );
      this._inputText( text, "fxo", [ 1, 2 ] );

      assertEquals( "fbaro", text.getValue() );
      assertEquals( 4, text._getSelectionStart() );
      assertEquals( 0, text._getSelectionLength() );
    },

    testSelectionDuringVerifyEvent : function() {
      TestUtil.flush();
      text.setValue( "foo" );
      var selection;
      var handler = function( event ) {
        event.text = "bar";
        selection = event.widget.getSelection();
      };

      EventBinding.addListener( text, "Verify", handler );
      this._inputText( text, "foxo", [ 2, 2 ] );

      assertEquals( 5, text._getSelectionStart() );
      assertEquals( 0, text._getSelectionLength() );
      assertEquals( [ 2, 2 ], selection );
    },

    testSelectionByKeyPressDuringVerifyEvent : function() {
      TestUtil.flush();
      text.setValue( "foo" );
      var selection;
      var handler = function( event ) {
        event.text = "bar";
        selection = event.widget.getSelection();
      } ;

      EventBinding.addListener( text, "Verify", handler );
      text._setSelectionStart( 2 );
      text._setSelectionLength( 0 );
      TestUtil.press( text, "x" );
      this._inputText( text, "foxo", [ 2, 2 ] );

      assertEquals( 5, text._getSelectionStart() );
      assertEquals( 0, text._getSelectionLength() );
      assertEquals( [ 2, 2 ], selection );
    },

    testAddListener_ModifyEvent : function() {
      TestUtil.flush();
      var logger = this._createLogger();

      EventBinding.addListener( text, "Modify", logger );
      text.setValue( "foo" );

      assertEquals( 1, logger.log.length );
    },

    testAddListener_VerifyAndModifyEvent : function() {
      TestUtil.flush();
      var logger = this._createLogger();

      EventBinding.addListener( text, "Modify", logger );
      EventBinding.addListener( text, "Verify", logger );
      this._inputText( text, "foo" );

      assertEquals( 2, logger.log.length );
      assertEquals( SWT.Verify, logger.log[ 0 ].type );
      assertEquals( SWT.Modify, logger.log[ 1 ].type );
    },

    testAddListener_PaintEvent : function() {
      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.Canvas",
        "properties" : {
          "style" : [ ],
          "parent" : "w2"
        }
      } );
      var canvas = ObjectRegistry.getObject( "w4" );
      var logger = this._createLogger();
      TestUtil.flush();

      EventBinding.addListener( canvas, "Paint", logger );
      canvas.dispatchSimpleEvent( "paint" );
      TestUtil.flush();

      assertEquals( 1, logger.log.length );
      canvas.destroy();
    },

    testAddListener_SelectionEventOnButton : function() {
      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2"
        }
      } );
      var button = ObjectRegistry.getObject( "w4" );
      var logger = this._createLogger();
      TestUtil.flush();

      EventBinding.addListener( button, "Selection", logger );
      TestUtil.click( button );
      TestUtil.flush();

      assertEquals( 1, logger.log.length );
    },

    testAddListenerToCombo_ModifyEvent : function() {
      var combo = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.Combo" );
      TestUtil.flush();
      var logger = this._createLogger();

      EventBinding.addListener( combo, "Modify", logger );
      TestUtil.fakeResponse( true );
      combo._field.setValue( "foo" );
      TestUtil.fakeResponse( false);

      assertEquals( 1, logger.log.length );
      combo.destroy();
    },

    testAddListenerToCombo_VerifyEvent : function() {
      var combo = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.Combo" );
      TestUtil.flush();
      var logger = this._createLogger();

      EventBinding.addListener( combo, "Verify", logger );
      this._inputText( combo._field, "goo" );

      assertEquals( 1, logger.log.length );
      rwt.remote.Connection.getInstance().send();
      combo.destroy();
    },

    testAddListenerToScale_SelectionEvent : function() {
      var scale = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.Scale" );
      TestUtil.flush();
      var logger = this._createLogger();

      EventBinding.addListener( scale, "Selection", logger );
      scale.dispatchSimpleEvent( "selectionChanged" );

      assertEquals( 1, logger.log.length );
      rwt.remote.Connection.getInstance().send();
      scale.destroy();
    },

    testAddListenerToSpinner_SelectionEvent : function() {
      var spinner = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.Spinner" );
      TestUtil.flush();
      var logger = this._createLogger();

      EventBinding.addListener( spinner, "Selection", logger );
      spinner.setValue( 23 );

      assertEquals( 1, logger.log.length );
      rwt.remote.Connection.getInstance().send();
      spinner.destroy();
    },

    testAddListenerToSpinner_ModifyEvent : function() {
      var spinner = TestUtil.createWidgetByProtocol( "w4", "w2", "rwt.widgets.Spinner" );
      TestUtil.flush();
      var logger = this._createLogger();

      EventBinding.addListener( spinner, "Modify", logger );
      spinner._textfield.setValue( "foo" );

      assertEquals( 1, logger.log.length );
      rwt.remote.Connection.getInstance().send();
      spinner.destroy();
    },

    /////////
    // helper

    _createLogger : function() {
      var log = [];
      var result = function( arg ) {
        log.push( arg );
      };
      result.log = log;
      return result;
    },

    setUp : function() {
      TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE", "RIGHT" ],
          "parent" : "w2"
        }
      } );
      TestUtil.flush();
      text = ObjectRegistry.getObject( "w3" );
      text.focus();
      textEl = text.getElement();
    },

    tearDown : function() {
      Processor.processOperation( {
        "target" : "w2",
        "action" : "destroy"
      } );
      text = null;
    },

    _inputText : function( textWidget, text, oldSel ) {
      TestUtil.forceTimerOnce(); // IE first input fix
      if( typeof oldSel !== "undefined" ) {
        textWidget._setSelectionStart( oldSel[ 0 ] );
        textWidget._setSelectionLength( oldSel[ 1 ] - oldSel[ 0 ] );
        TestUtil.click( textWidget ); // pasting
      }
      textWidget._inValueProperty = true;
      textWidget._inputElement.value = text;
      textWidget._inValueProperty = false;
      textWidget._oninput( { "propertyName" : "value" } );
      TestUtil.forceTimerOnce();
    }

  }

} );

}());
