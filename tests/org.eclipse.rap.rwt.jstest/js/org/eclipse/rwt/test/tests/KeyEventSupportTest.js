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
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.initRequestLog();
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "#66" : true } );
      testUtil.press( widget, "a", false, 0 );
      testUtil.press( widget, "b", false, 0 );
      testUtil.press( widget, "c", false, 0 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var expected1 = "org.eclipse.swt.events.keyDown=w1"; 
      var expected2 = "org.eclipse.swt.events.keyDown.keyCode=66";
      var expected2 = "org.eclipse.swt.events.keyDown.charCode=98";
      var msg = testUtil.getMessage();
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      widget.destroy();
    },

    testKeyBindingSingleCharacterHold : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.initRequestLog();
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "#66" : true } );
      testUtil.keyDown( widget.getElement(), "b", 0 );
      testUtil.keyHold( widget.getElement(), "b", 0 );
      testUtil.keyUp( widget.getElement(), "b", 0 );
      assertEquals( 2, testUtil.getRequestsSend() );
      var expected1 = "org.eclipse.swt.events.keyDown=w1"; 
      var expected2 = "org.eclipse.swt.events.keyDown.keyCode=66";
      var expected2 = "org.eclipse.swt.events.keyDown.charCode=98";
      var msg = testUtil.getRequestLog()[ 0 ];
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      msg = testUtil.getRequestLog()[ 1 ];
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      widget.destroy();
    },

