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

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.SVGTest", {
  extend : rwt.qx.Object,

  members : {

    TARGETENGINE : [ "gecko", "webkit", "newmshtml" ],

    testDrawShapeInWidget : function() {
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
      assertEquals( "svg", canvasNode.tagName );
      assertEquals( "rect", canvasNode.lastChild.tagName );
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
      GraphicsUtil.handleAppear( canvas );
      parentNode.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.setFillColor( shape, null);
      assertEquals( null, GraphicsUtil.getFillType( shape ) );
      assertEquals( null, GraphicsUtil.getFillColor( shape ) );
      assertEquals( "none", shape.node.getAttribute( "fill" ) );
      GraphicsUtil.setFillColor( shape, "green" );      
      assertEquals( "green", shape.node.getAttribute( "fill" ) );
      assertEquals( "color", GraphicsUtil.getFillType( shape ) );
      assertEquals( "green", GraphicsUtil.getFillColor( shape ) );
      parentNode.removeChild( GraphicsUtil.getCanvasNode( canvas ) );      
    },

    testFillGradient : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = GraphicsUtil.createCanvas();
      var shape = GraphicsUtil.createShape( "rect" );
      var hash = rwt.qx.Object.toHashCode( shape );
      GraphicsUtil.addToCanvas( canvas, shape );
      GraphicsUtil.handleAppear( canvas );
      parentNode.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.setFillGradient( shape, [ [ 0, "red" ], [ 1, "green" ] ] );
      assertEquals( "gradient", GraphicsUtil.getFillType( shape ) );
      var expected = "url(#gradient_" + hash + ")"; 
      assertEquals( expected, shape.node.getAttribute( "fill" ) );
      var gradNode = canvas.defsNode.firstChild;
      assertEquals( "linearGradient", gradNode.tagName ); 
      assertEquals( "gradient_" + hash, gradNode.getAttribute( "id" ) ); 
      assertEquals( "0", gradNode.firstChild.getAttribute( "offset" ) ); 
      assertEquals( "red", gradNode.firstChild.getAttribute( "stop-color" ) ); 
      assertEquals( "1", gradNode.lastChild.getAttribute( "offset" ) ); 
      assertEquals( 0, gradNode.getAttribute( "x1" ) );
      assertEquals( 0, gradNode.getAttribute( "y1" ) );
      assertEquals( 0, gradNode.getAttribute( "x2" ) );
      assertEquals( 1, gradNode.getAttribute( "y2" ) );      
      assertEquals( "green", gradNode.lastChild.getAttribute( "stop-color" ) ); 
      parentNode.removeChild( GraphicsUtil.getCanvasNode( canvas ) );      
    },

    testFillGradientHorizontal : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = GraphicsUtil.createCanvas();
      var shape = GraphicsUtil.createShape( "rect" );
      var hash = rwt.qx.Object.toHashCode( shape );
      GraphicsUtil.addToCanvas( canvas, shape );
      GraphicsUtil.handleAppear( canvas );
      parentNode.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      var gradient = [ [ 0, "red" ], [ 1, "green" ] ];
      gradient.horizontal = true;
      GraphicsUtil.setFillGradient( shape, gradient );
      assertEquals( "gradient", GraphicsUtil.getFillType( shape ) );
      var expected = "url(#gradient_" + hash + ")"; 
      assertEquals( expected, shape.node.getAttribute( "fill" ) );
      var gradNode = canvas.defsNode.firstChild;
      assertEquals( "linearGradient", gradNode.tagName ); 
      assertEquals( 0, gradNode.getAttribute( "x1" ) );
      assertEquals( 0, gradNode.getAttribute( "y1" ) );
      assertEquals( 1, gradNode.getAttribute( "x2" ) );
      assertEquals( 0, gradNode.getAttribute( "y2" ) );
      parentNode.removeChild( GraphicsUtil.getCanvasNode( canvas ) );      
    },

    testChangeGradientOrientation : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      TestUtil.flush();
      var parentNode = document.body;
      var canvas = GraphicsUtil.createCanvas();
      var shape = GraphicsUtil.createShape( "rect" );
      var hash = rwt.qx.Object.toHashCode( shape );
      GraphicsUtil.addToCanvas( canvas, shape );
      GraphicsUtil.handleAppear( canvas );
      parentNode.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      var gradient = [ [ 0, "red" ], [ 1, "green" ] ];
      gradient.horizontal = true;
      GraphicsUtil.setFillGradient( shape, gradient );
      var gradNode = canvas.defsNode.firstChild;
      assertEquals( 0, gradNode.getAttribute( "x1" ) );
      assertEquals( 0, gradNode.getAttribute( "y1" ) );
      assertEquals( 1, gradNode.getAttribute( "x2" ) );
      assertEquals( 0, gradNode.getAttribute( "y2" ) );
      var gradientVert = [ [ 0, "red" ], [ 1, "green" ] ];
      gradientVert.horizontal = false;
      GraphicsUtil.setFillGradient( shape, gradientVert );
      assertEquals( 0, gradNode.getAttribute( "x1" ) );
      assertEquals( 0, gradNode.getAttribute( "y1" ) );
      assertEquals( 0, gradNode.getAttribute( "x2" ) );
      assertEquals( 1, gradNode.getAttribute( "y2" ) );
      GraphicsUtil.setFillGradient( shape, gradient );
      assertEquals( 0, gradNode.getAttribute( "x1" ) );
      assertEquals( 0, gradNode.getAttribute( "y1" ) );
      assertEquals( 1, gradNode.getAttribute( "x2" ) );
      assertEquals( 0, gradNode.getAttribute( "y2" ) );
      
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
      var expected =   "M 10 10 L 26 10 A 4 4 0 0 1 30 14 L 30 27 "
                     + "A 3 3 0 0 1 27 30 L 12 30 A 2 2 0 0 1 10 28 Z";
      assertEquals( expected, shape.node.getAttribute( "d" ) );
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
      var expected = "M 10 20 A 10 10 0 0 1 20 10 L 30 10 30 10 L 30 30 30 30 L 10 30 10 30 Z";
      if( rwt.client.Client.isNewMshtml() ) {
        expected = "M 10 20 A 10 10 0 0 1 20 10 L 30 10 L 30 10 L 30 30 L 30 30 L 10 30 L 10 30 Z";
      }
      assertEquals( expected, shape.node.getAttribute( "d" ) );
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
      var expected = "M 10 20 A 10 10 0 0 1 20 10 L 30 10 30 10 L 30 30 30 30 L 10 30 10 30 Z";
      if( rwt.client.Client.isNewMshtml() ) {
        expected = "M 10 20 A 10 10 0 0 1 20 10 L 30 10 L 30 10 L 30 30 L 30 30 L 10 30 L 10 30 Z";
      } 
      assertEquals( expected, shape.node.getAttribute( "d" ) );
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
      assertEquals( "none", shape.node.getAttribute( "display" ) );
      assertFalse( GraphicsUtil.getDisplay( shape ) );
      parent.destroy();
      TestUtil.flush();
    },

    testFillPattern : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      var canvas = GraphicsUtil.createCanvas();
      var parent = document.body;
      var url = "./js/resource/tex.jpg";
      var shape = GraphicsUtil.createShape( "rect" );
      GraphicsUtil.setRectBounds( shape, 10, 10, 100, 100 );
      GraphicsUtil.setStroke( shape, "black", 2 );
      GraphicsUtil.setFillPattern( shape, url, 70, 70 );
      GraphicsUtil.addToCanvas( canvas, shape );
      parent.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.handleAppear( canvas );
      assertEquals( "rect", shape.node.tagName );
      assertEquals( "pattern", canvas.defsNode.firstChild.tagName );
      var patternId = canvas.defsNode.firstChild.getAttribute( "id" );
      assertTrue(  shape.node.getAttribute( "fill").search( patternId ) != -1 );      
      var imageNode = canvas.defsNode.firstChild.firstChild;
      assertEquals( "image", imageNode.tagName );
      assertEquals( 70, imageNode.getAttribute( "width" ) );
      assertEquals( 70, imageNode.getAttribute( "height" ) );
      // this is due to the workaround for Bug 301236:
      if( rwt.client.Client.getEngine() != "webkit" ) {
        assertEquals( url, imageNode.getAttribute( "href" ) );
      }
      assertEquals( "pattern", GraphicsUtil.getFillType( shape ) );
      parent.removeChild( GraphicsUtil.getCanvasNode( canvas ) );
    },

    testBlurFilter : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      var canvas = GraphicsUtil.createCanvas();
      var parent = document.body;
      var shape = GraphicsUtil.createShape( "rect" );
      GraphicsUtil.setRectBounds( shape, 10, 10, 100, 100 );
      GraphicsUtil.setBlur( shape, 4 );
      GraphicsUtil.addToCanvas( canvas, shape );
      parent.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.handleAppear( canvas );
      var hash = rwt.qx.Object.toHashCode( shape );
      var expected = "url(#filter_" + hash + ")"; 
      assertEquals( expected, shape.node.getAttribute( "filter" ) );
      var filterNode = canvas.defsNode.firstChild;
      assertEquals( "filter", filterNode.tagName ); 
      assertEquals( "filter_" + hash, filterNode.getAttribute( "id" ) ); 
      assertEquals( "feGaussianBlur", filterNode.firstChild.tagName ); 
      assertEquals( "2", filterNode.firstChild.getAttribute( "stdDeviation" ) ); 
      assertEquals( "4", GraphicsUtil.getBlur( shape ) ); 
      GraphicsUtil.setBlur( shape, 0 );
      assertEquals( "none", shape.node.getAttribute( "filter" ) );
      assertEquals( "0", GraphicsUtil.getBlur( shape ) ); 
      GraphicsUtil.setBlur( shape, 2 );
      assertEquals( expected, shape.node.getAttribute( "filter" ) );
      assertEquals( "1", filterNode.firstChild.getAttribute( "stdDeviation" ) );
      assertEquals( "2", GraphicsUtil.getBlur( shape ) ); 
      GraphicsUtil.setBlur( shape, 2 );
      parent.removeChild( GraphicsUtil.getCanvasNode( canvas ) );
    },

    testOpacity : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      var canvas = GraphicsUtil.createCanvas();
      var parent = document.body;
      var shape = GraphicsUtil.createShape( "rect" );
      GraphicsUtil.setRectBounds( shape, 10, 10, 100, 100 );
      assertEquals( 0, GraphicsUtil.getOpacity( shape ) );
      GraphicsUtil.setOpacity( shape, 0.4 );
      GraphicsUtil.addToCanvas( canvas, shape );
      parent.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.handleAppear( canvas );
      assertEquals( 0.4, shape.node.getAttribute( "opacity" ) );
      assertEquals( 0.4, GraphicsUtil.getOpacity( shape ) );
      parent.removeChild( GraphicsUtil.getCanvasNode( canvas ) );
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
      assertIdentical( nodes[ 1 ], shape1.node );
      assertIdentical( nodes[ 2 ], shape2.node );
      assertIdentical( nodes[ 3 ], shape3.node );
      parent.removeChild( canvas.node );
    },
    
    testEnableOverflow : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var GraphicsUtil = rwt.graphics.GraphicsUtil;
      var parent = document.body;
      var canvas = GraphicsUtil.createCanvas();
      parent.appendChild( GraphicsUtil.getCanvasNode( canvas ) );
      GraphicsUtil.handleAppear( canvas );
      var shape = GraphicsUtil.createShape( "rect" );
      GraphicsUtil.addToCanvas( canvas, shape );
      assertIdentical( canvas.node, canvas.group );
      assertIdentical( canvas.node, shape.node.parentNode );
      GraphicsUtil.enableOverflow( canvas, 50, 30, 150, 140 );
      assertEquals( "-50px", canvas.node.style.left );
      assertEquals( "-30px", canvas.node.style.top );
      assertEquals( "200px", canvas.node.style.width );
      assertEquals( "170px", canvas.node.style.height );
      assertIdentical( canvas.node, canvas.group.parentNode );
      assertIdentical( canvas.group, shape.node.parentNode );
      var transform = canvas.group.getAttribute( "transform" );
      if( rwt.client.Client.isNewMshtml() ) {
        transform = transform.split( " " ).join( "," );
      } else {
        transform = transform.split( " " ).join( "" );
      }
      assertEquals( "translate(50,30)", transform );
      GraphicsUtil.enableOverflow( canvas, 0, 0, 110, 120 );
      assertEquals( "0px", canvas.node.style.left );
      assertEquals( "0px", canvas.node.style.top );
      assertEquals( "110px", canvas.node.style.width );
      assertEquals( "120px", canvas.node.style.height );
      assertIdentical( canvas.node, canvas.group.parentNode );
      assertIdentical( canvas.group, shape.node.parentNode );
      var transform = canvas.group.getAttribute( "transform" );
      assertEquals( "", transform );
      GraphicsUtil.enableOverflow( canvas, 0, 0, null, null );
      assertEquals( "0px", canvas.node.style.left );
      assertEquals( "0px", canvas.node.style.top );
      assertEquals( "100%", canvas.node.style.width );
      assertEquals( "100%", canvas.node.style.height );
      parent.removeChild( canvas.node );
    }

  }
  
} );