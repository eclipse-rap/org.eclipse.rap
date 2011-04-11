/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.BorderTest", {

  extend : qx.core.Object,
  
  members : {

    testSimpleBorderConstructor : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var border = new qx.ui.core.Border( 2, "solid", "#FF00FF" );
      assertEquals( "#FF00FF", border.getColorTop() );
      assertEquals( "#FF00FF", border.getColorLeft() );
      assertEquals( "#FF00FF", border.getColorBottom() );
      assertEquals( "#FF00FF", border.getColorRight() );
      assertEquals( 2, border.getWidthTop() );
      assertEquals( 2, border.getWidthLeft() );
      assertEquals( 2, border.getWidthBottom() );
      assertEquals( 2, border.getWidthRight() );
      assertEquals( "solid", border.getStyleTop() );
      assertEquals( "solid", border.getStyleLeft() );
      assertEquals( "solid", border.getStyleBottom() );
      assertEquals( "solid", border.getStyleRight() );
      border.dispose();
    },

    testArrayArgumentConstructor : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var color = [ "#FF00EF", "#FF00EE", "#FF0EFF", "#FFE0FF" ];
      var width = [ 1, 2, 3, 4 ];
      var style = [ "solid", "outset", "inset", "groove" ];
      var border = new qx.ui.core.Border( 2, "solid", "#FF00FF" );
      assertEquals( "#FF00FF", border.getColorTop() );
      assertEquals( "#FF00FF", border.getColorLeft() );
      assertEquals( "#FF00FF", border.getColorBottom() );
      assertEquals( "#FF00FF", border.getColorRight() );
      assertEquals( 2, border.getWidthTop() );
      assertEquals( 2, border.getWidthLeft() );
      assertEquals( 2, border.getWidthBottom() );
      assertEquals( 2, border.getWidthRight() );
      assertEquals( "solid", border.getStyleTop() );
      assertEquals( "solid", border.getStyleLeft() );
      assertEquals( "solid", border.getStyleBottom() );
      assertEquals( "solid", border.getStyleRight() );
      border.dispose();
    },

    testRoundedBorderConstructor : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var border = new org.eclipse.rwt.RoundedBorder( 3, "#FF00F0", [ 0, 1, 2, 3 ] );
      assertEquals( "#FF00F0", border.getColorTop() );
      assertEquals( 3, border.getWidthTop() );
      assertEquals( [ 0, 1, 2, 3 ], border.getRadii() );
      border.dispose();
    }

  }
  
} );