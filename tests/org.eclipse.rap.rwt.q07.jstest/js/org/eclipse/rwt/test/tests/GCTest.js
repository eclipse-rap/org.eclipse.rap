/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.GCTest", {
  extend : qx.core.Object,

  members : {

    testCreateAndDisposeGC : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      assertTrue( canvas.isCreated() );
      assertNull( canvas._gc );
      var gc = canvas.getGC();
      assertNotNull( gc );
      assertNotNull( gc._canvas );
      var context = gc._context;
      assertNotNull( context );
      assertEquals( "function", typeof context.beginPath );
      var node = canvas._getTargetNode();
      assertIdentical( node.childNodes[ 0 ], gc._canvas );
      assertIdentical( node.childNodes[ 1 ], gc._textCanvas );
      canvas.destroy();
      testUtil.flush();
      assertTrue( canvas.isDisposed() );
      assertTrue( gc.isDisposed() );
      assertNull( gc._canvas );
      assertNull( gc._textCanvas );
      assertNull( gc._context );
      assertTrue( !context.isDisposed || context.isDisposed() );
    },
    
    testCreateGCBeforeControl : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      assertFalse( canvas.isCreated() );
      assertNull( canvas._gc );
      var gc = canvas.getGC();
      assertNotNull( gc );
      assertNotNull( gc._canvas );
      var context = gc._context;
      assertNotNull( context );
      assertEquals( "function", typeof context.beginPath );
      testUtil.flush();
      var node = canvas._getTargetNode();
      assertIdentical( node.childNodes[ 0 ], gc._canvas );
      assertIdentical( node.childNodes[ 1 ], gc._textCanvas );
      canvas.destroy();
      testUtil.flush();
    },
    
    testCanvasWithChildren : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      var widget = new qx.ui.basic.Terminator();
      widget.setParent( canvas );
      canvas.addToDocument();
      testUtil.flush();
      assertTrue( canvas.isCreated() );
      var node = canvas._getTargetNode();
      assertIdentical( node.childNodes[ 0 ], widget.getElement() );
      var gc = canvas.getGC();
      assertIdentical( node.childNodes[ 0 ], gc._canvas );
      assertIdentical( node.childNodes[ 1 ], gc._textCanvas );
      assertIdentical( node.childNodes[ 2 ], widget.getElement() );
      canvas.destroy();
      testUtil.flush();
    },
    
    testSetAndResetFields : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      gc.setProperty( "foreground", "#a1a2a3" );
      gc.setProperty( "background", "#657890" );
      gc.setProperty( "alpha", 128 );
      gc.setProperty( "lineWidth", 4 );
      gc.setProperty( "lineCap", 2 ); // "round"
      gc.setProperty( "lineJoin", 3 ); // "bevel"
      gc.setProperty( "font", "italic bold 16px Arial" );
      assertEquals( "#a1a2a3", gc._context.strokeStyle.toLowerCase() );
      assertEquals( "#657890", gc._context.fillStyle.toLowerCase() );
      assertEquals( 5, Math.round( gc._context.globalAlpha * 10 ) );
      assertEquals( 4, gc._context.lineWidth );
      assertEquals( "round", gc._context.lineCap );
      assertEquals( "bevel", gc._context.lineJoin );
      assertEquals( "italic bold 16px Arial", gc._context.font );
      gc.init( 300, 300, "10px Arial", "#ffffff", "#000000" );
      assertEquals( "#000000", gc._context.strokeStyle );
      assertEquals( "#ffffff", gc._context.fillStyle.toLowerCase() );
      assertEquals( 1, gc._context.globalAlpha );
      assertEquals( 1, gc._context.lineWidth );
      assertEquals( "butt", gc._context.lineCap );
      assertEquals( "miter", gc._context.lineJoin );
      assertEquals( "10px Arial", gc._context.font );
      canvas.destroy();
      testUtil.flush();
    },

    testSaveAndRestoreFields : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      gc.setProperty( "foreground", "#a1a2a3" );
      gc.setProperty( "background", "#657890" );
      gc.setProperty( "alpha", 128 );
      gc.setProperty( "lineWidth", 4 );
      gc.setProperty( "lineCap", 2 ); // "round"
      gc.setProperty( "lineJoin", 3 ); // "bevel"
      gc.setProperty( "font", "italic bold 16px Arial" );
      gc._context.save();
      // Do not use "init" as setting the dimension clears the stack
      gc._context.clearRect( 0,0,300,300 );
      gc._initFields( "10px Arial", "#ffffff", "#000000" );
      assertEquals( "#000000", gc._context.strokeStyle );
      assertEquals( "#ffffff", gc._context.fillStyle.toLowerCase() );
      assertEquals( 1, gc._context.globalAlpha );
      assertEquals( 1, gc._context.lineWidth );
      assertEquals( "butt", gc._context.lineCap );
      assertEquals( "miter", gc._context.lineJoin );
      assertEquals( "10px Arial", gc._context.font );
      gc._context.restore();
      assertEquals( "#a1a2a3", gc._context.strokeStyle.toLowerCase() );
      assertEquals( "#657890", gc._context.fillStyle.toLowerCase() );
      assertEquals( 5, Math.round( gc._context.globalAlpha * 10 ) );
      assertEquals( 4, gc._context.lineWidth );
      assertEquals( "round", gc._context.lineCap );
      assertEquals( "bevel", gc._context.lineJoin );
      assertEquals( "italic bold 16px Arial", gc._context.font );
      canvas.destroy();
      testUtil.flush();
    },
    
    testDrawText : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Canvas();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = canvas.getGC();
      gc.init( 300, 300, "10px Arial", "#ffffff", "#000000" );
      assertEquals( 0, gc._textCanvas.childNodes.length );
      gc.drawText( "Hello World", 40, 50, true );
      assertEquals( 1, gc._textCanvas.childNodes.length );
      var textNode = gc._textCanvas.firstChild;
      assertEquals( "Hello World", textNode.innerHTML );
      assertEquals( 40, parseInt( textNode.style.left ) );
      assertEquals( 50, parseInt( textNode.style.top ) );
      assertTrue( textNode.style.font.indexOf( "Arial" ) != -1 );
      var color = textNode.style.backgroundColor;
      assertTrue( color == "rgb(255, 255, 255)" || color == "#ffffff" );
      color = textNode.style.color;
      assertTrue( color == "rgb(0, 0, 0)" || color == "#000000" );
      gc.init( 300, 300, "10px Arial", "#ffffff", "#000000" );
      assertEquals( 0, gc._textCanvas.childNodes.length );
      canvas.destroy();
      testUtil.flush();
    }

  }
  
} );