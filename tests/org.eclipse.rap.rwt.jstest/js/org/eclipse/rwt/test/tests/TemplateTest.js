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

    testGetCellTop_TopIsOffset : function() {
      var template = new Template( [ { "top" : 12 } ] );

      assertEquals( 12, template.getCellTop( 0 ) );
    },

    testGetCellWidth_WidthIsSet : function() {
      var template = new Template( [ { "width" : 17 } ] );

      assertEquals( 17, template.getCellWidth( 0 ) );
    },

    testGetCellHeight_HeightIsSet : function() {
      var template = new Template( [ { "height" : 12 } ] );

      assertEquals( 12, template.getCellHeight( 0 ) );
    },

    testGetCellType : function() {
      var template = new Template( [ { "type" : "anyString" } ] );

      assertEquals( "anyString", template.getCellType( 0 ) );
    },

    testgetText_FromGridItem : function() {
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

  }

} );

var createGridItem = function( texts ) {
  var root = new rwt.widgets.GridItem();
  root.setItemCount( 1 );
  var result = new rwt.widgets.GridItem( root, 0 );
  result.setTexts( texts );
  return result;
};

}());
