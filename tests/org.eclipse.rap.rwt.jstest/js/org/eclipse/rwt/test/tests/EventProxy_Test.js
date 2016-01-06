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

var EventProxy = rwt.scripting.EventProxy;
var EventBinding = rwt.scripting.EventBinding;
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var Processor = rwt.remote.MessageProcessor;
var ObjectRegistry = rwt.remote.ObjectRegistry;
var WidgetProxyFactory = rwt.scripting.WidgetProxyFactory;

var text;
var shell;

rwt.qx.Class.define( "org.eclipse.rap.clientscripting.EventProxy_Test", {

  extend : rwt.qx.Object,

  members : {

    testCreateEventProxy : function() {
      var eventProxy;
      text.addEventListener( "keypress", function( event ) {
        eventProxy = new EventProxy( SWT.KeyDown, text, event );
      } );

      TestUtil.press( text, "a" );

      assertTrue( eventProxy instanceof EventProxy );
    },

    testEventProxyFields : function() {
      var eventProxy;
      text.addEventListener( "keypress", function( event ) {
        eventProxy = new EventProxy( SWT.KeyDown, text, event );
      } );

      TestUtil.press( text, "a" );

      // Test type (or initial value) of all currently supported fields
      assertTrue( eventProxy.doit );
      assertEquals( "string", typeof eventProxy.type );
      assertEquals( "string", typeof eventProxy.character );
      assertEquals( "string", typeof eventProxy.text );
      assertEquals( "number", typeof eventProxy.keyCode );
      assertEquals( "number", typeof eventProxy.stateMask );
      assertEquals( "number", typeof eventProxy.button );
      assertEquals( "number", typeof eventProxy.x );
      assertEquals( "number", typeof eventProxy.y );
      assertTrue( eventProxy.widget === rap.getObject( "w3" ) );
    },

    testType : function() {
      var eventProxy;
      text.addEventListener( "keypress", function( event ) {
        eventProxy = new EventProxy( SWT.KeyDown, text, event );
      } );

      TestUtil.press( text, "a" );

      assertEquals( SWT.KeyDown, eventProxy.type );
    },

    testCharacter : function() {
      var eventProxy;
      text.addEventListener( "keypress", function( event ) {
        eventProxy = new EventProxy( SWT.KeyDown, text, event );
      } );

      TestUtil.press( text, "a" );

      assertEquals( "a", eventProxy.character );
    },

    testKeyCodeCharacterLowerCase : function() {
      var eventProxy;
      text.addEventListener( "keypress", function( event ) {
        eventProxy = new EventProxy( SWT.KeyDown, text, event );
      } );

      TestUtil.press( text, "a" );

      assertEquals( 97, eventProxy.keyCode );
    },

    testKeyCodeCharacterUpperCase : function() {
      var eventProxy;
      text.addEventListener( "keypress", function( event ) {
        eventProxy = new EventProxy( SWT.KeyDown, text, event );
      } );

      TestUtil.press( text, "A" );

      assertEquals( 97, eventProxy.keyCode );
    },

    testKeyCodeCharacterNonPrintable : function() {
      var eventProxy;
      text.addEventListener( "keypress", function( event ) {
        eventProxy = new EventProxy( SWT.KeyDown, text, event );
      } );

      TestUtil.press( text, "Up" );

      assertEquals( '\u0000', eventProxy.character );
      assertEquals( SWT.ARROW_UP, eventProxy.keyCode );
    },

    testKeyCodeModifierKey : function() {
      var eventProxy;
      text.addEventListener( "keypress", function( event ) {
        eventProxy = new EventProxy( SWT.KeyDown, text, event );
      } );

      var shift = rwt.event.DomEvent.SHIFT_MASK;
      TestUtil.keyDown( text.getElement(), "Shift", shift );

      assertEquals( '\u0000', eventProxy.character );
      assertEquals( SWT.SHIFT, eventProxy.keyCode );
    },

    testModifierStateMask : function() {
      var eventProxy;
      text.addEventListener( "keypress", function( event ) {
        eventProxy = new EventProxy( SWT.KeyDown, text, event );
      } );

      var shift = rwt.event.DomEvent.SHIFT_MASK;
      var ctrl = rwt.event.DomEvent.CTRL_MASK;
      TestUtil.keyDown( text.getElement(), "A", shift | ctrl );

      assertEquals( SWT.SHIFT | SWT.CTRL, eventProxy.stateMask );
    },

    testMouseEventStateMask : function() {
      var eventProxy;
      text.addEventListener( "mousedown", function( event ) {
        eventProxy = new EventProxy( SWT.MouseDown, text, event );
      } );

      TestUtil.shiftClick( text );

      assertEquals( SWT.SHIFT, eventProxy.stateMask );
    },

    testMouseEventButtonLeft : function() {
      var eventProxy;
      text.addEventListener( "mousedown", function( event ) {
        eventProxy = new EventProxy( SWT.MouseDown, text, event );
      } );

      TestUtil.click( text );

      assertEquals( 1, eventProxy.button );
    },

    testMouseEventButtonRight : function() {
      var eventProxy;
      text.addEventListener( "mousedown", function( event ) {
        eventProxy = new EventProxy( SWT.MouseDown, text, event );
      } );

      TestUtil.rightClick( text );

      assertEquals( 3, eventProxy.button );
    },

    testMouseEventLocation : function() {
      text.setLocation( 10, 20 );
      text.setBorder( null );
      TestUtil.flush();
      var eventProxy;
      text.addEventListener( "mousedown", function( event ) {
        eventProxy = new EventProxy( SWT.MouseDown, text, event );
      } );

      TestUtil.click( text, 23, 34 );

      assertEquals( 3, eventProxy.x );
      assertEquals( 4, eventProxy.y );
    },

    testMouseEventLocationWithBorder : function() {
      text.setLocation( 10, 20 );
      text.setBorder( new rwt.html.Border( 2, "solid", "black" ) );
      TestUtil.flush();
      var eventProxy;
      text.addEventListener( "mousedown", function( event ) {
        eventProxy = new EventProxy( SWT.MouseDown, text, event );
      } );

      TestUtil.click( text, 23, 34 );

      assertEquals( 1, eventProxy.x );
      assertEquals( 2, eventProxy.y );
    },

    // NOTE : In webkit there is an issue with delayed rendered selection which can not be
    //        reproduced in testing, therefor this can produce false greens: Bug 404615
    testGetSelectionInModifyEvent : function() {
      var selection;
      text.setValue( "" );
      text.addEventListener( "changeValue", function( event ) {
        // NOTE: As in SWT, the event itself does not have the start/end fields set,
        //       but the widget should already have been updated with the new selection
        var eventProxy = new EventProxy( SWT.Modify, text, event );
        selection = eventProxy.widget.getSelection();
      } );

      this._textCharInput( text, "a" );
      TestUtil.forceTimerOnce();
      this._textCharInput( text, "b" );
      TestUtil.forceTimerOnce();

      assertEquals( [ 2, 2 ], selection );
    },

    testVerifyEventCharacter : function() {
      var eventProxy;
      text.setValue( "fooba" );
      text.addEventListener( "input", function( event ) {
        eventProxy = new EventProxy( SWT.Verify, text, event );
      } );

      this._textCharInput( text, "A" );

      assertEquals( 65, eventProxy.keyCode );
      assertEquals( 'A', eventProxy.character );
      assertEquals( "A", eventProxy.text );
      assertEquals( 5, eventProxy.start );
      assertEquals( 5, eventProxy.end );
    },

    testVerifyEventCharacterFirstChar : function() {
      var eventProxy;
      text.setValue( "" );
      text.addEventListener( "input", function( event ) {
        eventProxy = new EventProxy( SWT.Verify, text, event );
      } );

      this._textCharInput( text, "A" );

      assertEquals( 65, eventProxy.keyCode );
      assertEquals( 'A', eventProxy.character );
      assertEquals( "A", eventProxy.text );
      assertEquals( 0, eventProxy.start );
      assertEquals( 0, eventProxy.end );
    },

    testVerifyEventCharacterLeftCharacterSame : function() {
      var eventProxy;
      text.setValue( "fooba" );
      text.addEventListener( "input", function( event ) {
        eventProxy = new EventProxy( SWT.Verify, text, event );
      } );

      this._textCharInput( text, "a" );

      assertEquals( 65, eventProxy.keyCode );
      assertEquals( 'a', eventProxy.character );
      assertEquals( "a", eventProxy.text );
      assertEquals( 5, eventProxy.start );
      assertEquals( 5, eventProxy.end );
    },

    testVerifyEventPasteCharacter : function() {
      var eventProxy;
      text.setValue( "fooba" );
      text.addEventListener( "input", function( event ) {
        eventProxy = new EventProxy( SWT.Verify, text, event );
      } );

      this._textPaste( text, "fooAba", [ 3, 3 ] );

      assertEquals( 0, eventProxy.keyCode );
      assertEquals( '\u0000', eventProxy.character );
      assertEquals( "A", eventProxy.text );
      assertEquals( 3, eventProxy.start );
      assertEquals( 3, eventProxy.end );
    },

    testVerifyEventPasteCharacterBetweenSameCharacters : function() {
      var eventProxy;
      text.setValue( "fooba" );
      text.addEventListener( "input", function( event ) {
        eventProxy = new EventProxy( SWT.Verify, text, event );
      } );

      this._textPaste( text, "foooba", [ 2, 2 ] );

      assertEquals( "o", eventProxy.text );
      assertEquals( 2, eventProxy.start );
      assertEquals( 2, eventProxy.end );
    },

    testVerifyEventPasteText : function() {
      var eventProxy;
      text.setValue( "fooba" );
      text.addEventListener( "input", function( event ) {
        eventProxy = new EventProxy( SWT.Verify, text, event );
      } );

      this._textPaste( text, "fooLALAba", [ 3, 3 ] );

      assertEquals( "LALA", eventProxy.text );
      assertEquals( 3, eventProxy.start );
      assertEquals( 3, eventProxy.end );
    },

    testVerifyEventDeleteSelectedCharacter : function() {
      var eventProxy;
      text.setValue( "fooba" );
      text.addEventListener( "input", function( event ) {
        eventProxy = new EventProxy( SWT.Verify, text, event );
      } );

      this._textPaste( text, "foob", [ 4, 5 ] );

      assertEquals( 0, eventProxy.keyCode );
      assertEquals( '\u0000', eventProxy.character );
      assertEquals( "", eventProxy.text );
      assertEquals( 4, eventProxy.start );
      assertEquals( 5, eventProxy.end );
    },

    testVerifyEventDeleteCharacterWithDel : function() {
      var eventProxy;
      text.setValue( "fooba" );
      text.addEventListener( "input", function( event ) {
        eventProxy = new EventProxy( SWT.Verify, text, event );
      } );

      text.setSelection( [ 4, 4 ] );
      TestUtil.keyDown( text, 46 );
      text._inValueProperty = true;
      text._inputElement.value = "foob";
      text._inValueProperty = false;
      text._oninput( { "propertyName" : "value" } );
      TestUtil.keyUp( text, 46 );

      assertEquals( 0, eventProxy.keyCode );
      assertEquals( '\u0000', eventProxy.character );
      assertEquals( "", eventProxy.text );
      assertEquals( 4, eventProxy.start );
      assertEquals( 5, eventProxy.end );
    },

    testVerifyEventDeleteCharacterWithBackspace : function() {
      var eventProxy;
      text.setValue( "fooba" );
      text.addEventListener( "input", function( event ) {
        eventProxy = new EventProxy( SWT.Verify, text, event );
      } );

      text.setSelection( [ 5, 5 ] );
      TestUtil.keyDown( text, 8 );
      text._inValueProperty = true;
      text._inputElement.value = "foob";
      text._inValueProperty = false;
      text._oninput( { "propertyName" : "value" } );
      TestUtil.keyUp( text, 8 );

      assertEquals( 0, eventProxy.keyCode );
      assertEquals( '\u0000', eventProxy.character );
      assertEquals( "", eventProxy.text );
      assertEquals( 4, eventProxy.start );
      assertEquals( 5, eventProxy.end );
    },

    testVerifyEventDeleteMultiple : function() {
      var eventProxy;
      text.setValue( "fooba" );
      text.addEventListener( "input", function( event ) {
        eventProxy = new EventProxy( SWT.Verify, text, event );
      } );

      this._textPaste( text, "fba", [ 1, 3 ] );

      assertEquals( 0, eventProxy.keyCode );
      assertEquals( '\u0000', eventProxy.character );
      assertEquals( "", eventProxy.text );
      assertEquals( 1, eventProxy.start );
      assertEquals( 3, eventProxy.end );
    },

    testVerifyEventDeleteMultipleAmbigous : function() {
      var eventProxy;
      text.setValue( "foooooba" );
      text.addEventListener( "input", function( event ) {
        eventProxy = new EventProxy( SWT.Verify, text, event );
      } );

      this._textPaste( text, "fooba", [ 1, 4 ] );

      assertEquals( "", eventProxy.text );
      assertEquals( 1, eventProxy.start );
      assertEquals( 4, eventProxy.end );
    },

    testVerifyEventReplaceLonger : function() {
      var eventProxy;
      text.setValue( "foooba" );
      text.addEventListener( "input", function( event ) {
        eventProxy = new EventProxy( SWT.Verify, text, event );
      } );

      this._textPaste( text, "fooaaba", [ 3, 4 ] );

      assertEquals( "aa", eventProxy.text );
      assertEquals( 3, eventProxy.start );
      assertEquals( 4, eventProxy.end );
    },

    testVerifyEventReplaceLongerSameEnd : function() {
      var eventProxy;
      text.setValue( "foooba" );
      text.addEventListener( "input", function( event ) {
        eventProxy = new EventProxy( SWT.Verify, text, event );
      } );

      this._textPaste( text, "fooaaba", [ 3, 5 ] );

      assertEquals( "aab", eventProxy.text );
      assertEquals( 3, eventProxy.start );
      assertEquals( 5, eventProxy.end );
    },

    testVerifyEventInsertSameStart : function() {
      var eventProxy;
      text.setValue( "foooba" );
      text.addEventListener( "input", function( event ) {
        eventProxy = new EventProxy( SWT.Verify, text, event );
      } );

      this._textPaste( text, "foooooba", [ 3, 3 ] );

      assertEquals( "oo", eventProxy.text );
      assertEquals( 3, eventProxy.start );
      assertEquals( 3, eventProxy.end );
    },

    testVerifyEventReplaceShorter : function() {
      var eventProxy;
      text.setValue( "foooba" );
      text.addEventListener( "input", function( event ) {
        eventProxy = new EventProxy( SWT.Verify, text, event );
      } );

      this._textPaste( text, "faba", [ 1, 4 ] );

      assertEquals( "a", eventProxy.text );
      assertEquals( 1, eventProxy.start );
      assertEquals( 4, eventProxy.end );
    },

    testVerifyEventReplaceShorterSameEnd : function() {
      var eventProxy;
      text.setValue( "foooba" );
      text.addEventListener( "input", function( event ) {
        eventProxy = new EventProxy( SWT.Verify, text, event );
      } );

      this._textPaste( text, "faoba", [ 1, 4 ] );

      assertEquals( "ao", eventProxy.text );
      assertEquals( 1, eventProxy.start );
      assertEquals( 4, eventProxy.end );
    },

    testPaintEventHasGC : function() {
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
      TestUtil.flush();
      var gc;

      EventBinding.addListener( canvas, "Paint", function( ev ) {
        gc = ev.gc;
      } );
      WidgetProxyFactory.getWidgetProxy( canvas ).redraw();

      assertNotNull( gc );
      assertTrue( gc.stroke instanceof Function );
      canvas.destroy();
    },

    testPaintEventHasSameGCTwice : function() {
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
      TestUtil.flush();
      var gc = [];

      EventBinding.addListener( canvas, "Paint", function( ev ) {
        gc.push( ev.gc );
      } );
      WidgetProxyFactory.getWidgetProxy( canvas ).redraw();
      WidgetProxyFactory.getWidgetProxy( canvas ).redraw();

      assertTrue( gc[ 0 ].stroke instanceof Function );
      assertIdentical( gc[ 0 ], gc[ 1 ] );
      canvas.destroy();
    },

    testPaintUsesExistingGC : function() {
      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.Canvas",
        "properties" : {
          "style" : [ ],
          "parent" : "w2"
        }
      } );
      Processor.processOperation( {
        "target" : "w5",
        "action" : "create",
        "type" : "rwt.widgets.GC",
        "properties" : {
          "parent" : "w4"
        }
      } );
      var serverGc = ObjectRegistry.getObject( "w5" );
      var canvas = ObjectRegistry.getObject( "w4" );
      TestUtil.flush();
      var gc;

      EventBinding.addListener( canvas, "Paint", function( ev ) {
        gc = ev.gc;
      } );
      WidgetProxyFactory.getWidgetProxy( canvas ).redraw();

      assertIdentical( serverGc.getNativeContext(), gc );
      canvas.destroy();
    },

    testPaintResetsPropsToWidgetValues : function() {
      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.Canvas",
        "properties" : {
          "style" : [ ],
          "parent" : "w2",
          "bounds" : [10, 10, 10, 10] // The context object is buggy when no bounds are set (blink version 41+)
        }
      } );
      var canvas = ObjectRegistry.getObject( "w4" );
      canvas.setBackgroundColor( "#aaaaaa" );
      canvas.setTextColor( "#bbbbbb" );
      canvas.setFont( rwt.html.Font.fromString( "11px italic Arial") );
      TestUtil.flush();
      var props;

      EventBinding.addListener( canvas, "Paint", function( ev ) {
        var gc = ev.gc;
        props = [
          gc.strokeStyle,
          gc.fillStyle,
          rwt.html.Font.fromString( gc.font ).toCss()
        ];
        gc.strokeStyle = "#ff00ff";
        gc.fillStyle = "#00ff00";
      } );
      WidgetProxyFactory.getWidgetProxy( canvas ).redraw();
      WidgetProxyFactory.getWidgetProxy( canvas ).redraw();

      assertEquals( [ 187, 187, 187 ], rwt.util.Colors.stringToRgb( props[ 0 ] ) );
      assertEquals( [ 170, 170, 170 ], rwt.util.Colors.stringToRgb( props[ 1 ] ) );
      assertEquals( "italic 11px Arial", props[ 2 ] );
      canvas.destroy();
    },

    testPaintResetsPropsToWidgetValuesNull : function() {
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
      canvas.setBackgroundColor( null );
      canvas.setTextColor( null );
      canvas.setFont( null );
      TestUtil.flush();
      var props;

      EventBinding.addListener( canvas, "Paint", function( ev ) {
        var gc = ev.gc;
        props = [ gc.strokeStyle, gc.fillStyle ];
        gc.strokeStyle = "#ff00ff";
        gc.fillStyle = "#00ff00";
      } );
      WidgetProxyFactory.getWidgetProxy( canvas ).redraw();
      WidgetProxyFactory.getWidgetProxy( canvas ).redraw();

      assertEquals( [ 0, 0, 0 ], rwt.util.Colors.stringToRgb( props[ 0 ] ) );
      assertEquals( [ 0, 0, 0 ], rwt.util.Colors.stringToRgb( props[ 1 ] ) );
      canvas.destroy();
    },


    testPaintEventFromServer : function() {
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
      Processor.processOperation( {
        "target" : "w5",
        "action" : "create",
        "type" : "rwt.widgets.GC",
        "properties" : {
          "parent" : "w4"
        }
      } );
      var serverGc = ObjectRegistry.getObject( "w5" );
      TestUtil.flush();
      var fontArr = [ [ "Arial" ], 11, false, true ];
      var props;

      EventBinding.addListener( canvas, "Paint", function( ev ) {
        var gc = ev.gc;
        props = [
          gc.strokeStyle,
          gc.fillStyle,
          rwt.html.Font.fromString( gc.font ).toCss()
        ];
        gc.strokeStyle = "#ff00ff";
        gc.fillStyle = "#00ff00";
      } );
      serverGc.init( 0, 0, 500, 500, fontArr, [ 170, 170, 170 ], [ 187, 187, 187 ] );

      assertEquals( [ 187, 187, 187 ], rwt.util.Colors.stringToRgb( props[ 0 ] ) );
      assertEquals( [ 170, 170, 170 ], rwt.util.Colors.stringToRgb( props[ 1 ] ) );
      assertEquals( "italic 11px Arial", props[ 2 ] );
      canvas.destroy();
    },

    /////////
    // Helper

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      shell.setBorder( null );
      shell.setLocation( 10, 10 );
      shell.setDimension( 300, 300 );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE", "RIGHT" ],
          "parent" : "w2",
          "bounds" : [ 0, 0, 10, 10 ]
        }
      } );
      TestUtil.flush();
      text = ObjectRegistry.getObject( "w3" );
      text.focus();
    },

    tearDown : function() {
      Processor.processOperation( {
        "target" : "w2",
        "action" : "destroy"
      } );
      text = null;
    },

    _textCharInput : function( textWidget, character ) {
      TestUtil.forceTimerOnce();
      TestUtil.keyDown( textWidget, character );
      // we will assume that the carret is at the end
      var newValue = textWidget._inputElement.value + character;
      var carret = textWidget._inputElement.value.length;
      textWidget.setSelection( [ carret, carret ] );
      textWidget._inValueProperty = true;
      textWidget._inputElement.value = newValue;
      textWidget._inValueProperty = false;
      textWidget._oninput( { "propertyName" : "value" } );
      TestUtil.keyUp( textWidget, character );
    },

    _textPaste : function( textWidget, value, oldSel ) {
      TestUtil.forceTimerOnce();
      if( typeof oldSel !== "undefined" ) {
        textWidget._setSelectionStart( oldSel[ 0 ] );
        textWidget._setSelectionLength( oldSel[ 1 ] - oldSel[ 0 ] );
        TestUtil.click( textWidget ); // pasting
      }
      textWidget._inValueProperty = true;
      textWidget._inputElement.value = value;
      textWidget._inValueProperty = false;
      textWidget._oninput( { "propertyName" : "value" } );
    }

  }

} );

}());
