/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.SVGTest", {
  extend : qx.core.Object,

  members : {

    TARGETENGINE : [ "gecko", "webkit" ],

    testDrawShapeInWidget : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var parent = new qx.ui.basic.Terminator();
      parent.setLocation( 10, 10 );
      parent.setDimension( 400, 400 );
      parent.addToDocument();
      testUtil.flush();
      var parentNode = parent._getTargetNode();
      var canvas = gfxUtil.createCanvas();
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.addToCanvas( canvas, shape );
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );      
      var canvasNode = parentNode.firstChild;
      assertEquals( "svg", canvasNode.tagName );
      assertEquals( "rect", canvasNode.lastChild.tagName );
      assertEquals( null, gfxUtil.getFillType( shape ) );
      assertTrue( gfxUtil.getDisplay( shape ) );
      parent.destroy();
      testUtil.flush();
    },
    
    testFillColor : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      testUtil.flush();
      var parentNode = document.body;
      var canvas = gfxUtil.createCanvas();
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.addToCanvas( canvas, shape );
      gfxUtil.handleAppear( canvas );
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.setFillColor( shape, null);
      assertEquals( null, gfxUtil.getFillType( shape ) );
      assertEquals( null, gfxUtil.getFillColor( shape ) );
      assertEquals( "none", shape.node.getAttribute( "fill" ) );
      gfxUtil.setFillColor( shape, "green" );      
      assertEquals( "green", shape.node.getAttribute( "fill" ) );
      assertEquals( "color", gfxUtil.getFillType( shape ) );
      assertEquals( "green", gfxUtil.getFillColor( shape ) );
      parentNode.removeChild( gfxUtil.getCanvasNode( canvas ) );      
    },

    testFillGradient : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      testUtil.flush();
      var parentNode = document.body;
      var canvas = gfxUtil.createCanvas();
      var shape = gfxUtil.createShape( "rect" );
      var hash = qx.core.Object.toHashCode( shape );
      gfxUtil.addToCanvas( canvas, shape );
      gfxUtil.handleAppear( canvas );
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.setFillGradient( shape, [ [ 0, "red" ], [ 1, "green" ] ] );
      assertEquals( "gradient", gfxUtil.getFillType( shape ) );
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
      parentNode.removeChild( gfxUtil.getCanvasNode( canvas ) );      
    },

    testFillGradientHorizontal : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      testUtil.flush();
      var parentNode = document.body;
      var canvas = gfxUtil.createCanvas();
      var shape = gfxUtil.createShape( "rect" );
      var hash = qx.core.Object.toHashCode( shape );
      gfxUtil.addToCanvas( canvas, shape );
      gfxUtil.handleAppear( canvas );
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      var gradient = [ [ 0, "red" ], [ 1, "green" ] ];
      gradient.horizontal = true;
      gfxUtil.setFillGradient( shape, gradient );
      assertEquals( "gradient", gfxUtil.getFillType( shape ) );
      var expected = "url(#gradient_" + hash + ")"; 
      assertEquals( expected, shape.node.getAttribute( "fill" ) );
      var gradNode = canvas.defsNode.firstChild;
      assertEquals( "linearGradient", gradNode.tagName ); 
      assertEquals( 0, gradNode.getAttribute( "x1" ) );
      assertEquals( 0, gradNode.getAttribute( "y1" ) );
      assertEquals( 1, gradNode.getAttribute( "x2" ) );
      assertEquals( 0, gradNode.getAttribute( "y2" ) );
      parentNode.removeChild( gfxUtil.getCanvasNode( canvas ) );      
    },

    testChangeGradientOrientation : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      testUtil.flush();
      var parentNode = document.body;
      var canvas = gfxUtil.createCanvas();
      var shape = gfxUtil.createShape( "rect" );
      var hash = qx.core.Object.toHashCode( shape );
      gfxUtil.addToCanvas( canvas, shape );
      gfxUtil.handleAppear( canvas );
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      var gradient = [ [ 0, "red" ], [ 1, "green" ] ];
      gradient.horizontal = true;
      gfxUtil.setFillGradient( shape, gradient );
      var gradNode = canvas.defsNode.firstChild;
      assertEquals( 0, gradNode.getAttribute( "x1" ) );
      assertEquals( 0, gradNode.getAttribute( "y1" ) );
      assertEquals( 1, gradNode.getAttribute( "x2" ) );
      assertEquals( 0, gradNode.getAttribute( "y2" ) );
      var gradientVert = [ [ 0, "red" ], [ 1, "green" ] ];
      gradientVert.horizontal = false;
      gfxUtil.setFillGradient( shape, gradientVert );
      assertEquals( 0, gradNode.getAttribute( "x1" ) );
      assertEquals( 0, gradNode.getAttribute( "y1" ) );
      assertEquals( 0, gradNode.getAttribute( "x2" ) );
      assertEquals( 1, gradNode.getAttribute( "y2" ) );
      gfxUtil.setFillGradient( shape, gradient );
      assertEquals( 0, gradNode.getAttribute( "x1" ) );
      assertEquals( 0, gradNode.getAttribute( "y1" ) );
      assertEquals( 1, gradNode.getAttribute( "x2" ) );
      assertEquals( 0, gradNode.getAttribute( "y2" ) );
      
      parentNode.removeChild( gfxUtil.getCanvasNode( canvas ) );      
    },

    testDrawRoundRect : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var canvas = gfxUtil.createCanvas();
      var parent = document.body;
      var shape = gfxUtil.createShape( "roundrect" );
      gfxUtil.setRoundRectLayout( shape, 10, 10, 20, 20, [ 0, 4, 3, 2 ] );
      gfxUtil.setStroke( shape, "black", 2 );
      gfxUtil.addToCanvas( canvas, shape );
      parent.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      var expected =   "M 10 10 L 26 10 A 4 4 0 0 1 30 14 L 30 27 "
                     + "A 3 3 0 0 1 27 30 L 12 30 A 2 2 0 0 1 10 28 Z";
      assertEquals( expected, shape.node.getAttribute( "d" ) );
      parent.removeChild( gfxUtil.getCanvasNode( canvas ) );
    },

    testDrawRoundRectOneRadiusOnlyMinimalMize : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var canvas = gfxUtil.createCanvas();
      var parent = document.body;
      var shape = gfxUtil.createShape( "roundrect" );
      gfxUtil.setRoundRectLayout( shape, 10, 10, 20, 20, [ 20, 0, 0, 0] );
      gfxUtil.setStroke( shape, "black", 2 );
      gfxUtil.setFillColor( shape, "red" );
      gfxUtil.addToCanvas( canvas, shape );
      parent.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      var expected =   "M 10 30 A 20 20 0 0 1 30 10 L 30 10 30 10 L 30 30 "
                     + "30 30 L 10 30 10 30 Z";
      assertEquals( expected, shape.node.getAttribute( "d" ) );
      parent.removeChild( gfxUtil.getCanvasNode( canvas ) );
    },

    testDrawRoundRectOneRadiusOnlyTooSmall : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var canvas = gfxUtil.createCanvas();
      var parent = document.body;
      var shape = gfxUtil.createShape( "roundrect" );
      gfxUtil.setRoundRectLayout( shape, 10, 10, 20, 20, [ 21, 0, 0, 0] );
      gfxUtil.setStroke( shape, "black", 2 );
      gfxUtil.setFillColor( shape, "red" );
      gfxUtil.addToCanvas( canvas, shape );
      parent.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      var expected = "M 10 10 L 30 10 30 10 L 30 30 30 30 L 10 30 10 30 Z"; 
      assertEquals( expected, shape.node.getAttribute( "d" ) );
      parent.removeChild( gfxUtil.getCanvasNode( canvas ) );
    },

    testDisplay : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var parent = new qx.ui.basic.Terminator();
      parent.setLocation( 10, 10 );
      parent.setDimension( 400, 400 );
      parent.addToDocument();
      testUtil.flush();
      var parentNode = parent._getTargetNode();
      var canvas = gfxUtil.createCanvas();
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.addToCanvas( canvas, shape );
      gfxUtil.setDisplay( shape, false );
      parentNode.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );      
      var canvasNode = parentNode.firstChild;
      assertEquals( "none", shape.node.getAttribute( "display" ) );
      assertFalse( gfxUtil.getDisplay( shape ) );
      parent.destroy();
      testUtil.flush();
    },

    testFillPattern : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var canvas = gfxUtil.createCanvas();
      var parent = document.body;
      var url = "./js/resource/tex.jpg";
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.setRectBounds( shape, 10, 10, 100, 100 );
      gfxUtil.setStroke( shape, "black", 2 );
      gfxUtil.setFillPattern( shape, url, 70, 70 );
      gfxUtil.addToCanvas( canvas, shape );
      parent.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      assertEquals( "rect", shape.node.tagName );
      assertEquals( "pattern", canvas.defsNode.firstChild.tagName )
      var patternId = canvas.defsNode.firstChild.getAttribute( "id" );
      assertTrue(  shape.node.getAttribute( "fill").search( patternId ) != -1 );      
      var imageNode = canvas.defsNode.firstChild.firstChild;
      assertEquals( "image", imageNode.tagName );
      assertEquals( 70, imageNode.getAttribute( "width" ) );
      assertEquals( 70, imageNode.getAttribute( "height" ) );
      // this is due to the workaround for Bug 301236:
      if( org.eclipse.rwt.Client.getEngine() != "webkit" ) {
        assertEquals( url, imageNode.getAttribute( "href" ) );
      }
      assertEquals( "pattern", gfxUtil.getFillType( shape ) );
      parent.removeChild( gfxUtil.getCanvasNode( canvas ) );
    },

    testBlurFilter : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var canvas = gfxUtil.createCanvas();
      var parent = document.body;
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.setRectBounds( shape, 10, 10, 100, 100 );
      gfxUtil.setBlur( shape, 4 );
      gfxUtil.addToCanvas( canvas, shape );
      parent.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      var hash = qx.core.Object.toHashCode( shape );
      var expected = "url(#filter_" + hash + ")"; 
      assertEquals( expected, shape.node.getAttribute( "filter" ) );
      var filterNode = canvas.defsNode.firstChild;
      assertEquals( "filter", filterNode.tagName ); 
      assertEquals( "filter_" + hash, filterNode.getAttribute( "id" ) ); 
      assertEquals( "feGaussianBlur", filterNode.firstChild.tagName ); 
      assertEquals( "2", filterNode.firstChild.getAttribute( "stdDeviation" ) ); 
      assertEquals( "4", gfxUtil.getBlur( shape ) ); 
      gfxUtil.setBlur( shape, 0 );
      assertEquals( "none", shape.node.getAttribute( "filter" ) );
      assertEquals( "0", gfxUtil.getBlur( shape ) ); 
      gfxUtil.setBlur( shape, 2 );
      assertEquals( expected, shape.node.getAttribute( "filter" ) );
      assertEquals( "1", filterNode.firstChild.getAttribute( "stdDeviation" ) );
      assertEquals( "2", gfxUtil.getBlur( shape ) ); 
      gfxUtil.setBlur( shape, 2 );
      parent.removeChild( gfxUtil.getCanvasNode( canvas ) );
    },

    testOpacity : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var canvas = gfxUtil.createCanvas();
      var parent = document.body;
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.setRectBounds( shape, 10, 10, 100, 100 );
      assertEquals( 0, gfxUtil.getOpacity( shape ) );
      gfxUtil.setOpacity( shape, 0.4 );
      gfxUtil.addToCanvas( canvas, shape );
      parent.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      assertEquals( 0.4, shape.node.getAttribute( "opacity" ) );
      assertEquals( 0.4, gfxUtil.getOpacity( shape ) );
      parent.removeChild( gfxUtil.getCanvasNode( canvas ) );
    },
    
    testNodeOrder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var parent = document.body;
      canvas = gfxUtil.createCanvas();
      parent.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      var shape1 = gfxUtil.createShape( "rect" );
      var shape2 = gfxUtil.createShape( "rect" );
      var shape3 = gfxUtil.createShape( "rect" );
      gfxUtil.addToCanvas( canvas, shape1 );
      gfxUtil.addToCanvas( canvas, shape3 );
      gfxUtil.addToCanvas( canvas, shape2, shape3 );
      var nodes = canvas.node.childNodes;
      assertIdentical( nodes[ 1 ], shape1.node );
      assertIdentical( nodes[ 2 ], shape2.node );
      assertIdentical( nodes[ 3 ], shape3.node );
      parent.removeChild( canvas.node );
    },
    
    testEnableOverflow : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil
      var parent = document.body;
      canvas = gfxUtil.createCanvas();
      parent.appendChild( gfxUtil.getCanvasNode( canvas ) );
      gfxUtil.handleAppear( canvas );
      var shape = gfxUtil.createShape( "rect" );
      gfxUtil.addToCanvas( canvas, shape );
      assertIdentical( canvas.node, canvas.group );
      assertIdentical( canvas.node, shape.node.parentNode );
      gfxUtil.enableOverflow( canvas, 50, 30, 150, 140 );
      assertEquals( "-50px", canvas.node.style.left );
      assertEquals( "-30px", canvas.node.style.top );
      assertEquals( "200px", canvas.node.style.width );
      assertEquals( "170px", canvas.node.style.height );
      assertIdentical( canvas.node, canvas.group.parentNode );
      assertIdentical( canvas.group, shape.node.parentNode );
      var transform = canvas.group.getAttribute( "transform" );
      transform = transform.split( " " ).join( "" );
      assertEquals( "translate(50,30)", transform );
      gfxUtil.enableOverflow( canvas, 0, 0, 110, 120 );
      assertEquals( "0px", canvas.node.style.left );
      assertEquals( "0px", canvas.node.style.top );
      assertEquals( "110px", canvas.node.style.width );
      assertEquals( "120px", canvas.node.style.height );
      assertIdentical( canvas.node, canvas.group.parentNode );
      assertIdentical( canvas.group, shape.node.parentNode );
      var transform = canvas.group.getAttribute( "transform" );
      assertEquals( "", transform );
      gfxUtil.enableOverflow( canvas, 0, 0, null, null );
      assertEquals( "0px", canvas.node.style.left );
      assertEquals( "0px", canvas.node.style.top );
      assertEquals( "100%", canvas.node.style.width );
      assertEquals( "100%", canvas.node.style.height );
      parent.removeChild( canvas.node );
    }

  }
  
} );