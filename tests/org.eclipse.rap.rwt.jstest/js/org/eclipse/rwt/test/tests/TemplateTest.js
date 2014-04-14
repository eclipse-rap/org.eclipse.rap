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

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var Template = rwt.widgets.util.Template;

var renderer = rwt.widgets.util.CellRendererRegistry.getInstance();

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.TemplateTest", {

  extend : rwt.qx.Object,

  members : {

    tearDown : function() {
      renderer.removeRendererFor( "fooType" );
    },

    testCreateWithEmptyTemplate : function() {
      var cells = [];
      var template = new Template( cells );
      assertIdentical( cells, template._cells );
    },

    testGetCellCount : function() {
      var template = createTemplate( [ "text", "text", "text" ] );

      assertEquals( 3, template.getCellCount() );
    },

    testCreateContainerFailsWithoutTarget : function() {
      var template = createTemplate( [ "text", "text", "text" ] );

      try {
        template._createContainer( {
          "element" : null,
          "zIndexOffset" : 100
        } );
        fail();
      } catch( ex ) {
        //expected
      }
    },

    testCreateContainerFailsWithoutZIndex : function() {
      var template = createTemplate( [ "text", "text", "text" ] );

      try {
        template._createContainer( {
          "element" : document.createElement( "div" ),
          "zIndexOffset" : null
        } );
        fail();
      } catch( ex ) {
        //expected
      }
    },

    testCreateContainerDoesNotFailWithMinimalArguments : function() {
      var template = createTemplate( [ "text", "text", "text" ] );

      var container = template._createContainer( {
        "element" : document.createElement( "div" ),
        "zIndexOffset" : 100
      } );
      assertTrue( container instanceof Object );
    },

    testRenderFailsWithoutValidContainer : function() {
      var template = createTemplate( [ "text", "text", "text" ] );

      try {
        template._render( { "bounds" : [ 0, 0, 100, 100 ], "container" : {}, "item" : null } );
        fail();
      } catch( ex ) {
        // expected
      }
    },

    testRenderCreatesElements_CreateOneTextElement : function() {
      var template = createTemplate( [ "text" ] );

      var element = render( template, createGridItem( [ "foo" ] ) );

      assertEquals( 1, element.children.length );
    },

    testRenderCreatesElements_TextElementStyles : function() {
      var template = createTemplate( [ "text" ] );

      var element = render( template, createGridItem( [ "foo" ] ) );

      assertEquals( "absolute", element.children[ 0 ].style.position );
      assertEquals( "hidden", element.children[ 0 ].style.overflow );
    },

    testRenderCreatesElements_CreateOneImageElement : function() {
      var template = createTemplate( [ "image" ] );

      var element = render( template, createGridItem( [], [ [ "foo.jpg", 10, 10 ] ] ) );

      assertEquals( 1, element.children.length );
    },

    testRenderCreatesElements_CreateMultipleElement : function() {
      var template = createTemplate( [ "text", "text", "text" ] );

      var element = render( template, createGridItem( [ "foo", "foo", "foo" ] ) );

      assertEquals( 3, element.children.length );
    },

    testRenderCreatesElements_ApplyZIndex : function() {
      var template = createTemplate( [ "text", "text", "text" ] );

      var element = render( template, createGridItem( [ "foo", null, "foo" ] ) );

      assertEquals( "100", element.children[ 0 ].style.zIndex );
      assertEquals( "102", element.children[ 1 ].style.zIndex );
    },

    testRenderCreatesElements_ReUsesElements : function() {
      var template = createTemplate( [ "text", "text", "text" ] );

      var container = createContainer( template );
      template._render( {
        "container" : container,
        "bounds" : [ 0, 0, 100, 100 ],
        "item" : createGridItem( [ "foo", "foo", "foo" ] )
      } );
      template._render( {
        "container" : container,
        "bounds" : [ 0, 0, 100, 100 ],
        "item" : createGridItem( [ "foo", "foo", "foo" ] )
      } );

      assertEquals( 3, container.element.children.length );
    },

    testRenderCreatesElements_CreateNoTextElementWithoutTextOrBackground : function() {
      var template = createTemplate( [ "text", "text", "text" ] );

      var element = render( template, createGridItem( [ "foo", "", "foo" ] ) );

      assertEquals( 2, element.children.length );
    },

    testRenderCreatesElements_CreateTextElementWithOnlyBackground : function() {
      var template = createTemplate( [ "text", "text", "text" ] );
      var item = createGridItem( [ "", "", "" ] );

      item.setCellBackgrounds( [ null, "rgb( 3, 4, 5 )", null ] );

      var element = render( template, item );

      assertEquals( 1, element.children.length );
    },

    testRenderCellLeft_LeftIsOffset : function() {
      var template = createTemplate( { "bindingIndex" : 0, "type" : "text", "left" : [ 0, 15 ] } );

      var element = render( template, createGridItem( [ "foo" ] ) );

      assertEquals( 15, getLeft( element.firstChild ) );
    },

    testRenderCellLeft_LeftIsOffsetWithRenderOffset : function() {
      var template = createTemplate( { "bindingIndex" : 0, "type" : "text", "left" : [ 0, 15 ] } );

      var bounds = [ 20, 0, 100, 100 ];
      var element = render( template, createGridItem( [ "foo" ] ), { "bounds" : bounds } );

      assertEquals( 35, getLeft( element.firstChild ) );
    },

    testRenderCellLeft_LeftIsPercentage : function() {
      var template = createTemplate( { "bindingIndex" : 0, "type" : "text", "left" : [ 10, 0 ] } );

      var bounds = [ 0, 0, 200, 200 ];
      var element = render( template, createGridItem( [ "foo" ] ), { "bounds" : bounds } );

      assertEquals( 20, getLeft( element.firstChild ) );
    },

    testRenderCellLeft_LeftIsPercentageAndOffset : function() {
      var template = createTemplate( { "bindingIndex" : 0, "type" : "text", "left" : [ 10, 15 ] } );

      var bounds = [ 0, 0, 200, 200 ];
      var element = render( template, createGridItem( [ "foo" ] ), { "bounds" : bounds }  );

      assertEquals( 35, getLeft( element.firstChild ) );
    },

    testGetCellLeft_LeftIsUndefined : function() {
      var template = createTemplate( {
        "bindingIndex" : 0,
        "type" : "text",
        "width" : 10,
        "right" : [ 0, 15 ],
        "left" : undefined
      } );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 75, getLeft( element.firstChild ) );
    },

    testGetCellLeft_LeftIsUndefinedWithRenderOffset : function() {
      var template = createTemplate( {
        "bindingIndex" : 0,
        "type" : "text",
        "width" : 10,
        "right" : [ 0, 15 ],
        "left" : undefined
      } );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 10, 0, 100, 30 ] } );

      assertEquals( 85, getLeft( element.firstChild ) );
    },

    testGetCellTop_TopIsOffset : function() {
      var template = createTemplate( { "bindingIndex" : 0, "type" : "text", "top" : [ 0, 12 ] } );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 12, getTop( element.firstChild ) );
    },

    testGetCellTop_TopIsPercentage : function() {
      var template = createTemplate( { "bindingIndex" : 0, "type" : "text", "top" : [ 10, 0 ] } );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 3, getTop( element.firstChild ) );
    },

    testGetCellTop_TopIsPercentageAndOffset : function() {
      var template = createTemplate( { "bindingIndex" : 0, "type" : "text", "top" : [ 10, 3 ] } );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 6, getTop( element.firstChild ) );
    },

    testGetCellTop_TopIsOffsetWithRenderOffset : function() {
      var template = createTemplate( { "bindingIndex" : 0, "type" : "text", "top" : [ 0, 12 ] } );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 10, 100, 30 ] } );

      assertEquals( 22, getTop( element.firstChild ) );
    },

    testGetCellTop_TopIsUndefined : function() {
      var template = createTemplate( {
        "bindingIndex" : 0,
        "type" : "text",
        "height" : 10,
        "bottom" : [ 0, 15 ],
        "top" : undefined
      } );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 5, getTop( element.firstChild ) );
    },

    testGetCellTop_TopIsUndefinedWithRenderOffset : function() {
      var template = createTemplate( {
        "bindingIndex" : 0,
        "type" : "text",
        "height" : 10,
        "bottom" : [ 0, 15 ],
        "top" : undefined
      } );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 5, 100, 30 ] } );

      assertEquals( 10, getTop( element.firstChild ) );
    },

    testGetCellWidth_WidthIsSet : function() {
      var template = createTemplate( { "bindingIndex" : 0, "type" : "text", "width" : 17 } );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 17, getWidth( element.firstChild ) );
    },

    testGetCellWidth_WidthIsUndefined : function() {
      var template = createTemplate( {
        "bindingIndex" : 0,
        "type" : "text",
        "left" : [ 0, 10 ],
        "right" : [ 0, 15 ],
        "width" : undefined
      } );
      var element = render( template,
                           createGridItem( [ "foo" ] ),
                           { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 75, getWidth( element.firstChild ) );
    },

    testGetCellWidth_WidthIsUndefinedAndRightIsPercentage : function() {
      var template = createTemplate( {
        "bindingIndex" : 0,
        "type" : "text",
        "left" : [ 0, 10 ],
        "right" : [ 10, 0 ],
        "width" : undefined
      } );
      var element = render( template,
                           createGridItem( [ "foo" ] ),
                           { "bounds" : [ 0, 0, 200, 30 ] } );

      assertEquals( 170, getWidth( element.firstChild ) );
    },

    testGetCellWidth_WidthIsUndefinedAndRightIsPercentageWithOffset : function() {
      var template = createTemplate( {
        "bindingIndex" : 0,
        "type" : "text",
        "left" : [ 0, 10 ],
        "right" : [ 10, 15 ],
        "width" : undefined
      } );
      var element = render( template,
                           createGridItem( [ "foo" ] ),
                           { "bounds" : [ 0, 0, 200, 30 ] } );

      assertEquals( 155, getWidth( element.firstChild ) );
    },

    testGetCellWidth_WidthIsUndefinedWithRenderOffset : function() {
      var template = createTemplate( {
        "bindingIndex" : 0,
        "type" : "text",
        "left" : [ 0, 10 ],
        "right" : [ 0, 15 ],
        "width" : undefined
      } );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 5, 0, 100, 30 ] }  );

      assertEquals( 75, getWidth( element.firstChild ) );
    },

    testGetCellHeight_HeightIsSet : function() {
      var template = createTemplate( { "bindingIndex" : 0, "type" : "text", "height" : 12 } );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 12, getHeight( element.firstChild ) );
    },

    testGetCellHeight_HeightIsUndefined : function() {
      var template = createTemplate( {
        "bindingIndex" : 0,
        "type" : "text",
        "top" : [ 0, 10 ],
        "bottom" : [ 0, 15 ],
        "height" : undefined
      } );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 5, getHeight( element.firstChild ) );
    },

    testGetCellHeight_HeightIsUndefinedAndBottomIsPercentage : function() {
      var template = createTemplate( {
        "bindingIndex" : 0,
        "type" : "text",
        "top" : [ 0, 10 ],
        "bottom" : [ 10, 0 ],
        "height" : undefined
      } );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 17, getHeight( element.firstChild ) );
    },

    testGetCellHeight_HeightIsUndefinedAndBottomIsPercentageWithOffset : function() {
      var template = createTemplate( {
        "bindingIndex" : 0,
        "type" : "text",
        "top" : [ 0, 10 ],
        "bottom" : [ 10, 5 ],
        "height" : undefined
      } );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 12, getHeight( element.firstChild ) );
    },

    testGetCellHeight_HeightIsUndefinedWithTopRenderOffset : function() {
      var template = createTemplate( {
        "bindingIndex" : 0,
        "type" : "text",
        "top" : [ 0, 10 ],
        "bottom" : [ 0, 15 ],
        "height" : undefined
      } );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 10, 100, 30 ] } );

      assertEquals( 5, getHeight( element.firstChild ) );
    },

    testGetCellType : function() {
      var template = createTemplate( { "type" : "anyString" } );

      assertEquals( "anyString", template.getCellType( 0 ) );
    },

    testGetCellContent_TextFromGridItem : function() {
      var template = new Template( [ { "type" : "text", "bindingIndex" : 1 }, { "type" : "text" } ] );
      var item = createGridItem( [ "foo", "bar" ] );

      assertEquals( "bar", template.getCellContent( item, 0 ) );
      assertEquals( "", template.getCellContent( item, 1 ) );
    },

    testGetCellContent_TextFromCellDefault : function() {
      var template = new Template( [ { "type" : "text", "text" : "abc" }, { "type" : "text" } ] );
      var item = createGridItem( [ "foo", "bar" ] );

      assertEquals( "abc", template.getCellContent( item, 0 ) );
    },

    testGetCellContent_TextFromNullItem : function() {
      var template = new Template( [ { "type" : "text", "bindingIndex" : 1 }, { "type" : "text" } ] );

      assertNull( template.getCellContent( null, 0 ) ); // there is a difference between null and ""
    },

    testHasContent_TextWithNoBindingOrDefault : function() {
      var template = new Template( [ { "type" : "text" } ] );
      var item = createGridItem( [ "foo", "bar" ] );

      assertFalse( template.hasContent( item, 0 ) );
    },

    testHasContent_TextBoundToEmptyAndNoDefault : function() {
      var template = new Template( [ { "type" : "text", "bindingIndex" : 0 } ] );
      var item = createGridItem( [ "", "bar" ] );

      assertFalse( template.hasContent( item, 0 ) );
    },

    testHasContent_TextBoundToEmptyWithDefault : function() {
      var template = new Template( [ { "type" : "text", "bindingIndex" : 0, "text" : "lalala" } ] );
      var item = createGridItem( [ "", "bar" ] );

      assertFalse( template.hasContent( item, 0 ) );
    },

    testHasContent_TextNotBoundWithDefault : function() {
      var template = new Template( [ { "type" : "text", "text" : "lalala" } ] );
      var item = createGridItem( [ "", "bar" ] );

      assertTrue( template.hasContent( item, 0 ) );
    },

    testHasContent_TextBoundToValue : function() {
      var template = new Template( [ { "bindingIndex" : 0, "type" : "text" } ] );
      var item = createGridItem( [ "foo", "bar" ] );

      assertTrue( template.hasContent( item, 0 ) );
    },

    testHasContent_TextBoundToNullItem : function() {
      var template = new Template( [ { "bindingIndex" : 0, "type" : "text" } ] );

      assertFalse( template.hasContent( null, 0 ) );
    },

    testHasContent_ImageWithNoBindingOrDefault : function() {
      var template = new Template( [ { "type" : "image" } ] );
      var item = createGridItem( [], [ [ "foo", 10, 10 ], [ "bar", 10, 10 ] ] );

      assertFalse( template.hasContent( item, 0 ) );
    },

    testHasContent_ImageBoundToEmptyAndNoDefault : function() {
      var template = new Template( [ { "type" : "image", "bindingIndex" : 0 } ] );
      var item = createGridItem( [], [ null, "bar" ] );

      assertFalse( template.hasContent( item, 0 ) );
    },

    testHasContent_ImageBoundToEmptyWithDefault : function() {
      var template = new Template( [ {
        "type" : "image",
        "bindingIndex" : 0,
        "image" : [ "x.jpg", 1, 1 ]
      } ] );
      var item = createGridItem(  [], [ null, "bar" ] );

      assertFalse( template.hasContent( item, 0 ) );
    },

    testHasContent_ImageNotBoundWithDefault : function() {
      var template = new Template( [ {
        "type" : "image",
        "image" : [ "x.jpg", 1, 1 ]
      } ] );
      var item = createGridItem( [], [ "", "bar" ] );

      assertTrue( template.hasContent( item, 0 ) );
    },

    testHasContent_ImageBoundToValue : function() {
      var template = new Template( [ { "bindingIndex" : 0, "type" : "image" } ] );
      var item = createGridItem( [], [ [ "foo", 10, 10 ], [ "bar", 10, 10 ] ] );

      assertTrue( template.hasContent( item, 0 ) );
    },

    testHasContent_ImageBoundToNullItem : function() {
      var template = new Template( [ { "bindingIndex" : 0, "type" : "image" } ] );

      assertFalse( template.hasContent( null, 0 ) );
    },

    testGetCellContent_ImageFromGridItem : function() {
      var template = new Template( [ { "type" : "image", "bindingIndex" : 1 } ] );
      var item = createGridItem( [], [ [ "foo.png", 10, 10 ], [ "bar.png", 10, 10 ] ] );

      assertEquals( [ "bar.png", 10, 10 ], template.getCellContent( item, 0 ) );
    },

    testGetCellContent_ImageFromCellDefault : function() {
      var template = new Template( [ { "type" : "image", "image" : [ "x.jpg", 1, 1 ] } ] );
      var item = createGridItem( [], [ [ "foo.png", 10, 10 ], [ "bar.png", 10, 10 ] ] );

      assertEquals( [ "x.jpg", 1, 1 ], template.getCellContent( item, 0 ) );
    },

    testGetCellContent_ImageFromNullItem : function() {
      var template = new Template( [ { "type" : "image", "bindingIndex" : 1 } ] );

      assertNull( template.getCellContent( null, 0 ) );
    },

    testGetCellFont_NotBoundIsNull : function() {
      var item = createGridItem( [ "foo", "bar" ] );
      item.setCellFonts( [ "14px Arial", "14px Arial" ] );

      var template = new Template( [ {}, {} ] );

      assertNull( template.getCellFont( item, 0 ) );
    },

    testGetCellFont_FromGridItem : function() {
      var template = new Template( [ { "bindingIndex" : 1 }, {} ] );
      var item = createGridItem( [ "foo", "bar" ] );

      item.setCellFonts( [ null, "14px Arial" ] );

      assertEquals( "14px Arial", template.getCellFont( item, 0 ) );
    },

    testGetCellFont_FromNullItem : function() {
      var template = new Template( [ { "bindingIndex" : 1 }, {} ] );

      assertEquals( null, template.getCellFont( null, 0 ) );
    },

    testGetCellFont_FromGridItemDoesOverwriteDefault : function() {
      var template = new Template( [ { "bindingIndex" : 1, "font" : [ [ "Foo" ], 17, false, false ] }, {} ] );
      var item = createGridItem( [ "foo", "bar" ] );

      item.setCellFonts( [ null, "14px Arial" ] );

      assertEquals( "14px Arial", template.getCellFont( item, 0 ) );
    },

    testGetCellFont_FromNullItemDoesOverwriteDefault : function() {
      var template = new Template( [ { "bindingIndex" : 1, "font" : [ [ "Foo" ], 17, false, false ] }, {} ] );

      assertEquals( null, template.getCellFont( null, 0 ) );
    },

    testGetCellFont_FromDefaultUnbound : function() {
      var template = new Template( [ { "font" : [ [ "Arial" ], 14, false, false ] }, {} ] );

      var item = createGridItem( [ "foo", "bar" ] );

      assertEquals( "14px Arial", template.getCellFont( item, 0 ) );
    },

    testGetCellFont_FromDefaultItemNotSet : function() {
      var template = new Template( [ { "bindingIndex" : 1, "font" : [ [ "Arial" ], 14, false, false ] }, {} ] );

      var item = createGridItem( [ "foo", "bar" ] );

      assertEquals( "14px Arial", template.getCellFont( item, 0 ) );
    },

    testGetCellForeground_NotBoundIsNull : function() {
      var item = createGridItem( [ "foo", "bar" ] );
      item.setCellForegrounds( [ "rgb( 0, 1, 2 )", "rgb( 3, 4, 5 )" ] );

      var template = new Template( [ {}, {} ] );

      assertNull( template.getCellForeground( item, 0 ) );
    },

    testGetCellForeground_FromGridItem : function() {
      var template = new Template( [ { "bindingIndex" : 1 }, {} ] );
      var item = createGridItem( [ "foo", "bar" ] );

      item.setCellForegrounds( [ null, "rgb( 3, 4, 5 )" ] );

      assertEquals( "rgb( 3, 4, 5 )", template.getCellForeground( item, 0 ) );
    },

    testGetCellForeground_FromNullItem : function() {
      var template = new Template( [ { "bindingIndex" : 1 }, {} ] );

      assertEquals( null, template.getCellForeground( null, 0 ) );
    },

    testGetCellForeground_FromGridItemDoesOverwriteDefault : function() {
      var template = new Template( [ { "bindingIndex" : 1, "foreground" : [ 255, 0, 0, 255 ] } ] );
      var item = createGridItem( [ "foo", "bar" ] );

      item.setCellForegrounds( [ null, "rgb( 3, 4, 5 )" ] );

      assertEquals( "rgb( 3, 4, 5 )", template.getCellForeground( item, 0 ) );
    },

    testGetCellForeground_FromNullItemDoesOverwriteDefault : function() {
      var template = new Template( [ { "bindingIndex" : 1, "foreground" : [ 255, 0, 0, 255 ] } ] );

      assertEquals( null, template.getCellForeground( null, 0 ) );
    },

    testGetCellForeground_FromDefaultUnbound : function() {
      var template = new Template( [ { "foreground" : [ 255, 0, 0, 255 ] } ] );
      var item = createGridItem( [ "foo", "bar" ] );

      assertEquals( "rgb(255,0,0)", template.getCellForeground( item, 0 ) );
    },

    testGetCellForeground_FromDefaultItemValueNotSet : function() {
      var template = new Template( [ { "bindingIndex" : 1, "foreground" : [ 255, 0, 0, 255 ] } ] );
      var item = createGridItem( [ "foo", "bar" ] );

      assertEquals( "rgb(255,0,0)", template.getCellForeground( item, 0 ) );
    },

    testGetCellBackground_NotBoundIsNull : function() {
      var item = createGridItem( [ "foo", "bar" ] );
      item.setCellBackgrounds( [ "rgb( 0, 1, 2 )", "rgb( 3, 4, 5 )" ] );

      var template = new Template( [ {}, {} ] );

      assertNull( template.getCellBackground( item, 0 ) );
    },

    testGetCellBackground_FromGridItem : function() {
      var template = new Template( [ { "bindingIndex" : 1 }, {} ] );
      var item = createGridItem( [ "foo", "bar" ] );

      item.setCellBackgrounds( [ null, "rgb( 3, 4, 5 )" ] );

      assertEquals( "rgb( 3, 4, 5 )", template.getCellBackground( item, 0 ) );
    },

    testGetCellBackground_FromNullItem : function() {
      var template = new Template( [ { "bindingIndex" : 1 }, {} ] );

      assertEquals( null, template.getCellBackground( null, 0 ) );
    },

    testGetCellBackground_FromGridItemDoesOverwriteDefault : function() {
      var template = new Template( [ { "bindingIndex" : 1, "background" : [ 255, 0, 0, 255 ] } ] );
      var item = createGridItem( [ "foo", "bar" ] );

      item.setCellBackgrounds( [ null, "rgb( 3, 4, 5 )" ] );

      assertEquals( "rgb( 3, 4, 5 )", template.getCellBackground( item, 0 ) );
    },

    testGetCellBackground_FromNullItemDoesOverwriteDefault : function() {
      var template = new Template( [ { "bindingIndex" : 1, "background" : [ 255, 0, 0, 255 ] } ] );

      assertEquals( null, template.getCellBackground( null, 0 ) );
    },

    testGetCellBackground_FromDefaultUnbound : function() {
      var template = new Template( [ { "background" : [ 255, 0, 0, 255 ] } ] );

      var item = createGridItem( [ "foo", "bar" ] );

      assertEquals( "rgb(255,0,0)", template.getCellBackground( item, 0 ) );
    },

    testGetCellBackground_FromDefaultItemValueNotSet : function() {
      var template = new Template( [ {
        "type" : "text",
        "bindingIndex" : 1,
        "background" : [ 255, 0, 0, 255 ]
      } ] );

      var item = createGridItem( [ "foo", "bar" ] );

      assertEquals( "rgb(255,0,0)", template.getCellBackground( item, 0 ) );
    },

    testCustomRenderer_hasContent : function() {
      renderer.add( createCellRenderer() );
      var template = new Template( [ { "bindingIndex" : 0, "type" : "fooType" } ] );
      var item = createGridItem( [ "foo", "bar" ] );

      assertTrue( template.hasContent( item, 0 ) );
    },

    testCustomRenderer_getContent : function() {
      renderer.add( createCellRenderer() );
      var template = new Template( [ { "bindingIndex" : 0, "type" : "fooType" } ] );
      var item = createGridItem( [ "foo", "bar" ] );

      assertEquals( "foo", template.getCellContent( item, 0 ) );
    },

    testCustomRenderer_ArgumentsForCreateElement : function() {
      var renderArgs;
      renderer.add( createCellRenderer( {
        "createElement" : function() {
          renderArgs = rwt.util.Arrays.fromArguments( arguments );
          return document.createElement( "div" );
        }
      } ) );
      var cellData = { "type" : "fooType", "bindingIndex" : 1 };
      var template = createTemplate( cellData );

      render( template, createGridItem( [ "foo", "bar" ] ) );

      assertEquals( 1, renderArgs.length );
      assertEquals( cellData.type, renderArgs[ 0 ].type );
      assertEquals( cellData.bindingIndex, renderArgs[ 0 ].bindingIndex );
    },

    testCustomRenderer_ArgumentsForRenderContent : function() {
      var renderArgs;
      renderer.add( createCellRenderer( {
        "renderContent" : function() {
          renderArgs = rwt.util.Arrays.fromArguments( arguments );
        }
      } ) );
      var cellData = { "type" : "fooType", "bindingIndex" : 1 };
      var template = createTemplate( cellData );
      var container = createContainer( template );
      var item = createGridItem( [ "foo", "bar" ] );
      var options = {
        "container" : container,
        "item" : item,
        "bounds" : [ 0, 0, 100, 100 ],
        "markupEnabled" : false,
        "seeable" : false,
        "enabled" : true
      };

      template._render( options );

      var cellRenderOptions = {
        "markupEnabled" : false,
        "seeable" : false,
        "width" : 10,
        "height" : 10,
        "enabled" : true
      };
      assertIdentical( template._getCellElement( container, 0 ), renderArgs[ 0 ] );
      assertIdentical( "bar", renderArgs[ 1 ] );
      assertIdentical( cellData.type, renderArgs[ 2 ].type );
      assertEquals( cellRenderOptions, renderArgs[ 3 ] );
    },

    testCustomRenderer_CellRenderOptionMarkupEnabled : function() {
      var options;
      renderer.add( createCellRenderer( {
        "renderContent" : function() { options = arguments[ 3 ]; }
      } ) );
      var template = createTemplate( { "bindingIndex" : 0, "type" : "fooType" } );
      var item = createGridItem( [ "foo", "bar" ] );

      render( template, item, { "markupEnabled" : true } );

      assertTrue( options.markupEnabled );
    },

    testDefaultTextRenderer_DefaultTextStyles : function() {
      var template = createTemplate( [ "text" ] );

      var element = render( template, createGridItem( [ "bar" ] ) );

      assertEquals( "nowrap", element.firstChild.style.whiteSpace );
    },

    testDefaultTextRenderer_RenderNullItemClearsContent : function() {
      var template = createTemplate( [ "text" ] );
      var container = createContainer( template );
      template._render( createRenderOptions( container, createGridItem( [ "bar" ] ) ) );

      template._render( createRenderOptions( container, null ) );

      assertEquals( "", container.element.firstChild.innerHTML );
    },

    testDefaultImageRenderer_RenderImageCentered : function() {
      var template = createTemplate( [ "image" ] );

      var element = render( template, createGridItem( [], [ [ "foo.jpg", 10, 10], [ "bar", 10, 10 ] ] ) );

      var image = TestUtil.getCssBackgroundImage( element.firstChild );
      assertTrue( image.indexOf( "foo.jpg" ) != -1 );
      var position = element.firstChild.style.backgroundPosition;
      assertTrue(    position == "center"
                  || position == "center center"
                  || position == "50% center"
                  || position == "center 50%"
                  || position == "50% 50%" );
      assertEquals( "no-repeat", element.firstChild.style.backgroundRepeat );
      if( !rwt.client.Client.isMshtml() ) {
        var opacity = element.firstChild.style.opacity;
        assertTrue( opacity === "1" || opacity === "" );
      }
    },

    testDefaultImageRenderer_RenderImageDisabled : function() {
      var template = createTemplate( { "bindingIndex" : 0, "type" : "image" } );
      var options = { "enabled" : false };

      var element = render( template, createGridItem( [], [ [ "foo.jpg", 10, 10], [ "bar", 10, 10 ] ] ), options );

      var image = TestUtil.getCssBackgroundImage( element.firstChild );
      assertTrue( image.indexOf( "foo.jpg" ) != -1 );
      if( !rwt.client.Client.isMshtml() ) {
        var opacity = element.firstChild.style.opacity;
        assertTrue( opacity === "0.3" );
      }
    },

    testDefaultImageRenderer_RenderNullItemClearsContent : function() {
      var template = createTemplate( [ "image" ] );
      var container = createContainer( template );
      var item = createGridItem( [], [ [ "foo.jpg", 10, 10 ], [ "bar", 10, 10 ] ] );
      template._render( createRenderOptions( container, item ) );

      template._render( createRenderOptions( container, null ) );

      assertEquals( "", container.element.style.backgroundImage );
    },

    testRenderBackgroundColor : function() {
      renderer.add( createCellRenderer() );
      var template = createTemplate( [ "fooType", "text" ] );
      var item = createGridItem( [] );
      item.setCellBackgrounds( [ "#ff00ff" ] );

      var element = render( template, item );

      var color = rwt.util.Colors.stringToRgb( element.firstChild.style.backgroundColor );
      assertEquals( [ 255, 0, 255 ], color );
    },

    testResetBackgroundColor : function() {
      renderer.add( createCellRenderer() );
      var template = createTemplate( [ "fooType", "text" ] );
      var item = createGridItem( [] );
      item.setCellBackgrounds( [ "#ff00ff" ] );
      var container = createContainer( template );
      var renderOptions = createRenderOptions( container, item );
      template._render( renderOptions );

      item.setCellBackgrounds( [ null ] );
      template._render( renderOptions );

      assertNull( TestUtil.getCssBackgroundColor( container.element.firstChild ) );
    },

    testRenderForegroundColor : function() {
      renderer.add( createCellRenderer() );
      var template = createTemplate( [ "fooType", "text" ] );
      var item = createGridItem( [ "text" ] );
      item.setCellForegrounds( [ "#ff00ff" ] );

      var element = render( template, item );

      var color = rwt.util.Colors.stringToRgb( element.firstChild.style.color );
      assertEquals( [ 255, 0, 255 ], color );
    },

    testResetForegroundColor : function() {
      renderer.add( createCellRenderer() );
      var template = createTemplate( [ "fooType", "text" ] );
      var item = createGridItem( [ "text" ] );
      item.setCellForegrounds( [ "#ff00ff" ] );
      var container = createContainer( template );
      var renderOptions = createRenderOptions( container, item );
      template._render( renderOptions );

      item.setCellForegrounds( [ null ] );
      template._render( renderOptions );

      var color = container.element.firstChild.style.color;
      assertTrue( color === "" || color === "inherit" );
    },

    testRenderFont : function() {
      renderer.add( createCellRenderer() );
      var template = createTemplate( [ "fooType", "text" ] );
      var item = createGridItem( [ "text" ] );
      item.setCellFonts( [ "14px Arial", "14px Arial" ] );

      var element = render( template, item );

      var font = TestUtil.getElementFont( element.firstChild );
      assertTrue( font.indexOf( "Arial" ) != -1 );
      assertTrue( font.indexOf( "14px" ) != -1 );
    },

    testResetFont : function() {
      renderer.add( createCellRenderer() );
      var template = createTemplate( [ "fooType", "text" ] );
      var item = createGridItem( [ "text" ] );
      item.setCellFonts( [ "14px Arial", "14px Arial" ] );
      var container = createContainer( template );
      var renderOptions = createRenderOptions( container, item );
      template._render( renderOptions );

      item.setCellFonts( [ null, "14px Arial" ] );
      template._render( renderOptions );

      var style = container.element.firstChild.style;
      if( !rwt.client.Client.isMobileSafari() ) {
        // on mobile safari the computed font is returned, making it impossibel to check for reset
        assertTrue( style.font === "" || style.font === "inherit"  );
        assertTrue( style.fontSize === "" || style.fontSize === "inherit" );
        assertTrue( style.fontFamily === "" || style.fontFamily === "inherit" );
      }
    },

    testGetCellByElement : function() {
      var template = createTemplate( [ "text", "text" ] );
      var container = createContainer( template );
      template._render( createRenderOptions( container, createGridItem( [ "bar", "foo" ] ) ) );

      var cell = template._getCellByElement( container, container.element.lastChild );

      assertEquals( 1, cell );
    },

    testIsCellSelectable_ReturnsTrue : function() {
      var template = createTemplate( { "selectable" : true } );

      assertTrue( template.isCellSelectable( 0 ) );
    },

    testIsCellSelectable_ReturnsFalse : function() {
      var template = createTemplate( [ "text" ] );

      assertFalse( template.isCellSelectable( 0 ) );
    },

    testGetCellName_ReturnNull : function() {
      var template = createTemplate( [ "text" ] );

      assertNull( template.getCellName( 0 ) );
    },

    testGetCellName_ReturnString : function() {
      var template = createTemplate( { "name" : "foobar" } );

      assertEquals( "foobar", template.getCellName( 0 ) );
    },

    testRender_RenderItemTwice : function() {
      var counter = 0;
      renderer.add( createCellRenderer( {
        "renderContent" : function() { counter++; }
      } ) );
      var template = createTemplate( { "bindingIndex" : 0, "type" : "fooType" } );
      var item = createGridItem( [ "foo", "bar" ] );
      var container = createContainer( template );
      var renderOptions = createRenderOptions( container, item );

      template._render( renderOptions );
      template._render( renderOptions );

      assertEquals( 2, counter );
    }


  }

} );

