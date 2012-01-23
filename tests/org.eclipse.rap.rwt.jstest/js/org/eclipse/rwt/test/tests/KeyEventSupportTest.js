/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.KeyEventSupportTest", {

  extend : qx.core.Object,
  
  construct : function() {
    this.base( arguments );
    org.eclipse.rwt.test.fixture.TestUtil.prepareTimerUse();
  },
  
  members : {
    
    testSetActiveKeysByProtocol : function() {
      var processor = org.eclipse.rwt.protocol.Processor;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      
      processor.processOperation( {
        "target" : "w1",
        "action" : "set",
        "properties" : {
          "activeKeys" : [ "#65", "CTRL+#65" ]
        }
      } );
      
      var expected = { "#65" : true, "CTRL+#65" : true };
      assertEquals( expected, keyUtil._keyBindings );
      keyUtil.setKeyBindings( {} );
    },

    testSetCancelKeysByProtocol : function() {
      var processor = org.eclipse.rwt.protocol.Processor;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      
      processor.processOperation( {
        "target" : "w1",
        "action" : "set",
        "properties" : {
          "cancelKeys" : [ "#65", "CTRL+#65" ]
        }
      } );
      
      var expected = { "#65" : true, "CTRL+#65" : true };
      assertEquals( expected, keyUtil._cancelKeys );
      keyUtil.setCancelKeys( {} );
    },

    testKeyBindingSingleCharacter : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.initRequestLog();
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "#66" : true } );
      TestUtil.press( widget, "b" );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var expected1 = "org.eclipse.swt.events.keyDown=w1"; 
      var expected2 = "org.eclipse.swt.events.keyDown.keyCode=66";
      var expected3 = "org.eclipse.swt.events.keyDown.charCode=98";
      var msg = TestUtil.getMessage();
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      assertTrue( msg.indexOf( expected3 ) != -1 );
      widget.destroy();
    },

    testKeyBindingWithMissingKeypressEventUpperCase : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.initRequestLog();
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "CTRL+SHIFT+#66" : true } );
      var dom = qx.event.type.DomEvent;
      var mod = dom.CTRL_MASK | dom.SHIFT_MASK;
      TestUtil.fireFakeKeyDomEvent( widget.getElement(), "keydown", "B", mod );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var expected1 = "org.eclipse.swt.events.keyDown=w1"; 
      var expected2 = "org.eclipse.swt.events.keyDown.keyCode=66";
      var expected2 = "org.eclipse.swt.events.keyDown.charCode=66";
      var msg = TestUtil.getMessage();
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      widget.destroy();
    },

    testKeyBindingWithMissingKeypressEventLowerCase : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.initRequestLog();
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "CTRL+#66" : true } );
      var dom = qx.event.type.DomEvent;
      TestUtil.fireFakeKeyDomEvent( widget.getElement(), "keydown", "b", dom.CTRL_MASK );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var expected1 = "org.eclipse.swt.events.keyDown=w1"; 
      var expected2 = "org.eclipse.swt.events.keyDown.keyCode=66";
      var expected2 = "org.eclipse.swt.events.keyDown.charCode=98";
      var msg = TestUtil.getMessage();
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      widget.destroy();
    },

    testKeyBindingSingleCharacterHold : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.initRequestLog();
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "#66" : true } );
      TestUtil.keyDown( widget.getElement(), "b", 0 );
      TestUtil.keyHold( widget.getElement(), "b", 0 );
      TestUtil.keyUp( widget.getElement(), "b", 0 );
      assertEquals( 2, TestUtil.getRequestsSend() );
      var expected1 = "org.eclipse.swt.events.keyDown=w1"; 
      var expected2 = "org.eclipse.swt.events.keyDown.keyCode=66";
      var expected2 = "org.eclipse.swt.events.keyDown.charCode=98";
      var msg = TestUtil.getRequestLog()[ 0 ];
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      msg = TestUtil.getRequestLog()[ 1 ];
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      widget.destroy();
    },

    testKeyBindingSingleCharacterHoldAndCancel : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.initRequestLog();
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "#66" : true } );
      keyUtil.setCancelKeys( { "#66" : true } );
      TestUtil.keyDown( widget.getElement(), "b", 0 );
      TestUtil.keyHold( widget.getElement(), "b", 0 );
      TestUtil.keyUp( widget.getElement(), "b", 0 );
      assertEquals( 2, TestUtil.getRequestsSend() );
      var expected1 = "org.eclipse.swt.events.keyDown=w1"; 
      var expected2 = "org.eclipse.swt.events.keyDown.keyCode=66";
      var expected2 = "org.eclipse.swt.events.keyDown.charCode=98";
      var msg = TestUtil.getRequestLog()[ 0 ];
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      msg = TestUtil.getRequestLog()[ 1 ];
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      widget.destroy();
    },

    testKeyBindingSpecialCharacter : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.initRequestLog();
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "/" : true } );
      TestUtil.press( widget, "/", false, 0 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var expected1 = "org.eclipse.swt.events.keyDown=w1"; 
      var expected2 = "org.eclipse.swt.events.keyDown.charCode=47"; 
      var msg = TestUtil.getMessage();
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      widget.destroy();
    },

    testKeyBindingPointCharacter : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.initRequestLog();
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "." : true } );
      TestUtil.press( widget, ".", false, 0 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var expected1 = "org.eclipse.swt.events.keyDown=w1"; 
      var expected2 = "org.eclipse.swt.events.keyDown.charCode=46"; 
      var msg = TestUtil.getMessage();
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      widget.destroy();
    },

    testKeyBindingsPreventDefaultNotCalled : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var prevented = false;
      var preventDefault = function() {
        prevented = true;
      };
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "#66" : true } );
      var node = widget._getTargetNode();
      var event = TestUtil.createFakeDomKeyEvent( node, "keydown", "b", 0 );
      event.preventDefault = preventDefault;
      TestUtil.fireFakeDomEvent( event );
      assertFalse( prevented );
      widget.destroy();
    },

    testKeyBindingWithServerKeyListener : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      widget.setUserData( "isControl", true );
      widget.setUserData( "keyListener", true );
      widget.focus();
      keyUtil.setKeyBindings( { "#66" : true } );
      assertEquals( 0, TestUtil.getRequestsSend() );
      TestUtil.press( widget, "b", false, 0 );
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertTrue( keyUtil._keyEventRequestRunning !== true );
      widget.destroy();
    },

    testKeyBindingWithClientKeyListener : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      var log = [];
      var logger = function( event ) {
        log.push( event );
      };
      widget.addEventListener( "keydown", logger );
      widget.addEventListener( "keypress", logger );
      widget.addEventListener( "keyup", logger );
      widget.focus();
      keyUtil.setKeyBindings( { "#66" : true } );
      assertEquals( 0, TestUtil.getRequestsSend() );
      TestUtil.press( widget, "a", false, 0 );
      assertEquals( 0, TestUtil.getRequestsSend() );
      assertEquals( 3, log.length );
      TestUtil.press( widget, "b", false, 0 );
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertEquals( 6, log.length );
      widget.destroy();
    },
    
    testKeyBindingsNoModifiersAllowed : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( {
        "66" : true
      } );
      var dom = qx.event.type.DomEvent;
      TestUtil.press( widget, "b", false, dom.CTRL_MASK );
      TestUtil.press( widget, "b", false, dom.ALT_MASK );
      TestUtil.press( widget, "b", false, dom.SHIFT_MASK );
      var all = dom.CTRL_MASK | dom.SHIFT_MASK | dom.ALT_MASK;
      TestUtil.press( widget, "b", false, all );
      assertEquals( 0, TestUtil.getRequestsSend() );
      widget.destroy();    
    },

    testKeyBindingsModifiersNeeded : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( {
        "SHIFT+#66" : true
      } );
      var dom = qx.event.type.DomEvent;
      TestUtil.press( widget, "b", false, 0 );
      TestUtil.press( widget, "b", false, dom.ALT_MASK );
      assertEquals( 0, TestUtil.getRequestsSend() );
      TestUtil.press( widget, "b", false, dom.SHIFT_MASK );
      assertEquals( 1, TestUtil.getRequestsSend() );
      widget.destroy();    
    },

    testKeyBindingsModifiersOrder : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "ALT+CTRL+SHIFT+#66" : true } );
      var dom = qx.event.type.DomEvent;
      TestUtil.press( widget, "b", false, 0 );
      TestUtil.press( widget, "b", false, dom.CTRL_MASK );
      TestUtil.press( widget, "b", false, dom.ALT_MASK );
      TestUtil.press( widget, "b", false, dom.SHIFT_MASK );
      assertEquals( 0, TestUtil.getRequestsSend() );
      var all = dom.CTRL_MASK | dom.SHIFT_MASK | dom.ALT_MASK;
      TestUtil.press( widget, "b", false, all );
      assertEquals( 1, TestUtil.getRequestsSend() );
      widget.destroy();          
    },

    testKeyBindingShiftAndNumberOnKeypress : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.initRequestLog();
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "SHIFT+#53" : true } );
      var dom = qx.event.type.DomEvent;
      TestUtil.fireFakeKeyDomEvent( widget.getElement(), "keydown", 53, dom.SHIFT_MASK );
      assertEquals( 0, TestUtil.getRequestsSend() );
      TestUtil.fireFakeKeyDomEvent( widget.getElement(), "keypress", 53, dom.SHIFT_MASK );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var expected1 = "org.eclipse.swt.events.keyDown=w1"; 
      var expected2 = "org.eclipse.swt.events.keyDown.keyCode=53";
      var expected3 = "org.eclipse.swt.events.keyDown.charCode=53";
      var msg = TestUtil.getMessage();
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      assertTrue( msg.indexOf( expected3 ) != -1 );
      widget.destroy();
    },

    testCancelKeysPreventDefaultNotCalled : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var prevented = false;
      var preventDefault = function() {
        prevented = true;
      };
      var widget = this._createWidget();
      keyUtil.setCancelKeys( { "#66" : true } ); // "b"
      var node = widget._getTargetNode();
      var event = TestUtil.createFakeDomKeyEvent( node, "keydown", "a", 0 );
      event.preventDefault = preventDefault;
      TestUtil.fireFakeDomEvent( event );
      assertFalse( prevented );
      widget.destroy();
    },

    testCancelKeysPreventDefaultCalled : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var preventedDown = false;
      var preventedPress = false;
      var widget = this._createWidget();
      keyUtil.setCancelKeys( { "#66" : true } );
      var node = widget._getTargetNode();
      var eventDown = TestUtil.createFakeDomKeyEvent( node, "keydown", "b", 0 );
      eventDown.preventDefault = function() {
        preventedDown = true;
      };
      var eventPress = TestUtil.createFakeDomKeyEvent( node, "keypress", "b", 0 );
      eventPress.preventDefault = function() {
        preventedPress = true;
      };
      TestUtil.fireFakeDomEvent( eventDown );
      TestUtil.fireFakeDomEvent( eventPress );
      assertTrue( preventedDown );
      assertTrue( preventedPress );
      widget.destroy();
    },

    testCancelKeysPreventDefaultCalledIfSent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var preventedDown = false;
      var preventedPress = false;
      var widget = this._createWidget();
      keyUtil.setCancelKeys( { "#13" : true } );
      keyUtil.setKeyBindings( { "#13" : true } );
      var node = widget._getTargetNode();
      var eventDown = TestUtil.createFakeDomKeyEvent( node, "keydown", 13, 0 );
      eventDown.preventDefault = function() {
        preventedDown = true;
      };
      var eventPress = TestUtil.createFakeDomKeyEvent( node, "keypress", 13, 0 );
      eventPress.preventDefault = function() {
        preventedPress = true;
      };
      TestUtil.fireFakeDomEvent( eventDown );
      TestUtil.fireFakeDomEvent( eventPress );
      assertTrue( preventedDown );
      assertTrue( preventedPress );
      widget.destroy();
    },

    testCancelKeysNotProcessed : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setCancelKeys( { "#66" : true } );
      var listener = function() {
        fail();
      };
      widget.addEventListener( "keydown", listener );
      widget.addEventListener( "keypress", listener );
      widget.addEventListener( "keyup", listener );
      TestUtil.press( widget, "b" );
      TestUtil.cleanUpKeyUtil();
      widget.destroy();
    },

    testBufferEvents : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var req = org.eclipse.swt.Request.getInstance();
      req.removeEventListener( "received", keyUtil._onRequestReceived, keyUtil );
      TestUtil.initRequestLog();
      var text = this._createTextWidget();
      var node = text._inputElement;
      TestUtil.press( text, "x", false, 0 );
      assertEquals( 0, keyUtil._bufferedEvents.length );
      TestUtil.press( text, "y", false, 0 );
      assertEquals( 1, keyUtil._bufferedEvents.length );
      TestUtil.press( text, "z", false, 0 );
      assertEquals( 2, keyUtil._bufferedEvents.length );
      assertEquals( 1, TestUtil.getRequestsSend() );
      keyUtil._onRequestReceived();
      assertEquals( 2, TestUtil.getRequestsSend() );
      assertEquals( 1, keyUtil._bufferedEvents.length );
      keyUtil._onRequestReceived();
      assertEquals( 3, TestUtil.getRequestsSend() );
      assertEquals( 0, keyUtil._bufferedEvents.length );
      keyUtil._pendingEventInfo = null;
      req.addEventListener( "received", keyUtil._onRequestReceived, keyUtil );
      this._disposeTextWidget( text );
    },

    testBufferedEventInfo : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var req = org.eclipse.swt.Request.getInstance();
      req.removeEventListener( "received", keyUtil._onRequestReceived, keyUtil );
      TestUtil.initRequestLog();
      var text = this._createTextWidget();
      TestUtil.press( text, "x", false, 0 );
      TestUtil.press( text, "y", false, 0 );
      assertEquals( 1, keyUtil._bufferedEvents.length );

      keyUtil._onRequestReceived();
      var msg = TestUtil.getRequestLog()[ 1 ];
      
      assertEquals( 2, TestUtil.getRequestsSend() );
      var expected1 = "org.eclipse.swt.events.keyDown=w3"; 
      var expected2 = "org.eclipse.swt.events.keyDown.keyCode=89"; 
      var expected3 = "org.eclipse.swt.events.keyDown.charCode=121"; 
      var expected4 = "org.eclipse.swt.events.keyDown.modifier=";
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      assertTrue( msg.indexOf( expected3 ) != -1 );
      assertTrue( msg.indexOf( expected4 ) != -1 );
      req.addEventListener( "received", keyUtil._onRequestReceived, keyUtil );
      this._disposeTextWidget( text );
    },

    testKeyEventListenerUntrustedKey : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      var node = text._inputElement;
      // cancel event
      assertNull( keyUtil._pendingEventInfo );
      assertEquals( 0, TestUtil.getRequestsSend() );
      TestUtil.fireFakeKeyDomEvent( node, "keydown", 37 );
      assertNull( keyUtil._pendingEventInfo );
      this._disposeTextWidget( text );
    },

    testKeyCodeLowerCase : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      TestUtil.press( text, "a", 0 );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=97";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=65";
      assertTrue( TestUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( TestUtil.getMessage().indexOf( expectedKey ) != -1 );
      this._disposeTextWidget( text );
    },

    testKeyCodeUpperCase : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      TestUtil.press( text, "A", 0 );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=65";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=65";
      assertTrue( TestUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( TestUtil.getMessage().indexOf( expectedKey ) != -1 );
      this._disposeTextWidget( text );
    },
    
    testSendModifier : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      var shift = qx.event.type.DomEvent.SHIFT_MASK;
      TestUtil.keyDown( text.getElement(), "Shift", shift );
      TestUtil.keyHold( text.getElement(), "Shift", shift );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=0";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=16";
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertTrue( TestUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( TestUtil.getMessage().indexOf( expectedKey ) != -1 );
      this._disposeTextWidget( text );
    },

    testSetActiveKeysByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "a", "b" ] );
      var expected = { "a" : true, "b" : true }
      assertEquals( expected, text.getUserData( "activeKeys" ) );
      this._disposeTextWidget( text );
    },

    testNonActiveCharKeysNotSent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [] );
      TestUtil.press( text, "a", 0 );
      TestUtil.press( text, "A", 0 );
      TestUtil.press( text, "z", 0 );
      TestUtil.press( text, "Z", 0 );
      assertEquals( 0, TestUtil.getRequestsSend() );
      this._disposeTextWidget( text );
    },

    testActiveLowerCharKeySent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "#90" ] );
      TestUtil.press( text, "a", 0 );
      TestUtil.press( text, "z", 0 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=122";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=90";
      assertTrue( TestUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( TestUtil.getMessage().indexOf( expectedKey ) != -1 );
      this._disposeTextWidget( text );
    },

    testActiveUpperCharKeySent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "#90" ] );
      TestUtil.press( text, "Z", 0 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=90";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=90";
      assertTrue( TestUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( TestUtil.getMessage().indexOf( expectedKey ) != -1 );
      this._disposeTextWidget( text );
    },

    testActiveSpecialKeySent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "#40" ] );
      TestUtil.press( text, "Up", 0 );
      TestUtil.press( text, "Down", 0 );
      assertEquals( 1, TestUtil.getRequestsSend() )
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=0";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=40";
      assertTrue( TestUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( TestUtil.getMessage().indexOf( expectedKey ) != -1 );
      this._disposeTextWidget( text );
    },

    testActiveSpecialCharKeySent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "/" ] );
      TestUtil.press( text, "\\", 0 );
      TestUtil.press( text, "/", 0 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=47";
      assertTrue( TestUtil.getMessage().indexOf( expectedChar ) != -1 );
      this._disposeTextWidget( text );
    },

    testActiveKeySequenceSent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "ALT+CTRL+#90" ] );
      var dom = qx.event.type.DomEvent;
      TestUtil.press( text, "z", false, 0 );
      TestUtil.press( text, "z", false, dom.CTRL_MASK );
      TestUtil.press( text, "z", false, dom.ALT_MASK );
      TestUtil.press( text, "z", false, dom.SHIFT_MASK );
      assertEquals( 0, TestUtil.getRequestsSend() );
      TestUtil.press( text, "z", false, dom.ALT_MASK | dom.CTRL_MASK);
      assertEquals( 1, TestUtil.getRequestsSend() );
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=90";
      assertTrue( TestUtil.getMessage().indexOf( expectedKey ) != -1 );
      this._disposeTextWidget( text );
    },

    testCancelKeySequenceNotProcessed : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setCancelKeys( text, [
        "#90", 
        "ALT+#90", 
        "CTRL+#90", 
        "SHIFT+#90" 
      ] );
      var log = [];
      var listener = function( e ) {
        log.push( e );
      };
      text.addEventListener( "keydown", listener );
      text.addEventListener( "keypress", listener );
      text.addEventListener( "keyup", listener );
      var dom = qx.event.type.DomEvent;
      TestUtil.press( text, "z", false, 0 );
      TestUtil.press( text, "z", false, dom.CTRL_MASK );
      TestUtil.press( text, "z", false, dom.ALT_MASK );
      TestUtil.press( text, "z", false, dom.SHIFT_MASK );
      assertEquals( 0, log.length );
      TestUtil.press( text, "z", false, dom.ALT_MASK | dom.CTRL_MASK );
      assertEquals( 3, log.length );
      this._disposeTextWidget( text );
    },

    testCancelKeySequenceSent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setCancelKeys( text, [
        "#90", 
        "ALT+#90", 
        "CTRL+#90", 
        "SHIFT+#90" 
      ] );

      var dom = qx.event.type.DomEvent;
      TestUtil.press( text, "z", false, 0 );
      TestUtil.press( text, "z", false, dom.CTRL_MASK );
      TestUtil.press( text, "z", false, dom.ALT_MASK );
      TestUtil.press( text, "z", false, dom.SHIFT_MASK );
      assertEquals( 4, TestUtil.getRequestsSend() );
      this._disposeTextWidget( text );
    },

    _createWidget : function() {
      var result = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      result.addToDocument();
      result.setLocation( 0, 0 );
      result.setDimension( 100, 100 );
      qx.ui.core.Widget.flushGlobalQueues();
      return result;
    },
    
    _createTextWidget : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Text",
        "properties" : {
          "style" : [ "SINGLE", "RIGHT" ],
          "parent" : "w2"
        }
      } );
      processor.processOperation( {
        "target" : "w3",
        "action" : "listen",
        "properties" : {
          "key" : true
        }
      } );
      var text = org.eclipse.rwt.protocol.ObjectManager.getObject( "w3" );
      TestUtil.flush();
      text.focus();
      TestUtil.initRequestLog();
      return text;
    },
    
    _disposeTextWidget : function( text ) {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.clearRequestLog();
      text.blur(); // otherwise TextUtil will crash on next server-request. 
      text.destroy();
      TestUtil.flush();
    },
    
    _setActiveKeys : function( widget, activeKeys ) {
      var id = org.eclipse.rwt.protocol.ObjectManager.getId( widget );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : id,
        "action" : "set",
        "properties" : {
          "activeKeys" : activeKeys
        }
      } );
    },

    _setCancelKeys : function( widget, cancelKeys ) {
      var id = org.eclipse.rwt.protocol.ObjectManager.getId( widget );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : id,
        "action" : "set",
        "properties" : {
          "cancelKeys" : cancelKeys
        }
      } );
    }

  }
  
} );