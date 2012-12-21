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

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ProtocolProcessorTest", {

  extend : rwt.qx.Object,

  members : {

    testAdapterRegistry : function() {
      var registry = rwt.remote.HandlerRegistry;
      var adapter = {};
      registry.add( "fooKey", adapter );
      assertIdentical( adapter, registry.getHandler( "fooKey" ) );
      registry.remove( "fooKey" );
      try {
        registry.getHandler( "fooKey" );
        fail();
      } catch( ex ) {
        // expected
      }
    },

    testProcessSet : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
        properties : [ "width", "height" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "height" : 33,
        "width" : 24
      };
      var operation = [ "set", "dummyId", properties ];
      processor.processOperationArray( operation );
      assertEquals( [ "width", 24, "height", 33 ], targetObject.getLog() );
      registry.remove( "dummyType" );
    },

    testProcessSetLessProperties : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
        properties : [ "width", "height" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "height" : 33
      };
      processor.processOperationArray( [ "set", "dummyId", properties ] );
      assertEquals( [ "height", 33 ], targetObject.getLog() );
      registry.remove( "dummyType" );
    },

    testProcessMoreProperties : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
        properties : [ "width", "height" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "height" : 33,
        "width" : 24,
        "top" : 14
      };
      processor.processOperationArray( [ "set", "dummyId", properties ] );
      assertEquals( [ "width", 24, "height", 33 ], targetObject.getLog() );
      registry.remove( "dummyType" );
    },

    testProcessSetNoproperties : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {} );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "height" : 33,
        "width" : 24
      };
      processor.processOperationArray( [ "set", "dummyId", properties ] );
      assertEquals( [], targetObject.getLog() );
      registry.remove( "dummyType" );
    },

    testProcessSetPropertyHandler : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
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
      processor.processOperationArray( [ "set", "dummyId", properties ] );
      assertEquals( [ "coolness", 100 ], targetObject.getLog() );
      registry.remove( "dummyType" );
    },


    testProcessCreate : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      var factory = this._getDummyFactory();
      registry.add( "dummyType", {
        factory : factory
      } );
      var properties = {
        style : []
      };
      processor.processOperationArray( [ "create", "dummyId", "dummyType", properties ] );
      var result = this._getTargetById( "dummyId" );
      assertEquals( "myclass", result.classname );
      registry.remove( "dummyType" );
    },

    testProcessCreateAdapterHasNoConstructorFails : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {} );
      var properties = {};
      var error = null;
      try {
        processor.processOperationArray( [ "create", "dummyId", "dummyType", properties ] );
      } catch ( ex ) {
        error = ex;
      }
      assertNotNull( error );
      registry.remove( "dummyType" );
    },

    testProcessCreateWithStyleStates : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
        factory : this._getDummyFactory()
      } );
      var properties = {
        style : [ "BORDER", "FLAT" ]
      };
      processor.processOperationArray( [ "create", "dummyId", "dummyType", properties ] );
      var result = this._getTargetById( "dummyId" );
      // NOTE: Order is NOT relevant!
      assertTrue( result.getLog().indexOf( "rwt_BORDER" ) !== -1 );
      assertTrue( result.getLog().indexOf( "rwt_FLAT" ) !== -1 );
      registry.remove( "dummyType" );
    },

    testProcessCreateGetStyleMap : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
        factory : this._getDummyFactory()
      } );
      var properties = {
        style : [ "BORDER", "FLAT" ]
      };
      processor.processOperationArray( [ "create", "dummyId", "dummyType", properties ] );
      var result = this._getTargetById( "dummyId" );
      var style = result.getStyleMap();
      assertTrue( style.BORDER );
      assertTrue( style.FLAT );
      registry.remove( "dummyType" );
    },

    testProcessCreateGetProperties : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
        factory : this._getDummyFactory()
      } );
      var properties = {
        style : [ "BORDER", "FLAT" ]
      };
      processor.processOperationArray( [ "create", "dummyId", "dummyType", properties ] );
      var result = this._getTargetById( "dummyId" );
      assertIdentical( properties.style, result.getProperties().style );
      registry.remove( "dummyType" );
    },

    testProcessCreateAndSetProperties : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
        factory : this._getDummyFactory(),
        properties : [ "width", "height" ]
      } );
      var properties = {
        style : [],
        width : 34
      };
      processor.processOperationArray( [ "create", "dummyId", "dummyType", properties ] );
      var result = this._getTargetById( "dummyId" );
      assertEquals( [ "width", 34 ], result.getLog() );
      registry.remove( "dummyType" );
    },

