/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.EventHandlerTest", {
  extend : rwt.qx.Object,

  members : {

    testOverOutEventsOrder : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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

    testCatchMouseEventError : function(){
      var widget = new rwt.widgets.base.Terminator();
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      TestUtil.flush();
      TestUtil.click( shell );

      TestUtil.click( rwt.widgets.base.ClientDocument.getInstance() );

      assertTrue( shell.getFocused() );
      assertFalse( rwt.widgets.base.ClientDocument.getInstance().getFocused() );
      assertEquals( shell, rwt.event.EventHandler.getFocusRoot() );
      shell.destroy();
    },

    testFocusWithCapturedWidget : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      org.eclipse.rwt.test.fixture.TestUtil.flush();
      return widget;
    }

  }
} );
