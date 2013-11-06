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

    testAddFunctionCreateElement : function() {
      registry.add(  {
        "cellType" : "foo",
        "contentType" : "bar",
        "renderContent" : function(){}
      } );

      var element = registry.getRendererFor( "foo" ).createElement( {} );
      assertEquals( "div", element.tagName.toLowerCase() );
      assertEquals( "absolute", element.style.position );
      assertEquals( "hidden", element.style.overflow );
      assertEquals( "rwt-cell", element.className || element.getAttribute( "class" ) );
    },

    testWrapFunctionCreateElement : function() {
      var arg;
      registry.add(  {
        "cellType" : "foo",
        "contentType" : "bar",
        "renderContent" : function(){},
        "createElement" : function() {
          arg = arguments[ 0 ];
          return document.createElement( "input" );
        }
      } );

      var element = registry.getRendererFor( "foo" ).createElement( { "x" : 123 } );
      assertEquals( { "x" : 123 }, arg );
      assertEquals( "input", element.tagName.toLowerCase() );
      assertEquals( "absolute", element.style.position );
      assertEquals( "hidden", element.style.overflow );
    },

    testAddFunctionCreateElement_ForSelectableCell : function() {
      registry.add(  {
        "cellType" : "foo",
        "contentType" : "bar",
        "renderContent" : function(){}
      } );

      var element = registry.getRendererFor( "foo" ).createElement( { "selectable" : true } );
      assertEquals( "rwt-cell-selectable", element.className || element.getAttribute( "class" ) );
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
    },

    testTextRenderer_CreateElementSetsTextOverflow : function() {
      var defaultRegistry = rwt.widgets.util.CellRendererRegistry.getInstance();
      var create = defaultRegistry.getRendererFor( "text" ).createElement;
      assertEquals( "ellipsis", create( {} ).style.textOverflow );
    },

    testTextRenderer_CreateElementSetsHorizontalAlignment : function() {
      var defaultRegistry = rwt.widgets.util.CellRendererRegistry.getInstance();
      var create = defaultRegistry.getRendererFor( "text" ).createElement;
      assertEquals( "left", create( { "alignment" : {} } ).style.textAlign );
      assertEquals( "left", create( { "alignment" : { "LEFT" : true } } ).style.textAlign );
      assertEquals( "center", create( { "alignment" : { "H_CENTER" : true } } ).style.textAlign );
      assertEquals( "right", create( { "alignment" : { "RIGHT" : true } } ).style.textAlign );
    },

    testTextRenderer_CreateElementSetsWrap : function() {
      var defaultRegistry = rwt.widgets.util.CellRendererRegistry.getInstance();
      var create = defaultRegistry.getRendererFor( "text" ).createElement;
      assertEquals( "nowrap", create( {} ).style.whiteSpace );
      assertTrue( "nowrap" != create( { "wrap" : true } ).style.whiteSpace );
    },

    testImageRenderer_CreateElementSetsAlignmentLeftTop : function() {
      var defaultRegistry = rwt.widgets.util.CellRendererRegistry.getInstance();
      var create = defaultRegistry.getRendererFor( "image" ).createElement;

      var element = create( { "alignment" : { "LEFT" : true, "TOP" : true } } );

      var position = element.style.backgroundPosition;
      assertTrue( "left top" === position || "0% 0%" === position );
    },

    testImageRenderer_CreateElementSetsAlignmentCenterCenter : function() {
      var defaultRegistry = rwt.widgets.util.CellRendererRegistry.getInstance();
      var create = defaultRegistry.getRendererFor( "image" ).createElement;

      var element = create( { "alignment" : { "H_CENTER" : true, "V_CENTER" : true } } );

      var position = element.style.backgroundPosition;
      assertTrue( "center" === position || "center center" === position || "50% 50%" === position );
    },

    testImageRenderer_CreateElementSetsAlignmentDefault : function() {
      var defaultRegistry = rwt.widgets.util.CellRendererRegistry.getInstance();
      var create = defaultRegistry.getRendererFor( "image" ).createElement;

      var element = create( { "alignment" : {} } );

      var position = element.style.backgroundPosition;
      assertTrue( "center" === position || "center center" === position || "50% 50%" === position );
    },

    testImageRenderer_CreateElementSetsAlignmentRightBottom : function() {
      var defaultRegistry = rwt.widgets.util.CellRendererRegistry.getInstance();
      var create = defaultRegistry.getRendererFor( "image" ).createElement;

      var element = create( { "alignment" : { "RIGHT" : true, "BOTTOM" : true } } );

      var position = element.style.backgroundPosition;
      assertTrue( "right bottom" === position || "100% 100%" === position );
    },


    testImageRenderer_CreateElementSetsScaleModeFit : function() {
      var defaultRegistry = rwt.widgets.util.CellRendererRegistry.getInstance();
      var create = defaultRegistry.getRendererFor( "image" ).createElement;

      var element = create( {
        "scaleMode" : "FIT",
        "alignment" : { "RIGHT" : true, "BOTTOM" : true }
      } );

      var position = element.style.backgroundPosition;
      var size = element.style.backgroundSize;
      assertTrue( "center" === position || "center center" === position || "50% 50%" === position );
      assertEquals( "contain", size );
    },

    testImageRenderer_CreateElementSetsScaleModeFill : function() {
      var defaultRegistry = rwt.widgets.util.CellRendererRegistry.getInstance();
      var create = defaultRegistry.getRendererFor( "image" ).createElement;

      var element = create( {
        "scaleMode" : "FILL",
        "alignment" : { "RIGHT" : true, "BOTTOM" : true }
      } );

      var position = element.style.backgroundPosition;
      var size = element.style.backgroundSize;
      assertTrue( "center" === position || "center center" === position || "50% 50%" === position );
      assertEquals( "cover", size );
    }

  }

} );

}());
