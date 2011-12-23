/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.KeyEventUtilTest", {

  extend : qx.core.Object,
  
  construct : function() {
    this.base( arguments );
    org.eclipse.rwt.test.fixture.TestUtil.prepareTimerUse();
  },
  
  members : {
    
    testSetActiveKeysByProtocol : function() {
      var processor = org.eclipse.rwt.protocol.Processor;
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
      
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
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
      
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
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "#66" : true } );
      testUtil.press( widget, "a", false, 0 );
      testUtil.press( widget, "b", false, 0 );
      testUtil.press( widget, "c", false, 0 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var expected1 = "org.eclipse.swt.events.keyDown=w1"; 
      var expected2 = "org.eclipse.swt.events.keyDown.keyCode=66";
      var msg = testUtil.getMessage();
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      widget.destroy();
    },

    testKeyBindingSpecialCharacter : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.initRequestLog();
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
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
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
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
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
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
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
      var widget = this._createWidget();
      widget.setUserData( "isControl", true );
      widget.setUserData( "keyListener", true );
      widget.focus();
      keyUtil.setKeyBindings( { "#66" : true } );
      var instance = keyUtil._getDelegate();
      assertEquals( 0, testUtil.getRequestsSend() );
      testUtil.press( widget, "b", false, 0 );
      testUtil.forceTimerOnce();
      //assertEquals( 1, testUtil.getRequestsSend() );
      assertEquals( 2, testUtil.getRequestsSend() ); // TODO [tb] : should only be one
      assertTrue( instance._keyEventRequestRunning !== true );
      widget.destroy();
    },

    testKeyBindingWithClientKeyListener : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
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
      var instance = keyUtil._getDelegate();
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
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
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
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
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
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
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
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
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
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
      var prevented = false;
      var preventDefault = function() {
        prevented = true;
      };
      var widget = this._createWidget();
      keyUtil.setCancelKeys( { "#66" : true } );
      var node = widget._getTargetNode();
      var event = testUtil.createFakeDomKeyEvent( node, "keydown", "b", 0 );
      event.preventDefault = preventDefault;
      testUtil.fireFakeDomEvent( event );
      assertTrue( prevented );
      widget.destroy();
    },

    testCancelKeysNotProcessed : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
      var widget = this._createWidget();
      keyUtil.setCancelKeys( { "#66" : true } );
      var listener = function() {
        fail();
      };
      widget.addEventListener( "keydown", listener );
      widget.addEventListener( "keypress", listener );
      widget.addEventListener( "keyup", listener );
      testUtil.press( widget, "b" );
      widget.destroy();
    },

    _createWidget : function() {
      var result = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      result.addToDocument();
      result.setLocation( 0, 0 );
      result.setDimension( 100, 100 );
      qx.ui.core.Widget.flushGlobalQueues();
      return result;
    }

  }
  
} );