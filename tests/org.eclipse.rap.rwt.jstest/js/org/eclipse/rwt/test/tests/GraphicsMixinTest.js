/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.GraphicsMixinTest", {
  extend : qx.core.Object,

	construct : function(){
	  this.gfxBorder = new org.eclipse.rwt.RoundedBorder( 1, "black" );
	  this.cssBorder = new qx.ui.core.Border( 1, "solid", "black" );
	  this.gradient = [ [ 0, "red" ], [ 1, "yellow" ] ];
	},

  members : {

    testSetGradient : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      assertTrue( shell.isSeeable() );      
      assertFalse( this.usesGfxBackground( shell ) );
      shell.setBackgroundGradient( this.gradient );
      var shape = shell._gfxData.backgroundShape;
      assertTrue( this.usesGfxBackground( shell ) );
      assertTrue( gfxUtil.getFillType( shape ) == "gradient" );
      shell.setBackgroundGradient( null );
      testUtil.flush();
      assertFalse( this.usesGfxBackground( shell ) );
      shell.destroy();
      testUtil.flush();
    },

    testSetGradientWhileNotInDOM : function() {    
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      shell.setParent( null );
      testUtil.flush();
      shell.setBackgroundGradient( this.gradient );
      shell.addToDocument();
      testUtil.flush();
      var shape = shell._gfxData.backgroundShape;
      assertTrue( this.usesGfxBackground( shell ) );
      assertTrue( gfxUtil.getFillType( shape ) == "gradient" );
      shell.setParent( null );
      testUtil.flush();
      shell.setBackgroundGradient( null );
      shell.addToDocument();
      testUtil.flush();
      assertFalse( this.usesGfxBackground( shell ) );
      shell.destroy();
      testUtil.flush();
    },

    testSetSolidFillAfterBorder : function() {    
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      shell.setBorder( this.gfxBorder );
      testUtil.flush();
      assertTrue( this.usesGfxBackground( shell ) );
      var shape = shell._gfxData.backgroundShape;
      assertNull( gfxUtil.getFillType( shape ) );
      shell.setBackgroundColor( "green" );
      assertEquals( "color", gfxUtil.getFillType( shape ) );
      assertEquals( "green", gfxUtil.getFillColor( shape ) );
      shell.destroy();
      testUtil.flush();
    },

    testSetSolidFillBeforeBorder : function() {    
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      shell.setBackgroundColor( "green" );
      shell.setBorder( this.gfxBorder );
      testUtil.flush();
      assertTrue( this.usesGfxBackground( shell ) );
      var shape = shell._gfxData.backgroundShape;
      assertEquals( "color", gfxUtil.getFillType( shape ) );
      assertEquals( "green", gfxUtil.getFillColor( shape ) );
      shell.destroy();
      testUtil.flush();
    },

    testRestoreBackgroundColor : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      shell.setBackgroundColor( "green" );          
      assertEquals( "green", testUtil.getCssBackgroundColor( shell ) );
      shell.setBackgroundGradient( this.gradient );
      assertNull( testUtil.getCssBackgroundColor( shell ) );
      shell.setBackgroundGradient( null );
      assertEquals( "green", testUtil.getCssBackgroundColor( shell ) );
      shell.destroy();
      testUtil.flush();
    },

    testCssBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      var node = shell.getElement();
      shell.setBorder( this.cssBorder );
      testUtil.flush();
      assertTrue( testUtil.hasCssBorder( node ) );
      shell.setBorder( this.gfxBorder );
      testUtil.flush();
      qx.ui.core.Widget.flushGlobalQueues();
      assertFalse( testUtil.hasCssBorder( node ) );      
      shell.setBorder( this.cssBorder );
      testUtil.flush();
      assertTrue( testUtil.hasCssBorder( node ) );
      shell.setBorder( null );
      testUtil.flush();
      assertFalse( testUtil.hasCssBorder( node ) );
      shell.destroy();
      testUtil.flush();
    },

    testPadding : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var widget = this._createWidget();
      this.gfxBorder.setWidth( 1 );
      widget.setBorder( this.gfxBorder );
      testUtil.flush();
      assertTrue( widget.getElement() !== widget._getTargetNode() );
      assertEquals( [ 1, 1, 1, 1 ], this.getFakePadding( widget ) );
      assertEquals( [ 1, 1, 1, 1 ], this.getBorderCache( widget ) );
      this.gfxBorder.setWidth( 0 );
      testUtil.flush();      
      assertEquals ( [ 0, 0, 0, 0 ], this.getFakePadding( widget ) );
      assertEquals ( [ 0, 0, 0, 0 ], this.getBorderCache( widget ) );
      this.gfxBorder.setWidth( 4, 0, 3, 0 );
      testUtil.flush();      
      assertEquals ( [ 4, 0, 3, 0 ], this.getFakePadding( widget ) );      
      assertEquals ( [ 4, 0, 3, 0 ], this.getBorderCache( widget ) );      
      widget.setBackgroundGradient( this.gradient );
      widget.setBorder( null );
      testUtil.flush();
      assertEquals ( [ 0, 0, 0, 0 ], this.getBorderCache( widget ) );      
      assertEquals ( [ 0, 0, 0, 0 ], this.getFakePadding( widget ) );
      widget.destroy();
      testUtil.flush();
    },

    testRoundedBorderWidth : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var widget = this._createWidget();
      this.gfxBorder.setRadius( 3 );      
      this.gfxBorder.setWidth( 0 );
      widget.setBorder( this.gfxBorder ); 
      testUtil.flush();
      assertTrue( this.usesGfxBorder( widget ) );
      var shape = widget._gfxData.backgroundShape;
      assertEquals( 0, gfxUtil.getStrokeWidth( shape ) );
      this.gfxBorder.setWidth( 2 );
      testUtil.flush();
      assertEquals( 2, gfxUtil.getStrokeWidth( shape ) );
      this.gfxBorder.setWidth( 1, 2, 3, 4 );
      testUtil.flush();
      assertEquals( 4, gfxUtil.getStrokeWidth( shape ) );
      widget.destroy();
      testUtil.flush();
    },

    testOutline : function() {
      // in Safari the outline must always be set because the default 
      // outline is visually incompatible with SVG (glitch)
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      this.gfxBorder.setRadius( 4 );
      shell.setBorder( this.gfxBorder );
      shell.setHideFocus( false );
      testUtil.flush();
      assertTrue( this.widgetContainsCanvas( shell ) );
      shell.blur();
      testUtil.flush();
      assertTrue( shell.getElement().style.outline != "" );
      shell.focus();
      testUtil.flush();
      assertTrue( shell.getElement().style.outline != "");
      shell.setHideFocus( true );
      shell.blur();
      testUtil.flush();
      assertTrue( shell.getElement().style.outline != "" );
      shell.focus();
      testUtil.flush();     
      assertTrue( shell.getElement().style.outline != "" );
      shell.destroy();
      testUtil.flush();     
    },

    testGfxRadiusInvisibleEdges : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new org.eclipse.rwt.widgets.MultiCellWidget( [] ); 
      widget.addToDocument();
      widget.setLocation( 0, 0 );
      widget.setDimension( 100, 100 );
      this.gfxBorder.setWidthTop( 1 );
      this.gfxBorder.setWidthRight( 0 );
      this.gfxBorder.setWidthBottom( 0 );
      this.gfxBorder.setWidthLeft( 1 );
      this.gfxBorder.setRadius( 5 );
      widget.setBorder( this.gfxBorder ); 
      testUtil.flush();
      assertTrue( this.usesGfxBorder( widget ) );
      var radii = widget.getGfxProperty( "borderRadii", radii );
      assertEquals( [ 5, 0, 0, 0 ], radii );
      widget.destroy();
      testUtil.flush();      
    },

    testGfxRadiusInvisibleBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new org.eclipse.rwt.widgets.MultiCellWidget( [] ); 
      widget.addToDocument();
      widget.setLocation( 0, 0 );
      widget.setDimension( 100, 100 );
      this.gfxBorder.setWidth( 0 );
      this.gfxBorder.setRadius( 5 );
      widget.setBorder( this.gfxBorder ); 
      testUtil.flush();
      assertTrue( this.usesGfxBorder( widget ) );
      var radii = widget.getGfxProperty( "borderRadii", radii );
      assertEquals( [ 5, 5, 5, 5 ], radii );
      widget.destroy();
      testUtil.flush();      
    },

    testGfxBackgroundImage : function() {
      var widget = new org.eclipse.rwt.widgets.MultiCellWidget( [] ); 
      widget.addToDocument();
      widget.setLocation( 0, 0 );
      widget.setDimension( 100, 100 );
      this.gfxBorder.setWidth( 0 );
      this.gfxBorder.setRadius( 5 );
      widget.setBorder( this.gfxBorder ); 
      widget.setBackgroundImage( "bla.jpg" );
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( this.usesGfxBackground( widget ) );
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shape = widget._gfxData.backgroundShape;
      assertTrue( gfxUtil.getFillType( shape ) == "pattern" );
      widget.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },

    testGfxBackgroundColorToImage : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      shell.setBackgroundColor( "green" );
      shell.setBorder( this.gfxBorder );
      testUtil.flush();
      assertTrue( this.usesGfxBackground( shell ) );
      var shape = shell._gfxData.backgroundShape;
      assertEquals( "color", gfxUtil.getFillType( shape ) );
      shell.setBackgroundImage( "bla.jpg" );
      testUtil.flush();
      assertTrue( gfxUtil.getFillType( shape ) == "pattern" );
      shell.destroy();
      testUtil.flush();
    },

    testOnCanvasAppearOnWidgetInsert : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget1 = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      var widget2 = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      var log = [];
      widget1._onCanvasAppear = function(){ log.push( "widget1" ); };
      widget2._onCanvasAppear = function(){ log.push( "widget2" ); };
      widget1.addToDocument();
      widget2.addToDocument();
      widget1.setBorder( this.gfxBorder );
      widget2.setBorder( this.gfxBorder );
      widget2.setVisibility( false );
      testUtil.flush();
      assertEquals( [ "widget1", "widget2" ], log );
      widget1.destroy();
      widget2.destroy();
    },

    testOnCanvasAppearOnSetDisplay : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget1 = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      var widget2 = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      var log = [];
      widget1._onCanvasAppear = function(){ log.push( "widget1" ); };
      widget2._onCanvasAppear = function(){ log.push( "widget2" ); };
      widget1.addToDocument();
      widget2.addToDocument();
      widget1.setBorder( this.gfxBorder );
      widget2.setBorder( this.gfxBorder );
      widget2.setVisibility( false );
      testUtil.flush();
      assertEquals( [ "widget1", "widget2" ], log );
      widget1.setDisplay( false );
      widget2.setDisplay( false );
      testUtil.flush();
      widget1.setDisplay( true );
      widget2.setDisplay( true );
      testUtil.flush();
      assertEquals( [ "widget1", "widget2", "widget1", "widget2" ], log );
      testUtil.flush();
    },

    testOnCanvasAppearOnEnhancedBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = this._createShell();
      var widget1 = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      var widget2 = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      var log = [];
      widget1._onCanvasAppear = function(){ log.push( "widget1" ); };
      widget2._onCanvasAppear = function(){ log.push( "widget2" ); };
      widget1.setParent( shell );
      widget2.setParent( shell );
      widget1.setBorder( this.gfxBorder );
      widget2.setBorder( this.gfxBorder );
      widget2.setVisibility( false );
      testUtil.flush();
      assertEquals( [ "widget1", "widget2" ], log );
      shell.setBackgroundColor( "green" );
      shell.setBorder( this.gfxBorder );
      testUtil.flush();
      assertEquals( [ "widget1", "widget2", "widget1", "widget2" ], log );
      shell.destroy();
      testUtil.flush();
    },

    testOpacityWidthEnhancedBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var widget = this._createWidget();
      assertTrue( widget.isSeeable() );      
      assertTrue( widget.getElement() === widget._getTargetNode() );
      widget.setOpacity( 0.5 );
      assertTrue( testUtil.hasElementOpacity( widget.getElement() ) );
      widget.setBackgroundGradient( this.gradient );
      assertTrue( widget.getElement() !== widget._getTargetNode() );
      assertTrue( testUtil.hasElementOpacity( widget.getElement() ) );
      assertFalse( testUtil.hasElementOpacity( widget._getTargetNode() ) );
      widget.setOpacity( 1 );
      assertFalse( testUtil.hasElementOpacity( widget.getElement() ) );
      assertFalse( testUtil.hasElementOpacity( widget._getTargetNode() ) );
      widget.destroy();
    },

    testAntialiasingBugIE : qx.core.Variant.select("qx.client", {
      "mshtml" : function() {
        var gfxUtil = org.eclipse.rwt.GraphicsUtil;
        var widget = this._createWidget();
        assertTrue( widget.isSeeable() );      
        assertTrue( widget.getElement() === widget._getTargetNode() );      
        widget.setBackgroundGradient( this.gradient );
        assertTrue( this.usesGfxBackground( widget ) );
        var innerCSS = widget._getTargetNode().style.cssText.toLowerCase();
        var outerCSS = widget.getElement().style.cssText.toLowerCase();
        assertTrue( innerCSS.indexOf( "filter" ) == -1 );
        assertTrue( outerCSS.indexOf( "filter" ) == -1 );      
        widget.destroy();
      },
      "default" : function(){}
    } ),

    testAntialiasingBugIEWithOpacitySet : qx.core.Variant.select("qx.client", {
      "mshtml" : function() {
        var gfxUtil = org.eclipse.rwt.GraphicsUtil;
        var widget = this._createWidget();
        widget.setOpacity( 0.5 );
        assertTrue( widget.isSeeable() );      
        assertTrue( widget.getElement() === widget._getTargetNode() );      
        widget.setBackgroundGradient( this.gradient );
        assertTrue( this.usesGfxBackground( widget ) );
        var innerCSS = widget._getTargetNode().style.cssText.toLowerCase();
        var outerCSS = widget.getElement().style.cssText.toLowerCase();
        assertTrue( innerCSS.indexOf( "filter" ) == -1 );
        assertTrue( outerCSS.indexOf( "filter" ) != -1 );
        widget.setOpacity( 1 );      
        innerCSS = widget._getTargetNode().style.cssText.toLowerCase();
        outerCSS = widget.getElement().style.cssText.toLowerCase();
        assertTrue( innerCSS.indexOf( "filter" ) == -1 );
        assertTrue( outerCSS.indexOf( "filter" ) == -1 );
        widget.destroy();
      },
      "default" : function(){}
    } ),
    
    testSetBackgroundImageOverwritesGradient : function() {
      // Note: a gradient and background-image can note be set at the same
      // time via CSS, but when using CSS and Java-API.  
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new org.eclipse.rwt.widgets.MultiCellWidget( [] ); 
      this.gfxBorder.setRadius( 5 );
      widget.addToDocument();
      widget.setLocation( 0, 0 );
      widget.setDimension( 100, 100 );
      widget.setBorder( this.gfxBorder ); 
      widget.setBackgroundImage( "bla.jpg" );
      widget.setBackgroundGradient( this.gradient );
      testUtil.flush();
      assertTrue( this.usesGfxBackground( widget ) );
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shape = widget._gfxData.backgroundShape;
      assertEquals( "pattern", gfxUtil.getFillType( shape )  );
      widget.destroy();
    },

    testChangeBackgroundImageWhileBackgoundGradientSet : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = new org.eclipse.rwt.widgets.MultiCellWidget( [] ); 
      widget.addToDocument();
      widget.setLocation( 0, 0 );
      widget.setDimension( 100, 100 );
      widget.setBackgroundGradient( this.gradient );
      testUtil.flush();
      assertTrue( "initally uses gfx", this.usesGfxBackground( widget ) );      
      widget.setBackgroundImage( "bla.jpg" );
      testUtil.flush();
      assertFalse( "no more gfx", this.usesGfxBackground( widget ) );
      var src = widget._getTargetNode().style.backgroundImage;
      assertTrue( "normal background", src.indexOf( "bla.jpg" ) != -1 );
      widget.setBackgroundImage( null );
      src = widget._getTargetNode().style.backgroundImage;
      assertTrue( "no more normal background", src.indexOf( "bla.jpg" ) == -1 );
      assertTrue( "uses gfx again", this.usesGfxBackground( widget ) );
      widget.destroy();
    },
    
    testSetShadowCreatesCanvas : function() {
      if( org.eclipse.rwt.GraphicsMixin.getSupportsShadows() ) {
        var widget = this._createWidget();
        widget.setShadow( [ false, 10, 10, 10, 3, "#ff00ff", 1 ] );
        assertTrue( this.widgetContainsCanvas( widget ) );
        widget.setShadow( null );
        assertFalse( this.widgetContainsCanvas( widget ) );
        widget.destroy();
      }
    },
    
    testSetShadowCreatesShape : function() {
      if( org.eclipse.rwt.GraphicsMixin.getSupportsShadows() ) {
        var gfxUtil = org.eclipse.rwt.GraphicsUtil;
        var widget = this._createWidget();
        widget.setShadow( [ false, 10, 10, 10, 3, "#ff00ff", 1 ] );
        var canvas = this._getCanvasGroupNode( widget._gfxCanvas );
        var shape = widget._gfxData.shadowShape.node;
        assertTrue( shape.parentNode === canvas );
        widget.setShadow( null );
        assertTrue( shape.parentNode !== canvas );
        widget.setShadow( [ false, 10, 10, 10, 3, "#ff00ff", 1 ] );
        assertTrue( shape.parentNode === canvas );
        widget.destroy();
      }
    },
    
    testDisableShadow : function() {
      if( org.eclipse.rwt.GraphicsMixin.getSupportsShadows() ) {
        var gfxUtil = org.eclipse.rwt.GraphicsUtil;
        var widget = this._createWidget();
        widget.setBackgroundGradient( this.gradient );
        widget.setBorder( this.gfxBorder );
        widget.setShadow( [ false, 10, 10, 10, 3, "#ff00ff", 1 ] );
        var canvas = this._getCanvasGroupNode( widget._gfxCanvas );
        var shape = widget._gfxData.shadowShape.node;
        assertTrue( shape.parentNode === canvas );
        widget.setShadow( null );
        assertTrue( shape.parentNode !== canvas );
        widget.destroy();
      }
    },
    
    testSetShadowEnablesOverflow : function() {
      if( org.eclipse.rwt.GraphicsMixin.getSupportsShadows() ) {
        var gfxUtil = org.eclipse.rwt.GraphicsUtil;
        var widget = this._createWidget();
        widget.setShadow( [ false, 10, 10, 10, 3, "#ff00ff", 1 ] );
        var canvas = widget._gfxCanvas;
        var isMshtml = qx.core.Variant.isSet( "qx.client", "mshtml" );
        if( !isMshtml )  {
          assertTrue( canvas.node !== canvas.group );
        }
        var node = gfxUtil.getCanvasNode( canvas );
        var pointerEvents = node.style.pointerEvents === "none";
        // indicates that HtmlUtil.passEventsThrough is activated:
        var cursorDefault = node.style.cursor === "default"; 
        assertTrue( pointerEvents || cursorDefault );
        widget.destroy();
      }
    },

    testSetShadowAndGradient : function() {
      if( org.eclipse.rwt.GraphicsMixin.getSupportsShadows() ) {
        var gfxUtil = org.eclipse.rwt.GraphicsUtil;
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var isMshtml = qx.core.Variant.isSet( "qx.client", "mshtml" );
        var childOffset = isMshtml ? 0 : 1;// Count defs-node
        var widget = this._createWidget();
        widget.setBackgroundGradient( this.gradient );
        testUtil.flush();
        widget.setShadow( [ false, 10, 10, 10, 3, "#ff00ff", 1 ] );
        var canvasNode = this._getCanvasGroupNode( widget._gfxCanvas );
        var shadow = widget._gfxData.shadowShape.node;
        var background = widget._gfxData.backgroundShape.node;
        assertEquals( 2 + childOffset, canvasNode.childNodes.length );  
        assertIdentical( canvasNode, background.parentNode );
        assertIdentical( canvasNode, shadow.parentNode );
        assertIdentical( shadow, canvasNode.childNodes[ 0 + childOffset ] );
        assertIdentical( background, canvasNode.childNodes[ 1 + childOffset ] );
        widget.setBackgroundGradient( null );
        testUtil.flush();
        assertFalse( background.parentNode === canvasNode );
        assertIdentical( canvasNode, shadow.parentNode );
        assertEquals( 1 + childOffset, canvasNode.childNodes.length );
        widget.destroy();
      }
    },
    
    testSetShadowBeforeCreate : function() {
      if( org.eclipse.rwt.GraphicsMixin.getSupportsShadows() ) {
        var gfxUtil = org.eclipse.rwt.GraphicsUtil;
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget( true );
        widget.setShadow( [ false, 10, 10, 10, 3, "#ff00ff", 1 ] );
        testUtil.flush();
        var canvas = this._getCanvasGroupNode( widget._gfxCanvas );
        var shadow = widget._gfxData.shadowShape.node;
        assertIdentical( canvas, shadow.parentNode );
        widget.destroy();
      }
    },
    
    testRemoveGradientSetShadow : function() {
      if( org.eclipse.rwt.GraphicsMixin.getSupportsShadows() ) {
        var gfxUtil = org.eclipse.rwt.GraphicsUtil;
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var widget = this._createWidget();
        widget.setBackgroundGradient( this.gradient );
        testUtil.flush();
        var canvas = this._getCanvasGroupNode( widget._gfxCanvas );
        var background = widget._gfxData.backgroundShape.node;
        assertTrue( canvas === background.parentNode );
        widget.setBackgroundGradient( null );
        widget.setShadow( [ false, 10, 10, 10, 3, "#ff00ff", 1 ] );
        canvas = this._getCanvasGroupNode( widget._gfxCanvas );
        var shadow = widget._gfxData.shadowShape.node;
        assertTrue( canvas === shadow.parentNode );
        assertFalse( canvas === background.parentNode );
        widget.destroy();
      }
    },

    testSetShadowColorAndOpacity : function() {
      // Note: In the CSS3 speficifcation the opacity is part of the color,
      // for simplicity this is a sepearate value in our shadow-array.
      if( org.eclipse.rwt.GraphicsMixin.getSupportsShadows() ) {
        var gfxUtil = org.eclipse.rwt.GraphicsUtil;
        var widget = this._createWidget();
        widget.setShadow( [ false, 10, 10, 3, 10, "#ff00ff", 1 ] );
        var shape = widget._gfxData.shadowShape;
        assertEquals( 3, gfxUtil.getBlur( shape ) );
        assertEquals( "#ff00ff", gfxUtil.getFillColor( shape ) );
        assertEquals( 1, gfxUtil.getOpacity( shape ) );
        widget.setShadow( [ false, 10, 10, 5, 10, "#000000", 0.5 ] );
        assertEquals( 5, gfxUtil.getBlur( shape ) );
        assertEquals( "#000000", gfxUtil.getFillColor( shape ) );
        assertEquals( 0.5, gfxUtil.getOpacity( shape ) );
        widget.setShadow( [ false, 10, 10, 0, 0, "#ffffff", 0.5 ] );
        assertEquals( 0, gfxUtil.getBlur( shape ) );
        assertEquals( "#ffffff", gfxUtil.getFillColor( shape ) );
        assertEquals( 0.5, gfxUtil.getOpacity( shape ) );
        widget.destroy();
      }
    },
    
    testBasicShadowLayout : function() {
      if( org.eclipse.rwt.GraphicsMixin.getSupportsShadows() ) {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var gfxUtil = org.eclipse.rwt.GraphicsUtil;
        var isMshtml = qx.core.Variant.isSet( "qx.client", "mshtml" );
        var widget = this._createWidget();
        widget.setShadow( [ false, 0, 0, 0, 0, "#000000", 1 ] );
        var shape = widget._gfxData.shadowShape;
        var expected1;
        var expected2;
        if( isMshtml ) {
          expected1 = " m-5,-5 l995,-5,995,995,-5,995 xe";
          expected2 = " m-5,-5 l1195,-5,1195,1495,-5,1495 xe";
        } else {
          expected1 = "M 0 0 L 100 0 100 0 L 100 100 100 100 L 0 100 0 100 Z";
          expected2 = "M 0 0 L 120 0 120 0 L 120 150 120 150 L 0 150 0 150 Z";
        }
        assertEquals( expected1, this._getPath( shape ) );
        widget.setWidth( 120 );
        widget.setHeight( 150 );
        testUtil.flush();
        assertEquals( expected2, this._getPath( shape ) );
        widget.destroy();
      }
    },
    
    testRoundedShadowLayout : function() {
      if( org.eclipse.rwt.GraphicsMixin.getSupportsShadows() ) {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var gfxUtil = org.eclipse.rwt.GraphicsUtil;
        var isMshtml = qx.core.Variant.isSet( "qx.client", "mshtml" );
        var widget = this._createWidget();
        widget.setShadow( [ false, 0, 0, 0, 0, "#000000", 1 ] );
        this.gfxBorder.setRadius( 5 );
        widget.setBorder( this.gfxBorder );
        testUtil.flush(); 
        var shape = widget._gfxData.shadowShape;
        var expected1;
        var expected2;
        if( isMshtml ) {
          expected1 = " al45,45,50,50,-11796300,-5898150 ae945,45,50,50,-17694450,-5898150 ae945,945,50,50,0,-5898150 ae45,945,50,50,-5898150,-5898150 x e";
          expected2 = " m-5,-5 l995,-5,995,995,-5,995 xe";
        } else {
          expected1 = "M 0 5 A 5 5 0 0 1 5 0 L 95 0 A 5 5 0 0 1 100 5 L 100 95 A 5 5 0 0 1 95 100 L 5 100 A 5 5 0 0 1 0 95 Z";
          expected2 = "M 0 0 L 100 0 100 0 L 100 100 100 100 L 0 100 0 100 Z";
        }
        assertEquals( expected1, this._getPath( shape ) );
        widget.setBorder( null );
        testUtil.flush();
        assertEquals( expected2, this._getPath( shape ) );
        widget.destroy();
      }
    },

    testShiftShadowLayout : function() {
      if( org.eclipse.rwt.GraphicsMixin.getSupportsShadows() ) {
        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var gfxUtil = org.eclipse.rwt.GraphicsUtil;
        var isMshtml = qx.core.Variant.isSet( "qx.client", "mshtml" );
        var widget = this._createWidget();
        widget.setShadow( [ false, 3, 5, 0, 0, "#000000", 1 ] );
        var shape = widget._gfxData.shadowShape;
        var expected1;
        var expected2;
        if( isMshtml ) {
          expected1 = " m55,35 l1055,35,1055,1035,55,1035 xe";
          expected2 = " m55,35 l1255,35,1255,1535,55,1535 xe";
        } else {
          expected1 = "M 6 4 L 106 4 106 4 L 106 104 106 104 L 6 104 6 104 Z";
          expected2 = "M 6 4 L 126 4 126 4 L 126 154 126 154 L 6 154 6 154 Z";
        }
        widget.setShadow( [ false, 6, 4, 0, 0, "#000000", 1 ] );
        assertEquals( expected1, this._getPath( shape ) );
        widget.setWidth( 120 );
        widget.setHeight( 150 );
        testUtil.flush();
        assertEquals( expected2, this._getPath( shape ) );
        widget.destroy();
      }
    },
    
    testEnableDisableOverflowForShadow : qx.core.Variant.select("qx.client", {
      "mshtml" : function() {
      },
      "default" : function(){
        if( org.eclipse.rwt.GraphicsMixin.getSupportsShadows() ) {  
          var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
          var gfxUtil = org.eclipse.rwt.GraphicsUtil;
          var isMshtml = qx.core.Variant.isSet( "qx.client", "mshtml" );
          var widget = this._createWidget();
          widget.setShadow( [ false, -3, -5, 4, 0, "#000000", 1 ] );
          testUtil.flush();
          var canvas = widget._gfxCanvas;
          assertEquals( "108px", canvas.node.style.width );
          assertEquals( "109px", canvas.node.style.height );
          assertEquals( "-7px", canvas.node.style.left );
          assertEquals( "-9px", canvas.node.style.top );
          widget.setShadow( [ false, 3, 5, 2, 0, "#000000", 1 ] );
          testUtil.flush();
          assertTrue( canvas.node.style.left == "0px" );
          assertTrue( canvas.node.style.top == "0px" );
          assertTrue( canvas.node.style.width == "105px" );
          assertTrue( canvas.node.style.height == "107px" );
          widget.setShadow( null );
          testUtil.flush();
          assertTrue( canvas.node.style.left == "0px" );
          assertTrue( canvas.node.style.top == "0px" );
          assertTrue( canvas.node.style.width == "100%" );
          assertTrue( canvas.node.style.height == "100%" );
          widget.destroy();
        }
      }
    } ),
    

    testLayoutTargetNodeAfterBorderRemove : qx.core.Variant.select("qx.client", {
      "mshtml" : function() {
        if( org.eclipse.rwt.GraphicsMixin.getSupportsShadows() ) {
          var gfxUtil = org.eclipse.rwt.GraphicsUtil;
          var widget = this._createWidget();
          widget.setBorder( this.gfxBorder );
          testUtil.flush();
          assertTrue( this.widgetContainsCanvas( widget ) );
          widget.setBorder( null );
          assertFalse( this.widgetContainsCanvas( widget ) );
          widget.setWidth( 400 );
          testUtil.flush();
          assertEquals( "400px", widget._getTargetNode().style.width );
          widget.destroy();
        }
      },
      "default" : function(){}
    } ),
    /////////
    // Helper

    _createShell : function() {
      var result = new org.eclipse.swt.widgets.Shell();
      result.addToDocument();
      result.setBackgroundColor( null );
      result.setShadow( null );
      result.open();
      qx.ui.core.Widget.flushGlobalQueues();
      return result;      
    },

    _createWidget : function( noFlush ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var result = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      result.addToDocument();
      result.setLocation( 0, 0 );
      result.setDimension( 100, 100 );
      if( !noFlush ) {
        testUtil.flush();
      }
      return result;
    },

    usesGfxBorder : function( widget ) {      
      return    widget._gfxBorderEnabled 
             && this.widgetContainsCanvas( widget )
             && widget._gfxData.backgroundShape == widget._gfxData.pathElement; 
    },

    usesGfxBackground : function( widget ) {
      var canvas = widget._gfxCanvas;
      var shape = widget._gfxData ? widget._gfxData.backgroundShape : null;
      var shapeInsert = false;
      if( canvas && shape ) {
        var parent = this._getCanvasGroupNode( widget._gfxCanvas );
        var child = shape.node;
        shapeInsert = parent === child.parentNode;
      }
      var result =    shapeInsert
                   && widget._gfxBackgroundEnabled 
                   && this.widgetContainsCanvas( widget );
      return result;
    },

   widgetContainsCanvas : function( widget ) {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var result = false;
      if( widget._gfxCanvas != null && widget.getElement() != null ) {
        var widgetNode = widget.getElement();
        var canvasNode = gfxUtil.getCanvasNode( widget._gfxCanvas );
        for( var i = 0; i < widgetNode.childNodes.length; i++ ) {
          if( widgetNode.childNodes[ i ] == canvasNode ) {
            result = true;
          }
        }
      }
      return result;
    },

    getFakePadding : function( widget ) {
      var result = null;
      var inner = widget._getTargetNode().style;
      var outer = widget._style;
      var isPx = true;
      isPx = ( inner.width.search( "%" ) == -1 ) ? isPx : false;
      isPx = ( inner.height.search( "%" ) == -1 ) ? isPx : false;
      if( inner == outer || isPx == false ) { 
        result = [ 0, 0, 0, 0 ];
      } else {
        var top = inner.top ? parseFloat( inner.top ) : 0;
        var left = inner.left ? parseFloat( inner.left ) : 0;
        var width = inner.width ? parseFloat( inner.width ) : 0; 
        var height = inner.height ? parseFloat( inner.height ) : 0;         
        var right = parseFloat( outer.width ) - left - width; 
        var bottom = parseFloat( outer.height ) - top - height;         
        result = [ top, right, bottom, left ];
      }
      return result;
    },

    getBorderCache : function( widget ) {
      return [ widget._cachedBorderTop,
               widget._cachedBorderRight,
               widget._cachedBorderBottom,
               widget._cachedBorderLeft ];
    },
    
    _getPath : function( shape ) {
      var result = null;
      var isMshtml = qx.core.Variant.isSet( "qx.client", "mshtml" );
      if( isMshtml ) {
        result = shape.node.path.v;
      } else {
        result = shape.node.getAttribute( "d" );
      }
      return result;
    },
    
    _getCanvasGroupNode : function( canvas ) {
      return canvas.group ? canvas.group : canvas.node;
    }

  }

} );