//    testSendCanceledKeysOnKeydownOnly : function() {
//      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
//      testUtil.initRequestLog();
//      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
//      var widget = this._createWidget();
//      keyUtil.setKeyBindings( { "#66" : true } );
//      keyUtil.setCancelKeys( { "#66" : true } );
//      testUtil.keyDown( widget.getElement(), "b", 0 );
//      assertEquals( 1, testUtil.getRequestsSend() );
//      testUtil.keyHold( widget.getElement(), "b", 0 );
//      testUtil.keyHold( widget.getElement(), "b", 0 );
//      testUtil.keyUp( widget.getElement(), "b", 0 );
//      assertEquals( 1, testUtil.getRequestsSend() );
//      var expected1 = "org.eclipse.swt.events.keyDown=w1"; 
//      var expected2 = "org.eclipse.swt.events.keyDown.keyCode=66";
//      var expected2 = "org.eclipse.swt.events.keyDown.charCode=98";
//      var msg = testUtil.getMessage();
//      assertTrue( msg.indexOf( expected1 ) != -1 );
//      assertTrue( msg.indexOf( expected2 ) != -1 );
//      // exp3 would not work right now
//      widget.destroy();
//    },

    testKeyBindingSpecialCharacter : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.initRequestLog();
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "/" : true } );
      testUtil.press( widget, "/", false, 0 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var expected1 = "org.eclipse.swt.events.keyDown=w1"; 
      var expected2 = "org.eclipse.swt.events.keyDown.charCode=47"; 
      var msg = testUtil.getMessage();
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      widget.destroy();
    },

    testKeyBindingPointCharacter : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.initRequestLog();
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "." : true } );
      testUtil.press( widget, ".", false, 0 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var expected1 = "org.eclipse.swt.events.keyDown=w1"; 
      var expected2 = "org.eclipse.swt.events.keyDown.charCode=46"; 
      var msg = testUtil.getMessage();
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      widget.destroy();
    },

    testKeyBindingsPreventDefaultNeverCalled : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var prevented = false;
      var preventDefault = function() {
        prevented = true;
      };
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "#66" : true } );
      var node = widget._getTargetNode();
      var event = testUtil.createFakeDomKeyEvent( node, "keydown", "b", 0 );
      event.preventDefault = preventDefault;
      testUtil.fireFakeDomEvent( event );
      assertFalse( prevented );
      widget.destroy();
    },

    testKeyBindingWithServerKeyListener : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      widget.setUserData( "isControl", true );
      widget.setUserData( "keyListener", true );
      widget.focus();
      keyUtil.setKeyBindings( { "#66" : true } );
      assertEquals( 0, testUtil.getRequestsSend() );
      testUtil.press( widget, "b", false, 0 );
      testUtil.forceTimerOnce();
      assertEquals( 1, testUtil.getRequestsSend() );
      assertTrue( keyUtil._keyEventRequestRunning !== true );
      widget.destroy();
    },

    testKeyBindingWithClientKeyListener : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      assertEquals( 0, testUtil.getRequestsSend() );
      testUtil.press( widget, "a", false, 0 );
      assertEquals( 0, testUtil.getRequestsSend() );
      assertEquals( 3, log.length );
      testUtil.press( widget, "b", false, 0 );
      testUtil.forceTimerOnce();
      assertEquals( 1, testUtil.getRequestsSend() );
      assertEquals( 6, log.length );
      widget.destroy();
    },
    
    testKeyBindingsNoModifiersAllowed : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( {
        "66" : true
      } );
      var dom = qx.event.type.DomEvent;
      testUtil.press( widget, "b", false, dom.CTRL_MASK );
      testUtil.press( widget, "b", false, dom.ALT_MASK );
      testUtil.press( widget, "b", false, dom.SHIFT_MASK );
      var all = dom.CTRL_MASK | dom.SHIFT_MASK | dom.ALT_MASK;
      testUtil.press( widget, "b", false, all );
      assertEquals( 0, testUtil.getRequestsSend() );
      widget.destroy();    
    },

    testKeyBindingsModifiersNeeded : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( {
        "SHIFT+#66" : true
      } );
      var dom = qx.event.type.DomEvent;
      testUtil.press( widget, "b", false, 0 );
      testUtil.press( widget, "b", false, dom.ALT_MASK );
      assertEquals( 0, testUtil.getRequestsSend() );
      testUtil.press( widget, "b", false, dom.SHIFT_MASK );
      assertEquals( 1, testUtil.getRequestsSend() );
      widget.destroy();    
    },

    testKeyBindingsModifiersOrder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "ALT+CTRL+SHIFT+#66" : true } );
      var dom = qx.event.type.DomEvent;
      testUtil.press( widget, "b", false, 0 );
      testUtil.press( widget, "b", false, dom.CTRL_MASK );
      testUtil.press( widget, "b", false, dom.ALT_MASK );
      testUtil.press( widget, "b", false, dom.SHIFT_MASK );
      assertEquals( 0, testUtil.getRequestsSend() );
      var all = dom.CTRL_MASK | dom.SHIFT_MASK | dom.ALT_MASK;
      testUtil.press( widget, "b", false, all );
      assertEquals( 1, testUtil.getRequestsSend() );
      widget.destroy();          
    },

    testCancelKeysPreventDefaultNotCalled : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var prevented = false;
      var preventDefault = function() {
        prevented = true;
      };
      var widget = this._createWidget();
      keyUtil.setCancelKeys( { "#66" : true } );
      var node = widget._getTargetNode();
      var event = testUtil.createFakeDomKeyEvent( node, "keydown", "a", 0 );
      event.preventDefault = preventDefault;
      testUtil.fireFakeDomEvent( event );
      assertFalse( prevented );
      widget.destroy();
    },

    testCancelKeysPreventDefaultCalled : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var preventedDown = false;
      var preventedPress = false;
      var widget = this._createWidget();
      keyUtil.setCancelKeys( { "#66" : true } );
      var node = widget._getTargetNode();
      var eventDown = testUtil.createFakeDomKeyEvent( node, "keydown", "b", 0 );
      eventDown.preventDefault = function() {
        preventedDown = true;
      };
      var eventPress = testUtil.createFakeDomKeyEvent( node, "keypress", "b", 0 );
      eventPress.preventDefault = function() {
        preventedPress = true;
      };
      testUtil.fireFakeDomEvent( eventDown );
      testUtil.fireFakeDomEvent( eventPress );
      if(    org.eclipse.rwt.Client.isMshtml() 
          || org.eclipse.rwt.Client.isNewMshtml() 
          || org.eclipse.rwt.Client.isWebkit() 
      ) {
        assertFalse( preventedDown ); // would prevent further keypress events otherwise
      } else {
        assertTrue( preventedDown );
      }
      assertTrue( preventedPress );
      widget.destroy();
    },

    testCancelKeysNotProcessed : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var widget = this._createWidget();
      keyUtil.setCancelKeys( { "#66" : true } );
      var listener = function() {
        fail();
      };
      widget.addEventListener( "keydown", listener );
      widget.addEventListener( "keypress", listener );
      widget.addEventListener( "keyup", listener );
      testUtil.press( widget, "b" );
      testUtil.cleanUpKeyUtil();
      widget.destroy();
    },

    testBufferEvents : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var req = org.eclipse.swt.Request.getInstance();
      req.removeEventListener( "received", keyUtil._onRequestReceived, keyUtil );
      testUtil.initRequestLog();
      var text = this._createTextWidget();
      var node = text._inputElement;
      testUtil.press( text, "x", false, 0 );
      assertEquals( 0, keyUtil._bufferedEvents.length );
      testUtil.press( text, "y", false, 0 );
      assertEquals( 1, keyUtil._bufferedEvents.length );
      testUtil.press( text, "z", false, 0 );
      assertEquals( 2, keyUtil._bufferedEvents.length );
      assertEquals( 1, testUtil.getRequestsSend() );
      keyUtil._onRequestReceived();
      assertEquals( 2, testUtil.getRequestsSend() );
      assertEquals( 1, keyUtil._bufferedEvents.length );
      keyUtil._onRequestReceived();
      assertEquals( 3, testUtil.getRequestsSend() );
      assertEquals( 0, keyUtil._bufferedEvents.length );
      keyUtil._pendingEventInfo = null;
      req.addEventListener( "received", keyUtil._onRequestReceived, keyUtil );
      this._disposeTextWidget( text );
    },

    testBufferedEventInfo : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var req = org.eclipse.swt.Request.getInstance();
      req.removeEventListener( "received", keyUtil._onRequestReceived, keyUtil );
      testUtil.initRequestLog();
      var text = this._createTextWidget();
      testUtil.press( text, "x", false, 0 );
      testUtil.press( text, "y", false, 0 );
      assertEquals( 1, keyUtil._bufferedEvents.length );

      keyUtil._onRequestReceived();
      var msg = testUtil.getRequestLog()[ 1 ];
      
      assertEquals( 2, testUtil.getRequestsSend() );
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
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      var node = text._inputElement;
      // cancel event
      assertNull( keyUtil._pendingEventInfo );
      assertEquals( 0, testUtil.getRequestsSend() );
      testUtil.fireFakeKeyDomEvent( node, "keydown", 37 );
      assertNull( keyUtil._pendingEventInfo );
      this._disposeTextWidget( text );
    },

    testKeyCodeLowerCase : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      testUtil.press( text, "a", 0 );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=97";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=65";
      assertTrue( testUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( testUtil.getMessage().indexOf( expectedKey ) != -1 );
      this._disposeTextWidget( text );
    },

    testKeyCodeUpperCase : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      testUtil.press( text, "A", 0 );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=65";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=65";
      assertTrue( testUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( testUtil.getMessage().indexOf( expectedKey ) != -1 );
      this._disposeTextWidget( text );
    },
    
    testSendModifier: function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      var shift = qx.event.type.DomEvent.SHIFT_MASK;
      testUtil.keyDown( text.getElement(), "Shift", shift );
      testUtil.keyHold( text.getElement(), "Shift", shift );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=0";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=16";
      assertEquals( 1, testUtil.getRequestsSend() );
      assertTrue( testUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( testUtil.getMessage().indexOf( expectedKey ) != -1 );
      this._disposeTextWidget( text );
    },

    testSetActiveKeysByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "a", "b" ] );
      var expected = { "a" : true, "b" : true }
      assertEquals( expected, text.getUserData( "activeKeys" ) );
      this._disposeTextWidget( text );
    },

    testNonActiveCharKeysNotSent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [] );
      testUtil.press( text, "a", 0 );
      testUtil.press( text, "A", 0 );
      testUtil.press( text, "z", 0 );
      testUtil.press( text, "Z", 0 );
      assertEquals( 0, testUtil.getRequestsSend() );
      this._disposeTextWidget( text );
    },

    testActiveLowerCharKeySent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "#90" ] );
      testUtil.press( text, "a", 0 );
      testUtil.press( text, "z", 0 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=122";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=90";
      assertTrue( testUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( testUtil.getMessage().indexOf( expectedKey ) != -1 );
      this._disposeTextWidget( text );
    },

    testActiveUpperCharKeySent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "#90" ] );
      testUtil.press( text, "Z", 0 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=90";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=90";
      assertTrue( testUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( testUtil.getMessage().indexOf( expectedKey ) != -1 );
      this._disposeTextWidget( text );
    },

    testActiveSpecialKeySent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "#40" ] );
      testUtil.press( text, "Up", 0 );
      testUtil.press( text, "Down", 0 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=0";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=40";
      assertTrue( testUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( testUtil.getMessage().indexOf( expectedKey ) != -1 );
      this._disposeTextWidget( text );
    },

    testActiveSpecialCharKeySent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "/" ] );
      testUtil.press( text, "\\", 0 );
      testUtil.press( text, "/", 0 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=47";
      assertTrue( testUtil.getMessage().indexOf( expectedChar ) != -1 );
      this._disposeTextWidget( text );
    },

    testActiveKeySequenceSent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "ALT+CTRL+#90" ] );
      var dom = qx.event.type.DomEvent;
      testUtil.press( text, "z", false, 0 );
      testUtil.press( text, "z", false, dom.CTRL_MASK );
      testUtil.press( text, "z", false, dom.ALT_MASK );
      testUtil.press( text, "z", false, dom.SHIFT_MASK );
      assertEquals( 0, testUtil.getRequestsSend() );
      testUtil.press( text, "z", false, dom.ALT_MASK | dom.CTRL_MASK);
      assertEquals( 1, testUtil.getRequestsSend() );
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=90";
      assertTrue( testUtil.getMessage().indexOf( expectedKey ) != -1 );
      this._disposeTextWidget( text );
    },

    testCancelKeySequenceNotProcessed : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      testUtil.press( text, "z", false, 0 );
      testUtil.press( text, "z", false, dom.CTRL_MASK );
      testUtil.press( text, "z", false, dom.ALT_MASK );
      testUtil.press( text, "z", false, dom.SHIFT_MASK );
      assertEquals( 0, log.length );
      testUtil.press( text, "z", false, dom.ALT_MASK | dom.CTRL_MASK );
      assertEquals( 3, log.length );
      this._disposeTextWidget( text );
    },

    testCancelKeySequenceSent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var text = this._createTextWidget();
      this._setCancelKeys( text, [
        "#90", 
        "ALT+#90", 
        "CTRL+#90", 
        "SHIFT+#90" 
      ] );

      var dom = qx.event.type.DomEvent;
      testUtil.press( text, "z", false, 0 );
      testUtil.press( text, "z", false, dom.CTRL_MASK );
      testUtil.press( text, "z", false, dom.ALT_MASK );
      testUtil.press( text, "z", false, dom.SHIFT_MASK );
      assertEquals( 4, testUtil.getRequestsSend() );
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
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventSupport.getInstance();
      var shell = testUtil.createShellByProtocol( "w2" );
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
      testUtil.flush();
      text.focus();
      testUtil.initRequestLog();
      return text;
    },
    
    _disposeTextWidget : function( text ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.clearRequestLog();
      text.blur(); // otherwise TextUtil will crash on next server-request. 
      text.destroy();
      testUtil.flush();
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