var createCellRenderer = function( map ) {
  var renderer =  {
    "cellType" : "fooType",
    "contentType" : "text",
    "renderContent" : function() {}
  };
  return rwt.util.Objects.mergeWith( renderer, map, true );
};

var createTemplate = function( cellTypes ) {
  if( cellTypes instanceof Array ) {
    var cells = [];
    for( var i = 0; i < cellTypes.length; i++ ) {
      cells[ i ] = {
        "type" : cellTypes[ i ],
        "bindingIndex" : i,
        "left" : [ 0, 0 ],
        "top" : [ 0, 0 ],
        "width" : 10,
        "height" : 10
      };
    }
    return new Template( cells );
  } else {
    var cellData =  {
      "type" : "text",
      "bindingIndex" : 0,
      "left" : [ 0, 0 ],
      "top" : [ 0, 0 ],
      "width" : 10,
      "height" : 10
    };
    cellData = rwt.util.Objects.mergeWith( cellData, cellTypes, true );
    return new Template( [ cellData ] );
  }
};

var createGridItem = function( texts, images ) {
  var root = new rwt.widgets.GridItem();
  root.setItemCount( 1 );
  var result = new rwt.widgets.GridItem( root, 0 );
  result.setTexts( texts );
  result.setImages( images );
  return result;
};

var render = function( template, item, options ) {
  var container = createContainer( template );
  var renderOptions = createRenderOptions( container, item );
  rwt.util.Objects.mergeWith( renderOptions, options, true );
  template._render( renderOptions );
  return container.element;
};

var createRenderOptions = function( container, item ) {
  return {
    "container" : container,
    "item" : item,
    "bounds" : [ 0, 0, 100, 100 ],
    "markupEnabled" : false,
    "enabled" : true
  };
};


var createContainer = function( template ) {
  return template._createContainer( {
    "element" : document.createElement( "div" ),
    "zIndexOffset" : 100
  } );
};

var getLeft = function( element ) {
  return parseInt( element.style.left, 10 );
};

var getTop = function( element ) {
  return parseInt( element.style.top, 10 );
};

var getWidth = function( element ) {
  return parseInt( element.style.width, 10 );
};

var getHeight = function( element ) {
  return parseInt( element.style.height, 10 );
};

}());
