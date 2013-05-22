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

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.GCCanvasTest", {
  extend : rwt.qx.Object,

  members : {

    TARGETENGINE : [ "gecko", "webkit", "newmshtml" ],
    
    // NOTE: drawImage can not be tested 

    testInit : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new rwt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      TestUtil.flush();
      var gc = new rwt.widgets.GC( canvas );
      gc.init( 300, 300,
               [ [ "Arial" ], 10, false, false ],
               [ 255, 0, 0, 255 ], [ 0, 0, 255, 255 ] );
      gc.draw( [ [ "beginPath" ], [ "moveTo", 10, 10 ], [ "lineTo", 20, 10 ], [ "stroke" ] ] );
      gc.init( 400, 500,
               [ [ "Arial" ], 10, false, false ],
               [ 255, 255, 255, 255 ], [ 0, 0, 0, 255 ] );
      assertEquals( 400, parseInt( gc._canvas.width ) );
      assertEquals( 400, parseInt( gc._canvas.style.width ) );
      assertEquals( 500, parseInt( gc._canvas.height ) );
      assertEquals( 500, parseInt( gc._canvas.style.height ) );
      canvas.destroy();
      TestUtil.flush();
    },
    
    // Note on "isPointInPath": This method is used to test if the given
    // point is "in" the path, meaning: Would the point be included in a 
    // potential "fill" operation? This makes it impossible to test paths with 
    // less then 3 points. In these cases the test will extend the path.  
    // Also, Firefox differs from Webkit in some cases on whether the points
    // on a path are "in" the path or not. 

    testDrawLine : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new rwt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      TestUtil.flush();
      var gc = new rwt.widgets.GC( canvas );
      gc.init( 300, 300,
               [ [ "Arial" ], 10, false, false ],
               [ 255, 0, 0, 255 ], [ 0, 0, 255, 255 ] );
      gc.draw( [ 
        [ "beginPath" ], 
        [ "moveTo", 10.5, 10.5 ], 
        [ "lineTo", 20.5, 10.5 ], 
        [ "stroke" ] 
      ] );
      var context = gc._context;
      context.lineTo( 20.5, 20.5 );
      context.lineTo( 10.5, 20.5 );
      assertFalse( context.isPointInPath( 10, 10 ) );
      assertTrue( context.isPointInPath( 11, 11 ) );
      assertTrue( context.isPointInPath( 16, 11 ) );
      assertTrue( context.isPointInPath( 19, 11 ) );
      assertFalse( context.isPointInPath( 21, 10 ) );
      canvas.destroy();
      TestUtil.flush();
    },

    testDrawEllipse : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new rwt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      TestUtil.flush();
      var gc = new rwt.widgets.GC( canvas );
      gc.init( 300, 300,
               [ [ "Arial" ], 10, false, false ],
               [ 255, 0, 0, 255 ], [ 0, 0, 255, 255 ] );

      //gc.drawArc( 100, 100, 60, 30, 180, 180, true );

      var x = 100;
      var y = 100;
      var width = 60;
      var height = 30;
      var startAngle = 180 * Math.PI / 180;
      var arcAngle = 180 * Math.PI / 180;
      var radiusX = width / 2;
      var radiusY = height / 2;
      gc.draw( [
        [ "beginPath" ],
        [ "ellipse", x + radiusX, y + radiusY, radiusX, radiusY, 0, -1 * startAngle, -1 * ( startAngle + arcAngle ), true ],
        [ "fill" ]
      ] );
            
      var context = gc._context;
      assertTrue( context.isPointInPath( 101, 115.1 ) );
      assertTrue( context.isPointInPath( 159, 115 ) );
      assertTrue( context.isPointInPath( 104, 122 ) );
      assertTrue( context.isPointInPath( 155, 122 ) );
      assertTrue( context.isPointInPath( 130, 129 ) );
      assertFalse( context.isPointInPath( 99, 115 ) );
      assertFalse( context.isPointInPath( 161, 115 ) );
      assertFalse( context.isPointInPath( 102, 122 ) );
      assertFalse( context.isPointInPath( 158, 122 ) );
      assertFalse( context.isPointInPath( 130, 131 ) );
      canvas.destroy();
      TestUtil.flush();
    }
    

  }
  
} );