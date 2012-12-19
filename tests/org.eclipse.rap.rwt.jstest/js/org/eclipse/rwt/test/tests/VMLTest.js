/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.VMLTest", {
  extend : rwt.qx.Object,

  members : {

    TARGETENGINE : [ "mshtml" ],

    testCreateCanvas : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      var canvas = GraphicsUtil.createCanvas();
      var canvasNode = GraphicsUtil.getCanvasNode( canvas );
      assertEquals( "DIV", canvasNode.tagName );
      assertEquals( "0", canvasNode.style.lineHeight );
      assertEquals( "0px", canvasNode.style.fontSize );
      TestUtil.flush();
    },

    testDrawRectInWidget : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      var parent = new rwt.widgets.base.Terminator();
      parent.setLocation( 10, 10 );
      parent.setDimension( 400, 400 );
      parent.addToDocument();
      TestUtil.flush();
      var parentNode = parent._getTargetNode();
      var canvas = GraphicsUtil.createCanvas();
      var shape = GraphicsUtil.createShape( "rect" );
      GraphicsUtil.addToCanvas( canvas, shape );
      parentNode.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.handleAppear( canvas );      
      var canvasNode = parentNode.firstChild;
      assertEquals( "rect", canvasNode.firstChild.tagName );
      assertEquals( null, GraphicsUtil.getFillType( shape ) );
      assertTrue( GraphicsUtil.getDisplay( shape ) );
      parent.destroy();
      TestUtil.flush();
    },

    testFillColor : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = GraphicsUtil.createCanvas();
      var shape = GraphicsUtil.createShape( "rect" );
      GraphicsUtil.addToCanvas( canvas, shape );
      parentNode.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.handleAppear( canvas );
      GraphicsUtil.setFillColor( shape, null );
      assertEquals( null, GraphicsUtil.getFillType( shape ) );
      assertEquals( null, GraphicsUtil.getFillColor( shape ) );
      assertFalse( shape.fill.on );
      GraphicsUtil.setFillColor( shape, "green" );
      assertTrue( shape.fill.on );
      assertEquals( "solid", shape.fill.type );      
      assertEquals( "green", shape.fill.color.value );
      assertEquals( "color", GraphicsUtil.getFillType( shape ) );
      assertEquals( "green", GraphicsUtil.getFillColor( shape ) );
      parentNode.removeChild( GraphicsUtil.getCanvasNode( canvas ) );      
    },

    testTransparentFill : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = GraphicsUtil.createCanvas();
      var shape = GraphicsUtil.createShape( "rect" );
      GraphicsUtil.addToCanvas( canvas, shape );
      parentNode.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.handleAppear( canvas );
      GraphicsUtil.setFillColor( shape, "green" );
      GraphicsUtil.setFillColor( shape, "transparent" );
      assertEquals( null, GraphicsUtil.getFillType( shape ) );
      assertEquals( null, GraphicsUtil.getFillColor( shape ) );
      GraphicsUtil.setFillColor( shape, "" );
      assertEquals( null, GraphicsUtil.getFillType( shape ) );
      assertEquals( null, GraphicsUtil.getFillColor( shape ) );
      assertFalse( shape.fill.on );
      parentNode.removeChild( GraphicsUtil.getCanvasNode( canvas ) );      
    },

    testColorRestore : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = GraphicsUtil.createCanvas();
      var shape = GraphicsUtil.createShape( "rect" );
      GraphicsUtil.addToCanvas( canvas, shape );
      parentNode.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.setFillColor( shape, "green" );
      assertEquals( "green", GraphicsUtil.getFillColor( shape ) );
      parentNode.removeChild( GraphicsUtil.getCanvasNode( canvas ) );
      parentNode.appendChild( GraphicsUtil.getCanvasNode( canvas ) );      
      assertEquals( "green", GraphicsUtil.getFillColor( shape ) );
      assertFalse( "green" == shape.fill.color );
      GraphicsUtil.handleAppear( canvas );
      assertTrue( "green" == shape.fill.color );
      parentNode.removeChild( GraphicsUtil.getCanvasNode( canvas ) );      
    },

    testFillGradient : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = GraphicsUtil.createCanvas();
      var shape = GraphicsUtil.createShape( "rect" );
      GraphicsUtil.addToCanvas( canvas, shape );
      parentNode.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.handleAppear( canvas );
      GraphicsUtil.setFillGradient( shape, [ [ 0, "red" ], [ 1, "yellow" ] ] );
      assertTrue( shape.fill.on );
      assertEquals( "gradient", shape.fill.type );      
      assertEquals( "gradient", GraphicsUtil.getFillType( shape ) );
      assertEquals( "yellow", shape.fill.color2.value );
      assertEquals( 180, shape.fill.angle );
      var expected = "0 red;.25 #ff4000;.5 #ff8000;.75 #ffbf00;1 yellow";
      assertEquals( expected, shape.fill.colors.value );
      parentNode.removeChild( GraphicsUtil.getCanvasNode( canvas ) );      
    },

    testFillGradientHorizontal : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = GraphicsUtil.createCanvas();
      var shape = GraphicsUtil.createShape( "rect" );
      GraphicsUtil.addToCanvas( canvas, shape );
      parentNode.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.handleAppear( canvas );
      var gradient = [ [ 0, "red" ], [ 1, "yellow" ] ];
      gradient.horizontal = true;
      GraphicsUtil.setFillGradient( shape, gradient );
      assertTrue( shape.fill.on );
      assertEquals( "gradient", shape.fill.type );      
      assertEquals( "gradient", GraphicsUtil.getFillType( shape ) );
      assertEquals( "yellow", shape.fill.color2.value );
      assertEquals( 270, shape.fill.angle );
      parentNode.removeChild( GraphicsUtil.getCanvasNode( canvas ) );      
    },

    testDrawRoundRect : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      var canvas = GraphicsUtil.createCanvas();
      var parent = document.body;
      var shape = GraphicsUtil.createShape( "roundrect" );
      GraphicsUtil.setRoundRectLayout( shape, 10, 10, 20, 20, [ 0, 4, 3, 2 ] );
      GraphicsUtil.setStroke( shape, "black", 2 );
      GraphicsUtil.addToCanvas( canvas, shape );
      parent.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.handleAppear( canvas );
      var expected =   " m95,95 ae255,135,40,40,-17694450,-5898150 " 
                     + "ae265,265,30,30,0,-5898150 " 
                     + "ae115,275,20,20,-5898150,-5898150 x e";
      assertEquals( expected, shape.node.path.v );
      parent.removeChild( GraphicsUtil.getCanvasNode( canvas ) );
    },

    testDrawRoundRectOneRadiusOnlyMinimalMize : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      var canvas = GraphicsUtil.createCanvas();
      var parent = document.body;
      var shape = GraphicsUtil.createShape( "roundrect" );
      GraphicsUtil.setRoundRectLayout( shape, 10, 10, 20, 20, [ 20, 0, 0, 0] );
      GraphicsUtil.setStroke( shape, "black", 2 );
      GraphicsUtil.setFillColor( shape, "red" );
      GraphicsUtil.addToCanvas( canvas, shape );
      parent.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.handleAppear( canvas );
      var expected = " al195,195,100,100,-11796300,-5898150 l295,95,295,295,95,295 x e";
      assertEquals( expected, shape.node.path.v );
      parent.removeChild( GraphicsUtil.getCanvasNode( canvas ) );
    },

    testDrawRoundRectOneRadiusOnlyTooSmall : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      var canvas = GraphicsUtil.createCanvas();
      var parent = document.body;
      var shape = GraphicsUtil.createShape( "roundrect" );
      GraphicsUtil.setRoundRectLayout( shape, 10, 10, 20, 20, [ 21, 0, 0, 0] );
      GraphicsUtil.setStroke( shape, "black", 2 );
      GraphicsUtil.setFillColor( shape, "red" );
      GraphicsUtil.addToCanvas( canvas, shape );
      parent.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.handleAppear( canvas );
      var expected = " al195,195,100,100,-11796300,-5898150 l295,95,295,295,95,295 x e";
      assertEquals( expected, shape.node.path.v );
      parent.removeChild( GraphicsUtil.getCanvasNode( canvas ) );
    },

    testDisplay : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      var parent = new rwt.widgets.base.Terminator();
      parent.setLocation( 10, 10 );
      parent.setDimension( 400, 400 );
      parent.addToDocument();
      TestUtil.flush();
      var parentNode = parent._getTargetNode();
      var canvas = GraphicsUtil.createCanvas();
      var shape = GraphicsUtil.createShape( "rect" );
      GraphicsUtil.addToCanvas( canvas, shape );
      GraphicsUtil.setDisplay( shape, false );
      parentNode.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.handleAppear( canvas );      
      var canvasNode = parentNode.firstChild;
      assertFalse( shape.fill.on );
      assertFalse( GraphicsUtil.getDisplay( shape ) );
      parent.destroy();
      TestUtil.flush();
    },
    
    testFillPattern : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      var parentNode = document.body;
      var canvas = GraphicsUtil.createCanvas();
      var shape = GraphicsUtil.createShape( "rect" );
      GraphicsUtil.setRectBounds( shape, 10, 10, 100, 100 );
      GraphicsUtil.setStroke( shape, "black", 2 );
      GraphicsUtil.setFillPattern( shape, "./js/resource/tex.jpg", 70, 70 );
      assertTrue( shape.fill.on );
      GraphicsUtil.addToCanvas( canvas, shape );
      parentNode.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.handleAppear( canvas );
      assertEquals( "rect", shape.node.tagName );
      assertTrue( shape.fill.on );      
      assertEquals( "tile", shape.fill.type );      
      assertEquals( "./js/resource/tex.jpg", shape.fill.src );
      assertEquals( "pattern", GraphicsUtil.getFillType( shape ) );
      parentNode.removeChild( GraphicsUtil.getCanvasNode( canvas ) );
    },
    
    testOpacity : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = GraphicsUtil.createCanvas();
      parentNode.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      var shape = GraphicsUtil.createShape( "rect" );
      GraphicsUtil.addToCanvas( canvas, shape );
      GraphicsUtil.setOpacity( shape, 0.5 );
      assertTrue( shape.node.style.cssText.indexOf( "FILTER:" ) != -1 );
      assertTrue( shape.node.style.filter.indexOf( "opacity=50" ) != -1 );
      assertEquals( 0.5, GraphicsUtil.getOpacity( shape ) ); 
      GraphicsUtil.setOpacity( shape, 1 );
      // It is important for some issues that filter is completely removed:
      assertTrue( shape.node.style.cssText.indexOf( "FILTER:" ) == -1 );
      assertEquals( 1, GraphicsUtil.getOpacity( shape ) ); 
      parentNode.removeChild( GraphicsUtil.getCanvasNode( canvas ) );      
    },

    testBlur : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = GraphicsUtil.createCanvas();
      parentNode.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      var shape = GraphicsUtil.createShape( "rect" );
      GraphicsUtil.addToCanvas( canvas, shape );
      GraphicsUtil.setBlur( shape, 4 );
      assertTrue( shape.node.style.cssText.indexOf( "FILTER:" ) != -1 );
      var filter = shape.node.style.filter;
      var expected = "progid:DXImageTransform.Microsoft.Blur(pixelradius=4)";
      assertTrue( filter.indexOf( expected ) != -1 );
      assertEquals( 4, GraphicsUtil.getBlur( shape ) ); 
      GraphicsUtil.setBlur( shape, 0 );
      // It is important for some issues that filter is completely removed:
      assertTrue( shape.node.style.cssText.indexOf( "FILTER:" ) == -1 );
      assertEquals( 0, GraphicsUtil.getBlur( shape ) ); 
      parentNode.removeChild( GraphicsUtil.getCanvasNode( canvas ) );
    },

    testBlurWithOpacity : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = GraphicsUtil.createCanvas();
      parentNode.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      var shape = GraphicsUtil.createShape( "rect" );
      GraphicsUtil.addToCanvas( canvas, shape );
      GraphicsUtil.setBlur( shape, 4 );
      GraphicsUtil.setOpacity( shape, 0.5 );
      assertTrue( shape.node.style.cssText.indexOf( "FILTER:" ) != -1 );
      var alpha = "Alpha(opacity=50)";
      var blur = "progid:DXImageTransform.Microsoft.Blur(pixelradius=4)";
      assertEquals( alpha+blur, shape.node.style.filter );
      GraphicsUtil.setBlur( shape, 0 );
      assertEquals( alpha, shape.node.style.filter );
      GraphicsUtil.setBlur( shape, 4 );
      assertEquals( alpha+blur, shape.node.style.filter );
      GraphicsUtil.setOpacity( shape, 1 );
      assertEquals( blur, shape.node.style.filter );
      GraphicsUtil.setBlur( shape, 0 );
      assertTrue( shape.node.style.cssText.indexOf( "FILTER:" ) == -1 );
      parentNode.removeChild( GraphicsUtil.getCanvasNode( canvas ) );
    },
    
    testNodeOrder : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      var parent = document.body;
      var canvas = GraphicsUtil.createCanvas();
      parent.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.handleAppear( canvas );
      var shape1 = GraphicsUtil.createShape( "rect" );
      var shape2 = GraphicsUtil.createShape( "rect" );
      var shape3 = GraphicsUtil.createShape( "rect" );
      GraphicsUtil.addToCanvas( canvas, shape1 );
      GraphicsUtil.addToCanvas( canvas, shape3 );
      GraphicsUtil.addToCanvas( canvas, shape2, shape3 );
      var nodes = canvas.node.childNodes;
      assertIdentical( nodes[ 0 ], shape1.node );
      assertIdentical( nodes[ 1 ], shape2.node );
      assertIdentical( nodes[ 2 ], shape3.node );
      TestUtil.flush();
    }


  }
  
} );