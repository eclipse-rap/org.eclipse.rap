/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function() {

var FunctionFactory = rwt.scripting.FunctionFactory;
var SWT = rwt.scripting.SWT;

rwt.qx.Class.define( "org.eclipse.rap.clientscripting.Function_Test", {

  extend : rwt.qx.Object,

  members : {

    testCreateFunctionWrongNamed : function() {
      var code = "function foo(){}";
      try {
        FunctionFactory.createFunction( code, "handleEvent" );
        fail();
      } catch( ex ) {
        // expected
      }
    },

    /*global global:true */
    testCreateFunctionWithHelper : function() {
      var code = "var foo = function(){  global = 1;  };var handleEvent = function(){ foo(); };";
      var listener = FunctionFactory.createFunction( code, "handleEvent" );
      listener();
      assertEquals( 1, global );
      delete global; // An alternative would be to create a storage for such cases in TestUtil
    },

    testCreateFunctionSyntaxError : function() {
      var code = "null.no!;";
      try {
        FunctionFactory.createFunction( code, "handleEvent" );
        fail();
      } catch( ex ) {
        // expected
      }
    },

    testCreateFunctionNoFunction : function() {
      var code = "1";
      try {
        FunctionFactory.createFunction( code, "handleEvent" );
        fail();
      } catch( ex ) {
        // expected
      }
    },

    testCreateFunctionByProtocol : function() {
      var ObjectManager = rwt.remote.ObjectRegistry;
      var processor = rwt.remote.MessageProcessor;
      var code = "var handleEvent = function(){};";

      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.scripting.Function",
        "properties" : {
          "scriptCode" : code,
          "name" : "handleEvent"
        }
      } );

      var result = ObjectManager.getObject( "w3" );
      assertTrue( result instanceof Function );
    },

    testCallWithArgument : function() {
      var code = "function handleEvent( e ){ e.x++; }";
      var listener = FunctionFactory.createFunction( code, "handleEvent" );

      var event = {
        x : 1
      };

      listener( event );

      assertEquals( 2, event.x );
    },

    testNoContext : function() {
      var code = "var handleEvent = function(){ this.x++; }";
      var listener = FunctionFactory.createFunction( code, "handleEvent" );
      listener.x = 1;

      listener();

      assertEquals( 1, listener.x );
    },

    testImportSWT : function() {
      var ObjectManager = rwt.remote.ObjectRegistry;
      var processor = rwt.remote.MessageProcessor;
      var code = "var handleEvent = function( obj ){ obj.SWT = SWT;};";

      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.scripting.Function",
        "properties" : {
          "scriptCode" : code,
          "name" : "handleEvent"
        }
      } );
      var result = ObjectManager.getObject( "w3" );
      var obj = {};

      result( obj );

      assertIdentical( rwt.scripting.SWT, obj.SWT );
    }

  }

} );

}());
