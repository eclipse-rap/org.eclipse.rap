/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var EventHandler = rwt.event.EventHandler;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.EventHandlerTest", {

  extend : rwt.qx.Object,

  members : {

    testOverOutEventsOrder : function() {
      var widget = this.createDefaultWidget();
      var targetNode = widget._getTargetNode();
      assertEquals( 2, targetNode.childNodes.length );
      var node1 = targetNode.childNodes[ 0 ];
      var node2 = targetNode.childNodes[ 0 ];
      var log = [];
      var handler = function( event ) {
        if( event.isDisposed() ) {
          throw "Error: event has been disposed!";
        }
        log.push( event.getType() );
      };
      widget.addEventListener( "mouseover", handler );
      widget.addEventListener( "mouseout", handler );
      widget.addEventListener( "elementOver", handler );
      widget.addEventListener( "elementOut", handler );
      TestUtil.hoverFromTo( document.body, targetNode );
      TestUtil.hoverFromTo( targetNode, node1 );
      TestUtil.hoverFromTo( node1, node2 );
      TestUtil.hoverFromTo( node2, targetNode );
      TestUtil.hoverFromTo( targetNode, document.body );
      var expected = [
        "elementOver",
        "mouseover",
        "elementOut",
        "elementOver",
        "elementOut",
        "elementOver",
        "elementOut",
        "elementOver",
        "elementOut",
        "mouseout"
      ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testOverOutEventsTarget : function() {
      var widget = this.createDefaultWidget();
      var targetNode = widget._getTargetNode();
      assertEquals( 2, targetNode.childNodes.length );
      var node1 = targetNode.childNodes[ 0 ];
      var node2 = targetNode.childNodes[ 0 ];
      var log = [];
      var handler = function( event ) {
        log.push( event.getDomTarget() );
      };
      widget.addEventListener( "mouseover", handler );
      widget.addEventListener( "mouseout", handler );
      widget.addEventListener( "elementOver", handler );
      widget.addEventListener( "elementOut", handler );
      TestUtil.hoverFromTo( document.body, targetNode );
      TestUtil.hoverFromTo( targetNode, node1 );
      TestUtil.hoverFromTo( node1, node2 );
      TestUtil.hoverFromTo( node2, targetNode );
      TestUtil.hoverFromTo( targetNode, document.body );
      var expected = [
        targetNode,
        targetNode,
        targetNode,
        node1,
        node1,
        node2,
        node2,
        targetNode,
        targetNode,
        targetNode
      ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testOverOutEventsRelatedTarget : function() {
      var widget = this.createDefaultWidget();
      var targetNode = widget._getTargetNode();
      assertEquals( 2, targetNode.childNodes.length );
      var node1 = targetNode.childNodes[ 0 ];
      var node2 = targetNode.childNodes[ 1 ];
      var log = [];
      var handler = function( event ) {
        log.push( event.getDomEvent().relatedTarget );
      };
      widget.addEventListener( "mouseover", handler );
      widget.addEventListener( "mouseout", handler );
      widget.addEventListener( "elementOver", handler );
      widget.addEventListener( "elementOut", handler );
      TestUtil.hoverFromTo( document.body, targetNode );
      TestUtil.hoverFromTo( targetNode, node1 );
      TestUtil.hoverFromTo( node1, node2 );
      TestUtil.hoverFromTo( node2, targetNode );
      TestUtil.hoverFromTo( targetNode, document.body );
      var expected = [
        document.body,
        document.body,
        node1,
        targetNode,
        node2,
        node1,
        targetNode,
        node2,
        document.body,
        document.body
      ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testClickFix : rwt.util.Variant.select( "qx.client", {
      "gecko" : function() {
        var widget = this.createDefaultWidget();
        var targetNode = widget._getTargetNode();
        assertEquals( 2, targetNode.childNodes.length );
        var node1 = targetNode.childNodes[ 0 ];
        var node2 = targetNode.childNodes[ 1 ];
        var log = [];
        var handler = function( event ) {
          log.push( event.getType() );
        };
        widget.addEventListener( "mousedown", handler );
        widget.addEventListener( "mouseup", handler );
        widget.addEventListener( "click", handler );
        var left = rwt.event.MouseEvent.buttons.left;
        TestUtil.fakeMouseEventDOM( node1, "mousedown", left );
        TestUtil.fakeMouseEventDOM( node2, "mouseup", left );
        var expected = [ "mousedown", "mouseup", "click" ];
        assertEquals( expected, log );
      },
      "default" : null
    } ),

    testDoubleClickWithRightMouseButton : rwt.util.Variant.select( "qx.client", {
      "default" : function() {
        var widget = this.createDefaultWidget();
        var node = widget._getTargetNode();
        var log = [];
        var handler = function( event ) {
          log.push( event.getType() );
        };
        widget.addEventListener( "dblclick", handler );
        var right = rwt.event.MouseEvent.buttons.right;
        TestUtil.fakeMouseEventDOM( node, "mousedown", right );
        TestUtil.fakeMouseEventDOM( node, "mouseup", right );
        TestUtil.fakeMouseEventDOM( node, "click", right );
        TestUtil.fakeMouseEventDOM( node, "mousedown", right );
        TestUtil.fakeMouseEventDOM( node, "mouseup", right );
        TestUtil.fakeMouseEventDOM( node, "click", right );
        TestUtil.fakeMouseEventDOM( node, "dblclick", right );
        var expected = [];
        assertEquals( expected, log );
      },
      "mshtml" : null
    } ),

//    testMissingMouseUp : function() {
//      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
//      var widget = this.createDefaultWidget();
//      var targetNode = widget._getTargetNode();
//      var log = [];
//      var handler = function( event ) {
//        log.push( event.getType() );
//      };
//      widget.addEventListener( "mousedown", handler );
//      widget.addEventListener( "mouseup", handler );
//      widget.addEventListener( "mousemove", handler );
//      TestUtil.fakeMouseEventDOM( targetNode, "mousedown" );
//      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", 0 );
//      assertEquals( [ "mousedown", "mouseup", "mousemove" ], log );
//      widget.destroy();
//    },
//
    testKeyDownCharCode : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      var log = [];
      widget.addEventListener( "keypress", function( event ) {
        log.push( event.getCharCode() );
        log.push( event.getKeyIdentifier() );
      } );
      TestUtil.keyDown( widget._getTargetNode(), "x" );
      var expected = [ 120, "X" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testKeyPressEnter : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      var log = [];
      widget.addEventListener( "keypress", function( event ) {
        log.push( event.getKeyCode() );
        log.push( event.getCharCode() );
        log.push( event.getKeyIdentifier() );
      } );
      TestUtil.keyDown( widget._getTargetNode(), "Enter" );
      var expected = [ 13, 0, "Enter" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testKeyPressEscape : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      var log = [];
      widget.addEventListener( "keypress", function( event ) {
        log.push( event.getKeyCode() );
        log.push( event.getCharCode() );
        log.push( event.getKeyIdentifier() );
      } );
      TestUtil.press( widget, "Escape", 0 );
      var expected = [ 27, 0, "Escape" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testKeyDownPrintable : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      TestUtil.keyDown( widget._getTargetNode(), "x" );
      var expected = [ "keydown", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testCancelKeyDownPrintable : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      widget.addEventListener( "keydown", function( event ) {
        event.preventDefault();
      } );
      TestUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      TestUtil.keyDown( widget._getTargetNode(), "x" );
      var expected = [ "keydown", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testKeyHoldPrintable : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      TestUtil.keyDown( widget._getTargetNode(), "x" );
      TestUtil.keyHold( widget._getTargetNode(), "x" );
      var expected = [ "keydown", "keypress", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testKeyUp : function() {
      // See Bug 335753
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      TestUtil.keyDown( widget._getTargetNode(), "x" );
      var log = this._addKeyLogger( widget, true, false, false );
      TestUtil.keyUp( widget._getTargetNode(), "x" );
      var expected = [ "keyup" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testKeyUpNumber : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      TestUtil.keyDown( widget._getTargetNode(), "1" );
      var log = this._addKeyLogger( widget, true, true, false );
      TestUtil.keyUp( widget._getTargetNode(), "1" );
      var expected = [ "keyup", "1" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testKeyDownNonPrintable : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      TestUtil.keyDown( widget._getTargetNode(), "Left" );
      var expected = [ "keydown", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testKeyDownAndHoldApps : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      TestUtil.keyDown( widget._getTargetNode(), "Apps" );
      TestUtil.keyDown( widget._getTargetNode(), "Apps" );
      var expected = [ "keydown", "keypress", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testAppsKeyLoosingKeyUp : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, true, false );
      TestUtil.keyDown( widget._getTargetNode(), "Apps" );
      TestUtil.keyDown( widget._getTargetNode(), "X" );
      TestUtil.keyUp( widget._getTargetNode(), "X" );
      TestUtil.keyDown( widget._getTargetNode(), "Apps" );
      var expected = [
        "keydown", "Apps",
        "keypress", "Apps",
        "keyup", "Apps",
        "keydown", "X",
        "keypress", "X",
        "keyup", "X",
        "keydown", "Apps",
        "keypress", "Apps"
      ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testKeyHoldNonPrintable : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      TestUtil.keyDown( widget._getTargetNode(), "Left" );
      TestUtil.keyHold( widget._getTargetNode(), "Left" );
      var expected = [ "keydown", "keypress", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testKeyDownPrintableSpecialChar : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      TestUtil.keyDown( widget._getTargetNode(), "Space" );
      var expected = [ "keydown", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testKeyHoldPrintableSpecialChar : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      TestUtil.keyDown( widget._getTargetNode(), "Space" );
      TestUtil.keyHold( widget._getTargetNode(), "Space" );
      var expected = [ "keydown", "keypress", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testKeyDownPrintableSpecialCharNoKeyInput : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      TestUtil.keyDown( widget._getTargetNode(), "Enter" );
      var expected = [ "keydown", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testKeyHoldPrintableSpecialCharNoKeyInput : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      TestUtil.keyDown( widget._getTargetNode(), "Enter" );
      TestUtil.keyHold( widget._getTargetNode(), "Enter" );
      var expected = [ "keydown", "keypress", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testPressDownModifier : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      TestUtil.keyDown( widget._getTargetNode(), "Shift" );
      TestUtil.keyUp( widget._getTargetNode(), "Shift" );
      var expected = [ "keydown", "keypress", "keyup" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testKeyHoldModifier : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      TestUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      TestUtil.keyDown( widget._getTargetNode(), "Shift" );
      TestUtil.keyHold( widget._getTargetNode(), "Shift" );
      var expected = [ "keydown", "keypress", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },

    testBlockKeyEvents_NoKeyEventsIssued : function() {
      var widget = this.createDefaultWidget();
      var log = this._addKeyLogger( widget, true, false, false );

      EventHandler.setBlockKeyEvents( true );
      TestUtil.press( widget, "A" );
      TestUtil.press( widget, "Enter" );

      assertEquals( 0, log.length );
      widget.destroy();
    },

    testBlockKeyEvents_KeyDownPreventDefault : function() {
      EventHandler.setBlockKeyEvents( true );

      var event = TestUtil.fireFakeKeyDomEvent( document.body, "keydown", "A", 0 );

      assertTrue( event._prevented );
    },

    testBlockKeyEvents_KeyPressPreventDefault : function() {
      EventHandler.setBlockKeyEvents( true );
      TestUtil.fireFakeKeyDomEvent( document.body, "keydown", "A", 0 );

      var event = TestUtil.fireFakeKeyDomEvent( document.body, "keypress", "A", 0 );

      assertTrue( event._prevented );
    },

    testBlockKeyEvents_KeyUpPreventDefault : function() {
      EventHandler.setBlockKeyEvents( true );
      TestUtil.fireFakeKeyDomEvent( document.body, "keydown", "A", 0 );
      TestUtil.fireFakeKeyDomEvent( document.body, "keypress", "A", 0 );

      var event = TestUtil.fireFakeKeyDomEvent( document.body, "keyup", "A", 0 );

      assertTrue( event._prevented );
    },

    testBlockKeyEvents_DoNotCallPreventDefaultFor : function() {
      var CTRL = rwt.event.DomEvent.CTRL_MASK;
      var ALT = rwt.event.DomEvent.ALT_MASK;
      var SHIFT = rwt.event.DomEvent.SHIFT_MASK;
      var dataPoints = [
        [ "Control", CTRL ],
        [ "Alt", ALT ],
        [ "Shift", SHIFT ],
        [ "Meta", 0 ],
        [ "Win", 0 ],
        [ "F1", 0 ],
        [ "F2", 0 ],
        [ "R", CTRL ], // typical combo for reload
        [ "T", CTRL ], // typical combo for new tub
        [ "N", CTRL ], // typical combo for new window
        [ "M", ALT ],  // typical combo for open a menu
        [ "F", ALT ] // typical combo for open a menu
      ];
      for( var i = 0; i < dataPoints.length; i++ ) {
        this.theoryBlockKeyEvents_DoNotCallPreventDefaultFor.apply( this, dataPoints[ i ] );
      }
    },

    theoryBlockKeyEvents_DoNotCallPreventDefaultFor : function( key, mod ) {
      EventHandler.setBlockKeyEvents( true );
      var widget = this.createDefaultWidget();
      widget.focus();
      var target = widget.getElement();
      var log = this._addKeyLogger( widget, true, false, false );

      var downEvent = TestUtil.fireFakeKeyDomEvent( target, "keydown", key, mod );
      var upEvent = TestUtil.fireFakeKeyDomEvent( target, "keyup", key, mod );

      assertTrue( "do not prevent " + key + "," + mod, !downEvent._prevented );
      assertTrue( "do not prevent " + key + "," + mod, !upEvent._prevented );
      assertEquals( 0, log.length ); // do not process event in any case
      TestUtil.resetEventHandler();
      widget.destroy();
    },

    testBlockKeyEvents_DoCallPreventDefaultFor : function() {
      var CTRL = rwt.event.DomEvent.CTRL_MASK;
      var SHIFT = rwt.event.DomEvent.SHIFT_MASK;
      var dataPoints = [
        [ "Left", 0 ],
        [ "Up", 0 ],
        [ "Right", 0 ],
        [ "Down", 0 ],
        [ "PageUp", 0 ],
        [ "PageDown", 0 ],
        [ "End", 0 ],
        [ "Home", 0 ],
        [ "Insert", 0 ],
        [ "Backspace", 0 ],
        [ "Tab", 0 ],
        [ "Insert", SHIFT ],
        [ "Delete", 0 ],
        [ "Delete", SHIFT ],
        [ "F", CTRL ], // search
        [ "A", CTRL ], // select all
        [ "C", CTRL ], // copy
        [ "V", CTRL ], // paste
        [ "X", CTRL ], // cut
        [ "Z", CTRL ], // undo
        [ "Y", CTRL ] // redo
      ];
      for( var i = 0; i < dataPoints.length; i++ ) {
        this.theoryBlockKeyEvents_DoCallPreventDefaultFor.apply( this, dataPoints[ i ] );
      }
    },

    theoryBlockKeyEvents_DoCallPreventDefaultFor : function( key, mod ) {
      EventHandler.setBlockKeyEvents( true );
      var widget = this.createDefaultWidget();
      widget.focus();
      var target = widget.getElement();
      var log = this._addKeyLogger( widget, true, false, false );

      var downEvent = TestUtil.fireFakeKeyDomEvent( target, "keydown", key, mod );
      var upEvent = TestUtil.fireFakeKeyDomEvent( target, "keyup", key, mod );

      assertTrue( "do block " + key + "," + mod, downEvent._prevented );
      assertTrue( "do block " + key + "," + mod, upEvent._prevented );
      assertEquals( 0, log.length ); // do not process event in any case
      TestUtil.resetEventHandler();
    },

    testCatchMouseEventError : function(){
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      widget.addEventListener( "click", function() {
        var foo = null;
        foo.bar();
      } );
      TestUtil.flush();
      TestUtil.initErrorPageLog();
      try {
        TestUtil.click( widget );
        fail();
      } catch( ex ) {
        // expected
      }
      assertNotNull( TestUtil.getErrorPage() );
      widget.destroy();
    },

    testCatchKeyEventError : function() {
      var widget = new rwt.widgets.base.Terminator();
      widget.addToDocument();
      widget.addEventListener( "keydown", function() {
        var foo = null;
        foo.bar();
      } );
      TestUtil.flush();
      TestUtil.initErrorPageLog();
      try{
        TestUtil.press( widget, "a" );
        fail();
      } catch( ex ) {
        // expected
      }
      assertNotNull( TestUtil.getErrorPage() );
      widget.destroy();
    },

    testFocusWithoutCapturedWidget : function() {
      var widget1 = new rwt.widgets.base.Terminator();
      widget1.setFocused( true );
      widget1.setTabIndex( 1 );
      var widget2 = new rwt.widgets.base.Terminator();
      widget2.setTabIndex( 1 );
      widget1.addToDocument();
      widget2.addToDocument();
      TestUtil.flush();
      var node = widget2._getTargetNode();
      var right = rwt.event.MouseEvent.buttons.right;
      TestUtil.fakeMouseEventDOM( node, "mousedown", right );
      assertTrue( widget2.getFocused() );
      widget1.destroy();
      widget2.destroy();
    },

    // See bug 368940
    testDontFocusClientDocumentOnMouseDown : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      TestUtil.flush();
      TestUtil.click( shell );

      TestUtil.click( rwt.widgets.base.ClientDocument.getInstance() );

      assertTrue( shell.getFocused() );
      assertFalse( rwt.widgets.base.ClientDocument.getInstance().getFocused() );
      assertEquals( shell, EventHandler.getFocusRoot() );
      shell.destroy();
    },

    testFocusWithCapturedWidget : function() {
      var widget1 = new rwt.widgets.base.Terminator();
      widget1.setFocused( true );
      widget1.setTabIndex( 1 );
      widget1.setCapture( true );
      var widget2 = new rwt.widgets.base.Terminator();
      widget2.setTabIndex( 1 );
      widget1.addToDocument();
      widget2.addToDocument();
      TestUtil.flush();
      var node = widget2._getTargetNode();
      var right = rwt.event.MouseEvent.buttons.right;
      TestUtil.fakeMouseEventDOM( node, "mousedown", right );
      assertFalse( widget2.getFocused() );
      widget1.destroy();
      widget2.destroy();
    },

    testNativeMenuConsumesEvent : function(){
      var text = new rwt.widgets.Text( false );
      text.addToDocument();
      text.setSpace( 0, 50, 0, 21 );
      TestUtil.flush();
      text.focus();
      var log = [];
      text.addEventListener( "contextmenu", function( event ) {
        log.push( event );
      } );
      var right = rwt.event.MouseEvent.buttons.right;
      TestUtil.fakeMouseEventDOM( text.getElement(), "contextmenu", right );
      assertEquals( 1, log.length );

      TestUtil.fakeMouseEventDOM( text._inputElement, "contextmenu", right );
      assertEquals( 1, log.length );

      text.destroy();
    },

    testStoreMouseEvent : function(){
      var text = new rwt.widgets.Text( false );
      text.addToDocument();
      text.setSpace( 0, 50, 0, 21 );
      TestUtil.flush();
      text.focus();
      var log = [];
      var left = rwt.event.MouseEvent.buttons.left;
      var right = rwt.event.MouseEvent.buttons.right;

      TestUtil.fakeMouseEventDOM( text.getElement(), "mousemove", left, 10, 20 );
      //TestUtil.fakeMouseEventDOM( text._inputElement, "contextmenu", right );

      assertEquals( 10, rwt.event.MouseEvent.getPageX() );
      assertEquals( 20, rwt.event.MouseEvent.getPageY() );
      text.destroy();
    },

    testStoreMouseEventNotContextMenu : function(){
      var text = new rwt.widgets.Text( false );
      text.addToDocument();
      text.setSpace( 0, 50, 0, 21 );
      TestUtil.flush();
      text.focus();
      var log = [];
      var left = rwt.event.MouseEvent.buttons.left;
      var right = rwt.event.MouseEvent.buttons.right;

      TestUtil.fakeMouseEventDOM( text.getElement(), "mousemove", left, 10, 20 );
      TestUtil.fakeMouseEventDOM( text._inputElement, "contextmenu", right, 0, 0 );

      assertEquals( 10, rwt.event.MouseEvent.getPageX() );
      assertEquals( 20, rwt.event.MouseEvent.getPageY() );
      text.destroy();
    },

    /////////
    // Helper

    _addKeyLogger : function( widget, type, identifier, modifier ) {
      var log = [];
      var logger = function( event ) {
        if( type ) {
          log.push( event.getType() );
        }
        if( identifier ) {
          log.push( event.getKeyIdentifier() );
        }
        if( modifier ) {
          log.push( event.getModifiers() );
        }
      };
      widget.addEventListener( "keydown", logger );
      widget.addEventListener( "keypress", logger );
      widget.addEventListener( "keyup", logger );
      return log;
    },

    createDefaultWidget : function() {
      var widget = new rwt.widgets.base.MultiCellWidget(
        [ "label", "label"]
      );
      widget.setCellContent( 0, "test0" );
      widget.setCellContent( 1, "test1" );
      widget.addToDocument();
      TestUtil.flush();
      return widget;
    }

  }
} );

}());
