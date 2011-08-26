/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ProtocolTest", {

  extend : qx.core.Object,
  
  members : {

    testAdapterRegistry : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var adapter = {};
      registry.add( "fooKey", adapter );
      assertIdentical( adapter, registry.getAdapter( "fooKey" ) );
      registry.remove( "fooKey" );
      try {
        registry.getAdapter( "fooKey" );
        fail();
      } catch( ex ) {
        // expected
      }
    },

    testProcessSet : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        properties : [ "width", "height" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "height" : 33,
        "width" : 24
      };
      var operation = {
        "target" : "dummyId",
        "action" : "set",
        "properties" : properties
      };
      processor.processOperation( operation );
      assertEquals( [ "width", 24, "height", 33 ], targetObject.getLog() );
      registry.remove( "dummyType" );
    },

    testProcessSetLessProperties : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      var targetObject = this._getDummyTarget( "dummyId" );
      registry.add( "dummyType", {
        properties : [ "width", "height" ]
      } );
      var properties = {
        "height" : 33
      };
      var operation = {
        "target" : "dummyId",
        "action" : "set",
        "properties" : properties
      };
      processor.processOperation( operation );
      assertEquals( [ "height", 33 ], targetObject.getLog() );
      registry.remove( "dummyType" );
    },

    testProcessMoreProperties : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        properties : [ "width", "height" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "height" : 33,
        "width" : 24,
        "top" : 14
      };
      var operation = {
        "target" : "dummyId",
        "action" : "set",
        "properties" : properties
      };
      processor.processOperation( operation );
      assertEquals( [ "width", 24, "height", 33 ], targetObject.getLog() );
      registry.remove( "dummyType" );
    },

    testProcessSetNoproperties : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {} );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "height" : 33,
        "width" : 24
      };
      var operation = {
        "target" : "dummyId",
        "action" : "set",
        "properties" : properties
      };
      processor.processOperation( operation );
      assertEquals( [], targetObject.getLog() );
      registry.remove( "dummyType" );
    },

    testProcessSetPropertyHandler : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
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
      var operation = {
        "target" : "dummyId",
        "action" : "set",
        "properties" : properties
      };
      processor.processOperation( operation );
      assertEquals( [ "coolness", 100 ], targetObject.getLog() );
      registry.remove( "dummyType" );
    },


    testProcessCreate : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      var factory = this._getDummyFactory();
      registry.add( "dummyType", {
        factory : factory
      } );
      var properties = {
        style : []
      };
      var operation = {
        type : "dummyType",
        "target" : "dummyId",
        "action" : "create",
        "properties" : properties
      };
      processor.processOperation( operation );
      var result = this._getTargetById( "dummyId" );
      assertEquals( "myclass", result.classname );
      registry.remove( "dummyType" );
    },

    testProcessCreateAdapterHasNoConstructorFails : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
      } );
      var properties = {
      };
      var error = null;
      var operation = {
        type : "dummyType",
        "target" : "dummyId",
        "action" : "create",
        "properties" : properties
      };
      try { 
        processor.processOperation( operation );
      } catch ( ex ) {
        error = ex;
      }
      assertNotNull( error );
      registry.remove( "dummyType" );
    },

    testProcessCreateWithStyleStates : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        factory : this._getDummyFactory()
      } );
      var properties = {
        style : [ "BORDER", "FLAT" ]
      };
      var operation = {
        type : "dummyType",
        "target" : "dummyId",
        "action" : "create",
        "properties" : properties
      };
      processor.processOperation( operation );
      var result = this._getTargetById( "dummyId" );
      // NOTE: Order is NOT relevant!
      assertTrue( result.getLog().indexOf( "rwt_BORDER" ) !== -1 );
      assertTrue( result.getLog().indexOf( "rwt_FLAT" ) !== -1 );
      registry.remove( "dummyType" );
    },

    testProcessCreateGetStyleMap : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        factory : this._getDummyFactory()
      } );
      var properties = {
        style : [ "BORDER", "FLAT" ]
      };
      var operation = {
        type : "dummyType",
        "target" : "dummyId",
        "action" : "create",
        "properties" : properties
      };
      processor.processOperation( operation );
      var result = this._getTargetById( "dummyId" );
      var style = result.getStyleMap();
      assertTrue( style.BORDER );
      assertTrue( style.FLAT );
      registry.remove( "dummyType" );
    },

    testProcessCreateGetProperties : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        factory : this._getDummyFactory()
      } );
      var properties = {
        style : [ "BORDER", "FLAT" ]
      };
      var operation = {
        type : "dummyType",
        "target" : "dummyId",
        "action" : "create",
        "properties" : properties
      };
      processor.processOperation( operation );
      var result = this._getTargetById( "dummyId" );
      assertIdentical( properties.style, result.getProperties().style );
      registry.remove( "dummyType" );
    },

    testProcessCreateAndSetProperties : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        factory : this._getDummyFactory(),
        properties : [ "width", "height" ]
      } );
      var properties = {
        style : [],
        width : 34
      };
      var operation = {
        type : "dummyType",
        "target" : "dummyId",
        "action" : "create",
        "properties" : properties
      };
      processor.processOperation( operation );
      var result = this._getTargetById( "dummyId" );
      assertEquals( [ "width", 34 ], result.getLog() );
      registry.remove( "dummyType" );
    },

