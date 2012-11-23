/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others. All rights reserved.
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

    TARGETPLATFORM : [ "win", "mac", "unix", "other" ], // touch devices don't support DND (yet)

    testCreateDragSourceByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var ObjectManager = rwt.protocol.ObjectRegistry;
      TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "parent" : "w2",
          "style" : [ "PUSH" ]
        }
      } );

      processor.processOperation( {
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var ObjectManager = rwt.protocol.ObjectRegistry;
      TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "parent" : "w2",
          "style" : [ "PUSH" ]
        }
      } );
      processor.processOperation( {
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

      processor.processOperation( {
        "target" : "w4",
        "action" : "destroy"
      } );
      assertFalse( dndSupport.isDragSource( button ) );
      button.destroy();
    },

    testDisposeDragSourceWithControlByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var ObjectManager = rwt.protocol.ObjectRegistry;
      TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "parent" : "w2",
          "style" : [ "PUSH" ]
        }
      } );
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.DragSource",
        "properties" : {
          "control" : "w3",
          "style" : [ "DROP_COPY", "DROP_MOVE" ]
        }
      } );
      var button = ObjectManager.getObject( "w3" );

      processor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );

      assertFalse( dndSupport.isDragSource( button ) );
      button.destroy();
    },

    testSetDragSourceTransferByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var ObjectManager = rwt.protocol.ObjectRegistry;
      TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "parent" : "w2",
          "style" : [ "PUSH" ]
        }
      } );

      processor.processOperation( {
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var ObjectManager = rwt.protocol.ObjectRegistry;
      TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "parent" : "w2",
          "style" : [ "PUSH" ]
        }
      } );

      processor.processOperation( {
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var ObjectManager = rwt.protocol.ObjectRegistry;
      TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "parent" : "w2",
          "style" : [ "PUSH" ]
        }
      } );
      processor.processOperation( {
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

      processor.processOperation( {
        "target" : "w4",
        "action" : "destroy"
      } );
      assertFalse( dndSupport.isDropTarget( button ) );
      button.destroy();
    },

    testDisposeDropTargetWithControlByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var ObjectManager = rwt.protocol.ObjectRegistry;
      TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "parent" : "w2",
          "style" : [ "PUSH" ]
        }
      } );
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.DropTarget",
        "properties" : {
          "control" : "w3",
          "style" : [ "DROP_COPY", "DROP_MOVE" ]
        }
      } );
      var button = ObjectManager.getObject( "w3" );

      processor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );

      assertFalse( dndSupport.isDropTarget( button ) );
      button.destroy();
    },

    testSetDropTargetTransferByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var ObjectManager = rwt.protocol.ObjectRegistry;
      TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "parent" : "w2",
          "style" : [ "PUSH" ]
        }
      } );

      processor.processOperation( {
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      TestUtil.flush();
    },

    testHashMaps : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      TestUtil.flush();
    },

    // TODO [tb] : fix order within tests (asserts at end)
    testDragStartAndFinish : function() {
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var source = this.createSource();
      var node = source._getTargetNode();

      TestUtil.fakeMouseEventDOM( node, "mousedown", leftButton, 11, 11 );
      TestUtil.clearRequestLog();
      TestUtil.fakeMouseEventDOM( node, "mousemove", leftButton, 19, 19 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w11", "dragStart" ) );
      TestUtil.clearRequestLog();
      TestUtil.fakeMouseEventDOM( node, "mouseup", leftButton, 19, 19 );

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w11", "dragFinished" ) );
      assertFalse( dndSupport._blockDrag );
      dndSupport.deregisterDragSource( source );
      source.setParent( null );
      source.destroy();
      TestUtil.flush();
    },

    testDragEndPropagation : function() {
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      var log = [];
      TestUtil.getDocument().addEventListener( "dragend", function( event ) {
        log.push( event );
      } );
      var source = this.createSource();
      var node = source._getTargetNode();

      TestUtil.fakeMouseEventDOM( node, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( node, "mousemove", leftButton, 19, 19 );
      TestUtil.fakeMouseEventDOM( node, "mouseup", leftButton, 19, 19 );

      assertEquals( 0, log.length );
      dndSupport.deregisterDragSource( source );
      source.destroy();
      TestUtil.flush();
    },

    testDragStartAndCancel : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var source = this.createSource();
      var node = source._getTargetNode();

      TestUtil.fakeMouseEventDOM( node, "mousedown", leftButton, 11, 11 );
      TestUtil.clearRequestLog();
      TestUtil.fakeMouseEventDOM( node, "mousemove", leftButton, 19, 19 );
      dndSupport.cancel();

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w11", "dragStart" ) );
      assertNull( dndHandler.__dragCache );
      assertEquals( 1, TestUtil.getRequestsSend() ); // no dragFinished
      assertFalse( dndSupport._blockDrag );
      dndSupport.deregisterDragSource( source );
      source.setParent( null );
      source.destroy();
      TestUtil.flush();
    },

    testDragEnterOverLeave : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.clearRequestLog();
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( 25, message.findNotifyProperty( "w11", "dragStart", "x" ) );
      assertEquals( 15, message.findNotifyProperty( "w11", "dragStart", "y" ) );
      TestUtil.clearRequestLog();
      TestUtil.clearTimerOnceLog();
      // Over
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      message = TestUtil.getMessageObject();
      assertEquals( "w11", message.findNotifyProperty( "w2", "dragEnter", "source" ) );
      assertEquals( 32, message.findNotifyProperty( "w2", "dragEnter", "x" ) );
      assertEquals( 15, message.findNotifyProperty( "w2", "dragEnter", "y" ) );
      assertEquals( "w11", message.findNotifyProperty( "w2", "dragOver", "source" ) );
      assertEquals( 32, message.findNotifyProperty( "w2", "dragOver", "x" ) );
      assertEquals( 15, message.findNotifyProperty( "w2", "dragOver", "y" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      // Move
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 33, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      message = TestUtil.getMessageObject();
      assertEquals( "w11", message.findNotifyProperty( "w2", "dragOver", "source" ) );
      assertEquals( 33, message.findNotifyProperty( "w2", "dragOver", "x" ) );
      assertEquals( 15, message.findNotifyProperty( "w2", "dragOver", "y" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      // Out
      TestUtil.fakeMouseEventDOM( targetNode, "mouseout", leftButton, 41, 15 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 42, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      message = TestUtil.getMessageObject();
      assertEquals( "w11", message.findNotifyProperty( "w2", "dragLeave", "source" ) );
      assertEquals( 42, message.findNotifyProperty( "w2", "dragLeave", "x" ) );
      assertEquals( 15, message.findNotifyProperty( "w2", "dragLeave", "y" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      TestUtil.flush();
    },

    // See Bug 301434
    testEnterEventOnDragStart: function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      // request uses SWT-like event-names:
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      target.setLocation( 10, 20 );
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
      TestUtil.clearRequestLog();

      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 19 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 11, 25 );
      TestUtil.forceTimerOnce();

      assertEquals( 2, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject( 0 ).findNotifyOperation( "w11", "dragStart" ) );
      assertNotNull( TestUtil.getMessageObject( 1 ).findNotifyOperation( "w2", "dragEnter" ) );
      assertNotNull( TestUtil.getMessageObject( 1 ).findNotifyOperation( "w2", "dragOver" ) );
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
    },

    // See Bug 301276
    testSetPropertyRetroactively : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
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
      assertEquals( null, message.findNotifyProperty( "w2", "dragEnter", "dataType" ) );
      assertEquals( "move", message.findNotifyProperty( "w2", "dragEnter", "operation" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      // Move
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 33, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      // set properties
      dndSupport.setDataType( target, "76135" );
      dndSupport.setOperationOverwrite( target, "copy" );
      // send event:
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      message = TestUtil.getMessageObject( 0 );
      assertEquals( "copy", message.findNotifyProperty( "w2", "dragOver", "operation" ) );
      assertEquals( 76135, message.findNotifyProperty( "w2", "dragOver", "dataType" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      TestUtil.flush();
    },

    // for Bug 299034
    testStopDropEventPropagation : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      var actions = [ "copy", "move", "alias" ];
      var source = this.createSource();
      var target = this.createTarget();
      var parentTarget = new rwt.widgets.base.Parent();
      parentTarget.setLocation( 10, 10 );
      parentTarget.setDimension( 100, 100 );
      parentTarget.addToDocument();
      org.eclipse.swt.WidgetManager.getInstance().add( parentTarget, "w3" );
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
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      TestUtil.flush();
    },

    testIgnorePassOverTarget : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
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
      assertNull( TestUtil.getMessageObject().findNotifyOperation( "w2", "dragLeave" ) );
      assertNull( TestUtil.getMessageObject().findNotifyOperation( "w2", "dragOver" ) );
      assertNull( TestUtil.getMessageObject().findNotifyOperation( "w2", "dragMove" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      TestUtil.flush();
    },

    testIgnorePassOverTargetAfterLeave : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
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
      assertEquals( "w11", message.findNotifyProperty( "w2", "dragLeave", "source" ) );
      assertEquals( 42, message.findNotifyProperty( "w2", "dragLeave", "x" ) );
      assertEquals( 15, message.findNotifyProperty( "w2", "dragLeave", "y" ) );
      assertNull( message.findNotifyOperation( "w2", "dragOver" ) );
      assertNull( message.findNotifyOperation( "w2", "dragEnter" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      TestUtil.flush();
    },

    testDragStartAndDrop : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.clearRequestLog();
      TestUtil.clearTimerOnceLog();
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyProperty( "w11", "dragStart" ) );
      TestUtil.clearRequestLog();
      TestUtil.clearTimerOnceLog();
      // Over
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyProperty( "w2", "dragEnter" ) );
      assertNotNull( message.findNotifyProperty( "w2", "dragOver" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      // Drop
      TestUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton, 32, 16 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      message = TestUtil.getMessageObject();
      assertEquals( "w11", message.findNotifyProperty( "w2", "dropAccept", "source" ) );
      assertEquals( 32, message.findNotifyProperty( "w2", "dropAccept", "x" ) );
      assertEquals( 16, message.findNotifyProperty( "w2", "dropAccept", "y" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      TestUtil.flush();
    },


    testFakeMouseEvents : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      var log = [];
      source.addEventListener( "mouseout", function( event ) {
        log.push( event );
      }, this );
      target.addEventListener( "mouseover", function( event ) {
        log.push( event );
      }, this );
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( sourceNode, "mousemove", leftButton, 25, 15 );
      assertEquals( 1, log.length );
      assertTrue( log[ 0 ] instanceof qx.event.type.MouseEvent );
      assertEquals( "mouseout", log[ 0 ].getType() );
      // Over
      TestUtil.fakeMouseEventDOM( sourceNode, "mouseout", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      // drop
      TestUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton );
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
      TestUtil.flush();
    },

    testMouseupOutOfDocument : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
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
      rwt.remote.Server.getInstance().send();
      TestUtil.clearRequestLog();
      TestUtil.fakeMouseEventDOM( targetNode, "mousedown", leftButton, 32, 16 );
      // mouseup
      TestUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton, 32, 16 );
      TestUtil.forceTimerOnce();
      assertEquals( 2, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "w11", message.findNotifyProperty( "w2", "dropAccept", "source" ) );
      assertEquals( 32, message.findNotifyProperty( "w2", "dropAccept", "x" ) );
      assertEquals( 16, message.findNotifyProperty( "w2", "dropAccept", "y" ) );
      assertNotNull( message.findNotifyProperty( "w11", "dragFinished" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      TestUtil.flush();
    },

    testTreeInsertAfterIndicator : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var tree = this.createTreeTarget();
      var item0 = this.createTreeItem( 1, tree, tree );
      this.createTreeItem( 2, tree, item0 );
      var source = this.createSource();
      item0.setExpanded( true );
      TestUtil.flush();
      var sourceNode = source._getTargetNode();
      var targetNode = tree._rowContainer._children[ 1 ]._getTargetNode();
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
      assertEquals( tree, dndSupport._currentDropTarget );
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
      if( rwt.client.Client.getEngine() != "mshtml" ) {
        // TODO [tb] : does not succeed in IE unless the debugger is running
        assertNull( indicator.parentNode );
      }
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( tree );
      source.setParent( null );
      source.destroy();
      tree.destroy();
    },

    testTreeInsertBeforeIndicator : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var tree = this.createTreeTarget();
      var item0 = this.createTreeItem( 1, tree, tree );
      this.createTreeItem( 2, tree, item0 );
      var source = this.createSource();
      item0.setExpanded( true );
      TestUtil.flush();
      var sourceNode = source._getTargetNode();
      var targetNode = tree._rowContainer._children[ 1 ]._getTargetNode();
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
      assertEquals( tree, dndSupport._currentDropTarget );
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
      if( rwt.client.Client.getEngine() != "mshtml" ) {
        // TODO [tb] : does not succeed in IE unless the debugger is running
        assertNull( indicator.parentNode );
      }
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( tree );
      source.setParent( null );
      source.destroy();
      tree.destroy();
    },

    testTreeFeedbackSelect : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var tree = this.createTreeTarget();
      var item0 = this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 1, tree, item0 );
      var source = this.createSource();
      item0.setExpanded( true );
      TestUtil.flush();
      var sourceNode = source._getTargetNode();
      var targetNode = tree._rowContainer._children[ 1 ]._getTargetNode();
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
      assertEquals( tree, dndSupport._currentDropTarget );
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      dndSupport.setFeedback( tree, [ "FEEDBACK_SELECT" ] );
      assertNotNull( dndSupport._dropFeedbackRenderer );
      TestUtil.flush();
      var row = tree._rowContainer._children[ 1 ];
      assertTrue( row.hasState( "dnd_selected") );
      assertNull( row.getBackgroundColor() ); // hover disabled
      assertTrue( row._getTargetNode().lastChild.style.backgroundColor !== "" );
      // drop
      TestUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton );
      assertTrue( row._getTargetNode().lastChild.style.display == "none" );
      assertEquals( "#b5b5b5", tree._rowContainer._children[ 1 ].getBackgroundColor() );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( tree );
      source.setParent( null );
      source.destroy();
      tree.destroy();
    },

    testTreeFeedbackExpand : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var tree = this.createTreeTarget();
      var item0 = this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 0, tree, item0 );
      var source = this.createSource();
      TestUtil.flush();
      var sourceNode = source._getTargetNode();
      var targetNode = tree._rowContainer._children[ 0 ]._getTargetNode();
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
      assertEquals( tree, dndSupport._currentDropTarget );
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
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( tree );
      source.setParent( null );
      source.destroy();
      tree.destroy();
    },

    // NOTE [tb] : this tests can fail if when its DNDTest is executed on its own. Reason unkown.
    testTreeFeedbackScroll : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var tree = this.createTreeTarget();
      this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 1, tree, tree );
      this.createTreeItem( 2, tree, tree );
      this.createTreeItem( 3, tree, tree );
      var source = this.createSource();
      TestUtil.flush();
      var sourceNode = source._getTargetNode();
      var targetNode = tree._rowContainer._children[ 1 ]._getTargetNode();
      var treeNode = tree.getElement();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      // Over tree
      TestUtil.fakeMouseEventDOM( treeNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( treeNode, "mousemove", leftButton );
      assertEquals( tree, dndSupport._currentDropTarget );
      // over item 2
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertIdentical( tree._rowContainer._children[ 1 ], dndSupport._getCurrentFeedbackTarget() );
      assertNull( dndSupport._dropFeedbackRenderer );
      dndSupport.setFeedback( tree, [ "FEEDBACK_SCROLL" ] );
      // setting feedback
      var feedback = dndSupport._dropFeedbackRenderer;
      assertNotNull( feedback );
      assertEquals( 0, tree._topItemIndex );
      //assertEquals( 1, feedback._getScrollDirection( 1 ) );
      TestUtil.clearTimerOnceLog();
      // scroll to item 3
      TestUtil.forceInterval( dndSupport._dropFeedbackRenderer._scrollTimer );
      TestUtil.flush();
      assertEquals( 1, tree._topItemIndex );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();

      // scroll to item 4
      TestUtil.forceInterval( dndSupport._dropFeedbackRenderer._scrollTimer );
      TestUtil.flush();
      assertEquals( 2, tree._topItemIndex );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      // drop
      TestUtil.fakeMouseEventDOM( treeNode, "mouseup", leftButton );
      assertNull( dndSupport._dropFeedbackRenderer );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( tree );
      source.setParent( null );
      source.destroy();
      tree.destroy();
    },

    testTreeRequestItem : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var tree = this.createTreeTarget();
      this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 1, tree, tree );
      var source = this.createSource();
      TestUtil.flush();
      var sourceNode = source._getTargetNode();
      var targetNode = tree._rowContainer._children[ 1 ]._getTargetNode();
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
      assertEquals( "w3", message.findNotifyProperty( "w2", "dropAccept", "item" ) );
      source.setParent( null );
      source.destroy();
      tree.destroy();
    },

    testTreeRequestItemOutsideRow : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var tree = this.createTreeTarget();
      this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 1, tree, tree );
      var source = this.createSource();
      TestUtil.flush();
      var sourceNode = source._getTargetNode();
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
      rwt.remote.Server.getInstance().send();
      TestUtil.initRequestLog();
      TestUtil.fakeMouseEventDOM( targetNode, "mouseup", leftButton );
      TestUtil.forceTimerOnce();
      // TODO [tb] : drop and dragEnd may be sent in two different requests?
      assertTrue( TestUtil.getRequestsSend() >= 1 );
      assertNull( TestUtil.getMessageObject().findNotifyProperty( "w2", "dropAccept", "item" ) );
      source.setParent( null );
      source.destroy();
      tree.destroy();
    },

    testTreeRequestItemOutsideRow_Bug_345692 : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var tree = this.createTreeTarget();
      this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 1, tree, tree );
      var source = this.createSource();
      TestUtil.flush();
      var sourceNode = source._getTargetNode();
      var targetNode = tree._rowContainer.getChildren()[ 0 ]._getTargetNode();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      // over treeRow
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton );
      assertTrue( dndSupport._currentTargetWidget instanceof rwt.widgets.base.GridRow );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton );
      assertTrue( dndSupport._isDropTargetEventScheduled( "dragEnter" ) );
      dndSupport._cancelDropTargetEvent( "dragEnter" );
      dndSupport._cancelDropTargetEvent( "dragOver" );
      // over clientArea
      // NOTE: IE may fire mousemove before mouseover, See Bug 345692
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton );
      assertFalse( dndSupport._currentTargetWidget instanceof rwt.widgets.base.GridRow );
      TestUtil.fakeMouseEventDOM( doc, "mouseover", leftButton );
      TestUtil.forceTimerOnce();
      assertEquals( 2, TestUtil.getRequestsSend() );
      assertNull( TestUtil.getMessageObject( 1 ).findNotifyProperty( "w2", "dragLeave", "item" ) );
      source.setParent( null );
      source.destroy();
      tree.destroy();
    },

    testTreeRequestItemIsSourceItem: function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      var tree = this.createTreeTarget();
      // Target is also source:
      var actions = [ "copy", "move", "alias" ];
      dndSupport.registerDragSource( tree, actions );
      dndSupport.setDragSourceTransferTypes( tree, [ "default" ] );
      this.createTreeItem( 0, tree, tree );
      this.createTreeItem( 1, tree, tree );
      var source = this.createSource();
      TestUtil.flush();
      var itemNode0 = tree._rowContainer._children[ 0 ]._getTargetNode();
      var itemNode1 = tree._rowContainer._children[ 1 ]._getTargetNode();
      // drag
      TestUtil.fakeMouseEventDOM( itemNode0, "mousedown", leftButton, 11, 11 );
      TestUtil.fakeMouseEventDOM( itemNode0, "mousemove", leftButton, 25, 15 );
      assertNotNull( dndHandler.__dragCache );
      assertTrue( dndHandler.__dragCache.dragHandlerActive );
      // over next item
      TestUtil.fakeMouseEventDOM( itemNode1, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( itemNode1, "mousemove", leftButton );
      rwt.remote.Server.getInstance().send();
      TestUtil.initRequestLog();
      TestUtil.clearTimerOnceLog();
      // over itself
      TestUtil.fakeMouseEventDOM( itemNode0, "mouseover", leftButton );
      TestUtil.fakeMouseEventDOM( itemNode0, "mousemove", leftButton );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "w2", message.findNotifyProperty( "w2", "dragOver", "item" ) );
      // drop
      TestUtil.fakeMouseEventDOM( itemNode1, "mouseup", leftButton );
      source.setParent( null );
      source.destroy();
      tree.destroy();
    },

    testTreeDragEmptyRow: function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      var tree = this.createTreeTarget();
      // Target is also source:
      var actions = [ "copy", "move", "alias" ];
      dndSupport.registerDragSource( tree, actions );
      dndSupport.setDragSourceTransferTypes( tree, [ "default" ] );
      var source = this.createSource();
      TestUtil.flush();
      var itemNode0 = tree._rowContainer._children[ 0 ]._getTargetNode();
      var itemNode1 = tree._rowContainer._children[ 1 ]._getTargetNode();
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
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w2", "dragStart" ) );
      assertNull( TestUtil.getMessageObject( 1 ).findNotifyProperty( "w2", "dragEnter", "item" ) );
      // drop
      TestUtil.fakeMouseEventDOM( itemNode1, "mouseup", leftButton );
      source.setParent( null );
      source.destroy();
      tree.destroy();
    },

    testFeedbackWidgetTree : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      var argsMap = {
        "appearance" : "tree",
        "selectionPadding" : [ 2, 2 ]
      };
      var tree = new rwt.widgets.Grid( argsMap );
      tree.setItemMetrics( 0, 0, 500, 0, 0, 0, 500 );
      org.eclipse.swt.WidgetManager.getInstance().add( tree, "w2" );
      tree.setUserData( "isControl", true );
      tree.setSpace( 13, 364, 27, 30 );
      tree.addToDocument();
      var actions = [ "copy", "move", "alias" ];
      dndSupport.registerDragSource( tree, actions );
      dndSupport.setDragSourceTransferTypes( tree, [ "default" ] );
      this.createTreeItem( 0, tree, tree );
      TestUtil.flush();
      var sourceNode = tree._rowContainer._children[ 0 ]._getTargetNode();
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
      assertEquals( "text2", widget.getCellContent( 1 ) );
      dndSupport.cancel();
      assertEquals( null, dndHandler.__feedbackWidget );
      dndSupport.deregisterDragSource( tree );
      tree.setParent( null );
      tree.destroy();
      TestUtil.flush();
    },

    testFeedbackWidgetTreeWithImage : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      var argsMap = {
        "appearance" : "tree",
        "selectionPadding" : [ 2, 2 ]
      };
      var tree = new rwt.widgets.Grid( argsMap );
      org.eclipse.swt.WidgetManager.getInstance().add( tree, "w2" );
      tree.setUserData( "isControl", true );
      tree.setSpace( 13, 364, 27, 30 );
      tree.setItemMetrics( 0, 50, 40, 50, 12, 65, 12 );
      tree.addToDocument();
      var actions = [ "copy", "move", "alias" ];
      dndSupport.registerDragSource( tree, actions );
      dndSupport.setDragSourceTransferTypes( tree, [ "default" ] );
      var item0 = this.createTreeItem( 0, tree, tree );
      item0.setImages( [ "bla.jpg" ] );
      TestUtil.flush();
      var sourceNode = tree._rowContainer._children[ 0 ]._getTargetNode();
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
      assertEquals( "text2", widget.getCellContent( 1 ) );
      dndSupport.cancel();
      assertEquals( null, dndHandler.__feedbackWidget );
      dndSupport.deregisterDragSource( tree );
      tree.setParent( null );
      tree.destroy();
      TestUtil.flush();
    },

    testOperationChanged : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      dndHandler.__dragCache = null;
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createSource();
      var target = this.createTarget();
      var sourceNode = source._getTargetNode();
      var targetNode = target._getTargetNode();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.clearRequestLog();
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w11", "dragStart" ) );
      TestUtil.clearRequestLog();
      TestUtil.clearTimerOnceLog();
      // Over
      TestUtil.fakeMouseEventDOM( targetNode, "mouseover", leftButton, 31, 15 );
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 32, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w2", "dragEnter" ) );
      assertNull( message.findNotifyOperation( "w2", "dragOperationChanged" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      // Move
      TestUtil.fakeMouseEventDOM( targetNode, "mousemove", leftButton, 33, 15 );
      assertTrue( TestUtil.getTimerOnceLog().length > 0 );
      TestUtil.forceTimerOnce();
      message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w2", "dragOver" ) );
      assertNull( message.findNotifyOperation( "w2", "dragOperationChanged" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      // NOTE : Currently the only way to test the operationChanged event,
      //        since TestUtil can not yet fake modifiers in domEvents
      dndSupport._setAction( "none", {} );
      TestUtil.forceTimerOnce();
      assertEquals( 1, TestUtil.getRequestsSend() );
      message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w2", "dragOperationChanged" ) );
      assertEquals( "none", message.findNotifyProperty( "w2", "dragOperationChanged", "operation" ) );
      TestUtil.clearTimerOnceLog();
      TestUtil.clearRequestLog();
      dndSupport.cancel();
      dndSupport.deregisterDragSource( source );
      dndSupport.deregisterDropTarget( target );
      source.setParent( null );
      source.destroy();
      target.setParent( null );
      target.destroy();
      TestUtil.flush();
    },

    testCancelDragFromServerSide : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      dndHandler.__dragCache = null;
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      var source = this.createSource();
      var dragSource = this.createDragSourceByProtocol( "w123", "w11" );
      var sourceNode = source._getTargetNode();
      var doc = document.body;
      // drag
      TestUtil.fakeMouseEventDOM( sourceNode, "mousedown", leftButton, 11, 11 );
      TestUtil.clearRequestLog();
      TestUtil.fakeMouseEventDOM( doc, "mousemove", leftButton, 25, 15 );
      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w11", "dragStart" ) );

      TestUtil.protocolCall( "w123", "cancel", {} );
      var dndHandler = qx.event.handler.DragAndDropHandler.getInstance();
      assertNull( dndHandler.__dragCache );

      rwt.protocol.MessageProcessor.processOperation( {
        "target" : "w123",
        "action" : "destroy"
      } );
      dndSupport.deregisterDragSource( source );
      source.setParent( null );
      source.destroy();
      TestUtil.flush();
    },

    /////////
    // Helper

    createDragSourceByProtocol : function( id, controlId ) {
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.DragSource",
        "properties" : {
          "control" : controlId,
          "style" : [ "DROP_COPY", "DROP_MOVE" ],
          "transfer" : [ "default" ]
        }
      } );
      return rwt.protocol.ObjectRegistry.getObject( id );
    },

    createSource : function() {
      var result = new rwt.widgets.base.Atom();
      result.setLocation( 10, 10 );
      result.setDimension( 10, 10 );
      result.addToDocument();
      org.eclipse.swt.WidgetManager.getInstance().add( result, "w11" );
      result.setUserData( "isControl", true );
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var actions = [ "copy", "move", "alias" ];
      dndSupport.registerDragSource( result, actions );
      dndSupport.setDragSourceTransferTypes( result, [ "default" ] );
      org.eclipse.rwt.test.fixture.TestUtil.flush();
      return result;
    },

    createTarget : function() {
      var result = new rwt.widgets.base.Atom();
      result.setLocation( 30, 10 );
      result.setDimension( 10, 10 );
      result.addToDocument();
      org.eclipse.swt.WidgetManager.getInstance().add( result, "w2" );
      result.setUserData( "isControl", true );
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var actions = [ "copy", "move", "alias" ];
      dndSupport.registerDropTarget( result, actions );
      dndSupport.setDropTargetTransferTypes( result, [ "default" ] );
      org.eclipse.rwt.test.fixture.TestUtil.flush();
      return result;
    },

    createTreeTarget : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
      var argsMap = {
        "appearance" : "tree",
        "selectionPadding" : [ 2, 2 ]
      };
      TestUtil.fakeAppearance( "tree-row",  {
        style : function( states ) {
          return {
            "itemBackground" : states.over ? "#b5b5b5" : "undefined",
            "itemBackgroundGradient" : "undefined",
            "itemBackgroundImage" : "undefined",
            "itemForeground" : "undefined",
            "checkBox" : null,
            "overlayBackground" : states.selected ? "blue" : "undefined",
            "overlayBackgroundImage" : null,
            "overlayBackgroundGradient" : null,
            "overlayForeground" : "undefined"
          };
        }
      } );
      var tree = new rwt.widgets.Grid( argsMap );
      rwt.protocol.ObjectRegistry.add( "w2", tree );
      rwt.protocol.ObjectRegistry.add( "w2_vscroll", tree.getVerticalBar() );
      rwt.protocol.ObjectRegistry.add( "w2_hscroll", tree.getHorizontalBar() );
      tree.setUserData( "isControl", true );
      tree.setSpace( 13, 364, 27, 30 );
      tree.setItemMetrics( 0, 0, 500, 0, 0, 0, 500 );
      tree.addToDocument();
      var actions = [ "copy", "move", "alias" ];
      dndSupport.registerDropTarget( tree, actions );
      dndSupport.setDropTargetTransferTypes( tree, [ "default" ] );
      return tree;
    },

    createTreeItem : function( itemNr, tree, parent ) {
      var nr = itemNr + 2;
      var parentItem = rwt.widgets.GridItem._getItem( parent );
      parentItem.setItemCount( itemNr + 1 );
      var item = new rwt.widgets.GridItem( parentItem, itemNr );
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      wm.add( item, "w" + nr, false );
      item.setTexts( [ "text" + nr ] );
      item.setImages( [ null ] );
      return item;
    },

    createTable : function() {
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var table = new rwt.widgets.Grid( "w2", "|multi" );
      wm.add( table, "w2", true );
      table.addToDocument();
      table.setSpace( 13, 360, 27, 147 );
      table.setItemHeight( 17 );
      table.setItemMetrics( 0, 0, 105, 2, 0, 2, 101 );
      table.setItemMetrics( 1, 105, 104, 107, 0, 107, 100 );
      table.updateRows();
      table.setItemCount( 4 );
      table.setScrollBarsVisibile( false, true );
      var w = new rwt.widgets.GridColumn( table );
      wm.add( w, "w3", false );
      w.setLabel( "" );
      w.setWidth( 105 );
      w = new rwt.widgets.GridColumn( table );
      wm.add( w, "w4", false );
      w.setLabel( "" );
      w.setLeft( 105 );
      w.setWidth( 104 );
      w.setZIndex( 299 );
      w = new rwt.widgets.GridItem( table, 0 );
      wm.add( w, "w5", false );
      w.setTexts( [ "Item name 1", "Item Value 1" ] );
      w.update();
      w = new rwt.widgets.GridItem( table, 0 );
      wm.add( w, "w6", false );
      w.setTexts( [ "Item name 2", "Item Value 2" ] );
      w.update();
      w = new rwt.widgets.GridItem( table, 0 );
      wm.add( w, "w7", false );
      w.setTexts( [ "Item name 3", "Item Value 3" ] );
      w.update();
      w = new rwt.widgets.GridItem( table, 0 );
      wm.add( w, "w8", false );
      w.setTexts( [ "Item name 4", "Item Value 4" ] );
      w.update();
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.flush(); // creates table
      TestUtil.flush(); // creates rows
      return table;
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