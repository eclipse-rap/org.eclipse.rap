/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
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
      assertIdentical( undefined, registry.getAdapter( "fooKey" ) );
    },

    testProcessSet : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownProperties : [ "width", "height" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var details = {
        "height" : 33,
        "width" : 24
      };
      var operation = {
        "target" : "dummyId",
        "type" : "set",
        "details" : details
      };
      processor.processOperation( operation );
      assertEquals( [ "width", 24, "height", 33 ], targetObject.getLog() );
      registry.remove( "dummyType" );
    },

    testProcessSetLessDetails : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      var targetObject = this._getDummyTarget( "dummyId" );
      registry.add( "dummyType", {
        knownProperties : [ "width", "height" ]
      } );
      var details = {
        "height" : 33
      };
      var operation = {
        "target" : "dummyId",
        "type" : "set",
        "details" : details
      };
      processor.processOperation( operation );
      assertEquals( [ "height", 33 ], targetObject.getLog() );
      registry.remove( "dummyType" );
    },

    testProcessMoreDetails : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownProperties : [ "width", "height" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var details = {
        "height" : 33,
        "width" : 24,
        "top" : 14
      };
      var operation = {
        "target" : "dummyId",
        "type" : "set",
        "details" : details
      };
      processor.processOperation( operation );
      assertEquals( [ "width", 24, "height", 33 ], targetObject.getLog() );
      registry.remove( "dummyType" );
    },

    testProcessSetNoKnownProperties : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {} );
      var targetObject = this._getDummyTarget( "dummyId" );
      var details = {
        "height" : 33,
        "width" : 24
      };
      var operation = {
        "target" : "dummyId",
        "type" : "set",
        "details" : details
      };
      processor.processOperation( operation );
      assertEquals( [], targetObject.getLog() );
      registry.remove( "dummyType" );
    },

    testProcessCreate : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      var constr = this._getDummyContructor();
      registry.add( "dummyType", {
        constructor : constr 
      } );
      var details = {
        type : "dummyType"
      };
      var operation = {
        "target" : "dummyId",
        "type" : "create",
        "details" : details
      };
      processor.processOperation( operation );
      var result = this._getTargetById( "dummyId" );
      assertTrue( result instanceof constr );
      registry.remove( "dummyType" );
    },

    testProcessCreateAdapterHasNoConstructorFails : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
      } );
      var details = {
        type : "dummyType"
      };
      var error = null;
      var operation = {
        "target" : "dummyId",
        "type" : "create",
        "details" : details
      };
      try { 
        processor.processOperation( operation );
      } catch ( ex ) {
        error = ex;
      }
      assertNotNull( error );
      registry.remove( "dummyType" );
    },

    testProcessCreateControl : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        constructor : this._getDummyContructor(),
        isControl : true
      } );
      var details = {
        type : "dummyType"
      };
      var operation = {
        "target" : "dummyId",
        "type" : "create",
        "details" : details
      };
      processor.processOperation( operation );
      var result = this._getTargetById( "dummyId" );
      assertTrue( this._isControl( result ) );
      registry.remove( "dummyType" );
    },

    testProcessCreateWithStyleFlags : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        constructor : this._getDummyContructor(),
        knownStyles : [ "FLAT", "BORDER" ]
      } );
      var details = {
        type : "dummyType",
        style : [ "BORDER", "FLAT" ]
      };
      var operation = {
        "target" : "dummyId",
        "type" : "create",
        "details" : details
      };
      processor.processOperation( operation );
      var result = this._getTargetById( "dummyId" );
      // NOTE: Order is NOT relevant!
      assertTrue( result.getLog().indexOf( "rwt_BORDER" ) !== -1 );
      assertTrue( result.getLog().indexOf( "rwt_FLAT" ) !== -1 );
      registry.remove( "dummyType" );
    },

    testProcessCreateWithMoreThanKnownStyleFlags : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        constructor : this._getDummyContructor(),
        knownStyles : [ "FLAT", "BORDER" ]
      } );
      var details = {
        type : "dummyType",
        style : [ "BORDER", "FLAT", "TOP" ]
      };
      var operation = {
        "target" : "dummyId",
        "type" : "create",
        "details" : details
      };
      processor.processOperation( operation );
      var result = this._getTargetById( "dummyId" );
      assertTrue( result.getLog().indexOf( "rwt_TOP" ) === -1 );
      assertTrue( result.getLog().indexOf( "rwt_BORDER" ) !== -1 );
      assertTrue( result.getLog().indexOf( "rwt_FLAT" ) !== -1 );
      registry.remove( "dummyType" );
    },

    testProcessCreateWithLessThanKnownStyleFlags : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        constructor : this._getDummyContructor(),
        knownStyles : [ "FLAT", "BORDER", "TOP" ]
      } );
      var details = {
        type : "dummyType",
        style : [ "BORDER", "FLAT" ]
      };
      var operation = {
        "target" : "dummyId",
        "type" : "create",
        "details" : details
      };
      processor.processOperation( operation );
      var result = this._getTargetById( "dummyId" );
      assertTrue( result.getLog().indexOf( "rwt_TOP" ) === -1 );
      assertTrue( result.getLog().indexOf( "rwt_BORDER" ) !== -1 );
      assertTrue( result.getLog().indexOf( "rwt_FLAT" ) !== -1 );
      registry.remove( "dummyType" );
    },

    testProcessCreateWithParent : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
         constructor : this._getDummyContructor()
      } );
      var details = {
        type : "dummyType",
        parent : "dummyParentId"
      };
      var parent = this._getDummyTarget( "dummyParentId" );
      var operation = {
        "target" : "dummyId",
        "type" : "create",
        "details" : details
      };
      processor.processOperation( operation );
      var result = this._getTargetById( "dummyId" );
      assertIdentical( parent, result.getParent() );
      registry.remove( "dummyType" );
    },

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
        "type" : "destroy",
        "details" : null
      };
      processor.processOperation( operation );
      assertEquals( [ "destroy" ], target.getLog() );  
      assertNull( target.getParent() );  
      assertTrue( this._getTargetById( "dummyId" ) === undefined );
      registry.remove( "dummyType" );
    },

    testProcessDo : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownActions : [ "doFoo" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var details = {
        name : "doFoo"
      };
      var operation = {
        "target" : "dummyId",
        "type" : "do",
        "details" : details
      };
      processor.processOperation( operation );
      assertEquals( "foo", targetObject.getLog()[ 0 ] );
      assertEquals( 0, targetObject.getLog()[ 1 ].length );
      registry.remove( "dummyType" );
    },

    testProcessDoWithParameters : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownActions : [ "doFoo" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var details = {
        name : "doFoo",
        parameter : [ 17, 42 ]
     };
      var operation = {
        "target" : "dummyId",
        "type" : "do",
        "details" : details
      };
      processor.processOperation( operation );
      assertEquals( "foo", targetObject.getLog()[ 0 ] );
      var args = targetObject.getLog()[ 1 ];
      assertEquals( 2, args.length );
      assertEquals( 17, args[ 0 ] );
      assertEquals( 42, args[ 1 ] );
      registry.remove( "dummyType" );
    },
    
    testProcessDoUnkownAction : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownActions : [ "doBar" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var details = {
        name : "doFoo"
      };
      var operation = {
        "target" : "dummyId",
        "type" : "do",
        "details" : details
      };
      processor.processOperation( operation );
      assertEquals( 0, targetObject.getLog().length );
      registry.remove( "dummyType" );
    },

    testProcessFocusListener : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownEvents : [ "focus" ]
      } );
      var targetObject = this._getDummyWidget( "dummyId" );
      var details = {
        add : [ "focus" ]
      };
      var operation = {
        "target" : "dummyId",
        "type" : "listen",
        "details" : details
      };
      processor.processOperation( operation );
      assertTrue( targetObject.hasEventListeners( "focusin" ) );
      assertTrue( targetObject.hasEventListeners( "focusout" ) );
      details = {
        remove : [ "focus" ]
      };
      var operation = {
        "target" : "dummyId",
        "type" : "listen",
        "details" : details
      };
      processor.processOperation( operation );
      // TODO: How to check for a specific listener?
      assertFalse( targetObject.hasEventListeners( "focusin" ) );
      assertFalse( targetObject.hasEventListeners( "focusout" ) );
      registry.remove( "dummyType" );
      targetObject.destroy();
    },

    testProcessMouseListener : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownEvents : [ "mouse" ]
      } );
      var targetObject = this._getDummyWidget( "dummyId" );
      var details = {
        add : [ "mouse" ]
      };
      var operation = {
        "target" : "dummyId",
        "type" : "listen",
        "details" : details
      };
      processor.processOperation( operation ); 
      assertTrue( targetObject.hasEventListeners( "mousedown" ) );
      assertTrue( targetObject.hasEventListeners( "mouseup" ) );
      details = {
        remove : [ "mouse" ]
      };
      var operation = {
        "target" : "dummyId",
        "type" : "listen",
        "details" : details
      };
      processor.processOperation( operation );
      // TODO: How to check for a specific listener?
      assertFalse( targetObject.hasEventListeners( "mousedown" ) );
      assertFalse( targetObject.hasEventListeners( "mouseup" ) );
      registry.remove( "dummyType" );
      targetObject.destroy();
    },

    testProcessUnkownListener : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownEvents : [ "focus" ]
      } );
      var targetObject = this._getDummyWidget( "dummyId" );
      var details = {
        add : [ "mouse" ]
      };
      var operation = {
        "target" : "dummyId",
        "type" : "listen",
        "details" : details
      };
      processor.processOperation( operation ); 
      // NOTE: hasEventListeners may return "undefined" instead of "false"      
      assertTrue( !targetObject.hasEventListeners( "mousedown" ) );
      assertTrue( !targetObject.hasEventListeners( "mouseup" ) );
      processor._addListener( targetObject, "mouse" );
      details = {
        remove : [ "mouse" ]
      };
      assertTrue( targetObject.hasEventListeners( "mousedown" ) );
      assertTrue( targetObject.hasEventListeners( "mouseup" ) );
      var operation = {
        "target" : "dummyId",
        "type" : "listen",
        "details" : details
      };
      processor.processOperation( operation );
      assertTrue( targetObject.hasEventListeners( "mousedown" ) );
      assertTrue( targetObject.hasEventListeners( "mouseup" ) );
      registry.remove( "dummyType" );
      targetObject.destroy();
    },

    testProcessNoKnownListener : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {} );
      var targetObject = this._getDummyWidget( "dummyId" );
      var details = {
        add : [ "mouse" ]
      };
      var operation = {
        "target" : "dummyId",
        "type" : "listen",
        "details" : details
      };
      processor.processOperation( operation );
      // NOTE: hasEventListeners may return "undefined" instead of "false"      
      assertTrue( !targetObject.hasEventListeners( "mousedown" ) );
      assertTrue( !targetObject.hasEventListeners( "mouseup" ) );
      processor._addListener( targetObject, "mouse" );
      details = {
        remove : [ "mouse" ]
      };
      assertTrue( targetObject.hasEventListeners( "mousedown" ) );
      assertTrue( targetObject.hasEventListeners( "mouseup" ) );
      var operation = {
        "target" : "dummyId",
        "type" : "listen",
        "details" : details
      };
      processor.processOperation( operation );
      assertTrue( targetObject.hasEventListeners( "mousedown" ) );
      assertTrue( targetObject.hasEventListeners( "mouseup" ) );
      registry.remove( "dummyType" );
      targetObject.destroy();
    },

    testProcessListenerDoesNotExistFails : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownEvents : [ "foo", "bar" ]
      } );
      var targetObject = this._getDummyWidget( "dummyId" );
      var details = {
        add : [ "foo" ]
      };
      var log = [];
      try {
        var operation = {
          "target" : "dummyId",
          "type" : "listen",
          "details" : details
        };
        processor.processOperation( operation );
      } catch( ex ) {
        log.push( ex );
      }
      details = {
        remove : [ "bar" ]
      };
      try {
        var operation = {
          "target" : "dummyId",
          "type" : "listen",
          "details" : details
        };
        processor.processOperation( operation );
      } catch( ex ) {
        log.push( ex );
      }
      assertEquals( 2, log.length );
      registry.remove( "dummyType" );
      targetObject.destroy();
    },

    testProcessExecute : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      globalTemp = 1;
      var details = {
        scriptType : "text/javascript",
        script : "globalTemp++;"
      };
      var operation = {
        "target" : "dummyId",
        "type" : "execute",
        "details" : details
      };
      processor.processOperation( operation );
      assertEquals( 2, globalTemp );
      delete globalTemp;
    },

    testProcessExecuteWrongType : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      globalTemp = 1;
      var details = {
        scriptType : "java",
        script : "globalTemp++;"
      };
      var operation = {
        "target" : "dummyId",
        "type" : "execute",
        "details" : details
      };
      processor.processOperation( operation );
      assertEquals( 1, globalTemp );
      delete globalTemp;
    },

    testProcessExecuteScriptMissing : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      globalTemp = 1;
      var details = {
        scriptType : "text/javascript"
      };
      var operation = {
        "target" : "dummyId",
        "type" : "execute",
        "details" : details
      };
      processor.processOperation( operation );
      assertEquals( 1, globalTemp );
      delete globalTemp;
    },


    testProcessExecuteScriptWithError : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      var details = {
        scriptType : "text/javascript",
        script : "x=null;x.test().bla();"
      };
      var operation = {
        "target" : "dummyId",
        "type" : "execute",
        "details" : details
      };
      processor.processOperation( operation );
      //suceeds by not crashing
    },
    
    testProcessMessage : function() {
      var registry = org.eclipse.rwt.protocol.AdapterRegistry;
      var processor = org.eclipse.rwt.protocol.Processor;
      registry.add( "dummyType", {
        knownProperties : [ "width", "height" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var operation1 = {
        "target" : "dummyId",
        "type" : "set",
        "details" : {
          "height" : 33
        }
      };
      var operation2 = {
        "target" : "dummyId",
        "type" : "set",
        "details" : {
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
        knownProperties : [ "width", "height", "coolness" ]
      } );
      var targetObject = this._getDummyTarget( "dummyId" );
      var operation = {
        "target" : "dummyId",
        "type" : "set",
        "details" : {
          "coolness" : 99
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
      console.log( error ); 
      assertTrue( error instanceof Error );
      var message = error.message.toLowerCase();
      var expected1 = "operation \"set\" on target \"dummyid\" of type \"myclass\"";
      var expected2 = "details: { \"height\" : 33 }"; 
      var expected3 = "item: \"height\""; 
      var expected4 = "myerror";
      assertTrue( message.indexOf( expected1 ) !== - 1 );
      // TODO [tb] : implement enhanced information gathering
      //assertTrue( message.indexOf( expected2 ) !== - 1 );
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
          throw "myerror";
        },
        setUserData : function( key, value ) {
          this._userData[ key ] = value;
        },
        getUserData : function( key ) {
          return this._userData[ key ];
        },
        doFoo : function() {
          log.push( "foo", arguments );
        },
        destroy : function() {
          log.push( "destroy" );
        },
        getDisposed : function() {
          return false;
        },
        setToolTip : function() {
          // NOTE: Currently needed (as getDisposed) by WidgetManager
          // should not be the case. 
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
    
    _getDummyContructor : function() {
      var constr = function(){
      };
      constr.prototype = this._getDummyTarget();
      return constr;
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