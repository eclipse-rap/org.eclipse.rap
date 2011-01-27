/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
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
      var expected =   "org.eclipse.swt.events.keyDown.charCode=" 
                     + "x".charCodeAt( 0 );
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
    
    /////////
    // Helper
    
    _createTextWidget : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.initRequestLog();
      var keyUtil = org.eclipse.rwt.AsyncKeyEventUtil.getInstance();
      var text = new org.eclipse.rwt.widgets.Text( false );
      text.setUserData( "isControl", true );
      text.setUserData( "keyListener", true );
      org.eclipse.swt.TextUtil.initialize( text );
      text.addToDocument();
      testUtil.flush();
      text.focus();
      return text;
    },
    
    _disposeTextWidget : function( text ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.clearRequestLog();
      text.blur(); // otherwise TextUtil will crash on next server-request. 
      text.destroy();
      testUtil.flush();
    }

  }
  
} );