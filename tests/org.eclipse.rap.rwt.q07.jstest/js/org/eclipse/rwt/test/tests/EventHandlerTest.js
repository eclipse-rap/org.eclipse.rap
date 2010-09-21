/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.EventHandlerTest", {
  extend : qx.core.Object,
  
  members : {        
    
    testOverOutEventsOrder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      testUtil.hoverFromTo( document.body, targetNode );
      testUtil.hoverFromTo( targetNode, node1 );
      testUtil.hoverFromTo( node1, node2 );
      testUtil.hoverFromTo( node2, targetNode );
      testUtil.hoverFromTo( targetNode, document.body );
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
    	var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      testUtil.hoverFromTo( document.body, targetNode );
      testUtil.hoverFromTo( targetNode, node1 );
      testUtil.hoverFromTo( node1, node2 );
      testUtil.hoverFromTo( node2, targetNode );
      testUtil.hoverFromTo( targetNode, document.body );
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
    	var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      testUtil.hoverFromTo( document.body, targetNode );
      testUtil.hoverFromTo( targetNode, node1 );
      testUtil.hoverFromTo( node1, node2 );
      testUtil.hoverFromTo( node2, targetNode );
      testUtil.hoverFromTo( targetNode, document.body );
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
    
    testClickFix : qx.core.Variant.select( "qx.client", {
      "gecko" : function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
        var left = qx.event.type.MouseEvent.buttons.left;
        testUtil.fakeMouseEventDOM( node1, "mousedown", left );
        testUtil.fakeMouseEventDOM( node2, "mouseup", left );
        var expected = [ "mousedown", "mouseup", "click" ];
        assertEquals( expected, log );
      },
      "default" : null
    } ),
    
    testDoubleClickWithRightMouseButton : qx.core.Variant.select( "qx.client", {
      "default" : function() {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this.createDefaultWidget();
        var node = widget._getTargetNode();
        var log = [];
        var handler = function( event ) {
          log.push( event.getType() );
        };
        widget.addEventListener( "dblclick", handler );
        var right = qx.event.type.MouseEvent.buttons.right;
        testUtil.fakeMouseEventDOM( node, "mousedown", right );
        testUtil.fakeMouseEventDOM( node, "mouseup", right );
        testUtil.fakeMouseEventDOM( node, "click", right );
        testUtil.fakeMouseEventDOM( node, "mousedown", right );
        testUtil.fakeMouseEventDOM( node, "mouseup", right );
        testUtil.fakeMouseEventDOM( node, "click", right );
        testUtil.fakeMouseEventDOM( node, "dblclick", right );
        var expected = [];
        assertEquals( expected, log );
      },
      "mshtml" : null
    } ),
    
    testKeyDownCharCode : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      widget.focus();
      var log = [];
      widget.addEventListener( "keypress", function( event ) {
        log.push( event.getCharCode() );
        log.push( event.getKeyIdentifier() );
      } );
      testUtil.keyDown( widget._getTargetNode(), "x" );
      var expected = [ 120, "X" ];
      assertEquals( expected, log );
      widget.destroy();
    },    
    
    testKeyDownPrintable : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      testUtil.keyDown( widget._getTargetNode(), "x" );
      var expected = [ "keydown", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },    
    
    testKeyHoldPrintable : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      testUtil.keyDown( widget._getTargetNode(), "x" );
      testUtil.keyHold( widget._getTargetNode(), "x" );
      var expected = [ "keydown", "keypress", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },    
    
    testKeyUp : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      widget.focus();
      testUtil.keyDown( widget._getTargetNode(), "x" );
      var log = this._addKeyLogger( widget, true, false, false );
      testUtil.keyUp( widget._getTargetNode(), "x" );
      var expected = [ "keyup" ];
      assertEquals( expected, log );
      widget.destroy();
    },    
  
    testKeyDownNonPrintable : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      testUtil.keyDown( widget._getTargetNode(), "Left" );
      var expected = [ "keydown", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },    
    
    testKeyHoldNonPrintable : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      testUtil.keyDown( widget._getTargetNode(), "Left" );
      testUtil.keyHold( widget._getTargetNode(), "Left" );
      var expected = [ "keydown", "keypress", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },    
    
    testKeyDownPrintableSpecialChar : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      testUtil.keyDown( widget._getTargetNode(), "Space" );
      var expected = [ "keydown", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },    
    
    testKeyHoldPrintableSpecialChar : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      testUtil.keyDown( widget._getTargetNode(), "Space" );
      testUtil.keyHold( widget._getTargetNode(), "Space" );
      var expected = [ "keydown", "keypress", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },  

    testKeyDownPrintableSpecialCharNoKeyInput : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      testUtil.keyDown( widget._getTargetNode(), "Enter" );
      var expected = [ "keydown", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },    
    
    testKeyHoldPrintableSpecialCharNoKeyInput : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      testUtil.keyDown( widget._getTargetNode(), "Enter" );
      testUtil.keyHold( widget._getTargetNode(), "Enter" );
      var expected = [ "keydown", "keypress", "keypress" ];
      assertEquals( expected, log );
      widget.destroy();
    },
    
    testKeyDownModifier : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      testUtil.keyDown( widget._getTargetNode(), "Shift" );
      var expected = qx.core.Variant.select( "qx.client", {
        "default" : [ "keydown", "keypress" ],
        "gecko|opera" : [ "keydown" ]
      } );
      assertEquals( expected, log );
      widget.destroy();
    },    
    
    testKeyHoldModifier : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new qx.ui.basic.Terminator();
      widget.addToDocument();
      testUtil.flush();
      widget.focus();
      var log = this._addKeyLogger( widget, true, false, false );
      testUtil.keyDown( widget._getTargetNode(), "Shift" );
      testUtil.keyHold( widget._getTargetNode(), "Shift" );
      var expected = qx.core.Variant.select( "qx.client", {
        "default" : [ "keydown", "keypress", "keypress" ],
        "gecko" : [ "keydown" ],
        "opera" : [ "keydown" ]
      } );
      assertEquals( expected, log );
      widget.destroy();
    },
 
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
      }
      widget.addEventListener( "keydown", logger );
      widget.addEventListener( "keypress", logger );
      widget.addEventListener( "keyup", logger );
      return log;
    },

    /////////
    // Helper

    createDefaultWidget : function() {
      var widget = new org.eclipse.rwt.widgets.MultiCellWidget( 
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