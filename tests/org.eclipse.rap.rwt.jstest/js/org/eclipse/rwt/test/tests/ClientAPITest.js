/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var ObjectRegistry = rwt.remote.ObjectRegistry;
var AdapterRegistry = rwt.remote.HandlerRegistry;
var MessageProcessor = rwt.remote.MessageProcessor;
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

var shell;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ClientAPITest", {

  extend : rwt.qx.Object,

  members : {

    testProtocolAdapterDelegation : function() {
      var handler = {};

      rap.registerTypeHandler( "myTestType", handler );

      assertIdentical( handler, AdapterRegistry.getHandler( "myTestType" ) );
      AdapterRegistry.remove( "myTestType" );
    },

    testGetServerObjectDelegation : function() {
      var handler = {};
      var obj = {};
      rap.registerTypeHandler( "myTestType", handler );
      ObjectRegistry.add( "r1", obj, handler );

      var result = rap.getRemoteObject( obj );

      assertIdentical( result, rwt.remote.Server.getInstance().getRemoteObject( obj ) );
      AdapterRegistry.remove( "myTestType" );
    },

    testGetObject : function() {
      var handler = {};
      var obj = {};
      rap.registerTypeHandler( "myTestType", handler );
      ObjectRegistry.add( "r1", obj, handler );

      var result = rap.getObject( "r1" );

      assertIdentical( obj, result );
      AdapterRegistry.remove( "myTestType" );
    },

    testGetInternalObject : function() {
      var result = rap.getObject( "w2" );

      assertFalse( result === ObjectRegistry.getObject( "w2" ) );
    },

    testGetInternalObjectTwice : function() {
      var resultOne = rap.getObject( "w2" );
      var resultTwo = rap.getObject( "w2" );

      assertTrue( resultOne === resultTwo );
    },

    testCompositeWrapperAppend : function() {
      MessageProcessor.processOperationArray( [ "create", "w3", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w2"
        }
      ] );
      TestUtil.flush();
      var element = document.createElement( "div" );

      rap.getObject( "w3" ).append( element );

      var composite = ObjectRegistry.getObject( "w3" );
      assertIdentical( composite._getTargetNode(), element.parentNode );
    },

    testCompositeWrapperAppendBeforeFlush : function() {
      MessageProcessor.processOperationArray( [ "create", "w3", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w2"
        }
      ] );
      var element = document.createElement( "div" );

      rap.getObject( "w3" ).append( element );
      TestUtil.flush();

      var composite = ObjectRegistry.getObject( "w3" );
      assertIdentical( composite._getTargetNode(), element.parentNode );
    },

    testCompositeWrapperAppendMultipleBeforeFlush : function() {
      MessageProcessor.processOperationArray( [ "create", "w3", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w2"
        }
      ] );
      var element = document.createElement( "div" );
      var otherElement = document.createElement( "div" );

      rap.getObject( "w3" ).append( element );
      rap.getObject( "w3" ).append( otherElement );
      TestUtil.flush();

      var composite = ObjectRegistry.getObject( "w3" );
      assertIdentical( composite._getTargetNode(), element.parentNode );
      assertIdentical( composite._getTargetNode(), otherElement.parentNode );
    },

    testCompositeWrapperAppendMultipleBeforeAndAfterFlush : function() {
      MessageProcessor.processOperationArray( [ "create", "w3", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w2"
        }
      ] );
      var element = document.createElement( "div" );
      var otherElement = document.createElement( "div" );

      rap.getObject( "w3" ).append( element );
      TestUtil.flush();
      rap.getObject( "w3" ).append( otherElement );

      var composite = ObjectRegistry.getObject( "w3" );
      assertIdentical( composite._getTargetNode(), element.parentNode );
      assertIdentical( composite._getTargetNode(), otherElement.parentNode );
    },

    testCompositeWrapperGetClientArea : function() {
      MessageProcessor.processOperationArray( [ "create", "w3", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w2",
          "clientArea" : [ 1, 2, 3, 4 ]
        }
      ] );

      assertEquals( [ 1, 2, 3, 4 ], rap.getObject( "w3" ).getClientArea() );
    },

    testCompositeWrapperGetClientArea_returnsSaveCopy : function() {
      MessageProcessor.processOperationArray( [ "create", "w3", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w2",
          "clientArea" : [ 1, 2, 3, 4 ]
        }
      ] );

      rap.getObject( "w3" ).getClientArea()[ 1 ] = 100;
      assertEquals( [ 1, 2, 3, 4 ], rap.getObject( "w3" ).getClientArea() );
    },


    testCompositeWrapperAddResizeListener : function() {
      MessageProcessor.processOperationArray( [ "create", "w3", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w2",
          "clientArea" : [ 1, 2, 3, 4 ]
        }
      ] );
      var logger = TestUtil.getLogger();

      rap.getObject( "w3" ).addListener( "Resize", logger.log );
      TestUtil.protocolSet( "w3", {
        "bounds" : [ 0, 0, 110, 120 ],
        "clientArea" : [ 0, 0, 109, 119 ]
      } );

      assertEquals( 1, logger.getLog().length );
    },

    testCompositeWrapperAddResizeListener_UnkownTypeThrowsException : function() {
      MessageProcessor.processOperationArray( [ "create", "w3", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w2",
          "clientArea" : [ 1, 2, 3, 4 ]
        }
      ] );
      var logger = TestUtil.getLogger();

      try {
        rap.getObject( "w3" ).addListener( "resize", logger.log );
        fail();
      } catch( ex ) {
        // expected
      }
    },

    testCompositeWrapperAddResizeListener_FireEventAfterClientAreaChanged : function() {
      MessageProcessor.processOperationArray( [ "create", "w3", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w2",
          "clientArea" : [ 1, 2, 3, 4 ]
        }
      ] );
      var logger = TestUtil.getLogger();
      rap.getObject( "w3" ).addListener( "Resize", function() {
        logger.log( rap.getObject( "w3" ).getClientArea() );
      } );

      TestUtil.protocolSet( "w3", {
        "bounds" : [ 0, 0, 110, 120 ],
        "clientArea" : [ 0, 0, 109, 119 ]
      } );

      assertEquals( [ 0, 0, 109, 119 ], logger.getLog()[ 0 ] );
    },

    testCompositeWrapperAddResizeListener_NoEventObjectGiven : function() {
      MessageProcessor.processOperationArray( [ "create", "w3", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w2",
          "clientArea" : [ 1, 2, 3, 4 ]
        }
      ] );
      var logger = TestUtil.getLogger();
      rap.getObject( "w3" ).addListener( "Resize", function() {
        logger.log( arguments );
      } );

      TestUtil.protocolSet( "w3", {
        "bounds" : [ 0, 0, 110, 120 ],
        "clientArea" : [ 0, 0, 109, 119 ]
      } );

      var args = logger.getLog()[ 0 ];
      assertTrue( args.length === 0 || args[ 0 ] === undefined );
    },

    testCompositeWrapperRemoveResizeListener : function() {
      MessageProcessor.processOperationArray( [ "create", "w3", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w2",
          "clientArea" : [ 1, 2, 3, 4 ]
        }
      ] );
      var logger = TestUtil.getLogger();
      rap.getObject( "w3" ).addListener( "Resize", logger.log );
      rap.getObject( "w3" ).removeListener( "Resize", logger.log );

      TestUtil.protocolSet( "w3", {
        "bounds" : [ 0, 0, 110, 120 ],
        "clientArea" : [ 0, 0, 109, 119 ]
      } );

      assertEquals( 0, logger.getLog().length );
    },

    testOn_UnkownTypeThrowsException : function() {
      try {
        rap.on( "unkownType", function() {} );
        fail();
      } catch( ex ) {
        // expected
      }
    },

    testOn_RegisterAndFireSendEvent : function() {
      var logger = TestUtil.getLogger();

      rap.on( "send", logger.log );
      rap.getRemoteObject( shell ).call( "foo" );

      assertEquals( 1, logger.getLog().length );
    },

    testOn_FireSendEventBeforeSend : function() {
      var logger = TestUtil.getLogger();

      rap.on( "send", function(){ logger.log( TestUtil.getRequestsSend() ); } );
      rap.getRemoteObject( shell ).call( "foo" );

      assertEquals( 0, logger.getLog()[ 0 ] );
    },

    testOn_RegisterAndFireRenderEvent : function() {
      var logger = TestUtil.getLogger();

      rap.on( "render", logger.log );
      rap.getRemoteObject( shell ).call( "foo" );

      assertEquals( 1, logger.getLog().length );
    },

    testOn_RegisterSameEventWithSameListenerTwice : function() {
      var logger = TestUtil.getLogger();

      rap.on( "render", logger.log );
      rap.on( "render", logger.log );
      rap.getRemoteObject( shell ).call( "foo" );

      assertEquals( 1, logger.getLog().length );
    },

    testOn_FireRenderEventAfterProcess : function() {
      var logger = TestUtil.getLogger();
      var server = rwt.remote.Server.getInstance();
      var now = server.getRequestCounter();

      rap.on( "render", function(){ logger.log( server.getRequestCounter() ); } );
      rap.getRemoteObject( shell ).call( "foo" );

      assertEquals( now + 1, logger.getLog()[ 0 ] );
    },

    testOff_DeregisterFirstListener : function() {
      var logger = TestUtil.getLogger();

      rap.on( "render", logger.log );
      rap.on( "send", logger.log );
      rap.off( "render", logger.log );
      rap.getRemoteObject( shell ).call( "foo" );

      assertEquals( 1, logger.getLog().length );
    },

    testOff_DeregisterSecondListener : function() {
      var logger = TestUtil.getLogger();

      rap.on( "render", logger.log );
      rap.on( "send", logger.log );
      rap.off( "render", logger.log );
      rap.getRemoteObject( shell ).call( "foo" );

      assertEquals( 1, logger.getLog().length );
    },

    testOff_DeregisterWithoutRegister : function() {
      var logger = TestUtil.getLogger();

      rap.off( "render", logger.log );
      // succeeds by not crashing
    },

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
    },

    tearDown : function() {
      MessageProcessor.processOperationArray( [ "destroy", "w2"] );
      shell = null;
    }


  }

} );

}());