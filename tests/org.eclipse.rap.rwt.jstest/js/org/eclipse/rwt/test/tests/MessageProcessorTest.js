/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

/*jshint newcap: false */
(function(){

var HandlerRegistry = rwt.remote.HandlerRegistry;
var MessageProcessor = rwt.remote.MessageProcessor;
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.MessageProcessorTest", {

  extend : rwt.qx.Object,

  members : {

    testAdapterRegistry : function() {
      var adapter = {};
      HandlerRegistry.add( "fooKey", adapter );
      assertIdentical( adapter, HandlerRegistry.getHandler( "fooKey" ) );
      HandlerRegistry.remove( "fooKey" );
      try {
        HandlerRegistry.getHandler( "fooKey" );
        fail();
      } catch( ex ) {
        // expected
      }
    },

    testProcessSet : function() {
      HandlerRegistry.add( "dummyType", {
        properties : [ "width", "height" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "height" : 33,
        "width" : 24
      };
      var operation = [ "set", "dummyId", properties ];
      MessageProcessor.processOperationArray( operation );
      assertEquals( [ "width", 24, "height", 33 ], targetObject.getLog() );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessSetLessProperties : function() {
      HandlerRegistry.add( "dummyType", {
        properties : [ "width", "height" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "height" : 33
      };
      MessageProcessor.processOperationArray( [ "set", "dummyId", properties ] );
      assertEquals( [ "height", 33 ], targetObject.getLog() );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessMoreProperties : function() {
      HandlerRegistry.add( "dummyType", {
        properties : [ "width", "height" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "height" : 33,
        "width" : 24,
        "top" : 14
      };
      MessageProcessor.processOperationArray( [ "set", "dummyId", properties ] );
      assertEquals( [ "width", 24, "height", 33 ], targetObject.getLog() );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessSetNoproperties : function() {
      HandlerRegistry.add( "dummyType", {} );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "height" : 33,
        "width" : 24
      };
      MessageProcessor.processOperationArray( [ "set", "dummyId", properties ] );
      assertEquals( [], targetObject.getLog() );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessSetPropertyHandler : function() {
      HandlerRegistry.add( "dummyType", {
        properties : [ "awesomeness" ],
        propertyHandler : {
          "awesomeness" : function( shell, value ) {
            shell.setCoolness( value * 100 );
          }
        }
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "awesomeness" : 1
      };
      MessageProcessor.processOperationArray( [ "set", "dummyId", properties ] );
      assertEquals( [ "coolness", 100 ], targetObject.getLog() );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessCreate : function() {
      var factory = this._getDummyFactory();
      HandlerRegistry.add( "dummyType", {
        factory : factory
      } );
      var properties = {
        style : []
      };
      MessageProcessor.processOperationArray( [ "create", "dummyId", "dummyType", properties ] );
      var result = this._getTargetById( "dummyId" );
      assertEquals( "myclass", result.classname );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessCreate_RemoteObjectListenMapNotInitializedForEvents : function() {
      HandlerRegistry.add( "dummyType", {
        factory : function(){ return {}; },
        events : [ "MyEventType", "MyOtherEventType" ]
      } );

      MessageProcessor.processOperationArray( [ "create", "dummyId", "dummyType", {} ] );

      var remoteObject = rwt.remote.RemoteObjectFactory._getRemoteObject( "dummyId" );
      assertFalse( remoteObject.isListening( "MyEventType" ) );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessCreate_RemoteObjectListenMapInitializedForListeners : function() {
      HandlerRegistry.add( "dummyType", {
        factory : function(){ return {}; },
        listeners : [ "MyEventType", "MyOtherEventType" ]
      } );

      MessageProcessor.processOperationArray( [ "create", "dummyId", "dummyType", {} ] );

      var remoteObject = rwt.remote.RemoteObjectFactory._getRemoteObject( "dummyId" );
      assertTrue( remoteObject.isListening( "MyEventType" ) );
      assertFalse( remoteObject.isListening( "MyUnkownEventType" ) );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessCreateServiceFails : function() {
      var factory = this._getDummyFactory();
      HandlerRegistry.add( "dummyType", {
        factory : factory,
        service : true
      } );
      var properties = {
          style : []
      };

      try {
        MessageProcessor.processOperationArray( [ "create", "dummyId", "dummyType", properties ] );
        fail();
      } catch( ex ) {
        // expected
      }

      assertTrue( this._getTargetById( "dummyId" ) == null );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessCreateAdapterHasNoConstructorFails : function() {
      HandlerRegistry.add( "dummyType", {} );
      var properties = {};
      var error = null;
      try {
        MessageProcessor.processOperationArray( [ "create", "dummyId", "dummyType", properties ] );
      } catch ( ex ) {
        error = ex;
      }
      assertNotNull( error );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessCreateWithStyleStates : function() {
      HandlerRegistry.add( "dummyType", {
        factory : this._getDummyFactory()
      } );
      var properties = {
        style : [ "BORDER", "FLAT" ]
      };
      MessageProcessor.processOperationArray( [ "create", "dummyId", "dummyType", properties ] );
      var result = this._getTargetById( "dummyId" );
      // NOTE: Order is NOT relevant!
      assertTrue( result.getLog().indexOf( "rwt_BORDER" ) !== -1 );
      assertTrue( result.getLog().indexOf( "rwt_FLAT" ) !== -1 );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessCreateGetStyleMap : function() {
      HandlerRegistry.add( "dummyType", {
        factory : this._getDummyFactory()
      } );
      var properties = {
        style : [ "BORDER", "FLAT" ]
      };
      MessageProcessor.processOperationArray( [ "create", "dummyId", "dummyType", properties ] );
      var result = this._getTargetById( "dummyId" );
      var style = result.getStyleMap();
      assertTrue( style.BORDER );
      assertTrue( style.FLAT );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessCreateGetProperties : function() {
      HandlerRegistry.add( "dummyType", {
        factory : this._getDummyFactory()
      } );
      var properties = {
        style : [ "BORDER", "FLAT" ]
      };
      MessageProcessor.processOperationArray( [ "create", "dummyId", "dummyType", properties ] );
      var result = this._getTargetById( "dummyId" );
      assertIdentical( properties.style, result.getProperties().style );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessCreateAndSetProperties : function() {
      HandlerRegistry.add( "dummyType", {
        factory : this._getDummyFactory(),
        properties : [ "width", "height" ]
      } );
      var properties = {
        style : [],
        width : 34
      };
      MessageProcessor.processOperationArray( [ "create", "dummyId", "dummyType", properties ] );
      var result = this._getTargetById( "dummyId" );
      assertEquals( [ "width", 34 ], result.getLog() );
      HandlerRegistry.remove( "dummyType" );
    },

//    testProcessCreateWithParent : function() {
//      HandlerRegistry.add( "dummyType", {
//         factory : this._getDummyFactory()
//      } );
//      var properties = {
//        parent : "dummyParentId",
//        style : []
//      };
//      var parent = this._getDummyTarget( "dummyParentId" );
//      var operation = {
//        type : "dummyType",
//        "target" : "dummyId",
//        "action" : "create",
//        "properties" : properties
//      };
//      MessageProcessor.processOperation( operation );
//      var result = this._getTargetById( "dummyId" );
//      assertIdentical( parent, result.getParent() );
//      HandlerRegistry.remove( "dummyType" );
//    },

    testProcessDestroy : function() {
      HandlerRegistry.add( "dummyType", {
        "destructor" : function( obj ) {
          obj.destroy();
        }
      } );
      var target = this._getDummyTarget( "dummyId" );
      MessageProcessor.processOperationArray( [ "destroy", "dummyId" ] );
      assertEquals( [ "destroy" ], target.getLog() );
      assertNull( target.getParent() );
      assertTrue( this._getTargetById( "dummyId" ) == null );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessDestroy_CallDestroyMethodDirectlyWhenNameIsGiven : function() {
      HandlerRegistry.add( "dummyType", {
        "destructor" : "destroy"
      } );
      var target = this._getDummyTarget( "dummyId" );

      MessageProcessor.processOperationArray( [ "destroy", "dummyId" ] );

      assertEquals( [ "destroy" ], target.getLog() );
      assertNull( target.getParent() );
      assertTrue( this._getTargetById( "dummyId" ) == null );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessDestroyWithDestructor : function() {
      HandlerRegistry.add( "dummyType", {
        "destructor" : function( widget ) {
          widget.addState( "foo" );
          widget.destroy();
        }
      } );
      var target = this._getDummyTarget( "dummyId" );
      MessageProcessor.processOperationArray( [ "destroy", "dummyId" ] );
      assertEquals( [ "foo", "destroy" ], target.getLog() );
      assertTrue( this._getTargetById( "dummyId" ) == null );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessDestroyWithChildren : function() {
      HandlerRegistry.add( "dummyType", {
        "destructor" : function( obj ) {
          obj.destroy();
        },
        "getDestroyableChildren" : function( obj ) {
          return obj.getChildren ? obj.getChildren() : [];
        }
      } );
      var target = this._getDummyTarget( "dummyId1" );
      var childOne = this._getDummyTarget( "dummyId2" );
      var childTwo = this._getDummyTarget( "dummyId3" );
      target.getChildren = function() {
        return [ childOne, childTwo, null, undefined ];
      };

      MessageProcessor.processOperationArray( [ "destroy", "dummyId1" ] );

      assertTrue( this._getTargetById( "dummyId1" ) == null );
      assertTrue( this._getTargetById( "dummyId2" ) == null );
      assertTrue( this._getTargetById( "dummyId3" ) == null );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessCall : function() {
      HandlerRegistry.add( "dummyType", {
        methods : [ "doFoo" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {};
      MessageProcessor.processOperationArray( [ "call", "dummyId", "doFoo", properties ] );
      assertEquals( "foo", targetObject.getLog()[ 0 ] );
      assertIdentical( properties, targetObject.getLog()[ 1 ] );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessCustomCall : function() {
      HandlerRegistry.add( "dummyType", {
        methods : [ "doBar" ],
        methodHandler : {
          "doBar" : function( widget, properties ) {
            widget.doFoo( properties );
          }
        }
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {};
      MessageProcessor.processOperationArray( [ "call", "dummyId", "doBar", properties ] );
      assertEquals( "foo", targetObject.getLog()[ 0 ] );
      assertIdentical( properties, targetObject.getLog()[ 1 ] );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessCallWithParameters : function() {
      HandlerRegistry.add( "dummyType", {
        methods : [ "doFoo" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "foo" : [ 17, 42 ]
     };
      MessageProcessor.processOperationArray( [ "call", "dummyId", "doFoo", properties ] );
      assertEquals( "foo", targetObject.getLog()[ 0 ] );
      var args = targetObject.getLog()[ 1 ];
      assertEquals( properties, args );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessCallUnkownMethod : function() {
      HandlerRegistry.add( "dummyType", {
        methods : [ "doBar" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        name : "doFoo"
      };
      MessageProcessor.processOperationArray( [ "call", "dummyId", undefined, properties ] );
      assertEquals( 0, targetObject.getLog().length );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessCallNoKownMethod : function() {
      HandlerRegistry.add( "dummyType", { } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = { name : "doFoo" };
      MessageProcessor.processOperationArray( [ "call", "dummyId", "", properties ] );
      assertEquals( 0, targetObject.getLog().length );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessUnkownListener : function() {
      HandlerRegistry.add( "dummyType", {
        listeners : [ "focus" ]
      } );
      var targetObject = this._getDummyWidget( "dummyId" );
      var properties = {
        add : [ "foo" ]
      };
      MessageProcessor.processOperationArray( [ "listen", "dummyId", properties ] );
      // succeeds by not crashing
      HandlerRegistry.remove( "dummyType" );
      targetObject.destroy();
    },

    testProcessNolisteners : function() {
      HandlerRegistry.add( "dummyType", {} );
      var targetObject = this._getDummyWidget( "dummyId" );
      var properties = {
        add : [ "mouse" ]
      };
      MessageProcessor.processOperationArray( [ "listen", "dummyId", properties ] );
      // NOTE: hasEventListeners may return "undefined" instead of "false"
      assertTrue( !targetObject.hasEventListeners( "mousedown" ) );
      assertTrue( !targetObject.hasEventListeners( "mouseup" ) );
      HandlerRegistry.remove( "dummyType" );
      targetObject.destroy();
    },

    testProcessSetterListener : function() {
      HandlerRegistry.add( "dummyType", {
        listeners : [ "foo", "bar" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "foo" : true
      };
      MessageProcessor.processOperationArray( [ "listen", "dummyId", properties ] );
      properties = {
        "foo" : false
      };
      MessageProcessor.processOperationArray( [ "listen", "dummyId", properties ] );
      assertEquals(  [ "fooListener", true, "fooListener", false ], targetObject.getLog() );
      targetObject.destroy();
    },

    testProcessCustomListener : function() {
      HandlerRegistry.add( "dummyType", {
        listeners : [ "foo", "bar" ],
        listenerHandler : {
          "bar" : function( targetObject, value ) {
            targetObject.setMyData( "barListener", value ? true : null );
          }
        }
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "bar" : true
      };
      MessageProcessor.processOperationArray( [ "listen", "dummyId", properties ] );
      assertTrue( targetObject.getMyData( "barListener" ) );
      properties = {
        "bar" : false
      };
      MessageProcessor.processOperationArray( [ "listen", "dummyId", properties ] );
      assertNull( targetObject.getMyData( "barListener" ) );
      targetObject.destroy();
    },

    testProcessListenUpdatesRemoteHandlerListen_ListenTrue : function() {
      HandlerRegistry.add( "dummyType", {
        events : [ "foo", "bar" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );

      MessageProcessor.processOperationArray( [ "listen", "dummyId", { "foo" : true } ] );

      var remoteObject = rwt.remote.RemoteObjectFactory._getRemoteObject( "dummyId" );
      assertTrue( remoteObject.isListening( "foo" ) );
      HandlerRegistry.remove( "dummyType" );
      targetObject.destroy();
    },

    testProcessListenUpdatesRemoteHandlerListen_ListenForUnkownType : function() {
      HandlerRegistry.add( "dummyType", {
        events : [ "foo", "bar" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );

      MessageProcessor.processOperationArray( [ "listen", "dummyId", { "foox" : true } ] );

      var remoteObject = rwt.remote.RemoteObjectFactory._getRemoteObject( "dummyId" );
      assertFalse( remoteObject.isListening( "foox" ) );
      HandlerRegistry.remove( "dummyType" );
      targetObject.destroy();
    },

    testProcessListenUpdatesRemoteHandlerListen_ListenFalse : function() {
      HandlerRegistry.add( "dummyType", {
        events : [ "foo", "bar" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );

      MessageProcessor.processOperationArray( [ "listen", "dummyId", { "foo" : true } ] );
      MessageProcessor.processOperationArray( [ "listen", "dummyId", { "foo" : false } ] );

      var remoteObject = rwt.remote.RemoteObjectFactory._getRemoteObject( "dummyId" );
      assertFalse( remoteObject.isListening( "foo" ) );
      HandlerRegistry.remove( "dummyType" );
      targetObject.destroy();
    },

    testProcessListenDoesNotUpdateRemoteHandlerListen_ListenFalse : function() {
      HandlerRegistry.add( "dummyType", {
        listeners : [ "foo", "bar" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );

      MessageProcessor.processOperationArray( [ "listen", "dummyId", { "foo" : true } ] );
      MessageProcessor.processOperationArray( [ "listen", "dummyId", { "foo" : false } ] );

      var remoteObject = rwt.remote.RemoteObjectFactory._getRemoteObject( "dummyId" );
      assertTrue( remoteObject.isListening( "foo" ) );
      HandlerRegistry.remove( "dummyType" );
      targetObject.destroy();
    },

    testProcessMessage : function() {
      HandlerRegistry.add( "dummyType", {
        properties : [ "width", "height" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var operation1 = [ "set", "dummyId", { "height" : 33 } ];
      var operation2 = [ "set", "dummyId", { "width" : 24 } ];
      var message = {
        "head" : {},
        "operations" : [ operation1, operation2 ]
      };
      MessageProcessor.processMessage( message );
      assertEquals( [ "height", 33, "width", 24 ], targetObject.getLog() );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessMessage_firesGlobalEvents : function() {
      var log = [];
      var message = { "head": {}, "operations" : [ [ "call", "dummyId", "processing"] ] };
      rwt.remote.HandlerRegistry.add( "dummyType", { methods : [ "processing" ] } );
      this._getDummyTarget( "dummyId" ).processing = function() { log.push( "processing" ); };
      rap.on( "receive", function(arg) { log.push( "receive", arg ); } );
      rap.on( "process", function(arg) { log.push( "process", arg ); } );

      rwt.remote.MessageProcessor.processMessage( message, function() {
        log.push( "callback" );
      } );

      assertEquals( [ "receive", message, "processing", "process", message, "callback" ], log );
      rwt.remote.HandlerRegistry.remove( "dummyType" );
    },

    testProcessMessage_withCallback : function() {
      HandlerRegistry.add( "dummyType", {
        properties : [ "width", "height" ]
      } );
      var log;
      var targetObject = this._getDummyTarget( "dummyId" );
      var operation1 = [ "set", "dummyId", { "height" : 33 } ];
      var operation2 = [ "set", "dummyId", { "width" : 24 } ];
      var message = { "head" : {}, "operations" : [ operation1, operation2 ] };

      MessageProcessor.processMessage( message, function() {
        log = targetObject.getLog();
      } );

      assertEquals( [ "height", 33, "width", 24 ], log );
      HandlerRegistry.remove( "dummyType" );
    },

    testSetError : function() {
      HandlerRegistry.add( "dummyType", {
        properties : [ "width", "height", "fail" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var operation = [ "set", "dummyId", { "fail" : 99 } ];
      var message = {
        "head" : {},
        "operations" : [ operation ]
      };
      var error = null;
      try {
        MessageProcessor.processMessage( message );
      } catch( ex ) {
        error = ex;
      }
      assertTrue( error instanceof Error );
      var message = error.message.toLowerCase();
      var expected1 = "operation \"set\" on target \"dummyid\" of type \"myclass\"";
      var expected2 = "fail = 99";
      var expected3 = "item: \"fail\"";
      var expected4 = "myerror";
      assertTrue( message.indexOf( expected1 ) !== - 1 );
      // TODO [tb] : implement enhanced information gathering
      assertTrue( message.indexOf( expected2 ) !== - 1 );
      //assertTrue( message.indexOf( expected3 ) !== - 1 );
      assertTrue( message.indexOf( expected4 ) !== - 1 );
      HandlerRegistry.remove( "dummyType" );
    },

    testProcessHeadSetConnectionId : function() {
      var message = {
        "head": {
          "cid": "foo"
        },
        "operations" : []
      };
      MessageProcessor.processMessage( message );
      var connection = rwt.remote.Connection.getInstance();
      assertEquals( "foo", connection.getConnectionId() );
    },

    testGetService : function() {
      var log = [];
      var object = {
        "call" : function() {
          log.push( "call" );
        }
      };
      var factory = function() {
        log.push( "create" );
        return object;
      };
      HandlerRegistry.add( "dummyType", {
        factory : factory,
        service : true,
        methods : [ "call" ]
      } );

      MessageProcessor.processOperationArray( [ "call", "dummyType", "call", {} ] );

      assertEquals( [ "create", "call" ], log );
      HandlerRegistry.remove( "dummyType" );
    },

    testGetServiceTwice : function() {
      var log = [];
      var object = {
          "call" : function() {
            log.push( "call" );
          }
      };
      var factory = function() {
        log.push( "create" );
        return object;
      };
      HandlerRegistry.add( "dummyType", {
        factory : factory,
        service : true,
        methods : [ "call" ]
      } );

      MessageProcessor.processOperationArray( [ "call", "dummyType", "call", {} ] );
      MessageProcessor.processOperationArray( [ "call", "dummyType", "call", {} ] );

      assertEquals( [ "create", "call", "call" ], log );
      HandlerRegistry.remove( "dummyType" );
    },

    ////////////////////////
    // Experimental Features

    testGenericSetter : function() {
      var logger = TestUtil.getLogger();
      HandlerRegistry.add( "dummyType", {
        factory : function() {
          return {
            set : function( map, options ) {
              logger.log( map );
              logger.log( options );
            }
          };
        },
        // causes set( map ) to be called, not setXXX, filter is not applied, but should if present:
        isGeneric : true
      } );
      MessageProcessor.processOperationArray( [ "create", "dummyId", "dummyType", {} ] );
      var map = {
        "foo" : "bar",
        "prop" : "val"
       };

      MessageProcessor.processOperationArray( [ "set", "dummyId", map ] );

      assertEquals( map, logger.getLog()[ 0 ] );
      assertEquals( { "nosync" : true }, logger.getLog()[ 1 ] );
      HandlerRegistry.remove( "dummyType" );
    },

    testGenericListen : function() {
      HandlerRegistry.add( "dummyType", {
        factory : function() {
          return {
            set : function( map ) {}
          };
        },
        // causes listen ops to work without "events" list (should only work if no list is present)
        isGeneric : true
      } );
      MessageProcessor.processOperationArray( [ "create", "dummyId", "dummyType", {} ] );
      var map = {
        "foo" : true,
        "bar" : true
       };

      MessageProcessor.processOperationArray( [ "listen", "dummyId", map ] );

      var remoteObject = rwt.remote.RemoteObjectFactory._getRemoteObject( "dummyId" );
      assertTrue( map, remoteObject.isListening( "foo" ) );
      assertTrue( map, remoteObject.isListening( "bar" ) );
      HandlerRegistry.remove( "dummyType" );
    },

    testGenericCall : function() {
      var logger = TestUtil.getLogger();
      HandlerRegistry.add( "dummyType", {
        factory : function() {
          return {
            set : function( map ) {},
            myMethod : function( props ) {
              logger.log( props );
            }
          };
        },
        // causes methods list to be ignored
        isGeneric : true
      } );
      MessageProcessor.processOperationArray( [ "create", "dummyId", "dummyType", {} ] );
      var map = {
        "foo" : "bar",
        "rainbow" : "dash"
       };

      MessageProcessor.processOperationArray( [ "call", "dummyId", "myMethod", map ] );

      var remoteObject = rwt.remote.RemoteObjectFactory._getRemoteObject( "dummyId" );
      var log = logger.getLog();
      assertEquals( map, log[ 0 ] );
      HandlerRegistry.remove( "dummyType" );
    },

    testPauseExecution : function() {
      HandlerRegistry.add( "dummyType", {
        factory : this._getDummyFactory(),
        properties : [ "width" ]
      } );
      var message = {
        "head" : {},
        "operations" : [
          [ "create", "dummyId", "dummyType", { "style" : [], "width" : 44 } ],
          [
            "call",
            "rwt.client.JavaScriptExecutor",
            "execute",
            { "content" : "rwt.remote.MessageProcessor.pauseExecution();" }
          ],
          [ "set", "dummyId", { "width" : 45 } ]
        ]
      };

      MessageProcessor.processMessage( message );
      assertTrue( MessageProcessor.isPaused() );
      assertEquals( [ "width", 44 ], this._getTargetById( "dummyId" ).getLog() );
      MessageProcessor.continueExecution();

      assertEquals( [ "width", 44, "width", 45 ], this._getTargetById( "dummyId" ).getLog() );
    },

    testPauseExecutionWhileFirstMessageStillPendingFails : function() {
      HandlerRegistry.add( "dummyType", {
        factory : this._getDummyFactory(),
        properties : [ "width" ]
      } );
      var message = {
        "head" : {},
        "operations" : [
          [
            "call",
            "rwt.client.JavaScriptExecutor",
            "execute",
            { "content" : "rwt.remote.MessageProcessor.pauseExecution();" }
          ]
        ]
      };

      MessageProcessor.processMessage( message );

      try {
        MessageProcessor.processMessage( message );
        fail();
      } catch( ex ) {
        MessageProcessor.continueExecution();
      }
    },

    /////////
    // Helper

    _getDummyTarget : function( targetId ) {
      var log = [];
      var targetObject = {
        _userData : {
        },
        _parent : null,
        _styleMap : null,
        setParent : function( parent ) {
          this._parent = parent;
        },
        _renderAppearance : function() {
        },
        getParent : function() {
          return this._parent;
        },
        addState : function( state ) {
          log.push( state );
        },
        setWidth : function( value ) {
          log.push( "width", value );
        },
        setHeight : function( value ) {
          log.push( "height", value );
        },
        setCoolness : function( value ) {
          log.push( "coolness", value );
        },
        setFail : function( value ) {
          throw "myerror";
        },
        setMyData : function( key, value ) {
          this._userData[ key ] = value;
        },
        getMyData : function( key ) {
          return this._userData[ key ];
        },
        getProperties : function() {
          return this._prop;
        },
        getStyleMap : function() {
          return rwt.remote.HandlerUtil.createStyleMap( this._prop.style );
        },
        doFoo : function( arg ) {
          log.push( "foo", arg );
        },
        destroy : function() {
          this.setParent( null );
          log.push( "destroy" );
        },
        getDisposed : function() {
          return false;
        },
        setToolTip : function() {
          // NOTE: Currently needed (as getDisposed) by WidgetManager
          // should not be the case.
        },
        setHasFooListener : function( value ) {
          log.push( "fooListener", value );
        },
        classname : "myclass"
      };
      targetObject.getLog = function() {
        return log;
      };
      if( typeof targetId === "string" ) {
        var adapter = rwt.remote.HandlerRegistry.getHandler( "dummyType" );
        rwt.remote.ObjectRegistry.add( targetId, targetObject, adapter );
      }
      return targetObject;
    },

    _getDummyFactory : function() {
      var constr = function( properties ) {
        if( properties ) {
          rwt.remote.HandlerUtil.addStatesForStyles( this, properties.style );
        }
        this._prop = properties;
      };
      constr.prototype = this._getDummyTarget();
      var result = function( prop ) {
        return new constr( prop );
      };
      return result;
    },

    _getTargetById : function( id ) {
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      return widgetManager.findWidgetById( id );
    },

    _isControl : function( target ) {
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      return widgetManager.isControl( target );
    },

    _getDummyWidget : function( targetId ) {
      var result = new rwt.widgets.base.Terminator();
      result.addToDocument();
      result.setLeft( 10 );
      result.setTop( 10 );
      result.setWidth( 100 );
      result.setHeight( 20 );
      TestUtil.flush();
      if( typeof targetId === "string" ) {
        var widgetManager = rwt.remote.WidgetManager.getInstance();
        widgetManager.add( result, targetId, true, "dummyType" );
      }
      return result;
    }

  }

} );

}());
