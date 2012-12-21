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

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.GCTest", {
  extend : rwt.qx.Object,

  members : {

    testCreateAndDisposeGC : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new rwt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      TestUtil.flush();
      assertTrue( canvas.isCreated() );
      var gc = new rwt.widgets.GC( canvas );
      var context = gc._context;
      assertNotNull( context );
      assertEquals( "function", typeof context.beginPath );
      var node = canvas._getTargetNode();
      assertIdentical( node.childNodes[ 0 ], gc._canvas );
      assertIdentical( node.childNodes[ 1 ], gc._textCanvas );
      canvas.destroy();
      gc.dispose();
      TestUtil.flush();
      assertTrue( canvas.isDisposed() );
      assertTrue( gc.isDisposed() );
      assertNull( gc._canvas );
      assertNull( gc._textCanvas );
      assertNull( gc._context );
      assertTrue( !context.isDisposed || context.isDisposed() );
    },

    testCreateGCObjectRef : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new rwt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      TestUtil.flush();
      var gc = new rwt.widgets.GC( canvas );

      assertIdentical( gc, gc._canvas.rwtObject );
      assertIdentical( gc, gc._textCanvas.rwtObject );
      canvas.destroy();
    },

    testCreateGCByProtocol : function() {
      this._createGCByProtocol();
      var ObjectManager = rwt.remote.ObjectRegistry;
      var shell = ObjectManager.getObject( "w2" );
      var canvas = ObjectManager.getObject( "w3" );
      var gc = ObjectManager.getObject( "w4" );
      assertTrue( canvas instanceof rwt.widgets.Composite );
      assertTrue( gc instanceof rwt.widgets.GC );
      assertIdentical( shell, canvas.getParent() );
      assertIdentical( canvas, gc._control );
      assertTrue( canvas.getUserData( "isControl") );
      gc.dispose();
      canvas.destroy();
      shell.destroy();
    },

    testDisposeGCByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      this._createGCByProtocol();
      TestUtil.flush();
      var ObjectManager = rwt.remote.ObjectRegistry;
      var shell = ObjectManager.getObject( "w2" );
      var canvas = ObjectManager.getObject( "w3" );
      var gc = ObjectManager.getObject( "w4" );
      assertTrue( gc instanceof rwt.widgets.GC );
      var node = canvas._getTargetNode();
      assertTrue( node.childNodes.length > 1 );

      var processor = rwt.remote.MessageProcessor;
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
//    testHandleOnAppear : rwt.util.Variant.select( "qx.client", {
//      "default" : function() {},
//      "mshtml" : function() {
//        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
//        var graphicsUtil = rwt.graphics.GraphicsUtil;
//        var border = new rwt.html.Border( 3, "rounded", "#FF00F0", [ 0, 1, 2, 3 ] );
//        var parent = new rwt.widgets.Composite();
//        parent.addToDocument();
//        var canvas = new rwt.widgets.Composite();
//        canvas.setDimension( 300, 300 );
//        canvas.setParent( parent );
//        var log = [];
//        var orgAppear = graphicsUtil.handleAppear;
//        graphicsUtil.handleAppear = function( canvas ) {
//          log.push( canvas );
//        };
//        TestUtil.flush();
//        assertEquals( 1, log.length );
//        canvas.setBorder( border );
//        TestUtil.flush();
//        assertEquals( 2, log.length );
//        parent.setBorder( border );
//        TestUtil.flush();
//        assertEquals( 3, log.length );
//        graphicsUtil.handleAppear = orgAppear;
//        canvas.destroy();
//      }
//    } ),

    testCreateGCBeforeControl : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new rwt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      assertFalse( canvas.isCreated() );
      var gc = new rwt.widgets.GC( canvas );
      assertNotNull( gc );
      assertNotNull( gc._canvas );
      var context = gc._context;
      assertNotNull( context );
      assertEquals( "function", typeof context.beginPath );
      TestUtil.flush();
      var node = canvas._getTargetNode();
      assertIdentical( node.childNodes[ 0 ], gc._canvas );
      assertIdentical( node.childNodes[ 1 ], gc._textCanvas );
      canvas.destroy();
      TestUtil.flush();
    },

    testCanvasWithChildren : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new rwt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      var widget = new rwt.widgets.base.Terminator();
      widget.setParent( canvas );
      canvas.addToDocument();
      TestUtil.flush();
      assertTrue( canvas.isCreated() );
      var node = canvas._getTargetNode();
      assertIdentical( node.childNodes[ 0 ], widget.getElement() );
      var gc = new rwt.widgets.GC( canvas );
      assertIdentical( node.childNodes[ 0 ], gc._canvas );
      assertIdentical( node.childNodes[ 1 ], gc._textCanvas );
      assertIdentical( node.childNodes[ 2 ], widget.getElement() );
      canvas.destroy();
      TestUtil.flush();
    },

    testSetAndResetFields : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new rwt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      TestUtil.flush();
      var gc = new rwt.widgets.GC( canvas );
      this._setProperty( gc, "strokeStyle", [ 1, 2, 3 ] );
      this._setProperty( gc, "fillStyle", [ 4, 5, 6 ] );
      this._setProperty( gc, "globalAlpha", 0.128 );
      this._setProperty( gc, "lineWidth", 4 );
      this._setProperty( gc, "lineCap", "round" );
      this._setProperty( gc, "lineJoin", "bevel" );
      this._setProperty( gc, "font", [ [ "Arial" ], 16, true, true ] );
      assertEquals( [ 1, 2, 3 ], rwt.util.Colors.stringToRgb( gc._context.strokeStyle ) );
      assertEquals( [ 4, 5, 6 ], rwt.util.Colors.stringToRgb( gc._context.fillStyle ) );
      assertEquals( 128, Math.round( gc._context.globalAlpha * 1000 ) );
      assertEquals( 4, gc._context.lineWidth );
      assertEquals( "round", gc._context.lineCap );
      assertEquals( "bevel", gc._context.lineJoin );
      if( !( rwt.client.Client.isWebkit() && rwt.client.Client.getMajor() === 537 ) ) {
        assertTrue(    gc._context.font === "italic bold 16px Arial"
                    || gc._context.font === "bold italic 16px Arial" );
      }
      gc.init( 300, 300,
               [ [ "Arial" ], 10, false, false ],
               [ 255, 255, 255, 255 ], [ 0, 0, 0, 255 ] );
      assertEquals( [ 0, 0, 0 ], rwt.util.Colors.stringToRgb( gc._context.strokeStyle ) );
      assertEquals( [ 255, 255, 255 ], rwt.util.Colors.stringToRgb( gc._context.fillStyle ) );
      assertEquals( 1, gc._context.globalAlpha );
      assertEquals( 1, gc._context.lineWidth );
      assertEquals( "butt", gc._context.lineCap );
      assertEquals( "miter", gc._context.lineJoin );
      assertEquals( "10px Arial", gc._context.font );
      canvas.destroy();
      TestUtil.flush();
    },

    testSaveAndRestoreFields : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new rwt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      TestUtil.flush();
      var gc = new rwt.widgets.GC( canvas );
      this._setProperty( gc, "strokeStyle", [ 1,2,3, 255 ] );
      this._setProperty( gc, "fillStyle", [ 4, 5, 6, 255 ] );
      this._setProperty( gc, "globalAlpha", 0.128 );
      this._setProperty( gc, "lineWidth", 4 );
      this._setProperty( gc, "lineCap", "round" ); // "round"
      this._setProperty( gc, "lineJoin", "bevel" ); // "bevel"
      this._setProperty( gc, "font", [ [ "Arial" ], 16, true, true ] );
      gc._context.save();
      // Do not use "init" as setting the dimension clears the stack
      gc._context.clearRect( 0, 0, 300, 300 );
      gc._initFields( [ [ "Arial" ], 10, false, false ], [ 255, 255, 255, 255 ], [ 0, 0, 0, 255 ] );
      assertEquals( [ 0, 0, 0 ], rwt.util.Colors.stringToRgb( gc._context.strokeStyle ) );
      assertEquals( [ 255, 255, 255 ], rwt.util.Colors.stringToRgb( gc._context.fillStyle ) );
      assertEquals( 1, gc._context.globalAlpha );
      assertEquals( 1, gc._context.lineWidth );
      assertEquals( "butt", gc._context.lineCap );
      assertEquals( "miter", gc._context.lineJoin );
      assertEquals( "10px Arial", gc._context.font );
      gc._context.restore();
      assertEquals( [ 1, 2, 3 ], rwt.util.Colors.stringToRgb( gc._context.strokeStyle ) );
      assertEquals( [ 4, 5, 6 ], rwt.util.Colors.stringToRgb( gc._context.fillStyle ) );
      assertEquals( 128, Math.round( gc._context.globalAlpha * 1000 ) );
      assertEquals( 4, gc._context.lineWidth );
      assertEquals( "round", gc._context.lineCap );
      assertEquals( "bevel", gc._context.lineJoin );
      // [if] Font restore is not using by RAP. Not supported in FF 3.0.
      // assertEquals( "italic bold 16px Arial", gc._context.font );
      canvas.destroy();
      TestUtil.flush();
    },

    testDrawText : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new rwt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      TestUtil.flush();
      var gc = new rwt.widgets.GC( canvas );
      gc.init( 300, 300,
               [ [ "Arial" ], 10, false, false ],
               [ 255, 255, 255, 255 ], [ 0, 0, 0, 255 ] );
      assertEquals( 0, gc._textCanvas.childNodes.length );
      gc.draw( [ [ "fillText", "Hello World", false, false, false, 40, 50 ] ]);
      assertEquals( 1, gc._textCanvas.childNodes.length );
      var textNode = gc._textCanvas.firstChild;
      assertEquals( "Hello World", textNode.innerHTML );
      assertEquals( 40, parseInt( textNode.style.left, 10 ) );
      assertEquals( 50, parseInt( textNode.style.top, 10 ) );
      assertTrue( textNode.style.font.indexOf( "Arial" ) != -1 );
      assertEquals( [ 0, 0, 0 ], rwt.util.Colors.stringToRgb( textNode.style.color ) );
      assertEquals( [ 255, 255, 255 ], rwt.util.Colors.stringToRgb( textNode.style.backgroundColor ) );
      gc.init( 300, 300,
               [ [ "Arial" ], 10, false, false ],
               [ 255, 255, 255 ], [ 0, 0, 0 ] );
      assertEquals( 0, gc._textCanvas.childNodes.length );
      canvas.destroy();
      TestUtil.flush();
    },

    // Tests ported from GCOperationWriter_Test#testProcessText...
    testEscapeText : function() {
      var text = "text with \ttab, \nnew line and &mnemonic";
      var gc = this._createGCByProtocol();
      var expected = "text with &nbsp;&nbsp;&nbsp;&nbsp;tab, <br/>new line and mnemonic";
      assertEquals( expected, gc._escapeText( text, true, true, true ) );
      expected = "text with &nbsp;&nbsp;&nbsp;&nbsp;tab, <br/>new line and &amp;mnemonic";
      assertEquals( expected, gc._escapeText( text, false, true, true ) );
      expected = "text with &nbsp;&nbsp;&nbsp;&nbsp;tab, new line and &amp;mnemonic";
      assertEquals( expected, gc._escapeText( text, false, false, true ) );
      expected = "text with tab, new line and &amp;mnemonic";
      assertEquals( expected, gc._escapeText( text, false, false, false ) );

      // test mnemonic
      text = "text without mnemonic";
      assertEquals( text, gc._escapeText( text, true, false, false ) );
      text = "text with &mnemonic";
      expected = "text with mnemonic";
      assertEquals( expected, gc._escapeText( text, true, false, false ) );
      expected = "text with &amp;mnemonic";
      assertEquals( expected, gc._escapeText( text, false, false, false ) );
      text = "text with &&mnemonic";
      expected = "text with &amp;mnemonic";
      assertEquals( expected, gc._escapeText( text, true, false, false ) );
      expected = "text with &amp;&amp;mnemonic";
      assertEquals( expected, gc._escapeText( text, false, false, false ) );

      gc.dispose();
      rwt.remote.ObjectRegistry.getObject( "w2" ).destroy();
      rwt.remote.ObjectRegistry.getObject( "w3" ).destroy();
    },

    /////////
    // Helper

    _createGCByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
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
        "type" : "rwt.widgets.GC",
        "properties" : {
          "parent" : "w3"
        }
      } );
      return rwt.remote.ObjectRegistry.getObject( "w4" );
    },

    _setProperty : function( gc, property, value ) {
      gc.draw( [ [ property, value ] ] );
    }

  }

} );