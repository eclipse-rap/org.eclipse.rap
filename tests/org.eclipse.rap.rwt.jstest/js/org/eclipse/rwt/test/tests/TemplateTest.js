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
        template.createContainer( {
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
        template.createContainer( {
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

      var container = template.createContainer( {
        "element" : document.createElement( "div" ),
        "zIndexOffset" : 100
      } );
      assertTrue( container instanceof Object );
    },

    testRenderFailsWithoutValidContainer : function() {
      var template = createTemplate( [ "text", "text", "text" ] );

      try {
        template.render( { "bounds" : [ 0, 0, 100, 100 ], "container" : {}, "item" : null } );
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

      var element = render( template, createGridItem( [], [ "foo.jpg" ] ) );

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

      assertEquals( 100, element.children[ 0 ].style.zIndex );
      assertEquals( 102, element.children[ 1 ].style.zIndex );
    },

    testRenderCreatesElements_ReUsesElements : function() {
      var template = createTemplate( [ "text", "text", "text" ] );

      var container = createContainer( template );
      template.render( {
        "container" : container,
        "bounds" : [ 0, 0, 100, 100 ],
        "item" : createGridItem( [ "foo", "foo", "foo" ] )
      } );
      template.render( {
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
      var template = new Template( [ { "bindingIndex" : 0, "type" : "text", "left" : 15 } ] );

      var element = render( template, createGridItem( [ "foo" ] ) );

      assertEquals( 15, getLeft( element.firstChild ) );
    },

    testGetCellLeft_LeftIsUndefined : function() {
      var template = new Template( [ {
        "bindingIndex" : 0,
        "type" : "text",
        "width" : 10,
        "right" : 15
      } ] );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 75, getLeft( element.firstChild ) );
    },

    testGetCellTop_TopIsOffset : function() {
      var template = new Template( [ { "bindingIndex" : 0, "type" : "text", "top" : 12 } ] );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 12, getTop( element.firstChild ) );
    },

    testGetCellTop_TopIsUndefined : function() {
      var template = new Template( [ {
        "bindingIndex" : 0,
        "type" : "text",
        "height" : 10,
        "bottom" : 15
      } ] );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 5, getTop( element.firstChild ) );
    },

    testGetCellWidth_WidthIsSet : function() {
      var template = new Template( [ { "bindingIndex" : 0, "type" : "text", "width" : 17 } ] );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 17, getWidth( element.firstChild ) );
    },

    testGetCellWidth_WidthIsUndefined : function() {
      var template = new Template( [ {
        "bindingIndex" : 0,
        "type" : "text",
        "left" : 10,
        "right" : 15
      } ] );
      var element = render( template, createGridItem( [ "foo" ] ), [ 100, 30 ] );

      assertEquals( 75, getWidth( element.firstChild ) );
    },

    testGetCellHeight_HeightIsSet : function() {
      var template = new Template( [ { "bindingIndex" : 0, "type" : "text", "height" : 12 } ] );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 12, getHeight( element.firstChild ) );
    },

    testGetCellHeight_HeightIsUndefined : function() {
      var template = new Template( [ {
        "bindingIndex" : 0,
        "type" : "text",
        "top" : 10,
        "bottom" : 15
      } ] );
      var element = render( template,
                            createGridItem( [ "foo" ] ),
                            { "bounds" : [ 0, 0, 100, 30 ] } );

      assertEquals( 5, getHeight( element.firstChild ) );
    },

    testGetCellType : function() {
      var template = new Template( [ { "type" : "anyString" } ] );

      assertEquals( "anyString", template.getCellType( 0 ) );
    },

    testGetCellContent_TextFromGridItem : function() {
      var template = new Template( [ { "type" : "text", "bindingIndex" : 1 }, { "type" : "text" } ] );
      var item = createGridItem( [ "foo", "bar" ] );

      assertEquals( "bar", template.getCellContent( item, 0 ) );
      assertEquals( "", template.getCellContent( item, 1 ) );
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
      var item = createGridItem( [], [ "foo", "bar" ] );

      assertFalse( template.hasContent( item, 0 ) );
    },

    testHasContent_ImageBoundToEmptyAndNoDefault : function() {
      var template = new Template( [ { "type" : "image", "bindingIndex" : 0 } ] );
      var item = createGridItem( [], [ null, "bar" ] );

      assertFalse( template.hasContent( item, 0 ) );
    },

    testHasContent_ImageBoundToValue : function() {
      var template = new Template( [ { "bindingIndex" : 0, "type" : "image" } ] );
      var item = createGridItem( [], [ "foo", "bar" ] );

      assertTrue( template.hasContent( item, 0 ) );
    },

    testHasContent_ImageBoundToNullItem : function() {
      var template = new Template( [ { "bindingIndex" : 0, "type" : "image" } ] );

      assertFalse( template.hasContent( null, 0 ) );
    },

    testgetCellContent_ImageFromGridItem : function() {
      var template = new Template( [ { "type" : "image", "bindingIndex" : 1 } ] );
      var item = createGridItem( [], [ "foo.png", "bar.png" ] );

      assertEquals( "bar.png", template.getCellContent( item, 0 ) );
    },

    testgetCellContent_ImageFromNullItem : function() {
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
      var template = new Template( [ cellData ] );

      render( template, createGridItem( [ "foo", "bar" ] ) );

      assertEquals( 1, renderArgs.length );
      assertIdentical( cellData, renderArgs[ 0 ] );
    },

    testCustomRenderer_ArgumentsForRenderContent : function() {
      var renderArgs;
      renderer.add( createCellRenderer( {
        "renderContent" : function() {
          renderArgs = rwt.util.Arrays.fromArguments( arguments );
        }
      } ) );
      var cellData = { "type" : "fooType", "bindingIndex" : 1 };
      var template = new Template( [ cellData ] );
      var container = createContainer( template );
      var item = createGridItem( [ "foo", "bar" ] );
      var options = {
        "container" : container,
        "item" : item,
        "bounds" : [ 0, 0, 100, 100 ],
        "markupEnabled" : false,
        "seeable" : false
      };

      template.render( options );

      var cellRenderOptions = {
        "markupEnabled" : false,
        "escaped" : false,
        "seeable" : false
      };
      assertIdentical( template.getCellElement( container, 0 ), renderArgs[ 0 ] );
      assertIdentical( "bar", renderArgs[ 1 ] );
      assertIdentical( cellData, renderArgs[ 2 ] );
      assertEquals( cellRenderOptions, renderArgs[ 3 ] );
    },

    testCustomRenderer_ArgumentsForShouldEscapeText : function() {
      var renderArgs;
      renderer.add( createCellRenderer( {
        "shouldEscapeText" : function() {
          renderArgs = rwt.util.Arrays.fromArguments( arguments );
        }
      } ) );
      var cellData = { "type" : "fooType", "bindingIndex" : 1 };
      var template = new Template( [ cellData ] );
      var container = createContainer( template );
      var item = createGridItem( [ "foo", "bar" ] );
      var options = {
        "container" : container,
        "item" : item,
        "bounds" : [ 0, 0, 100, 100 ],
        "markupEnabled" : false
      };

      template.render( options );

      assertEquals( [ options ], renderArgs );
      renderer.removeRendererFor( "fooType" );
    },

    testCustomRenderer_CellRenderOptionMarkupEnabled : function() {
      var options;
      renderer.add( createCellRenderer( {
        "renderContent" : function() { options = arguments[ 3 ]; }
      } ) );
      var template = new Template( [ { "bindingIndex" : 0, "type" : "fooType" } ] );
      var item = createGridItem( [ "foo", "bar" ] );

      render( template, item, { "markupEnabled" : true } );

      assertTrue( options.markupEnabled );
    },

    testCustomRenderer_CellRenderOptionEscaped : function() {
      var options;
      renderer.add( createCellRenderer( {
        "renderContent" : function() { options = arguments[ 3 ]; },
        "shouldEscapeText" : function() { return true; }
      } ) );
      var template = new Template( [ { "bindingIndex" : 0, "type" : "fooType" } ] );
      var item = createGridItem( [ "foo", "bar" ] );

      render( template, item );

      assertTrue( options.escaped );
    },

    testCustomRenderer_ForwardShouldEscapeTextReturnValueToItemGetText : function() {
      renderer.add( createCellRenderer( {
        "shouldEscapeText" : function() { return 123; }
      } ) );
      var template = new Template( [ { "type" : "fooType", "bindingIndex" : 0 } ] );
      var item = createGridItem( [ "foo", "bar" ] );
      var arg;
      item.getText = function() { arg = arguments; };

      render( template, item );

      assertEquals( [ 0, 123 ], rwt.util.Arrays.fromArguments( arg ) );
      renderer.removeRendererFor( "fooType" );
    },

    testDefaultTextRenderer_DefaultTextStyles : function() {
      var template = createTemplate( [ "text" ] );

      var element = render( template, createGridItem( [ "bar" ] ) );

      assertEquals( "nowrap", element.firstChild.style.whiteSpace );
    },

    testDefaultTextRenderer_RenderNullItemClearsContent : function() {
      var template = createTemplate( [ "text" ] );
      var container = createContainer( template );
      template.render( createRenderOptions( container, createGridItem( [ "bar" ] ) ) );

      template.render( createRenderOptions( container, null ) );

      assertEquals( "", container.element.firstChild.innerHTML );
    },

    testDefaultImageRenderer_RenderImageCentered : function() {
      var template = createTemplate( [ "image" ] );

      var element = render( template, createGridItem( [], [ "foo.jpg", "bar" ] ) );

      var image = TestUtil.getCssBackgroundImage( element.firstChild );
      assertTrue( image.indexOf( "foo.jpg" ) != -1 );
      var position = element.firstChild.style.backgroundPosition;
      assertTrue( position == "center" || position == "center center" || position == "50% 50%" );
      assertEquals( "no-repeat", element.firstChild.style.backgroundRepeat );
      if( !rwt.client.Client.isMshtml() ) {
        var opacity = element.firstChild.style.opacity;
        assertTrue( opacity === "1" || opacity === "" );
      }
    },

    testDefaultImageRenderer_RenderImageDisabled : function() {
      var template = new Template( [ { "bindingIndex" : 0, "type" : "image" } ] );
      var options = { "enabled" : false };

      var element = render( template, createGridItem( [], [ "foo.jpg", "bar" ] ), options );

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
      var item = createGridItem( [], [ "foo.jpg", "bar" ] );
      template.render( createRenderOptions( container, item ) );

      template.render( createRenderOptions( container, null ) );

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
      template.render( renderOptions );

      item.setCellBackgrounds( [ null ] );
      template.render( renderOptions );

      var color = container.element.firstChild.style.backgroundColor.toLowerCase();
      assertTrue( color === "" || color === "transparent" );
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
      template.render( renderOptions );

      item.setCellForegrounds( [ null ] );
      template.render( renderOptions );

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
      template.render( renderOptions );

      item.setCellFonts( [ null, "14px Arial" ] );
      template.render( renderOptions );

      var style = container.element.firstChild.style;
      assertTrue( style.font === "" || style.font === "inherit" );
      assertTrue( style.fontSize === "" || style.fontSize === "inherit" );
      assertTrue( style.fontFamily === "" || style.fontFamily === "inherit" );
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
  var cells = [];
  for( var i = 0; i < cellTypes.length; i++ ) {
    cells[ i ] = {
      "type" : cellTypes[ i ],
      "bindingIndex" : i
    };
  }
  return new Template( cells );
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
  template.render( renderOptions );
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
  return template.createContainer( {
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
