/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function() {

var Processor = rwt.remote.MessageProcessor;
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var ObjectManager = rwt.remote.ObjectRegistry;
var dndHandler;
var dndSupport;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.DNDTest", {

  extend : rwt.qx.Object,

  members : {

    TARGETPLATFORM : [ "win", "mac", "unix", "other" ], // touch devices don't support DND (yet)

    setUp : function() {
      dndSupport = rwt.remote.DNDSupport.getInstance();
      dndHandler = rwt.event.DragAndDropHandler.getInstance();
      dndHandler.__dragCache = null;
    },

    tearDown : function() {
      dndSupport.cancel();
      for( var hash in dndSupport._dragSources ) {
        var dragSource = dndSupport._dragSources[ hash ];
        dragSource.dispose();
        dragSource.control.destroy();
      }
      for( var hash in dndSupport._dropTargets ) {
        var dropTarget = dndSupport._dropTargets[ hash ];
        dropTarget.dispose();
        dropTarget.control.destroy();
      }
      var shell = ObjectManager.getObject( "w2" );
      if( shell ) {
        rwt.remote.MessageProcessor.processOperationArray( [ "destroy", "w2" ] );
      }
      rwt.runtime.Singletons.clear( rwt.remote.DNDSupport );
      TestUtil.flush();
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
    },

    testDNDSupportInstance : function() {
      var instance = rwt.remote.DNDSupport.getInstance();

      assertIdentical( instance, rwt.runtime.Singletons.get( rwt.remote.DNDSupport ) );
    },

    testCreateDragSourceByProtocol : function() {
      TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "parent" : "w2",
          "style" : [ "PUSH" ]
        }
      } );

      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.DragSource",
        "properties" : {
          "control" : "w3",
          "style" : [ "DROP_COPY", "DROP_MOVE" ]
        }
      } );

      var button = ObjectManager.getObject( "w3" );
      assertTrue( dndSupport.isDragSource( button ) );
      var actions = dndSupport._dragSources[ button.toHashCode() ].actions;
      var expected = {
        "copy" : true,
        "move" : true
      };
      assertEquals( expected, actions );
      button.destroy();
    },

    testDisposeDragSourceByProtocol : function() {
      TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "parent" : "w2",
          "style" : [ "PUSH" ]
        }
      } );
      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.DragSource",
        "properties" : {
          "control" : "w3",
          "style" : [ "DROP_COPY", "DROP_MOVE" ]
        }
      } );

      var button = ObjectManager.getObject( "w3" );
      assertTrue( dndSupport.isDragSource( button ) );

      Processor.processOperation( {
        "target" : "w4",
        "action" : "destroy"
      } );
      assertFalse( dndSupport.isDragSource( button ) );
      button.destroy();
    },

    testDisposeDragSourceWithControlByProtocol : function() {
      TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "parent" : "w2",
          "style" : [ "PUSH" ]
        }
      } );
      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.DragSource",
        "properties" : {
          "control" : "w3",
          "style" : [ "DROP_COPY", "DROP_MOVE" ]
        }
      } );
      var button = ObjectManager.getObject( "w3" );

      Processor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );

      assertFalse( dndSupport.isDragSource( button ) );
      button.destroy();
    },

    testSetDragSourceTransferByProtocol : function() {
      TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "parent" : "w2",
          "style" : [ "PUSH" ]
        }
      } );

      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.DragSource",
        "properties" : {
          "control" : "w3",
          "style" : [ "DROP_COPY", "DROP_MOVE" ],
          "transfer" : [ "my", "transfer" ]
        }
      } );

      var button = ObjectManager.getObject( "w3" );
      assertTrue( dndSupport.isDragSource( button ) );
      var types = dndSupport._dragSources[ button.toHashCode() ].dataTypes;
      var expected = [ "my", "transfer" ];
      assertEquals( expected, types );
      button.destroy();
    },

    testCreateDropTargetByProtocol : function() {
      TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "parent" : "w2",
          "style" : [ "PUSH" ]
        }
      } );

      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.DropTarget",
        "properties" : {
          "control" : "w3",
          "style" : [ "DROP_COPY", "DROP_MOVE" ]
        }
      } );

      var button = ObjectManager.getObject( "w3" );
      assertTrue( dndSupport.isDropTarget( button ) );
      var actions = dndSupport._dropTargets[ button.toHashCode() ].actions;
      var expected = {
        "copy" : true,
        "move" : true
      };
      assertEquals( expected, actions );
      button.destroy();
    },

    testDisposeDropTargetByProtocol : function() {
      TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "parent" : "w2",
          "style" : [ "PUSH" ]
        }
      } );
      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.DropTarget",
        "properties" : {
          "control" : "w3",
          "style" : [ "DROP_COPY", "DROP_MOVE" ]
        }
      } );

      var button = ObjectManager.getObject( "w3" );
      assertTrue( dndSupport.isDropTarget( button ) );

      Processor.processOperation( {
        "target" : "w4",
        "action" : "destroy"
      } );
      assertFalse( dndSupport.isDropTarget( button ) );
      button.destroy();
    },

    testDisposeDropTargetWithControlByProtocol : function() {
      TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "parent" : "w2",
          "style" : [ "PUSH" ]
        }
      } );
      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.DropTarget",
        "properties" : {
          "control" : "w3",
          "style" : [ "DROP_COPY", "DROP_MOVE" ]
        }
      } );
      var button = ObjectManager.getObject( "w3" );

      Processor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );

      assertFalse( dndSupport.isDropTarget( button ) );
      button.destroy();
    },

    testSetDropTargetTransferByProtocol : function() {
      TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "parent" : "w2",
          "style" : [ "PUSH" ]
        }
      } );

      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.DropTarget",
        "properties" : {
          "control" : "w3",
          "style" : [ "DROP_COPY", "DROP_MOVE" ],
          "transfer" : [ "my", "transfer" ]
        }
      } );

      var button = ObjectManager.getObject( "w3" );
      assertTrue( dndSupport.isDropTarget( button ) );
      var types = button.getDropDataTypes();
      var expected = [ "my", "transfer" ];
      assertEquals( expected, types );
      button.destroy();
    },

    testEventListener : function() {
      var source = this.createDragSource();
      var target = this.createDropTarget();
      assertTrue( source.control.hasEventListeners( "dragstart" ) );
      assertTrue( source.control.hasEventListeners( "dragend" ) );
      assertTrue( target.control.hasEventListeners( "dragover" ) );
      assertTrue( target.control.hasEventListeners( "dragmove" ) );
      assertTrue( target.control.hasEventListeners( "dragout" ) );
      assertTrue( target.control.hasEventListeners( "dragdrop" ) );
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      assertFalse( source.control.hasEventListeners( "dragstart" ) );
      assertFalse( source.control.hasEventListeners( "dragend" ) );
      assertFalse( target.control.hasEventListeners( "dragover" ) );
      assertFalse( target.control.hasEventListeners( "dragmove" ) );
      assertFalse( target.control.hasEventListeners( "dragout" ) );
      assertFalse( target.control.hasEventListeners( "dragdrop" ) );
    },

    testHashMaps : function() {
      var source = this.createDragSource();
      var target = this.createDropTarget();
      assertTrue( typeof source == "object" );
      assertTrue( source.actions.copy );
      assertTrue( source.actions.move );
      assertTrue( source.actions.alias );
      assertTrue( source.dataTypes[ 0 ] == "default" );
      assertTrue( typeof target == "object" );
      assertTrue( target.actions.copy );
      assertTrue( target.actions.move );
      assertTrue( target.actions.alias );
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      assertFalse( dndSupport.isDragSource( source.control ) );
      assertFalse( dndSupport.isDropTarget( target.control ) );
    },

    // TODO [tb] : fix order within tests (asserts at end)
    testDragStartAndFinish : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var source = this.createDragSource();
      var node = source.control._getTargetNode();

      TestUtil.fakeMouseEventDOM( node, "mousedown", leftButton, 11, 11 );
      TestUtil.clearRequestLog();
      TestUtil.fakeMouseEventDOM( node, "mousemove", leftButton, 19, 19 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "ds#c0", "DragStart" ) );
      TestUtil.clearRequestLog();
      TestUtil.fakeMouseEventDOM( node, "mouseup", leftButton, 19, 19 );

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "ds#c0", "DragEnd" ) );
      assertFalse( dndSupport._blockDrag );
    },

    testDragEndPropagation : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      var log = [];
      TestUtil.getDocument().addEventListener( "dragend", function( event ) {
        log.push( event );
      } );
      var source = this.createDragSource();
      var node = source.control._getTargetNode();

      TestUtil.fakeMouseEventDOM( node, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( node, "mousemove", leftButton, 19, 19 );
      TestUtil.fakeMouseEventDOM( node, "mouseup", leftButton, 19, 19 );

      assertEquals( 0, log.length );
    },

    testDragStartAndCancel : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var source = this.createDragSource();
      var node = source.control._getTargetNode();

      TestUtil.fakeMouseEventDOM( node, "mousedown", leftButton, 11, 11 );
      TestUtil.clearRequestLog();
      TestUtil.fakeMouseEventDOM( node, "mousemove", leftButton, 19, 19 );
      dndSupport.cancel();

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "ds#c0", "DragStart" ) );
      assertNull( dndHandler.__dragCache );
      assertEquals( 1, TestUtil.getRequestsSend() ); // no dragFinished
      assertFalse( dndSupport._blockDrag );
    },

    testDragEnterOverLeave : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createDragSource();
      var target = this.createDropTarget();
      var sourceNode = source.control._getTargetNode();
      var targetNode = target.control._getTargetNode();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.clearRequestLog();
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( 25, message.findNotifyProperty( "ds#c0", "DragStart", "x" ) );
      assertEquals( 15, message.findNotifyProperty( "ds#c0", "DragStart", "y" ) );
      TestUtil.clearRequestLog();
      TestUtil.clearTimerOnceLog();
      // Over
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      message = TestUtil.getMessageObject();
      assertEquals( "c0", message.findNotifyProperty( "dt#c1", "DragEnter", "source" ) );
      assertEquals( 32, message.findNotifyProperty( "dt#c1", "DragEnter", "x" ) );
      assertEquals( 15, message.findNotifyProperty( "dt#c1", "DragEnter", "y" ) );
      assertEquals( "c0", message.findNotifyProperty( "dt#c1", "DragOver", "source" ) );
      assertEquals( 32, message.findNotifyProperty( "dt#c1", "DragOver", "x" ) );
      assertEquals( 15, message.findNotifyProperty( "dt#c1", "DragOver", "y" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      // Move
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 33, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      message = TestUtil.getMessageObject();
      assertEquals( "c0", message.findNotifyProperty( "dt#c1", "DragOver", "source" ) );
      assertEquals( 33, message.findNotifyProperty( "dt#c1", "DragOver", "x" ) );
      assertEquals( 15, message.findNotifyProperty( "dt#c1", "DragOver", "y" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      // Out
      TestUtil.fakeMouseEventDOM( targetNode, "mouseout", leftButton, 41, 15 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 42, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      message = TestUtil.getMessageObject();
      assertEquals( "c0", message.findNotifyProperty( "dt#c1", "DragLeave", "source" ) );
      assertEquals( 42, message.findNotifyProperty( "dt#c1", "DragLeave", "x" ) );
      assertEquals( 15, message.findNotifyProperty( "dt#c1", "DragLeave", "y" ) );
    },

    // See Bug 301434
    testEnterEventOnDragStart: function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      // request uses SWT-like event-names:
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createDragSource();
      var target = this.createDropTarget();
      target.control.setLocation( 10, 20 );
      var sourceNode = source.control._getTargetNode();
      var targetNode = target.control._getTargetNode();
      TestUtil.clearRequestLog();

      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 19 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 11, 25 );
      TestUtil.forceTimerOnce();

      assertEquals( 2, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject( 0 ).findNotifyOperation( "ds#c0", "DragStart" ) );
      assertNotNull( TestUtil.getMessageObject( 1 ).findNotifyOperation( "dt#c1", "DragEnter" ) );
      assertNotNull( TestUtil.getMessageObject( 1 ).findNotifyOperation( "dt#c1", "DragOver" ) );
    },

    // See Bug 301276
    testSetPropertyRetroactively : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createDragSource();
      var target = this.createDropTarget();
      var sourceNode = source.control._getTargetNode();
      var targetNode = target.control._getTargetNode();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.clearRequestLog();
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      // Over
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      TestUtil.forceTimerOnce();
      assertEquals( 2, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject( 1 );
      assertEquals( null, message.findNotifyProperty( "dt#c1", "DragEnter", "dataType" ) );
      assertEquals( "move", message.findNotifyProperty( "dt#c1", "DragEnter", "operation" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      // Move
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 33, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      // set properties
      target.changeDataType( "76135" );
      target.changeDetail( "copy" );
      // send event:
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      message = TestUtil.getMessageObject( 0 );
      assertEquals( "copy", message.findNotifyProperty( "dt#c1", "DragOver", "operation" ) );
      assertEquals( "76135", message.findNotifyProperty( "dt#c1", "DragOver", "dataType" ) );
    },

    // for Bug 299034
    testStopDropEventPropagation : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      var source = this.createDragSource();
      var target = this.createDropTarget();
      var parentTarget = new rwt.widgets.base.Parent();
      parentTarget.setLocation( 10, 10 );
      parentTarget.setDimension( 100, 100 );
      parentTarget.addToDocument();
      rwt.remote.WidgetManager.getInstance().add( parentTarget, "w3" );
      parentTarget.setUserData( "isControl", true );
      this.createDropTarget( parentTarget );
      target.control.setParent( parentTarget );
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
      target.control.addEventListener( "dragover", addToTargetLog );
      target.control.addEventListener( "dragmove", addToTargetLog );
      target.control.addEventListener( "dragout", addToTargetLog );
      target.control.addEventListener( "dragdrop", addToTargetLog );
      TestUtil.flush();
      var sourceNode = source.control._getTargetNode();
      var targetNode = target.control._getTargetNode();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      // Over + move
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      // Move
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 33, 15 );
      // Out
      TestUtil.fakeMouseEventDOM( targetNode, "mouseout", leftButton, 41, 15 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 42, 15 );
      // Over + move
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      // Drop
      TestUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton, 32, 16 );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      assertEquals( 0, parentTargetLog.length );
      assertEquals( 7, targetLog.length );
    },

    testIgnorePassOverTarget : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createDragSource();
      var target = this.createDropTarget();
      var sourceNode = source.control._getTargetNode();
      var targetNode = target.control._getTargetNode();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.clearRequestLog();
      TestUtil.clearTimerOnceLog();
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      TestUtil.clearRequestLog();
      TestUtil.clearTimerOnceLog();
      // Simulate over->move->out, results in no request send
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mouseout", leftButton, 41, 15 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 42, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNull( TestUtil.getMessageObject().findNotifyOperation( "dt#c1", "DragLeave" ) );
      assertNull( TestUtil.getMessageObject().findNotifyOperation( "dt#c1", "DragOver" ) );
      assertNull( TestUtil.getMessageObject().findNotifyOperation( "dt#c1", "dragMove" ) );
    },

    testIgnorePassOverTargetAfterLeave : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createDragSource();
      var target = this.createDropTarget();
      var sourceNode = source.control._getTargetNode();
      var targetNode = target.control._getTargetNode();
      var doc = document.body;

      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.clearRequestLog();
      TestUtil.clearTimerOnceLog();
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      TestUtil.clearRequestLog();
      TestUtil.clearTimerOnceLog();
      // Over
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      // Simulate move->out->over->move->out, results in out only
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 33, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mouseout", leftButton, 41, 15 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 42, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mouseout", leftButton, 41, 15 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 42, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "c0", message.findNotifyProperty( "dt#c1", "DragLeave", "source" ) );
      assertEquals( 42, message.findNotifyProperty( "dt#c1", "DragLeave", "x" ) );
      assertEquals( 15, message.findNotifyProperty( "dt#c1", "DragLeave", "y" ) );
      assertNull( message.findNotifyOperation( "dt#c1", "DragOver" ) );
      assertNull( message.findNotifyOperation( "dt#c1", "DragEnter" ) );
    },

    testDragStartAndDrop : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createDragSource();
      var target = this.createDropTarget();
      var sourceNode = source.control._getTargetNode();
      var targetNode = target.control._getTargetNode();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.clearRequestLog();
      TestUtil.clearTimerOnceLog();
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyProperty( "ds#c0", "DragStart" ) );
      TestUtil.clearRequestLog();
      TestUtil.clearTimerOnceLog();
      // Over
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyProperty( "dt#c1", "DragEnter" ) );
      assertNotNull( message.findNotifyProperty( "dt#c1", "DragOver" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      // Drop
      TestUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton, 32, 16 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      message = TestUtil.getMessageObject();
      assertEquals( "c0", message.findNotifyProperty( "dt#c1", "DropAccept", "source" ) );
      assertEquals( 32, message.findNotifyProperty( "dt#c1", "DropAccept", "x" ) );
      assertEquals( 16, message.findNotifyProperty( "dt#c1", "DropAccept", "y" ) );
    },

    testFakeMouseEvents : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createDragSource();
      var target = this.createDropTarget();
      var log = [];
      source.control.addEventListener( "mouseout", function( event ) {
        log.push( event );
      }, this );
      target.control.addEventListener( "mouseover", function( event ) {
        log.push( event );
      }, this );
      var sourceNode = source.control._getTargetNode();
      var targetNode = target.control._getTargetNode();
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( sourceNode, "mousemove", leftButton, 25, 15 );
      assertEquals( 1, log.length );
      assertTrue( log[ 0 ] instanceof rwt.event.MouseEvent );
      assertEquals( "mouseout", log[ 0 ].getType() );
      // Over
      TestUtil.fakeMouseEventDOM( sourceNode, "mouseout", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      // drop
      TestUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton );
      assertEquals( 2, log.length );
      assertTrue( log[ 1 ] instanceof rwt.event.MouseEvent );
      assertEquals( "mouseover", log[ 1 ].getType() );
    },

    testMouseupOutOfDocument : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createDragSource();
      var target = this.createDropTarget();
      var sourceNode = source.control._getTargetNode();
      var targetNode = target.control._getTargetNode();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      // Over
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      // mouseup out of document
      // ( no need to simulate )
      // mousdown
      TestUtil.clearTimerOnceLog();
      rwt.remote.Connection.getInstance().send();
      TestUtil.clearRequestLog();
      TestUtil.fakeMouseEventDOM( targetNode, "mousedown", leftButton, 32, 16 );
      // mouseup
      TestUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton, 32, 16 );
      TestUtil.forceTimerOnce();
      assertEquals( 2, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "c0", message.findNotifyProperty( "dt#c1", "DropAccept", "source" ) );
      assertEquals( 32, message.findNotifyProperty( "dt#c1", "DropAccept", "x" ) );
      assertEquals( 16, message.findNotifyProperty( "dt#c1", "DropAccept", "y" ) );
      assertNotNull( message.findNotifyProperty( "ds#c0", "DragEnd" ) );
    },

    testTreeInsertAfterIndicator : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var target = this.createDropTargetWithTree();
      var tree = target.control;
      var item0 = this.createTreeItem( 1, tree, tree );
      this.createTreeItem( 2, tree, item0 );
      var source = this.createDragSource();
      item0.setExpanded( true );
      TestUtil.flush();
      var sourceNode = source.control._getTargetNode();
      var targetNode = tree._rowContainer.getRow( 1 ).$el.get( 0 );
      var treeNode = tree.getElement();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      // Over
      TestUtil.fakeMouseEventDOM( treeNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( treeNode, "mousemove", leftButton );
      assertEquals( tree, dndSupport._currentDropControl );
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      dndSupport.setFeedback( tree, [ "FEEDBACK_INSERT_AFTER" ] );
      assertNotNull( dndSupport._dropFeedbackRenderer );
      TestUtil.flush();
      var indicator = dndSupport._dropFeedbackRenderer._insertIndicator;
      assertNotNull( indicator );
      assertIdentical( tree._getTargetNode(), indicator.parentNode );
      var bounds = TestUtil.getElementBounds( indicator );
      assertEquals( 31, bounds.top );
      assertEquals( 0, bounds.left );
      assertEquals( 358, bounds.width );
      assertEquals( 2, bounds.height );
      // drop
      TestUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      assertNull( indicator.parentNode );
    },

    testTreeInsertBeforeIndicator : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var target = this.createDropTargetWithTree();
      var tree = target.control;
      var item0 = this.createTreeItem( 1, tree, tree );
      this.createTreeItem( 2, tree, item0 );
      var source = this.createDragSource();
      item0.setExpanded( true );
      TestUtil.flush();
      var sourceNode = source.control._getTargetNode();
      var targetNode = tree._rowContainer.getRow( 1 ).$el.get( 0 );
      var treeNode = tree.getElement();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      // Over
      TestUtil.fakeMouseEventDOM( treeNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( treeNode, "mousemove", leftButton );
      assertEquals( tree, dndSupport._currentDropControl );
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      dndSupport.setFeedback( tree, [ "FEEDBACK_INSERT_BEFORE" ] );
      assertNotNull( dndSupport._dropFeedbackRenderer );
      TestUtil.flush();
      var indicator = dndSupport._dropFeedbackRenderer._insertIndicator;
      assertNotNull( indicator );
      assertIdentical( tree._getTargetNode(), indicator.parentNode );
      var bounds = TestUtil.getElementBounds( indicator );
      assertEquals( 15, bounds.top );
      assertEquals( 0, bounds.left );
      assertEquals( 358, bounds.width );
      assertEquals( 2, bounds.height );
      // drop
      TestUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      assertNull( indicator.parentNode );
    },

    testTreeFeedbackSelect : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var target = this.createDropTargetWithTree();
      var tree = target.control;
      var item0 = this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 1, tree, item0 );
      var source = this.createDragSource();
      item0.setExpanded( true );
      TestUtil.flush();
      var sourceNode = source.control._getTargetNode();
      var targetNode = tree._rowContainer.getRow( 1 ).$el.get( 0 );
      var treeNode = tree.getElement();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      // Over
      TestUtil.fakeMouseEventDOM( treeNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( treeNode, "mousemove", leftButton );
      assertEquals( tree, dndSupport._currentDropControl );
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      dndSupport.setFeedback( tree, [ "FEEDBACK_SELECT" ] );
      assertNotNull( dndSupport._dropFeedbackRenderer );
      TestUtil.flush();
      var row = tree._rowContainer.getRow( 1 );
      assertTrue( row.hasState( "dnd_selected") );
      assertEquals( "transparent", row.$el.css( "backgroundColor" ) ); // hover disabled
      assertTrue( row.$overlay.css( "backgroundColor" ) !== "" );
      // drop
      TestUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton );
      assertTrue( row.$overlay.css( "display" ) == "none" );
      // NOTE: hover effect temporarily disabled on trees without FULL_SELECTION
      //assertEquals( "#b5b5b5", tree._rowcontainer.$rows.prop( "children" )[ 1 ].getBackgroundColor() );
    },

    testTreeFeedbackExpand : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var target = this.createDropTargetWithTree();
      var tree = target.control;
      var item0 = this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 0, tree, item0 );
      var source = this.createDragSource();
      TestUtil.flush();
      var sourceNode = source.control._getTargetNode();
      var targetNode = tree._rowContainer.getRow( 0 ).$el.get( 0 );
      var treeNode = tree.getElement();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      assertFalse( item0.isExpanded() );
      // Over
      TestUtil.fakeMouseEventDOM( treeNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( treeNode, "mousemove", leftButton );
      assertEquals( tree, dndSupport._currentDropControl );
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      dndSupport.setFeedback( tree, [ "FEEDBACK_EXPAND" ] );
      assertNotNull( dndSupport._dropFeedbackRenderer );
      assertNotNull( dndSupport._dropFeedbackRenderer._expandTimer );
      TestUtil.clearTimerOnceLog();
      TestUtil.forceInterval( dndSupport._dropFeedbackRenderer._expandTimer );
      TestUtil.flush();
      TestUtil.forceTimerOnce(); // Tree uses timer for in method "open"
      assertTrue( item0.isExpanded() );
    },

    testTreeFeedbackScrollDown : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      var tree = this.createDropTargetWithTree().control;
      this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 1, tree, tree );
      this.createTreeItem( 2, tree, tree );
      this.createTreeItem( 3, tree, tree );
      var source = this.createDragSource();
      TestUtil.flush();
      var sourceNode = source.control._getTargetNode();
      var targetNode = tree._rowContainer.getRow( 1 ).$el.get( 0 );
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( document.body, "mousemove", leftButton, 25, 15 );
      TestUtil.fakeMouseEventDOM( tree.getElement(), "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( tree.getElement(), "mousemove", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertIdentical( tree._rowContainer.getRow( 1 ), dndSupport._getCurrentFeedbackTarget() );
      assertEquals( 0, tree.getTopItemIndex() );

      dndSupport.setFeedback( tree, [ "FEEDBACK_SCROLL" ] );
      TestUtil.clearTimerOnceLog();
      TestUtil.forceInterval( dndSupport._dropFeedbackRenderer._scrollTimer );
      TestUtil.flush();
      TestUtil.forceTimerOnce();
      TestUtil.forceInterval( dndSupport._dropFeedbackRenderer._scrollTimer );
      TestUtil.flush();

      assertEquals( 2, tree.getTopItemIndex() );
      TestUtil.forceTimerOnce();
      TestUtil.fakeMouseEventDOM( tree.getElement(), "mouseup", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
    },

    testTreeFeedbackScrollUp : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      var tree = this.createDropTargetWithTree().control;
      this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 1, tree, tree );
      this.createTreeItem( 2, tree, tree );
      this.createTreeItem( 3, tree, tree );
      var source = this.createDragSource();
      tree.setTopItemIndex( 2 );
      TestUtil.flush();
      var sourceNode = source.control._getTargetNode();
      var targetNode = tree._rowContainer.getRow( 0 ).$el.get( 0 );
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( document.body, "mousemove", leftButton, 25, 15 );
      TestUtil.fakeMouseEventDOM( tree.getElement(), "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( tree.getElement(), "mousemove", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertIdentical( tree._rowContainer.getRow( 0 ), dndSupport._getCurrentFeedbackTarget() );
      assertEquals( 2, tree.getTopItemIndex() );

      dndSupport.setFeedback( tree, [ "FEEDBACK_SCROLL" ] );
      TestUtil.clearTimerOnceLog();
      TestUtil.forceInterval( dndSupport._dropFeedbackRenderer._scrollTimer );
      TestUtil.flush();
      TestUtil.forceTimerOnce();
      TestUtil.forceInterval( dndSupport._dropFeedbackRenderer._scrollTimer );
      TestUtil.flush();

      assertEquals( 0, tree.getTopItemIndex() );
      TestUtil.forceTimerOnce();
      TestUtil.fakeMouseEventDOM( tree.getElement(), "mouseup", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
    },

    testTreeFeedbackScrollDoesNotScroll : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      var tree = this.createDropTargetWithTree().control;
      tree.setHeight( 70 );
      this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 1, tree, tree );
      this.createTreeItem( 2, tree, tree );
      this.createTreeItem( 3, tree, tree );
      this.createTreeItem( 4, tree, tree );
      this.createTreeItem( 5, tree, tree );
      var source = this.createDragSource();
      TestUtil.flush();
      var sourceNode = source.control._getTargetNode();
      var targetNode = tree._rowContainer.getRow( 2 ).$el.get( 0 );
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( document.body, "mousemove", leftButton, 25, 15 );
      TestUtil.fakeMouseEventDOM( tree.getElement(), "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( tree.getElement(), "mousemove", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertIdentical( tree._rowContainer.getRow( 2 ), dndSupport._getCurrentFeedbackTarget() );
      assertEquals( 0, tree.getTopItemIndex() );

      dndSupport.setFeedback( tree, [ "FEEDBACK_SCROLL" ] );
      TestUtil.clearTimerOnceLog();
      TestUtil.forceInterval( dndSupport._dropFeedbackRenderer._scrollTimer );
      TestUtil.flush();

      assertEquals( 0, tree.getTopItemIndex() );
      TestUtil.forceTimerOnce();
      TestUtil.fakeMouseEventDOM( tree.getElement(), "mouseup", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
    },

    testTreeFeedbackScroll_EmptyRow : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      var target = this.createDropTargetWithTree();
      var tree = target.control;
      this.createTreeItem( 0, tree, tree );
      var source = this.createDragSource();
      TestUtil.flush();
      var sourceNode = source.control._getTargetNode();
      var targetNode = tree._rowContainer.getRow( 1 ).$el.get( 0 );
      var treeNode = tree.getElement();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      // Over tree
      TestUtil.fakeMouseEventDOM( treeNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( treeNode, "mousemove", leftButton );
      // over row 2 (empty)
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertIdentical( tree._rowContainer.getRow( 1 ), dndSupport._getCurrentFeedbackTarget() );
      dndSupport.setFeedback( tree, [ "FEEDBACK_SCROLL" ] );
      TestUtil.forceInterval( dndSupport._dropFeedbackRenderer._scrollTimer );
      TestUtil.flush();

      assertEquals( 0, tree._topItemIndex );

      TestUtil.fakeMouseEventDOM( treeNode, "mouseup", leftButton );
    },

    testTreeFeedbackScroll_FixedColumnsLeft : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      var target = this.createDropTargetWithTree( true );
      var tree = target.control;
      this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 1, tree, tree );
      this.createTreeItem( 2, tree, tree );
      this.createTreeItem( 3, tree, tree );
      var source = this.createDragSource();
      TestUtil.flush();
      var sourceNode = source.control._getTargetNode();
      var container = tree.getRowContainer().getSubContainer( 0 );
      var targetNode = container.$rows.prop( "children" )[ 1 ];
      var treeNode = tree.getElement();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      // Over tree
      TestUtil.fakeMouseEventDOM( treeNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( treeNode, "mousemove", leftButton );
      // over row 2
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertIdentical( container.getRow( 1 ), dndSupport._getCurrentFeedbackTarget() );
      dndSupport.setFeedback( tree, [ "FEEDBACK_SCROLL" ] );
      TestUtil.forceInterval( dndSupport._dropFeedbackRenderer._scrollTimer );
      TestUtil.flush();

      assertEquals( 1, tree._topItemIndex );

      TestUtil.fakeMouseEventDOM( treeNode, "mouseup", leftButton );
    },

    testTreeFeedbackScroll_FixedColumnsRight : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      var target = this.createDropTargetWithTree( true );
      var tree = target.control;
      this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 1, tree, tree );
      this.createTreeItem( 2, tree, tree );
      this.createTreeItem( 3, tree, tree );
      var source = this.createDragSource();
      TestUtil.flush();
      var sourceNode = source.control._getTargetNode();
      var container = tree.getRowContainer().getSubContainer( 1 );
      var targetNode = container.$rows.prop( "children" )[ 1 ];
      var treeNode = tree.getElement();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      // Over tree
      TestUtil.fakeMouseEventDOM( treeNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( treeNode, "mousemove", leftButton );
      // over row 2
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertIdentical( container.getRow( 1 ), dndSupport._getCurrentFeedbackTarget() );
      dndSupport.setFeedback( tree, [ "FEEDBACK_SCROLL" ] );
      TestUtil.forceInterval( dndSupport._dropFeedbackRenderer._scrollTimer );
      TestUtil.flush();

      assertEquals( 1, tree._topItemIndex );

      TestUtil.fakeMouseEventDOM( treeNode, "mouseup", leftButton );
    },

    testTreeRequestItem : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var target = this.createDropTargetWithTree();
      var tree = target.control;
      this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 1, tree, tree );
      var source = this.createDragSource();
      TestUtil.flush();
      var sourceNode = source.control._getTargetNode();
      var targetNode = tree._rowContainer.getRow( 1 ).$el.get( 0 );
      tree.getElement();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      // over item 2
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      // drop
      TestUtil.initRequestLog();
      TestUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "item1", message.findNotifyProperty( "dt#grid0", "DropAccept", "item" ) );
      tree.destroy();
    },

    testTreeRequestItemOutsideRow : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var target = this.createDropTargetWithTree();
      var tree = target.control;
      this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 1, tree, tree );
      var source = this.createDragSource();
      TestUtil.flush();
      var sourceNode = source.control._getTargetNode();
      var targetNode = tree._rowContainer._getTargetNode();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      // over clientArea
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      // drop
      rwt.remote.Connection.getInstance().send();
      TestUtil.initRequestLog();
      TestUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton );
      TestUtil.forceTimerOnce();
      // TODO [tb] : drop and dragEnd may be sent in two different requests?
      assertTrue( TestUtil.getRequestsSend() >= 1 );
      var message = TestUtil.getMessageObject();
      assertNull( message.findNotifyProperty( "dt#grid0", "DropAccept", "item" ) );
      tree.destroy();
    },

    testTreeRequestItemOutsideRow_Bug_345692 : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var target = this.createDropTargetWithTree();
      var tree = target.control;
      this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 1, tree, tree );
      var source = this.createDragSource();
      TestUtil.flush();
      var sourceNode = source.control._getTargetNode();
      var targetNode = tree._rowContainer.getRow( 0 ).$el.get( 0 );
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      // over treeRow
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      assertIdentical( targetNode, dndSupport._currentTargetElement );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertTrue( dndSupport._isEventScheduled( "DragEnter" ) );
      dndSupport._cancelEvent( "DragEnter" );
      dndSupport._cancelEvent( "DragOver" );
      // over clientArea
      // NOTE: IE may fire mousemove before mouseover, See Bug 345692
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton );
      assertFalse( dndSupport._currentTargetElement instanceof rwt.widgets.base.GridRow );
      TestUtil.fakeMouseEventDOM( doc, "mouseover", leftButton );
      TestUtil.forceTimerOnce();
      assertEquals( 2, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject( 1 );
      assertNull( message.findNotifyProperty( "dt#grid0", "DragLeave", "item" ) );
      tree.destroy();
    },

    testTreeRequestItemIsSourceItem : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      var target = this.createDropTargetWithTree();
      var tree = target.control;
      // Target is also source:
      this.createDragSource( tree );
      this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 1, tree, tree );
      TestUtil.flush();
      var itemNode0 = tree._rowContainer.getRow( 0 ).$el.get( 0 );
      var itemNode1 = tree._rowContainer.getRow( 1 ).$el.get( 0 );
      // drag
      TestUtil.fakeMouseEventDOM( itemNode0, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( itemNode0, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      // over next item
      TestUtil.fakeMouseEventDOM( itemNode1, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( itemNode1, "mousemove", leftButton );
      rwt.remote.Connection.getInstance().send();
      TestUtil.initRequestLog();
      TestUtil.clearTimerOnceLog();
      // over itself
      TestUtil.fakeMouseEventDOM( itemNode0, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( itemNode0, "mousemove", leftButton );
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "item0", message.findNotifyProperty( "dt#grid0", "DragOver", "item" ) );
      // drop
      TestUtil.fakeMouseEventDOM( itemNode1, "mouseup", leftButton );
      tree.destroy();
    },

    testTreeDragEmptyRow: function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      var target = this.createDropTargetWithTree();
      var tree = target.control;
      // Target is also source:
      this.createDragSource( tree );
      TestUtil.flush();
      var itemNode0 = tree._rowContainer.getRow( 0 ).$el.get( 0 );
      var itemNode1 = tree._rowContainer.getRow( 1 ).$el.get( 0 );
      // drag
      TestUtil.initRequestLog();
      TestUtil.clearTimerOnceLog();
      TestUtil.fakeMouseEventDOM( itemNode0, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( itemNode0, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      assertEquals( 2, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "ds#grid0", "DragStart" ) );
      var message = TestUtil.getMessageObject( 1 );
      assertNull( message.findNotifyProperty( "dt#grid0", "DragEnter", "item" ) );
      // drop
      TestUtil.fakeMouseEventDOM( itemNode1, "mouseup", leftButton );
      tree.destroy();
    },

    testFeedbackWidgetTree : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      var tree = this.createTree();
      this.createDragSource( tree );
      this.createTreeItem( 0, tree, tree );
      TestUtil.flush();
      var sourceNode = tree._rowContainer.getRow( 0 ).$el.get( 0 );
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      TestUtil.flush();
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      var widget = dndSupport._dragFeedbackWidget;
      assertTrue( widget instanceof rwt.widgets.base.MultiCellWidget );
      assertIdentical( widget, dndHandler.__feedbackWidget );
      assertEquals( "text0", widget.getCellContent( 1 ) );
      tree.destroy();
    },

    testFeedbackWidgetEscape : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      var tree = this.createTree();
      this.createDragSource( tree );
      this.createTreeItem( 0, tree, tree ).setTexts( [ "te&st" ] );
      TestUtil.flush();
      var sourceNode = tree._rowContainer.getRow( 0 ).$el.get( 0 );

      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( document.body, "mousemove", leftButton, 25, 15 );
      tree._scheduleUpdate(); // rendering that could crash
      TestUtil.flush();

      var widget = dndSupport._dragFeedbackWidget;
      assertEquals( "te&amp;st", widget.getCellContent( 1 ) );
      tree.destroy();
    },

    testFeedbackWidgetTreeWithImage : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      var tree = this.createTree();
      this.createDragSource( tree );
      var item0 = this.createTreeItem( 0, tree, tree );
      item0.setImages( [ [ "bla.jpg", 10, 10 ] ] );
      TestUtil.flush();
      var sourceNode = tree._rowContainer.getRow( 0 ).$el.get( 0 );
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      TestUtil.flush();
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      var widget = dndSupport._dragFeedbackWidget;
      assertTrue( widget instanceof rwt.widgets.base.MultiCellWidget );
      assertIdentical( widget, dndHandler.__feedbackWidget );
      assertEquals( "bla.jpg", widget.getCellContent( 0 ) );
      assertEquals( [ 12, 16 ], widget.getCellDimension( 0 ) );
      assertEquals( "text0", widget.getCellContent( 1 ) );
      tree.destroy();
    },

    testOperationChanged : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createDragSource();
      var target = this.createDropTarget();
      var sourceNode = source.control._getTargetNode();
      var targetNode = target.control._getTargetNode();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.clearRequestLog();
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "ds#c0", "DragStart" ) );
      TestUtil.clearRequestLog();
      TestUtil.clearTimerOnceLog();
      // Over
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "dt#c1", "DragEnter" ) );
      assertNull( message.findNotifyOperation( "dt#c1", "DragOperationChanged" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      // Move
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 33, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "dt#c1", "DragOver" ) );
      assertNull( message.findNotifyOperation( "dt#c1", "DragOperationChanged" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      // NOTE : Currently the only way to test the operationChanged event,
      //        since TestUtil can not yet fake modifiers in domEvents
      dndSupport._setAction( "none", {} );
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "dt#c1", "DragOperationChanged" ) );
      assertEquals( "none", message.findNotifyProperty( "dt#c1", "DragOperationChanged", "operation" ) );
    },

    testCancelDragFromServerSide : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createDragSource();
      this.createDragSourceByProtocol( "w123", "c0" );
      var sourceNode = source.control._getTargetNode();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.clearRequestLog();
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w123", "DragStart" ) );

      TestUtil.protocolCall( "w123", "cancel", {} );
      assertNull( dndHandler.__dragCache );

      rwt.remote.MessageProcessor.processOperation( {
        "target" : "w123",
        "action" : "destroy"
      } );
    },

    testEventOrder_withoutLeave : function() {
      var events = {
        "DragEnter" : {},
        "DragOver" : {}
      };

      var expected = [ "DragStart", "DragEnter", "DragOperationChanged", "DragOver", "DragLeave",
                       "DropAccept", "DragEnd" ];
      assertEquals( expected, dndSupport._getEventOrder( events ) );
    },

    testEventOrder_withoutEnter : function() {
      var events = {
        "DragOver" : {},
        "DragLeave" : {}
      };

      var expected = [ "DragStart", "DragEnter", "DragOperationChanged", "DragOver", "DragLeave",
                       "DropAccept", "DragEnd" ];
      assertEquals( expected, dndSupport._getEventOrder( events ) );
    },

    testEventOrder_enterBeforeLeave : function() {
      var events = {
        "DragEnter" : { "param" : { "time" : 1 } },
        "DragLeave" : { "param" : { "time" : 2 } }
      };

      var expected = [ "DragStart", "DragEnter", "DragOperationChanged", "DragOver", "DragLeave",
                       "DropAccept", "DragEnd" ];
      assertEquals( expected, dndSupport._getEventOrder( events ) );
    },

    testEventOrder_leaveBeforeEnter : function() {
      var events = {
        "DragEnter" : { "param" : { "time" : 2 } },
        "DragLeave" : { "param" : { "time" : 1 } }
      };

      var expected = [ "DragStart", "DragLeave", "DragEnter", "DragOperationChanged", "DragOver",
                       "DropAccept", "DragEnd" ];
      assertEquals( expected, dndSupport._getEventOrder( events ) );
    },

    /////////
    // Helper

    createDragSourceByProtocol : function( id, controlId ) {
      Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.DragSource",
        "properties" : {
          "control" : controlId,
          "style" : [ "DROP_COPY", "DROP_MOVE" ],
          "transfer" : [ "default" ]
        }
      } );
      TestUtil.protocolListen( id, { "DragStart" : true, "DragEnd" : true } );
      return rwt.remote.ObjectRegistry.getObject( id );
    },

    createDropTargetWithTree : function( fixedColumns ) {
      var argsMap = {
        "appearance" : "tree",
        "selectionPadding" : [ 2, 2 ]
      };
      if( fixedColumns ) {
        argsMap[ "splitContainer" ] = true;
      }
      TestUtil.fakeAppearance( "tree-row",  {
        style : function( states ) {
          return {
            "background" : states.over ? "#b5b5b5" : "undefined",
            "backgroundGradient" : null,
            "backgroundImage" : null,
            "foreground" : "undefined",
            "checkBox" : null
          };
        }
      } );
      TestUtil.fakeAppearance( "tree-row-overlay",  {
        style : function( states ) {
          return {
            "background" : states.selected ? "blue" : "undefined",
            "backgroundGradient" : null,
            "backgroundImage" : null,
            "foreground" : "undefined",
            "checkBox" : null
          };
        }
      } );
      var tree = new rwt.widgets.Grid( argsMap );
      var gridHandler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Grid" );
      var barHandler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.ScrollBar" );
      rwt.remote.ObjectRegistry.add( "grid0", tree, gridHandler );
      rwt.remote.ObjectRegistry.add( "grid0#vscroll", tree.getVerticalBar(), barHandler );
      rwt.remote.ObjectRegistry.add( "grid0#hscroll", tree.getHorizontalBar(), barHandler );
      tree.setUserData( "isControl", true );
      tree.setSpace( 13, 364, 27, 45 );
      tree.setItemMetrics( 0, 0, 500, 0, 0, 0, 500 );
      tree.addToDocument();
      return this.createDropTarget( tree );
    },

    createDragSource : function( control ) {
      var dragControl = control;
      if( !control ) {
        var dragControl = new rwt.widgets.base.Terminator();
        dragControl.setLocation( 10, 10 );
        dragControl.setDimension( 10, 10 );
        dragControl.addToDocument();
        rwt.remote.WidgetManager.getInstance().add( dragControl, "c0" );
        dragControl.setUserData( "isControl", true );
        TestUtil.flush();
      }
      var controlId = rwt.remote.ObjectRegistry.getId( dragControl );
      var operations = [ "DROP_COPY", "DROP_MOVE", "DROP_LINK" ];
      var dragSource = new rwt.widgets.DragSource( dragControl, operations );
      rwt.remote.ObjectRegistry.add( "ds#" + controlId, dragSource );
      this.setHasDragDropListeners( dragSource, true );
      dragSource.setTransfer( [ "default" ] );
      return dragSource;
    },

    createDropTarget : function( control ) {
      var dropControl = control;
      if( !control ) {
        var dropControl = new rwt.widgets.base.Terminator();
        dropControl.setLocation( 30, 10 );
        dropControl.setDimension( 10, 10 );
        dropControl.addToDocument();
        rwt.remote.WidgetManager.getInstance().add( dropControl, "c1" );
        dropControl.setUserData( "isControl", true );
        TestUtil.flush();
      }
      var controlId = rwt.remote.ObjectRegistry.getId( dropControl );
      var operations = [ "DROP_COPY", "DROP_MOVE", "DROP_LINK" ];
      var dropTarget = new rwt.widgets.DropTarget( dropControl, operations );
      rwt.remote.ObjectRegistry.add( "dt#" + controlId, dropTarget );
      this.setHasDragDropListeners( dropTarget, true );
      dropTarget.setTransfer( [ "default" ] );
      return dropTarget;
    },

    setHasDragDropListeners : function( widget, value ) {
      this.setHasListener( widget, "DragStart", value );
      this.setHasListener( widget, "DragEnd", value );
      this.setHasListener( widget, "DragEnter", value );
      this.setHasListener( widget, "DragOver", value );
      this.setHasListener( widget, "DragLeave", value );
      this.setHasListener( widget, "DragOperationChanged", value );
      this.setHasListener( widget, "DropAccept", value );
    },

    setHasListener : function( widget, type, value ) {
      var remoteObject = rwt.remote.RemoteObjectFactory.getRemoteObject( widget );
      remoteObject._.listen[ type ] = value;
    },

    createTree : function() {
      var argsMap = {
        "appearance" : "tree",
        "selectionPadding" : [ 2, 2 ]
      };
      var tree = new rwt.widgets.Grid( argsMap );
      rwt.remote.WidgetManager.getInstance().add( tree, "grid0" );
      tree.setUserData( "isControl", true );
      tree.setSpace( 13, 364, 27, 30 );
      tree.setItemMetrics( 0, 50, 40, 50, 12, 65, 12 );
      tree.addToDocument();
      return tree;
    },

    createTreeItem : function( itemNr, tree, parent ) {
      var parentItem = rwt.widgets.GridItem._getItem( parent );
      parentItem.setItemCount( itemNr + 1 );
      var item = new rwt.widgets.GridItem( parentItem, itemNr );
      var wm = rwt.remote.WidgetManager.getInstance();
      wm.add( item, "item" + itemNr, false );
      item.setTexts( [ "text" + itemNr ] );
      item.setImages( [ null ] );
      return item;
    }

  }

} );

}());