//    testProcessCreateWithParent : function() {
//      var registry = rwt.remote.HandlerRegistry;
//      var processor = rwt.remote.MessageProcessor;
//      registry.add( "dummyType", {
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
//      processor.processOperation( operation );
//      var result = this._getTargetById( "dummyId" );
//      assertIdentical( parent, result.getParent() );
//      registry.remove( "dummyType" );
//    },

    testProcessDestroy : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
        "destructor" : function( obj ) {
          obj.destroy();
        }
      } );
      var target = this._getDummyTarget( "dummyId" );
      target.setParent( {
        getChildren : function() {
          return [ target ];
        }
      } );
      processor.processOperationArray( [ "destroy", "dummyId" ] );
      assertEquals( [ "destroy" ], target.getLog() );
      assertNull( target.getParent() );
      assertTrue( this._getTargetById( "dummyId" ) == null );
      registry.remove( "dummyType" );
    },

    testProcessDestroyWithDestructor : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
        "destructor" : function( widget ) {
          widget.addState( "foo" );
          widget.destroy();
        }
      } );
      var target = this._getDummyTarget( "dummyId" );
      target.setParent( {
        getChildren : function() {
          return [ target ];
        }
      } );
      processor.processOperationArray( [ "destroy", "dummyId" ] );
      assertEquals( [ "foo", "destroy" ], target.getLog() );
      assertTrue( this._getTargetById( "dummyId" ) == null );
      registry.remove( "dummyType" );
    },

    testProcessCall : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
        methods : [ "doFoo" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {};
      processor.processOperationArray( [ "call", "dummyId", "doFoo", properties ] );
      assertEquals( "foo", targetObject.getLog()[ 0 ] );
      assertIdentical( properties, targetObject.getLog()[ 1 ] );
      registry.remove( "dummyType" );
    },

    testProcessCustomCall : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
        methods : [ "doBar" ],
        methodHandler : {
          "doBar" : function( widget, properties ) {
            widget.doFoo( properties );
          }
        }
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {};
      processor.processOperationArray( [ "call", "dummyId", "doBar", properties ] );
      assertEquals( "foo", targetObject.getLog()[ 0 ] );
      assertIdentical( properties, targetObject.getLog()[ 1 ] );
      registry.remove( "dummyType" );
    },

    testProcessCallWithParameters : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
        methods : [ "doFoo" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "foo" : [ 17, 42 ]
     };
      processor.processOperationArray( [ "call", "dummyId", "doFoo", properties ] );
      assertEquals( "foo", targetObject.getLog()[ 0 ] );
      var args = targetObject.getLog()[ 1 ];
      assertEquals( properties, args );
      registry.remove( "dummyType" );
    },

    testProcessCallUnkownMethod : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
        methods : [ "doBar" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        name : "doFoo"
      };
      processor.processOperationArray( [ "call", "dummyId", undefined, properties ] );
      assertEquals( 0, targetObject.getLog().length );
      registry.remove( "dummyType" );
    },

    testProcessCallNoKownMethod : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", { } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = { name : "doFoo" };
      processor.processOperationArray( [ "call", "dummyId", "", properties ] );
      assertEquals( 0, targetObject.getLog().length );
      registry.remove( "dummyType" );
    },

    testProcessUnkownListener : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
        listeners : [ "focus" ]
      } );
      var targetObject = this._getDummyWidget( "dummyId" );
      var properties = {
        add : [ "foo" ]
      };
      processor.processOperationArray( [ "listen", "dummyId", properties ] );
      // succeeds by not crashing
      registry.remove( "dummyType" );
      targetObject.destroy();
    },

    testProcessNolisteners : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {} );
      var targetObject = this._getDummyWidget( "dummyId" );
      var properties = {
        add : [ "mouse" ]
      };
      processor.processOperationArray( [ "listen", "dummyId", properties ] );
      // NOTE: hasEventListeners may return "undefined" instead of "false"
      assertTrue( !targetObject.hasEventListeners( "mousedown" ) );
      assertTrue( !targetObject.hasEventListeners( "mouseup" ) );
      registry.remove( "dummyType" );
      targetObject.destroy();
    },

    testProcessSetterListener : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
        listeners : [ "foo", "bar" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "foo" : true
      };
      processor.processOperationArray( [ "listen", "dummyId", properties ] );
      properties = {
        "foo" : false
      };
      processor.processOperationArray( [ "listen", "dummyId", properties ] );
      assertEquals(  [ "fooListener", true, "fooListener", false ], targetObject.getLog() );
      targetObject.destroy();
    },

    testProcessCustomListener : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
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
      processor.processOperationArray( [ "listen", "dummyId", properties ] );
      assertTrue( targetObject.getMyData( "barListener" ) );
      properties = {
        "bar" : false
      };
      processor.processOperationArray( [ "listen", "dummyId", properties ] );
      assertNull( targetObject.getMyData( "barListener" ) );
      targetObject.destroy();
    },

    testProcessMessage : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
        properties : [ "width", "height" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var operation1 = [ "set", "dummyId", { "height" : 33 } ];
      var operation2 = [ "set", "dummyId", { "width" : 24 } ];
      var message = {
        "head" : {},
        "operations" : [ operation1, operation2 ]
      };
      processor.processMessage( message );
      assertEquals( [ "height", 33, "width", 24 ], targetObject.getLog() );
      registry.remove( "dummyType" );
    },

    testSetError : function() {
      var registry = rwt.remote.HandlerRegistry;
      var processor = rwt.remote.MessageProcessor;
      registry.add( "dummyType", {
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
        processor.processMessage( message );
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
      registry.remove( "dummyType" );
    },

    testProcessHeadSetRequestCounter : function() {
      var processor = rwt.remote.MessageProcessor;
      var message = {
        "head": {
          "requestCounter": 3
        },
        "operations" : []
      };
      processor.processMessage( message );
      var req = rwt.remote.Server.getInstance();
      assertEquals( 3, req.getRequestCounter() );
    },

  // TODO : how to test adapters?
  // construct + (all setter once => no crash) + specific cases?

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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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