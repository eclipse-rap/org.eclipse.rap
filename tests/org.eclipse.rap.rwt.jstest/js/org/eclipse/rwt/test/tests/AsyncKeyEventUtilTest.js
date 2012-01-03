/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.AsyncKeyEventUtilTest", {

  extend : qx.core.Object,
  
  construct : function() {
    this.base( arguments );
    org.eclipse.rwt.test.fixture.TestUtil.prepareTimerUse();
  },
  
  members : {
    
    TARGETENGINE : [ "gecko" ],
    
    testGetInstance : function() {
      var instance = org.eclipse.rwt.KeyEventUtil.getInstance()._getDelegate();
      assertTrue( instance instanceof org.eclipse.rwt.AsyncKeyEventUtil );
    },

    testCancelEvent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.initRequestLog();
      var keyUtil = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
      var text = this._createTextWidget();
      var node = text._inputElement;
      assertEquals( "", text.getComputedValue() );
      assertNull( keyUtil._pendingEventInfo );
      assertEquals( 0, testUtil.getRequestsSend() );
      // cancel event
      testUtil.fireFakeKeyDomEvent( node, "keypress", "x" );
      var expected = "org.eclipse.swt.events.keyDown.charCode=" + "x".charCodeAt( 0 );
      assertTrue( testUtil.getMessage().indexOf( expected ) != -1 );
      assertNotNull( keyUtil._pendingEventInfo );
      assertEquals( "", text.getComputedValue() );
      keyUtil.cancelEvent();
      assertNull( keyUtil._pendingEventInfo );
      assertEquals( "", text.getComputedValue() );
      this._disposeTextWidget( text );
    },
    
    testAllowEvent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
      var text = this._createTextWidget();
      var node = text._inputElement;
      assertEquals( "", text.getComputedValue() );
      assertNull( keyUtil._pendingEventInfo );
      assertEquals( 0, testUtil.getRequestsSend() );
      // cancel event
      testUtil.fireFakeKeyDomEvent( node, "keypress", "x" );
      var expected =   "org.eclipse.swt.events.keyDown.charCode=" 
                     + "x".charCodeAt( 0 );
      assertTrue( testUtil.getMessage().indexOf( expected ) != -1 );
      assertNotNull( keyUtil._pendingEventInfo );
      assertEquals( "", text.getComputedValue() );
      keyUtil.allowEvent();
      keyUtil._pendingEventInfo = null;
      assertEquals( "x", text.getComputedValue() );
      this._disposeTextWidget( text );
    },
    
    testBufferEvent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.initRequestLog();
      var keyUtil = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
      var req = org.eclipse.swt.Request.getInstance();
      req.removeEventListener( "received", 
                               keyUtil._onRequestReceived, keyUtil );      
      var text = this._createTextWidget();
      var node = text._inputElement;
      assertEquals( "", text.getComputedValue() );
      assertNull( keyUtil._pendingEventInfo );
      testUtil.fireFakeKeyDomEvent( node, "keypress", "x" );
      assertEquals( 0, keyUtil._bufferedEvents.length );
      assertNotNull( keyUtil._pendingEventInfo );
      var pending = keyUtil._pendingEventInfo;
      testUtil.fireFakeKeyDomEvent( node, "keypress", "y" );
      assertEquals( 1, keyUtil._bufferedEvents.length );
      assertIdentical( pending, keyUtil._pendingEventInfo );
      testUtil.fireFakeKeyDomEvent( node, "keypress", "z" );
      assertIdentical( pending, keyUtil._pendingEventInfo );
      assertEquals( "", text.getComputedValue() );
      assertEquals( 2, keyUtil._bufferedEvents.length );
      assertEquals( 1, testUtil.getRequestsSend() );
      var nextPending = keyUtil._bufferedEvents[ 0 ];
      keyUtil.cancelEvent();
      keyUtil._onRequestReceived();
      // Note : The redispatch can't be tested, the events can't be intercepted
      assertEquals( "yz", text.getComputedValue() );
      assertEquals( 0, keyUtil._bufferedEvents.length );
      keyUtil._pendingEventInfo = null;
      req.addEventListener( "received", 
                             keyUtil._onRequestReceived, keyUtil );
      this._disposeTextWidget( text );
    },

    testKeyEventListenerUntrustedKey : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
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
      var keyUtil = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
      var text = this._createTextWidget();
      testUtil.press( text, "a", 0 );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=97";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=65";
      assertTrue( testUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( testUtil.getMessage().indexOf( expectedKey ) != -1 );
      keyUtil.allowEvent();
      this._disposeTextWidget( text );
    },

    testKeyCodeUpperCase : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
      var text = this._createTextWidget();
      testUtil.press( text, "A", 0 );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=65";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=65";
      assertTrue( testUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( testUtil.getMessage().indexOf( expectedKey ) != -1 );
      keyUtil.allowEvent();
      this._disposeTextWidget( text );
    },
    
    testSendModifier: function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
      var text = this._createTextWidget();
      var shift = qx.event.type.DomEvent.SHIFT_MASK;
      testUtil.keyDown( text.getElement(), "Shift", shift );
      keyUtil.allowEvent();
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
      var keyUtil = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "a", "b" ] );
      var expected = {
        "a" : true,
        "b" : true
      }
      assertEquals( expected, text.getUserData( "activeKeys" ) );
      this._disposeTextWidget( text );
    },

    testNonActiveCharKeysNotSent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [] );
      testUtil.press( text, "a", 0 );
      testUtil.press( text, "A", 0 );
      testUtil.press( text, "z", 0 );
      testUtil.press( text, "Z", 0 );
      assertEquals( 0, testUtil.getRequestsSend() );
      keyUtil.allowEvent();
      this._disposeTextWidget( text );
    },

    testActiveLowerCharKeySent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "#90" ] );
      testUtil.press( text, "a", 0 );
      testUtil.press( text, "z", 0 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=122";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=90";
      assertTrue( testUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( testUtil.getMessage().indexOf( expectedKey ) != -1 );
      keyUtil.allowEvent();
      this._disposeTextWidget( text );
    },

    testActiveUpperCharKeySent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "#90" ] );
      testUtil.press( text, "Z", 0 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=90";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=90";
      assertTrue( testUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( testUtil.getMessage().indexOf( expectedKey ) != -1 );
      keyUtil.allowEvent();
      this._disposeTextWidget( text );
    },

    testActiveSpecialKeySent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "#40" ] );
      testUtil.press( text, "Up", 0 );
      testUtil.press( text, "Down", 0 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=0";
      var expectedKey = "org.eclipse.swt.events.keyDown.keyCode=40";
      assertTrue( testUtil.getMessage().indexOf( expectedChar ) != -1 );
      assertTrue( testUtil.getMessage().indexOf( expectedKey ) != -1 );
      keyUtil.allowEvent();
      this._disposeTextWidget( text );
    },

    testActiveSpecialCharKeySent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
      var text = this._createTextWidget();
      this._setActiveKeys( text, [ "/" ] );
      testUtil.press( text, "\\", 0 );
      testUtil.press( text, "/", 0 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var expectedChar = "org.eclipse.swt.events.keyDown.charCode=47";
      assertTrue( testUtil.getMessage().indexOf( expectedChar ) != -1 );
      keyUtil.allowEvent();
      this._disposeTextWidget( text );
    },

    testActiveKeySequenceSent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
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
      keyUtil.allowEvent();
      this._disposeTextWidget( text );
    },

    /////////
    // Helper

    _createTextWidget : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
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
    }

  }
  
} );