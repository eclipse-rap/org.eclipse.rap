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
      var border = new org.eclipse.rwt.Border( 2, "solid", "#FF00FF" );
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

    testGetStyle : function() {
      var border = new org.eclipse.rwt.Border( 2, "solid", "#FF00FF" );
      assertEquals( "solid", border.getStyle() );
      border = new org.eclipse.rwt.Border( 2, [ "solid", "solid", "solid", "solid" ], "#FF00FF" );
      assertNull( border.getStyle() );
      border.dispose();
    },

    testGetColor : function() {
      var border = new org.eclipse.rwt.Border( 2, "solid", "#FF00FF" );
      assertEquals( "#FF00FF", border.getColor() );
      border = new org.eclipse.rwt.Border( 2, "solid", [ "#FF00FF", "#FF00FF", "#FF00FF", "#FF00FF" ] );
      assertNull( border.getColor() );
      border = new org.eclipse.rwt.Border( 2, "solid", null );
      assertEquals( "", border.getColor() );
    },

    testArrayArgumentConstructor : function() {
      var color = [ "#FF00EF", "#FF00EE", "#FF0EFF", "#FFE0FF" ];
      var width = [ 1, 2, 3, 4 ];
      var style = [ "solid", "outset", "inset", "groove" ];
      var border = new org.eclipse.rwt.Border( width, style, color );
      assertEquals( color[ 0 ], border.getColorTop() );
      assertEquals( color[ 1 ], border.getColorRight() );
      assertEquals( color[ 2 ], border.getColorBottom() );
      assertEquals( color[ 3 ], border.getColorLeft() );
      assertEquals( width[ 0 ], border.getWidthTop() );
      assertEquals( width[ 1 ], border.getWidthRight() );
      assertEquals( width[ 2 ], border.getWidthBottom() );
      assertEquals( width[ 3 ], border.getWidthLeft() );
      assertEquals( style[ 0 ], border.getStyleTop() );
      assertEquals( style[ 1 ], border.getStyleRight() );
      assertEquals( style[ 2 ], border.getStyleBottom() );
      assertEquals( style[ 3 ], border.getStyleLeft() );
      border.dispose();
    },
    
    testRoundedBorderConstructor : function() {
      // NOTE: Render RoundedBorder Tests are only in GraphicsMixinTest for now
      var border = new org.eclipse.rwt.Border( 3, "rounded", "#FF00F0", [ 0, 1, 2, 3 ] );
      assertEquals( "#FF00F0", border.getColor() );
      assertEquals( 3, border.getWidthTop() );
      assertEquals( [ 0, 1, 2, 3 ], border.getRadii() );
      border.dispose();
    },

    testRoundedBorderWithoutRadii : function() {
      try{ 
        var border = new org.eclipse.rwt.Border( 3, "rounded", "#FF00F0" );
        fail();
      } catch( ex ) {
        //expected
      }
    },

    testRoundedBorderWithSingleRadius: function() {
      var border = new org.eclipse.rwt.Border( 3, "rounded", "#FF00F0", 4 );
      assertEquals( [ 4, 4, 4, 4 ], border.getRadii() );
      border.dispose();    
    },
    
    testRoundedBorderWithMultipleColors : function() {
      var colors = [ "#FF00EF", "#FF00EE", "#FF0EFF", "#FFE0FF" ];
      try{ 
        var border = new org.eclipse.rwt.Border( 3, "rounded", colors, [ 3, 3, 3, 3 ] );
        fail();
      } catch( ex ) {
        //expected
      }
    },

    testSetInnerColor : function() {
      var color = [ "#FF00EF", "#FF00EE", "#FF0EFF", "#FFE0FF" ];
      var colorInner = [ "#DD00EF", "#DD00EE", "#DD0EFF", "#DDE0FF" ];
      var width = [ 1, 2, 3, 4 ];
      var style = "complex";
      var border = new org.eclipse.rwt.Border( width, style, color, colorInner );
      assertEquals( color[ 0 ], border.getColorTop() );
      assertEquals( color[ 1 ], border.getColorRight() );
      assertEquals( color[ 2 ], border.getColorBottom() );
      assertEquals( color[ 3 ], border.getColorLeft() );
      assertEquals( colorInner[ 0 ], border.getColorInnerTop() );
      assertEquals( colorInner[ 1 ], border.getColorInnerRight() );
      assertEquals( colorInner[ 2 ], border.getColorInnerBottom() );
      assertEquals( colorInner[ 3 ], border.getColorInnerLeft() );
      border.dispose();
    },

    testCopmlexBorderWithoutInnerColor : function() {
      try {
        var border = new org.eclipse.rwt.Border( 2, "complex", "red" );
        fail();
      } catch( ex ) {
        //expected
      }
    },

    testTooMuchArgumentsForSimpleBorder : function() {
      try {
        var border = new org.eclipse.rwt.Border( 2, "solid", "red", 4 );
        fail();
      } catch( ex ) {
        //expected
      }
    },

    testRenderSimpleBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var border = new org.eclipse.rwt.Border( 2, "solid", "#FF00FF" );
      var widget = this._createWidget();
      assertEquals( [ "", "", "", "" ], this._getBorderColors( widget._style ) );
      assertEquals( [ "", "", "", "" ], this._getBorderStyles( widget._style ) );
      assertEquals( [ 0, 0, 0, 0 ], this._getBorderWidths( widget._style ) );
      widget.setBorder( border );
      testUtil.flush();
      var expectedColors = [ "#FF00FF", "#FF00FF", "#FF00FF", "#FF00FF" ];
      var expectedStyles = [ "solid", "solid", "solid", "solid" ];
      var expectedWidths = [ 2, 2, 2, 2 ];
      assertEquals( expectedColors, this._getBorderColors( widget._style ) );
      assertEquals( expectedStyles, this._getBorderStyles( widget._style ) );
      assertEquals( expectedWidths, this._getBorderWidths( widget._style ) );
      widget.destroy();
      border.dispose();
    },
    
    testRemoveSimpleBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var border = new org.eclipse.rwt.Border( 2, "solid", "#FF00FF" );
      var widget = this._createWidget();
      widget.setBorder( border );
      testUtil.flush();
      widget.setBorder( null );
      testUtil.flush();
      assertEquals( [ "", "", "", "" ], this._getBorderColors( widget._style ) );
      assertEquals( [ "", "", "", "" ], this._getBorderStyles( widget._style ) );
      assertEquals( [ 0, 0, 0, 0 ], this._getBorderWidths( widget._style ) );
      widget.destroy();
      border.dispose();
    },
    
    testRenderDifferendEdges : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var color = [ "#FF00EF", "#FF00EE", "#FF0EFF", "#FFE0FF" ];
      var width = [ 1, 2, 3, 4 ];
      var style = [ "solid", "outset", "inset", "groove" ];
      var border = new org.eclipse.rwt.Border( width, style, color );
      var widget = this._createWidget();
      widget.setBorder( border );
      testUtil.flush();
      assertEquals( color, this._getBorderColors( widget._style ) );
      assertEquals( style, this._getBorderStyles( widget._style ) );
      assertEquals( width, this._getBorderWidths( widget._style ) );
      widget.destroy();
      border.dispose();
    },

    testRenderComplexBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var colorOuter = [ "#FF00EF", "#FF00EE", "#FF0EFF", "#FFE0FF" ];
      var colorInner = [ "#DD00EF", "#DD00EE", "#DD0EFF", "#DDE0FF" ];
      var border = new org.eclipse.rwt.Border( 2, "complex", colorOuter, colorInner ); 
      var widget = this._createWidget();
      widget.setBorder( border );
      testUtil.flush();
      assertEquals( colorOuter, this._getBorderColors( widget._style ) );
      var solidStyles = [ "solid", "solid", "solid", "solid" ];
      assertEquals( solidStyles, this._getBorderStyles( widget._style ) );
      assertEquals( colorOuter, this._getBorderColors( widget._style ) );
      if( org.eclipse.rwt.Client.isGecko() ) {
        assertEquals( [ 2, 2, 2, 2 ], this._getBorderWidths( widget._style ) );        
        assertEquals( "rgb(255, 0, 239) rgb(221, 0, 239)", widget._style.MozBorderTopColors );
        assertEquals( "rgb(255, 0, 238) rgb(221, 0, 238)", widget._style.MozBorderRightColors );
        assertEquals( "rgb(255, 14, 255) rgb(221, 14, 255)", widget._style.MozBorderBottomColors );
        assertEquals( "rgb(255, 224, 255) rgb(221, 224, 255)", widget._style.MozBorderLeftColors );
      } else {
        assertEquals( [ 1, 1, 1, 1 ], this._getBorderWidths( widget._style ) );
        assertEquals( solidStyles, this._getBorderStyles( widget._innerStyle ) );
        assertEquals( [ 1, 1, 1, 1 ], this._getBorderWidths( widget._innerStyle ) );
        assertEquals( colorInner, this._getBorderColors( widget._innerStyle ) );
      }
      widget.destroy();
      border.dispose();
    },

    testRemoveComplexBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var colorOuter = [ "#FF00EF", "#FF00EE", "#FF0EFF", "#FFE0FF" ];
      var colorInner = [ "#DD00EF", "#DD00EE", "#DD0EFF", "#DDE0FF" ];
      var border = new org.eclipse.rwt.Border( 2, "complex", colorOuter, colorInner  );
      var widget = this._createWidget();
      widget.setBorder( border );
      testUtil.flush();
      widget.setBorder( null );
      testUtil.flush();
      assertEquals( [ "", "", "", "" ], this._getBorderColors( widget._style ) );
      assertEquals( [ "", "", "", "" ], this._getBorderStyles( widget._style ) );
      assertEquals( [ 0, 0, 0, 0 ], this._getBorderWidths( widget._style ) );
      if( org.eclipse.rwt.Client.isGecko() ) {
        assertEquals( "", widget._style.MozBorderTopColors );
        assertEquals( "", widget._style.MozBorderRightColors );
        assertEquals( "", widget._style.MozBorderBottomColors );
        assertEquals( "", widget._style.MozBorderLeftColors );
      } else {
        assertEquals( [ "", "", "", "" ], this._getBorderColors( widget._innerStyle ) );
        assertEquals( [ "", "", "", "" ], this._getBorderStyles( widget._innerStyle ) );
        assertEquals( [ 0, 0, 0, 0 ], this._getBorderWidths( widget._innerStyle ) );
      }
      widget.destroy();
      border.dispose();
    },


    testRenderSimpleAfterComplexBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var colorSimple = [ "#00EF00", "#00EE00", "#0EFF00", "#E0FF00" ];
      var colorOuter = [ "#FF00EF", "#FF00EE", "#FF0EFF", "#FFE0FF" ];
      var colorInner = [ "#DD00EF", "#DD00EE", "#DD0EFF", "#DDE0FF" ];
      var complexBorder = new org.eclipse.rwt.Border( 2, "complex", colorOuter, colorInner );
      var simpleBorder = new org.eclipse.rwt.Border( 2, "outset", colorSimple );
      var widget = this._createWidget();
      widget.setBorder( complexBorder );
      testUtil.flush();
      widget.setBorder( simpleBorder );
      testUtil.flush();
      var outsetStyles = [ "outset", "outset", "outset", "outset" ];
      assertEquals( colorSimple, this._getBorderColors( widget._style ) );
      assertEquals( outsetStyles, this._getBorderStyles( widget._style ) );
      assertEquals( [ 2, 2, 2, 2 ], this._getBorderWidths( widget._style ) );        
      if( org.eclipse.rwt.Client.isGecko() ) {
        assertEquals( "", widget._style.MozBorderTopColors );
        assertEquals( "", widget._style.MozBorderRightColors );
        assertEquals( "", widget._style.MozBorderBottomColors );
        assertEquals( "", widget._style.MozBorderLeftColors );
      } else {
        assertEquals( [ "", "", "", "" ], this._getBorderStyles( widget._innerStyle ) );
        assertEquals( [ 0, 0, 0, 0 ], this._getBorderWidths( widget._innerStyle ) );
        assertEquals( [ "", "", "", "" ], this._getBorderColors( widget._innerStyle ) );
      }
      widget.destroy();
      simpleBorder.dispose();
      complexBorder.dispose();
    },
    
    // NOTE : This tests relies on the fact that the push-button has a rounded border
    //        that doesnt change on hover. Should the change in the default theme, the test
    //        looses its validity.
    testCacheRoundedBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var button = new org.eclipse.rwt.widgets.Button( "push" ); 
      button.addState( "rwt_PUSH" );       
      button.addToDocument();
      testUtil.flush();
      var borderOne = button.getBorder();
      button.addState( "over" );
      testUtil.flush();
      var borderTwo = button.getBorder();
      assertIdentical( borderOne, borderTwo );
    },
    
    testRenderRoundedBorder : function() {
      // NOTE: Tests for non-css3 browser are in GraphicsMixinTest
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var radii = [ 0, 1, 2, 3 ];
        var border = new org.eclipse.rwt.Border( 3, "rounded", "#FF00FF", [ 0, 1, 2, 3 ] );
        var widget = this._createWidget();
        assertEquals( [ "", "", "", "" ], this._getBorderColors( widget._style ) );
        assertEquals( [ "", "", "", "" ], this._getBorderStyles( widget._style ) );
        assertEquals( [ 0, 0, 0, 0 ], this._getBorderWidths( widget._style ) );
        assertEquals( [ 0, 0, 0, 0 ], this._getBorderRadii( widget._style ) );
        widget.setBorder( border );
        testUtil.flush();
        var expectedColors = [ "#FF00FF", "#FF00FF", "#FF00FF", "#FF00FF" ];
        var expectedStyles = [ "solid", "solid", "solid", "solid" ];
        var expectedWidths = [ 3, 3, 3, 3 ];
        assertEquals( expectedColors, this._getBorderColors( widget._style ) );
        assertEquals( expectedStyles, this._getBorderStyles( widget._style ) );
        assertEquals( expectedWidths, this._getBorderWidths( widget._style ) );
        assertEquals( radii, this._getBorderRadii( widget._style ) );
        border.dispose();
      }
    },
    
    testRemoveRoundedBorder : function() {
      // NOTE: Tests for non-css3 browser are in GraphicsMixinTest
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var radii = [ 0, 1, 2, 3 ];
        var border = new org.eclipse.rwt.Border( 3, "rounded", "#FF00FF", [ 0, 1, 2, 3 ] );
        var widget = this._createWidget();
        widget.setBorder( border );
        testUtil.flush();
        widget.setBorder( null );
        testUtil.flush();
        assertEquals( [ "", "", "", "" ], this._getBorderColors( widget._style ) );
        assertEquals( [ "", "", "", "" ], this._getBorderStyles( widget._style ) );
        assertEquals( [ 0, 0, 0, 0 ], this._getBorderWidths( widget._style ) );
        assertEquals( [ 0, 0, 0, 0 ], this._getBorderRadii( widget._style ) );
        border.dispose();
      }
    },

    testRemoveRadiiWithSimpleBorder : function() {
      // NOTE: Tests for non-css3 browser are in GraphicsMixinTest
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var radii = [ 0, 1, 2, 3 ];
        var border = new org.eclipse.rwt.Border( 3, "rounded", "#FF00FF", [ 0, 1, 2, 3 ] );
        var widget = this._createWidget();
        widget.setBorder( border );
        testUtil.flush();
        var simpleBorder = new org.eclipse.rwt.Border( 3, "solid", "#FF00FF" );
        widget.setBorder( simpleBorder );
        testUtil.flush();
        assertEquals( [ 0, 0, 0, 0 ], this._getBorderRadii( widget._style ) );
        border.dispose();
      }
    },

    testRemoveRadiiWithComplexBorder : function() {
      // NOTE: Tests for non-css3 browser are in GraphicsMixinTest
      if( org.eclipse.rwt.Client.supportsCss3() ) {
        var radii = [ 0, 1, 2, 3 ];
        var border = new org.eclipse.rwt.Border( 3, "rounded", "#FF00FF", [ 0, 1, 2, 3 ] );
        var widget = this._createWidget();
        widget.setBorder( border );
        testUtil.flush();
        var colorOuter = [ "#FF00EF", "#FF00EE", "#FF0EFF", "#FFE0FF" ];
        var colorInner = [ "#DD00EF", "#DD00EE", "#DD0EFF", "#DDE0FF" ];
        var complexBorder = new org.eclipse.rwt.Border( 2, "complex", colorOuter, colorInner );
        widget.setBorder( complexBorder );
        testUtil.flush();
        assertEquals( [ 0, 0, 0, 0 ], this._getBorderRadii( widget._style ) );
        border.dispose();
      }
    },

    //////////////
    // helper
    
    _createWidget : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      widget.addToDocument();
      testUtil.flush();
      return widget;
    },
    
    _getBorderColors : function( style ) {
      var colorUtil = qx.util.ColorUtil;
      var result = [];
      result[ 0 ] = style.borderTopColor; 
      result[ 1 ] = style.borderRightColor;
      result[ 2 ] = style.borderBottomColor; 
      result[ 3 ] = style.borderLeftColor;
      for( var i = 0; i < 4; i++ ) {
        try {
          result[ i ] = "#" + colorUtil.rgbToHexString( colorUtil.stringToRgb( result [ i ] ) );
        } catch( ex ) {
          // no color defined
        }
      }
      return result;
    },
    
    _getBorderStyles : function( style ) {
      var result = [];
      result[ 0 ] = style.borderTopStyle; 
      result[ 1 ] = style.borderRightStyle;
      result[ 2 ] = style.borderBottomStyle; 
      result[ 3 ] = style.borderLeftStyle;
      for( var i = 0; i < 4; i++ ) {
        result[ i ] = result[ i ] === "none" ? "" : result[ i ]; 
      }
      return result;
    },
    
    _getBorderWidths : function( style ) {
      var result = [];
      result[ 0 ] = parseInt( style.borderTopWidth ); 
      result[ 1 ] = parseInt( style.borderRightWidth );
      result[ 2 ] = parseInt( style.borderBottomWidth );
      result[ 3 ] = parseInt( style.borderLeftWidth );
      for( var i = 0; i < 4; i++ ) {
        result[ i ] = isNaN( result[ i ] ) ? 0 : result[ i ];
      }
      return result;
    },
    
    _getBorderRadii : function( style ) {
      var result = [];
      var props = org.eclipse.rwt.Border._BORDERRADII;
      result[ 0 ] = parseInt( style[ props[ 0 ] ] );
      result[ 1 ] = parseInt( style[ props[ 1 ] ] );
      result[ 2 ] = parseInt( style[ props[ 2 ] ] );
      result[ 3 ] = parseInt( style[ props[ 3 ] ] );
      for( var i = 0; i < 4; i++ ) {
        result[ i ] = isNaN( result[ i ] ) ? 0 : result[ i ];
      }
      return result;
    }

  }
  
} );