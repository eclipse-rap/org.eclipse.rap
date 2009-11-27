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
      testUtil.clearTimerOnceLog();
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
      assertTrue( request.search( dragDrop + ".x=32" ) != -1 );
      assertTrue( request.search( dragDrop + ".y=16" ) != -1 );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      dndSupport.cancel();
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
      assertTrue( request.search( dragDrop + ".x=32" ) != -1 );
      assertTrue( request.search( dragDrop + ".y=16" ) != -1 );
      assertTrue( request.search( dragEnd ) != -1 );
      testUtil.clearTimerOnceLog();
      testUtil.clearRequestLog();
      dndSupport.cancel();
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      testUtil.flush();      
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
     }

  }
  
} );