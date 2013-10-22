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

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

var registry;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.CellRendererRegistryTest", {

  extend : rwt.qx.Object,

  members : {

    setUp : function() {
      registry = new rwt.widgets.util.CellRendererRegistry();
    },

    tearDown : function() {
      registry = null;
    },

    testAdd_FailsWithNull : function() {
      try {
        registry.add( null );
        fail();
      } catch( ex ) {
        // expected
      }
    },

    testAdd_DoesNotFailWithMinimalRenderer : function() {
      try {
        registry.add( {
          "cellType" : "foo",
          "contentType" : "foo",
          "renderContent" : function(){}
        } );
      } catch( ex ) {
        fail();
      }
    },

    testAdd_FailsWithoutCellType : function() {
      try {
        registry.add( {
          "cellType" : null,
          "contentType" : "foo",
          "renderContent" : function(){}
        } );
        fail();
      } catch( ex ) {
      }
    },

    testAdd_FailsWithoutContentType : function() {
      try {
        registry.add( {
          "cellType" : "foo",
          "contentType" : null,
          "renderContent" : function(){}
        } );
        fail();
      } catch( ex ) {
        // expected
      }
    },

    testAdd_FailsForAddingSameCellTypeTwice : function() {
      registry.add( {
        "cellType" : "text",
        "contentType" : "foo",
        "renderContent" : function(){}
      } );
      try {
        registry.add( {
          "cellType" : "text",
          "contentType" : "foo",
          "renderContent" : function(){}
        } );
        fail();
      } catch( ex ) {
        // expected
      }
    },

    testGetRendererFor : function() {
      var renderer = {
        "cellType" : "foo",
        "contentType" : "bar",
        "renderContent" : function(){}
      };
      registry.add( renderer );

      assertIdentical( renderer, registry.getRendererFor( "foo" ) );
    },

    testAddFunctionShouldEscapeText : function() {
      registry.add(  {
        "cellType" : "foo",
        "contentType" : "bar",
        "renderContent" : function(){}
      } );
      registry.add(  {
        "cellType" : "foo2",
        "contentType" : "bar",
        "renderContent" : function(){},
        "shouldEscapeText" : rwt.util.Functions.returnTrue
      } );

      assertFalse( registry.getRendererFor( "foo" ).shouldEscapeText() );
      assertTrue( registry.getRendererFor( "foo2" ).shouldEscapeText() );
    },

    testGetAll : function() {
      registry.add( {
        "cellType" : "bar1",
        "contentType" : "foo",
        "renderContent" : function(){}
      } );
      registry.add( {
        "cellType" : "bar2",
        "contentType" : "foo",
        "renderContent" : function(){}
      } );

      var map = registry.getAll();

      assertTrue( map.bar1 != null );
      assertTrue( map.bar2 != null );
    },

    testGetAll_UpdatedAfterGet : function() {
      registry.add( {
        "cellType" : "bar1",
        "contentType" : "foo",
        "renderContent" : function(){}
      } );

      var map = registry.getAll();
      registry.add( {
        "cellType" : "bar2",
        "contentType" : "foo",
        "renderContent" : function(){}
      } );

      assertTrue( map.bar1 != null );
      assertTrue( map.bar2 != null );
    },

    testGetAll_ProtectedAgainstManipulation : function() {
      registry.add( {
        "cellType" : "bar1",
        "contentType" : "foo",
        "renderContent" : function(){}
      } );
      registry.add( {
        "cellType" : "bar2",
        "contentType" : "foo",
        "renderContent" : function(){}
      } );

      registry.getAll().bar1 = null;
      var map = registry.getAll();

      assertTrue( map.bar1 != null );
      assertTrue( map.bar2 != null );
    },

    testRemove : function() {
      var renderer = {
        "cellType" : "foo",
        "contentType" : "bar",
        "renderContent" : function(){}
      };
      registry.add( renderer );

      registry.removeRendererFor( "foo" );

      assertNull( renderer, registry.getRendererFor( "foo" ) );
    }

  }

} );

}());