//    testProcessCreateWithParent : function() {
//      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
//      var processor = org.eclipse.rwt.protocol.Processor;
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
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {} );
      var target = this._getDummyTarget( "dummyId" );
      target.setParent( {
        getChildren : function() {
          return [ target ];
        }
      } );
      var operation = {
        "target" : "dummyId",
        "action" : "destroy"
      };
      processor.processOperation( operation );
      assertEquals( [ "destroy" ], target.getLog() );  
      assertNull( target.getParent() );  
      assertTrue( this._getTargetById( "dummyId" ) === undefined );
      registry.remove( "dummyType" );
    },

    testProcessDestroyWithDestructor : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
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
      var operation = {
        "target" : "dummyId",
        "action" : "destroy"
      };
      processor.processOperation( operation );
      assertEquals( [ "foo", "destroy" ], target.getLog() );
      assertTrue( this._getTargetById( "dummyId" ) === undefined );
      registry.remove( "dummyType" );
    },

    testProcessCall : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownMethods : [ "doFoo" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
      };
      var operation = {
        "target" : "dummyId",
        "action" : "call",
        "method" : "doFoo",
        "properties" : properties
      };
      processor.processOperation( operation );
      assertEquals( "foo", targetObject.getLog()[ 0 ] );
      assertIdentical( properties, targetObject.getLog()[ 1 ] );
      registry.remove( "dummyType" );
    },

    testProcessCustomCall : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownMethods : [ "doBar" ],
        methodHandler : {
          "doBar" : function( widget, properties ) {
            widget.doFoo( properties );
          }
        }
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {};
      var operation = {
        "target" : "dummyId",
        "action" : "call",
        "method" : "doBar",
        "properties" : properties
      };
      processor.processOperation( operation );
      assertEquals( "foo", targetObject.getLog()[ 0 ] );
      assertIdentical( properties, targetObject.getLog()[ 1 ] );
      registry.remove( "dummyType" );
    },

    testProcessCallWithParameters : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownMethods : [ "doFoo" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "foo" : [ 17, 42 ]
     };
      var operation = {
        "target" : "dummyId",
        "action" : "call",
        "method" : "doFoo",
        "properties" : properties
      };
      processor.processOperation( operation );
      assertEquals( "foo", targetObject.getLog()[ 0 ] );
      var args = targetObject.getLog()[ 1 ];
      assertEquals( properties, args );
      registry.remove( "dummyType" );
    },
    
    testProcessCallUnkownMethod : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownMethods : [ "doBar" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        name : "doFoo"
      };
      var operation = {
        "target" : "dummyId",
        "action" : "call",
        "properties" : properties
      };
      processor.processOperation( operation );
      assertEquals( 0, targetObject.getLog().length );
      registry.remove( "dummyType" );
    },
    
    testProcessCallNoKownMethod : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", { } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        name : "doFoo"
      };
      var operation = {
        "target" : "dummyId",
        "action" : "call",
        "properties" : properties
      };
      processor.processOperation( operation );
      assertEquals( 0, targetObject.getLog().length );
      registry.remove( "dummyType" );
    },

    testProcessUnkownListener : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownListeners : [ "focus" ]
      } );
      var targetObject = this._getDummyWidget( "dummyId" );
      var properties = {
        add : [ "foo" ]
      };
      var operation = {
        "target" : "dummyId",
        "action" : "listen",
        "properties" : properties
      };
      processor.processOperation( operation ); 
      // succeeds by not crashing
      registry.remove( "dummyType" );
      targetObject.destroy();
    },

    testProcessNoknownListeners : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {} );
      var targetObject = this._getDummyWidget( "dummyId" );
      var properties = {
        add : [ "mouse" ]
      };
      var operation = {
        "target" : "dummyId",
        "action" : "listen",
        "properties" : properties
      };
      processor.processOperation( operation );
      // NOTE: hasEventListeners may return "undefined" instead of "false"      
      assertTrue( !targetObject.hasEventListeners( "mousedown" ) );
      assertTrue( !targetObject.hasEventListeners( "mouseup" ) );
      registry.remove( "dummyType" );
      targetObject.destroy();
    },

    testProcessSetterListener : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownListeners : [ "foo", "bar" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "foo" : true
      };
      var operation = {
        "target" : "dummyId",
        "action" : "listen",
        "properties" : properties
      };
      processor.processOperation( operation );
      properties = {
        "foo" : false
      };
      var operation = {
        "target" : "dummyId",
        "action" : "listen",
        "properties" : properties
      };
      processor.processOperation( operation );
      assertEquals(  [ "fooListener", true, "fooListener", false ], targetObject.getLog() );
      targetObject.destroy();
    },

    testProcessCustomListener : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownListeners : [ "foo", "bar" ],
        listenerHandler : {
          "bar" : function( targetObject, value ) {
            targetObject.setUserData( "barListener", value ? true : null );
          }
        }
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var properties = {
        "bar" : true
      };
      var operation = {
        "target" : "dummyId",
        "action" : "listen",
        "properties" : properties
      };
      processor.processOperation( operation );
      assertTrue( targetObject.getUserData( "barListener" ) );
      properties = {
        "bar" : false
      };
      var operation = {
        "target" : "dummyId",
        "action" : "listen",
        "properties" : properties
      };
      processor.processOperation( operation );
      assertNull( targetObject.getUserData( "barListener" ) );
      targetObject.destroy();
    },

    testProcessExecute : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      globalTemp = 1;
      var properties = {
      };
      var operation = {
        "scriptType" : "text/javascript",
        "content" : "globalTemp++;",
        "target" : "dummyId",
        "action" : "execute"
      };
      processor.processOperation( operation );
      assertEquals( 2, globalTemp );
      delete globalTemp;
    },

    testProcessExecuteWrongType : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      globalTemp = 1;
      var properties = {
        scriptType : "java",
        script : "globalTemp++;"
      };
      var operation = {
        "target" : "dummyId",
        "action" : "execute",
        "properties" : properties
      };
      processor.processOperation( operation );
      assertEquals( 1, globalTemp );
      delete globalTemp;
    },

    testProcessExecuteScriptMissing : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      globalTemp = 1;
      var properties = {
        scriptType : "text/javascript"
      };
      var operation = {
        "target" : "dummyId",
        "action" : "execute",
        "properties" : properties
      };
      processor.processOperation( operation );
      assertEquals( 1, globalTemp );
      delete globalTemp;
    },


    testProcessExecuteScriptWithError : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      var properties = {
        scriptType : "text/javascript",
        script : "x=null;x.test().bla();"
      };
      var operation = {
        "target" : "dummyId",
        "action" : "execute",
        "properties" : properties
      };
      processor.processOperation( operation );
      //suceeds by not crashing
    },
    
    testProcessMessage : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        properties : [ "width", "height" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var operation1 = {
        "target" : "dummyId",
        "action" : "set",
        "properties" : {
          "height" : 33
        }
      };
      var operation2 = {
        "target" : "dummyId",
        "action" : "set",
        "properties" : {
          "width" : 24
        }
      };
      var message = {
        "meta" : {},
        "operations" : [ operation1, operation2 ]
      };
      processor.processMessage( message );
      assertEquals( [ "height", 33, "width", 24 ], targetObject.getLog() );
      registry.remove( "dummyType" );      
    },

    testSetError : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        properties : [ "width", "height", "fail" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var operation = {
        "target" : "dummyId",
        "action" : "set",
        "properties" : {
          "fail" : 99
        }
      };
      var message = {
        "meta" : {},
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
        setUserData : function( key, value ) {
          this._userData[ key ] = value;
        },
        getUserData : function( key ) {
          return this._userData[ key ];
        },
        getProperties : function() {
          return this._prop;
        },
        getStyleMap : function() {
          return org.eclipse.rwt.protocol.AdapterUtil.createStyleMap( this._prop.style );
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
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        widgetManager.add( targetObject, targetId, true, "dummyType" );
      }
      return targetObject;
    },

    _getDummyFactory : function() {
      var constr = function( properties ) {
        if( properties ) {
          org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( this, properties.style );
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
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      return widgetManager.findWidgetById( id );
    },

    _isControl : function( target ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      return widgetManager.isControl( target );
    },

    _getDummyWidget : function( targetId ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var result = new qx.ui.basic.Terminator();
      result.addToDocument();
      result.setLeft( 10 );
      result.setTop( 10 );
      result.setWidth( 100 );
      result.setHeight( 20 ); 
      testUtil.flush();
      if( typeof targetId === "string" ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        widgetManager.add( result, targetId, true, "dummyType" );
      }
      return result;
    }

  }
  
} );