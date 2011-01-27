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
    
    testKeyBindingSingleCharacter : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
      var widget = this._createWidget();
      keyUtil.setKeyBindings( {
        "66" : true
      } );
      testUtil.press( widget, "a", false, 0 );
      testUtil.press( widget, "b", false, 0 );
      testUtil.press( widget, "c", false, 0 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var expected1 = "org.eclipse.swt.events.keyDown=w1"; 
      var expected2 = "org.eclipse.swt.events.keyDown.keyCode=66";
      var msg = testUtil.getMessage();
      console.log( msg );
      assertTrue( msg.indexOf( expected1 ) != -1 );
      assertTrue( msg.indexOf( expected2 ) != -1 );
      widget.destroy();
    },
    
    testKeyBindingPreventDefaultNotCalled : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
      var prevented = false;
      var preventDefault = function() {
        prevented = true;
      };
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "66" : true } );
      var node = widget._getTargetNode();
      var event = testUtil.createFakeDomKeyEvent( node, "keydown", "a", 0 );
      event.preventDefault = preventDefault;
      testUtil.fireFakeDomEvent( event );
      var event = testUtil.createFakeDomKeyEvent( node, "keypress", "b", 0 );
      event.preventDefault = preventDefault;
      testUtil.fireFakeDomEvent( event );
      var event = testUtil.createFakeDomKeyEvent( node, "keyup", "b", 0 );
      event.preventDefault = preventDefault;
      testUtil.fireFakeDomEvent( event );
      assertFalse( prevented );
      widget.destroy();
    },
    
    testKeyBindingPreventDefaultCalled : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
      var prevented = false;
      var preventDefault = function() {
        prevented = true;
      };
      var widget = this._createWidget();
      keyUtil.setKeyBindings( { "66" : true } );
      var node = widget._getTargetNode();
      var event = testUtil.createFakeDomKeyEvent( node, "keydown", "b", 0 );
      event.preventDefault = preventDefault;
      testUtil.fireFakeDomEvent( event );
      assertTrue( prevented );
      widget.destroy();
    },
    
    testKeyBindingWithServerKeyListener : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
      var widget = this._createWidget();
      widget.setUserData( "isControl", true );
      widget.setUserData( "keyListener", true );
      widget.focus();
      keyUtil.setKeyBindings( { "66" : true } );
      var instance = keyUtil._getDelegate();
      assertEquals( 0, testUtil.getRequestsSend() );
      testUtil.press( widget, "b", false, 0 );
      testUtil.forceTimerOnce();
      assertEquals( 1, testUtil.getRequestsSend() );
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
      keyUtil.setKeyBindings( { "66" : true } );
      var instance = keyUtil._getDelegate();
      assertEquals( 0, testUtil.getRequestsSend() );
      testUtil.press( widget, "a", false, 0 );
      assertEquals( 0, testUtil.getRequestsSend() );
      assertEquals( 3, log.length );
      testUtil.press( widget, "b", false, 0 );
      testUtil.forceTimerOnce();
      assertEquals( 1, testUtil.getRequestsSend() );
      assertEquals( 3, log.length );
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
        "SHIFT+66" : true
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
      keyUtil.setKeyBindings( {
        "ALT+CTRL+SHIFT+66" : true
      } );
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