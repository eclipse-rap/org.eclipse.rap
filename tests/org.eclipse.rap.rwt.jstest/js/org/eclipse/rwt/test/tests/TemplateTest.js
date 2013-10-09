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

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.TemplateTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateWithEmptyTemplate : function() {
      var cells = [];
      var template = new Template( cells );
      assertIdentical( cells, template._cells );
    },

    testGetCellCount : function() {
      var template = new Template( [ {}, {}, {} ] );

      assertEquals( 3, template.getCellCount() );
    },

    testGetCellLeft_LeftIsOffset : function() {
      var template = new Template( [ { "left" : 15 } ] );

      assertEquals( 15, template.getCellLeft( 0 ) );
    },

    testGetCellLeft_LeftIsUndefined : function() {
      var template = new Template( [ { "width" : 10, "right" : 15 } ] );
      template.configure( null, [ 100, 30 ] );

      assertEquals( 75, template.getCellLeft( 0 ) );
    },

    testGetCellTop_TopIsOffset : function() {
      var template = new Template( [ { "top" : 12 } ] );

      assertEquals( 12, template.getCellTop( 0 ) );
    },

    testGetCellTop_TopIsUndefined : function() {
      var template = new Template( [ { "height" : 10, "bottom" : 15 } ] );
      template.configure( null, [ 100, 30 ] );

      assertEquals( 5, template.getCellTop( 0 ) );
    },

    testGetCellWidth_WidthIsSet : function() {
      var template = new Template( [ { "width" : 17 } ] );

      assertEquals( 17, template.getCellWidth( 0 ) );
    },

    testGetCellWidth_WidthIsUndefined : function() {
      var template = new Template( [ { "left" : 10, "right" : 15 } ] );
      template.configure( null, [ 100, 30 ] );

      assertEquals( 75, template.getCellWidth( 0 ) );
    },

    testGetCellHeight_HeightIsSet : function() {
      var template = new Template( [ { "height" : 12 } ] );

      assertEquals( 12, template.getCellHeight( 0 ) );
    },

    testGetCellHeight_HeightIsUndefined : function() {
      var template = new Template( [ { "top" : 10, "bottom" : 15 } ] );
      template.configure( null, [ 100, 30 ] );

      assertEquals( 5, template.getCellHeight( 0 ) );
    },

    testGetCellType : function() {
      var template = new Template( [ { "type" : "anyString" } ] );

      assertEquals( "anyString", template.getCellType( 0 ) );
    },

    testGetText_FromGridItem : function() {
      var template = new Template( [ { "bindingIndex" : 1 }, {} ] );

      template.configure( createGridItem( [ "foo", "bar" ] ) );
      assertEquals( "bar", template.getText( 0 ) );
      assertEquals( "", template.getText( 1 ) );
    },

    testGetText_ForwardArgument : function() {
      var template = new Template( [ { "bindingIndex" : 0 } ] );
      var item = createGridItem( [ "foo", "bar" ] );
      var arg;
      item.getText = function() { arg = arguments; };

      template.configure( item );
      template.getText( 0, 33 );

      assertEquals( [ 0, 33 ], rwt.util.Arrays.fromArguments( arg ) );
    },

    testHasText_NoBindingOrDefault : function() {
      var template = new Template( [ { } ] );
      var item = createGridItem( [ "foo", "bar" ] );
      template.configure( item );

      assertFalse( template.hasText( 0 ) );
    },

    testHasText_BoundToEmptyAndNoDefault : function() {
      var template = new Template( [ { "bindingIndex" : 0 } ] );
      var item = createGridItem( [ "", "bar" ] );
      template.configure( item );

      assertFalse( template.hasText( 0 ) );
    },

    testHasText_BoundToValue : function() {
      var template = new Template( [ { "bindingIndex" : 0 } ] );
      var item = createGridItem( [ "foo", "bar" ] );
      template.configure( item );

      assertTrue( template.hasText( 0 ) );
    },

    testGetImage_FromGridItem : function() {
      var template = new Template( [ { "bindingIndex" : 1 }, {} ] );

      template.configure( createGridItem( [], [ "foo.png", "bar.png" ] ) );
      assertEquals( "bar.png", template.getImage( 0 ) );
      assertNull( template.getImage( 1 ) );
    },

    testGetCellFont_NotBoundIsNull : function() {
      var item = createGridItem( [ "foo", "bar" ] );
      item.setCellFonts( [ "14px Arial", "14px Arial" ] );

      var template = new Template( [ {}, {} ] );
      template.configure( item );

      assertNull( template.getCellFont( 0 ) );
    },

    testGetCellFont_FromGridItem : function() {
      var template = new Template( [ { "bindingIndex" : 1 }, {} ] );
      var item = createGridItem( [ "foo", "bar" ] );

      item.setCellFonts( [ null, "14px Arial" ] );
      template.configure( item );

      assertEquals( "14px Arial", template.getCellFont( 0 ) );
    },

    testGetCellFont_FromGridItemDoesOverwriteDefault : function() {
      var template = new Template( [ { "bindingIndex" : 1, "font" : [ [ "Foo" ], 17, false, false ] }, {} ] );
      var item = createGridItem( [ "foo", "bar" ] );

      item.setCellFonts( [ null, "14px Arial" ] );
      template.configure( item );

      assertEquals( "14px Arial", template.getCellFont( 0 ) );
    },

    testGetCellFont_FromDefaultUnbound : function() {
      var template = new Template( [ { "font" : [ [ "Arial" ], 14, false, false ] }, {} ] );

      template.configure( createGridItem( [ "foo", "bar" ] ) );

      assertEquals( "14px Arial", template.getCellFont( 0 ) );
    },

    testGetCellFont_FromDefaultItemNotSet : function() {
      var template = new Template( [ { "bindingIndex" : 1, "font" : [ [ "Arial" ], 14, false, false ] }, {} ] );

      template.configure( createGridItem( [ "foo", "bar" ] ) );

      assertEquals( "14px Arial", template.getCellFont( 0 ) );
    },

    testGetCellForeground_NotBoundIsNull : function() {
      var item = createGridItem( [ "foo", "bar" ] );
      item.setCellForegrounds( [ "rgb( 0, 1, 2 )", "rgb( 3, 4, 5 )" ] );

      var template = new Template( [ {}, {} ] );
      template.configure( item );

      assertNull( template.getCellForeground( 0 ) );
    },

    testGetCellForeground_FromGridItem : function() {
      var template = new Template( [ { "bindingIndex" : 1 }, {} ] );
      var item = createGridItem( [ "foo", "bar" ] );

      item.setCellForegrounds( [ null, "rgb( 3, 4, 5 )" ] );
      template.configure( item );

      assertEquals( "rgb( 3, 4, 5 )", template.getCellForeground( 0 ) );
    },

    testGetCellForeground_FromGridItemDoesOverwriteDefault : function() {
      var template = new Template( [ { "bindingIndex" : 1, "foreground" : [ 255, 0, 0, 255 ] } ] );
      var item = createGridItem( [ "foo", "bar" ] );

      item.setCellForegrounds( [ null, "rgb( 3, 4, 5 )" ] );
      template.configure( item );

      assertEquals( "rgb( 3, 4, 5 )", template.getCellForeground( 0 ) );
    },

    testGetCellForeground_FromDefaultUnbound : function() {
      var template = new Template( [ { "foreground" : [ 255, 0, 0, 255 ] } ] );

      template.configure( createGridItem( [ "foo", "bar" ] ) );

      assertEquals( "rgb(255,0,0)", template.getCellForeground( 0 ) );
    },

    testGetCellForeground_FromDefaultItemValueNotSet : function() {
      var template = new Template( [ { "bindingIndex" : 1, "foreground" : [ 255, 0, 0, 255 ] } ] );

      template.configure( createGridItem( [ "foo", "bar" ] ) );

      assertEquals( "rgb(255,0,0)", template.getCellForeground( 0 ) );
    },

    testGetCellBackground_NotBoundIsNull : function() {
      var item = createGridItem( [ "foo", "bar" ] );
      item.setCellBackgrounds( [ "rgb( 0, 1, 2 )", "rgb( 3, 4, 5 )" ] );

      var template = new Template( [ {}, {} ] );
      template.configure( item );

      assertNull( template.getCellBackground( 0 ) );
    },

    testGetCellBackground_FromGridItem : function() {
      var template = new Template( [ { "bindingIndex" : 1 }, {} ] );
      var item = createGridItem( [ "foo", "bar" ] );

      item.setCellBackgrounds( [ null, "rgb( 3, 4, 5 )" ] );
      template.configure( item );

      assertEquals( "rgb( 3, 4, 5 )", template.getCellBackground( 0 ) );
    },

    testGetCellBackground_FromGridItemDoesOverwriteDefault : function() {
      var template = new Template( [ { "bindingIndex" : 1, "background" : [ 255, 0, 0, 255 ] } ] );
      var item = createGridItem( [ "foo", "bar" ] );

      item.setCellBackgrounds( [ null, "rgb( 3, 4, 5 )" ] );
      template.configure( item );

      assertEquals( "rgb( 3, 4, 5 )", template.getCellBackground( 0 ) );
    },

    testGetCellBackground_FromDefaultUnbound : function() {
      var template = new Template( [ { "background" : [ 255, 0, 0, 255 ] } ] );

      template.configure( createGridItem( [ "foo", "bar" ] ) );

      assertEquals( "rgb(255,0,0)", template.getCellBackground( 0 ) );
    },

    testGetCellBackground_FromDefaultItemValueNotSet : function() {
      var template = new Template( [ { "bindingIndex" : 1, "background" : [ 255, 0, 0, 255 ] } ] );

      template.configure( createGridItem( [ "foo", "bar" ] ) );

      assertEquals( "rgb(255,0,0)", template.getCellBackground( 0 ) );
    }

  }

} );

var createGridItem = function( texts, images ) {
  var root = new rwt.widgets.GridItem();
  root.setItemCount( 1 );
  var result = new rwt.widgets.GridItem( root, 0 );
  result.setTexts( texts );
  result.setImages( images );
  return result;
};

}());
