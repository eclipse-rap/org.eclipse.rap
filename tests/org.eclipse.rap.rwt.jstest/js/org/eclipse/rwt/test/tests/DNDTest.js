/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.DNDTest", {
  extend : qx.core.Object,
  
  construct : function() {
    this.base( arguments );
  },
  
  members : {

    testEventListener : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var source = this.createSource();
      var target = this.createTarget();
      assertTrue( source.hasEventListeners( "dragstart" ) );
      assertTrue( source.hasEventListeners( "dragend" ) );
      assertTrue( target.hasEventListeners( "dragover" ) );
      assertTrue( target.hasEventListeners( "dragmove" ) );
      assertTrue( target.hasEventListeners( "dragout" ) );
      assertTrue( target.hasEventListeners( "dragdrop" ) );
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      assertFalse( source.hasEventListeners( "dragstart" ) );
      assertFalse( source.hasEventListeners( "dragend" ) );
      assertFalse( target.hasEventListeners( "dragover" ) );
      assertFalse( target.hasEventListeners( "dragmove" ) );
      assertFalse( target.hasEventListeners( "dragout" ) );
      assertFalse( target.hasEventListeners( "dragdrop" ) );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      testUtil.flush();
    },
    
    testHashMaps : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var source = this.createSource();
      var target = this.createTarget();
      var sourceHash = source.toHashCode();
      var targetHash = target.toHashCode();
      var sourceEntry = dndSupport._dragSources[ sourceHash ];
      var targetEntry = dndSupport._dropTargets[ targetHash ];
      assertTrue( typeof sourceEntry == "object" );
      assertTrue( sourceEntry.actions.copy );
      assertTrue( sourceEntry.actions.move );
      assertTrue( sourceEntry.actions.alias );
      assertTrue( sourceEntry.dataTypes[ 0 ] == "default" );
      assertTrue( typeof targetEntry == "object" );
      assertTrue( targetEntry.actions.copy );
      assertTrue( targetEntry.actions.move );
      assertTrue( targetEntry.actions.alias );
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      sourceEntry = dndSupport._dragSources[ sourceHash ];
      targetEntry = dndSupport._dropTargets[ targetHash ];
      assertTrue( typeof sourceEntry == "undefined" );
      assertTrue( typeof targetEntry == "undefined" );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      testUtil.flush();
    },
     
    testDragStartAndFinish : function() {
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      testUtil.initRequestLog();
      var source = this.createSource();
      var node = source._getTargetNode();
      testUtil.fakeMouseEventDOM( node, "mousedown", leftButton, 11, 11 );
      testUtil.clearRequestLog();
      testUtil.fakeMouseEventDOM( node, "mousemove", leftButton, 19, 19 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];
      var dragStart = "org.eclipse.swt.dnd.dragStart=w1";
      assertTrue( request.search( dragStart ) != -1 );
      testUtil.clearRequestLog();
      testUtil.fakeMouseEventDOM( node, "mouseup", leftButton, 19, 19 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];
      var dragFinished = "org.eclipse.swt.dnd.dragFinished=w1";
      assertTrue( request.search( dragFinished ) != -1 );
      assertFalse( dndSupport._blockDrag );
      dndSupport.deregisterDragSource( source );
      source.setParent( null );
      source.destroy();
      testUtil.flush();
    },

    testDragStartAndCancel : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      testUtil.initRequestLog();
      var source = this.createSource();
      var node = source._getTargetNode();
      testUtil.fakeMouseEventDOM( node, "mousedown", leftButton, 11, 11 );
      testUtil.clearRequestLog();
      testUtil.fakeMouseEventDOM( node, "mousemove", leftButton, 19, 19 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];      
      var dragStart = "org.eclipse.swt.dnd.dragStart=w1";
      assertTrue( request.search( dragStart ) != -1 );
      testUtil.clearRequestLog();
      dndSupport.cancel();
      assertNull( dndHandler.__dragCache );
      assertEquals( 0, testUtil.getRequestsSend() ); // no dragFinished
      assertFalse( dndSupport._blockDrag );
      dndSupport.deregisterDragSource( source );
      source.setParent( null );
      source.destroy();
      testUtil.flush();
    },

    testDragEnterOverLeave : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      // request uses SWT-like event-names: 
      var dragStart = "org.eclipse.swt.dnd.dragStart";
      var dragOver = "org.eclipse.swt.dnd.dragEnter";
      var dragMove = "org.eclipse.swt.dnd.dragOver";
      var dragOut = "org.eclipse.swt.dnd.dragLeave";
      testUtil.prepareTimerUse();
      testUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
      var doc = document.body;
      // drag
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.clearRequestLog();
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];
      assertTrue( request.search( dragStart + "=w1" ) != -1 );
      assertTrue( request.search( dragStart + ".x=25" ) != -1 );
      assertTrue( request.search( dragStart + ".y=15" ) != -1 );
      testUtil.clearRequestLog();
      testUtil.clearTimerOnceLog();
      // Over
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      assertTrue( testUtil.getTimerOnceLog().length > 0 );
      testUtil.forceTimerOnce();
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];      
      assertTrue( request.search( dragOver + "=w2" ) != -1 );
      assertTrue( request.search( dragOver + ".source=w1" ) != -1 );
      assertTrue( request.search( dragOver + ".x=32" ) != -1 );
      assertTrue( request.search( dragOver + ".y=15" ) != -1 );
      assertTrue( request.search( dragMove + "=w2" ) != -1 );
      assertTrue( request.search( dragMove + ".source=w1" ) != -1 );
      assertTrue( request.search( dragMove + ".x=32" ) != -1 );
      assertTrue( request.search( dragMove + ".y=15" ) != -1 );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      // Move
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 33, 15 );
      assertTrue( testUtil.getTimerOnceLog().length > 0 );
      testUtil.forceTimerOnce();
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];
      assertTrue( request.search( dragMove + "=w2" ) != -1 );
      assertTrue( request.search( dragMove + ".source=w1" ) != -1 );
      assertTrue( request.search( dragMove + ".x=33" ) != -1 );
      assertTrue( request.search( dragMove + ".y=15" ) != -1 );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      // Out
      testUtil.fakeMouseEventDOM( targetNode, "mouseout", leftButton, 41, 15 );
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 42, 15 );
      assertTrue( testUtil.getTimerOnceLog().length > 0 );
      testUtil.forceTimerOnce();
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];
      assertTrue( request.search( dragOut + "=w2" ) != -1 );
      assertTrue( request.search( dragOut + ".source=w1" ) != -1 );
      assertTrue( request.search( dragOut + ".x=42" ) != -1 );
      assertTrue( request.search( dragOut + ".y=15" ) != -1 );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      testUtil.flush();
    },

    // See Bug 301434
    testEnterEventOnDragStart: function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      // request uses SWT-like event-names: 
      var dragStart = "org.eclipse.swt.dnd.dragStart";
      var dragOver = "org.eclipse.swt.dnd.dragEnter";
      var dragMove = "org.eclipse.swt.dnd.dragOver";
      testUtil.prepareTimerUse();
      testUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      target.setLocation( 10, 20 );
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
      var doc = document.body;
      // drag + over
      testUtil.clearRequestLog();
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 19 );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 11, 25 );
      testUtil.forceTimerOnce();
      assertEquals( 2, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];      
      assertTrue( request.search( dragStart + "=w1" ) != -1 );
      request = testUtil.getRequestLog()[ 1 ];      
      assertTrue( request.search( dragOver + "=w2" ) != -1 );
      assertTrue( request.search( dragMove + "=w2" ) != -1 );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      testUtil.flush();
    },

    // See Bug 301276
    testSetPropertyRetroactively : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      // request uses SWT-like event-names: 
      var dragOver = "org.eclipse.swt.dnd.dragEnter";
      var dragMove = "org.eclipse.swt.dnd.dragOver";
      testUtil.prepareTimerUse();
      testUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
      var doc = document.body;
      // drag
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.clearRequestLog();
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      // Over
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      testUtil.forceTimerOnce();
      assertEquals( 2, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 1 ];      
      assertTrue( request.search( dragOver + "=w2" ) != -1 );
      assertTrue( request.search( dragOver + ".dataType=null" ) != -1 );
      assertTrue( request.search( dragOver + ".operation=move" ) != -1 );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      // Move
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 33, 15 );
      assertTrue( testUtil.getTimerOnceLog().length > 0 );
      // set properties
      dndSupport.setDataType( target, "76135" );
      dndSupport.setOperationOverwrite( target, "copy" );
      // send event:
      testUtil.forceTimerOnce();
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];
      assertTrue( request.search( dragMove + "=w2" ) != -1 );      
      assertTrue( request.search( dragMove + ".operation=copy" ) != -1 );
      assertTrue( request.search( dragMove + ".dataType=76135" ) != -1 );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      testUtil.flush();
    },

    // for Bug 299034
    testStopDropEventPropagation : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      var actions = [ "copy", "move", "alias" ];
      var source = this.createSource();
      var target = this.createTarget();
      var parentTarget = new qx.ui.layout.CanvasLayout();
      parentTarget.setLocation( 10, 10 );
      parentTarget.setDimension( 100, 100 );
      parentTarget.addToDocument();
      parentTarget.setUserData( "id", "w3" );
      parentTarget.setUserData( "isControl", true );
      dndSupport.registerDropTarget( parentTarget, actions );
      dndSupport.setDropTargetTransferTypes( parentTarget, [ "default" ] );
      target.setParent( parentTarget );
      var targetLog = [];
      var parentTargetLog = [];
      var addToTargetLog = function( value ){ 
        targetLog.push( value ); 
      };
      var addToParentTargetLog = function( value ){ 
        parentTargetLog.push( value ); 
      };
      parentTarget.addEventListener( "dragover", addToParentTargetLog ); 
      parentTarget.addEventListener( "dragmove", addToParentTargetLog ); 
      parentTarget.addEventListener( "dragout", addToParentTargetLog ); 
      parentTarget.addEventListener( "dragdrop", addToParentTargetLog ); 
      target.addEventListener( "dragover", addToTargetLog ); 
      target.addEventListener( "dragmove", addToTargetLog ); 
      target.addEventListener( "dragout", addToTargetLog ); 
      target.addEventListener( "dragdrop", addToTargetLog ); 
      org.eclipse.rwt.test.fixture.TestUtil.flush();      
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
      var doc = document.body;
      // drag
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      // Over + move
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      // Move
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 33, 15 );
      // Out
      testUtil.fakeMouseEventDOM( targetNode, "mouseout", leftButton, 41, 15 );
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 42, 15 );      
      // Over + move
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      // Drop 
      testUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton, 32, 16 );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      assertEquals( 0, parentTargetLog.length );
      assertEquals( 7, targetLog.length );
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      testUtil.flush();
    },

    testIgnorePassOverTarget : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      // request uses SWT-like event-names:
      var dragStart = "org.eclipse.swt.dnd.dragStart";
      var dragOver = "org.eclipse.swt.dnd.dragEnter";
      var dragMove = "org.eclipse.swt.dnd.dragOver";
      var dragOut = "org.eclipse.swt.dnd.dragLeave";
      testUtil.prepareTimerUse();
      testUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
      var doc = document.body;
      // drag
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.clearRequestLog();
      testUtil.clearTimerOnceLog();
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertEquals( 1, testUtil.getRequestsSend() );
      testUtil.clearRequestLog();
      testUtil.clearTimerOnceLog();
      // Simulate over->move->out, results in no request send
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      testUtil.fakeMouseEventDOM( targetNode, "mouseout", leftButton, 41, 15 );
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 42, 15 );
      assertTrue( testUtil.getTimerOnceLog().length > 0 );
      testUtil.forceTimerOnce();      
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];      
      assertFalse( request.search( dragOut ) != -1 );
      assertFalse( request.search( dragMove ) != -1 );
      assertFalse( request.search( dragOver ) != -1 );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      testUtil.flush();
    },
    
    testIgnorePassOverTargetAfterLeave : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      // request uses SWT-like event-names:
      var dragStart = "org.eclipse.swt.dnd.dragStart";
      var dragOver = "org.eclipse.swt.dnd.dragEnter";
      var dragMove = "org.eclipse.swt.dnd.dragOver";
      var dragOut = "org.eclipse.swt.dnd.dragLeave";
      testUtil.prepareTimerUse();
      testUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
      var doc = document.body;
      // drag
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.clearRequestLog();
      testUtil.clearTimerOnceLog();
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertEquals( 1, testUtil.getRequestsSend() );
      testUtil.clearRequestLog();
      testUtil.clearTimerOnceLog();
      // Over
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      assertTrue( testUtil.getTimerOnceLog().length > 0 );
      testUtil.forceTimerOnce();
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      // Simulate move->out->over->move->out, results in out only
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 33, 15 );
      testUtil.fakeMouseEventDOM( targetNode, "mouseout", leftButton, 41, 15 );
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 42, 15 );
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      testUtil.fakeMouseEventDOM( targetNode, "mouseout", leftButton, 41, 15 );
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 42, 15 );
      assertTrue( testUtil.getTimerOnceLog().length > 0 );
      testUtil.forceTimerOnce();
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];
      assertTrue( request.search( dragOut + "=w2" ) != -1 );
      assertTrue( request.search( dragOut + ".source=w1" ) != -1 );
      assertTrue( request.search( dragOut + ".x=42" ) != -1 );
      assertTrue( request.search( dragOut + ".y=15" ) != -1 );
      assertFalse( request.search( dragMove ) != -1 );
      assertFalse( request.search( dragOver ) != -1 );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      testUtil.flush();
    },
    
    testDragStartAndDrop : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      var dragStart = "org.eclipse.swt.dnd.dragStart";
      var dragOver = "org.eclipse.swt.dnd.dragEnter";
      var dragMove = "org.eclipse.swt.dnd.dragOver";
      var dragDrop = "org.eclipse.swt.dnd.dropAccept";
      var dragEnd = "org.eclipse.swt.dnd.dragFinished";
      testUtil.prepareTimerUse();
      testUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
      var doc = document.body;
      // drag
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.clearRequestLog();
      testUtil.clearTimerOnceLog();
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];
      assertTrue( request.search( dragStart + "=w1" ) != -1 );
      testUtil.clearRequestLog();
      testUtil.clearTimerOnceLog();
      // Over
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      assertTrue( testUtil.getTimerOnceLog().length > 0 );
      testUtil.forceTimerOnce();
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];
      assertTrue( request.search( dragOver + "=w2" ) != -1 );
      assertTrue( request.search( dragMove + "=w2" ) != -1 );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      // Drop
      testUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton, 32, 16 );
      //assertTrue( testUtil.getTimerOnceLog().length > 0 );
      //testUtil.forceTimerOnce();      
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];
      assertTrue( request.search( dragDrop + "=w2" ) != -1 );
      assertTrue( request.search( dragDrop + ".source=w1" ) != -1 );
      assertTrue( request.search( dragDrop + ".x=32" ) != -1 );
      assertTrue( request.search( dragDrop + ".y=16" ) != -1 );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      testUtil.flush();
    },
 
    
    testFakeMouseEvents : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      var dragStart = "org.eclipse.swt.dnd.dragStart";
      var dragOver = "org.eclipse.swt.dnd.dragEnter";
      var dragMove = "org.eclipse.swt.dnd.dragOver";
      var dragDrop = "org.eclipse.swt.dnd.dropAccept";
      var dragEnd = "org.eclipse.swt.dnd.dragFinished";
      testUtil.prepareTimerUse();
      testUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      var log = new Array();
      source.addEventListener( "mouseout", function( event ) {
        log.push( event );
      }, this );
      target.addEventListener( "mouseover", function( event ) {
        log.push( event );
      }, this );
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
      var doc = document.body;
      // drag
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.fakeMouseEventDOM( sourceNode, "mousemove", leftButton, 25, 15 );
      assertEquals( 1, log.length );
      assertTrue( log[ 0 ] instanceof qx.event.type.MouseEvent );
      assertEquals( "mouseout", log[ 0 ].getType() );
      // Over
      testUtil.fakeMouseEventDOM( sourceNode, "mouseout", leftButton, 31, 15 );
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      // drop
      testUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton );
      assertEquals( 2, log.length );
      assertTrue( log[ 1 ] instanceof qx.event.type.MouseEvent );
      assertEquals( "mouseover", log[ 1 ].getType() );
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      testUtil.flush();
    },
 
    
    testMouseupOutOfDocument : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      var dragStart = "org.eclipse.swt.dnd.dragStart";
      var dragOver = "org.eclipse.swt.dnd.dragEnter";
      var dragMove = "org.eclipse.swt.dnd.dragOver";
      var dragDrop = "org.eclipse.swt.dnd.dropAccept";
      var dragEnd = "org.eclipse.swt.dnd.dragFinished";
      testUtil.prepareTimerUse();
      testUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
      var doc = document.body;
      // drag
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      // Over      
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      // mouseup out of document
      // ( no need to simulate )
      // mousdown
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      testUtil.fakeMouseEventDOM( targetNode, "mousedown", leftButton, 32, 16 );
      // mouseup
      testUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton, 32, 16 );
      testUtil.forceTimerOnce();
      assertEquals(2, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];
      assertTrue( request.search( dragDrop + "=w2" ) != -1 );
      assertTrue( request.search( dragDrop + ".source=w1" ) != -1 );
      assertTrue( request.search( dragDrop + ".x=32" ) != -1 );
      assertTrue( request.search( dragDrop + ".y=16" ) != -1 );
      assertTrue( request.search( dragEnd ) != -1 );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );  
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      testUtil.flush();
    },

    testTreeInsertIndicator : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      testUtil.prepareTimerUse();
      testUtil.initRequestLog();
      var tree = this.createTreeTarget();
      var item1 = this.createTreeItem( 1, tree, tree );
      var item2 = this.createTreeItem( 2, tree, item1 );
      var source = this.createSource();
      item1.setOpen( true ) 
      testUtil.flush();
      var sourceNode = source._getTargetNode();
      var targetNode = item2._getTargetNode();
      var treeNode = tree.getElement();
      var doc = document.body;
      // drag
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      // Over
      testUtil.fakeMouseEventDOM( treeNode, "mouseover", leftButton );
      testUtil.fakeMouseEventDOM( treeNode, "mousemove", leftButton );
      assertEquals( tree, dndSupport._currentDropTarget );
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      dndSupport.setFeedback( tree, [ "after" ] );
      assertNotNull( dndSupport._dropFeedbackRenderer );
      testUtil.flush();
      var indicator = dndSupport._dropFeedbackRenderer._insertIndicator;
      assertNotNull( indicator );
      assertIdentical( tree._tree._getTargetNode(), indicator.parentNode );
      // drop
      testUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      if( qx.core.Client.getEngine() != "mshtml" ) {
        // TODO [tb] : does not succeed in IE unless the debugger is running
        assertNull( indicator.parentNode );
      }
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( tree );
      source.setParent( null );
      source.destroy();
      tree.setParent( null );
      tree.destroy();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      wm.dispose( "w3" );
      wm.dispose( "w4" );
      testUtil.flush();
    },

    testTreeFeedbackSelect : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      testUtil.prepareTimerUse();
      testUtil.initRequestLog();
      var tree = this.createTreeTarget();
      var item1 = this.createTreeItem( 1, tree, tree );
      var item2 = this.createTreeItem( 2, tree, item1 );
      var source = this.createSource();
      item1.setOpen( true ) 
      testUtil.flush();
      var sourceNode = source._getTargetNode();
      var targetNode = item2._getTargetNode();
      var treeNode = tree.getElement();
      var doc = document.body;
      // drag
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      // Over
      testUtil.fakeMouseEventDOM( treeNode, "mouseover", leftButton );
      testUtil.fakeMouseEventDOM( treeNode, "mousemove", leftButton );
      assertEquals( tree, dndSupport._currentDropTarget );
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      dndSupport.setFeedback( tree, [ "select" ] );
      assertNotNull( dndSupport._dropFeedbackRenderer );
      testUtil.flush();
      assertTrue( item2.hasState( "selected" ) );
      // drop
      testUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton );
      assertFalse( item2.hasState( "selected" ) );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( tree );
      source.setParent( null );
      source.destroy();
      tree.setParent( null );
      tree.destroy();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      wm.dispose( "w3" );
      wm.dispose( "w4" );
      testUtil.flush();
    },

    testTreeFeedbackExpand : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      testUtil.prepareTimerUse();
      testUtil.initRequestLog();
      var tree = this.createTreeTarget();
      var item1 = this.createTreeItem( 1, tree, tree );
      var item2 = this.createTreeItem( 2, tree, item1 );
      var source = this.createSource();
      testUtil.flush();
      var sourceNode = source._getTargetNode();
      var targetNode = item1._getTargetNode();
      var treeNode = tree.getElement();
      var doc = document.body;
      // drag
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      assertFalse( item2.isSeeable() );
      // Over
      testUtil.fakeMouseEventDOM( treeNode, "mouseover", leftButton );
      testUtil.fakeMouseEventDOM( treeNode, "mousemove", leftButton );
      assertEquals( tree, dndSupport._currentDropTarget );
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      dndSupport.setFeedback( tree, [ "expand" ] );
      assertNotNull( dndSupport._dropFeedbackRenderer );
      assertNotNull( dndSupport._dropFeedbackRenderer._expandTimer );
      testUtil.clearTimerOnceLog();
      testUtil.forceInterval( dndSupport._dropFeedbackRenderer._expandTimer );
      testUtil.flush();
      testUtil.forceTimerOnce(); // Tree uses timer for in method "open"
      assertTrue( item1.getOpen() );
      assertTrue( item2.isSeeable() );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( tree );
      source.setParent( null );
      source.destroy();
      tree.setParent( null );
      tree.destroy();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      wm.dispose( "w3" );
      wm.dispose( "w4" );
      testUtil.flush();
    },

    testTreeFeedbackScroll : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      testUtil.prepareTimerUse();
      testUtil.initRequestLog();
      var tree = this.createTreeTarget();
      var item1 = this.createTreeItem( 1, tree, tree );
      var item2 = this.createTreeItem( 2, tree, tree );
      var item3 = this.createTreeItem( 3, tree, tree );
      var item4 = this.createTreeItem( 4, tree, tree );
      var source = this.createSource();
      testUtil.flush();
      var sourceNode = source._getTargetNode();
      var targetNode = item2._getTargetNode();
      var treeNode = tree.getElement();
      var doc = document.body;
      // drag 
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      // Over tree
      testUtil.fakeMouseEventDOM( treeNode, "mouseover", leftButton );
      testUtil.fakeMouseEventDOM( treeNode, "mousemove", leftButton );
      assertEquals( tree, dndSupport._currentDropTarget );
      // over item 2
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      dndSupport.setFeedback( tree, [ "scroll" ] );
      // setting feedback
      var feedback = dndSupport._dropFeedbackRenderer;
      assertNotNull( feedback );
      assertTrue( feedback._isScrolledIntoView( item1 ) );
      assertFalse( feedback._isScrolledIntoView( item2 ) );
      // item2 is only partly in view, so it is the next target
      assertEquals( item2, dndSupport._dropFeedbackRenderer._currentItem );
      assertEquals( item2, feedback._getScrollItem( item2 ) );
      testUtil.clearTimerOnceLog();
      // scroll to item 2
      testUtil.forceInterval( dndSupport._dropFeedbackRenderer._scrollTimer );
      testUtil.flush();   
      var scrollTop = tree._tree.getScrollTop();
      assertTrue( scrollTop > 0 );
      assertTrue( testUtil.getTimerOnceLog().length > 0 );
      testUtil.forceTimerOnce();
      assertEquals( item2, dndSupport._dropFeedbackRenderer._currentItem );
      // scroll to item 3
      assertEquals( item3, feedback._getScrollItem( item2 ) );
      testUtil.forceInterval( dndSupport._dropFeedbackRenderer._scrollTimer );
      testUtil.flush();
      assertTrue( tree._tree.getScrollTop() > scrollTop );
      scrollTop = tree._tree.getScrollTop();
      assertTrue( testUtil.getTimerOnceLog().length > 0 );
      testUtil.forceTimerOnce();
      assertEquals( item3, dndSupport._dropFeedbackRenderer._currentItem );
      // scroll to item 4
      assertEquals( item4, feedback._getScrollItem( item3 ) );
      testUtil.forceInterval( dndSupport._dropFeedbackRenderer._scrollTimer );
      testUtil.flush();
      assertTrue( tree._tree.getScrollTop() > scrollTop );
      assertTrue( testUtil.getTimerOnceLog().length > 0 );
      testUtil.forceTimerOnce();
      assertEquals( item4, dndSupport._dropFeedbackRenderer._currentItem );
      // drop
      testUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( tree );
      source.setParent( null );
      source.destroy();
      tree.setParent( null );
      tree.destroy();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      wm.dispose( "w3" );
      wm.dispose( "w4" );
      wm.dispose( "w5" );
      wm.dispose( "w6" );
      testUtil.flush();
    },

    testTreeRequestItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      testUtil.prepareTimerUse();
      testUtil.initRequestLog();
      var tree = this.createTreeTarget();
      var item1 = this.createTreeItem( 1, tree, tree );
      var item2 = this.createTreeItem( 2, tree, tree );
      var source = this.createSource();
      testUtil.flush();
      var sourceNode = source._getTargetNode();
      var targetNode = item2._getTargetNode();
      var treeNode = tree.getElement();
      var doc = document.body;
      // drag 
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      // over item 2
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      dndSupport.setFeedback( tree, [ "scroll" ] );
      // drop
      testUtil.initRequestLog();
      testUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton );
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getRequestLog()[ 0 ];
      var expected = "dropAccept.item=w4";
      assertTrue( request.search( expected ) != -1 );
      source.setParent( null );
      source.destroy();
      tree.setParent( null );
      tree.destroy();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      wm.dispose( "w3" );
      wm.dispose( "w4" );
      wm.dispose( "w5" );
      wm.dispose( "w6" );
      testUtil.flush();
    },

    testFeedbackWidgetTree : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      var tree = new org.eclipse.swt.widgets.Tree("");
      tree.setUserData( "id", "w2" );
      tree.setUserData( "isControl", true );
      tree.setSpace( 13, 364, 27, 30 );
      tree.addToDocument();
      var actions = [ "copy", "move", "alias" ];
      dndSupport.registerDragSource( tree, actions );
      dndSupport.setDragSourceTransferTypes( tree, [ "default" ] );
      var item1 = this.createTreeItem( 1, tree, tree );
      testUtil.flush();
      var sourceNode = item1.getLabelObject()._getTargetNode();
      var doc = document.body;
      // drag
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      testUtil.flush();
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      assertEquals( dndSupport._dropFeedbackRendererWidget, dndHandler.__dropFeedbackRendererWidget );
      dndSupport.cancel();
      assertEquals( null, dndHandler.__dropFeedbackRendererWidget );
      dndSupport.deregisterDragSource( tree );
      tree.setParent( null );
      tree.destroy();
      testUtil.flush();
    },
    
    testFeedbackWidgetTable : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      var table = this.createTable();
      dndSupport.registerDragSource( table, [ "move" ] );
      dndSupport.setDragSourceTransferTypes( table, [ "default" ] );
      var sourceNode = table._rows[ 0 ].getElement();      
      var doc = document.body;
      // drag
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      testUtil.flush();
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      assertEquals( dndSupport._dropFeedbackRendererWidget, dndHandler.__dropFeedbackRendererWidget );
      dndSupport.cancel();
      assertEquals( null, dndHandler.__dropFeedbackRendererWidget );
      dndSupport.deregisterDragSource( table );
      this.disposeTable();
    },
    
    testTableFeedbackSelect : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      var source = this.createSource();
      var table = this.createTable();
      dndSupport.registerDropTarget( table, [ "move" ] );
      dndSupport.setDropTargetTransferTypes( table, [ "default" ] );
      testUtil.flush();
      var sourceNode = source._getTargetNode();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var row2 = table._rows[ 1 ];
      assertEquals( row2.getItemIndex(), 1 );
      var targetNode = row2._getTargetNode();
      var tableNode = table._getTargetNode();
      var doc = document.body;
      // drag
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      // Over
      testUtil.fakeMouseEventDOM( tableNode, "mouseover", leftButton );
      testUtil.fakeMouseEventDOM( tableNode, "mousemove", leftButton );
      assertEquals( table, dndSupport._currentDropTarget );
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      dndSupport.setFeedback( table, [ "select" ] );
      assertNotNull( dndSupport._dropFeedbackRenderer );
      testUtil.flush();
      assertTrue( row2.hasState( "selected" ) );
      // drop
      testUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton );
      assertFalse( row2.hasState( "selected" ) );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDropTarget( table );
      dndSupport.deregisterDragSource( source );
      this.disposeTable();
    },
    
    testTableFeedbackScroll : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      var table = this.createTable();
      table.setHeight( 28 );
      dndSupport.registerDropTarget( table, [ "move" ] );
      dndSupport.setDropTargetTransferTypes( table, [ "default" ] );
      var source = this.createSource();
      testUtil.flush();
      var sourceNode = source._getTargetNode();
      var tableNode = table.getElement();
      assertEquals( 2, table._rows.length );
      var doc = document.body;
      // drag 
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      // Over Table
      testUtil.fakeMouseEventDOM( tableNode, "mouseover", leftButton );
      testUtil.fakeMouseEventDOM( tableNode, "mousemove", leftButton );
      assertEquals( table, dndSupport._currentDropTarget );
      // over row 2
      var targetNode = table._rows[ 1 ].getElement();
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      dndSupport.setFeedback( table, [ "scroll" ] );
      // setting feedback
      var feedback = dndSupport._dropFeedbackRenderer;
      assertNotNull( feedback );
      // row 2 is only partly in view, so scroll down
      assertEquals( table._rows[ 1 ], dndSupport._dropFeedbackRenderer._currentRow );
      assertEquals( 1, table._rows[ 1 ].getItemIndex() );
      assertEquals( 1, feedback._getScrollDirection( 1 ) );
      testUtil.clearTimerOnceLog();
      // scroll to row 3 (partly in view)
      testUtil.forceInterval( dndSupport._dropFeedbackRenderer._scrollTimer );
      testUtil.flush();
      assertEquals( 2, table._rows[ 1 ].getItemIndex() );
      assertTrue( testUtil.getTimerOnceLog().length > 0 );
      assertEquals( table._rows[ 0 ], dndSupport._dropFeedbackRenderer._currentRow );
      testUtil.forceTimerOnce();  // targetUpdateCheck
      assertEquals( table._rows[ 1 ], dndSupport._dropFeedbackRenderer._currentRow );
      // drop
      testUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( table );
      this.disposeTable();      
    },

    /////////
    // Helper
    
    createSource : function() {
      var result = new qx.ui.basic.Atom();
      result.setLocation( 10, 10 );
      result.setDimension( 10, 10 );
      result.addToDocument();
      result.setUserData( "id", "w1" );
      result.setUserData( "isControl", true );
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var actions = [ "copy", "move", "alias" ];
      dndSupport.registerDragSource( result, actions );
      dndSupport.setDragSourceTransferTypes( result, [ "default" ] );
      org.eclipse.rwt.test.fixture.TestUtil.flush();
      return result;
    },
    
    createTarget : function() {
      var result = new qx.ui.basic.Atom();
      result.setLocation( 30, 10 );
      result.setDimension( 10, 10 );
      result.addToDocument();
      result.setUserData( "id", "w2" );
      result.setUserData( "isControl", true );
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var actions = [ "copy", "move", "alias" ];
      dndSupport.registerDropTarget( result, actions );
      dndSupport.setDropTargetTransferTypes( result, [ "default" ] );
      org.eclipse.rwt.test.fixture.TestUtil.flush();
      return result;
    },
    
    createTreeTarget : function() {
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var tree = new org.eclipse.swt.widgets.Tree("");
      tree.setUserData( "id", "w2" );
      tree.setUserData( "isControl", true );
      tree.setSpace( 13, 364, 27, 30 );
      tree.addToDocument();
      var actions = [ "copy", "move", "alias" ];
      dndSupport.registerDropTarget( tree, actions );
      dndSupport.setDropTargetTransferTypes( tree, [ "default" ] );
      return tree;
    },
    
    createTreeItem : function( itemNr, tree, parent ) {
      var nr = itemNr + 2;
      org.eclipse.swt.TreeItemUtil.createTreeItem( "w" + nr, parent, tree, itemNr );
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var item = wm.findWidgetById( "w" + nr );
      item.setTexts( [ "text" + nr ] );
      item.setImages( [ null ] );
      return item;
    },
    
    createTable : function() {
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var table = new org.eclipse.swt.widgets.Table( "w2", "|multi" );
      wm.add( table, "w2", true );
      table.addToDocument();
      table.setSpace( 13, 360, 27, 147 );
      table.setItemHeight( 17 );
      table.setItemMetrics( 0, 0, 105, 2, 0, 2, 101 );
      table.setItemMetrics( 1, 105, 104, 107, 0, 107, 100 );
      table.updateRows();
      table.setItemCount( 4 );
      table.setScrollBarsVisibile( false, true );
      var w = new org.eclipse.swt.widgets.TableColumn( table );
      wm.add( w, "w3", false );
      w.setLabel( "" );
      w.setWidth( 105 );
      w = new org.eclipse.swt.widgets.TableColumn( table );
      wm.add( w, "w4", false );
      w.setLabel( "" );
      w.setLeft( 105 );
      w.setWidth( 104 );
      w.setZIndex( 299 );
      w = new org.eclipse.swt.widgets.TableItem( table, 0 );
      wm.add( w, "w5", false );
      w.setTexts( [ "Item name 1", "Item Value 1" ] );
      w.update();
      w = new org.eclipse.swt.widgets.TableItem( table, 0 );
      wm.add( w, "w6", false );
      w.setTexts( [ "Item name 2", "Item Value 2" ] );
      w.update();
      w = new org.eclipse.swt.widgets.TableItem( table, 0 );
      wm.add( w, "w7", false );
      w.setTexts( [ "Item name 3", "Item Value 3" ] );
      w.update();
      w = new org.eclipse.swt.widgets.TableItem( table, 0 );
      wm.add( w, "w8", false );
      w.setTexts( [ "Item name 4", "Item Value 4" ] );
      w.update();
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.flush(); // creates table
      testUtil.flush(); // creates rows
      delete wm;
      return table;
   },


    testOperationChanged : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance()
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      // request uses SWT-like event-names: 
      var dragStart = "org.eclipse.swt.dnd.dragStart";
      var dragOver = "org.eclipse.swt.dnd.dragEnter";
      var dragMove = "org.eclipse.swt.dnd.dragOver";
      var actionChanged = "org.eclipse.swt.dnd.dragOperationChanged";
      testUtil.prepareTimerUse();
      testUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
      var doc = document.body;
      // drag
      testUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      testUtil.clearRequestLog();
      testUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      var request = testUtil.getRequestLog()[ 0 ];
      assertTrue( request.search( dragStart + "=w1" ) != -1 );
      testUtil.clearRequestLog();
      testUtil.clearTimerOnceLog();
      // Over
      testUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      assertTrue( testUtil.getTimerOnceLog().length > 0 );
      testUtil.forceTimerOnce();
      request = testUtil.getRequestLog()[ 0 ];      
      assertTrue( request.search( dragOver + "=w2" ) != -1 );
      assertFalse( request.search( actionChanged ) != -1 );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();      
      // Move
      testUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 33, 15 );
      assertTrue( testUtil.getTimerOnceLog().length > 0 );
      testUtil.forceTimerOnce();
      request = testUtil.getRequestLog()[ 0 ];
      assertTrue( request.search( dragMove + "=w2" ) != -1 );
      assertFalse( request.search( actionChanged ) != -1 );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      // NOTE : Currently the only way to test the operationChanged event,
      //        since testUtil can not yet fake modifiers in domEvents
      dndSupport._setAction( "none", {} );
      testUtil.forceTimerOnce();
      assertEquals( 1, testUtil.getRequestsSend() );
      request = testUtil.getRequestLog()[ 0 ];
      assertTrue( request.search( actionChanged + "=w2" ) != -1 );
      assertFalse( request.search( actionChanged + ".detail=none") != -1 );      
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      testUtil.flush();
    },
       
   disposeTable : function() {
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      wm.dispose( "w2" );
      wm.dispose( "w3" );
      wm.dispose( "w4" );
      org.eclipse.rwt.test.fixture.TestUtil.flush();
   }
 }
 
} );