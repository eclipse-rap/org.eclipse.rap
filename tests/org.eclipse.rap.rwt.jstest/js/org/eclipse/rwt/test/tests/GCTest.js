/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.GCTest", {
  extend : qx.core.Object,

  members : {

    testCreateAndDisposeGC : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      assertTrue( canvas.isCreated() );
      var gc = new org.eclipse.swt.graphics.GC( canvas );
      var context = gc._context;
      assertNotNull( context );
      assertEquals( "function", typeof context.beginPath );
      var node = canvas._getTargetNode();
      assertIdentical( node.childNodes[ 0 ], gc._canvas );
      assertIdentical( node.childNodes[ 1 ], gc._textCanvas );
      canvas.destroy();
      gc.dispose();
      testUtil.flush();
      assertTrue( canvas.isDisposed() );
      assertTrue( gc.isDisposed() );
      assertNull( gc._canvas );
      assertNull( gc._textCanvas );
      assertNull( gc._context );
      assertTrue( !context.isDisposed || context.isDisposed() );
    },

    testCreateGCByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      this._createGCByProtocol();
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var shell = objectManager.getObject( "w2" );
      var canvas = objectManager.getObject( "w3" );
      var gc = objectManager.getObject( "w4" );
      assertTrue( canvas instanceof org.eclipse.swt.widgets.Composite );
      assertTrue( gc instanceof org.eclipse.swt.graphics.GC );
      assertIdentical( shell, canvas.getParent() );
      assertIdentical( canvas, gc._control );
      assertTrue( canvas.getUserData( "isControl") );
      gc.dispose();
      canvas.destroy();
      shell.destroy();
    },

    testDisposeGCByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      this._createGCByProtocol();
      testUtil.flush();
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var shell = objectManager.getObject( "w2" );
      canvas = objectManager.getObject( "w3" );
      var gc = objectManager.getObject( "w4" );
      assertTrue( gc instanceof org.eclipse.swt.graphics.GC );
      var node = canvas._getTargetNode();
      assertTrue( node.childNodes.length > 1 );
      
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "destroy"
      } );
      
      assertTrue( gc.isDisposed() );
      assertFalse( canvas.hasEventListeners( "insertDom" ) );
      assertTrue( node.childNodes.length <= 1 );
      canvas.destroy();
      shell.destroy();
    },

//    TODO [tb] : a bug in theory, but apparently not reproduceable in actual RAP application
//
//    testHandleOnAppear : qx.core.Variant.select( "qx.client", {
//      "default" : function() {},
//      "mshtml" : function() {
//        var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
//        var graphicsUtil = org.eclipse.rwt.GraphicsUtil;
//        var border = new org.eclipse.rwt.Border( 3, "rounded", "#FF00F0", [ 0, 1, 2, 3 ] );
//        var parent = new org.eclipse.swt.widgets.Composite();
//        parent.addToDocument();
//        var canvas = new org.eclipse.swt.widgets.Composite();
//        canvas.setDimension( 300, 300 );
//        canvas.setParent( parent );
//        var log = [];
//        var orgAppear = graphicsUtil.handleAppear;
//        graphicsUtil.handleAppear = function( canvas ) {
//          log.push( canvas );
//        };
//        testUtil.flush();
//        assertEquals( 1, log.length );
//        canvas.setBorder( border );
//        testUtil.flush();
//        assertEquals( 2, log.length );
//        parent.setBorder( border );
//        testUtil.flush();
//        assertEquals( 3, log.length );
//        graphicsUtil.handleAppear = orgAppear;
//        canvas.destroy();
//      }
//    } ),
    
    testCreateGCBeforeControl : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      assertFalse( canvas.isCreated() );
      var gc = new org.eclipse.swt.graphics.GC( canvas );
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
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      var widget = new qx.ui.basic.Terminator();
      widget.setParent( canvas );
      canvas.addToDocument();
      testUtil.flush();
      assertTrue( canvas.isCreated() );
      var node = canvas._getTargetNode();
      assertIdentical( node.childNodes[ 0 ], widget.getElement() );
      var gc = new org.eclipse.swt.graphics.GC( canvas );
      assertIdentical( node.childNodes[ 0 ], gc._canvas );
      assertIdentical( node.childNodes[ 1 ], gc._textCanvas );
      assertIdentical( node.childNodes[ 2 ], widget.getElement() );
      canvas.destroy();
      testUtil.flush();
    },
    
    testSetAndResetFields : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = new org.eclipse.swt.graphics.GC( canvas );
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
      assertTrue(    gc._context.font === "italic bold 16px Arial" 
                  || gc._context.font === "bold italic 16px Arial" );
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
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = new org.eclipse.swt.graphics.GC( canvas );
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
      // [if] Font restore is not using by RAP. Not supported in FF 3.0.
      // assertEquals( "italic bold 16px Arial", gc._context.font );
      canvas.destroy();
      testUtil.flush();
    },
    
    testDrawText : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = new org.eclipse.swt.graphics.GC( canvas );
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
    },
    
    /////////
    // Helper
    
    _createGCByProtocol : function() {
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Canvas",
        "properties" : {
          "parent" : "w2",
          "style" : []
        }
      } );
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.GC",
        "properties" : {
         "parent" : "w3"
        }
      } );
    }

  }
  
} );