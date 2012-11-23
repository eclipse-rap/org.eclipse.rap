/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
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

qx.Class.define( "org.eclipse.rwt.test.tests.CoolItemTest", {

  extend : qx.core.Object,

  members : {

    testCreateCoolItemByProtocol : function() {
      var bar = this._createCoolBar();
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CoolItem",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var item = rwt.protocol.ObjectRegistry.getObject( "w3" );
      assertTrue( item instanceof rwt.widgets.CoolItem );
      assertIdentical( bar, item.getParent() );
      assertEquals( 0, item.getMinWidth() );
      assertEquals( 0, item.getMinHeight() );
      assertEquals( "horizontal", item._orientation );
      bar.destroy();
    },

    testCreateCoolItemByProtocolVertical : function() {
      var bar = this._createCoolBar();
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CoolItem",
        "properties" : {
          "style" : [ "VERTICAL" ],
          "parent" : "w2"
        }
      } );
      var item = rwt.protocol.ObjectRegistry.getObject( "w3" );
      assertTrue( item instanceof rwt.widgets.CoolItem );
      assertIdentical( bar, item.getParent() );
      assertEquals( 0, item.getMinWidth() );
      assertEquals( 0, item.getMinHeight() );
      assertEquals( "vertical", item._orientation );
      bar.destroy();
    },

    testDestroyCoolItemByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bar = this._createCoolBar();
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CoolItem",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var item = rwt.protocol.ObjectRegistry.getObject( "w3" );
      TestUtil.flush();
      processor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );
      assertNull( item.getParent() );
      bar.destroy();
    },

    testDestroyCoolItemWithCoolBar : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bar = this._createCoolBar();
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CoolItem",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var item = rwt.protocol.ObjectRegistry.getObject( "w3" );
      TestUtil.flush();

      processor.processOperationArray( [ "destroy", "w2" ] );
      TestUtil.flush();

      assertTrue( rwt.protocol.ObjectRegistry.getObject( "w3" ) == null );
      assertTrue( item.isDisposed() );
      bar.destroy();
    },

    testUpdateHandleHeight : function() {
      var bar = this._createCoolBar();
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CoolItem",
        "properties" : {
          "style" : [ ],
          "parent" : "w2",
          "bounds" : [ 0, 0, 30, 40 ]
        }
      } );
      var item = rwt.protocol.ObjectRegistry.getObject( "w3" );
      assertEquals( 30, item.getWidth() );
      assertEquals( 40, item.getHeight() );
      assertEquals( 40, item._handle.getHeight() );
      bar.destroy();
    },

    testSetControlByProtocol: function() {
      var bar = this._createCoolBar();
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CoolItem",
        "properties" : {
          "style" : [ ],
          "parent" : "w2",
          "control" : "w4"
        }
      } );
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2"
        }
      } );
      var item = rwt.protocol.ObjectRegistry.getObject( "w3" );
      var button = rwt.protocol.ObjectRegistry.getObject( "w4" );
      assertIdentical( button, item._control );
      bar.destroy();
    },

    testDestroyCoolItemAndControlWithCoolBar : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bar = this._createCoolBar();
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CoolItem",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "control" : "w4"
        }
      } );
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w2"
        }
      } );
      var item = rwt.protocol.ObjectRegistry.getObject( "w3" );
      var control = rwt.protocol.ObjectRegistry.getObject( "w4" );
      TestUtil.flush();

      processor.processOperationArray( [ "destroy", "w2" ] );
      TestUtil.flush();

      assertTrue( rwt.protocol.ObjectRegistry.getObject( "w3" ) == null );
      assertTrue( item.isDisposed() );
      assertTrue( rwt.protocol.ObjectRegistry.getObject( "w4" ) == null );
      assertTrue( control.isDisposed() );
      bar.destroy();
    },

    testSetLockByProtocol: function() {
      var bar = this._createCoolBar();
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CoolItem",
        "properties" : {
          "style" : [ ],
          "parent" : "w2"        }
      } );
      var item = rwt.protocol.ObjectRegistry.getObject( "w3" );
      assertTrue( item._handle.getDisplay() );
      processor.processOperation( {
        "target" : "w2",
        "action" : "set",
        "properties" : {
          "locked" : true
        }
      } );
      assertFalse( item._handle.getDisplay() );
      bar.destroy();
    },

    testCreateLockedByProtocol: function() {
      var bar = this._createCoolBar();
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w2",
        "action" : "set",
        "properties" : {
          "locked" : true
        }
      } );
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CoolItem",
        "properties" : {
          "style" : [ ],
          "parent" : "w2"
        }
      } );
      var item = rwt.protocol.ObjectRegistry.getObject( "w3" );
      assertFalse( item._handle.getDisplay() );
      bar.destroy();
    },

    testMove : function() {
      var bar = this._createCoolBar();
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CoolItem",
        "properties" : {
          "style" : [ ],
          "parent" : "w2",
          "bounds" : [ 0, 0, 100, 10 ]
        }
      } );
      var item = rwt.protocol.ObjectRegistry.getObject( "w3" );
      TestUtil.flush();

      TestUtil.fakeMouseEvent( item._handle, "mousedown", 0, 0 );
      TestUtil.fakeMouseEvent( item._handle, "mousemove", 20, 0 );
      TestUtil.fakeMouseEvent( item._handle, "mouseup", 20, 0 );

      assertEquals( 20, item.getLeft() );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getLastMessage();
      assertEquals( 20, message.findCallProperty( "w3", "move", "left") );
      bar.destroy();
    },

    /////////
    // helper

    _createCoolBar : function() {
      TestUtil.createShellByProtocol( "shell" );
      var processor = rwt.protocol.MessageProcessor;
      processor.processOperation( {
        "target" : "w2",
        "action" : "create",
        "type" : "rwt.widgets.CoolBar",
        "properties" : {
          "style" : [ "BORDER" ],
          "parent" : "shell"
        }
      } );
      return rwt.protocol.ObjectRegistry.getObject( "w2" );
    }

  }

} );

}());
