/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.GCVMLTest", {
  extend : qx.core.Object,

  members : {

    TARGETENGINE : [ "mshtml" ],
    
    testStrokeProperties : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var VML = org.eclipse.rwt.VML;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      var vmlCanvas = gc._context._canvas;
      assertEquals( 0, vmlCanvas.node.childNodes.length );
      gc._initFields( "10px Arial", "#657890", "#a1a2a3" );
      gc.setProperty( "alpha", 128 );
      gc.setProperty( "lineWidth", 4 );
      gc.setProperty( "lineCap", 2 ); // "round"
      gc.setProperty( "lineJoin", 3 ); // "bevel"
      gc.drawRectangle( 10, 10, 20, 20, false );
      assertEquals( 1, vmlCanvas.node.childNodes.length );
      var shape = null;
      for( var hash in vmlCanvas.children ) {
        shape = vmlCanvas.children[ hash ];
      }
      assertIdentical( shape.node, vmlCanvas.node.childNodes[ 0 ] );
      assertEquals( 4, VML.getStrokeWidth( shape ) );
      glob = shape.node.strokeColor;
      assertEquals( "#a1a2a3", shape.node.strokeColor.value );
      assertNotNull( shape.stroke );
      assertTrue( shape.node.style.filter.indexOf( "opacity=50" ) != -1 );
      assertEquals( "round", shape.stroke.endcap ); 
      assertEquals( "bevel", shape.stroke.joinstyle ); 
      canvas.destroy();
      testUtil.flush();      
    },
    
    testFillProperties : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var VML = org.eclipse.rwt.VML;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      var vmlCanvas = gc._context._canvas;
      assertEquals( 0, vmlCanvas.node.childNodes.length );
      gc._initFields( "10px Arial", "#657890", "#a1a2a3" );
      gc.setProperty( "alpha", 128 );
      gc.drawRectangle( 10, 10, 20, 20, true );
      assertEquals( 1, vmlCanvas.node.childNodes.length );
      var shape = null;
      for( var hash in vmlCanvas.children ) {
        shape = vmlCanvas.children[ hash ];
      }
      assertIdentical( shape.node, vmlCanvas.node.childNodes[ 0 ] );
      assertEquals( "#657890", VML.getFillColor( shape ) );
      assertTrue( shape.node.style.filter.indexOf( "opacity=50" ) != -1 );
      canvas.destroy();
      testUtil.flush();      
    },
    
    testInit : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      var vmlCanvas = gc._context._canvas;
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      gc.drawLine( 10, 10, 20, 10 );
      assertEquals( 1, vmlCanvas.node.childNodes.length );
      gc.init( 400, 500 );
      assertEquals( 0, vmlCanvas.node.childNodes.length );
      canvas.destroy();
      testUtil.flush();
    },

    testDrawLine : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      gc.drawLine( 10, 10, 20, 10 );
      assertEquals( "m95,95 l195,95 e", this._getLastPath( gc ) );
      canvas.destroy();
      testUtil.flush();
    },
    

    testDrawPoint : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      context = gc._context;
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      gc.drawPoint( 40, 30 );
      var expected = "m395,295 l405,295,405,305,395,305 xe";
      assertEquals( expected, this._getLastPath( gc ) );
      canvas.destroy();
      testUtil.flush();
    },
    
    testDrawRectangle : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      context = gc._context;
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      gc.drawRectangle( 10, 20, 30, 40 );      
      var expected = "m95,195 l395,195,395,595,95,595 xe";
      assertEquals( expected, this._getLastPath( gc ) );
      canvas.destroy();
      testUtil.flush();
    },

    testDrawRoundRectangle : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      context = gc._context;
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      gc.drawRoundRectangle( 2, 4, 20, 30, 4, 10, true  );      
      var expected =   "m15,135 l15,235 qb15,335 l55,335,175,335 " 
                     + "qb215,335 l215,235,215,135 qb215,35 l175,35,55,35 "
                     + "qb15,35 l15,135 e";
      assertEquals( expected, this._getLastPath( gc ) );
      canvas.destroy();
      testUtil.flush();
    },

    testFillGradientRectangle : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      context = gc._context;
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      gc.fillGradientRectangle( 40, 60, -30, -40, false );      
      var expected = "m395,595 l95,595,95,195,395,195 xe"
      assertEquals( expected, this._getLastPath( gc ) );
      expected = "0 red;.25 #bf0040;.5 purple;.75 #4000bf;1 blue";
      assertEquals( expected, gc._canvas.lastChild.fill.colors.value );
      assertEquals( 270, gc._canvas.lastChild.fill.angle );
      assertEquals( "blue", gc._canvas.lastChild.fill.color2.value );
      canvas.destroy();
      testUtil.flush();
    },
    
    testDrawArc : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      gc.drawArc( 100, 100, 60, 30, 180, 180, true );
      var expected = "m995,1145 ae1295,1145,300,150,11796300,11796300 e";
      assertEquals( expected, this._getLastPath( gc ) );      
      canvas.destroy();
      testUtil.flush();
    },

    testDrawPolyline : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      gc.drawPolyline( [ 10, 10, 100, 70, 70, 100 ], true, true );      
      var expected = "m95,95 l995,695,695,995,95,95 e";
      assertEquals( expected, this._getLastPath( gc ) );
      canvas.destroy();
      testUtil.flush();
    },
    
    testDrawImage : function() {
      // NOTE: drawImage can not be tested directly, test "setImageData" instead
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      var vmlCanvas = gc._context._canvas;
      var shape = org.eclipse.rwt.VML.createShape( "image" );
      org.eclipse.rwt.VML.setImageData( shape, 
                                        "test.jpg", 
                                        40, 
                                        50, 
                                        100, 
                                        200, 
                                        [ 0.1, 0.2, 0.3, 0.4 ] );
      org.eclipse.rwt.VML.addToCanvas( vmlCanvas, shape );
      assertEquals( "test.jpg", shape.node.src );
      assertEquals( 400, parseInt( shape.node.style.left ) );
      assertEquals( 500, parseInt( shape.node.style.top ) );
      assertEquals( 1000, parseInt( shape.node.style.width ) );
      assertEquals( 2000, parseInt( shape.node.style.height ) );
      assertEquals( 1, Math.round( shape.node.cropTop * 10 ) );
      assertEquals( 2, Math.round( shape.node.cropRight * 10 ) );
      assertEquals( 3, Math.round( shape.node.cropBottom * 10 ) );
      assertEquals( 4, Math.round( shape.node.cropLeft * 10 ) );
      canvas.destroy();
      testUtil.flush();
    },

    _getLastPath : function( gc ) {
      var result = gc._context._canvas.node.lastChild.path.v.toLowerCase();
      if( result.charAt( 0 ) == " " ) {
        result = result.slice( 1 );
      }
      return result;
    }

  }

} );