/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function() {

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var ObjectRegistry = rwt.remote.ObjectRegistry;
var Processor = rwt.remote.MessageProcessor;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.SashTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateSashByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Sash",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Sash );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "sash", widget.getAppearance() );
      assertEquals( "vertical", widget.getOrientation() );
      shell.destroy();
      widget.destroy();
    },

    testCreateSashHorizontalByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Sash",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Sash );
      assertEquals( "horizontal", widget.getOrientation() );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionListenerByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Sash",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );

      TestUtil.protocolListen( "w3", { "Selection" : true } );

      var remoteObject = rwt.remote.Connection.getInstance().getRemoteObject( widget );
      assertTrue( remoteObject.isListening( "Selection" ) );
      shell.destroy();
      widget.destroy();
    },

    testSendWidgetSelected : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Sash",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "bounds" : [ 10, 20 , 10, 100 ]
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      TestUtil.protocolListen( "w3", { "Selection" : true } );

      this.fakeDrag( widget, 5, 0 );

      var message = TestUtil.getLastMessage();
      assertEquals( 15, message.findNotifyProperty( "w3", "Selection", "x" ) );
      assertEquals( 20, message.findNotifyProperty( "w3", "Selection", "y" ) );
      assertEquals( 10, message.findNotifyProperty( "w3", "Selection", "width" ) );
      assertEquals( 100, message.findNotifyProperty( "w3", "Selection", "height" ) );
      shell.destroy();
    },

    testMouseOver : function() {
      var sash = new rwt.widgets.Sash();
      sash.addToDocument();
      TestUtil.flush();

      TestUtil.mouseOver( sash );
      assertTrue( sash.hasState( "over" ) );

      TestUtil.mouseOut( sash );
      assertFalse( sash.hasState( "over" ) );

      sash.destroy();
    },

    fakeDrag : function( sash, leftOffset, topOffset ) {
      sash._commonMouseDown();
      sash.setLeft( sash.getLeft() + leftOffset );
      sash.setTop( sash.getTop() + topOffset );
      sash._commonMouseUp();
    }

  }

} );

}() );
