/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
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
      assertEquals( "green", gradNode.lastChild.getAttribute( "stop-color" ) ); 
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
      if( qx.core.Client.getEngine() != "webkit" ) {
        assertEquals( url, imageNode.getAttribute( "href" ) );
      }
      assertEquals( "pattern", gfxUtil.getFillType( shape ) );
      parent.removeChild( gfxUtil.getCanvasNode( canvas ) );
    }

  }
  
} );