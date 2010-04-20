/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.GCCanvasTest", {
  extend : qx.core.Object,

  members : {

    TARGETENGINE : [ "gecko", "webkit" ],
    
    // NOTE: drawImage can not be tested 

    testInit : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      gc.drawLine( 10, 10, 20, 10 );
      gc.init( 400, 500, "10px Arial", "#ffffff", "#000000" );
      assertEquals( 400, parseInt( gc._canvas.width ) );
      assertEquals( 400, parseInt( gc._canvas.style.width ) );
      assertEquals( 500, parseInt( gc._canvas.height ) );
      assertEquals( 500, parseInt( gc._canvas.style.height ) );
      canvas.destroy();
      testUtil.flush();
    },
    
    // Note on "isPointInPath": This method is used to test if the given
    // point is "in" the path, meaning: Would the point be included in a 
    // potential "fill" operation? This makes it impossible to test paths with 
    // less then 3 points. In these cases the test will extend the path.  
    // Also, Firefox differs from Webkit in some cases on whether the points
    // on a path are "in" the path or not. 

    testDrawLine : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      gc.drawLine( 10, 10, 20, 10 );
      var context = gc._context;
      context.lineTo( 15, 15 );
      assertFalse( context.isPointInPath( 9.9, 10 ) );
      assertTrue( context.isPointInPath( 11, 10 ) );
      assertTrue( context.isPointInPath( 15, 10 ) );
      assertTrue( context.isPointInPath( 20, 10 ) );
      assertFalse( context.isPointInPath( 21, 10 ) );
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
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      gc.drawPoint( 40, 30 );      
      var context = gc._context;
      assertTrue( context.isPointInPath( 40.1, 30.1 ) );
      assertTrue( context.isPointInPath( 41, 30 ) );
      assertTrue( context.isPointInPath( 40, 31 ) );
      assertTrue( context.isPointInPath( 41, 31 ) );
      assertFalse( context.isPointInPath( 41.1, 30 ) );
      assertFalse( context.isPointInPath( 40, 31.1 ) );
      assertFalse( context.isPointInPath( 41.1, 31.1 ) );
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
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      gc.drawRectangle( 10, 20, 30, 40 );      
      var context = gc._context;
      assertTrue( context.isPointInPath( 10.1, 20.1 ) );
      assertTrue( context.isPointInPath( 40, 30 ) );
      assertTrue( context.isPointInPath( 10, 60 ) );
      assertTrue( context.isPointInPath( 40, 60 ) );
      assertFalse( context.isPointInPath( 40.1, 30 ) );
      assertFalse( context.isPointInPath( 10, 60.1 ) );
      assertFalse( context.isPointInPath( 40.1, 60.1 ) );
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
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      gc.drawRoundRectangle( 2, 4, 20, 30, 4, 10, true  );      
      var context = gc._context;
      assertTrue( context.isPointInPath( 2, 14 ) );
      assertTrue( context.isPointInPath( 6, 4.1 ) );
      assertTrue( context.isPointInPath( 18, 4 ) );
      assertTrue( context.isPointInPath( 22, 14 ) );
      assertTrue( context.isPointInPath( 22, 24 ) );
      assertTrue( context.isPointInPath( 18, 34 ) );
      assertTrue( context.isPointInPath( 6, 34 ) );
      assertTrue( context.isPointInPath( 2, 24 ) );
      assertFalse( context.isPointInPath( 1, 13 ) );
      assertFalse( context.isPointInPath( 5, 3 ) );
      assertFalse( context.isPointInPath( 19, 3 ) );
      assertFalse( context.isPointInPath( 24, 15 ) );
      assertFalse( context.isPointInPath( 23, 25 ) );
      assertFalse( context.isPointInPath( 19, 35 ) );
      assertFalse( context.isPointInPath( 5, 35 ) );
      assertFalse( context.isPointInPath( 1, 25 ) );
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
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      gc.fillGradientRectangle( 40, 60, -30, -40, false );      
      var context = gc._context;
      assertTrue( context.isPointInPath( 10.1, 20.1 ) );
      assertTrue( context.isPointInPath( 40, 30 ) );
      assertTrue( context.isPointInPath( 10, 60 ) );
      assertTrue( context.isPointInPath( 40, 60 ) );
      assertFalse( context.isPointInPath( 40.1, 30 ) );
      assertFalse( context.isPointInPath( 10, 60.1 ) );
      assertFalse( context.isPointInPath( 40.1, 60.1 ) );
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
      var context = gc._context;
      assertTrue( context.isPointInPath( 101, 115 ) );
      assertTrue( context.isPointInPath( 159, 115 ) );
      assertTrue( context.isPointInPath( 104, 122 ) );
      assertTrue( context.isPointInPath( 156, 122 ) );
      assertTrue( context.isPointInPath( 130, 130 ) );
      assertFalse( context.isPointInPath( 99, 115 ) );
      assertFalse( context.isPointInPath( 161, 115 ) );
      assertFalse( context.isPointInPath( 102, 122 ) );
      assertFalse( context.isPointInPath( 158, 122 ) );
      assertFalse( context.isPointInPath( 130, 131 ) );
      canvas.destroy();
      testUtil.flush();
    },
    
    
    testDrawArcSizeZero : function() {
      // Test passes by not crashing
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      gc.init( 300, 300, "10px Arial", "#FF0000", "#0000FF" );
      gc.drawArc( 100, 100, 0, 0, 180, 180, true );
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
      var context = gc._context;
      assertTrue( context.isPointInPath( 11, 11 ) );
      assertTrue( context.isPointInPath( 100, 70 ) );
      assertTrue( context.isPointInPath( 70, 100 ) );
      assertFalse( context.isPointInPath( 10, 9 ) );
      assertFalse( context.isPointInPath( 101, 70 ) );
      assertFalse( context.isPointInPath( 70, 101 ) );
      canvas.destroy();
      testUtil.flush();
    }

  }
  
} );