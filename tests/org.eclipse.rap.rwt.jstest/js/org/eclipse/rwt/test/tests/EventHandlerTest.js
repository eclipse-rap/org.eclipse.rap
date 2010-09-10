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
      var node2 = targetNode.childNodes[ 0 ];
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
    
    // testKeyDownPrintable
    // testKeyHoldPrintable
    // testKeyReleasePrintable (non-printable, modifier)

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